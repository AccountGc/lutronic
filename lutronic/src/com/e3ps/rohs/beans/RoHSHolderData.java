package com.e3ps.rohs.beans;

import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;

public class RoHSHolderData {

	public String oid;
	
	public ROHSMaterial rohs;
	
	public String fileType;
	public String publicationDate;
	public String fileName;
	
	public String manufaturer;
	
	public RoHSHolderData(ROHSContHolder holder) {
		
		this.rohs = holder.getRohs();
		
		this.oid = holder.getPersistInfo().getObjectIdentifier().toString();
		
		this.fileType = holder.getFileType();
		this.publicationDate = holder.getPublicationDate();
		this.fileName = holder.getFileName();
		
		this.manufaturer = "";
	}

	public ROHSMaterial getRohs() {
		return rohs;
	}

	public void setRohs(ROHSMaterial rohs) {
		this.rohs = rohs;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getManufaturer() {
		return manufaturer;
	}

	public void setManufaturer(String manufaturer) {
		this.manufaturer = manufaturer;
	}
	
}
