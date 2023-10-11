package com.e3ps;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.util.WCUtil;
import com.e3ps.rohs.ROHSAttr;
import com.e3ps.rohs.ROHSContHolder;

import wt.content.ApplicationData;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		EChangeOrder ec  = (EChangeOrder) rf.getReference("com.e3ps.change.EChangeOrder:282303").getObject();
		
		
		//ROHSContHolder 의 FileType 를  ApplicationData 의 roletype로 변경
		QuerySpec query = new QuerySpec();
		System.out.println(WCUtil.getWTContainerRef());
		int idx = query.appendClassList(ROHSContHolder.class, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ROHSContHolder rohs = (ROHSContHolder) obj[0];
			QuerySpec query2 = new QuerySpec();
			int idx2 = query2.appendClassList(ApplicationData.class, true);
			
			SearchCondition sc = new SearchCondition(ApplicationData.class, "thePersistInfo.theObjectIdentifier.id", "=", rohs.getApp().getPersistInfo().getObjectIdentifier().getId());
			query2.appendWhere(sc, new int[] { idx2 });
			QueryResult result2 = PersistenceHelper.manager.find(query2);
			
			if (result2.hasMoreElements()) {
				Object[] obj2 = (Object[]) result2.nextElement();
				ApplicationData appData = (ApplicationData) obj2[0];
				rohs.setFileType(appData.getRole().toString());
				PersistenceHelper.manager.modify(rohs);
				
				QuerySpec query3 = new QuerySpec();
				int idx3 = query3.appendClassList(ROHSAttr.class, true);
				
				SearchCondition sc3 = new SearchCondition(ROHSAttr.class, "appReference.key.id", "=", appData.getPersistInfo().getObjectIdentifier().getId());
				query3.appendWhere(sc3, new int[] { idx3 });
				QueryResult result3 = PersistenceHelper.manager.find(query3);
				
				if (result3.hasMoreElements()) {
					Object[] obj3 = (Object[]) result3.nextElement();
					ROHSAttr rohsAttr = (ROHSAttr) obj3[0];
					rohsAttr.setPublicationDate(rohs.getPublicationDate());
					PersistenceHelper.manager.modify(rohsAttr);
				}else {
					ROHSAttr rohsAttr = ROHSAttr.newROHSAttr();
					rohsAttr.setPublicationDate(rohs.getPublicationDate());
					rohsAttr.setApp(appData);
					PersistenceHelper.manager.save(rohsAttr);
				}
				
			}
			
	
		}
		
		
	}
}