package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import jp.simplespace.simplecommandlog.bungee.BCmdLog;
import jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public class CommandLogListener implements Listener {
    private final Gson gson = new Gson();
    @EventHandler
    public void onBungeePubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_cmdlog")){
            return;
        }
        JsonObject jsonObject = gson.fromJson(event.getMessage(),JsonObject.class);
        String serverName = jsonObject.get("server").getAsString();
        String senderName = jsonObject.get("sender").getAsString();
        String message = jsonObject.get("message").getAsString();
        TextComponent component = BCmdLog.createTextComponent(serverName,senderName,message);
        BSimpleCommandLog.getLog().info(component.getText());
        BCmdLog.sendCommandLogMessage(component);
    }
    @Subscribe
    public void onVelocityPubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_cmdlog")){
            return;
        }
    }
    public static String serializeJson(String server,String sender,String message) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(stringWriter));
        jsonWriter.beginObject()
                .name("server").value(server)
                .name("sender").value(sender)
                .name("message").value(message)
                .endObject()
                .close();
        return new String(stringWriter.getBuffer());
    }
    public static String sendChannelMessage(String server, String sender, String message) throws IOException{
        String json = serializeJson(server,sender,message);
        RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage("scl_cmdlog",json);
        return json;
    }
}
