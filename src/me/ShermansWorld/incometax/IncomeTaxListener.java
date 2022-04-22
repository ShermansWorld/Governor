package me.ShermansWorld.incometax;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Main;
import me.ShermansWorld.Governor.config.ConfigVals;
import net.ess3.api.events.UserBalanceUpdateEvent;
import net.ess3.api.events.UserBalanceUpdateEvent.Cause;

public class IncomeTaxListener implements Listener {

	boolean townWithdraw = false; // flags so it recognizes when interfacing with towny bank, only run once
	boolean nationWithdraw = false;

	@EventHandler
	public void playerIncome(UserBalanceUpdateEvent e) {
		// Bukkit.broadcastMessage(e.getPlayer().getName()); // print player or "bank"
		// name. Used for testing

		if (!ConfigVals.incomeTaxEnabled) {
			return;
		}

		if (e.getCause().equals(Cause.COMMAND_ECO)) { // do not tax /eco set (amount)
			return;
		}

		if (e.getCause().equals(Cause.COMMAND_PAY)) { // do not tax /pay if it is disabled in config
			if (!ConfigVals.taxEssentialsPay) {
				return;
			}
		}

		if (e.getPlayer().getName().substring(0, 5).contentEquals("town_")) {
			townWithdraw = true;
		}
		if (e.getPlayer().getName().substring(0, 7).contentEquals("nation_")) {
			nationWithdraw = true;
		}
		if (e.getOldBalance().compareTo(e.getNewBalance()) == -1) {
			if (!nationWithdraw && !townWithdraw) {
				// income tax
				Resident r = TownyAPI.getInstance().getResident(e.getPlayer());
				if (r.hasTown()) {

					double townRate = ConfigVals.defaultTownTax;
					double nationRate = ConfigVals.defaultNationTax;

					if (Main.incomeTownTaxMap.keySet().contains(r.getTownOrNull().getName())) { // if town has set rate
						townRate = Main.incomeTownTaxMap.get(r.getTownOrNull().getName());
					}
					if (r.hasNation()) {
						if (Main.incomeNationTaxMap.keySet().contains(r.getNationOrNull().getName())) { // if nation has
																										// set
							// rate
							nationRate = Main.incomeNationTaxMap.get(r.getNationOrNull().getName());
						}
					}

					BigDecimal income = e.getNewBalance().subtract(e.getOldBalance());
					BigDecimal townTax = income.multiply(BigDecimal.valueOf(townRate));
					BigDecimal nationTax = income.multiply(BigDecimal.valueOf(nationRate));

					if (townRate != 0.0 && townTax.intValue() != 0) { // towny banks only work
						final double rate = townRate; // with ints
						if (ConfigVals.incomeTaxChatMsgs) {
							if (e.getPlayer().isOnline()) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
									@Override
									public void run() {
										e.getPlayer()
												.sendMessage(Helper.Taxlabel() + Helper
														.color("&3Town &bIncome Tax: &c-&a$" + townTax.intValue()
																+ " &6(" + String.valueOf(rate * 100) + "%)"));
									}
								}, 10L); // 20 Tick (1 Second) delay before run() is called
							}
						}
						try {
							r.getTownOrNull().depositToBank(r, townTax.intValue());
						} catch (TownyException e1) {
						}
						e.setNewBalance(
								e.getNewBalance().subtract(BigDecimal.valueOf(Double.valueOf(townTax.intValue()))));

					}
					if (nationRate != 0.0 && nationTax.intValue() != 0) { // towny banks
																			// only work
																			// with ints
						if (r.hasNation()) {
							final double rate = nationRate;
							if (ConfigVals.incomeTaxChatMsgs) {
								if (e.getPlayer().isOnline()) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
										@Override
										public void run() {
											e.getPlayer()
													.sendMessage(Helper.Taxlabel() + Helper
															.color("&6Nation &bIncome Tax: &c-&a$" + nationTax.intValue()
																	+ " &6(" + String.valueOf(rate * 100) + "%)"));
										}
									}, 10L); // 20 Tick (1 Second) delay before run() is called
								}
							}
							try {
								r.getNation().depositToBank(r, nationTax.intValue());
							} catch (TownyException e1) {
							}
							e.setNewBalance(e.getNewBalance()
									.subtract(BigDecimal.valueOf(Double.valueOf(nationTax.intValue()))));
						}

					}
				}
			}
			townWithdraw = false;
			nationWithdraw = false;

		}
	}
}