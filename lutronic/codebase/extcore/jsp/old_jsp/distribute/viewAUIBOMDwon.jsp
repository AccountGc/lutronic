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

.Backgroun_img {
	
	background-image:url('/Windchill/jsp/portal/images/img_default/background_white.png');
	width: 100px;
	height:100px;

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

//AUIGrid 칼럼 설정
var columnLayout = [ 
     
	{
		dataField : "level",
		headerText : "Level",
		width: "5%",
		
	}, 
    {
		dataField : "number",
		headerText : "부품번호",
		width: "20%",
		filter : {
			showIcon : true
		},
	}, 
	{
		dataField : "rowId",
		headerText : "rowId",
		width: "10%",
	}, 
	
	{
	    dataField: "dwgNo",
	    headerText: "도면번호",
	    style: "AUI_left",
		width: "10%",//dwgOid
		renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openDistributeView('" + item.dwgOid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	}, 
	
	{
		dataField: "name",
		headerText: "부품명",
		style: "AUI_left",
		width : "20%",
		filter : {
			showIcon : true
		},
		renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openDistributeView('" + item.id + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	}, 
	{
		dataField: "rev",
		headerText: "REV",
		width: "5%"
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
		width: "10%"
	},
	{
		dataField: "deptcode",
		headerText: "부서",
		width: "5%",
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
			
			showSelectionBorder : false,
			
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
			
			// 행의 고유 필드명
			rowIdField : "rowId",
			
			// 트리의 고유 필드명
			treeIdField : "id",
			
			// 계층 구조에서 내 부모 행의 treeIdField 참고 필드명
			treeIdRefField : "parent"
	};
	
	// 실제로 #grid_wrap 에 그리드 생성
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	//BOM Search
	viewPartBomAction($("#oid").val(),$("#baseline2").val());
})

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
	*                      상세정보 버튼
	----------------------------------------------------------%>
	$("#detailView").click(function() {
		openView($("#oid").val());
	})
	$("#latestBom").click(function() {
		$("#baseline").val("");
		rowId = 0;
		partTreeGrid.clearAll();
		viewPartBomAction('0', $('#oid').val(), "true", '');
	})
	
	<%----------------------------------------------------------
	*                      Drawing Down 버튼
	----------------------------------------------------------%>
	$("#drawingDown").click(function() {
		$("#PartTreeForm").attr("method", "post");
		$("#PartTreeForm").attr("action", getURLString("drawing", "partTreeDrawingDown", "do")).submit();
	})
	
	var isExpanded = false;
	<%----------------------------------------------------------
	*                     모두 펼치기
	----------------------------------------------------------%>
	$("#expand").click(function() {
		if (!isExpanded) {
			AUIGrid.expandAll(myBOMGridID);
			isExpanded = true;
		} else {
			AUIGrid.collapseAll(myBOMGridID);
			isExpanded = false;
		}
	})
	<%----------------------------------------------------------
	*                     엑셀 다운로드
	----------------------------------------------------------%>
	$("#excelDown").click(function() {
		
		if(confirm("필터는 초기화 됩니다.")){
			if(!isExpanded){
				AUIGrid.expandAll(myBOMGridID);
				isExpanded = true;
			}
			AUIGrid.clearFilterAll(myBOMGridID);
			exportAUIBOMExcel('BOM')
		}
	})
	
	<%----------------------------------------------------------
	*                     부품 첨부 파일 다운로드
	----------------------------------------------------------%>
	$("#attachDown").click(function() {
		attachDown();
	})
	
	
})

function excelAUIDown(){
	exportAUIBOMExcel('BOM')
}
<%----------------------------------------------------------
*                      Bom 리스트 검색
----------------------------------------------------------%>
function viewPartBomAction(oid, baseline){
	var url	= getURLString("distribute", "viewPartBomAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{
			oid : oid,
			//isTopAssy : isTopAssy,
			//parentId : id,
			//desc : $('#desc').val(),
			baseline : baseline
		},
		dataType:"json",
		async: true,
		cache: false,
	
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
			var gridData = data;
			
			// 그리드에 데이터 세팅
			AUIGrid.setGridData(myBOMGridID, gridData);
			
			excelAUIDown();
			//2Level까지만 펼치기
			//AUIGrid.showItemsOnDepth(myBOMGridID, Number("2") );
			
			//var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
			//totalDepth = totalDepth -1;
			//setDepthList(totalDepth)
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
	    }
		,complete: function() {
			gfn_EndShowProcessing();
	    }
	});
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
	for(var i=1; i<=totalDepth; i++) {
		var temp = i;
		$("#depthSelect").append("<option value='" +i + "'> Level " +(temp-1)+ "</option>");
	}
}

<%----------------------------------------------------------
*                     부품 첨부 파일 다운로드
----------------------------------------------------------%>
window.attachDown = function() {
	
	var param = new Object();
	
	
	var checkedItems = AUIGrid.getCheckedRowItems(myBOMGridID);
	console.log("checkedItems.length ="  + checkedItems.length)
	if(checkedItems.length == 0 ) {
		alert("하나 이상  선택해 주세요");
		return;
	}
	
	
	for(var i=0 ; i < checkedItems.length ; i++) {
		var checkedItem = checkedItems[i].item;
		console.log(checkedItem.number+","+checkedItem.id)
	}
	
	
	param.rowItems = checkedItems;
	
	$("input[type=hidden]").each(function() {
		if($.trim(this.name) != "") {
			param[this.name] = this.value;
		}
	})
	
	var url	= getURLString("part", "partTreeSelectAttachDown", "do");
	$.ajax({
		type : "POST",
		url : url,
		data : JSON.stringify(param),
		dataType : "json",
		contentType : "application/json; charset=UTF-8",
		async : true,
		cache : false,

		/*
		error:function(data){
			var msg = "데이터 검색오류";
			alert(msg);
		},
*/
		success:function(data){
			//alert(data.message);
			//search();
		}
		
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	})
	
}

</script>

<form name=PartTreeForm id="PartTreeForm" method="post">

<input type="hidden" name="viewName" id="viewName" value="<c:out value='${view }'/>" />

<input type="hidden" name="oid" id="oid" value="<c:out value='${oid }'/>"/>

<input type="hidden" name="oid2" id="oid2"/>
<input type="hidden" name="baseline2" id="baseline2" value="<c:out value='${baseline }'/>"/>
<div >
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr>
		
		<td>
			<div id="grid_wrap" style="width: 100%; height:500px; margin-top: 2px; border-top: 3px solid #244b9c; background-color:red">
			</div>
		</td>
	</tr>
	

</table>
</div>
</form>
