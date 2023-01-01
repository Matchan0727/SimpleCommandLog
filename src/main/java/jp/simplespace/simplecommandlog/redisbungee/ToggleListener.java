package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import jp.simplespace.simplecommandlog.bungee.BCmdLog;
import jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog.*;

public class ToggleListener implements Listener {
    private final Gson gson = new Gson();
    @EventHandler
    public void onBungeePubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_toggle")){
            return;
        }
        JsonObject jsonObject = gson.fromJson(event.getMessage(),JsonObject.class);
        String uuid = jsonObject.get("uuid").getAsString();
        boolean boo = jsonObject.get("boolean").getAsBoolean();
        List<String> list = config.getStringList("cmdlog.players");
        ProxiedPlayer p = BSimpleCommandLog.proxy.getPlayer(UUID.fromString(uuid));
        if(boo){
            list.add(uuid);
            if(p!=null){
                p.sendMessage(new TextComponent(prefix + ChatColor.GRAY + "コマンドログ表示を有効にしました。"));
            }
        }
        else {
            list.remove(uuid);
            if(p!=null){
                p.sendMessage(new TextComponent(prefix + ChatColor.GRAY + "コマンドログ表示を無効にしました。"));
            }
        }
        config.set("cmdlog.players",list);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,new File(plugin.getDataFolder(),"config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void onVelocityPubSubMessage(PubSubMessageEvent event){
        if(!event.getChannel().equals("scl_toggle")){
            return;
        }
    }
    public static String serializeJson(String uuid,boolean boo) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(stringWriter));
        jsonWriter.beginObject()
                .name("uuid").value(uuid)
                .name("boolean").value(boo)
                .endObject()
                .close();
        return new String(stringWriter.getBuffer());
    }
    public static String sendChannelMessage(String uuid,boolean boo) throws IOException{
        String json = serializeJson(uuid,boo);
        RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage("scl_toggle",json);
        return json;
    }
}
