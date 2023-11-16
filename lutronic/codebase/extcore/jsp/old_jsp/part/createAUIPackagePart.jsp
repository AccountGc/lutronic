<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<style type="text/css">
/* 드랍 리스트 왼쪽 정렬 재정의*/
.aui-grid-drop-list-ul {
    text-align:left;
}

.header-column {
	color:#D9418C;
}
</style>
<script>
<%----------------------------------------------------------
*                      AUI 그리드 
----------------------------------------------------------%>
//AUIGrid 생성 후 반환 ID
var myGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;

var mdoelValueList = [];
var productMethodValueList = [];
var deptcodeValueList = [];
var unitValueList = [];
var manufactureValueList = []; 
var matValueList = []; 
var finishValueList = []; 

var partName1ValueList =[];
var partName2ValueList =[];
var partName3ValueList =[];

var partType1ValueList =[];
var partType2ValueList =[];
var partType3ValueList =[];


var newwin

//AUI 컬러 설정
var columnLayout = [
	{
		dataField: "id",
		headerText: "id",
		editable : false,
		width : "1%"
	
	},
	{
		dataField: "number",
		headerText: "품목번호",
		editable : false,
		width : "5%",
		renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	
	},
	{
		dataField: "oid",
		headerText: "oid",
		editable : false,
		width : "1%"
	
	},
	{
		dataField: "state",
		headerText: "결과상태",
		editable : false,
		width : "1%"
	
	},
	{
		dataField: "msg",
		headerText: "결과",
		editable : false,
		width : "10%"
	
	}, 
   
	{
		dataField : "partType1",
		headerText : "품목구분(*)",
		width: "1%",
		headerStyle : "header-column"
	},
	{
		dataField : "partType1Name",
		headerText : "품목구분(*)",
		editable : false,
		width: "5%",
		headerStyle : "header-column"
	}, 
	{
	
		dataField : "partType2",
		headerText : "대분류(*)",
		width: "1%",
	 },
	 {
			
		dataField : "partType2Name",
		headerText : "대분류(*)",
		editable : false,
		width: "10%",
	 },
	 {
		dataField : "partType3",
		headerText : "중분류(*)",
		width: 140,
	 },
	 {
		dataField : "partType3Name",
		headerText : "중분류(*)",
		editable : false,
		width: "10%",
     },
	 {
	
		dataField: "seq",
		headerText: "SEQ",
		style: "AUI_left editAble_Cell",
		editable : true,
		width : "3%",
		headerTooltip : {
			show : true,
			tooltipHtml : "3자리"
		}
	},{
	
		dataField: "etc",
		style: "AUI_left editAble_Cell",
		editable : true,
		width : "3%",
		headerTooltip : {
			show : true,
			tooltipHtml : "2자리"
		}
	},
	{
		
		headerText: "품목명(*)",
		children : [ 
		    {
				dataField : "partName1",
				headerText : "대제목",
				style: "editAble_Cell",
				editable : true,
				width: "7%",
				
			}, 
			{
				 dataField : "partName2",
			     headerText : '중제목',
			     style: "editAble_Cell",
			     editable : true,
			     width: "7%",
			     
			},
			{
				 dataField : "partName3",
			     headerText : '소제목',
			     style: "editAble_Cell",
			     editable : true,
			     width: "7%",
			     
			} ,
			{
				 dataField : "partName4",
			     headerText : '사용자 Key in',
			     style: "AUI_left editAble_Cell",
			     editable : true,
			     width: "10%"
			},
			{
				 dataField : "displayName",
			     headerText : '합산 품명',
			     editable : true,
			     width: "10%"
			},
		]
	},
	{
		dataField: "model",
		headerText: "프로젝트 코드 (*)",
		editable : false,
		width: "10%"
	},
	{
		dataField: "modelName",
		headerText: "프로젝트 코드 (*)",
		editable : false,
		width: "10%"
	}, 
	{
		dataField: "productmethod",
		headerText: "제작방법 (*)",
		editable : false,
		width: "10%"
	}, 
	{
		dataField: "productmethodName",
		headerText: "제작방법 (*)",
		editable : false,
		width: "10%"
	}, 
	{
		dataField: "deptcode",
		headerText: "부서(*)",
		editable : false,
		width: "5%"
	},
	{
		dataField: "deptcodeName",
		headerText: "부서(*)",
		editable : false,
		width: "5%"
	},
	{
		dataField: "unit",
		headerText: "단위(*)",
		editable : false,
		width: "5%",
	},
	{
		dataField: "manufacture",
		headerText: "MANUFATURER",
		editable : false,
		width: "10%"
	},
	{
		dataField: "manufactureName",
		headerText: "MANUFATURER",
		editable : false,
		width: "10%"
	},
	{
		dataField: "mat",
		headerText: "재질 ",
		editable : false,
		width: "10%"
	},
	{
		dataField: "matName",
		headerText: "재질 ",
		editable : false,
		width: "10%"
	},
	{
		dataField: "finish",
		headerText: "후처리 ",
		editable : false,
		width: "10%"
	},
	{
		dataField: "finishName",
		headerText: "후처리 ",
		editable : false,
		width: "10%"
		
	},
	{
		dataField: "specification",
		headerText: "사양 ",
		style: "AUI_left editAble_Cell",
		width: "10%"
	},
	{
		dataField: "weight",
		headerText: "무게(g)",
		style: "AUI_left editAble_Cell",
		width: "5%",
		dataType : "numeric",
		formatString : "#,##0.####",
		headerTooltip : {
				show : true,
				tooltipHtml : "소수점이 있으면 (둘째짜리까지) 표시, 없으면 미표시"
		}
		
	},
	{
		dataField: "remarks",
		//headerText: "비고 ",
		headerText: "OEM Info.",
		style: "AUI_left editAble_Cell",
		width: "10%"
		
	},
	
	{
		dataField: "primary",
		headerText: "주도면 ",
		style: "AUI_left editAble_Cell",
		width: "10%"
		
	}
<%---------------------------컬럼 END-------------------------------%>
];
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	// AUIGrid 그리드를 생성합니다.
	createAUIGrid(columnLayout);
	
})


$(function() {
	
	$("#addRow").click(function() {
		addRowFunction();
	})
	$("#removeRow").click(function() {
		
		var data = AUIGrid.getGridData(myGridID);
		
		if( data.length > 0) {
			var rowPos = "selectedIndex";
			AUIGrid.removeRow(myGridID, rowPos);
			
		}
		
	})
	
	
	$("#saveBatch").click(function() {
		
		if($.trim($("#fid").val()) == "") {
			alert("${f:getMessage('품목분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(!validation()){
			return;
		}
		
		var formData = new FormData();
		var param = new Object();
		
	
		$("input[type=hidden]").each(function() {
			if($.trim(this.name) != "") {
				formData.append(this.name, this.value);
			}
		})
		
		var rowList = AUIGrid.getGridData(myGridID);
		
		if(rowList != null) {
			formData.append("rowList", JSON.stringify(rowList));
		}
		
		if (confirm("${f:getMessage('등록 하시겠습니까?')}")){
			
			//var form = $("form[name=updatePackagePart]").serialize();
			var url	= getURLString("part", "createAUIPackagePartAction", "do");
			
			$.ajax({
				type : "POST",
				url : url,
				data : formData,
				dataType: "json",
				contentType : "application/json; charset=UTF-8",
				cache: false,
				processData: false,
				contentType: false,
				error:function(data){
					var msg = "${f:getMessage('변경 오류')}";
					alert(msg);
				},

				success:function(data){
					console.log("return data = " +data.result);
					
					var returnData = data.returnList;
					var result = data.result;
					console.log("result ="+result+",returnData = "+ returnData);
					for(var i = 0 ;  i < returnData.length ; i++){
						
						var number = returnData[i].number;
						var oid = returnData[i].oid
						var id = returnData[i].id;
						var msg = returnData[i].msg;
						var state = returnData[i].state;
						console.log(id+",state ="+state);
						if(!result){
							var number = "";
							var oid = "";
							if(state == "S"){
								msg ="";
								state ="";
							}
						}
						var rowItems = AUIGrid.getItemsByValue(myGridID, "id", id);
						var index = AUIGrid.rowIdToIndex(myGridID, id);	
						var rowItems = {
								number : number,
								oid : oid,
								msg : msg,
								state : state
						}
						
						AUIGrid.updateRow(myGridID, rowItems, index);
						
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
	<%----------------------------------------------------------
	*                      추가 버튼 클릭
	----------------------------------------------------------%>
	$("#batchCreate").click(function() {
		batchCreate();
	})
	
	<%----------------------------------------------------------
	*                      old Excel 등록 이동
	----------------------------------------------------------%>
	$("#batchExcelMove").click(function() {
		var url = getURLString("part", "createPackagePart", "do");
		document.location = url;
	})
	
})



// AUIGrid 를 생성합니다.
function createAUIGrid(columnLayout) {
	
	var auiGridProps = {
			rowIdField : "id",
			rowIdValue : "id",
			showStateColumn : true,
			selectionMode : "multipleCells",
			showRowNumColumn : true,
			softRemoveRowMode : false,
			processValidData : true,
			// 편집 가능 여부
			editable : true,
			enableRestore : true,
			//엔터키가 다음 행으로 이동하지 않고, 다음 칼럼으로 이동할지 여부를 지정합니다.
			enterKeyColumnBase : false,
			headerHeight : headerHeight,
			rowHeight : rowHeight,
			height : 400,
			enableFilter : true,
			//showRowCheckColumn : true,
			editableOnFixedCell : true,
			fixedColumnCount : 13,
			rowStyleFunction: function(rowIndex, item) {
				if($.trim(item.state) == "F") {
					return "deletedColor";
				}
				return null;
			},
		};
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	AUIGrid.hideColumnByDataField(myGridID, "id");
	AUIGrid.hideColumnByDataField(myGridID, "partType1");
	AUIGrid.hideColumnByDataField(myGridID, "partType2");
	AUIGrid.hideColumnByDataField(myGridID, "partType3");
	AUIGrid.hideColumnByDataField(myGridID, "oid");
	AUIGrid.hideColumnByDataField(myGridID, "state");
	AUIGrid.hideColumnByDataField(myGridID, "displayName");
	
	AUIGrid.hideColumnByDataField(myGridID, "model");
	AUIGrid.hideColumnByDataField(myGridID, "productmethod");
	AUIGrid.hideColumnByDataField(myGridID, "deptcode");
	AUIGrid.hideColumnByDataField(myGridID, "manufacture");
	AUIGrid.hideColumnByDataField(myGridID, "mat");
	AUIGrid.hideColumnByDataField(myGridID, "finish");
	
	// 에디팅 시작 이벤트 바인딩
	AUIGrid.bind(myGridID, "cellDoubleClick", function(event) {
		
		currentRowIndex = event.rowIndex; // // 에디팅 시작 시 해당 행 인덱스 보관
		modifyRowOpen(event.item);
		
		return false; // false 반환하면 그리드 내장 에디터 표시 안함.(더 이상 진행 안함)
	});
	
}


<%----------------------------------------------------------
*                     정합성 체크
----------------------------------------------------------%>
function validation(){

	var data = AUIGrid.getGridData(myGridID);
	
	if(data.length ==0 ){
		alert("등록 대상을 추가해 주세요");
		return;
	}
	
	for(var i = 0 ;  i < data.length ;  i++){
		
		var number = data[i].number;
		
		if(number !=""){
			alert("이미 등록된 품목입니다.등록된 리스트를 삭제후 추가 해 주세요.")
			return;
		}
		
		
		console.log("model = " + data[i].model);
		var index = AUIGrid.rowIdToIndex(myGridID, data[i].id);
		index = index +1;
		var partType1 = data[i].partType1;
		var partType2 = data[i].partType2;
		var partType3 = data[i].partType3;
		var seq = data[i].seq;
		var etc = data[i].etc;
		var partName1 = data[i].partName1;
		var partName2 = data[i].partName2;
		var partName3 = data[i].partName3;
		var partName4 = data[i].partName4;
		var partName = "";
		partName = partNameCheck(partName,partName1);
		partName = partNameCheck(partName,partName2);
		partName = partNameCheck(partName,partName3);
		partName = partNameCheck(partName,partName4);
		
		var displayName = data[i].displayName;
		var model = data[i].model;
		var productmethod = data[i].productmethod;
		var deptcode = data[i].deptcode;
		var unit = data[i].unit;
		if(partType1 == "" ) {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('품목 구분')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(partType2 == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('대분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(partType3 == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('중분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(seq !="" && seq.length>3){
			alert(index+"${f:getMessage('번째 라인의 ')}seq${f:getMessage('는 3자리까지만 가능합니다.')}");
			return;
		}
		
		if(etc !="" && etc.length>3){
			alert(index+"${f:getMessage('번째 라인의 ')}etc${f:getMessage('는 2자리까지만 가능합니다.')}");
			return;
		}
		
		if($.trim(partName1) == ""
		   && $.trim(partName2) == ""
		   && $.trim(partName3) == ""
		   && $.trim(partName4) == "" ) {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('품목명')}${f:getMessage('을(를) 입력하세요.')}");
			return;
		}else if(partName.length > 40) {
			
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('품목명')}${f:getMessage('은(는) 40자 이내로 입력하세요.')}");
			return;
		}
		/*
		if($("input[name=seqType]:checked").val() == "1") {
			if($.trim($("#seq").val()) == "") {
				alert("SEQ${f:getMessage('을(를) 입력하세요.')}");
				$("#seq").focus();
				return;
			}
			if($.trim($("#etc").val()) == "") {
				alert("${f:getMessage('기타')}${f:getMessage('을(를) 입력하세요.')}");
				$("#etc").focus();
				return;
			}
		}
		*/
		
		if(model == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(productmethod == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('제작방법')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(deptcode == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('부서')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(unit == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('단위')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
	}
	
	
	return true;
}

function partNameCheck(partName,tempName){
	
	if(tempName.length ==0){
		return partName;
	}
	
	if(partName.length > 0) {
		partName = partName + "_";
	}
	partName = partName+tempName;
	
	return partName;
}
<%----------------------------------------------------------
*                     개별 Row 수정 Page
----------------------------------------------------------%>
function modifyRowOpen(gridItem) {
	
	var str = getURLString("part", "batchCreate", "do")+"?auiId="+gridItem.id+"&mode=single";
	
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=800,height=600,left=" + leftpos + ',top=' + toppos;
    var modifywin = window.open( str , "batchModify", opts+rest);
}

function resizeGrid() {
	var data = AUIGrid.getGridData(myGridID);
	$("#grid_td").css("height", defaultGridHeight + (data.length * rowHeight));
	AUIGrid.resize(myGridID, null, defaultGridHeight + (data.length * rowHeight));
}
<%----------------------------------------------------------
*                      일괄 등록 Row 추가 팝업
----------------------------------------------------------%>
function batchCreate(){
	
	var str = getURLString("part", "batchCreate", "do");
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=800,height=600,left=" + leftpos + ',top=' + toppos;
    newwin = window.open( str , "batchCreate", opts+rest);
    
    
}
<%----------------------------------------------------------
*                      NumberCode Setup
----------------------------------------------------------%>
function setNumberCodelList(type){ //numberCodeList
	
	var data = common_numberCodeList(type,'', false);
	
	var data2 =eval(data.responseText)
	console.log(type +" =" + data2.length);
	for(var i=0; i<data2.length; i++) {
		
		var dataMap = new Object();
		
		if(type =="PARTNAME1" || type =="PARTNAME2" || type =="PARTNAME3"){
			dataMap["code"] = data2[i].name;
			dataMap["value"] = data2[i].name;
		}else if(type == "PARTTYPE"){
			dataMap["code"] = data2[i].oid;
			dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		
		}else{
			dataMap["code"] = data2[i].code;
			dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		}
		
		
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
		}else if(type =="PARTNAME1"){
			partName1ValueList[i] = dataMap;
		}else if(type =="PARTNAME2"){
			partName2ValueList[i] = dataMap;
		}else if(type =="PARTNAME3"){
			partName3ValueList[i] = dataMap;
		}else if(type == "PARTTYPE"){
			console.log(type +"=" +data2[i].name )
			partType1ValueList[i] = dataMap;
		}
		
	}
	
}
<%----------------------------------------------------------
*                      부품 분류
----------------------------------------------------------%>
function partTypeValueList(type,parentCode,level){
	
	//console.log(type + ","+parentCode);
	var data = common_numberCodeList(type,parentCode, false);
	
	var data2 =eval(data.responseText)
	//console.log("data2 : " + data2.length);
	partType2ValueList =[];
	partType3ValueList =[];
	for(var i=0; i<data2.length; i++) {
		//console.log(type +" =" + parentCode +","+level);
		var dataMap = new Object();
		dataMap["code"] = data2[i].oid;
		dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		if(level ==2){
			partType2ValueList[i] = dataMap;
		}else{
			partType3ValueList[i] = dataMap;
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
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	console.log(" AUIPackage redrawGrid width =" + width);
	AUIGrid.resize(myGridID);
}
<%----------------------------------------------------------
*                      일괄 추가
----------------------------------------------------------%>
function addRow(dataMap){
	var addCount = dataMap["addCount"];
	var partType1 = dataMap["partType1"];
	var partType1Name = dataMap["partType1Name"];
	var partType2 = dataMap["partType2"];
	var partType2Name = dataMap["partType2Name"];
	var partType3 = dataMap["partType3"];
	var partType3Name = dataMap["partType3Name"];
	
	var partName1 = dataMap["partName1"];
	var partName2 = dataMap["partName2"];
	var partName3 = dataMap["partName3"];
	var partName4 = dataMap["partName4"];
	var displayName = dataMap["displayName"];
	
	var seq = dataMap["seq"];
	var etc = dataMap["etc"];
	
	var model = dataMap["model"];
	var modelName = dataMap["modelName"];
	var productmethod = dataMap["productmethod"];
	var productmethodName = dataMap["productmethodName"];
	var deptcode = dataMap["deptcode"];
	var deptcodeName = dataMap["deptcodeName"];
	var unit = dataMap["unit"];
	var manufacture = dataMap["manufacture"];
	var manufactureName = dataMap["manufactureName"];
	var mat = dataMap["mat"];
	var matName = dataMap["matName"];
	var finish = dataMap["finish"];
	var finishName = dataMap["finishName"];
	var remarks = dataMap["remarks"];
	var weight = dataMap["weight"];
	var specification = dataMap["specification"];
	
	
	var rowList = [];
	
	
	for(var i=0; i<addCount; i++) {
			
		rowList[i] = {
				number    : "",
				state	  : "",
				msg		  : "",
				partType1 : partType1,
				partType1Name : partType1Name,
				partType2 : partType2,
				partType2Name : partType2Name,
				partType3 : partType3,
				partType3Name : partType3Name,
				seq : seq,
				etc : etc,
				partName1 : partName1,
				partName2 : partName2,
				partName3 : partName3,
				partName4 : partName4,
				displayName : displayName,
				model : model,
				modelName : modelName,
				productmethod : productmethod,
				productmethodName : productmethodName,
				deptcode : deptcode,
				deptcodeName : deptcodeName,
				unit : unit,
				manufacture : manufacture,
				manufactureName : manufactureName,
				mat : mat,
				matName : matName,
				finish : finish,
				finishName : finishName,
				remarks : remarks,
				weight : weight,
				specification : specification,
				primary : ""
		}
	}
	
	AUIGrid.addRow(myGridID, rowList, "last");
	alert(addCount+"개 추가 되었습니다.");
	newwin.focus();
	
}

<%----------------------------------------------------------
*                      개별 수정
----------------------------------------------------------%>
function modifyRow(dataMap){
	var id = dataMap["auiId"];
	var partType1 = dataMap["partType1"];
	var partType1Name = dataMap["partType1Name"];
	var partType2 = dataMap["partType2"];
	var partType2Name = dataMap["partType2Name"];
	var partType3 = dataMap["partType3"];
	var partType3Name = dataMap["partType3Name"];
	
	var partName1 = dataMap["partName1"];
	var partName2 = dataMap["partName2"];
	var partName3 = dataMap["partName3"];
	var partName4 = dataMap["partName4"];
	var displayName = dataMap["displayName"];
	
	var seq = dataMap["seq"];
	var etc = dataMap["etc"];
	
	var model = dataMap["model"];
	var modelName = dataMap["modelName"];
	var productmethod = dataMap["productmethod"];
	var productmethodName = dataMap["productmethodName"];
	var deptcode = dataMap["deptcode"];
	var deptcodeName = dataMap["deptcodeName"];
	var unit = dataMap["unit"];
	var manufacture = dataMap["manufacture"];
	var manufactureName = dataMap["manufactureName"];
	var mat = dataMap["mat"];
	var matName = dataMap["matName"];
	var finish = dataMap["finish"];
	var finishName = dataMap["finishName"];
	var remarks = dataMap["remarks"];
	var weight = dataMap["weight"];
	var specification = dataMap["specification"];
	
	
	var rowItems = AUIGrid.getItemsByValue(myGridID, "id", id);
	var index = AUIGrid.rowIdToIndex(myGridID, id);	
	var rowItems = {
			partType1 : partType1,
			partType1Name : partType1Name,
			partType2 : partType2,
			partType2Name : partType2Name,
			partType3 : partType3,
			partType3Name : partType3Name,
			seq : seq,
			etc : etc,
			partName1 : partName1,
			partName2 : partName2,
			partName3 : partName3,
			partName4 : partName4,
			displayName : displayName,
			model : model,
			modelName : modelName,
			productmethod : productmethod,
			productmethodName : productmethodName,
			deptcode : deptcode,
			deptcodeName : deptcodeName,
			unit : unit,
			manufacture : manufacture,
			manufactureName : manufactureName,
			mat : mat,
			matName : matName,
			finish : finish,
			finishName : finishName,
			remarks : remarks,
			weight : weight,
			specification : specification
			
	}
	
	AUIGrid.updateRow(myGridID, rowItems, index);
	
	
}
<%----------------------------------------------------------
*                      AUI 그리드 id로 item 얻기
----------------------------------------------------------%>
function getIDAttribute(id){
	var rowItems = AUIGrid.getItemsByValue(myGridID, "id", id);
	return rowItems;
	
}
<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	AUIGrid.resize(myGridID);
}

window.closeProcessing = function() {
	gfn_EndShowProcessing();
}
</script>

<body>

<form name="createPackagePart" id="createPackagePart"  enctype="multipart/form-data">
<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<input type="hidden" name="fid"				id="fid" 			value="">
<input type="hidden" name="auiId" id="auiId" >

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							${f:getMessage('제품')}/${f:getMessage('품목')}
							${f:getMessage('관리')}
							>
							${f:getMessage('제품')}/${f:getMessage('품목')}
							${f:getMessage('일괄등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>
<table border="0" cellpadding="0" cellspacing="4" align=right>
	<tr>
		<td>
			<button type="button" class="btnCRUD" id="saveBatch">
              	<span></span>
              	등록
            </button>
		</td>
		
		<td>
			<button type="button" class="btnCustom" id="batchExcelMove">
              	<span></span>
              	엑셀 등록 이동
            </button>
		</td>
	</tr>
</table>
							

<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr  align=center>
        <td valign="top" style="padding:0px 0px 0px 0px">
        	
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
                <tr><td height=1 width=100%></td></tr>
            </table>
            
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
            <col width='15%'><col width='35%'><col width='15%'><col width='35%'>
            
	            <tr>
			        <td class="tdblueM">
			        	${f:getMessage('품목분류')}
			        </td>
			        
			        <td class="tdwhiteL">
			        	<b>
			        		<span id="locationName">
			        			/Default/PART_Drawing
			        		</span>
			        	</b>
			        </td>
			    </tr>
                <tr bgcolor="ffffff">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createPackagePart"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="btnId" value="saveBatch" />
						</jsp:include>
					</td>
				</tr>
				
       		</table>
        </td>
    </tr>
</table>
<table style="width:100%; margin-top: 5px" cellspacing="0">
	<tr>
		<td style="text-align: right;">
			
			<button type="button" class="btnCustom" id="batchCreate"  >
				<span></span>
				${f:getMessage('추가')}
			</button>
			<button type="button" class="btnCustom" id="removeRow"  >
				<span>${f:getMessage('삭제')}</span>
			</button>
		</td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
		<td>
			<div id="grid_wrap" style="width: 100%; height:400px; margin-top: 10px; border-top: 3px solid #244b9c;">
			</div>
		</td>
  	</tr>
</table>	



</form>

</body>
</html>