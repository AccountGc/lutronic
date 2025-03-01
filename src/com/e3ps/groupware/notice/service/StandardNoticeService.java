package com.e3ps.groupware.notice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.dto.NoticeDTO;

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
		boolean isPopup = (boolean) params.get("isPopup");
		try {
			trs.start();

			Notice notice = (Notice) CommonUtil.getObject(oid);
			notice.setTitle(title);
			notice.setContents(contents);
			notice.setIsPopup(isPopup);
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
	public void read(String oid) throws Exception {
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
	public void create(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		String title = (String) params.get("title");
		String contents = (String) params.get("contents");
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
		boolean isPopup = (boolean) params.get("isPopup");
		try {
			trs.start();

			Notice notice = Notice.newNotice();
			notice.setTitle(title);
			notice.setContents(contents);
			notice.setOwner(SessionHelper.manager.getPrincipalReference());
			notice.setIsPopup(isPopup);
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
