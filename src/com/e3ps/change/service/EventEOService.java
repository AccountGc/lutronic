package com.e3ps.change.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EventEOService {

	void eventListener(Object _obj, String _event) throws Exception;

}
