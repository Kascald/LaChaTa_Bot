package com.lachata;

import com.lachata.config.ConfigLoader;

import java.util.Properties;

public class MainBot {
	public static void main(String[] args) {
		Properties properties = new Properties();
		ConfigLoader botConfigLoader = new ConfigLoader(properties);

		botConfigLoader.loadingFromFile();
		botConfigLoader.loadToken()

	}
}