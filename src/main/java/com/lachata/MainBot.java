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
import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.Properties;

public class MainBot {
	public static void main(String[] args) {
//		Properties properties = new Properties();
//		ConfigLoader botConfigLoader = new ConfigLoader(properties);
//		botConfigLoader.loadingFromFile();
//		String botToken = botConfigLoader.loadToken();

		// 환경 변수로부터 DISCORD_BOT_TOKEN 가져오기
		String botToken = System.getenv("BOT_TOKEN");



		EmbedUtils embedUtils = new EmbedUtils();
		CommandManager botCommandManager = new CommandManager(embedUtils);
//		botCommandManager.setCommands();

//		List<CommandData> commands = new CopyOnWriteArrayList<>();
//		commands.add(Commands.slash("재생", "음악 재생 : URL , 검색어  (유투브 기준) ")
//				             .addOption(OptionType.STRING, "url", "youtube URL", false)
//				             .addOption(OptionType.STRING,"keyword", "youtube search keyword",false)
//		            );
//		commands.add(Commands.slash("일시정지", "음악 정지"));
//		commands.add(Commands.slash("재개", "일시정지된 음악 재생"));
//		commands.add(Commands.slash("스킵", "음악 스킵"));
//		commands.add(Commands.slash("대기열", "추가한 대기열 목록보기"));
//		commands.add(Commands.slash("현재", "현재 재생중인 노래"));
//		commands.add(Commands.slash("볼륨", "음악 재생 볼륨 조절 ( 0 ~ 100 ) ")
//				             .addOption(OptionType.INTEGER, "volume","control volume",true));
//		commands.add(Commands.slash("나가", "음악채널 나감" ));
//		commands.add(Commands.slash("도움말", "명령어 도움말 호출"));
//		commands.add(Commands.slash("헬프", "명령어 도움말 호출"));
//		commands.add(Commands.slash("help", "명령어 도움말 호출"));
//

		JDA jda = JDABuilder.createDefault(botToken,
		                                   EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES))  // GUILD_VOICE_STATES 추가
				.setActivity(Activity.playing("Youtube Playing !도움말 !헬프 !help \n Slash 명령어 추가!"))
				.build();

//		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			if (jda != null) {
//				jda.shutdown();
//			}
//		}));

		jda.addEventListener(new SlashCommandHandler(embedUtils));
		jda.addEventListener(botCommandManager);
		jda.updateCommands().addCommands(botCommandManager.getCommands()).queue();



	}
}