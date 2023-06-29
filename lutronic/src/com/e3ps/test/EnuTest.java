package com.e3ps.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.content.ContentRoleType;

import com.e3ps.common.code.NumberCodeType;
import com.e3ps.rohs.service.RohsUtil;

public class EnuTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//NumberCodeType[] codeType = NumberCodeType.getNumberCodeTypeSet();
		/*
		ContentRoleType[] contentRole = ContentRoleType.getContentRoleTypeSet();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		for(int i=0; i < contentRole.length; i++){	
			String value = contentRole[i].toString();
			//System.out.println("indexOf = "+value.indexOf("ROHS") +":" + value);
			if(value.indexOf("ROHS")>-1){
				//System.out.println("value = "+value);
			}
		}
		*/
		
		List<String> list = RohsUtil.getROHSContentRoleKey();
		
		for(String aa : list){
			//System.out.println(aa);
		}
	}

}
