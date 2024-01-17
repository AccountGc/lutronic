package com.e3ps.change.eo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eo.column.EoColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPDev600Connection;
import com.e3ps.sap.service.SAPHelper;
import com.e3ps.sap.util.SAPUtil;
import com.e3ps.system.service.SystemHelper;
import com.ibm.icu.text.DecimalFormat;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.vc.wip.WorkInProgressHelper;

public class EoHelper {

	public static final EoService service = ServiceFactory.getService(EoService.class);
	public static final EoHelper manager = new EoHelper();

	/**
	 * 큐 관련 상수
	 */
	private static final String processQueueName = "SapProcessQueue";
	private static final String className = "com.e3ps.change.util.EChangeUtils";
	private static final String methodName = "afterEoAction";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<EoColumn> list = new ArrayList<>();

		ArrayList<Map<String, String>> rows104 = (ArrayList<Map<String, String>>) params.get("rows104");

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String eoType = (String) params.get("eoType");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String creatorOid = (String) params.get("creatorOid");
		String approveFrom = (String) params.get("approveFrom");
		String approveTo = (String) params.get("approveTo");
		String state = (String) params.get("state");
		String model = (String) params.get("model");

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
		QuerySpecUtils.toState(query, idx, EChangeOrder.class, state);
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeOrder.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		if (approveFrom != null && approveFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, approveFrom), new int[] { idx });
		}
		if (approveTo != null && approveTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, approveTo), new int[] { idx });
		}
		if (StringUtil.checkString(eoType)) {
			QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, eoType);
		} else {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendOpenParen();
			QuerySpecUtils.toEquals(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "DEV");
			query.appendOr();
			QuerySpecUtils.toEquals(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "PRODUCT");
			query.appendCloseParen();
		}
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.MODEL, model);

		if (rows104 != null && rows104.size() > 0) {
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
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EoColumn data = new EoColumn(obj);
			data.setRowNum(rowNum++);
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
	 * EO와 연결된 완제품 리스트
	 */
	public ArrayList<EOCompletePartLink> completeParts(Object obj) throws Exception {
		ArrayList<EOCompletePartLink> list = new ArrayList<EOCompletePartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eo = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class, false);
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
	 * 진행중인 EO에 완제품이 있는지
	 */
	public Map<String, Object> checkerCompletePart(WTPart part) throws Exception {
		ArrayList<EOCompletePartLink> list = completeParts(part);
		Map<String, Object> map = new HashMap<>();
		for (EOCompletePartLink link : list) {
			EChangeOrder eo = link.getEco();

			// ECO 경우 패스
			if (eo.getEoType().equals("CHANGE")) {
				continue;
			}

			// 승인된 EO 혹은 취소된 EO 패스
			String state = eo.getState().toString();
			if (state.equals("APPROVED") || state.equals("CANCELLED")) {
				continue;
			}

			String msg = part.getNumber() + " 품목이 EO = " + eo.getEoNumber() + "에서 작업중인 업무가 있습니다.";
			map.put("msg", msg);
			map.put("result", false);
			return map;
		}
		map.put("result", true);
		return map;
	}

	/**
	 * ECO에서 진행중인 품목인지
	 */
	public Map<String, Object> validatePart(WTPart part) throws Exception {
		Map<String, Object> map = new HashMap<>();
		QueryResult result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			String state = eco.getState().toString();
			if (state.equals("APPROVED") || state.equals("CANCELLED")) {
				continue;
			}
			String msg = part.getNumber() + " 품목이 ECO = " + eco.getEoNumber() + "에서 작업중인 업무가 있습니다.";
			map.put("msg", msg);
			map.put("result", false);
			return map;
		}
		map.put("result", true);
		return map;
	}

	/**
	 * EO 관련 객체
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
		if ("doc".equalsIgnoreCase(type)) {
			// 문서
			return JSONArray.fromObject(referenceDoc(eo, list));
		} else if ("part".equalsIgnoreCase(type)) {
			// 완제품
			return JSONArray.fromObject(referenceComplete(eo, list));
		} else if ("activity".equalsIgnoreCase(type)) {
			// ECA
			return JSONArray.fromObject(referenceActivity(eo, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			// 제품
			return JSONArray.fromObject(referenceCode(eo, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * EO 관련 문서
	 */
	private Object referenceDoc(EChangeOrder eo, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eo, "document", DocumentEOLink.class);
		while (result.hasMoreElements()) {
			WTDocument doc = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * EO 관련 완제품
	 */
	private ArrayList<Map<String, Object>> referenceComplete(EChangeOrder eo, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eo, "completePart", EOCompletePartLink.class);
		while (result.hasMoreElements()) {
			WTPartMaster master = (WTPartMaster) result.nextElement();
			WTPart part = PartHelper.manager.latest(master);
			Map<String, Object> map = new HashMap<>();
			map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
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
	 * EO 관련 설계변경 활동
	 */
	private Object referenceActivity(EChangeOrder eo, ArrayList<Map<String, Object>> list) throws Exception {
		ArrayList<EChangeActivity> colletActivityList = ActivityHelper.manager.colletActivity(eo);
		for (EChangeActivity item : colletActivityList) {
			ActDTO dto = new ActDTO(item);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		if (model != null) {
			String[] ss = model.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
					if (n != null) {
						display += s + " [" + n.getName() + "]";
					}
				} else {
					NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
					if (n != null) {
						display += s + " [" + n.getName() + "], ";
					}
				}
			}
		}
		return display;
	}

	/**
	 * 완제품 아래의 모든 부품을 가져오는 함수
	 */
	public ArrayList<WTPart> getter(EChangeOrder eo, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			WTPartMaster m = link.getCompletePart();
			String v = link.getVersion();
			WTPart p = PartHelper.manager.getPart(m.getNumber(), v);
			list = PartHelper.manager.descendants(p);
		}
		return list;
	}

	/**
	 * EO 결재후 진행될 내용 큐로 처리
	 */
	public void postAfterAction(EChangeOrder e) throws Exception {
		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", e.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * MODEL
	 */
	private Object referenceCode(EChangeOrder eo, ArrayList<Map<String, Object>> list) throws Exception {
		String[] codes = eo.getModel() != null ? eo.getModel().split(",") : null;

		if (codes != null) {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(NumberCode.class, true);
			for (int i = 0; i < codes.length; i++) {
				QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, codes[i]);
			}
			QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "MODEL");
			QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				NumberCode n = (NumberCode) obj[0];
				NumberCodeDTO dto = new NumberCodeDTO(n);
				Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
				list.add(map);
			}
		}
		return list;
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
			EChangeOrder eo = (EChangeOrder) obj[0];
			String eoNumber = eo.getEoNumber();
			String next = eoNumber.substring(6); // 00
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "01";
		}
		return rtn;
	}

	/**
	 * EO 등록시 체크아웃된 품목이 있는지 확인
	 */
	public Map<String, Object> checkCheckout(EChangeOrder eo) throws Exception {
		ArrayList<EOCompletePartLink> completeParts = EoHelper.manager.completeParts(eo);
		Map<String, Object> map = new HashMap<>();
		for (EOCompletePartLink link : completeParts) {
			WTPartMaster master = link.getCompletePart();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), link.getVersion());
			ArrayList<WTPart> list = PartHelper.manager.descendants(part);
			for (WTPart p : list) {
				boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
				if (isCheckOut) {
					String msg = p.getNumber() + " 품목이 체크아웃 상태입니다.";
					map.put("msg", msg);
					map.put("result", false);
					return map;
				}
			}
		}
		map.put("result", true);
		return map;
	}

	/**
	 * SAP 전송전 사전 검증
	 */
	public boolean sendPartValidate(String oid) throws Exception {
		EChangeOrder e = (EChangeOrder) CommonUtil.getObject(oid);
		ArrayList<EOCompletePartLink> completeParts = EoHelper.manager.completeParts(e);
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			list = SAPHelper.manager.getterSkip(root);
		}

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		int idx = 1;
		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
		// 자재는 한번에 넘기고 함수 호출
		for (WTPart part : list) {
			Map<String, Object> params = new HashMap<>();
			String number = part.getNumber();
			// 전송 제외 품목
			if (SAPHelper.manager.skipEight(number)) {
				continue;
			}

			if (SAPHelper.manager.skipLength(number)) {
				continue;
			}

			insertTable.insertRow(idx);
			insertTable.setValue("AENNR8", e.getEoNumber()); // 변경번호 8자리
			params.put("AENNR8", e.getEoNumber());
			insertTable.setValue("MATNR", number); // 자재번호
			params.put("MATNR", number);
			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
			params.put("MAKTX", part.getName());

			if (part.getDefaultUnit().toString().toUpperCase().equals("EA")) {
				insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
				params.put("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
			} else {
				insertTable.setValue("MEINS", "EA"); // 기본단위
				params.put("MEINS", "EA");
			}

			insertTable.setValue("ZSPEC", IBAUtil.getStringValue(part, "SPECIFICATION")); // 사양
			params.put("ZSPEC", IBAUtil.getStringValue(part, "SPECIFICATION")); // 사양

			String ZMODEL_CODE = SAPHelper.manager.convertSapValue(part, "MODEL");
			String ZMODEL_VALUE = SAPUtil.sapValue(ZMODEL_CODE, "MODEL"); // Model:프로젝트
			insertTable.setValue("ZMODEL", ZMODEL_VALUE);
			params.put("ZMODEL", ZMODEL_VALUE);

			String ZPRODM_CODE = SAPHelper.manager.convertSapValue(part, "PRODUCTMETHOD");
			String ZPRODM_VALUE = SAPUtil.sapValue(ZPRODM_CODE, "PRODUCTMETHOD"); // 제작방법
			insertTable.setValue("ZPRODM", ZPRODM_VALUE);
			params.put("ZPRODM", ZPRODM_VALUE);

			String ZDEPT_CODE = SAPHelper.manager.convertSapValue(part, "DEPTCODE");
			String ZDEPT_VALUE = SAPUtil.sapValue(ZDEPT_CODE, "DEPTCODE"); // 부서
			insertTable.setValue("ZDEPT", ZDEPT_VALUE);
			params.put("ZDEPT", ZDEPT_VALUE);

			// 샘플링 실제는 2D여부 확인해서 전송

			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			if (epm != null) {
				EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
				if (epm2D != null) {
					insertTable.setValue("ZDWGNO", epm2D.getNumber());
					params.put("ZDWGNO", "");
				}
			} else {
				insertTable.setValue("ZDWGNO", "");
				params.put("ZDWGNO", "");
			}

			String v = part.getVersionIdentifier().getSeries().getValue();
			insertTable.setValue("ZEIVR", v); // 버전
			params.put("ZEIVR", v);
			// 테스트 용으로 전송
			insertTable.setValue("ZPREPO", ""); // 선구매필요 EO 시에는 어떻게 처리??
			params.put("ZPREPO", "");

			// ?? 코드: 단위 형태인지
			String weight = IBAUtil.getAttrfloatValue(part, "WEIGHT"); // 중량
			insertTable.setValue("BRGEW", weight);
			params.put("BRGEW", weight);

			insertTable.setValue("GEWEI", "G");
			params.put("GEWEI", "G");

			String ZMATLT = SAPHelper.manager.convertSapValue(part, "MAT");
			insertTable.setValue("ZMATLT", ZMATLT); // 재질
			params.put("ZMATLT", ZMATLT);

			String ZPOSTP = SAPHelper.manager.convertSapValue(part, "FINISH");
			insertTable.setValue("ZPOSTP", ZPOSTP); // 후처리
			params.put("ZPOSTP", ZPOSTP);

			String ZDEVND = SAPHelper.manager.convertSapValue(part, "MANUFACTURE");
			insertTable.setValue("ZDEVND", ZDEVND); // 개발공급업체
			params.put("ZDEVND", ZDEVND);
			params.put("sendResult", true);
			idx++;
		}

//		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_MAT");
		rtnTable.firstRow();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			System.out.println("ZIFSTA=" + ZIFSTA);
		}
		return true;
	}
}