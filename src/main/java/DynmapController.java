import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import java.util.LinkedList; 
import java.util.Queue; 

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import sx.blah.discord.handle.obj.IChannel;

public class DynmapController {

	private MessageQueue mq;
	
    private final JSONParser jsonParser = new JSONParser();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> scannerHandle;
			
    public DynmapController() {
		mq = new MessageQueue(); 
    }

	public void activate() {
		final Runnable scanner = new Runnable() {
            public void run() {
                String jsonContent = getMapContent();
				if(jsonContent!=null) {
					//System.out.println(jsonContent);
                    parseJson(jsonContent);
                }
            }
        };  
		scannerHandle = scheduler.scheduleAtFixedRate(scanner, 0, 2000, MILLISECONDS);
	}
	
	public void deactivate() {
		if(scannerHandle!=null) {
			scannerHandle.cancel(false);
		}
	}
	
    public String getMapContent() {
        try {
            URL tmpUrl = new URL("http://vidya.zapto.org/up/world/world/" + (System.currentTimeMillis() - 2100));
            URL url = new URL(tmpUrl.getProtocol(), tmpUrl.getHost(), 8234, tmpUrl.getFile());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (Exception e) {
			  //Logger.log(e);
        }
        return null;
    }

    public void parseJson(String json) {
        try {
            JSONObject head = (JSONObject) jsonParser.parse(json);
            JSONArray updateArr = (JSONArray) head.get("updates");
            JSONObject update;
            for(Object o : updateArr) {
                update = (JSONObject) o;
				
                if(update.get("type").equals("chat")) {
                    String playerName = (String) update.get("playerName");
                    if(!playerName.equals("129.21.90.52")) {
                        String message = (String) update.get("message");
						sendMcRelayMessage(playerName + ": " + message);
                    }
                } else if(update.get("type").equals("playerjoin")) {
                    String playerName = (String) update.get("playerName");
					sendMcRelayMessage(playerName + " joined");
                } else if(update.get("type").equals("playerquit")) {
                    String playerName = (String) update.get("playerName");
                    sendMcRelayMessage(playerName + " quit");
                }
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }
	
	public void sendMcRelayMessage(String message) {
		if(!mq.contains(message)) {
			BotUtils.sendMessage(MainRunner.mcRelayChannel, message);
			mq.add(message);
		}
	}
}
