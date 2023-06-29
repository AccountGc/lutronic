package com.e3ps.common.scheduler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerServlet extends HttpServlet {

	SchedulerFactory schedFact = new StdSchedulerFactory();

	public void init() throws ServletException {

		System.out
				.println("init()........................................................................");

		try {
			Scheduler sched = schedFact.getScheduler();
			sched.start();

			JobDetail jobDetail = new JobDetail("PDFMS", "SCHEDULER", E3PScheduler.class);

			CronTrigger trigger = new CronTrigger("PDFMS", "SCHEDULER");
			trigger.setCronExpression("0 0 0 * * ?");
			sched.scheduleJob(jobDetail, trigger);

			System.out
					.println("############### schuduer.... SchedulerServlet........");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void destroy() {
		try {
			Scheduler sched = schedFact.getScheduler();
			sched.shutdown();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {

	}
}
