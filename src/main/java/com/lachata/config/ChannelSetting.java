package com.lachata.config;

import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ChannelSetting {
	private final CopyOnWriteArrayList<Channel> channelList;
	//	private final int trackVolume;

	public ChannelSetting() {
		this.channelList = new CopyOnWriteArrayList<>();
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

	public CopyOnWriteArrayList<Channel> getChannelList() {
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

	public boolean checkChannel(Long channelId) {
		for(Channel channel : channelList) {
			if(channel.getIdLong() == channelId) {
				return true;
			}
		}
		return false;
	}
}
