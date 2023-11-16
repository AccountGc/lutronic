<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("changeECO", "updateEO", "do") + "?oid="+$("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		
		var form = $("form[name=viewECOForm]").serialize();
		var url	= getURLString("changeECO", "deleteECOction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.msg);
				if(data.result) {
					if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
						parent.opener.location.reload();
					}else {
						parent.opener.$("#sessionId").val("");
						parent.opener.lfn_Search();
					}
					window.close();
				}else {
					document.location = data.view;
				}
			}
		});
	}),
	<%----------------------------------------------------------
	*                      산출물 버튼
	----------------------------------------------------------%>
	$("#viewECA").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("change" , "viewECA", "do") + "?oid="+oid;
		openWindow(url, "eca", 1000, 600);
	}),
	<%----------------------------------------------------------
	*                      다운로드 이력 버튼
	----------------------------------------------------------%>
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      결재이력 버튼
	----------------------------------------------------------%>
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
})

</script>

<body>

<form name="viewECOForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<c:out value="${ecoData.oid }" />" />
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
					<td ><img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
					&nbsp;
					<c:choose>
					<c:when test= "${ecoData.eoType == 'CHANGE'}">ECO</c:when>
					<c:otherwise>EO</c:otherwise>
					</c:choose>
					 ${f:getMessage('상세보기')}</td>
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                	<c:if test="${ecoData.isModify() }" >
									<td>
										<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('수정')}
										</button>
									</td>
									<td>
										<button type="button" name="deleteBtn" id="deleteBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('삭제')}
										</button>
									</td>
								</c:if>
								<td>
									<button type="button" name="versionBtn" id="viewECA" class="btnCustom">
										<span></span>
										${f:getMessage('산출물')}
									</button>
								</td>
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom">
										<span></span>
										${f:getMessage('결재이력')}
									</button>
								</td>
								<td>
									<button type="button" name="downloadBtn" id="downloadBtn" class="btnCustom">
										<span></span>
										${f:getMessage('다운로드이력')}
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
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									<c:choose>
									<c:when test= "${ecoData.eoType == 'CHANGE'}">ECO</c:when>
									<c:otherwise>EO </c:otherwise>
									</c:choose>
								${f:getMessage('제목')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.name }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
								<c:choose>
								<c:when test= "${ecoData.eoType == 'CHANGE'}">ECO</c:when>
								<c:otherwise>EO</c:otherwise>
								</c:choose>
								${f:getMessage('번호')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.number }"/>
								</td>
								<td class="tdblueM">
								
								${f:getMessage('상태')}</TD>
								<td class="tdwhiteL">
									<c:out value="${ecoData.getLifecycle() }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.creator }"/>
								</td>
								<td class="tdblueM">구분</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.getEoTypeDisplay() }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.createDate }"/>
								</td>
								<td class="tdblueM">${f:getMessage('수정일')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.modifyDate }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('제품명')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.getModelDisplay() }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('제품 설계 개요')}</td>
			                    <td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.viewCommentA }" escapeXml="false"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('특기사항')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.viewCommentB }" escapeXml="false"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('기타사항')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.viewCommentC }" escapeXml="false"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('첨부파일')}</td>					
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFileView">
										<jsp:param value="${ecoData.oid }" name="oid"/>
									</jsp:include>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<!-- 활동 현황 -->
			
			
			<!-- 완제품 품목 -->
			<jsp:include page="/eSolution/changeECO/include_CompletePartView.do" flush="false" />
			
			<c:if test="${ecoData.eoType eq 'CHANGE'}">
			<!-- 대상 품목 -->
			<jsp:include page="/eSolution/changeECO/include_ChangePartView.do" flush="false" />
			
			<!-- 설계변경내역 -->
			<jsp:include page="/eSolution/changeECO/include_EulView.do" flush="false" />
			
			<!-- 관련 ECR -->
			<jsp:include page="/eSolution/changeECR/include_ECRView.do" flush="false" />
			
			<!-- 관련 ECN -->
			<jsp:include page="/eSolution/changeECN/include_ECNView.do" flush="false" />
			</c:if>
		</td>
	</tr>
</table>
</form>
</body>
</html>