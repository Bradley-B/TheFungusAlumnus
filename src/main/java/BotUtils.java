import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
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

        // This might look weird but it'll be explained in another page.
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                sendMePm("help: "+e.getMessage()+"\n\n"+e.getStackTrace());
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
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
                sendMePm("help: "+e.getMessage()+"\n\n"+e.getStackTrace());
            	System.err.println("File could not be sent with error: ");
                e.printStackTrace();
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