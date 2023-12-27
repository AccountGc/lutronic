package com.e3ps.change.service;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.editor.BEContext;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.org.WTUser;

public class ChangeUtil {
	
	
	public static ImageIcon icon1;
	public static ImageIcon icon2;
    public static ImageIcon icon3;
    public static ImageIcon icon4;
	static{
		try{
		 icon1=new ImageIcon(new URL(BEContext.host+"jsp/portal/images/tree/task_red.gif"));
		 icon2=new ImageIcon(new URL(BEContext.host+"jsp/portal/images/tree/task_complete.gif"));
		 icon3=new ImageIcon(new URL(BEContext.host+"jsp/portal/images/tree/task_progress.gif"));
		 icon4=new ImageIcon(new URL(BEContext.host+"jsp/portal/images/tree/task_ready.gif"));
		}catch(Exception ex){
		
		}
	}
	
	public static Vector<NumberCode> getNumberCodeVec(String values,String codeType){
		
		Vector<NumberCode> vec = new Vector<NumberCode>();
		try{
			values = StringUtil.checkNull(values);
			if(values.length() ==0 ){
				return vec;
			}
			StringTokenizer tokens = new StringTokenizer(values,",");
			while(tokens.hasMoreTokens()){
				String pp = (String)tokens.nextToken();
				NumberCode code = CodeHelper.service.getNumberCode(codeType, pp);
				if(code != null){
					vec.add(code);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	
	public static String getCodeListDisplay(Vector<NumberCode> vec){
		
		String display = "";
		if(vec == null){
			return display ;
		}
		
		for(NumberCode code : vec){
			display = display+ Message.getNC(code)+",";
		}
		
		if(display.length() == 0){
			return "";
		}
		
		display =  display.substring(0, display.length()-1);
		return display;
	}
	
	public static String getArrayList(String[] values){
		
		String value ="";
		try{
			if(values == null) return value;
			
			for(int i = 0 ; i<values.length ;  i++){
				value =  value +values[i]+",";
			}
			value = value.substring(0, value.length()-1);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return value;
		
	}
	
	public static Vector<EcoPartLink> getECOPartLinkVec(EChangeOrder eco){
		
		Vector<EcoPartLink> vec = new Vector<EcoPartLink>();
		try{
			QueryResult tpQr = ECOSearchHelper.service.ecoPartLink(eco);//EChangeHelper.getPartForECO(eco, "true", null);
			if(tpQr != null) {
				int idxPart = 0;
				
				while (tpQr.hasMoreElements()) {
				
					Object[] o = (Object[])tpQr.nextElement();
					
					EcoPartLink link = (EcoPartLink)o[0];
					
					vec.add(link);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	
	/**
	 * Activity를 Map 으로 Return
	 * @return
	 */
	public static Map<String, String>  getActivityTypeMap(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		String[] activityCodeList = ChangeKey.EO_ACTIVITY_CODE_LIST;
		String[] activityCodeNAME = ChangeKey.EO_ACTIVITY_CODE_NAME;
		
		for(int i = 0 ; i< activityCodeList.length ; i++){
			map.put(activityCodeList[i], activityCodeNAME[i]);
		}
		
		return map;
	}
	
	/**
	 * Activity를 List 으로 Return
	 * @return
	 */
	public static List<Map<String, String>>  getActivityTypeList(){
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		String[] activityCodeList = ChangeKey.EO_ACTIVITY_CODE_LIST;
		String[] activityCodeNAME = ChangeKey.EO_ACTIVITY_CODE_NAME;
		
		for(int i = 0 ; i< activityCodeList.length ; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", activityCodeList[i]);
			map.put("name", activityCodeNAME[i]);
			list.add(map);
		}
		
		return list;
	}
	
	public static String getActivityName(String activity){
		
		Map<String, String> map = getActivityTypeMap();
		String name = map.get(activity);
		return name;
	}
	
	public static ImageIcon getECAStateImg(Timestamp finishDate,String state){
		
		Timestamp toDate = DateUtil.convertDate(DateUtil.getToDay());
		ImageIcon icon = null;
		if(state.equals("INWORK")){
    		
    		if(finishDate==null  ){
    			icon = icon3;
    		}else if(toDate.getTime() > finishDate.getTime()) {
    		
    			icon = icon1;
    		}else if(toDate.getTime() == finishDate.getTime() || toDate.getTime() < finishDate.getTime() ){
    			icon = icon3;
    		} 
    	}else if(state.equals("INTAKE")){
    		icon =icon4;
    	}else if(state.equals("COMPLETED")){
    		icon =icon2;
    	}
		
		
		
		return icon;
		
	}
	
	public static Map<String, String > approvedSetData(EChangeOrder eco) throws Exception {
		
		Vector<WTUser> approvedUser = WFItemHelper.service.getApprover(eco);
		String approveName ="-";
		String checkerName = "-";
		WTUser approver = null;
		WTUser checker = null; 
		if(approvedUser.size()>1){
			//approver = approvedUser.get(1);
			//checker = approvedUser.get(0);
			approver = approvedUser.get(0);
			checker = approvedUser.get(1);
			approveName = approver.getFullName();
			checkerName = checker.getFullName();
		}else if (approvedUser.size()==1){
			approver = approvedUser.get(0);
			approveName = approver.getFullName();
		}else{
			
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ECONO", eco.getEoNumber());
		map.put("ECODATE", DateUtil.getDateString(eco.getModifyTimestamp(), "d"));
		map.put("CHK", checkerName);
		map.put("APR", approveName);
		
		return map;
		
	}
	
	
    public static Map<String, String> getApproveInfo(WTObject obj) throws Exception {
    	
    	Vector<WFItemUserLink> vec =  WFItemHelper.service.getApproverLink(obj);
    	
    	Map<String, String> map = new HashMap<String, String>();
    	
    	String approveName ="";
    	String checkerName ="";
    	String approveDate ="";
    	String checkDate = "";
    	
    	
    	if(vec.size()>1){
		
			approveName = vec.get(0).getUser().getFullName();
			checkerName = vec.get(1).getUser().getFullName();
			approveDate = DateUtil.getDateString(vec.get(0).getProcessDate(), "d") ;
			checkDate = DateUtil.getDateString(vec.get(1).getProcessDate(), "d") ;
		}else if (vec.size()==1){
			approveName = vec.get(0).getUser().getFullName();
			approveDate = DateUtil.getDateString(vec.get(0).getProcessDate(), "d") ;
		}else{
			
		}
    	map.put("approveName", approveName);
    	map.put("approveDate", approveDate);
    	map.put("checkerName", checkerName);
    	map.put("checkDate", checkDate);
    	
    	return map;
    }
	
	/**
	 * 부품,도면 메카 유무 체크
	 * @param location
	 * @return
	 */
	public static boolean isMeca(String location){
		boolean isMeca = false; 
		
		if(location.lastIndexOf("메카")>0){
			isMeca = true;
		}else{
			isMeca = false;
		}
		
		return isMeca;
	}
	
	/**
	 * 메카 이면 검도자,승인자 이상훈 수석으로 변경
	 * @param rc
	 * @param map
	 * @return
	 */
	public static Map<String, String> changeApprove(RevisionControlled rc,Map<String, String> map){
		
		String location = rc.getLocation();
		if(isMeca(location)){
			//map.put("APR", "이상훈");
			map.put("CHK", "-");
		}
		
		return map;
		
	}
	
	/**
	 * ECO riskType명 칭
	 * @param riskType
	 * @return
	 */
	public static String getRiskTypeName(String riskType,boolean isHtml){
		riskType = StringUtil.checkNull(riskType);
		String riskTypeName ="";
		if(isHtml){
			riskTypeName ="<b><font color='red'>선택안됨</font><b>";
		}else{
			riskTypeName ="선택안됨";
		}
		
		if(riskType.equals("0")){
    		riskTypeName = "불필요" ;
    	}else if(riskType.equals("1")){
    		riskTypeName = "필요" ;
    	}
		return riskTypeName;
	}
}
