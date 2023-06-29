package com.e3ps.migration.beans;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.service.GroupwareHelper;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class MigrationECOHlper implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static MigrationECOHlper manager = new MigrationECOHlper();
	
	public static void main(String[] args) {
		//System.out.println(" START ");
		MigrationECOHlper.manager.setApproved("");
		//System.out.println(" END ");
	}
	
	public void setApproved(String date){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{String.class};
            Object args[] = new Object[]{date};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "setApproved",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		
		try{
			//QuerySpec query = getECOQuery(request);
			QuerySpec qs = new QuerySpec();
			Class ecoClass = EChangeOrder.class;
			int ecoIdx = qs.appendClassList(ecoClass, true);
			
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LIFE_CYCLE_STATE, SearchCondition.EQUAL , "APPROVED"), new int[] {ecoIdx});
			
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			while(rt.hasMoreElements()){
				Object[] o = (Object[]) rt.nextElement();
				EChangeOrder eco = (EChangeOrder) o[0];
				
				List<Map<String,Object>> appList = null;
				String approverDate = "";
				String ecoOid = CommonUtil.getOIDString(eco);
				try {
					appList = GroupwareHelper.service.getApprovalList(ecoOid);
					for (int i = 0; i < appList.size(); i++) {
						Map<String,Object> map = appList.get(i);
						String tmp = StringUtil.checkNull(String.valueOf(map.get("approveDate")));
						if(tmp.length()>0){
							approverDate = tmp;
							approverDate = DateUtil.subString(approverDate, 0, 10);
						}
					}
				} catch(Exception e) {
					appList = new ArrayList<Map<String,Object>>();
				}
				//System.out.println(eco.getEoNumber() +":" +approverDate);
				eco.setEoApproveDate(approverDate);
				PersistenceHelper.manager.modify(eco);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
