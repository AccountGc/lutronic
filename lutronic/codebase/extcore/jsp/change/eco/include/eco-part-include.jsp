<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.rohs.service.RohsHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<style type="text/css">
.preMerge {
	font-weight: bold !important;
	color: red !important;
	background-color: rgb(206, 222, 255) !important;
}

.afterMerge {
	font-weight: bold !important;
	color: red !important;
	background-color: rgb(200, 255, 203) !important;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				완제품 품목
			</div>
		</td>
	</tr>
</table>

<div id="grid500" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID500;
	const columns500 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "",
		headerText : "BOM",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "",
		headerText : "도면",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : true,
		},
	} ]

	function createAUIGrid500(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			enableFilter : true,
			autoGridHeight : true,
		}
		myGridID500 = AUIGrid.create("#grid500", columnLayout, props);
		AUIGrid.setGridData(myGridID500,
<%=AUIGridUtil.include(oid, "complete")%>
	);
	}
</script>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				설계변경 품목
			</div>
		</td>
	</tr>
</table>

<div id="grid510" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID510;
	const columns510 = [ {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
		},
	}, {
		headerText : "변경 전",
		children : [ {
			dataField : "part_name",
			dataType : "string",
			headerText : "품목명",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 4, // 셀 가로 병합 대상은 6개로 설정
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.part_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.preMerge === true) {
					return "preMerge";
				}
				return null;
			},
		}, {
			dataField : "part_state",
			dataType : "string",
			headerText : "상태",
			width : 80,
		}, {
			dataField : "part_version",
			dataType : "string",
			headerText : "REV",
			width : 80,
		}, {
			dataField : "part_creator",
			dataType : "string",
			headerText : "등록자",
			width : 100,
		} ]
	}, {
		headerText : "변경 후",
		children : [ {
			dataField : "next_name",
			dataType : "string",
			headerText : "품목명",
			cellColMerge : true, // 셀 가로 병합 실행
			cellColSpan : 4, // 셀 가로 병합 대상은 6개로 설정
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.next_oid;
					if (oid === "") {
						return false;
					}
					const url = getCallUrl("/part/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			},
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (item.afterMerge === true) {
					return "afterMerge";
				}
				return null;
			},
		}, {
			dataField : "next_state",
			dataType : "string",
			headerText : "상태",
			width : 80,
		}, {
			dataField : "next_version",
			dataType : "string",
			headerText : "REV",
			width : 80,
		}, {
			dataField : "next_creator",
			dataType : "string",
			headerText : "등록자",
			width : 100,
		} ]
	}, {
		headerText : "BOM",
		children : [ {
			dataField : "",
			dataType : "string",
			headerText : "",
			width : 140,
			renderer : {
				type : "ButtonRenderer",
				labelText : "BOM 비교",
				onClick : function(event) {
					const oid = event.item.next_oid;
				}
			}
		}, {
			dataField : "",
			dataType : "string",
			headerText : "",
			width : 140,
			renderer : {
				type : "ButtonRenderer",
				labelText : "BOM 보기",
				onClick : function(event) {
					const oid = event.item.next_oid;
					if (oid === "") {
						alert("변경후 품목이 없습니다.");
						return false;
					}
					const url = getCallUrl("/bom/view?oid=" + oid);
					_popup(url, 1600, 800, "n");
				}
			}
		} ]
	} ]

	function createAUIGrid510(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			enableFilter : true,
			autoGridHeight : true,
			fillColumnSizeMode : true,
			enableCellMerge : true,
			cellColMergeFunction : function(rowIndex, columnIndex, item) {
				if (item.preMerge === true) {
					return true;
				}
				if (item.afterMerge === true) {
					return true;
				}
				return false;
			},
		}
		myGridID510 = AUIGrid.create("#grid510", columnLayout, props);
		AUIGrid.setGridData(myGridID510,
<%=AUIGridUtil.include(oid, "part")%>
	);
	}
</script>