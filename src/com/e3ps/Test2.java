package com.e3ps;

import java.util.ArrayList;

import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;

import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.org.WTUser;

public class Test2 {

	public static void main(String[] args) throws Exception {

		int list = 2;
		int loop = 2 / 45;
		int gap = 2 % 45;
		System.out.println(loop);
		System.out.println(gap);
		if(gap > 0) {
			loop = loop + 1;
		}
		
		System.out.println(loop);

		System.exit(0);

	}
}