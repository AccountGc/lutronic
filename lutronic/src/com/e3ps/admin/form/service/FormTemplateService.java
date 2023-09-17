package com.e3ps.admin.form.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface FormTemplateService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
