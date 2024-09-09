package com.lachata.manager;

import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
	private final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
	private final AudioPlayer player;
	private final MusicQueue musicQueue;
	private final Guild guild;
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledFuture;

	public TrackScheduler(AudioPlayer player, MusicQueue musicQueue, Guild guild) {
		this.player = player;
		this.musicQueue = musicQueue;
		this.guild = guild;
		this.scheduler = Executors.newScheduledThreadPool(1);  // 스케줄러 초기화
	}

	public void playQueue(AudioTrack track) {
		player.startTrack(track, true);
		musicQueue.getFirstTrack();
		checkStreamLoaded(track);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason == AudioTrackEndReason.FINISHED) {
			logger.info("Track finished playing.");
			nextTrack();  // 다음 트랙 재생
		} else if (!endReason.mayStartNext) {
			logger.info("No more tracks to play, scheduling bot to leave after delay.");
			scheduleLeaveAfterDelay();  // 더 이상 트랙이 없을 때 봇이 일정 시간 후 떠남
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		if (scheduledFuture != null && !scheduledFuture.isDone()) {
			scheduledFuture.cancel(true);  // 새로운 트랙이 시작되면 기존의 타이머 작업을 취소
			logger.info("Canceling scheduled leave as new track has started.");
		}
	}

	public void scheduleLeaveAfterDelay() {
		// 기존 타이머가 있다면 취소
		if (scheduledFuture != null && !scheduledFuture.isDone()) {
			logger.info("Canceling previous leave schedule.");
			scheduledFuture.cancel(true);
		}

		final long startTime = System.currentTimeMillis();  // 타이머 시작 시간 기록

		// 1초마다 경과 시간 로그를 출력하며 30초 후에 음성 채널을 떠남
		scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
			long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;  // 경과 시간 계산 (초 단위)
//			logger.info("Elapsed time: {} seconds", elapsedTime);

			if (elapsedTime >= 30) {  // 30초가 지나면 음성 채널을 떠남
				logger.info("Bot leaves... after Track End... 30 seconds.");
				leaveVoiceChannel();
				scheduledFuture.cancel(false);  // 타이머 종료
			}
		}, 0, 1, TimeUnit.SECONDS);  // 즉시 시작, 1초마다 실행
	}

	// 음성 채널 떠나기
	public void leaveVoiceChannel() {
		AudioManager audioManager = guild.getAudioManager();
		if (audioManager.isConnected()) {
			audioManager.closeAudioConnection();
		} else {
			logger.info("Bot was not connected to any voice channel.");
		}
	}

	public void skipTrack() {
		player.stopTrack();
		AudioTrack nextTrack = musicQueue.nextTrack();

		if (nextTrack != null) {
			player.startTrack(nextTrack, false);
		} else {
			scheduleLeaveAfterDelay();
		}
	}

	public void nextTrack() {
		AudioTrack nextTrack = musicQueue.nextTrack();
		if (nextTrack != null) {
			player.startTrack(nextTrack, false);  // 다음 트랙을 재생
		} else {
			logger.info("End of the queue, no more tracks.");  // 다음 트랙이 없음
			musicQueue.clearMusicQueue();  // 트랙 대기열 클리어
			scheduleLeaveAfterDelay();  // 더 이상 트랙이 없을 때 봇이 일정 시간 후 떠남
		}
	}

	public MusicQueue getMusicQueue() {
		return musicQueue;
	}

	public Boolean isThereMoreTracks() {
		// 다음 트랙을 삭제하지 않고 확인하기 위해 peek() 사용
		AudioTrack nextTrack = musicQueue.peekNextTrack();
		return nextTrack != null && nextTrack.isSeekable();
	}

	// 실제로 오디오가 재생될 수 있는지 확인
	private void checkStreamLoaded(AudioTrack track) {
		new Thread(() -> {
			try {
				int retries = 0;
				while (retries < 10) {
					AudioFrame frame = player.provide();
					if (frame != null) {
						logger.info("Stream is successfully loaded and playing: {}", track.getInfo().title);
						return;  // 스트림 로딩 성공
					}
					retries++;
					Thread.sleep(500);  // 0.5초마다 다시 확인
				}
				logger.warn("Stream did not load in time for track: {}", track.getInfo().title);
			} catch (InterruptedException e) {
				logger.error("Error while checking stream load status", e);
			}
		}).start();
	}

}
