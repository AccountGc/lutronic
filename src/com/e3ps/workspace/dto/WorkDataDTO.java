package com.e3ps.workspace.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
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
	private boolean isValidate = false;

	private String viewUrl;
	private final String context = "/Windchill/plm";

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
		if (workData.getPer() != null) {
			setInfo(workData.getPer());
		}
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
			String docType = doc.getDocType().toString();
			if ("$$MMDocument".equals(docType)) {
				setPersistType("금형문서");
				setViewUrl(this.context + "/mold/view?oid=" + getPoid());
			} else if ("$$ROHS".equals(docType)) {
				setPersistType("ROHS");
				setViewUrl(this.context + "/rohs/view?oid=" + getPoid());
			} else {
				setPersistType("문서");
				setViewUrl(this.context + "/doc/view?oid=" + getPoid());
			}
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
			setViewUrl(this.context + "/cr/view?oid=" + getPoid());
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			if (eco.getEoType().equals("CHANGE")) {
				setPersistType("ECO");
				setViewUrl(this.context + "/eco/view?oid=" + getPoid());
			} else {
				setPersistType("EO");
				setViewUrl(this.context + "/eo/view?oid=" + getPoid());
			}
			setValidate(true);
			setNumber(eco.getEoNumber());
			setName(eco.getEoName());
			setState(eco.getLifeCycleState().getDisplay());
			setCreator(eco.getCreatorName());
			setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
//		} else if (per instanceof ROHSMaterial) {
//			ROHSMaterial rohs = (ROHSMaterial) per;
//			setNumber(rohs.getNumber());
//			setName(rohs.getName());
//			setPersistType("ROHS");
//			setState(rohs.getLifeCycleState().getDisplay());
//			setCreator(rohs.getCreatorName());
//			setCreatedDate_txt(rohs.getCreateTimestamp().toString().substring(0, 10));
//			setViewUrl(this.context + "/rohs/view?oid=" + getPoid());
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			setNumber(asm.getNumber());
			setName(asm.getName());
			setPersistType("일괄결재");
			setState(asm.getLifeCycleState().getDisplay());
			setCreator(asm.getCreatorName());
			setCreatedDate_txt(asm.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/asm/view?oid=" + getPoid());
		} else if (per instanceof ECPRRequest) {
			ECPRRequest ecpr = (ECPRRequest) per;
			setNumber(ecpr.getEoNumber());
			setName(ecpr.getEoName());
			setPersistType("ECPR");
			setState(ecpr.getLifeCycleState().getDisplay());
			setCreator(ecpr.getCreatorName());
			setCreatedDate_txt(ecpr.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/ecpr/view?oid=" + getPoid());
		}
	}
}
