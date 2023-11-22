package com.e3ps.workspace.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.mold.dto.MoldDTO;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.WorkData;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.lifecycle.LifeCycleManaged;

@Getter
@Setter
public class WorkDataColumn {

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

	public WorkDataColumn() {

	}

	public WorkDataColumn(Object[] obj) throws Exception {
		this((WorkData) obj[0]);
	}

	public WorkDataColumn(WorkData workData) throws Exception {
		setOid(workData.getPersistInfo().getObjectIdentifier().getStringValue());
		setRead(workData.getReads());
		setWork("결재선 지정");
		setPoid(workData.getPer().getPersistInfo().getObjectIdentifier().getStringValue());
		setInfo(workData.getPer());
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
			setPersistType("문사");
			setState(doc.getLifeCycleState().getDisplay());
			setCreator(doc.getCreatorName());
			setCreatedDate(doc.getCreateTimestamp());
			setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			setNumber(eco.getEoNumber());
			setName(eco.getEoName());

			if (eco.getEoType().equals("CHANGE")) {
				setPersistType("ECO");
			} else {
				setPersistType("EO");
			}

			setState(eco.getLifeCycleState().getDisplay());
			setCreator(eco.getCreatorName());
			setCreatedDate(eco.getCreateTimestamp());
			setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof EChangeRequest) {
			EChangeRequest ecr = (EChangeRequest) per;
			setNumber(ecr.getEoNumber());
			setName(ecr.getEoName());
			setPersistType("CR");
			setState(ecr.getLifeCycleState().getDisplay());
			setCreator(ecr.getCreatorName());
			setCreatedDate(ecr.getCreateTimestamp());
			setCreatedDate_txt(ecr.getCreateTimestamp().toString().substring(0, 10));
		} else if (per instanceof ROHSMaterial) {
			ROHSMaterial rohs = (ROHSMaterial) per;
			setNumber(rohs.getNumber());
			setName(rohs.getName());
			setPersistType("ROHS");
			setState(rohs.getLifeCycleState().getDisplay());
			setCreator(rohs.getCreatorName());
			setCreatedDate(rohs.getCreateTimestamp());
			setCreatedDate_txt(rohs.getCreateTimestamp().toString().substring(0, 10));
		}
	}
}
