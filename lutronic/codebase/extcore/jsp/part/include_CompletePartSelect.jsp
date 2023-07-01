<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">

var partOid = "<c:out value='${paramName}' />";
var moduleType = "<c:out value='${moduleType}' />";
var mode = "<c:out value='${mode}' />";
$(function() {
	<%----------------------------------------------------------
	*                      품목 추가 버튼
	----------------------------------------------------------%>
	$("#addCompleteBtn").click(function () {
		if($("#maxcnt").val() != "" ) {
			if($("#completePartTable tr").length > $("#maxcnt").val()) {
				alert("${f:getMessage('품목을 하나이상 추가할 수 없습니다.')}");
				return;
			}
		}
		
		var url = getURLString("part", "selectPartPopup", "do") + "?mode="+mode +"&moduleType="+moduleType+"&state=INWORKING"//+$("#state").val();
		openOtherName(url,"window","1180","600","status=no,scrollbars=yes,resizable=yes");
		//attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=1180px; dialogHeight:880px; center:yes");
	}),
	<%----------------------------------------------------------
	*                      품목 삭제 버튼
	----------------------------------------------------------%>
	$("#delCompleteBtn").click(function () {
		var obj = $("input[name='completeDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
	
	$("#parCompletetcheck").click(function() {
		if(this.checked) {
			$("input[name='completeDelete']").prop("checked", "checked");
		}else {
			$("input[name='completeDelete']").prop("checked", "");
		}
	})
	
})

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addCompletePart(obj,isHref) {
	
	for (var i = 0; i < obj.length; i++) {
		
		//alert($("input[name='eoType']:checked").val());
		//alert($("input[name=eoType]:checked").val());
		
		if($("input[name='eoType']:checked").length == 0 ){
			alert("EO ${f:getMessage('구분')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		//DEV 1인 아닌 부품,PRODUCT 1인 부품 
		
		
		if(CompleteDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='completeDelete' id='completeDelete'>";
			html += "		<input type='hidden' name='completeOid' id='completeOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='hidden' name='completeNumber' id='completeNumber' value='" + obj[i][1] + "' />";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			
			if(isHref) {
				html += "<a href=javascript:openView('" + obj[i][0] + "')>";
				html += obj[i][2];
				html += "</a>";
			}else {
				html += obj[i][2];
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][3];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<button type='button' name='bom' id='" + obj[i][0] + "' class='btnCustom' onclick=\"viewBom('"+ obj[i][0] +"')\">"
			html += "		<span>BOM</span></button>"
			html += "	</td>";
		
			//html += "	<td class='tdwhiteM'>";
			//html += "		<input type='button' name='bom' id='" + obj[i][0] + "' value='BOM전개'>";
			//html += "	</td>";
			
			html += "</tr>";
			$("#completePartTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      선택 품목 데이터 중복 검사
----------------------------------------------------------%>
function CompleteDuplicateCheck(number) {
	var obj = $("input[name='completeNumber']");
	
	if(!partCheck(number)){
		return false;
	}
	
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == number) {
			return false;
		}
	}
	return true;
}

<%----------------------------------------------------------
*                      BOM 버튼
----------------------------------------------------------%>

function viewBom(oid){
	
	var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM", opts+rest);
    newwin.focus();
    
}
</script>


<body>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="2">
			    <tr>
			        <td>
			        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('추가')}" id="addCompleteBtn" name="addCompleteBtn">
			            	<span></span>
			            	${f:getMessage('추가')}
			           	</button>
			        </td>
			        <td>
			        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('삭제')}" id="delCompleteBtn" name="delCompleteBtn">
			            	<span></span>
			            	${f:getMessage('삭제')}
			           	</button>
			           	
			        </td>
			        
			    </tr>
			</table>
			
			<table width="100%" cellspacing="0" cellpadding="2" border="0" id="completePartTable" align="center">
			    <tbody>
			        <tr>
			        	<td class="tdblueM" width="5%"><input type="checkbox" name="parCompletetcheck" id="parCompletetcheck" ></td>
			            <td class="tdblueM" width="25%">${f:getMessage('품목번호')}</td>
			            <td class="tdblueM" width="30%">${f:getMessage('품명')}</td>
			            <td class="tdblueM" width="10%">${f:getMessage('Rev.')} </td>
			            <td class="tdblueM" width="10%">BOM${f:getMessage('보기')} </td>
			            
			        </tr>
			        <c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="partData" varStatus="status" >
								<tr>
									<td class="tdwhiteM" width="5%">
										<input type='checkbox' name='completeDelete' id='completeDelete'>
										<input type='hidden' name='completeOid' id='completeOid' value="<c:out value="${partData.oid }" />" />
									</td>
									
									<td class="tdwhiteM" width="25%">
										<c:out value="${partData.number }" />
									</td>
									
									<td class="tdwhiteM" width="30%">
										<a href="javascript:openView('<c:out value="${partData.oid }" />')">
											<c:out value="${partData.name }" />
										</a>
									</td>
									
									<td class="tdwhiteM" width="10%">
										<c:out value="${partData.version }" />.<c:out value="${partData.iteration }" />
									</td>
									<td class="tdwhiteM" width="10%">
										<button type='button' name='bom' id='<c:out value="${partData.oid }" />' class='btnCustom' onclick="viewBom('<c:out value="${partData.oid }" />')">
										<span>BOM</span></button>
										</button>
									</td>
								</tr>
							</c:forEach>
						</c:when>
					</c:choose>
			    </tbody>
			</table>
		</td>
	</tr>
</table>
</body>
</html>