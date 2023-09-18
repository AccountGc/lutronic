package com.e3ps.admin.form.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface FormTemplateService {

	/**
	 * 문서 양식 등록
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 문서 양식 로더
	 */
	public abstract void loader() throws Exception;

	/**
	 * 문서 양식 수정
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

	/**
	 * 문서 양식 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
