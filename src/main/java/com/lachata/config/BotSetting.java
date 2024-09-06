package com.lachata.config;

import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BotSetting {
	private final List<Channel> channelList;
//	private final int trackVolume;

	public BotSetting() {
		this.channelList = new ArrayList<>();
//		this.trackVolume = 100;
	}

	public boolean checkChannel(long channelId) {
		if (channelList.isEmpty())
			return true; // 비어있다면 아직 채널설정을 안한 것으로 간주 true

		for (Channel channel : channelList) {
			return channelId == channel.getIdLong(); // 설정된 채널과 같다면 true
		}
		return false;  //설정 채널과 다름으로 간주하고 false
	}

	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(Channel channel) {
		channelList.add(channel);
	}

	public String toStringChannelList() {
		return channelList.stream()
				.map(Channel::getName)
				.collect(Collectors.joining(", "));
	}
}
