package com.e3ps.change.activity.dto;

import java.sql.Timestamp;
import java.util.Date;

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
	private String current; // 현황
	private String def;
	private String name;
	private String step;
	private String step_name;
//	private String active_type;
//	private String active_name;
	private String activity_type;
	private String activity_name;
	private String activeUser_oid;
	private String activeUser_name;
	private String department_name;
	private int sort;
	private String description;
	private String finishDate;
	private String completeDate;
	private String state;

	public ActDTO() {

	}

	public ActDTO(String oid) throws Exception {
		this((EChangeActivityDefinition) CommonUtil.getObject(oid));
	}

	/**
	 * 설계변경 활동 루트 정보
	 */
	public ActDTO(EChangeActivityDefinition def) throws Exception {
		setOid(def.getPersistInfo().getObjectIdentifier().getStringValue());
		setDef(def.getRoot().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(def.getName());
		if (def.getStep() != null) {
			setStep(def.getStep());
			NumberCode step = NumberCodeHelper.manager.getNumberCode(getStep(), "EOSTEP");
			if (step != null) {
				setStep_name(step.getName());
			}
		}
		setActivity_type(def.getActiveType());
		setActivity_name(ActivityHelper.manager.getActName(getActivity_type()));
		if (def.getActiveUser() != null) {
			setActiveUser_oid(def.getActiveUser().getPersistInfo().getObjectIdentifier().getStringValue());
			setActiveUser_name(def.getActiveUser().getFullName());

			People p = PeopleHelper.manager.getPeople(def.getActiveUser());
			if (p != null) {
				setDepartment_name(p.getDepartment() != null ? p.getDepartment().getName() : "지정안됨");
			}
		}
		setSort(def.getSortNumber());
		setDescription(def.getDescription());
	}

	/**
	 * 설계변경 활동 정보
	 */
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
		setFinishDate(act.getFinishDate() == null ? null : act.getFinishDate().toString());
		setCompleteDate(act.getModifyTimestamp().toString().substring(0, 10));
		setState(act.getLifeCycleState().getDisplay());
		setCurrentInfo(act);
	}

	/**
	 * 설변 활동 정보
	 */
	private void setCurrentInfo(EChangeActivity act) throws Exception {
		String current = "";
		String state = act.getLifeCycleState().toString();
		// 완료에정일보다 오늘 날짜가 더 늦으면 지연
		Timestamp today = new Timestamp(new Date().getTime());
		Timestamp finish = act.getFinishDate();
		if (finish.after(today)) {
			current = "DELAY";
		} else {
			if ("INTAKE".equals(state)) {
				current = "STAND";
			} else if ("INWORK".equals(state)) {
				current = "PROGRESS";
			} else if ("COMPLETED".equals(state)) {
				current = "COMPLETED";
			}
		}
		setCurrent(current);
	}
}
