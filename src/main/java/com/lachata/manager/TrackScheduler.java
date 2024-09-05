package com.lachata.manager;

import com.lachata.entity.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	private final AudioPlayer player;
	private final MusicQueue musicQueue;

	public TrackScheduler(AudioPlayer player, MusicQueue musicQueue) {
		this.player = player;
		this.musicQueue = musicQueue;
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
		}
	}

	public void skipTrack() {
		AudioTrack track = musicQueue.getFirstTrack();
	}

	public void nextTrack() {
		AudioTrack nextTrack = musicQueue.nextTrack();
		if (nextTrack != null) {
			player.startTrack(nextTrack, false);  // 다음 트랙을 재생
		} else {
			System.out.println("End of the queue, no more tracks."); //다음 트랙이 없음
			musicQueue.clearMusicQueue(); // 트랙 대기열 객체를 클리어함.
		}
	}

	public MusicQueue getMusicQueue() {
		return musicQueue;
	}
}
