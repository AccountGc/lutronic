package com.e3ps;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.beans.UserHelper;

import wt.org.WTUser;

public class UserSync {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("test1");
			try{
				String oid = args[0];
				WTUser user = (WTUser)CommonUtil.getObject(oid);
				com.e3ps.org.service.UserHelper.service.syncSave(user);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//Windchill com.e3ps.UserSync wt.org.WTUser:156326
			//System.out.println("test2");
	}

}
