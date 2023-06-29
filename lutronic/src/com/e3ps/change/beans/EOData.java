package com.e3ps.change.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;

import com.e3ps.change.ECOChange;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.org.beans.PeopleData;


public class EOData {
	
	public final ECOChange eo;
	public final String number;
	public final String name;
	public final String oid;
    public String model;
	public String commentA;
	public String commentB;
	public String commentC;
	public String commentD;
	public String viewCommentA;
	public String viewCommentB;
	public String viewCommentC;
	public String viewCommentD;
	
	public String creator;
	public String createDate;
	public String modifyDate;
    
	public String eoType;
	public String eoApproveDate;
	public String partToEoBaseline;
	
	
   
	public Vector<NumberCode> modelCode = null;
    
    public EOData(final ECOChange eo) {
    	this.eo = eo;
    	this.oid = CommonUtil.getOIDString(eo);
		this.name = eo.getEoName();
		this.number = eo.getEoNumber();
		this.model = eo.getModel();
		
		this.eoType = eo.getEoType();
		this.creator = eo.getCreatorFullName();
		this.createDate = DateUtil.getDateString(eo.getCreateTimestamp(),"a");
		this.modifyDate = DateUtil.getDateString(eo.getModifyTimestamp(),"a");
		
			
		this.commentA = eo.getEoCommentA();
		this.commentB = eo.getEoCommentB();
		this.commentC = eo.getEoCommentC();
		this.commentD = eo.getEoCommentD();
		
		this.viewCommentA = WebUtil.getHtml(eo.getEoCommentA());
		this.viewCommentB = WebUtil.getHtml(eo.getEoCommentB());
		this.viewCommentC = WebUtil.getHtml(eo.getEoCommentC());
		this.viewCommentD = WebUtil.getHtml(eo.getEoCommentD());
		
		this.eoApproveDate = StringUtil.checkNull(eo.getEoApproveDate());
		
		
		
		
			
    }
    
   public boolean isApproved() {
	   return isState("APPROVED");
   }

	/**
     * 
     * @param isCreateDate
     * @return
     */
    public String dateSubString(boolean isCreateDate) {
		if(isCreateDate) {
			return DateUtil.subString(this.createDate, 0, 10);
		}else {
			return DateUtil.subString(this.modifyDate, 0, 10);
		}
	}
    /**
	 * Lifecycle State
	 * @return
	 */
	public String getLifecycle() {
    	return  eo.getLifeCycleState().getDisplay(Message.getLocale());
    }
	
	/**
	 * 폴더 위치
	 * @return
	 */
	public String getLocation() {
		return StringUtil.checkNull(eo.getLocation()).replaceAll("/Default","");
	}
	
	/**
	 * 작업 중 유무 체크
	 * @return
	 */
	public boolean isWorking() {
		return (State.INWORK).equals(eo.getLifeCycleState());
	}
    
	/**
	 * 상태 값 체크
	 * @return
	 */
    public boolean isState(String state) {
    	return  isWithDrawStateCheck() || (State.toState(state)).equals(eo.getLifeCycleState());
    }
	
    /**
     * 제품 코드
     * @return
     */
    public Vector<NumberCode> getModelCode(){
    	this.modelCode = ChangeUtil.getNumberCodeVec(this.model, "MODEL");
    	return modelCode;
    	
    }
    
    /**
    * 제품 코드 ArrayList
    * @return
    */
    public List<NumberCode> getModelList(){
    	List<NumberCode> list = new ArrayList<NumberCode>();
    	
    	if(this.modelCode == null){
    		this.modelCode = getModelCode();
    	}
    	
    	for(NumberCode code : this.modelCode){
    		list.add(code);
    	}
    	
    	return list;
    }
    
    
    /**
     * 제품 Display
     * @return
     */
    public String getModelDisplay(){
    	
    	if(modelCode == null){
    		modelCode = getModelCode();
    	}
    	
    	return ChangeUtil.getCodeListDisplay(modelCode);
    }
    
    /**
     * 권한 설정 수정,삭제
     */
    public boolean isModify(){
    	
    	//INWORK,ACTIVITY,APPROVE_REQUEST,REWORK
    	String state = this.eo.getLifeCycleState().toString();
    	boolean isWorking = isState("INWORK") || isState("ACTIVITY") || isState("APPROVE_REQUEST") || isState("REWORK");
    	boolean isOwner = false;
    	boolean isAdmin = false;
		try {
			isOwner = this.eo.getCreatorName().equals(SessionHelper.getPrincipal().getName());
			isAdmin = CommonUtil.isAdmin();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
//		System.out.println("isModify isWorking = "  + isWorking);
//		System.out.println("isModify isOwner = "  + isOwner);
    	boolean isModify = (isWorking && isOwner) || isAdmin;
    	
    	return isModify;
    }
    
    /**
     * 회수 권한  승인중 && (소유자 || 관리자 ) && 기본 결재 
     * @return
     */
    public boolean isWithDraw(){
		
    	try{
			return  (isState("APPROVING") && ( isOwner() || CommonUtil.isAdmin()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
		
	}
	
   private boolean isWithDrawStateCheck() {
	   LifeCycleManaged lm = (LifeCycleManaged)CommonUtil.getObject(oid);
		
		String state = lm.getLifeCycleState().toString();
		if(state.equals("APPROVE_REQUEST")) return false;
		else return false;
	}

/**
    * Owner 유무 체크
    * @return
    */
	public boolean isOwner(){
		
		try{
			return SessionHelper.getPrincipal().getName().equals(eo.getCreatorName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
    
    /**
     * eo 구분
     * @return
     */
    public String getEoTypeDisplay(){
    	String display ="";
    	if(this.eoType.equals(ECOKey.ECO_DEV)){
    		display= Message.getMessage("개발");
    	}else if(this.eoType.equals(ECOKey.ECO_PRODUCT)){
    		display= Message.getMessage("양산");
    	}else {
    		display= Message.getMessage("설계변경");
    	}
    	return display;
    }
    
    public String getCreatorDepartment() {
    	
    	String user = eo.getCreator().getObjectId().toString();
    	
    	WTUser creator = (WTUser)CommonUtil.getObject(user);
    	try {
	    	PeopleData data = new PeopleData(creator);
	    	
	    	return data.departmentName;
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "";
    	}
    }
    	
    
    
    /**
     * 일반 속성
     */
    
    
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCommentA() {
		return commentA;
	}

	public void setCommentA(String commentA) {
		this.commentA = commentA;
	}

	public String getCommentB() {
		return commentB;
	}

	public void setCommentB(String commentB) {
		this.commentB = commentB;
	}

	public String getCommentC() {
		return commentC;
	}

	public void setCommentC(String commentC) {
		this.commentC = commentC;
	}

	public String getCommentD() {
		return commentD;
	}

	public void setCommentD(String commentD) {
		this.commentD = commentD;
	}

	public String getViewCommentA() {
		return viewCommentA;
	}

	public void setViewCommentA(String viewCommentA) {
		this.viewCommentA = viewCommentA;
	}

	public String getViewCommentB() {
		return viewCommentB;
	}

	public void setViewCommentB(String viewCommentB) {
		this.viewCommentB = viewCommentB;
	}

	public String getViewCommentC() {
		return viewCommentC;
	}

	public void setViewCommentC(String viewCommentC) {
		this.viewCommentC = viewCommentC;
	}

	public String getViewCommentD() {
		return viewCommentD;
	}

	public void setViewCommentD(String viewCommentD) {
		this.viewCommentD = viewCommentD;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getEoType() {
		return eoType;
	}

	public void setEoType(String eoType) {
		this.eoType = eoType;
	}

	public ECOChange getEo() {
		return eo;
	}

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public String getOid() {
		return oid;
	}

	public void setModelCode(Vector<NumberCode> modelCode) {
			this.modelCode = modelCode;
	}

	public String getEoApproveDate() {
		return eoApproveDate;
	}

	public void setEoApproveDate(String eoApproveDate) {
		this.eoApproveDate = eoApproveDate;
	}

	public String getPartToEoBaseline() {
		return partToEoBaseline;
	}

	public void setPartToEoBaseline(String partToEoBaseline) {
		this.partToEoBaseline = partToEoBaseline;
	}
    
    
}
