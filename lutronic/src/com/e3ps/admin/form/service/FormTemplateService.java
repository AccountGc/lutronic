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

}
