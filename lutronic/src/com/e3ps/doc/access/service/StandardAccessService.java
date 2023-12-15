package com.e3ps.doc.access.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.doc.access.FolderAccessWTGroupLink;
import com.e3ps.doc.access.FolderAccessWTUserLink;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardAccessService extends StandardManager implements AccessService {

	public static StandardAccessService newStandardAccessService() throws WTException {
		StandardAccessService instance = new StandardAccessService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(Map<String, Object> params) throws Exception {
		String foid = (String) params.get("foid");
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) params.get("data");
		Transaction trs = new Transaction();
		try {
			trs.start();
			Folder f = (Folder) CommonUtil.getObject(foid);
			// 기존 권한 모두 삭제 후 재 생성으로간다...
			QueryResult qr = PersistenceHelper.manager.navigate(f, "user", FolderAccessWTUserLink.class, false);
			while (qr.hasMoreElements()) {
				FolderAccessWTUserLink link = (FolderAccessWTUserLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			qr.reset();
			qr = PersistenceHelper.manager.navigate(f, "group", FolderAccessWTGroupLink.class, false);
			while (qr.hasMoreElements()) {
				FolderAccessWTGroupLink link = (FolderAccessWTGroupLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (Map<String, String> map : list) {
				String oid = map.get("oid");
				if (oid.indexOf("WTUser") > -1) {
					WTUser user = (WTUser) CommonUtil.getObject(oid);
					FolderAccessWTUserLink link = FolderAccessWTUserLink.newFolderAccessWTUserLink(f, user);
					PersistenceHelper.manager.save(link);
				} else if (oid.indexOf("WTGroup") > -1) {
					WTGroup group = (WTGroup) CommonUtil.getObject(oid);
					FolderAccessWTGroupLink link = FolderAccessWTGroupLink.newFolderAccessWTGroupLink(f, group);
					PersistenceHelper.manager.save(link);

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

	}
}
