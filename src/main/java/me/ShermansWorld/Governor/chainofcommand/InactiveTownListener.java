package me.ShermansWorld.Governor.chainofcommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.ShermansWorld.Governor.Helper;
import me.ShermansWorld.Governor.Main;
import me.ShermansWorld.Governor.config.ConfigVals;

public class InactiveTownListener {

	public static List<Town> inactiveTowns;

	public static void initListener() {

		final List<Resident> mayors = new ArrayList<Resident>();
		inactiveTowns = new ArrayList<Town>();

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			public void run() {
				List<Town> towns = TownyAPI.getInstance().getTowns();
				mayors.clear();
				for (int i = 0; i < towns.size(); i++) {
					mayors.add(towns.get(i).getMayor());
				}
				if (!mayors.isEmpty()) {
					inactiveTowns.clear();
					for (int i = 0; i < mayors.size(); i++) {
						try {
							if (!mayors.get(i).isOnline()) {
								long now = System.currentTimeMillis();
								long lastPlayed = mayors.get(i).getLastOnline();
								long lastOnline = now - lastPlayed; // miliseconds player has been offline
								long milisecPerDay = 1000 * 60 * 60 * 24; // 1000 milisec/sec * 60 sec/min * 60 min/hr *
																			// 24 hr/day
								long limit = milisecPerDay * ConfigVals.daysInactive;
								//long limit = 100; //short timer for testing
								if (lastOnline > limit) {
									try {
										inactiveTowns.add(mayors.get(i).getTown());
									} catch (NotRegisteredException e) {
										Bukkit.getLogger().warning(Helper.Chatlabel()
												+ "Error when getting mayor's Town in InactiveTownListener");
									}
								}
							}
						} catch (NullPointerException e) {

						}
					}
				}
			}
		}, 0L, 12000L); // 0 Tick initial delay, 12000 Tick (10 min) between repeats
	}
}
