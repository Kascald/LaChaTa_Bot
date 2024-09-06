package com.lachata.manager;

import com.lachata.config.BotSetting;
import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
	private final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
	private final AudioPlayer player;
	private final MusicQueue musicQueue;
	private final Guild guild;
//	private Timer leaveTimer;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> leaveTask;
	public TrackScheduler(AudioPlayer player, MusicQueue musicQueue, Guild guild) {
		this.player = player;
		this.musicQueue = musicQueue;
		this.guild = guild;
	}

	public void playQueue(AudioTrack track) {
		if(player.startTrack(track,true)) {
			musicQueue.getFirstTrack();
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		} else if (endReason == AudioTrackEndReason.FINISHED) { //곡 끝났음이면 스케쥴타이머 설정
			scheduleLeaveAfterDelay();
		}
	}


	//트랙 시작하면 타이머 취소
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
//		if(leaveTimer != null) {
//			leaveTimer.cancel();
//		}
		if (leaveTask != null) {
			leaveTask.cancel(false); // 기존 작업 취소
		}
	}

	// 딜레이 스케줄 설정
//	private synchronized void scheduleLeaveAfterDelay() {
//		if(leaveTimer != null) {
//			leaveTimer.cancel();
//		}
//
//		leaveTimer = new Timer();
//		leaveTimer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				leaveVoiceChannel();
//			}
//		},30 * 1000);
//	}
	public void scheduleLeaveAfterDelay() {
		if (leaveTask != null && !leaveTask.isDone()) {
			leaveTask.cancel(false); // 기존 작업 취소
		}

		// 30초 후에 실행될 작업 예약
		leaveTask = scheduler.schedule(() -> leaveVoiceChannel(), 30, TimeUnit.SECONDS);
	}

	// 음성 채널 떠나기
	public void leaveVoiceChannel() {
		AudioManager audioManager = guild.getAudioManager();
		if(audioManager.isConnected()) {
			audioManager.closeAudioConnection();
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
			logger.info("End of the queue, no more tracks."); //다음 트랙이 없음
			musicQueue.clearMusicQueue(); // 트랙 대기열 객체를 클리어함.
		}
	}

	public MusicInfo getNowPlayingMusic() {
		return musicQueue.getNowPlaying();
	}

	public MusicQueue getMusicQueue() {
		return musicQueue;
	}

	public Boolean isThereMoreTracks() {
		// 다음 트랙을 삭제하지 않고 확인하기 위해 peek() 사용
		AudioTrack nextTrack = musicQueue.peekNextTrack();
		if (nextTrack != null) {
			return nextTrack.isSeekable();  // null이 아닐 때만 isSeekable() 호출
		} else {
			return false;  // 다음 트랙이 없을 경우 false 반환
		}
	}
}
