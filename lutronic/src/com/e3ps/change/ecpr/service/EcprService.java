package com.e3ps.change.ecpr.service;

import com.e3ps.change.cr.dto.CrDTO;
import com.e3ps.change.ecpr.dto.EcprDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EcprService {
	/**
	 * CR 등록
	 */
	public abstract void create(EcprDTO dto) throws Exception;
}
