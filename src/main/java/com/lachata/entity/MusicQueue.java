package com.lachata.entity;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;

public class MusicQueue {
	private final ConcurrentLinkedQueue<MusicInfo> playingQueue; // FIFO 구조

	public MusicQueue() {
		this.playingQueue = new ConcurrentLinkedQueue<>();
	}

	// 대기열에 새로운 트랙 추가
	public void addQueue(MusicInfo musicInfo) {
		int maxSize = 30;
		if (playingQueue.size() < maxSize) {
			playingQueue.add(musicInfo);
		} else {
			System.out.println("대기열이 가득 찼습니다.");
		}
	}

	// 첫 번째 트랙 가져오기
	public AudioTrack getFirstTrack() {
		return playingQueue.isEmpty() ? null : playingQueue.peek().getTrack();
	}

	// 현재 재생 중인 트랙 반환
	public MusicInfo getNowPlaying() {
		return playingQueue.peek();
	}

	public void clearMusicQueue() {
		playingQueue.clear();
	}

	// 삭제하고 다음 트랙을 가져오기
	public AudioTrack nextTrack() {
		playingQueue.poll(); // 현재 트랙을 삭제
		return playingQueue.isEmpty() ? null : playingQueue.peek().getTrack();
	}

	// 대기열의 모든 트랙 목록 가져오기
	public List<MusicInfo> getQueueList() {
		return new ArrayList<>(playingQueue); // 방어적 복사
	}

	// 대기열의 크기 확인
	public int getQueueSize() {
		return playingQueue.size();
	}

	public AudioTrack peekNextTrack() {
		return playingQueue.isEmpty() ? null : playingQueue.peek().getTrack();
	}
}
