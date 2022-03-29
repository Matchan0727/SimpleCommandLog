package jp.simplespace.simplecommandlog.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PluginListCommand extends Command {
    public PluginListCommand(){
        super("bungeeplugins","scl.command.plugins","bpl");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        StringBuilder sb = new StringBuilder();
        List<Plugin> list = new ArrayList<>(BSimpleCommandLog.proxy.getPluginManager().getPlugins());
        sb.append("Plugins (").append(list.size()).append("): ");
        for (int i = 0; i < list.size(); i++) {
            sb.append(ChatColor.GREEN+list.get(i).getDescription().getName()+ChatColor.RESET);
            if(i!=list.size()-1){
                sb.append(", ");
            }
        }
        sb.append("\n"+ChatColor.AQUA+"Provided by SimpleCommandLog");
        sender.sendMessage(new TextComponent(sb.toString()));
    }
}
