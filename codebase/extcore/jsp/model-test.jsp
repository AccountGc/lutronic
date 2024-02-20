<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="wt.vc.views.View"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="com.e3ps.change.activity.service.ActivityHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "com.e3ps.change.EChangeOrder:";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
String model = "";
ArrayList<WTPart> list = new ArrayList<WTPart>(); // 품목 리스트 담기..
while (qr.hasMoreElements()) {
	EcoPartLink link = (EcoPartLink) qr.nextElement();
	WTPartMaster m = link.getPart();
	String v = link.getVersion();
	WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

	// 대상 품목의 BOM 전개 ..
	reverseStructure(part, list);
}

out.println("=======" + list.size());
%>

<%!public void reverseStructure(WTPart end, ArrayList<WTPart> list) throws Exception {
		if (!list.contains(end)) {
			list.add(end);
		}
		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();

		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);

		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		String viewName = end.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		String state = end.getLifeCycleState().toString();
		if (state != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);

		query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
				new int[] { idx_part });

		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			if (!list.contains(p)) {
				list.add(p);
			}
			reverseStructure(p, list);
		}
	}%>