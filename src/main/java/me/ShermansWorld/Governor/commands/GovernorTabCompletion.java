package me.ShermansWorld.Governor.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

import me.ShermansWorld.Governor.chainofcommand.InactiveTownListener;
import me.ShermansWorld.Governor.config.ConfigVals;

public class GovernorTabCompletion implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		Player p = (Player)sender;
		if (args.length == 1) {
			if (p.hasPermission("Governor.reload")) {
				completions.add("reload");
			}
			completions.add("taxtown");
			completions.add("taxnation");
			if (ConfigVals.claimEnabled) {
				completions.add("claim");
				completions.add("checkmayor");
			}
			if (ConfigVals.incomeTaxEnabled) {
				completions.add("townincometax");
				completions.add("nationincometax");
			}
			completions.add("help");
			completions.add("version");
			return completions;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("claim")) {
				for (Town town : InactiveTownListener.inactiveTowns) {
					completions.add(town.getName());
				}
				return completions;
			}
			if (args[0].equalsIgnoreCase("checkmayor")) {
				for (Town town : TownyAPI.getInstance().getTowns()) {
					completions.add(town.getName());
				}
			}
			if (args[0].equalsIgnoreCase("townincometax") || args[0].equalsIgnoreCase("nationincometax")) {
				completions.add("0.05");
				return completions;
			}
		}
		return Collections.emptyList();
	}
}

