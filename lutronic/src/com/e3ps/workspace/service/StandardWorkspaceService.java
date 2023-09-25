package com.e3ps.workspace.service;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class StandardWorkspaceService extends StandardManager implements WorkspaceService {

	public static StandardWorkspaceService newStandardWorkspaceService() throws WTException {
		StandardWorkspaceService instance = new StandardWorkspaceService();
		instance.initialize();
		return instance;
	}

	@Override
	public void convert() throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WorkItem.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WorkItem workItem = (WorkItem) obj[0];
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
