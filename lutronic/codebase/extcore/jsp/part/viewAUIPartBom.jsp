<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/css.jsp"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


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
	String baseline = (String) request.getAttribute("baseline");
	List<Map<String, String>> list = (List<Map<String, String>>) request.getAttribute("list");
	String allBaseline = (String) request.getAttribute("allBaseline");
	String desc = (String) request.getAttribute("desc");
	String view = (String) request.getAttribute("view");

%>
<form name=PartTreeForm id="PartTreeForm" method="post">

<input type="hidden" name="viewName" id="viewName" value="<%= view %>" />

<input type="hidden" name="oid" id="oid" value="<%= oid %>"/>

<input type="hidden" name="oid2" id="oid2"/>

<input type="hidden" name="baseline2" id="baseline2" value="<%= baseline %>"/>

<table width="100%" border="0" cellpadding="0" cellspacing="0" >

	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><%= title %> BOM</B></td>
		   		</tr>
			</table>
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td width="40%" align="left">
						<input type="button" value="펼치기" title="펼치기" id="expand" class="width-80">
						
						<select id="depthSelect" onchange="showItemsOnDepth()" class="AXSelect width-120"></select>
						
						<select name="desc" id="desc" class="AXSelect width-100">
							<option value="true" >정전개</option>
							<option value="false">역전개</option>
			    		</select>
						<input type="checkbox" name="checkDummy" id="checkDummy" value="true"  onchange="viewAUIPartBomAction()" checked> 더미제외
			    	</td>
			    	
		    		<td  align="right">
						<select name="baselineView" id="baselineView"  class="AXSelect width-150">
				    		<option value="" selected="selected" disabled="disabled">-- Baseline 보기 --</option>
				    		<option value="<c:out value='${lastedoid }'/>"  ><c:out value='${number }'/>[BOM]</option>
			    			</option>
				    		<c:forEach items="${list }" var="baseline">
				    			<option value='<c:out value="${baseline.baseOid }" />' title="<c:out value="${baseline.partOid }" />">
				    				<c:out value="${baseline.baseName }" />
				    			</option>
				    		</c:forEach>
			    		</select>
			    		
						<select name="baseline" id="baseline"  class="AXSelect width-150">
				    		<option value="" selected="selected" disabled="disabled">-- Baseline 비교--</option>
			    			<c:forEach items="${list }" var="baseline">
				    			<option value='<c:out value="${baseline.baseOid }" />' title="<c:out value="${baseline.partOid }" />">
				    				<c:out value="${baseline.baseName }" />
				    			</option>
				    		</c:forEach>
			    		</select>
			    		
						<input type="button" value="상위품목" title="상위품목" id="upItem">
						
						<input type="button" value="하위품목" title="하위품목" id="downItem">
						
						<input type="button" value="END ITEM" title="END ITEM" id="endItem">
						
						<input type="button" value="EXCEL" title="EXCEL" id="excelDown">
						
						<input type="button" value="첨부" title="첨부" id="attachDown">
						
						<input type="button" value="도면" title="도면" id="drawingDown">
						
						<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
									
<!-- 									<button type="button" name="" id="" class="btnClose" onclick="self.close()" style="width: 50px"> -->
<!-- 										<span></span> -->
<!-- 										닫기 -->
<!-- 									</button> -->
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
			dataField : "seq",
			headerText : "seq",
			width: "5%",
		}, 
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
		treeColumnIndex : 2,
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
		//fixed 기능 부품 번호
		fixedColumnCount  : 3,
	};
	
	
	
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	
	
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
		
		
		
		popupAUIResize();
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
		
		var url	= getCallUrl("/part/viewAUIPartBomAction");
		AUIGrid.showAjaxLoader(myBOMGridID);
		call(url, params, function(data) {
			var gridData = data;
			// 그리드에 데이터 세팅
			AUIGrid.removeAjaxLoader(myBOMGridID);
			AUIGrid.setGridData(myBOMGridID, gridData);
			//2Level까지만 펼치기
			AUIGrid.showItemsOnDepth(myBOMGridID, 1 );
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
	var str = getURLString("part", "bomPartList", "do") + "?oid="+$("#oid").val()+"&bomType="+bomType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBomList", opts+rest);
    newwin.focus();
}


</script>