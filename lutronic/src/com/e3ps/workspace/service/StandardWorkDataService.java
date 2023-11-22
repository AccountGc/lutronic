package com.e3ps.workspace.service;

import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.WorkData;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
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

			Ownership ownership = CommonUtil.sessionOwner();

			WorkData data = WorkData.newWorkData();
			data.setPer(per);
			data.setOwnership(ownership);
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

	@Override
	public void _submit(Map<String, Object> params) throws Exception {
		String oid = (String)params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();


			WorkData data = (WorkData)CommonUtil.getObject(oid);

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
