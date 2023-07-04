package com.e3ps.common.beans;

import java.rmi.RemoteException;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContained;
import wt.lifecycle.State;
import wt.org.WTUser;
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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionData {
	
	private RevisionControlled rev;
	
	private String oid;
	private String vrOid;
	private String name;
	private String version;
	private String iteration;
	private String creator;
	private String createDate;
	private String modifier;
	private String modifyDate;
	private String location;
	private String stateKey;
	
	public VersionData(final RevisionControlled rev) throws Exception {
		setRev(rev);
		setOid(rev.getPersistInfo().getObjectIdentifier().toString());
		setVrOid(CommonUtil.getVROID(rev));
		setName(rev.getName());
		setVersion(rev.getVersionIdentifier().getValue());
		setIteration(rev.getIterationIdentifier().getSeries().getValue());
		setCreator(VersionControlHelper.getVersionCreator(rev).getDisplayName());
		setCreateDate(DateUtil.getDateString(rev.getPersistInfo().getCreateStamp(), "a"));
		setModifier(rev.getModifierFullName());
		setModifyDate(DateUtil.getDateString(rev.getPersistInfo().getModifyStamp(), "a"));
		setLocation(rev.getLocation());
		setStateKey(rev.getLifeCycleState().toString());
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
}
