package com.e3ps.change.eo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.part.column.PartColumn;
import com.e3ps.part.service.PartHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

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
		Transaction trs = new Transaction();
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
			validateAndCompleteSave(eo, rows104);

			// 첨부 파일
			saveAttach(eo, dto);

			// 설변 활동 생성
			ActivityHelper.service.saveActivity(eo, rows200);

			// 활동 생성
//	    	boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// eco 상태 설정
			/*
			 * if(isActivity){
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("ACTIVITY")); }else{
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("APPROVE_REQUEST")); }
			 */

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
		SearchCondition sc = new SearchCondition(DocumentEOLink.class, "roleBObjectRef.key.id", "=", eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DocumentEOLink link = (DocumentEOLink) obj[0];
			PersistenceHelper.manager.delete(link);
		}
		
	}
	
	private void validateAndCompleteSave(EChangeOrder eo, ArrayList<Map<String, String>> rows104) throws Exception {
		// 완제품 연결
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (Map<String, String> map : rows104) {
			String oid = map.get("part_oid");
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			Map<String, Object> m = EoHelper.manager.validatePart(part);
			if (!(boolean) m.get("result")) {
				throw new Exception((String) m.get("msg"));
			}

			Map<String, Object> c = EoHelper.manager.checkerCompletePart(part);
			if (!(boolean) c.get("result")) {
				throw new Exception((String) c.get("msg"));
			}
			list.add(part);
		}
		completeSave(eo, list);
	}

	private void deleteCompleteSave(EChangeOrder eo) throws Exception {
		// 완제품 삭제
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EOCompletePartLink.class, true);
		SearchCondition sc = new SearchCondition(EOCompletePartLink.class, "roleBObjectRef.key.id", "=", eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EOCompletePartLink link = (EOCompletePartLink) obj[0];
			PersistenceHelper.manager.delete(link);
		}
		
	}
	
	
	private void completeSave(EChangeOrder eo, ArrayList<WTPart> list) throws Exception {
		for (WTPart part : list) {
			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			String state = part.getState().toString();
			// ECO 이면서 A 면서 작업중인 것은 제외 한다.
			// 완제품 조건??
			// 아래는 설계변경일 경우에만.. EO일경우 아님
//			if ("CHANGE".equals(eo.getEoType())) {
//				if (version.equals("A") && "INWORK".equals(state)) {
//					continue;
//				} else {
//					EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
//							eo);
//					link.setVersion(version);
//					PersistenceHelper.manager.save(link);
//				}
//			} else {
			
			EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(), eo);
			link.setVersion(version);
			PersistenceHelper.manager.save(link);
//			}
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
		
		ArrayList<Map<String, String>> rows104 = dto.getRows104();
		ArrayList<Map<String, String>> rows200 = dto.getRows200();
		ArrayList<Map<String, String>> rows300 = dto.getRows300();
//		String model_oid = dto.getModel_oid();
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

			// 관련 링크들
			deleteLink(eo);
			saveLink(eo, dto);

//			 완제품 링크 및 검증
			deleteCompleteSave(eo);
			validateAndCompleteSave(eo, rows104);
			
			// 첨부 파일
			removeAttach(eo);
			saveAttach(eo, dto);

			// 설변 활동 생성
//			ActivityHelper.service.saveActivity(eo, rows200);
			
			
			ActivityHelper.service.deleteActivity(eo);
			ActivityHelper.service.saveActivity(eo, rows200);

			// 활동 생성
//	    	boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// eco 상태 설정
			/*
			 * if(isActivity){
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("ACTIVITY")); }else{
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("APPROVE_REQUEST")); }
			 */

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
	
}
