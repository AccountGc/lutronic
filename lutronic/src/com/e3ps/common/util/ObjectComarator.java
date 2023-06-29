package com.e3ps.common.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import wt.doc.WTDocument;
import wt.part.WTPart;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.erp.beans.BomData;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.beans.RohsData;

public class ObjectComarator implements Comparator{
	
	public ObjectComarator(){
		
		
	}
	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		int result = 0;
		
		 if(o1 instanceof WTDocument){
			WTDocument doc1 = (WTDocument)o1;
			WTDocument doc2 = (WTDocument)o2;
			
			String number1 = doc1.getNumber();
			String number2 = doc2.getNumber();
			//System.out.println(number1 +" = RohsData compare ="+number2);
			result = number1.compareTo(number2);
		}
		
		return result;
	}

}
