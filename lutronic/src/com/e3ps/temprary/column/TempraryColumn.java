package com.e3ps.temprary.column;

import java.sql.Timestamp;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.rohs.ROHSMaterial;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.lifecycle.LifeCycleManaged;
import wt.part.WTPart;

@Getter
@Setter
public class TempraryColumn {

	private String oid;
	private String name;
	private String number;
	private String dataType;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;

	public TempraryColumn() {

	}

	public TempraryColumn(Object[] obj) throws Exception {
		this((LifeCycleManaged) obj[0]);
	}

	public TempraryColumn(LifeCycleManaged lcm) throws Exception {
		setOid(lcm.getPersistInfo().getObjectIdentifier().getStringValue());
		setInfo(lcm);
		setState(lcm.getLifeCycleState().getDisplay());
	}

	/**
	 * 기본객체 설정값
	 */
	private void setInfo(LifeCycleManaged lcm) throws Exception {
		 if(lcm instanceof ROHSMaterial) {
			// 물질
			ROHSMaterial rohs = (ROHSMaterial) lcm;
			setName(rohs.getName());
			setNumber(rohs.getNumber());
			setDataType("ROHS");
			setCreator(rohs.getCreatorFullName());
			setCreatedDate(rohs.getCreateTimestamp());
			setCreatedDate_txt(rohs.getCreateTimestamp().toString().substring(0, 10));
		 }else if (lcm instanceof WTDocument) {
			// 문서
			WTDocument doc = (WTDocument) lcm;
			setName(doc.getName());
			setNumber(doc.getNumber());
			setCreator(doc.getCreatorFullName());
			setCreatedDate(doc.getCreateTimestamp());
			setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
			if ("$$MMDocument".equals(doc.getDocType().toString())) {
				setDataType("금형");
			} else {
				setDataType("문서");				
			}
		}else if(lcm instanceof EChangeRequest) {
			// CR 
			EChangeRequest cr = (EChangeRequest) lcm;
			setName(cr.getEoName());
			setNumber(cr.getEoNumber());
			setDataType("CR");
			setCreator(cr.getCreatorFullName());
			setCreatedDate(cr.getCreateTimestamp());
			setCreatedDate_txt(cr.getCreateTimestamp().toString().substring(0, 10));
		} else if (lcm instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) lcm;
			if (eco.getEoType().equals("CHANGE")) {
				// ECO
				setName(eco.getName());
				setNumber(eco.getEoNumber());
				setDataType("ECO");
				setCreator(eco.getCreatorFullName());
				setCreatedDate(eco.getCreateTimestamp());
				setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
			} else {
				// EO
				setName(eco.getName());
				setNumber(eco.getEoNumber());
				setDataType("EO");
				setCreator(eco.getCreatorFullName());
				setCreatedDate(eco.getCreateTimestamp());
				setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
			}
		} else if (lcm instanceof EChangeNotice) {
			// ECN
			EChangeNotice ecn = (EChangeNotice) lcm;
			setName(ecn.getName());
			setNumber(ecn.getEoNumber());
			setDataType("ECN");
			setCreator(ecn.getCreatorFullName());
			setCreatedDate(ecn.getCreateTimestamp());
			setCreatedDate_txt(ecn.getCreateTimestamp().toString().substring(0, 10));
		} else if (lcm instanceof ECPRRequest) {
			// ECPR
			ECPRRequest ecpr = (ECPRRequest) lcm;
			setName(ecpr.getName());
			setNumber(ecpr.getEoNumber());
			setDataType("ECPR");
			setCreator(ecpr.getCreatorFullName());
			setCreatedDate(ecpr.getCreateTimestamp());
			setCreatedDate_txt(ecpr.getCreateTimestamp().toString().substring(0, 10));
		} else if (lcm instanceof WTPart) {
			// 품목
			WTPart part = (WTPart) lcm;
			setName(part.getName());
			setNumber(part.getNumber());
			setDataType("품목");
			setCreator(part.getCreatorFullName());
			setCreatedDate(part.getCreateTimestamp());
			setCreatedDate_txt(part.getCreateTimestamp().toString().substring(0, 10));
		} else if (lcm instanceof EPMDocument) {
			// 도면
			EPMDocument drawing = (EPMDocument) lcm;
			setName(drawing.getName());
			setNumber(drawing.getNumber());
			setDataType("도면");
			setCreator(drawing.getCreatorFullName());
			setCreatedDate(drawing.getCreateTimestamp());
			setCreatedDate_txt(drawing.getCreateTimestamp().toString().substring(0, 10));
		}
	}
}
