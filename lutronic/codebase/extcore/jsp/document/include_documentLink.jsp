<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
$(document).ready(function() {
})

$(function() {
	$('#createDocument').click(function() {
		var url = getURLString("doc", "createDocumentPop", "do") + "?parentOid=" + $('#oid').val() + "&type=" + $('#type').val();
		openOtherName(url,"window","920","550","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#linkDocument").click(function() {
		var url = getURLString("doc", "createDocumentLink", "do") + "?parentOid=" + $('#oid').val() + "&type=" + $('#type').val();
		window.open(url, "CreateDocumentLink" , "status=no,  width=880px, height=520px, resizable=yes, scrollbars=yes");
	})
	
	$('img[name=docDelete]').click(function() {
		
		if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
			var url	= getURLString("doc", "deleteDocumentLinkAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				dataType:"json",
				data: {
					parentOid : $('#oid').val(),
					linkOid : this.id,
					type : $('#type').val()
				},
				success:function(data){
					if(data.result) {
						if($('#type').val() == 'active'){
							location.reload();
						}else {
							docLinkReload();
						}
					}else {
						alert("${f:getMessage('삭제 실패하였습니다.')}\n" + data.message);
					}
				}
			});
		}
	})
})

window.docLinkReload = function() {
	divPageLoad('docLink', $('#oid').val(), 'doc', '<c:out value="${title }" />', 'include_documentLink', '<c:out value="${enabled }" />');
}

window.pageReload = function() {
	location.reload();
}

</script>

<body>

<input type='hidden' name='oid' id='oid' value='<c:out value="${oid}"/>' />
<input type='hidden' name='type' id='type' value='<c:out value="${module}"/>' />
	
<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
			
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*" align="left">
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>
							<c:out value='${title }' />
						</b>
					</td>
					
					<c:if test="${enabled }">
						<td align="right" width="80">
							<button type="button" class="btnCustom" id="createDocument">
								<span></span>
								${f:getMessage('직접등록')}
							</button>
						</td>	
							
						<td align="right" width="80">
							<button type="button" class="btnCustom" id="linkDocument">
								<span></span>
								${f:getMessage('링크등록')}
							</button>
						</td>	
					</c:if>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
					<td height="1" width="100%">
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				
				<tr>
					<td class="tdblueM">${f:getMessage('문서번호')}</td>
					<td class="tdblueM">${f:getMessage('문서명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('삭제')}</td>
				</tr>
				
				<c:forEach items="${list }" var="docData">
				
					<tr>
						<td class="tdwhiteM">
							<a href="JavaScript:openView('<c:out value="${docData.oid }"/>');">
								<c:out value="${docData.number }"/>
							</a>
							<input type='hidden' name='docNumber' value='<c:out value="${docData.number }"/>' />
						</td>
						
						<td class="tdwhiteM">
							<c:out value="${docData.name }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${docData.version }"/>.<c:out value="${docData.iteration }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${docData.getLifecycle() }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${docData.creator }"/>
						</td>
						<td class="tdwhiteM">
							<c:if test="${enabled }">
								<img src="/Windchill/jsp/portal/images/x.gif" border=0 name="docDelete" id="<c:out value="${docData.linkOid }"/>" style="cursor: pointer;">
							</c:if>
						</td>
					</tr>
					
				</c:forEach>
			</table>
		</td>
	</tr>
</table>

</body>
</html>