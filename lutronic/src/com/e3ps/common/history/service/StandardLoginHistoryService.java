package com.e3ps.common.history.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.common.history.LoginHistory;

import wt.fc.PersistenceHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.OrganizationServicesMgr;
import wt.org.OrganizationServicesServerHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardLoginHistoryService extends StandardManager implements LoginHistoryService {

	public static StandardLoginHistoryService newStandardLoginHistoryService() throws WTException {
		StandardLoginHistoryService instance = new StandardLoginHistoryService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> create(String j_username, HttpServletRequest request) throws Exception {
		String ip = request.getRemoteAddr();
		Map<String, Object> result = new HashMap<>();
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			WTUser user = OrganizationServicesMgr.getUser(j_username);
			LoginHistory history = LoginHistory.newLoginHistory();
			history.setIp(ip);
			history.setId(j_username);
			history.setName(user.getFullName());
			PersistenceHelper.manager.save(history);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}

		return result;
	}
}
