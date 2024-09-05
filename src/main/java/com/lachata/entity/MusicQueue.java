package com.lachata.entity;

import com.lachata.checker.FormatChecker;
import com.lachata.exception.TypeParsingException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Getter @Setter
public class MusicQueue {
	private final ConcurrentHashMap<Integer , MusicInfo> playingQueue; //  { 재생번호 , 재생 곡 }
	private final FormatChecker formatChecker;
	private final int maxSize = 30;
	private int nowPlaying = 0;
	public MusicQueue(FormatChecker formatChecker) {
		this.formatChecker = formatChecker;
		this.playingQueue = new ConcurrentHashMap<>();
	}

	public MusicQueue(List<MusicInfo> wannaAddQueue, FormatChecker formatChecker) {
		this.formatChecker = formatChecker;
		this.playingQueue = new ConcurrentHashMap<>();
		AtomicInteger index = new AtomicInteger(0);

		wannaAddQueue.stream()
				.limit(maxSize)
				.forEach(musicInfo ->
						         playingQueue.put(index.getAndIncrement() , musicInfo)
				        );
	}

	public void addQueue(MusicInfo musicInfo) {
		int nextIndex = playingQueue.size();
		playingQueue.put(nextIndex , musicInfo);
	}

	public void deleteQueue(String musicIndex) {
		int wantedIndex;
		try {
			wantedIndex = Integer.parseInt(musicIndex);
			if(!formatChecker.integerCheck(wantedIndex)) throw new TypeParsingException("400", "Error at Parsing Index, Check Input Number");
			playingQueue.remove(wantedIndex);
		} catch (TypeParsingException err) {
			err.getMessage();
		}
	}

	public AudioTrack getStartQueue() {
		this.nowPlaying = 0;
		return playingQueue.get(0).getTrack();
	}

	public MusicInfo getNowPlaying() {
		return playingQueue.get(nowPlaying);
	}

	public AudioTrack nextTrack() {
		if (nowPlaying + 1 < playingQueue.size()) {
			nowPlaying++;
			return playingQueue.get(nowPlaying).getTrack();
		} else {
			System.out.println("End of Track List");
			return null;
		}
	}
}
