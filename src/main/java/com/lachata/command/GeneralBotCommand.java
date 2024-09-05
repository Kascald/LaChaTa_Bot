package com.lachata.command;

import com.lachata.entity.MusicInfo;
import com.lachata.entity.MusicQueue;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface GeneralBotCommand {
//	    this.commands.add(Commands.slash("재생", "음악 재생 : URL , 검색어  (유투브 기준) "));
//		this.commands.add(Commands.slash("정지", "음악 정지"));
//		this.commands.add(Commands.slash("스킵", "음악 스킵"));
//		this.commands.add(Commands.slash("대기열", "추가한 대기열 목록보기"));
//		this.commands.add(Commands.slash("현재", "현재 재생중인 노래"));
//		this.commands.add(Commands.slash("볼륨", "음악 재생 볼륨 조절 ( 0 ~ 100 ) "));

	void playingMusic();
	void musicStop();
	void musicSkip();
	MusicQueue nowQueue();
}
