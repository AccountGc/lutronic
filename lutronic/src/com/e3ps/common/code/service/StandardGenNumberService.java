package com.e3ps.common.code.service;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.util.CommonUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardGenNumberService extends StandardManager implements GenNumberService {

	public static StandardGenNumberService newStandardGenNumberService() throws Exception {
		final StandardGenNumberService instance = new StandardGenNumberService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public boolean checkCode(String codeType, String code) {
		
        return checkCode(codeType,"",code);
    }
	
	@Override
	public boolean checkCode(String codeType,String parentOid, String code) {
		boolean bool = false;
        try
        {	//NumberCodeType NCodeType = NumberCodeType.toNumberCodeType(codeType);
        	//boolean isTree = NCodeType.getAbbreviatedDisplay().equals("true") ? true : false;
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
            
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        return bool;
    }
}
