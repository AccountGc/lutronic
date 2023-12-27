package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.List;

import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.common.util.StringUtil;

import net.sf.json.JSONArray;
import wt.services.ServiceFactory;

public class ChangeHelper {
	public static final ChangeService service = ServiceFactory.getService(ChangeService.class);
	public static final ChangeHelper manager = new ChangeHelper();
	
	public JSONArray include_ECOList(String oid, String moduleType) throws Exception {
    	List<EcoDTO> list = new ArrayList<EcoDTO>();
    	try {
    		if(StringUtil.checkString(oid)){
        		if("doc".equals(moduleType)) {
            		List<EcoDTO> dataList = ECOSearchHelper.service.getECOListToLinkRoleName(oid, "used");
            		for(EcoDTO data : dataList) {
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
