<%@page import="java.io.StreamCorruptedException"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.ownership.Ownership"%>
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
// String oid = "com.e3ps.change.EChangeOrder:2558459";
// Persistable per = CommonUtil.getObject(oid);

QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EChangeOrder.class, true);
QueryResult qr = PersistenceHelper.manager.find(query);
out.println("시작!!!!");
System.out.println("시작!!!!");
int count = 0;
while (qr.hasMoreElements()) {
	count++;
	Object[] obj = (Object[]) qr.nextElement();
	Persistable per = (Persistable) obj[0];
	EChangeOrder eco = (EChangeOrder) per;
	System.out.println("COUNT = " + count + ", ECO = " + eco.getEoNumber());
	WFItem item = getWFItem(per);
	ApprovalMaster master = null;
	if (item != null) {
		String v = item.getObjectVersion();
		ArrayList<WFItemUserLink> list = get(item);
		String name = WorkspaceHelper.manager.getName(per);
		String state = item.getObjectState();

		// 승인중인것들만
		if ("APPROVED".equals(state)) {

	for (WFItemUserLink link : list) {
		int processOrder = link.getProcessOrder();
		WTUser user = link.getUser(); // 사용자...
		String actName = link.getActivityName(); // 기안,결재,합의,수신
		String comment = (String) link.getComment(); // 읩견
		String lineState = link.getState();

		if (!StringUtil.checkString(lineState)) {
			continue;
		}

		Timestamp completeDate = link.getProcessDate();
		Timestamp startDate = link.getCreateTimestamp();

		out.println("상태 = " + state + ", ECO 번호 = " + eco.getEoNumber() + ", 결재자 = " + user.getFullName()
				+ ", 활동명 = " + actName + ", 의견 = " + comment + ", 완료일 = " + completeDate + "<br>");

		System.out.println("순서 = " + processOrder + ", 상태 = " + state + ", ECO 번호 = " + eco.getEoNumber()
				+ ", 결재자 = " + user.getFullName() + ", 활동명 = " + actName + ", 의견 = " + comment + ", 완료일 = "
				+ completeDate + "<br>");
		// 		if (processOrder == 0){
		if ("기안".equals(actName)) {
			master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(null);
			master.setOwnership(Ownership.newOwnership(user));
			master.setPersist(per);
			master.setStartTime(startDate);
			master.setMig("MIG");
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_COMPLETE);
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);
			master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);
		}

		ApprovalLine startLine = ApprovalLine.newApprovalLine();
		startLine.setName(name);
		startLine.setOwnership(Ownership.newOwnership(user));
		startLine.setMaster(master);
		startLine.setMig("MIG");
		startLine.setReads(true);
		startLine.setSort(processOrder);
		startLine.setStartTime(link.getCreateTimestamp());

		if ("기안".equals(actName)) {
			startLine.setType(WorkspaceHelper.SUBMIT_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_SUBMITTER);
			startLine.setState(WorkspaceHelper.STATE_SUBMIT_COMPLETE);
		} else if ("결재".equals(actName)) {
			startLine.setType(WorkspaceHelper.APPROVAL_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
			if ("승인(결재)".equals(lineState.trim())) {
				startLine.setState(WorkspaceHelper.STATE_APPROVAL_COMPLETE);
			}
		} else if ("합의".equals(actName)) {
			startLine.setType(WorkspaceHelper.AGREE_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_AGREE);
			// 			if ("합의(합의)".equals(lineState.trim())) {
			startLine.setState(WorkspaceHelper.STATE_AGREE_COMPLETE);
			// 			} else {

			// 			}

		} else if ("수신".equals(actName)) {
			startLine.setType(WorkspaceHelper.RECEIVE_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
			if (completeDate == null) {
				startLine.setState(WorkspaceHelper.STATE_RECEIVE_START);
			} else {
				startLine.setState(WorkspaceHelper.STATE_RECEIVE_COMPLETE);
			}
		}
		startLine.setDescription(comment);
		startLine.setCompleteTime(completeDate);
		PersistenceHelper.manager.save(startLine);
	}
		}
	}
}
out.println("정상 종료 시작!!!!" + count);
System.out.println("정상 종료 시작!!!!" + count);
%>

<%!public static WFItem getWFItem(Persistable per) throws Exception {
		long id = per.getPersistInfo().getObjectIdentifier().getId();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WFItem.class, true);
		SearchCondition sc = new SearchCondition(WFItem.class, "wfObjectReference.key.id", "=", id);
		query.appendWhere(sc, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			return (WFItem) obj[0];
		}
		return null;
	}%>

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

		ClassAttribute ca = new ClassAttribute(WFItemUserLink.class, WFItemUserLink.PROCESS_ORDER);
		OrderBy by = new OrderBy(ca, false);
		qs.appendOrderBy(by, new int[] { idx_l });
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			list.add((WFItemUserLink) obj[0]);
		}
		return list;
	}%>