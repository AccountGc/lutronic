<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="com.e3ps.common.util.PageQueryUtils"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid %>" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 결재 회수
			</div>
		</td>
		<td class="right">
			<input type="button" value="회수" name="withDrawAction" id="withDrawAction">
			<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="search-table">
	<colgroup>
		<col width="174">
		<col width="*">
		<col width="174">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">결재 회수 방식 <span class="red">*</span></th>
		<td class="indent5" colspan="3">
			<div class="pretty p-switch">
				<input type="radio" name="withDrawType" value="init" checked="checked">
				<div class="state p-success">
					<label> <b>결재선 현황 초기화</b>
					</label>
				</div>
			</div> &nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="withDrawType" value="keep">
				<div class="state p-success">
					<label> <b>결재선 현황 유지</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
</table>

<script type="text/javascript">
	$('#withDrawAction').click(function() {
		if (!confirm("결재 회수 하시겠습니까?")) {
			return;
		}
	});
</script>