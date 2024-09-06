//package com.lachata.config;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//
//// /* deprecated */
//
//
//public class ConfigLoader {
//
//	private final Properties botConfig;
//
//	public ConfigLoader(Properties botConfig) {
//		this.botConfig = botConfig;
//	}
//
//	public void loadingFromFile() {
//		// resource 폴더의 config.properties 파일 로드
//		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//			if (input == null) {
//				System.out.println("Sorry, unable to find config.properties");
//				return;
//			}
//			// properties 파일 로드
//			this.botConfig.load(input);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public String loadToken() {
//		return botConfig.getProperty("DISCORD_BOT_TOKEN");
//	}
//
//}
