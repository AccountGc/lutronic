package com.e3ps.admin.form.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.admin.form.dto.FormTemplateDTO;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;

import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class FormTemplateHelper {

	public static final FormTemplateService service = ServiceFactory.getService(FormTemplateService.class);
	public static final FormTemplateHelper manager = new FormTemplateHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<FormTemplateDTO> list = new ArrayList<>();

		String name = (String) params.get("name");
		String formType = (String) params.get("formType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FormTemplate.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, FormTemplate.class, FormTemplate.NAME, name);
		QuerySpecUtils.toEqualsAnd(query, idx, FormTemplate.class, FormTemplate.FORM_TYPE, formType);

		QuerySpecUtils.toOrderBy(query, idx, FormTemplate.class, FormTemplate.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			FormTemplate formTemplate = (FormTemplate) obj[0];
			FormTemplateDTO data = new FormTemplateDTO(formTemplate);
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

}
