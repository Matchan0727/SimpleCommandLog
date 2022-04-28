package jp.simplespace.simplecommandlog.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;


import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.config;
import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.plugin;

public class Eval implements CommandExecutor {
    private static final Map<CommandSender,ScriptEngine> map = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!config.getBoolean("eval",false)){
            sender.sendMessage(ChatColor.RED+"評価機能は無効化されています。\nconfig.ymlを確認してください。");
            return true;
        }
        if(!map.containsKey(sender)){
            ScriptEngine se = new NashornScriptEngineFactory().getScriptEngine();
            se.put("plugin", plugin);
            se.put("server", Bukkit.getServer());
            se.put("GameMode","org.bukkit.GameMode");
            se.put("EntityType","org.bukkit.entity.EntityType");
            se.put("ChatColor","org.bukkit.ChatColor");
            se.put("player",(Player)sender);
            map.put(sender,se);
        }
        ScriptEngine se = map.get(sender);
        try
        {
            sender.sendMessage(ChatColor.GREEN+"成功しました:\n"+ChatColor.RESET+se.eval(String.join(" ",args)));
        }
        catch(Exception e) {
            sender.sendMessage(ChatColor.RED+ "例外がスローされました:\n" +ChatColor.RESET+ e);

        }
        return true;
    }
}
