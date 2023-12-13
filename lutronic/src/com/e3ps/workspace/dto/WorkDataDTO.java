package com.e3ps.workspace.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.WorkData;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.fc.Persistable;

@Getter
@Setter
public class WorkDataDTO {

	private String oid;
	private String poid;
	private String persistType;
	private String number;
	private String name;
	private String state;
	private String creator;
	private String createdDate_txt;

	// 결재 변수
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	// 외부 메일 변수
	private ArrayList<Map<String, String>> external = new ArrayList<Map<String, String>>();

	private String description;

	public WorkDataDTO() {

	}

	public WorkDataDTO(String oid) throws Exception {
		this((WorkData) CommonUtil.getObject(oid));
	}

	public WorkDataDTO(WorkData workData) throws Exception {
		setOid(workData.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(workData.getPer().getPersistInfo().getObjectIdentifier().getStringValue());
		setInfo(workData.getPer());
	}

	/**
	 * 결재선 지정 대상 객체 정보
	 */
	private void setInfo(Persistable per) throws Exception {
		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			setName(doc.getName());
			setNumber(doc.getNumber());
			setName(doc.getName());
			setPersistType("문사");
			setState(doc.getLifeCycleState().getDisplay());
			setCreator(doc.getCreatorName());
			setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeRequest) {
			EChangeRequest ecr = (EChangeRequest) per;
			setNumber(ecr.getEoNumber());
			setName(ecr.getEoName());
			setPersistType("CR");
			setState(ecr.getLifeCycleState().getDisplay());
			setCreator(ecr.getCreatorName());
			setCreatedDate_txt(ecr.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			if (eco.getEoType().equals("CHANGE")) {
				setPersistType("ECO");
			} else {
				setPersistType("EO");
			}
			setNumber(eco.getEoNumber());
			setName(eco.getEoName());
			setState(eco.getLifeCycleState().getDisplay());
			setCreator(eco.getCreatorName());
			setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof ROHSMaterial) {
			ROHSMaterial rohs = (ROHSMaterial) per;
			setNumber(rohs.getNumber());
			setName(rohs.getName());
			setPersistType("ROHS");
			setState(rohs.getLifeCycleState().getDisplay());
			setCreator(rohs.getCreatorName());
			setCreatedDate_txt(rohs.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			setNumber(asm.getNumber());
			setName(asm.getName());
			setPersistType("일괄결재");
			setState(asm.getLifeCycleState().getDisplay());
			setCreator(asm.getCreatorName());
			setCreatedDate_txt(asm.getCreateTimestamp().toString().substring(0, 10));
		}
	}
}
