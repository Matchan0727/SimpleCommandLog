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
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.*;
import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.noPermission;

public class TypeLog implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。");
            return true;
        }
        else {
            Player p = (Player) sender;
            if(p.hasPermission("scl.command.stl")){
                List<String> list = config.getStringList("typelog.players");
                if (list.contains(p.getUniqueId().toString())){
                    list.remove(p.getUniqueId().toString());
                    p.sendMessage(prefix + ChatColor.GRAY + "入力ログ表示を無効にしました。");
                }
                else {
                    list.add(p.getUniqueId().toString());
                    p.sendMessage(prefix + ChatColor.GRAY + "入力ログ表示を有効にしました。");
                }
                config.set("typelog.players",list);
                plugin.saveConfig();
            }
            else p.sendMessage(noPermission);
        }
        return true;
    }
    //入力ログをログ表示を有効にしているプレイヤーに送信。
    @EventHandler
    public void onType(TabCompleteEvent e){
        List<String> list = config.getStringList("typelog.players");
        CommandSender sender = e.getSender();
        for(String puuid : list){
            Player p = Bukkit.getPlayer(UUID.fromString(puuid));
            if(p!=null) p.sendMessage(ChatColor.GRAY + "[TL] @" + sender.getName() + " " + e.getBuffer());
        }
    }
}
