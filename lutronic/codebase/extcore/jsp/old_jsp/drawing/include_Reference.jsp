<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_ReferenceViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})
})
</script>

<title>Insert title here</title>
</head>
<body>

<!-- 참조 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				<c:out value="${title }" />
			</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ReferenceViewToggle" alt='include_ReferenceView' >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id='include_ReferenceView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="table-layout:fixed">
					<tr>
						<td class="tdblueM" width="20%">
							${f:getMessage('도면')}${f:getMessage('번호')}
						</td>
						
						<td class="tdblueM" width="4%">
							&nbsp;
						</td>
						
						<td class="tdblueM" width="37%" style="word-break:break-all;">
							${f:getMessage('도면명')}
						</td>
						
						<td class="tdblueM" width="13%">
							${f:getMessage('상태')}
						</td>
						
						<td class="tdblueM" width="6%">
							Rev.
						</td>
						
						<td class="tdblueM" width="11%">
							${f:getMessage('등록자')}
						</td>
						<td class="tdblueM" width="11%">
							${f:getMessage('종속유형')}
						</td>
						<td class="tdblueM" width="9%">
							${f:getMessage('수정일')}
						</td>
					</tr>
					
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="epmRef">
								<tr>
									<td class="tdwhiteL">
										<c:choose>
											<c:when test="${distribute eq 'true' }">
												<c:choose>
														<c:when test="${epmRef.isApproved()}">
															<a href="javascript:openDistributeView('<c:out value="${epmRef.oid }" />')">
																<c:out value="${epmRef.number }" />
															</a>
														</c:when>
														<c:otherwise>
															<c:out value="${epmRef.number }" />
														</c:otherwise>
													</c:choose>	
											</c:when>
											<c:otherwise>
												<a href="javascript:openView('<c:out value="${epmRef.oid }" />')">
													<c:out value="${epmRef.number }" />
												</a>
											</c:otherwise>
										</c:choose>	
									</td>
									<td class="tdwhiteM">
									</td>
									<td class="tdwhiteL" title="<c:out value="${epmRef.number }" />">
										<c:out value="${epmRef.name }" />
									</td>
									<td class="tdwhiteM" align="center">
										<c:out value="${epmRef.state }" />
									</td>
									<td class="tdwhiteM" align="center">
										<c:out value="${epmRef.version }" />.<c:out value="${epmRef.iteration }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${epmRef.creator }" />
										<%-- <c:out value="${epmRef.dateSubString(true) }" /> --%>
									</td>
									<td class="tdwhiteM">
										<c:out value="${epmRef.linkRefernceType }" />
										<%-- <c:out value="${epmRef.dateSubString(true) }" /> --%>
									</td>
									<td class="tdwhiteM">
										<c:out value="${epmRef.dateSubString(false) }" />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<tr>
								<td class="tdwhiteM0" colspan="7" width="100%" align="center"><c:out value="${title }" />${f:getMessage('이(가) 없습니다.')}</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</table>
			</div>
		</td>
	</tr>
</table>

</body>
</html>