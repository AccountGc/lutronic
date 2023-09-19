<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>




<style type="text/css">
	/* 계층 트리 아이콘  Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif*/
	.aui-grid-tree-plus-icon {
		display: inline-block;
		width:16px;
		height:16px;
		border:none;
		background: url(/Windchill/extcore/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif) 50% 50% no-repeat;
		background-size:16px;
		vertical-align:bottom;
		margin: 0 2px 0 0;
	}
	/*평쳤을때 이미지*/
	.aui-grid-tree-minus-icon {
		display: inline-block;
		width:16px;
		height:16px;
		border:none;
		background: url(/Windchill/extcore/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/minus.gif) 50% 50% no-repeat;
		background-size:16px;
		vertical-align:bottom;
		margin: 0 2px 0 0;
	}
	/*데이터 앞 이미지*/
	.aui-grid-tree-branch-icon {
		display: inline-block;
		width: 16px;
		height: 16px;
		background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
		background-size:16px;
		vertical-align: bottom;
		
	}
	
	.aui-grid-tree-branch-open-icon {
		display: inline-block;
		width: 16px;
		height: 16px;
		background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
		background-size:16px;
		vertical-align: bottom;
		
	}
	
	.aui-grid-tree-leaf-icon {
		display: inline-block;
		width: 16px;
		height:16px;
		background: url(/Windchill/wtcore/images/part.gif) no-repeat;
		background-size:16px;
		vertical-align: bottom;
		margin: 0 2px 0 4px;
	}
	/* 계층 트리 아이콘 끝*/
	.AUI_left {
		text-align: left;
	}
</style>

<% 
	String oid = (String) request.getAttribute("oid");
	String title = (String) request.getAttribute("title");
	String lastedoid = (String) request.getAttribute("lastedoid");
	String number = (String) request.getAttribute("number");
	List<Map<String, String>> list = (List<Map<String, String>>) request.getAttribute("list");

%>
<form name=PartTreeForm id="PartTreeForm" method="post">


<input type="hidden" name="oid" id="oid" value="<%= oid %>"/>

<input type="hidden" name="oid2" id="oid2"/>


<table>

	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><%= title %> BOM</B></td>
		   		</tr>
			</table>
		    <table  border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td width="40%" align="left">
						<input type="button" value="펼치기" title="펼치기" id="expand" class="width-80">
						
						<select id="depthSelect" onchange="showItemsOnDepth()" class="AXSelect width-120">
							<option value="expandAll">전체확장</option>
							<option value="1" selected>1레벨</option>
							<option value="2">2레벨</option>
							<option value="3">3레벨</option>
							<option value="4">4레벨</option>
							<option value="5">5레벨</option>
						</select>
						
						<select name="desc" id="desc" class="AXSelect width-100">
							<option value="true" >정전개</option>
							<option value="false">역전개</option>
			    		</select>
						<input type="checkbox" name="checkDummy" id="checkDummy" value="true"  onchange="viewAUIPartBomAction()" checked> 더미제외
			    	</td>
			    	
		    		<td  align="right">
		    			<input type="button" value="저장" title="저장" class="gray" onclick="saveBtn();">
						<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
									
		    		</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
		
    		<div id="partTree" ></div>
		</td>
	</tr>
	<tr>
		
		<td>
			<div id="grid_wrap" style="height:500px; margin-top: 2px; border-top: 3px solid #244b9c;">
			</div>
		</td>
	</tr>
	

</table>

</form>

<script type="text/javascript">

	var rowId = 0;
	<%----------------------------------------------------------
	*                      AUI Tree 그리드 
	----------------------------------------------------------%>
	var myBOMGridID;
	
	var defaultGridHeight = 61;
	var rowHeight = 26;
	var headerHeight = 24;
	var footerHeight = 30;
	var isExpand = false;
	//AUIGrid 칼럼 설정
	var columnLayout = [ 
		{
			dataField : "level",
			headerText : "Level",
			width: "5%"
		}, 
	    {
			dataField : "number",
			headerText : "부품번호",
			width: "15%",
			filter : {
				showIcon : true
			},
		}, 
		{
		    dataField: "dwgNo",
		    headerText: "도면번호",
		    width: "7%",//dwgOid
			renderer : { // HTML 템플릿 렌더러 사용
				type : "TemplateRenderer"
			},
			filter : {
				showIcon : true
			},
			labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
				
				var temp = "<a href=javascript:openView('" + item.dwgOid + "') style='line-height:26px;'>" + value + "</a>"
				return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
			}
		}, 
		
		{
			dataField: "name",
			headerText: "부품명",
			style: "AUI_left",
			width : "15%",
			filter : {
				showIcon : true
			},
			renderer : { // HTML 템플릿 렌더러 사용
				type : "TemplateRenderer"
			},
			labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
				var temp = "<a href=javascript:openView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
				return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
			}
		}, 
		{
			dataField: "rev",
			headerText: "REV",
			width: "5%"
		}, 
		{
			dataField: "remarks",
			headerText: "OEM Info.",
			width: "10%"
		}, 
		
		{
			dataField: "state",
			headerText: "상태",
			width: "8%",
			filter : {
				showIcon : true
			},
		}, 
		
		{
			dataField: "modifier",
			headerText: "수정자",
			width: "5%",
			filter : {
				showIcon : true
			},
		}, 
		{
			dataField: "spec",
			headerText: "사양",
			width: "10%",
			filter : {
				showIcon : true
			},
		},
		{
			dataField: "quantity",
			headerText: "수량",
			width: "5%"
		},
		{
			dataField: "ecoNo",
			headerText: "ECO NO.",
			width: "10%",
			filter : {
				showIcon : true
			},
		},
		{
			dataField: "model",
			headerText: "프로젝트코드",
			width: "10%",
			filter : {
				showIcon : true
			},
		},
		{
			dataField: "deptcode",
			headerText: "부서",
			width: "5%",
			filter : {
				showIcon : true
			},
			
		},
		{
			dataField: "manufacture",
			headerText: "MANUFACTURER",
			width: "10%",
			filter : {
				showIcon : true
			},
			
		},
		{
			dataField: "productmethod",
			headerText: "제작방법",
			width: "8%",
			filter : {
				showIcon : true
			},
			
		},
		
	];

	var auiGridProps = {
		/********트리 그리드************/
		softRemoveRowMode : false,
		showRowCheckColumn : false,
		// singleRow 선택모드
		showSelectionBorder : true,
		displayTreeOpen : true,
		// 편집 가능 여부
		editable : false,
		// 엑스트라 행 체크박스 칼럼이 트리 의존적인지 여부
		// 트리 의존적인 경우, 부모를 체크하면 자식도 체크됨.
		rowCheckDependingTree : true,
		// 트리 컬럼(즉, 폴딩 아이콘 출력 칼럼) 을 인덱스1번으로 설정함(디폴트 0번임)
		treeColumnIndex : 1,
		// 필터사용
		enableFilter : true,
		// 일반 데이터를 트리로 표현할지 여부(treeIdField, treeIdRefField 설정 필수)
		flat2tree : true,
		useContextMenu : true,
		enableSorting : false,
		//fixed 기능 부품 번호
		fixedColumnCount  : 2,
		// 트리그리드에서 하위 데이터를 나중에 요청하기 위한 true 설정
		treeLazyMode: true,	
		treeLevelIndent : 35, 
	};
	
	
	
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	
	AUIGrid.bind(myBOMGridID, "treeLazyRequest", function (event) {
		var item = event.item;

		// 자식 데이터 요청
		var  params =  {"oid":item.oid};
		var url	= getCallUrl("/part/viewAUIPartBomChildAction");
		
		call(url, params, function(data) {
			for(var i =0;i<data.length;i++){
				data[i].level =event.item._$depth+1;
			}
			
			event.response(data);
		},"POST");
		
		
		
	});
	
	AUIGrid.bind(myBOMGridID, "contextMenu", function(event) {
		AUIGrid.setSelectionByIndex(myBOMGridID, event.rowIndex, event.columnIndex);
		var menus = [ {
			label : "체크인",
			callback : contextItemHandler
		}, {
			label : "체크아웃",
			callback : contextItemHandler
		}, {
			label : "체크아웃 취소",
			callback : contextItemHandler
		}, {
			label : "기존부품 추가",
			callback : contextItemHandler
		}, {
			label : "교체",
			callback : contextItemHandler
		}, {
			label : "_$line"
		}, {
			label : "레벨 올리기 (Shift + Alt + ←)",
			callback : contextItemHandler
		}, {
			label : "레벨 내리기 (Shift + Alt + →)",
			callback : contextItemHandler
		}, {
			label : "위로 (Shift + Alt + ↑)",
			callback : contextItemHandler
		}, {
			label : "아래로 (Shift + Alt + ↓)",
			callback : contextItemHandler
		}, {
			label : "_$line"
		}, {
			label : "작업취소 Undo (Ctrl + z)",
			callback : contextItemHandler
		}, {
			label : "작업취소 Redo (Ctrl + y)",
			callback : contextItemHandler
		}, {
			label : "삭제 (Ctrl + Delete)",
			callback : contextItemHandler
		}, ]
		if (event.dataField == "number") {
			var item = event.item;
			// 			if (item.state == "릴리즈됨") {
			// 				return false;
			// 			}
			return menus;
		}
		return false;
	});
	
	
	
	function contextItemHandler(event) {
		var item = event.item;
		var rowIndex = event.rowIndex;
		switch (event.contextIndex) {
		case 0:
			var url	= getCallUrl("/part/partCheckIn");
			call(url, item, function(data) {
				if(!isEmpty(data.msg)){
					alert(data.msg);	
				}
				viewAUIPartBomAction();
			},"POST");
			break;
		case 1:
			var url	= getCallUrl("/part/partCheckOut");
			call(url, item, function(data) {
				if(!isEmpty(data.msg)){
					alert(data.msg);	
				}
				viewAUIPartBomAction();
			},"POST");
			break;
		case 2:
			var url	= getCallUrl("/part/partUndoCheckOut");
			call(url, item, function(data) {
				if(!isEmpty(data.msg)){
					alert(data.msg);	
				}
				viewAUIPartBomAction();
			},"POST");
			break;
		case 3:
// 			var url = "/Windchill/platform/part/exist?partTypeCd=" + item.partTypeCd + "&rowId=" + item._$uid + "&poid=" + item.oid + "&box=2&callBack=_child2";
// 			_popup(url, "", "", "f");
			break;
		case 4:
// 			var url = "/Windchill/platform/part/exist?partTypeCd=" + item.partTypeCd + "&rowId=" + item._$uid + "&poid=" + item.oid + "&box=2&callBack=_child2";
// 			_popup(url, "", "", "f");
			break;	
		
			
			
			
		case 6:
			AUIGrid.outdentTreeDepth(myBOMGridID);
			break;
		case 7:
			AUIGrid.indentTreeDepth(myBOMGridID);
			var parentItem = AUIGrid.getParentItemByRowId(myBOMGridID, item.uid);
			break;
		case 8:
			AUIGrid.moveRowsToUp(myBOMGridID);
			break;
		case 9:
			AUIGrid.moveRowsToDown(myBOMGridID);
			break;
		case 11:
			AUIGrid.undo(myBOMGridID);
			break;
		case 12:
			AUIGrid.redo(myBOMGridID);
			break;
		case 13:
			AUIGrid.removeRow(myBOMGridID, "selectedIndex");
			break;
		}
	};

	
	
	<%----------------------------------------------------------
	*                      페이지 초기 설정
	----------------------------------------------------------%>
	$(document).ready(function() {
		
		
		
		//BOM Search
		viewAUIPartBomAction();
		selectbox("depthSelect");
		selectbox("desc");
		selectbox("baselineView");
		selectbox("baseline");
		
		
		
// 		popupAUIResize();
	});

<%----------------------------------------------------------
*                      view 리스트 가져오기
----------------------------------------------------------%>
function getViews() {
	var form = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("common", "getViews", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "View 목록 에러";
			alert(msg);
		},

		success:function(data){
			for(var i=0; i<data.length; i++) {
				$("#view").append("<option value='"+data[i]+"'>" + data[i] + "</option>");
			}
		}
	});
}

	$(function() {
		
		<%----------------------------------------------------------
		*                      상위품목 버튼
		----------------------------------------------------------%>
		$("#upItem").click(function() {
			viewBomList("up");
		})
		<%----------------------------------------------------------
		*                      하위품목 버튼
		----------------------------------------------------------%>
		$("#downItem").click(function() {
			viewBomList("down");
		})
		<%----------------------------------------------------------
		*                      END ITEM 버튼
		----------------------------------------------------------%>
		$("#endItem").click(function() {
			viewBomList("end");
		})
	
	<%----------------------------------------------------------
	*                      상세정보 버튼
	----------------------------------------------------------%>
	$("#detailView").click(function() {
		openView($("#oid").val());
	})
	$("#latestBom").click(function() {
		$("#baseline").val("");
		rowId = 0;
		partTreeGrid.clearAll();
		viewAUIPartBomAction('0', $('#oid').val(), "true", '');
	})
	
	<%----------------------------------------------------------
	*                      Drawing Down 버튼
	----------------------------------------------------------%>
	/*
	$("#drawingDown").click(function() {
		$("#PartTreeForm").attr("method", "post");
		$("#PartTreeForm").attr("action", getURLString("drawing", "partTreeDrawingDown", "do")).submit();
	})
	*/
	
	<%----------------------------------------------------------
	*                     모두 펼치기
	----------------------------------------------------------%>
	$("#expand").click(function() {
		if (!isExpand) {
			AUIGrid.expandAll(myBOMGridID);
			$("#expand").val("접기")
			isExpand = true;
		} else {
			$("#expand").val("펼치기")
			AUIGrid.collapseAll(myBOMGridID);
			isExpand = false;
		}
		
		
		$("#depthSelect option:eq(0)").attr("selected","selected");
		
	})
	<%----------------------------------------------------------
	*                     엑셀 다운로드
	----------------------------------------------------------%>
	$("#excelDown").click(function() {
		
		//alert($("#excelDownOption").val());
		//if( $("#excelDownOption").val() =="1"){
			expandAll();
		//}
		var excelName="<c:out value='${baseName}'/>";
		var fileName= "<c:out value='${number}'/>";
		exportAUIBOMExcel(fileName+excelName)
	})
	
	<%----------------------------------------------------------
	*                     부품 첨부 파일 다운로드
	----------------------------------------------------------%>
	$("#attachDown").click(function() {
		attachDown();
		//alert("필터는 최기화 됩니다.");
		//batchDownLoad('BOM')
	})
	$("#drawingDown").click(function() {
		//attachDown();
		//alert("필터는 최기화 됩니다.");
		drawingDown();
	})
	
	<%----------------------------------------------------------
	*                    Baseline 비교
	----------------------------------------------------------%>
	$("#baseline").change(function() {
		/*
	    if($("input[name='baseCheck']:checked").length == 0) {
	    	alert("비교할 BOM이 선택 되지 않았습니다.");
	    	return;
	    }
	    
	    var ss = $("input[name='baseCheck']:checked").attr("value").split("$");
	    
	    $("#oid2").val(ss[0]);
	    $("#baseline2").val(ss[1]);
	    
	    $("#PartTreeForm").attr("action", getURLString("part", "partTreeCompare", "do")).submit();
	    */
	    
	    var oid = $("#oid2").val($('#baseline option:selected').attr('title'));
	    //$("#baseline2").val($('#baseline').val());
	    $("#PartTreeForm").attr("action", getURLString("part", "partTreeCompare", "do")).submit();
	    
	})
	<%----------------------------------------------------------
	*                    Baseline 보기
	----------------------------------------------------------%>
	$("#baselineView").change(function() {
		
		var tempValue = $('#baselineView option:selected').attr('value');
		if( tempValue.indexOf("WTPart") > 0) {
			var oid = tempValue;
			
			$("#PartTreeForm").attr("action", getCallUrl("/part/viewAUIPartBom") + "?oid="+oid).submit();
		}else{
			var oid = $('#baselineView option:selected').attr('title');
			var baseline = $('#baselineView option:selected').attr('value');
			
			$("#PartTreeForm").attr("action", getCallUrl("/part/viewAUIPartBom") + "?oid="+oid+ "&baseline="+baseline).submit();
			
		}
			
		
		
	})
	
	<%----------------------------------------------------------
	*                      정전개 / 역전개 변경시
	----------------------------------------------------------%>
	$("#desc").change(function() {
		
		viewAUIPartBomAction();
		
	})
	
	
})

function expandAll(){
	
	if(confirm("전체 다운로드 하시겠습니까?[필터는 초기화 됩니다.] \n*취소시 화면에  표현된 BOM만 다운로드 됩니다.")){
		if(!isExpand){
			AUIGrid.expandAll(myBOMGridID);
			isExpand = true;
		}
		AUIGrid.clearFilterAll(myBOMGridID);
		
	}
}
	<%----------------------------------------------------------
	*                      Bom 리스트 검색
	----------------------------------------------------------%>
	function viewAUIPartBomAction(){
		isExpand = false;
		let params = new Object();
		const field = ["oid","view","desc","baseline2","checkDummy"];
//			const latest = !!document.querySelector("input[name=latest]:checked").value; 
		params = toField(params, field);
		
		var url	= getCallUrl("/part/bomEditorList");
		AUIGrid.showAjaxLoader(myBOMGridID);
		call(url, params, function(data) {
			var gridData = data;
			gridData[0].level=1;
			// 그리드에 데이터 세팅
			AUIGrid.removeAjaxLoader(myBOMGridID);
			AUIGrid.setGridData(myBOMGridID, gridData);
			//2Level까지만 펼치기
// 			AUIGrid.showItemsOnDepth(myBOMGridID, 1 );
			var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
			//alert(totalDepth);
			//totalDepth = totalDepth -1;
			
			setDepthList(totalDepth)
		},"POST");
		
	}

<%----------------------------------------------------------
*                      선택한 Depth 만큼 펴칠기
----------------------------------------------------------%>
function showItemsOnDepth(event) {
	var depthSelect = document.getElementById("depthSelect");
	var  depth = depthSelect.value;
	
	// 해당 depth 까지 오픈함
	AUIGrid.showItemsOnDepth(myBOMGridID, Number(depth) );
}

<%----------------------------------------------------------
*                     동적으로 Level 표시
----------------------------------------------------------%>
function setDepthList(totalDepth){
	$("#depthSelect").empty();
	$("#depthSelect").append("<option value='default' selected='selected' disabled='disabled'>특정 계층 선택</option>");
	
	for(var i=1; i<=totalDepth; i++) {
		var temp = i;
		$("#depthSelect").append("<option value='" +i + "'> Level " +(temp-1)+ "</option>");
	}
	selectbox("depthSelect");
}

<%----------------------------------------------------------
*                     부품 첨부 파일 다운로드
----------------------------------------------------------%>
window.attachDown = function() {
	
	batchBOMSelectDownLoad($("#oid").val(),"attach");
}

<%----------------------------------------------------------
*                     부품 첨부 파일 다운로드
----------------------------------------------------------%>
window.drawingDown = function() {
	
	batchBOMSelectDownLoad($("#oid").val(),"drawing");
}

window.mygridCheck = function() {
	
	
	var checkedItems = AUIGrid.getCheckedRowItems(myBOMGridID);
	
	/*
	for(var i = 0; i < checkedItems.length; i++){
		var checkedItem = checkedItems[i].item;
		alert(checkedItem.id);
		console.log("===mygridCheck===" +checkedItem.id )
	}
	*/
	return checkedItems
}

	<%----------------------------------------------------------
	*                      Bom Type에 따른 bom 검색
	----------------------------------------------------------%>
	function viewBomList(bomType){
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/part/bomPartList?oid=" + $("#oid").val() + "&bomType=" + bomType);
		_popup(url, 800, 550,"n");
	}
	

	$(window).resize(function() {
		AUIGrid.resize("#grid_wrap");
	});

</script>