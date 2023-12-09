package com.e3ps.system.service;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.system.SystemFasooLog;

import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSystemService extends StandardManager implements SystemService {

	public static StandardSystemService newStandardSystemService() throws WTException {
		StandardSystemService instance = new StandardSystemService();
		instance.initialize();
		return instance;
	}

	@Override
	public void fasooLogger(String name, HttpServletRequest request) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			SystemFasooLog logger = SystemFasooLog.newSystemFasooLog();
			logger.setName(name);
			logger.setOwnership(CommonUtil.sessionOwner());
			logger.setIp(request.getLocalAddr());
			logger.setName(name);
			PersistenceHelper.manager.save(logger);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
