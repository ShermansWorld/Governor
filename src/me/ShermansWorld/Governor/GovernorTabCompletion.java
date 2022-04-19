package me.ShermansWorld.Governor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.object.Town;

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
			}
			completions.add("help");
			completions.add("version");
			return completions;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("claim")) {
				for (Town town : InactiveTownListener.inactiveTowns) {
					completions.add(town.getName());
				}
			} 
		}
		return Collections.emptyList();
	}
}

