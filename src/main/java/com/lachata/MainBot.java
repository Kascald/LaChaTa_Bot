package com.lachata;

//import com.lachata.config.ConfigLoader;
import com.lachata.manager.CommandManager;
import com.lachata.utils.EmbedUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;
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
				.setActivity(Activity.playing("Youtube Playing !도움말 !헬프 !help"))
				.build();

		EmbedUtils embedUtils = new EmbedUtils();
		CommandManager botCommandManager = new CommandManager(embedUtils);
//		botCommandManager.setCommands();

		jda.addEventListener(botCommandManager);

		jda.updateCommands().addCommands(botCommandManager.getCommands()).queue();


	}
}