package com.e3ps.test;

import java.util.Enumeration;

import com.e3ps.org.People;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.query.QuerySpec;

public class UserToGroup {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//UserTOGroupList();
		test();
	}
	
	public static void test(){
		
		String aa[] = new String[100]; 
		
		aa[0] ="aaaaa";
		aa[1] ="bbbb";
		for(int j = 0 ;  j < 10 ; j++){
			for(int i = 0 ; i < aa.length ; i++){
				if(aa[i] == null ){
					break;
				}
				//System.out.println(j+" . " +i +","+aa[i]);
			}
		}
		
		
	}
	
	public static void UserTOGroupList(){
		
		try{
			
			QuerySpec qs= new QuerySpec();
			Class cls = People.class;
			qs.addClassList(cls, true);
			
			QueryResult rt =PersistenceHelper.manager.find(qs);
			
			int i = 1; 
			while(rt.hasMoreElements()){
				Object[] ob = (Object[])rt.nextElement();
				
				People pp = (People)ob[0];
				WTUser user = pp.getUser();
				
					Enumeration en = user.parentGroups(false);
			        while (en.hasMoreElements ()) {
			        	
			        	Object ob2 = (Object)en.nextElement();
			        	WTPrincipalReference aa = (WTPrincipalReference)ob2;
			        	WTGroup group = (WTGroup)aa.getPrincipal();
			        	
			        	if(ob2 instanceof WTOrganization){
			        		continue;
			        	}
			        	
			        	if(!group.getDomainRef().equals(user.getDomainRef())){
			        		continue;
			        	}
			        	
			        	if(group.getBusinessType().equals("WTGroup")){	
			        		//System.out.println(i+","+pp.getName() +","+group.getName());
			        	}
			        	
			        }
				
				
				i++;
				//System.out.println((i++)+", "+pp.getName() +","+pp.getUser());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
