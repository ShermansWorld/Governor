package me.ShermansWorld.Governor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TownTaxTabCompletion implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			completions.add("accept");
			completions.add("deny");
			return completions;
		}
		return Collections.emptyList();
	}
}

