<%@page import="wt.fc.Persistable"%>
<%@page import="com.e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="com.e3ps.workspace.ApprovalMaster"%>
<%@page import="com.e3ps.workspace.ApprovalLine"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.groupware.workprocess.WFItemUserLink"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.groupware.workprocess.WFItem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "com.e3ps.groupware.workprocess.WFItem:1891717";
WFItem item = (WFItem) CommonUtil.getObject(oid);
WTObject wtobj = item.getWfObject();
String v = item.getObjectVersion();
ArrayList<WFItemUserLink> list = get(item);

String name = WorkspaceHelper.manager.getName((Persistable) wtobj);
String state = item.getObjectState();

// 마스터 생성
ApprovalMaster master = ApprovalMaster.newApprovalMaster();
master.setName(name);
master.setCompleteTime(null);
master.setOwnership(item.getOwnership());
master.setPersist((Persistable) wtobj);
master.setStartTime(startTime);

if ("INWORK".equals(state)) {

} else if ("APPROVED".equals(state)) {

} else if ("APPROVING".equals(state)) {

} else if ("RETURN".equals(state)) {

}

master = (ApprovalMaster) PersistenceHelper.manager.save(master);

for (WFItemUserLink link : list) {
	if (wtobj instanceof EChangeOrder) {
		EChangeOrder eco = (EChangeOrder) wtobj;
		WTUser user = link.getUser(); // 사용자...
		String actName = link.getActivityName(); // 기안,결재,합의,수신
		String comment = (String) link.getComment(); // 읩견
		Timestamp completeDate = link.getProcessDate();

		out.println("ECO 번호 = " + eco.getEoNumber() + ", 결재자 = " + user.getFullName() + ", 활동명 = " + actName + ", 의견 = "
		+ comment + ", 완료일 = " + completeDate + "<br>");

		if ("기안".equals(actName.trim())) {
			
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(name);
			startLine.setOwnership(item.getOwnership());
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(completeDate);
			startLine.setType(WorkspaceHelper.SUBMIT_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_SUBMITTER);
			startLine.setDescription(item.getOwnership().getOwner().getFullName() + " 사용자가 결재를 기안하였습니다.");
			startLine.setState(WorkspaceHelper.STATE_SUBMIT_COMPLETE);
			startLine.setCompleteTime(completeDate);

			PersistenceHelper.manager.save(startLine);
			
		} else if ("결재".equals(actName.trim())) {

		} else if ("수신".equals(actName.trim())) {

		} else if ("합의".equals(actName.trim())) {

		}

	}
}
%>


<%!public static ArrayList<WFItemUserLink> get(WFItem item) throws Exception {
		ArrayList<WFItemUserLink> list = new ArrayList<WFItemUserLink>();
		QuerySpec qs = new QuerySpec();
		int idx_l = qs.appendClassList(WFItemUserLink.class, true);

		SearchCondition sc = new SearchCondition(WFItemUserLink.class, "roleBObjectRef.key.id", "=",
				item.getPersistInfo().getObjectIdentifier().getId());
		qs.appendWhere(sc, new int[] { idx_l });
		qs.appendAnd();

		sc = new SearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED, SearchCondition.IS_FALSE);
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