package jp.simplespace.simplecommandlog.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;

import static jp.simplespace.simplecommandlog.bukkit.SimpleCommandLog.config;
import static jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog.plugin;
import static jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog.proxy;

public class BEval extends Command {
    private static final Map<CommandSender, ScriptEngine> map = new HashMap<>();

    public BEval(){
        super("beval","scl.command.eval");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!config.getBoolean("eval")){
            sender.sendMessage(org.bukkit.ChatColor.RED+"評価機能は無効化されています。\nconfig.ymlを確認してください。");
            return;
        }
        if(!map.containsKey(sender)){
            ScriptEngine se = new NashornScriptEngineFactory().getScriptEngine();
            se.put("plugin", plugin);
            se.put("proxy", proxy);
            se.put("ChatColor","net.md_5.bungee.api.ChatColor");
            se.put("player",(ProxiedPlayer)sender);
            map.put(sender,se);
        }
        ScriptEngine se = map.get(sender);
        try
        {
            sender.sendMessage(new TextComponent(ChatColor.GREEN+"成功しました:\n"+ChatColor.RESET+se.eval(String.join(" ",args))));
        }
        catch(Exception e) {
            sender.sendMessage(new TextComponent(ChatColor.RED+ "例外がスローされました:\n" +ChatColor.RESET+ e));
        }
    }
}
