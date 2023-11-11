<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 일괄개정
			</div>
		</td>
		<td class="right">
			<input type="button" value="일괄개정" title="일괄개정" onclick="revise();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
<script>
	const list = window.list;
	const link = window.link;
	let myGridiD;
	const columns = [ {
		headerText : "부품번호",
		dataField : "part_number",
		dataType : "string",
		width : 140
	}, {
		headerText : "부품명",
		dataField : "part_name",
		dataType : "string",
		style : "aui-left",
	}, {
		headerText : "REV",
		dataField : "version",
		dataType : "string",
		width : 140
	}, {
		headerText : "상태",
		dataField : "state",
		dataType : "string",
		width : 140
	}, {
		headerText : "최신버전",
		dataField : "latest",
		dataType : "string",
		width : 140,
		renderer : {
			type : "CheckBoxEditRenderer",
		}
	}, {
		headerText : "주도면",
		dataField : "epm_number",
		dataType : "string",
		width : 200
	}, {
		headerText : "참조항목",
		dataField : "reference",
		dataType : "string",
		width : 300,
		renderer : {
			type : "TemplateRenderer"
		}
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			autoGridHeight : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
		load();
	})

	function load() {
		const url = getCallUrl("/activity/load");
		const params = {
			list : link
		};

		logger(params);
		parent.openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			parent.closeLayer();
		});
	}

	function revise() {
		const data = AUIGrid.getGridData(myGridID);

		if (data.length === 0) {
			alert("개정 대상 품목이 없습니다.");
			return false;
		}

		if (!confirm("해당 품목 및 도면을 일괄개정 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;

		const params = {
			data : data,
			oid : oid
		};
		const url = getCallUrl("/activity/revise");
		parent.openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			}
			parent.closeLayer();
		})

	}

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>