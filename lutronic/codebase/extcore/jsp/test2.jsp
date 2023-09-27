<%@page import="java.util.ArrayList"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.vc.config.LatestConfigSpec"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.epm.structure.EPMMemberLink"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "";
ReferenceFactory rf = new ReferenceFactory();
EPMDocument top = (EPMDocument) rf.getReference(oid).getObject();
QuerySpec query = new QuerySpec(EPMMemberLink.class);
ClassAttribute ca = new ClassAttribute(EPMMemberLink.class, EPMMemberLink.COMP_NUMBER);
OrderBy by = new OrderBy(ca, false);
query.appendOrderBy(by, new int[] { 0 });
LatestConfigSpec spec = new LatestConfigSpec();

QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(top, query, true, spec);
ArrayList<EPMDocument> list = new ArrayList<>();
list.add(top);
while (qr.hasMoreElements()) {
	EPMDocument e = (EPMDocument) qr.nextElement();
	list.add(e);
	assembly(e, list);
}

for (EPMDocument ee : list) {
	out.println("e=" + ee.getName() + "<br>");
}
%>


<%!private static void assembly(EPMDocument parent, ArrayList<EPMDocument> list) throws Exception {
		QuerySpec query = new QuerySpec(EPMMemberLink.class);
		ClassAttribute ca = new ClassAttribute(EPMMemberLink.class, EPMMemberLink.COMP_NUMBER);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { 0 });
		LatestConfigSpec spec = new LatestConfigSpec();

		QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(parent, query, true, spec);
		while (qr.hasMoreElements()) {
			EPMDocument e = (EPMDocument) qr.nextElement();
			list.add(e);
			assembly(e, list);
		}
	}%>