package com.e3ps.change.beans;

import java.util.Vector;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;

import lombok.Getter;
import lombok.Setter;
import wt.session.SessionHelper;

@Getter
@Setter
public class ECRData{

//    private EChangeRequest ecr;
	private String oid;
    private String writeDate;
    private String approveDate;
    private String createDepart;
    private String writer;
    private String proposer;
    private String changeSection;
    private Vector<NumberCode> changeCode = null;
    private String eoName;
    private String eoNumber;
    private String model;
    private String modelDisplay;
    public Vector<NumberCode> modelCode = null;
    private String creator;
    private String createDate;
    private String modifyDate;
    private String state;
    private String eoCommentA;
    private String eoCommentB;
    private String eoCommentC;
    
    public ECRData() {
    	
    }

	public ECRData(EChangeRequest ecr) {
//    	super(ecr);
    	
//    	setEcr(getEcr());
		setOid(CommonUtil.getOIDString(ecr));
    	setWriteDate(StringUtil.checkNull(ecr.getCreateDate()));
    	setApproveDate(StringUtil.checkNull(ecr.getApproveDate()));
    	setCreateDepart(StringUtil.checkNull(ecr.getCreateDepart()));
    	setWriter(StringUtil.checkNull(ecr.getWriter()));
    	setCreator(ecr.getCreatorFullName());
    	setChangeSection(StringUtil.checkNull(ecr.getChangeSection()));
    	setProposer(StringUtil.checkNull(ecr.getProposer()));
    	setChangeCode(getChangeCode());
    	setEoName(ecr.getEoName());
    	setEoNumber(ecr.getEoNumber());
    	setModel(ecr.getModel());
    	setModelCode(ChangeUtil.getNumberCodeVec(ecr.getModel(), "MODEL"));
    	setModelDisplay(ChangeUtil.getCodeListDisplay(getModelCode()));
    	setCreateDate(DateUtil.getDateString(ecr.getCreateTimestamp(),"a"));
    	setModifyDate(DateUtil.getDateString(ecr.getModifyTimestamp(),"a"));
    	setState(ecr.getLifeCycleState().toString());
    	setEoCommentA(ecr.getEoCommentA());
    	setEoCommentB(ecr.getEoCommentB());
    	setEoCommentC(ecr.getEoCommentC());
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
     * 변경 구분
     * @return
     */
    public Vector<NumberCode> getChangeode(){
    	this.changeCode = ChangeUtil.getNumberCodeVec(this.changeSection, "CHANGESECTION");
    	return changeCode;
    	
    }
    
    /**
     * 변경 구분 Display
     * @return
     */
    public String getChangeDisplay(){
    	
    	if(changeCode == null){
    		changeCode = getChangeode();
    	}
    	
    	return ChangeUtil.getCodeListDisplay(changeCode);
    }
	
    /**
     * 관련 ECO
     * @return
     */
//    public List<Map<String,Object>> getEcoLink(){
//    	
//    	Vector<RequestOrderLink> vecLinks = ChangeHelper.service.getRelationECO((EChangeRequest)this.eo);
//    	
//    	List<Map<String,Object>> ecoLink = new ArrayList<Map<String,Object>>();
//		for(int i = 0 ; i < vecLinks.size() ; i++){
//			RequestOrderLink link = vecLinks.get(i);
//			if(link.getEcoType().equals(ChangeKey.ecrReference)) continue;
//			EChangeOrder eco = link.getEco();
//			Map<String,Object> map = new HashMap<String,Object>();
//			map.put("ecoOid", PersistenceHelper.getObjectIdentifier( eco ).toString());
//			map.put("ecoNumber", eco.getEoNumber());
//			map.put("ecoName", eco.getEoName());
//			map.put("ecoState", eco.getLifeCycleState().getDisplay(Message.getLocale()));
//			map.put("ecoCreator", eco.getCreatorFullName());
//			map.put("ecoDate", DateUtil.getDateString(eco.getCreateTimestamp(), "d"));
//			ecoLink.add(map);
//			
//		}
//    	
//    	return ecoLink;
//    	
//    }
}
