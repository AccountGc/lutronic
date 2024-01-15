package com.e3ps.change.ecn.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeNotice;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcnColumn {
	private int rowNum;
	private String oid;
	private String partName;
	private String partNumber;
	private String ecoNumber;
	private String eoid;
	private String number;
	private String name;
	private String progress;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String worker_oid;

	public EcnColumn() {

	}

	public EcnColumn(Object[] obj) throws Exception {
		this((EChangeNotice) obj[0]);
	}

	public EcnColumn(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setPartName(ecn.getPartName());
		setPartNumber(ecn.getPartNumber());
		setEcoNumber(ecn.getEco() != null ? ecn.getEco().getEoNumber() : "");
		setEoid(ecn.getEco().getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(ecn.getEoNumber());
		setName(ecn.getEoName());
		setProgress(ecn.getProgress());
		setCreator(ecn.getCreatorFullName());
		setCreatedDate(ecn.getCreateTimestamp());
		setCreatedDate_txt(ecn.getCreateTimestamp().toString().substring(0, 10));
		setWorker_oid(
				ecn.getWorker() != null ? ecn.getWorker().getPersistInfo().getObjectIdentifier().getStringValue() : "");
	}
}
