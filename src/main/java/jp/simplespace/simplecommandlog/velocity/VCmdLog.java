package jp.simplespace.simplecommandlog.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jp.simplespace.simplecommandlog.ConfigData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
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
                    list.remove(p.getUniqueId().toString());
                    p.sendMessage(Component.text().append(prefix).append(Component.text( "コマンドログ表示を無効にしました。",NamedTextColor.GRAY)).build());
                } else {
                    list.add(p.getUniqueId().toString());
                    p.sendMessage(Component.text().append(prefix).append(Component.text("コマンドログ表示を有効にしました。",NamedTextColor.GRAY)).build());
                }
                if(!configData.cmdlog.containsKey("players")) configData.getCmdlog().put("players",list);
                else configData.getCmdlog().replace("players",list);
                VSimpleCommandLog.saveConfig(configData);
            } else p.sendMessage(noPermission);
        }
    }

    //コマンドログをログ表示を有効にしているプレイヤーに送信。
    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if(!(event.getCommandSource() instanceof Player sender)) return;
        ProxyServer proxy = VSimpleCommandLog.getServer();

        ConfigData data = VSimpleCommandLog.getConfigData();
        List<String> list = new ArrayList<>(data.getCmdlog().get("players"));
        TextComponent component = Component.text("[CL] " + sender.getCurrentServer().get().getServerInfo().getName() + "@" + sender.getUsername() + " /" + event.getCommand(), NamedTextColor.GRAY);
        VSimpleCommandLog.getLogger().info(component.content());
        for (String puuid : list) {
            Player p = proxy.getPlayer(UUID.fromString(puuid)).get();
            if (p != null){
                p.sendMessage(component);
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("scl.command.scl");
    }
}
