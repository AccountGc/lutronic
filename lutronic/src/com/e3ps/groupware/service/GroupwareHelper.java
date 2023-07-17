package com.e3ps.groupware.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class GroupwareHelper {
	public static final GroupwareService service = ServiceFactory.getService(GroupwareService.class);
	public static final GroupwareHelper manager = new GroupwareHelper();
	
	public Map<String, Object> listNotice(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<NoticeData> list = new ArrayList<>();
		
		QuerySpec query = new QuerySpec();
	    int idx = query.addClassList(Notice.class, true);
	    
	    try {
	    	String nameValue = StringUtil.checkNull((String) params.get("name"));
		    String creator = StringUtil.checkNull((String) params.get("creator"));

			if(nameValue != null && nameValue.trim().length() > 0){
				query.appendWhere(new SearchCondition(Notice.class, "title", SearchCondition.LIKE, "%" + nameValue.trim() + "%", false), new int[]{idx});
		    }	

			if(creator!=null && creator.length()>0) {
				ReferenceFactory rf = new ReferenceFactory();
				People people = (People)rf.getReference(creator).getObject();
				WTUser user = people.getUser();

				if(query.getConditionCount()>0)query.appendAnd();
				query.appendWhere(new SearchCondition(Notice.class,"owner.key","=", PersistenceHelper.getObjectIdentifier( user )), new int[]{idx});
			}

			query.appendOrderBy(new OrderBy(new ClassAttribute(Notice.class,"thePersistInfo.createStamp"), true), new int[] { idx });
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				NoticeData data = new NoticeData((Notice) obj[0]);
				list.add(data);
			}

			map.put("list", list);
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
	    
	}
}
