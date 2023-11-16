<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<body>
<input type="hidden" name="ecaAction" id="ecaAction" >
<input type="hidden" name="docOid" id="docOid" >
<input type="hidden" name="docLinkOid" id="docLinkOid">
<input type="hidden" name="ecaOid" id="ecaOid" value="<c:out value="${ecaOid }"/>">

<iframe src="" name="hiddenFrame" id="hiddenFrame" scrolling=no frameborder=no marginwidth=0 marginheight=0 style="display:none"></iframe>

<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*"><img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('관련 문서')}/${f:getMessage('관련 도면 체크')}</b></td>
						
				</tr>
			</table>

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
	
			<!-- 대상 품목 -->
			 <table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  style="table-layout:fixed">
				<tr height=25>
					<td class="tdblueM" width="5%">&nbsp;</td>
					<td class="tdblueM" width="5%">&nbsp;</td>
					<td class="tdblueM" width="20%">${f:getMessage('부품')}${f:getMessage('번호')}</td>
					<td class="tdblueM" width="20%">${f:getMessage('부품명')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
					<td class="tdblueM" width="10%">Rev.</td>
					<td class="tdblueM" width="5%">${f:getMessage('등록자')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('제어문서')}&${f:getMessage('제어도면')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('PDF')}</td>
				</tr>
				
				<c:forEach items="${list }" var="list">
					<input type="hidden" name="drawingNumber" id="drawingNumber" value="<c:out value="${list.partNumber }"/>">
					<input type="hidden" name="isDwg" id="isDwg" value="<c:out value="${list.isDwg }"/>">
					<input type="hidden" name="isOrCad" id="isOrCad" value="<c:out value="${list.isOrCad }"/>">
					<input type="hidden" name="isOrCadDoc" id="isOrCadDoc" value="<c:out value="${list.isOrCadDoc }"/>">
					<input type="hidden" name="isControl" id="isControl" value="<c:out value="${list.isControl }"/>">
					<input type="hidden" name="isPDF" id="isPDF" value="<c:out value="${list.isPDF }"/>">
					<tr >
						<td class="tdwhiteM" align=center>
							<c:out value="${list.imgUrl }" escapeXml="false" />
						</td>
						<td class="tdwhiteM" align=center>
							<c:out value="${list.icon }" escapeXml="false" />
						</td>
						<td class="tdwhiteL">
							<c:out value="${list.partNumber }" />
						</td>
						<td class="tdwhiteL" title="<c:out value="${list.partName }" />">
						<div style="width:250;border:0;padding:0;margin:0;text-overflow:ellipsis;overflow:hidden;">
							<nobr>
							<a href="JavaScript:openView('<c:out value="${list.partOid }" />', 'full', 'full', 'true')">
								<c:out value="${list.partName }" />
							</a>
							</nobr>
						</div>
						</td>
						<td class="tdwhiteM" align=center>
							<c:out value="${list.partState }" />
						</td>
						<td class="tdwhiteM" align=center>
							<c:out value="${list.partVersion }" />
						</td>
						<td class="tdwhiteM">
							<c:out value="${list.partCreator }" />
						</td>
						<td class="tdwhiteM">
							<c:if test="${list.isControl eq 'true' }">
								<c:out value="${list.isOrCadDoc }" /> & <c:out value="${list.isOrCad }" />
							</c:if>&nbsp;
						</td>
						<td class="tdwhiteM">
							<c:if test="${list.isDwg eq 'true' }">
								<c:out value="${list.isPDF }" />
							</c:if>&nbsp;
						</td>
					</tr>
				
				</c:forEach>
				
			</table>
		</td>
	</tr>
</table>


</body>
</html>