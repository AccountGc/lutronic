package com.e3ps.doc.dto;

import com.e3ps.doc.DocumentClass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentClassDTO {

	private String oid;
	private String clazz;
	private String name;
	private String sort;
	private boolean enabled;
	private String description;
	private String classType;

	/**
	 * 변수
	 */
	private String parentRowId;

	public DocumentClassDTO() {

	}

	public DocumentClassDTO(DocumentClass n) throws Exception {
		setOid(n.getPersistInfo().getObjectIdentifier().getStringValue());
		setClazz(n.getClazz());
		setName(n.getName());
		setSort(n.getSort());
		setEnabled(!n.getEnabled());
		setDescription(n.getDescription());
		setClassType(n.getClassType().getDisplay());
	}
}
