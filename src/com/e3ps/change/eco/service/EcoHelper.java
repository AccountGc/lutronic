package com.e3ps.change.eco.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcnEco;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.EcrmToEcoLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.ecn.column.EcnColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.change.ecrm.column.EcrmColumn;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.service.GroupwareHelper;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.part.service.PartHelper;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTProperties;

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
		} else if ("ecpr".equals(type)) {
			return JSONArray.fromObject(referenceEcpr(eco, list));
		} else if ("ecrm".equals(type)) {
			return JSONArray.fromObject(referenceEcrm(eco, list));
		} else if ("ecn".equals(type)) {
			return JSONArray.fromObject(referenceEcn(eco, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECPR
	 */
	private Object referenceEcpr(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecpr", EcoToEcprLink.class);
		while (qr.hasMoreElements()) {
			ECPRRequest ecpr = (ECPRRequest) qr.nextElement();
			EcprColumn data = new EcprColumn(ecpr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECRM
	 */
	private Object referenceEcrm(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecrm", EcrmToEcoLink.class);
		while (qr.hasMoreElements()) {
			ECRMRequest ecrm = (ECRMRequest) qr.nextElement();
			EcrmColumn data = new EcrmColumn(ecrm);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECN
	 */
	private Object referenceEcn(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecn", EcnEco.class);
		while (qr.hasMoreElements()) {
			EChangeNotice ecn = (EChangeNotice) qr.nextElement();
			EcnColumn data = new EcnColumn(ecn);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
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
			map.put("oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
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
						if (next_part != null) {
							// 개정데이터가 있을경우
							map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
							map.put("next_number", next_part.getNumber());
							map.put("next_name", next_part.getName());
							map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
									+ next_part.getIterationIdentifier().getSeries().getValue());
							map.put("next_creator", next_part.getCreatorFullName());
							map.put("next_state", next_part.getLifeCycleState().getDisplay());
							map.put("afterMerge", false);
						} else {
							map.put("next_oid", "");
							map.put("next_number", "변경후 데이터가 없습니다.");
							map.put("next_name", "변경후 데이터가 없습니다.");
							map.put("next_state", "변경후 데이터가 없습니다.");
							map.put("next_version", "변경후 데이터가 없습니다.");
							map.put("next_creator", "변경후 데이터가 없습니다.");
							map.put("afterMerge", true);
						}
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
			} else {
				WTPart pre_part = part;
				map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("part_number", pre_part.getNumber());
				map.put("part_name", pre_part.getName());
				map.put("part_state", pre_part.getLifeCycleState().getDisplay());
				map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
						+ pre_part.getIterationIdentifier().getSeries().getValue());
				map.put("part_creator", pre_part.getCreatorFullName());
				map.put("preMerge", false);

				if (link.isRevise()) {
					WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
					if (next_part != null) {
						map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("next_number", next_part.getNumber());
						map.put("next_name", next_part.getName());
						map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
								+ next_part.getIterationIdentifier().getSeries().getValue());
						map.put("next_creator", next_part.getCreatorFullName());
						map.put("next_state", next_part.getLifeCycleState().getDisplay());
						map.put("afterMerge", false);
					} else {
						map.put("next_oid", "");
						map.put("next_number", "변경후 데이터가 없습니다.");
						map.put("next_name", "변경후 데이터가 없습니다.");
						map.put("next_state", "변경후 데이터가 없습니다.");
						map.put("next_version", "변경후 데이터가 없습니다.");
						map.put("next_creator", "변경후 데이터가 없습니다.");
						map.put("afterMerge", true);
					}
				}
				list.add(map);
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

	/**
	 * ECO 다음번호
	 */
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

	/**
	 * ECO 관련 도면 다운로드
	 */
	public void summaryData(Map<String, Object> params) throws Exception {
		String type = (String) params.get("type");

	}

	/*
	 * 설계변경 통보서
	 */
	public Map<String, Object> excel(String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		EcoDTO dto = new EcoDTO(eco);
//		Map<String, String> map = ChangeUtil.getApproveInfo(eco);
		Map<String, String> map = new HashMap<>();

		String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
		String path = WTProperties.getServerProperties().getProperty("wt.temp");

		File orgFile = new File(wtHome + "/codebase/com/e3ps/change/eco/dto/eco.xlsx");

		File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + dto.getNumber() + ".xlsx"));

		FileInputStream file = new FileInputStream(newFile);

		XSSFWorkbook workbook = new XSSFWorkbook(file);

		XSSFSheet sheet = workbook.getSheetAt(0);

		workbook.setSheetName(0, dto.getNumber());

		System.out.println("쓰기 시작....." + POIUtil.getSheetRow(sheet));

		// 문서번호 (9-D ,8-3)
		XSSFCell documentNumber = sheet.getRow(8).getCell(5);
		documentNumber.setCellValue(dto.getNumber());

		// 작성자 (17-I, 16-8)
		XSSFCell creator = sheet.getRow(16).getCell(8);
		creator.setCellValue(dto.getCreator());

		// 검토자 (17-K, 16-10)
		XSSFCell chk = sheet.getRow(16).getCell(10);
		chk.setCellValue(map.get("checkerName"));

		// 승인자 (17-M 16-12)
		XSSFCell approver = sheet.getRow(16).getCell(12);
		approver.setCellValue(map.get("approveName"));

		// 작성일 (18-I, 17-8)
		XSSFCell creatDate = sheet.getRow(17).getCell(8);
		creatDate.setCellValue(dto.getCreatedDate_txt());

		// 검토일 (18-K, 17-10)
		XSSFCell chkDate = sheet.getRow(17).getCell(10);
		chkDate.setCellValue(map.get("checkDate"));

		// 승인일 (18-M, 17-12)
		XSSFCell approveDate = sheet.getRow(17).getCell(12);
		approveDate.setCellValue(map.get("approveDate"));

		// 문서 번호 (29-D, 28-3)
		XSSFCell documentNumber2 = sheet.getRow(28).getCell(3);
		documentNumber2.setCellValue(dto.getNumber());

		// 제목 (30-D, 29-3)
		XSSFCell documentName = sheet.getRow(29).getCell(3);
		documentName.setCellValue(dto.getName());

		// 작성일 (32-D, 31-3)
		XSSFCell creatDate2 = sheet.getRow(31).getCell(3);
		creatDate2.setCellValue(dto.getCreatedDate_txt());

		WTUser user = (WTUser) eco.getCreator().getObject();
		PeopleDTO pdto = new PeopleDTO(user);

		// 작성부서 (32-I, 31-8)
		XSSFCell createDept = sheet.getRow(31).getCell(8);
		createDept.setCellValue(pdto.getDepartment_name());

		// 승인일 (33-D, 32-3)
		XSSFCell approveDate2 = sheet.getRow(32).getCell(3);
		approveDate2.setCellValue(map.get("approveDate"));

		// 작성자 (33-I, 32-8)
		XSSFCell creator2 = sheet.getRow(32).getCell(8);
		creator2.setCellValue(dto.getCreator());

		// 제품명 (35-D, 34-3)
		XSSFCell modelName = sheet.getRow(34).getCell(3);
		modelName.setCellValue(dto.getModel_name());

		ArrayList<EOCompletePartLink> completeParts = completeParts(eco);

		// 완제품 품번 (36-D, 35-3)
		String completePart = "";
		for (int i = 0; i < completeParts.size(); i++) {
			EOCompletePartLink link = (EOCompletePartLink) completeParts.get(i);
			String n = link.getCompletePart().getNumber();
			completePart = completePart + n;
			if (i != (completeParts.size() - 1)) {
				completePart += ",";
			}
		}
		XSSFCell completePartName = sheet.getRow(35).getCell(3);
		completePartName.setCellValue(completePart);

		// 변경사유(37-D, 36-3)
		XSSFCell eoCommentA = sheet.getRow(36).getCell(3);
		// PJT EDIT 20161122
		XSSFCellStyle cellStyle_UP = workbook.createCellStyle();
		cellStyle_UP.setAlignment(HorizontalAlignment.LEFT);
		cellStyle_UP.setShrinkToFit(true);
		XSSFFont sfont = workbook.createFont();
		sfont.setFontHeightInPoints((short) 10);
		cellStyle_UP.setFont(sfont);
		cellStyle_UP.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyle_UP.setBorderBottom(BorderStyle.THIN);
		cellStyle_UP.setBorderTop(BorderStyle.THIN);
		cellStyle_UP.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle_UP.setBottomBorderColor(IndexedColors.BLACK.getIndex());

		cellStyle_UP.setWrapText(true);
		eoCommentA.setCellStyle(cellStyle_UP);
		eoCommentA.setCellValue(dto.getEoCommentA());
		XSSFRow comARow = (XSSFRow) sheet.getRow(36);
		int height = comARow.getHeight();
		String com = dto.getEoCommentA();
		if (null != com) {
			for (int i = 0; i < com.length(); i++) {
				char ca = com.charAt(i);
				Character careCa = Character.valueOf('\n');
				if (ca == careCa) {
					height += 150;
				}
			}
		}
		System.out.println("heigh2t=" + height);
		comARow.setHeight((short) height);
		// 변경근거(38-D, 37-3)
		XSSFCell ecrNo = sheet.getRow(37).getCell(3);
		// to enable newlines you need set a cell styles with wrap=true
		XSSFCellStyle cellStyleEcrNo_UP = workbook.createCellStyle();
		cellStyleEcrNo_UP.setAlignment(HorizontalAlignment.LEFT);
		cellStyleEcrNo_UP.setShrinkToFit(true);
		cellStyleEcrNo_UP.setFont(sfont);
		cellStyleEcrNo_UP.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyleEcrNo_UP.setBorderBottom(BorderStyle.MEDIUM);
		cellStyleEcrNo_UP.setBorderTop(BorderStyle.THIN);
		cellStyleEcrNo_UP.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyleEcrNo_UP.setBottomBorderColor(IndexedColors.BLACK.getIndex());

		cellStyleEcrNo_UP.setWrapText(true);
		ecrNo.setCellStyle(cellStyleEcrNo_UP);

		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecr", RequestOrderLink.class);
		String ecrNumbersNames = "";
		while (qr.hasMoreElements()) {
			EChangeRequest ecr = (EChangeRequest) qr.nextElement();
			String nn = ecr.getEoNumber() + " [" + ecr.getName() + "]";
			ecrNumbersNames += (nn + "\r\n");
		}
		XSSFRow ecrNumbersNamesRow = (XSSFRow) sheet.getRow(37);
		if (null != ecrNumbersNames) {
			for (int i = 0; i < ecrNumbersNames.length(); i++) {
				char ca = ecrNumbersNames.charAt(i);
				Character careCa = Character.valueOf('\n');
				if (ca == careCa) {
					height += 150;
				}
			}
		}
		ecrNumbersNamesRow.setHeight((short) height);
		ecrNo.setCellValue(ecrNumbersNames);

		/**
		 * 설계변경 부품 내역 46 Line(45 Index) 부터 ~
		 */

		int row = 45;

//		List<Map<String, String>> excelList = getECO_RoleTypeExcelData(eco);
//
//		for (Map<String, String> excelMap : excelList) {
//
//			if (row > 45) {
//				POIUtil.copyRow(workbook, sheet, (row - 1), 1);
//			}
//
//			// NO (B, 1)
//			XSSFCell excelNo = sheet.getRow(row).getCell(1);
//			excelNo.setCellValue((String) excelMap.get("no"));
//
//			// 변경 전 품번 (C, 2)
//			XSSFCell oldPartNumber = sheet.getRow(row).getCell(2);
//			oldPartNumber.setCellValue((String) excelMap.get("oldPartNumber"));
//
//			// 변경 후 품번 (F, 5)
//			XSSFCell newPartNumber = sheet.getRow(row).getCell(5);
//			newPartNumber.setCellValue((String) excelMap.get("newPartNumber"));
//
//			// 품명 (G, 6)
//			XSSFCell partName = sheet.getRow(row).getCell(6);
//			partName.setCellValue((String) excelMap.get("partName"));
//
//			// 부품 상태 코드 (I, 8)
//			XSSFCell stateCode = sheet.getRow(row).getCell(8);
//			stateCode.setCellValue((String) excelMap.get("stateCode"));
//
//			// 납품 장비 (J, 9)
//			XSSFCell deliveryProduct = sheet.getRow(row).getCell(9);
//			deliveryProduct.setCellValue((String) excelMap.get("deliveryProduct"));
//
//			// 완성 장비 (K, 10)
//			XSSFCell completeProduct = sheet.getRow(row).getCell(10);
//			completeProduct.setCellValue((String) excelMap.get("completeProduct"));
//
//			// 사내 재고 (L, 11)
//			XSSFCell companyStock = sheet.getRow(row).getCell(11);
//			companyStock.setCellValue((String) excelMap.get("companyStock"));
//
//			// 발주 부품 (M, 12)
//			XSSFCell orderProduct = sheet.getRow(row).getCell(12);
//			orderProduct.setCellValue((String) excelMap.get("orderProduct"));
//
//			// 중량 (N, 13)
//			XSSFCell wegiht = sheet.getRow(row).getCell(13);
//			wegiht.setCellValue((String) excelMap.get("wegiht"));
//
//			row++;
//		}

		/**
		 * 설계변경 부품 내역 끝
		 */

		if (row == 45)
			row++;

		row++;
		row++;
		row++;

		// 설계변경 세부내용 (blank)
		sheet.getRow(row).setHeight((short) 90);
		row++;

		// 설계변경 세부내용 (B, 1)
		sheet.getRow(row).setHeight((short) 1050);

		XSSFCell eoCommentB = sheet.getRow(row).getCell(1);
		eoCommentB.setCellValue(dto.getEoCommentB());
		XSSFRow comBRow = (XSSFRow) sheet.getRow(row);
		int bheight = comBRow.getHeight();
		String comB = dto.getEoCommentB();
		if (null != comB) {
			for (int i = 0; i < comB.length(); i++) {
				char ca = comB.charAt(i);
				Character careCa = Character.valueOf('\n');
				if (ca == careCa) {
					bheight += 300;
				}
			}
		}

		System.out.println("bheight=" + bheight);
		comBRow.setHeight((short) bheight);
		row++;

		row++;
		row++;
		row++;

		/**
		 * 변경 문건 시작
		 */
		int startRow = row;
		int documentIndex = 1;
//
//		List<Map<String, Object>> docList = ECAHelper.service.viewECA(eco);
//
//		for (Map<String, Object> dmap : docList) {
//
//			ECAData ecaData = (ECAData) dmap.get("ecaData");
//
//			List<DocumentData> documentList = ecaData.getDocList();
//
//			for (DocumentData docData : documentList) {
//				if (row > startRow) {
//					POIUtil.copyRow(workbook, sheet, (row - 1), 1);
//				}
//
//				// NO (B, 1)
//				XSSFCell docNo = sheet.getRow(row).getCell(1);
//				docNo.setCellValue(documentIndex);
//
//				// 문서명 (E, 4)
//				XSSFCell docName = sheet.getRow(row).getCell(4);
//				// docName.setCellStyle(cellStyle_LEFT);
//				docName.setCellValue(docData.name);
//
//				// 문서번호 (I, 8)
//				XSSFCell docNumber = sheet.getRow(row).getCell(8);
//				docNumber.setCellValue(docData.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));
//
//				// 버전 (M, 12)
//				XSSFCell docRev = sheet.getRow(row).getCell(12);
//				docRev.setCellValue(docData.version + "." + docData.iteration);
//
//				documentIndex++;
//
//				row++;
//			}
//		}

		if (startRow == row)
			row++;
		row++;
		// 위험관리
		XSSFCell licensing = sheet.getRow(row).getCell(4);
		System.out.println("licensing =" + licensing);
		licensing.setCellValue(dto.getLicensing_name());
		// 위험 통제
		XSSFCell riskType = sheet.getRow(row).getCell(9);
		riskType.setCellValue(dto.getRiskType_name());
		row++;
		row++;

		// 특기사항 (blank)
		sheet.getRow(row).setHeight((short) 90);
		row++;

		// 특기사항 (E, 4)
		sheet.getRow(row).setHeight((short) 1050);

		XSSFCell eoCommentC = sheet.getRow(row).getCell(4);
		eoCommentC.setCellValue(dto.getEoCommentC());
		XSSFRow comCRow = (XSSFRow) sheet.getRow(row);
		int cheight = comCRow.getHeight();
		String comC = dto.getEoCommentC();
		if (null != comC) {
			for (int i = 0; i < comC.length(); i++) {
				char ca = comC.charAt(i);
				Character careCa = Character.valueOf('\n');
				if (ca == careCa) {
					cheight += 350;
				}
			}
		}
		System.out.println("bheight=" + cheight);
		comCRow.setHeight((short) cheight);
		row++;

		// 기타사항 Blank
		sheet.getRow(row).setHeight((short) 90);
		row++;

		// 기타사항 (E, 4)
		sheet.getRow(row).setHeight((short) 1050);
		XSSFCell eoCommentD = sheet.getRow(row).getCell(4);
		eoCommentD.setCellValue(dto.getEoCommentD());
		XSSFRow comDRow = (XSSFRow) sheet.getRow(row);
		int dheight = comDRow.getHeight();
		String comd = dto.getEoCommentD();
		if (null != comd) {
			for (int i = 0; i < comd.length(); i++) {
				char ca = comd.charAt(i);
				Character careCa = Character.valueOf('\n');
				if (ca == careCa) {
					dheight += 350;
				}
			}
		}
		System.out.println("dheight=" + dheight);
		comDRow.setHeight((short) dheight);
		row++;

		row++;
		row++;
		row++;

		/**
		 * 첨부파일 시작 (기타사항 +4)
		 */

		startRow = row;
		int attachCount = 1;

		QueryResult attachQr = ContentHelper.service.getContentsByRole(eco, ContentRoleType.SECONDARY);

		while (attachQr.hasMoreElements()) {

			ContentItem item = (ContentItem) attachQr.nextElement();

			if (item != null) {

				if (row > startRow) {
					POIUtil.copyRow(workbook, sheet, (row - 1), 1);
				}

				ApplicationData appData = (ApplicationData) item;

				// NO (B, 1)
				XSSFCell attchNo = sheet.getRow(row).getCell(1);
				attchNo.setCellValue(attachCount);

				// 파일명 (C, 2)
				XSSFCell attchName = sheet.getRow(row).getCell(2);
				attchName.setCellValue(appData.getFileName());

				attachCount++;
				row++;
			}
		}

		/**
		 * 첨부파일 끝
		 */

		if (startRow == row)
			row++;
		row++;
		row++;
		row++;

		/**
		 * 결재자 의견 시작(첨부파일 +2)
		 */

		startRow = row;

		List<Map<String, Object>> appList = GroupwareHelper.service.getApprovalList(oid);

		for (Map<String, Object> amap : appList) {

			if (row > startRow) {
				POIUtil.copyRow(workbook, sheet, (row - 1), 1);
			}

			// 이름 (B, 1)
			XSSFCell name = sheet.getRow(row).getCell(1);
			name.setCellValue((String) amap.get("userName"));

			// 날짜 (D, 3)
			String processDate = "";
			if (amap.get("processDate") != null) {
				System.out.println((amap.get("processDate")).getClass());
				processDate = DateUtil.subString(((Object) amap.get("processDate")).toString(), 0, 10);
			}

			XSSFCell date = sheet.getRow(row).getCell(3);
			date.setCellValue(processDate);

			// 내용 (F, 5)
			XSSFCell description = sheet.getRow(row).getCell(5);
			description.setCellValue((String) amap.get("comment"));
			XSSFRow comDescRow = (XSSFRow) sheet.getRow(row);
			int descheight = comDescRow.getHeight();
			String comdescd = (String) amap.get("comment");
			if (null != comdescd) {
				for (int i = 0; i < comdescd.length(); i++) {
					char ca = comdescd.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						descheight += 350;
					}
				}
			}
			System.out.println("descheight=" + descheight);
			comDescRow.setHeight((short) descheight);

			row++;
		}

		/**
		 * 결재자 의견 끝
		 */

		System.out.println("쓰기 종료....." + POIUtil.getSheetRow(sheet));

		file.close();

		FileOutputStream outFile = new FileOutputStream(newFile);
		workbook.write(outFile);
		outFile.close();

		workbook.close();

		result.put("name", newFile.getName());
		return result;
	}
}