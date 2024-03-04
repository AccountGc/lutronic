package com.e3ps.change.cr.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.CrToDocumentLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.ZipUtil;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.rohs.ROHSMaterial;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSession;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

public class CrHelper {

	public static final CrService service = ServiceFactory.getService(CrService.class);
	public static final CrHelper manager = new CrHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("CR 쿼리 시작 = " + start);
		Map<String, Object> map = new HashMap<>();
		ArrayList<CrColumn> list = new ArrayList<>();

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
		int idx = query.appendClassList(EChangeRequest.class, true);
		// 제목
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.EO_NAME, name);
		// 번호
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.EO_NUMBER, number);
		// 상태
		QuerySpecUtils.toState(query, idx, EChangeRequest.class, state);
		// 등록자
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeRequest.class, creator);
		// 등록일
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeRequest.class, EChangeRequest.CREATE_TIMESTAMP,
				createdFrom, createdTo);
		// 승인일
		if (approveFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeRequest.class, EChangeRequest.APPROVE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, approveFrom), new int[] { idx });
		}
		if (approveTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeRequest.class, EChangeRequest.APPROVE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, approveTo), new int[] { idx });
		}
		// 작성자
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.WRITER, writer);
		// 작성부서
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.CREATE_DEPART, createDepart);

		// 작성일
		if (writedFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeRequest.class, EChangeRequest.CREATE_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, writedFrom), new int[] { idx });
		}
		if (writedTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EChangeRequest.class, EChangeRequest.CREATE_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, writedTo), new int[] { idx });
		}
		// 제안자
//		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.PROPOSER, proposer);

		// 프로젝트 코드
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.MODEL, model);

		// 변경구분
		QuerySpecUtils.toLikeAnd(query, idx, EChangeRequest.class, EChangeRequest.CHANGE_SECTION, changeSection);

		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, EChangeRequest.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CrColumn data = new CrColumn(obj);
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
		System.out.println("CR 쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	private String toSortKey(String sortKey) throws Exception {
		if ("number".equals(sortKey)) {
			return EChangeRequest.EO_NUMBER;
		} else if ("name".equals(sortKey)) {
			return EChangeRequest.EO_NAME;
		} else if ("changeSection".equals(sortKey)) {
			return EChangeRequest.CHANGE_SECTION;
		} else if ("createDepart".equals(sortKey)) {
			return EChangeRequest.CREATE_DEPART;
		} else if ("writer".equals(sortKey)) {
			return EChangeRequest.WRITER;
		} else if ("writeDate".equals(sortKey)) {
			return EChangeRequest.CREATE_DATE;
		} else if ("state".equals(sortKey)) {
			return EChangeRequest.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return EChangeRequest.CREATOR_FULL_NAME;
		} else if ("createdDate".equals(sortKey)) {
			return EChangeRequest.CREATE_TIMESTAMP;
		} else if ("approveDate".equals(sortKey)) {
			return EChangeRequest.APPROVE_DATE;
		}

		return EChangeRequest.CREATE_TIMESTAMP;
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
						display += s + " [<font color='red'><b>" + n.getName() + "</b></font>]";
					}
				} else {
					NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
					if (n != null) {
						display += s + " [<font color='red'><b>" + n.getName() + "</b></font>], ";
					}
				}
			}
		}
		return display;
	}

	/**
	 * 변경구분 복수개로 인해서 처리 하는 함수
	 */
	public String displayToSection(String section) throws Exception {
		String display = "";
		if (section != null) {
			String[] ss = section.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION");
				} else {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION") + ",";
				}
			}
		}
		return display;
	}

	/**
	 * 작성부서 코드 -> 값
	 */
	public String displayToDept(String dept) throws Exception {
		if (dept == null) {
			return "";
		}
		return NumberCodeHelper.manager.getNumberCodeName(dept, "DEPTCODE");
	}

	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);
		if ("cr".equalsIgnoreCase(type)) {
			// CR
			return JSONArray.fromObject(referenceCr(cr, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			// 제품명
			return JSONArray.fromObject(referenceCode(cr, list));
		} else if ("eco".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceEco(cr, list));
		} else if ("doc".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceDoc(cr, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * CR과 돤련된 문서
	 */
	private Object referenceDoc(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "doc", CrToDocumentLink.class);
		while (result.hasMoreElements()) {
			WTDocument doc = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * CR과 돤련된 ECO
	 */
	private Object referenceEco(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "eco", RequestOrderLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			EcoColumn dto = new EcoColumn(eco);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * CR과 관련된 제품
	 */
	private Object referenceCode(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {

		String[] codes = cr.getModel() != null ? cr.getModel().split(",") : null;

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
	 * CR과 돤련된 CR
	 */
	private Object referenceCr(EChangeRequest cr, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "useBy", EcrToEcrLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest doc = (EChangeRequest) result.nextElement();
			CrColumn dto = new CrColumn(doc);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * CR 다음번호
	 */
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("000");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeRequest.class, true);
		SearchCondition sc = new SearchCondition(EChangeRequest.class, EChangeRequest.EO_NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, EChangeRequest.class, EChangeRequest.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// E2312N45
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EChangeRequest cr = (EChangeRequest) obj[0];
			String crNumber = cr.getEoNumber();
			String next = crNumber.substring(crNumber.length() - 3);
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "001";
		}
		return rtn;
	}

	/**
	 * 차트 드릴다운 첫번째
	 */
	public Map<String, Integer> getDrill() throws Exception {
		Map<String, Integer> result = new HashMap<>();

//		String[] ss = new String[] { "영업/마케팅", "원가 절감", "기능/성능 변경", "공정 변경", "자재 변경", "허가/규제 변경", "품질 개선", "라벨링",
//				"기타" };

		int market = 0;
		int cost = 0;
		int perform = 0;
		int line = 0;
		int mat = 0;
		int permission = 0;
		int product = 0;
		int label = 0;
		int etc = 0;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeRequest.class, true);
		QueryResult rs = PersistenceHelper.manager.find(query);
		while (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			EChangeRequest cr = (EChangeRequest) obj[0];
			String a = cr.getChangeSection();
			// 구데잍..
			if (StringUtil.checkString(a)) {
				String[] t = a.split(",");
				for (String tt : t) {
					if (tt.equals("영업/마케팅")) {
						++market;
						result.put("영업/마케팅", market);
					} else if (tt.equals("원가 절감")) {
						++cost;
						result.put("영업/마케팅", cost);
					} else if (tt.equals("기능/성능 변경")) {
						++perform;
						result.put("기능/성능 변경", perform);
					} else if (tt.equals("공정 변경")) {
						++line;
						result.put("공정 변경", line);
					} else if (tt.equals("자재 변경")) {
						++mat;
						result.put("자재 변경", mat);
					} else if (tt.equals("허가/규제 변경")) {
						++permission;
						result.put("허가/규제 변경", permission);
					} else if (tt.equals("품질 개선")) {
						++product;
						result.put("품질 개선", product);
					} else if (tt.equals("라벨링")) {
						++label;
						result.put("라벨링", label);
					} else if (tt.equals("기타")) {
						System.out.println("tt=" + tt);
						++etc;
						result.put("기타", etc);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 내가 작성하고 승인됨 아닌 CR
	 */
	public QueryResult getMyCr(String oid) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeRequest.class, true);
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeRequest.class, oid);
		QuerySpecUtils.toNotEqualsAnd(query, idx, EChangeRequest.class, "state.state", "APPROVED");
		QuerySpecUtils.toOrderBy(query, idx, EChangeRequest.class, EChangeRequest.CREATE_TIMESTAMP, true);
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

		EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);

		QueryResult qr = ContentHelper.service.getContentsByRole(cr, ContentRoleType.PRIMARY);
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
		qr = ContentHelper.service.getContentsByRole(cr, ContentRoleType.SECONDARY);
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
		qr = ContentHelper.service.getContentsByRole(cr, ContentRoleType.toContentRoleType("ECR"));
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

		String nn = "CR-" + id + ".zip";

		ZipUtil.compress(today + File.separator + id, nn);

		File[] fs = ff.listFiles();
		for (File f : fs) {
			f.delete();
			System.out.println("파일 삭제!");
		}
		result.put("name", nn);
		return result;
	}
}
