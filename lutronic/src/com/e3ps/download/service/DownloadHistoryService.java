package com.e3ps.download.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.e3ps.common.beans.BatchDownData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface DownloadHistoryService {

	/**
	 * 다운로드 이력 생성
	 */
	public abstract void create(String oid) throws Exception;

	/**
	 * 일괄 다운로드 이력
	 */
	public abstract void create(ArrayList<String> list) throws Exception;

}
