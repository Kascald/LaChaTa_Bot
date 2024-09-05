package com.lachata.manager;

import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import com.lachata.manager.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LavaMusicManager {
	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final ConcurrentHashMap<Long, GuildMusicManager> musicManager = new ConcurrentHashMap<>();
	private static final YoutubeSearchProvider youtubeSearchProvider = new YoutubeSearchProvider(); // YouTube 검색 기능 추가

	static {
		// YouTubeAudioSourceManager를 AudioPlayerManager에 등록
		playerManager.registerSourceManager(new Youtube());

		// 추가적으로 다른 오디오 소스를 등록할 수 있습니다 (예: SoundCloud)
		playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
	}

	public static synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
		long guildId = Long.parseLong(guild.getId());

		return musicManager.computeIfAbsent(guildId, id -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
			return guildMusicManager;
		});
	}

	// URL 또는 검색어를 받아 트랙을 로드하고 재생
	public static void loadAndPlay(final TextChannel textChannel, final Guild guild, final String input) {
		if (input.startsWith("https://")) {
			// 입력이 URL인 경우, URL을 통한 재생
			loadUrlAndPlay(textChannel, guild, input);
		} else {
			// 입력이 검색어인 경우, 검색을 통한 재생
			loadSearchAndPlay(textChannel, guild, input);
		}
	}

	// URL을 사용한 트랙 로드 및 재생
	public static void loadUrlAndPlay(final TextChannel textChannel, final Guild guild, final String trackUrl) {
//		final String sanitizedUrl = sanitizeUrl(trackUrl); // 파라미터 제거
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		final MusicQueue musicQueue = musicManager.scheduler.getMusicQueue();

//		playerManager.loadItemOrdered(musicManager, sanitizedUrl, new AudioLoadResultHandler() {
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				// 트랙을 대기열에 추가하고 재생
				MusicInfo musicInfo = new MusicInfo(audioTrack, audioTrack.getInfo().title, audioTrack.getInfo().author, audioTrack.getInfo().length, 0, false);
				musicQueue.addQueue(musicInfo);  // 대기열에 추가

				musicManager.scheduler.playQueue(audioTrack);

				textChannel.sendMessage(
						String.format("Added track **`%s`** by **`%s`**",
						              audioTrack.getInfo().title,
						              audioTrack.getInfo().author)
				                       ).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				final List<AudioTrack> tracks = audioPlaylist.getTracks();
				if (!tracks.isEmpty()) {
					AudioTrack firstTrack = tracks.get(0);

					// 플레이리스트의 모든 트랙을 대기열에 추가
					for (AudioTrack track : tracks) {
						MusicInfo musicInfo = new MusicInfo(track, track.getInfo().title, track.getInfo().author, track.getInfo().length, 0, false);
						musicQueue.addQueue(musicInfo);  // 대기열에 추가
					}

					musicManager.scheduler.playQueue(firstTrack);

					textChannel.sendMessage(
							String.format("Added playlist with **%d** tracks. Now playing: **`%s`** by **`%s`**",
							              tracks.size(),                           // 전체 트랙 수
							              firstTrack.getInfo().title,              // 첫 번째 트랙의 제목
							              firstTrack.getInfo().author)             // 첫 번째 트랙의 저자
					                       ).queue();
				}
			}

			@Override
			public void noMatches() {
				// 검색어 또는 URL이 유효하지 않을 때
				textChannel.sendMessage("일치하는 트랙을 찾지 못했습니다!").queue();
			}

			@Override
			public void loadFailed(FriendlyException e) {
				// 트랙 로드 중 실패했을 때
				textChannel.sendMessage("트랙을 로드하는 중 오류가 발생했습니다: " + e.getMessage()).queue();
			}
		});
	}

	// 검색어를 사용한 트랙 로드 및 재생
	public static void loadSearchAndPlay(final TextChannel textChannel, final Guild guild, final String searchQuery) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		final MusicQueue musicQueue = musicManager.scheduler.getMusicQueue();

		// YoutubeSearchProvider로 검색
		youtubeSearchProvider.loadSearchResult(searchQuery, trackInfo -> {
			// 검색 결과로부터 트랙을 로드
			String trackUrl = "https://www.youtube.com/watch?v=" + trackInfo.identifier;

			playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {

				@Override
				public void trackLoaded(AudioTrack audioTrack) {
					// 트랙을 대기열에 추가하고 재생
					MusicInfo musicInfo = new MusicInfo(audioTrack, audioTrack.getInfo().title, audioTrack.getInfo().author, audioTrack.getInfo().length, 0, false);
					musicQueue.addQueue(musicInfo);  // 대기열에 추가

					musicManager.scheduler.playQueue(audioTrack);

					textChannel.sendMessage(
							String.format("Added track **`%s`** by **`%s`**",
							              audioTrack.getInfo().title,
							              audioTrack.getInfo().author)
					                       ).queue();
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					final List<AudioTrack> tracks = audioPlaylist.getTracks();
					if (!tracks.isEmpty()) {
						AudioTrack firstTrack = tracks.get(0);

						// 플레이리스트의 모든 트랙을 대기열에 추가
						for (AudioTrack track : tracks) {
							MusicInfo musicInfo = new MusicInfo(track, track.getInfo().title, track.getInfo().author, track.getInfo().length, 0, false);
							musicQueue.addQueue(musicInfo);  // 대기열에 추가
						}

						musicManager.scheduler.playQueue(firstTrack);

						textChannel.sendMessage(
								String.format("Added playlist with **%d** tracks. Now playing: **`%s`** by **`%s`**",
								              tracks.size(),                           // 전체 트랙 수
								              firstTrack.getInfo().title,              // 첫 번째 트랙의 제목
								              firstTrack.getInfo().author)             // 첫 번째 트랙의 저자
						                       ).queue();
					}
				}

				@Override
				public void noMatches() {
					// 검색어 또는 URL이 유효하지 않을 때
					textChannel.sendMessage("검색 결과를 찾을 수 없습니다: " + searchQuery).queue();
					return;
				}

				@Override
				public void loadFailed(FriendlyException e) {
					// 트랙 로드 중 실패했을 때
					textChannel.sendMessage("검색 트랙을 로드하는 중 오류가 발생했습니다: " + e.getMessage()).queue();
				}
			});
			return null;  // trackInfo에 대한 처리가 끝났으면 null 반환
		});
	}



	public static boolean hasGuildMusicManager(Guild guild) {
		return musicManager.containsKey(Long.parseLong(guild.getId()));
	}

	public static void removeGuildMusicManager(Guild guild) {
		musicManager.remove(Long.parseLong(guild.getId()));
	}

	// 파라미터 제거 메서드
	public static String sanitizeUrl(String url) {
		return url.split("\\?")[0];
	}
}
