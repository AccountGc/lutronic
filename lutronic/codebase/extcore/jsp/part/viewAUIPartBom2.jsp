<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="/Windchill/jsp/js/dhtmlx/dhtmlx.js" ></script>
<style type="text/css">
/* 계층 트리 아이콘  Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif*/
.aui-grid-tree-plus-icon {
	display: inline-block;
	width:16px;
	height:16px;
	border:none;
	background: url(/Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif) 50% 50% no-repeat;
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
	background: url(/Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/minus.gif) 50% 50% no-repeat;
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
<script>

var rowId = 0;
<%----------------------------------------------------------
*                      AUI Tree 그리드 
----------------------------------------------------------%>
var myBOMGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;
var isExpanded = false;
//AUIGrid 칼럼 설정
var columnLayout = [ 
	/* 
	{
		dataField : "parent",
		headerText : "parent",
		width: "10%"
	},
	{
		dataField : "id",
		headerText : "id",
		width: "0.5%"
	},   */
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
		jsCallback : function(rowIndex, columnIndex, value, item) {
		    alert("( " + rowIndex + ", " + columnIndex + " ) " + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
		 } 

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



<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	
	//Aui 그리드 설정
	var auiGridProps = {
		
		/********트리 그리드************/
			softRemoveRowMode : false,
		
			showRowCheckColumn : true,
			// singleRow 선택모드
			selectionMode : "multipleCells",
			
			showSelectionBorder : true,
			
			displayTreeOpen : true,
			// 편집 가능 여부
			editable : false,
			
			// 엑스트라 행 체크박스 칼럼이 트리 의존적인지 여부
			// 트리 의존적인 경우, 부모를 체크하면 자식도 체크됨.
			rowCheckDependingTree : true,
			
			// 트리 컬럼(즉, 폴딩 아이콘 출력 칼럼) 을 인덱스1번으로 설정함(디폴트 0번임)
			treeColumnIndex :1,
			// 트리그리드에서 하위 데이터를 나중에 요청하기 위한 true 설정
			treeLazyMode : true,
			// 필터사용
			enableFilter : true,
			
			// 일반 데이터를 트리로 표현할지 여부(treeIdField, treeIdRefField 설정 필수)
			flat2tree : true,
			
			// 행의 고유 필드명
			rowIdField : "rowId",
			
			// 트리의 고유 필드명
			treeIdField : "id",
			
			// 계층 구조에서 내 부모 행의 treeIdField 참고 필드명
			treeIdRefField : "parent",
			
			enableSorting : false,
						
	};
	//fixed 기능 부품 번호
	auiGridProps.fixedColumnCount =2;
	// 실제로 #grid_wrap 에 그리드 생성
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	// 트리그리드 lazyLoading 요청 이벤트 핸들러 
	AUIGrid.bind(myBOMGridID, "treeLazyRequest", bomTree_auiGridTreeLazeRequestHandler);
	
	AUIGrid.bind(myBOMGridID, "treeOpenChange", bomTree_treeLazeOpenHandler);
	
	AUIGrid.bind(myBOMGridID, "keyDown", function(event) {
		console.log(event.keyCode);
		try{
	     if(event.keyCode == 39) { // 오른쪽 방향 키
	    	 var selectedItems = AUIGrid.getSelectedItems(myBOMGridID);
	    	 if(selectedItems.length <= 0) return;
	    	 var item = selectedItems[0];
	    	 if(item.columnIndex!=1) return;
	    	 console.log(item);
	          // alert("엔터 키 누름");
	          var plusIconCheck = $('span:contains("'+item.value+'")').prev().prev();
	          var className = plusIconCheck[0].className;
	          console.log(className);
	          if(className=="aui-grid-tree-plus-icon"){
	        	  plusIconCheck[0].click();
	        	  AUIGrid.setSelectionByIndex(myBOMGridID, item.rowIndex+1, 1);
	        	  return false; // 선택 아래로 내리지 않음(즉, 기본행위 안함)
	          }
	     }else if(event.keyCode == 37) { // 왼쪽 방향 키
	    	 var selectedItems = AUIGrid.getSelectedItems(myBOMGridID);
	    	 if(selectedItems.length <= 0) return;
	    	 var item = selectedItems[0];
	    	 if(item.columnIndex!=1) return;
	    	 console.log(item);
	          // alert("엔터 키 누름");
	          var plusIconCheck = $('span:contains("'+item.value+'")').prev().prev();
	          var className = plusIconCheck[0].className;
	          console.log(className);
	          if(className=="aui-grid-tree-minus-icon"){
	        	  plusIconCheck[0].click();
	        	  AUIGrid.setSelectionByIndex(myBOMGridID, item.rowIndex-1, 1); 
	        	  return false; // 선택 아래로 내리지 않음(즉, 기본행위 안함)
	          }
	     }
		}catch(e){}
	     return true;
	});
	//BOM Search
	getAUIBOMRootChildAction();
	
	popupAUIResize();
})
function bomTree_treeLazeOpenHandler(event){
     console.log(event.type + " : " + event.isOpen + ", rowIndex : " + event.rowIndex);
}
//lazyLoading 핸들러
function bomTree_auiGridTreeLazeRequestHandler(event) {
	var item = event.item;

	var oid = item.oid;
	var id = item.id;
	var level = item.level+1;
	var parentOid = item.parentOid;
	$("#callOid").val(oid);
	$("#callLevel").val(level);
	$("#callPOid").val(parentOid);
	$("#pID").val(id);
	var form = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("part", "getAUIBOMPartChildAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data : form,
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
			var gridData = data;
			event.response(gridData);
		}
	}); 
}
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
		getAUIBOMRootChildAction('0', $('#oid').val(), "true", '');
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
	*                     엑셀 다운로드
	----------------------------------------------------------%>
	$("#excelDown").click(function() {
		
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
		
		var tempValue = $('#baselineView option:selected').attr('value')
		alert(tempValue);
		if( tempValue.indexOf("WTPart") > 0) {
			var oid = tempValue;
			
			$("#oid").val(oid);
		
		    $("#PartTreeForm").attr("action", getURLString("part", "viewAUIPartBom2", "do")).submit();
		}else{
			var oid = $('#baselineView option:selected').attr('title');
			var baseline = $('#baselineView option:selected').attr('value');
			$("#oid").val(oid);
			$("#baseline").val(baseline)
			
		    $("#PartTreeForm").attr("action", getURLString("part", "viewAUIPartBom2", "do")).submit();
		}
			
		
		
	})
	
	<%----------------------------------------------------------
	*                      정전개 / 역전개 변경시
	----------------------------------------------------------%>
	$("#desc").change(function() {
		
		getAUIBOMRootChildAction();
		
	})
	
	
})
<%----------------------------------------------------------
*                      Bom 리스트 검색
----------------------------------------------------------%>
function getAUIBOMRootChildAction(){
	isExpanded = false;
	var form = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("part", "getAUIBOMRootChildAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data : form,
		dataType:"json",
		async: true,
		cache: false,
	
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
			var gridData = data;
			AUIGrid.setGridData(myBOMGridID, gridData);
			var data = $(".aui-grid-tree-plus-icon");
			if(data.length>0)
				$(".aui-grid-tree-plus-icon")[0].click();
			AUIGrid.setSelectionByIndex(myBOMGridID, 0, 1); 
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
	    }
		,complete: function() {
			gfn_EndShowProcessing();
	    }
	});
}

//현재 선택된 행의 rowId 값 반환
function getSelectedRowId() {
	var selectedItems = AUIGrid.getSelectedItems(myBOMGridID);
	if(selectedItems.length <= 0)
		return null;
	
	return selectedItems[0].rowIdValue;
}
//자손 행들 얻기
function getDescendants() {
	var descendants;
	
	var rowId = getSelectedRowId(); // 현재 선택된 행의 rowId 값 얻기
	if(rowId != null) {
		descendants = AUIGrid.getDescendantsByRowId(myBOMGridID, rowId); // 자손행들
		if(descendants && descendants.length) {
			return descendants;
		} else {
			//alert("Leaf 에 해당되는 행으로 자손(들)은 없습니다.");
		}
	}
};


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
	var str = getURLString("part", "bomPartList", "do") + "?oid="+$("#oid").val()+"&bomType="+bomType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBomList", opts+rest);
    newwin.focus();
}

</script>

<form name=PartTreeForm id="PartTreeForm" method="post">

<input type="hidden" name="viewName" id="viewName" value="<c:out value='${view }'/>" />

<input type="hidden" name="oid" id="oid" value="<c:out value='${oid }'/>"/>

<input type="hidden" name="callLevel" id="callLevel" value=""/>
<input type="hidden" name="callOid" id="callOid" value=""/>
<input type="hidden" name="pID" id="pID" value=""/>
<input type="hidden" name="callPOid" id="callPOid" value=""/>

<input type="hidden" name="oid2" id="oid2"/>

<input type="hidden" name="baseline2" id="baseline2" value="<c:out value='${baseline }'/>"/>
<table width="100%" border="0" cellpadding="0" cellspacing="0" >

	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white><c:out value='${title }'/> BOM</font></B></td>
		   		</tr>
			</table>
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td width="40%" align="left">
		    			<table border="0" cellpadding="0" cellspacing="4" align="left">
		    				<tr>
					    		<td valign="bottom">
									<select name="desc" id="desc">
										<option value="true" >정전개</option>
										<option value="false">역전개</option>
						    		</select>
						    	</td>
						    	<td valign="bottom">
									<input type="checkbox" name="checkDummy" id="checkDummy" value="true" checked onchange="getAUIBOMRootChildAction()" > 더미제외
								</td>
							</tr>
						</table>
			    	</td>
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									<select name="baselineView" id="baselineView" >
							    		<option value="" selected="selected" disabled="disabled">-- Baseline 보기 --</option>
							    		<option value="<c:out value='${lastedoid }'/>"  ><c:out value='${number }'/>[BOM]</option>
						    			</option>
							    		<c:forEach items="${list }" var="baseline">
							    			<option value='<c:out value="${baseline.baseOid }" />' title="<c:out value="${baseline.partOid }" />">
							    				<c:out value="${baseline.baseName }" />
							    			</option>
							    		</c:forEach>
						    		</select>
								</td>
								<td>
									<select name="baseline" id="baseline" >
							    		<option value="" selected="selected" disabled="disabled">-- Baseline 비교--</option>
						    			<c:forEach items="${list }" var="baseline">
							    			<option value='<c:out value="${baseline.baseOid }" />' title="<c:out value="${baseline.partOid }" />">
							    				<c:out value="${baseline.baseName }" />
							    			</option>
							    		</c:forEach>
						    		</select>
								</td>
								<td>
									<button type="button" name="upItem" id="upItem" class="btnCustom" style="width: 80px">
										<span></span>
										상위품목
									</button>
								</td>
								
					    		<td>
					    			<button type="button" name="downItem" id="downItem" class="btnCustom" style="width: 80px">
										<span></span>
										하위품목
									</button>
								</td>
								
					    		<td>
					    			<button type="button" name="endItem" id="endItem" class="btnCustom" style="width: 90px">
										<span></span>
										END ITEM
									</button>
								</td>
								<!--
								<td>
									<button type="button" name="compare" id="compare" class="btnCustom" style="width: 120px">
										<span></span>
										베이스라인 비교
									</button>
								</td>
								-->
					    		<!-- <td>
					    		
					    			<input type="radio" id="excelDownOption" name="excelDownOption" value="1" checked>:전체
					    			<input type="radio" id="excelDownOption" name="excelDownOption" value="0">:화면
					    		
					    			<button type="button" name="excelDown" id="excelDown" class="btnCustom" style="width: 80px">
										<span></span>
										EXCEL
									</button>
								</td> -->
								
								<td>
									<button type="button" name="attachDown" id="attachDown" class="btnCustom" style="width: 50px">
										<span></span>
										첨부
									</button>
								</td>
								<td>
									<button type="button" name="drawingDown" id="drawingDown" class="btnCustom" style="width: 50px">
										<span></span>
										도면 
									</button>
								</td>
								<td>
									<button type="button" name="" id="" class="btnClose" onclick="self.close()" style="width: 50px">
										<span></span>
										닫기
									</button>
								</td>
							</tr>
						</table>
		    		</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
		
			<table width="100%"  border="0" cellpadding="0" cellspacing="0" class="tablehead" align="center">
	    		<tr>
	    			<td height=1 width=100%></td>
	    		</tr>
			</table>
    		<div id="partTree" style="width:100%;"></div>
		</td>
	</tr>
	<tr>
		
		<td>
			<div id="grid_wrap" style="width: 100%; height:500px; margin-top: 2px; border-top: 3px solid #244b9c;">
			</div>
		</td>
	</tr>
	

</table>

</form>
