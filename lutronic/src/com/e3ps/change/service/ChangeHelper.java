package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.List;

import com.e3ps.change.beans.ECOData;
import com.e3ps.common.util.StringUtil;

import net.sf.json.JSONArray;
import wt.services.ServiceFactory;

public class ChangeHelper {
	public static final ChangeService service = ServiceFactory.getService(ChangeService.class);
	public static final ChangeHelper manager = new ChangeHelper();
	
	public JSONArray include_ECOList(String oid, String moduleType) throws Exception {
    	List<ECOData> list = new ArrayList<ECOData>();
    	try {
    		if(StringUtil.checkString(oid)){
        		if("doc".equals(moduleType)) {
            		List<ECOData> dataList = ECOSearchHelper.service.getECOListToLinkRoleName(oid, "used");
            		for(ECOData data : dataList) {
            			list.add(data);
            		}
            	}
        	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return JSONArray.fromObject(list);
    }
}
