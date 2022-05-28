package me.ShermansWorld.Governor.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EmptyTownException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Main;
import me.ShermansWorld.Governor.chainofcommand.InactiveTownListener;
import me.ShermansWorld.Governor.config.ConfigVals;
import me.ShermansWorld.Governor.taxcalls.NationTaxSession;
import me.ShermansWorld.Governor.taxcalls.TownTaxSession;

public class GovernorCommands implements CommandExecutor {

	public GovernorCommands(Main plugin) {
		plugin.getCommand("governor").setExecutor((CommandExecutor) this); // command to run in chat
	}

	// Player that sends command
	// Command it sends
	// Alias of the command which was used
	// args for Other values within command

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;
		Town town;
		Nation nation;
		double maxAmount = 100.0;
		double amount = 0;
		
		if (args.length == 0) {
			p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor taxtown [amount/cancel] &aStarts/cancels a &3town &atax call"));
			p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor taxnation [amount/cancel] &aStarts/cancels a &6nation &atax call"));
			if (ConfigVals.claimEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor claim &aClaim mayorship of an inactive town"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor checkmayor [town] &aCheck how many days a mayor has been offline"));
			}
			if (ConfigVals.incomeTaxEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor townincometax &aSet the &3town &aincome tax rate (%)"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor nationincometax &aSet the &6nation &aincome tax rate (%)"));
			}
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if (!p.hasPermission("Governor.reload")) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou do not have permission to do this"));
				return false;
			}
			Main.getInstance().reloadConfig();
			Main.getInstance().saveDefaultConfig();
			ConfigVals.initConfigVals();
			p.sendMessage(Helper.Chatlabel() + Helper.color("&econfig.yml reloaded"));
			return true;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
			p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor taxtown [amount/cancel] &aStarts/cancels a &3town &atax call"));
			p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor taxnation [amount/cancel] &aStarts/cancels a &6nation &atax call"));
			if (ConfigVals.claimEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor claim &aClaim mayorship of an inactive town"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor checkmayor [town] &aCheck how many days a mayor has been offline"));
			}
			if (ConfigVals.incomeTaxEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor townincometax &aSet the &3town &aincome tax rate (%)"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&e/governor nationincometax &aSet the &6nation &aincome tax rate (%)"));
			}
			return true;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
			p.sendMessage(Helper.Chatlabel() + Helper.color("&aVersion: &6BETA-1.2.1"));
			return true;
		}

		if (args[0].equalsIgnoreCase("taxtown")) {

			if (args.length != 2) {
				p.sendMessage(
						Helper.Chatlabel() + Helper.color("&cInvalid arguments. Usage: &e/governor taxtown [amount]"));
				return false;
			}

			try {
				town = TownyAPI.getInstance().getResident(p).getTown();
			} catch (NotRegisteredException e1) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou must be in a town to execute this command"));
				return false;
			}

			if (args[1].equalsIgnoreCase("cancel")) {
				if (Main.townTaxSessions.isEmpty()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cThere is currently no tax call for " + town.getName()));
					return false;
				}
				for (int i = 0; i < Main.townTaxSessions.size(); i++) { // check for an existing tax session
					if (Main.townTaxSessions.get(i).getTown().equals(town)) {
						if (p.equals(town.getMayor().getPlayer()) || Main.townTaxSessions.get(i).getMayor().equals(p)) { 
							Main.townTaxSessions.get(i).setTaxingStatus(false);
							Main.townTaxSessions.get(i).cancel(p);
							Main.townTaxSessions.remove(i);
							p.sendMessage(Helper.Chatlabel() + Helper.color("&3Town &atax call has been canceled"));
							return true;
						} else {
							p.sendMessage(Helper.Chatlabel()
									+ Helper.color("&cYou do not have permission to cancel this town tax call"));
							return false;
						}
					}
				}
				p.sendMessage(
						Helper.Chatlabel() + Helper.color("&cThere is currently no tax call for " + town.getName()));
				return false;
			}

			for (TownTaxSession taxSession : Main.townTaxSessions) { // check for an existing tax session
				if (taxSession.getTown().equals(town)) {
					if (taxSession.isTaxing()) {
						p.sendMessage(Helper.Chatlabel() + Helper.color(
								"&aA &3town &atax has already been called! To cancel it type &e/governor taxtown cancel"));
						return false;
					}
				}
			}

			try {
				amount = Double.parseDouble(args[1]);
				amount = Helper.round(amount, 2); // round amount to 2 decimal places
			} catch (NumberFormatException e) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid Format: amount must be a number"));
				return false;
			}

			maxAmount = Double.valueOf(ConfigVals.maxTownTaxAmount);

			if (amount > maxAmount) {
				p.sendMessage(Helper.Chatlabel() + Helper
						.color("&cThis amount is too high! (Must be less than or equal to " + String.valueOf(maxAmount))
						+ ")");
				return false;
			}

			List<String> townRanks = TownyAPI.getInstance().getResident(p).getTownRanks();
			
			
			if (townRanks.isEmpty()) {
				if (!TownyAPI.getInstance().getResident(p).isMayor()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to start a town tax call"));
					return false;
				}
			}

			boolean allowedRank = false;
			for (int i = 0; i < ConfigVals.taxTownAllowedRanks.size(); i++) {
				for (int j = 0; j < townRanks.size(); j++) {
					if (townRanks.get(j).equalsIgnoreCase(ConfigVals.taxTownAllowedRanks.get(i))) {
						allowedRank = true;
					}
				}
			}

			if (!allowedRank) {
				if (!TownyAPI.getInstance().getResident(p).isMayor()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to start a town tax call"));
					return false;
				}
			}

			TownTaxSession taxSession = new TownTaxSession(p, town, amount);
			Main.townTaxSessions.add(taxSession);
			taxSession.init();

			return true;

		} else if (args[0].equalsIgnoreCase("taxnation")) {

			if (args.length != 2) {
				p.sendMessage(Helper.Chatlabel()
						+ Helper.color("&cInvalid arguments. Usage: &e/governor taxnation [amount]"));
			}

			try {
				nation = TownyAPI.getInstance().getResident(p).getNation();
			} catch (TownyException e2) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou must be in a nation to execute this command"));
				return false;
			}

			if (args[1].equalsIgnoreCase("cancel")) {
				if (Main.nationTaxSessions.isEmpty()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cThere is currently no tax call for " + nation.getName()));
				}
				for (int i = 0; i < Main.nationTaxSessions.size(); i++) { // check for an existing tax session
					if (Main.nationTaxSessions.get(i).getNation().equals(nation)) {
						if (p.equals(nation.getKing().getPlayer())
								|| Main.nationTaxSessions.get(i).getMayor().equals(p)) { // if the player is mayor or
																							// they started the tax call
							Main.nationTaxSessions.get(i).setTaxingStatus(false);
							Main.nationTaxSessions.get(i).cancel(p);
							Main.nationTaxSessions.remove(i);
							p.sendMessage(Helper.Chatlabel() + Helper.color("&6Nation &atax call has been canceled"));
							return true;
						} else {
							p.sendMessage(Helper.Chatlabel()
									+ Helper.color("&cYou do not have permission to cancel this nation tax call"));
							return false;
						}
					}
				}
				p.sendMessage(
						Helper.Chatlabel() + Helper.color("&cThere is currently no tax call for " + nation.getName()));
				return false;
			}

			for (NationTaxSession taxSession : Main.nationTaxSessions) { // check for an existing tax session
				if (taxSession.getNation().equals(nation)) {
					if (taxSession.isTaxing()) {
						p.sendMessage(Helper.Chatlabel() + Helper.color(
								"&aA &6nation &atax has already been called! To cancel it type &e/governor taxnation cancel"));
						return false;
					}
				}
			}

			try {
				amount = Double.parseDouble(args[1]);
				amount = Helper.round(amount, 2); // round amount to 2 decimal places
			} catch (NumberFormatException e) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid Format: amount must be a number"));
				return false;
			}

			maxAmount = Double.valueOf(ConfigVals.maxNationTaxAmount);

			if (amount > maxAmount) {
				p.sendMessage(Helper.Chatlabel() + Helper
						.color("&cThis amount is too high! (Must be less than or equal to " + String.valueOf(maxAmount))
						+ ")");
				return false;
			}

			List<String> nationRanks = TownyAPI.getInstance().getResident(p).getNationRanks();

			if (nationRanks.isEmpty()) {
				if (!TownyAPI.getInstance().getResident(p).isKing()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to start a nation tax call"));
					return false;
				}
			}

			boolean allowedRank = false;
			for (int i = 0; i < ConfigVals.taxNationAllowedRanks.size(); i++) {
				for (int j = 0; j < nationRanks.size(); j++) {
					if (nationRanks.get(j).equalsIgnoreCase(ConfigVals.taxNationAllowedRanks.get(i))) {
						allowedRank = true;
					}
				}
			}

			if (!allowedRank) {
				if (!TownyAPI.getInstance().getResident(p).isKing()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to start a town tax call"));
					return false;
				}
			}

			NationTaxSession taxSession = new NationTaxSession(p, nation, amount);
			Main.nationTaxSessions.add(taxSession);
			taxSession.init();

			return true;

		} else if (args[0].equalsIgnoreCase("claim")) {
			
			if (!ConfigVals.claimEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cChain-of-Command is not enabled on this server"));
				return false;
			}

			Resident newMayor = TownyAPI.getInstance().getResident(p);

			if (ConfigVals.anyoneCanClaim || ConfigVals.anyNationMemberCanClaim) {
				if (args.length != 2) {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid Input: Usage &e/governor claim [town]"));
					return false;
				}
				if (newMayor.isMayor()) {
					p.sendMessage(Helper.Chatlabel() + Helper.color(
							"&cYou cannot claim mayorship of a town if you are already a mayor of another town"));
					return false;
				}
				if (newMayor.isKing()) {
					p.sendMessage(Helper.Chatlabel() + Helper
							.color("&cYou cannot claim mayorship of a town if you are already a king of a nation"));
					return false;
				}
				boolean townExists = false;
				Town target = null;
				for (Town t : InactiveTownListener.inactiveTowns) {
					if (t.getName().equalsIgnoreCase(args[1])) {
						townExists = true;
						target = t;
					}
				}
				if (!townExists) {
					p.sendMessage(Helper.Chatlabel() + Helper
							.color("&cYou can only claim mayorship if the town's mayor has been inactive for over &6"
									+ String.valueOf(ConfigVals.daysInactive) + " days"));
					return false;
				}

				if (!ConfigVals.anyoneCanClaim) { // if nation member trying to claim
					try {
						if (!newMayor.getNation().equals(target.getNation())) {
							p.sendMessage(Helper.Chatlabel() + Helper
									.color("&cYou must be in the same nation as " + target.getName() + " to do this"));
							return false;
						}
					} catch (NotRegisteredException e) {
						p.sendMessage(Helper.Chatlabel() + Helper
								.color("&cYou must be in the same nation as " + target.getName() + " to do this"));
						return false;
					} catch (TownyException e) {
						p.sendMessage(Helper.Chatlabel() + Helper
								.color("&cYou must be in the same nation as " + target.getName() + " to do this"));
						return false;
					}
				}
				if (!target.getResidents().contains(newMayor)) { // if you are not in the town
					if (newMayor.hasTown()) {
						try {
							newMayor.getTown().removeResident(newMayor);
						} catch (NotRegisteredException | EmptyTownException e) {
							Bukkit.broadcastMessage("test1");
						}
					}
					try {
						newMayor.setTown(target, true);
					} catch (AlreadyRegisteredException e) {
						Bukkit.broadcastMessage("test2");
					}
				}
				target.setMayor(newMayor);
				InactiveTownListener.inactiveTowns.remove(target);
				p.sendMessage(Helper.Chatlabel() +  Helper.color("&aYou are now the new mayor of &e" + target.getName() + "&a!"));
				Bukkit.broadcastMessage(
						Helper.Chatlabel() + Helper.color("&5" + p.getName() + " &ahas claimed mayorship of &e"
								+ target.getName() + " &adue to the prior mayor being inactive for over &6"
								+ String.valueOf(ConfigVals.daysInactive) + " days"));
				return true;
			} else {
				if (newMayor.hasTown()) {
					for (Town t : InactiveTownListener.inactiveTowns) {
						try {
							if (newMayor.getTown().equals(t)) {
								if (!ConfigVals.anyTownMemberCanClaim) {

									List<String> townRanks = newMayor.getTownRanks();
									if (townRanks.isEmpty()) {
										if (newMayor.isMayor()) {
											p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou are already mayor of " + t.getName()));
											return false;
										}
										p.sendMessage(Helper.Chatlabel() + Helper
												.color("&cYou town rank is not high enough to claim mayorship"));
										return false;
									}

									boolean allowedRank = false;
									for (int i = 0; i < ConfigVals.claimAllowedRanks.size(); i++) {
										for (int j = 0; j < townRanks.size(); j++) {
											if (townRanks.get(j)
													.equalsIgnoreCase(ConfigVals.claimAllowedRanks.get(i))) {
												allowedRank = true;
											}
										}
									}

									if (!allowedRank) {
										if (!TownyAPI.getInstance().getResident(p).isMayor()) {
											p.sendMessage(Helper.Chatlabel() + Helper
													.color("&cYou town rank is not high enough to claim mayorship"));
											return false;
										}
									}
								}
								t.setMayor(newMayor);
								InactiveTownListener.inactiveTowns.remove(t);
								p.sendMessage(Helper.Chatlabel() + Helper.color("&aYou are now the new mayor of &e" + t.getName() + "&a!"));
								Bukkit.broadcastMessage(
										Helper.Chatlabel() + Helper.color("&5" + p.getName() + " &ahas claimed mayorship of &e"
												+ t.getName() + " &adue to the prior mayor being inactive for over &6"
												+ String.valueOf(ConfigVals.daysInactive) + " days"));
								return true;
							}
						} catch (NotRegisteredException e) {
							p.sendMessage(
									Helper.Chatlabel() + Helper.color("&cYou must be in " + t.getName() + " to claim mayorship"));
							return false;
						}
					}
					p.sendMessage(Helper.Chatlabel() + Helper
							.color("&cYou can only claim mayorship if your town's mayor has been inactive for over &6"
									+ String.valueOf(ConfigVals.daysInactive) + " days"));
					return false;
				} else {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou must be in a town to claim mayorship"));
					return false;
				}
			}
		} else if (args[0].equalsIgnoreCase("checkmayor")) {
			
			if (!ConfigVals.claimEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cChain-of-Command is not enabled on this server"));
				return false;
			}
			if (args.length == 1) {
				try {
					town = TownyAPI.getInstance().getResident(p).getTown();
				} catch (NotRegisteredException e) {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou are not in a town"));
					return false;
				}
				Resident mayor = town.getMayor();
				if (mayor.isOnline()) {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cThe mayor of &e" + town.getName() + " &cis online"));
					return false;
				} else {
					long now = System.currentTimeMillis();
					long lastOnline = mayor.getLastOnline();
					long milisecPerDay = 1000 * 60 * 60 * 24;
					lastOnline = ((now - lastOnline)/milisecPerDay);
					int daysOffline = (int) lastOnline;
					p.sendMessage(Helper.Chatlabel() + Helper.color("&b" + mayor.getName() + "&a, the mayor of &3" + town.getName() + " &ahas been offline for &6" + String.valueOf(daysOffline) + " days"));
					return true;
				}
			} else if (args.length == 2) {
				town = TownyAPI.getInstance().getTown(args[1]);
				if (!TownyAPI.getInstance().getTowns().contains(town)) {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cThe town you entered does not exist!"));
					return false;
				}
				Resident mayor = town.getMayor();
				if (mayor.isOnline()) {
					p.sendMessage(Helper.Chatlabel() + Helper.color("&cThe mayor of &e" + town.getName() + " &cis online"));
					return false;
				} else {
					long now = System.currentTimeMillis();
					long lastOnline = mayor.getLastOnline();
					long milisecPerDay = 1000 * 60 * 60 * 24;
					lastOnline = ((now - lastOnline)/milisecPerDay);
					int daysOffline = (int) lastOnline;
					p.sendMessage(Helper.Chatlabel() + Helper.color("&b" + mayor.getName() + "&a, the mayor of &3" + town.getName() + " &ahas been offline for &6" + String.valueOf(daysOffline) + " days"));
					return true;
				}
			} else {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid arguments. Usage: &e/governor checkmayor [town]"));
				return false;
			}

		} else if (args[0].equalsIgnoreCase("townincometax")) {
			
			if (!ConfigVals.incomeTaxEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cIncome tax is disabled on this server"));
				return false;
			}
			
			
			double percent;

			if (args.length != 2) {
				p.sendMessage(
						Helper.Chatlabel() + Helper.color("&cInvalid arguments. Usage: &e/governor townincometax [rate]"));
				return false;
			}

			try {
				town = TownyAPI.getInstance().getResident(p).getTown();
			} catch (NotRegisteredException e1) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou must be in a town to execute this command"));
				return false;
			}

			try {
				percent = Double.parseDouble(args[1]);
				percent = Helper.round(percent, 3); // round amount to 2 decimal places
			} catch (NumberFormatException e) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid Format: rate must be a number"));
				return false;
			}
			
		
			if (percent > ConfigVals.maxTaxRate) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou cannot set an income tax above " + String.valueOf(ConfigVals.maxTaxRate*100) + "%"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cExample: '0.05' = 5% income tax"));
				return false;
			}
			
			List<String> townRanks = TownyAPI.getInstance().getResident(p).getTownRanks();
			
			
			if (townRanks.isEmpty()) {
				if (!TownyAPI.getInstance().getResident(p).isMayor()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to change the town income tax rate"));
					return false;
				}
			}

			boolean allowedRank = false;
			for (int i = 0; i < ConfigVals.incomeTaxTownAllowedRanks.size(); i++) {
				for (int j = 0; j < townRanks.size(); j++) {
					if (townRanks.get(j).equalsIgnoreCase(ConfigVals.incomeTaxTownAllowedRanks.get(i))) {
						allowedRank = true;
					}
				}
			}

			if (!allowedRank) {
				if (!TownyAPI.getInstance().getResident(p).isMayor()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou town rank is not high enough to change the town income tax rate"));
					return false;
				}
			}
			
			try {
				Main.incomeTownTaxMap.put(TownyAPI.getInstance().getResident(p).getTown().getName(), percent);
				Main.incomeTaxData.getConfig().set("Towns." + TownyAPI.getInstance().getResident(p).getTown().getName(), percent);
				Main.incomeTaxData.saveConfig();
			} catch (NotRegisteredException e) {
			}
			
			p.sendMessage(Helper.Chatlabel() + Helper.color("&3Town &aincome tax rate set to &6" + String.valueOf(percent*100) + "%"));
			

			return true;

		} else if (args[0].equalsIgnoreCase("nationincometax")) {
			
			if (!ConfigVals.incomeTaxEnabled) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cIncome tax is disabled on this server"));
				return false;
			}
			
			double percent;

			if (args.length != 2) {
				p.sendMessage(
						Helper.Chatlabel() + Helper.color("&cInvalid arguments. Usage: &e/governor nationincometax [rate]"));
				return false;
			}

			try {
				town = TownyAPI.getInstance().getResident(p).getTown();
			} catch (NotRegisteredException e1) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou must be in a town to execute this command"));
				return false;
			}

			try {
				percent = Double.parseDouble(args[1]);
				percent = Helper.round(percent, 3); // round amount to 2 decimal places
			} catch (NumberFormatException e) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cInvalid Format: rate must be a number"));
				return false;
			}
			
		
			if (percent > ConfigVals.maxTaxRate) {
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cYou cannot set an income tax above " + String.valueOf(ConfigVals.maxTaxRate*100) + "%"));
				p.sendMessage(Helper.Chatlabel() + Helper.color("&cExample: '0.05' = 5% income tax"));
				return false;
			}
			
			List<String> nationRanks = TownyAPI.getInstance().getResident(p).getTownRanks();
			
			
			if (nationRanks.isEmpty()) {
				if (!TownyAPI.getInstance().getResident(p).isKing()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou nation rank is not high enough to change the nation income tax rate"));
					return false;
				}
			}

			boolean allowedRank = false;
			for (int i = 0; i < ConfigVals.incomeTaxNationAllowedRanks.size(); i++) {
				for (int j = 0; j < nationRanks.size(); j++) {
					if (nationRanks.get(j).equalsIgnoreCase(ConfigVals.incomeTaxNationAllowedRanks.get(i))) {
						allowedRank = true;
					}
				}
			}

			if (!allowedRank) {
				if (!TownyAPI.getInstance().getResident(p).isKing()) {
					p.sendMessage(Helper.Chatlabel()
							+ Helper.color("&cYou nation rank is not high enough to change the nation income tax rate"));
					return false;
				}
			}
			
			try {
				Main.incomeNationTaxMap.put(TownyAPI.getInstance().getResident(p).getNation().getName(), percent);
				Main.incomeTaxData.getConfig().set("Nations." + TownyAPI.getInstance().getResident(p).getNation().getName(), percent);
				Main.incomeTaxData.saveConfig();
			} catch (TownyException e) {
			}
			
			p.sendMessage(Helper.Chatlabel() + Helper.color("&6Nation &aincome tax rate set to &6" + String.valueOf(percent*100) + "%"));
			

			return true;

		}

		// -----------------------------------------------------------------------------------------------------------------------

		return false;
	}
}
