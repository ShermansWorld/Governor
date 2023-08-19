package me.ShermansWorld.Governor.taxcalls;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Main;


public class TownTaxCommands implements CommandExecutor {

	public TownTaxCommands(Main plugin) {
		plugin.getCommand("ttax").setExecutor((CommandExecutor) this); // command to run in chat
	}

	// Player that sends command
	// Command it sends
	// Alias of the command which was used
	// args for Other values within command

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender; // Convert sender into player
		Resident r = TownyAPI.getInstance().getResident(p);
		Town town;
		try {
			town = TownyAPI.getInstance().getResident(p).getTown();
		} catch (NotRegisteredException e1) {
			p.sendMessage(Helper.Taxlabel() + Helper.color("&cYou are not in a town!"));
			return false;
		}

		// Check arguments
		// -------------------------------------------------------------------------------------------------------------

		if (args.length != 1) {
			p.sendMessage(Helper.Taxlabel() + "&cInvalid input: &e/tax accept &cor &e/tax deny");
			return false;
		} else {
			boolean taxSessionExists = false;
			TownTaxSession townTaxSession = null;
			for (TownTaxSession taxSession : Main.townTaxSessions) { // remove from main list, close tax session
				if (taxSession.getTown().equals(town)) {
					taxSessionExists = true;
					townTaxSession = taxSession;
				}
			}

			if (!taxSessionExists) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bThere is no &3town &btax call right now"));
				return false;
			}

			if (!townTaxSession.isTaxing()) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bThere is no &3town &3tax call right now"));
				return false;
			}
			
			if (!townTaxSession.getTown().getResidents().contains(r)) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou are not a resident of " + "&e" + town.getName()));
				return false;
			}
			
			if (!townTaxSession.getAcceptedTax().isEmpty()) {
				if (townTaxSession.getAcceptedTax().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have already paid this &3town &btax"));
					return false;
				}
			}
			
			if (!townTaxSession.getDeniedTax().isEmpty()) {
				if (townTaxSession.getDeniedTax().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have already chosen to not pay this &3town &btax"));
					return false;
				}
			}
			
			if (args[0].equalsIgnoreCase("accept")) {
				if (!townTaxSession.getTaxablePlayers().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + "&bYou were &6exempt &bfrom this tax");
				}
				townTaxSession.getTaxablePlayers().remove(p);
				townTaxSession.getAcceptedTax().add(p);
				Main.economy.withdrawPlayer(p, townTaxSession.getTaxAmount());
				town.getAccount().deposit(townTaxSession.getTaxAmount(), "Resident Tax");
				townTaxSession.getMayor().sendMessage(Helper.Chatlabel() + Helper.color("&b" + p.getName() + " &ahas paid the &3town &atax"));
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have paid the &3town &btax of &6$" + String.valueOf(townTaxSession.getTaxAmount()) + "&b to the bank of &e" + townTaxSession.getTown()));
			} else if (args[0].equalsIgnoreCase("deny")) {
				townTaxSession.getTaxablePlayers().remove(p);
				townTaxSession.getDeniedTax().add(p);
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have chosen to not pay the &3town &btax of &6$" + String.valueOf(townTaxSession.getTaxAmount()) + "&b to the bank of &e" + townTaxSession.getTown()));
				townTaxSession.getMayor().sendMessage(Helper.Chatlabel() + Helper.color("&b" + p.getName() + " &chas chosen to not pay the &3town &ctax"));
			}
		}
		return false;
	}

}
