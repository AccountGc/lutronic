package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ActivityService {

	/**
	 * 설변활동 삭제
	 */
	public abstract void delete(Map<String, ArrayList<Map<String, String>>> params) throws Exception;

}
