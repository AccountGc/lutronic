package com.e3ps.change.ecn.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcnToPartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
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

			Date currentDate = new Date();
			String number = "N" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeNotice", EChangeNotice.EO_NUMBER);
			number = number + seqNo;

			EChangeNotice ecn = EChangeNotice.newEChangeNotice();

			Ownership ownership = Ownership.newOwnership(eco.getEcnUser());

			System.out.println("ownership=" + ownership);

			ecn.setOwnership(ownership); // 담당자 세팅
			ecn.setEoName(eco.getEoName());
			ecn.setEoNumber(number);
			ecn.setModel(eco.getModel());
			ecn.setEoCommentA(eco.getEoCommentA());
			ecn.setEoCommentB(eco.getEoCommentB());
			ecn.setEoCommentC(eco.getEoCommentC());
			ecn.setEoCommentD(eco.getEoCommentD());
			ecn.setEoCommentE(eco.getEoCommentE());
			ecn.setEoType(eco.getEoType());
			ecn.setEco(eco);

			String location = "/Default/설계변경/ECN";
			String lifecycle = "LC_ECN";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) ecn, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(ecn,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			ecn = (EChangeNotice) PersistenceHelper.manager.save(ecn);

			// 설변 품목 개수 만큼 돈다..
			QuerySpec query = new QuerySpec();
			int idx_link = query.appendClassList(EcoPartLink.class, true);
			int idx_m = query.appendClassList(WTPartMaster.class, false);

			ClassAttribute ca_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
			ClassAttribute ca_link = new ClassAttribute(EcoPartLink.class, "roleAObjectRef.key.id");

			query.appendWhere(new SearchCondition(ca_m, "=", ca_link), new int[] { idx_m, idx_link });
			query.appendAnd();
			query.appendWhere(new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", "=",
					eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_link });

			QuerySpecUtils.toOrderBy(query, idx_m, WTPartMaster.class, WTPartMaster.NUMBER, false);
			QueryResult qr = PersistenceHelper.manager.find(query);

			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				EcoPartLink link = (EcoPartLink) obj[0];
				WTPartMaster master = link.getPart();
				String version = link.getVersion();
				WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				String group = "";
				if (isApproved) {
					WTPart next_part = (WTPart) EChangeUtils.getNext(part);
					group = EChangeUtils.manager.getPartGroup(next_part, eco);
				} else {
					group = EChangeUtils.manager.getPartGroup(part, eco);
				}

				String[] groups = group.split(",");
				for (String s : groups) {
					EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(s.trim());
					System.out.println("ecr=" + ecr);
					EcnToPartLink eLink = EcnToPartLink.newEcnToPartLink(ecn, part);
					eLink.setEcr(ecr);
					PersistenceHelper.manager.save(eLink);
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

	@Override
	public void save(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				String worker_oid = (String) editRow.get("worker_oid");
				EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
				WTUser worker = (WTUser) CommonUtil.getObject(worker_oid);
				ecn.setWorker(worker);
				PersistenceHelper.manager.modify(ecn);
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
