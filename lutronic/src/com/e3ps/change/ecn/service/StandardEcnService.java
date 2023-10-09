package com.e3ps.change.ecn.service;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.util.CommonUtil;

import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcnService extends StandardManager implements EcnService {

	public static StandardEcnService newStandardEcnService() throws WTException {
		StandardEcnService instance = new StandardEcnService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(ecn);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

}
