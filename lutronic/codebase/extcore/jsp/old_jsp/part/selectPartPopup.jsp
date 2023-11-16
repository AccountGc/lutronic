<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
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
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	lfn_getStateList();
	getQuantityUnit();
	numberCodeList('mat');
	numberCodeList('deptcode');
	numberCodeList('model');
	numberCodeList('manufacture');
	numberCodeList('finish');
	numberCodeList('productmethod');
	
	lfn_Search();
});

<%----------------------------------------------------------
*                      폴더 선택시
----------------------------------------------------------%>
function setLocationDocument(foid,loc,isLast){
	$("#locationName").html(loc);
	$("#location").val(loc);
	$("#fid").val(foid);
	$("#islastversion").val(isLast);
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit(){
	var COLNAMEARR = {
			 col01 : ''
			,col02 : '${f:getMessage('번호')}'
			,col03 : ''
			,col04 : '${f:getMessage('품목번호')}'
			,col05 : '${f:getMessage('품목명')}'
			,col06 : '${f:getMessage('품목분류')}'
			,col07 : '${f:getMessage('Rev.')}'
			,col08 : '${f:getMessage('OEM Info.')}'
			,col09 : '${f:getMessage('상태')}'
			,col10 : '${f:getMessage('등록자')}'
			,col11 : '${f:getMessage('등록일')}'
			,col12 : '${f:getMessage('수정일')}'
			,col13 : 'BOM'
			,col14 : 'disabled'
	}
	
	var sHeader = "";
	if('${mode}' == 'mutil') {
		sHeader +=       '<input type=checkbox name=allCheck id=allCheck>'
	}else {
		sHeader +=       COLNAMEARR.col01;
	}
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
	sHeader += "," + COLNAMEARR.col12;
	sHeader += "," + COLNAMEARR.col13;
	sHeader += "," + COLNAMEARR.col14;
	
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
	sHeaderAlign[11]  = "text-align:center;";
	sHeaderAlign[12]  = "text-align:center;";
	sHeaderAlign[13]  = "text-align:center;";

	var sWidth = "";
	sWidth += "5";
	sWidth += ",5";
	sWidth += ",5";
	sWidth += ",10";
	sWidth += ",27";
	sWidth += ",14";
	sWidth += ",6";
	sWidth += ",7";
	sWidth += ",7";
	sWidth += ",9";
	sWidth += ",9";
	sWidth += ",9";
	sWidth += ",8";
	sWidth += ",0";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	var sColType = "";
	if('${mode}' == 'mutil') {
		sColType += "ch";
	}else {
		sColType += "ra";
	}
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
    
	documentListGrid.setColumnHidden(12,true);
	
	documentListGrid.attachEvent("onHeaderClick",function(rowId,cellIndex){
		
		var sortValue = "";
		var header = "";
		
		if(rowId == 3) {
			sortValue = "master>number";
			header = COLNAMEARR.col04;
		}else if(rowId == 4 ){
			sortValue = "master>name";
			header = COLNAMEARR.col05;
		}else if(rowId == 7 ) {
			sortValue = "creator";
			header = COLNAMEARR.col08;
		}else if(rowId == 8) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col09;
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
			
			documentListGrid.setColLabel(3, COLNAMEARR.col04);
			documentListGrid.setColLabel(4, COLNAMEARR.col05);
			documentListGrid.setColLabel(7, COLNAMEARR.col08);
			documentListGrid.setColLabel(8, COLNAMEARR.col09);
			documentListGrid.setColLabel(rowId, header);
			
			$("#sortValue").val(sortValue);
			$("#sessionId").val("");
			$("#page").val(1);
			lfn_Search();
		}
	});
	
	documentListGrid.init();
}

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		resetSearch();
	})
	<%----------------------------------------------------------
	*                      선택 버튼 클릭시
	----------------------------------------------------------%>
	$("#selectBtn").click(function () {
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
		}
		
		var array = checked.split(",");
		var returnArr = new Array();
		
		for(var i =0; i < array.length; i++) {
			
			var oid = array[i];		
   			var number = documentListGrid.cells(array[i], 3).getValue();				 // 품목코드
   			var name = documentListGrid.cells(array[i], 4).getValue();				 // 품명
   			var version = documentListGrid.cells(array[i], 6).getValue();				 // 버전
   			var state = documentListGrid.cells(array[i], 7).getValue();	 		 // 상태
   			var creator = documentListGrid.cells(array[i], 8).getValue();     // 등록자
 			var createDate = documentListGrid.cells(array[i], 9).getValue();			 	 // 등록일
  			var updateDate = documentListGrid.cells(array[i], 10).getValue();			 	 // 수정일
  			var bom = documentListGrid.cells(array[i], 11).getValue();			 	 // BOM
  				
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = number;
   			returnArr[i][2] = name;
   			returnArr[i][3] = version;
   			returnArr[i][4] = state;
   			returnArr[i][5] = creator;
  			returnArr[i][6] = createDate;
  			returnArr[i][7] = updateDate;
  			returnArr[i][8] = bom;
		}
		if($("#moduleType").val() =="EO"){
			
			opener.parent.addCompletePart(returnArr);
		}else{
			var cnt = opener.parent.addPart(returnArr);
			if(cnt>0){
				alert("품목이 추가되었습니다.");
			}
		}
		
		if('${mode}' == 'single') {
			self.close();
		}
	})
	<%----------------------------------------------------------
	*                      서브메뉴 숨기기 / 펼치기
	----------------------------------------------------------%>
	$("#hiddenArrow").click(function () {
	    if($("#subMenu_table").css('display') == 'none') {
	    	$("#subMenu_table").show();
	    	$("#subMenu_table_h").hide();
	    	$("#hiddenArrow").attr('src', "/Windchill/jsp/portal/images/base_design/layer_hidden.gif");
	    	$("#hiddenArrow").attr('title', "${f:getMessage('서브메뉴')} ${f:getMessage('닫기')}");
	    }else {
	    	$("#subMenu_table").hide();
	    	$("#subMenu_table_h").show();
	    	$("#hiddenArrow").attr('src', "/Windchill/jsp/portal/images/base_design/layer_show.gif");
	    	$("#hiddenArrow").attr('title', "${f:getMessage('서브메뉴')} ${f:getMessage('열기')}");
	    }
	    

	    if($("#xmlString").val() != null) {
	    	redrawGrid();
	    }
	})
	<%----------------------------------------------------------
	*                      상세검색 버튼
	----------------------------------------------------------%>
	$("#detailBtn").click(function () {
		if($('#SearchDetailProject').css('display') == 'none'){ 
		   $('#SearchDetailProject').show(); 
		} else { 
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

$(document).on('click', 'input:checkbox[name="allCheck"]', function(){
	if(this.checked) {
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck checked>");
		var rows = documentListGrid.getRowsNum();
		console.log(rows);
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			if(documentListGrid.cells(rowId,13).getValue() == "") {
				documentListGrid.cellById(rowId, 0).setChecked(true);
			} else {
				if("false" == documentListGrid.cells(rowId,13).getValue()) {
					documentListGrid.cellById(rowId, 0).setChecked(true);
				}
			}
		}
	}else {
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck>");
		var rows = documentListGrid.getRowsNum();
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			if(documentListGrid.cells(rowId,13).getValue() == "") {
				documentListGrid.cellById(rowId, 0).setChecked(false);
			} else {
				if("false" == documentListGrid.cells(rowId,13).getValue()) {
					documentListGrid.cellById(rowId, 0).setChecked(false);
				}
			}
		}
	}
})

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("part", "listPartAction", "do") + "?select=true";
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
			
			IsCellDisable();
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
*                      CheckBox를 비활성화 하기 위한 함수
----------------------------------------------------------%>
function IsCellDisable(){
	var rows = documentListGrid.getRowsNum();
	for(var i = 0; i < rows; i++) {
		var rowId = documentListGrid.getRowId(i);
		if("true" == documentListGrid.cells(rowId,13).getValue()) {
			documentListGrid.cellById(rowId, 0).setDisabled(true);
		}
	}
}

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList() {
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("WFItem", "lifecycleList", "do");
	var state ="<c:out value='${state}' />";
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
			if(state !=""){
				if(data.length > 0) {
					for(var i=0; i<data.length; i++) {
						if( data[i].value == state){
							$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
						}
						
					}
				}
			}else{
				if($("#stateValue").val() == "") {
					$("#state").append("<option value=''>${f:getMessage('선택')}</option>");
				}
				
				if(data.length > 0) {
					for(var i=0; i<data.length; i++) {
						$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
					}
				}
			}
			
			
		}
	});
}

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
*                      단위 리스트 가져오기
----------------------------------------------------------%>
function getQuantityUnit() {
	var url	= getURLString("part", "getQuantityUnit", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('단위 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			$("#unit").append("<option value=''> ${f:getMessage('선택')} </option>");
			for(var i=0; i<data.length; i++) {
				$("#unit").append("<option value='" + data[i] + "'>" + data[i] + "</option>");
			}
		}
	});
}

<%----------------------------------------------------------
*                      유저 데이터 설정
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

<%----------------------------------------------------------
*                      BOM 버튼
----------------------------------------------------------%>

function viewBom(oid){
	
	var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM", opts+rest);
    newwin.focus();
    
}
</script>

<body>

<form name="PartDrawingForm" id="PartDrawingForm" method="post">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<input type="hidden" name="lifecycle" id="lifecycle" value="LC_PART">
<input type="hidden" name="fid"	id="fid" value="">
<input type="hidden" name="location" id="location" value="/Default/PART_Drawing">

<input type="hidden" name="moduleType" id="moduleType" value="<c:out value="${moduleType }"/>" />
<input type="hidden" name="stateValue" id="stateValue" value="<c:out value="${state }"/>" />

<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td valign=top width=0 background="/Windchill/jsp/portal/images/ds_sub.gif" bgcolor=ffffff >
            <table id="subMenu_table" width="180" style="display:" height="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top">
                    	<jsp:include page="/eSolution/folder/treeFolder.do">
							<jsp:param value="/Default/PART_Drawing" name="folder"/>
						</jsp:include>
                    </td>
                </tr>
            </table>
        </td>
    
    	<td>
    		<table border=0 id=subMenu_table_h  width=1  height="100%"  style="display:none"  cellpadding="0" cellspacing="0" >
				<tr>
					<td valign=top height="100%" >

						<table width="100%" border="0" cellspacing="0" cellpadding="0" height=100%>
							<tr>
								<td  align="center" valign="top">
									<table border="0" height="100%" class="info_table" cellspacing="0" cellpadding="0" width=100% style="table-layout:auto">
										<tr>
											<td align="center" class="Subinfo_img"><span id ="menuTitle"></span></td>
										</tr>
										<tr>
											<td align="center" valign="top" width=99% style="padding-right:4;padding-left:4"></td>
										</tr>
										<tr>
											<td class="info_btm"></td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
    	</td>
    
	    <td valign=top width=7  background="/Windchill/jsp/portal/images/barFrame_bg.gif">
	        <table cellpadding="0" cellspacing="0">
	            <tr>
	                <td height=15></td>
	            </tr>
	            <tr>
	                <td valign=top>
	                    <a href="javascript:void(0);">
	                    	<img id="hiddenArrow" src="/Windchill/jsp/portal/images/base_design/layer_hidden.gif" border=0 title="서브메뉴닫기" />
	                    </a>
	                </td>
	            </tr>
	        </table>
	    </td>
	    
    	<td width=100% valign="top">
    		<table width="100%" border="0" cellpadding="0" cellspacing="3" >
			    <tr align="center">
			        <td valign="top" style="padding:0px 0px 0px 0px">
			
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
						    <tr>
						        <td height="1" width=100%></td>
						    </tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
						    <col width="13%"><col width="37%"><col width="13%"><col width="37%">
						    <tr>
						        <td class="tdblueM">${f:getMessage('품목분류')}</td>
						        <td class="tdwhiteL">
						        	<b>
						        		<span id="locationName">
						        			/Default/PART_Drawing
						        		</span>
						        	</b>
						        </td>
						        <td class="tdblueM">${f:getMessage('Rev.')}</td>
								<td class="tdwhiteL">
								    <input name="islastversion" type="radio" class="Checkbox" value="true" checked /> ${f:getMessage('최신Rev.')}
									<input name="islastversion" type="radio" class="Checkbox" value="false" /> ${f:getMessage('모든Rev.')}
								</td>
						        
						    </tr>
						    <tr>
						        <td class="tdblueM">${f:getMessage('품목번호')}</td>
						        <td class="tdwhiteL">
						            <input name="partNumber" class="txt_field" size="30" style="width:90%" onblur="this.value=this.value.toUpperCase();" />
						        </td>
						        <td class="tdblueM">${f:getMessage('품목명')}</td>
						        <td class="tdwhiteL">
						            <input name="partName" class="txt_field" size="30" style="width:90%" />
						        </td>
						    </tr>
						    
							<tr>
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
								    <input name="predate" id="predate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="predateBtn" id="predateBtn" />
									</a>
									<a href="javascript:void(0);" onclick="clearText('predate');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
									~
									<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="postdateBtn" id="postdateBtn" />
									</a>
									<a href="javascript:void(0);" onclick="clearText('postdate');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
								</td>
								<td class="tdblueM">${f:getMessage('수정일')}</td>
								<td class="tdwhiteL">
								    <input name="predate_modify" id="predate_modify" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="predate_modifyBtn" id="predate_modifyBtn" />
									</a>
									<a href="javascript:void(0);" onclick="clearText('predate_modify');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
									~
									<input name="postdate_modify" id="postdate_modify" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="postdate_modifyBtn" id="postdate_modifyBtn"/>
									</a>
									<a href="javascript:void(0);" onclick="clearText('postdate_modify');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
								</td>
							</tr>
							<tr>
								<td class="tdblueM">${f:getMessage('등록자')}</td>
				                <td class="tdwhiteL">
				                	<%-- 
				                   	<input type="hidden" id="creator" name="creator" value="" />
									<input type="text" id="creatorName" name="creatorName" size="30" style="width:30%" value="" readonly/>
									<a href="JavaScript:searchUser('PartDrawingForm','single','creator','creatorName','people')">
										<img src="/Windchill/jsp/portal/images/s_search.gif" border="0" />
									</a>
									<a href="JavaScript:clearText('creator');clearText('creatorName');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
									--%>
									
									<jsp:include page="/eSolution/common/userSearchForm.do">
										<jsp:param value="single" name="searchMode"/>
										<jsp:param value="creator" name="hiddenParam"/>
										<jsp:param value="creatorName" name="textParam"/>
										<jsp:param value="people" name="userType"/>
									</jsp:include>
									
								</td>
								
								<td class="tdblueM">${f:getMessage('상태')}</td>
								<td class="tdwhiteL">
			 						<select name="state" id="state">
									</select>
			             		</td>      
			             	</tr>
			         	</table>
			         	
			   			<div id="SearchDetailProject" style="display: none;" >
				         	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				         	<col width="13%"><col width="37%"><col width="13%"><col width="37%">
				         	
					         	<!-- MODEL, PRODUCTMETHOD -->			
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('프로젝트코드')}
									</td>
									
									<td class="tdwhiteM">
										<select id='model' name='model' style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
										${f:getMessage('제작방법')}
									</td>
									
									<td class="tdwhiteM">
										<select id='productmethod' name='productmethod' style="width: 95%">
										</select>
									</td>
								</tr>
								
								<!-- DEPTCODE, UNIT -->
				              	<tr bgcolor="ffffff" height="35">
				              		<td class="tdblueM">
										${f:getMessage('부서')}
									</td>					
									
									<td class="tdwhiteM">
										<select id="deptcode" name="deptcode" style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
										${f:getMessage('단위')}
									</td>					
									
									<td class="tdwhiteM">
										<select id='unit' name='unit' style="width: 95%">
										</select>
									</td>
								</tr>
				                
								<!-- WEIGHT, MANUFACTURE -->
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('무게')}
									</td>
									
									<td class="tdwhiteM">
										<input id="weight" name="weight" type="text" style="width: 95%;"/>
									</td>
									
									<td class="tdblueM">
										MANUFACTURE
									</td>
									
									<td class="tdwhiteM">
										<select id="manufacture" name="manufacture" style="width: 95%">
										</select>
									</td>
								</tr>
								
								<!-- MAT, FINISH -->
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('재질')}
									</td>
									
									<td class="tdwhiteM">
										<select id="mat" name="mat" style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
										${f:getMessage('후처리')}
									</td>
									
									<td class="tdwhiteM">
										<select id="finish" name="finish" style="width: 95%">
										</select>
									</td>
								</tr>
								
								<!-- REMARKS, SPECIFICATION -->
								<tr bgcolor="ffffff" height="35">
				               		<td class="tdblueM">
										${f:getMessage('비고')}
									</td>
									
									<td class="tdwhiteM">
										<input id="remarks" name="remarks" type="text" style="width: 95%;"/>
									</td>
									
				               		<td class="tdblueM">
										${f:getMessage('사양')}
									</td>
									
									<td class="tdwhiteM">
										<input id="specification" name="specification" type="text" style="width: 95%;"/>
									</td>
								</tr>
				         	
				         	</table>
			         	</div>
			   		</td>
			   	</tr>
			   	
				<tr>
					<td align="right">
						<table border="0" cellpadding="0" cellspacing="4">
							<tr align="right">
				              	
								<td>
									<button type="button" class="btnCRUD" title="${f:getMessage('선택')}" id="selectBtn" name="selectBtn">
					            		<span></span>
				                  		${f:getMessage('선택')}
				                	</button>
								</td>
				              
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
							      	<button type="button" class="btnCustom" title="${f:getMessage('상세검색')}" id="detailBtn" name="detailBtn">
					                  	<span></span>
					                  	${f:getMessage('상세검색')}
				                  	</button> 
							    </td>
							      
							    <td>
								    <button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
			                			<span></span>
			                			${f:getMessage('닫기')}
			                		</button>
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
		</td>
	</tr>
</table>
</form>
</body>
</html>