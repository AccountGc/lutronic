package com.e3ps.change.beans;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class DEFData {

	private String oid;
	private String rootOid;
	private String name;
	private String name_eng;
	private String step;
	private String stepName;
	private String activityType;
	private String activityName;
	private WTUser activeUser;
	private String activeUserOid;
	private String activeUserName;
	private int sortNumber;
	private String description;
	private String viewDescription;

	public DEFData(EChangeActivityDefinition def) {
		setOid(CommonUtil.getOIDString(def));
		setRootOid(CommonUtil.getOIDString(def.getRoot()));
		setName(StringUtil.checkNull(def.getName()));
		setName_eng(StringUtil.checkNull(def.getName_eng()));
		setStep(StringUtil.checkNull(def.getStep()));
		NumberCode stepCode = NumberCodeHelper.service.getNumberCode("EOSTEP", getStep());
		if (stepCode != null) {
			setStepName(stepCode.getName());
		}

		setActivityType(StringUtil.checkNull(def.getActiveType()));
		setActivityName(ChangeUtil.getActivityName(getActivityType()));
		if (def.getActiveUser() != null) {
			setActiveUser(def.getActiveUser());
			setActiveUserOid(CommonUtil.getOIDString(getActiveUser()));
			setActiveUserName(getActiveUser().getFullName());
		}
		setSortNumber(def.getSortNumber());
		setDescription(StringUtil.checkNull(def.getDescription()));
		setViewDescription(WebUtil.getHtml(getDescription()));
	}
}
