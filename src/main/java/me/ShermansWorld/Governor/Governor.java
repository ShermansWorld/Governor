package me.ShermansWorld.Governor;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.ShermansWorld.Governor.chainofcommand.InactiveTownListener;
import me.ShermansWorld.Governor.commands.GovernorCommands;
import me.ShermansWorld.Governor.commands.GovernorTabCompletion;
import me.ShermansWorld.Governor.config.Config;
import me.ShermansWorld.Governor.incometax.ChestShopListener;
import me.ShermansWorld.Governor.incometax.IncomeTaxData;
import me.ShermansWorld.Governor.incometax.IncomeTaxListener;
import me.ShermansWorld.Governor.incometax.JobsListener;
import me.ShermansWorld.Governor.incometax.QuickShopListener;
import me.ShermansWorld.Governor.taxcalls.NationTaxCommands;
import me.ShermansWorld.Governor.taxcalls.NationTaxSession;
import me.ShermansWorld.Governor.taxcalls.NationTaxTabCompletion;
import me.ShermansWorld.Governor.taxcalls.TownTaxCommands;
import me.ShermansWorld.Governor.taxcalls.TownTaxSession;
import me.ShermansWorld.Governor.taxcalls.TownTaxTabCompletion;
import net.milkbowl.vault.economy.Economy;

public class Governor extends JavaPlugin {

	public static Governor instance;
	public static Economy economy = null;

	public static IncomeTaxData incomeTaxData;

	public static ArrayList<TownTaxSession> townTaxSessions = new ArrayList<TownTaxSession>();
	public static ArrayList<NationTaxSession> nationTaxSessions = new ArrayList<NationTaxSession>();
	public static HashMap<String, Double> incomeTownTaxMap = new HashMap<String, Double>();
	public static HashMap<String, Double> incomeNationTaxMap = new HashMap<String, Double>();

	public static Governor getInstance() {
		return Governor.instance;
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

	private void initData() {
		incomeTaxData = new IncomeTaxData(this);
		try {
			for (String key : incomeTaxData.getConfig().getConfigurationSection("Towns").getKeys(false)) {
				incomeTownTaxMap.put(key, incomeTaxData.getConfig().getDouble("Towns." + key));
			}
		} catch (NullPointerException e) {
		}
		try {
			for (String key : incomeTaxData.getConfig().getConfigurationSection("Nations").getKeys(false)) {
				incomeNationTaxMap.put(key, incomeTaxData.getConfig().getDouble("Nations." + key));
			}
		} catch (NullPointerException e) {
		}
	}
	
	private void enableHooks() {
		if (Bukkit.getServer().getPluginManager().getPlugin("QuickShop") != null) {
			  Bukkit.getLogger().info("[Governor] QuickShop detected! Enabling support...");
			  Bukkit.getPluginManager().registerEvents((Listener) new QuickShopListener(), (Plugin) this);
		}
		if (Bukkit.getServer().getPluginManager().getPlugin("ChestShop") != null) {
			  Bukkit.getLogger().info("[Governor] ChestShop detected! Enabling support...");
			  Bukkit.getPluginManager().registerEvents((Listener) new ChestShopListener(), (Plugin) this);
		}
		if (Bukkit.getServer().getPluginManager().getPlugin("ChestShop") != null) {
			  Bukkit.getLogger().info("[Governor] Jobs detected! Enabling support...");
			  Bukkit.getPluginManager().registerEvents((Listener) new JobsListener(), (Plugin) this);
		}
	}

	@Override
	public void onEnable() { // What runs when you start server
		// setup main
		Governor.instance = this;

		// initialize config
		saveDefaultConfig();
		Config.initConfigVals();

		// initialize custom data files
		initData();

		// initialize economy
		if (!setupEconomy()) {
			Bukkit.getLogger().severe(Helper.Chatlabel()
					+ " Error when setting up economy. Check to make sure Vault is installed properly!");
		}

		// initialize commands
		new GovernorCommands(this);
		new TownTaxCommands(this);
		new NationTaxCommands(this);

		// tab completion
		this.getCommand("governor").setTabCompleter(new GovernorTabCompletion());
		this.getCommand("ttax").setTabCompleter(new TownTaxTabCompletion());
		this.getCommand("ntax").setTabCompleter(new NationTaxTabCompletion());

		// Event Listeners
		Bukkit.getPluginManager().registerEvents((Listener) new IncomeTaxListener(), (Plugin) this);
		enableHooks();

		// Custom listeners
		if (Config.claimEnabled) { // if chain-of-command feature enabled
			InactiveTownListener.initListener();
		}
	}

	@Override
	public void onDisable() { // What runs when you start server
		Bukkit.getScheduler().cancelTasks(this);
	}

}
