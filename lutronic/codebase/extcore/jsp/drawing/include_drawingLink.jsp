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
	$("#linkDrawing").click(function() {
		var url = getURLString("drawing", "selectDrawingPopup", "do") + "?mode=multi";
		window.open(url, "CreateDrawingLink" , "status=no,  width=1200px, height=520px, resizable=no, scrollbars=no");
	})
	
	$('img[name=drawingDelete]').click(function() {
		if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
			var url	= getURLString("drawing", "deleteDrwaingLinkAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					parentOid : $('#oid').val(),
					drawingOid : this.id,
					type : $('#type').val()
				},
				success:function(data){
					if(data.result) {
						if($('#type').val() == 'active'){
							location.reload();
						}else {
							epmLinkReload();
						}
					}else {
						alert("${f:getMessage('삭제 실패하였습니다.')}\n" + data.message);
					}
				}
			});
		}
	})
})

window.addDrawing = function(obj) {
	for (var i = 0; i < obj.length; i++) {
		if(DuplicateCheck(obj[i][2])) {
			linkDrwaing(obj[i][0], $('#oid').val());
		}else {
			alert(obj[i][2] + "${f:getMessage('가 중복 되었습니다.')}");
		}
	}
}

<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DuplicateCheck(number) {
	var obj = $("input[name='drawingNumber']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == number) {
			return false;
		}
	}
	return true;
}

window.linkDrwaing = function(drawingOid, parentOid) {
	var url	= getURLString('drawing', 'linkDrawingAction', "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			drawingOid : drawingOid,
			parentOid : parentOid,
			type : $('#type').val()
		},
		success:function(data){
			if(data.result) {
				if($('#type').val() == 'active'){
					location.reload();
				}else {
					epmLinkReload();
				}
			}else {
				alert(data.message);
			}
		}
	});
}

window.epmLinkReload = function() {
	divPageLoad('drawingLink', $('#oid').val(), 'drawing', '${f:getMessage('관련도면') }', 'include_drawingLink', '<c:out value="${enabled }" />');
}

</script>

<body>

<input type='hidden' name='oid' id='oid' value='<c:out value="${oid}"/>' />
<input type='hidden' name='type' id='type' value='<c:out value="${module}"/>' />
	
<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td height="10">
			&nbsp;
		</td>
	</tr>
	
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
							<button type="button" class="btnCustom" id="linkDrawing">
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
					<td class="tdblueM">${f:getMessage('도면번호')}</td>
					<td class="tdblueM">${f:getMessage('도면명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('삭제')}</td>
				</tr>
				
				<c:forEach items="${list }" var="epmLink">
				
					<tr>
						<td class="tdwhiteM">
							<a href="JavaScript:openView('<c:out value="${epmLink.oid }"/>');">
								<c:out value="${epmLink.number }"/>
							</a>
							<input type='hidden' name='drawingNumber' value='<c:out value="${epmLink.number }"/>' />
						</td>
						
						<td class="tdwhiteM">
							<c:out value="${epmLink.name }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${epmLink.version }"/>.<c:out value="${epmLink.iteration }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${epmLink.getLifecycle() }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${epmLink.creator }"/>
						</td>
						<td class="tdwhiteM">
							<c:if test="${enabled }">
								<img src="/Windchill/jsp/portal/images/x.gif" border=0 name="drawingDelete" id="<c:out value="${epmLink.oid }"/>" style="cursor: pointer;">
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