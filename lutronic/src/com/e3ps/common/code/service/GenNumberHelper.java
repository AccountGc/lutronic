package com.e3ps.common.code.service;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class GenNumberHelper {
	public static final GenNumberService service = ServiceFactory.getService(GenNumberService.class);
	public static final GenNumberHelper manager = new GenNumberHelper();
	
	public boolean checkCode(String codeType,String parentOid, String code) throws Exception {
		boolean bool = false;
        QuerySpec select = new QuerySpec(NumberCode.class);
        select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
        long longOid =0;
        if(parentOid.length()>0){
        	longOid = CommonUtil.getOIDLongValue(parentOid);
        	select.appendAnd();
            select.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", "=", longOid), new int[] { 0 });
        }else{
        	select.appendAnd();
        	select.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", "=", longOid), new int[] { 0 });
        }
        
        select.appendAnd();
        select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
        QueryResult result = PersistenceHelper.manager.find(select);
        //System.out.println(select);
        if (result.hasMoreElements())
        {
            //return true;
        	bool = true;
        }
            
        return bool;
    }
}
