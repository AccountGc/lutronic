package com.e3ps.change.key;

public class ChangeKey {
	public static String ecrType ="ECR";
	public static String ecoType ="ECO";
	public static String ecnType ="ECN";
	public static String mcoType ="MCO"; //Minor change
	public static String mcnType ="MCN"; //Minor change Notice
	public static String ipoType ="IPO"; //신제품&부품
	public static String ecrOwner ="OWNER";
	public static String ecrReference ="REFERENCE";
	
	
	/**
	 * 
	 *      ECA Code
	 * 
	 */
	
	public static final String ECO_CHECK_METTING 		= "CHECK_METTING"; 			//검토회의
	public static final String ECO_REVISE_BOM 			= "REVISE_BOM"; 				//도면/BOM 변경
	public static final String ECO_ECO_CONFIRM 			= "ECO_CONFIRM"; 			//ECO 확정 회의
	public static final String ECO_ECA_SETTING 			= "ECA_SETTING"; 		//업무 할당
	public static final String ECO_BOM_CHANGE			= "BOM_CHANGE"; 			//BOM 변경
	public static final String ECO_ORDER_NUMBER 		= "ORDER_NUMBER"; 	//진체번
	public static final String ECO_CHECK_DRAWING 		= "CHECK_DRAWING"; 	//관련 문서/관련 도면 Check
	
	
	/**
	 * 루트로닉 추가
	 */
	public static final String[] EO_ACTIVITY_CODE_LIST ={"ORDER_NUMBER","REVISE_BOM","DOCUMENT"};
	public static final String[] EO_ACTIVITY_CODE_NAME ={"진채번","개정/BOM 변경","산출물등록"};
}
