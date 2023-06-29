package com.e3ps.groupware.notice.service;

import java.util.Hashtable;
import java.util.List;

import com.e3ps.common.beans.ResultData;
import com.e3ps.groupware.notice.beans.NoticeData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NoticeService {

	String create(Hashtable hash, String[] loc) throws Exception;

	String delete(String oid);

	String modify(Hashtable hash, String[] loc, String[] deloc) throws Exception;

	void updateCount(String oid) throws Exception;

	List<NoticeData> getPopUpNotice();

}
