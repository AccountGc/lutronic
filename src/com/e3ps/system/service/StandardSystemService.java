package com.e3ps.system.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.system.PrintHistory;
import com.e3ps.system.SAPInterfacePartLogger;
import com.e3ps.system.SystemFasooLog;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSystemService extends StandardManager implements SystemService {

	public static StandardSystemService newStandardSystemService() throws WTException {
		StandardSystemService instance = new StandardSystemService();
		instance.initialize();
		return instance;
	}

	@Override
	public void fasooLogger(String name, HttpServletRequest request) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			SystemFasooLog logger = SystemFasooLog.newSystemFasooLog();
			logger.setName(name);
			logger.setOwnership(CommonUtil.sessionOwner());
			logger.setIp(request.getLocalAddr());
			logger.setName(name);
			PersistenceHelper.manager.save(logger);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void saveSendPartLogger(Map<String, Object> params) throws Exception {
		String AENNR8 = (String) params.get("AENNR8");
		String MATNR = (String) params.get("MATNR");
		String MAKTX = (String) params.get("MAKTX");
		String MEINS = (String) params.get("MEINS");
		String ZSPEC = (String) params.get("ZSPEC");
		String ZMODEL = (String) params.get("ZMODEL");
		String ZPRODM = (String) params.get("ZPRODM");
		String ZDEPT = (String) params.get("ZDEPT");
		String ZDWGNO = (String) params.get("ZDWGNO");
		String ZEIVR = (String) params.get("ZEIVR");
		String ZPREPO = (String) params.get("ZPREPO");
		String BRGEW = (String) params.get("BRGEW");
		String GEWEI = (String) params.get("GEWEI");
		String ZMATLT = (String) params.get("ZMATLT");
		String ZPOSTP = (String) params.get("ZPOSTP");
		String ZDEVND = (String) params.get("ZDEVND");
		boolean sendResult = (boolean) params.get("sendResult");
		Transaction trs = new Transaction();
		try {
			trs.start();

			SAPInterfacePartLogger logger = SAPInterfacePartLogger.newSAPInterfacePartLogger();
			logger.setAennr8(AENNR8);
			logger.setMatnr(MATNR);
			logger.setMaktx(MAKTX);
			logger.setMeins(MEINS);
			logger.setZspec(ZSPEC);
			logger.setZmodel(ZMODEL);
			logger.setZprodm(ZPRODM);
			logger.setZdept(ZDEPT);
			logger.setZdwgno(ZDWGNO);
			logger.setZeivr(ZEIVR);
			logger.setZprepo(ZPREPO);
			logger.setBrgew(BRGEW);
			logger.setGewei(GEWEI);
			logger.setZmatlt(ZMATLT);
			logger.setZpostp(ZPOSTP);
			logger.setZdevnd(ZDEVND);
			logger.setSendResult(sendResult);
			logger.setOwnership(CommonUtil.sessionOwner());

			PersistenceHelper.manager.save(logger);

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
	public boolean savePrintHistory(HttpServletRequest request) throws Exception {
		boolean isPrint = true;
		String oid = request.getParameter("oid");
		PeopleDTO dto = new PeopleDTO();
		String ip = request.getRemoteAddr();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Persistable per = CommonUtil.getObject(oid);
			String targetName = "";
			if (per instanceof WTDocument) {
				WTDocument d = (WTDocument) per;
				targetName = d.getNumber() + " [" + d.getName() + "]";
			} else if (per instanceof ECPRRequest) {
				ECPRRequest d = (ECPRRequest) per;
				targetName = d.getEoNumber() + " [" + d.getEoName() + "]";
			} else if (per instanceof ECRMRequest) {
				ECRMRequest d = (ECRMRequest) per;
				targetName = d.getEoNumber() + " [" + d.getEoName() + "]";
			} else if (per instanceof EChangeRequest) {
				EChangeRequest d = (EChangeRequest) per;
				targetName = d.getEoNumber() + " [" + d.getEoName() + "]";
			}

			PrintHistory history = PrintHistory.newPrintHistory();
			history.setIp(ip);
			history.setName(dto.getName());
			history.setTargetName(targetName);
			history.setDepartmentName(dto.getDepartment_name());
			PersistenceHelper.manager.save(history);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			isPrint = false;
			throw e;
		} finally {
			if (trs != null) {
				isPrint = false;
				trs.rollback();
			}
		}
		return isPrint;
	}
}
