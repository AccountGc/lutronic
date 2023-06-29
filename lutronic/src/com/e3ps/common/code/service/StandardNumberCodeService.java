package com.e3ps.common.code.service;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.download.DownloadHistory;

public class StandardNumberCodeService extends StandardManager implements NumberCodeService {

	public static StandardNumberCodeService newStandardNumberCodeService() throws Exception {
		final StandardNumberCodeService instance = new StandardNumberCodeService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public String getValue(String codeType,String code)throws Exception{
    	NumberCode nc = getNumberCode(codeType,code);
    	
    	//System.out.println("NumberCode nc : "+nc);
    	
    	if(nc==null){
    		return null;
    	}
    	return Message.getNC(nc);
    }
	
	 /**
     * Ư�� �ڵ� Ÿ�Կ� �ڵ尡 code�� NumberCode�� ��ȯ
     * 
     * @param codeType
     * @param code
     * @return
     */
	@Override
    public NumberCode getNumberCode(String codeType, String code) {
		return getNumberCode(codeType, code, "");
    }
	
	@Override
	public NumberCode getNumberCode(String codeType, String code, String parentOid) {
		if (code == null) { 
        	return null; 
        }
        
        try {
            QuerySpec select = new QuerySpec(NumberCode.class);
            select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
            select.appendAnd();
            select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
            
            if(StringUtil.checkString(parentOid)) {
	            select.appendAnd();
	            select.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", "=", CommonUtil.getOIDLongValue(parentOid)), new int[] {0});
            }
            select.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, "thePersistInfo.modifyStamp"), true), new int[] { 0 });
            //System.out.println("GetNumberCode : "+select);
            QueryResult result = PersistenceHelper.manager.find(select);
            while (result.hasMoreElements()) {
                return (NumberCode) result.nextElement();
            }
        } catch (QueryException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}
	
	
	@Override
	public NumberCode getNumberCodeFormName(String codeType, String name) {
		if (name == null) { 
        	return null; 
        }
        
        try {
            QuerySpec select = new QuerySpec(NumberCode.class);
            select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
            select.appendAnd();
            select.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, "=", name), new int[] { 0 });
            QueryResult result = PersistenceHelper.manager.find(select);
            while (result.hasMoreElements()) {
                return (NumberCode) result.nextElement();
            }
        } catch (QueryException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}
}
