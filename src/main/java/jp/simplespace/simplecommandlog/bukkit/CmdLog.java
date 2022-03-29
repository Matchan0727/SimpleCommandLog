package jp.simplespace.simplecommandlog.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.prefix;
import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.plugin;
import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.config;
import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.noPermission;

public class CmdLog implements CommandExecutor,Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。");
            return true;
        }
        else {
            Player p = (Player) sender;
            if(p.hasPermission("scl.command.scl")){
                List<String> list = config.getStringList("cmdlog.players");
                if (list.contains(p.getUniqueId().toString())){
                    list.remove(p.getUniqueId().toString());
                    p.sendMessage(prefix + ChatColor.GRAY + "コマンドログ表示を無効にしました。");
                }
                else {
                    list.add(p.getUniqueId().toString());
                    p.sendMessage(prefix + ChatColor.GRAY + "コマンドログ表示を有効にしました。");
                }
                config.set("cmdlog.players",list);
                plugin.saveConfig();
            }
            else p.sendMessage(noPermission);
        }
        return true;
    }
    //コマンドログをログ表示を有効にしているプレイヤーに送信。
    @EventHandler
    public void onProcessCommand(PlayerCommandPreprocessEvent e){
        List<String> list = config.getStringList("cmdlog.players");
        Player sender = e.getPlayer();
        for(String puuid : list){
            Player p = Bukkit.getPlayer(UUID.fromString(puuid));
            if(p!=null) p.sendMessage(ChatColor.GRAY + "[CL] @" + sender.getName() + " " + e.getMessage());
        }
    }
}
