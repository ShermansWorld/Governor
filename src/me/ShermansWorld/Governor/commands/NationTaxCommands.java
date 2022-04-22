package me.ShermansWorld.Governor.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Main;
import me.ShermansWorld.Governor.taxcalls.NationTaxSession;


public class NationTaxCommands implements CommandExecutor {

	public NationTaxCommands(Main plugin) {
		plugin.getCommand("ntax").setExecutor((CommandExecutor) this); // command to run in chat
	}

	// Player that sends command
	// Command it sends
	// Alias of the command which was used
	// args for Other values within command

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender; // Convert sender into player
		Resident r = TownyAPI.getInstance().getResident(p);
		Nation nation;
		try {
			nation = TownyAPI.getInstance().getResident(p).getTown().getNation();
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
			NationTaxSession nationTaxSession = null;
			for (NationTaxSession taxSession : Main.nationTaxSessions) { // remove from main list, close tax session
				if (taxSession.getNation().equals(nation)) {
					taxSessionExists = true;
					nationTaxSession = taxSession;
				}
			}

			if (!taxSessionExists) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bThere is no &6nation &btax call right now"));
				return false;
			}

			if (!nationTaxSession.isTaxing()) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bThere is no &6nation &btax call right now"));
				return false;
			}
			
			if (!nationTaxSession.getNation().getResidents().contains(r)) {
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou are not a member of " + "&e" + nation.getName()));
				return false;
			}
			
			if (!nationTaxSession.getAcceptedTax().isEmpty()) {
				if (nationTaxSession.getAcceptedTax().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have already paid this &6nation &btax"));
					return false;
				}
			}
			
			if (!nationTaxSession.getDeniedTax().isEmpty()) {
				if (nationTaxSession.getDeniedTax().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have already chosen to not pay this &6nation &btax"));
					return false;
				}
			}
			
			if (args[0].equalsIgnoreCase("accept")) {
				if (!nationTaxSession.getTaxablePlayers().contains(p)) {
					p.sendMessage(Helper.Taxlabel() + "&bYou were &6exempt &bfrom this tax");
				}
				nationTaxSession.getTaxablePlayers().remove(p);
				nationTaxSession.getAcceptedTax().add(p);
				Main.economy.withdrawPlayer(p, nationTaxSession.getTaxAmount());
				nation.getAccount().deposit(nationTaxSession.getTaxAmount(), "Member Tax");
				nationTaxSession.getMayor().sendMessage(Helper.Chatlabel() + Helper.color("&b" + p.getName() + " &ahas paid the &6nation &atax"));
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have paid the &6nation &btax of &6$" + String.valueOf(nationTaxSession.getTaxAmount()) + "&b to the bank of &e" + nationTaxSession.getNation()));
			} else if (args[0].equalsIgnoreCase("deny")) {
				nationTaxSession.getTaxablePlayers().remove(p);
				nationTaxSession.getDeniedTax().add(p);
				p.sendMessage(Helper.Taxlabel() + Helper.color("&bYou have chosen to not pay the &6nation &btax of &6$" + String.valueOf(nationTaxSession.getTaxAmount()) + "&b to the bank of &e" + nationTaxSession.getNation()));
				nationTaxSession.getMayor().sendMessage(Helper.Chatlabel() + Helper.color("&b" + p.getName() + " &chas chosen to not pay the &6nation &ctax"));
			}
		}
		return false;
	}

}
