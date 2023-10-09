package com.e3ps.change.ecn.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcnService {

	/**
	 * ECN 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
