package com.lachata.manager;

import com.lachata.command.OnMessageCommandHandler;
import com.lachata.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager extends ListenerAdapter {
	private final Logger logger = LoggerFactory.getLogger(CommandManager.class);
	private final EmbedUtils embedUtils = new EmbedUtils();

	private final List<CommandData> commands = new ArrayList<>();
	private static final List<String> commandDictionary = List.of("재생", "일시정지", "재개" ,"스킵", "대기열", "현재", "볼륨","나가","도움말", "헬프", "help");
	private final OnMessageCommandHandler messageCommand;
	private CopyOnWriteArrayList<Channel> channels;

	public CommandManager(EmbedUtils embedUtils) {
		//	private final SlashCommandHandler slashCommand;
		//	this.slashCommand = new SlashCommandHandler();
		this.messageCommand = new OnMessageCommandHandler(embedUtils);
		this.channels = new CopyOnWriteArrayList<>();
		this.commands.add(Commands.slash("재생", "음악 재생 : URL , 검색어  (유투브 기준) "));
		this.commands.add(Commands.slash("일시정지", "음악 정지"));
		this.commands.add(Commands.slash("재개", "일시정지된 음악 재생"));
		this.commands.add(Commands.slash("스킵", "음악 스킵"));
		this.commands.add(Commands.slash("대기열", "추가한 대기열 목록보기"));
		this.commands.add(Commands.slash("현재", "현재 재생중인 노래"));
		this.commands.add(Commands.slash("볼륨", "음악 재생 볼륨 조절 ( 0 ~ 100 ) "));
		this.commands.add(Commands.slash("나가", "음악채널 나감" ));
		this.commands.add(Commands.slash("도움말", "명령어 도움말 호출"));
		this.commands.add(Commands.slash("헬프", "명령어 도움말 호출"));
		this.commands.add(Commands.slash("help", "명령어 도움말 호출"));

	}

	public List<CommandData> getCommands() {
		return commands;
	}

//	@Override //Will Update
//	public void onSlashCommandInteraction(SlashCommandInteractionEvent scie) {
//		super.onSlashCommandInteraction(scie);
//	}

	@Override
	public void onMessageReceived(MessageReceivedEvent mre) {

		final long messageFromChannel = mre.getChannel().getIdLong();
		final String message = mre.getMessage().getContentRaw();

		if(message.startsWith("!채널")) {
			//lavaplayer 에서 채널세팅 설정하기.
			final Guild guild = mre.getGuild();
			messageCommand.handleChannelSetting(message, mre.getChannel(), guild);
		}



		if (message.startsWith("!")) {
			final Guild guild = mre.getGuild();
			final GuildMusicManager guildMusicManager = LavaMusicManager.getGuildMusicManager(guild);
			this.channels = guildMusicManager.channelSetting.getChannelList();

			if(!guildMusicManager.channelSetting.checkChannel(messageFromChannel)) {
				return;
			}

			final String[] inputCommand = message.substring(1).split(" ", 2);
			if (commandDictionary.contains(inputCommand[0])) {
				final String commandName = inputCommand[0];
				final String arguments = inputCommand.length > 1 ? inputCommand[1] : "";

				if (commandName.matches("도움말|help|헬프")) {
					mre.getChannel().sendMessageEmbeds(embedUtils.commandHelpEmbed().build()).queue();
				}

				messageCommand.handleCommand(mre, commandName , arguments);
			}
		}
	}
}
