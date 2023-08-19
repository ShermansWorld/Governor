package me.ShermansWorld.Governor.incometax;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;


public class ChestShopListener implements Listener {
	
	public static boolean isChestShopPayment = false;
	
	@EventHandler
	public void shopTransaction(PreTransactionEvent e) {
		isChestShopPayment = true;
	}
}	
