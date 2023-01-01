package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.JsonObject;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import jp.simplespace.simplecommandlog.velocity.VCmdLog;
import jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog;

public class VCommandLogListener extends CommandLogListener{
    @Subscribe
    public void onVelocityPubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_cmdlog")){
            return;
        }
        JsonObject jsonObject = gson.fromJson(event.getMessage(),JsonObject.class);
        String serverName = jsonObject.get("server").getAsString();
        String senderName = jsonObject.get("sender").getAsString();
        String message = jsonObject.get("message").getAsString();
        net.kyori.adventure.text.TextComponent component = VCmdLog.createTextComponent(serverName,senderName,message);
        VSimpleCommandLog.getLogger().info(component.content());
        VCmdLog.sendCommandLogMessage(component);
    }
}
