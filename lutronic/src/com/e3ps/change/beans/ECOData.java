package com.e3ps.change.beans;

import java.util.ArrayList;
//import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeUtil;
//import com.e3ps.change.service.ECNSearchHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.change.service.ECRSearchHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
//import com.e3ps.common.web.WebUtil;
import com.e3ps.erp.service.ERPSearchHelper;
//import com.e3ps.groupware.workprocess.service.AsmSearchHelper;
//import com.e3ps.org.People;
//import com.e3ps.org.beans.UserHelper;
import com.e3ps.part.service.PartHelper;

//import wt.doc.WTDocument;
//import wt.fc.PersistenceHelper;
//import wt.lifecycle.LifeCycleManaged;
//import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.vc.baseline.ManagedBaseline;



public class ECOData extends EOData{
	
	public EChangeOrder eco;
	public String licensing;
	public Vector<NumberCode> licensingCode = null;
	public EChangeRequest ecr;
	public String riskType;
	public String riskTypeName;
	public String listInitCount;
	
	
	
	public ECOData(final EChangeOrder eco) {
		super(eco);
		
		this.eco= eco;
    	
    	this.licensing = eco.getLicensingChange();
    	this.riskType = StringUtil.checkNull(eco.getRiskType());
    	this.riskTypeName = ChangeUtil.getRiskTypeName(riskType,true);
    	
    	
    	
	}
	
	/**
	 * 완제품 품목
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getCompletePartList() throws Exception{
		
		
		List<EOCompletePartLink> list= ECOSearchHelper.service.getCompletePartLink(eco);
		//( (이 CHANGE (ECO) 자동으로 생성,승인됨이 아닌것 , 작성자  ) || 관리자 ) && ECO
		String eoType = eco.getEoType();
		String state = eco.getLifeCycleState().toString();
		String userName = eco.getCreator().getName();
		String sessionName = SessionHelper.getPrincipal().getName();
		
		boolean isECO = eoType.equals(ECOKey.ECO_CHANGE);
		boolean isCreate = userName.equals(sessionName);
		boolean isstate = !state.equals("APPROVED") ;
		boolean isDelete = ((isCreate && isstate) || CommonUtil.isAdmin()) && isECO;
		
		List<Map<String,Object>> listLink = new ArrayList<Map<String,Object>>();
		for(EOCompletePartLink link : list){
			
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster)link.getCompletePart();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
			boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED") ? true : false; 
			Map<String,Object> map = new HashMap<String,Object>();
			
			map.put("Oid", CommonUtil.getOIDString(part));
			//System.out.println("========Oid= " + CommonUtil.getOIDString(part));
			map.put("isDelete", isDelete);
			map.put("likOid", CommonUtil.getOIDString(link));
			map.put("Number", part.getNumber());
			map.put("Name", part.getName());
			map.put("ver", part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
			map.put("State", part.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("Creator", part.getCreatorFullName());
			map.put("CreateDate", DateUtil.getDateString(part.getCreateTimestamp(), "d"));
			map.put("isApproved", isApproved);
			map.put("baselineOid", CommonUtil.getOIDString(baseline));
			listLink.add(map);
			
		}
		//PJT EDIT START START 20161116
		Collections.sort(listLink,new NumberAscCompare());
		//PJT EDIT START END
		return listLink;
	}
	
	/**
     * 인허가 변경 Code
     * @return
     */
    public Vector<NumberCode> getlicensingCode(){
    	this.licensingCode = ChangeUtil.getNumberCodeVec(this.licensing, "LICENSING");
    	return licensingCode;
    	
    }
	
    public String getlicensingDisplay(){
    	return getlicensingDisplay(true);
    }
    /**
     * 인허가 변경 Display
     * @return
     */
    public String getlicensingDisplay(boolean isHtml){
    	
    	if(licensingCode == null){
    		licensingCode = getlicensingCode();
    	}
    	String licensingDisplay = ChangeUtil.getCodeListDisplay(licensingCode);
    	if(licensingDisplay.length()==0){
    		if(isHtml){
    			licensingDisplay ="<b><font color='red'>선택안됨</font</b>";
    		}else{
    			licensingDisplay ="선택안됨";
    		}
    		
    	}
    	return licensingDisplay;
    }
    
    /**
     * 관련 ECR
     * @return
     * @throws Exception
     */
    public EChangeRequest getECR() throws Exception{
    	
//    	System.out.println("===== TEST EChangeRequest getECR() =======" +eco.getEoNumber());
    	List<EChangeRequest> list = ECRSearchHelper.service.getRequestOrderLinkECR(eco);
    	
    	for(EChangeRequest ecr : list){
    		this.ecr = ecr;
    	}
    	
		return ecr;
    }
    
    
    
    /**
     * 관련 ECR Name
     * @return
     * @throws Exception
     */
    public String getECRName() throws Exception{
    	
    	String ecrNumber="";
    	if(this.ecr == null){
    		this.ecr= getECR();
    		if(this.ecr == null){
    			return "";
    		}
    		ecrNumber = ecr.getEoNumber()+"["+ecr.getEoName()+"]";
    	}else{
    		ecrNumber = ecr.getEoNumber()+"["+ecr.getEoName()+"]";
    	}
    	
    	return ecrNumber;
    }
    
    /**
     * 관련 ECR Oid
     * @return
     * @throws Exception
     */
    public String getECROid() throws Exception{
    	
    	String ecrOid ="";
    	
    	if(this.ecr == null){
    		this.ecr= getECR();
    		if(this.ecr == null){
    			return "";
    		}
    		ecrOid = CommonUtil.getOIDString(ecr);
    	}else{
    		ecrOid = CommonUtil.getOIDString(ecr);
    	}
    	
    	return ecrOid;
    }
    
    /**
     * 관련 ECR Number
     * @return
     * @throws Exception
     */
    public String getECRNumber() throws Exception{
    	
    	String ecrNumber ="";
    	
    	if(this.ecr == null){
    		this.ecr= getECR();
    		if(this.ecr == null){
    			return "";
    		}
    		ecrNumber = ecr.getEoNumber();
    	}else{
    		ecrNumber = ecr.getEoNumber();
    	}
    	
    	return ecrNumber;
    }

    /**
     * 관련 ECR Number
     * @return
     * @throws Exception
     */
    public ArrayList<String> getECRNumberAndNames() throws Exception{
    	
    	ArrayList<String> list = new ArrayList<String>();
    	List<EChangeRequest> list_ECR = ECRSearchHelper.service.getRequestOrderLinkECR(eco);
    	for (EChangeRequest ecr : list_ECR) {
    		list.add(ecr.getEoNumber()+"  :  "+ecr.getEoName());
		}
    	return list;
    }
	
    /*********** 일반 속성 ***************/
    public String getLicensing() {
		return licensing;
	}

	public void setLicensing(String licensing) {
		this.licensing = licensing;
	}

	public EChangeOrder getEco() {
		return eco;
	}

	public void setEco(EChangeOrder eco) {
		this.eco = eco;
	}
	
	
	
	

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getRiskTypeName() {
		return riskTypeName;
	}

	public void setRiskTypeName(String riskTypeName) {
		this.riskTypeName = riskTypeName;
	}

    


	public String getListInitCount() {
		return listInitCount;
	}

	public void setListInitCount(String listInitCount) {
		this.listInitCount = listInitCount;
	}




	//PJT EDIT START START 20161116
	static class NumberAscCompare implements Comparator<Map<String,Object>> {
		 
		/**
		 * 오름차순(ASC)
		 */
			@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			//return o1.getName().compareTo(o2.getName());
				String o1N = String.valueOf(o1.get("Number"));
				String o2N = String.valueOf(o2.get("Number"));
			return o1N.compareTo(o2N);
		}
 
	}
	
	/**
	 * ERP 전송 유무 체크
	 * 승인됨,관리자, 전송이력이 없음
	 * @return
	 */
	public boolean isERPSend(){
		boolean isERPSend = false;
		try{
			isERPSend = isApproved() && CommonUtil.isAdmin() && ! ERPSearchHelper.service.checkSendEO(this.number);
		}catch(Exception e){
			e.printStackTrace();
		}
		return isERPSend;
	}
	
}
