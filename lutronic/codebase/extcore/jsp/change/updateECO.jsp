<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
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
	numberCodeList('LICENSING', '','<c:out value="${ecoData.licensing}"/>');
	
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	var type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText),value);
	
	setRiskType();
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
function addSelectList(type,data,value){
	var id ="licensing";
	$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value=''> 해제");
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
	}
	
	setSelectList(id, value);
}

<%----------------------------------------------------------
*               기존 데이터 selectBodx에 옵션 추가
----------------------------------------------------------%>
function setSelectList(id, value){
	var valueArr = value.split(',');
	
	
		$("input:radio[id='" + id +"']").each(function() {		
			if(this.value == valueArr[0]) {
				this.checked = true;
			}
		})
	
}

<%----------------------------------------------------------
*               기존 데이터 selectBodx에 옵션 추가
----------------------------------------------------------%>
function setRiskType(){
	var riskType = '<c:out value="${ecoData.getRiskType()}"/>';

	$("input:radio[id='riskType']").each(function() {
		//consloe.log("this.value =" + this.value);
		if(this.value == riskType) {
			this.checked = true;
		}
		
	})
}

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		if($.trim($("#name").val()) == "") {
			alert("${f:getMessage('제목')} ${f:getMessage('을(를) 입력하세요.')}");
			$("#name").focus();
			return false;
		}
		
		/**
		if($("input[name='model']").size() == 0 ){
			alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#completeOid").size()) == "") {
			alert("${f:getMessage('완제품 품번')} ${f:getMessage('을(를) 선택하세요.')}");
			return;		
		}
		**/
		if(!textAreaLengthCheckId('eoCommentA','3000','${f:getMessage("변경 사유")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentB','3000','${f:getMessage("변경 사항")}')){
			return;
		}
		
		if($.trim($("#partOid").size()) == "") {
			alert("${f:getMessage('설계변경 부품')} ${f:getMessage('을(를) 선택하세요.')}");
			return;		
		}
		
		if(!textAreaLengthCheckId('eoCommentC','3000','${f:getMessage("특기사항")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentD','3000','${f:getMessage("기타사항")}')){
			return;
		}
		
		if(!checkActivity()){
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
	});
	$("#download").click(function() {
		location ="/Windchill/jsp/change/ECO_PART.xlsx";
	});
	
})


<%----------------------------------------------------------
*            			관련 ECR select
----------------------------------------------------------%>
function selectECR(){
	var url = getURLString("changeECR", "selectECRPopup", "do");
	openOtherName(url,"selectECRPopup","1180","880","status=no,scrollbars=yes,resizable=yes");
}

<%----------------------------------------------------------
*                      관련 ECR 추가
----------------------------------------------------------%>
function addECR(obj) {
	$("#ecrOid").val(obj[0][0]);
	$("#ecrNumber").val(obj[0][1]);
	
	
}
</script>

<body>
<form name="updateEOForm" id="updateEOForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="oid" id="oid" value="<c:out value="${ecoData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="update" />
<input type="hidden" name="eoType" id="eoType" value="CHANGE">
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
					<td >
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						ECO
						${f:getMessage('수정')}
					</td>
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
						<col width="13%"><col width="37%"><col width="13%"><col width="37%">
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
									CR/ECPR ${f:getMessage('제목')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<input name="name" id="name" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecoData.name }"/>"/>
								</td>
								
								
							</tr>
							<tr>	
								<td class="tdblueM">
									${f:getMessage('관련')}CR/ECPR
								</td>
								<td class="tdwhiteL" colspan="3">
								<jsp:include page="/eSolution/changeECR/include_ECRSelect.do" flush="false" />
									
								</td>
				             </tr>
							
							<!--  
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">제품명<span class="style1">*</span></td>
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
								<td class="tdblueM">${f:getMessage('완제품 품목')}<span style="color:red;">*</span></td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/part/include_CompletePartSelect.do" flush="false" >
										<jsp:param name="moduleType" value="CompletePart"/>
										<jsp:param name="mode" value="mutil" />
										<jsp:param name="paramName" value="CompletePartOid"/>
										<jsp:param name="form" value="simple" />
										<jsp:param name="isBom" value="false" />
										<jsp:param name="selectBom" value="false" />
										
									</jsp:include>
								</td>
							</tr>
							-->
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('변경 사유')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentA" id="eoCommentA" rows="10" onchange="textAreaLengthCheckName('eoCommentA', '4000', '${f:getMessage('변경 사유')}')"><c:out value="${ecoData.commentA  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('변경 사항')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentB" id="eoCommentB" rows="10" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('변경 사항')}')"><c:out value="${ecoData.commentB  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
									${f:getMessage('설계변경 부품')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
										<jsp:param name="moduleType" value="ECO"/>
										<jsp:param name="mode" value="mutil" />
										<jsp:param name="paramName" value="partOid"/>
										<jsp:param name="form" value="simple" />
										<jsp:param name="isBom" value="true" />
										<jsp:param name="selectBom" value="true" />
										
									</jsp:include>
									<br>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
									${f:getMessage('인허가변경')}
								</td>
								
								<td class="tdwhiteL" >
									<div id='licensing'></div>	
								</td>
								<td class="tdblueM">
									${f:getMessage('위험 통제')}
								</td>
								
								<td class="tdwhiteL" >
									<input type='radio' name="riskType" id="riskType" value=''> 해제	
									<input type='radio' name="riskType" id="riskType" value='0' > 불필요
									<input type='radio' name="riskType" id="riskType" value='1'> 필요 
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('특기사항')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentC" id="eoCommentC" rows="5" onchange="textAreaLengthCheckName('eoCommentC', '4000', '${f:getMessage('특기사항')}')"><c:out value="${ecoData.commentC  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('기타사항')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentD" id="eoCommentD" rows="5" onchange="textAreaLengthCheckName('eoCommentD', '4000', '${f:getMessage('기타사항')}')"><c:out value="${ecoData.commentD  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('설계변경 부품 내역파일')}
									<button type="button" class="btnCustom" id="download">
       									<span></span>
       									${f:getMessage('양식다운')}
       								</button>
       							</td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="updateEOForm"/>
										<jsp:param name="type" value="ECO"/>
										<jsp:param name="oid" value="${ecoData.oid }"/>
										<jsp:param name="btnId" value="updateBtn" />
									</jsp:include>
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