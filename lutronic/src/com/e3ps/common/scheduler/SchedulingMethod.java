package com.e3ps.common.scheduler;

import java.io.Serializable;
import java.net.URL;

import com.e3ps.drawing.service.EpmSearchHelper;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTProperties;

/**
 * Desc :
 * 
 * @Author :
 * @Date : 2012. 3. 7.
 * @Version :
 */

@SuppressWarnings("serial")
public class SchedulingMethod implements Serializable, RemoteAccess {

	public static void _startTask() throws Exception {
		boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
		SessionContext sessioncontext = SessionContext.newContext();

		try {

			SessionHelper.manager.setAdministrator();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SessionContext.setContext(sessioncontext);
			SessionServerHelper.manager.setAccessEnforced(bool);
		}

	}

	public static void startTask() throws Exception {

		try {

			WTProperties prover = WTProperties.getServerProperties();
			String server = prover.getProperty("wt.cache.master.codebase");

			if (server == null) {
				server = prover.getProperty("wt.server.codebase");
			}
			Object obj = RemoteMethodServer.getInstance(new URL(server + "/"),
					"BackgroundMethodServer").invoke("_startTask",
					SchedulingMethod.class.getName(), null, null, null);
			return;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void _cadPublishScheduler() throws Exception {
		boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
		SessionContext sessioncontext = SessionContext.newContext();

		try {

			SessionHelper.manager.setAdministrator();
			//System.out.println("::::::::::::::: SchedulingMethod _cadPublishScheduler START:::::::::::::::");
			EpmSearchHelper.service.cadPublishScheduler();
			//System.out.println("::::::::::::::: SchedulingMethod _cadPublishScheduler END:::::::::::::::");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SessionContext.setContext(sessioncontext);
			SessionServerHelper.manager.setAccessEnforced(bool);
		}

	}

	public static void cadPublishScheduler() throws Exception {

		try {

			WTProperties prover = WTProperties.getServerProperties();
			String server = prover.getProperty("wt.cache.master.codebase");

			if (server == null) {
				server = prover.getProperty("wt.server.codebase");
			}
			Object obj = RemoteMethodServer.getInstance(new URL(server + "/"),
					"BackgroundMethodServer").invoke("_cadPublishScheduler",
					SchedulingMethod.class.getName(), null, null, null);
			return;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void main(String args[]) {

	}
}
