
package com.e3ps.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.admin.dto.MailUserDTO;
import com.e3ps.change.EChangeActivityDefinition;
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
import com.e3ps.download.dto.DownloadDTO;
import com.e3ps.org.MailUser;

import wt.fc.PagingQueryResult;
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
	 * 다운로드 이력 리스트
	 */
	public Map<String, Object> downLoadHistory(Map<String, Object> params) throws Exception {
		String type = StringUtil.checkNull((String) params.get("type"));
		String userId = StringUtil.checkNull((String) params.get("managerOid"));
		String predate = StringUtil.checkNull((String) params.get("createdFrom"));
		String postdate = StringUtil.checkNull((String) params.get("createdTo"));

		QuerySpec qs = new QuerySpec();

		int idx = qs.appendClassList(DownloadHistory.class, true);

		if (type != null && type.trim().length() > 0) {
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "persistReference.key.classname",
					SearchCondition.LIKE, "%" + type + "%"), new int[] { idx });
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

	/**
	 * 외부 메일 리스트
	 */
	public Map<String, Object> mail(Map<String, Object> params) throws Exception {
		List<MailUserDTO> list = new ArrayList<MailUserDTO>();
		Map<String, Object> map = new HashMap<String, Object>();
		String name = (String) params.get("name");
		String email = (String) params.get("email");
		boolean enable = (boolean) params.get("enable");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(MailUser.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, MailUser.class, MailUser.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, MailUser.class, MailUser.EMAIL, email);
		QuerySpecUtils.toBooleanAnd(query, idx, MailUser.class, MailUser.IS_DISABLE, enable);
		QuerySpecUtils.toOrderBy(query, idx, MailUser.class, MailUser.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MailUserDTO dto = new MailUserDTO(obj);
			dto.setRowNum(rowNum++);
			list.add(dto);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
