<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.representation.Representation"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EPMDocument.class, true);
QueryResult qr = PersistenceHelper.manager.find(query);
System.out.println("전체 개수 시작 = " + qr.size());
int i = 0;
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	EPMDocument epm = (EPMDocument) obj[0];
	Representation representation = PublishUtils.getRepresentation(epm);

	if (representation != null) {
		String v = epm.getVersionIdentifier().getSeries().getValue() + "."
		+ epm.getIterationIdentifier().getSeries().getValue();
		System.out.println("도면 썸네일 삭제 =  " + epm.getNumber() + "_" + v);
		PersistenceHelper.manager.delete(representation);
	}

	WTPart part = PartHelper.manager.getPart(epm);
	if (part != null) {
		Representation _representation = PublishUtils.getRepresentation(part);

		if (_representation != null) {
	String v = part.getVersionIdentifier().getSeries().getValue() + "."
			+ part.getIterationIdentifier().getSeries().getValue();
	System.out.println("품목 썸네일 삭제 =  " + part.getNumber() + "_" + v);
	PersistenceHelper.manager.delete(_representation);
		}
	}
	System.out.println(i + "번째 완료!");
	i++;
}
%>