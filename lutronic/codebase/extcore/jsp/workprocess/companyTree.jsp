<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	lfn_Search();
})

<%----------------------------------------------------------
*                      부서 선택시
----------------------------------------------------------%>
function lfn_DeptTreeOnClick(id){
	var text   = deptTree.getAttribute(id, "text");
	var oid    = deptTree.getAttribute(id, "oid");
	$("#deptName").html(text);
	$("#oid").val(oid);
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

$(function() {
	<%----------------------------------------------------------
	*                      검색 버튼
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	})
	
	$("#name").keypress(function(event) {
		 if( event.which == 13) {
			 $("#sessionId").val("");
			 $("#page").val(1);
			 lfn_Search();
		 }
	})
})


<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage('번호')}'
			,col02 : '${f:getMessage('아이디')}'
			,col03 : '${f:getMessage('이름')}'
			,col04 : '${f:getMessage('부서')}'
			,col05 : '${f:getMessage('직위')}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	
	var sWidth = "";
	sWidth += "7";
	sWidth += ",53";
	sWidth += ",15";
	sWidth += ",15";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",left";
	sColAlign += ",left";
	sColAlign += ",left";
	sColAlign += ",left";
	
	var sColType = "";
	sColType += "ro";
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
	var form = $("form[name=companyTreeForm]").serialize();
	var url	= getURLString("user", "companyTreeSearch", "do");
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
*                      사용자 선택시 비밀번호 변경
----------------------------------------------------------%>
function changePassword(id) {
	var url = getURLString("groupware", "changePassword", "do") + "?isPop=true&id=" + id;
	window.open(url, "passwordchange" , "status=no, width=700px, height=250px, resizable=no, scrollbars=no");
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

<form name="companyTreeForm" id="companyTreeForm" method="get">
<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- Input text가 하나이면 Enter 입력시  submit 되는 현상을 방지하기 위한 text -->
<input type="text" style="display: none;"/>

<input type="hidden" name="oid" id="oid" value="" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('조직도')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<colgroup>
		<col width="20%">
		<col width="80%">
	</colgroup>
	
	<tbody>
		<tr>
			<td valign="top">
				<jsp:include page="/eSolution/department/treeDepartment.do">
					<jsp:param value="ROOT" name="code"/>
				</jsp:include>
			</td>
			
			<td valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<table width="95%" cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td><br></td>
								</tr>
							</table>
							
							<table width="96%" height="40" align="center" border="0">
								<tr>
									<td>
										<table border="0" cellpadding="0" cellspacing="0" width="100%">
											<tr>
												
												<td>
													<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
												
													${f:getMessage('부서 및 사원 관리')}
													<b>
													[
													<span id="deptName">
														LUTRONIC
													</span>
													]
													</b>
												</td>
												
												<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
												
												<c:if test="${isAdmin }">
												
													<td>
														<b><font color="red">* ${f:getMessage('아이디를 클릭하면 비밀번호를 변경할 수 있습니다.')}</font></b>
													</td>
												
												</c:if>
												
												<td align="right">
													${f:getMessage('이름')} : <input type="text" name="name" id="name" class="txt_field" value=""/>
												</td>
												
												<td align="right">
													<button type="button" name="searchBtn" id="searchBtn" class="btnSearch">
														<span></span>
														${f:getMessage('검색')}
													</button>
												</td>
											</tr>
											
											<tr>
												<td colspan="7">
													<div id="listGridBox" style="width:100%;" ></div>
												</td>
											</tr>
											
											<tr>
												<td colspan="7">
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
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</tbody>
	
</table>

</form>
</body>
</html>