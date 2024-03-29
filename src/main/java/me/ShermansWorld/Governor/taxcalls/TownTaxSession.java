package me.ShermansWorld.Governor.taxcalls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Governor;
import me.ShermansWorld.Governor.config.Config;

public class TownTaxSession {

	private Player mayor;
	private Town town;
	private double amount;
	private boolean isTaxing;
	private int id;
	private boolean isCanceled;

	private List<Player> taxablePlayers = new ArrayList<>();
	private List<Player> acceptedTax = new ArrayList<>();
	private List<Player> deniedTax = new ArrayList<>();

	public TownTaxSession(Player mayor, Town town, double amount) {
		this.mayor = mayor;
		this.town = town;
		this.amount = amount;
		isTaxing = false;
		isCanceled = false;
	}

	public void init() {
		isTaxing = true;
		List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
		List<Resident> residents = town.getResidents();

		mayor.sendMessage(Helper.Chatlabel() + Helper.color(
				"&aA tax of &6$" + String.valueOf(amount) + " &ahas been requested for online &3town &aresidents"));

		id = Bukkit.getScheduler().scheduleSyncDelayedTask(Governor.getInstance(), new Runnable() {
			@Override
			public void run() {
				isTaxing = false;
				if (!isCanceled) {
					mayor.sendMessage(Helper.Chatlabel() + Helper.color("&aThe &3town &atax call has ended"));
					if (!Governor.townTaxSessions.isEmpty()) {
						for (int i = 0; i < Governor.townTaxSessions.size(); i++) { // remove from main list, close tax
																				// session
							if (Governor.townTaxSessions.get(i).getTown().equals(town)) {
								Governor.townTaxSessions.remove(i);
								break;
							}
						}
					}
					if (!Governor.townTaxSessions.isEmpty()) {
						for (int i = 0; i < Governor.townTaxSessions.size(); i++) { // remove from main list, close tax
																				// session
							if (Governor.townTaxSessions.get(i).getTown().equals(town)) {
								Governor.townTaxSessions.remove(i);
								break;
							}
						}
					}
				}
			}
		}, Config.maxTownTaxCallTime); // 100L = 5 seconds

		for (int i = 0; i < residents.size(); i++) {
			if (residents.get(i).getName() == null) {
				Bukkit.getLogger().warning(Helper.Chatlabel() + "Error when parsing town, null residents");
			} else {
				Resident resident = residents.get(i);
				Player player = resident.getPlayer();
				if (onlinePlayers.contains(resident.getPlayer()) && resident.getPlayer() != mayor) {
					player.sendMessage(Helper.Chatlabel()
							+ Helper.color("&2" + mayor.getName() + " &ahas called for a &3town &atax of &6$"
									+ String.valueOf(amount) + " &afor all online town members."));
					if (Governor.economy.getBalance(player) > amount) {
						taxablePlayers.add(player);
						player.sendMessage(
								Helper.Chatlabel() + Helper.color("&aType &e/ttax accept &aor &e/ttax deny"));
					} else {
						player.sendMessage(Helper.Chatlabel()
								+ Helper.color("&aDue to your low balance you are &6exempt &afrom this &3town &atax!"));
						mayor.sendMessage(Helper.Chatlabel() + Helper.color("&b" + player.getName()
								+ " &awas &6exempt &afrom this &3town &atax due to a low balance"));
					}
				} else if (onlinePlayers.contains(resident.getPlayer()) && resident.getPlayer().equals(mayor) && Config.townAskCaller) {
					if (Governor.economy.getBalance(player) > amount) {
						taxablePlayers.add(player);
						player.sendMessage(
								Helper.Chatlabel() + Helper.color("&aType &e/ttax accept &aor &e/ttax deny"));
					} else {
						player.sendMessage(Helper.Chatlabel()
								+ Helper.color("&aDue to your low balance you are &6exempt &afrom this &3town &atax!"));
						mayor.sendMessage(Helper.Chatlabel() + Helper.color("&b" + player.getName()
								+ " &awas &6exempt &afrom this &3town &atax due to a low balance"));
					}
				}
			}
		}

	}

	public void cancel(Player canceler) {
		List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
		List<Resident> residents = town.getResidents();
		for (int i = 0; i < residents.size(); i++) {
			if (onlinePlayers.contains(residents.get(i).getPlayer())
					&& !(residents.get(i).getPlayer().equals(canceler))) {
				residents.get(i).getPlayer().sendMessage(Helper.Chatlabel()
						+ Helper.color("&aThe &3town &atax call has been canceled by " + "&2" + canceler.getName()));
			}
		}
		isCanceled = true;
	}

	// Getters

	public Town getTown() {
		return town;
	}

	public Player getMayor() {
		return mayor;
	}

	public double getTaxAmount() {
		return amount;
	}

	public boolean isTaxing() {
		return isTaxing;
	}

	public List<Player> getTaxablePlayers() {
		return taxablePlayers;
	}

	public List<Player> getAcceptedTax() {
		return acceptedTax;
	}

	public List<Player> getDeniedTax() {
		return deniedTax;
	}

	public int getid() {
		return id;
	}

	// Setters

	public void setTaxingStatus(boolean isTaxing) {
		this.isTaxing = isTaxing;
	}

}
