import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

	// Constants for use throughout the bot
	final static String BOT_PREFIX = "!";
	final static String BOT_NAME = "fungus";
	final static String BOT_TAG = "<@452314331800141824>";
	
	static void reactWithRegionalIndicators(IMessage message, String reaction) {
		reaction = reaction.toLowerCase();
		boolean uniqueCharacters = RandomUtils.hasUniqueCharacters(reaction);
		boolean allLetters = reaction.replaceAll("[a-z]| ", "").length()==0;
		
		if(uniqueCharacters && allLetters) {
			sendReaction(message, ReactionEmoji.of(RandomUtils.regionalIndicators.get(reaction.charAt(0))));
			for(int i=reaction.length()-1;i>1;i--) {
				sendReaction(message, ReactionEmoji.of(RandomUtils.regionalIndicators.get(reaction.charAt(i))));
			}
		}
	}
	
	// Handles the creation and getting of a IDiscordClient object for a token
	static IDiscordClient getBuiltDiscordClient(String token){

		// The ClientBuilder object is where you will attach your params for configuring the instance of your bot.
		// Such as withToken, setDaemon etc
		return new ClientBuilder()
				.withToken(token)
				.build();
	}

	static void sendReaction(IMessage message, ReactionEmoji emoji) {
		RequestBuffer.request(() -> {
			try{
				message.addReaction(emoji);
			} catch (DiscordException e){
				Logger.log(e);
			}
		});
	}
	
	// Helper functions to make certain aspects of the bot easier to use.
	static void sendMessage(IChannel channel, String message){
		RequestBuffer.request(() -> {
			try{
				channel.sendMessage(message);
			} catch (DiscordException e){
				Logger.log(e);
			}
		});
	}

	static List<File> downloadAttachments(IMessage message) {
		System.setProperty("http.agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		List<File> attachedFiles = new ArrayList<>();
		try {
			for(Attachment attachment : message.getAttachments()) {
				URL url = new URL(attachment.getUrl());
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
				conn.connect();
				
				File destination = new File("/remote/TheFungusAlumnus/src/main/resources/downloaded/"+attachment.getFilename());
				FileUtils.copyInputStreamToFile(conn.getInputStream(), destination);
				attachedFiles.add(destination);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		return attachedFiles;
	}

	static void sendStampImage(IChannel channel, File baseImage, File stampImage) {
		if(baseImage.getName().toLowerCase().endsWith(".png") || baseImage.getName().toLowerCase().endsWith(".jpg") || baseImage.getName().toLowerCase().endsWith(".jpeg")) {
			try {
				BufferedImage validStampedImage = new StampImage(baseImage, stampImage).getImage();
				String destinationStr = "/remote/TheFungusAlumnus/src/main/resources/downloaded/valid_"+baseImage.getName().substring(0, baseImage.getName().indexOf("."));
				File destinationJpg = new File(destinationStr+".jpg");
				ImageIO.write(validStampedImage, "jpg", destinationJpg);
				sendFile(channel, destinationJpg);
			} catch (Exception e) {
				Logger.log(e);
			}
		}			
	}

	static void sendBread(IChannel channel) {
		sendFile(channel, getRandomFile("/remote/TheFungusAlumnus/src/main/resources/bread/"));
	}

	static void sendToast(IChannel channel) {
		sendFile(channel, getRandomFile("/remote/TheFungusAlumnus/src/main/resources/toast/"));
	}

	static void sendWaluigi(IChannel channel) {
		sendFile(channel, new File("/remote/TheFungusAlumnus/src/main/resources/waluigi.gif"));
	}

	static void sendFile(IChannel channel, File file) {
		RequestBuffer.request(() -> {
			try{
				channel.sendFile(file);
			} catch (FileNotFoundException e){
				Logger.log(e);
			}
		});
	}

	private static File getRandomFile(String folder) {
		File directory = new File(folder);
		//  	System.out.println("getting random file from "+directory.toPath());
		File[] files = directory.listFiles();
		return files[new Random().nextInt(files.length)];
	}

	static void sendMePm(String message) {
		IChannel pmChannel = MainRunner.cli.fetchUser(164034386528960512L).getOrCreatePMChannel();
		sendMessage(pmChannel, message);
	}

	static void sendMorganPm(String message) {
		IChannel pmChannel = MainRunner.cli.fetchUser(226127828905754624L).getOrCreatePMChannel();
		sendMessage(pmChannel, message);
	}

	static void sendDynmapMessage(MessageReceivedEvent event) {
		try {
			URL tmpUrl = new URL("http://vidya.zapto.org/up/sendmessage");
			URL url = new URL(tmpUrl.getProtocol(), tmpUrl.getHost(), 8234, tmpUrl.getFile());
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			StringBuilder sb = new StringBuilder();
			sb.append("{\"name\":\"");
			sb.append(event.getAuthor().getName());
			sb.append("\",\"message\":\"");
			sb.append(event.getAuthor().getName() + ": " + event.getMessage().getFormattedContent());
			sb.append("\"}");

			byte[] out = sb.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			http.disconnect();
		} catch (Exception e) {
			Logger.log(e);
		}
	}
}