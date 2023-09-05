package com.e3ps.org.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.e3ps.org.MailWTobjectLink;

import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.query.QuerySpec;

@RemoteInterface
public interface MailUserService {

	Vector<MailWTobjectLink> getMailUserLinkList(String oid);

	QuerySpec getQuery(HashMap map);

	Map<String, String> createMailUser(HashMap map);

	Vector<MailWTobjectLink> createMailUserLink(HashMap map) throws Exception;

	boolean deleteMailUserLink(HashMap map) throws Exception;

	Map<String, String> modifyMailUser(HashMap map);

	Map<String, String> deleteMailUser(String oid);

	void sendWTObjectMailUser(WTObject wtobject);

	public void adminMailSave(Map<String, Object> params) throws Exception;
}
