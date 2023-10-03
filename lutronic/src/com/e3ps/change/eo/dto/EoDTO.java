package com.e3ps.change.eo.dto;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EoDTO {

	private String oid;

	public EoDTO() {

	}

	public EoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EoDTO(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
	}
}
