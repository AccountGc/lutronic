package com.e3ps.change.ecn.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcnColumn {

	private String oid;
	private String number;
	private String name;
	private String state;
	private String creator;
	private String worker;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;

	public EcnColumn() {

	}

	public EcnColumn(Object[] obj) throws Exception {
		this((EChangeNotice) obj[0]);
	}

	public EcnColumn(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(ecn.getEoNumber());
		setName(ecn.getEoName());
		setState(ecn.getLifeCycleState().getDisplay());
		setCreator(ecn.getCreatorFullName());
		setWorker(ecn.getOwnership().getOwner().getFullName());
		setCreatedDate(ecn.getCreateTimestamp());
		setCreatedDate_txt(ecn.getCreateTimestamp().toString().substring(0, 10));
	}
}
