package com.e3ps.change.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;
import com.e3ps.org.beans.UserHelper;
import com.e3ps.org.service.PeopleHelper;

import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;

@Getter
@Setter
public class ECRData{

//    private EChangeRequest ecr;
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
    private String createDate;
    private String state;

	public ECRData(EChangeRequest ecr) {
//    	super(ecr);
    	
//    	setEcr(getEcr());
    	setWriteDate(StringUtil.checkNull(ecr.getCreateDate()));
    	setApproveDate(StringUtil.checkNull(ecr.getApproveDate()));
    	setCreateDepart(StringUtil.checkNull(ecr.getCreateDepart()));
//    	setWriter(StringUtil.checkNull(ecr.getWriter()));
    	setWriter(ecr.getCreatorFullName());
    	setChangeSection(StringUtil.checkNull(ecr.getChangeSection()));
    	setProposer(StringUtil.checkNull(ecr.getProposer()));
    	setChangeCode(getChangeCode());
    	setEoName(ecr.getEoName());
    	setEoNumber(ecr.getEoNumber());
    	setModel(ecr.getModel());
    	setCreateDate(DateUtil.getDateString(ecr.getCreateTimestamp(),"a"));
    	setState(ecr.getLifeCycleState().toString());
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
