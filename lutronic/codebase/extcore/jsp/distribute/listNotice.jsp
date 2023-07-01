<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
<%----------------------------------------------------------
*                      페이지 초기설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_DhtmlxGridInit()
	lfn_Search()
})

$(function() {
	<%----------------------------------------------------------
	*                      검색 버튼
	----------------------------------------------------------%>
	$("#search").click(function() {
		resetSearch();
	}),
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#create").click(function() {
		document.location =  getURLString("groupware", "createNotice", "do");
	})
	$(document).keypress(function(event) {
		 if(event.which == 13 && $( '#userNameSearch' ).is( ':hidden' )) {
			 resetSearch();
		 }
	})
})

window.resetSearch = function() {
	lfn_DhtmlxGridInit();
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage('번호')}'
			,col02 : '${f:getMessage('제목')}'
			,col03 : '${f:getMessage('등록자')}'
			,col04 : '${f:getMessage('등록일')}'
			,col05 : '${f:getMessage('조회횟수')}'
			,col06 : '${f:getMessage('팝업')}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";

	var sWidth = "";
	sWidth += "6";
	sWidth += ",44";
	sWidth += ",15";
	sWidth += ",15";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",left";
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

	var sColSorting = "";
	sColSorting += "na";
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

	documentListGrid.init();
}

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function lfn_Search() {
	var form = $("form[name=listNotice]").serialize();
	var url	= getURLString("groupware", "listNoticeAction", "do");
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
	$("#tempcreator").val(obj[0][1]);
}

<%----------------------------------------------------------
*                      공지사항 상세보기
----------------------------------------------------------%>
function gotoView(oid) {
	document.location = getURLString("distribute", "viewNotice", "do") + "?oid="+oid;
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

<form name="listNotice" id="listNotice">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />
<input type="hidden" name="distribute" id="xmlString" value="<c:out value="${distribute }" />" />
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('공지사항')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="10" cellspacing="10" > <!--//여백 테이블-->
	<tr align=center>
		<td>

			<table width="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr  align=center>
					<td valign="top" style="padding:0px 0px 0px 0px">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
							<tr>
								<td height=1 width=100%></td>
							</tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<col width='100'><col><col width='100'><col>
			
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('제목')}</td>
								<td class="tdwhiteL">
									<input type="text" name="name" size="60" class="txt_field" value=""/>
								</td>
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
								
									<jsp:include page="/eSolution/common/userSearchForm.do">
										<jsp:param value="single" name="searchMode"/>
										<jsp:param value="creator" name="hiddenParam"/>
										<jsp:param value="creatorName" name="textParam"/>
										<jsp:param value="people" name="userType"/>
										<jsp:param value="" name="returnFunction"/>
									</jsp:include>
								
									<%-- 
									<input TYPE="hidden" name="creator" id="creator" value="">
									<input type="text"  name="tempcreator" id="tempcreator" value="" class="txt_field" readonly>
			
									<a href="JavaScript:searchUser('documentForm','single','creator','creatorName','people')">
										<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
									</a>
									<a href="JavaScript:clearText('creator');clearText('tempcreator')">
										<img src="/Windchill/jsp/portal/images/x.gif" border=0>
									</a>
									--%>
									
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr height=35>
					<td align="right">
						<table>
							<tr>
								<td>
									<button type="button" class="btnSearch" id="search" name="search">
										<span></span>
										${f:getMessage('검색')}
									</button>
								</td>
								<!--  
								<c:if test="${isAdmin }">
									<td>
										<button type="button" class="btnCRUD" id="create" name="create">
											<span></span>
											${f:getMessage('등록')}
										</button>
									</td>
								</c:if>
								-->
								<td>
									<button type="Reset" class="btnCustom" id="reset" name="reset">
										<span></span>
										${f:getMessage('초기화')}
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