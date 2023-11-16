<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
var searchUserId = "";				// 담당자 검색시 ID를 저장하기 위한 변수
$(document).ready(function() {
	var eoType = "<c:out value='${eoType}'/>";
	var ecaOid = "<c:out value='${ecaOid}'/>";
	EOActivityLoad(eoType, ecaOid);
	
	var cnt = $("#activityBody > tr").length;
	
	for(var i=0; i<cnt; i++) {
		gfn_InitCalendar("finishDate_" + i, "finishDate_" + i + "_btn");
	}
})

$(function() {
	<%----------------------------------------------------------
	*                      각 활동에 대한 체크박스 동작
	----------------------------------------------------------%>
	$("input[name=activity]").click(function() {
		var id = this.id
		if("true" == $("#"+id).attr("isNeed")) {
			alert($("#"+id).attr("activeName")+"${f:getMessage('변경은 필수사항 입니다.')}");
			this.checked = true;
		}
	})
	<%----------------------------------------------------------
	*                      전체 활동에 대한 체크박스 동작
	----------------------------------------------------------%>
	$("#allCheck").click(function() {
		if(this.checked) {
			$("input[name=activity]").each(function() {
				this.checked = true;
			})
		}else {
			var len = $("input[name=activity]").length;
			for(var i=0; i<len; i++) {
				var id = $("input[name=activity]").eq(i).attr("id");
				if($("#"+id).is(":checked")) {
					if("true" == $("#"+id).attr("isNeed")) {
						alert($("#"+id).attr("activeName")+"${f:getMessage('변경은 필수사항 입니다.')}");
						$("#"+id).prop("checked", "checked");
					}else {
						$("#"+id).prop("checked", "");
					}
				}else {
					$("#"+id).prop("checked", "");
				}
			}
		}
	})
	<%----------------------------------------------------------
	*                      이미지이면서 이름이 searchUser에 대한 이미지의 동작 제어
	----------------------------------------------------------%>
	$("img[name='searchUser']").click(function() {
		searchUserId = this.id;
		searchUser('createNewProduct','single','','','','setEOActivityUser');
	})
	<%----------------------------------------------------------
	*                      등록
	----------------------------------------------------------%>
	$("#active_create").click(function () {
		var len = $("input[name=activity]").length;
		for(var i=0; i<len; i++) {
			if($("input[name=activity]").eq(i).is(":checked")) {
				var obj = $("input[name=activity]").eq(i);
				
				var activeCode = obj.attr("activeCode");
				
				if($.trim($("#activeUser_"+activeCode).val()) == "") {
					alert(obj.attr("activeName") + "${f:getMessage('활동 담당자를 선택하세요.')}");
					return false;
				}
				
				if($.trim($("#finishDate_"+i).val()) == "") {
					alert(obj.attr("activeName") + "${f:getMessage('활동 요청 완료일을 선택하세요.')}");
					return false;
				}
				
				if($.trim($("#finishDate_"+i).val()) != "") {
					if(!gfn_compareDateToDay($("#finishDate_"+i).val())) {
						alert("${f:getMessage('요청완료일은 오늘 날짜 이전일 수 없습니다.')}");
						return false;
					}
				}
			}
		}
		
		if(!confirm("${f:getMessage('등록하시겠습니까?')}")){
			return;
		}
		
		$("#ecaAction").val("ecaSetting");
		$("#ECAProcess").attr("target", "hiddenFrame");
		$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
	})
})

<%----------------------------------------------------------
*                      eoType 활동에 대한 리스트 가져오기
----------------------------------------------------------%>
function EOActivityLoad(eoType, ecaOid) {
	var url	= getURLString("change", "getEOActivityList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{eoType:eoType, ecaOid:ecaOid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			addEOActivity(data);
		}
	});
}

<%----------------------------------------------------------
*                      각 활동에 대한 리스트 추가
----------------------------------------------------------%>
function addEOActivity(data){
	if(data.length > 0) {
		for(var i=0; i < data.length; i++){
			var html = "";
			html += "<tr>";
			html += "	<td class=tdwhiteM>";
			html += data[i].activeGroupName;
			html += "	<input type='hidden' name='ecaCheck' value='" + data[i].ecaCheck + "'>";
			html += "		<span class=style1 >";
			html += data[i].activeNeeds;
			html += "		</span>";
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<input type=checkbox name=activity id="+i+" value='" + data[i].oid + "' isNeed='" + data[i].isNeed + "' activeName='" + data[i].name + "' activeCode='" + data[i].activeCode + "' " + data[i].checked + ">";
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += data[i].name;
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<span id='tempDepart_" + data[i].activeCode + "'>" + data[i].activeDepart + "</span>";
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<input type=hidden name=activity_value>";
			html += "		<input type=hidden name='activeUser_" + data[i].activeCode + "' id='activeUser_" + data[i].activeCode + "' value='" + data[i].peopleOid + "'>";
			html += "		<table width=100% border=0 cellpadding=0 cellspacing=1>";
			html += "			<tr align=center>";
			html += "				<td align=left width=95%>";
			html += "					<span id='tempactivity_" + data[i].activeCode + "'>" + data[i].activeUserName;
			
			if(data[i].peopleOid != "") {
				html += "					<a href=javascript:cancelUserDepart('activeUser_" + data[i].activeCode + "','tempactivity_" + data[i].activeCode + "','tempDepart_" + data[i].activeCode + "','')>";
				html += "					 X";
				html += "					</a>";
			}
			
			html += "					</span>";
			
			html += "				</td>";
			html += "				<td align=right width=50px>";
			html += "					<img src=/Windchill/jsp/portal/images/s_search.gif border=0 id=" + data[i].activeCode +" name=searchUser style=cursor:pointer>";
			html += "				</td>";
			html += "			</tr>";
			html += "		</table>";
			html += "	</td>";
			html += "	<td class=tdwhiteL>";
			html += "		<input name='finishDate_" + data[i].activeCode + "' id='finishDate_" + i + "' class=txt_field size=12 value='" + data[i].finishDate + "' readonly>";
			html += "		<img src=/Windchill/jsp/portal/images/calendar_icon.gif border=0 id='finishDate_" + i + "_btn' style=cursor:pointer>";
			html += "		<a href=javascript:clearText('finishDate_" + i + "')>";
			html += "			<img src=/Windchill/jsp/portal/images/x.gif border=0>";
			html += "		</a>";
			html += "	</td>";
			html += "</tr>";
			
			$("#activityBody").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      담당자 선택시 등록되는 데이터 제어
----------------------------------------------------------%>
function setEOActivityUser(arrObj) {
	$("#tempDepart_"+searchUserId).html(arrObj[0][3]);
	$("#activeUser_"+searchUserId).val(arrObj[0][0]);
	
	var html = "";
	html += arrObj[0][1];
	html += " <a href=javascript:cancelUserDepart('activeUser_" + searchUserId + "','tempactivity_" + searchUserId + "','tempDepart_" + searchUserId + "','')>";
	html += "X";
	html += "</a>";
	
	$("#tempactivity_"+searchUserId).html(html);
}

</script>

<body>
<input type="hidden" name="aaa" id="aa" value="<c:out value='${eoType}'/>" >
<input type="hidden" name="ecaAction" id="ecaAction" >
<input type="hidden" name="ecaOid" id="ecaOid" value="<c:out value="${ecaOid }"/>">

<iframe src="" name="hiddenFrame" id="hiddenFrame" scrolling=no frameborder=no marginwidth=0 marginheight=0 style="display:none"></iframe>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td height="10">&nbsp;</td></tr>
	<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*">
						<img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('활동설정')}</b>
					</td>
					
					<td align="right" width="80">
						<button type="button" class="btnCRUD" id="active_create">
							<span></span>
							${f:getMessage('활동등록')}
						</button>
					</td>		
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
	
			<table border="0" cellspacing="0" cellpadding="0" width="100%">	
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">${f:getMessage('활동')} <span class="style1">*</span></td>
                 	<td class="tdwhiteL" colspan=3>
                 		<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  !style="tdisplay:none;able-layout:fixed">
							<tr>
								<td  class="tdblueM"  width=15%>${f:getMessage('단계')}</td>
								<td  class="tdblueM"  width=5%><input type="checkbox" name="allCheck" id="allCheck" checked >&nbsp;</td>
								<td  class="tdblueM"  width=25%>${f:getMessage('활동명')}</td>
								<td  class="tdblueM"  width=20%>${f:getMessage('담당부서')}</td>
								<td  class="tdblueM"  width=15%>${f:getMessage('담당자')}</td>
								<td  class="tdblueM"  width=20%>${f:getMessage('요청 완료일')}</td>
							</tr>
							
							<tbody id="activityBody">
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