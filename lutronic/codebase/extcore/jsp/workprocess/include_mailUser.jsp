<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

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
	$("#addMailuser").click(function() {
		var workOid = "<c:out value='${workOid}'/>";
		var url = getURLString("groupware", "emailUserList", "do") + "?workOid="+workOid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      관련 문서 삭제
	----------------------------------------------------------%>
	$("#delMailUser").click(function() {
		
		var oids = "";
		
		var obj = $("input[name='check']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				//obj.eq(i).parent().parent().remove();
				//alert(obj.attr("value"));
				oids += obj.eq(i).attr("value") + ",";
			}
		}
		emailUserAction(oids);
		
	});
	$(document).on('click', 'input:checkbox[name="allCheck"]', function(){
		if(this.checked) {
			$("input[name='check']").prop("checked", "checked");
		}else {
			$("input[name='check']").prop("checked", "");
		}
	})
})

function addEmailUser(obj){
	for (var i = 0; i < obj.length; i++) {
		if(duplicateCheck(obj[i][0])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='check' value='" + obj[i][0] + "'>";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][2];
			html += "	</td>";
			
			html += "</tr>";
	
			$("#mailUserBody").append(html);
		}
	}
}

function duplicateCheck(mailOid) {
	var chkLen = $("input[name='check']").length;
	
	for(var i=0; i<chkLen; i++) {
		var chkObj = $("input[name='check']").eq(i);
		if(mailOid == chkObj.attr("value")) {
			return false;
		}
	}
	return true;
}

function emailUserAction(linkOid) {
	var workOid = "<c:out value='${workOid }'/>";
	var command = "delMailUser";
	var url	= getURLString("groupware", "emailUserAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {workOid: workOid,command: command, linkOid: linkOid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
		success:function(data){
			if(data.result) {
				var linkOids = data.linkOid.split(",");
				var obj = $("input[name='check']");
				var checkCount = obj.size();
				for (var i = 0; i < checkCount; i++) {
					if (obj.eq(i).is(":checked")) {
						obj.eq(i).parent().parent().remove();
					}
				}
			}else {
				alert("${f:getMessage('외부 메일 삭제시 오류가 발생 하였습니다.')}");
				return;
			}
		}
	});
}
</script>

<body>

<table id="mailUserInnerTempTable" style="display:none">
    <tr>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
    </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td width="*" align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('외부 메일 지정')}</b>
		</td>
		<td>
		<!-- 버튼 테이블 시작 -->
		<table border="0" cellpadding="0" cellspacing="4" align="right">
              <tr>
             	<td>
             		<button type="button" id="addMailuser" class="btnCustom">
             			<span></span>
             			${f:getMessage('추가')}
             		</button>
             	</td>
             	
				<td>
					<button type="button" id="delMailUser" class="btnCustom">
             			<span></span>
             			${f:getMessage('삭제')}
             		</button>
				</td>
			</tr>
       </table>
           <!-- 버튼 테이블 끝 -->
	</tr>
</table>

<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	<tr>
		<td height=1 width=100%></td>
	</tr>
</table>

<table id="mailUserTable" border="0" cellpadding="0" cellspacing="0" width="100%"> <!--//여백 테이블-->
	<tr> 
	
		<td class="tdblueM" width="10%" height="23">
			<input type=checkbox name=allCheck id=allCheck>
		</td>
		<td class="tdblueM" width="35%">${f:getMessage('이름')}</td>
		<td class="tdblueM" width="55%" height="23">${f:getMessage('메일')}</td>
	</tr>
	
	<tbody id="mailUserBody">
	
		<c:forEach items="${list }" var="list">
			<tr>
				<td class="tdwhiteM">
					<input type="checkbox" name="check" value="<c:out value='${list.oid }'/>">
				</td>
				
				<td class="tdwhiteM">
					<c:out value='${list.name }'/>
				</td>
				
				<td class="tdwhiteM">
					<c:out value='${list.email }'/>
				</td>
			
			</tr>
		
		</c:forEach>
	
	
	</tbody>
	
</table>

</body>
</html>