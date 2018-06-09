import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
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
    
    // Handles the creation and getting of a IDiscordClient object for a token
    static IDiscordClient getBuiltDiscordClient(String token){

        // The ClientBuilder object is where you will attach your params for configuring the instance of your bot.
        // Such as withToken, setDaemon etc
        return new ClientBuilder()
                .withToken(token)
                .build();
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

        /*
        // The below example is written to demonstrate sending a message if you want to catch the RLE for logging purposes
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (RateLimitException e){
                System.out.println("Do some logging");
                throw e;
            }
        });
        */

    }

    static List<File> downloadAttachments(IMessage message) {
    	System.setProperty("http.agent", "Chrome");
    	List<File> attachedFiles = new ArrayList<>();
    	try {
			for(Attachment attachment : message.getAttachments()) {
				URL url = new URL(attachment.getUrl());
				File destination = new File("/remote/TheFungusAlumnus/src/main/resources/downloaded/"+attachment.getFilename());
				FileUtils.copyURLToFile(url, destination);
				attachedFiles.add(destination);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
    	return attachedFiles;
    }
    
    static void sendValidStamp(IChannel channel, File file) {
    	if(file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")) {
			try {
				BufferedImage validStampedImage = new ValidImage(file).getImage();
				File destination = new File("/remote/TheFungusAlumnus/src/main/resources/downloaded/valid_"+file.getName().substring(0, file.getName().indexOf("."))+".png");
				ImageIO.write(validStampedImage, "png", destination);
				sendFile(channel, destination);					
			} catch (IOException e) {
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
}