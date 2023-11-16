<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<form name=projectHistory>

<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				<tr> 
					<td height=30 width=99% align=center><B><font color=white></font></B></td>
				</tr>
			</table>
			
			<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1" >
				<tr>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp;${f:getMessage('버전 이력보기')}
					</td>
					
					<td align="right">
						<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
               				<span></span>
               				${f:getMessage('닫기')}
               			</button>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
					<td height=1 width=100%></td>
				</tr>
			</table>
			
			<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1" bgcolor=#cfcfcf class=9pt >
				<tr>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM">${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('수정자')}</td>
					<td class="tdblueM">${f:getMessage('등록일')}</td>
					<td class="tdblueM">${f:getMessage('수정일')}</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('이력내용')}</td>
				</tr>
				
				<c:choose>
					<c:when test="${fn:length(list) != 0 }">
				
						<c:forEach items="${list }" var="list">
							<tr>
								<td class="tdwhiteM">
									<c:choose>
										<c:when test="${distribute eq 'true' }">
												<c:choose>
													<c:when test="${list.isApproved}">
														<a href="javascript:openDistributeView('<c:out value="${list.oid }" />')">
															<c:out value="${list.version }" />.<c:out value="${list.iteration }" />
														</a>
													</c:when>
													<c:otherwise>
														<c:out value="${list.version }" />.<c:out value="${list.iteration }" />
													</c:otherwise>
												</c:choose>	
											</c:when>
										<c:otherwise>
											<a href="javascript:openView('<c:out value="${list.oid }" />')">
												<c:out value="${list.version }" />.<c:out value="${list.iteration }" />
											</a>
										</c:otherwise>
									</c:choose>	
								</td>
							
								<td class="tdwhiteM">
									<c:out value="${list.creator }" />
								</td>
								
								<td class="tdwhiteM">
									<c:out value="${list.modifier }" />
								</td>
								
								<td class="tdwhiteM">
									<c:out value="${list.createDate }" />
								</td>
								
								<td class="tdwhiteM">
									<c:out value="${list.modifyDate }" />
								</td>
								
								<td class="tdwhiteM">
									<c:out value="${list.state }" />
								</td>
								
								<td class="tdwhiteM">
									<c:out value="${list.note }" />
								</td>
								
							</tr>
						
						</c:forEach>
						
					</c:when>
					
					<c:otherwise>
						<tr>
							<td class='tdwhiteM0' align='center' colspan='6'>${f:getMessage('검색 결과가 없습니다')}</td>
						</tr>
					</c:otherwise>
					
				</c:choose>
				
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>