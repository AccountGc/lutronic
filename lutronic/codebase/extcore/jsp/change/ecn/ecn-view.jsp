<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.change.ecn.service.EcnHelper"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.ecn.dto.EcnDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcnDTO dto = (EcnDTO) request.getAttribute("dto");
JSONArray arr = (JSONArray) request.getAttribute("arr");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
boolean edit = dto.isEditable();
%>

<style type="text/css">
.isSend {
	background-color: #dedede !important;
	color: red !important;
	font-weight: bold !important;
}

.workEnd {
	background-color: rgb(200, 255, 203);
}
</style>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECN 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="ERP전송" title="ERP전송" class="blue" onclick="send();">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 80px; border-top: 1px solid #3180c3; margin: 5px;"></div>


<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "rate",
		headerText : "진행율",
		dataType : "string",
		width : 80,
		editable : false,
		renderer: {
			type: "BarRenderer",
			min: 0,
			max: 100
		},		
	},{
		dataField : "partNumber",
		headerText : "제품번호",
		dataType : "string",
		width : 100,
		editable : false,
		cellMerge : true,
	},{
		dataField : "partName",
		headerText : "제품명",
		dataType : "string",
		width : 250,
// 		style : "aui-left",
		editable : false,
		cellMerge : true,
	},{
		dataField : "crNumber",
		headerText : "CR 번호",
		dataType : "string",
		width : 140,
		editable : false,
		cellMerge : true,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.coid;
				const url = getCallUrl("/cr/view?oid=" + oid);
				_popup(url, "", "", "f");
			}
		},		
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 200,
		editable : false,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.poid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},	
	}, {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 120,
		editable : false,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.poid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},			
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 80,
		editable : false
	}, {
		headerText : "확정 인허가일",
		children : [ {
		<%int i = 1;
for (Map<String, String> map : list) {
	String dataField = map.get("code");%>	
				dataField : "<%=dataField%>_date",
				headerText : "<%=map.get("name")%>",
				dataType : "date",
				dateInputFormat : "yyyy-mm-dd", // 실제 데이터의 형식 지정
				formatString : "yyyy년 mm월 dd일", // 실제 데이터 형식을 어떻게 표시할지 지정
				width : 160,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const isSend = item.<%=dataField%>_isSend;
					if(isSend === true) {
						return "isSend";
					}
					return null;
				},				
				renderer : {
					type : "IconRenderer",
					iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
					iconHeight : 16,
					iconPosition : "aisleRight",
					iconTableRef : { // icon 값 참조할 테이블 레퍼런스
						"default" : "/Windchill/extcore/component/AUIGrid/images/calendar-icon.png" // default
					},
					onClick : function(event) {
						// 달력 아이콘 클릭하면 실제로 달력을 띄움.
						// 즉, 수정으로 진입함.
						AUIGrid.openInputer(event.pid);
					}
				},
				editRenderer : {
					type : "CalendarRenderer",
					defaultFormat : "yyyy-mm-dd", // 달력 선택 시 데이터에 적용되는 날짜 형식
					showPlaceholder : true, // defaultFormat 설정된 값으로 플래스홀더 표시
					showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 출력 여부
					onlyCalendar : false, // 사용자 입력 불가, 즉 달력으로만 날짜입력 (기본값 : true)
					showExtraDays : true, // 지난 달, 다음 달 여분의 날짜(days) 출력
					showTodayBtn : true, // 오늘 날짜 선택 버턴 출력
					showUncheckDateBtn : true, // 날짜 선택 해제 버턴 출력
					todayText : "오늘 선택", // 오늘 날짜 버턴 텍스트
					uncheckDateText : "날짜 선택 해제", // 날짜 선택 해제 버턴 텍스트
					uncheckDateValue : "-", // 날짜 선택 해제 버턴 클릭 시 적용될 값.
				}				
			<%if (i != list.size()) {%>
			}, {
		<%}
i++;
}%>
		}]
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			enableCellMerge : true,
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
// 			showRowCheckColumn : true,
			selectionMode : "multipleCells",
			autoGridHeight : true,
			editable : true,
			enableCellMerge : true,
// 			rowStyleFunction: function (rowIndex, item) {
// 				if (item.workEnd) {
// 					return "workEnd";
// 				}
// 				return "";
// 			}
		}
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBeginHandler);
		logger(<%=arr%>);
		AUIGrid.setGridData(myGridID, <%=arr%>);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});
	
	function auiCellEditBeginHandler(event) {
		<%if (!edit) {%>
			alert("수정 권한이 없습니다.\n담당자 및 관리자만 수정가능합니다.");
			return false;
		<%}%>
		const item = event.item;
		const dataField = event.dataField;
		const k = dataField.substring(0, dataField.lastIndexOf("_"));
		const rowIndex = event.rowIndex;
		const isSend = AUIGrid.getCellValue(myGridID, rowIndex, k + "_isSend");
		
		if(isSend) {
			alert("이미 SAP로 전송된 인허가일이 있습니다.");
			return false;
		}
		
		return true;
	}

	function send() {

		const editRows = AUIGrid.getEditedRowItems(myGridID);
		logger(editRows);
		if (editRows.length === 0) {
			alert("수정 내역이 없습니다.");
			return false;
		}

		if (!confirm("수정 내용을 SAP로 전송 하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/ecn/send");
		const oid = document.getElementById("oid").value;
		const params = {
			oid : oid,
			editRows : editRows
		};
		logger(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecn/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				clsoeLayer();
			}
		}, "DELETE");
	}
</script>