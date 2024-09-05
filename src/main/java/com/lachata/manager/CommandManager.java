package com.lachata.manager;

import com.lachata.command.OnMessageCommandHandler;
//import com.lachata.command.SlashCommandHandler;
import com.lachata.config.BotSetting;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager extends ListenerAdapter {

	private final List<CommandData> commands = new ArrayList<>();
	private static final List<String> commandDictionary = List.of("재생", "정지", "스킵", "대기열", "현재", "볼륨");
	private final OnMessageCommandHandler messageCommand;
//	private final SlashCommandHandler slashCommand;
	private final BotSetting botSetting;

	public CommandManager() {
		this.messageCommand = new OnMessageCommandHandler();
//		this.slashCommand = new SlashCommandHandler();
		this.botSetting = new BotSetting();
		this.commands.add(Commands.slash("재생", "음악 재생 : URL , 검색어  (유투브 기준) "));
		this.commands.add(Commands.slash("정지", "음악 정지"));
		this.commands.add(Commands.slash("스킵", "음악 스킵"));
		this.commands.add(Commands.slash("대기열", "추가한 대기열 목록보기"));
		this.commands.add(Commands.slash("현재", "현재 재생중인 노래"));
		this.commands.add(Commands.slash("볼륨", "음악 재생 볼륨 조절 ( 0 ~ 100 ) "));
	}

//	@Override
//	public void onSlashCommandInteraction(SlashCommandInteractionEvent scie) {
//		super.onSlashCommandInteraction(scie);
//	}

	@Override
	public void onMessageReceived(MessageReceivedEvent mre) {
		final long messageFromChannel = mre.getChannel().getIdLong();
		if(!botSetting.checkChannel(messageFromChannel)) {
			return;
		}

		final String message = mre.getMessage().getContentRaw();

		if (message.startsWith("!")) {
			final String[] inputCommand = message.substring(1).split(" ", 2);

			if (commandDictionary.contains(inputCommand[0])) {
				final String commandName = inputCommand[0];
				final String arguments = inputCommand.length > 1 ? inputCommand[1] : "";

				if (!"".equals(arguments)) {
					messageCommand.handleCommand(mre, commandName , arguments);
				} else {
					mre.getChannel().sendMessage("알 수 없는 명령어 입니다.").queue();
				}
			}

		}
	}
}
