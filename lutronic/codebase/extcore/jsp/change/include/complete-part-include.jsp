<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean multi = Boolean.parseBoolean(request.getParameter("multi"));
boolean view = "view".equals(mode);
boolean update = "update".equals(mode);
boolean create = "create".equals(mode);
%>
<%
if (create || update) {
%>
<input type="button" value="추가" title="추가" class="blue" onclick="popup104();">
<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow104();">
<%
}
%>
<div id="grid104" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID104;
	const columns104 = [ {
		dataField : "_3d",
		headerText : "3D",
		dataType : "string",
		width : 60,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "_2d",
		headerText : "2D",
		dataType : "string",
		width : 60,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "step",
		headerText : "STEP",
		dataType : "string",
		width : 60,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "dxf",
		headerText : "DXF",
		dataType : "string",
		width : 60,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
		},
	}, {
		dataField : "pdf",
		headerText : "PDF",
		dataType : "string",
		width : 60,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : false,
		},
	}, {
		headerText : "변경이력",
		width : 80,
		renderer : {
			type : "IconRenderer",
			iconPosition : "aisleCenter", // 아이콘 위치
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/extcore/images/help.gif" // default
			},
			onClick : function(event) {
				const oid = event.item.part_oid;
				const url = getCallUrl("/part/changeList?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
		filter : {
			showIcon : false,
		},
	}, {
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
		},
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		style : "aui-left",
		width : 380,
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
		},
	}, {
		dataField : "location",
		headerText : "품목분류",
		dataType : "string",
		width : 180,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 90,
		renderer : {
			type : "TemplateRenderer"
		},
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "remarks",
		headerText : "OEM Info.",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "createdDate_txt",
		headerText : "등록일",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "modifiedDate_txt",
		headerText : "수정일",
		dataType : "string",
		width : 140,
		filter : {
			showIcon : true,
		},
	}, {
		dataField : "ecoNo",
		headerText : "BOM",
		dataType : "string",
		width : 80,
		filter : {
			showIcon : false,
		},
	} ]

	function createAUIGrid104(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : true,
			selectionMode : "multipleCells",
			<%if (create || update) {%>
			showStateColumn : true,
			showRowCheckColumn : true,
			<%}%>
			<%if (!multi) {%>
			rowCheckToRadio : true,
			<%}%>
			enableFilter : true,
			autoGridHeight : true
		}
		myGridID104 = AUIGrid.create("#grid104", columnLayout, props);
		<%if (view || update) {%>
		AUIGrid.setGridData(myGridID104, <%=AUIGridUtil.include(oid, "part")%>);
		<%}%>
	}

	function popup104() {
		const url = getCallUrl("/part/popup?method=insert104&multi=true");
		_popup(url, 1600, 800, "n");
	}

	function insert104(arr, callBack) {
		arr.forEach(function(dd) {
			const rowIndex = dd.rowIndex;
			const item = dd.item;
			const unique = AUIGrid.isUniqueValue(myGridID104, "part_oid", item.part_oid);
			if (unique) {
				AUIGrid.addRow(myGridID104, item, rowIndex);
			} else {
				// 중복은 그냥 경고 없이 처리 할지 합의?
				alert(item.number + " 품목은 이미 추가 되어있습니다.");
			}
		})
		callBack(true);
	}

	function deleteRow104() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID104);
		if (checkedItems.length === 0) {
			alert("삭제할 행을 선택하세요.");
			return false;
		}

		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID104, rowIndex);
		}
	}
</script>