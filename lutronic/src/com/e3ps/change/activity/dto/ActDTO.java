package com.e3ps.change.activity.dto;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.People;
import com.e3ps.org.service.PeopleHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActDTO {

	private String oid;
	private String def;
	private String name;
	private String step;
	private String step_name;
	private String active_type;
	private String active_name;
	private String activity_type;
	private String activity_name;
	private String activeUser_oid;
	private String activeUser_name;
	private String department_name;
	private int sort;
	private String description;
	private String finishDate;

	public ActDTO() {

	}

	public ActDTO(String oid) throws Exception {
		this((EChangeActivityDefinition) CommonUtil.getObject(oid));
	}

	public ActDTO(EChangeActivityDefinition act) throws Exception {
		setOid(act.getPersistInfo().getObjectIdentifier().getStringValue());
		setDef(act.getRoot().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(act.getName());
		if (act.getStep() != null) {
			setStep(act.getStep());
			NumberCode step = NumberCodeHelper.manager.getNumberCode(getStep(), "EOSTEP");
			if (step != null) {
				setStep_name(step.getName());
			}
		}
		setActivity_type(act.getActiveType());
		setActivity_name(ActivityHelper.manager.getActName(getActivity_type()));
		if (act.getActiveUser() != null) {
			setActiveUser_oid(act.getActiveUser().getPersistInfo().getObjectIdentifier().getStringValue());
			setActiveUser_name(act.getActiveUser().getFullName());

			People p = PeopleHelper.manager.getPeople(act.getActiveUser());
			if (p != null) {
				setDepartment_name(p.getDepartment() != null ? p.getDepartment().getName() : "지정안됨");
			}
		}
		setSort(act.getSortNumber());
		setDescription(act.getDescription());
	}
	
	public ActDTO(EChangeActivity act) throws Exception {
		setOid(act.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(act.getName());
		if (act.getStep() != null) {
			setStep(act.getStep());
			NumberCode step = NumberCodeHelper.manager.getNumberCode(getStep(), "EOSTEP");
			if (step != null) {
				setStep_name(step.getName());
			}
			setStep_name(act.getStep());
		}
		
		setActive_type(act.getActiveType());
		setActive_name(ActivityHelper.manager.getActName(getActive_type()));
		if (act.getActiveUser() != null) {
			setActiveUser_oid(act.getActiveUser().getPersistInfo().getObjectIdentifier().getStringValue());
			setActiveUser_name(act.getActiveUser().getFullName());

			People p = PeopleHelper.manager.getPeople(act.getActiveUser());
			if (p != null) {
				setDepartment_name(p.getDepartment() != null ? p.getDepartment().getName() : "지정안됨");
			}
		}
		setSort(act.getSortNumber());
		setDescription(act.getDescription());
		setFinishDate(act.getFinishDate() == null ?  null:act.getFinishDate().toString());
		
		
		
	}
	
}
