package me.ShermansWorld.Governor.incometax;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.api.JobsPaymentEvent;

public class JobsListener implements Listener {
	
	public static boolean isJobsPayment = false;
	
	@EventHandler
	public void jobsIncome(JobsPaymentEvent e) {
		isJobsPayment = true;
		
	}
}	
