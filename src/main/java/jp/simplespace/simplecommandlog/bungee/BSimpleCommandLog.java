package jp.simplespace.simplecommandlog.bungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import jp.simplespace.simplecommandlog.redisbungee.BCommandLogListener;
import jp.simplespace.simplecommandlog.redisbungee.BToggleListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

public class BSimpleCommandLog extends Plugin {
    public static String prefix = ChatColor.AQUA+"[BSCL] "+ChatColor.RESET;
    public static String noPermission = prefix+ChatColor.RED + "あなたに実行する権限はありません。";
    public static Configuration config;
    public static Plugin plugin;
    public static ProxyServer proxy;
    public static Logger logger;
    public static boolean enableRedisBungee = false;
    @Override
    public void onEnable() {
        plugin = this;
        proxy= getProxy();
        PluginManager pm = proxy.getPluginManager();
        logger=proxy.getLogger();
        //RedisBungeeの初期化
        enableRedisBungee = pm.getPlugin("RedisBungee")!=null;
        if(enableRedisBungee){
            RedisBungeeAPI rapi = RedisBungeeAPI.getRedisBungeeApi();
            rapi.registerPubSubChannels("scl_cmdlog");
            rapi.registerPubSubChannels("scl_toggle");
            pm.registerListener(this,new BCommandLogListener());
            pm.registerListener(this,new BToggleListener());
        }
        //コマンドの登録
        pm.registerCommand(this,new BCmdLog());
        pm.registerCommand(this,new BEval());
        pm.registerCommand(this,new PluginListCommand());
        //イベントリスナーの登録
        pm.registerListener(this,new BCmdLog());
        //configの生成
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(),"config.yml"));
            if(!config.getBoolean("eval",false)){
                config.set("eval",false);
                saveConfig(config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN+"プラグインが有効化されました！");
    }

    @Override
    public void onDisable() {
        logger.info("プラグインが無効化されました。");
    }
    public static Plugin getPlugin(){
        return plugin;
    }
    public void saveDefaultConfig(){
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveConfig(Configuration config) throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,new File(getDataFolder(),"config.yml"));
    }
    public static Logger getLog(){
        return logger;
    }
}
