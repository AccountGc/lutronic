package com.e3ps.change.beans;

import wt.org.WTUser;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.PeopleHelper;
import com.e3ps.org.service.UserHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EADData {
	private String oid;
	private String rootOid;
	private String name;
	private String name_eng;
	private String step;
	private String stepName;
	private String stepSort;
	private String activityType;
	private String activityName;
	private String activeUserOid;
	private String activeUserName;
	private String departName;
	private int sortNumber;
	private String description;
	private String viewDescription;
	private String finishDate;
	private boolean isModify = true;
	
	public EADData() {
		
	}
	
	public EADData(EChangeActivityDefinition ead) throws Exception {
		setOid(CommonUtil.getOIDString(ead));
		setRootOid(CommonUtil.getOIDString(ead.getRoot()));
		setName(StringUtil.checkNull(ead.getName()));
		setName_eng(StringUtil.checkNull(ead.getName_eng()));
		setStep(StringUtil.checkNull(ead.getStep()));
		NumberCodeDTO stepCode = NumberCodeHelper.manager.getStepNumberCode("EOSTEP", ead.getStep());
		if (stepCode != null) {
			setStepName(stepCode.getName());
		}
		setActivityType(StringUtil.checkNull(ead.getActiveType()));
		setActivityName(ChangeUtil.getActivityName(ead.getActiveType()));
		if(ead.getActiveUser() != null){
			setActiveUserOid(CommonUtil.getOIDString(ead.getActiveUser()));
			setActiveUserName(ead.getActiveUser().getFullName());
		}
		setSortNumber(ead.getSortNumber());
		setDescription(StringUtil.checkNull(ead.getDescription()));
		setViewDescription(WebUtil.getHtml(ead.getDescription()));
		setModify(true);
		PeopleDTO pp = PeopleHelper.manager.getPeople(ead.getActiveUser());
		if(pp != null){
			setDepartName(pp.getDepartmentName());
		}
	}
}
