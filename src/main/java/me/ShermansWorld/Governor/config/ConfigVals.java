package me.ShermansWorld.Governor.config;

import java.util.List;

import me.ShermansWorld.Governor.Main;
import me.ShermansWorld.Governor.config.ConfigVals;

public class ConfigVals {
	
	// Config version
	public static int configVersion = 2; // default = up-to-date version
	
	// Taxing variables
	
	public static List<String> taxTownAllowedRanks;
	public static List<String> taxNationAllowedRanks;
	public static int maxTownTaxAmount = 1000; // default
	public static int maxNationTaxAmount = 1000; // default
	public static long maxTownTaxCallTime = 6000; // default = 5 min
	public static long maxNationTaxCallTime = 6000; // default = 5 min
	public static boolean townAskCaller = false; // default = false
	public static boolean nationAskCaller = false; // default = false
	
	// Chain-of-Command variables
	public static int daysInactive = 30; // default = 30 days
	public static boolean anyoneCanClaim = false; // default = false
	public static boolean anyTownMemberCanClaim = false; // default = false
	public static boolean anyNationMemberCanClaim = false; // default = false
	public static List<String> claimAllowedRanks;
	public static boolean claimEnabled = true; // default = true
	
	// Income Tax variables
	public static boolean incomeTaxEnabled = true; // default = true
	public static boolean incomeTaxChatMsgs = true; // default = true
	public static double defaultTownTax = 0.05; // default = 5%
	public static double defaultNationTax = 0.05; // default = 5%
	public static boolean taxEssentialsPay = true; // default = true
	public static List<String> incomeTaxTownAllowedRanks;
	public static List<String> incomeTaxNationAllowedRanks;
	public static double maxTaxRate = 0.5; // default = 50%
	// Income Tax hooks
	public static boolean chestShopIncomeEnabled = true; // default = true
	public static boolean jobsIncomeEnabled = true; // default = true
	public static boolean quickShopIncomeEnabled = true; // default = true
	
	public static void initConfigVals() {
		
		// config version
		configVersion = Main.getInstance().getConfig().getInt("config-version");
		
		// taxes
		taxTownAllowedRanks = Main.getInstance().getConfig().getStringList("TownTax.AllowedRanks");
		taxNationAllowedRanks = Main.getInstance().getConfig().getStringList("NationTax.AllowedRanks");
		maxTownTaxAmount = Main.getInstance().getConfig().getInt("TownTax.MaxTaxAmount");
		maxNationTaxAmount = Main.getInstance().getConfig().getInt("NationTax.MaxTaxAmount");
		maxTownTaxCallTime = Main.getInstance().getConfig().getLong("TownTax.CallTime");
		maxNationTaxCallTime = Main.getInstance().getConfig().getLong("NationTax.CallTime");
		townAskCaller = Main.getInstance().getConfig().getBoolean("TownTax.AskCaller");
		nationAskCaller = Main.getInstance().getConfig().getBoolean("NationTax.AskCaller");
		
		// chain of command
		daysInactive = Main.getInstance().getConfig().getInt("ChainOfCommand.InactiveTime");
		anyoneCanClaim = Main.getInstance().getConfig().getBoolean("ChainOfCommand.AnyoneCanClaim");
		anyTownMemberCanClaim = Main.getInstance().getConfig().getBoolean("ChainOfCommand.AnyTownMemberCanClaim");
		anyNationMemberCanClaim = Main.getInstance().getConfig().getBoolean("ChainOfCommand.AnyNationMemberCanClaim");
		claimAllowedRanks = Main.getInstance().getConfig().getStringList("ChainOfCommand.AllowedRanks");
		claimEnabled = Main.getInstance().getConfig().getBoolean("ChainOfCommand.Enabled");
		
		//income tax
		incomeTaxEnabled = Main.getInstance().getConfig().getBoolean("IncomeTax.Enabled");
		incomeTaxChatMsgs = Main.getInstance().getConfig().getBoolean("IncomeTax.EnableChatMessages");
		defaultTownTax = Main.getInstance().getConfig().getDouble("IncomeTax.DefaultTownTax");
		defaultNationTax = Main.getInstance().getConfig().getDouble("IncomeTax.DefaultNationTax");
		taxEssentialsPay = Main.getInstance().getConfig().getBoolean("IncomeTax.TaxEssentialsPay");
		incomeTaxTownAllowedRanks = Main.getInstance().getConfig().getStringList("IncomeTax.TownAllowedRanks");
		incomeTaxNationAllowedRanks = Main.getInstance().getConfig().getStringList("IncomeTax.NationAllowedRanks");
		maxTaxRate = Main.getInstance().getConfig().getDouble("IncomeTax.MaxTaxRate");
		//income tax hooks
		chestShopIncomeEnabled = Main.getInstance().getConfig().getBoolean("IncomeTax.EnableChestShopIncome");
		jobsIncomeEnabled = Main.getInstance().getConfig().getBoolean("IncomeTax.EnableJobsIncome");
		quickShopIncomeEnabled = Main.getInstance().getConfig().getBoolean("IncomeTax.EnableQuickShopIncome");
	}
	

}
