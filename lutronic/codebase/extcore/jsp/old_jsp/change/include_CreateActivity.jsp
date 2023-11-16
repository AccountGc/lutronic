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
	setRootDefinition('');
	getEOActivity();
})

$(function() {
	
	<%----------------------------------------------------------
	*                      전체 활동에 대한 체크박스 동작
	----------------------------------------------------------%>
	$("#allCheck").click(function() {
		//alert(this.checked);
		if(this.checked) {
			/*
			$("input[name=active_checkbox]").each(function() {
				this.checked = true;
			})
			*/
			var len = $("input[name=active_checkbox]").length;
			for(var i=0; i<len; i++) {
				var id = $("input[name=active_checkbox]").eq(i).attr("id");
				var modify = $("input[name=active_checkbox]").eq(i).attr("modify");
				//alert(modify)
				if(modify == "true"){
					$("#"+id).prop("checked", "true");
				}
			}
		}else {
			var len = $("input[name=active_checkbox]").length;
			for(var i=0; i<len; i++) {
				var id = $("input[name=active_checkbox]").eq(i).attr("id");
				
				$("#"+id).prop("checked", "");
				
			}
		}
	})
	<%----------------------------------------------------------
	*                      이미지이면서 이름이 searchUser에 대한 이미지의 동작 제어
	----------------------------------------------------------%>
	$("img[name='searchUser']").click(function() {
	
		//alert("searchUser")
		//searchUserId = this.id;
		//searchUser('createECO','single','','','','setEOActivityUser');
		//'createECO','single','activeUser_"+rowId+"','activeUser_"+rowId+"','wtuser','setEOActivityUser'
	})
	
	<%----------------------------------------------------------
	*                      활동 추가 
	----------------------------------------------------------%>
	$("#createActivity").click(function() {
		var url = getURLString("changeECA", "createActivity", "do") + "?oid="+$("#rootOid").val();
		openOtherName(url,"window","500","500","status=no,scrollbars=yes,resizable=yes");
		
		
	})
	
	<%----------------------------------------------------------
	*                      활동 삭제
	----------------------------------------------------------%>
	$("#deleteActivity").click(function() {
		
		var tableId = "active_table";
		var checkboxName = "active_checkbox";

		var table = document.getElementById(tableId);
		var activeList = document.getElementsByName(checkboxName);

	    // #. 추가된 관련 문서 목록이 존재 여부 판단
	    if (activeList == null || activeList.length < 1) {
	    	return;
	    }
	    
	    // #. 체크박스 선택된 row 삭제
	    for(var i = activeList.length - 1 ; i >= 0 ; i--) {
	    	if (activeList.item(i).checked) {
	    		/*
	    		if(table.rows[i+1].cells.length > 6){
	    			if( table.rows[i+2]!=null && table.rows[i+2].cells.length == 6){
	    				var stepCell = table.rows[i+2].insertCell(0);
	    				stepCell.setAttribute("align", "center");
	    				stepCell.innerHTML = table.rows[i+1].cells[0].innerHTML;
	    			}
	    		}
	    		*/
				//table.deleteRow(i+1);
	    		table.deleteRow(i);
			}
		}
	    
	    //resetRowspan();
	    
	    // #. 체크박스 체크 해제
	  //  var toggleCheckbox = getToggleCheckbox(table);
		//if (toggleCheckbox != null) {
			//toggleCheckbox.checked = false;
		//}
	})
	
	
	
	<%----------------------------------------------------------
	*                      Root 변경시 
	----------------------------------------------------------%>
	$("#rootOid").change(function() {
		
		//alert($("#rootOid").val());
		if($("#rootOid").val() ==""){
			/*
			if (!confirm("${f:getMessage('활동을 초기화 하시겠습니까')}")){
				return;
			}
			*/
			initActivityTable();
		}else{
			setECAActivityLoad($("#rootOid").val());
		}
		
	})
})
function getEOActivity(){
	var eoOid = $("#oid").val();
	var cmd = $("#cmd").val();
	if(cmd == "save"){
		return;
	}
	var url	= getURLString("changeECA", "getEOActivity", "do");
	//alert(eoOid);
	$.ajax({
		type:"POST",
		url: url,
		data:{oid:eoOid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			//initActivityTable();
			//alert(data);
			addECActivity(data);
		}
	});
}
<%----------------------------------------------------------
*                      Root 타임에  활동에 대한 리스트 가져오기
----------------------------------------------------------%>
function setECAActivityLoad(rootOid) {
	var url	= getURLString("changeECA", "setActiveDefinition", "do") +"?oid="+rootOid;
	$.ajax({
		type:"POST",
		url: url,
		data:{oid:rootOid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			initActivityTable();
			addECActivity(data);
		}
	});
}

<%----------------------------------------------------------
*                     Root 변경시 활동 리스트 초기화
----------------------------------------------------------%>
function initActivityTable(){
	
	var tableId = "active_table";
	var checkboxName = "active_checkbox";

	var table = document.getElementById(tableId);
	var activeList = document.getElementsByName(checkboxName);

    // #. 추가된 관련 문서 목록이 존재 여부 판단
    if (activeList == null || activeList.length < 1) {
    	return;
    }
    
    // #. row 삭제
    for(var i = activeList.length - 1 ; i >= 0 ; i--) {
    		table.deleteRow(i);
	}
    
	
}
function addECActivity(data){
	
	
	if(data.length > 0) {
		for(var i=0; i < data.length; i++){
			setActivityTable(data[i]);
			/*
			var html = "";
			html += "<tr>";
			html += "	<td class=tdwhiteM>";
			html += data[i].stepName;
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<input type=checkbox name=activity id="+i+" value='" + data[i].oid + "'>";
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += data[i].name;
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<span id='tempDepart'>" + data[i].departName + "</span>";
			html += "	</td>";
			html += "	<td class=tdwhiteM>";
			html += "		<input type=hidden name='activeUser' value='" + data[i].activeUserOid + "'>";
			html += "		<table width=100% border=0 cellpadding=0 cellspacing=1>";
			html += "			<tr align=center>";
			html += "				<td align=left width=95%>";
			html += "					<span id='tempactivityUser '>" + data[i].activeUserName+"</span>";
			
			if(data[i].activeUserOid != "") {
				html += "					<a href=javascript:cancelUserDepart('activeUser','tempactivityUser','tempDepart','')>";
				html += "					 X";
				html += "					</a>";
			}
			
			html += "					</span>";
			
			html += "				</td>";
			html += "				<td align=right width=50px>";
			html += "					<img src=/Windchill/jsp/portal/images/s_search.gif border=0 name=searchUser style=cursor:pointer>";
			html += "				</td>";
			html += "			</tr>";
			html += "		</table>";
			html += "	</td>";
			html += "	<td class=tdwhiteL>";
			html += "		<input name='finishDate_"+i+ "' id='finishDate_" + i + "' class=txt_field size=12 value='' readonly>";
			html += "		<img src=/Windchill/jsp/portal/images/calendar_icon.gif border=0 id='finishDate_" + i + "_btn' style=cursor:pointer>";
			html += "		<a href=javascript:clearText('finishDate_" + i + "')>";
			html += "			<img src=/Windchill/jsp/portal/images/x.gif border=0>";
			html += "		</a>";
			html += "	</td>";
			html += "</tr>";
			
			$("#activityBody").append(html);
			*/
		}
	}
	

}

<%----------------------------------------------------------
*                      각 활동에 대한 리스트 추가
----------------------------------------------------------%>
var rowId = 0;
function setActivityTable(data){
	
	
	var tableId = "active_table";
	var checkboxName = "active_checkbox";
	var stepName = "step";

	var table = document.getElementById(tableId);
	var stepList = document.getElementsByName(stepName);
	var rowposition = table.rows.length;
	
	// STEP 순서에 따라 추가
	var stepSortList = document.getElementsByName("stepSort");
	var nextSort = "";
	var sortIdx = 0 ;
	var tempidx =1;
	var noraml ="O"
	if(stepSortList!=null){
	   for(var i =0 ; i < stepSortList.length ; i++) {
	    	
	    	if(data.stepSort == stepSortList.item(i).value){
	    		break;
	    	}else if(data.stepSort<stepSortList.item(i).value){
	    		//alert(data.stepSort +":"+stepSortList.item(i).value +";"+i);
	    		nextSort = stepSortList.item(i).value;
	    		sortIdx = tempidx-1;
	    		noraml ="X";
	    		break;
	    	}
	    	tempidx++;
		}
	}
	
	var count = 0;
	var tableRowIdx =0 ;
	if(stepList!=null){
	    for(var i = stepList.length - 1 ; i >= 0 ; i--) {
	    	if(data.step == stepList.item(i).value){
	    		count++;
	    		tableRowIdx = i ;
	    	}
		}
	}
	if(stepList != null && count==0){
		count = stepList.length;
	}
	
	if(sortIdx > 0 ){
		rowposition = sortIdx;
	}else if(sortIdx <0){
		rowposition = 0;
	
	}else{
		if(stepSortList!=null && noraml=="X"){
			rowposition = sortIdx;
		}else{
			rowposition = tableRowIdx+count;
		}
		
	}
	
	//alert(data.step+ ","+ "count ="+count+",tableRowIdx="+tableRowIdx +",sortIdx="+ sortIdx+"  = rowposition ="+rowposition );
	var row = table.insertRow(rowposition);
	
	row.className = 'hidden';
	var count = 0;

	var stepCell = row.insertCell(count++);stepCell.className="tdwhiteM";
	var checkboxCell = row.insertCell(count++);checkboxCell.className="tdwhiteM";
	var activityNameCell = row.insertCell(count++);activityNameCell.className="tdwhiteM";
	var activitytypeCell = row.insertCell(count++);activitytypeCell.className="tdwhiteM";
	var departNameCell = row.insertCell(count++);departNameCell.className="tdwhiteM";
	var activeUser = row.insertCell(count++);activeUser.className="tdwhiteM";
	var dateCell = row.insertCell(count++);dateCell.className="tdwhiteM";
	var descriptionCell = row.insertCell(count++);
	var isModify = data.isModify;
	stepCell.setAttribute("align", "center");
	checkboxCell.setAttribute("align", "center");
	activityNameCell.setAttribute("align", "left");
	activitytypeCell.setAttribute("align", "center");
	departNameCell.setAttribute("align", "center");
	activeUser.setAttribute("align", "center");
	dateCell.setAttribute("align", "center");
	
	stepCell.innerHTML = data.stepName;
	descriptionCell.setAttribute("align", "center");
	
	//수정 여부 체크
	var disabled ="";
	if(!isModify){
		disabled = "disabled";
	}
	
	checkboxCell.innerHTML 		= "<input type=\"checkbox\" name=\"" + checkboxName + "\" id="+rowId+" "+disabled+" modify="+isModify+">" 
								+ "<input type=\"hidden\" name=\"" +stepName+ "\" value=\""+data.step+"\" >"
								+ "<input type=\"hidden\" name=\"stepSort\" value=\""+data.stepSort+"\" >"
								+ "<input type=\"hidden\" name=\"rowId\" value=\""+rowId+"\" >"
								+ "<input type=\"hidden\" name=\"ecaOid\" id=\"ecaOid_"+rowId+"\" value=\""+data.oid+"\" >";
			
	//alert(data.oid);
	
	activityNameCell.innerHTML 	= data.name+ "<input type=\"hidden\" name=\"active_name\" value=\""+data.name+"\">";
	activitytypeCell.innerHTML 	= data.activityName+ "<input type=\"hidden\" name=\"active_type\" value=\""+data.activityType+"\">"
	departNameCell.innerHTML 	= "<span id='tempDepart_"+rowId+"'>" + data.departName + "</span>";
	
	
	activeUser.innerHTML 		= "<input type=hidden name='activeUser' id='activeUser_"+rowId+"' value='" + data.activeUserOid + "'>"
								+ "<span id='tempactivityUser_"+rowId+"'>" + data.activeUserName+"</span>"
								+ "<span id='X_"+rowId+"' onclick=\"cancelEOActivityUser('"+rowId+"')\" style='cursor:pointer'>"
								+ "X"
								+ "</span>"
								+ "&nbsp;<span id='User_"+rowId+"' onclick=\"setActivityUser('"+rowId+"')\" style='cursor:pointer'> <img src='/Windchill/jsp/portal/images/s_search.gif' border='0' name='searchUser' style='cursor:pointer'></span>";	
	
	
	if(!data.finishDate){
		data.finishDate = "";
	}				
	dateCell.innerHTML 			= "<input type='text' id='finishDate_"+rowId+"' value='"+data.finishDate+"' name='finishDate' class=txt_field size=12 value='' readonly>"					
								+ "<img src=/Windchill/jsp/portal/images/calendar_icon.gif border=0 id='finishDate_" + rowId + "_btn' style=cursor:pointer>"
								+ "<a href=javascript:clearText('finishDate_" +rowId + "')>"
								+ "<img src=/Windchill/jsp/portal/images/x.gif border=0 id='calendarX_"+rowId+"''>"
								+ "</a>";
								
	//달력 Setting
	gfn_InitCalendar("finishDate_" +rowId , "finishDate_" + rowId + "_btn");
	descriptionCell.innerHTML 	= "<input type=\"hidden\" name=\"active_description\" value=\""+data.description+"\">";
	
	if(data.activeUserName ==""){
		
		$("#X_"+rowId).hide();
		
	}
	
	if(!isModify){
		$("#X_"+rowId).hide();
		$("#User_"+rowId).hide();
		$("#calendarX_"+rowId).hide();
		$("#finishDate_"+rowId+"_btn").hide();
	}
	
	rowId++;
}




<%----------------------------------------------------------
*                      담당자 선택시 등록되는 데이터 제어
----------------------------------------------------------%>
function setActivityUser(id){
	
	searchUserId = id;
	searchUser('createECO','single','','','wtuser','setEOActivityUser');
}


function setEOActivityUser(arrObj) {
	
	$("#tempDepart_"+searchUserId).html(arrObj[0][3]);
	$("#activeUser_"+searchUserId).val(arrObj[0][6]);
	$("#tempactivityUser_"+searchUserId).html(arrObj[0][1]);
	
	$("#X_"+rowId).show();
	
}

//function cancelEOActivityUser(inputObj,inputDepart, inputLabel, value){
<%----------------------------------------------------------
*                      담당자 삭제
----------------------------------------------------------%>
function cancelEOActivityUser(rowId){
//'activeUser_"+rowId+"','tempactivityUser_"+rowId+"','tempDepart_"+rowId+"',''
   
var activeUser ="activeUser_" +rowId ;				//유저 OID
var tempactivityUser ="tempactivityUser_" +rowId ;	//유저명
var tempDepart ="tempDepart_" +rowId ;				//부서명

var span2 = document.getElementById(tempDepart); //부서명
  // var userName = span2.innerHTML;//
   
   var span3 = document.getElementById(tempactivityUser); //유저명
  // var departName = span3.innerHTML;//
   
   var aaa = document.getElementById(activeUser); //유저 OID
   //var peopleOid =  aaa.value;//
/*
   if(peopleOid==null)peopleOid = "";
   var tempArry = peopleOid.split(",");
   var tempArry2 = userName.split("|");

   var newPeopleOid = "";
   var newPeopleLabel = "";
*/
   aaa.value = "";//newPeopleOid;
   span2.innerHTML = "";
   span3.innerHTML = "";
   
   $("#X_"+rowId).hide();
   
}
	
<%----------------------------------------------------------
*                      Rowspan
var tableId = "active_table";
var table = document.getElementById(tableId);
table.rows[i]
----------------------------------------------------------%>
function resetRowspan(){

	var tableId = "active_table";
	var table = document.getElementById(tableId);
	var stepName = "step";
	var stepList = document.getElementsByName(stepName);
	
	//rowspan 재설정
    var rows = 1;
	var pstep = null;
	var rcell = null;
	
    for(var i = 1; i < table.rows.length ; i++) {
    	if(pstep != stepList.item(i-1).value){
    		if(rcell!=null){
    			rcell.setAttribute("rowspan",rows);
    		}
    		pstep = stepList.item(i-1).value;
    		rcell = table.rows[i].cells[0];
    		rows=1;
    	}else{
    		if(table.rows[i].cells.length > 6){
    			table.rows[i].deleteCell(0);
    		}
    		rows++;
    	}
	}
    if(rcell!=null){
		rcell.setAttribute("rowspan",rows);
	}
}

<%----------------------------------------------------------
*                     활동에 대한 정합성 체크
----------------------------------------------------------%>
function checkActivity(){
	
	
	var len = $("input[name=active_checkbox]").length;
	//alert(len);
	//활동 리스트 체크
	if (len == null || len < 1) {
		
		//alert("${f:getMessage('활동')} ${f:getMessage('을(를) 입력하세요.')}");
    	//return false;
    }
	
	
	for(var i=0; i<len; i++) {
		var obj = $("input[name=active_checkbox]").eq(i);//active_name
		var activeName = $("input[name=active_name]").eq(i)
		
		var rowId = obj.attr("id");
		//alert(activeName.val()+"="+rowId.val());
		//alert(rowId);
		//활동에 대한 담당자 체크
		
		
		//alert($("#activeUser_"+rowId).val());
		if($.trim($("#activeUser_"+rowId).val()) == "") {
			alert(activeName.val() + "${f:getMessage('의 활동 담당자를 선택하세요.')}");
			return false;
		}
		
		
		//활동에 대한 완료 요청일 체크
		if($.trim($("#finishDate_"+rowId).val()) == "") {
			alert(activeName.val() + "${f:getMessage('의 활동 요청 완료일을 선택하세요.')}");
			return false;
		}
		
		
		if($("#cmd").val() == "save"){
			if($.trim($("#finishDate_"+rowId).val()) != "") {
				
				console.log("finishDate_ = " + $("#finishDate_"+rowId).val());
				if(!gfn_compareDateToDay($("#finishDate_"+rowId).val())) {
					alert("${f:getMessage('요청완료일은 오늘 날짜 이전일 수 없습니다.')}")
					return false;
				}
			}
		}
	}
	
	return true;
	
}
</script>

<body>
<input type="hidden" name="aaa" id="aa" value="<c:out value='${eoType}'/>" >
<input type="hidden" name="ecaAction" id="ecaAction" >
<input type="hidden" name="eoOid" id="eoOid" value="<c:out value="${eoOid }"/>">

<iframe src="" name="hiddenFrame" id="hiddenFrame" scrolling=no frameborder=no marginwidth=0 marginheight=0 style="display:none"></iframe>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td height="10">&nbsp;</td></tr>
	<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*" align="left">
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>
							${f:getMessage('활동 설정')}
						</b>
					</td>
					<td width="*" align="right">
					
						<b>${f:getMessage('활동 불러오기')} : 
						</b>
						
						<select id="rootOid" name="rootOid" style="width:30%">
						</select>
						
						<button title="활동추가" class="btnCRUD" type="button" id="createActivity">
	                 	 	<span></span>
	                 	 	${f:getMessage('활동추가')}
	                 	</button>
	                 	
	                 	<button title="활동삭제" class="btnCRUD" type="button" id="deleteActivity">
	                 	 	<span></span>
	                 	 	${f:getMessage('활동삭제')}
	                 	</button>
					
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
	
			<table border="0" cellspacing="0" cellpadding="0" width="100%">	
				<tr bgcolor="ffffff" height=35>
					
                 	<td class="tdwhiteL" colspan=3>
                 		<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  !style="tdisplay:none;able-layout:fixed">
							<tr>
								<td  class="tdblueM"  width=15%>${f:getMessage('Step')}</td>
								<td  class="tdblueM"  width=5%><input type="checkbox" name="allCheck" id="allCheck" >&nbsp;</td>
								<td  class="tdblueM"  width=25%>${f:getMessage('활동명')}</td>
								<td  class="tdblueM"  width=10%>${f:getMessage('활동구분')}</td>
								<td  class="tdblueM"  width=10%>${f:getMessage('부서')}</td>
								<td  class="tdblueM"  width=15%>${f:getMessage('담당자')}</td>
								<td  class="tdblueM"  width=20%>${f:getMessage('완료 요청일')}</td>
							</tr>
							
							<tbody id="active_table">
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