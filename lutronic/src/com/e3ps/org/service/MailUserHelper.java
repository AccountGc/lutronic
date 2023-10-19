package com.e3ps.org.service;

import java.util.ArrayList;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.MailWTobjectLink;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.services.ServiceFactory;

public class MailUserHelper {
	public static final MailUserService service = ServiceFactory.getService(MailUserService.class);
	public static final MailUserHelper manager = new MailUserHelper();

	/**
	 * 외부메일 유저+객체 링크 리스트
	 */
	public ArrayList<MailWTobjectLink> navigate(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		return navigate(per);
	}

	/**
	 * 외부메일 유저+객체 링크 리스트
	 */
	public ArrayList<MailWTobjectLink> navigate(Persistable per) throws Exception {
		ArrayList<MailWTobjectLink> list = new ArrayList<MailWTobjectLink>();
		QueryResult qr = PersistenceHelper.manager.navigate((WTObject) per, "user", MailWTobjectLink.class, false);
		while (qr.hasMoreElements()) {
			MailWTobjectLink link = (MailWTobjectLink) qr.nextElement();
			list.add(link);
		}
		return list;
	}

}
