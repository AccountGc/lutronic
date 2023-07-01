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
$(function() {
	$('#updateComment').click(function() {
		if (confirm('<c:out value="${title }" />' + "${f:getMessage('을(를) 입력하시겠습니까?')}")){
			var url	= getURLString('development', 'updatCommentAction', 'do');
			$.ajax({
				type:"POST",
				url: url,
				data: {
					oid : $('#oid').val(),
					comment : $('#comment').val(),
				},
				success:function(data){
					alert(data.message);
					if(data.result){
						divPageLoad('workerComment', $('#oid').val(), 'development', '${f:getMessage('의견') }', 'include_viewComment', $('#enabled').val());
					}
				}
			});
		}
	})
	
	$('#cancel').click(function() {
		divPageLoad('workerComment', $('#oid').val(), 'development', '${f:getMessage('의견') }', 'include_viewComment', $('#enabled').val());
	})
})

</script>

<body>

<input type='hidden' name='oid' id='oid' value='<c:out value="${oid}"/>' />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				<c:out value="${title }" />
			</b>
		</td>
		<td align="right">
			<button type="button" name="updateComment" id="updateComment" class="btnCRUD">
				<span></span>
				<c:out value="${title }" />${f:getMessage(' 저장')}
			</button>
			
			<button type="button" name="cancel" id="cancel" class="btnCustom">
				<span></span>
				${f:getMessage('취소')}
			</button>
			
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
			
				<tr>
					<td class="tdblueM">
						<c:out value="${title }" /> 
					</td>
					<td class="tdwhiteL" colspan="3" style="height: 88px">
						<textarea name="comment" id="comment" rows="6" class="fm_area" style="width:95%" style="overflow:auto" wrap="hard" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('의견')}')"><c:out value="${comment}" escapeXml="false"/></textarea>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</body>
</html>