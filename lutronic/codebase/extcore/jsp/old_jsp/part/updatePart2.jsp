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
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	$("#" + this.id + "Search").hide();
	
	var name = '';
	
	if(!$.trim($('#partName1').val()) == '') {
		name += $('#partName1').val();
	}
	
	if(!$.trim($('#partName2').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName2').val();
	}
	
	if(!$.trim($('#partName3').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName3').val();
	}
	
	if(!$.trim($('#partNameCustom').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partNameCustom').val();
	}
	
	$('#displayName').html(name);
	
	
})

$(function () {
	$(".partName").keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if((charCode == 38 || charCode == 40) ) {
			if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
				var isAdd = false;
				if(charCode == 38){
					isAdd = true;
				}
				movePartNameFocus(this.id, isAdd);
			}
		} else if(charCode == 13 || charCode == 27){
			$("#" + this.id + "Search").hide();
		} else {
			autoSearchPartName(this.id, this.value);
		}
	})
	
	$('#partNameCustom').focusout(function() {
		$('#partNameCustom').val(this.value.toUpperCase());
	})
	
	$(".partName").focusout(function () {
		$("#" + this.id + "Search").hide();
		
		var name = '';
		
		if(!$.trim($('#partName1').val()) == '') {
			name += $('#partName1').val();
		}
		
		if(!$.trim($('#partName2').val()) == '') {
			if(!$.trim(name) == '') {
				name += '_';
			}
			name += $('#partName2').val();
		}
		
		if(!$.trim($('#partName3').val()) == '') {
			if(!$.trim(name) == '') {
				name += '_';
			}
			name += $('#partName3').val();
		}
		
		if(!$.trim($('#partNameCustom').val()) == '') {
			if(!$.trim(name) == '') {
				name += '_';
			}
			name += $('#partNameCustom').val();
		}
		
		$('#displayName').html(name);
	})
	
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		
		if($.trim($("#partName1").val()) == ""
		   && $.trim($("#partName2").val()) == ""
		   && $.trim($("#partName3").val()) == ""
		   && $.trim($("#partNameCustom").val()) == "" ) {
			alert("${f:getMessage('품목명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#partName1").focus();
			return;
		}else if($("#displayName").text().length > 40) {
			alert("${f:getMessage('품목명')}${f:getMessage('은(는) 40자 이내로 입력하세요.')}");
			return;
		}
		if($("#model").val() == "") {
			alert("${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
			$("#model").focus();
			return;
		}
		
		if($("#productmethod").val() == "") {
			alert("${f:getMessage('제작방법')}${f:getMessage('을(를) 선택하세요.')}");
			$("#productmethod").focus();
			return;
		}
		
		if($("#deptcode").val() == "") {
			alert("${f:getMessage('부서')}${f:getMessage('을(를) 선택하세요.')}");
			$("#deptcode").focus();
			return;
		}
		
		if($("#unit").val() == "") {
			alert("${f:getMessage('단위')}${f:getMessage('을(를) 선택하세요.')}");
			$("#unit").focus();
			return;
		}
		
		if(confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=partModifyForm]").serialize();
			var url	= getURLString("part", "updatePartAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('수정 성공하였습니다.')}");
						location.href = getURLString("part", "viewPart", "do") + "?oid="+data.oid;
					}else {
						alert("${f:getMessage('수정 실패하였습니다.')} :: " + data.message);
					}
				}
				,beforeSend: function() {
					gfn_StartShowProcessing();
		        }
				,complete: function() {
					gfn_EndShowProcessing();
		        }
			});
		}
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
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.movePartNameFocus = function(id,isAdd) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL li").eq(i).removeClass("hover");
			removeCount = i;
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	$("#" + id + "UL li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
}

<%----------------------------------------------------------
*                      품목명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchPartName = function(id, value) {
	if($.trim(value) == "") {
		addSearchList(id, '', true);
	} else {
		var codeType = id.toUpperCase();
		var data = common_autoSearchName(codeType, value);
		addSearchList(id, eval(data.responseText), false);
	}
}

<%----------------------------------------------------------
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, isRemove) {
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search").hide();
	}else{
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "'>" + data[i].name);
			}
		}else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      품목명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	$(this).addClass("hover") ;
	$("#" + partName).val($(this).text());
})

<%----------------------------------------------------------
*                      품목명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover") ;
})
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>

<body>

<form name="partModifyForm" id="partModifyForm" >

<input type="hidden"	name="oid"		id="oid"		value="<c:out value="${partData.oid }" />">

<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
			   	<tr> 
			   		<td height=30 width=93% align=center><B><font color=white>${f:getMessage('품목')} ${f:getMessage('수정')}</font></B></td>
			   	</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
				        	<tr>
				            	<td>
									<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
								
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom" onclick="javascript:history.back();">
										<span></span>
										${f:getMessage('이전페이지')}
									</button>
								</td>
							</tr>
				        </table>
				    </td>
				</tr>
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan=2></td>
				</tr>
			</table>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
            	<tr><td height="1" width="100%"></td> </tr>
            </table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
            	<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
					 
				<tr bgcolor="ffffff">
					<td class="tdblueM">
						${f:getMessage('품목번호')}
					</td>
					
				    <td class="tdwhiteL" colspan="3">
				    	<c:out value="${partData.number }" />
				    </td>
				</tr>
					           
                <tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('품목')} ${f:getMessage('분류')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/folder/include_FolderSelect.do">
							<jsp:param value="/Default/PART_Drawing" name="root"/>
							<jsp:param value="/Default${partData.location }" name="folder"/>
						</jsp:include>
					</td>
				</tr>

                <tr>
                	<td class="tdblueM" >
                		${f:getMessage('품목명')} <span style="color:red;">*</span>
                	</td>
                	
				    <td class="tdwhiteL25">
				    
				    	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
					
							<tr bgcolor="ffffff" height="35">
							
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('대제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName1" name="partName1" class='partName' type="text" value="<c:out value='${partData.getPartName(1)}'/>" style="width: 95%;">
									<div id="partName1Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
										<ul id="partName1UL" style="list-style-type: none; padding-left: 0px;">
										</ul>
									</div>
								</td>
							
							</tr>
						
							<tr bgcolor="ffffff" height="35">
							
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('중제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName2" name="partName2" class='partName' type="text" value="<c:out value='${partData.getPartName(2)}'/>" style="width: 95%">
									<div id="partName2Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
										<ul id="partName2UL" style="list-style-type: none; padding-left: 0px;">
										</ul>
									</div>
								
								</td>
							</tr>
						
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('소제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName3" name="partName3" class='partName' type="text" value="<c:out value='${partData.getPartName(3)}'/>"  style="width: 95%">
									<div id="partName3Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
										<ul id="partName3UL" style="list-style-type: none; padding-left: 0px;">
										</ul>
									</div>
								</td>
							</tr>
						
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('사용자')} Key in
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partNameCustom" name="partName4" class='partName' type="text" value="<c:out value='${partData.getPartName(4)}'/>"  style="width: 95%; text-transform: uppercase;">
								</td>
							</tr>
					
						</table>
					
					</td>
					
					<td class="tdblueM" id="auto" colspan="2" >
						<div>
							<span style="font-weight: bold; vertical-align: middle;" id="displayName">
								<c:out value="${partData.name }" />
							</span>
						</div>
					</td>
                </tr>
            </table>
		</td>
	</tr>
</table>

<!-- 속정 정보 -->
<jsp:include page="/eSolution/common/include_createAttributes.do">
	<jsp:param value="part" name="module"/>
	<jsp:param value="${f:getMessage('속성 정보')}" name="title"/>
	<jsp:param value="${partData.oid }" name="oid"/>
</jsp:include>

<c:if test="${!partData.isMainEPM() }">
	<!-- 주 도면 -->
	<table width="100%" border="0" cellpadding="0" cellspacing="3" >
		<tr bgcolor="ffffff" height=5>
			<td colspan="5">&nbsp;</td>
		</tr>  
		              
	    <tr>
			<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
				<b>
					${f:getMessage('주 도면')}
					&nbsp;&nbsp;&nbsp;
					<font color="red">(메카/광학 : CAD파일)&nbsp;,&nbsp;(제어/파워/인증 : PDF파일)</font>
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
				
					<!-- SPECIFICATION, UNIT -->
	              	<tr bgcolor="ffffff" height="35">
	               		<td class="tdblueM">
							${f:getMessage('주 도면')}
						</td>
						
						<td class="tdwhiteL" colspan="3">
							<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
								<jsp:param name="formId" value="partModifyForm"/>
								<jsp:param name="command" value="insert"/>
								<jsp:param name="type" value="PRIMARY"/>
								<jsp:param name="btnId" value="updateBtn" />
							</jsp:include>
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
	</table>
</c:if>

<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
</jsp:include>

<!-- 관련 rohs -->
<jsp:include page="/eSolution/rohs/include_RohsSelect.do">
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param name="paramName" value="rohsOid"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="part" name="module"/>
</jsp:include>

<!-- 첨부 파일 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>                
          <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('첨부파일')}
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
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">${f:getMessage('첨부파일')}</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="partModifyForm"/>
							<jsp:param name="type" value="secondary"/>
							<jsp:param name="oid" value="${partData.oid }"/>
							<jsp:param name="btnId" value="updateBtn" />
						</jsp:include>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>
</body>
</html>