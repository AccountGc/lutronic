package com.e3ps.system.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.system.SAPInterfacePartLogger;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class SendPartLoggerDTO {

	private String number;
	private String eoNumber;
	private String name;
	private String meins;
	private String zspec;
	private String zmodel;
	private String zprodm;
	private String zdept;
	private String zdwgno;
	private String version;
	private String zprepo;
	private String brgew;
	private String gewei;
	private String zmatlt;
	private String zpostp;
	private String zdevnd;
	private String result;
	private String creator;
	private String createdDate_txt;

	public SendPartLoggerDTO() {

	}

	public SendPartLoggerDTO(String oid) throws Exception {
		this((SAPInterfacePartLogger) CommonUtil.getObject(oid));
	}

	public SendPartLoggerDTO(SAPInterfacePartLogger logger) throws Exception {
		setNumber(logger.getMatnr());
		setName(logger.getMaktx());
		setEoNumber(logger.getAennr8());
		setMeins(logger.getMeins());
		setZspec(logger.getZspec());
		setZmodel(logger.getZmodel());
		setZprodm(logger.getZprodm());
		setZdept(logger.getZdept());
		setZdwgno(logger.getZdwgno());
		setVersion(logger.getZeivr());
		setZprepo(logger.getZprepo() == null ? "X" : "O");
		setBrgew(logger.getBrgew());
		setZmatlt(logger.getZmatlt());
		setZpostp(logger.getZpostp());
		setZdevnd(logger.getZdevnd());
		setResult(logger.getSendResult() ? "성공" : "실패");
		setCreator(logger.getOwnership().getOwner().getFullName());
		setCreatedDate_txt(logger.getCreateTimestamp().toString().substring(0, 16));
	}
}
