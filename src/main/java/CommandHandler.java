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
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class CommandHandler {

	private Random random;
	private DynmapController dynmapController;

	// A static map of commands mapping from command string to the functional impl
	private static Map<String, Command> commandMap = new HashMap<>();

	// Statically populate the commandMap with the intended functionality
	public CommandHandler() {

		random = new Random();
//		dynmapController = new DynmapController();
//		dynmapController.activate();
		
		//a test command to ping the bot
		commandMap.put("ping", (event, args) -> {
			String[] pings = {"Hewwo?", "owo who's this?"};
			BotUtils.sendMessage(event.getChannel(), pings[new Random().nextInt(pings.length)]);
		});

		commandMap.put("info", (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), "I'm sorry, did you need something?");
		});

		commandMap.put("hello", (event, args) -> {
			BotUtils.sendBread(event.getChannel());
		});
		
//		commandMap.put("mcactivate", (event, args) -> {
//			dynmapController.activate();
//		});
//		
//		commandMap.put("mcdeactivate", (event, args) -> {
//			dynmapController.deactivate();
//		});
	}

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event){
		System.out.println(event.getAuthor().getName()+": "+event.getMessage().getContent());
		String message = event.getMessage().getContent();

		if(event.getChannel().equals(MainRunner.mcRelayChannel)) {
			BotUtils.sendDynmapMessage(event);
			return;
		}

		List<File> attachedFiles = BotUtils.downloadAttachments(event.getMessage());

		if(attachedFiles.size()>0 && event.getChannel().getName().equals("valid") || (message.contains(BotUtils.BOT_TAG) && message.toLowerCase().contains("valid"))) {
			if(random.nextInt(100)>15) { //15
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
		}

	}
}