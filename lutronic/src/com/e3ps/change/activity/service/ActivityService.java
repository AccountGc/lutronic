package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ActivityService {

	/**
	 * 설변활동 삭제
	 */
	public abstract Map<String, Object> delete(Map<String, Object> params) throws Exception;

	/**
	 * 설변 루트 빛 활동 생성
	 * 
	 * @param params
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

}
