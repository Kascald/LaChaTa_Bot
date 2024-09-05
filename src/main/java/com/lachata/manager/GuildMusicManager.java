package com.lachata.manager;

import com.lachata.utils.FormatChecker;
import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
	public final AudioPlayer audioPlayer;
	public final TrackScheduler scheduler;

	public GuildMusicManager(AudioPlayerManager manager) {
		FormatChecker formatChecker = new FormatChecker();
		MusicQueue trackList = new MusicQueue(formatChecker);

		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer , trackList);
		this.audioPlayer.addListener(this.scheduler);
	}

	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(audioPlayer);
	}

	//재생 정지
	public void pauseTrack() {
		audioPlayer.setPaused(true); // 트랙을 일시정지
	}

	public void resumeTrack() {
		audioPlayer.setPaused(false); // 일시정지된 트랙을 재개
	}

	public boolean isPaused() {
		return audioPlayer.isPaused(); // 현재 플레이어가 일시정지 상태인지 확인
	}
	//재생 건너뛰기 (스킵)

	// 현재 재생곡 반환


	// 현재 재생목록 반환
	public MusicQueue getMusicQueue() {
		return scheduler.getMusicQueue();
	}

	//볼륨 설정
	public void setVolume(int volume) {
		audioPlayer.setVolume(volume);
	}

}
