package me.ShermansWorld.Governor.taxcalls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Governor;
import me.ShermansWorld.Governor.config.Config;

public class NationTaxSession {

	private Player mayor;
	private Nation nation;
	private double amount;
	private boolean isTaxing;
	private int id;
	private boolean isCanceled;

	private List<Player> taxablePlayers = new ArrayList<>();
	private List<Player> acceptedTax = new ArrayList<>();
	private List<Player> deniedTax = new ArrayList<>();

	public NationTaxSession(Player mayor, Nation nation, double amount) {
		this.mayor = mayor;
		this.nation = nation;
		this.amount = amount;
		isTaxing = false;
		isCanceled = false;
	}

	public void init() {
		isTaxing = true;
		List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
		List<Resident> residents = nation.getResidents();

		mayor.sendMessage(Helper.Chatlabel() + Helper.color(
				"&aA tax of &6$" + String.valueOf(amount) + " &ahas been requested for online &6nation &amembers"));

		id = Bukkit.getScheduler().scheduleSyncDelayedTask(Governor.getInstance(), new Runnable() {
			@Override
			public void run() {
				isTaxing = false;
				if (!isCanceled) {
					mayor.sendMessage(Helper.Chatlabel() + Helper.color("&aThe &6nation &atax call has ended"));
					if (!Governor.nationTaxSessions.isEmpty()) {
						for (int i = 0; i < Governor.nationTaxSessions.size(); i++) { // remove from main list, close tax
																					// session
							if (Governor.nationTaxSessions.get(i).getNation().equals(nation)) {
								Governor.nationTaxSessions.remove(i);
								break;
							}
						}
					}
					for (int i = 0; i < taxablePlayers.size(); i++) {
						mayor.sendMessage(Helper.Chatlabel() + Helper.color("&b" + taxablePlayers.get(i).getName()
								+ " &eabstained from paying the &6nation &etax"));
						taxablePlayers.get(i)
								.sendMessage(Helper.Taxlabel() + Helper.color("&bThe &6nation &btax call has ended"));
					}
				}
			}
		}, Config.maxNationTaxCallTime); // 100L = 5 seconds

		for (int i = 0; i < residents.size(); i++) {
			if (residents.get(i).getName() == null) {
				Bukkit.getLogger().warning(Helper.Chatlabel() + "Error when parsing nation, null residents");
			} else {
				Resident resident = residents.get(i);
				Player player = resident.getPlayer();
				if (onlinePlayers.contains(resident.getPlayer()) && resident.getPlayer() != mayor) {
					player.sendMessage(Helper.Chatlabel()
							+ Helper.color("&2" + mayor.getName() + " &ahas called for a &6nation &atax of &6$"
									+ String.valueOf(amount) + " &afor all online town members."));
					if (Governor.economy.getBalance(player) > amount) {
						taxablePlayers.add(player);
						player.sendMessage(
								Helper.Chatlabel() + Helper.color("&aType &e/ntax accept &aor &e/ntax deny"));
					} else {
						player.sendMessage(Helper.Chatlabel() + Helper
								.color("&aDue to your low balance you are &6exempt &afrom this &6nation &atax!"));
						mayor.sendMessage(Helper.Chatlabel() + Helper.color("&b" + player.getName()
								+ " &awas &6exempt &afrom this &6nation &atax due to a low balance"));
					}
				} else if (onlinePlayers.contains(resident.getPlayer()) && resident.getPlayer().equals(mayor) && Config.nationAskCaller) {
					if (Governor.economy.getBalance(player) > amount) {
						taxablePlayers.add(player);
						player.sendMessage(
								Helper.Chatlabel() + Helper.color("&aType &e/ntax accept &aor &e/ntax deny"));
					} else {
						player.sendMessage(Helper.Chatlabel() + Helper
								.color("&aDue to your low balance you are &6exempt &afrom this &6nation &atax!"));
						mayor.sendMessage(Helper.Chatlabel() + Helper.color("&b" + player.getName()
								+ " &awas &6exempt &afrom this &6nation &atax due to a low balance"));
					}
				}
			}
		}

	}

	public void cancel(Player canceler) {
		List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
		List<Resident> residents = nation.getResidents();
		for (int i = 0; i < residents.size(); i++) {
			if (onlinePlayers.contains(residents.get(i).getPlayer())
					&& !(residents.get(i).getPlayer().equals(canceler))) {
				residents.get(i).getPlayer().sendMessage(Helper.Chatlabel()
						+ Helper.color("&aThe &6nation &atax call has been canceled by " + "&2" + canceler.getName()));
			}
		}
		isCanceled = true;
	}

	// Getters

	public Nation getNation() {
		return nation;
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
