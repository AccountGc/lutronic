<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.ecn.dto.EcnDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcnDTO dto = (EcnDTO) request.getAttribute("dto");
ArrayList<EChangeRequest> crList = dto.getList();
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
%>

<style type="text/css">
.isSend {
	background-color: #dedede !important;
	color: red !important;
	font-weight: bold !important;
}
</style>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<%
int idx = 1;
for (EChangeRequest ecr : crList) {
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECN (<font color="red"><b><%=ecr.getEoNumber()%></b></font>)
			</div>
		</td>
		<td class="right">
			<input type="button" value="ERP전송" title="ERP전송" class="blue" onclick="send<%=idx%>();">
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
<div id="grid_wrap<%=idx%>" style="height: 80px; border-top: 1px solid #3180c3; margin: 5px;"></div>


<script type="text/javascript">
	let myGridID<%=idx%>;
	const columns<%=idx%> = [ {
		dataField : "ecoNumber",
		headerText : "ECO 번호",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		width : 200,
		editable : false,
	}, {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		width : 120,
		editable : false
	}, {
		dataField : "version",
		headerText : "REV",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		headerText : "확정 인허가일",
		children : [ {
		<%
			int i = 1;
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
		<%
			}
			i++;
		}
		%>
		}]
	} ]

	function createAUIGrid<%=idx%>(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			showRowCheckColumn : true,
			selectionMode : "multipleCells",
			autoGridHeight : true,
			enableCellMerge : true,
			editable : true
		}
		myGridID<%=idx%> = AUIGrid.create("#grid_wrap<%=idx%>", columnLayout, props);
		AUIGrid.bind(myGridID<%=idx%>, "cellEditBegin", auiCellEditBeginHandler<%=idx%>);
		
		// V스크롤 체인지 핸들러.
// 		AUIGrid.bind(myGridID "vScrollChange", function (event) {
// 			//console.log(event.type + ", position : " + event.position + ", (min : " + event.minPosition + ", max : " + event.maxPosition);
// 			AUIGrid.setRowPosition(myGridID2, event.position); // 수평 스크롤 이동 시킴..
// 		});

	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid<%=idx%>(columns<%=idx%>);
		AUIGrid.resize(myGridID<%=idx%>);
	});
	
	function auiCellEditBeginHandler<%=idx%>(event) {
		const item = event.item;
		const dataField = event.dataField;
		const rowIndex = event.rowIndex;
		const isSend = AUIGrid.getCellValue(myGridID<%=idx%>, rowIndex, dataField);
		if(isSend !== undefined) {
			alert("SAP로 전송 완료된 값입니다.");
			return false;
		}
		return true;
	}

	function send<%=idx%>() {

		const editRows = AUIGrid.getEditedRowItems(myGridID<%=idx%>);

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
<%
idx++;
}
%>