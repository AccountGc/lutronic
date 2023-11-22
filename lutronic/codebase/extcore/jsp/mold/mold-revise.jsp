<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
String oid = (String) request.getParameter("oid");
%>

<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 결재 타입 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="이전" title="이전" onclick="history.back();">
			<input type="button" value="개정" title="개정" class="blue" onclick="revise();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
		<th class="req lb">결재방식</th>
		<td colspan="3">
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio"name="lifecycle" value="LC_Default" checked="checked">
				<div class="state p-success">
					<label> <b>기본결재</b>
					</label>
				</div>
			</div> &nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="lifecycle" value="LC_Default_NonWF">
				<div class="state p-success">
					<label> <b>일괄결재</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function revise(){
		if (!confirm("개정 하시겠습니까?")) {
			return;
		}
		
		let params = new Object();
		params.lifecycle = $('input[name=lifecycle]:checked').val();
		params.oid = $("#oid").val();
		const url = getCallUrl("/mold/revise");
		call(url, params, function(data) {
			if(data.result){
				alert("개정 성공하였습니다.");
				self.close();
				location.reload();
			}else{
				alert(data.msg);
			}
		});
	}
</script>
