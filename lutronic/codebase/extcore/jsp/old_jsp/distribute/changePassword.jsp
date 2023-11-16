<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#update").click(function() {
		var field_view = $('#field_view').val();
		var _field_view = $('#_field_view').val();
		
		if(field_view.search(/\s/) != -1) {
			alert("${f:getMessage('공백없이 입력해 주세요')}");
			return;
		}
		/*
		if(8 > field_view.length || 20 < field_view.length) {
			alert("${f:getMessage('비밀번호는 최소 8자이상 20자 이하 입니다.')}");
			return;
		}
		
		if(/(\w)\1\1/.test(field_view)) {
			alert("${f:getMessage('동일 키를 3개 이상 연속적으로 입력할 수 없습니다.')}");
			return;
		}
		
		var len_num = field_view.search(/[0-9]/g);
		var len_eng = field_view.search(/[A-z]/g);
		var len_asc = field_view.search(/\W|\s/g);
		
		if(len_num < 0 || len_eng < 0 || len_asc < 0) {
			alert("${f:getMessage('알파벳, 숫자, 특수문자를 조합하여 입력해야 합니다.')}");
			return;
		}
		*/
		if(field_view != _field_view) {
			alert("${f:getMessage('비밀번호가 다릅니다.')}");
			return;
		}
		
		$("#changePasswordForm").attr("action", getURLString("groupware", "changePasswordAction", "do")).submit();
	})
})

</script>

<body>

<form name="changePassworDForm" id="changePasswordForm" method="get">

<input type="hidden"	name="isPop"	id="isPop"	value="<c:out value="${isPop }"/>" />

<input type="hidden" name="field" id="field" value="replace">
<input type="hidden" name="id" id="id" value="<c:out value="${id }"/> ">

<c:if test="${isPop eq 'false' }">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의 업무')} > ${f:getMessage('비밀번호 변경')} 
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</c:if>

<table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
<tr  height=5><td>

<table width="100%" border="0" cellpadding="10" cellspacing="3">
	<tr  align=center height=5>
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="5" cellspacing="1" class="tablehead" align=center>
				<tr><td height=3 width=100%></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr  align=left>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="5" cellspacing="1" bgcolor=003A9D align=center>
			<col width='20%'><col width='75%'>

				<tr bgcolor="ffffff" height=35>
					<td class="small" colspan=2>[<c:out value="${id }"/>]${f:getMessage('비밀번호 변경 시 로그인을 다시 해야 합니다.')}</td>
				</tr>
				<tr bgcolor="ffffff" height=35>
					<td><b>${f:getMessage('변경 비밀번호')}<span class="style1">*</span></b></td>
					<td>
						<input type=password name="field_view" id="field_view" class="txt_field" size="85" style="width:30%"/>
					</td>
				</tr>
				<tr bgcolor="ffffff" height=35>
					
					<td><b>${f:getMessage('변경 비밀번호 확인')}<span class="style1">*</span></b></td>
					<td>
						<input type=password name="_field_view" id="_field_view" class="txt_field" size="85" style="width:30%"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center" colspan=2>
            <table border="0" cellpadding="0" cellspacing="4" align="center">
                <tr>
                    <td>
                    	<button type="button" id="update" name="update" class="btnCRUD">
                    		<span></span>
                    		${f:getMessage('수정')}
                    	</button>
                    </td>
                    
                    <c:choose>
	                    <c:when test="${isPop eq 'false' }">
		                    <td>
		                   		<button type="button" class="btnCustom" onclick="javascript:history.back();">
		                    		<span></span>
		                    		${f:getMessage('뒤로')}
		                    	</button>
		                    </td>
	                    </c:when>
	                    
	                    <c:otherwise>
	                    	<td>
		                   		<button type="button" class="btnCustom" onclick="self.close();">
		                    		<span></span>
		                    		${f:getMessage('닫기')}
		                    	</button>
		                    </td>
	                    </c:otherwise>
                    </c:choose>
                </tr>
            </table>
		</td>
	</tr>

</table>
		
</td></tr>
</table>

</form>
</body>
</html>