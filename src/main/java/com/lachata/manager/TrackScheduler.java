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
			musicQueue.getStartQueue();
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}

	public void nextTrack() {
		player.startTrack(musicQueue.nextTrack(), false);
	}
    //대기열 가져오기
}
