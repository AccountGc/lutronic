<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	$("#iframe1").attr("src", getURLString("admin", "admin_numberCodeTree", "do") + "?codeType=PARTTYPE");
});

$(function() {
	$("#iframe1").load(function() {
		//$("#iframe1").css({"height": + $("#mainTable").height() + "px"});
	})
})

function gotoView(tree,type) {
	if(tree == "true") {
		
		$("#iframe1").attr("src", getURLString("admin", "admin_numberCodeTree", "do") + "?codeType="+ type);
		
	}else {
		
		$("#iframe1").attr("src", getURLString("admin", "admin_numberCodeList", "do") + "?codeType="+ type);
	}
}

</script>

<form name="admin_numberCode" id="admin_numberCode">

<table id="mainTable" width="100%" height="100%" border="0" cellpadding="0" cellspacing="1" bgcolor=EDE9DD>

	<tr height="100%">
		<td bgcolor="EDE9DD" valign="top">
			<!-- 왼쪽서브메뉴 -->
			<table style="width: 200px" border="0" cellpadding="0" cellspacing="1">		
			
				<c:forEach items="${list }" var="list">
					<tr height="20" style="padding-left:10">
						<td height="25" bgcolor=white>
							<a href="javascript:gotoView('<c:out value="${list.tree }" />','<c:out value="${list.value }" />')" >
								 <c:out value="${list.name }" />
							</a>
						</td>
					</tr>
				</c:forEach>
					
			</table>
		</td>

		<!--  코드 체계 관리 -->

		<td width="100%" valign="top" bgcolor=white>
			<iframe frameborder=0 style="width: 100%; overflow: scroll; height:700px" id=iframe1>
			</iframe>
		</td>

	</tr>
</table>

</form>

