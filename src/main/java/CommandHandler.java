import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vdurmont.emoji.Emoji;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.impl.obj.Role;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class CommandHandler {

	private IGuild guild;
	private Random random;
	private Map<Integer, IRole> roleMap = new HashMap<>();
	//private DynmapController dynmapController;

	// A static map of commands mapping from command string to the functional impl
	private static Map<String, Command> commandMap = new HashMap<>();

	// Statically populate the commandMap with the intended functionality
	public CommandHandler() {

		random = new Random();
//		dynmapController = new DynmapController();
//		dynmapController.activate();
		
		//a test command to ping the bot
//		commandMap.put("ping", (event, args) -> {
//			String[] pings = {"Hewwo?", "owo who's this?"};
//			BotUtils.sendMessage(event.getChannel(), pings[new Random().nextInt(pings.length)]);
//		});
//
//		commandMap.put("info", (event, args) -> {
//			BotUtils.sendMessage(event.getChannel(), "I'm sorry, did you need something?");
//		});
//
//		commandMap.put("hello", (event, args) -> {
//			BotUtils.sendBread(event.getChannel());
//		});

//		commandMap.put("mcactivate", (event, args) -> {
//			dynmapController.activate();
//		});
//		
//		commandMap.put("mcdeactivate", (event, args) -> {
//			dynmapController.deactivate();
//		});
	}

	@EventSubscriber
	public void onUserJoined(UserJoinEvent event) {
		event.getUser().getOrCreatePMChannel().sendMessage(
				"Welcome to the 303 alumni chat\n\n"
				+ "This is an alumni chat, so please only share this chat with other 303 alumni only. Alumni include anyone who is no longer in high school, and was on the team at any point in school. \n\n"
				+ "Reply to this PM with \"setyear {year}\" where {year} is your high school graduation year to be added to the correct role.\n\n"
				+ "Reply to this PM with \"setnick {nickname}\" where {nickname} is your desired nickname to be given a nickname. You can also set this in the discord app if you know how.\n\n"
				+ "~~By joining this server you relinquish your right to your immortal soul~~\n\n"
				+ "Here is a quick rundown of all the channels \n"
				+ "**#general** is where you can talk or share anything \n"
				+ "**#valid** Post text or pictures and ask \"valid?\" and TheFungusAlumnus will tell you if it is, in fact, valid.\n"
				+ "**#minecraft** The Nassons let us turn Audrey's world into our own. In the pinned messages you will find the server ip and dynmap address. Please join in and claim a spot of land as your own. Feel free to talk about the game in this chat or the relay chat. You can also talk to others on the server using the voice channel minecraft: talking edition\n"
				+ "**#minecraft-chat-relay** Is a hyper-amazing modern feat of engineering where any messages sent on the minecraft server are sent to this channel. Anything you type here will also be broadcast to the minecraft server.\n"
				+ "**#where-in-the-world-is-sarah** is a chat where you can post pictures of the rare Sarah Nasson. Try to be as creepy as possible. \n"
				+ "**#server-requests** is where you can request server additions\n"
				+ "**#bread-chat-iii** is a bread chat, share some whole grain memes in this chat. \n"
				+ "**#bot-spam** is bot spam\n"
				+ "**#alumni-photos** is where you can post any alumni related photos. \n"
				+ "**#announcements** is a dead chat that shall never be used, ever.\n\n"
				+ "\t - Bradley and Morgan");

		event.getUser().getOrCreatePMChannel().sendMessage(
				"\n\nYou can see these channels once you are given the member role. If you haven't been given the member role you can bother us on **#let-me-in**");
	}

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event){
		System.out.println(event.getAuthor().getName()+": "+event.getMessage().getContent());
		String message = event.getMessage().getContent();

		if(event.getChannel().equals(MainRunner.mcRelayChannel)) {
			BotUtils.sendDynmapMessage(event);
			return;
		}

		if(event.getChannel().isPrivate()) {
			String setyearCommand = "setyear ";
			String setnickCommand = "setnick ";

			if(message.startsWith(setyearCommand)) {
				message = message.replace(setyearCommand, "");

				try {
					int year = Integer.parseInt(message);
					IRole role = roleMap.get(year);

					if(year==2004 || year==2018) {
						event.getAuthor().getOrCreatePMChannel().sendMessage("This role is locked. This incident has been reported, and you have been put on the naughty list.");
						BotUtils.sendMePm(event.getAuthor().getName() + " tried to join role year \"" + year + "\" but it failed.");
						BotUtils.sendMorganPm(event.getAuthor().getName() + " tried to join role year \"" + year + "\" but it failed.");
						return;
					}

					if(role==null) {
						event.getAuthor().getOrCreatePMChannel().sendMessage("That role year doesn't exist yet. Stay tuned.");
						BotUtils.sendMePm(event.getAuthor().getName() + " tried to join role year \"" + year + "\" but it failed.");
						BotUtils.sendMorganPm(event.getAuthor().getName() + " tried to join role year \"" + year + "\" but it failed.");
					} else {
						List<IRole> rolesUserAlreadyHas = event.getAuthor().getRolesForGuild(guild);
						rolesUserAlreadyHas.removeAll(roleMap.values());
						rolesUserAlreadyHas.add(role);
						guild.editUserRoles(event.getAuthor(), rolesUserAlreadyHas.toArray(new IRole[0]));
						event.getAuthor().getOrCreatePMChannel().sendMessage("you have been given role " + role.getName());
						BotUtils.sendMePm(event.getAuthor().getName() + " has been given role " + message);
						BotUtils.sendMorganPm(event.getAuthor().getName() + " has been given role " + message);
					}

				} catch (NumberFormatException e) {
					event.getAuthor().getOrCreatePMChannel().sendMessage("setyear must be a number. Example: " + setyearCommand + "1995");
					BotUtils.sendMePm(event.getAuthor().getName() + " tried to join role name " + message + " but it failed.");
					BotUtils.sendMorganPm(event.getAuthor().getName() + " tried to join role name " + message + " but it failed.");
				}

				return;
			} else if(message.startsWith(setnickCommand)) {
				message = message.replace(setnickCommand, "");
				IUser author = event.getAuthor();
				guild.setUserNickname(author, message);
				event.getAuthor().getOrCreatePMChannel().sendMessage("you have been given nickname " + message);
				return;
			}
		}

		List<File> attachedFiles = BotUtils.downloadAttachments(event.getMessage());

		if(attachedFiles.size()>0 && event.getChannel().getName().equals("valid") || (message.contains(BotUtils.BOT_TAG) && message.toLowerCase().contains("valid"))) {
			if(random.nextInt(100)>10) {
				BotUtils.sendMessage(event.getChannel(), "valid");
				attachedFiles.forEach(file -> BotUtils.sendStampImage(event.getChannel(), file, new File("/remote/TheFungusAlumnus/src/main/resources/valid.png")));
			} else {
				BotUtils.sendMessage(event.getChannel(), "**Enough is enough!  invalid!  invalid!  INVALID!!!!!!**");
				attachedFiles.forEach(file -> BotUtils.sendStampImage(event.getChannel(), file, new File("/remote/TheFungusAlumnus/src/main/resources/invalid.png")));
			}
		}

		if(message.contains(BotUtils.BOT_TAG) || (message.contains(BotUtils.BOT_NAME) && message.startsWith(BotUtils.BOT_PREFIX))) { //mentioned the bot
			if(message.toLowerCase().contains("hello")) {
				BotUtils.sendBread(event.getChannel());
			} else if(message.toLowerCase().contains("oast")) {
				BotUtils.sendToast(event.getChannel());
			} else if(message.toLowerCase().contains("valid")) {
				//this is handled by the other if statement
			} else if(message.toLowerCase().contains("ping")) {
				String[] pings = {"Hewwo?", "owo who's this?"};
				BotUtils.sendMessage(event.getChannel(), pings[new Random().nextInt(pings.length)]);
			} else {
				BotUtils.sendWaluigi(event.getChannel());
				//IMessage message2 = event.getMessage();
				//BotUtils.reactWithRegionalIndicators(message2, "abcdefg");
			}
		} else {
//			String[] argArray = event.getMessage().getContent().split(" ");
//
//			if(argArray.length < 2) return;
//			if(!argArray[0].startsWith(BotUtils.BOT_PREFIX+BotUtils.BOT_NAME)) return;
//
//			String commandStr = argArray[1];
//			List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
//			argsList.remove(0); // Remove the invocation
//
//			if(commandMap.containsKey(commandStr)) {
//				Command command = commandMap.get(commandStr);
//				command.runCommand(event, argsList);
//			}
		}

	}

	public void setGuild(IGuild guild) {
		this.guild = guild;
		populateYearRolesMap(guild);
	}

	private void populateYearRolesMap(IGuild guild) {
		Map<Integer, IRole> roleMap = new HashMap<>();
		List<IRole> roles = guild.getRoles();
		for(IRole role : roles) {
			String displayName = role.getName();
			displayName = displayName.replace("Class of ", "");

			try {
				int year = Integer.parseInt(displayName);
				roleMap.put(year, role);
			} catch (NumberFormatException e) {}

		}
		this.roleMap = roleMap;
	}

}