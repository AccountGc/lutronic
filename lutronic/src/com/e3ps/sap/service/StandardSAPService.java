package com.e3ps.sap.service;

import org.apache.xmlbeans.impl.store.Saaj;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.iba.AttributeKey.ECOKey;

import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSAPService extends StandardManager implements SAPService {

	public static StandardSAPService newStandardSAPService() throws WTException {
		StandardSAPService instance = new StandardSAPService();
		instance.initialize();
		return instance;
	}

	@Override
	public void sendEoTo(EChangeOrder eco) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String eoType = eco.getEoType(); // 설계변경, 양산 EO 인경우애만 ERP 전송

			// ECO
			if (eoType.equals(ECOKey.ECO_CHANGE)) {
				sendECOERP(eco);
				// EO
			} else if (eoType.equals(ECOKey.ECO_PRODUCT)) {
				sendEoERP(eco);
			} else {
				return;
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
