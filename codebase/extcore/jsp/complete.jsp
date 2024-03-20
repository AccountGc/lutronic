<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eco.service.EcoHelper"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "com.e3ps.change.EChangeOrder:208027737";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

QueryResult result = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class, false);
while (result.hasMoreElements()) {
	EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
	PersistenceHelper.manager.delete(link);
}

QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
ArrayList<WTPart> list = new ArrayList<WTPart>(); // 품목 리스트 담기..
while (qr.hasMoreElements()) {
	EcoPartLink link = (EcoPartLink) qr.nextElement();
	WTPartMaster m = link.getPart();
	String v = link.getVersion();
	WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

	EcoHelper.manager.reverseStructure(part, list);
}

for (WTPart pp : list) {

	if (PartHelper.isCollectNumber(pp.getNumber())) {
		System.out.println("처음 걸러진 번호 = " + pp.getNumber());
		continue;
	}

	if (!PartHelper.isTopNumber(pp.getNumber())) {
		System.out.println("두번째 걸러진 번호 = " + pp.getNumber());
		continue;
	}

	QueryResult rs = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class, false);
	if (rs.size() > 0) {
		continue;
	}

	out.println("등록되는 완제품=" + pp.getNumber() + "<br>");

	EOCompletePartLink cLink = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) pp.getMaster(), eco);
	cLink.setVersion(pp.getVersionIdentifier().getSeries().getValue());
	PersistenceHelper.manager.save(cLink);
}
%>