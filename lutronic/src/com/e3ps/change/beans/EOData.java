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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EOData {
	
//	public final ECOChange eo;
	private String eoName;
	private String eoNumber;
	private String model;
	private String oid;
	private String commentA;
	private String commentB;
	private String commentC;
	private String commentD;
	private String viewCommentA;
	private String viewCommentB;
	private String viewCommentC;
	private String viewCommentD;
	
	private String creator;
	private String createDate;
	private String modifyDate;
    
	private String eoType;
	private String eoApproveDate;
	private String partToEoBaseline;
    private String state;
    
	public Vector<NumberCode> modelCode = null;
    
    public EOData(ECOChange eo) {
    	setEoName(eo.getEoName());
    	setEoNumber(eo.getEoNumber());
    	setOid(CommonUtil.getOIDString(eo));
    	setModel(eo.getModel());
    	if(eo.getEoType().equals("DEV")) {
    		setEoType("개발");
    	}else {
    		setEoType("양산");
    	}
    	setCommentA(eo.getEoCommentA());
    	setCommentB(eo.getEoCommentB());
    	setCommentC(eo.getEoCommentC());
    	setCommentD(eo.getEoCommentD());
    	setViewCommentA(WebUtil.getHtml(eo.getEoCommentA()));
    	setViewCommentB(WebUtil.getHtml(eo.getEoCommentB()));
    	setViewCommentC(WebUtil.getHtml(eo.getEoCommentC()));
    	setViewCommentD(WebUtil.getHtml(eo.getEoCommentD()));
    	setCreator(eo.getCreatorFullName());
    	setCreateDate(DateUtil.getDateString(eo.getCreateTimestamp(),"a"));
    	setModifyDate(DateUtil.getDateString(eo.getCreateTimestamp(),"a"));
    	setState(eo.getLifeCycleState().getDisplay());
    	setEoApproveDate(eo.getEoApproveDate());
		
    }
    
//   public boolean isApproved() {
//	   return isState("APPROVED");
//   }
//
//	/**
//     * 
//     * @param isCreateDate
//     * @return
//     */
//    public String dateSubString(boolean isCreateDate) {
//		if(isCreateDate) {
//			return DateUtil.subString(this.createDate, 0, 10);
//		}else {
//			return DateUtil.subString(this.modifyDate, 0, 10);
//		}
//	}
//    /**
//	 * Lifecycle State
//	 * @return
//	 */
//	public String getLifecycle() {
//    	return  eo.getLifeCycleState().getDisplay(Message.getLocale());
//    }
//	
//	/**
//	 * 폴더 위치
//	 * @return
//	 */
//	public String getLocation() {
//		return StringUtil.checkNull(eo.getLocation()).replaceAll("/Default","");
//	}
//	
//	/**
//	 * 작업 중 유무 체크
//	 * @return
//	 */
//	public boolean isWorking() {
//		return (State.INWORK).equals(eo.getLifeCycleState());
//	}
//    
//	/**
//	 * 상태 값 체크
//	 * @return
//	 */
//    public boolean isState(String state) {
//    	return  isWithDrawStateCheck() || (State.toState(state)).equals(eo.getLifeCycleState());
//    }
//	
//    /**
//     * 제품 코드
//     * @return
//     */
//    public Vector<NumberCode> getModelCode(){
//    	this.modelCode = ChangeUtil.getNumberCodeVec(this.model, "MODEL");
//    	return modelCode;
//    	
//    }
//    
//    /**
//    * 제품 코드 ArrayList
//    * @return
//    */
//    public List<NumberCode> getModelList(){
//    	List<NumberCode> list = new ArrayList<NumberCode>();
//    	
//    	if(this.modelCode == null){
//    		this.modelCode = getModelCode();
//    	}
//    	
//    	for(NumberCode code : this.modelCode){
//    		list.add(code);
//    	}
//    	
//    	return list;
//    }
//    
//    
//    /**
//     * 제품 Display
//     * @return
//     */
//    public String getModelDisplay(){
//    	
//    	if(modelCode == null){
//    		modelCode = getModelCode();
//    	}
//    	
//    	return ChangeUtil.getCodeListDisplay(modelCode);
//    }
//    
//    /**
//     * 권한 설정 수정,삭제
//     */
//    public boolean isModify(){
//    	
//    	//INWORK,ACTIVITY,APPROVE_REQUEST,REWORK
//    	String state = this.eo.getLifeCycleState().toString();
//    	boolean isWorking = isState("INWORK") || isState("ACTIVITY") || isState("APPROVE_REQUEST") || isState("REWORK");
//    	boolean isOwner = false;
//    	boolean isAdmin = false;
//		try {
//			isOwner = this.eo.getCreatorName().equals(SessionHelper.getPrincipal().getName());
//			isAdmin = CommonUtil.isAdmin();
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		
////		System.out.println("isModify isWorking = "  + isWorking);
////		System.out.println("isModify isOwner = "  + isOwner);
//    	boolean isModify = (isWorking && isOwner) || isAdmin;
//    	
//    	return isModify;
//    }
//    
//    /**
//     * 회수 권한  승인중 && (소유자 || 관리자 ) && 기본 결재 
//     * @return
//     */
//    public boolean isWithDraw(){
//		
//    	try{
//			return  (isState("APPROVING") && ( isOwner() || CommonUtil.isAdmin()));
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return false;
//		
//	}
//	
//   private boolean isWithDrawStateCheck() {
//	   LifeCycleManaged lm = (LifeCycleManaged)CommonUtil.getObject(oid);
//		
//		String state = lm.getLifeCycleState().toString();
//		if(state.equals("APPROVE_REQUEST")) return false;
//		else return false;
//	}
//
///**
//    * Owner 유무 체크
//    * @return
//    */
//	public boolean isOwner(){
//		
//		try{
//			return SessionHelper.getPrincipal().getName().equals(eo.getCreatorName());
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return false;
//	}
//    
//    /**
//     * eo 구분
//     * @return
//     */
//    public String getEoTypeDisplay(){
//    	String display ="";
//    	if(this.eoType.equals(ECOKey.ECO_DEV)){
//    		display= Message.getMessage("개발");
//    	}else if(this.eoType.equals(ECOKey.ECO_PRODUCT)){
//    		display= Message.getMessage("양산");
//    	}else {
//    		display= Message.getMessage("설계변경");
//    	}
//    	return display;
//    }
//    
//    public String getCreatorDepartment() {
//    	
//    	String user = eo.getCreator().getObjectId().toString();
//    	
//    	WTUser creator = (WTUser)CommonUtil.getObject(user);
//    	try {
//	    	PeopleData data = new PeopleData(creator);
//	    	
//	    	return data.departmentName;
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    		return "";
//    	}
//    }
    	
}
