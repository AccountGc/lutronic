package com.e3ps.common.code.dto;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberCodeDTO {

	private String oid;
	private String code;
	private String name;
	private String sort;
	private boolean enabled;
	private String description;
	private String codeType;

	/**
	 * 변수
	 */
	private String parentRowId;

	public NumberCodeDTO() {

	}

	public NumberCodeDTO(NumberCode n) throws Exception {
		setOid(n.getPersistInfo().getObjectIdentifier().getStringValue());
		setCode(n.getCode());
		setName(n.getName());
		setSort(n.getSort());
		setEnabled(!n.isDisabled());
		setDescription(n.getDescription());
		setCodeType(n.getCodeType().getDisplay());
	}
}
