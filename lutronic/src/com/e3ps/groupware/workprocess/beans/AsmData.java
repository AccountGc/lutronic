package com.e3ps.groupware.workprocess.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;

public class AsmData {
	
	public AsmApproval asm;
	public String oid;
	public String name;
	public String number;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String description;
	public String viewDescription;
	
	public AsmData(final AsmApproval asm) throws Exception {
		
		this.asm = asm;
		this.oid = CommonUtil.getOIDString(asm);
		this.name = asm.getName();
		this.number = asm.getNumber();
		this.creator =asm.getCreatorFullName();
    	this.createDate = DateUtil.getDateString(asm.getPersistInfo().getCreateStamp(), "a");
    	this.modifyDate = DateUtil.getDateString(asm.getPersistInfo().getModifyStamp(), "a");
    	
    	this.description = asm.getDescription();
    	this.viewDescription = WebUtil.getHtml(asm.getDescription());
	}
	
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
    	return  asm.getLifeCycleState().getDisplay(Message.getLocale());
    }
	
	public String getApprovalType(){
		//NDBT,ROHSBT,MMBT
		String approvalType= "";
		if(this.number.startsWith("NDBT")){
			approvalType ="문서";
		}else if(this.number.startsWith("MMBT")){
			approvalType ="금형";
		}else if(this.number.startsWith("ROHSBT")){
			approvalType ="ROHS";
		}
		
		return approvalType;
	}
	
	
	public String getApprovalKey(){
		String approvalKey= "";
		if(this.number.startsWith("NDBT")){
			approvalKey ="NDBT";
		}else if(this.number.startsWith("MMBT")){
			approvalKey ="MMBT";
		}else if(this.number.startsWith("ROHSBT")){
			approvalKey ="ROHSBT";
		}
		return approvalKey;
	}
	
	public String getSearchType(){
		String searchType= "";
		if(this.number.startsWith("NDBT")){
			searchType ="document";
		}else if(this.number.startsWith("MMBT")){
			searchType ="MOLD";
		}else if(this.number.startsWith("ROHSBT")){
			searchType ="ROHS";
		}
		return searchType;
	}
	
	/**
     * 회수 권한  승인중 && (소유자 || 관리자 ) && 기본 결재 
     * @return
     */
    public boolean isWithDraw(){
		
    	try{
    	
			return (isState("APPROVING") && ( isOwner() || CommonUtil.isAdmin()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
		
	}
	
   /**
    * Owner 유무 체크
    * @return
    */
	public boolean isOwner(){
		
		try{
			return SessionHelper.getPrincipal().getName().equals(asm.getCreatorName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean isState(String state) {
    	return (State.toState(state)).equals(asm.getLifeCycleState());
    }
	
	public List<Map<String,Object>> getObjectToLoink(){
		
		
		List<WTDocument> list= AsmSearchHelper.service.getObjectForAsmApproval(this.asm);
		
		List<Map<String,Object>> listLink = new ArrayList<Map<String,Object>>();
		for(WTDocument doc : list){
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("Oid", PersistenceHelper.getObjectIdentifier( doc ).toString());
			map.put("Number", doc.getNumber());
			map.put("Name", doc.getName());
			map.put("State", doc.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("Creator", doc.getCreatorFullName());
			map.put("CreateDate", DateUtil.getDateString(doc.getCreateTimestamp(), "d"));
			listLink.add(map);
			
		}
		
		return listLink;
	}
	
	public boolean isModify() throws Exception{
		
		//return isState("INWORK") && (isOwner() || CommonUtil.isAdmin());
		return (isState("INWORK")||isState("REWORK") || CommonUtil.isAdmin()) && (isOwner());
		//return true;
	}

	public AsmApproval getAsm() {
		return asm;
	}

	public void setAsm(AsmApproval asm) {
		this.asm = asm;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getViewDescription() {
		return viewDescription;
	}

	public void setViewDescription(String viewDescription) {
		this.viewDescription = viewDescription;
	}
	
	
}
