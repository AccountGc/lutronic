<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECA 산출물 등록</title>
</head>
<script type="text/javascript">
$(document).ready(function() {
	//alert('${ecaOid }');
	divPageLoad('docLink', '${ecaOid }', 'doc', '${f:getMessage('산출물') }', 'include_documentLink', true);

})

<%----------------------------------------------------------
*                      DIV 페이지 로딩 설정
----------------------------------------------------------%>
window.divPageLoad = function(divId, oid, moduleName, title, url, enabled) {
	if(url.length > 0) {
		var url	= getURLString(moduleName, url, "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				oid : oid,
				title : title,
				module : 'ecaAction',
				enabled : enabled
			},
			success:function(data){
				$('#' + divId).html(data);
			}
		});
	} else {
		$('#' + divId).html('');
	}
}
</script>

<body>
<form>
<table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
	<tr> 
		<td height="30" width="93%" align="center"><B><font color=white></font></B></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td>
			<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
			<b>ECA ${f:getMessage('산출물')} 등록/삭제</b>
		</td>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
				<tr>
					<td>
						<button type="button" class="btnClose" onclick="self.close();">
							<span></span>
							${f:getMessage('닫기')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td>
			<!-- 문서 링크 -->
		<div id='docLink'></div>
		
		</td>
	</tr>
</table>
</form>

</body>
</html>