package com.lachata.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.ConcurrentHashMap;

public class LavaMusicManager {
	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final ConcurrentHashMap<Long, GuildMusicManager> musicManager = new ConcurrentHashMap<>();
	public static synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
		return musicManager.computeIfAbsent(guildId, id -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
			return guildMusicManager;
		});
	}

	public static void loadAndPlay(final Guild guild, final AudioTrack track) {
		GuildMusicManager musicManager = getGuildMusicManager(guild);

		musicManager.scheduler.playQueue(track);
	}

	public static boolean hasGuildMusicManager(Guild guild) {
		return musicManager.containsKey(Long.parseLong(guild.getId()));
	}

	public static void removeGuildMusicManager(Guild guild) {
		musicManager.remove(Long.parseLong(guild.getId()));
	}
}


