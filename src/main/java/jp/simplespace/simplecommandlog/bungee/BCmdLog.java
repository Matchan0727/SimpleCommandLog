package jp.simplespace.simplecommandlog.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static jp.simplespace.simplecommandlog.bungee.BSimpleCommandLog.*;

public class BCmdLog extends Command implements Listener {
    public BCmdLog(){
        super("bscl","scl.command.scl","bcmdlog","bcl");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(new TextComponent(prefix + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。"));
            return;
        }
        else {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.hasPermission("scl.command.scl")){
                List<String> list = config.getStringList("cmdlog.players");
                if (list.contains(p.getUniqueId().toString())){
                    list.remove(p.getUniqueId().toString());
                    p.sendMessage(new TextComponent(prefix + ChatColor.GRAY + "コマンドログ表示を無効にしました。"));
                }
                else {
                    list.add(p.getUniqueId().toString());
                    p.sendMessage(new TextComponent(prefix + ChatColor.GRAY + "コマンドログ表示を有効にしました。"));
                }
                config.set("cmdlog.players",list);
                try {
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,new File(plugin.getDataFolder(),"config.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else p.sendMessage(new TextComponent(noPermission));
        }
    }
    @EventHandler
    public void onChat(ChatEvent event){
        if(!event.getMessage().startsWith("/")){
            return;
        }
        if(!(event.getSender() instanceof ProxiedPlayer)){
            return;
        }
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        TextComponent component = new TextComponent(ChatColor.GRAY + "[CL] "+sender.getServer().getInfo().getName()+"@" + sender.getName() + " " + event.getMessage());
        BSimpleCommandLog.getLog().info(component.getText());
        List<String> list = config.getStringList("cmdlog.players");
        for(String puuid : list){
            ProxiedPlayer p = proxy.getPlayer(UUID.fromString(puuid));
            if(p!=null) p.sendMessage(component);
        }
    }
}
