package me.ShermansWorld.Governor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.ChatColor;

public class Helper
{
    public static String color(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
    
    public static String Chatlabel() {
        return color("&2[&aGovernor&2]&r ");
    }
    
    public static String Taxlabel() {
        return color("&b[Taxes]&r ");
    }
    
    public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
