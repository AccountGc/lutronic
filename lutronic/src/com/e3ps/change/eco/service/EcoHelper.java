package com.e3ps.change.eco.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.PartHelper;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;

public class EcoHelper {

	public static final EcoService service = ServiceFactory.getService(EcoService.class);
	public static final EcoHelper manager = new EcoHelper();

	/**
	 * 큐 관련 상수
	 */
	private static final String processQueueName = "SapProcessQueue";
	private static final String className = "com.e3ps.change.util.EChangeUtils";
	private static final String methodName = "afterEcoAction";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {

		Map<String, Object> map = new HashMap<>();
		ArrayList<EcoColumn> list = new ArrayList<>();

		ArrayList<Map<String, String>> rows104 = (ArrayList<Map<String, String>>) params.get("rows104");

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String eoType = (String) params.get("eoType");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String approveFrom = (String) params.get("approveFrom");
		String approveTo = (String) params.get("approveTo");

		String licensing = (String) params.get("licensing");
		String riskType = (String) params.get("riskType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);

		// 상태 임시저장 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(EChangeOrder.class, EChangeOrder.LIFE_CYCLE_STATE,
				SearchCondition.NOT_EQUAL, "TEMPRARY"), new int[] { idx });
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NUMBER, number);
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeOrder.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		if (approveFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, approveFrom), new int[] { idx });
		}
		if (approveTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, approveTo), new int[] { idx });
		}
		QuerySpecUtils.toState(query, idx, EChangeOrder.class, state);
		if (StringUtil.checkString(eoType)) {
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, eoType);
		} else {
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "CHANGE");
		}
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.LICENSING_CHANGE, licensing);
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.RISK_TYPE, riskType);

		if (rows104.size() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int idx_p = query.appendClassList(EOCompletePartLink.class, false);
			query.appendWhere(
					new SearchCondition(EChangeOrder.class, "thePersistInfo.theObjectIdentifier.id",
							EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"),
					new int[] { idx, idx_p });
			query.appendAnd();
			query.appendOpenParen();
			for (int i = 0; i < rows104.size(); i++) {
				Map<String, String> row = (Map<String, String>) rows104.get(i);
				if (i != 0) {
					query.appendOr();
				}
				String oid = row.get("part_oid");
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				long ids = part.getMaster().getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(new SearchCondition(EOCompletePartLink.class,
						EOCompletePartLink.ROLE_AOBJECT_REF + ".key.id", SearchCondition.EQUAL, ids),
						new int[] { idx_p });
			}
			query.appendCloseParen();
		}
		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, EChangeOrder.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcoColumn data = new EcoColumn(obj);
			list.add(data);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 설변관련 대상들 가져오기
	 */
	public Map<String, Object> dataMap(ArrayList<Map<String, String>> rows500) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<WTPart> clist = new ArrayList<WTPart>(); // 설변 대상 리스트
		ArrayList<WTPart> plist = new ArrayList<WTPart>(); // 완제품 리스트
		ArrayList<String> mlist = new ArrayList<String>(); // 제품 리스트
		String model = "";
		for (Map<String, String> row500 : rows500) {
			String part_oid = row500.get("part_oid");
			WTPart part = (WTPart) CommonUtil.getObject(part_oid);
			clist.add(part);

			// 제품명 담기
//			model = putModel(model, part, mlist);

			plist = PartHelper.manager.collectEndItem(part, plist);
		}

		for (int i = 0; i < plist.size(); i++) {
			WTPart pp = (WTPart) plist.get(i);

			if (isSkip(pp)) {
				// 그냥 여기서 제외 하면 되는거 아닌가?
				plist.remove(i);
				continue;
			}
//			putModel(model, pp, mlist);
		}
		map.put("model", model);
		map.put("plist", plist);
		map.put("clist", clist);
		return map;
	}

	/**
	 * 설변 대상 품목 제외 ECO 일경우 최초버전이면서 작업중인 항목은 제외한다.
	 */
	public boolean isSkip(WTPart pp) throws Exception {
		String version = pp.getVersionIdentifier().getSeries().getValue();
		String state = pp.getLifeCycleState().toString();
		if (version.equals("A") && state.equals("INWORK")) {
			return true;
		}
		return false;
	}

	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		String[] ss = model.split(",");
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			if (ss.length - 1 == i) {
				display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL");
			} else {
				display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL") + ",";
			}
		}
		return display;
	}

	/**
	 * ECO 관련 객체들
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		if ("part".equalsIgnoreCase(type)) {
			// 설계변경 품목
			return JSONArray.fromObject(referencePart(eco, list));
		} else if ("activity".equalsIgnoreCase(type)) {
			// 설계변경 활동
			return JSONArray.fromObject(referenceActivity(eco, list));
		} else if ("cr".equalsIgnoreCase(type)) {
			// 설계변경 활동
			return JSONArray.fromObject(referenceCr(eco, list));
		} else if ("complete".equals(type)) {
			return JSONArray.fromObject(referenceComplete(eco, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 완제품 목록
	 */
	private ArrayList<Map<String, Object>> referenceComplete(EChangeOrder eco, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class);
		while (result.hasMoreElements()) {
			WTPartMaster master = (WTPartMaster) result.nextElement();
			WTPart part = PartHelper.manager.getLatest(master);
			Map<String, Object> map = new HashMap<>();
			map.put("number", part.getNumber());
			map.put("name", part.getName());
			map.put("state", part.getLifeCycleState().getDisplay());
			map.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			map.put("creator", part.getCreatorFullName());
			map.put("createdDate_txt", part.getCreateTimestamp().toString().substring(0, 10));
			list.add(map);
		}
		return list;
	}

	/**
	 * ECO 설계변경 품목
	 */
	private ArrayList<Map<String, Object>> referencePart(EChangeOrder eco, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			WTPartMaster master = link.getPart();
//			WTPart part = PartHelper.manager.getLatest(master);
			WTPart part = PartHelper.manager.getPart(master.getNumber(), link.getVersion());
			Map<String, Object> map = new HashMap<>();

			boolean isPast = link.getPast();
			boolean preOrder = link.getPreOrder();
			map.put("delivery", link.getDelivery());
			map.put("complete", link.getComplete());
			map.put("inner", link.getInner());
			map.put("order", link.getOrders());
			map.put("part_state_code", link.getPartStateCode());
			map.put("preOrder", preOrder);
			map.put("weight", link.getWeight());

			// 과저 데이터 처리
			if (!isPast) {

				boolean isRight = link.getRightPart();
				boolean isLeft = link.getLeftPart();
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				boolean isFour = part.getNumber().startsWith("4"); // 4로 시작하는것은 무조건 모두 새품번
				boolean isRevise = link.isRevise();

				if (isLeft) {
					map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("part_number", part.getNumber());
					map.put("part_name", part.getName());
					map.put("part_state", part.getLifeCycleState().getDisplay());
					map.put("part_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("part_creator", part.getCreatorFullName());

					// 개정된 데이터가 없을 경우
					if (!isRevise && !isFour) {
						map.put("next_oid", "");
						map.put("next_number", "변경후 데이터가 없습니다.");
						map.put("next_name", "변경후 데이터가 없습니다.");
						map.put("next_state", "변경후 데이터가 없습니다.");
						map.put("next_version", "변경후 데이터가 없습니다.");
						map.put("next_creator", "변경후 데이터가 없습니다.");
						map.put("afterMerge", true);
					} else {
						WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
						// 개정데이터가 있을경우
						map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("next_number", next_part.getNumber());
						map.put("next_name", next_part.getName());
						map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
								+ next_part.getIterationIdentifier().getSeries().getValue());
						map.put("next_creator", next_part.getCreatorFullName());
						map.put("next_state", next_part.getLifeCycleState().getDisplay());
						map.put("afterMerge", false);
					}
				} else if (isRight) {
					WTPart pre_part = EChangeUtils.manager.getEcoPrePart(eco, part);
					// 변경후
					map.put("next_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", part.getNumber());
					map.put("next_name", part.getName());
					map.put("next_state", part.getLifeCycleState().getDisplay());
					map.put("next_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", part.getCreatorFullName());

					if (pre_part == null) {
						map.put("part_oid", "");
						map.put("part_number", "변경전 데이터가 없습니다.");
						map.put("part_name", "변경전 데이터가 없습니다.");
						map.put("part_state", "변경전 데이터가 없습니다.");
						map.put("part_version", "변경전 데이터가 없습니다.");
						map.put("part_creator", "변경전 데이터가 없습니다.");
						map.put("preMerge", true);
					} else {
						map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("part_number", pre_part.getNumber());
						map.put("part_name", pre_part.getName());
						map.put("part_state", pre_part.getLifeCycleState().getDisplay());
						map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
								+ pre_part.getIterationIdentifier().getSeries().getValue());
						map.put("part_creator", pre_part.getCreatorFullName());
						map.put("preMerge", false);
					}
				}
				list.add(map);
				// 고도화 데이터 위
			} else {

			}
		}
		return list;
	}

	/**
	 * ECO 관련 설계변경 활동
	 */
	private Object referenceActivity(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		JSONArray j = new JSONArray();
		ArrayList<EChangeActivity> colletActivityList = ActivityHelper.manager.colletActivity(eco);
		for (EChangeActivity item : colletActivityList) {
			ActDTO dto = new ActDTO(item);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}

		return list;
	}

	/**
	 * ECO CR 목록
	 */
	private Object referenceCr(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "ecr", RequestOrderLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest ecr = (EChangeRequest) result.nextElement();
			CrColumn data = new CrColumn(ecr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECO 결재 완료후 호출 되는 함수 큐 이용
	 */
	public void postAfterAction(EChangeOrder e) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", e.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * ECO와 연결된 완제품 리스트
	 */
	public ArrayList<EOCompletePartLink> completeParts(Object obj) throws Exception {
		ArrayList<EOCompletePartLink> list = new ArrayList<EOCompletePartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class, false);
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EOCompletePartLink.class, false);
		}

		while (result.hasMoreElements()) {
			EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * ECO 관련 품목 수집
	 */
	public ArrayList<EcoPartLink> ecoParts(Object obj) throws Exception {
		ArrayList<EcoPartLink> list = new ArrayList<EcoPartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class, false);
		}

		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * IBA 값 세팅 및 상태 변경
	 */
	public void setIBAAndState(ArrayList<EcoPartLink> ecoParts, ArrayList<EOCompletePartLink> completeParts)
			throws Exception {
		// 설변대상 품목
		for (EcoPartLink link : ecoParts) {
			EChangeOrder eco = link.getEco();
			WTPartMaster m = link.getPart();
			String v = link.getVersion();
			WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

			if (part != null) {
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				if (!isApproved) {
					LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
					part = (WTPart) PersistenceHelper.manager.refresh(part);
				}

				// 최종승인일..
				String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
				IBAUtils.appendIBA(part, "CHANGENO", eco.getEoNumber(), "s");
				IBAUtils.appendIBA(part, "CHANGEDATE", today, "s");

				EPMDocument epm = PartHelper.manager.getEPMDocument(part);
				if (epm != null) {
					IBAUtils.appendIBA(epm, "CHANGENO", eco.getEoNumber(), "s");
					IBAUtils.appendIBA(epm, "CHANGEDATE", today, "s");
				}
			}

			// 메카가 아닐경우에만 멀 하는데..???
//			if(!ChangeUtil.isMeca(location)){
//				IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
//				IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
//			}

			// EO,ECO시 누적으로 등록
//			String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGENO));
//			String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGEDATE));
		}

		// 완제품
		for (EOCompletePartLink link : completeParts) {
			EChangeOrder eco = link.getEco();
			WTPartMaster m = link.getCompletePart();
			String v = link.getVersion();
			WTPart part = PartHelper.manager.getPart(m.getNumber(), v);
			if (part != null) {
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				if (!isApproved) {
					LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
					part = (WTPart) PersistenceHelper.manager.refresh(part);
				}

				// 최종승인일..
				String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
				IBAUtils.appendIBA(part, "CHANGENO", eco.getEoNumber(), "s");
				IBAUtils.appendIBA(part, "CHANGEDATE", today, "s");

				EPMDocument epm = PartHelper.manager.getEPMDocument(part);
				if (epm != null) {
					IBAUtils.appendIBA(epm, "CHANGENO", eco.getEoNumber(), "s");
					IBAUtils.appendIBA(epm, "CHANGEDATE", today, "s");
				}
			}
		}
	}

	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("00");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);
		SearchCondition sc = new SearchCondition(EChangeOrder.class, EChangeOrder.EO_NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// E2312N45
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) obj[0];
			String ecoNumber = eco.getEoNumber();
			String next = ecoNumber.substring(6); // 00
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "01";
		}
		return rtn;
	}
}