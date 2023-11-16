<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	batchDownDescribe();
});

$(function() {
	$('#attachDown').click(function() {
		if($('#describeType').val() == ""){
			alert("다운로드 사유를 선택해 주세요");
			return;
		}
		console.log("===batchEODownAction===")
		
		//$("#batchEoDown").attr("method", "post");
		//$("#batchEoDown").attr("action", getURLString("common", "batchEODownAction", "do")).submit();
		
		var form = $("form[name=batchEoDown]").serialize();
		var url	= getURLString("common", "batchEODownAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			success:function(data){
				console.log(data.message);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
		
	})
})
<%----------------------------------------------------------
*                      다운로드 사유 가져오기
----------------------------------------------------------%>
function batchDownDescribe() {
	var form = $("form[name=batchEoDown]").serialize();
	var url	= getURLString("common", "batchDownDescribe", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#describeType").find("option").remove();
			$("#describeType").append("<option value=''>${f:getMessage('선택')}</option>");
			
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#describeType").append("<option value='" + data[i].code + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}

</script>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<form name=batchEoDown method=post>
<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />" />
<input type="hidden" name="downType" id="downType" value="<c:out value="${downType }" />" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				<tr> 
					<td height=30 width=99% align=center><B><font color=white><c:out value="${title }" /></font></B></td>
				</tr>
			</table>
			
			<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1" >
				<tr>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp;${f:getMessage('일괄 다운로드')}
					</td>
					
					
					<td align="right">
						<button type="button" name="attachDown" id="attachDown" class="btnClose" >
               				<span></span>
               				${f:getMessage('다운로드')}
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
	            	<td width="25%"></td>
					<td width="57%"></td>
				
			    </tr>
			    <tr bgcolor="ffffff" height="35" >
					<td class="tdblueM">
						${f:getMessage('다운로드 사유 선택')}<span class="style1">*</span>
						
					</td>
					
					<td class="tdwhiteL" >
						<select name="describeType" id="describeType">
						</select>
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35" >
					<td class="tdblueM">
						${f:getMessage('다운로드 상세 사유')}
						
					</td>
					
					<td class="tdwhiteL" >
						<textarea name="describe" id="describe" rows="13" class="fm_area" style="width:98%" onchange="textAreaLengthCheckName('describe', '4000', '${f:getMessage('다운로드 사유')}')"></textarea>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>