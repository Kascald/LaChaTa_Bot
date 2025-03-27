package com.lachata.command;

import com.lachata.entity.MusicQueue;
import com.lachata.manager.GuildMusicManager;
import com.lachata.manager.LavaMusicManager;
import com.lachata.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.PremiumRequiredCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/* update later */
public class SlashCommandHandler extends ListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandHandler.class);
	private final EmbedUtils embedUtils;

	public SlashCommandHandler(EmbedUtils embedUtils) {this.embedUtils = embedUtils;}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		LOGGER.info("handleCommand detect - command: {} /  arg : {}", event.getName() , event.getContext() );
		TextChannel channel = event.getChannel().asTextChannel();
		Guild guild = event.getGuild();

		String url = event.getOption("url") != null ? event.getOption("url").getAsString() : null;
		String keyword = event.getOption("keyword") != null ? event.getOption("keyword").getAsString() : null;
		String volume = event.getOption("volume") != null ? event.getOption("volume").getAsString() : null;

		String option;
		if(url != null) option = url;
		else if(keyword != null) option = keyword;
		else if(volume != null) option = volume;
		else option = "";

//		event.deferReply().queue();

		CompletableFuture.runAsync(() -> {
			switch (event.getName()) {
					case "재생":
						inviteBotVoice(event);
						LavaMusicManager.loadAndPlay(channel , guild, option);
						event.reply("재생정보는 아래 메시지를 확인하세요").setEphemeral(true).queue();
						break;

					case "일시정지":
						if(!LavaMusicManager.isTrackPaused(guild)) {
							LavaMusicManager.pauseTrack(channel, guild);
							event.reply("노래를 일시정지하였습니다.").setEphemeral(true).queue();
							break;
						}

					case "재개":
						if(LavaMusicManager.isTrackPaused(guild)) {
							LavaMusicManager.resumeTrack(channel, guild);
							event.reply("노래 재생을 재개합니다.").setEphemeral(true).queue();
							break;
						}

					case "스킵":
						LavaMusicManager.skipTrack(guild, channel);
						event.reply("스킵 실행! ").setEphemeral(true).queue();
						break;

					case "대기열":
						MusicQueue nowPlayinLinst = LavaMusicManager.nowQueueList(guild);
						channel.sendMessageEmbeds(
								embedUtils.createQueueEmbed(nowPlayinLinst).build()
						                         ).queue();
						event.reply("재생신청 곡 리스트 출력!").setEphemeral(true).queue();
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

						event.reply("현재 재생중인 곡 정보!").setEphemeral(true).queue();
						break;

					case "볼륨":

						int wannaVolume = Integer.parseInt(option);
						LavaMusicManager.setVolume(channel, guild, wannaVolume);
						event.reply("볼륨을 조절합니다!").setEphemeral(true).queue();
						break;

					case "나가":
						LavaMusicManager.clearPlayList(guild);
						guild.getAudioManager().closeAudioConnection();
						event.reply("봇 퇴장!").setEphemeral(true).queue();
						break;
				}
		}).thenRun(() ->{
//			event.getHook().editOriginal("").queue();
//			event.reply("").queue();
		});

	}


	private void inviteBotVoice(SlashCommandInteractionEvent mre) {
		if (mre.getMember() == null || mre.getMember().getVoiceState() == null) {
			LOGGER.info("User Not in Voice Channel");
			// 사용자가 음성 채널에 참여하지 않으면 메시지를 출력
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
			return;
		}
		mre.getMember().getVoiceState().getChannel();
		final AudioChannelUnion audioChannel = mre.getMember().getVoiceState().getChannel();

		if (audioChannel != null && audioChannel.getType().isAudio()) {
			// AudioChannelUnion을 VoiceChannel로 변환
			VoiceChannel voiceChannel = audioChannel.asVoiceChannel();
			LOGGER.info("Voice Channel: {}  <- Bot join", voiceChannel.getName());

			// 봇을 해당 음성 채널에 연결
			Guild guild = mre.getGuild();
			guild.getAudioManager().openAudioConnection(voiceChannel);
		} else {
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
		}
	}

	public void handleChannelSetting(String message , MessageChannelUnion channel, Guild guild) {
		LOGGER.info("handleChannelSetting detect - msg: {}", message);
		if (message.substring(3).startsWith("설정"))
			addChannel(channel,guild);
		if(message.substring(3).startsWith("보기"))
			viewChannel(channel,guild);
	}


	private void addChannel(MessageChannelUnion messageChannelUnion, Guild guild) {

		Channel ch = messageChannelUnion.asTextChannel();
		final GuildMusicManager musicManager = LavaMusicManager.getGuildMusicManager(guild);

		musicManager.channelSetting.setChannelList(ch);

		LOGGER.info("addChannel detect - ch: {}", ch);
		messageChannelUnion.sendMessage("입력하신 채널 설정이 완료되었습니다.").queue();
	}

	private void viewChannel(MessageChannelUnion messageChannelUnion, Guild guild) {
		LOGGER.info("viewChannel detect");

		final GuildMusicManager musicManager = LavaMusicManager.getGuildMusicManager(guild);

		messageChannelUnion.sendMessage("설정된 채널 : "+ musicManager.channelSetting.toStringChannelList()).queue();
	}


}
