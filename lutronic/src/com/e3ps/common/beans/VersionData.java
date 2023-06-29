package com.e3ps.common.beans;

import java.rmi.RemoteException;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContained;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.VersionHelper;

public class VersionData {
	
	public RevisionControlled rev;
	
	public String oid;
	public String vrOid;
	public String name;
	public String version;
	public String iteration;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String location;
	public String stateKey;
	
	public VersionData(final RevisionControlled rev) throws Exception {
		
		this.rev = rev;
	 	this.oid = rev.getPersistInfo().getObjectIdentifier().toString();
	 	this.vrOid = CommonUtil.getVROID(rev);
    	this.name = rev.getName();
    	this.version = rev.getVersionIdentifier().getValue();
    	this.iteration = rev.getIterationIdentifier().getSeries().getValue();
    	this.creator = VersionControlHelper.getVersionCreator(rev).getDisplayName();
    	this.createDate = DateUtil.getDateString(rev.getPersistInfo().getCreateStamp(), "a");
    	this.modifier = rev.getModifierFullName();
    	this.modifyDate = DateUtil.getDateString(rev.getPersistInfo().getModifyStamp(), "a");
    	this.location = rev.getLocation();
    	this.stateKey = rev.getLifeCycleState().toString();
    	
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
    	return  rev.getLifeCycleState().getDisplay(Message.getLocale());
    }
	
	/**
	 * 폴더 위치
	 * @return
	 */
	public String getLocation() {
		return StringUtil.checkNull(rev.getLocation()).replaceAll("/Default","");
	}
	
	/**
	 * 최신 버전 유무
	 * @return
	 */
	public boolean isLatest() {
		return VersionHelper.service.isLastVersion(rev);
	}
	
	
	public String latestOid() throws Exception {
		RevisionControlled latestObj = (RevisionControlled)ObjectUtil.getLatestObject((Master)rev.getMaster());
		return latestObj.getPersistInfo().getObjectIdentifier().toString();
	}
	
	public String getPDMLinkProductOid(){
		WTContained wc = (WTContained)rev;
		String wcOid = CommonUtil.getOIDString(wc.getContainer());
		return wcOid;
	}
	
	/**
	 * 작업 중 유무 체크
	 * @return
	 */
	public boolean isWorking() {
		return (State.INWORK).equals(rev.getLifeCycleState());
	}
	
	/**
	 * 승인됨 유무 체크
	 * @return
	 */
	public boolean isApproved() {
		return (State.toState("APPROVED")).equals(rev.getLifeCycleState());
	}
    

    public boolean isState(String state) {
    	return (State.toState(state)).equals(rev.getLifeCycleState());
    }
    
    
    /**
     * 회수 권한  승인중 && (소유자 || 관리자 ) && 기본 결재 
     * @return
     */
    public boolean isWithDraw(){
		
    	try{
    		boolean isDefalut = getApprovalTypeCode().equals(AttributeKey.CommonKey.COMMON_DEFAULT);
			return (isState("APPROVING") && ( isOwner() || CommonUtil.isAdmin()) && isDefalut);
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
			return SessionHelper.getPrincipal().getName().equals(rev.getCreatorName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 문서,금형,RoHS 에서의 결재 타입 기본 결재,일괄결재
	 * @return
	 */
	public String getApprovalType(){
		String approvalType = "";
		
		approvalType = getApprovalTypeCode();
		approvalType = CommonUtil.getApprovalDisplay(approvalType);
		
		return approvalType;
	}
	
	public String getApprovalTypeCode(){
		String approvalTypeCode= "";
		try {
			approvalTypeCode = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rev, AttributeKey.IBAKey.IBA_APPROVALTYPE));
		
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return approvalTypeCode;
	}
	/* 변수 GnS */
	
	public RevisionControlled getRev() {
		return rev;
	}

	public void setRev(RevisionControlled rev) {
		this.rev = rev;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
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

	public void setLocation(String location) {
		this.location = location;
	}

	public String getVrOid() {
		return vrOid;
	}

	public void setVrOid(String vrOid) {
		this.vrOid = vrOid;
	}

	public String getStateKey() {
		return stateKey;
	}

	public void setStateKey(String stateKey) {
		this.stateKey = stateKey;
	}
	
	
}
