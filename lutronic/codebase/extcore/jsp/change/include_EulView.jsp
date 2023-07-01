<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

</script>

<body>

<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('설계변경내역')}</b></td>
				</tr>	
			</table>

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
		  			<td height="1" width="100%"></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
				<tr bgcolor="9acd32" height="25">
					<td class="tdblueM" width="5%"></td>
					<td class="tdblueM" width="10%">${f:getMessage('부품번호')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('등록부서')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('등록자')}</td>
					<td class="tdblueM" width="15%"></td>
					<td class="tdblueM" width="10%">Rev.</td>
					<td class="tdblueM" width="10%">${f:getMessage('변경')}BOM</td>
					<td class="tdblueM" width="10%">BOM</td>
					<td class="tdblueM" width="10%">${f:getMessage('을지')}</td>
				</tr>
				
				<tbody id="eulTableBody">
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="data"  varStatus="status">
								<tr>
									<td class="tdwhiteM">
										<c:out value="${data.icon }" />
									</td>
									<td class="tdwhiteM">
										<a href="javascript:openView('<c:out value="${data.eulOid }" />')">
											<c:out value="${data.converNumber }" />
										</a>
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.creatorDept }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.creatorName }" />
									</td>
									<td class="tdwhiteM">
										<a href="javascript:openView('<c:out value="${data.topAssyOid }" />')">
											<img src="/Windchill/netmarkets/images/details.gif "  border=0>
										</a>
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.version }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.eulTree }" escapeXml="false"/>
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.bom }" escapeXml="false"/>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
			           		 	<td class="tdwhiteM" colspan="9" align="center">${f:getMessage('설계 변경 내역이 없습니다.')}</td>
			            	</tr>	
					 	</c:otherwise>
					</c:choose> 
				</tbody>
			</table>
		</td>
	</tr>
</table>


</body>
</html>