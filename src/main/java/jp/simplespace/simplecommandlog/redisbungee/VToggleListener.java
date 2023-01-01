package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.JsonObject;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import jp.simplespace.simplecommandlog.ConfigData;
import jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VToggleListener extends ToggleListener{
    @Subscribe
    public void onVelocityPubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_toggle")){
            return;
        }
        JsonObject jsonObject = gson.fromJson(event.getMessage(),JsonObject.class);
        String uuid = jsonObject.get("uuid").getAsString();
        boolean boo = jsonObject.get("boolean").getAsBoolean();
        ConfigData configData = VSimpleCommandLog.getConfigData();
        List<String> list = configData.getCmdlog().get("players");
        if(list==null){
            list=new ArrayList<>();
        }
        Optional<Player> p = VSimpleCommandLog.getServer().getPlayer(UUID.fromString(uuid));
        if(boo){
            list.add(uuid);
            p.ifPresent(player -> player.sendMessage(Component.text().append(VSimpleCommandLog.prefix).append(Component.text("コマンドログ表示を有効にしました。", NamedTextColor.GRAY)).build()));
        }
        else {
            list.remove(uuid);
            if(p.isPresent()){
                p.ifPresent(player -> player.sendMessage(Component.text().append(VSimpleCommandLog.prefix).append(Component.text("コマンドログ表示を無効にしました。", NamedTextColor.GRAY)).build()));
            }
        }
        if(!configData.cmdlog.containsKey("players")) configData.getCmdlog().put("players",list);
        else configData.getCmdlog().replace("players",list);
        VSimpleCommandLog.saveConfig(configData);
    }
}
