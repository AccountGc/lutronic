package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.common.util.CommonUtil;

import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardActivityService extends StandardManager implements ActivityService {

	public static StandardActivityService newStandardActivityService() throws WTException {
		StandardActivityService instance = new StandardActivityService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(Map<String, ArrayList<Map<String, String>>> params) throws Exception {
		ArrayList<Map<String, String>> list = params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> map : list) {
				String oid = map.get("oid");
				EChangeActivityDefinition def = (EChangeActivityDefinition) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(def);
			}

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
