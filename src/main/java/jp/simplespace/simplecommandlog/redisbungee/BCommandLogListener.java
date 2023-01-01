package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.JsonObject;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import jp.simplespace.simplecommandlog.bungee.BCmdLog;
import jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BCommandLogListener extends CommandLogListener implements Listener {
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
}
