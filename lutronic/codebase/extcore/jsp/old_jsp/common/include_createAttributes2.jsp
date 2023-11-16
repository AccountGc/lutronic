<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	numberCodeList('model', '', '<c:out value="${model}"/>');
	numberCodeList('deptcode', '', '<c:out value="${deptcode}"/>');
	<c:choose>
		<c:when test="${module eq 'doc' }">
			numberCodeList('preseration', '', '<c:out value="${preseration}"/>');
			numberCodeList('manufacture', '', '<c:out value="${manufacture}"/>');
			numberCodeList('moldtype', '', '<c:out value="${moldtype}"/>');
		</c:when>
		
		<c:otherwise>
			numberCodeList('mat', '', '<c:out value="${mat}"/>');
			numberCodeList('manufacture', '', '<c:out value="${manufacture}"/>');
			numberCodeList('finish', '', '<c:out value="${finish}"/>');
			numberCodeList('productmethod', '', '<c:out value="${productmethod}"/>');
			getQuantityUnit('<c:out value="${unit}"/>');
		</c:otherwise>
	
	</c:choose>
})

$(function() {
	<%----------------------------------------------------------
	*                      Weight 입력 중
	----------------------------------------------------------%>
	$('#weight').keypress(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		return (charCode == 46) || common_isNumber(event, this);
	})
	<%----------------------------------------------------------
	*                      Weight 입력시
	----------------------------------------------------------%>
	$("#weight").keyup(function() {
		var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
		$("#weight").val(result);
	})
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
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

<%----------------------------------------------------------
*                      단위 리스트 가져오기
----------------------------------------------------------%>
window.getQuantityUnit = function(value) {
	var url	= getURLString("part", "getQuantityUnit", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('단위 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			//$("#unit").append("<option value=''> ${f:getMessage('선택')} </option>");
			for(var i=0; i<data.length; i++) {
				var html = "<option value='" + data[i] + "'";
				
				if(data[i] == value) {
					html += " selected";
				}
				
				html += " > " + data[i] + "</option>";
				$("#unit").append(html);
			}
		}
	});
}

</script>

<!-- 품목 속성 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('품목')}
				${f:getMessage('속성')}
			</b>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
			
				<c:choose>
					<c:when test="${module eq 'doc' }">
						
						<!-- MODEL, 보존기간 -->			
						<c:if test="${preseration ne ''}">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('프로젝트코드')}
								</td>
								
								<td class="tdwhiteM">
									<select id='model' name='model' style="width: 95%">
									</select>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('보존기간')} <span style="COLOR: red;">*</span>
								</td>
								
								<td class="tdwhiteM">
									<select id='preseration' name='preseration' style="width: 95%">
									</select>
								</td>
							</tr>
						</c:if>
						<c:if test="${moldtype ne ''}">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									manufaturer
								</td>
								
								<td class="tdwhiteM">
									<select id='manufacture' name='manufacture' style="width: 95%">
									</select>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('금형타입')} <span style="COLOR: red;">*</span>
								</td>
								
								<td class="tdwhiteM">
									<select id='moldtype' name='moldtype' style="width: 95%">
									</select>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('업체자체금형번호')}
								</td>
								
								<td class="tdwhiteM">
									<input id="moldnumber" name="moldnumber" type="text" value="<c:out value='${moldnumber }'/>" style="width: 95%;"/>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('금형개발비')}
								</td>
								
								<td class="tdwhiteM">
									<input id="moldcost" name="moldcost" type="text" value="<c:out value='${moldcost }'/>" style="width: 95%;"/>
								</td>
							</tr>
						</c:if>
						<!-- 내부 분서 번호, 부서 -->
		              	<tr bgcolor="ffffff" height="35">
		              		<td class="tdblueM">
								${f:getMessage('내부 문서번호')}
							</td>					
							
							<td class="tdwhiteM">
								<input id="interalnumber" name="interalnumber" type="text" value="<c:out value='${interalnumber }'/>" style="width: 95%;"/>
							</td>
							
							<td class="tdblueM">
								${f:getMessage('부서')}
							</td>					
							
							<td class="tdwhiteM">
								<select id="deptcode" name="deptcode" style="width: 95%">
								</select>
							</td>
							
						</tr>
						
						<c:if test="${preseration ne ''}">
						<!-- 작성자 -->
		              	<tr bgcolor="ffffff" height="35">
		              		<td class="tdblueM">
								${f:getMessage('작성자')}
							</td>					
							
							<td class="tdwhiteM">
								<input id="writer" name="writer" type="text" value="<c:out value='${writer }'/>" style="width: 95%;"/>
							</td>
						</tr>
						</c:if>
						
					</c:when>
		
					<c:otherwise>
		
						<!-- MODEL, PRODUCTMETHOD -->			
						<tr bgcolor="ffffff" height="35">
							<td class="tdblueM">
								${f:getMessage('프로젝트코드')} <span style="COLOR: red;">*</span>
							</td>
							
							<td class="tdwhiteM">
								<select id='model' name='model' style="width: 95%">
								</select>
							</td>
							
							<td class="tdblueM">
								${f:getMessage('제작방법')} <span style="COLOR: red;">*</span>
							</td>
							
							<td class="tdwhiteM">
								<select id='productmethod' name='productmethod' style="width: 95%">
								</select>
							</td>
						</tr>
						
						<!-- DEPTCODE, UNIT -->
		              	<tr bgcolor="ffffff" height="35">
		              		<td class="tdblueM">
								${f:getMessage('부서')} <span style="COLOR: red;">*</span>
							</td>					
							
							<td class="tdwhiteM">
								<select id="deptcode" name="deptcode" style="width: 95%">
								</select>
							</td>
							
							<td class="tdblueM">
								${f:getMessage('단위')} <span style="COLOR: red;">*</span>
							</td>					
							
							<td class="tdwhiteM">
								<select id='unit' name='unit' style="width: 95%">
								</select>
							</td>
						</tr>
		                
						<!-- WEIGHT, MANUFATURER -->
						<tr bgcolor="ffffff" height="35">
							<td class="tdblueM">
								${f:getMessage('무게')}(g)
							</td>
							
							<td class="tdwhiteM">
								<input id="weight" name="weight" type="text" value="<c:out value='${weight }'/>" style="width: 95%;"/>
							</td>
							
							<td class="tdblueM">
								MANUFACTURER
							</td>
							
							<td class="tdwhiteM">
								<select id="manufacture" name="manufacture" style="width: 95%">
								</select>
							</td>
						</tr>
						
						<!-- MAT, FINISH -->
						<tr bgcolor="ffffff" height="35">
							<td class="tdblueM">
								${f:getMessage('재질')}
							</td>
							
							<td class="tdwhiteM">
								<select id="mat" name="mat" style="width: 95%">
								</select>
							</td>
							
							<td class="tdblueM">
								${f:getMessage('후처리')}
							</td>
							
							<td class="tdwhiteM">
								<select id="finish" name="finish" style="width: 95%">
								</select>
							</td>
						</tr>
						
						<!-- REMARKS, SPECIFICATION -->
						<tr bgcolor="ffffff" height="35">
		               		<td class="tdblueM">
								${f:getMessage('OEM Info.')}
							</td>
							
							<td class="tdwhiteM">
								<input id="remarks" name="remarks" type="text" value="<c:out value='${remarks }'/>" style="width: 95%;"/>
							</td>
							
		               		<td class="tdblueM">
								${f:getMessage('사양')}
							</td>
							
							<td class="tdwhiteM">
								<input id="specification" name="specification" type="text" value="<c:out value='${specification }'/>" style="width: 95%;"/>
							</td>
						</tr>
					</c:otherwise>
					
				</c:choose>
			</table>
		</td>
	</tr>
</table>