<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
$(function() {
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#eulOid").click(function () {
		
		if (!confirm("${f:getMessage('삭제 하시겠습니까?')}")){
			return;
		}
		
	})
})
function viewInfo(oid){
	var str="/Windchill/jsp/change/EOEulB.jsp?oid="+oid;
	openWindow(str, '', 800, 600);
}
</script>

<body>
<form name="incldue_EulListForm" id="incldue_EulListForm" method="post" >
<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td>
			<table width="100%" border="0" cellpadding="4" cellspacing="0" >
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('등록된 을지목록')}</b></td>
					<td align="right" colspan="2">
						<button type="button" class="btnCustom" title="을지등록" id="createEul" name="createEul">
		                 	<span></span>
		                 	${f:getMessage('을지등록')}
		               	</button>
						<button type="button" class="btnCustom" title="v" id="reflash" name="reflash" onclick="location.reload();">
		                 	<span></span>
		                 	${f:getMessage('새로고침')}
		               	</button>
					</td>
				</tr>	
			</table>

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
		  			<td height="1" width="100%"></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
				<col width="5%"><col width="20%"><col width="*"><col width="5%"><col width="15%"><col width="5%">
				<tr bgcolor="9acd32" height="25">
					<td class="tdblueM">&nbsp;</td>
					<td class="tdblueM">${f:getMessage('부품번호')}</td>
					<td class="tdblueM">${f:getMessage('부품명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM">${f:getMessage('수정일')}</td>
					<td class="tdblueM0">${f:getMessage('삭제')}</td>
				</tr>
				
				<tbody id="eulTableBody">
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="data"  varStatus="status">
								<tr>
									<td class="tdwhiteM">
										<input type=checkbox name=check onclick='oneClick(this)' value="<c:out value="${data.oid }" />">
									</td>
									<td class="tdwhiteM">
										<a href="javascript:viewInfo('<c:out value="${data.oid }" />')">
											<c:out value="${data.assy_num }" />
										</a>
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.assy_name }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.version }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.version }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.viewName }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${data.eulB }" />
									</td>
									<td class="tdwhiteM">
										<input type = "hidden" name = "eulOid" id = "eulOid" value='<c:out value="${data.oid }" />'/>
										<button type="button" class="btnCustom" title="삭제" id="delEul" name="delEul">
						                 	<span></span>
						                 	${f:getMessage('삭제')}
						               	</button>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
			           		 	<td class="tdwhiteM" colspan="6"><font color=red>${f:getMessage('등록된 을지가 없습니다.')}</font></td>
			            	</tr>	
					 	</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</td>
	</tr>
</table>
</form>

</body>
</html>