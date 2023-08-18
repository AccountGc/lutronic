<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<body>

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr heighe="5">
		<td>
		
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />&nbsp;${f:getMessage('다운로드 이력')}
					</td>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
			               				<span></span>
			               				${f:getMessage('닫기')}
			               			</button>
								</td>
							</tr>
			            </table>
					</td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr><td height="1" width="100%"></td></tr>
						</table>

						<table name="download" id="download" border="0" cellpadding="0" cellspacing="0" width="100%"> <!--//여백 테이블-->
							<tr height=25>
								<td class="tdblueM" width="15%">${f:getMessage('부서')}</td>
								<td class="tdblueM" width="10%">${f:getMessage('직급')}</td>
								<td class="tdblueM" width="20%">${f:getMessage('이름')}</td>
								<td class="tdblueM" width="20%">${f:getMessage('다운로드 시간')}</td>
								<td class="tdblueM" width="10%">${f:getMessage('다운로드 횟수')}</td>
							</tr>
							
							<c:choose>
								<c:when test="${fn:length(list) != 0 }">
									<c:forEach items="${list }" var="list">
										<tr bgcolor="ffffff">
										
											<td class="tdwhiteM">
												<c:out value="${list.dept }" />
											</td>
											
											<td class="tdwhiteM">
												<c:out value="${list.duty }" />
											</td>
											
											<td class="tdwhiteM">
												<c:out value="${list.name }" />
											</td>
											
											<td class="tdwhiteM">
												<c:out value="${list.time }" />
											</td>
											
											<td class="tdwhiteM">
												<c:out value="${list.count }" />
											</td>
										
										</tr>
									</c:forEach>
								</c:when>
								
								<c:otherwise>
									<tr bgcolor="ffffff">
										<td class="tdwhiteM" colspan="5">${f:getMessage('다운로드 이력이 없습니다.')}</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


</body>
</html>