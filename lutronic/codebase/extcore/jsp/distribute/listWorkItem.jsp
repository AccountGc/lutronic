<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
});

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage('구분')}'
			,col02 : '${f:getMessage('활동')}' 						// 문서번호
			,col03 : '${f:getMessage('제목')}'						// 문서제목
			,col04 : '${f:getMessage('등록자')}'								// 상태
			,col05 : '${f:getMessage('상태')}'								// 버전
			,col06 : '${f:getMessage('수신일')}'								// 등록자
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
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";

	var sWidth = "";
	sWidth += "15";
	sWidth += ",10";
	sWidth += ",45";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",left";
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

$(function(){
	<%----------------------------------------------------------
	*                      새로고침 버튼 클릭시
	----------------------------------------------------------%>
	$("#refreshBtn").click(function (){
		resetSearch();
	})
	<%----------------------------------------------------------
	*                      검색 버튼
	----------------------------------------------------------%>
	$("#searchItem").click(function() {
		resetSearch();
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
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=workListForm]").serialize();
	var url	= getURLString("distribute", "listWorkItemAction", "do");
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
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	documentListGrid.clearAll();
	documentListGrid.loadXMLString($("#xmlString").val());
}

<%----------------------------------------------------------
*                      결재 상세화면 이동
----------------------------------------------------------%>
function gotoView(url, oid, viewOid){
	var params = url.split("&");
    var urls = params[params.length - 1].split("=");
    var url = getURLString("distribute", "approval", "do") + "?action=" + urls[urls.length - 1] + "&workoid=" + oid + "&pboOid=" + viewOid;
    location.href = url;
}

<%----------------------------------------------------------
*                      유저 데이터 설정
----------------------------------------------------------%>
function addUser(obj) {
	$("#userOid").val(obj[0][0]);
	$("#tempnewUser").val(obj[0][1]);
}
</script>

<body>

<form method="post" name='workListForm'>

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="15"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>
<input type="hidden" name="distribute"    id="distribute"     value="true"/>
<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; 
							${f:getMessage('나의 업무')} > ${f:getMessage('작업함')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="1"  >
				<tr>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
				              <tr>
				              		<c:if test="${isAdmin }">
					              	    <td class="tdwhiteL" width="300px">
					              	    
					              	    	<div style='float: left; margin-top: 5px;'>
						              	    	${f:getMessage('작업자')} : 
						              	    </div>
						              	    
						              	    <div style="margin-left: 5px; float: left;">
						              	    	<jsp:include page="/eSolution/common/userSearchForm.do">
													<jsp:param value="single" name="searchMode"/>
													<jsp:param value="userOid" name="hiddenParam"/>
													<jsp:param value="tempnewUser" name="textParam"/>
													<jsp:param value="people" name="userType"/>
													<jsp:param value="" name="returnFunction"/>
												</jsp:include>
					              	    	</div>
					              	    	<%-- 
											<input type='text' name='tempnewUser' id='tempnewUser' readonly size=10 class="txt_field" size="6">
											<input type='hidden' name='userOid' id='userOid' value=''>&nbsp;
											<a href="JavaScript:searchUser('workListForm','single','userOid','tempnewUser','people')">
												<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
											</a>
											--%>
										</td>
										
										<td>
											<button type="button" id="searchItem" name="searchItem" class="btnSearch" >
												<span></span>
												${f:getMessage('검색')}
											</button>
										</td>
									</c:if>
									<td>
										<button type="button" id="refreshBtn" name="refreshBtn" class="btnCustom" >
											<span></span>
											${f:getMessage('새로고침')}
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