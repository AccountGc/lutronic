package com.e3ps.change.activity.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.PartGroupLink;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class StandardActivityService extends StandardManager implements ActivityService {

	public static StandardActivityService newStandardActivityService() throws WTException {
		StandardActivityService instance = new StandardActivityService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> delete(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = (String) params.get("oid");
			boolean dependency = ActivityHelper.manager.dependency(oid);
			if (dependency) {
				map.put("success", false);
				map.put("msg", "삭제 하려는 루트 활동에 설계변경 활동이 존재합니다.");
				return map;
			}

			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(def);

			map.put("success", true);

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
		return map;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String type = (String) params.get("type");
		Transaction trs = new Transaction();
		try {
			trs.start();

			if ("root".equals(type)) {
				String name = (String) params.get("name");
				String sort = (String) params.get("sort");
				String description = (String) params.get("description");

				EChangeActivityDefinitionRoot def = EChangeActivityDefinitionRoot.newEChangeActivityDefinitionRoot();
				def.setName(name);
				def.setSortNumber(Integer.parseInt(sort));
				def.setDescription(description);
				PersistenceHelper.manager.save(def);

			} else if ("act".equals(type)) {

				String oid = (String) params.get("oid");
				String name = (String) params.get("name");
				String step = (String) params.get("step");
				String activeType = (String) params.get("activeType");
				String sort = (String) params.get("sort");
				String description = (String) params.get("description");

				EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
				EChangeActivityDefinition act = EChangeActivityDefinition.newEChangeActivityDefinition();
				act.setName(name);
				act.setStep(step);
				act.setActiveType(activeType);
				act.setDescription(description);
				act.setSortNumber(Integer.parseInt(sort));
				act.setRoot(def);
				PersistenceHelper.manager.save(act);
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
	public void save(HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap) throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = dataMap.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (LinkedHashMap<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				String step = (String) editRow.get("step");
				String name = (String) editRow.get("name");
				String activity_type = (String) editRow.get("activity_type"); // code..
				int sort = (int) editRow.get("sort");
				String activeUser_oid = (String) editRow.get("activeUser_oid"); // wtuser
				EChangeActivityDefinition act = (EChangeActivityDefinition) CommonUtil.getObject(oid);
				act.setName(name);
				act.setActiveUser((WTUser) CommonUtil.getObject(activeUser_oid));
				act.setSortNumber(sort);
				act.setStep(step);
				act.setActiveType(activity_type);
				PersistenceHelper.manager.modify(act);
			}

			for (LinkedHashMap<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				EChangeActivityDefinition act = (EChangeActivityDefinition) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(act);
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
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String name = (String) params.get("name");
		String sort = (String) params.get("sort");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();
			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
			def.setName(name);
			def.setSortNumber(Integer.parseInt(sort));
			def.setDescription(description);
			PersistenceHelper.manager.modify(def);

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
	public void saveActivity(EChangeOrder eo, ArrayList<Map<String, String>> list) throws Exception {

		int sort = 0;
		for (Map<String, String> map : list) {
			String step_name = map.get("step_name");
			String name = map.get("name");
			String activity_type = map.get("activity_type");
			String activeUser_oid = map.get("activeUser_oid");
			String finishDate = map.get("finishDate");

			WTUser user = (WTUser) CommonUtil.getObject(activeUser_oid);

			EChangeActivity eca = EChangeActivity.newEChangeActivity();
			eca.setStep(step_name);
//			eca.setName(name);
			eca.setActiveType(activity_type);
			eca.setActiveUser(user);
			eca.setFinishDate(DateUtil.convertDate(finishDate));
			eca.setSortNumber(sort);
			eca.setEo(eo);

			String location = "/Default/설계변경/ECA";
			String lifecycle = "LC_ECA_PROCESS";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) eca, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(eca,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			eca = (EChangeActivity) PersistenceHelper.manager.save(eca);
			sort++;

			// STEP1 코드
			if (eca.getStep().equals("ES001")) {
				State state = State.toState("INWORK");
				// STEP01 단계부터 무조건 시작
				LifeCycleHelper.service.setLifeCycleState(eca, state);
			}
		}
	}

	@Override
	public void deleteActivity(EChangeOrder eo) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeActivity.class, true);
		SearchCondition sc = new SearchCondition(EChangeActivity.class, "eoReference.key.id", "=",
				eo.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eca = (EChangeActivity) obj[0];
			PersistenceHelper.manager.delete(eca);
		}
	}

	@Override
	public void saveLink(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
			for (String s : list) {
				WTDocument doc = (WTDocument) CommonUtil.getObject(s);
				DocumentActivityLink link = DocumentActivityLink
						.newDocumentActivityLink((WTDocumentMaster) doc.getMaster(), eca);
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
	public void deleteLink(String oid) throws Exception {
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
	public void complete(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid"); // eca oid
		String description = (String) params.get("description"); // 완료 의견
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
//		String ecnUserOid = (String) params.get("ecnUserOid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
			eca.setDescription(description);
			PersistenceHelper.manager.modify(eca);

			EChangeOrder eco = (EChangeOrder) eca.getEo();

//			if (StringUtil.checkString(ecnUserOid)) {
//				WTUser ecnUser = (WTUser) CommonUtil.getObject(ecnUserOid);
//				EChangeOrder eco = (EChangeOrder) eca.getEo();
//				// ecn 담당자 설정
//				eco.setEcnUser(ecnUser);
//				eco = (EChangeOrder) PersistenceHelper.manager.modify(eco);
//			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(eca);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(eca, applicationData, vault.getPath());
			}

			// 승인됨으로 변경한다.
			LifeCycleHelper.service.setLifeCycleState(eca, State.toState("COMPLETED"));

			String step = eca.getStep();

			// 같은 STEP 단계확인을 하여 해당 스텝이 마지막인지 확인
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EChangeActivity.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeActivity.class, "eoReference.key.id", eca.getEo());
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeActivity.class, EChangeActivity.STEP, step);
			QueryResult qr = PersistenceHelper.manager.find(query);
			boolean isLast = true;
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				EChangeActivity ee = (EChangeActivity) obj[0];
				String state = ee.getLifeCycleState().toString();
				if ("INTAKE".equals(state)) {
					isLast = false; // 접수가 있으면 마지막이 아니다.
					break;
				}
			}
			// ES001 ES002 ES003 ES004
			// 스텝 코드 순서
			int last = Integer.parseInt(step.substring(4));
			String nextStep = step.substring(0, 4) + (last + 1); // ES001 -> ES002
			// 마지막일경우 다음 스텝의 ECA 활동을 작업중으로 변경처리한다.
			if (isLast) {
				nextValidate(eca.getEo(), nextStep);
			}

			// 모든 ECA가 끝났을 경우 EO, ECO상태값을변경한다.
			boolean isEnd = isEnd(eca.getEo());
			if (isEnd) {
				LifeCycleHelper.service.setLifeCycleState(eca.getEo(), State.toState("APPROVING"));
				// 결재선들 시작 한다
				WorkspaceHelper.service.start(eca.getEo());
			}

			// ECO 일경우 처리

			afterActivityAction(eco);

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
	 * 설변 활동 완료후 ECO 작업
	 */
	private void afterActivityAction(EChangeOrder eco) throws Exception {
		String eoType = eco.getEoType();

		if ("CHANGE".equals(eoType)) {
			// 완제품 링크...
			ArrayList<WTPart> list = new ArrayList<WTPart>(); // 품목 리스트 담기..
			QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
			String model = "";
			while (qr.hasMoreElements()) {
				EcoPartLink link = (EcoPartLink) qr.nextElement();
				WTPartMaster m = link.getPart();
				String v = link.getVersion();
				WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

				// 대상 품목의 BOM 전개 ..
				reverseStructure(part, list);
			}

			// 완제품만 넣을것..
			ArrayList<WTPart> clist = new ArrayList<WTPart>();
			ArrayList<String> mlist = new ArrayList<String>();
			System.out.println("list=" + list.size());
			for (WTPart p : list) {
				String number = p.getNumber();
				// 완제품
				String firstNumber = number.substring(0, 1);
				String endNumber = number.substring(5, 8);// number.substring(5,number.length());
				if (firstNumber.equals("1") && !endNumber.endsWith("000")) { // 6,7,8이 000인경우
					if (!clist.contains(p)) {
						clist.add(p);
					}
				}
				putModel(model, p, mlist);
			}

			for (WTPart pp : clist) {
				System.out.println("제품 = " + pp.getNumber());
				EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) pp.getMaster(), eco);
				link.setVersion(pp.getVersionIdentifier().getSeries().getValue());
				PersistenceServerHelper.manager.insert(link);
			}
			eco.setModel(model);
			PersistenceHelper.manager.modify(eco);
		}
	}

	/**
	 * 제품명 담기
	 */
	private String putModel(String model, WTPart part, ArrayList<String> mlist) throws Exception {
		String m = IBAUtil.getAttrValue(part, "MODEL");
		if (!mlist.contains(m)) {
			model = model + "," + m;
			mlist.add(m);
		}
		return model;
	}

	/**
	 * ECO 시 역전개하여 품목 가져오기
	 */
	public void reverseStructure(WTPart end, ArrayList<WTPart> list) throws Exception {
		if (!list.contains(end)) {
			list.add(end);
		}
		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();

		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);

		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		View view = ViewHelper.service.getView(end.getViewName());
		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		String state = end.getLifeCycleState().toString();
		if (state != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);

		query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
				new int[] { idx_part });

		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			if (!list.contains(p)) {
				list.add(p);
			}
			reverseStructure(p, list);
		}
	}

	/**
	 * 설변 활동 다음 스텝 체크
	 */
	private void nextValidate(ECOChange eo, String nextStep) throws Exception {
		// 5번 스텝으로 되면 끝을낸다.
		if (nextStep.equals("ES005")) {
			return;
		}

		// ES002 가 nextStep 으로 들어옴..
		QuerySpec qs = new QuerySpec(EChangeActivity.class);
		QuerySpecUtils.toEqualsAnd(qs, 0, EChangeActivity.class, "eoReference.key.id", eo);
		QuerySpecUtils.toEqualsAnd(qs, 0, EChangeActivity.class, EChangeActivity.STEP, nextStep);
		QueryResult result = PersistenceHelper.manager.find(qs);

		// 해당사항이 없다면 다음 스텝 체크..
		if (result.size() == 0) {
			int last = Integer.parseInt(nextStep.substring(4));
			String toNext = nextStep.substring(0, 4) + (last + 1); // ES001 -> ES002
			nextValidate(eo, toNext);
		}

		while (result.hasMoreElements()) {
			EChangeActivity next = (EChangeActivity) result.nextElement();
			// 작업중으로 전부 변경한다 다음 스텝
			LifeCycleHelper.service.setLifeCycleState(next, State.toState("INWORK"));
		}
	}

	/**
	 * 해당 EO에 관련된 ECA가 모두 끝낫는지 확인 후 승인중 상태로 변경한다.
	 */
	private boolean isEnd(ECOChange eo) throws Exception {
		boolean isEnd = true;

		QuerySpec qs = new QuerySpec(EChangeActivity.class);
		QuerySpecUtils.toEqualsAnd(qs, 0, EChangeActivity.class, "eoReference.key.id", eo);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			EChangeActivity eca = (EChangeActivity) qr.nextElement();
			String state = eca.getLifeCycleState().toString();
			// 승인됨이 아닌게 있을 경우 마지막이 아니다
			if (!"COMPLETED".equals(state)) {
				isEnd = false;
				break;
			}
		}
		return isEnd;
	}

	@Override
	public void revise(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) params.get("data");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
			EChangeOrder eco = (EChangeOrder) eca.getEo();
			String message = "[" + eco.getEoNumber() + "]를 통해서 수정되었습니다.";

			for (Map<String, Object> map : list) {
				String part_oid = (String) map.get("part_oid");
				String link_oid = (String) map.get("link_oid");

				EcoPartLink link = (EcoPartLink) CommonUtil.getObject(link_oid);

				WTPart part = (WTPart) CommonUtil.getObject(part_oid);
				EPMDocument epm = PartHelper.manager.getEPMDocument(part);

				// 개정
				WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part);
				VersionControlHelper.setNote(part, message);
				newPart = (WTPart) PersistenceHelper.manager.save(newPart);
				newPart = (WTPart) PersistenceHelper.manager.refresh(newPart);

				// 라이프사이클 재지정

				LifeCycleTemplateReference lct = LifeCycleHelper.service.getLifeCycleTemplateReference("LC_PART",
						WCUtil.getWTContainerRef());
				LifeCycleHelper.service.reassign(newPart, lct);

				// IBA 속성처리
				removeAttr((IBAHolder) newPart, newPart.getVersionIdentifier().getSeries().getValue());
				// 기타 부품 연관 처리
				copyLink(part, newPart);

				// ROHS 물질 연결

				EPMDocument newEpm = null;

				// 개정인데?? 체크인아웃??
				if (epm != null) {
					boolean isApproved = epm.getLifeCycleState().toString().equals("APPROVED");
					if (isApproved) {
						newEpm = (EPMDocument) VersionControlHelper.service.newVersion(epm);
						VersionControlHelper.setNote(epm, message);
						PersistenceHelper.manager.save(newEpm);
						removeAttr((IBAHolder) newEpm, newEpm.getVersionIdentifier().getSeries().getValue());

						QueryResult qr = PersistenceHelper.manager.navigate(epm, "describes", EPMDescribeLink.class);
						while (qr.hasMoreElements()) {
							WTPart p = (WTPart) qr.nextElement();
							EPMDescribeLink newLink = EPMDescribeLink.newEPMDescribeLink(p, newEpm);
							PersistenceServerHelper.manager.insert(newLink);
						}
					}
				}

				// 2D 도면 개정인데 체크해봐야함
				if (newEpm != null) {

				} else {
					if (epm != null) {

					}
				}

				// 관련 도면??
				if (newPart != null) {

				}

				link.setRevise(true);
				PersistenceHelper.manager.modify(link);

				PartToPartLink pLink = PartToPartLink.newPartToPartLink(part.getMaster(), newPart.getMaster());
				pLink.setPreVersion(part.getVersionIdentifier().getSeries().getValue());
				pLink.setAfterVersion(newPart.getVersionIdentifier().getSeries().getValue());
				pLink.setEco(eco);
				PersistenceHelper.manager.save(pLink);

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
	 * 기존부품과 연관 되어 있던 데이터들 연결
	 */
	private void copyLink(WTPart part, WTPart newPart) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(newPart, doc);
			PersistenceServerHelper.manager.insert(link);
		}

		qr.reset();

	}

	/**
	 * IBA 속성 값 삭제 및 REV 속성 추가
	 */
	private void removeAttr(IBAHolder holder, String v) throws Exception {
		IBAUtil.deleteIBA(holder, "APR", "string");
		IBAUtil.deleteIBA(holder, "CHK", "string");
		IBAUtil.changeIBAValue(holder, "REV", v, "string");
	}

	@Override
	public void replace(ArrayList<LinkedHashMap<String, Object>> addRows,
			ArrayList<LinkedHashMap<String, Object>> removeRows, String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

			for (LinkedHashMap<String, Object> map : addRows) {
				String part_oid = (String) map.get("part_oid");
				WTPart part = (WTPart) CommonUtil.getObject(part_oid);
				// 새로 추가된 품번인데.. 히스토리 만들기 위해 검색한다..
				EcoPartLink link = EcoPartLink.newEcoPartLink(part.getMaster(), eco);
				link.setVersion(part.getVersionIdentifier().getSeries().getValue());
				link.setBaseline(true);
				link.setPreOrder(false);
				PersistenceHelper.manager.save(link);

				WTPart prevPart = ActivityHelper.manager.prevPart(part.getNumber());
				if (prevPart != null) {
					PartToPartLink pLink = PartToPartLink.newPartToPartLink(prevPart.getMaster(), part.getMaster());
					pLink.setPreVersion(prevPart.getVersionIdentifier().getSeries().getValue());
					pLink.setAfterVersion(part.getVersionIdentifier().getSeries().getValue());
					pLink.setEco(eco);
					PersistenceHelper.manager.save(pLink);
				}
			}

			for (LinkedHashMap<String, Object> map : removeRows) {
				String link_oid = (String) map.get("link_oid");
				EcoPartLink link = (EcoPartLink) CommonUtil.getObject(link_oid);
				PersistenceHelper.manager.delete(link);
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
	public void prev(Map<String, Object> params) throws Exception {
		String prev = (String) params.get("prev");
		String after = (String) params.get("after");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);

			WTPart prePart = (WTPart) CommonUtil.getObject(prev);
			WTPart afterPart = (WTPart) CommonUtil.getObject(after);

			PartToPartLink link = PartToPartLink.newPartToPartLink(prePart.getMaster(), afterPart.getMaster());
			link.setPreVersion(prePart.getVersionIdentifier().getSeries().getValue());
			link.setAfterVersion(afterPart.getVersionIdentifier().getSeries().getValue());
			link.setEco((EChangeOrder) eca.getEo());
			PersistenceHelper.manager.save(link);

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
	public void saveData(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = (String) params.get("oid");
			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
			EChangeOrder eco = (EChangeOrder) eca.getEo();

			for (Map<String, Object> map : editRows) {
				boolean preOrder = (boolean) map.get("preOrder");
				String next_oid = (String) map.get("next_oid");

				WTPart nextPart = (WTPart) CommonUtil.getObject(next_oid);

				String group = (String) map.get("group");
				if (StringUtil.checkString(group)) {
					String[] groups = group.split(",");
					for (String s : groups) {
						EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(s.trim());
						PartGroupLink link = PartGroupLink.newPartGroupLink(nextPart, ecr);
						link.setEco(eco);
						PersistenceHelper.manager.save(link);
					}
				}

				String delivery = (String) map.get("delivery");
				String complete = (String) map.get("complete");
				String inner = (String) map.get("inner");
				String order = (String) map.get("order");
				String partStateCode = (String) map.get("partStateCode");
				String link_oid = (String) map.get("link_oid");

				EcoPartLink eLink = (EcoPartLink) CommonUtil.getObject(link_oid);
				eLink.setPreOrder(preOrder);
				eLink.setPartStateCode(partStateCode);
				eLink.setDelivery(delivery);
				eLink.setComplete(complete);
				eLink.setInner(inner);
				eLink.setOrders(order);

				if (preOrder) {
					IBAUtil.createIba(nextPart, "boolean", "PREORDER", "true");
				}

				PersistenceHelper.manager.modify(eLink);

			}

			for (Map<String, Object> removeRow : removeRows) {
				String part_oid = (String) removeRow.get("part_oid");
				WTPart prePart = (WTPart) CommonUtil.getObject(part_oid);
				WTPartMaster master = (WTPartMaster) prePart.getMaster();
				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(PartToPartLink.class, true);
				QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "ecoReference.key.id", eco);
				QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "roleAObjectRef.key.id", master);
				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					PartToPartLink link = (PartToPartLink) obj[0];
					PersistenceHelper.manager.delete(link);
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
