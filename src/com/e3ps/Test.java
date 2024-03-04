package com.e3ps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadUser;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class Test {

	public static void main(String[] args) throws Exception {

		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		String today = DateUtil.getToDay();
		String id = user.getName();

		String path = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + today + File.separator
				+ id;

		String oid = "wt.doc.WTDocument:1574033";
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		while (qr.hasMoreElements()) {
			ApplicationData dd = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
			String name = dd.getFileName();
			File file = new File(path + File.separator + name);
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		qr.reset();
		qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData dd = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
			String name = dd.getFileName();
			File file = new File(path + File.separator + name);
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		System.out.println("종료!");

		System.exit(0);
	}
}