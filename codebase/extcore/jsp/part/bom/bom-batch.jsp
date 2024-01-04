<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTPart part = (WTPart) request.getAttribute("part");
String title = (String) request.getAttribute("title");
String target = (String) request.getAttribute("target");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="target" id="target" value="<%=target%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=title%>
				일괄다운로드(
				<font color="red">
					<b><%=part.getNumber()%></b>
				</font>
				)
			</div>
		</td>
		<td class="right">
			<input type="button" title="ALL" value="ALL" class="blue" onclick="batch('ALL');">
			<input type="button" title="SCREEN" value="SCREEN" class="red" onclick="batch('SCREEN');">
			<input type="button" title="닫기" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150px">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb req">다운로드 사유 선택</th>
		<td class="indent5">
			<select name="reason" id="reason" class="AXSelect width-200">
				<option value="">선택</option>
				<option value="1">공정 검토용</option>
				<option value="2">제작 발주용</option>
				<option value="3">수입 검사용</option>
				<option value="4">인증용</option>
				<option value="5">ROHS 확인용</option>
				<option value="6">기타</option>
			</select>
		</td>
	</tr>
	<tr>
		<th class="lb req">다운로드 사유</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="10"></textarea>
		</td>
	</tr>
</table>



<script type="text/javascript">
	function batch(mode) {
		const gridData = opener.gridData(mode);
		const reason = document.getElementById("reason").value;
		const target = document.getElementById("target").value;
		const description = document.getElementById("description").value;
		if (reason === "") {
			alert("다운로드 사유를 선택하세요.");
			return false;
		}

		const oid = document.getElementById("oid").value;
		const arr = new Array();
		for (let i = 0; i < gridData.length; i++) {
			const item = gridData[i];
			arr.push(item.oid);
		}
		const params = {
			arr : arr,
			oid : oid,
			description : description,
			reason : reason,
			target : target
		}

		openLayer();
		const url = getCallUrl("/bom/batch");
		logger(params);
		call(url, params, function(data) {
			const url = data.url;
			alert(url);
			if (data.result) {
				document.location.href = url;
			}
			closeLayer();
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		selectbox("reason");
	})
</script>