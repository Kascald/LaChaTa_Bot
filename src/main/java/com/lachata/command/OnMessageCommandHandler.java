package com.lachata.command;

import com.lachata.config.ChannelSetting;
import com.lachata.entity.MusicQueue;
import com.lachata.manager.GuildMusicManager;
import com.lachata.manager.LavaMusicManager;
import com.lachata.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
//import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OnMessageCommandHandler {
//	private static final List<String> commandDictionary = List.of("재생", "정지", "스킵", "대기열", "현재", "볼륨");
	private final Logger logger = LoggerFactory.getLogger(OnMessageCommandHandler.class);
	private final EmbedUtils embedUtils;

	public OnMessageCommandHandler(EmbedUtils embedUtils) {
		this.embedUtils = embedUtils;
	}

	public void handleCommand(MessageReceivedEvent mre, String commandName, String arguments) {
		logger.info("handleCommand detect - command: {} /  arg : {}", commandName , arguments );
		TextChannel channel = mre.getChannel().asTextChannel();
		Guild guild = mre.getGuild();

		switch(commandName){
			case "재생":
				inviteBotVoice(mre);
				LavaMusicManager.loadAndPlay(channel , guild, arguments);
				break;
			case "일시정지":
				if(!LavaMusicManager.isTrackPaused(guild)) {
					LavaMusicManager.pauseTrack(channel, guild);
					break;
				}

			case "재개":
				if(LavaMusicManager.isTrackPaused(guild)) {
					LavaMusicManager.resumeTrack(channel, guild);
					break;
				}

			case "스킵":
				LavaMusicManager.skipTrack(guild, channel);
				break;

			case "대기열":
				MusicQueue nowPlayinLinst = LavaMusicManager.nowQueueList(guild);
				channel.sendMessageEmbeds(
						embedUtils.createQueueEmbed(nowPlayinLinst).build()
				                         ).queue();
				break;

			case "현재":
				AudioTrack currentPlaying = LavaMusicManager.nowPlayingInfo(guild);
				long currentPosition = 0L;

				if(currentPlaying != null) {
					currentPosition = LavaMusicManager.nowPlayingLength(guild);

					channel.sendMessageEmbeds(
							embedUtils.createNowPlayingEmbed(currentPlaying,currentPosition).build()
					                         ).queue();
				} else { //현재 재생곡이 없는 경우
					channel.sendMessageEmbeds(
							embedUtils.nowPlayingNullEmbed().build()
					                         ).queue();
				}


				break;

			case "볼륨":
				int wannaVolume = Integer.parseInt(arguments);
				LavaMusicManager.setVolume(channel, guild, wannaVolume);
				break;

			case "나가":
				LavaMusicManager.clearPlayList(guild);
				guild.getAudioManager().closeAudioConnection();
				break;
		}
	}

	private void inviteBotVoice(MessageReceivedEvent mre) {
		if (mre.getMember() == null || mre.getMember().getVoiceState() == null) {
			logger.info("User Not in Voice Channel");
			// 사용자가 음성 채널에 참여하지 않으면 메시지를 출력
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
			return;
		}

		final AudioChannelUnion audioChannel = mre.getMember().getVoiceState().getChannel();

		if (audioChannel != null && audioChannel.getType().isAudio()) {
			// AudioChannelUnion을 VoiceChannel로 변환
			VoiceChannel voiceChannel = audioChannel.asVoiceChannel();
			logger.info("Voice Channel: {}  <- Bot join", voiceChannel.getName());

			// 봇을 해당 음성 채널에 연결
			Guild guild = mre.getGuild();
			guild.getAudioManager().openAudioConnection(voiceChannel);
		} else {
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
		}
	}

	public void handleChannelSetting(String message , MessageChannelUnion channel, Guild guild) {
		logger.info("handleChannelSetting detect - msg: {}", message);
		if (message.substring(3).startsWith("설정"))
			addChannel(channel,guild);
		if(message.substring(3).startsWith("보기"))
		    viewChannel(channel,guild);
	}


	private void addChannel( MessageChannelUnion messageChannelUnion, Guild guild) {

		Channel ch = messageChannelUnion.asTextChannel();
		final GuildMusicManager musicManager = LavaMusicManager.getGuildMusicManager(guild);

		musicManager.channelSetting.setChannelList(ch);

		logger.info("addChannel detect - ch: {}", ch);
		messageChannelUnion.sendMessage("입력하신 채널 설정이 완료되었습니다.").queue();
	}

	private void viewChannel(MessageChannelUnion messageChannelUnion, Guild guild) {
		logger.info("viewChannel detect");

		final GuildMusicManager musicManager = LavaMusicManager.getGuildMusicManager(guild);

		messageChannelUnion.sendMessage("설정된 채널 : "+ musicManager.channelSetting.toStringChannelList()).queue();
	}
}
