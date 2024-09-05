//package com.lachata.command;
//
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.entities.Entitlement;
//import net.dv8tion.jda.api.entities.Guild;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
//import net.dv8tion.jda.api.interactions.DiscordLocale;
//import net.dv8tion.jda.api.interactions.InteractionHook;
//import net.dv8tion.jda.api.interactions.commands.Command;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
//import net.dv8tion.jda.api.interactions.modals.Modal;
//import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
//import net.dv8tion.jda.api.requests.restaction.interactions.PremiumRequiredCallbackAction;
//import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
//
//import java.util.List;
//
//public class SlashCommandHandler implements SlashCommandInteraction {
//	@Override
//	public int getTypeRaw() {
//		return 0;
//	}
//
//	@Override
//	public String getToken() {
//		return "";
//	}
//
//	@Override
//	public Guild getGuild() {
//		return null;
//	}
//
//	@Override
//	public User getUser() {
//		return null;
//	}
//
//	@Override
//	public Member getMember() {
//		return null;
//	}
//
//	@Override
//	public boolean isAcknowledged() {
//		return false;
//	}
//
//	@Override
//	public MessageChannelUnion getChannel() {
//		return null;
//	}
//
//	@Override
//	public long getChannelIdLong() {
//		return 0;
//	}
//
//	@Override
//	public DiscordLocale getUserLocale() {
//		return null;
//	}
//
//	@Override
//	public List<Entitlement> getEntitlements() {
//		return List.of();
//	}
//
//	@Override
//	public JDA getJDA() {
//		return null;
//	}
//
//	@Override
//	public ModalCallbackAction replyModal(Modal modal) {
//		return null;
//	}
//
//	@Override
//	public PremiumRequiredCallbackAction replyWithPremiumRequired() {
//		return null;
//	}
//
//	@Override
//	public ReplyCallbackAction deferReply() {
//		return null;
//	}
//
//	@Override
//	public InteractionHook getHook() {
//		return null;
//	}
//
//	@Override
//	public Command.Type getCommandType() {
//		return null;
//	}
//
//	@Override
//	public String getName() {
//		return "";
//	}
//
//	@Override
//	public String getSubcommandName() {
//		return "";
//	}
//
//	@Override
//	public String getSubcommandGroup() {
//		return "";
//	}
//
//	@Override
//	public long getCommandIdLong() {
//		return 0;
//	}
//
//	@Override
//	public boolean isGuildCommand() {
//		return false;
//	}
//
//	@Override
//	public List<OptionMapping> getOptions() {
//		return List.of();
//	}
//
//	@Override
//	public long getIdLong() {
//		return 0;
//	}
//}
