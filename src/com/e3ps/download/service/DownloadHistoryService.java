package com.e3ps.download.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface DownloadHistoryService {

	/**
	 * 다운로드 이력 생성
	 */
	public abstract void create(String oid) throws Exception;

}
