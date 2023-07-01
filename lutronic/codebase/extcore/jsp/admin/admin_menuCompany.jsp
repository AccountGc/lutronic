<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">

$(function() {
	$("#addDepart").click(function() {
		if($.trim($("#soid").val()) == "") {
			alert("${f:getMessage('상위 부서를 선택하세요.')}");
			return;
		}
		if($.trim($("#cname").val()) == "") {
			alert("${f:getMessage('부서명을 입력하세요.')}");
			return;
		}
		if($.trim($("#ccode").val()) == "") {
			alert("${f:getMessage('부서 코드를 입력하세요.')}");
			return;
		}
		if($.isNumeric($("#csort").val()) == "") {
			alert("${f:getMessage('소트 넘버는 숫자만 입력 가능합니다.')}");
			return;
		}
		actionDepartment("create");
	})
	$("#updateDepart").click(function() {
		if($.trim($("#soid").val()) == "") {
			alert("${f:getMessage('수정할 부서를 선택하세요.')}");
			return;
		}
		actionDepartment("update");
	})
	$("#delDepart").click(function() {
		
		if($.trim($("#soid").val()) == "") {
			alert("${f:getMessage('삭제할 부서를 선택하세요.')}");
			return;
		}
		
		if($("#isLeaf").val() == "0") {
			alert("${f:getMessage('하위 부서가 있는경우 삭제할 수 없습니다.')}");
			return;
		}
		actionDepartment("delete");
	})
})

function actionDepartment(command) {
	$("#command").val(command);
	$("#admin_menuCompany").attr("action", getURLString("admin", "admin_actionDepartment", "do")).submit();
}

<%----------------------------------------------------------
*                      부서 선택시
----------------------------------------------------------%>
function lfn_DeptTreeOnClick(id){
	var name   = deptTree.getAttribute(id, "text");
	var oid    = deptTree.getAttribute(id, "oid");
	var code   = deptTree.getAttribute(id, "code");
	var sort   = deptTree.getAttribute(id, "sort");
	var parentOid = deptTree.getAttribute(id, "parentOid");
	var pcode = deptTree.getAttribute(id, "pcode");
	var isLeaf = deptTree.getAttribute(id, "isLeaf");
	
	//alert("name == " + name + "\noid == " + oid + "\ncode == " + code + "\nsort == " + sort + "\nparentOid == " + parentOid);
	$("#sname").val(name);
	$("#scode").val(code);
	$("#ssort").val(sort);
	$("#sdept").val(pcode);
	$("#soid").val(oid);
	$("#isLeaf").val(isLeaf);
	
	$("#departmentOid").val(oid);
	
	if(code != "ROOT") {
		$("#setDepart").show();
		$("#setDuty").hide();
	}else {
		$("#setDepart").hide();
		$("#setDuty").show();
		$("#departmentOid").val("");
	}
	
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}
</script>

<body>

<form name="admin_menuCompany" id="admin_menuCompany">

<input type="hidden" name="isLeaf" id="isLeaf">
<input type="hidden" name="soid" id="soid">
<input type="hidden" name="command" id="command">

<table width="180" border="0" cellpadding="1" cellspacing="1" bgcolor=AABCC7 align=center>			
	<tr bgcolor="#D9E2E7" align=center><td colspan=2 >${f:getMessage('부서 추가')}</td></tr>
	<tr>
		<td align=center width=80 bgcolor=eeeeee>${f:getMessage('부서명')}</td>
		<td align=center width=100 bgcolor=ffffff>
			<INPUT size=15 name=cname id=cname>
		</td>
	</tr>
	<tr>
		<td align=center nowrap bgcolor=eeeeee>${f:getMessage('부서코드')}</td>
		<td align=center nowrap bgcolor=ffffff>
			<INPUT size=15 name=ccode id=ccode>
		</td>
	</tr>
	<tr>
		<td align=center nowrap bgcolor=eeeeee>${f:getMessage('소트넘버')}</td>
		<td align=center nowrap bgcolor=ffffff>
			<INPUT size=15 name=csort id=csort>
		</td>
	</tr>
	<tr>
		<td colspan=2 align=center bgcolor=ffffff>
			<button type="button" id="addDepart" class="btnCRUD">
				<span></span>
				${f:getMessage('추가')}
			</button>
		</td>
	</tr>
</table>
<!--//테이블1끝//-->
<br>
<!--//테이블2//-->
<table width="180" border="0" cellpadding="1" cellspacing="1" bgcolor=AABCC7 align=center>			
	<tr bgcolor="#D9E2E7" align=center><td colspan=2 >${f:getMessage('선택 부서')}</td></tr>
	<tr>
		<td align=center width=80 bgcolor=eeeeee>${f:getMessage('부서명')}</td>
		<td align=center width=100 bgcolor=ffffff>
			<INPUT size=15 name=sname id=sname >
		</td>
	</tr>
	<tr>
		<td align=center nowrap bgcolor=eeeeee>${f:getMessage('부서코드')}</td>
		<td align=center nowrap bgcolor=ffffff>
			<INPUT size=15 name=scode id=scode >
		</td>
	</tr>
	<tr>
		<td align=center nowrap bgcolor=eeeeee>${f:getMessage('소트넘버')}</td>
		<td align=center width=100 bgcolor=ffffff>
			<INPUT size=15 name=ssort id=ssort >
		</td>
	</tr>
	<tr>
		<td align=center nowrap bgcolor=eeeeee nowrap>${f:getMessage('상위부서코드')}</td>
		<td align=center width=100 bgcolor=ffffff>
			<INPUT size=15 name=sdept id=sdept >
		</td>
	</tr>
	<tr>
		<td bgcolor=ffffff align="center" colspan="2">
			<table width="100%" border="0" align=center>
				<tr>
					<td align="center">
						<button type="button" id="updateDepart" class="btnCRUD">
							<span></span>
							${f:getMessage('수정')}
						</button>
					</td>
					
					<td align="center">
						<button type="button" id="delDepart" class="btnCRUD">
							<span></span>
							${f:getMessage('삭제')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>
			<jsp:include page="/eSolution/department/treeDepartment.do">
				<jsp:param value="ROOT" name="code"/>
			</jsp:include>
		</td>
	</tr>
</table>

</form>

</body>
</html>