<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
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
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("activeDateStart", "activeDateStartBtn");
	gfn_InitCalendar("activeDateEnd", "activeDateEndBtn");
	gfn_InitCalendar("finishDateStart", "finishDateStartBtn");
	gfn_InitCalendar("finishDateEnd", "finishDateEndBtn");
	lfn_getStateList('LC_Development');
	
	numberCodeList('model', '', '');
	
	lfn_Search();
});

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList(lifecycle) {
	var url	= getURLString("WFItem", "lifecycleList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			lifecycle : lifecycle
		},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$(".state").find("option").remove();
			$(".state").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$(".state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}

<%----------------------------------------------------------
*                      DocumentType 리스트 가져오기
----------------------------------------------------------%>
window.documentType = function(documentType, searchType) {
	var data = common_documentType('GENERAL', '');
	
	addSelectList('documentType', eval(data.responseText));
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id) {
	var data = common_numberCodeList(id.toUpperCase(), '',true);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].name + "'";
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : ''
			,col02 : 'NO'
			,col03 : '${f:getMessage('프로젝트코드')}'
			,col04 : '${f:getMessage('프로젝트명')}'
			,col05 : '${f:getMessage('프로젝트상태')}'
			,col06 : 'TASK${f:getMessage('명')}'
			,col07 : 'ACTIVITY${f:getMessage('명')}'
			,col08 : '${f:getMessage('업무 부여자')}'
			,col09 : '${f:getMessage('업무 수행자')}'
			,col10 : '${f:getMessage('요청 일자')}'
			,col11 : '${f:getMessage('완료 일자')}'
			,col12 : 'ACTIVITY${f:getMessage('상태')}'
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
	sWidth += "4";
	sWidth += ",3";
	sWidth += ",8";
	sWidth += ",20";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",7";
	sWidth += ",7";
	sWidth += ",7";
	sWidth += ",7";
	sWidth += ",7";
	
	
	var sColAlign = "";
	sColAlign += "center";
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
	sColType += "ch";
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
    /*grid text copy option*/

	
	documentListGrid.attachEvent("onHeaderClick",function(rowId,cellIndex){
		
		var sortValue = "";
		var header = "";
		
		if(rowId == 1) {
			sortValue = "master>number";
			header = COLNAMEARR.col02;
		}else if(rowId == 2 ){
			sortValue = "master>name";
			header = COLNAMEARR.col03;
		}else if(rowId == 6 ) {
			sortValue = "iterationInfo.creator.key.id";
			header = COLNAMEARR.col07;
		}else if(rowId == 7) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col08;
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
			documentListGrid.setColLabel(6, COLNAMEARR.col07);
			documentListGrid.setColLabel(7, COLNAMEARR.col08);
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
	*                      프로젝트 코드 선택시
	----------------------------------------------------------%>
	$("#model").change(function() {
		$("#modelName").html($("#model option:selected").attr("title"));
	})
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		lfn_DhtmlxGridInit();
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}),
	<%----------------------------------------------------------
	*                      초기화 버튼 클릭시
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
	})
	$('#selectBtn').click(function() {
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");

		var returnArr = new Array();
		
		for(var i =0; i < array.length; i++) {
			
			var oid = array[i];															 
   			var model = documentListGrid.cells(array[i], 2).getValue();
   			var modelName = documentListGrid.cells(array[i], 3).getValue();
   			var devState = documentListGrid.cells(array[i], 4).getValue();
   			var taskName = documentListGrid.cells(array[i], 5).getValue();
   			var activityName = documentListGrid.cells(array[i], 6).getValue();
 			var dm = documentListGrid.cells(array[i], 7).getValue();
 			var worker = documentListGrid.cells(array[i], 8).getValue();
 			var activeDate = documentListGrid.cells(array[i], 9).getValue();
 			var finishDate = documentListGrid.cells(array[i], 10).getValue();
 			var activityState = documentListGrid.cells(array[i], 11).getValue();
 			
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = model;
   			returnArr[i][2] = modelName;
   			returnArr[i][3] = devState;
   			returnArr[i][4] = taskName;
   			returnArr[i][5] = activityName;
  			returnArr[i][6] = dm;
  			returnArr[i][7] = worker;
  			returnArr[i][8] = activeDate;
  			returnArr[i][9] = finishDate;
  			returnArr[i][10] = activityState;
		}
		opener.parent.addDevelopment(returnArr);
	})
})

<%----------------------------------------------------------
*                      데이터 검색 AJAX
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=selectDevelopmentForm]").serialize();
	var url	= getURLString("development", "listMyDevelopmentAction", "do");
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

function onExcelDown() {
	//$("#documentForm").attr("method", "post");
	//$("#documentForm").attr("action", getURLString("excelDown", "documentExcelDown", "do")).submit();
}

</script>

<body>
<form name="selectDevelopmentForm" id='selectDevelopmentForm' method="post">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 폴더 선택시 검색 function을 호출하기 위한 변수 -->
<input type="hidden" name="search" id="search" value="true" />

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<!-- select popup 여부 -->
<input type="hidden" name="select" id="select" value='select' />

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<tr align="center">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
						
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<colgroup>
					<col width='10%'>
					<col width='40%'>
					<col width='10%'>
					<col width='40%'>
				</colgroup>
				
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('프로젝트코드')}
					</td>
					<td class="tdwhiteL">
						<select id='model' name='model' style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('프로젝트명')}
					</td>
					<td class="tdwhiteL">
						<span id='modelName'></span>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('프로젝트상태')}
					</td>
					<td class="tdwhiteL">
						<select name="masterState" id="masterState" class='state'>
	                    </select>
					</td>
					
					<td class="tdblueM">
						ACTIVITY${f:getMessage('명')}
					</td>
					
					<td class="tdwhiteL">
						<input name="activeName" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('요청 일자')}
					</td>
					<td class="tdwhiteL">
						<input name="activeDateStart" id="activeDateStart" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="activeDateStartBtn" id="activeDateStartBtn" >
						</a>
						
						<a href="JavaScript:clearText('activeDateStart')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="activeDateEnd" id="activeDateEnd" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="activeDateEndBtn" id="activeDateEndBtn" >
						</a>
						
						<a href="JavaScript:clearText('activeDateEnd')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('완료 일자')}
					</td>
					
					<td class="tdwhiteL">
						<input name="finishDateStart" id="finishDateStart" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="finishDateStartBtn" id="finishDateStartBtn" >
						</a>
						
						<a href="JavaScript:clearText('finishDateStart')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="finishDateEnd" id="finishDateEnd" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="finishDateEndBtn" id="finishDateEndBtn" >
						</a>
						
						<a href="JavaScript:clearText('finishDateEnd')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						ACTIVITY${f:getMessage('상태')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<select name="activeState" id="activeState" class='state'>
	                    </select>
					</td>
				</tr>
				
			</table>
	
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
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
					
					<%-- 
					<td>
						<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
							<span></span>
							${f:getMessage('닫기')}
						</button>
					</td>
					--%>         
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