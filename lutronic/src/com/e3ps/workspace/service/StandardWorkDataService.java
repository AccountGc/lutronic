package com.e3ps.workspace.service;

import com.e3ps.workspace.WorkData;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkDataService extends StandardManager implements WorkDataService {

	public static StandardWorkDataService newStandardWorkDataService() throws WTException {
		StandardWorkDataService instance = new StandardWorkDataService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Persistable per) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			WorkData data = WorkData.newWorkData();
			data.setPer(per);
			PersistenceHelper.manager.save(data);

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
