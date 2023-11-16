<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	var partOid = "<c:out value='${partOid}'/>";
	var moduleType = "<c:out value='${moduleType}'/>";
	$("#partOid").val(partOid);
	$("#moduleType").val(moduleType);
	var desc = $("#desc").val();
	partExpandAction(partOid,moduleType,desc);
})

$(function() {
	$("#onSelect").click(function() {
		if($("input[name='check']:checked").length == 0){
			alert("${f:getMessage('선택한 품목이 없습니다.')}");
	        return;
		}
		
		var returnArr = new Array();
		
		var val = $("input[name='check']:checked").length;
		for(var i=0; i<val; i++) {
			var checked = $("input[name='check']:checked").eq(i).val();
			var array = checked.split("†");
			returnArr[i] = new Array();
			for(var j=0; j<array.length; j++) {
				returnArr[i][j] = array[j];
			}
		}
		opener.parent.addPart(returnArr,true);
		self.close();
	})
	$("#chkAll").click(function() {
		var checked = this.checked;
		$("input[name='check']").each(function() {
			this.checked = checked;
		})
	})
	$("#desc").change(function() {
		var partOid = $("#partOid").val();
		var moduleType = "<c:out value='${moduleType}'/>";
		partExpandAction(partOid,moduleType,this.value);
	})
})

function partExpandAction(partOid,moduleType,desc) {
	
	$("#expandBody > tr").remove();
	
	var url	= getURLString("part", "partExpandAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{partOid:partOid,moduleType:moduleType, desc:desc} ,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "PartTree ${f:getMessage('목록 오류')}";
			alert(msg);
		},

		success:function(data){
			addPartExpandData(data);
		}
	});
}

function addPartExpandData(data){
	console.log(data);
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			
			var html = "";
			html += "<tr>";
			html += "	<td class='tdwhiteM'>";
			
			if(data[i].isSelect) {
				html += "<input type='checkbox' name='check' value='";
				html += data[i].oid + "†" + data[i].number + "†" + data[i].name + "†" + data[i].rev; 
				html += "' >";
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += data[i].line;
			html += data[i].lineImg;
			html += data[i].icon;
			html += "		<a href=javascript:gotoViewPartTree('" + data[i].oid + "')>";
			html += data[i].number;
			html += "		</a>";
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += data[i].level;
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<a href=javascript:openView('" + data[i].partOid + "')>";
			html += "			<img src='/Windchill/netmarkets/images/details.gif'  border=0>";
			html += "		</a>";
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += data[i].name;
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += data[i].state;
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += data[i].rev + "(" + data[i].partView + ")";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += data[i].unit;
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += data[i].quantity;
			html += "	</td>";
			html += "</tr>";
			
			$("#expandBody").append(html);
		}
	}
}

function gotoViewPartTree(partOid) {
	$("#partOid").val(partOid);
	var moduleType = "<c:out value='${moduleType}'/>";
	var desc = $("#desc").val();
	partExpandAction(partOid,moduleType,desc)
}
</script>

<body>

<input type="hidden" id="partOid" name="partOid" >
<input type="hidden" id="moduleType" name="moduleType" >

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="3" >
    <tr align=center>
        <td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					 <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				   		<tr> 
				   			<td height="30" width="93%" align="center"><B><font color=white>BOM ${f:getMessage('품목')}</font></B></td>
				   		</tr>
					</table>
						<table border="0" cellpadding="1" cellspacing="1" align="right">
			            	<tr>
			                	<td>
			                  		<select name=desc id=desc >
									    <option value="true" >${f:getMessage('정전개')}</option>
										<option value="false">${f:getMessage('역전개')}</option>
									</select>
								</td>
			                 	<td>
			                 		<button type="button" class="btnCustom" id="onSelect">
			                 			<span></span>
			                 			${f:getMessage('선택')}
			                 		</button>
			                 	<td>
			                 		<button type="button" class="btnClose" onclick="self.close();">
			                 			<span></span>
			                 			${f:getMessage('닫기')}
			                 		</button>
			                 	</td>
			             	</tr>
			        	</table>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="3" >
				<tr align=center>
					<td valign="top" style="padding:0px 0px 0px 0px">
			
						<table width="100%"  border="0" cellpadding="1" cellspacing="0"  class="tablehead" align=center>
						    <tr><td height=1 width=100%></td></tr>
						</table>
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  >
						    <tr height=25>
						    	<td class="tdblueM" width="5%"><input name="chkAll" id="chkAll" type="checkbox" class="Checkbox"></td>
						        <td class="tdblueM" width="25%">${f:getMessage('품목번호')}</td>
								<td class="tdblueM" width="4%">level</td>       
						        <td class="tdblueM" width="4%">&nbsp;</td>
						        <td class="tdblueM" width="21%">${f:getMessage('품목명')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('상태')}</td>
						        <td class="tdblueM" width="8%">${f:getMessage('Rev.')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('단위')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('수량')}</td>
						    </tr>
						    
						    <tbody id="expandBody">
						    
						    </tbody>
						    
					    </table>
			    	</td>
			    </tr>
			</table>
				 
		</td>
	</tr>
</table>

</body>
</html>