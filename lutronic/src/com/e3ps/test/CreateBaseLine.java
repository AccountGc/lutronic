package com.e3ps.test;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.ECOSearchHelper;

public class CreateBaseLine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ecoNumber = "C1704002";
		
		try{
			EChangeOrder eco = ECOSearchHelper.service.getEChangeOrder(ecoNumber);
			ECOHelper.service.createECOBaseline(eco);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
