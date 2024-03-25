package com.e3ps.system.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.system.SAPInterfaceBOMLogger;
import com.e3ps.system.SAPInterfacePartLogger;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendBOMLoggerDTO {

	private int rowNum;
	private String oldParentNumber;
	private String oldChildNumber;
	private String oldParentVersion;
	private String oldChildVersion;
	private String newParentNumber;
	private String newChildNumber;
	private String newParentVersion;
	private String newChildVersion;
	private String qty;
	private String sendType;
	private String creator;
	private String createdDate_txt;

	public SendBOMLoggerDTO() {

	}

	public SendBOMLoggerDTO(String oid) throws Exception {
		this((SAPInterfaceBOMLogger) CommonUtil.getObject(oid));
	}

	public SendBOMLoggerDTO(SAPInterfaceBOMLogger logger) throws Exception {
		setOldParentNumber(logger.getOldParentNumber());
		setOldChildNumber(logger.getOldChildNumber());
		setOldParentVersion(logger.getOldParentVersion());
		setOldChildVersion(logger.getOldChildVersion());
		setNewParentNumber(logger.getNewParentNumber());
		setNewChildNumber(logger.getNewChildNumber());
		setNewParentVersion(logger.getNewParentVersion());
		setNewChildVersion(logger.getNewChildVersion());
		setQty(logger.getQty());
		setSendType(logger.getSendType());
		setCreator(logger.getOwnership().getOwner().getFullName());
		setCreatedDate_txt(logger.getCreateTimestamp().toString().substring(0, 16));
	}
}
