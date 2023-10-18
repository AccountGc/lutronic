package com.e3ps.workspace.dto;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;

@Getter
@Setter
public class EcaDTO {

	private String oid;
	private String eoid;
	private String name;
	private String number;
	private String eoType;
	private String createdDate_txt; // 도착일
	private String activityUser_txt; // 작업자
	private String finishDate_txt; // 완료 에정일
	private String state;
	private JSONArray docList = new JSONArray();

	public EcaDTO() {

	}

	public EcaDTO(String oid) throws Exception {
		this((EChangeActivity) CommonUtil.getObject(oid));
	}

	public EcaDTO(EChangeActivity eca) throws Exception {
		setOid(eca.getPersistInfo().getObjectIdentifier().getStringValue());
		setEoid(eca.getEo().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(eca.getEo().getEoName());
		setNumber(eca.getEo().getEoNumber());
		setEoType(eca.getEo().getEoType());
		setCreatedDate_txt(eca.getCreateTimestamp().toString().substring(0, 16));
		setActivityUser_txt(eca.getActiveUser().getFullName());
		setFinishDate_txt(eca.getFinishDate().toString().substring(0, 10));
		setState(eca.getLifeCycleState().getDisplay());
		setDocList(ActivityHelper.manager.docList(eca));
	}
}
