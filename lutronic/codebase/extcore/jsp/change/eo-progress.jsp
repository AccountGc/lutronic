<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.development.service.DevelopmentHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 설계변경 진행상황 (EO 기준)
			</div>
		</td>
	</tr>
</table>
<div id="grid_eo" style="height: 180px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let eoGridID;
	const columnEO = [ {
		headerText : "EO",
		children : [{
			dataField : "eoNumber",
			headerText : "EO번호",
			width: 180,
			dataType : "string",
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "eoCreate",
			headerText : "등록",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "eoActivity",
			headerText : "ECA활동",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "ecpr",
			headerText : "ECPR",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "eoIng",
			headerText : "승인중",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "eoComplete",
			headerText : "승인완료",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}]
	}, {
		headerText : "ECN",
		children : [{
			dataField : "ecnNumber",
			headerText : "ECO번호",
			width: 180,
			dataType : "string",
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "ecnCreate",
			headerText : "등록",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "ecnIng",
			headerText : "승인중",
			width: 100,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}, {
			dataField : "ecnComplete",
			headerText : "ERP전송완료",
			width: 110,
			dataType : "string",
			renderer : {
				type : "ImageRenderer",
				imgHeight : 16, // 이미지 높이, 지정하지 않으면 rowHeight에 맞게 자동 조절되지만 빠른 렌더링을 위해 설정을 추천합니다.
				altField : "color", // alt(title) 속성에 삽입될 필드명, 툴팁으로 출력됨
				srcFunction : function(rowIndex, columnIndex, value, item) {
					switch(value) {
						case "green": //완료
						return "/Windchill/extcore/images/task_complete.gif";
						case "blue": //진행중
						return "/Windchill/extcore/images/task_progress.gif";
						default:
						return null; // null 반환하면 이미지 표시 안함.
					}
				}
			},
			filter : {
				showIcon : true,
				inline : true
			}
		}]
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridEO(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			fillColumnSizeMode: true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			enableMovingColumn : true,
			enableFilter : true,
			showInlineFilter : false,
			useContextMenu : true,
			enableRightDownFocus : true,
			filterLayerWidth : 320,
			filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요."
		}
		
		eoGridID = AUIGrid.create("#grid_eo", columnLayout, props);
		
		var jsonEO = [{
		    "eoNumber" : "E-230407",
		    "eoCreate" : "green",
		    "eoActivity" : "green",
		    "ecpr" : "green",
		    "eoIng" : "green",
		    "eoComplete" : "green",
		    "ecnNumber" : "ECN-230410",
		    "ecnCreate" : "green",
		    "ecnIng" : "blue",
		    "ecnComplete" : "",
		}, {
			"eoNumber" : "E-230408",
		    "eoCreate" : "green",
		    "eoActivity" : "green",
		    "ecpr" : "green",
		    "eoIng" : "blue",
		    "eoComplete" : "",
		    "ecnNumber" : "",
		    "ecnCreate" : "",
		    "ecnIng" : "",
		    "ecnComplete" : "",
		}, {
			"eoNumber" : "E-230501",
		    "eoCreate" : "green",
		    "eoActivity" : "green",
		    "ecpr" : "green",
		    "eoIng" : "green",
		    "eoComplete" : "blue",
		    "ecnNumber" : "",
		    "ecnCreate" : "",
		    "ecnIng" : "",
		    "ecnComplete" : "",
		}, {
			"eoNumber" : "E-230503",
		    "eoCreate" : "green",
		    "eoActivity" : "green",
		    "ecpr" : "green",
		    "eoIng" : "green",
		    "eoComplete" : "green",
		    "ecnNumber" : "ECN-230511",
		    "ecnCreate" : "green",
		    "ecnIng" : "green",
		    "ecnComplete" : "blue",
		}]
		AUIGrid.setGridData(eoGridID, jsonEO);
	}
	
</script>