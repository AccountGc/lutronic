package com.e3ps.rohs.dto;

import java.util.ArrayList;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsHelper;

import lombok.Getter;
import lombok.Setter;
import wt.session.SessionHelper;

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
	private String approvalTypeDisplay;
	private String creator;
	private String modifier;
	private String createDate;
	private String modifyDate;
	private String version;
	private boolean isLatest;
	private String description;
	private String fileType;
	private String publicationDate;
	private String fileName;
	
	private ROHSMaterial rohs;
	// auth
	private boolean isModify = false;
	
	// 댓글
	private ArrayList<CommentsDTO> comments = new ArrayList<CommentsDTO>();
	
	public RohsData(String oid) throws Exception {
		this((ROHSMaterial) CommonUtil.getObject(oid));
	}
	
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
		setCreateDate(rohs.getCreateTimestamp().toString().substring(0, 10));
		setModifyDate(rohs.getModifyTimestamp().toString().substring(0, 10));
		// 협력업체
		String manufacture = IBAUtil.getAttrValue(rohs, IBAKey.IBA_MANUFACTURE);
		String manufactureDisplay = keyToValue(manufacture, IBAKey.IBA_MANUFACTURE);
		setManufacture(manufacture);
		setManufactureDisplay(manufactureDisplay);
		// 결재타입
		String approvalType = IBAUtil.getAttrValue(rohs, IBAKey.IBA_APPROVALTYPE);
		String approvalTypeDisplay = "BATCH".equals(approvalType) ? "일괄결재" : "기본결재";
		setApprovalType(approvalType);
		setApprovalTypeDisplay(approvalTypeDisplay);
		setLatest(CommonUtil.isLatestVersion(rohs));
		setDescription(StringUtil.checkNull(rohs.getDescription()));
		setVersion(rohs.getVersionIdentifier().getValue() + "." + rohs.getIterationIdentifier().getValue());
		ROHSContHolder ch = RohsHelper.manager.getRohsContHolder(rohs);
		if(ch!=null) {
			setFileType(StringUtil.checkNull(ch.getFileType()));
			setPublicationDate(StringUtil.checkNull(ch.getPublicationDate()));
		}
		setComments(CommentsHelper.manager.comments(rohs));
		
		setRohs(rohs);
		setAuth();
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
	
	/**
	 * IBA 값 디스플레이 값으로 변경
	 */
	private String keyToValue(String code, String codeType) throws Exception {
		return NumberCodeHelper.manager.getNumberCodeName(code, codeType);
	}
	
	/**
	 * 권한 설정
	 */
	private void setAuth() throws Exception {
		// 삭제, 수정 권한 - (최신버전 && ( 작업중 || 임시저장 || 재작업))
		if (check("INWORK") || check("TEMPRARY") || check("REWORK")) {
			setModify(true);
		}
	}
	
	/**
	 * 상태값 여부 체크
	 */
	private boolean check(String state) throws Exception {
		boolean check = false;
		String compare = getRohs().getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
