package com.e3ps.auth.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface E3PSService {

	boolean isAuth(String _codeA, String _codeB, String _codeC);

	boolean isMember(String _codeA, String _codeB, String _codeC);

	boolean isGroupMember(String _codeA, String _codeB, String _codeC);

}
