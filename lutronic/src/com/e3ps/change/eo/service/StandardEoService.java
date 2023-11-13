package com.e3ps.change.eo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.service.SAPHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
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
import wt.vc.VersionControlHelper;
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
//		String model_oid = dto.getModel_oid();
		// 결재
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		Transaction trs = new Transaction();
		// 외부 메일
		ArrayList<Map<String, String>> external = dto.getExternal();
		try {
			trs.start();

			String type = "E";
			if (dto.getEoType().equals("DEV")) {
				type = "D";
			}
			String number = type + DateUtil.getCurrentDateString("ym");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

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

			// 외부 메일 링크 저장
			MailUserHelper.service.saveLink(eo, external);

			// 결재 생성후
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(eo, agreeRows, approvalRows, receiveRows);
			}

			// 활동이 잇을 경우 상태값 대기모드로 변경한다.
			if (rows200.size() > 0) {
				WorkspaceHelper.service.stand(eo);
				// ECA 활동으로 변경
				eo = (EChangeOrder) PersistenceHelper.manager.refresh(eo);
				LifeCycleHelper.service.setLifeCycleState(eo, State.toState("ACTIVITY"));
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
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DocumentEOLink.class, true);
		SearchCondition sc = new SearchCondition(DocumentEOLink.class, "roleBObjectRef.key.id", "=",
				eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DocumentEOLink link = (DocumentEOLink) obj[0];
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
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EOCompletePartLink.class, true);
		SearchCondition sc = new SearchCondition(EOCompletePartLink.class, "roleBObjectRef.key.id", "=",
				eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EOCompletePartLink link = (EOCompletePartLink) obj[0];
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
		ReferenceFactory rf = new ReferenceFactory();

		boolean temprary = dto.isTemprary();
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 제품
		ArrayList<Map<String, String>> rows104 = dto.getRows104(); // 완제품
		ArrayList<Map<String, String>> rows200 = dto.getRows200(); // ECA
		ArrayList<Map<String, String>> external = dto.getExternal(); // 외부메일
		
		// 결재
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
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

			EChangeOrder eo = (EChangeOrder) rf.getReference(dto.getOid()).getObject();
			eo.setEoName(dto.getName());
			eo.setModel(model);
			eo.setEoType(dto.getEoType());
			eo.setEoCommentA(dto.getEoCommentA());
			eo.setEoCommentB(dto.getEoCommentB());
			eo.setEoCommentC(dto.getEoCommentC());
			
			eo = (EChangeOrder) PersistenceHelper.manager.modify(eo);
			
			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(eo, state);
			}else {
				State state = State.toState("INWORK");
 				LifeCycleHelper.service.setLifeCycleState(eo, state);
			}
			
			// 관련 링크들
			deleteLink(eo);
			saveLink(eo, dto);

//			 완제품 링크 및 검증
			deleteCompletePart(eo); // 지우고 새로 만드는거 맞음??
			validateAndSaveCompletePart(eo, rows104);

			// 첨부 파일
			removeAttach(eo);
			saveAttach(eo, dto);
			
			// 외부 메일 링크 삭제
			MailUserHelper.service.deleteLink(dto.getOid());
			// 외부 메일 링크 추가
			MailUserHelper.service.saveLink(eo, external);
			
			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(eo, agreeRows, approvalRows, receiveRows);
			}

			if(rows200.size()>0){
				// 설변활동 어떻게 처리되는지...
				ActivityHelper.service.deleteActivity(eo);
				ActivityHelper.service.saveActivity(eo, rows200);
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
}