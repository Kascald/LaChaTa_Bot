package com.lachata.manager;

//import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {
	public final AudioPlayer audioPlayer;
	public final TrackScheduler scheduler;
	public final Guild guild;

	public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
		this.guild = guild;
		MusicQueue trackList = new MusicQueue();

		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer , trackList, guild);
		this.audioPlayer.addListener(this.scheduler);

		audioPlayer.setVolume(100);
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

	// 현재 재생곡 반환 (MusicQueue 기반)
	public AudioTrack getCurrentTrack() {
		// by Player
		AudioTrack nowTrack = audioPlayer.getPlayingTrack();

//		// by Queue
//		AudioTrack maybeNowTrack = scheduler.getNowPlayingMusic().getTrack();
//
//		if(nowTrack.getInfo().title.equals(maybeNowTrack.getInfo().title))
		return nowTrack;

//		return null;
	}

	public long currentTrackPosition() {
		return audioPlayer.getPlayingTrack().getPosition();
	}

	// 현재 재생목록 반환
	public MusicQueue getMusicQueue() {
		return scheduler.getMusicQueue();
	}

	//볼륨 설정
	public void setVolume(int volume) {
		audioPlayer.setVolume(volume);
	}
}
