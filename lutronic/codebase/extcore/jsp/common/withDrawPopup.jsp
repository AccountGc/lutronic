<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(function() {
	$('#withDrawAction').click(function() {
		
		
		if(confirm("${f:getMessage('결재 회수 하시겠습니까?')}")){
			var form = $("form[name=withDraw]").serialize();
			var url	= getURLString("common", "withDrawAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
				
					alert("${f:getMessage('결재 회수 오류 발생')}");
				},

				success:function(data){
					if(data.result) {
						alert(data.message);
						opener.location.reload();
						/*
						if($('#module').val() == 'rohs') {
							opener.location.href = getURLString("rohs", "viewRohs", "do") + "?oid="+data.oid;
						}else {
							opener.location.href = getURLString("doc", "viewDocument", "do") + "?oid="+data.oid;
						}
						*/
						self.close();
					}else {
						alert("${f:getMessage('결재 회수 실패하였습니다.')} \n" + data.message);
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

<form name="withDraw" id="withDraw" method="post" >
<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center">
						<B><font color="white"></font></B>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						
						${f:getMessage('결재 회수')}
						
					</td>
					
					<td>
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="withDrawAction" id="withDrawAction" class="btnCRUD">
										<span></span>
										${f:getMessage('회수')}
									</button>
								</td>
								
								<td>
									<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
		                				<span></span>
		                				${f:getMessage('닫기')}
		                			</button>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr><td height="1" width="100%"></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('결재 회수')}${f:getMessage('방식')} <span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<input type="radio" name="withDrawType" id="withDrawType" value="init" checked="checked">
										<span></span>
										${f:getMessage('결재선 현황 초기화')}
									<input type="radio" name="withDrawType" id="withDrawType" value="keep">
										<span></span>
										${f:getMessage('결재선 현황 유지')}
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>