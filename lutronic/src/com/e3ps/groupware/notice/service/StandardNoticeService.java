package com.e3ps.groupware.notice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;

public class StandardNoticeService extends StandardManager implements NoticeService {

	public static StandardNoticeService newStandardNoticeService() throws Exception {
		final StandardNoticeService instance = new StandardNoticeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notice notice = (Notice) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(notice);

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
		Transaction trs = new Transaction();
		String oid = (String) params.get("oid");
		String title = (String) params.get("title");
		String contents = (String) params.get("contents");
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
		try {
			trs.start();

			Notice notice = (Notice) CommonUtil.getObject(oid);
			notice.setTitle(title);
			notice.setContents(contents);
//			notice.setIsPopup(isPopup);
			notice = (Notice) PersistenceHelper.manager.modify(notice);

			QueryResult result = ContentHelper.service.getContentsByRole(notice, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				ContentServerHelper.service.deleteContent(notice, data);
			}

			for (int i = 0; i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(notice);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(notice, applicationData, vault.getPath());
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}

	@Override
	public void updateCount(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notice notice = (Notice) CommonUtil.getObject(oid);
			notice.setCount(notice.getCount() + 1);
			PersistenceHelper.manager.modify(notice);

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
	public List<NoticeData> getPopUpNotice() {

		List<NoticeData> returnData = new ArrayList<NoticeData>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(Notice.class, true);
			query.appendWhere(new SearchCondition(Notice.class, Notice.IS_POPUP, SearchCondition.IS_TRUE),
					new int[] { idx });
			QueryResult rt = PersistenceHelper.manager.find(query);
			while (rt.hasMoreElements()) {
				Object[] o = (Object[]) rt.nextElement();
				Notice notice = (Notice) o[0];
				NoticeData data = new NoticeData(notice);
				String oid = CommonUtil.getOIDString(notice);
				returnData.add(data);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnData;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		String title = (String) params.get("title");
		String contents = (String) params.get("contents");
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
		try {
			trs.start();

			Notice notice = Notice.newNotice();
			notice.setTitle(title);
			notice.setContents(contents);
			notice.setOwner(SessionHelper.manager.getPrincipalReference());
			PersistenceHelper.manager.save(notice);

			for (int i = 0; i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(notice);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(notice, applicationData, vault.getPath());
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}

}
