package com.e3ps.rohs.beans;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.part.service.VersionHelper;
import com.e3ps.rohs.ROHSMaterial;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;

@Getter
@Setter
public class RohsData{
	
//	public ROHSMaterial rohs;
	private String oid;
	private String number;
	private String name;
	private String state;
	private String stateDisplay;
	private String manufacture;
	private String manufactureDisplay;
	private String approvalType;
	private String creator;
	private String modifier;
	private String createDate;
	private String modifyDate;
	private String version;
	private boolean isLatest;
	private String description;
	
	public RohsData(ROHSMaterial rohs) throws Exception {
//		super(rohs);
//		setRohs(rohs);
		setOid(CommonUtil.getOIDString(rohs));
		setNumber(rohs.getNumber());
		setName(rohs.getName());
		setState(rohs.getLifeCycleState().toString());
		setStateDisplay(rohs.getLifeCycleState().getDisplay());
		setCreator(rohs.getCreatorFullName());
		setModifier(rohs.getModifierFullName());
		setCreateDate(DateUtil.getDateString(rohs.getCreateTimestamp(),"a"));
		setModifyDate(DateUtil.getDateString(rohs.getModifyTimestamp(),"a"));
//		setCreateDate(DateUtil.getTimeFormat(rohs.getCreateTimestamp(),"yyyy-MM-dd"));
//		setModifyDate(DateUtil.getTimeFormat(rohs.getModifyTimestamp(),"yyyy-MM-dd"));
		String manufa =IBAUtil.getAttrValue(rohs, IBAKey.IBA_MANUFACTURE);
		NumberCode code =NumberCodeHelper.service.getNumberCode("MANUFACTURE", manufa);
		if(code !=null){
			setManufactureDisplay(code.getName());
		}
		String appType =IBAUtil.getAttrValue(rohs, IBAKey.IBA_APPROVALTYPE);
		NumberCode code2 =NumberCodeHelper.service.getNumberCode("APPROVALTYPE", appType);
		if(code2 !=null){
			setApprovalType(code2.getCode());
		}
		setLatest(VersionHelper.service.isLastVersion(rohs));
		setDescription(rohs.getDescription());
		setVersion(rohs.getVersionIdentifier().getValue() + "." + rohs.getIterationIdentifier().getValue());
	}
	
	/**
   * 회수 권한  승인중 && (소유자 || 관리자 ) && 기본 결재 
   * @return
   */
   public boolean isWithDraw(){
  	   try{
			return  (state.equals("APPROVING") && ( isOwner() || CommonUtil.isAdmin()));
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
			return SessionHelper.getPrincipal().getName().equals(getCreator());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
//	public String getDescription(boolean isView) {
//		String description = StringUtil.checkNull(this.rohs.getDescription());
//		if(isView) {
//			description = WebUtil.getHtml(description);
//		}
//		return description;
//	}
//	
//	public String getRohsType() {
//		String type = this.rohs.getDocType().getDisplay(Message.getLocale());
//		return type;
//	}
//	

}
