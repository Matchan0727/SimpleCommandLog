package jp.simplespace.simplecommandlog.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCommandLog extends JavaPlugin {
    public static String prefix = ChatColor.AQUA+"[SCL] "+ChatColor.RESET;
    public static String noPermission = prefix+ChatColor.RED + "あなたに実行する権限はありません。";
    public static FileConfiguration config;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        //コマンドの登録
        getCommand("scl").setExecutor(new CmdLog());
        getCommand("eval").setExecutor(new Eval());
        //イベントリスナーの登録
        getServer().getPluginManager().registerEvents(new CmdLog(),this);

        //configの生成
        saveDefaultConfig();
        config = plugin.getConfig();
        if(!config.isBoolean("eval")){
            config.set("eval",false);
            saveConfig();

        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static Plugin getPlugin(){
        return plugin;
    }
}
