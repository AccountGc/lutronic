<%@page import="net.sf.json.JSONObject"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.util.EChangeUtils"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
ArrayList<Map<String, Object>> list = EChangeUtils.manager.summary(oid);
%>

<%
int idx = 0;
for (int i = 0; i < list.size(); i++) {
	Map<String, Object> map = list.get(i);
	String eoid = (String) map.get("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물<%
				if (CommonUtil.isAdmin()) {
				%>&nbsp;<%
				//=eoid
				%>
				<%
				}
				%>
			</div>
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="450">
		<col width="130">
		<col width="450">
		<col width="130">
		<col width="450">
	</colgroup>
	<tr>
		<th class="lb">담당부서</th>
		<td class="indent5"><%=map.get("department_name")%></td>
		<th>담당자</th>
		<td class="indent5"><%=map.get("activity_user")%></td>
		<th>상태</th>
		<td class="indent5"><%=map.get("state")%></td>
	</tr>
	<tr>
		<th class="lb">완료 요청일</th>
		<td class="indent5"><%=map.get("finishDate")%></td>
		<th>완료일</th>
		<td class="indent5" colspan="3"><%=map.get("completeDate")%></td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="5">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=eoid%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">의견</th>
		<td class="indent5" colspan="5"><%=map.get("description") != null ? map.get("description") : ""%></td>
	</tr>
	<tr>
		<th class="lb">산출물</th>
		<td colspan="5" style="padding-left: 11px;">
			<table class="view-table" style="margin: 5px 0px 5px -5px;">
				<colgroup>
					<col width="120">
					<col width="*">
					<col width="80">
					<col width="80">
					<col width="100">
					<col width="100">
					<col width="100">
				</colgroup>
				<tr>
					<th class="lb">내부문서번호</th>
					<th class="lb">문서제목</th>
					<th class="lb">상태</th>
					<th class="lb">REV</th>
					<th class="lb">작성자</th>
					<th class="lb">등록자</th>
					<th class="lb">등록일</th>
				</tr>
				<%
				JSONArray arr = (JSONArray) map.get("data");
				for (int k = 0; k < arr.size(); k++) {
					JSONObject node = (JSONObject) arr.get(k);
					String _oid = (String) node.get("oid");
					String url = "/Windchill/plm/doc/view?oid=" + _oid;
				%>
				<tr>
					<td class="center">
						<a href="javascript:_popup('<%=url%>', 1600, 800, 'n');">
							<%=node.get("number")%>
						</a>
					</td>
					<td class="indent5">
						<a href="javascript:_popup('<%=url%>', 1600, 800, 'n');">
							<%=node.get("name")%>
						</a>
					</td>
					<td class="center"><%=node.get("state")%></td>
					<td class="center"><%=node.get("version")%></td>
					<td class="center"><%=node.get("writer")%></td>
					<td class="center"><%=node.get("creator")%></td>
					<td class="center"><%=node.get("createdDate_txt")%></td>
				</tr>
				<%
				}
				%>
			</table>
		</td>
	</tr>
</table>
<%
if (i != list.size() - 1) {
%>
<br>
<%
}
%>
<%
}
%>