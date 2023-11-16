<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	
})

$(function() {
	$("#doBatchRevision").click(function() {
		for(var i=0; i<$("input[name='isBatchRevison']").length; i++) {
			if($("input[name='isBatchRevison']").eq(i).val() == "false") {
				alert("${f:getMessage('최신 버전이 아닌 도면이나 품목이 있습니다.')}");
				return ;
			}
		}
		
		if(!confirm("${f:getMessage('일괄 수정을 하시겠습니까?')}")){
			return;
		}
		
		$("#batchRevision").attr("action", getURLString("changeECO", "batchCheckInOutAction", "do")).submit();
	})
})
</script>

<body>

<form name="batchRevision" id="batchRevision">

<table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
	<tr> 
		<td height="30" width="93%" align="center"><B><font color=white></font></B></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td ><img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />&nbsp;<b>${f:getMessage('품목 일괄 수정')}</b></td>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
				<tr>
					<td>
						<button type="button" class="btnCRUD" title="${f:getMessage('일괄개정')}" id="doBatchRevision" name="doBatchRevision">
		                 	<span></span>
		                 	${f:getMessage('일괄수정')}
	                	</button>
					</td>
					<td>
                    	<button type="button" class="btnClose" onclick="self.close()">
                    		<span></span>
                    		${f:getMessage('닫기')}
                    	</button>
					</td>
                  </tr>
              </table>
		</td>
	</tr>
</table>

<table width="100%" border="0"  cellspacing="0">
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="99%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="99%" border="0"  cellpadding="1" cellspacing="1" align=center>
				<col  width="5%"><col  width="15%"><col  width="15%"><col  width="5%"><col  width="6%">
				
				<col  width="15%"><col  width="6%">
				<col  width="15%"><col  width="6%">
				<col  width="15%">
				<tr>
					<td class="tdblueM">${f:getMessage('번호')}</td>
					<td class="tdblueM">${f:getMessage('부품번호')}</td>
					<td class="tdblueM">${f:getMessage('부품명')}</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('최신버전')}</td>
					
					<td class="tdblueM3">${f:getMessage('주도면')}</td>
					<td class="tdblueM3">${f:getMessage('최신버전')}</td>
					
					<td class="tdblueM4">${f:getMessage('참조항목')}</td>
					<td class="tdblueM4">${f:getMessage('최신버전')}</td>
					
					<td class="tdblueM">${f:getMessage('관련도면')}</td>
				</tr>
				
				<c:forEach items="${list }" var="list" varStatus="i">
				
					<tr>
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							${i.index + 1 }
							<input type="hidden" name="revisableOid" value="<c:out value="${list.linkOid }" />" />
							<input type="hidden" name="isBatchRevison" value="<c:out value="${list.isBatchRevison }" />" />
						</td>
						
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<a href="javascript:openView('<c:out value="${list.partOid }"/>')">
								<c:out value="${list.partNumber }"/>
							</a>
						</td>
				
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<c:out value="${list.partName }"/>
						</td>
						
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<c:out value="${list.partState }"/>
						</td>
				
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<c:out value="${list.partStyle }" escapeXml="false" />&nbsp;[<c:out value="${list.partVersion }"/>]
						</td>
						
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<c:out value="${list.epmNumber }"/>&nbsp;
						</td>
						
						<td class="tdwhiteL" rowspan="<c:out value="${list.rowSpan }"/>">
							<c:out value="${list.epm3DStyle }" escapeXml="false"/>&nbsp;<c:out value="${list.epmVersion }"/>
						</td>
						
						<c:choose>
							<c:when test="${fn:length(list.epm2D) ne 0 }">
								<c:forEach items="${list.epm2D }" var="epm2D">
										<td class="tdwhiteL">
											<c:out value="${epm2D.epm2DNumber }" />
										</td>
										<td class="tdwhiteL">
											<c:out value="${epm2D.epm2DStyle }" escapeXml="false"/>&nbsp;[<c:out value="${epm2D.epm2DVersion }" />]
										</td>
								</c:forEach>							
							</c:when>
							<c:otherwise>
								<td class="tdwhiteL">&nbsp;</td>
								<td class="tdwhiteL">&nbsp;</td>
							</c:otherwise>
						</c:choose>
						
						<td class="tdwhiteL">
							<c:forEach items="${list.epmDesc }" var="epmDesc" varStatus="i">
								<c:out value="${epmDesc}" escapeXml="false"/>
								<c:if test="${!i.last}">
									<br>
								</c:if>
							</c:forEach>
						</td>
					</tr>
				</c:forEach>
				
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>