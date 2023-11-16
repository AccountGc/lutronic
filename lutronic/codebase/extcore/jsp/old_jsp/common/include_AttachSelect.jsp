<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var attachName = '';

$(document).ready(function() {
	var type = "<c:out value='${type}' />";
	if(type == null || type == '') {
		attachName = 'secondary';
	}
})


$(function() {
	$("#addFileBtn").unbind("click").click(function() {
		var html = "";
		html += "<tr align=center bgcolor=#ffffff align=center>";
		html += "	<td id=tb_gray width=3% height=25>";
		html += "		<input type='checkbox' name=fileDelete>";
		html += "	</td>";
		html += "	<td id=tb_gray width=97% align=left>" + attachName;
		html += "		<input type='file' name='" + attachName + "' style=width:99% >";
		html += "	</td>";
		html += "</tr>";
		
		$("#secondBody").append(html);
	}),
	$("#delFile").unbind("click").click(function() {
		var obj = $("input[name='fileDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
})

</script>

<body>

<c:choose>
	<c:when test="${type eq 'p' }">
		<input name="primary" id="primary" type="file"  class="txt_field" size="80" border="0">
	</c:when>
	
	<c:otherwise>
		<table width="100%" align="center">
			<tr> 
				<td height="25">
		
					<table border=0 cellpadding="0" cellspacing=2 align="left">
		                <tr>
					        <td>
								           	
		                    	<button type="button" class="btnCustom" title="${f:getMessage('추가')}" name="addFileBtn" id="addFileBtn">
				                  	<span></span>
				                  	${f:getMessage('추가')}
			                  	</button>
			                </td>
			                
			                <td>
			                  	<button type="button" class="btnCustom" title="${f:getMessage('삭제')}" name="delFile" id="delFile">
				                  	<span></span>
				                  	${f:getMessage('삭제')}
			                  	</button>
		                    </td>
		                </tr>
		            </table>		
				</td>									
			</tr>
		</table>
		
		<table width="100%" cellspacing="0" cellpadding="1" border="0" id="second" align="center">
			<tr bgcolor="#f1f1f1"  align=center>
				<td width="100%" height="22" colspan="2" id=tb_inner></td>
			</tr>
			
			<tbody id="secondBody">
			</tbody>
			
		</table>
	
	</c:otherwise>
</c:choose>
</body>
</html>