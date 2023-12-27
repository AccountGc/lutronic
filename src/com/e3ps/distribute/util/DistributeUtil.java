package com.e3ps.distribute.util;

import java.util.Enumeration;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.service.UserHelper;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;



public class DistributeUtil {
	
	public static final String DistributeGropuInner = "DistributeInner";
	public static final String DistributeGropuOuter = "DistributeOuter";
	public static final int Distribute_Inner = 1;		//배포 그룸
	public static final int Distribute_Double = 2;		//배포 그룹 및 내부 그룹
	public static final int Distribute_Defalut = 3;     // 내부 그룹만
	public static final String Session_DistributeType = "DistributeType";
	
	
	public static int distributeInnerType(){
		
		
		WTUser user = null;
		try {
			user = (WTUser)SessionHelper.getPrincipal();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distributeInnerType(user);
		
	}
	/**
	 * 배포 그룹 구분
	 * @param user
	 * @return
	 */
	public static int distributeInnerType(WTUser user){
		
		boolean isDistribute = false;
		boolean isInner = false;
		int distributeType = 3; 
		boolean isAdmin = false;
		try{
			
			// System.out.println("============ " +  user.getDomainRef().getName());
			
				if(CommonUtil.isAdmin()){
					return 2;
				}
				
				
				Enumeration en = user.parentGroups(false);
		        while (en.hasMoreElements ()) {
		        	
		        	Object ob = (Object)en.nextElement();
		        	
		        	if(ob instanceof WTOrganization){
		        		continue;
		        	}
		        	WTPrincipalReference aa = (WTPrincipalReference)ob;
		        	WTGroup group = (WTGroup)aa.getPrincipal();
		        	/*
		        	if(group.getBusinessType().equals("WTOrganization")){
		        		continue;
		        	}
		        	*/
		        	if(!group.getDomainRef().equals(user.getDomainRef())){
		        		continue;
		        	}
		        	//System.out.println(group.getName() + ","+group.getBusinessType()+","+group.getType() +","+group.getDomainRef().getName() +","+group.getContainerPath());
		        	
		        	/*
		        	if(group.getName().equals("DistributeInner")){
		        		isDistribute = true; 
		        	}else{
		        		isInner = true;
		        	}
		        	*/
		        	if(group.getBusinessType().equals("WTGroup")){	
		        		
		        		if(group.getName().equals("DistributeInner")){
		        			//System.out.println("CCC = "+group.getName() + ","+group.getBusinessType()+","+group.getType() +","+group.getDomainRef().getName() +","+group.getContainerPath());
		        			isDistribute = CommonUtil.isMember("DistributeInner",user);
		        		}else{
		        			//System.out.println("EEE = "+group.getName() + ","+group.getBusinessType()+","+group.getType() +","+group.getDomainRef().getName() +","+group.getContainerPath());

		        			isInner = true;
		        		}
		        	}
		        	/*
		            String st = (String) en.nextElement ();
		            
		            System.out.println("st =" + st);
		            if (st.equals ( DistributeGropuInner )) {
		            	
		            }else{
		            	w
		            }
		            */
		           
		        }
		        //System.out.println("isDistribute =" + isDistribute +", isInner="+ isInner);
		      if(isDistribute && isInner){
		    	  distributeType = 2;
		      }else if(isDistribute && !isInner){
		    	  distributeType = 1;
		      }else {
		    	  distributeType = 3;
		      }
		     
		     // System.out.println("distributeType ="+distributeType);
		}catch(Exception e){
			e.printStackTrace();
		}
	    return distributeType; 
	}
}
