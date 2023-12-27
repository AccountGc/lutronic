package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.beans.ECAData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;

import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ECAHelper {
	public static final ECAService service = ServiceFactory.getService(ECAService.class);
	public static final ECAHelper manager = new ECAHelper();
	
	public JSONArray include_ecaList(String oid) throws Exception {
		List<ECAData> list = new ArrayList<ECAData>();
		try{
			if (oid.length() > 0) {
				ECOChange eo = (ECOChange)CommonUtil.getObject(oid);
				List<EChangeActivity> ecalist=getECAList(eo);
				
				for(EChangeActivity eca :ecalist ){
					
					ECAData data = new ECAData(eca);
					ImageIcon icon = ChangeUtil.getECAStateImg(eca.getFinishDate(), data.getState());
					data.setIcon(icon);
					list.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}
	
	public List<EChangeActivity> getECAList(ECOChange eo) throws Exception{
		List<EChangeActivity> list = new ArrayList<EChangeActivity>();
		
		ClassAttribute classattribute1 = null;
        ClassAttribute classattribute2 = null;
        ClassAttribute classattribute3 = null;
		SearchCondition sc = null;
		
		long longOid = CommonUtil.getOIDLongValue(eo);
		QuerySpec qs = new QuerySpec();
		
		Class cls1 = EChangeActivity.class;
		Class cls2 = NumberCode.class;
		
		int idx1 = qs.appendClassList(cls1, true);
		int idx2 = qs.addClassList(cls2, false);
		
		//Join 
		classattribute1 = new ClassAttribute(cls1,"step" );
	    classattribute2= new ClassAttribute(cls2, "code");
		sc = new SearchCondition(classattribute1, "=", classattribute2);
		sc.setFromIndicies(new int[] {idx1, idx2}, 0);
        sc.setOuterJoin(0);
        qs.appendWhere(sc, new int[] {idx1, idx2});
		
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(cls1,"eoReference.key.id",SearchCondition.EQUAL,longOid),new int[] {idx1});
        
        qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls2, "sort"), false),
				new int[] { idx2 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls1, "sortNumber"), false),
				new int[] { idx1 });
//        System.out.println(qs);
        QueryResult result = PersistenceHelper.manager.find(qs);
		
        while(result.hasMoreElements()){
        	
        	Object[] resultObj = (Object[])result.nextElement();
        	EChangeActivity eca = (EChangeActivity)resultObj[0];
        	
        	
        	list.add(eca);
        }
		return list;
		
	}
}
