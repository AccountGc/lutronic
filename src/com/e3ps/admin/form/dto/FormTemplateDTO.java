package com.e3ps.admin.form.dto;

import java.sql.Timestamp;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormTemplateDTO {

	private String oid;
	private String name;
	private String number;
	private int version;
	private String description;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String formType;

	public FormTemplateDTO() {

	}

	public FormTemplateDTO(String oid) throws Exception {
		this((FormTemplate) CommonUtil.getObject(oid));
	}

	public FormTemplateDTO(FormTemplate form) throws Exception {
		setOid(form.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(form.getName());
		setNumber(form.getNumber());
		setVersion(form.getVersion());
		setDescription(form.getDescription());
		setCreator(form.getOwnership().getOwner().getFullName());
		setCreatedDate(form.getCreateTimestamp());
		setFormType(form.getFormType());
	}
}
