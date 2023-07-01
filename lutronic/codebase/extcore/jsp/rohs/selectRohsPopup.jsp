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
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	lfn_getStateList();
	
	numberCodeList('manufacture', '');
	
	numberCodeList('model', '');
	numberCodeList('deptcode', '');
	numberCodeList('preseration', '');
	
	lfn_Search();
});

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id) {
	var data = common_numberCodeList(id.toUpperCase(), '', true);
	
	addSelectList(id, eval(data.responseText), '');
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data,searchType){
	$("#"+ id + " option").remove();
	if(searchType == '') {
		$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	}
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "'>";
			
			if(id != 'documentType') {
				html += " [" + data[i].code + "] ";
			} 
			html += data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '<input type=checkbox name=allCheck id=allCheck>'
			,col02 : '${f:getMessage('번호')}'
			,col03 : '${f:getMessage('물질')}${f:getMessage('번호')}'
			,col04 : '${f:getMessage('협력업체')}'
			,col05 : '${f:getMessage('물질명')}'
			,col06 : '${f:getMessage('Rev.')}'
			,col07 : '${f:getMessage('상태')}'
			,col08 : '${f:getMessage('등록자')}'
			,col09 : '${f:getMessage('등록일')}'
			,col10 : '${f:getMessage('수정일')}'
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

	var sWidth = "";
	sWidth += "3";
	sWidth += ",3";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",24";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",left";
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
		
		if(rowId == 2) {
			sortValue = "master>number";
			header = COLNAMEARR.col03;
		}else if(rowId == 3 ){
			sortValue = "manufacture";
			header = COLNAMEARR.col04;
		}else if(rowId == 4 ){
			sortValue = "master>name";
			header = COLNAMEARR.col05;
		}else if(rowId == 7 ) {
			sortValue = "iterationInfo.creator.key.id";
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

			documentListGrid.setColLabel(2, COLNAMEARR.col03);
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

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList() {
	var url	= getURLString("WFItem", "lifecycleList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{
			state : $("#stateValue").val(),
			lifecycle : $("#lifecycle").val()
		},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#state").find("option").remove();
			
			if($("#stateValue").val() == "") {
				$("#state").append("<option value=''>${f:getMessage('선택')}</option>");
			}
			
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
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
		var type = "<c:out value='${type}'/>";
		
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");

		if(type == "select") {
		
			var returnArr = new Array();
			
			for(var i =0; i < array.length; i++) {
				
				var oid = array[i];															 // OID
	   			var number = documentListGrid.cells(array[i], 2).getValue();				 // 문서번호
	   			var name = documentListGrid.cells(array[i], 3).getValue();				 // 품명
	   			var version = documentListGrid.cells(array[i], 4).getValue();				 // 버전
	   			var state = documentListGrid.cells(array[i], 5).getValue();	 		 // 상태
	   			var creator = documentListGrid.cells(array[i], 6).getValue();     // 등록자
	 			var createDate = documentListGrid.cells(array[i], 7).getValue();			 	 // 등록일
	  			var updateDate = documentListGrid.cells(array[i], 8).getValue();			 	 // 수정일
	  				
	  			returnArr[i] = new Array();
	   			returnArr[i][0] = oid;
	   			returnArr[i][1] = number;
	   			returnArr[i][2] = name;
	   			returnArr[i][3] = version;
	   			returnArr[i][4] = state;
	   			returnArr[i][5] = creator;
	  			returnArr[i][6] = createDate;
	  			returnArr[i][7] = updateDate;
			}
			opener.parent.addRohs(returnArr);
		}else if(type == "link") {
			var docOid = array[0];
			opener.set_Document(docOid,"<c:out value='${outputOid}'/>");
		}
		//self.close();
	}),
	<%----------------------------------------------------------
	*                      죄측 메뉴 숨기기/보이기
	----------------------------------------------------------%>
	$("#hiddenArrow").click(function () {
	    if($("#subMenu_table").css('display') == 'none') {
	    	$("#subMenu_table").show();
	    	$("#subMenu_table_h").hide();
	    	$("#hiddenArrow").attr('src', "/Windchill/jsp/portal/images/base_design/layer_hidden.gif");
	    	$("#hiddenArrow").attr('title', "${f:getMessage('서브메뉴')}${f:getMessage('닫기')}");
	    }else {
	    	$("#subMenu_table").hide();
	    	$("#subMenu_table_h").show();
	    	$("#hiddenArrow").attr('src', "/Windchill/jsp/portal/images/base_design/layer_show.gif");
	    	$("#hiddenArrow").attr('title', "${f:getMessage('서브메뉴')}${f:getMessage('열기')}");
	    }
	    

	    if($("#xmlString").val() != null) {
	    	redrawGrid();
	    }
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
*                      데이터 검색 AJAX
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=selectRohsForm]").serialize();
	var url	= getURLString("rohs", "listRohsAction", "do") + "?select=true";
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
*                      선택 폴더 설정
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
<form name="selectRohsForm" method="post">
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

<input type="hidden" name="fid" 			id="fid"					value="">
<input type="hidden" name="location"		id="location" 				value="/Default/Document">

<input type="hidden" name="stateValue"		id="stateValue"				value="<c:out value='${state }'/>" />
<input type="hidden" name="searchType" 		id="searchType" 			value="<c:out value='${searchType }'/>" />
<input type="hidden" name="lifecycle" 		id="lifecycle" 				value="<c:out value='${lifecycle }'/>" />

<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
	   	<td width=100% valign="top">
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
								<td class="tdblueM">${f:getMessage('물질명')}</td>
								<td class="tdwhiteL" colspan="3">
									<input name="rohsName" class="txt_field" size="30" style="width:90%" value=""/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('물질번호')}</td>
								<td class="tdwhiteL">
									<input name="rohsNumber" class="txt_field" size="30" style="width:90%" value=""/>
								</td>
								<td class="tdblueM">${f:getMessage('협력업체')}</td>
								<td class="tdwhiteL">
									<select id='manufacture' name='manufacture' style="width: 95%"> 
									</select>
								</td>
							</tr>
						
							<tr>
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
									<input name="predate" id="predate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="predateBtn" id="predateBtn" ></a>
									<a href="JavaScript:clearText('predate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
									~
									<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postdateBtn" id="postdateBtn" ></a>
									<a href="JavaScript:clearText('postdate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
								</td>
								<td class="tdblueM">${f:getMessage('수정일')}</td>
								<td class="tdwhiteL">
									<input name="predate_modify" id="predate_modify" class="txt_field" size="12"  maxlength=15 readonly  value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="predate_modifyBtn" id="predate_modifyBtn" ></a>
									<a href="JavaScript:clearText('predate_modify')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
									~
									<input name="postdate_modify" id="postdate_modify" class="txt_field" size="12"  maxlength=15 readonly  value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postdate_modifyBtn" id="postdate_modifyBtn" ></a>
									<a href="JavaScript:clearText('postdate_modify')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
								
									<%-- 
									<input TYPE="hidden" name="creator" value="">
									<input type="text" name="creatorName" value="" readonly>
			
									<a href="JavaScript:searchUser('selectRohsForm','true','creator','creatorName','people')">
										<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
									</a>
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
								<td class="tdwhiteL" >
				                    <select name="state" id="state">
				                    </select>
			                    </td>
							</tr>
						</table>
						
						<%-- 
						<div id="SearchDetailProject" style="display: none;" >
							<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
								<colgroup>
									<col width='10%'>
									<col width='40%'>
									<col width='10%'>
									<col width='40%'>
								</colgroup>
								
								<tr bgcolor="ffffff" height="35">
								
									<td class="tdblueM">
										${f:getMessage('문서 종류')}
									</td>
									
									<td class="tdwhiteL" >
										<select id='documentType' name='documentType' style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
										${f:getMessage('프로젝트코드')}
									</td>
									
									<td class="tdwhiteL" >
										<select id='model' name='model' style="width: 95%">
										</select>
									</td>
								</tr>
								
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('내부 문서번호')}
									</td>
									
									<td class="tdwhiteL">
										<input name="interalnumber" id="interalnumber" class="txt_field" size="85" style="width:90%" maxlength="80" />
									</td>
									
									<td class="tdblueM">
										${f:getMessage('부서')}
									</td>
									
									<td class="tdwhiteL">
										<select id='deptcode' name='deptcode' style="width: 95%">
										</select>
									</td>
								</tr>
								
							</table>
						</div>
						--%>
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
							      	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('상세검색')}" id="detailBtn" name="detailBtn">
					                  	<span></span>
					                  	${f:getMessage('상세검색')}
				                  	</button> 
							    </td>
							    --%>
							    
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