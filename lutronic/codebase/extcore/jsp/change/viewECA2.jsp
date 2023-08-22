<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script>
$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#DocumentCreate").click(function () {
		
		var oid = $("#DocumentCreate").val();
		//alert(oid);
		var url = getURLString("changeECA", "outPutCreate", "do") + "?oid=" + oid;
		openOtherName(url,"outPutCreate","830","600","status=no,scrollbars=yes,resizable=yes");
		
		//location.href = getURLString("changeECO", "updateEO", "do") + "?oid="+$("#oid").val();
	})
	$("#modifyEca").click(function () {
		
		var oid = $("#DocumentCreate").val();
		//alert(oid);
		var url = getURLString("changeECA", "modifyECA", "do") + "?oid=" + oid;
		openOtherName(url,"outPutCreate","830","600","status=no,scrollbars=yes,resizable=yes");
		
		//location.href = getURLString("changeECO", "updateEO", "do") + "?oid="+$("#oid").val();
	})
	
})
</script>
<body>

<table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
	<tr> 
		<td height="30" width="93%" align="center"><B><font color=white></font></B></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td>
			<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
			<b>ECA ${f:getMessage('산출물')} [<c:out value="${eoNumber }" />]</b>
		</td>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
				<tr>
					<td>
						<button type="button" class="btnClose" onclick="self.close();">
							<span></span>
							${f:getMessage('닫기')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


<table width="100%" border="0%" cellpadding="5" cellspacing="0">

	<c:forEach items="${list }" var="eca">
		<tr>
			<td class="tdwhiteL" colspan="5">
				<img src="/Windchill/jsp/portal/img/bt_01.gif" >
				<b>
					<c:out value="${eca.ecaData.name }" /> 
					<c:if test="${admin }">
						[<c:out value="${eca.ecaData.oid }" />]
					</c:if>
				</b>
				&nbsp;<!--  isAutoCreate-->
				<c:if test="${eca.ecaData.isAutoCreate() }">
					<button type="button" name="DocumentCreate" id="DocumentCreate" class="btnCRUD" value='<c:out value="${eca.ecaData.oid }" />'>
						<span></span>
						${f:getMessage('산출물 등록')}
					</button>
					<button type="button" name="modifyEca" id="modifyEca" class="btnCRUD" value='<c:out value="${eca.ecaData.oid }" />'>
						<span></span>
						${f:getMessage('의견 및 첨부 등록')}
					</button>
				</c:if>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
					<tr>
						<td height=1 width=100%></td>
					</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  !style="tdisplay:none;able-layout:fixed">
                    <tr>
                    	<td  class="tdblueM"  width=15%>${f:getMessage('담당 부서')}</td>
                    	<td class="tdwhiteL">
                    		<c:out value="${eca.ecaData.getDepartmentName() }" /> 
                    	</td>
                    	
                    	<td  class="tdblueM"  width=15%>${f:getMessage('담당자')}</td>
                    	<td class="tdwhiteL">
                    		<c:out value="${eca.ecaData.activeUserName }" /> 
                    	</td>
                    	
                    	<td  class="tdblueM"  width=10%>${f:getMessage('상태')}</td>
                    	<td class="tdwhiteL">
                    		<c:out value="${eca.ecaData.stateName }" /> 
                    	</td>
                    </tr>
                    
                    <tr>
						<td  class="tdblueM"  width=10%>${f:getMessage('요청 완료일')}</td>
						<td class="tdwhiteL">
							<c:out value="${eca.ecaData.finishDate }" /> 
                    	</td>
                    	
                    	<td  class="tdblueM"  width=10%>${f:getMessage('완료일')}</td>
                    	<td class="tdwhiteL" colspan="3">
                    		<c:out value="${eca.ecaData.completeDate }" /> 
                    	</td>
                    </tr>
                    
                    <tr bgcolor="ffffff" height=35>
                    	<td class="tdblueM">${f:getMessage('첨부파일')}</td>
						<td class="tdwhiteL" colspan=5>
							<jsp:include page="/eSolution/content/includeAttachFileView">
								<jsp:param value="${eca.ecaData.oid }" name="oid"/>
							</jsp:include>
						</td>
					</tr>
					
					<tr>
	                	<td  class="tdblueM"  width=15%>${f:getMessage('의견')}</td>
	                	<td class="tdwhiteL" colspan=5>
	                		<c:out value="${eca.ecaData.comments }" /> 
	                	</td>
	                </tr>
	                
	                <c:if test="${eca.ecaData.activityType eq 'DOCUMENT' }">
	                	<tr bgcolor="ffffff" height=35>
							<td class="tdblueM">${f:getMessage('관련 문서')}</td>
	                		<td class="tdwhiteL" colspan=5>
	                			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="table-layout:fixed">
	                				<tr>
		                				<td class="tdblueM" width="20%">${f:getMessage('문서번호')}</td>
	                                    <td class="tdblueM" width="37%" style="word-break:break-all;">${f:getMessage('문서명')}</td>
	                                    <td class="tdblueM" width="6%">Rev.</td>
	                                    <td class="tdblueM" width="11%">${f:getMessage('등록자')}</td>
	                                    <td class="tdblueM" width="13%">${f:getMessage('상태')}</td>
	                                    <td class="tdblueM" width="9%">${f:getMessage('등록일')}</td>
	                				</tr>
	                				
	                				<c:forEach items="${eca.ecaData.getDocList() }" var="docData">
	                					<tr>
	                						<td class="tdwhiteM">
	                							<c:choose>
													<c:when test="${distribute eq 'true' }">
															<c:choose>
																<c:when test="${docData.isApproved()}">
																	<a href="javascript:openDistributeView('<c:out value="${docData.oid }" />')">
																		<c:out value="${docData.number }" />
																	</a>
																</c:when>
																<c:otherwise>
																	<c:out value="${docData.number }" />
																</c:otherwise>
															</c:choose>	
														</c:when>
													<c:otherwise>
														<a href="javascript:openView('<c:out value="${docData.oid }" />')">
															<c:out value="${docData.number }" />
														</a>
													</c:otherwise>
												</c:choose>	
	                						
	                						
	                						</td>
	                						
	                						<td class="tdwhiteM">
	                							<c:out value="${docData.name }" />
	                						</td>
	                						
	                						<td class="tdwhiteM">
	                							<c:out value="${docData.version }.${docData.iteration }" />
	                						</td>
	                						
	                						<td class="tdwhiteM">
	                							<c:out value="${docData.creator }" />
	                						</td>
	                						
	                						<td class="tdwhiteM">
	                							<c:out value="${docData.getLifecycle() }" />
	                						</td>
	                						
	                						<td class="tdwhiteM">
	                							<c:out value="${docData.dateSubString(true) }" />
	                						</td>
	                					</tr>
	                				</c:forEach>
	                			</table>
	                		</td>
	                	</tr>
	                
	                </c:if>
                    
				</table>
			</td>
		</tr>
		
      	<tr>
      		<td height="10">&nbsp;</td>
      	</tr>
      	
	</c:forEach>


</table>

</body>
</html>