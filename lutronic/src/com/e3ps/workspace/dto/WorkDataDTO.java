package com.e3ps.workspace.dto;

import com.e3ps.common.util.CommonUtil;
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
		}
	}
}
