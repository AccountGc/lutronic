package com.e3ps.change.eo.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.part.service.PartHelper;
import com.e3ps.workspace.service.WorkDataHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.ManagedBaseline;

public class StandardEoService extends StandardManager implements EoService {

	public static StandardEoService newStandardEoService() throws WTException {
		StandardEoService instance = new StandardEoService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EoDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows104 = dto.getRows104();
		ArrayList<Map<String, String>> rows200 = dto.getRows200();
		ArrayList<Map<String, String>> rows300 = dto.getRows300();
		boolean temprary = dto.isTemprary();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = "E" + DateUtil.getCurrentDateString("ym") + "N";
//			String seqNo = SequenceDao.manager.getSeqNo(number, "00", "EChangeOrder", EChangeOrder.EO_NUMBER);
//			number = number + "N" + seqNo;

			number = EoHelper.manager.getNextNumber(number);

			// 모델 배열 처리
			// US21,MD23,PN21,
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				if (rows300.size() - 1 == i) {
					model += n.getCode();
				} else {
					model += n.getCode() + ",";
				}
			}

			EChangeOrder eo = EChangeOrder.newEChangeOrder();

			eo.setEoName(dto.getName());
			eo.setEoNumber(number);
			eo.setModel(model);
			eo.setEoType(dto.getEoType());
			eo.setEoCommentA(dto.getEoCommentA());
			eo.setEoCommentB(dto.getEoCommentB());
			eo.setEoCommentC(dto.getEoCommentC());

			String location = "/Default/설계변경/EO";
			String lifecycle = "LC_ECO";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) eo, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(eo,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			eo = (EChangeOrder) PersistenceHelper.manager.save(eo);

			// 관련 링크들
			saveLink(eo, dto);

			// 완제품 링크 및 검증
			validateAndSaveCompletePart(eo, rows104);

			// 첨부 파일
			saveAttach(eo, dto);

			// 설변 활동 생성
			if (rows200.size() > 0) {
				ActivityHelper.service.saveActivity(eo, rows200);
			}

			// 활동이 잇을 경우 상태값 대기모드로 변경한다.
			if (rows200.size() > 0) {
//				WorkspaceHelper.service.stand(eo);
				// 설변활동이 있으면서 임시 활동일 경우...
				if (temprary) {
					State state = State.toState("TEMPRARY");
					LifeCycleHelper.service.setLifeCycleState(eo, state);
				} else {
					eo = (EChangeOrder) PersistenceHelper.manager.refresh(eo);
					LifeCycleHelper.service.setLifeCycleState(eo, State.toState("ACTIVITY"));
				}
			} else {
				if (temprary) {
					State state = State.toState("TEMPRARY");
					// 상태값 변경해준다 임시저장 <<< StateRB 추가..
					LifeCycleHelper.service.setLifeCycleState(eo, state);
				} else {
					WorkDataHelper.service.create(eo);
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

	private void saveLink(EChangeOrder eo, EoDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows90 = dto.getRows90();
		// 관련문서
		for (Map<String, String> row90 : rows90) {
			String gridState = row90.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row90.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
				DocumentEOLink link = DocumentEOLink.newDocumentEOLink(doc, eo);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	private void deleteLink(EChangeOrder eo) throws Exception {
		// 관련문서 삭제
		QueryResult result = PersistenceHelper.manager.navigate(eo, "document", DocumentEOLink.class, false);
		while (result.hasMoreElements()) {
			DocumentEOLink link = (DocumentEOLink) result.nextElement();
			PersistenceHelper.manager.delete(link);
		}
	}

	private void validateAndSaveCompletePart(EChangeOrder eo, ArrayList<Map<String, String>> rows104) throws Exception {
		// 완제품 연결
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (Map<String, String> map : rows104) {
			String oid = map.get("part_oid");
			WTPart part = (WTPart) CommonUtil.getObject(oid);

			Map<String, Object> m = EoHelper.manager.validatePart(part);
//			if (!(boolean) m.get("result")) {
//				throw new Exception((String) m.get("msg"));
//			}
//
//			Map<String, Object> c = EoHelper.manager.checkerCompletePart(part);
//			if (!(boolean) c.get("result")) {
//				throw new Exception((String) c.get("msg"));
//			}
			list.add(part);
		}
		saveCompletePart(eo, list);
	}

	/**
	 * 완제품 연결 제거 함수
	 */
	private void deleteCompletePart(EChangeOrder eo) throws Exception {
		// 완제품 삭제
		QueryResult result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class, false);
		while (result.hasMoreElements()) {
			EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * 완제품 연결 함수
	 */
	private void saveCompletePart(EChangeOrder eo, ArrayList<WTPart> list) throws Exception {
		for (WTPart part : list) {
			EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(), eo);
			link.setVersion(part.getVersionIdentifier().getSeries().getValue());
			PersistenceHelper.manager.save(link);
		}
	}

	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(EChangeOrder eo, EoDTO dto) throws Exception {
		ArrayList<String> secondarys = dto.getSecondarys();

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(eo);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(eo, applicationData, vault.getPath());
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);

			// 연관 문서 링크 삭제
			deleteLink(eo);

			// 관련 완제품 품목 삭제
			QueryResult result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class,
					false);
			while (result.hasMoreElements()) {
				EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			PersistenceHelper.manager.delete(eo);

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
	public void modify(EoDTO dto) throws Exception {
		boolean temprary = dto.isTemprary();
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 제품
		ArrayList<Map<String, String>> rows104 = dto.getRows104(); // 완제품
		ArrayList<Map<String, String>> rows200 = dto.getRows200(); // ECA

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 모델 배열 처리
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				if (rows300.size() - 1 == i) {
					model += n.getCode();
				} else {
					model += n.getCode() + ",";
				}
			}

			EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(dto.getOid());
			eo.setEoName(dto.getName());
			eo.setModel(model);
			eo.setEoType(dto.getEoType());
			eo.setEoCommentA(dto.getEoCommentA());
			eo.setEoCommentB(dto.getEoCommentB());
			eo.setEoCommentC(dto.getEoCommentC());

			eo = (EChangeOrder) PersistenceHelper.manager.modify(eo);

			if (temprary) {
				State state = State.toState("TEMPRARY");
				LifeCycleHelper.service.setLifeCycleState(eo, state);
			}

			// 관련 링크들
			deleteLink(eo);
			saveLink(eo, dto);

			deleteCompletePart(eo); // 지우고 새로 만드는거 맞음??
			validateAndSaveCompletePart(eo, rows104);

			// 첨부 파일
			removeAttach(eo);
			saveAttach(eo, dto);

			if (rows200.size() > 0) {
//				ActivityHelper.service.deleteActivity(eco);
				ActivityHelper.service.saveActivity(eo, rows200);
			}
			
			// 임시저장일 경우만 수정 가능한데...
//			if (rows200.size() > 0) {
//				if (temprary) {
//					State state = State.toState("TEMPRARY");
//					LifeCycleHelper.service.setLifeCycleState(eo, state);
//				} else {
//					eo = (EChangeOrder) PersistenceHelper.manager.refresh(eo);
//					LifeCycleHelper.service.setLifeCycleState(eo, State.toState("ACTIVITY"));
//				}
//			} else {
//				if (temprary) {
//					State state = State.toState("TEMPRARY");
//					// 상태값 변경해준다 임시저장 <<< StateRB 추가..
//					LifeCycleHelper.service.setLifeCycleState(eo, state);
//				} else {
//					WorkDataHelper.service.create(eo);
//				}
//			}

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

	/**
	 * 첨부 파일 삭제
	 */
	private void removeAttach(EChangeOrder eo) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(eo, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(eo, item);
		}
	}

	/**
	 * 베이스 라인 저장 함수
	 */
	@Override
	public void saveBaseline(EChangeOrder eo, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster) link.getCompletePart();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
			list.add(part);
		}

		for (WTPart part : list) {
			String state = part.getLifeCycleState().toString();
			String v = part.getVersionIdentifier().getSeries().getValue();
			boolean isApproved = state.equals("APPROVED");
			boolean isFirst = v.equals("A");

			// 승인된게 아니면서
//			if (!isApproved) {
//				// A버전이 아닌거?
//				if (!isFirst) {
//					System.out.println("number = " + part.getNumber() + ", version = "
//							+ part.getVersionIdentifier().getSeries().getValue() + "."
//							+ part.getIterationIdentifier().getSeries().getValue());
//
//					ObjectReference orf = (ObjectReference) VersionControlHelper.getPredecessor(part);
//					if (orf != null) {
//						WTPart prev = (WTPart) orf.getObject();
//						if (prev != null) {
//							System.out.println("prev = " + prev.getVersionIdentifier().getSeries().getValue() + "."
//									+ prev.getIterationIdentifier().getSeries().getValue());
//						}
//					}
//					part = (WTPart) VersionControlHelper.service.predecessorOf(part);
//					if (part != null) {
//						System.out.println("part = " + part.getVersionIdentifier().getSeries().getValue() + "."
//								+ part.getIterationIdentifier().getSeries().getValue());
//					}
//				}
//			}

			boolean isBaseline = true;
			String oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
			List<Map<String, String>> baseLine = EChangeUtils.manager.getBaseline(oid);
			for (Map<String, String> map : baseLine) {
				String baseLine_name = map.get("baseLine_name");
				if (baseLine_name.equals(eo.getEoNumber())) {
					isBaseline = false;
					break;
				}
			}
			if (isBaseline) {
				saveBaseLine(part, eo);
			}
		}
	}

	/**
	 * EO 베이스 라인 저장
	 */
	@Override
	public void saveBaseLine(WTPart part, EChangeOrder eo) throws Exception {
		String name = eo.getEoNumber() + ":" + part.getNumber();
		String location = "/Default/설계변경/Baseline";
		Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		LifeCycleTemplate lct = (LifeCycleTemplate) part.getLifeCycleTemplate().getObject();
		Vector v = new Vector();
		// 베이스 라인 대상 부품 수집
		EChangeUtils.manager.collectBaseLineParts(part, v);
		ManagedBaseline managedbaseline = ManagedBaseline.newManagedBaseline();
		managedbaseline.setName(name);
		managedbaseline.setDescription(eo.getEoNumber() + "관련 베이스 라인 작성");
		managedbaseline = (ManagedBaseline) LifeCycleHelper.setLifeCycle(managedbaseline, lct);
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setAdministrator();
		FolderHelper.assignLocation((FolderEntry) managedbaseline, folder);
		managedbaseline = (ManagedBaseline) PersistenceHelper.manager.save(managedbaseline);
		managedbaseline = (ManagedBaseline) BaselineHelper.service.addToBaseline(v, managedbaseline);
		SessionHelper.manager.setPrincipal(user.getName());
	}

	@Override
	public void eoPartApproved(EChangeOrder eo, ArrayList<WTPart> list) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			State approved = State.toState("APPROVED");
			for (WTPart part : list) {
				// 부품 승인
				LifeCycleHelper.service.setLifeCycleState(part, approved);
				String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
				IBAUtils.appendIBA(part, "ECONO", eo.getEoNumber(), "s");
				IBAUtils.appendIBA(part, "ECODATE", today, "s");
				// 3D 승인
				EPMDocument epm = PartHelper.manager.getEPMDocument(part);
				if (epm != null) {
					LifeCycleHelper.service.setLifeCycleState(epm, approved);
					IBAUtils.appendIBA(epm, "ECONO", eo.getEoNumber(), "s");
					IBAUtils.appendIBA(epm, "ECODATE", today, "s");
					// 2D 승인
					EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
					if (epm2D != null) {
						LifeCycleHelper.service.setLifeCycleState(epm2D, approved);
						IBAUtils.appendIBA(epm2D, "ECONO", eo.getEoNumber(), "s");
						IBAUtils.appendIBA(epm2D, "ECODATE", today, "s");
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