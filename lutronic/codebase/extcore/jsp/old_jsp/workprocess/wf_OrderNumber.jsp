<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	$(":button[name='orderNumber']").click(function() {
		var str = getURLString("part", "updateAUIPartChange", "do") + "?partOid=" + this.id;
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "viewBOM", opts+rest);
		newwin.focus();
	});
	$(":button[name='addPart']").click(function() {
		var str = getURLString("change", "AddECOPart", "do") + "?oid=${ecaOid}";
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1180,height=880,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "viewBOM", opts+rest);
		newwin.focus();
	});
});
</script>

<body>

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
					<td width="*">
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>
							${f:getMessage('진채번 현황')}
						</b>
					</td>
					<td align="right">
						<button type="button" class="btnCustom" title="${f:getMessage('품목추가')}" id="addPart" name="addPart">
		                 	<span></span>
		                 	${f:getMessage('품목추가')}
	                	</button>
						<button type="button" class="btnCustom" title="v" id="reflash" name="reflash" onclick="location.reload();">
		                 	<span></span>
		                 	${f:getMessage('새로고침')}
	                	</button>
					</td>
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
					<td class="tdblueM" width="20%">${f:getMessage('품목번호')}</td>
					<td class="tdblueM" width="20%">${f:getMessage('품목명')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
					<td class="tdblueM" width="10%">Rev.</td>
					<td class="tdblueM" width="5%">${f:getMessage('등록자')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('채번')}</td>
				</tr>
				
				<c:forEach items="${list }" var="list">
					
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
							<button type="button" class="btnCustom" name="orderNumber" id="<c:out value="${list.partOid }" />">
								<span></span>
								${f:getMessage('채번')}
							</button>
						</td>
					</tr>
				
				</c:forEach>
				
			</table>
		</td>
	</tr>
</table>

</body>
</html>