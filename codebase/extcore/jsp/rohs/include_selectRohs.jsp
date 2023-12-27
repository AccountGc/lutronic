<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String module = request.getParameter("module");
String roleType = request.getParameter("roleType");
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 관련 RoHS
			</div>
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">관련 RoHS</th>
		<td class="indent5 pt5 ">
			<input type="button" value="추가" title="추가" class="blue" onclick="addRohs();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRohs();">
			<div id="grid_rohs" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let rohsGridID;
	const columnsRohs = [ {
		dataField : "number",
		headerText : "물질번호",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "name",
		headerText : "물질명",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid6(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			<%if (isCreate || isUpdate) {%>
			showStateColumn : true,
			showRowCheckColumn : true,
			<%}%>
			enableFilter : true,
			autoGridHeight : true
		}
		rohsGridID = AUIGrid.create("#grid_rohs", columnLayout, props);
		<%if (isUpdate) {%>
			AUIGrid.setGridData(rohsGridID, <%=RohsHelper.manager.reference(oid, "rohs")%>);
		<%}%>
	}

	function addRohs() {
		const url = getCallUrl("/rohs/popup?method=rohsAppend&multi=true");
		_popup(url, 1500, 700, "n");
	}
	
	function rohsAppend(arr, callBack){
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(rohsGridID, "oid", item.oid);
			if (unique) {
				AUIGrid.addRow(rohsGridID, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " 물질는 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}

	function deleteRohs() {
		const checked = AUIGrid.getCheckedRowItems(rohsGridID);
		if (checked.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(rohsGridID, rowIndex);
		}
	}
	
</script>