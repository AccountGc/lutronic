<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	/* var now = new Date();	// 현재 날짜 및 시간
	var year = now.getFullYear();	// 연도
	var yearDate = year + "-01-01";
	
	$('#predate').val(yearDate); */
	
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	lfn_getStateList();
	lfn_getCadDivisionList();
	lfn_getCadTypeList();
	lfn_Search();
	numberCodeList('mat');
	numberCodeList('deptcode');
	numberCodeList('model');
	numberCodeList('manufacture');
	numberCodeList('finish');
	numberCodeList('productmethod');
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, '', true);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			$("#"+ id).append("<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>");
		}
	}
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage('번호')}'
			,col02 : 'CAD${f:getMessage('타입')}'
			,col03 : '${f:getMessage('도면번호')}'
			,col04 : 'Thumbnail'
			,col05 : '${f:getMessage('도면명')}'
			,col06 : '${f:getMessage('도면분류')}'
			,col07 : 'Rev.'
			,col08 : '${f:getMessage('상태')}'
			,col09 : '${f:getMessage('등록자')}'
			,col10 : '${f:getMessage('등록일')}'
			,col11 : '${f:getMessage('수정일')}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	sHeader += "," + COLNAMEARR.col07;
	sHeader += "," + COLNAMEARR.col08;
	sHeader += "," + COLNAMEARR.col09;
	sHeader += "," + COLNAMEARR.col10;
	sHeader += "," + COLNAMEARR.col11;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[6]  = "text-align:center;";
	sHeaderAlign[7]  = "text-align:center;";
	sHeaderAlign[8]  = "text-align:center;";
	sHeaderAlign[9]  = "text-align:center;";
	sHeaderAlign[10]  = "text-align:center;";
	
	var sWidth = "";
	sWidth += "3";
	sWidth += ",7";
	sWidth += ",10";
	sWidth += ",5";
	sWidth += ",17";
	sWidth += ",15";
	sWidth += ",8";
	sWidth += ",8";
	sWidth += ",9";
	sWidth += ",9";
	sWidth += ",9";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	var sColType = "";
	sColType += "ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	
	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	
	documentListGrid = new dhtmlXGridObject('listGridBox');
	documentListGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	documentListGrid.setHeader(sHeader,null,sHeaderAlign);
	documentListGrid.enableAutoHeight(true);
	documentListGrid.setInitWidthsP(sWidth);
	documentListGrid.setColAlign(sColAlign);
	documentListGrid.setColTypes(sColType);
	documentListGrid.setColSorting(sColSorting);
	
	/*grid text copy option*/
    documentListGrid.enableBlockSelection();
    documentListGrid.forceLabelSelection(true);
    documentListGrid.attachEvent("onKeyPress",function onKeyPressed(code,ctrl,shift){
        if(code==67&&ctrl){
            if(!documentListGrid._selectionArea) return alert("return");
            documentListGrid.setCSVDelimiter("\t");
            documentListGrid.copyBlockToClipboard();
	    }
	    return;
	});
	
	documentListGrid.attachEvent("onHeaderClick",function(rowId,cellIndex){
		
		var sortValue = "";
		var header = "";
		if(rowId == 1) {
			sortValue = "checkoutInfo.state";
			header = COLNAMEARR.col02;
		}else if(rowId == 2) {
			sortValue = "master>number";
			header = COLNAMEARR.col03;
		}else if(rowId == 4 ){
			sortValue = "master>name";
			header = COLNAMEARR.col05;
		}else if(rowId == 5 ){
			sortValue = "folderingInfo.parentFolder.key.id";
			header = COLNAMEARR.col06;
		}else if(rowId == 7 ){
			sortValue = "state.state";
			header = COLNAMEARR.col08;
		}else if(rowId == 8 ) {
			sortValue = "creator";
			header = COLNAMEARR.col09;
		}else if(rowId == 9 ) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col10;
		}else if(rowId == 10 ) {
			sortValue = "thePersistInfo.modifyStamp";
			header = COLNAMEARR.col11;
		}
		
		
		if(sortValue != "") {
			if($("#sortValue").val() != sortValue) {
				$("#sortCheck").val("");
			}
			
			if("true" == $("#sortCheck").val()) {
				$("#sortCheck").val("false");
				header += " ▼";
			} else {
				$("#sortCheck").val("true");
				header += " ▲";
			}
			documentListGrid.setColLabel(1, COLNAMEARR.col02);
			documentListGrid.setColLabel(2, COLNAMEARR.col03);
			documentListGrid.setColLabel(4, COLNAMEARR.col05);
			documentListGrid.setColLabel(5, COLNAMEARR.col06);
			documentListGrid.setColLabel(7, COLNAMEARR.col08);
			documentListGrid.setColLabel(8, COLNAMEARR.col09);
			documentListGrid.setColLabel(9, COLNAMEARR.col10);
			documentListGrid.setColLabel(10, COLNAMEARR.col11);
			documentListGrid.setColLabel(rowId, header);
			
			$("#sortValue").val(sortValue);
			$("#sessionId").val("");
			$("#page").val(1);
			lfn_Search();
		}
	});
	
	documentListGrid.init();
}

<%----------------------------------------------------------
*                      CAD 구분 리스트 가져오기
----------------------------------------------------------%>
function lfn_getCadDivisionList(){
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("drawing", "cadDivisionList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#cadDivision").find("option").remove();
			$("#cadDivision").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					if(data[i].value == "PROE" || data[i].value == "OTHER")  {
						$("#cadDivision").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
					}
				}
			}
			
		}
	});
}

<%----------------------------------------------------------
*                      CAD 타입 리스트 가져오기
----------------------------------------------------------%>
function lfn_getCadTypeList(){
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("drawing", "cadTypeList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#cadType").find("option").remove();
			$("#cadType").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					if(data[i].value == "CADDRAWING" || data[i].value == "CADASSEMBLY" || data[i].value == "CADCOMPONENT" || data[i].value == "OTHER") {
						$("#cadType").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
					}
				}
			}
			
		}
	});
}

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		resetSearch();
	})
	<%----------------------------------------------------------
	*                      초기화 버튼 클릭시
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
		$("#fid").val("");
		$("#location").val("/Default/PART_Drawing");
		$("#locationName").html("/Default/PART_Drawing");
	})
	<%----------------------------------------------------------
	*                      상세검색 검색 버튼
	----------------------------------------------------------%>
	$("#detailBtn").click(function () {
		if($('#SearchDetailProject').css('display') == 'none'){ 
			$("#detailText").html("${f:getMessage('상세검색')} -");
		   	$('#SearchDetailProject').show(); 
		} else { 
			$("#detailText").html("${f:getMessage('상세검색')} +");
		   	$('#SearchDetailProject').hide(); 
		}
	})
	$(document).keypress(function(event) {
		 if(event.which == 13 && $( '#userNameSearch' ).is( ':hidden' )) {
			 resetSearch();
		 }
	})
})

window.resetSearch = function() {
	lfn_DhtmlxGridInit();
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("drawing", "listDrawingAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			var vArr = new Array();
			vArr[0] = data.rows    ;
			vArr[1] = data.formPage   ;
			vArr[2] = data.totalCount ;
			vArr[3] = data.totalPage  ;
			vArr[4] = data.currentPage;
			vArr[5] = data.startPage  ;
			vArr[6] = data.endPage    ;
			vArr[7] = data.sessionId  ;
			
			documentListGrid.clearAll();
			documentListGrid.loadXMLString(data.xmlString);
			
			$("#xmlString").val(data.xmlString);
			
			gfn_SetPaging(vArr,"pagingBox");
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
*                      Lifecycle 리스트 가져오기
----------------------------------------------------------%>
function lfn_getStateList() {
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("WFItem", "lifecycleList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#state").find("option").remove();
			$("#state").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}

<%----------------------------------------------------------
*                      등록자 데이터 추가
----------------------------------------------------------%>
function addUser(obj) {
	$("#creator").val(obj[0][0]);
	$("#creatorName").val(obj[0][1]);
}

<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	documentListGrid.clearAll();
	documentListGrid.loadXMLString($("#xmlString").val());
}

var dragObject = null;
var mouseOffset = null;
var firstX = null;
var firstY = null;

<%----------------------------------------------------------
*                      미리 보기
----------------------------------------------------------%>
function showThumAction(oid){
	
	var url	= getURLString("drawing", "showThumAction", "do");
	//console.log("showThumAction="+oid);
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : oid
		},
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage(' 오류')}";
			alert(msg);
		}
		
		,success: function(data) {
			console.log("showThumAction data="+data +","+data.result);
			if(data.result) {
				var num= data.num;
				var imgpath = data.imgpath;
				var copyTag = data.copyTag;
				
				showThum(num, imgpath, oid, copyTag);
			}
		}
	});
	
}
function showThum( num, imgpath, oid, copyTag){
	if(imgpath!="") {
		var image = new Image();
		image.src = imgpath;
		var text; 
		console.log("num = " + num+" : imgpath = " +imgpath)
		text ='<table cellpadding="1" cellspacing="1" bgcolor="#FFFFFF" border-style:solid;" border=1 width=195>'; 
		text += '<tr align = center>';
		text += '<td>';
		text += '<img id=thumImg title="'+num+'" src = ' + imgpath + ' border = 0 style="cursor:move">';

		text += '<br>';
		text += '</td>';
		text += '</tr>';
		text += '<tr>';
		text += '<td align=center><table width=100% border=0 cellpadding="0" cellspacing="0"><tr align=center>';
		text += '<td align=center valign=middle><a href="JavaScript:openView(\''+oid+'\')" title="상세보기"><b>'+num+'</a></td>';
		text += '<td align=right width=40><a href="JavaScript:hideThum()">[닫기]</a></td></tr></table></td>';
		text += '</tr>';
		text += '</table>'; 
		
		var t;
		t = document.getElementById('imgView');
		t.innerHTML=text; 
		var obj = document.getElementById(oid);
		
		
		var pos = findPos2(obj);
		$("div#imgView").css({"left":(pos[0]+33)+"px"});
	    $("div#imgView").css({"top":(pos[1]+15)+"px"});

		tig = document.getElementById('thumImg');
		
		tig.onmousemove = mouseMove;
		tig.onmouseup = mouseUp;
		tig.onmousedown = function(event){
			dragObject = t;
			mouseOffset = getMouseOffset(t,event);
			
			return false;
		};
		t.style.visibility='visible'; 

	}
}

function findPos2(obj) {
	var curleft = curtop = curbuttom = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft
		curtop = obj.offsetTop
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	return [curleft,curtop];
}


function hideThum(){ 
	var t;
	t = document.getElementById('imgView');
	t.innerHTML=''; 
	t.style.visibility='hidden'; 
}

function mouseUp(event){
	dragObject = null;
}

function mouseMove(event){
	if(!dragObject)return;

	event = event ? event : window.event;
	var mousePos = mousePosition(event);
	t = document.getElementById('imgView');

	if(dragObject){
		dragObject.style.top = mousePos.y - mouseOffset.y + "px";
		dragObject.style.left = mousePos.x - mouseOffset.x + "px";
		return false;
	}
}

function getMouseOffset(target,event){
	event = event ? event : window.event;
	var mousePos = mousePosition(event);
	var x = mousePos.x - target.offsetLeft;
	var y = mousePos.y - target.offsetTop;

	return new mousePoint(x,y);
}

function mousePosition(event){
	var x = Number(event.clientX);
	var y = Number(event.clientY);
	return new mousePoint(x,y);
}

function mousePoint(x,y){
	this.x = x;
	this.y = y;
}

function onExcelDown() {
	$("#PartDrawingForm").attr("method", "post");
	$("#PartDrawingForm").attr("action", getURLString("excelDown", "drawingExcelDown", "do")).submit();
}
</script>
<body>
<form name="PartDrawingForm" id="PartDrawingForm" method="post" style="padding:0px;margin:0px" >

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 폴더 선택시 검색 function을 호출하기 위한 변수 -->
<input type="hidden" name="search" id="search" value="true" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<input type="hidden" name="lifecycle"	 id="lifecycle"  	value="LC_PART">
<input type="hidden" name="fid" 		 id="fid" 	 	 	value="" />
<input type="hidden" name="location" 	 id="location" 		value="/Default/PART_Drawing" />

<div id=imgView style="visibility=hidden; position:absolute; left:78px; top:165px; width:400px; height:62px; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;"></div>

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							${f:getMessage('도면')}
							${f:getMessage('관리')}
							>
							${f:getMessage('도면')}
							${f:getMessage('검색')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height="1" width="100%"></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>				
				<tr >
					<td class="tdblueM">${f:getMessage('도면분류')}</td>
					<td class="tdwhiteL">
						<b>
							<span id="locationName">
								/Default/PART_Drawing
							</span>
						</b>
					</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdwhiteL">
						<input name="islastversion" type="radio" class="Checkbox" value="true" checked>${f:getMessage('최신버전')}
						<input name="islastversion" type="radio" class="Checkbox" value="false" >${f:getMessage('모든버전')}
					</td>
				</tr>
				<tr>
					<td class="tdblueM">CAD ${f:getMessage('구분')}</td>
					<td class="tdwhiteL">
						<select name="cadDivision" id="cadDivision" style="width:95%">
							<option value="">${f:getMessage('선택')}</option>
						</select>
					</td>
					<td class="tdblueM">CAD ${f:getMessage('타입')}</td>
					<td class="tdwhiteL">
						<select name="cadType" id="cadType" style="width:95%">
							<option value="">${f:getMessage('선택')}</option>
						</select>
					</td>
				</tr>				
				<tr>
					<td class="tdblueM">${f:getMessage('도면')}${f:getMessage('번호')}</td>
					<td class="tdwhiteL">
						<input name="number" class="txt_field" size="30" style="width:90%" value="" onblur="this.value=this.value.toUpperCase();" />
					</td>
					<td class="tdblueM">${f:getMessage('도면명')}</td>
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" style="width:90%" engnum="engnum" value=""/>
					</td>					
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>				
				<tr>
					<td class="tdblueM">${f:getMessage('등록일')}</td>
					<td class="tdwhiteL">
						<input name="predate" id="predate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly />
						<a href="javascript:void(0);">
							<img id="predateBtn" src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0>
						</a>
						<a href="JavaScript:clearText('predate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly />
						<a href="javascript:void(0);">
							<img id="postdateBtn" src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0>
						</a>
						<a href="JavaScript:clearText('postdate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
					<td class="tdblueM">${f:getMessage('수정일')}</td>
					<td class="tdwhiteL">
						<input name="predate_modify" id="predate_modify" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly />
						<a href="javascript:void(0);">
							<img id="predate_modifyBtn" src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0>
						</a>
						<a href="JavaScript:clearText('predate_modify')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="postdate_modify" id="postdate_modify" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly />
						<a href="javascript:void(0);">
							<img id="postdate_modifyBtn" src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0>
						</a>
						<a href="JavaScript:clearText('postdate_modify')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
				</tr>
				
				<tr>
                    <td class="tdblueM">${f:getMessage('등록자')}</td>
                    <td class="tdwhiteL">
                    
                    	<%-- 
                        <input TYPE="hidden" name="creator"  id="creator"   value="" >
                        <input type=text  name="creatorName" id="creatorName" size="30" style="width:30%" value="" class="txt_field" readOnly >

                        <a href="JavaScript:searchUser('documentForm','single','creator','creatorName','people')">
                        	<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
                        </a>
                        <a href="JavaScript:clearText('creator');clearText('creatorName')">
                        	<img src="/Windchill/jsp/portal/images/x.gif" border=0>
                        </a>
                        --%>
                        
                        <jsp:include page="/eSolution/common/userSearchForm.do">
							<jsp:param value="single" name="searchMode"/>
							<jsp:param value="creator" name="hiddenParam"/>
							<jsp:param value="creatorName" name="textParam"/>
							<jsp:param value="people" name="userType"/>
							<jsp:param value="" name="returnFunction"/>
						</jsp:include>
						
                    </td>	
                    				
                    <td class="tdblueM">
                    	${f:getMessage('상태')}
                    </td>
                    
                   	<td class="tdwhiteL">
                   		<select name=state id="state">
						</select>
                   	</td>					
				</tr>
			</table>
			<div id="SearchDetailProject" style="display: none" >
         	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
         	<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
         		<!-- MODEL, PRODUCTMETHOD -->			
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('프로젝트코드')}
						</td>
						
						<td class="tdwhiteL">
							<select id='model' name='model' style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('제작방법')}
						</td>
						
						<td class="tdwhiteL">
							<select id='productmethod' name='productmethod' style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- DEPTCODE, UNIT -->
	              	<tr bgcolor="ffffff" height="35">
	              		<td class="tdblueM">
							${f:getMessage('부서')}
						</td>					
						
						<td class="tdwhiteL">
							<select id="deptcode" name="deptcode" style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('단위')}
						</td>					
						
						<td class="tdwhiteL">
							<select id='unit' name='unit' style="width: 95%">
							</select>
						</td>
					</tr>
	                
					<!-- WEIGHT, MANUFACTURE -->
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('무게')}
						</td>
						
						<td class="tdwhiteL">
							<input id="weight1" name="weight1" type="text" style="width: 30%;"/> ~ <input id="weight1" name="weight2" type="text" style="width: 30%;"/>
						</td>
						
						<td class="tdblueM">
							MANUFACTURER
						</td>
						
						<td class="tdwhiteL">
							<select id="manufacture" name="manufacture" style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- MAT, FINISH -->
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('재질')}
						</td>
						
						<td class="tdwhiteL">
							<select id="mat" name="mat" style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('후처리')}
						</td>
						
						<td class="tdwhiteL">
							<select id="finish" name="finish" style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- REMARKS, SPECIFICATION -->
					<tr bgcolor="ffffff" height="35">
	               		<td class="tdblueM">
							${f:getMessage('비고')}
						</td>
						
						<td class="tdwhiteL">
							<input id="remarks" name="remarks" type="text" style="width: 95%;"/>
						</td>
						
	               		<td class="tdblueM">
							${f:getMessage('사양')}
						</td>
						
						<td class="tdwhiteL">
							<input id="specification" name="specification" type="text" style="width: 95%;"/>
						</td>
					</tr>
       		</table>
         	</div>
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
              <tr>
                  <td>
                  	<button type="button" name="" value="" class="btnSearch" title="검색" id="searchBtn" name="searchBtn">
	                  	<span></span>
	                  	${f:getMessage('검색')}
                  	</button>   
                  </td>
                  <td>
                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">${f:getMessage('초기화')}</button>
                  </td>
                  <td>
                  	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('상세검색')}" id="detailBtn" name="detailBtn">
	                  	<span></span>
	                  	<font id="detailText" >${f:getMessage('상세검색')} + </font>
                  	</button>   
                  </td>
                  <td>
	                  <a href="javascript:onExcelDown();">
					  	<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="absmiddle">
				      </a>
			      </td>
              </tr>
           </table>
		</td>
	</tr>
	
	<tr>
		<td>
			<div id="listGridBox" style="width:100%;" ></div>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="2" cellspacing="1" align="center" valign=top>
				<tr height="35">
					<td>
						<div id="pagingBox"></div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
</table>
</form>
</body>
</html>