package com.e3ps.test;


import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import com.e3ps.groupware.notice.Notice;

public class NoticeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	
	public static void test(){
		
		try{
			QuerySpec query = new QuerySpec();
		    int idx = query.addClassList(Notice.class, true);
			query.appendWhere(new SearchCondition(Notice.class, Notice.IS_POPUP, SearchCondition.IS_TRUE), new int[]{idx});

			//System.out.println(query.toString());
			
			QueryResult rt = PersistenceHelper.manager.find(query);
			while(rt.hasMoreElements()){
				Object[] o = (Object[]) rt.nextElement();
				Notice notice = (Notice) o[0];
				//System.out.println("notice ="+ notice.getTitle());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
