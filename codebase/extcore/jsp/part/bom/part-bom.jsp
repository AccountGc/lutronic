<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getParameter("oid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
<div style="display: flex; gap: 5px; border: 1px solid #3180c3; padding: 5px">
<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
				<img src="/Windchill/netmarkets/images/checkin.png" title="체크인">
				<img src="/Windchill/netmarkets/images/checkout.png" title="체크아웃">
				<img src="/Windchill/netmarkets/images/cancel9x9.gif" title="체크아웃 취소">
				<img src="/Windchill/netmarkets/images/part_add.gif" title="부품추가">
				<img src="/Windchill/netmarkets/images/remove.gif" title="부품제거">
				<img src="/Windchill/netmarkets/images/info.gif" title="상세정보">
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
</div>
<div style="display: flex; ">
	<div id="grid_ver" style="height: 300px; width: 50%;  border-top: 1px solid #3180c3; margin: 5px;"></div>
	<div style="height: 300px; width: 50%;  border-top: 1px solid #3180c3; margin: 5px; padding: none;">
		<div id="tabs" style="height: 100%; width: 100%; box-sizing: border-box;">
			<ul>
				<li>
					<a href="#tabs-1">시각화</a>
				</li>
				<li>
					<a href="#tabs-2">부품정보</a>
				</li>
			</ul>
			<div id="tabs-1">
				<table class="view-table">
					<tr>
						<td class="tdwhiteL" align="center" >
		<%--                    	<jsp:include page="/eSolution/drawing/thumbview.do" flush="true"> --%>
		<%-- 						<jsp:param name="oid" value="<%=data.getEpmOid() %>" /> --%>
		<%-- 					</jsp:include> --%>
						</td>
					</tr>
				</table>
			</div>
			<div id="tabs-2">
				<table class="view-table">
					<colgroup>
						<col width="150">
						<col width="500">
						<col width="150">
						<col width="500">
					</colgroup>
					<tr>
						<th class="lb" colspan="5">부품정보</th>
					</tr>
					<tr>
						<th class="lb">품목번호</th>
						<td class="indent5">1010101100</td>
						<th class="lb">품목명</th>
						<td class="indent5">의료기기</td>
					</tr>
					<tr>
						<th class="lb">상태</th>
						<td class="indent5">승인됨</td>
						<th class="lb">REV</th>
						<td class="indent5">D.1</td>
					</tr>
					<tr>
						<th class="lb">등록자</th>
						<td class="indent5">김준호</td>
						<th class="lb">등록일</th>
						<td class="indent5" >2023-08-10</td>
					</tr>											
				</table>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
let myGridID;
const column = [ {
			dataField : "number",
			headerText : "품목번호",
			dataType : "string",
			width : 180,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "name",
			headerText : "품목명",
			dataType : "string",
			width : 380,
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
			width : 90,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "creator",
			headerText : "등록자",
			dataType : "string",
			width : 140,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "creator",
			headerText : "등록일",
			dataType : "string",
			width : 140,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "oid",
			visible : false
	} ]

	function createAUIGridVer(columnLayout) {
		const props = {
			headerHeight : 30,
			useContextMenu : true,
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			rowCheckToRadio : false,
			displayTreeOpen : true,
			forceTreeView : true
		}
		
		props.treeIconFunction = function(rowIndex, isBranch, isOpen, depth, item) {
			var imgSrc = "/Windchill/extcore/images/icon/part.gif";
			return imgSrc;
		};
		
		myGridID = AUIGrid.create("#grid_ver", columnLayout, props);
		AUIGrid.bind(myGridID, "contextMenu", function(event) {
            var myContextMenus  = [{
               label : "체크인",
            }, {
               label : "체크아웃",
            }, {
               label : "체크아웃 취소",
            }, {
               label : "부품추가",
            }, {
               label : "부품제거",
            }, {
               label : "상세정보",
            }];
            return myContextMenus;
         });
<%-- 		AUIGrid.setGridData(verGridID, <%=json%>); --%>
		
		var json = [{
		    "number" : "1010101100",
		    "name" : "의료기기",
		    "location" : "",
		    "drawingNum" : "ND",
		    "version" : "D.1",
		    "state" : "승인됨",
		    "creator" : "김준호",
		    "createDate" : "2023-08-10",
		    "children": [{
			    "number" : "3410001234",
			    "name" : "반제품",
			    "location" : "",
			    "drawingNum" : "ND",
			    "version" : "C.1",
			    "state" : "승인됨",
			    "creator" : "박영선",
			    "createDate" : "2023-08-10",
			    "children": []
			}, {
			    "number" : "3410101320",
			    "name" : "반제품",
			    "location" : "",
			    "drawingNum" : "ND",
			    "version" : "E.5",
			    "state" : "승인됨",
			    "creator" : "장원정",
			    "createDate" : "2023-08-10",
			    "children": [{
				    "number" : "5432110501",
				    "name" : "나사",
				    "location" : "",
				    "drawingNum" : "ND",
				    "version" : "F.6",
				    "state" : "승인됨",
				    "creator" : "박영선",
				    "createDate" : "2023-08-10"
				}, {
				    "number" : "6432111103",
				    "name" : "볼트",
				    "location" : "",
				    "drawingNum" : "ND",
				    "version" : "G.9",
				    "state" : "승인됨",
				    "creator" : "김준호",
				    "createDate" : "2023-08-10"
				}]
			}]
		}]
		AUIGrid.setGridData(myGridID, json);
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGridVer(column);
		AUIGrid.resize(myGridID);
	});
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				let isCreated = false;
				switch (tabId) {
				case "tabs-1":
					break;
				case "tabs-2":
					break;
				}
			},
		});
	});
	
</script>
</body>
</html>