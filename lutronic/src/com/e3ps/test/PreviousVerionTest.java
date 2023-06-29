package com.e3ps.test;

import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.PartHelper;

import wt.part.WTPart;

public class PreviousVerionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			
			String number ="1040200800";
			WTPart part = PartHelper.service.getPart(number);
			
			//완제품인 승인된 완제품 , 작업중이면 전버전 완제품, A버전이면 Skip
			//System.out.println(" before ="+part.getNumber() +"" + part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
			if(part.getLifeCycleState().toString().equals("APPROVED")){
				//System.out.println("APPROVED before ="+part.getNumber() +"" + part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
			}else{
				if(!part.getVersionIdentifier().getValue().equals("A")){
					part =  (WTPart) ObjectUtil.getPreviousVersion(part);
					
				}
			}
			
			//part =  (WTPart)ObjectUtil.getPreviousVersion(part);
		
			//System.out.println("after ="+part.getNumber() +"" + part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
