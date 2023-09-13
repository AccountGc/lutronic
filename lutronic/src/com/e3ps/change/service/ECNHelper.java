package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.beans.ECNData;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ECNHelper {
	public static final ECNService service = ServiceFactory.getService(ECNService.class);
	public static final ECNHelper manager = new ECNHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception{
		ArrayList<ECNData> list = new ArrayList<ECNData>();
		Map<String, Object> map = new HashMap<>();
		try {
			String name = (String) params.get("name");
			String number = (String) params.get("number");
			String creator = (String) params.get("creatorOid");
			String state = (String) params.get("state");
			String createdFrom = (String) params.get("createdFrom");
			String createdTo = (String) params.get("createdTo");
			
			QuerySpec qs = new QuerySpec();
			int idx = qs.appendClassList(EChangeNotice.class, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(EChangeNotice.class, EChangeNotice.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {idx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(EChangeNotice.class, EChangeNotice.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {idx});
			}
			
			//발행일
			if(createdFrom.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(EChangeNotice.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(createdFrom)), new int[] {idx});
			}
			//발생일
			if(createdTo.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(EChangeNotice.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(createdTo)), new int[] {idx});
			}
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(EChangeNotice.class, EChangeNotice.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {idx});
			}//creator.key.id
			
			//등록자
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(EChangeNotice.class, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {idx});
			}
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeNotice.class, "thePersistInfo.updateStamp"), true), new int[] { idx });
			
			PageQueryUtils pager = new PageQueryUtils(params, qs);
			PagingQueryResult result = pager.find();
			
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ECNData data = new ECNData((EChangeNotice) obj[0]);
				list.add(data);
			}
			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
