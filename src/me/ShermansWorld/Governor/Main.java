package me.ShermansWorld.Governor;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	public static Main instance;
	public static Economy economy = null;
	
	public static ArrayList<TownTaxSession> townTaxSessions = new ArrayList<TownTaxSession>();
	public static ArrayList<NationTaxSession> nationTaxSessions = new ArrayList<NationTaxSession>();
	
	
	public static Main getInstance() {
        return Main.instance;
    }
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	@Override
	public void onEnable() { //What runs when you start server
		this.saveDefaultConfig();
		Main.instance = this;
		//initialize commands
		ConfigVals.initConfigVals();
		if(!setupEconomy()) {
			Bukkit.getLogger().severe(Helper.Chatlabel() + " Error when setting up economy. Check to make sure Vault is installed properly!");
		}
		
		// commands
		new GovernorCommands(this);
		new TownTaxCommands(this);
		new NationTaxCommands(this);
		
		// tab completion
		this.getCommand("governor").setTabCompleter(new GovernorTabCompletion());
		this.getCommand("ttax").setTabCompleter(new TownTaxTabCompletion());
		this.getCommand("ntax").setTabCompleter(new NationTaxTabCompletion());
		
		
		// Custom listeners
		InactiveTownListener.initListener();
	}
	
	@Override
	public void onDisable() { //What runs when you start server
		Bukkit.getScheduler().cancelTasks(this);
	}
	
}
