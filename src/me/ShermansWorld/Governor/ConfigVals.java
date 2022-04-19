package me.ShermansWorld.Governor;

import java.util.List;

public class ConfigVals {
	
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
	
	public static void initConfigVals() {
		
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
		
		
	}
	

}
