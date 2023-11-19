<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
String oid = (String) request.getAttribute("oid");
WTPart part = (WTPart) request.getAttribute("part");
Map<String, Object> attr = (Map<String, Object>) request.getAttribute("attr");
ArrayList<NumberCode> list = (ArrayList<NumberCode>) request.getAttribute("list");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목속성(
				<font color="red">
					<b><%=part.getNumber()%></b>
				</font>
				)
			</div>
		</td>
		<td class="right">
			<input type="button" title="속성 CLEANING" value="속성 CLEANING" class="red" onclick="_clean();">
			<input type="button" title="닫기" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="13%">
		<col width="450px">
		<col width="13%">
		<col width="450px">
	</colgroup>
	<tr>
		<th class="lb">프로젝트코드</th>
		<td class="indent5"><%=attr.get("model")%></td>
		<th>제작방법</th>
		<td class="indent5"><%=attr.get("productmethod")%></td>
	</tr>
	<tr>
		<th class="lb">부서</th>
		<td class="indent5"><%=attr.get("deptcode")%></td>
		<th>단위</th>
		<td class="indent5"><%=attr.get("unit")%></td>
	</tr>
	<tr>
		<th class="lb">무게</th>
		<td class="indent5"><%=attr.get("weight")%></td>
		<th>MANUFACTURE</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="AXSelect" style="width: 250px; position: relative; bottom: 1px;">
				<option value="">선택</option>
				<%
				for (NumberCode n : list) {
					String manufacture = (String) attr.get("manufacture");
					String selected = "";
					if(n.getCode().equals(manufacture)) {
						selected = "selected='selected'";
					}
				%>
				<option value="<%=n.getCode()%>" <%=selected %>>[<%=n.getCode()%>]
					<%=n.getName()%></option>
				<%
				}
				%>
			</select>
			&nbsp;
			<input style="position: relative; top: 2px;" type="button" title="속성 변경" value="속성 변경" class="blue" onclick="attrUpdate();">
		</td>
	</tr>
	<tr>
		<th class="lb">재질</th>
		<td class="indent5"><%=attr.get("mat")%></td>
		<th>후처리</th>
		<td class="indent5"><%=attr.get("finish")%></td>
	</tr>
	<tr>
		<th class="lb">OEM Info</th>
		<td class="indent5"><%=attr.get("remarks")%></td>
		<th>사양</th>
		<td class="indent5"><%=attr.get("specification")%></td>
	</tr>
	<tr>
		<th class="lb">EO No</th>
		<td class="indent5"><%=attr.get("eoNo")%></td>
		<th>EO Date</th>
		<td class="indent5"><%=attr.get("eoDate")%></td>
	</tr>
	<tr>
		<th class="lb">검토자</th>
		<td class="indent5"><%=attr.get("chk")%></td>
		<th>승인자</th>
		<td class="indent5"><%=attr.get("apr")%></td>
	</tr>
	<tr>
		<th class="lb">REV</th>
		<td class="indent5"><%=attr.get("rev")%></td>
		<th>DES</th>
		<td class="indent5"><%=attr.get("des")%></td>
	</tr>
	<tr>
		<th class="lb">ECO No</th>
		<td class="indent5"><%=attr.get("ecoNo")%></td>
		<th>ECO Date</th>
		<td class="indent5"><%=attr.get("ecoDate")%></td>
	</tr>
</table>

<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		selectbox("manufacture");
	})

	function _clean() {
		if (!confirm("속성 CLEANING을 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/_clean");
		const params = {
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	function attrUpdate() {
		const code = document.getElementById("manufacture").value;
		if (code === "") {
			alert("변경할 코드를 선택하세요.");
			return false;
		}

		if (!confirm("속성변경을 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/attrUpdate");
		const params = {
			oid : oid,
			value : code
		};
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}
</script>