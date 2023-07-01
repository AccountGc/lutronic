<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/eo.js" ></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	setEoType();
})


$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
			if($.trim($("#name").val()) == "" ) {
				alert("CR/ECPR ${f:getMessage('제목')}${f:getMessage('을(를) 입력하세요.')}");
				$('#name').focus();
				return;		
			}
			
			if($("input[name='model']").size() == 0 ){
				alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
				return;
			}
			
			if($.trim($("#completeOid").val()) == "") {
				alert("${f:getMessage('완제품 품번')} ${f:getMessage('을(를) 선택하세요.')}");
				return;		
			}
			
			if(!textAreaLengthCheckId('eoCommentA','2000','${f:getMessage("제품 설계 개요")}')){
				return;
			}
			
			if(!textAreaLengthCheckId('eoCommentB','2000','${f:getMessage("특기사항")}')){
				return;
			}
			
			if(!textAreaLengthCheckId('eoCommentC','2000','${f:getMessage("기타사항")}')){
				return;
			}
			
			if(!checkActivity()){
				return;
			}
			
			if(!compeletPartCheck()){
				return;
			}
			
			if(confirm("${f:getMessage('수정 하시겠습니까?')}")){
				
				var form = $("form[name=updateEOForm]").serialize();
				var url	= getURLString("changeECO", "updateEOAction", "do");
				$.ajax({
					type:"POST",
					url: url,
					data:form,
					dataType:"json",
					async: true,
					cache: false,
					error:function(data){
						var msg = "${f:getMessage('등록 오류')}";
						alert(msg);
					},
	
					success:function(data){
						if(data.result) {
							alert("${f:getMessage('수정 성공하였습니다.')}");
							location.href = getURLString("changeECO", "viewECO", "do") + "?oid="+data.oid;
						}else {
							alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
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
			//$("#updateEOForm").attr("action" , getURLString("changeECR", "updateECRAction", "do") + "?oid=" + $("#oid").val()).submit();
	})
	
	<%----------------------------------------------------------
	*                      제품  추가 버튼
	----------------------------------------------------------%>
	$("#addNumberCode").click(function() {
		
		var url = getURLString("common", "popup_numberCodeList", "do")+ "?codeType=MODEL";
		openOtherName(url,"selectCodePopup","800","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      제품  삭제 버튼
	----------------------------------------------------------%>
	$("#delNumberCode").click(function() {
		
		delteNumberCode();
	})
	
})

function setEoType(){
	
	var eoType = "<c:out value="${ecoData.eoType }" />";
	
	var obj = $("input[name='eoType']");
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
		
		if(obj.eq(i).val() == eoType) {
			
			obj.eq(i).attr("checked",true);
		}else{
			obj.eq(i).attr("disabled",true);
		}
	}
	
}

function compeletPartCheck(){
	
	var obj = $("input[name='completeNumber']");
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
		
		if(!partCheck(obj.eq(i).val())){
			return false;
		}
	}
	
	return true;
}
/**************************************************************
*                       EO 등록시 EO 구분에 따른 완제품 품목 조건
****************************************************************/
function partCheck(number){
	
	//DEV 1인 아닌 부품,PRODUCT 1인 부품 
	//partNumberCheck eo.js
	if(!partNumberCheck(number)|| number.length !=10){
		alert(number +"${f:getMessage('가채번 ,더미 ,제품군 부품은 선택 할수 없습니다.')}" )
		return false;
	}
	
	var eoType = $("input[name=eoType]:checked").val();
	var firstNumber = number.substring(0,1)
	//DEV 1인 아닌 부품,PRODUCT 1인 부품 
	if(eoType == "DEV"){
		if(firstNumber=="1"){
			alert("${f:getMessage('개발 EO는 는 완제품을 선택 할수 없습니다.')}" )
			return false;
		}
	}else{
		if(firstNumber !="1"){
			alert("${f:getMessage('양산 EO는 는 완제품만 선택할수 있습니다.')}" )
			return false;
		}
	}
	
	return true;
	
}

//*************** 체크박스용 시작 ***************//
//checked - 체크 여부, name - 객체 이름 
function f_checkbox(checked, name) {
  document.getElementById(name).checked = !checked;
}
</script>

<body>
<form name="updateEOForm" id="updateEOForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="oid" id="oid" value="<c:out value="${ecoData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="update" />
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td ><img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />&nbsp;EO ${f:getMessage('수정')}</td>
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom" onclick="javascript:history.back();">
										<span></span>
										${f:getMessage('이전페이지')}
									</button>
								</td>
							</tr>
			            </table>
			            <!-- 버튼 테이블 끝 -->
					</td>
				</tr>
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr><td height="1" width="100%"></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
						<col width="10%"><col width="40%"><col width="10%"><col width="40%">
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
									CR/ECPR${f:getMessage('제목')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<input name="name" id="name" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecoData.name }"/>"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM" >
									EO${f:getMessage('구분')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" >
									<input type="radio" name=eoType value="DEV" >${f:getMessage('개발')}
									<input type="radio" name=eoType value="PRODUCT" >${f:getMessage('양산')}
								</td>
							
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('제품명')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<table border="0" cellpadding="0" cellspacing="2" width="100%">
										<tr align="left">
											<td>
												<button type="button" name="addNumberCode" id="addNumberCode" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
												
												<button type="button" name="delNumberCode" id="delNumberCode" class="btnCustom">
													<span></span>
													${f:getMessage('삭제')}
												</button>
											</td>
										</tr>
										
										<tr>
											<td>
												<div id='modeltable'>
													<c:forEach items="${ecoData.getModelCode()}" var="code">
														<div id='<c:out value="${code.getCode()}"/>' style='float: left'>
													   		<input type='checkbox' name='modelcode' id='modelcode' value='<c:out value="${code.getCode()}"/>'>
													   		<input type='hidden' name='model' id='model' value='<c:out value="${code.getCode()}" />'>
													  		<c:out value="${code.getName() }" />
													   </div>
													</c:forEach>
												</div>	
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
									${f:getMessage('완제품 품목')}
									<span style="color:red;">*</span>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/part/include_CompletePartSelect.do" flush="false" >
										<jsp:param name="moduleType" value="EO"/>
										<jsp:param name="mode" value="mutil" />
										<jsp:param name="paramName" value="CompletePartOid"/>
										<jsp:param name="form" value="simple" />
										<jsp:param name="isBom" value="false" />
										<jsp:param name="selectBom" value="false" />
									</jsp:include>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('제품 설계 개요')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentA" id="eoCommentA" rows="5" onchange="textAreaLengthCheckName('eoCommentA', '4000', '${f:getMessage('제품 설계 개요')}')"><c:out value="${ecoData.commentA  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('특기사항')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentB" id="eoCommentB" rows="5" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('특기사항')}')"><c:out value="${ecoData.commentB  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('기타사항')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentC" id="eoCommentC" rows="5" onchange="textAreaLengthCheckName('eoCommentC', '4000', '${f:getMessage('기타사항')}')"><c:out value="${ecoData.commentC  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('첨부파일')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="updateEOForm"/>
										<jsp:param name="oid" value="${ecoData.oid }"/>
									</jsp:include>
								</td>
							</tr>
							<!-- 활동 현황 추가 -->
							<jsp:include page="/eSolution/changeECA/include_CreateActivity.do" flush="false" />
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