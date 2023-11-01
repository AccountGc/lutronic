<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTPart root = (WTPart) request.getAttribute("part");
String oid = root.getPersistInfo().getObjectIdentifier().getStringValue();
ArrayList<Map<String, String>> baseline = (ArrayList<Map<String, String>>) request.getAttribute("baseline");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				BOM (<%=root.getNumber()%>) 보기
			</div>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<select name="depth" id="depth" onchange="depth();" class="AXSelect width-120">
				<option value="expandAll">전체확장</option>
				<option value="1" selected="selected">1레벨</option>
				<option value="2">2레벨</option>
				<option value="3">3레벨</option>
				<option value="4">4레벨</option>
				<option value="5">5레벨</option>
			</select>
			&nbsp;
			<select name="sort" id="sort" class="AXSelect width-100">
				<option value="desendent">정전개</option>
				<option value="reverse">역전개</option>
			</select>
			&nbsp;
			<div class="pretty p-switch">
				<input type="checkbox" name="dummy" id="dummy" value="true" checked="checked">
				<div class="state p-success">
					<label>
						<b>더미제외</b>
					</label>
				</div>
			</div>
		</td>
		<td class="right">
			<select name="baseline" id="baseline" class="AXSelect width-150">
				<option value="" selected="selected" disabled="disabled">베이스라인 보기</option>
				<%
				for (Map<String, String> m : baseline) {
				%>
				<option value="<%=m.get("baseLine_oid")%>"><%=m.get("baseLine_name")%></option>
				<%
				}
				%>
			</select>
			&nbsp;
			<select name="compare" id="compare" class="AXSelect width-150">
				<option value="" selected="selected" disabled="disabled">베이스라인 비교</option>
				<%
				for (Map<String, String> m : baseline) {
				%>
				<option value="<%=m.get("baseLine_oid")%>"><%=m.get("baseLine_name")%></option>
				<%
				}
				%>
			</select>

			<input type="button" value="상위품목" title="상위품목" id="upItem">

			<input type="button" value="하위품목" title="하위품목" id="downItem">

			<input type="button" value="END ITEM" title="END ITEM" id="endItem">

			<input type="button" value="EXCEL" title="EXCEL" id="excelDown">

			<input type="button" value="첨부" title="첨부" id="attachDown">

			<input type="button" value="도면" title="도면" id="drawingDown">

			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	const columnLayout = [ {
		dataField : "level",
		headerText : "Level",
		dataType : "string",
		width : 80
	}, {
		dataField : "number",
		headerText : "부품번호",
		dataType : "string",
		width : 300
	}, {
		dataField : "dwgNo",
		headerText : "도면번호",
		dataType : "string",
		width : 140,
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		style : "aui-left",
		width : 300
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80
	}, {
		dataField : "remarks",
		headerText : "OEM Info.",
		dataType : "string",
		width : 150
	}, {
		dataField : "checkout",
		headerText : "체크아웃 상태",
		dataType : "string",
		width : 120
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
	}, {
		dataField : "modifier",
		headerText : "수정자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 150,
	}, {
		dataField : "quantity",
		headerText : "수량",
		dataType : "string",
		width : 100
	}, {
		dataField : "ecoNo",
		headerText : "ECO NO.",
		dataType : "string",
		width : 150,
	}, {
		dataField : "model",
		headerText : "프로젝트코드",
		dataType : "string",
		width : 150,
	}, {
		dataField : "deptcode",
		headerText : "부서",
		dataType : "string",
		width : 100,
	}, {
		dataField : "manufacture",
		headerText : "MANUFACTURER",
		dataType : "string",
		width : 150,
	}, {
		dataField : "productmethod",
		headerText : "제작방법",
		dataType : "string",
		width : 100,
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
			displayTreeOpen : true,
			editable : false,
			treeColumnIndex : 1,
			enableFilter : true,
			flat2tree : true,
			enableSorting : false,
			fixedColumnCount : 2,
			treeLazyMode : true,
			treeLevelIndent : 35,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
	}

	function loadGridData() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/bom/loadStructure");
		const params = {
			oid : oid
		};
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columnLayout);
		selectbox("depth");
		selectbox("sort");
		selectbox("baseline");
		selectbox("compare");
	})
</script>