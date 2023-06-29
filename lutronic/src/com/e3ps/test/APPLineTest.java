package com.e3ps.test;

import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.workflow.work.WorkItem;

import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;

public class APPLineTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String workItemOid ="wt.workflow.work.WorkItem:8615021";
		
		deleteAppline(workItemOid);
	}
	
	public static void deleteAppline(String workItemOid){
		
		try{
			WorkItem workItem = (WorkItem)CommonUtil.getObject(workItemOid);
			WTObject obj = (WTObject)workItem.getPrimaryBusinessObject().getObject();
			WFItem wfItem =  WFItemHelper.service.getWFItem(obj);
			
			Config conf = ConfigImpl.getInstance();
	        String activity = conf.getString("process.route.type.0"); //기안
			
			Vector<WFItemUserLink> vec = WFItemHelper.service.getAppline(wfItem, false, "", ""); //결재라인 사용중인 link
			for(int i = 0 ; i < vec.size() ; i++ ){
				WFItemUserLink link = vec.get(i);
				if(link.getActivityName().equals(activity)) continue;
				
				PersistenceHelper.manager.delete(link);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
