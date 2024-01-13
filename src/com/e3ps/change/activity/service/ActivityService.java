package com.e3ps.change.activity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;

import wt.method.RemoteInterface;
import wt.part.WTPart;

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

	/**
	 * EO 설변 활동 삭제
	 */
	public abstract void deleteActivity(EChangeOrder eo) throws Exception;

	/**
	 * 설변활동 + 산출물 링크 등록
	 */
	public abstract void saveLink(Map<String, Object> params) throws Exception;

	/**
	 * 설변활동 산출물 링크 삭제
	 */
	public abstract void deleteLink(String oid) throws Exception;

	/**
	 * ECA 활동 완료 이벤트
	 */
	public abstract void complete(Map<String, Object> params) throws Exception;

	/**
	 * ECO 대상품목 일괄 개정 함수
	 */
	public abstract void revise(Map<String, Object> params) throws Exception;

	/**
	 * EO 대상 품목 수정
	 */
	public abstract Map<String, Object> replace(ArrayList<LinkedHashMap<String, Object>> addRows,
			ArrayList<LinkedHashMap<String, Object>> removeRows, String oid) throws Exception;

	/**
	 * ECO 활동중 이전 품목 추가
	 */
	public abstract void prev(Map<String, Object> params) throws Exception;

	/**
	 * ECO 활동 중 품목 데이터 저장
	 */
	public abstract void saveData(Map<String, Object> params) throws Exception;

	/**
	 * 설변 삭제 품목 저장
	 */
	public abstract void saveRemoveData(WTPart part, EChangeOrder eco) throws Exception;
}
