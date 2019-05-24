package me.featureable.motdsetter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.skionz.pingapi.PingAPI;
import com.skionz.pingapi.PingEvent;
import com.skionz.pingapi.PingListener;

public class MOTDSetter extends JavaPlugin implements Listener {
	
private String pluginAuthors = "Author: " + getDescription().getAuthors().toString();
	
private String motd = "";
private String defaultmotd = "&4&lMOTDSETTER - CHANGE MOTD IN CONFIG.YML OR IN GAME";
private String motdheader = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "MOTDSetter" + ChatColor.DARK_GRAY + "]";

private FileConfiguration config;
private File cfile;

	@Override
	public void onEnable() {
		SpigotUpdater updater = new SpigotUpdater(this, 66903);
	    try {
	        if (updater.checkForUpdates())
	            getLogger().info("An update was found! New version: " + updater.getLatestVersion() + " download: " + updater.getResourceURL());
	    } catch (Exception e) {
	        getLogger().warning("Could not check for updates! Stacktrace:");
	        e.printStackTrace();
	    } 
	    
	    if (updater.getLatestVersion() == updater.getResourceURL()) {
	    	getLogger().info("WORKED");
	    }
		
		motdheader = ChatColor.translateAlternateColorCodes('&', motdheader);
		//getLogger().info(motdheader + " Starting MOTDSetter");
		getLogger().info(pluginAuthors);
		
		config = getConfig();
		cfile = new File(getDataFolder(), "config.yml");
		
		if (!cfile.exists()) {
			config.options().copyDefaults(true);
			saveConfig();
			motd = ChatColor.translateAlternateColorCodes('&', defaultmotd);
			config.set("motd", motd);
			saveConfig();
		} else {
			config.options().copyDefaults(true);
			saveConfig();
		}
		registerEvents();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setmotd")) {
			if (sender.hasPermission("motdsetter.set") | sender.hasPermission("motdsetter.*")) {
				if (args.length == 0) {
					sender.sendMessage(motdheader + ChatColor.DARK_RED + " You must enter an motd!");
				} else {
					motd = String.join(" ", args);
					motd = ChatColor.translateAlternateColorCodes('&', motd);
					
					config.set("motd", motd);
					saveConfig();
					sender.sendMessage(motdheader + ChatColor.GREEN + " You have set the motd to: " + config.getString("motd"));
				}
			} else {
				sender.sendMessage(motdheader + ChatColor.DARK_RED + " You do not have permission to execute this command!");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("motdhelp")) {
			if (sender.hasPermission("motdsetter.help")  | sender.hasPermission("motdsetter.*")) {
				sender.sendMessage(motdheader + ChatColor.GREEN + " HELP");
				sender.sendMessage(ChatColor.GREEN + "/setmotd - Set the MOTD.");
				sender.sendMessage(ChatColor.GREEN + "/motdreload - Reload the MOTDSetter config.");
				sender.sendMessage(ChatColor.GREEN + "/motdhelp - Bring up this help menu.");
			} else {
				sender.sendMessage(motdheader + ChatColor.DARK_RED + " You do not have permission to execute this command!");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("motdreload")) {
			if (sender.hasPermission("motdsetter.reload") | sender.hasPermission("motdsetter.*")) {
				config = YamlConfiguration.loadConfiguration(cfile);
				sender.sendMessage(motdheader + ChatColor.GREEN + " You reloaded the config.yml!");
			} else {
				sender.sendMessage(motdheader + ChatColor.DARK_RED + " You do not have permission to execute this command!");
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		e.setMotd(config.getString("motd"));
		//setPlayerCount();
	}
	
//	public void setPlayerCount() {
//		PingAPI.registerListener(new PingListener() {
//            public void onPing(PingEvent event) {
//                event.getReply().setMaxPlayers(5);
//                event.getReply().setOnlinePlayers(3);
//            }
//        });
//	}
}
