<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      BOM 보기
----------------------------------------------------------%>
function gotoViewPartTree(oid) {
	/*
	var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM", opts+rest);
    newwin.focus();
	*/
	auiBom(oid,'');
}

</script>

<body>

<form name="PartTreeForm"  method="post" >
<input type="hidden" name="oid" 		id="oid"		value="">
<input type="hidden" name="bomType" 	id="bomType"	value="">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color="white"><c:out value="${partNumber }" /> <c:out value="${title }" /></font></B></td>
		   		</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
				<tr height="30">
					<td>
						<b>
							<c:out value="${title }" />
						</b>
					</td>
		  			<td align="right">
		  				<button type="button" name="" id="" class="btnClose" onclick="self.close()">
							<span></span>
							${f:getMessage('닫기')}
						</button>
		  			</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%"  border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	    		<tr><td height="1" width="100%"></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				<tr height="25">
			        <td class="tdblueM" width="33%">${f:getMessage('품목번호')}</td>
					<td class="tdblueM" width="8%">&nbsp;</td>
					<td class="tdblueM" width="33%">${f:getMessage('품명')}</td>
					<td class="tdblueM" width="13%">${f:getMessage('상태')}</td>
					<td class="tdblueM" width="13%">${f:getMessage('Rev.')}</td>
				</tr>
				
				<c:choose>
					<c:when test="${fn:length(list) != 0 }">
						<c:forEach items="${list }" var="list">
							<tr>
								<td class="tdwhiteL" >
									<c:out value="${list.icon }" escapeXml="false" />
									<a href="JavaScript:gotoViewPartTree('<c:out value="${list.oid }" />')">
										<c:out value="${list.number }" />
									</a>
								</td>
								
								<td class="tdwhiteM" >
									<a href="JavaScript:openView('<c:out value="${list.oid }" />')" >
										<img src="/Windchill/netmarkets/images/details.gif "  border=0>
									</a>
								</td>
								
								<td class="tdwhiteL" >
									<c:out value="${list.name }" />
								</td>
								
								<td class="tdwhiteM" >
									<c:out value="${list.state }" />
								</td>
								
								<td class="tdwhiteM" >
									<c:out value="${list.version }" />
								</td>
								
							</tr>
						</c:forEach>
					</c:when>
					
					<c:otherwise>
						<tr>
							<td  class="tdwhiteM" colspan="5">
								<c:out value="${msg }" />
							</td>
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