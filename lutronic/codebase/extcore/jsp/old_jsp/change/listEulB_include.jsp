<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	listEulB_IncludeAction();	
})

function listEulB_IncludeAction(){
	var partOid = "<c:out value='${partOid }'/>";
	var allBaseline = "<c:out value='${allBaseline }'/>";
	var baseline = "<c:out value='${baseline }'/>";;
	var url	= getURLString("change", "listEulB_IncludeAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{partOid:partOid, allBaseline:allBaseline, baseline:baseline},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "PartTree ${f:getMessage('목록 오류')}";
			alert(msg);
		},

		success:function(data){
			addlistEulB_Include(data);
		}
	});
}

function addlistEulB_Include(data) {
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "";
			html += "<tr>";
			html += "	<td class='td" + data[i].color + "M'>";
			html += "		<input type=radio name=baseCheck value='" + data[i].partOid +"$" + data[i].baseOid + "'>";
			html += "	</td>";
			
			html += "	<td class='td" + data[i].color + "M'>";
			html += data[i].baseName;
			html += "	</td>";
			
			html += "	<td class='td" + data[i].color + "M'>";
			html += data[i].partVersion
			html += "	</td>";
			
			html += "	<td class='td" + data[i].color + "M'>";
			html += data[i].baseDate
			html += "	</td>";
			
			html += "	<td class='td" + data[i].color + "M'>";
			html += "		<a href=javascript:baselinePartTree('" + data[i].baseOid + "')>"
			html += "			BaseLine ${f:getMessage('전개')}";
			html += "		</a>";
			html += "	</td>";
			html += "</tr>";
			
			$("#listEulB").append(html);
		}
	}
}

</script>

<style>
.tdkhakiM {
    border-bottom-width: 1px;
    border-bottom-style: solid;
    border-bottom-color: #e6e6e6  ;      
    border-right-width: 1px;
    border-right-style: solid;
    border-right-color: #e6e6e6;
    background-color: #f0e68c  ;
    height: 22px;
    color:#3f2c12;
    padding-top: 2px;
    text-align: center;
    font-family:Dotum;;
    font-size: 12px;
}
.tdkhakiL {
    border-bottom-width: 1px;
    border-bottom-style: solid;
    border-bottom-color: #e6e6e6 ;
    border-right-width: 1px;
    border-right-style: solid;
    border-right-color: #e6e6e6;
    background-color: #f0e68c  ;
    height: 22px;
    color:#3f2c12;
    padding-top: 2px;
    text-align: left;
    font-family:Dotum;;
    font-size: 12px;
}
</style>

<body>

<input type="hidden" name="partOid" id="partOid" value="<c:out value="${partOid }"/>">
<input type="hidden" name="allBaseline" id="allBaseline" value="<c:out value="${allBaseline }"/>">


<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
        <tr><td height=1 width=100%></td></tr>
    </table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  style="table-layout:fixed">
	<tr height=25>
	    <td class="tdblueM" width="3%">&nbsp;</td>
	    <td class="tdblueM" width="30%">ECO</td>
	    <td class="tdblueM" width="10%">Rev.</td>
	    <td class="tdblueM" width="10%">${f:getMessage('변경일자')}</td>
	    <td class="tdblueM" width="10%">BOM</td> 
	</tr>
	
	<tbody id="listEulB">
	
	</tbody>
	
</table>

</body>
</html>