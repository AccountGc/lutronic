package com.e3ps.change.ecrm.service;

import com.e3ps.change.ecrm.dto.EcrmDTO;

import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcrmService extends StandardManager implements EcrmService {

	public static StandardEcrmService newStandardEcrmService() throws WTException {
		StandardEcrmService instance = new StandardEcrmService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcrmDTO dto) throws Exception {

		Transaction trs = new Transaction();
		try {
			trs.start();

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
