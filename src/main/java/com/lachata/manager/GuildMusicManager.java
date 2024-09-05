package com.lachata.manager;

import com.lachata.checker.FormatChecker;
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

	public MusicQueue getMusicQueue() {
		return scheduler.getMusicQueue();
	}

}
