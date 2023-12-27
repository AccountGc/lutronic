package com.e3ps.change.service;

import java.util.List;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.beans.ECNData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.ECRData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ECNService {
	void createECN(EChangeOrder _eco) throws Exception;
	List<ECNData> include_ecnList(String oid) throws Exception;
	public void create(ECNData data) throws Exception;
}
