package com.lachata.utils;

import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class EmbedUtils {
	public EmbedBuilder createQueueEmbed(MusicQueue nowPlayinLinst) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Now Playing").setDescription("현재 재생 중인 트랙 목록");

		// MusicQueue에서 대기열 리스트를 가져옵니다.
		List<MusicInfo> queue = nowPlayinLinst.getQueueList();

		if (queue.isEmpty()) {
			builder.addField("대기열이 비어 있습니다.", "", false);
			return builder;
		}

		// 최대 30개의 트랙을 임베드에 추가
		for (int i = 0; i < queue.size() && i < 30; i++) {
			MusicInfo trackInfo = queue.get(i);
			String trackTitle = trackInfo.getTitle();
			String trackArtist = trackInfo.getArtist();
			long trackDuration = trackInfo.getRuntime(); // 트랙 길이를 가져옵니다.

			// 트랙의 길이를 MM:SS 형식으로 변환
			String trackDurationFormatted = String.format("%02d:%02d",
			                                              (trackDuration / 1000) / 60,
			                                              (trackDuration / 1000) % 60
			                                             );

			// 트랙 정보를 필드로 추가 (제목, 아티스트, 길이)
			builder.addField(
					(i + 1) + ". " + trackTitle, // 트랙 번호와 제목
					"올린 이 : " + trackArtist + " | 길이 : " + trackDurationFormatted,
					false
			                );
		}

		// 만약 대기열에 30개 이상의 트랙이 있으면 추가 트랙이 있음을 알림
		if (queue.size() > 30) {
			builder.addField("...", "그 외 " + (queue.size() - 30) + "개의 트랙이 있습니다.", false);
		}

		return builder;
	}

	public EmbedBuilder createNowPlayingEmbed(AudioTrack currentTrack, long currentSec) {
		EmbedBuilder builder = new EmbedBuilder();

		if (currentTrack == null) {
			builder.setTitle("Now Playing").setDescription("현재 재생 중인 곡이 없습니다.");
		} else {
			String trackTitle = currentTrack.getInfo().title;
			String trackArtist = currentTrack.getInfo().author;
			long trackDuration = currentTrack.getInfo().length;

			// 트랙 길이와 현재 재생 위치를 MM:SS 형식으로 변환
			String trackDurationFormatted = String.format("%02d:%02d",
			                                              (trackDuration / 1000) / 60,
			                                              (trackDuration / 1000) % 60
			                                             );
			String currentPositionFormatted = String.format("%02d:%02d",
			                                                (currentSec / 1000) / 60,
			                                                (currentSec / 1000) % 60
			                                               );

			// 현재 위치 퍼센트 계산
			int progress = (int) ((double) currentSec / trackDuration * 30);

			// 진행 바 생성 (30개 길이)
			StringBuilder progressBar = new StringBuilder();
			for (int i = 0; i < 30; i++) {
				if (i == progress) {
					progressBar.append("@");  // 현재 위치
				} else if (i < progress) {
					progressBar.append("=");  // 진행된 부분
				} else {
					progressBar.append("-");  // 남은 부분
				}
			}

			builder.setTitle("Now Playing")
					.setDescription("현재 재생 중인 트랙 정보")
					.addField("제목", trackTitle, false)
					.addField("올린 이", trackArtist, false)
					.addField("현재 재생위치", currentPositionFormatted + " / " + trackDurationFormatted, false)
					.addField("진행 상황", progressBar.toString(), false);
		}

		return builder;
	}
}
