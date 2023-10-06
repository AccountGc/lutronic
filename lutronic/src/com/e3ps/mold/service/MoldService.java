package com.e3ps.mold.service;

import java.util.Map;

import com.e3ps.mold.dto.MoldDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface MoldService {
	public void create(MoldDTO dto) throws Exception;
	
	public void revise(Map<String, Object> params) throws Exception;
}
