package com.e3ps.system.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.system.SAPInterfaceBOMLogger;
import com.e3ps.system.SAPInterfacePartLogger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendBOMLoggerDTO {

	public SendBOMLoggerDTO() {

	}

	public SendBOMLoggerDTO(String oid) throws Exception {
		this((SAPInterfaceBOMLogger) CommonUtil.getObject(oid));
	}

	public SendBOMLoggerDTO(SAPInterfaceBOMLogger logger) throws Exception {

	}

}
