<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
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
.AUI_Header_Requisite {
	background-color: #ff00ff;
}

.deletedColor {
	background-color: #ffd7d7;
}

/* 드랍 리스트 왼쪽 정렬 재정의*/
.aui-grid-drop-list-ul {
    text-align:left;
}

</style>
<script type="text/javascript">
<%----------------------------------------------------------
*                      AUI Tree 그리드 
----------------------------------------------------------%>
var myBOMGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;
var isExpanded = false;
var mdoelValueList = [];
var productMethodValueList = [];
var deptcodeValueList = [];
var unitValueList = [];
var manufactureValueList = []; 
var matValueList = []; 
var finishValueList = []; 
//AUIGrid 칼럼 설정
var columnLayout = [ 
	{
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

$(document).ready(function() {
	
	setNumberCodelList('MODEL');
	setNumberCodelList('PRODUCTMETHOD');
	setNumberCodelList('DEPTCODE');
	setNumberCodelList('MANUFACTURE');
	setNumberCodelList('MAT');  
	setNumberCodelList('FINISH');  
	setUnitList();
	
	//Aui 그리드 설정
	var auiGridProps = {
		
		/********트리 그리드************/
			softRemoveRowMode : false,
			
			showStateColumn : true,
			
			showRowCheckColumn : true,
			// singleRow 선택모드
			selectionMode : "multipleCells",
			
			showSelectionBorder : true,
			
			displayTreeOpen : true,
			// 편집 가능 여부
			editable : true,
			
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
			//승인됨 체크
			rowStyleFunction: function(rowIndex, item) {
				if($.trim(item.state) == "승인됨") {
					return "deletedColor";
				}
				return null;
			},
			//fixed 고정
			fixedColumnCount : 5
	};
	
	// 실제로 #grid_wrap 에 그리드 생성
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	// 에디팅 정상 종료 이벤트 바인딩
	//AUIGrid.bind(myBOMGridID, "cellEditEnd", auiCellEditingHandler);
	
	//BOM Search
	viewAUIPartBomAction();
	
	popupAUIResize();
	/*
	AUIGrid.bind(myBOMGridID, "rowCheckClick", function( event ) {
	     
		var item = event.item;
	    var rowIndex = event.rowIndex;
	    varchecked = event.checked;
	    var selectIndexItems = AUIGrid.getSelectedItems(myBOMGridID);
	    
		var rowIdArray = [];
 	    var uniqueRowIdArray = []; 
 	   // uniqueRowIdArray.push(rowIndex);
	    
	    if(selectIndexItems.length > 0){
	    
	 	    $.each(selectIndexItems, function(n, v) {
	 	    	
	 	          rowIdArray.push (v.rowIdValue); // rowIdField 의 값만 배열에 보관.
	 	    });
			
	 	    // rowIdField 의 배열 중 모든 셀에 대하여 rowIdField 값이기 때문에 중복이 발생하고 있음.
	 	    // 따라서 중복 제거 시킴.
	 	    $.each(rowIdArray, function(n, v) {
	 	         if(uniqueRowIdArray.indexOf(v) == -1) uniqueRowIdArray.push(v);
	 	    });
	    }
		
	    if(varchecked){
 	 	// 엑스트라 체크박스 체크 하기
 	 		AUIGrid.setCheckedRowsByIds(myBOMGridID, uniqueRowIdArray);
 	 	}else{
 	 		 AUIGrid.addUncheckedRowsByIds(myBOMGridID, uniqueRowIdArray);
 	 	}
	    
	 	
	   

	});
	*/
	
	//console.log("model = " + modelList.length);
	/*
	AUIGrid.bind(myBOMGridID, "filtering", function( event ) {
		var dataFiled = new Object();
	    var i = 0 ;
		for(var n in event.filterCache) {
			 console.log( "dataField = "+ n  +" ,filter = " + event.filterCache[n] );
	          
	           i++;
	     }
	     var gridItemList = AUIGrid.getGridData(myBOMGridID)
	     console.log("gridItem length= " + gridItemList.length);
	     for(var i = 0 ; i < gridItemList.length ;  i++ ){
	    	 console.log("gridItem number =" +gridItemList[i].number +","+gridItemList[i].model +);
	    	 if()
	     }
	});
	*/
	// 에디팅 시작 이벤트 바인딩
	/*
	AUIGrid.bind(myBOMGridID, "cellEditEndBefore", function(event) { 
        // 셀이 Anna 인 경우 
       console.log("cellEditEndBefore event " + event.item.number)
    });
	// 에디팅 정상 종료 이벤트 바인딩
	AUIGrid.bind(myBOMGridID, "cellEditEnd", function( event ) {
		console.log("cellEditEnd event " + event.item.number)
	})
	
	
	
	/*
	AUIGrid.bind(myBOMGridID, "selectionChange", function( event ) {
	       alert("rowIndex : " + event.rowIndex + ", columnIndex  =" + event.item.number );
	 });
	
	*/
	/*
	AUIGrid.bind(myBOMGridID, "rowAllChkClick", function( event ) {
		var rowList = AUIGrid.getCheckedRowItems(myBOMGridID);
	})
	*/
	/*
	AUIGrid.bind(myBOMGridID, "rowCheckClick", function( event ) {
		// console.log("event.item :" + event.item);
		 var item = event.item
		 
		 childCheckBox(item);
		 
		// console.log("childList" + event.item);
  	   	  for(var i= 0 ; i < childList.length ; i++){
  	   		var childItem = childList[i]
  	   	
  	   		console.log(childItem.number+","+childItem.rowId + childItem.state);
  	   		//if(childItem.state == "승인됨"){
  	   			//AUIGrid.addUncheckedRowsByIds(myBOMGridID,"state","승인됨");
  	   			AUIGrid.addUncheckedRowsByIds(myBOMGridID,childItem.rowId);
  	   		//}
  	    	
  	   	  }
			//console.log("rowIndex : " + event.rowIndex + ",  event.item.rowId  =" + event.item.rowId + " ,"+event.item.number);
	      // var childList = AUIGrid.getDescendantsByRowId(event.item.rowId);
	       //console.log("childList =" + childList)
	       //
	       /*
	       var rowList = AUIGrid.getCheckedRowItemsAll(myBOMGridID);
	       for( var i = 0 ; i < rowList.length ;  i++){
	    	  // console.log(rowList+",rowList[0] = "+rowList[0] +",rowList[1]" + rowList[1]);
	    	   var item = rowList[i];
	    	   console.log(item.number);
	       }
	       */
	       /*
	       var rowList = AUIGrid.getCheckedRowItems(myBOMGridID);
	       for( var i = 0 ; i < rowList.length ;  i++){
	    	   console.log(rowList[i].item.);
	    	  
	    	   
	       }
	       
	});
	*/
	
})

$(function() {
	$("#updatePack").click(function() {
		if(validationCheck()) {
			return;
		}
		var param = new Object();
		$("input[type=hidden]").each(function() {
			if($.trim(this.name) != "") {
				console.log(this.name +"="+this.value)
				param[this.name] = this.value;
			}
		})
		
		var rowList = AUIGrid.getEditedRowItems(myBOMGridID);//AUIGrid.getCheckedRowItems(myBOMGridID);
		console.log("rowList ="+rowList.length);
		param.rowList = rowList;
		
		if (confirm("${f:getMessage('변경하시겠습니까?')}")){
			
			//var form = $("form[name=updatePackagePart]").serialize();
			var url	= getURLString("part", "updateAUIPackagePartAction", "do");
			
			$.ajax({
				type:"POST",
				url: url,
				data : JSON.stringify(param),
				dataType:"json",
				contentType : "application/json; charset=UTF-8",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('변경 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('변경 성공하였습니다.')}");
						location.href = getURLString("part", "updateAUIPackagePart", "do") + "?oid=" + data.oid;
					}else {
						alert("${f:getMessage('변경 실패하였습니다.')}\n" + data.message);
					}
				}
				,beforeSend: function() {
					gfn_StartShowProcessing();
		        }
				,complete: function() {
					gfn_EndShowProcessing();
		        }
			});
		}
	})
	$('#allCheck').click(function() {
		if(this.checked) {
			$("input[name='modifyChecks']").prop("checked", "checked");
		}else {
			$("input[name='modifyChecks']").prop("checked", "");
		}
	})
	$('#filterInit').click(function() {
		AUIGrid.clearFilterAll(myBOMGridID);
		
	})
	
	$('#batchAttribute').click(function() {
		batchAttribute();
		
	})
	
	
	
})

<%----------------------------------------------------------
*                     bom 하위 체크 box 체크 유무 체크
----------------------------------------------------------%>
function childCheckBox(item) {
	
	if(item.state == "승인됨"){
		 AUIGrid.addUncheckedRowsByIds(myBOMGridID,item.rowId);
	 }
	
	 //AUIGrid.setCheckedRowsByValue(myBOMGridID, "state", ["승인됨"]); 
      var childList = item.children;

  	  for(var i= 0 ; i < childList.length ; i++){
  		var childItem = childList[i]
  		childCheckBox(childItem)
  		//console.log(childItem.number+","+childItem.rowId + childItem.state);
  		//if(childItem.state == "승인됨"){
  			//AUIGrid.addUncheckedRowsByIds(myBOMGridID,"state","승인됨");
  			//AUIGrid.addUncheckedRowsByIds(myBOMGridID,childItem.rowId);
  		//}
   	
  	  }
}

<%----------------------------------------------------------
*                     정합성 체크
----------------------------------------------------------%>
function validationCheck() {
	
	var checkedItems = AUIGrid.getEditedRowItems(myBOMGridID)//AUIGrid.getCheckedRowItems(myBOMGridID);
	
	if(checkedItems.length == 0){
		alert("${f:getMessage('수정된 ')}${f:getMessage(' 부품이 없습니다.')}");
		return true;
	}else{
		for(var i = 0; i < checkedItems.length; i++){
			
			var checkedItem = checkedItems[i];
			
			
			var number = checkedItem.number;
			var rowId = checkedItem.rowId;
			var model = checkedItem.model;
			var productmethod = checkedItem.productmethod;
			var deptcode = checkedItem.deptcode;
			var unit = checkedItem.unit;
			
			if(model == ""){
				alert("["+rowId+"]"+number+"의 ${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
				return true;
			}
			
			if(productmethod == ""){
				alert("["+rowId+"]"+number+"의 ${f:getMessage('제작방법')}${f:getMessage('을(를) 선택하세요.')}");
				return true;
			}
			
			if(deptcode == ""){
				alert("["+rowId+"]"+number+"의 ${f:getMessage('부서')}${f:getMessage('을(를) 선택하세요.')}");
				return true;
			}
			
			if(unit == ""){
				alert("["+rowId+"]"+number+"의 unit ${f:getMessage('을(를) 선택하세요.')}");
				return true;
			}
		}
	}
	
	
}

<%----------------------------------------------------------
*                      Bom 리스트 검색
----------------------------------------------------------%>
function viewAUIPartBomAction(){
	isExpanded = false;
	var form = $("form[name=updatePackagePart]").serialize();
	var url	= getURLString("part", "updateAUIPackageSearchAction", "do");
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
			
			var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
			setDepthList(totalDepth)
			
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
<%----------------------------------------------------------
*                      단위 리스트
----------------------------------------------------------%>
function setUnitList(){
	var data = partUnitList();
	for(var i=0; i<data.length; i++) {
		var dataMap = new Object();
		
		dataMap["code"] = data2[i]
		dataMap["value"] = data2[i];
		
		unitValueList[i] = dataMap;
	}
	
}


<%----------------------------------------------------------
*                      Bom Type에 따른 bom 검색
----------------------------------------------------------%>
function batchAttribute(){
	var str = getURLString("part", "batchAtttribute", "do");
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "batchAttribute", opts+rest);
    newwin.focus();
}

function apply(dataMap){
	//alert(dataMap)
	
	//alert(dataMap["model"]);
	var model = dataMap["model"];
	var productmethod = dataMap["productmethod"];
	var deptcode = dataMap["deptcode"];
	var unit = dataMap["unit"];
	var manufacture = dataMap["manufacture"];
	var mat = dataMap["mat"];
	var finish = dataMap["finish"];
	var remark = dataMap["remark"];
	var weight = dataMap["weight"];
	var specification = dataMap["specification"];
	
	console.log("change  ========== "+model+","+productmethod+","+deptcode+","+unit+","+manufacture+","+mat +","+remark);
	
	var checkedItems = AUIGrid.getCheckedRowItems(myBOMGridID);//AUIGrid.getCheckedRowItemsAll(myBOMGridID);//
	
	for(var i = 0; i < checkedItems.length; i++){
		var checkedItem = checkedItems[i].item;
		
		var number = checkedItem.number
		var rowId = checkedItem.rowId
		//console.log(rowId+",ORG number =" + number +",model =" + checkedItem.model+",productmethod ="+checkedItem.productmethod);
		var cmodel = "";
		var cproductmethod = "";
		var changedeptcode = "";
		var changeunit = "";
		var changemat = "";
		var changemanufacture = "";
		var changefinish = "";
		var changeremark = "";
		var changeweight = "";
		var changespecification = "";
		
		
		if(model.length == ""){
			
			cmodel = checkedItem.model;
		}else{
			cmodel = model
		}
		if(productmethod =="" ){
			
			cproductmethod = checkedItem.productmethod;
		}else{
			cproductmethod = productmethod;
		}
		
		if(deptcode ==""){
			changedeptcode = checkedItem.deptcode;
		}else{
			changedeptcode = deptcode;
		}
		
		if(unit ==""){
			changeunit = checkedItem.unit;
		}else{
			changeunit = unit;
		}
		
		if(mat ==""){
			changemat = checkedItem.mat;
		}else{
			changemat = mat;
		}
		
		if(manufacture ==""){
			changemanufacture = checkedItem.manufacture;
		}else{
			changemanufacture = manufacture;
		}
		
		if(finish ==""){
			changefinish = checkedItem.finish;
		}else{
			changefinish = finish;
		}
		
		if(remark ==""){
			changeremark = checkedItem.remark;
		}else{
			changeremark = remark;
		}
		
		if(weight ==""){
			changeweight = checkedItem.weight;
		}else{
			changeweight = weight;
		}
		
		if(specification ==""){
			changespecification = checkedItem.specification;
		}else{
			changespecification = specification;
		}
		
		console.log("update  ==========rowId = "+rowId+","+cmodel+","+cproductmethod+","+changedeptcode+","+changeunit+","+changemanufacture+","+changemat);
		
	   // var updateItem = {id : checkedItem.id ,  remarks : model}
		var index = AUIGrid.rowIdToIndex(myBOMGridID, checkedItem.rowId);	
		var checkedItem = {
				//rowId : rowId,
				model : cmodel,
				productmethod : cproductmethod,
				deptcode : changedeptcode,
				unit : changeunit,
				manufacture : changemanufacture,
				mat : changemat,
				finish : changefinish,
				remark : changeremark,
				weight : changeweight,
				specification : changespecification
		}
		
		AUIGrid.updateRow(myBOMGridID, checkedItem, index);
		//AUIGrid.updateRowsById(myBOMGridID,checkedItem)
		
		
	}
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
}
</script>

<body>

<form name=updatePackagePart id=updatePackagePart  method=post  >

<input type="hidden" name="oid" id="oid" value="<c:out value='${oid }'/>">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white>${f:getMessage('품목')}${f:getMessage('일괄')}${f:getMessage('수정')}</font></B></td>
		   		</tr>
			</table>
			
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align=left>
							<tr>
								<td>
									<input type="checkbox" name="checkDummy" id="checkDummy" value="true" checked onchange="viewAUIPartBomAction()" > 더미제외
								</td>
								<td>
									<select id="depthSelect" onchange="showItemsOnDepth()">
									</select>
								</td>
								
								<td>
									<button type="button" class="btnCustom" id="filterInit"  >
										<span></span>
										${f:getMessage('필터 초기화')}
									</button>
								</td>
								
							</tr>
						</table>
		    		</td>
		    		<td>
		    			<table border="0" cellpadding="0" cellspacing="4" align=right>
							<tr>
								<td>
									<button type="button" class="btnCustom" id="batchAttribute"  >
										<span></span>
										${f:getMessage('속성 일괄 적용')}
									</button>
								</td>
								<td>
									<button type="button" class="btnCRUD" id="updatePack">
										<span></span>
										${f:getMessage('저장')}
									</button>
								</td>
								
								<td>
									<button type="button" class="btnClose" onclick="self.close();" >
										<span></span>
										${f:getMessage('닫기')}
									</button>
								</td>
							</tr>
						</table>
		    		</td>
		    	</tr>
		    </table>
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

</body>
</html>