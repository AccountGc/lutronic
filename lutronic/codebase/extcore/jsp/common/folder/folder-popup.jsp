<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String location = (String) request.getAttribute("location");
%>
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				폴더 선택
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" title="추가" class="blue" onclick="set();">
			<input type="button" value="닫기" title="닫기" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 540px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "폴더명",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			showRowCheckColumn : true,
			rowCheckToRadio : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			enableFilter : true,
			displayTreeOpen : true,
			forceTreeView : true
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		tree();
		AUIGrid.bind(myGridID, "cellDoubleClick", auiCellDoubleClick);
		AUIGrid.bind(myGridID, "cellClick", auiCellClick);
	}

	function auiCellClick(event) {

	}

	function auiCellDoubleClick(event) {

	}

	function set() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("폴더를 선택하세요.");
			return false;
		}
		const item = checkedItems[0].item;
		const oid = item.oid;
		const location = item.location;
		opener.document.getElementById("location").value = location;
		opener.document.getElementById("locationText").innerText = location;
		self.close();
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	function tree() {
		const location = decodeURIComponent(document.getElementById("location").value);
		const url = getCallUrl("/folder/tree");
		const params = {
			location : location
		}
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}
</script>