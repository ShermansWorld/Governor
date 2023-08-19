package me.ShermansWorld.Governor.incometax;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Governor;
import me.ShermansWorld.Governor.config.Config;
import net.ess3.api.events.UserBalanceUpdateEvent;
import net.ess3.api.events.UserBalanceUpdateEvent.Cause;

public class IncomeTaxListener implements Listener {

	boolean townWithdraw = false; // flags so it recognizes when interfacing with towny bank, only run once
	boolean nationWithdraw = false;

	@EventHandler
	public void playerIncome(final UserBalanceUpdateEvent e) {
		// Bukkit.broadcastMessage(e.getPlayer().getName()); // print player or "bank"
		// name. Used for testing

		if (!Config.incomeTaxEnabled) {
			return;
		}

		if (e.getCause().equals(Cause.COMMAND_ECO)) { // do not tax /eco set (amount)
			return;
		}

		if (e.getCause().equals(Cause.COMMAND_PAY)) { // do not tax /pay if it is disabled in config
			if (!Config.taxEssentialsPay) {
				return;
			}
		}
		
		// Check for disabled hooks
		if (ChestShopListener.isChestShopPayment) {
			if (!Config.chestShopIncomeEnabled) {
				return;
			}
			ChestShopListener.isChestShopPayment = false;
		}
		if (JobsListener.isJobsPayment) {
			if (!Config.jobsIncomeEnabled) {
				return;
			}
			JobsListener.isJobsPayment = false;
		}
		if (QuickShopListener.isQuickShopPayment) {
			if (!Config.quickShopIncomeEnabled) {
				return;
			}
			QuickShopListener.isQuickShopPayment = false;
		}
		
		
		if (e.getPlayer().getName().length() > 5) {
			if (e.getPlayer().getName().substring(0, 5).contentEquals("town_")) {
				townWithdraw = true;
			}
		}
		if (e.getPlayer().getName().length() > 7) {
			if (e.getPlayer().getName().substring(0, 7).contentEquals("nation_")) {
				nationWithdraw = true;
			}
		}
		if (e.getOldBalance().compareTo(e.getNewBalance()) == -1) { // if balance net gain
			if (!nationWithdraw && !townWithdraw) {
				// income tax
				Resident r;
				try {
					// income tax
					r = TownyAPI.getInstance().getResident(e.getPlayer());
					if (r.hasTown()) {

						double townRate = Config.defaultTownTax;
						double nationRate = Config.defaultNationTax;

						if (Governor.incomeTownTaxMap.keySet().contains(r.getTownOrNull().getName())) { // if town has set rate
							townRate = Governor.incomeTownTaxMap.get(r.getTownOrNull().getName());
						}
						if (r.hasNation()) {
							if (Governor.incomeNationTaxMap.keySet().contains(r.getNationOrNull().getName())) { // if nation has
																											// set
								// rate
								nationRate = Governor.incomeNationTaxMap.get(r.getNationOrNull().getName());
							}
						}

						BigDecimal income = e.getNewBalance().subtract(e.getOldBalance());
						final BigDecimal townTax = income.multiply(BigDecimal.valueOf(townRate));
						final BigDecimal nationTax = income.multiply(BigDecimal.valueOf(nationRate));

						if (townRate != 0.0 && townTax.intValue() != 0) { // towny banks only work
							final double rate = townRate; // with ints
							if (Config.incomeTaxChatMsgs) {
								if (e.getPlayer().isOnline()) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(Governor.getInstance(), new Runnable() {
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
								if (Config.incomeTaxChatMsgs) {
									if (e.getPlayer().isOnline()) {
										Bukkit.getScheduler().scheduleSyncDelayedTask(Governor.getInstance(), new Runnable() {
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
				} catch (NullPointerException e2) {  // error when getting resident. Clearly not a player or player is not in a town
					return;
				}
			}
			townWithdraw = false;
			nationWithdraw = false;

		}
	}
}