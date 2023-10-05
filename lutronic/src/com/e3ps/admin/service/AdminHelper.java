
package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ROOTData;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.service.GenNumberHelper;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.beans.DownloadDTO;
import com.e3ps.org.MailUser;

import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class AdminHelper {
	public static final AdminService service = ServiceFactory.getService(AdminService.class);
	public static final AdminHelper manager = new AdminHelper();

	/**
	 * 설계 변경 관리 리스트
	 */
	public Map<String, Object> changeActivityList(Map<String, Object> params) throws Exception {
		ArrayList<EADData> list = new ArrayList<>();

		String rootOid = StringUtil.checkNull((String) params.get("rootOid"));
		if (rootOid.length() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", list);
			map.put("pageSize", 30);
			map.put("total", 0);
			map.put("curPage", 1);
			map.put("topListCount", 0);
			return map;
		}
		long logRootOid = CommonUtil.getOIDLongValue(rootOid);

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);

		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", logRootOid);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinition.class, EChangeActivityDefinition.SORT_NUMBER,
				false);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EADData data = new EADData((EChangeActivityDefinition) o[0]);
			list.add(data);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 외부 메일 리스트
	 */
	public Map<String, Object> adminMail(Map<String, Object> params) throws Exception {
		String oid = StringUtil.checkNull((String) params.get("oid"));
		String name = StringUtil.checkNull((String) params.get("name"));
		String email = StringUtil.checkNull((String) params.get("email"));
		boolean enable = params.get("enable").equals("true") ? true : false;

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(MailUser.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, MailUser.class, MailUser.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, MailUser.class, MailUser.EMAIL, email);
		QuerySpecUtils.toBooleanAnd(query, idx, MailUser.class, MailUser.IS_DISABLE, enable);
		QuerySpecUtils.toOrderBy(query, idx, MailUser.class, MailUser.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			MailUser user = (MailUser) o[0];

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("oid", user.getPersistInfo().getObjectIdentifier().toString());
			map.put("name", user.getName());
			map.put("email", user.getEmail());
			map.put("enable", user.isIsDisable());
			list.add(map);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 접속 이력 리스트
	 */
	public Map<String, Object> loginHistory(Map<String, Object> params) throws Exception {
		String userName = (String) params.get("userName");
		String userId = (String) params.get("userId");

		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(LoginHistory.class, true);

		if (userName != null && userName.trim().length() > 0) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(LoginHistory.class, "name", SearchCondition.LIKE, "%" + userName + "%"),
					new int[] { idx });
		}

		if (userId != null && userId.trim().length() > 0) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(LoginHistory.class, "id", SearchCondition.LIKE, "%" + userId + "%"),
					new int[] { idx });
		}

		qs.appendOrderBy(new OrderBy(new ClassAttribute(LoginHistory.class, "thePersistInfo.createStamp"), true),
				new int[] { idx });

		PageQueryUtils pager = new PageQueryUtils(params, qs);
		PagingQueryResult result = pager.find();

//	    long querystart = System.currentTimeMillis();
//	    qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
//	    long queryend = System.currentTimeMillis();
//	    System.out.println("queryResult " + (queryend-querystart));

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			LoginHistory history = (LoginHistory) o[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("oid", history.getPersistInfo().getObjectIdentifier().toString());
			map.put("name", history.getName());
			map.put("id", history.getId());
			map.put("createDate", DateUtil.getDateString(history.getPersistInfo().getCreateStamp(), "a"));
			list.add(map);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 다운로드 이력 리스트
	 */
	public Map<String, Object> downLoadHistory(Map<String, Object> params) throws Exception {
		String type = StringUtil.checkNull((String) params.get("type"));
		String userId = StringUtil.checkNull((String) params.get("manager"));
		String predate = StringUtil.checkNull((String) params.get("createdFrom"));
		String postdate = StringUtil.checkNull((String) params.get("createdTo"));

		QuerySpec qs = new QuerySpec();

		int idx = qs.appendClassList(DownloadHistory.class, true);

		if (type != null && type.trim().length() > 0) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, DownloadHistory.D_OID, SearchCondition.LIKE,
					"%" + type + "%"), new int[] { idx });
		}

		if (userId.length() > 0) {
			WTUser user = (WTUser) CommonUtil.getObject(userId);
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "userReference.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(user)), new int[] { idx });
		}

		if (predate.length() > 0) {
			if (qs.getConditionCount() > 0) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.updateStamp",
					SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
		}

		if (postdate.length() > 0) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.updateStamp",
					SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
		}
		qs.appendOrderBy(new OrderBy(new ClassAttribute(DownloadHistory.class, "thePersistInfo.updateStamp"), true),
				new int[] { idx });

		PageQueryUtils pager = new PageQueryUtils(params, qs);
		PagingQueryResult result = pager.find();

		ArrayList<DownloadDTO> list = new ArrayList<DownloadDTO>();
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			DownloadHistory history = (DownloadHistory) obj[0];
			DownloadDTO data = new DownloadDTO(history);
			list.add(data);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 코드 중복 확인
	 */
	public Map<String, Object> codeCheck(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addList = (ArrayList<Map<String, Object>>) params.get("addRow");
		Map<String, Object> result = new HashMap<String, Object>();
		if (addList.size() > 0) {
			for (Map<String, Object> map : addList) {
				String codeType = (String) map.get("codeType");
				String parentOid = StringUtil.checkNull((String) map.get("parentOid"));
				String code = (String) map.get("code");
				NumberCodeType ctype = NumberCodeType.toNumberCodeType(codeType);
				boolean isSeq = ctype.getShortDescription().equals("true") ? true : false;
				if (!isSeq && GenNumberHelper.manager.checkCode(codeType, parentOid, code.toUpperCase())) {
					result.put("result", false);
					result.put("msg", Message.get("입력하신 코드가 이미(PDM) 등록되어 있습니다. 다시 확인 후 등록해 주세요."));
					return result;
				}
			}
		}
		result.put("result", true);
		return result;
	}

}
