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

import wt.fc.PersistenceHelper;
import wt.org.WTUser;

public class ECRData extends EOData{

    public EChangeRequest ecr;
    public String writeDate;
    public String approveDate;
    public String createDepart;
    public String writer;
    public String proposer;
    public String changeSection;
    public Vector<NumberCode> changeCode = null;
    

	public ECRData(final EChangeRequest ecr) {
    	super(ecr);
    	this.ecr = ecr;
    	this.writeDate = StringUtil.checkNull(ecr.getCreateDate());
    	this.approveDate = StringUtil.checkNull(ecr.getApproveDate());
    	this.createDepart = StringUtil.checkNull(ecr.getCreateDepart());
    	this.writer = StringUtil.checkNull(ecr.getWriter());
    	this.changeSection = StringUtil.checkNull(ecr.getChangeSection());
    	this.proposer =StringUtil.checkNull(ecr.getProposer());
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
    public List<Map<String,Object>> getEcoLink(){
    	
    	Vector<RequestOrderLink> vecLinks = ChangeHelper.service.getRelationECO((EChangeRequest)this.eo);
    	
    	List<Map<String,Object>> ecoLink = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i < vecLinks.size() ; i++){
			RequestOrderLink link = vecLinks.get(i);
			if(link.getEcoType().equals(ChangeKey.ecrReference)) continue;
			EChangeOrder eco = link.getEco();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("ecoOid", PersistenceHelper.getObjectIdentifier( eco ).toString());
			map.put("ecoNumber", eco.getEoNumber());
			map.put("ecoName", eco.getEoName());
			map.put("ecoState", eco.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("ecoCreator", eco.getCreatorFullName());
			map.put("ecoDate", DateUtil.getDateString(eco.getCreateTimestamp(), "d"));
			ecoLink.add(map);
			
		}
    	
    	return ecoLink;
    	
    }

	public EChangeRequest getEcr() {
		return ecr;
	}

	public void setEcr(EChangeRequest ecr) {
		this.ecr = ecr;
	}
	
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}

	public String getCreateDepart() {
		return createDepart;
	}

	public void setCreateDepart(String createDepart) {
		this.createDepart = createDepart;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getProposer() {
		return proposer;
	}

	public void setProposer(String proposer) {
		this.proposer = proposer;
	}

	public String getChangeSection() {
		return changeSection;
	}

	public void setChangeSection(String changeSection) {
		this.changeSection = changeSection;
	}

	public Vector<NumberCode> getChangeCode() {
		return changeCode;
	}

	public void setChangeCode(Vector<NumberCode> changeCode) {
		this.changeCode = changeCode;
	}
    
    
    
}
