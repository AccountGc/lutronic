<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 일괄다운로드
			</div>
		</td>
		<td class="right">
			<input type="button" title="DXF" value="DXF" class="blue" onclick="progress('DXF');">
			<input type="button" title="PDF" value="PDF" class="red" onclick="progress('PDF');">
			<input type="button" title="닫기" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 30px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;

	function a() {
		alert("C");
	}
</script>