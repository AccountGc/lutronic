<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

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
var newwin;  //팝업창
//AUI 컬러 설정
var fileList = [];

var columnLayout = [
	{
		dataField: "id",
		headerText: "id",
		editable : false,
		width : "1%"
	
	},
	{
		dataField: "number",
		headerText: "문서번호",
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
		dataField: "documentName",
		headerText: "문서종류(*)",
		editable : false,
		width : "10%"
	
	},
	{
		dataField: "docName",
		headerText: "문서명",
		editable : true,
		style: "AUI_left editAble_Cell",
		width : "10%"
	},
	{
		dataField: "lifecycle",
		headerText: "결재방식",
		editable : false,
		width : "1%"
	},
	{
		dataField: "lifecycleName",
		headerText: "결재방식(*)",
		editable : false,
		width : "5%"
	},
	{
		dataField: "documentType",
		headerText: "문서유형",
		editable : false,
		width : "1%"
	},
	{
		dataField: "documentTypeName",
		headerText: "문서유형(*)",
		editable : false,
		width : "10%"
	},
	{
		dataField: "model",
		headerText: "프로젝트 코드 ",
		editable : false,
		width: "1%"
	},
	{
		dataField: "description",
		headerText: "설명 ",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%"
	},
	{
		dataField: "modelName",
		headerText: "프로젝트 코드 ",
		editable : false,
		width: "10%"
	},
	{
		dataField: "writer",
		headerText: "작성자",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "5%"
	},
	{
		dataField: "interalnumber",
		headerText: "내부문서번호",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "7%"
	},
	{
		dataField: "deptcode",
		headerText: "부서",
		editable : false,
		width: "1%"
	},
	{
		dataField: "deptcodeName",
		headerText: "부서",
		editable : false,
		width: "5%"
	},
	{
		dataField: "preseration",
		headerText: "보존기간",
		editable : false,
		width: "1%"
	},
	{
		dataField: "preserationName",
		headerText: "보존기간(*)",
		editable : false,
		width: "7%"
	},
	{
		dataField: "location",
		headerText: "분류체계(*)",
		editable : false,
		width: "15%"
	},
	{
		dataField: "folder",
		headerText: "분류체계 oid",
		editable : false,
		width: "1%"
	},
	{
		dataField: "fileName",
		headerText: "파일명(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
				/* var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.fileName.length);
				if(filesobj.length>0 && item.fileName.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
		///fileList
	},
	{
		dataField: "attachmentFileName1",
		headerText: "부첨부파일명1(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
			/* 	var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.attachmentFileName1.length);
				if(filesobj.length>0 && item.attachmentFileName1.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
	},
	{
		dataField: "attachmentFileName2",
		headerText: "부첨부파일명2(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
			/* 	var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.attachmentFileName2.length);
				if(filesobj.length>0 && item.attachmentFileName2.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
	},
	{
		dataField: "attachmentFileName3",
		headerText: "부첨부파일명3(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
				/* var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.attachmentFileName3.length);
				if(filesobj.length>0 && item.attachmentFileName3.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
	},
	{
		dataField: "attachmentFileName4",
		headerText: "부첨부파일명4(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
				/* var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.attachmentFileName4.length);
				if(filesobj.length>0 && item.attachmentFileName4.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
	},
	{
		dataField: "attachmentFileName5",
		headerText: "부첨부파일명5(*)",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%",
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				//console.log(item)
			/* 	var filesobj = $(".AXUploadTit");
				console.log(filesobj.length);
				console.log(item.attachmentFileName5.length);
				if(filesobj.length>0 && item.attachmentFileName5.length!=0) {
					fileList =[];
					for(var dx =0;dx<filesobj.length;dx++){
						var file = filesobj.get(dx).innerText;
						fileList.push(file);
					}
					console.log(fileList);
				}  */
				return fileList;
			}
		}
	},
	{
		dataField: "partNumber",
		headerText: "관련부품",
		editable : true,
		style: "AUI_left editAble_Cell",
		width: "10%"
	},
]
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	// AUIGrid 그리드를 생성합니다.
	createAUIGrid(columnLayout);
})

$(function() {
	
	
	$("#uploadPackageDocument").click(function() {
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
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			var url	= getURLString("doc", "createAUIPackageDocumentAction", "do");
			
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
			/*
			gfn_StartShowProcessing();
			$("#createPackageDocument").attr("target", "list");
			$("#createPackageDocument").attr("action", getURLString("doc", "createAUIPackageDocumentAction", "do")).submit();
			*/
		}
	})
	
	<%----------------------------------------------------------
	*                     row 삭제
	----------------------------------------------------------%>
	$("#removeRow").click(function() {
		var data = AUIGrid.getGridData(myGridID);
		if( data.length > 0) {
			var rowPos = "selectedIndex";
			AUIGrid.removeRow(myGridID, rowPos);
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
		var url = getURLString("doc", "createPackageDocument", "do");
		document.location = url;
	})
})
function fileListSet(){
	var filesobj = $(".AXUploadTit");
	if(filesobj.length>0 ) {
		fileList =[];
		for(var dx =0;dx<filesobj.length;dx++){
			var file = filesobj.get(dx).innerText;
			fileList.push(file);
		}
		console.log(fileList);
	} 
}
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
			enterKeyColumnBase : false,
			headerHeight : headerHeight,
			rowHeight : rowHeight,
			height : 400,
			enableFilter : true,
			showRowCheckColumn : true,
			editableOnFixedCell : true,
			fixedColumnCount : 7,
			rowStyleFunction: function(rowIndex, item) {
				if($.trim(item.state) == "F") {
					return "deletedColor";
				}
				return null;
			},
		};
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	//Hidden 필드 Setting
	AUIGrid.hideColumnByDataField(myGridID, "id");
	AUIGrid.hideColumnByDataField(myGridID, "oid");
	AUIGrid.hideColumnByDataField(myGridID, "state");
	AUIGrid.hideColumnByDataField(myGridID, "lifecycle");
	AUIGrid.hideColumnByDataField(myGridID, "documentType");
	AUIGrid.hideColumnByDataField(myGridID, "model");
	AUIGrid.hideColumnByDataField(myGridID, "deptcode");
	AUIGrid.hideColumnByDataField(myGridID, "preseration");
	AUIGrid.hideColumnByDataField(myGridID, "folder");
	// 에디팅 시작 이벤트 바인딩
	AUIGrid.bind(myGridID, "cellDoubleClick", function(event) {
		modifyRowOpen(event.item);
		return false; // false 반환하면 그리드 내장 에디터 표시 안함.(더 이상 진행 안함)
	});
	AUIGrid.bind(myGridID, "cellEditEnd", auicellEditEndHandler);
	
}
//편집 핸들러
function auicellEditEndHandler(event) {
	console.log(event);
	var field = event.dataField;
	var value = event.value;
	var oldValue = event.oldValue;
	if(event.type == "cellEditEnd" && field.toUpperCase().indexOf("FILENAME")>-1) {
		if(value.length>0){
			for(var d=0;d<fileList.length;d++){
				if(fileList[d]==value){
					delete fileList[d];	
				}
				
			}
		}else if(oldValue.length>0){
			fileList.push( oldValue);
		}
	}
};
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
			alert("이미 등록된 문서입니다.등록된 리스트를 삭제후 추가 해 주세요.")
			return;
		}
		
		var index = AUIGrid.rowIdToIndex(myGridID, data[i].id);
		index = index +1;
		var documentName = data[i].documentName;
		var lifecycle = data[i].lifecycle;
		var documentType = data[i].documentType;
		var preseration = data[i].preseration;
		var location = data[i].location;
		var fileName = data[i].fileName;
		console.log("fileName = " + fileName);
		if(documentName == "" ) {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('문서종류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		if(lifecycle == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('결재방식')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		if(documentType == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('문서유형')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		if(preseration ==""){
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('보존기간')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		if(location =="/Default/Document"){
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('분류체계')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		if(fileName == "") {
			alert(index+"${f:getMessage('번째 라인의 ')}${f:getMessage('파일명')}${f:getMessage('을(를) 입력해 주세요')}");
			return;
		}
	}
	return true;
}
<%----------------------------------------------------------
*                      일괄 등록 Row 추가 팝업
----------------------------------------------------------%>
function batchCreate(){
	var str = getURLString("doc", "batchDocumentCreate", "do");
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=800,height=400,left=" + leftpos + ',top=' + toppos;
    newwin = window.open( str , "batchDocumentCreate", opts+rest);
}

<%----------------------------------------------------------
*                      일괄 추가
----------------------------------------------------------%>
function addRow(dataMap){
	var addCount = dataMap["addCount"];
	var documentName = dataMap["documentName"];
	var docName = dataMap["docName"];
	var lifecycle = dataMap["lifecycle"];
	var lifecycleName = dataMap["lifecycleName"];
	var documentType = dataMap["documentType"];
	var documentTypeName = dataMap["documentTypeName"];
	var description = dataMap["description"];
	var model = dataMap["model"];
	var modelName = dataMap["modelName"];
	var interalnumber = dataMap["interalnumber"];
	var writer = dataMap["writer"];
	var deptcode = dataMap["deptcode"];
	var deptcodeName = dataMap["deptcodeName"];
	var preseration = dataMap["preseration"];
	var preserationName = dataMap["preserationName"];
	var location = dataMap["location"];
	var folder = dataMap["folder"];
	var rowList = [];
	for(var i=0; i<addCount; i++) {
		rowList[i] = {
				number    : "",
				state	  : "",
				msg		  : "",
				documentName : documentName,
				docName : docName,
				lifecycle : lifecycle,
				lifecycleName : lifecycleName,
				documentType : documentType,
				documentTypeName : documentTypeName,
				description : description,
				model : model,
				modelName : modelName,
				writer : writer,
				interalnumber : interalnumber,
				deptcode : deptcode,
				deptcodeName : deptcodeName,
				preseration : preseration,
				preserationName : preserationName,
				location : location,
				folder : folder,
				fileName : "",
				attachmentFileName1 : "",
				attachmentFileName2 : "",
				attachmentFileName3 : "",
				attachmentFileName4 : "",
				attachmentFileName5 : "",
				partNumber : ""
		}
	}
	AUIGrid.addRow(myGridID, rowList, "last");
	alert(addCount+"개 추가 되었습니다.");
	newwin.focus();
}

<%----------------------------------------------------------
*                     개별 Row 수정 Page
----------------------------------------------------------%>
function modifyRowOpen(gridItem) {
	
	var str = getURLString("doc", "batchDocumentCreate", "do")+"?auiId="+gridItem.id+"&mode=single";
	
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=800,height=600,left=" + leftpos + ',top=' + toppos;
    var modifywin = window.open( str , "batchDocumentModify", opts+rest);
}

<%----------------------------------------------------------
*                      개별 수정
----------------------------------------------------------%>
function modifyRow(dataMap){
	var id = dataMap["auiId"];
	var documentName = dataMap["documentName"];
	var docName = dataMap["docName"];
	var lifecycle = dataMap["lifecycle"];
	var lifecycleName = dataMap["lifecycleName"];
	var documentType = dataMap["documentType"];
	var documentTypeName = dataMap["documentTypeName"];
	var description = dataMap["description"];
	var model = dataMap["model"];
	var modelName = dataMap["modelName"];
	var interalnumber = dataMap["interalnumber"];
	var writer = dataMap["writer"];
	var deptcode = dataMap["deptcode"];
	var deptcodeName = dataMap["deptcodeName"];
	var preseration = dataMap["preseration"];
	var preserationName = dataMap["preserationName"];
	var location = dataMap["location"];
	var folder = dataMap["folder"];
	var rowItems = AUIGrid.getItemsByValue(myGridID, "id", id);
	var index = AUIGrid.rowIdToIndex(myGridID, id);	
	var rowItems = {
			documentName : documentName,
			docName : docName,
			lifecycle : lifecycle,
			lifecycleName : lifecycleName,
			documentType : documentType,
			documentTypeName : documentTypeName,
			description : description,
			model : model,
			modelName : modelName,
			interalnumber : interalnumber,
			writer : writer,
			deptcode : deptcode,
			deptcodeName : deptcodeName,
			preseration : preseration,
			preserationName : preserationName,
			location : location,
			folder : folder
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
window.closeProcessing = function() {
	gfn_EndShowProcessing();
}
</script>

<body>

<form name="createPackageDocument" id="createPackageDocument" method="post" enctype="multipart/form-data">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ${f:getMessage('문서')} ${f:getMessage('일괄등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>
<table border="0" cellpadding="0" cellspacing="4" align=right>
	<tr>
		<td>
			<button type="button" class="btnCRUD" id="uploadPackageDocument">
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
            
            	<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('문서분류')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<b>
							<span id="locationName">
								/Default/Document
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
							<jsp:param name="formId" value="createPackageDocument"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="btnId" value="uploadPackageDocument" />
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