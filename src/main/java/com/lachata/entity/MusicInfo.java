package com.lachata.entity;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MusicInfo {
	private AudioTrack track;
	private String title;
	private String artist;
	private String runtime;
	private String priority; //play priority
	private boolean isSkip;  //

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
}
