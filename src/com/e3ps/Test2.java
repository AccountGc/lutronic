package com.e3ps;

import java.util.ArrayList;

import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;

import wt.doc.WTDocument;
import wt.org.WTUser;

public class Test2 {

	public static void main(String[] args) throws Exception {

		String oid = "wt.doc.WTDocument:1532391";
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

		WTUser user = (WTUser) CommonUtil.getObject("wt.org.WTUser:311774");

		ArrayList<WTUser> ll = new ArrayList<>();

		ll.add(user);
		ll.add(user);
		ll.add(user);
		ll.add(user);

		MailUtils.manager.sendAgreeMailTest(doc, ll);

		System.exit(0);

	}
}