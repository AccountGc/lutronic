package com.e3ps;

import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;

import wt.doc.WTDocument;
import wt.lifecycle.LifeCycleManaged;

public class SendMailTest {

	public static void main(String[] args) throws Exception {

		WTDocument doc = (WTDocument) CommonUtil.getObject("wt.doc.WTDocument:153521");

		MailUtils.manager.sendWorkDataMail(doc, "결재선지정", "결재선지정");
//		System.exit(0);
	}
}
