package com.lachata;

//import com.lachata.config.ConfigLoader;
import com.lachata.command.SlashCommandHandler;
import com.lachata.manager.CommandManager;
import com.lachata.utils.EmbedUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
//import java.util.Properties;

public class MainBot {
	public static void main(String[] args) {
//		Properties properties = new Properties();
//		ConfigLoader botConfigLoader = new ConfigLoader(properties);
//		botConfigLoader.loadingFromFile();
//		String botToken = botConfigLoader.loadToken();

		// 환경 변수로부터 DISCORD_BOT_TOKEN 가져오기
		String botToken = System.getenv("BOT_TOKEN");

		JDA jda = JDABuilder.createDefault(botToken,
		                                   EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES))  // GUILD_VOICE_STATES 추가
				.setActivity(Activity.playing("Youtube Playing !도움말 !헬프 !help \n Slash 명령어 추가!"))
				.build();

		EmbedUtils embedUtils = new EmbedUtils();
		CommandManager botCommandManager = new CommandManager(embedUtils);
//		botCommandManager.setCommands();

//		List<CommandData> cmd = new ArrayList<>();
//		cmd.add(Commands.slash("재생", "music play")
//				        .addOption(OptionType.STRING,"url", "youtube URL",false)
//				        .addOption(OptionType.STRING,"keyword", "youtube search keyword",false)
//		       );
//		cmd.add(Commands.slash("볼륨" , "volume 설정")
//				        .addOption(OptionType.INTEGER, "volume","control volume",true));
//		cmd.add(Commands.slash("일시정지","일시정지"));
//		cmd.add(Commands.slash("재개","일시정지 풀기"));
//		cmd.add(Commands.slash("스킵","현재 곡 넘기기"));
//		cmd.add(Commands.slash("대기열","현재 재생목록"));
//		cmd.add(Commands.slash("현재","지금 재생중인 곡"));
//		cmd.add(Commands.slash("나가","봇 나가기"));
//
//		jda.updateCommands()
//				.addCommands(cmd.toArray(new CommandData[0])) // 새로운 명령어 목록만 추가
//				.queue();
		jda.addEventListener(new SlashCommandHandler(embedUtils));
		jda.addEventListener(botCommandManager);
		jda.updateCommands().addCommands(botCommandManager.getCommands()).queue();


	}
}