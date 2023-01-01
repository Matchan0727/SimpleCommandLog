package jp.simplespace.simplecommandlog.redisbungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import jp.simplespace.simplecommandlog.ConfigData;
import jp.simplespace.simplecommandlog.bungee.BCmdLog;
import jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog;
import jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog.*;

public class ToggleListener {
    final Gson gson = new Gson();
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
