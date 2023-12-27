package com.e3ps.part.dto;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import wt.doc.WTDocument;
import wt.part.WTPart;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.erp.beans.BomData;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;

public class ObjectComarator implements Comparator{
	
	public ObjectComarator(){
		
		
	}
	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		int result = 0;
		if(o1 instanceof PartTreeData){
			
			PartTreeData data1 =(PartTreeData)o1;
			PartTreeData data2 =(PartTreeData)o2;
			
			String number1 = data1.number;
			String number2 = data2.number;
			result = number1.compareTo(number2);
		//	System.out.println("number1 =" + number1 +",number2="+ number2 +"=========" + result);
		}else if(o1 instanceof BomData){
			
			BomData  bomdata1 = (BomData)o1;
			BomData  bomdata2 = (BomData)o2;
			
			String partentNumber1 = bomdata1.parent;
			String partentNumber2 = bomdata2.parent;
			
			result = partentNumber1.compareTo(partentNumber2);
			
			if(result != 0){
				return result;
			}
			
			String childNumber1 = bomdata1.child;
			String childNumber2 = bomdata2.child;
			
			result = childNumber1.compareTo(childNumber2);
		}else if(o1 instanceof PartData){
			
			
			PartData supart1 = (PartData)o1;
			PartData supart2 = (PartData)o2;
			
			String number1 = supart1.getNumber();
			String number2 = supart2.getNumber();
			//System.out.println(number1 +" = PartData compare ="+number2);
			result = number1.compareTo(number2);
		}else if(o1 instanceof WTPart){
			
			WTPart supart1 = (WTPart)o1;
			WTPart supart2 = (WTPart)o2;
			
			String number1 = supart1.getNumber();
			String number2 = supart2.getNumber();
			result = number1.compareTo(number2);
			//System.out.println(number1 +" = WTPart compare ="+number2);
			//System.out.println("number1 =" + number1 +",number2="+ number2 +"=========" + result);
			
		}else if(o1 instanceof HashMap){
			HashMap<String, Object> map1 = (HashMap)o1;
			String oid1 = (String)map1.get("partOid");
			int level1 = (Integer)map1.get("level");
			String level11 = String.valueOf(level1);
			
			WTPart supart1 = (WTPart)CommonUtil.getObject(oid1);
			String number1 = supart1.getNumber();
			
			HashMap<String, Object> map2 = (HashMap)o2;
			String oid2 = (String)map2.get("partOid");
			int level2 = (Integer)map2.get("level");
			String level22 = String.valueOf(level1);
			WTPart supart2 = (WTPart)CommonUtil.getObject(oid2);
			String number2 = supart2.getNumber();
			
			result = level11.compareTo(level22);
			//System.out.println("level11 =" + level11 +",level22="+ level22 +"=========" + level22);
			if(result != 0){
				return result;
			}
			result = number1.compareTo(number2);
			//System.out.println("number1 =" + number1 +",number2="+ number2 +"=========" + result);
		}else if(o1 instanceof RohsData){
			
			
			RohsData doc1 = (RohsData)o1;
			RohsData doc2 = (RohsData)o2;
			
			String number1 = doc1.getNumber();
			String number2 = doc2.getNumber();
			//System.out.println(number1 +" = RohsData compare ="+number2);
			result = number1.compareTo(number2);
		}
		
		return result;
	}

}
