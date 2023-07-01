<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECA 의견 및 첨부 등록</title>
</head>
<script type="text/javascript">
$(document).ready(function() {
	

})
$(function() {
	
	
	
	$('#modifyECA').click(function() {
		
		if(confirm("${f:getMessage('수정 하시겠습니까?')}")){
			
			var form = $("form[name=ecaForm]").serialize();
			var url	= getURLString("changeECA", "modifyECAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('수정 성공하였습니다.')}");
						opener.location.reload(true);  
						self.close();  
					}else {
						alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
					}
				}
				,beforeSend: function() {
					gfn_StartShowProcessing();
		        }
				,complete: function() {
					gfn_EndShowProcessing();
		        }
			});
		}
	}) 
})

</script>

<body>
<form name="ecaForm" id="ecaForm" method="post" enctype="multipart/form-data">
<input type="hidden" name="oid" id="oid" value="<c:out value="${ecaOid }" />" />
<table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
	<tr> 
		<td height="30" width="93%" align="center"><B><font color=white></font></B></td>
	</tr>
</table>
<br>
<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td>
			<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
			<b>ECA ${f:getMessage('의견 및  첨부파일')} </b>
		</td>
		<td align="right">
			<button type="button" name="modifyECA" id="modifyECA" class="btnCRUD" >
   				<span></span>
   				저장
   			</button>
			<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
   				<span></span>
   				${f:getMessage('닫기')}
   			</button>
		</td>
		
	</tr>
</table>
<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
					<td height=1 width=100%></td>
				</tr>
			</table>
<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1" bgcolor=#cfcfcf class=9pt >
		<tr>
           	<td width="15%"></td>
			<td width="85%"></td>
		
	    </tr>
	   <tr bgcolor="ffffff" height="35" >
			<td class="tdblueM">
				${f:getMessage('의견')}
				
			</td>
			
			<td class="tdwhiteL" >
				<textarea name="comments" id="comments" rows="8" class="fm_area" style="width:98%" onchange="textAreaLengthCheckName('comments', '4000', '${f:getMessage('의견')}')"><c:out value="${comments }" /></textarea>
			</td>
		</tr>
		<tr bgcolor="ffffff" height="35">
			<td class="tdblueM">
				${f:getMessage('첨부파일')}
			</td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
					<jsp:param name="formId" value="ecaForm"/>
				</jsp:include>
			</td>
		</tr>
		
</table>

</form>

</body>
</html>