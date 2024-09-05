package com.lachata.entity;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MusicInfo {
	private String title;
	private String artist;
	private String runtime;
	private String priority; //play priority
	private boolean isSkip;  //
}
