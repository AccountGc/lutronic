package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.ecn.dto.EcnDTO;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcnService extends StandardManager implements EcnService {

	public static StandardEcnService newStandardEcnService() throws WTException {
		StandardEcnService instance = new StandardEcnService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(ecn);

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
	public void create(EChangeOrder eco) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn =EChangeNotice.newEChangeNotice();
			// 나머지 정보들 대충 세팅 해서 일단 리스트 나오게
			ecn.setEco(eco);
			PersistenceHelper.manager.save(ecn);

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
