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
	numberCodeList('CHANGESECTION', '','<c:out value="${ecrData.changeSection}"/>');
	//var modelData  = '<c:out value="${ecrData.getModelCode()}"/>';
	//alert(modelData);
	
	
	gfn_InitCalendar("createDate", "createDateBtn");
	gfn_InitCalendar("approveDate", "approveDateBtn");
	
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	var type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText),value);
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
function addSelectList(type,data,value){
	var id ="changeSection";
	
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='checkbox' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
	}
	setSelectList(id, value);
	
	
	
	
}

<%----------------------------------------------------------
*               기존 데이터 selectBodx에 옵션 추가
----------------------------------------------------------%>
function setSelectList(id, value){
	var valueArr = value.split(',');
	
	if(id == "changeSection") {
		for(var i=0; i<valueArr.length; i++) {
			$("input:checkbox[id='" + id +"']").each(function() {
				if(this.value == valueArr[i]) {
					this.checked = true;
				}
			})
		}
	}else {
		$("input:radio[id='" + id +"']").each(function() {		
			if(this.value == valueArr[0]) {
				this.checked = true;
			}
		})
	}
}


$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
			if($.trim($("#name").val()) == "" ) {
				alert("ECR ${f:getMessage('제목')}${f:getMessage('을(를) 입력하세요.')}");
				$('#name').focus();
				return;		
			}
			
			if($.trim($("#number").val()) == "" ) {
				alert("ECR ${f:getMessage('번호')} ${f:getMessage('을(를) 입력하세요.')}");
				$('#number').focus();
				return;		
			}
			
			if($("input[name='model']").size() == 0 ){
				alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
				return;
			}
			
			if(!textAreaLengthCheckId('eoCommentA','2000','${f:getMessage("변경 사유")}')){
				return;
			}
			
			if(!textAreaLengthCheckId('eoCommentB','2000','${f:getMessage("변경 사항")}')){
				return;
			}
			if(!textAreaLengthCheckId('eoCommentC','2000','${f:getMessage("참고 사항")}')){
				return;
			}
			
			if(confirm("${f:getMessage('수정 하시겠습니까?')}")){
				//common_submit("changeECR", "updateECRAction", "updateECRForm", "listECR").submit();
			
				var form = $("form[name=updateECRForm]").serialize();
				var url	= getURLString("changeECR", "updateECRAction", "do");
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
							location.href = getURLString("changeECR", "viewECR", "do") + "?oid="+data.oid;
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
			//$("#updateECRForm").attr("action" , getURLString("changeECR", "updateECRAction", "do") + "?oid=" + $("#oid").val()).submit();
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

</script>

<body>
<form name="updateECRForm" id="updateECRForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="oid" id="oid" value="<c:out value="${ecrData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="" />
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
					<td ><img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />&nbsp;ECR ${f:getMessage('수정')}</td>
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
									CR/ECPR${f:getMessage('제목')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" >
									<input name="name" id="name" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecrData.name }"/>"/>
								</td>
								
								<td class="tdblueM">
									CR/ECPR${f:getMessage('번호')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" >
									<input name="number" id="number" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecrData.number }"/>"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('작성일')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="createDate" id="createDate" class="txt_field" size="12"  maxlength=15 readonly value="<c:out value="${ecrData.writeDate }"/>"/>
									
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="createDateBtn" id="createDateBtn" >
									</a>
									
									<a href="JavaScript:clearText('createDate')">
										<img src="/Windchill/jsp/portal/images/x.gif" border=0>
									</a>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('승인일')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="approveDate" id="approveDate" class="txt_field" size="12"  maxlength=15 readonly value="<c:out value="${ecrData.approveDate }"/>"/>
									
									<a href="javascript:void(0);">
										<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="approveDateBtn" id="approveDateBtn" >
									</a>
									
									<a href="JavaScript:clearText('approveDate')">
										<img src="/Windchill/jsp/portal/images/x.gif" border=0>
									</a>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('작성부서')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="createDepart" id="createDepart" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecrData.createDepart }"/>"/>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('작성자')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="writer" id="writer" class="txt_field" size="85" style="width:98%" value="<c:out value="${ecrData.writer }"/>"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">제품명 <span class="style1">*</span></td>
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
												<c:forEach items="${ecrData.getModelCode()}" var="code">
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
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('제안자')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="proposer" id="proposer" class="txt_field" style="width:98%" value="<c:out value="${ecrData.proposer }"/>" />
								</td>
								
								<td class="tdblueM">
									${f:getMessage('변경구분')}
								</td>
								
								<td class="tdwhiteL" >
									<div id='changeSection'></div>	
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('변경 사유')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentA" id="eoCommentA" rows="10" onchange="textAreaLengthCheckName('eoCommentA', '4000', '${f:getMessage('변경 사유')}')"><c:out value="${ecrData.commentA  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('변경 사항')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentB" id="eoCommentB" rows="10" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('변경 사유')}')"><c:out value="${ecrData.commentB  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('참고 사항')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentC" id="eoCommentC" rows="10" onchange="textAreaLengthCheckName('eoCommentC', '4000', '${f:getMessage('참고 사항')}')"><c:out value="${ecrData.commentC  }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
									<tr>
										<td class="tdblueM">${f:getMessage('관련')}CR/ECPR</td>
										<td class="tdwhiteL" colspan="3"><jsp:include
												page="/eSolution/changeECR/include_ECRSelect.do"
												flush="false" /></td>
									</tr>
									<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('주 첨부파일')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="updateECRForm"/>
										<jsp:param name="type" value="ECR"/>
										<jsp:param name="oid" value="${ecrData.oid }"/>
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
										<jsp:param name="formId" value="updateECRForm"/>
										<jsp:param name="oid" value="${ecrData.oid }"/>
									</jsp:include>
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