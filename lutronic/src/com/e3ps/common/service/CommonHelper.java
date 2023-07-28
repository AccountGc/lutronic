package com.e3ps.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.services.ServiceFactory;

public class CommonHelper {
	public static final CommonService service = ServiceFactory.getService(CommonService.class);
	public static final CommonHelper manager = new CommonHelper();
	
	public Map<String,String> getAttributes(String oid, String mode) throws Exception {
    	IBAHolder iba = (IBAHolder)CommonUtil.getObject(oid);
    	
    	//IBA Attribute
    	String specification = StringUtil.checkNull(IBAUtil.getAttrValue2(iba, AttributeKey.IBAKey.IBA_SPECIFICATION));
    	String weight = StringUtil.checkNull(IBAUtil.getAttrfloatValue(iba, AttributeKey.IBAKey.IBA_WEIGHT));
    	String manufacture = "";
    	//System.out.println("oid ="+oid+"\tCheck ="+(oid.toUpperCase().contains("PART")));
    	if(oid.toUpperCase().contains("PART"))
    		manufacture = StringUtil.checkNull(IBAUtil.getAttrValue_Part_Desc(iba, AttributeKey.IBAKey.IBA_MANUFACTURE));
    	else
            manufacture = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MANUFACTURE));
    	String mat = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MAT));
    	String finish = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_FINISH));
    	String remarks = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_REMARKS));
    	String deptcode = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DEPTCODE));
    	String model = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MODEL));
    	String productmethod = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_PRODUCTMETHOD));
		String interalnumber = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_INTERALNUMBER));
		String preseration = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_PRESERATION));
    	String moldtype = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDTYPE));
    	String moldNumber = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDNUMBER));
    	String moldCost = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDCOST));
    	String writer = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DSGN));
    	
    	String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_ECONO));
    	String ecoDate = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_ECODATE));
    	String chk = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHK));
    	String apr = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_APR));
    	String rev = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_REV));
    	String des = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DES));
    	String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHANGENO));
    	String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHANGEDATE));
    	
    	String unit = "";
    	if(iba instanceof WTPart) {
    		WTPart part = (WTPart)iba;
    		unit = part.getDefaultUnit().toString();
    	}else if(iba instanceof EPMDocument) {
    		//unit = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.EPMKey.IBA_UNIT));
    		EPMDocument epm = (EPMDocument)iba;
    		unit = epm.getDefaultUnit().toString();
    	}
    	
    	Map<String,String> map = new HashMap<String,String>();
    	
    	map.put("specification", specification);
    	map.put("weight", weight);
    	map.put("unit", unit);
    	map.put("remarks", remarks);
    	map.put("interalnumber", interalnumber);
    	map.put("moldnumber", moldNumber);
    	map.put("moldcost", moldCost);
    	map.put("writer", writer);
    	
    	map.put("ecoNo", ecoNo);
    	map.put("ecoDate", ecoDate);
    	map.put("chk", chk);
    	map.put("apr", apr);
    	map.put("rev", rev);
    	map.put("des", des);
    	map.put("changeNo", changeNo);
    	map.put("changeDate", changeDate);
    	
    	if("view".equals(mode)) {
    		if(iba instanceof EPMDocument) {
    	    	map.put("finish", finish);
    	    	map.put("mat", mat);
    		}else {
    			map.put("finish", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_FINISH, finish));
    			map.put("mat", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MAT, mat));
    		}
    		map.put("model", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MODEL, model));
	    	map.put("manufacture", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture));
	    	map.put("productmethod", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod));
	    	map.put("deptcode", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_DEPTCODE, deptcode));
	    	map.put("preseration", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_PRESERATION, preseration));
	    	map.put("moldtype", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MOLDTYPE, moldtype));
    	}else {
    		map.put("model", model);
	    	map.put("finish", finish);
	    	map.put("mat", mat);
	    	map.put("manufacture", manufacture);
	    	map.put("productmethod", productmethod);
	    	map.put("deptcode", deptcode);
	    	map.put("preseration", preseration);
	    	map.put("moldtype", moldtype);
    	}
    	
    	return map;
    }
	
	/**
	 * 관리자 속성 가져오기
	 */
	public JSONArray include_adminAttribute(String oid, String module) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			
			Map<String,String> map = new HashMap<String,String>();
			
			Object object = CommonUtil.getObject(oid);
			
			HashMap has = null;
			if("part".equals(module)) {
				has = IBAUtil.getAttributes((WTPart)object);
			}else if("drawing".equals(module)) {
				has = IBAUtil.getAttributes((EPMDocument)object);
			}else if("doc".equals(module)) {
				has = IBAUtil.getAttributes((WTDocument)object);
			}
			
			String temp = has.toString();
			StringTokenizer tokens = new StringTokenizer(temp,", ");
			
			while(tokens.hasMoreTokens()){
				String badT = (String)tokens.nextToken();
				map.put("badT", badT);
				
				list.add(map);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return JSONArray.fromObject(list);
	}
}
