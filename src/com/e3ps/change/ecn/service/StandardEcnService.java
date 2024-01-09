package com.e3ps.change.ecn.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcnToPartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.PartToSendLink;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
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
import wt.part.WTPart;
import wt.part.WTPartMaster;
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

			QueryResult qr = PersistenceHelper.manager.navigate(ecn, "part", EcnToPartLink.class, false);
			while (qr.hasMoreElements()) {
				EcnToPartLink link = (EcnToPartLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

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

			// 완제품 개수 만큼 일단 ECN이 생성되야 생성
			DecimalFormat df = new DecimalFormat("00");
			int idx = 0;
			for (EOCompletePartLink link : completeParts) {
				EChangeNotice ecn = EChangeNotice.newEChangeNotice();
				WTPartMaster m = link.getCompletePart();

				ecn.setPartName(m.getName()); // 완제품 명으로 하냐.. 번호로하냐
				ecn.setPartNumber(m.getNumber());
				ecn.setEoName(eco.getEoName());
				ecn.setEoNumber("N" + eco.getEoNumber() + df.format(idx));
				ecn.setModel(eco.getModel());
				ecn.setEoCommentA(eco.getEoCommentA());
				ecn.setEoCommentB(eco.getEoCommentB());
				ecn.setEoCommentC(eco.getEoCommentC());
				ecn.setEoCommentD(eco.getEoCommentD());
				ecn.setEoCommentE(eco.getEoCommentE());
				ecn.setEoType(eco.getEoType());
				ecn.setEco(eco);
				ecn.setProgress("작업중 ");
				ecn.setRate(0D);

				String location = "/Default/설계변경/ECN";
				String lifecycle = "LC_ECN";

				Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
				FolderHelper.assignLocation((FolderEntry) ecn, folder);
				// 문서 lifeCycle 설정
				LifeCycleHelper.setLifeCycle(ecn,
						LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
				ecn = (EChangeNotice) PersistenceHelper.manager.save(ecn);

//				for (EcoPartLink l : ecoParts) {
//					WTPartMaster master = l.getPart();
//					String version = l.getVersion();
//					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
//
//					JSONArray end = PartHelper.manager.end(part.getPersistInfo().getObjectIdentifier().getStringValue(),
//							null);
//					for (int k = 0; k < end.size(); k++) {
//						Map<String, String> endMap = (Map<String, String>) end.get(k);
//						String part_oid = endMap.get("oid");
//						WTPart endPart = (WTPart) CommonUtil.getObject(part_oid);
//						WTPartMaster endMaster = (WTPartMaster) endPart.getMaster();
//						// 최종품목이 포함되어있을 경우??
//						if (endMaster.getPersistInfo().getObjectIdentifier().getId() == m.getPersistInfo()
//								.getObjectIdentifier().getId()) {
//							boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
//							String group = "";
//							if (isApproved) {
//								WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
//								if (next_part != null) {
//									group = EChangeUtils.manager.getPartGroup(next_part, eco);
//								}
//							} else {
//								if (part != null) {
//									group = EChangeUtils.manager.getPartGroup(part, eco);
//								}
//							}
//
//							if (group.length() > 0) {
//
//								String[] groups = group.split(",");
//								for (String s : groups) {
//									EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(s.trim());
//									EcnToPartLink eLink = EcnToPartLink.newEcnToPartLink(ecn, part);
//									eLink.setEcr(ecr);
//									eLink.setCompletePart(endMaster);
//									PersistenceHelper.manager.save(eLink);
//								}
//							}
//						}
//					}
//				}
				idx++;
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
	public void complete(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		ArrayList<Map<String, Object>> gridData = (ArrayList<Map<String, Object>>) params.get("gridData");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
			ecn.setProgress("완료");

			for (Map<String, Object> m : gridData) {
				WTPart part = (WTPart) CommonUtil.getObject((String) m.get("poid"));
				EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject((String) m.get("coid"));
				ArrayList<Map<String, String>> countrys = NumberCodeHelper.manager.getCountry();
				for (Map<String, String> country : countrys) {
					String code = country.get("code");
					String value = (String) m.get(code + "_date");
					boolean isSend = (boolean) m.get(code + "_isSend");
					if ("".equals(value) && !isSend) {
						// 보낼거 다 보내고 완료 처리하는거로 한다 없는건 N/A로 강제기입
						PartToSendLink link = PartToSendLink.newPartToSendLink();
						link.setEcr(ecr);
						link.setNation(code);
						link.setPart(part);
						link.setEcn(ecn);
						link.setIsSend(true);
						link.setSendDate(DateUtil.convertDate("3000-12-31"));
						PersistenceHelper.manager.save(link);
					}
				}
			}

			PersistenceHelper.manager.modify(ecn);

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
	public void update(String oid) throws Exception {
		try {
			EcnToPartLink link = (EcnToPartLink) CommonUtil.getObject(oid);
			WTPart part = link.getPart();
			EChangeNotice ecn = link.getEcn();
			EChangeRequest cr = link.getEcr();

			ArrayList<Map<String, String>> countrys = NumberCodeHelper.manager.getCountry();
			int size = countrys.size();
			int calc = countrys.size(); // 전체..개수 같다고 시작
			for (Map<String, String> country : countrys) {
				String code = country.get("code");
				PartToSendLink sendLink = EcnHelper.manager.getSendLink(ecn, part, code, cr);
				if (sendLink == null) {
					calc--; // 없는거 만큼 하나식 숫자를 줄여나간다
				}
			}

			double rate = 0D;
			if (calc == 0) {
				rate = 0D;
			} else {
				rate = (double) calc / size;
			}

			if (rate == 1D) {
				link.setWorkEnd(true);
			}
//			link.setRate(Math.round(100 * rate));
			link.setRate(100 * rate);
			PersistenceHelper.manager.modify(link);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
