package com.lachata.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

	private final Properties botConfig;

	public ConfigLoader(Properties botConfig) {
		this.botConfig = botConfig;
	}

	public void loadingFromFile() {
		// config.properties 파일 로드
		try {
			FileInputStream fis = new FileInputStream("config.properties");
			this.botConfig.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String loadToken() {
		return botConfig.getProperty("DISCORD_BOT_TOKEN");
	}

}
