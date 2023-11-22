package com.e3ps.workspace.column;

import java.sql.Timestamp;

import com.e3ps.workspace.WorkData;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.fc.Persistable;

@Getter
@Setter
public class WorkDataColumn {

	private String oid;
	private String name;
	private String creator;
	private String state;
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
		setInfo(workData.getPer());
	}

	/**
	 * 결재 데이터 정보 
	 */
	private void setInfo(Persistable per) throws Exception {
		
		
	}
}
