package com.e3ps.test;

import java.util.HashMap;
import java.util.Map;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;

public class DrawingTest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oid = "wt.epm.EPMDocument:8449041";
		EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
		
		String application =epm.getAuthoringApplication().toString();
		String cadType = epm.getDocType().toString();
		
		//System.out.println(application +":"+cadType);
		
		
	}
}
