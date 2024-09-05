package com.lachata.entity;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Objects;
public class MusicInfo {
	private AudioTrack track;
	private String title;
	private String artist;
	private long runtime;
	private int priority; //play priority
	private boolean isSkip;  //

	public MusicInfo(AudioTrack audioTrack, String title,String author, long length, int priority , boolean isSkip) {
		this.track = audioTrack;
		this.title = title;
		this.artist = author;
		this.runtime = length;
		this.priority = priority;
		this.isSkip = isSkip;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MusicInfo musicInfo)) return false;
		return isSkip == musicInfo.isSkip && Objects.equals(title, musicInfo.title) && Objects.equals(artist, musicInfo.artist) && Objects.equals(runtime, musicInfo.runtime) && Objects.equals(priority, musicInfo.priority);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, artist, runtime, priority, isSkip);
	}

	public AudioTrack getTrack() {
		return track;
	}

	public void setTrack(AudioTrack track) {
		this.track = track;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getRuntime() {
		return runtime;
	}

	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isSkip() {
		return isSkip;
	}

	public void setSkip(boolean skip) {
		isSkip = skip;
	}
}
