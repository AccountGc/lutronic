<%@page import="wt.epm.structure.EPMContainedIn"%>
<%@page import="wt.epm.familytable.EPMSepFamilyTable"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.epm.EPMDocument"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

// OID 뒤에 숫자값으로 변경 후 실행 해주세요
String oid = "wt.epm.EPMDocument:OID 숫자값";
ReferenceFactory rf = new ReferenceFactory();
EPMDocument e3 = (EPMDocument) rf.getReference(oid).getObject();

QueryResult _qr = PersistenceHelper.manager.navigate(e3, "containedIn", EPMContainedIn.class);
if (_qr.hasMoreElements()) {
	EPMSepFamilyTable ft = (EPMSepFamilyTable) _qr.nextElement();
	QueryResult _result = ContentHelper.service.getContentsByRole(ft, ContentRoleType.PRIMARY);

	if (_result.hasMoreElements()) {
		ApplicationData data = (ApplicationData) _result.nextElement();
		String fname = ft.getName();
		System.out.println("1번 = " + fname);
		// 맞으면 주석 해제후 실행 해봐주세요
// 		PersistenceHelper.manager.delete(ft);
	}

	QueryResult qr10 = ContentHelper.service.getContentsByRole(e3, ContentRoleType.PRIMARY);
	if (qr10.hasMoreElements()) {
		ApplicationData data = (ApplicationData) qr10.nextElement();
		String fname = e3.getCADName();
		System.out.println("2번 = " + fname);
		// 맞으면 주석 해제후 실행 해봐주세요
// 		PersistenceHelper.manager.delete(e3);
	}

	QueryResult qr100 = ContentHelper.service.getContentsByRole(e3, ContentRoleType.SECONDARY);
	while (qr100.hasMoreElements()) {
		ApplicationData data = (ApplicationData) qr100.nextElement();
		String fname = e3.getCADName();
		System.out.println("3번 = " + fname);
		// 맞으면 주석 해제후 실행 해봐주세요
// 		PersistenceHelper.manager.delete(e3);
	}
}
%>