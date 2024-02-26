package com.e3ps.change.eco.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.service.WorkDataHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

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
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.ManagedBaseline;

public class StandardEcoService extends StandardManager implements EcoService {

	public static StandardEcoService newStandardEcoService() throws WTException {
		StandardEcoService instance = new StandardEcoService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcoDTO dto) throws Exception {
		String name = dto.getName();
		String sendType = dto.getSendType();
		String riskType = dto.getRiskType();
		String licensing = dto.getLicensing();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		String eoCommentD = dto.getEoCommentD();
		ArrayList<Map<String, String>> rows200 = dto.getRows200(); // 활동
//		ArrayList<Map<String, String>> rows103 = dto.getRows103(); // ECPR
//		ArrayList<Map<String, String>> rows500 = dto.getRows500(); // 변경대상 품목
		boolean temprary = dto.isTemprary();

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 21.12.30_shjeong 기존 YYMM 으로 사용 시 12월 마지막주에는 다음 년도로 표기되는 오류로 인해 수정.
//			Date currentDate = new Date();
//			String number = "C" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
//			String seqNo = SequenceDao.manager.getSeqNo(number, "00", "EChangeOrder", EChangeOrder.EO_NUMBER);
//
//			number = number + "N" + seqNo;

			String number = "C" + DateUtil.getCurrentDateString("ym") + "N";
//			String seqNo = SequenceDao.manager.getSeqNo(number, "00", "EChangeOrder", EChangeOrder.EO_NUMBER);
//			number = number + "N" + seqNo;

			number = EcoHelper.manager.getNextNumber(number);

			EChangeOrder eco = EChangeOrder.newEChangeOrder();
			eco.setSendType(sendType);
			eco.setEoName(name);
			eco.setEoNumber(number);
			eco.setEoType("CHANGE");
//			eco.setModel((String) dataMap.get("model"));
			eco.setLicensingChange(licensing);
			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);
			eco.setEoCommentD(eoCommentD);
			eco.setRiskType(riskType);

			String location = "/Default/설계변경/ECO";
			String lifecycle = "LC_ECO";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) eco, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(eco,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			eco = (EChangeOrder) PersistenceHelper.manager.save(eco);

			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(eco, state);
			}

			// 첨부 파일 저장
			saveAttach(eco, dto);

			// 관련 CR 및 ECPR
			saveLink(eco, dto);

			// 설변 활동 생성
			// 활동이 잇을 경우 상태값 대기모드로 변경한다.
			// ECPR 진행을 안할경우에만 바로 진행??
			if (rows200.size() > 0) {
				ActivityHelper.service.saveActivity(eco, rows200);
				WorkspaceHelper.service.stand(eco);
				// ECA 활동으로 변경
				eco = (EChangeOrder) PersistenceHelper.manager.refresh(eco);
				LifeCycleHelper.service.setLifeCycleState(eco, State.toState("ACTIVITY"));
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
	 * ECO 변경대상 품목 링크 생성
	 */
	private void saveEcoPart(EChangeOrder eco, ArrayList<WTPart> clist) throws Exception {
		// 그룹핑필요함...
		for (WTPart part : clist) {
			// EO진행 여부 체크
//			boolean _isSelectBom = false;
//			// if(vecBom.contains(part.getNumber())) _isSelectBom = true;
//			_isSelectBom = true;
//			boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
//			if (!isSelect) {
//				throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
//			}
			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
			link.setVersion(version);
			link.setBaseline(true);
			PersistenceServerHelper.manager.insert(link);
		}

	}

	/**
	 * 완제품 연결 - ECO에서만 사용
	 */
	private void saveCompletePart(EChangeOrder eco, ArrayList<WTPart> plist) throws Exception {
		for (WTPart part : plist) {
			// ECO 이면서 A 면서 작업중인 것은 제외 한다.
			if (EcoHelper.manager.isSkip(part)) {
				continue;
			} else {
				EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
						eco);
				link.setVersion(part.getVersionIdentifier().getSeries().getValue());
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 관련 객체 연결
	 */
	private void saveLink(EChangeOrder eco, EcoDTO dto) throws Exception {
		// ECPR 구분 있어야 할듯??
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(oid);
				RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, ecr);
				link.setEcoType(eco.getEoType());
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows103 = dto.getRows103(); // 관련 ECPR
		for (Map<String, String> row103 : rows103) {
			String gridState = row103.get("gridState");
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row103.get("oid");
				ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(oid);
				EcoToEcprLink link = EcoToEcprLink.newEcoToEcprLink(eco, ecpr);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * ECO 첨부파일
	 */
	private void saveAttach(EChangeOrder eco, EcoDTO dto) throws Exception {
//		String primary = dto.getPrimary();
//		if (StringUtil.checkString(primary)) {
//			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
//			ApplicationData applicationData = ApplicationData.newApplicationData(eco);
//			applicationData.setRole(ContentRoleType.toContentRoleType("ECO"));
//			PersistenceHelper.manager.save(applicationData);
//			ContentServerHelper.service.updateContent(eco, applicationData, vault.getPath());
//		}

		ArrayList<String> secondarys = dto.getSecondarys();
		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(eco);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(eco, applicationData, vault.getPath());
		}
	}

	@Override
	public void modify(EcoDTO dto) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		String name = dto.getName();
		String sendType = dto.getSendType();
		String riskType = dto.getRiskType();
		String licensing = dto.getLicensing();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		String eoCommentD = dto.getEoCommentD();
		ArrayList<Map<String, String>> rows200 = dto.getRows200(); // 활동
//		ArrayList<Map<String, String>> rows500 = dto.getRows500(); // 변경대상 품목
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
		boolean temprary = dto.isTemprary();

		Transaction trs = new Transaction();
		try {
			trs.start();

//			Map<String, Object> dataMap = EcoHelper.manager.dataMap(rows500);

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

			EChangeOrder eco = (EChangeOrder) rf.getReference(dto.getOid()).getObject();
			eco.setEoName(name);
			// 설별 활동에서 처리
//			eco.setModel((String) dataMap.get("model"));
			eco.setModel(model);
			eco.setLicensingChange(licensing);
			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);
			eco.setEoCommentD(eoCommentD);
			eco.setRiskType(riskType);
			eco.setSendType(sendType);

			eco = (EChangeOrder) PersistenceHelper.manager.modify(eco);

			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(eco, state);
			}

			// 첨부 파일 저장
			removeAttach(eco);
			saveAttach(eco, dto);

			// 관련 CR 및 ECPR
			deleteLink(eco);
			saveLink(eco, dto);

			// 설변 활동 생성
			if (rows200.size() > 0) {
//				ActivityHelper.service.deleteActivity(eco);
				ActivityHelper.service.saveActivity(eco, rows200);
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
	private void removeAttach(EChangeOrder eco) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(eco, ContentRoleType.toContentRoleType("ECO"));
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(eco, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(eco, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(eco, item);
		}
	}

	/**
	 * 관련 CR 및 ECPR 삭제
	 */
	private void deleteLink(EChangeOrder eco) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestOrderLink.class, true);
		SearchCondition sc = new SearchCondition(RequestOrderLink.class, "roleAObjectRef.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestOrderLink link = (RequestOrderLink) obj[0];
			PersistenceHelper.manager.delete(link);
		}

		QueryResult rs = PersistenceHelper.manager.navigate(eco, "ecpr", EcoToEcprLink.class, false);
		while (rs.hasMoreElements()) {
			EcoToEcprLink link = (EcoToEcprLink) rs.nextElement();
			PersistenceHelper.manager.delete(link);
		}

	}

	/**
	 * 완제품 삭제
	 */
	private void deleteCompletPart(EChangeOrder eco) throws Exception {

		// 완제품 삭제
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EOCompletePartLink.class, true);
		SearchCondition sc = new SearchCondition(EOCompletePartLink.class, "roleBObjectRef.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EOCompletePartLink link = (EOCompletePartLink) obj[0];
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * 변경 대상 품목 링크 삭제
	 */
	private void deleteEcoPart(EChangeOrder eco) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EcoPartLink.class, true);
		SearchCondition sc = new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcoPartLink link = (EcoPartLink) obj[0];
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * ECO 삭제
	 */
	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

			// 설변활동 삭제
			ArrayList<EChangeActivity> list = ActivityHelper.manager.getActivity(eco);
			for (EChangeActivity eca : list) {
				PersistenceHelper.manager.delete(eca);
			}

			// 관련 링크 삭제
			deleteLink(eco);
			// 결재선 지정 삭제
			WorkData dd = WorkDataHelper.manager.getWorkData(eco);
			if (dd != null) {
				PersistenceHelper.manager.delete(dd);
			}
			// 외부 메일 삭제
			MailUserHelper.service.deleteLink(oid);
			// 모든 결재선 삭제
			WorkspaceHelper.service.deleteAllLines(eco);
			// 데이터 삭제
			PersistenceHelper.manager.delete(eco);

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
	public void ecoPartApproved(EChangeOrder eco) throws Exception {

		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			State approved = State.toState("APPROVED");
			QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
			while (qr.hasMoreElements()) {
				EcoPartLink link = (EcoPartLink) qr.nextElement();
				boolean preOrder = link.getPreOrder();
				WTPartMaster master = link.getPart();
				String version = link.getVersion();
				WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
				boolean isApproved = target.getLifeCycleState().toString().equals("APPROVED");
				boolean isFour = target.getName().startsWith("4"); // 4번 품목..
				boolean isPast = link.getPast();

				// 신규 데이터
				WTPart part = null;
				if (!isPast) {
					// 개정 케이스 - 이전품목을 가여와야한다.
					// 변경 후 품목이냐 변경 대상 푼목이냐
					boolean isLeft = link.getLeftPart();
					boolean isRight = link.getRightPart();
//					if (isApproved) {

					// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
					if (isLeft) {
						// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
						WTPart next_part = (WTPart) EChangeUtils.manager.getNext(target);
						part = next_part;
					} else if (isRight) {
						// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
						part = target;
					}

					if (part != null) {

						// 부품 승인
						LifeCycleHelper.service.setLifeCycleState(part, approved);
						// 3D 승인
						EPMDocument epm = PartHelper.manager.getEPMDocument(part);
						if (epm != null) {
							LifeCycleHelper.service.setLifeCycleState(epm, approved);
							// 2D 승인
							EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
							if (epm2D != null) {
								LifeCycleHelper.service.setLifeCycleState(epm2D, approved);
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void save(HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap) throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (LinkedHashMap<String, Object> editRow : editRows) {
				String link_oid = (String) editRow.get("link_oid");
				String delivery = (String) editRow.get("delivery");
				String complete = (String) editRow.get("complete");
				String inner = (String) editRow.get("inner");
				String order = (String) editRow.get("order");
				String partStateCode = (String) editRow.get("part_state_code");

				Object object = editRow.get("weight");
				double value = 0D;
				if (object != null) {
					if (object instanceof Double) {
						value = (double) editRow.get("weight");
					} else if (object instanceof Integer) {
						int weight = (int) editRow.get("weight");
						value = (double) weight;
					}
				}

				EcoPartLink eLink = (EcoPartLink) CommonUtil.getObject(link_oid);
				eLink.setPartStateCode(partStateCode);
				eLink.setDelivery(delivery);
				eLink.setComplete(complete);
				eLink.setInner(inner);
				eLink.setOrders(order);
				eLink.setWeight(value);

				PersistenceHelper.manager.modify(eLink);
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
	public void save90(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
			// 기존꺼 삭제
//			QueryResult qr = PersistenceHelper.manager.navigate(eco, "document", DocumentECOLink.class, false);
//			while (qr.hasMoreElements()) {
//				DocumentECOLink l = (DocumentECOLink) qr.nextElement();
//				PersistenceHelper.manager.delete(l);
//			}

			for (Map<String, Object> m : removeRows) {
				String s = (String) m.get("loid");
				DocumentECOLink link = (DocumentECOLink) CommonUtil.getObject(s);
				PersistenceHelper.manager.delete(link);
			}

			for (Map<String, Object> m : addRows) {
				String s = (String) m.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(s);
				DocumentECOLink link = DocumentECOLink.newDocumentECOLink(doc, eco);
				PersistenceHelper.manager.save(link);
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
	public void removeLink(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			DocumentActivityLink link = (DocumentActivityLink) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(link);

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
	public void deleteLink(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			EOCompletePartLink link = (EOCompletePartLink) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(link);

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
	public void sync(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
			String model = "";
			ArrayList<WTPart> list = new ArrayList<WTPart>(); // 품목 리스트 담기..
			while (qr.hasMoreElements()) {
				EcoPartLink link = (EcoPartLink) qr.nextElement();
				WTPartMaster m = link.getPart();
				String v = link.getVersion();
				WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

				// 대상 품목의 BOM 전개 ..
				EcoHelper.manager.reverseStructure(part, list);
			}

			int i = 0;
			for (WTPart pp : list) {

				if (PartHelper.isCollectNumber(pp.getNumber())) {
					continue;
				}

				if (!PartHelper.isTopNumber(pp.getNumber())) {
					continue;
				}
				if (i == 0) {
					model += IBAUtil.getStringValue(pp, "MODEL");
				} else {
					String nn = IBAUtil.getStringValue(pp, "MODEL");
					if (model.indexOf(nn) <= -1) {
						model += nn + ",";
					}
				}
				i++;
			}
			eco.setModel(model);
			PersistenceHelper.manager.modify(eco);

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
