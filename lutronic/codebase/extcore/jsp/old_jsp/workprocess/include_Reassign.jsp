<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<script>
$(function() {
	$("#entrust").click(function() {
		if( $.trim($("#newUser").val()) == "" ){
			alert("${f:getMessage('위임자')}${f:getMessage('을(를) 선택하세요.')}")
			return false;
		}
		if( $.trim($("#newUser").val()) == $.trim($("#drafter").val())) {
			alert("${f:getMessage('기안자에게는 위임을 할 수 없습니다.')}");
			return;
		}
		
		parent.$("#cmd").get(0).value ="reassign";
		
		//2016.1.13 ECA 활동에선느 formname이 달라서 form 배열 받도록 수정.
		$("form").attr("action", getURLString("groupware", "approveAction", "do")).submit();
	})
})
</script>

<body>

<input type="hidden" name="assignrolename"  id="assignrolename" value="<c:out value="${rolName }"/>">
<input type="hidden" name="workItemOid"		id="workItemOid"	value="<c:out value="${workItemOid }"/>" >
<input type="hidden" name="drafter"			id="drafter"		value="<c:out value="${drafter }"/>" >
<!-- <input type="hidden" name="cmd"			id="cmd"		value="reassign" /> -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="tab_btm2"></td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td  class="tab_btm1"></td>
	</tr>
</table>
<TABLE border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="tdblueM" width="15%">
			${f:getMessage('결재위임하기')}
		</td>
		
		<td class="tdwhiteL" width="250px">
		
			<jsp:include page="/eSolution/common/userSearchForm.do">
				<jsp:param value="single" name="searchMode"/>
				<jsp:param value="newUser" name="hiddenParam"/>
				<jsp:param value="tempnewUser" name="textParam"/>
				<jsp:param value="people" name="userType"/>
				<jsp:param value="" name="returnFunction"/>
			</jsp:include>
		
			<%-- 
			${f:getMessage('위임자')} : 
			<input type="text" name="tempnewUser" id="tempnewUser" readonly size="10" class="txt_field" size="6">
			<input type="hidden" name="newUser" id="newUser">&nbsp;
			<a href="JavaScript:searchUser('','single','newUser','tempnewUser','people')">
				<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
			</a>
			--%>
		</td>
		
		<td class="tdwhiteL">
			<button class="btnCustom" id="entrust" name="entrust" type="button" >
				<span></span>
				${f:getMessage('위임')}
			</button>
		</td>
	</tr>
</table>

</body>
</html>