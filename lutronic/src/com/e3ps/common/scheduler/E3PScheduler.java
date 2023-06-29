package com.e3ps.common.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import wt.method.RemoteAccess;

public class E3PScheduler implements Job, RemoteAccess {
	public void execute(JobExecutionContext cntxt) throws JobExecutionException {
		try {
			// Project 스케줄러
			//System.out.println("### Project Scheduler start...");

			SchedulingMethod.startTask();

			//System.out.println("### Project Scheduler end...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		SchedulingMethod.startTask();
	}
}
