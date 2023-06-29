package com.e3ps.groupware.workprocess.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EventAsmService {
	
	void eventListener(Object _obj, String _event) throws Exception;

}
