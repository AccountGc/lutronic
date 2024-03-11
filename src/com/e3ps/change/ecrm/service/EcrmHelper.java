package com.e3ps.change.ecrm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.EcrmToCrLink;
import com.e3ps.change.EcrmToDocumentLink;
import com.e3ps.change.EcrmToEcoLink;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.ecrm.column.EcrmColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.ZipUtil;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class EcrmHelper {

	public static final EcrmService service = ServiceFactory.getService(EcrmService.class);
	public static final EcrmHelper manager = new EcrmHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("ECRM 쿼리 시작 = " + start);
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcrmColumn> list = new ArrayList<>();

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String state = (String) params.get("state");
		String creator = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String approveFrom = (String) params.get("approveFrom");
		String approveTo = (String) params.get("approveTo");
		String writer = (String) params.get("writer");
		String createDepart = (String) params.get("createDepart");
		String writedFrom = (String) params.get("writedFrom");
		String writedTo = (String) params.get("writedTo");
//		String proposer = (String) params.get("proposer");
		String changeSection = (String) params.get("changeSection");
		String model = (String) params.get("modelcode");

		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECRMRequest.class, true);

		// 상태 임시저장 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		if (!CommonUtil.isAdmin()) {
//			query.appendWhere(new SearchCondition(ECRMRequest.class, ECRMRequest.LIFE_CYCLE_STATE,
//					SearchCondition.NOT_EQUAL, "LINE_REGISTER"), new int[] { idx });
		}
		// 제목
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.EO_NAME, name);
		// 번호
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.EO_NUMBER, number);
		// 상태
		QuerySpecUtils.toState(query, idx, ECRMRequest.class, state);
		// 등록자
		QuerySpecUtils.toCreatorQuery(query, idx, ECRMRequest.class, creator);
		// 등록일
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ECRMRequest.class, ECRMRequest.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		// 승인일
		if (approveFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ECRMRequest.class, ECRMRequest.APPROVE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, approveFrom), new int[] { idx });
		}
		if (approveTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ECRMRequest.class, ECRMRequest.APPROVE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, approveTo), new int[] { idx });
		}
		// 작성자
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.WRITER, writer);

		// 작성부서
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.CREATE_DEPART, createDepart);

		// 작성일
		if (writedFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ECRMRequest.class, ECRMRequest.CREATE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, writedFrom), new int[] { idx });
		}
		if (writedTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ECRMRequest.class, ECRMRequest.CREATE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, writedTo), new int[] { idx });
		}
		// 제안자
//		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.PROPOSER, proposer);

		// 프로젝트 코드
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.MODEL, model);

		// 변경구분
		QuerySpecUtils.toLikeAnd(query, idx, ECRMRequest.class, ECRMRequest.CHANGE_SECTION, changeSection);

		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, ECRMRequest.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcrmColumn data = new EcrmColumn(obj);
			data.setRowNum(rowNum++);
			list.add(data);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		long end = System.currentTimeMillis() / 1000;
		System.out.println("ECRM 쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	private String toSortKey(String sortKey) throws Exception {
		if ("number".equals(sortKey)) {
			return ECRMRequest.EO_NUMBER;
		} else if ("name".equals(sortKey)) {
			return ECRMRequest.EO_NAME;
		} else if ("period".equals(sortKey)) {
			return ECRMRequest.PERIOD;
		} else if ("createDepart".equals(sortKey)) {
			return ECRMRequest.CREATE_DEPART;
		} else if ("writer".equals(sortKey)) {
			return ECRMRequest.WRITER;
		} else if ("writeDate".equals(sortKey)) {
			return ECRMRequest.CREATE_DATE;
		} else if ("state".equals(sortKey)) {
			return ECRMRequest.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return ECRMRequest.CREATOR_FULL_NAME;
		} else if ("createdDate_txt".equals(sortKey)) {
			return ECRMRequest.CREATE_TIMESTAMP;
		} else if ("approvedDate".equals(sortKey)) {
			return ECRMRequest.APPROVE_DATE;
		}
		return ECRMRequest.CREATE_TIMESTAMP;
	}

	/**
	 * CR 다음번호
	 */
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("000");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECRMRequest.class, true);
		SearchCondition sc = new SearchCondition(ECRMRequest.class, ECRMRequest.EO_NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, ECRMRequest.class, ECRMRequest.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// E2312N45
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ECRMRequest ecrm = (ECRMRequest) obj[0];
			String ecrmNumber = ecrm.getEoNumber();
			String next = ecrmNumber.substring(ecrmNumber.length() - 3);
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "001";
		}
		return rtn;
	}

	/**
	 * ECRM 관련 객체 불러오기 메서드
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ECRMRequest ecrm = (ECRMRequest) CommonUtil.getObject(oid);
		if ("doc".equalsIgnoreCase(type)) {
			// 문서
			return JSONArray.fromObject(referenceDoc(ecrm, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceCode(ecrm, list));
		} else if ("cr".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceCr(ecrm, list));
		} else if ("eco".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceEco(ecrm, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * ECRM과 관련된 ECO
	 */
	private Object referenceEco(ECRMRequest ecrm, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecrm, "eco", EcrmToEcoLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			EcoColumn dto = new EcoColumn(eco);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECRM과 관련된 CR
	 */
	private Object referenceCr(ECRMRequest ecrm, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecrm, "cr", EcrmToCrLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest cr = (EChangeRequest) result.nextElement();
			CrColumn dto = new CrColumn(cr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECRM과 관련된 DOC
	 */
	private Object referenceDoc(ECRMRequest ecrm, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecrm, "doc", EcrmToDocumentLink.class);
		while (result.hasMoreElements()) {
			WTDocument doc = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECRM과 관련된 제품
	 */
	private Object referenceCode(ECRMRequest ecrm, ArrayList<Map<String, Object>> list) throws Exception {

		String[] codes = ecrm.getModel() != null ? ecrm.getModel().split(",") : null;

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

	/**
	 * 내가 작성하고 승인되지 않은 ECRM
	 */
	public QueryResult getMyEcrm(String oid) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECRMRequest.class, true);
		QuerySpecUtils.toCreatorQuery(query, idx, ECRMRequest.class, oid);
		QuerySpecUtils.toNotEqualsAnd(query, idx, ECRMRequest.class, "state.state", "APPROVED");
		QuerySpecUtils.toOrderBy(query, idx, ECRMRequest.class, ECRMRequest.CREATE_TIMESTAMP, true);
		return PagingSessionHelper.openPagingSession(0, 5, query);
	}

	/**
	 * 일괄 다운로드
	 */
	public Map<String, Object> download(String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		String today = DateUtil.getToDay();
		String id = user.getName();

		String path = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "pdm" + File.separator
				+ today + File.separator + id;

		File ff = new File(path);
		if (!ff.exists()) {
			ff.mkdirs();
		}

		ECRMRequest ecrm = (ECRMRequest) CommonUtil.getObject(oid);

		QueryResult qr = ContentHelper.service.getContentsByRole(ecrm, ContentRoleType.PRIMARY);
		while (qr.hasMoreElements()) {
			ApplicationData dd = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
			String name = dd.getFileName();
			File file = new File(path + File.separator + name);
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		qr.reset();
		qr = ContentHelper.service.getContentsByRole(ecrm, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData dd = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
			String name = dd.getFileName();
			File file = new File(path + File.separator + name);
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		String nn = "ECRM-" + id + ".zip";

		ZipUtil.compress(today + File.separator + id, nn);

		File[] fs = ff.listFiles();
		for (File f : fs) {
			f.delete();
			System.out.println("파일 삭제!");
		}
		result.put("name", nn);
		DownloadHistoryHelper.service.create(oid, nn, "ECRM 첨부파일 일괄 다운로드");
		return result;
	}
}
