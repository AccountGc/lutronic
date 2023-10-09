package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ActivityService {

	/**
	 * 설변활동 삭제
	 */
	public abstract Map<String, Object> delete(Map<String, Object> params) throws Exception;

	/**
	 * 설변 루트 빛 활동 생성
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 설변활동 그리드 저장
	 */
	public abstract void save(HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap) throws Exception;

	/**
	 * 설변루트 수정 함수
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

	/**
	 * EO 설변 활동 생성
	 */
	public abstract void saveActivity(EChangeOrder eo, ArrayList<Map<String, String>> list) throws Exception;

}
