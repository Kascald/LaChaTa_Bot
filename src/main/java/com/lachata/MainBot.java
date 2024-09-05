package com.lachata;

import com.lachata.config.ConfigLoader;
import com.lachata.manager.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Properties;

public class MainBot {
	public static void main(String[] args) {
		Properties properties = new Properties();
		ConfigLoader botConfigLoader = new ConfigLoader(properties);

		botConfigLoader.loadingFromFile();
		String botToken = botConfigLoader.loadToken();

		JDA jda = JDABuilder.createDefault(botToken).build();

		CommandManager botCommandManager = new CommandManager();
//		botCommandManager.setCommands();

		jda.addEventListener(botCommandManager);
		jda.updateCommands().addCommands(botCommandManager.getCommands());

	}
}