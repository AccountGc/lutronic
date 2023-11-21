<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>

<form name=updatePackagePart>
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
		<tr height="5">
			<td align=center colspan="2">
				<B><font>품목 일괄 수정</font></B>
			</td>
    	<tr height="30">
    		<td align=left>
				<input type="checkbox" name="checkDummy" id="checkDummy" value="true" checked onchange="viewAUIPartBomAction()" > 더미제외
				<select id="depthSelect" onchange="showItemsOnDepth()">
				</select>
				<input type="button" value="필터 초기화" title="필터 초기화">
			</td>
    		<td align=right>
				<input type="button" value="속성 일괄 적용" title="속성 일괄 적용">
				<input type="button" value="저장" title="저장">
				<input type="button" value="닫기" title="닫기"  onclick="self.close();" >
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</form>
<script type="text/javascript">
	<%----------------------------------------------------------
	*                      AUI Tree 그리드 
	----------------------------------------------------------%>
	var  myGridID;
	
	// var isExpanded = false;
	var mdoelValueList = [];
	var productMethodValueList = [];
	var deptcodeValueList = [];
	var unitValueList = [];
	var manufactureValueList = []; 
	var matValueList = []; 
	var finishValueList = []; 
	
	var  layout= [{
				dataField: "rowId",
				headerText: "rowId",
				editable : false,
				width : "3%"
			},
			{
				dataField : "level",
				headerText : "Level",
				width: "5%",
				filter : {
					showIcon : true
				},
				
			}, 
		    {
				dataField : "number",
				headerText : "부품번호",
				width: "10%",
				editable : false,
				filter : {
					showIcon : true
				},
			}, 
			{
				dataField: "name",
				headerText: "부품명",
				style: "AUI_left",
				editable : false,
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
				dataField: "state",
				headerText: "상태",
				editable : false,
				width : "5%",
				filter : {
					showIcon : true
				},
			}, 
			{
				dataField: "model",
				headerText: "프로젝트 코드 (*)",
				headerStyle : "AUI_Header_Requisite",
				style: "AUI_left",
				width: "10%",
				filter : {
					showIcon : true
				},
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : mdoelValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=mdoelValueList.length; i<len; i++) {
		        		
		        		if(value == mdoelValueList[i].code){
		        			reStr = mdoelValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
		        
			}, 
			{
				dataField: "productmethod",
				headerText: "제작방법 (*)",
				style: "AUI_left",
				width: "10%",
				filter : {
					showIcon : true
				},
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : productMethodValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=productMethodValueList.length; i<len; i++) {
		        		
		        		if(value == productMethodValueList[i].code){
		        			reStr = productMethodValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
			}, 
			{
				dataField: "deptcode",
				headerText: "부서(*)",
				style: "AUI_left",
				width: "5%",
				filter : {
					showIcon : true
				},
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : deptcodeValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=deptcodeValueList.length; i<len; i++) {
		        		if(value == deptcodeValueList[i].code){
		        			reStr = deptcodeValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
				
			},
			{
				dataField: "unit",
				headerText: "단위(*)",
				style: "AUI_left",
				width: "5%",
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : unitValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=unitValueList.length; i<len; i++) {
		        		if(value == unitValueList[i].code){
		        			reStr = unitValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
				
			},
			{
				dataField: "manufacture",
				headerText: "MANUFATURER",
				style: "AUI_left",
				filter : {
					showIcon : true
				},
				width: "10%",
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : manufactureValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=manufactureValueList.length; i<len; i++) {
		        		if(value == manufactureValueList[i].code){
		        			reStr = manufactureValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
				
				
			},
			{
				dataField: "mat",
				headerText: "재질 ",
				style: "AUI_left",
				filter : {
					showIcon : true
				},
				width: "5%",
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : matValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=matValueList.length; i<len; i++) {
		        		if(value == matValueList[i].code){
		        			reStr = matValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
				
				
			},
			{
				dataField: "finish",
				headerText: "후처리 ",
				style: "AUI_left",
				filter : {
					showIcon : true
				},
				width: "10%",
				renderer : {
		        	type : "DropDownListRenderer",
		        	list : finishValueList,
		        	keyField : "code",
		        	valueField : "value"
		        },
		        
		        labelFunction : function( rowIndex, columnIndex, value, item) {
		        	var reStr = "";
		        	
		        	for(var i=0,len=finishValueList.length; i<len; i++) {
		        		if(value == finishValueList[i].code){
		        			reStr = finishValueList[i].value
		        			break;
		        		}
		        	}
		        	return reStr;
		        },
				
				
			},
			{
				dataField: "remark",
				headerText: "OEM Info.",
				width: "15%",
				filter : {
					showIcon : true
				},
				
				
			},
			{
				dataField: "weight",
				headerText: "무게(g)",
				width: "8%",
				filter : {
					showIcon : true
				},
				
				
			},
			{
				dataField: "specification",
				headerText: "사양 ",
				width: "15%",
				filter : {
					showIcon : true
				},
				
				
			},
			
		];

function createAUIGrid() {
	
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		fillColumnSizeMode: false,
		showAutoNoDataMessage : false,
		selectionMode : "multipleCells",
		enableMovingColumn : true,
		enableFilter : true,
		showInlineFilter : false,
		useContextMenu : true,
		enableRightDownFocus : true,
		filterLayerWidth : 320,
		filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
	};
	myGridID = AUIGrid.create("#grid_wrap", layout, props);
	loadGridData();
	AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
	AUIGrid.bind(myGridID, "vScrollChange", function(event) {
		hideContextMenu();
		vScrollChangeHandler(event);
	});
	AUIGrid.bind(myGridID, "hScrollChange", function(event) {
		hideContextMenu();
	});
}

function loadGridData() {
	debugger;
	const params = new Object();
	$("input[type=hidden]").each(function() {
		if($.trim(this.name) != "") {
			params[this.name] = this.value;
		}
	});
	var rowList = AUIGrid.getEditedRowItems(myGridID);
	params.rowList = rowList;
	const url = getCallUrl("/part/updateAUIPackagePartAction");
	const oid = document.querySelector("#oid").value;
	params.oid = oid;
	AUIGrid.showAjaxLoader(myGridID);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
	});
}
	
	$(document).ready(function() {
		createAUIGrid();
		AUIGrid.resize(myGridID);
	});

<%----------------------------------------------------------
*                      NumberCode Setup
----------------------------------------------------------%>
function setNumberCodelList(type){ //numberCodeList
	
	var data = common_numberCodeList(type,'', false);
	
	var data2 =eval(data.responseText)
	//console.log(type +" =" + data2.length);
	for(var i=0; i<data2.length; i++) {
		
		var dataMap = new Object();
		
		dataMap["code"] = data2[i].code;
		dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		
		var code = data2[i].code;
		var value = data2[i].name;
		//console.log(code +"="+ value);
		if(type =="MODEL"){
			mdoelValueList[i] = dataMap;
		}else if(type =="PRODUCTMETHOD"){
			productMethodValueList[i] = dataMap;
		}else if(type =="DEPTCODE"){
			deptcodeValueList[i] = dataMap;
		}else if(type =="MANUFACTURE"){
			manufactureValueList[i] = dataMap;
		}else if(type =="MAT"){
			matValueList[i] = dataMap;
		}else if(type =="FINISH"){
			finishValueList[i] = dataMap;
		}
		
	}
	
}
</script>

