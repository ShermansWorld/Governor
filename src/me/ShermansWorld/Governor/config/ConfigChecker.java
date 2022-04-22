package me.ShermansWorld.Governor.config;

import me.ShermansWorld.Governor.Main;

public class ConfigChecker {
	public static void V1ToV2() {
		if(ConfigVals.configVersion == 1) {
			Main.getInstance().getConfig().set("config-version", 2);
			Main.getInstance().getConfig().set("IncomeTax.Enabled", true);
			Main.getInstance().getConfig().set("IncomeTax.EnableChatMessages", true);
			Main.getInstance().getConfig().set("IncomeTax.TaxEssentialsPay", true);
			Main.getInstance().getConfig().set("IncomeTax.DefaultTownTax", 0.05);
			Main.getInstance().getConfig().set("IncomeTax.DefaultNationTax", 0.05);
			Main.getInstance().saveConfig();
			Main.getInstance().saveDefaultConfig();
		}
	}
}
