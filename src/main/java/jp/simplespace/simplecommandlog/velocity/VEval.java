package jp.simplespace.simplecommandlog.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jp.simplespace.simplecommandlog.ConfigData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jp.simplespace.simplecommandlog.velocity.VSimpleCommandLog.noPermission;

public class VEval implements SimpleCommand {
    private static final Map<CommandSource,ScriptEngine> map = new HashMap<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if(!source.hasPermission("scl.command.eval")){
            source.sendMessage(noPermission);
        }
        ConfigData configData = VSimpleCommandLog.getConfigData();
        if(!configData.eval){
            source.sendMessage(Component.text("評価機能は無効化されています。\nconfig.ymlを確認してください。",NamedTextColor.RED));
            return;
        }
        ProxyServer proxy = VSimpleCommandLog.getServer();
        if(!map.containsKey(source)){
            ScriptEngine se = new NashornScriptEngineFactory().getScriptEngine(VSimpleCommandLog.class.getClassLoader());
            se.put("plugin", proxy.getPluginManager().getPlugin("simplecommandlog").get());
            se.put("invocation",invocation);
            se.put("proxy", proxy);
            se.put("Component","net.kyori.adventure.text.Component");
            se.put("NamedTextColor","net.kyori.adventure.text.format.NamedTextColor");
            se.put("source",source);
            if(source instanceof Player) se.put("player",(Player)source);
            se.put("config",configData);
            se.put("yaml",VSimpleCommandLog.getYaml());
            map.put(source,se);
        }
        ScriptEngine se = map.get(source);
        try
        {
            source.sendMessage(Component.text().append(Component.text("成功しました:\n",NamedTextColor.GREEN)).append(Component.text(String.valueOf(se.eval(String.join(" ",invocation.arguments()))),NamedTextColor.WHITE)).build());
        }
        catch(Exception e) {
            source.sendMessage(Component.text().append(Component.text("例外がスローされました:\n",NamedTextColor.RED)).append(Component.text(e.getLocalizedMessage(),NamedTextColor.WHITE)).build());
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("scl.command.eval");
    }
}
