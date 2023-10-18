package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.WCUtil;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
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
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = (String) params.get("oid");
			boolean dependency = ActivityHelper.manager.dependency(oid);
			if (dependency) {
				map.put("success", false);
				map.put("msg", "삭제 하려는 루트 활동에 설계변경 활동이 존재합니다.");
				return map;
			}

			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(def);

			map.put("success", true);

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
				act.setSortNumber(Integer.parseInt(sort));
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

	@Override
	public void save(HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap) throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = dataMap.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (LinkedHashMap<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				String step = (String) editRow.get("step");
				String name = (String) editRow.get("name");
				String activity_name = (String) editRow.get("activity_name"); // code..
				int sort = (int) editRow.get("sort");
				String activeUser_oid = (String) editRow.get("activeUser_oid"); // wtuser
				EChangeActivityDefinition act = (EChangeActivityDefinition) CommonUtil.getObject(oid);
				act.setName(name);
				act.setActiveUser((WTUser) CommonUtil.getObject(activeUser_oid));
				act.setSortNumber(sort);
				act.setStep(step);
				act.setActiveType(activity_name);
				PersistenceHelper.manager.modify(act);
			}

			for (LinkedHashMap<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				EChangeActivityDefinition act = (EChangeActivityDefinition) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(act);
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

	@Override
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String name = (String) params.get("name");
		String sort = (String) params.get("sort");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();
			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
			def.setName(name);
			def.setSortNumber(Integer.parseInt(sort));
			def.setDescription(description);
			PersistenceHelper.manager.modify(def);

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
	public void saveActivity(EChangeOrder eo, ArrayList<Map<String, String>> list) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			int sort = 0;
			for (Map<String, String> map : list) {
				String step_name = map.get("step_name");
				String name = map.get("name");
				String active_type = map.get("active_type");
				String activeUser_oid = map.get("activeUser_oid");
				String finishDate = map.get("finishDate");

				WTUser user = (WTUser) CommonUtil.getObject(activeUser_oid);

				EChangeActivity eca = EChangeActivity.newEChangeActivity();
				eca.setStep(step_name);
				eca.setName(name);
				eca.setActiveType(active_type);
				eca.setActiveUser(user);
				eca.setFinishDate(DateUtil.convertDate(finishDate));
				eca.setSortNumber(sort);
				eca.setEo(eo);

				String location = "/Default/설계변경/ECA";
				String lifecycle = "LC_ECA_PROCESS";

				Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
				FolderHelper.assignLocation((FolderEntry) eca, folder);
				// 문서 lifeCycle 설정
				LifeCycleHelper.setLifeCycle(eca,
						LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

				PersistenceHelper.manager.save(eca);
				sort++;
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

	@Override
	public void deleteActivity(EChangeOrder eo) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeActivity.class, true);
		SearchCondition sc = new SearchCondition(EChangeActivity.class, "eoReference.key.id", "=",
				eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eChangeActivity = (EChangeActivity) obj[0];
			PersistenceHelper.manager.delete(eChangeActivity);
		}

	}

	@Override
	public void saveLink(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
			for (String s : list) {
				WTDocument doc = (WTDocument) CommonUtil.getObject(s);
				DocumentActivityLink link = DocumentActivityLink
						.newDocumentActivityLink((WTDocumentMaster) doc.getMaster(), eca);
				PersistenceHelper.manager.save(link);
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

	@Override
	public void deleteLink(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			DocumentActivityLink link = (DocumentActivityLink) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(link);

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
