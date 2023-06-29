package com.e3ps.test;

import java.util.Iterator;
import java.util.TreeMap;

import wt.epm.EPMDocument;
import wt.epm.util.EPMSearchHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;

import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.service.PartHelper;

public class DoubleCheck {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("START");
		doubleCheck();
		//System.out.println("END");
	}
	
	public static void doubleCheck(){
		
		
		try{
			QuerySpec qs = new QuerySpec(WTPart.class);

	        qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { 0 });
	        SearchUtil.addLastVersionCondition(qs, WTPart.class, 0);
	        
	        QueryResult qr = PersistenceHelper.manager.find(qs);
	        //System.out.println("qr.size =" + qr.size());
	        int count =1;
	        while(qr.hasMoreElements()){
	        	Object[] o = (Object[])qr.nextElement();
	        	
	        	WTPart part = (WTPart)o[0];
	        	String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue(part, IBAKey.IBA_CHANGEDATE));
	        	String changeNO  = StringUtil.checkNull(IBAUtil.getAttrValue(part, IBAKey.IBA_CHANGENO));
	        	
	        	//System.out.println("조건 없음"+part.getNumber() +" ::: "+changeNO+":::"+changeNO);
	        	if(changeDate.length()>0  ){
	        		
	        		//String[] splitDate = changeDate.split(",");
		        	//String[] splitNo = changeNO.split(",");
		        	
		        	//if(splitDate.length>1){
		        		///System.out.println("N개 "+part.getNumber() +" ::: "+changeNO+":::"+changeNO);
		        	//}else{
		        		changeIBA(changeNO, part, IBAKey.IBA_CHANGENO,count);
		        		changeIBA(changeDate, part, IBAKey.IBA_CHANGEDATE,count);
		        		count++;
		        		//System.out.println("1개"+part.getNumber() +" ::: "+changeNO+":::"+changeNO);
		        	//}
	        	}else{
	        		//System.out.println("0개"+part.getNumber() +" ::: "+changeNO+":::"+changeNO);
	        	}
	        	
	        	
	            
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void changeIBA(String changeNo,WTPart part,String ibaName,int count) throws Exception{
		String[] splitNo = changeNo.split(",");
		TreeMap<String,String> noMap = new TreeMap<String,String>();
		boolean isDouble = false;
		for(int i = 0 ; i < splitNo.length ; i++){
			String temp = StringUtil.checkNull(splitNo[i]).trim();
			if( temp.length()>0){
				
				if(noMap.containsKey(temp)){
					isDouble = true;
				}else{
					noMap.put(temp, temp);
					//System.out.println(i+" = " + splitNo[i] +", "+ temp.length());
				}
				
				
			}
		}
		
		Iterator it = noMap.keySet().iterator();
		
		if(isDouble){
			String tempValue = "";
			while(it.hasNext()){
				String key = (String)it.next();
				
				tempValue = tempValue+key+",";
				
			}
			tempValue = tempValue.substring(0,tempValue.length()-1);
			//System.out.println(count+", isDouble tempValue ="+part.getNumber()+" , " +tempValue);
			
			IBAUtil.changeIBAValue((IBAHolder)part, ibaName, tempValue , "string");
			EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
			if(epm != null){
				IBAUtil.changeIBAValue((IBAHolder)epm, ibaName, tempValue , "string");
				
			}
			//
		}
		
		
	}

}
