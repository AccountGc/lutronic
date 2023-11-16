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
	$("#linkPart").click(function() {
		var url = getURLString("part", "selectPartPopup", "do") + "?mode=mutil";
		window.open(url, "CreatePartLink" , "status=no,  width=1200px, height=520px, resizable=no, scrollbars=no");
	})
	
	$('img[name=partDelete]').click(function() {
		if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
			var url	= getURLString("part", "deletePartLinkAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					parentOid : $('#oid').val(),
					partOid : this.id,
					type : $('#type').val()
				},
				success:function(data){
					if(data.result) {
						if($('#type').val() == 'active'){
							location.reload();
						}else {
							partLinkReload();
						}
					}else {
						alert("${f:getMessage('삭제 실패하였습니다.')}\n" + data.message);
					}
				}
			});
		}
	})
})

window.addPart = function(obj) {
	for (var i = 0; i < obj.length; i++) {
		if(DuplicateCheck(obj[i][1])) {
			linkPart(obj[i][0], $('#oid').val());
		}else {
			alert(obj[i][1] + "${f:getMessage('가 중복 되었습니다.')}");
		}
	}
}

<%----------------------------------------------------------
*                      검색 문서 중복 검색
----------------------------------------------------------%>
function DuplicateCheck(number) {
	var obj = $("input[name='partNumber']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == number) {
			return false;
		}
	}
	return true;
}

window.linkPart = function(partOid, parentOid) {
	var url	= getURLString('part', 'linkPartAction', "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			partOid : partOid,
			parentOid : parentOid,
			type : $('#type').val()
		},
		success:function(data){
			if(data.result) {
				if($('#type').val() == 'active'){
					location.reload();
				}else {
					partLinkReload();
				}
			}else {
				alert(data.message);
			}
		}
	});
}

window.partLinkReload = function() {
	divPageLoad('partLink', $('#oid').val(), 'part', '${f:getMessage('관련 품목') }', 'include_partLink', $('#enabled').val());
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
							<button type="button" class="btnCustom" id="linkPart">
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
					<td class="tdblueM">${f:getMessage('품목번호')}</td>
					<td class="tdblueM">${f:getMessage('품목명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('삭제')}</td>
				</tr>
				
				<c:forEach items="${list }" var="partLink">
				
					<tr>
						<td class="tdwhiteM">
							<a href="JavaScript:openView('<c:out value="${partLink.oid }"/>');">
								<c:out value="${partLink.number }"/>
							</a>
							<input type='hidden' name='partNumber' value='<c:out value="${partLink.number }"/>' />
						</td>
						
						<td class="tdwhiteM">
							<c:out value="${partLink.name }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${partLink.version }"/>.<c:out value="${partLink.iteration }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${partLink.getLifecycle() }"/>
						</td>
						<td class="tdwhiteM">
							<c:out value="${partLink.creator }"/>
						</td>
						<td class="tdwhiteM">
							<c:if test="${enabled }">
								<img src="/Windchill/jsp/portal/images/x.gif" border=0 name="partDelete" id="<c:out value="${partLink.oid }"/>" style="cursor: pointer;">
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