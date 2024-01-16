<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="com.e3ps.download.DownloadHistory"%>
<%@page import="com.e3ps.org.MailWTobjectLink"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.e3ps.change.CrToDocumentLink"%>
<%@page import="com.e3ps.change.EcrToEcrLink"%>
<%@page import="com.e3ps.change.RequestOrderLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="com.e3ps.change.ECPRRequest"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EChangeRequest.class, true);
SearchCondition sc = new SearchCondition(EChangeRequest.class, EChangeRequest.EO_NUMBER, "LIKE", "%ECPR%");
query.appendWhere(sc, new int[] { idx });
QueryResult qr = PersistenceHelper.manager.find(query);
int i = 0;
System.out.println("qr=" + qr.size());
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	EChangeRequest e = (EChangeRequest) obj[0];

	QueryResult result = PersistenceHelper.manager.navigate(e, "eco", RequestOrderLink.class, false);
	while (result.hasMoreElements()) {
		RequestOrderLink link = (RequestOrderLink) result.nextElement();
		PersistenceHelper.manager.delete(link);
	}

	result.reset();
	result = PersistenceHelper.manager.navigate(e, "useBy", EcrToEcrLink.class, false);
	while (result.hasMoreElements()) {
		EcrToEcrLink link = (EcrToEcrLink) result.nextElement();
		PersistenceHelper.manager.delete(link);
	}

	result.reset();
	result = PersistenceHelper.manager.navigate(e, "doc", CrToDocumentLink.class);
	while (result.hasMoreElements()) {
		CrToDocumentLink link = (CrToDocumentLink) result.nextElement();
		PersistenceHelper.manager.delete(link);
	}

	result.reset();
	result = PersistenceHelper.manager.navigate((WTObject) e, "user", MailWTobjectLink.class);
	while (result.hasMoreElements()) {
		MailWTobjectLink link = (MailWTobjectLink) result.nextElement();
		PersistenceHelper.manager.delete(link);
	}

	QuerySpec qs = new QuerySpec();
	int k = qs.appendClassList(DownloadHistory.class, true);
	QuerySpecUtils.toEquals(qs, k, DownloadHistory.class, "persistReference.key.id", e);
	QueryResult rs = PersistenceHelper.manager.find(qs);
	while (rs.hasMoreElements()) {
		Object[] oo = (Object[]) rs.nextElement();
		DownloadHistory d = (DownloadHistory) oo[0];
		PersistenceHelper.manager.delete(d);
	}

	PersistenceHelper.manager.delete(e);
	System.out.println("i=" + i + "번째 삭제 ECPR = " + e.getEoNumber());
	i++;
}
%>