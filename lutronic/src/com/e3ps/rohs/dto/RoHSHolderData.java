package com.e3ps.rohs.dto;

import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoHSHolderData {

	private String oid;
	private ROHSMaterial rohs;
	private String fileType;
	private String publicationDate;
	private String fileName;
	private String primary;

	public RoHSHolderData(ROHSContHolder holder) throws Exception {
		setOid(holder.getPersistInfo().getObjectIdentifier().toString());
		setRohs(holder.getRohs());
		setFileType(holder.getFileType());
		setPublicationDate(holder.getPublicationDate());
		setFileName(holder.getFileName());
		setPrimary(AUIGridUtil.primary(holder));
	}
}
