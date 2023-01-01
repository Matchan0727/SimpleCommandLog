package jp.simplespace.simplecommandlog.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jp.simplespace.simplecommandlog.ConfigData;
import jp.simplespace.simplecommandlog.redisbungee.VCommandLogListener;
import jp.simplespace.simplecommandlog.redisbungee.VToggleListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog.noPermission;
import static jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog.prefix;

public class VCmdLog implements RawCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text().append(prefix).append(Component.text("このコマンドはプレイヤーのみ実行できます。",NamedTextColor.RED)).build());
        } else {
            Player p = (Player) source;
            ConfigData configData = VSimpleCommandLog.getConfigData();
            if (p.hasPermission("scl.command.scl")) {
                List<String> list = configData.getCmdlog().get("players");
                if(list==null){
                    list=new ArrayList<>();
                }
                if (list.contains(p.getUniqueId().toString())) {
                    if(!VSimpleCommandLog.enableRedisBungee){
                        list.remove(p.getUniqueId().toString());
                        p.sendMessage(Component.text().append(prefix).append(Component.text( "コマンドログ表示を無効にしました。",NamedTextColor.GRAY)).build());
                    }
                    else {
                        try {
                            VToggleListener.sendChannelMessage(p.getUniqueId().toString(),false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if(!VSimpleCommandLog.enableRedisBungee){
                        list.add(p.getUniqueId().toString());
                        p.sendMessage(Component.text().append(prefix).append(Component.text("コマンドログ表示を有効にしました。",NamedTextColor.GRAY)).build());
                    }
                    else {
                        try {
                            VToggleListener.sendChannelMessage(p.getUniqueId().toString(),true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if(!VSimpleCommandLog.enableRedisBungee){
                    if(!configData.cmdlog.containsKey("players")) configData.getCmdlog().put("players",list);
                    else configData.getCmdlog().replace("players",list);
                    VSimpleCommandLog.saveConfig(configData);
                }
            } else p.sendMessage(noPermission);
        }
    }

    //コマンドログをログ表示を有効にしているプレイヤーに送信。
    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if(!(event.getCommandSource() instanceof Player)) return;
        Player sender = (Player) event.getCommandSource();
        if(!VSimpleCommandLog.enableRedisBungee){
            TextComponent component = createTextComponent(sender.getCurrentServer().get().getServerInfo().getName(),sender.getUsername(),event.getCommand());
            VSimpleCommandLog.getLogger().info(component.content());
            sendCommandLogMessage(component);
        }
        else {
            try {
                VCommandLogListener.sendChannelMessage(sender.getCurrentServer().get().getServerInfo().getName(),sender.getUsername(),event.getCommand());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static TextComponent createTextComponent(String serverName, String senderName, String message){
        return Component.text("[CL] " + senderName + "@" + serverName + " /" + message, NamedTextColor.GRAY);
    }
    public static void sendCommandLogMessage(TextComponent component){
        ProxyServer proxy = VSimpleCommandLog.getServer();
        ConfigData data = VSimpleCommandLog.getConfigData();
        List<String> list = new ArrayList<>(data.getCmdlog().get("players"));
        for (String puuid : list) {
            Optional<Player> p = proxy.getPlayer(UUID.fromString(puuid));
            if (p.isPresent()){
                p.get().sendMessage(component);
            }
        }
    }
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("scl.command.scl");
    }
}
