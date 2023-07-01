<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	numberCodeList('manufacture', '', '<c:out value="${manufacture}"/>');
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText), value);
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].oid + "'";
			
			//console.log("data name : "+data[i].name + "data code : "+data[i].code);
			console.log("value :"+ value);
			
			if(value.indexOf("&amp;") > 0){
				value = value.replace("&amp;","&");
				console.log("replace value :"+ value);
			}
			
			if(data[i].name == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			//console.log(data[i].code);
			$("#"+ id).append(html);
		}
	}
}


$(function() {
	
	<%----------------------------------------------------------
	*                      속성 수정
	----------------------------------------------------------%>
	$("#attributeChangeButn").click(function() {
		attributeChange();
	})
})

<%----------------------------------------------------------
*                      속성 수정
----------------------------------------------------------%>
function attributeChange(){
	if(!confirm("속성 수정 하시겠습니까?")){
		return;
	}
	var url	= getURLString("part", "attributeChange", "do");
	$.ajax({
		type : "POST",
		url : url,
		data : {
			oid : $("#oid").val(),
			value : $("#manufacture").val(),
			numberCodeType : "MANUFACTURE"
			
		},
		dataType : "json",
		async : true,
		cache : false,
	
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
		
			alert(data.message);
		}
	})
}
</script>


<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">

	<colgroup>
		<col width="150">
		<col width="350">
		<col width="150">
		<col width="350">
	</colgroup>

	<c:choose>
		<c:when test="${module eq 'doc' }">
			<tr id="attr1">
				
				<td class="tdblueM">
					${f:getMessage('프로젝트코드')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${model }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('보존기간')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${preseration }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('내부 문서번호')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${interalnumber }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('부서')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${deptcode }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('작성자')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${writer }" />
				</td>
			</tr>
			
		</c:when>
		<c:when test="${module eq 'mold' }">
			<tr>
				<td class="tdblueM">
					MANUFACTURER
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${manufacture }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('금형타입')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${moldtype }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('내부 문서번호')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${interalnumber }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('부서')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${deptcode }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('업체 금형번호')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${moldnumber }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('금형개발비')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${moldcost }" />
				</td>
			</tr>
		</c:when>
		
		<c:otherwise>
			<tr id="attr1">
				<td class="tdblueM">
					${f:getMessage('프로젝트코드')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${model }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('제작방법')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${productmethod }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('부서')}
				</td>
				
				<td class="tdwhiteL">
					<c:out value="${deptcode }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('단위')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${unit }" />
				</td>
			</tr>
			
			<tr id="attr2">
				<td class="tdblueM">
					${f:getMessage('무게')}(g)
				</td>
				<td class="tdwhiteL">
					<c:out value="${weight }" />
				</td>
				
				<td class="tdblueM">
					MANUFACTURER
				</td>
				<td class="tdwhiteL">
					
					<select id='manufacture' name='manufacture' style="width: 50%">
									</select>
									<button type="button" name="attributeChangeButn" id="attributeChangeButn" class="btnCRUD">
												<span></span>
												${f:getMessage('속성변경')}
										</button>
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('재질')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${mat }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('후처리')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${finish }" />
				</td>
			</tr>
			
			<tr id="attr3">
				<td class="tdblueM">
					${f:getMessage('OEM Info.')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${remarks }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('사양')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${specification }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					EO No.
				</td>
				<td class="tdwhiteL">
					<c:out value="${ecoNo }" />
				</td>
				
				<td class="tdblueM">
					EO Date
				</td>
				<td class="tdwhiteL">
					<c:out value="${ecoDate }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					${f:getMessage('검토자')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${chk }" />
				</td>
				
				<td class="tdblueM">
					${f:getMessage('승인자')}
				</td>
				<td class="tdwhiteL">
					<c:out value="${apr }" />
				</td>
			</tr>
			
			<tr>
				<td class="tdblueM">
					Rev.
				</td>
				<td class="tdwhiteL">
					<c:out value="${rev }" />
				</td>
				
				<td class="tdblueM">
					DES
				</td>
				<td class="tdwhiteL">
					<c:out value="${des }" />
				</td>
			</tr>
			
			
			<tr>
				<td class="tdblueM">
					ECO No.
				</td>
				<td class="tdwhiteL">
					<c:out value="${changeNo }" />
				</td>
				
				<td class="tdblueM">
					ECO Date
				</td>
				<td class="tdwhiteL">
					<c:out value="${changeDate }" />
				</td>
			</tr>
			
			
			
		</c:otherwise>
		
	</c:choose>
		
</table>