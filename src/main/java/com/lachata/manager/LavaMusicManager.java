package com.lachata.manager;

import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidTestsuite;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LavaMusicManager {
	private static final Logger logger = LoggerFactory.getLogger(LavaMusicManager.class);
	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final ConcurrentHashMap<Long, GuildMusicManager> musicManager = new ConcurrentHashMap<>();

	// YoutubeAudioSourceManager 인스턴스를 초기화합니다. 검색을 활성화하고 다양한 클라이언트를 추가합니다.
	private static final YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(
			/*allowSearch:*/ true,
			                 new Client[] {
					                 new Music(),
					                 new Web(),
					                 new AndroidTestsuite()
			                 }
	);

	static {
		// YoutubeAudioSourceManager를 등록합니다.
		playerManager.registerSourceManager(youtube);

		// 기타 소스 관리자를 등록합니다.
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}

	// 길드별 음악 관리자를 반환하거나 없으면 생성합니다.
	public static synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
		long guildId = Long.parseLong(guild.getId());

		return musicManager.computeIfAbsent(guildId, id -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager, guild);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
			return guildMusicManager;
		});
	}

	// URL 또는 검색어를 받아 트랙을 로드하고 재생합니다.
	public static void loadAndPlay(final TextChannel textChannel, final Guild guild, final String input) {
		loadUrlAndPlay(textChannel, guild, input);
	}

	// URL을 사용한 트랙 로드 및 재생.
	public static void loadUrlAndPlay(final TextChannel textChannel, final Guild guild, final String trackUrl) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		final MusicQueue musicQueue = musicManager.scheduler.getMusicQueue();

		// 주어진 URL 또는 검색어를 사용해 트랙을 로드합니다.
		playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				logger.info("Single track requested: {}" , audioTrack.getInfo().title);
				// 트랙을 대기열에 추가하고 재생
				MusicInfo musicInfo = new MusicInfo(audioTrack, audioTrack.getInfo().title, audioTrack.getInfo().author, audioTrack.getInfo().length, 0, false);
				musicQueue.addQueue(musicInfo);  // 대기열에 추가

				musicManager.scheduler.playQueue(audioTrack);

				textChannel.sendMessage(
						String.format("Added track **`%s`** by **`%s`**", audioTrack.getInfo().title, audioTrack.getInfo().author)
				                       ).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				logger.info("List Play requested");
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
							              tracks.size(), firstTrack.getInfo().title, firstTrack.getInfo().author)
					                       ).queue();
				}
			}

			@Override
			public void noMatches() {
				logger.info("No matches found for track **`{}`**", trackUrl);
				// 검색어 또는 URL이 유효하지 않을 때
				textChannel.sendMessage("일치하는 트랙을 찾지 못했습니다!").queue();
			}

			@Override
			public void loadFailed(FriendlyException e) {
				logger.info("Track load failed: {} " , e.getMessage());
				// 트랙 로드 중 실패했을 때
				textChannel.sendMessage("트랙을 로드하는 중 오류가 발생했습니다: ").queue(); // + e.getMessage()).queue();
			}
		});
	}

	// 특정 길드에 음악 관리자가 있는지 확인합니다.
	public static boolean hasGuildMusicManager(Guild guild) {
		return musicManager.containsKey(Long.parseLong(guild.getId()));
	}

	// 길드 음악 관리자를 제거합니다.
	public static void removeGuildMusicManager(Guild guild) {
		musicManager.remove(Long.parseLong(guild.getId()));
	}

	// 트랙 일시정지 메서드
	public static void pauseTrack(final TextChannel textChannel, final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		musicManager.pauseTrack();

		textChannel.sendMessage("현재 곡을 일시정지합니다.").queue();
	}

	// 트랙 재개 메서드
	public static void resumeTrack(final TextChannel textChannel, final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);

		if (musicManager.isPaused())
			musicManager.resumeTrack();

		textChannel.sendMessage("일시정지된 현재 곡을 재생합니다.").queue();
	}

	// 일시정지된 트랙 유무 확인
	public static boolean isTrackPaused(final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		return musicManager.isPaused();
	}


	// 현재 곡 스킵하기
	public static void skipTrack(final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		musicManager.scheduler.skipTrack();
	}

	// 대기열 목록 반환
	public static MusicQueue nowQueueList(final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		return musicManager.getMusicQueue();
	}

	// 현재 재생 곡 정보 반환
	public static AudioTrack nowPlayingInfo(final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);

		return musicManager.getCurrentTrack();
	}

	public static long nowPlayingLength(final Guild guild) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);
		return musicManager.currentTrackPosition();
	}


	// 플레이어 볼륨 설정
	public static void setVolume(final TextChannel textChannel, final Guild guild, int volume) {
		final GuildMusicManager musicManager = getGuildMusicManager(guild);

		if(musicManager.getCurrentTrack() != null && !musicManager.audioPlayer.isPaused()) { // 현재 재생곡이 있고, 일시정지가 아니라면.
			musicManager.pauseTrack();      // 일시 정지한 후
			musicManager.setVolume(volume); // 볼륨 조절하고
			musicManager.resumeTrack();     // 다시 재생 재개
		} else {  // 그 외 상태라면
			musicManager.setVolume(volume); // 볼륨 조절
		}
		textChannel.sendMessage(String.format("볼륨이 %d 로 설정되었습니다. ", volume)).queue();
	}

}
