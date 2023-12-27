package com.e3ps.admin.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.MailUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import com.e3ps.org.MailUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailUserDTO {

	private String oid;
	private String name;
	private String email;
	private boolean enable;

	/**
	 * 변수
	 */
	@JsonIgnore
	private ArrayList<Map<String, Object>> addRow = new ArrayList<Map<String, Object>>();
	@JsonIgnore
	private ArrayList<Map<String, Object>> editRow = new ArrayList<Map<String, Object>>();
	@JsonIgnore
	private ArrayList<Map<String, Object>> removeRow = new ArrayList<Map<String, Object>>();

	public MailUserDTO() {

	}

	public MailUserDTO(Object[] obj) throws Exception {
		this((MailUser) obj[0]);
	}

	public MailUserDTO(MailUser mailUser) throws Exception {
		setOid(mailUser.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(mailUser.getName());
		setEmail(mailUser.getEmail());
		setEnable(mailUser.isIsDisable());
	}
}
