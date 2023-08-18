package com.e3ps.common.content.service;

import java.io.File;
import java.util.List;

import com.e3ps.common.beans.BatchDownData;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.fv.uploadtocache.CacheDescriptor;
import wt.fv.uploadtocache.CachedContentDescriptor;

public interface CommonContentService {
	
	/**
	 * 첨부 파일 파일볼트 업로드
	 */
	public abstract CachedContentDescriptor doUpload(CacheDescriptor localCacheDescriptor, File file) throws Exception;

	ContentHolder attachPrimary(ContentHolder holder, String loc) throws Exception;

	ContentHolder attach(ContentHolder holder, String loc) throws Exception;

	ContentHolder attach(ContentHolder holder, String loc, String desc) throws Exception;

	ContentHolder attach(ContentHolder holder, String loc, String desc, ContentRoleType contentRoleType) throws Exception;

	ContentHolder delete(ContentHolder holder, ContentItem deleteItem) throws Exception;

	ContentHolder delete(ContentHolder holder) throws Exception;

	ContentHolder attach(ContentHolder holder, ApplicationData data, boolean isPrimary) throws Exception;

	ContentHolder attach(ContentHolder holder, ApplicationData data,
			ContentRoleType contentRoleType) throws Exception;
	
	ContentHolder attach(ContentHolder holder, CachedContentDescriptor casheDes, String fileName, String desc, ContentRoleType contentRoleType) throws Exception;
	
	ContentHolder attach(ContentHolder holder, String primary, String[] secondary) throws Exception ;
	
	ContentHolder attach(ContentHolder holder, String primary, String[] secondary, String[] delSecondary) throws Exception ;
	
	ContentHolder attach(ContentHolder holder, String primary, String[] secondary, String[] delSecondary, boolean isWorkingCopy) throws Exception ;

	ApplicationData attachADDRole(ContentHolder holder, String roleType,
			String cachFile, boolean isWorkingCopy) throws Exception;

	ApplicationData attachROHS(ContentHolder holder,
			CachedContentDescriptor casheDes, String fileName, String desc,
			ContentRoleType contentRoleType) throws Exception;
	
	String copyApplicationData(String appOid) throws Exception;

	ContentHolder attach(ContentHolder holder, String primary,
			String[] secondary, String[] delSecondary, String description,
			boolean isWorkingCopy) throws Exception;

	void fileDown(String appOid) throws Exception;

	List<ApplicationData> getAttachFileList(ContentHolder holder,
			String contentRoleType, List<ApplicationData> list) throws Exception;

	List<BatchDownData> getAttachFileList(ContentHolder holder,
			List<BatchDownData> list, String describe) throws Exception;

	BatchDownData setBatchDownData(ContentHolder holder, BatchDownData data);

	ApplicationData attachADDRole(ContentHolder holder, String roleType,
			String cachFile, String delFileName, boolean b) throws Exception;
	
	void clear(ContentHolder holder) throws Exception;
}
