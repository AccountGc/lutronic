package com.e3ps.common.code.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;

@SuppressWarnings("serial")
public class StandardCodeService extends StandardManager implements CodeService {

	public static StandardCodeService newStandardCodeService() throws Exception {
		final StandardCodeService instance = new StandardCodeService();
		instance.initialize();
		return instance;
	}

	/**
	 * 
	 * LUTRONIC 추가 시작
	 * 
	 * 
	 */

	@Override
	public List<NumberCodeDTO> numberCodeList(String codeType, String parentOid, boolean search) {
		List<NumberCodeDTO> list = new ArrayList<NumberCodeDTO>();

		QueryResult qr = numberCodeListResult(codeType, parentOid, search);
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			NumberCode code = (NumberCode) o[0];
			NumberCodeDTO data = new NumberCodeDTO(code);
			list.add(data);
		}

		return list;
	}

	@Override
	public List<NumberCodeDTO> numberParentCodeList(String codeType, String pCode, boolean search) {
		List<NumberCodeDTO> list = new ArrayList<NumberCodeDTO>();

		QueryResult qr = numberParentCodeListResult(codeType, pCode, search);
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			NumberCode code = (NumberCode) o[0];
			NumberCodeDTO data = new NumberCodeDTO(code);
			list.add(data);
		}

		return list;
	}

	private QuerySpec numberCodeListQuery(String codeType, String parentOid, boolean search) {
		QuerySpec query = null;
		try {
			long parentLongOid = 0;
			if (StringUtil.checkString(parentOid)) {
				parentLongOid = CommonUtil.getOIDLongValue(parentOid);
			}

			query = new QuerySpec();

			int idx = query.addClassList(NumberCode.class, true);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { idx });
			if (!search) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { idx });
			}
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL,
					parentLongOid), new int[] { idx });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.SORT), false),
					new int[] { idx });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.NAME), false),
					new int[] { idx });

			//// System.out.println("QuerySpec =" + query.toString());
		} catch (QueryException e) {
			e.printStackTrace();
		}
		return query;
	}

	private QuerySpec numberParentCodeListQuery(String codeType, String pCode, boolean search) {
		QuerySpec query = null;
		try {
			pCode = StringUtil.checkNull(pCode);
			query = new QuerySpec();

			int idx = query.addClassList(NumberCode.class, true);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { idx });
			if (!search) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { idx });
			}

			if (pCode.length() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE, SearchCondition.EQUAL, pCode),
						new int[] { idx });
			}

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.SORT), false),
					new int[] { idx });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.NAME), false),
					new int[] { idx });

			// System.out.println("QuerySpec =" + query.toString());
		} catch (QueryException e) {
			e.printStackTrace();
		}
		return query;
	}

	private QueryResult numberCodeListResult(String codeType, String parentOid, boolean search) {
		try {

			QuerySpec query = numberCodeListQuery(codeType, parentOid, search);

			return PersistenceHelper.manager.find((StatementSpec) query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	private QueryResult numberParentCodeListResult(String codeType, String pCode, boolean search) {
		try {

			QuerySpec query = numberParentCodeListQuery(codeType, pCode, search);

			return PersistenceHelper.manager.find((StatementSpec) query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	@Override
	public List<NumberCodeDTO> autoSearchName(String codeType, String name) {
		List<NumberCodeDTO> list = new ArrayList<NumberCodeDTO>();

		QueryResult qr = autoSearchNameResult(codeType, name);
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			NumberCode numbercode = (NumberCode) o[0];
			NumberCodeDTO data = new NumberCodeDTO(numbercode);
			list.add(data);
		}

		return list;
	}

	@Override
	public List<String> autoSearchNameRtnName(String codeType, String name) {
		List<String> list = new ArrayList<String>();

		QueryResult qr = autoSearchNameResult(codeType, name);
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			NumberCode numbercode = (NumberCode) o[0];
			NumberCodeDTO data = new NumberCodeDTO(numbercode);
			list.add(data.name);
		}

		return list;
	}

	// autoSearchNameRtnName
	private QueryResult autoSearchNameResult(String codeType, String name) {
		QuerySpec qs = null;
		QueryResult qr = null;

		try {
			qs = new QuerySpec();

			int idx = qs.addClassList(NumberCode.class, true);

			qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE_TYPE, SearchCondition.EQUAL, codeType),
					new int[] { idx });

			if (qs.getConditionCount() > 0) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, SearchCondition.LIKE,
					"%" + name + "%", false), new int[] { idx });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { idx });

			qr = PersistenceHelper.manager.find((StatementSpec) qs);

		} catch (WTException e) {
			e.printStackTrace();
			qr = new QueryResult();
		}
		return qr;
	}

	/**
	 * poupup NumberCodeList
	 */
	@Override
	public Map<String, Object> popup_numberCodeAction(HttpServletRequest request, HttpServletResponse response,
			boolean disabled) throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {

			String codeType = StringUtil.checkReplaceStr(request.getParameter("codeType"), "");
			String parentOid = StringUtil.checkReplaceStr(request.getParameter("parentOid"), "");
			String name = StringUtil.checkNull(request.getParameter("name"));
			String code = StringUtil.checkNull(request.getParameter("code"));
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			long longOid = 0;
			if (parentOid != null && parentOid.length() > 0) {
				longOid = CommonUtil.getOIDLongValue(parentOid);
				query.appendAnd();
				query.appendWhere(
						new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, longOid),
						new int[] { 0 });
			} else {
				query.appendAnd();
				query.appendWhere(
						new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, longOid),
						new int[] { 0 });
			}

			if (!disabled) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { 0 });
			}

			if (name.length() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, SearchCondition.LIKE,
						"%" + name + "%", false), new int[] { 0 });
			}

			if (code.length() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE, SearchCondition.LIKE,
						"%" + code + "%", false), new int[] { 0 });
			}

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.SORT), false),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
			// System.out.println(query);
		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");
		// System.out.println("qr.size() = "+ qr.size());
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			NumberCode ncode = (NumberCode) o[0];

			xmlBuf.append("<row id='" + ncode.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ncode.getName() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ncode.getCode() + "]]></cell>");
			xmlBuf.append(
					"<cell><![CDATA[" + StringUtil.checkReplaceStr(ncode.getDescription(), "&nbsp;") + "]]></cell>");
			xmlBuf.append("</row>");

		}
		xmlBuf.append("</rows>");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;
	}

	/**
	 * 
	 * LUTRONIC 추가 끝
	 * 
	 * 
	 */

	@Override
	public QueryResult getCode(String key) {
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			if (key.equals("CADCREATOR")) {
				query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.ENG_NAME), false),
						new int[] { 0 });
			} else {
				query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
						new int[] { 0 });
			}
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	@Override
	public NumberCode getNumberCode(String key, String code) {
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
			// query.appendAnd();
			// query.appendWhere(new SearchCondition(NumberCode.class, "disabled",
			// SearchCondition.IS_FALSE), new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode cc = (NumberCode) qr.nextElement();
				return cc;
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public List<NumberCodeDTO> topCodeToList(String key) {
		List<NumberCodeDTO> list = new ArrayList<NumberCodeDTO>();
		QueryResult qr = getTopCode(key);
		while (qr.hasMoreElements()) {
			NumberCode code = (NumberCode) qr.nextElement();
			NumberCodeDTO data = new NumberCodeDTO(code);
			list.add(data);
		}
		return list;
	}

	// 1Levle code
	@Override
	public QueryResult getTopCode(String key) {
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });

			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	@Override
	public String getName(String key, String code) {
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode cc = (NumberCode) qr.nextElement();
				return Message.getNC(cc);
			}
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	@Override
	public List<NumberCodeDTO> childNumberCodeList(String type, String parentCode) {
		List<NumberCodeDTO> list = new ArrayList<NumberCodeDTO>();

		NumberCode parent = NumberCodeHelper.service.getNumberCode(type, parentCode);

		QueryResult qr = getChildCode(type, CommonUtil.getOIDString(parent));

		while (qr.hasMoreElements()) {
			NumberCode code = (NumberCode) qr.nextElement();
			NumberCodeDTO data = new NumberCodeDTO(code);
			list.add(data);
		}

		return list;
	}

	@Override
	public QueryResult getChildCode(String key, String parentoid) {

		try {
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(parentoid)), new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}

	}

	@Override
	public HashMap<String, String> getCodeMap(String key) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			QueryResult rt = getCode(key);
			while (rt.hasMoreElements()) {
				NumberCode code = (NumberCode) rt.nextElement();
				map.put(code.getCode(), Message.getNC(code));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * NumberCode Vector return ;
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Vector<NumberCode> getCodeVec(String key) {
		Vector<NumberCode> vec = new Vector();
		try {
			QueryResult rt = getCode(key);
			while (rt.hasMoreElements()) {
				vec.add((NumberCode) rt.nextElement());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}

}
