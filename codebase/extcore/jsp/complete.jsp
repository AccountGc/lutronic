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
while (qr.hasMoreElements()) {
	EcoPartLink link = (EcoPartLink) qr.nextElement();
	WTPartMaster m = link.getPart();
	String v = link.getVersion();
	WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

	String part_oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
	JSONArray end = PartHelper.manager.end(part_oid, null);
	for (int i = 0; i < end.size(); i++) {
		Map<String, String> map = (Map<String, String>) end.get(i);
		String s = map.get("oid");
		WTPart endPart = (WTPart) CommonUtil.getObject(s);
		WTPartMaster mm = (WTPartMaster) endPart.getMaster();

		if (PartHelper.isCollectNumber(mm.getNumber())) {
	continue;
		}

		if (!PartHelper.isTopNumber(mm.getNumber())) {
	continue;
		}

		QueryResult rs = PersistenceHelper.manager.navigate(mm, "eco", EOCompletePartLink.class);
		if (rs.size() > 0) {
	out.println("중복 완제품 = " + mm.getNumber() + "<br>");
	continue;
		}

		out.println("등록되는 완제품=" + mm.getNumber() + "<br>");

		EOCompletePartLink cLink = EOCompletePartLink.newEOCompletePartLink(mm, eco);
		cLink.setVersion(endPart.getVersionIdentifier().getSeries().getValue());
		PersistenceHelper.manager.save(cLink);
	}
}
%>