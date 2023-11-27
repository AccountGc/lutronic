package com.e3ps.change.ecn.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcnToPartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;

import net.sf.json.JSONArray;
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

	@Override
	public void create(EChangeOrder eco, ArrayList<EcoPartLink> ecoParts, ArrayList<EOCompletePartLink> completeParts)
			throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 완제품개수만큼 ECN이 발행된다???

//			Date currentDate = new Date();
//			String number = "N" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
//			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeNotice", EChangeNotice.EO_NUMBER);
//			number = number + seqNo;

			for (EOCompletePartLink link : completeParts) {
				EChangeNotice ecn = EChangeNotice.newEChangeNotice();
				WTPartMaster m = link.getCompletePart();

				ecn.setPartName(m.getName()); // 완제품 명으로 하냐.. 번호로하냐
				ecn.setPartNumber(m.getNumber());
				ecn.setEoName(eco.getEoName());
				ecn.setEoNumber("N" + eco.getEoNumber());
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

				for (EcoPartLink l : ecoParts) {
					WTPartMaster master = l.getPart();
					String version = l.getVersion();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);

					ArrayList<WTPart> end = PartHelper.manager.collectEndItem(part, new ArrayList<WTPart>());
					for (WTPart endPart : end) {
						WTPartMaster endMaster = (WTPartMaster) endPart.getMaster();
						// 최종품목이 포함되어있을 경우??
						if (endMaster.getPersistInfo().getObjectIdentifier().getId() == m.getPersistInfo()
								.getObjectIdentifier().getId()) {
							boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
							String group = "";
							if (isApproved) {
								WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
								group = EChangeUtils.manager.getPartGroup(next_part, eco);
							} else {
								group = EChangeUtils.manager.getPartGroup(part, eco);
							}

							String[] groups = group.split(",");
							for (String s : groups) {
								EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(s.trim());
								EcnToPartLink eLink = EcnToPartLink.newEcnToPartLink(ecn, part);
								eLink.setEcr(ecr);
								eLink.setCompletePart(endMaster);
								PersistenceHelper.manager.save(eLink);
							}
						}
					}
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
