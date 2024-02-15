package com.e3ps.sap.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPDev600Connection;
import com.e3ps.sap.conn.SAPDevConnection;
import com.e3ps.sap.dto.SAPBomDTO;
import com.e3ps.sap.dto.SAPReverseBomDTO;
import com.e3ps.sap.dto.SAPSendBomDTO;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class SAPHelper {

	public static final SAPService service = ServiceFactory.getService(SAPService.class);
	public static final SAPHelper manager = new SAPHelper();

	/**
	 * 샘플 데이터 만들기 전용 코드 가져오기
	 */
	public String get(String name, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.NAME, name.trim());
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, codeType);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			return n.getCode();
		}
		return null;
	}

	/**
	 * 자재마스터 테스트용 품목 OID 숫자값, EO/ECO NUMBER
	 */
	public void ZPPIF_PDM_001_TEST(long id, String AENNR8) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		System.out.println("destination=" + destination);
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		WTPart root = (WTPart) CommonUtil.getObject("wt.part.WTPart:" + id);
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		getterSkip(root, list);
//		ArrayList<WTPart> list = descendants(root);

		System.out.println("SAP PART INTERFACE START!");
		// ET_MAT
		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트

		int idx = 1;
		for (WTPart part : list) {

//			String next = getNextSeq("PART", "00000000");
//			String ZIFNO = "PART-" + next;

			insertTable.insertRow(idx);

			// 플랜트

			// 샘플로 넣기
			insertTable.setValue("AENNR8", AENNR8); // 변경번호 8자리
			insertTable.setValue("MATNR", part.getNumber()); // 자재번호
			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
			insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위

			String ZSPEC = IBAUtil.getStringValue(part, "SPECIFICATION");
			insertTable.setValue("ZSPEC", ZSPEC); // 사양

			String ZMODEL = IBAUtil.getStringValue(part, "MODEL");
			insertTable.setValue("ZMODEL", ZMODEL); // Model:프로젝트

			String ZPRODM_CODE = IBAUtil.getStringValue(part, "PRODUCTMETHOD");
			NumberCode ZPRODM_N = NumberCodeHelper.manager.getNumberCode(ZPRODM_CODE, "PRODUCTMETHOD");
			if (ZPRODM_N != null) {
				insertTable.setValue("ZPRODM", ZPRODM_N.getName()); // 제작방법
			} else {
				insertTable.setValue("ZPRODM", ""); // 제작방법
			}

			String ZDEPT = IBAUtil.getStringValue(part, "DEPTCODE");
			insertTable.setValue("ZDEPT", ZDEPT); // 설계부서

			// 샘플링 실제는 2D여부 확인해서 전송
			insertTable.setValue("ZDWGNO", part.getNumber() + ".DRW"); // 도면번호

			String v = part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue();
			insertTable.setValue("ZEIVR", v); // 버전
			// 테스트 용으로 전송
			insertTable.setValue("ZPREPO", "X"); // 선구매필요

			System.out.println("number = " + part.getNumber() + ", version = " + v);
			idx++;
		}

		function.execute(destination);
		JCoParameterList result = function.getExportParameterList();
//		JCoStructure e_return = result.getStructure("E_RETURN");
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);

		// ERP 전송 로그 작성
		System.out.println("SAP PART INTERFACE END!");
//		System.out.println("result=" + result);
	}

	/**
	 * BOM이력 테스트 용
	 */
	public void ZPPIF_PDM_002_TEST(long pid, long eid) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDevConnection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		WTPart root = (WTPart) CommonUtil.getObject("wt.part.WTPart:" + pid);
		ArrayList<WTPart> list = PartHelper.manager.descendants(root);

		// ES_ECM
		System.out.println("SAP ECO INTERFACE START!");
		JCoTable insertTable = function.getTableParameterList().getTable("ES_ECM");

		EChangeOrder e = (EChangeOrder) CommonUtil.getObject("com.e3ps.change.EChangeOrder:" + eid);

		insertTable.setValue("AENNR8", e.getEoNumber()); // 변경번호
		insertTable.setValue("ZECMID", e.getEoType()); // EO/ECO구분
		insertTable.setValue("DATUV", ""); // 효력시작일
		insertTable.setValue("AEGRU", ""); // 변경사유
		insertTable.setValue("AETXT", ""); // 변경내역
		insertTable.setValue("AETXT_L", ""); // 변경내역

		int idx = 1;
		for (WTPart part : list) {

		}

		System.out.println("SAP ECO INTERFACE END!");

		function.execute(destination);
		JCoTable result = function.getTableParameterList().getTable("ET_MAT"); // 여기도 뭐??
		result.firstRow();
		for (int j = 1; j <= result.getNumRows(); j++, result.nextRow()) {
			System.out.println("ZIFSTA(상태) = " + result.getValue("ZIFSTA"));
			System.out.println("ZIFMSG(처리상태) = " + result.getValue("ZIFMSG"));
		}

		System.out.println("SAP BOM INTERFACE END!");
		// ERP 전송 로그 작성

		System.out.println("result=" + result);
	}

	/**
	 * ECN 테스트용
	 */
	public void ZPPIF_PDM_003_TEST() throws Exception {
	}

	/**
	 * 테스트용 데이터 만들기 위한것, 부품번호, 버전, 이터레이션으로 부품 찾기
	 */
	public WTPart getPart(String number, String v, String i) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.NUMBER, number);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "versionInfo.identifier.versionId", v);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "iterationInfo.identifier.iterationId", i);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			return part;
		}
		return null;
	}

	/**
	 * 테스트용 데이터 만들기 위한것, 부품번호로 마스터 찾기
	 */
	public WTPartMaster getMaster(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.NUMBER, number);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			return (WTPartMaster) part.getMaster();
		}
		return null;
	}

	/**
	 * 테이블 시퀀시 가져오기
	 */
	public String getNextSeq(String table, String ft) throws Exception {
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			StringBuffer sb = null;

			sb = new StringBuffer();

			sb.append("SELECT " + table + "_SEQ.NEXTVAL from dual");
			st = con.prepareStatement(sb.toString());

			rs = st.executeQuery();

			String next = "";
			while (rs.next()) {
				BigDecimal bd = rs.getBigDecimal(1);
				next = String.valueOf(bd);
			}

			// System.out.println("getECOSeq seqNum1 = " + seqNum);
//			String format = "00000000";
			if (next == null) {
				DecimalFormat decimalformat = new DecimalFormat(ft);
				next = decimalformat.format(Long.parseLong(ft) + 1);
			} else {
				DecimalFormat decimalformat = new DecimalFormat(ft);
				next = decimalformat.format(Long.parseLong(next));
			}
			return next;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

	/**
	 * 품목 SAP 전용 BOM 구조 함수 - 둥복 자재 제거 테스트용
	 */
	public ArrayList<WTPart> descendants(WTPart part) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		// root 추가
		list.add(part);

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);

		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(p);
			descendants(p, list);
		}
		return list;
	}

	/**
	 * 품목 SAP 전용 BOM 구조 재귀 함수 - 중복제거 테스트용
	 */
	private void descendants(WTPart part, ArrayList<WTPart> list) throws Exception {

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(p);
			descendants(p, list);
		}
	}

	/**
	 * ERP 전송시 사용 - 중복 제외
	 */
	public void getterSkip(WTPart root, ArrayList<WTPart> list) throws Exception {
		// root 추가

		String viewName = root.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (!list.contains(p)) {
				list.add(p);
			}
			getterSkip(p, list);
		}
	}

	/**
	 * SAP 전송할 품목 수집
	 */
	public ArrayList<WTPart> getterSkip(WTPart part) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		// root 추가
		if (!list.contains(part)) {
			list.add(part);
		}
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);

		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (!list.contains(p)) {
				list.add(p);
			}
			getterSkip(p, list);
		}
		return list;
	}

	/**
	 * BOM 전송용 데이터 만드는 함수
	 */
	public ArrayList<SAPBomDTO> getterBomData(WTPart part) throws Exception {
		ArrayList<SAPBomDTO> list = new ArrayList<SAPBomDTO>();
		// root 추가

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);

		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			WTPartMaster child = link.getUses();

			if (SAPHelper.manager.skipLength(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(child.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(child.getNumber())) {
				continue;
			}

			SAPBomDTO dto = new SAPBomDTO(link);
			list.add(dto);
			getterBomData(p, list);
		}
		return list;
	}

	/**
	 * BOM 전송용 데이터 만드는 함수
	 */
	private void getterBomData(WTPart part, ArrayList<SAPBomDTO> list) throws Exception {
		// root 추가
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			WTPartMaster child = link.getUses();
			if (SAPHelper.manager.skipLength(child.getNumber())) {
				continue;
			}
			if (SAPHelper.manager.skipEight(child.getNumber())) {
				continue;
			}
			SAPBomDTO dto = new SAPBomDTO(link);
			list.add(dto);
			getterBomData(p, list);
		}
	}

	/**
	 * BOM 역전개 데이터 검색후 SAP 전송에 맞게 가공
	 */
	public ArrayList<SAPReverseBomDTO> getReverseBomData(WTPart end, EChangeOrder eco) throws Exception {
		ArrayList<SAPReverseBomDTO> list = new ArrayList<SAPReverseBomDTO>();
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

		String viewName = end.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

//		String state = end.getLifeCycleState().toString();
//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
//		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);

		query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
				new int[] { idx_part });

		QueryResult re = PersistenceHelper.manager.find(query);
		while (re.hasMoreElements()) {
			Object obj[] = (Object[]) re.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (SAPHelper.manager.skipEight(end.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(end.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(p.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(p.getNumber())) {
				continue;
			}

			SAPReverseBomDTO dto = new SAPReverseBomDTO(end, p, link, eco);
			list.add(dto);
			getReverseBomData(p, list, eco);
		}
		return list;
	}

	/**
	 * BOM 역전개 데이터 검색후 SAP 전송에 맞게 가공
	 */
	private ArrayList<SAPReverseBomDTO> getReverseBomData(WTPart end, ArrayList<SAPReverseBomDTO> list,
			EChangeOrder eco) throws Exception {
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

		String viewName = end.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

//		String state = end.getLifeCycleState().toString();
//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
//		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);

		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			SAPReverseBomDTO dto = new SAPReverseBomDTO(end, p, link, eco);
			list.add(dto);
			getReverseBomData(p, list, eco);
		}
		return list;
	}

	/**
	 * ECO 최하위 품목 모으기..
	 */
	public ArrayList<WTPart> getEcoEndParts(EChangeOrder eco) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		QueryResult result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
			String viewName = part.getViewName();
			if (!StringUtil.checkString(viewName)) {
				viewName = "Design";
			}

			View view = ViewHelper.service.getView(viewName);
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
			QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
			if (qr.size() == 0) {
				list.add(part);
			}
		}
		return list;
	}

	/**
	 * 이전 품목
	 */
	public WTPart getPre(WTPart after, EChangeOrder eco) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToPartLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "roleBObjectRef.key.id",
				(WTPartMaster) after.getMaster());
		QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "ecoReference.key.id", eco);
		QueryResult result = PersistenceHelper.manager.find(query);
		WTPart pre_part = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartToPartLink link = (PartToPartLink) obj[0];
			WTPartMaster m = link.getPrev();
			String version = link.getPreVersion();
			pre_part = PartHelper.manager.getPart(m.getNumber(), version);
		}
		return pre_part;
	}

	/**
	 * 길이 및 정규식 제외
	 */
	public boolean skipLength(String number) throws Exception {
		if (number.length() > 10) {
			return true;
		}

		if (!(Pattern.matches("^[0-9]+$", number))) {
			return true;
		}
		return false;
	}

	/**
	 * 8번 품번 제외
	 */
	public boolean skipEight(String number) throws Exception {
		if (number.startsWith("8")) {
			return true;
		}
		// 정규식으로 알파벳 제외한다..
		return false;
	}

	/**
	 * 정전개 해서 보낸다.. 1레벨만 재귀 함수 X
	 */
	public ArrayList<SAPSendBomDTO> getOneLevel(WTPart parent, EChangeOrder eco) throws Exception {
		ArrayList<SAPSendBomDTO> list = new ArrayList<SAPSendBomDTO>();
		String viewName = parent.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult qr = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart child = (WTPart) obj[1];

			if (SAPHelper.manager.skipEight(parent.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(parent.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(child.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(child.getNumber())) {
				continue;
			}

			SAPSendBomDTO dto = new SAPSendBomDTO(child, parent, link, eco);
			list.add(dto);
		}

		return list;
	}

	/**
	 * 코드:이름 SAP 전송값으로 변경
	 */
	public String convertSapValue(WTPart part, String key) throws Exception {
		String iba = IBAUtil.getStringValue(part, key);
		return convertSapValue(iba, key);
	}

	/**
	 * 코드:이름 SAP 전송값으로 변경
	 */
	private String convertSapValue(String iba, String key) throws Exception {
		String value = iba + ":";
		NumberCode n = NumberCodeHelper.manager.getNumberCode(iba, key);
		if (n != null) {
			value += n.getName();
		}
		return value;
	}

	/**
	 * 삭제된 품목 리스트
	 */
	public ArrayList<Map<String, Object>> removeList(ArrayList<Object[]> lefts, ArrayList<Object[]> rights)
			throws Exception {
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<Map<String, Object>> removeList = new ArrayList<>();

		for (Object[] o : rights) {
			WTPart l = (WTPart) o[1];
			WTPartUsageLink link = (WTPartUsageLink) o[0];
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("number", l.getNumber());
			mergedData.put("qty", (int) link.getQuantity().getAmount());
			mergedData.put("unit", link.getQuantity().getUnit().toString().toUpperCase());
			mergedData.put("oid", l.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (Object[] o : lefts) {
			WTPart r = (WTPart) o[1];
			WTPartUsageLink link = (WTPartUsageLink) o[0];
			String key = r.getPersistInfo().getObjectIdentifier().getStringValue();
			boolean isExist = false;

			for (Map<String, Object> mergedData : mergedList) {
				String oid = (String) mergedData.get("oid");
				String _key = oid;
				if (key.equals(_key)) {
					isExist = true;
					break;
				}
			}

			if (!isExist) {
				// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
				Map<String, Object> mergedData = new HashMap<>();
				mergedData.put("oid", key);
				mergedData.put("qty", (int) link.getQuantity().getAmount());
				mergedData.put("unit", link.getQuantity().getUnit().toString().toUpperCase());
				mergedData.put("number", r.getNumber());
				compList.add(mergedData);
			}
		}

		for (Map<String, Object> m : compList) {
			String oid = (String) m.get("oid");
			WTPart pp = (WTPart) CommonUtil.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(pp.getMaster(), "after", PartToPartLink.class);
			if (qr.size() > 0) {
				continue;
			}
			removeList.add(m);
		}
		return removeList;
	}

	/**
	 * 추가된 품목 리스트
	 */
	public ArrayList<Map<String, Object>> addList(ArrayList<Object[]> lefts, ArrayList<Object[]> rights)
			throws Exception {
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		ArrayList<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();

		ArrayList<Map<String, Object>> addList = new ArrayList<>();

		for (Object[] o : lefts) {
			WTPart l = (WTPart) o[1];
			WTPartUsageLink link = (WTPartUsageLink) o[0];
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("number", l.getNumber());
			mergedData.put("qty", (int) link.getQuantity().getAmount());
			mergedData.put("unit", link.getQuantity().getUnit().toString().toUpperCase());
			mergedData.put("oid", l.getPersistInfo().getObjectIdentifier().getStringValue());
			mergedList.add(mergedData);
		}

		for (Object[] o : rights) {
			WTPart r = (WTPart) o[1];
			WTPartUsageLink link = (WTPartUsageLink) o[0];
			String key = r.getPersistInfo().getObjectIdentifier().getStringValue();
			boolean isExist = false;

			for (Map<String, Object> mergedData : mergedList) {
				String oid = (String) mergedData.get("oid");
				String _key = oid;
				if (key.equals(_key)) {
					isExist = true;
					break;
				}
			}

			if (!isExist) {
				// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
				Map<String, Object> mergedData = new HashMap<>();
				mergedData.put("oid", key);
				mergedData.put("qty", (int) link.getQuantity().getAmount());
				mergedData.put("unit", link.getQuantity().getUnit().toString().toUpperCase());
				mergedData.put("number", r.getNumber());
				compList.add(mergedData);
			}
		}

		for (Map<String, Object> m : compList) {
			String oid = (String) m.get("oid");
			WTPart pp = (WTPart) CommonUtil.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(pp.getMaster(), "prev", PartToPartLink.class);
			if (qr.size() > 0) {
				continue;
			}
			addList.add(m);
		}
		return addList;
	}

	/**
	 * ECO 전송 비교 대상을 위한 함수
	 */
	public ArrayList<Object[]> sendList(WTPart part) throws Exception {
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		// root 추가
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];

			if (SAPHelper.manager.skipEight(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(p.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(p.getNumber())) {
				continue;
			}

			list.add(obj);
			sendList(p, list);
		}
		return list;
	}

	/**
	 * ECO 전송 비교 대상을 위한 함수
	 */
	private void sendList(WTPart part, ArrayList<Object[]> list) throws Exception {
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];

			if (SAPHelper.manager.skipEight(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(part.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipEight(p.getNumber())) {
				continue;
			}

			if (SAPHelper.manager.skipLength(p.getNumber())) {
				continue;
			}

			list.add(obj);
			sendList(p, list);
		}
	}

	/**
	 * 전송 제외 처리 체크
	 */
	public boolean isPass(SAPSendBomDTO dto) throws Exception {

		if (dto.getChildPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getChildPartNumber())) {
				return true;
			}
		}

		if (dto.getChildPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getChildPartNumber())) {
				return true;
			}
		}

		if (dto.getParentPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getParentPartNumber())) {
				return true;
			}
		}

		if (dto.getParentPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getParentPartNumber())) {
				return true;
			}
		}

		if (dto.getChildPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getChildPartNumber())) {
				return true;
			}
		}

		if (dto.getChildPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getChildPartNumber())) {
				return true;
			}
		}

		if (dto.getParentPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getParentPartNumber())) {
				return true;
			}
		}

		if (dto.getParentPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getParentPartNumber())) {
				return true;
			}
		}

		// 신규
		if (dto.getNewChildPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getNewChildPartNumber())) {
				return true;
			}
		}

		if (dto.getNewChildPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getNewChildPartNumber())) {
				return true;
			}
		}

		if (dto.getNewParentPartNumber() != null) {
			if (SAPHelper.manager.skipEight(dto.getNewParentPartNumber())) {
				return true;
			}
		}

		if (dto.getNewParentPartNumber() != null) {
			if (SAPHelper.manager.skipLength(dto.getNewParentPartNumber())) {
				return true;
			}
		}
		return false;
	}
}
