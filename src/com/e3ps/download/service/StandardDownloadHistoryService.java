package com.e3ps.download.service;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.download.DownloadHistory;

import wt.content.ApplicationData;
import wt.content.HolderToContent;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;

public class StandardDownloadHistoryService extends StandardManager implements DownloadHistoryService {

	public static StandardDownloadHistoryService newStandardDownloadHistoryService() throws Exception {
		final StandardDownloadHistoryService instance = new StandardDownloadHistoryService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 횟수 업데이트는 없는거로 한다..
			ApplicationData data = (ApplicationData) CommonUtil.getObject(oid);
			QueryResult result = PersistenceHelper.manager.navigate(data, "theContentHolder", HolderToContent.class);
			Persistable per = null;
			if (result.hasMoreElements()) {
				per = (Persistable) result.nextElement();
			}
			// 링크인데.. 중복으로 나올 가능성은 적어 보이는데..
			DownloadHistory history = DownloadHistory.newDownloadHistory();
			WTUser user = CommonUtil.sessionUser();
			history.setName(data.getFileName());
			history.setPersist(per);
			history.setCnt(1);
			history.setUser(user);
			PersistenceHelper.manager.save(history);

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