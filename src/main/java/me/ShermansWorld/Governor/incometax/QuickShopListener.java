package me.ShermansWorld.Governor.incometax;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maxgamer.quickshop.api.event.EconomyCommitEvent;


public class QuickShopListener implements Listener {
	
	public static boolean isQuickShopPayment = false;
	
	@EventHandler
	public void shopTransaction(EconomyCommitEvent e) {
		isQuickShopPayment = true;
	}
}	
