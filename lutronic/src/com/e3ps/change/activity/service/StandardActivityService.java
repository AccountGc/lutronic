package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

import wt.fc.PersistenceHelper;
import wt.org.WTUser;
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
	public Map<String, Object> delete(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String type = (String) params.get("type");
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			if ("root".equals(type)) {
				String oid = (String) params.get("oid");
				boolean dependency = ActivityHelper.manager.dependency(oid);
				if (dependency) {
					map.put("result", false);
					map.put("msg", "삭제 하려는 루트 활동에 설계변경 활동이 존재합니다.");
					return map;
				}

				EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(def);

			} else if ("act".equals(type)) {
				for (Map<String, String> m : list) {
					String oid = m.get("oid");
					EChangeActivityDefinition def = (EChangeActivityDefinition) CommonUtil.getObject(oid);
					PersistenceHelper.manager.delete(def);
				}
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
		return map;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String type = (String) params.get("type");
		Transaction trs = new Transaction();
		try {
			trs.start();

			if ("root".equals(type)) {
				String name = (String) params.get("name");
				String sort = (String) params.get("sort");
				String description = (String) params.get("description");

				EChangeActivityDefinitionRoot def = EChangeActivityDefinitionRoot.newEChangeActivityDefinitionRoot();
				def.setName(name);
				def.setSortNumber(Integer.parseInt(sort));
				def.setDescription(description);
				PersistenceHelper.manager.save(def);

			} else if ("act".equals(type)) {

				String oid = (String) params.get("oid");
				String name = (String) params.get("name");
				String step = (String) params.get("step");
				String activeType = (String) params.get("activeType");
				String sort = (String) params.get("sort");
				String description = (String) params.get("description");

				EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
				EChangeActivityDefinition act = EChangeActivityDefinition.newEChangeActivityDefinition();
				act.setName(name);
				act.setStep(step);
				act.setActiveType(activeType);
				act.setDescription(description);
				act.setSortNumber(Integer.parseInt(description));
				act.setRoot(def);
				PersistenceHelper.manager.save(act);
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
