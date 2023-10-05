package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;

import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class ActivityHelper {

	public static final ActivityService service = ServiceFactory.getService(ActivityService.class);
	public static final ActivityHelper manager = new ActivityHelper();

	/**
	 * 루트 별 설계변경 활동 리스트
	 */
	public ArrayList<DefDTO> root() throws Exception {
		ArrayList<DefDTO> list = new ArrayList<DefDTO>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinitionRoot.class, true);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinitionRoot.class,
				EChangeActivityDefinitionRoot.SORT_NUMBER, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) o[0];
			DefDTO dto = new DefDTO(def);
			list.add(dto);
		}
		return list;
	}

	/**
	 * 설계변경 활동 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ActDTO> list = new ArrayList<>();
		String root = (String) params.get("root");

		EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(root);

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);

		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", def);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinition.class, EChangeActivityDefinition.SORT_NUMBER,
				false);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivityDefinition act = (EChangeActivityDefinition) obj[0];
			ActDTO dto = new ActDTO(act);
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

	/**
	 * 설변 활동 담기
	 */
	public Map<String, String> getActMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ORDER_NUMBER", "진채번");
		map.put("REVISE_BOM", "개정/BOM 변경");
		map.put("DOCUMENT", "산출물 등록");
		return map;
	}

	/**
	 * 설변 활동 타입에 맞는 설변타입명 반환
	 */
	public String getActName(String act) throws Exception {
		Map<String, String> map = getActMap();
		return map.get(act);
	}
}
