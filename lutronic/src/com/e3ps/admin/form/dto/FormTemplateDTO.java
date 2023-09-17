package com.e3ps.admin.form.dto;

import java.sql.Timestamp;

import com.e3ps.admin.form.FormTemplate;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormTemplateDTO {

	private String oid;
	private String name;
	private String number;
	private String creator;
	private String createdDate;
	private String formType;

	public FormTemplateDTO() {

	}

	public FormTemplateDTO(FormTemplate form) throws Exception {
		setOid(form.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(form.getName());
		setNumber(form.getNumber());
		setCreator(form.getOwnership().getOwner().getFullName());
		setCreatedDate(form.getCreateTimestamp().toString().substring(0, 10));
		setFormType(form.getFormType());
	}
}
