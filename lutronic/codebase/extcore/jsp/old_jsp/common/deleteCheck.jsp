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
	$("#cancleBtn").click(function () {
		parent.opener.close();
		self.close();
	});
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		var form = $("form[name=deleteCheck]").serialize();
		var url	= getURLString("changeECO", "deleteECOAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.msg);
				if(data.result) {
					/* if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
						parent.opener.location.reload();
					}else {
						parent.opener.$("#sessionId").val("");
						parent.opener.lfn_Search();
					} */
					if(parent.opener.parent.opener.$("#sessionId").val() == "undefined" || parent.opener.parent.opener.$("#sessionId").val() == null){
						parent.opener.parent.opener.location.reload();
					}else {
						parent.opener.parent.opener.$("#sessionId").val("");
						parent.opener.parent.opener.lfn_Search();
					}
					parent.opener.window.close();
					window.close();
				}else {
					document.location = data.view;
				}
			}
		});
	});
})
</script>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<form name=deleteCheck>
<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />">
<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				<tr> 
					<td height=30 width=99% align=center><B><font color=white></font></B></td>
				</tr>
			</table>
			
			<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1" >
				<tr>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp;${f:getMessage('객체 삭제 재확인')}
					</td>
					
					<td align="right">
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
			
			<table width="100%" border="0" align="center" cellpadding="1" cellspacing="1"  class=9pt >
				<tr>
					<td >
								<h2>
									<strong>정말로 해당 설계변경을 삭제하시겠습니까? </strong>
								</h2>
								<h2>
									<strong>*설계변경을 삭제할 경우, 복구가 불가능합니다. </strong>
								</h2>
								<!-- <h2>
									<strong>또한, 설계변경일 경우 설계변경을 통해 개정한 부품들을 복구해야 합니다. </strong>
								</h2> -->
								<h2>
									<strong>반드시 관리자와 상의 후 삭제해주시기 바랍니다. </strong>
								</h2>
								<h2>
									<strong>정말로 삭제를 원하시는 경우 확인을 눌러주세요.</strong>
								</h2>
							</td>
				</tr>
				<tr>
					<td align="center">
										<button type="button" name="deleteBtn" id="deleteBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('확인')}
										</button>
										<button type="button" name="cancleBtn" id="cancleBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('취소')}
										</button>
									</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>