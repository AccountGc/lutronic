package com.e3ps.groupware.notice.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.e3ps.groupware.notice.beans.NoticeData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NoticeService {
	
	public void createNotice(NoticeData data) throws Exception;

	String create(Hashtable hash, String[] loc) throws Exception;

	String delete(String oid);

	Map<String, Object> modify(Map<String, Object> params) throws Exception;

	void updateCount(String oid) throws Exception;

	List<NoticeData> getPopUpNotice();

}
