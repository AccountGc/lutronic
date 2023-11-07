package com.e3ps.temprary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.temprary.column.TempraryColumn;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.IteratedFolderMemberLink;
import wt.lifecycle.LifeCycleManaged;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class TempraryHelper {

	public static final TempraryService service = ServiceFactory.getService(TempraryService.class);
	public static final TempraryHelper manager = new TempraryHelper();

	/**
	 * 임시저장함 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<TempraryColumn> list = new ArrayList<TempraryColumn>();
		ReferenceFactory rf = new ReferenceFactory();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(LifeCycleManaged.class, true);
		
		String number = StringUtil.checkNull((String) params.get("number"));
		String name = StringUtil.checkNull((String) params.get("name"));
		String dataType = StringUtil.checkNull((String) params.get("dataType"));
		
		
		// 단순 상태값으로만??/
		QuerySpecUtils.toState(query, idx, LifeCycleManaged.class, "TEMPRARY");
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TempraryColumn data = new TempraryColumn(obj);
			if("품목".equals(data.getDataType()) || "도면".equals(data.getDataType()) || "ROHS".equals(data.getDataType())) {
				if(CommonUtil.isLatestVersion(rf.getReference(data.getOid()).getObject())) {
					boolean check =true;
					String temNumber = data.getNumber();
					String temName = data.getName();
					String temDataType = data.getDataType();
					
					if((!"".equals(number) && temNumber.indexOf(number)>=0)) {
						check=true;
					}else if((!"".equals(number) && temNumber.indexOf(number)<0)) {
						check=false;
					}
					
					
					if(!"".equals(name) && temName.indexOf(name)>=0 && check) {
						check =true;
					}else if((!"".equals(name) && temName.indexOf(name)<0)) {
						check=false;
					}
					
					
					if(!"".equals(dataType) && temDataType.equals(dataType) && check) {
						check =true;
					}else if(!"".equals(dataType) && !temDataType.equals(dataType)) {
						check=false;
					}
					
					if(check) {
						list.add(data);
					}
					
//					list.add(data);
				}
			}else {
				boolean check =true;
				String temNumber = data.getNumber();
				String temName = data.getName();
				String temDataType = data.getDataType();
				
				if((!"".equals(number) && temNumber.indexOf(number)>=0)) {
					check=true;
				}else if((!"".equals(number) && temNumber.indexOf(number)<0)) {
					check=false;
				}
				
				
				if(!"".equals(name) && temName.indexOf(name)>=0 && check) {
					check =true;
				}else if((!"".equals(name) && temName.indexOf(name)<0)) {
					check=false;
				}
				
				
				if(!"".equals(dataType) && temDataType.equals(dataType) && check) {
					check =true;
				}else if(!"".equals(dataType) && !temDataType.equals(dataType)) {
					check=false;
				}
				
				if(check) {
					list.add(data);
				}
				
			}
			
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
