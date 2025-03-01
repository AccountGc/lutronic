package com.e3ps.workspace.column;

import java.sql.Timestamp;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.WorkData;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.part.WTPart;

@Getter
@Setter
public class WorkDataColumn {

	private int rowNum;
	private String oid;
	private boolean read;
	private String poid;
	private String work;
	private String persistType;
	private String number;
	private String name;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String viewUrl;

	private final String context = "/Windchill/plm";

	public WorkDataColumn() {

	}

	public WorkDataColumn(Object[] obj) throws Exception {
		this((WorkData) obj[0]);
	}

	public WorkDataColumn(WorkData workData) throws Exception {
		setOid(workData.getPersistInfo().getObjectIdentifier().getStringValue());
		setRead(workData.getReads());
		setWork("결재선 지정");
		if (workData.getPer() != null) {
			setPoid(workData.getPer().getPersistInfo().getObjectIdentifier().getStringValue());
			setInfo(workData.getPer());
		}
	}

	/**
	 * 결재 데이터 정보
	 */
	private void setInfo(Persistable per) throws Exception {
		// 객체별 데이터 세팅
		if (per instanceof WTDocument) {
			// 세부 구분 금형문서, 일반문서
			WTDocument doc = (WTDocument) per;
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
			setCreator(doc.getCreatorFullName());
			setCreatedDate(doc.getCreateTimestamp());
			setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			setNumber(eco.getEoNumber());
			setName(eco.getEoName());

			if (eco.getEoType().equals("CHANGE")) {
				setPersistType("ECO");
				setViewUrl(this.context + "/eco/view?oid=" + getPoid());
			} else {
				setPersistType("EO");
				setViewUrl(this.context + "/eo/view?oid=" + getPoid());
			}

			setState(eco.getLifeCycleState().getDisplay());
			setCreator(eco.getCreatorFullName());
			setCreatedDate(eco.getCreateTimestamp());
			setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeRequest) {
			EChangeRequest ecr = (EChangeRequest) per;
			setNumber(ecr.getEoNumber());
			setName(ecr.getEoName());
			setPersistType("CR");
			setState(ecr.getLifeCycleState().getDisplay());
			setCreator(ecr.getCreatorFullName());
			setCreatedDate(ecr.getCreateTimestamp());
			setCreatedDate_txt(ecr.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/cr/view?oid=" + getPoid());
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			String number = asm.getNumber();
			setNumber(number);

			if (number.startsWith("NDBT")) {
				setPersistType("일괄결재(문서)");
			} else if (number.startsWith("ROHSBT")) {
				setPersistType("일괄결재(ROHS)");
			} else if (number.startsWith("MMBT")) {
				setPersistType("일괄결재(금형문서)");
			} else if (number.startsWith("AMBT")) {
				setPersistType("일괄결재(병리연구문서)");
			} else if (number.startsWith("BMBT")) {
				setPersistType("일괄결재(임상개발문서)");
			} else if (number.startsWith("CMBT")) {
				setPersistType("일괄결재(화장품문서)");
			}
			setName(asm.getName());
			setState(asm.getLifeCycleState().getDisplay());
			setCreator(asm.getCreatorFullName());
			setCreatedDate(asm.getCreateTimestamp());
			setCreatedDate_txt(asm.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/asm/view?oid=" + getPoid());
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			setNumber(part.getNumber());
			setName(part.getName());
			setPersistType("품목");
			setState(part.getLifeCycleState().getDisplay());
			setCreator(part.getCreatorFullName());
			setCreatedDate(part.getCreateTimestamp());
			setCreatedDate_txt(part.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/part/view?oid=" + getPoid());
		} else if (per instanceof ECPRRequest) {
			ECPRRequest ecpr = (ECPRRequest) per;
			setNumber(ecpr.getEoNumber());
			setName(ecpr.getEoName());
			setPersistType("ECPR");
			setState(ecpr.getLifeCycleState().getDisplay());
			setCreator(ecpr.getCreatorFullName());
			setCreatedDate(ecpr.getCreateTimestamp());
			setCreatedDate_txt(ecpr.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/ecpr/view?oid=" + getPoid());
		} else if (per instanceof ECRMRequest) {
			ECRMRequest ecrm = (ECRMRequest) per;
			setNumber(ecrm.getEoNumber());
			setName(ecrm.getEoName());
			setPersistType("ECRM");
			setState(ecrm.getLifeCycleState().getDisplay());
			setCreator(ecrm.getCreatorFullName());
			setCreatedDate(ecrm.getCreateTimestamp());
			setCreatedDate_txt(ecrm.getCreateTimestamp().toString().substring(0, 10));
			setViewUrl(this.context + "/ecrm/view?oid=" + getPoid());
		}
	}
}
