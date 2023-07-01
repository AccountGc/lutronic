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
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	lfn_getStateList();
	lfn_getCadDivisionList();
	lfn_getCadTypeList();
	lfn_Search();
})

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR;
	
	if('${mode}' == 'multi') {
		COLNAMEARR = {
				col01 : '<input type=checkbox name=allCheck id=allCheck>'
				,col02 : '${f:getMessage('번호')}'
				,col03 : 'CAD ${f:getMessage('구분')}'
				,col04 : '${f:getMessage('도면')}${f:getMessage('번호')}'
				,col05 : ''
				,col06 : '${f:getMessage('도면명')}'
				,col07 : '${f:getMessage('도면분류')}'
				,col08 : 'Rev.'
				,col09 : '${f:getMessage('상태')}'
				,col10 : '${f:getMessage('등록자')}'
				,col11 : '${f:getMessage('등록일')}'
				,col12 : '${f:getMessage('수정일')}'
		}
	}else {
		COLNAMEARR = {
				col01 : ''
				,col02 : '${f:getMessage('번호')}'
				,col03 : 'CAD ${f:getMessage('구분')}'
				,col04 : '${f:getMessage('도면')}${f:getMessage('번호')}'
				,col05 : ''
				,col06 : '${f:getMessage('도면명')}'
				,col07 : '${f:getMessage('도면분류')}'
				,col08 : 'Rev.'
				,col09 : '${f:getMessage('상태')}'
				,col10 : '${f:getMessage('등록자')}'
				,col11 : '${f:getMessage('등록일')}'
				,col12 : '${f:getMessage('수정일')}'
		}
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
	sHeader += "," + COLNAMEARR.col12;
	
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

	var sWidth = "";
	sWidth += "3";
	sWidth += ",3";
	sWidth += ",7";
	sWidth += ",10";
	sWidth += ",5";
	sWidth += ",16";
	sWidth += ",15";
	sWidth += ",8";
	sWidth += ",8";
	sWidth += ",9";
	sWidth += ",8";
	sWidth += ",8";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	var sColType = "";
	if('${mode}' == 'multi') {
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
		
		if(rowId == 3) {
			sortValue = "master>number";
			header = COLNAMEARR.col04;
		}else if(rowId == 5 ){
			sortValue = "master>name";
			header = COLNAMEARR.col06;
		}else if(rowId == 8 ) {
			sortValue = "creator";
			header = COLNAMEARR.col09;
		}else if(rowId == 9 ) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col10;
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
			documentListGrid.setColLabel(5, COLNAMEARR.col06);
			documentListGrid.setColLabel(8, COLNAMEARR.col09);
			documentListGrid.setColLabel(9, COLNAMEARR.col10);
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
	var form = $("form[name=selectDrawingForm]").serialize();
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
					if(data[i].value == "ORCAD" || data[i].value == "SOLIDWORKS" || data[i].value == "ACAD") {
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
	var form = $("form[name=selectDrawingForm]").serialize();
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
			$("#cadType").append("<option value=''>선택</option>");
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
			
			var oid = array[i];															 // OID
   			var cadType = documentListGrid.cells(array[i], 2).getValue();				 // CAD구분
   			var number = documentListGrid.cells(array[i], 3).getValue();				 // 도면 번호
   			//var thum = documentListGrid.cells(array[i], 4).getValue();				 	 // thum
   			var name = documentListGrid.cells(array[i], 5).getValue();	 		 		 // 도면 명
   			var version = documentListGrid.cells(array[i], 6).getValue();     			// 버전
 			var state = documentListGrid.cells(array[i], 7).getValue();			 	 // 상태
 			var creator = documentListGrid.cells(array[i], 8).getValue();			 	 // 등록자
 			var createDate = documentListGrid.cells(array[i], 9).getValue();			 	 // 등록자
 			var updateDate = documentListGrid.cells(array[i], 10).getValue();			 	 // 등록자
  				
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = cadType;
   			returnArr[i][2] = number;
   			returnArr[i][3] = name;
   			returnArr[i][4] = version;
   			returnArr[i][5] = state;
  			returnArr[i][6] = creator;
  			returnArr[i][7] = createDate;
  			returnArr[i][8] = updateDate;
  			//returnArr[i][9] = thum;
		}
		opener.parent.addDrawing(returnArr);
		//self.close();
	})
	<%----------------------------------------------------------
	*                      서브메뉴 숨기기/펼치기
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
		this.checked = true;
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck checked>");
		var rows = documentListGrid.getRowsNum();
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			documentListGrid.cellById(rowId, 0).setChecked(true);
		}
	}else {
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck>");
		var rows = documentListGrid.getRowsNum();
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			documentListGrid.cellById(rowId, 0).setChecked(false);
		}
	}
})

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=selectDrawingForm]").serialize();
	var url	= getURLString("drawing", "listDrawingAction", "do") + "?select=true";
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
	var form = $("form[name=selectDrawingForm]").serialize();
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
*                      폴더 설정
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
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	documentListGrid.clearAll();
	documentListGrid.loadXMLString($("#xmlString").val());
}

</script>
<body>
<form name="selectDrawingForm" method="post" style="padding:0px;margin:0px" >

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

<input type="hidden" name="lifecycle"	 id="lifecycle"  	value="LC_PART">
<input type="hidden" name="fid" 		 id="fid" 	 	 	value="" />
<input type="hidden" name="location" 	 id="location" 		value="/Default/PART_Drawing" />

<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td valign=top width=0 background="/Windchill/jsp/portal/images/ds_sub.gif" bgcolor=ffffff >
            <table id="subMenu_table" width="180" style="display:block" height="100%" border="0" cellpadding="0" cellspacing="0">
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
	                    <a href="javascript:void(0);" >
	                    <img id="hiddenArrow" src="/Windchill/jsp/portal/images/base_design/layer_hidden.gif" border=0 title="${f:getMessage('서브메뉴')} ${f:getMessage('닫기')}" />
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
			                        <input type=text  name="creatorName" id="tempcreator" size="30" style="width:30%" value="" class="txt_field">
			
			                        <a href="JavaScript:selectUser(document.selectDrawingForm.creator, document.selectDrawingForm.creatorName)"><img src="/Windchill/jsp/portal/images/s_search.gif" border=0></a>
			                        <a href="JavaScript:clearText('creator');clearText('creatorName')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
			                        --%>
			                        
			                        <jsp:include page="/eSolution/common/userSearchForm.do">
										<jsp:param value="single" name="searchMode"/>
										<jsp:param value="creator" name="hiddenParam"/>
										<jsp:param value="creatorName" name="textParam"/>
										<jsp:param value="people" name="userType"/>
										<jsp:param value="" name="returnFunction"/>
									</jsp:include>
			                        
			                    </td>					
			                    <td class="tdblueM">${f:getMessage('상태')}</td>
			                   	<td class="tdwhiteL">
			                   		<select name=state id="state">
									</select>
			                   	</td>					
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="right">
						<table border="0" cellpadding="0" cellspacing="4">
							<tr>
			                  	<td>
			                  	 	<button type="button" class="btnCRUD" title="${f:getMessage('선택')}" id="selectBtn" name="selectBtn">
					                  	<span></span>
					                	${f:getMessage('선택')}
				                 	</button>
				              	</td>
				               	  
			                	<td>
				                  	<button type="button" class="btnSearch" title="검색" id="searchBtn" name="searchBtn">
					                  	<span></span>
					                  	${f:getMessage('검색')}
				                  	</button>   
			                  	</td>
			                  	
			                  	<td>
			                 	 	<button title="Reset" class="btnCustom" type="reset" id="btnReset">
			                 	 		<span></span>
			                 	 		${f:getMessage('초기화')}
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