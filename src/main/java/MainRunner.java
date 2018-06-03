import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class MainRunner {

	static IDiscordClient cli;
	
    public static void main(String[] args){

        if(args.length != 1){
            System.out.println("Please enter the bots token as the first argument e.g java -jar thisjar.jar tokenhere");
            return;
        }

        cli = BotUtils.getBuiltDiscordClient(args[0]);

        // Register a listener via the EventSubscriber annotation which allows for organization and delegation of events
        cli.getDispatcher().registerListener(new CommandHandler());
        
        // Only login after all events are registered otherwise some may be missed.
        cli.login();
        
        try {Thread.sleep(5000);} catch (InterruptedException e) {}
        IGuild guild = cli.getGuilds().get(0);
        IChannel channel = guild.getChannelByID(452323794065948707L);
        channel.sendMessage("you called?");
    }

}