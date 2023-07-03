package com.e3ps.rohs.beans;

import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoHSHolderData {

	public String oid;
	
	public ROHSMaterial rohs;
	
	public String fileType;
	public String publicationDate;
	public String fileName;
	
	public String manufaturer;
	
	public RoHSHolderData(ROHSContHolder holder) {
		
		setOid( holder.getPersistInfo().getObjectIdentifier().toString());
		setRohs(holder.getRohs());
		setFileType(holder.getFileType());
		setPublicationDate(holder.getPublicationDate());
		setFileName(holder.getFileName());
		setManufaturer("");
	}
	
}
