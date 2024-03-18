<%@page import="java.util.ArrayList"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="com.e3ps.groupware.workprocess.AsmApproval"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.e3ps.groupware.workprocess.WFItem"%>
<%@page import="com.e3ps.groupware.workprocess.WFItemUserLink"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="java.util.Base64"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.sql.Blob"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.e3ps.workspace.AppPerLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(WFItem.class, true);
QueryResult qr = PersistenceHelper.manager.find(query);
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	WFItem item = (WFItem) obj[0];
	ArrayList<WFItemUserLink> list = get(item);
	for (WFItemUserLink link : list) {
		out.println("결재 타입 = " + link.getActivityName() + ", 결재자 = " + link.getApprover() + ", 결재의견 = "
		+ link.getComment() + ", 결재일 = " + link.getProcessDate());
	}
}
%>

<%!public static ArrayList<WFItemUserLink> get(WFItem item) throws Exception {
		ArrayList<WFItemUserLink> list = new ArrayList<WFItemUserLink>();
		QuerySpec qs = new QuerySpec();
		int idx_l = qs.appendClassList(WFItemUserLink.class, true);

		SearchCondition sc = new SearchCondition(WFItemUserLink.class, "", "=",
				item.getPersistInfo().getObjectIdentifier().getId());
		qs.appendWhere(sc, new int[] { idx_l });

		ClassAttribute ca = new ClassAttribute(WFItemUserLink.class, WFItemUserLink.CREATE_TIMESTAMP);
		OrderBy by = new OrderBy(ca, true);
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			list.add((WFItemUserLink) obj[0]);
		}
		return list;
	}%>