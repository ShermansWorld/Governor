package me.ShermansWorld.Governor.config;

import java.util.List;

import me.ShermansWorld.Governor.Governor;
import me.ShermansWorld.Governor.config.Config;

public class Config {
	
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
		configVersion = Governor.getInstance().getConfig().getInt("config-version");
		
		// taxes
		taxTownAllowedRanks = Governor.getInstance().getConfig().getStringList("TownTax.AllowedRanks");
		taxNationAllowedRanks = Governor.getInstance().getConfig().getStringList("NationTax.AllowedRanks");
		maxTownTaxAmount = Governor.getInstance().getConfig().getInt("TownTax.MaxTaxAmount");
		maxNationTaxAmount = Governor.getInstance().getConfig().getInt("NationTax.MaxTaxAmount");
		maxTownTaxCallTime = Governor.getInstance().getConfig().getLong("TownTax.CallTime");
		maxNationTaxCallTime = Governor.getInstance().getConfig().getLong("NationTax.CallTime");
		townAskCaller = Governor.getInstance().getConfig().getBoolean("TownTax.AskCaller");
		nationAskCaller = Governor.getInstance().getConfig().getBoolean("NationTax.AskCaller");
		
		// chain of command
		daysInactive = Governor.getInstance().getConfig().getInt("ChainOfCommand.InactiveTime");
		anyoneCanClaim = Governor.getInstance().getConfig().getBoolean("ChainOfCommand.AnyoneCanClaim");
		anyTownMemberCanClaim = Governor.getInstance().getConfig().getBoolean("ChainOfCommand.AnyTownMemberCanClaim");
		anyNationMemberCanClaim = Governor.getInstance().getConfig().getBoolean("ChainOfCommand.AnyNationMemberCanClaim");
		claimAllowedRanks = Governor.getInstance().getConfig().getStringList("ChainOfCommand.AllowedRanks");
		claimEnabled = Governor.getInstance().getConfig().getBoolean("ChainOfCommand.Enabled");
		
		//income tax
		incomeTaxEnabled = Governor.getInstance().getConfig().getBoolean("IncomeTax.Enabled");
		incomeTaxChatMsgs = Governor.getInstance().getConfig().getBoolean("IncomeTax.EnableChatMessages");
		defaultTownTax = Governor.getInstance().getConfig().getDouble("IncomeTax.DefaultTownTax");
		defaultNationTax = Governor.getInstance().getConfig().getDouble("IncomeTax.DefaultNationTax");
		taxEssentialsPay = Governor.getInstance().getConfig().getBoolean("IncomeTax.TaxEssentialsPay");
		incomeTaxTownAllowedRanks = Governor.getInstance().getConfig().getStringList("IncomeTax.TownAllowedRanks");
		incomeTaxNationAllowedRanks = Governor.getInstance().getConfig().getStringList("IncomeTax.NationAllowedRanks");
		maxTaxRate = Governor.getInstance().getConfig().getDouble("IncomeTax.MaxTaxRate");
		//income tax hooks
		chestShopIncomeEnabled = Governor.getInstance().getConfig().getBoolean("IncomeTax.EnableChestShopIncome");
		jobsIncomeEnabled = Governor.getInstance().getConfig().getBoolean("IncomeTax.EnableJobsIncome");
		quickShopIncomeEnabled = Governor.getInstance().getConfig().getBoolean("IncomeTax.EnableQuickShopIncome");
	}
	

}
