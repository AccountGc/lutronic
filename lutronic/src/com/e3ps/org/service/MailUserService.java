package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.e3ps.admin.dto.MailUserDTO;
import com.e3ps.org.MailWTobjectLink;

import wt.fc.Persistable;
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

	/**
	 * 외부메일 저장
	 */
	public abstract void mail(MailUserDTO dto) throws Exception;

	/**
	 * 외부 메일 유저 링크 등록 - 객제 결재시
	 */
	public abstract void saveLink(Persistable per, ArrayList<Map<String, String>> params) throws Exception;

	/**
	 * 외부 메일 유저 링크 삭제 - 객제 결재시
	 */
	public abstract void deleteLink(Map<String, Object> params) throws Exception;
}
