<%@page import="java.util.regex.Pattern"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="wt.vc.views.View"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.fc.*"%>
<%@page import="wt.query.*"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.part.WTPart:211812696";
WTPart part = (WTPart) CommonUtil.getObject(oid);

ArrayList<WTPart> list = new ArrayList<>();
endRecursive(list, part);

for (WTPart p : list) {
	out.println(p.getNumber() + "<br>");
}
%>

<%!private void endRecursive(ArrayList<WTPart> list, WTPart part) throws Exception {
		WTPartMaster master = (WTPartMaster) part.getMaster();

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
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

		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// 		if (qr.size() == 0) {
		// // 			if (!list.contains(part)) {
		// 				list.add(part);
		// // 			}
		// 		}
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (!isCollectNumber(part.getNumber())) {
				if (isTopNumber(part.getNumber())) {
					if (!list.contains(part)) {
						list.add(part);
					}
				}
			}
			endRecursive(list, p);
		}
	}%>

<%!public static boolean isTopNumber(String number) {
		String firstNumber = number.substring(0, 1);
		String endNumber = number.substring(5, 8);// number.substring(5,number.length());
		if (firstNumber.equals("1") && !endNumber.endsWith("000")) { // 6,7,8이 000인경우
			return true;
		}
		return false;
	}%>

<%!public static boolean isCollectNumber(String partNumber) {
		boolean reValue = true;
		if (partNumber != null) {
			if (partNumber.length() == 10) {
				if (Pattern.matches("^[0-9]+$", partNumber)) {
					// 숫자임
					reValue = false;
				} else {
					// 숫자아님
					reValue = true;
				}
			} else {
				reValue = true;
			}
		} else {
			// 입력값 없음.
			reValue = true;
		}
		return reValue;
	}%>