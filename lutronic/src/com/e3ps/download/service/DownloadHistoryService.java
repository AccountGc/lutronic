package com.e3ps.download.service;

import java.util.Hashtable;
import java.util.List;

import com.e3ps.common.beans.BatchDownData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface DownloadHistoryService {

	String createDownloadHistory(Hashtable hash) throws Exception;

	void createBatchDownloadHistory(List<BatchDownData> targetlist)
			throws Exception;

}
