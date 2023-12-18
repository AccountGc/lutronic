<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String partNumber = request.getParameter("partNumber");
%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<input type="hidden" name="partNumber" id="partNumber" value="<%=partNumber%>" />
<input type="hidden" name="_psize" id="_psize" value="10" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				SEQ 현황
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 330px; border-top: 1px solid #3180c3;"></div>
<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		width : 350,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.part_oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "location",
		headerText : "품목분류",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "modifitedDate",
		headerText : "수정일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		}
	}, {
		dataField : "remarks",
		headerText : "OEM Info.",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
			inline : true
		},
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : false,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableFilter : true,
			showInlineFilter : false,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		loadGridData();
	}

	function loadGridData() {
		let params = new Object();
		const url = getCallUrl("/part/seq");
		const partNumber = document.querySelector("#partNumber").value;
		const field = [ "partNumber" ];
		params = toField(params, field);
		openLayer();
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			if (data.result) {
				totalPage = Math.ceil(data.total / data.pageSize);
				document.getElementById("sessionid").value = data.sessionid;
				createPagingNavigator(data.curPage);
				AUIGrid.setGridData(myGridID, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>