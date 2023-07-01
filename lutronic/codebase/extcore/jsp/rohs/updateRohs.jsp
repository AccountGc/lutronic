<%@page contentType="text/html; charset=UTF-8"%>

<link rel="stylesheet" href="/Windchill/jsp/css/e3ps.css" type="text/css">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script language="javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	numberCodeList('manufacture', '', '<c:out value="${rohsData.getManufactureDisplay(false) }"/>');
	
})
function date_mask(formd, textid) {
	

	/*
	input onkeyup에서
	formd == this.form.name
	textid == this.name
	*/
	var form = eval("document."+formd);
	var text = eval("form."+textid);

	var textlength = text.value.length;

	if (textlength == 4) {
		text.value = text.value + "-";
	} else if (textlength == 7) {
		text.value = text.value + "-";
	} else if (textlength > 9) {
		//날짜 수동 입력 Validation 체크
		var chk_date = checkdate(text);
	
		if (chk_date == false) {
			return;
		}
	}
}

<%----------------------------------------------------------
*                      페이지 이동 function
----------------------------------------------------------%>
window.divPageLoad = function(divId,roleType,oid){
	var btnId = $('.btnCRUD').attr('id');
	var url	= getURLString("content", "includeAttachFiles", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			formId : 'rohsModifyForm',
			type : roleType,
			oid : oid,
			btnId : btnId
		},
		success:function(data){
			$('#' + divId).html(data);
		}
	});
}

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

window.rohsFileType = function(id,value) {
	var url	= getURLString("rohs", "rohsFileType", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			addSelectList(id, data, value);
		}
	});
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

$(function() {
	<%----------------------------------------------------------
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
		if($.trim($("#rohsName").val()) == '') {
			alert("${f:getMessage('물질명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#rohsName").focus();
			return;
		}
		if($("#manufacture").val() == '') {
			alert("${f:getMessage('협력업체')}${f:getMessage('을(를) 선택하세요.')}");
			$("#manufacture").focus();
			return;
		}
		
		var length = $('input[name=roleType]').length;
		for(var i=0; i<length; i++) {
			
			var fileValue = $('input[name=roleType]').eq(i).val();
			if($('#' + fileValue).length == 0 && $('#' + fileValue + '_delocIds').length == 0) {
				alert("${f:getMessage('파일')}${f:getMessage('을(를) 첨부하세요.')}");
				return;
			}
			if($('.fileType').eq(i).val() == '') {
				alert("${f:getMessage('파일구분')}${f:getMessage('을(를) 선택하세요.')}");
				$('.fileType').eq(i).focus();
				return;
			}
			//16.11.09 PJT EDIT
			if($('.fileType').eq(i).val() == 'TR' && $('.date').eq(i).val() == ''){
			//if($('.date').eq(i).val() == '') {
				alert("${f:getMessage('발행일')}${f:getMessage('을(를) 선택하세요.')}");
				$('.date').eq(i).focus();
				return;
			}
		}
		
		if (confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=rohsModifyForm]").serialize();
			var url	= getURLString("rohs", "updateRohsAction", "do");
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
						location.href = getURLString("rohs", "viewRohs", "do") + "?oid="+data.oid;
					}else {
						alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
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
	<%----------------------------------------------------------
	*                      첨부파일 추가
	----------------------------------------------------------%>
	$("#addFile").click(function() {
		var id = getRoleTypeValue();
		var length = $('#filesBody .roleType').length;
		if(id < 21) {

			var html = ""
			html += "<tr>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='fileDelete' id='fileDelete' value='ROHS" + id + "' >";
			html += "		<input type='hidden' name='roleType' value='ROHS" + id + "' />"
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += "		<div id='ROHS" + id + "_div'>";
			html += "		</div>";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<select id='ROHS" + id + "_fileType' name='ROHS" + id + "_fileType' class='fileType' style='width: 90%'>"; 
			html += "		</select>";
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += "		<input name='ROHS" + id + "_date' id='ROHS" + id + "_date' class='txt_field date' onblur =\"checkDate('ROHS" + id + "_date',this.value)\" size='12'  maxlength='15' onkeyup='javascript:date_mask(this.form.name, this.name);'  />";
			html += "		<a href='javascript:void(0);'>";
			html += "			<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border='0' id='ROHS" + id + "_date_btn' >";
			html += "		</a>";
			html += "		<a href=JavaScript:clearText('ROHS" + id + "_date');>";
			html += "			<img src='/Windchill/jsp/portal/images/x.gif' border=0>";
			html += "		</a>";
			html += "	</td>";
			html += "</tr>";
				
			$("#filesBody").append(html);
			
			divPageLoad('ROHS'+ id + '_div', 'ROHS' + id);
			gfn_InitCalendar("ROHS" + id + "_date", "ROHS" + id + "_date_btn");
			rohsFileType('ROHS' + id + '_fileType');
		}else {
			alert("stop");
			
		}
	})
	$('#delFile').click(function() {
		var obj = $("input[name='fileDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				var fileId = obj.eq(i).val();
				$('#' + fileId).remove();
				$('#' + fileId + '_delocIds').remove();
				obj.eq(i).parent().parent().remove();
			}
		}
	})
})

window.getRoleTypeValue = function() {
	for(var i=0; i<20; i++) {
		var obj = $('[value^=ROHS' + (i+1) + ']').val();
		if(obj == 'undefined' || obj == null) {
			return (i+1);
		}
	}
}
</script>

<form name="rohsModifyForm" id="rohsModifyForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="oid"  id="oid" value="<c:out value="${rohsData.oid }" />" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr align="center" height="5">
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="3" align="center">
				<tr>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                    <td align="center">
									<table>
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
			            </table>
					</td>
				</tr>
				
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>RoHS${f:getMessage('정보')}</b></td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan=2>
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr><td height=1 width=100%></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							<colgroup>
								<col width="150">
								<col width="350">
								<col width="150">
								<col width="350">
							</colgroup>
			
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" colspan=4>
									<b> 
										<c:out value="${rohsData.number }" /> 
										[<c:out value="${rohsData.name }" />]
									</b>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('물질명')} <span class="style1">*</span></td>
								<td class="tdwhiteL">
									<input name="rohsName" id="rohsName" class="txt_field" size="85" maxlength="80" engnum="engnum" style="width:90%" value="<c:out value="${rohsData.name }" />"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('협력업체')} <span class="style1">*</span></td>
								<td class="tdwhiteL">
									<select id='manufacture' name='manufacture' style="width: 95%"> 
									</select>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('설명')}</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="description" id="description" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"><c:out value="${rohsData.getDescription(false) }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff">
								<td class="tdblueM">
									${f:getMessage('첨부파일')}
								</td>
								<td class="tdwhiteL" colspan="3">
								<!-- ROHS 파일 START -->
									<table border="0" cellpadding="0" cellspacing="0" style="width: 100%">
										<tr>
											<td>
												<table border="0" cellpadding="0" cellspacing="2">
												    <tr>
												        <td>
												        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('추가')}" id="addFile" name="addFile">
												            	<span></span>
												            	${f:getMessage('추가')}
												           	</button>
												        </td>
												        <td>
												        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('삭제')}" id="delFile" name="delFile">
												            	<span></span>
												            	${f:getMessage('삭제')}
												           	</button>
												        </td>
												    </tr>
												</table>
											</td>
										</tr>
										
										<tr>
											<td>
												<table width="100%" cellspacing="0" cellpadding="1" border="0" id="fileTable" align="center">
													<tr>
														<td class="tdblueM" width="5%"></td>
													    <td class="tdblueM" width="*">${f:getMessage('첨부파일')}</td>
													    <td class="tdblueM" width="25%">${f:getMessage('파일구분')} </td>
													  	<td class="tdblueM" width="20%">${f:getMessage('발행일')}</td>
													</tr>
													
													<tbody id='filesBody'>
													
														<c:if test="${fn:length(list) != 0 }">
															<c:forEach items="${list}" var="contentData" >
															
																<tr>
																	<td class='tdwhiteM'>
																		<input type='checkbox' name='fileDelete' id='fileDelete' value='<c:out value="${contentData.fileRole }"/>' >
																		
																		<input type='hidden' name='roleType' class='roleType' value='<c:out value="${contentData.fileRole }"/>' />
																		
																		<input type='hidden' name='<c:out value="${contentData.fileRole }"/>_oid' 
																			   value='<c:out value="${contentData.fileOid }"/>' />
																			   
																		<input type='hidden' name='<c:out value="${contentData.fileRole }"/>_AppOid' 
																			   value='<c:out value="${contentData.fileAppOid }"/>' />
																	</td>
																	
																	<td class='tdwhiteL'>
																		<div id='<c:out value="${contentData.fileRole }"/>_div'>
																		</div>
																	</td>
																	
																	<td class='tdwhiteM'>
																		<select id='<c:out value="${contentData.fileRole }"/>_fileType' name='<c:out value="${contentData.fileRole }"/>_fileType' 
																				class='fileType' style='width: 90%'> 
																		</select>
																	</td>
																	
																	<td class='tdwhiteL'>
																	
																		<input name='<c:out value="${contentData.fileRole }"/>_date' id='<c:out value="${contentData.fileRole }"/>_date' 
																				class='txt_field date' size='12' value='<c:out value="${contentData.fileDate }"/>' onkeyup="javascript:date_mask(this.form.name, this.name);" onblur ="checkDate('<c:out value="${contentData.fileRole }"/>_date',this.value)" maxlength='15'/>
																				
																		<a href='javascript:void(0);'>
																			<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border='0' id='<c:out value="${contentData.fileRole }"/>_date_btn' >
																		</a>
																		
																		<a href="JavaScript:clearText('<c:out value="${contentData.fileRole }"/>_date');">
																			<img src='/Windchill/jsp/portal/images/x.gif' border=0>
																		</a>
																	</td>
																</tr>
																
																
																<script type="text/javascript">
																rohsFileType('<c:out value="${contentData.fileRole }"/>_fileType', '<c:out value="${contentData.fileTypeCode }"/>');
																gfn_InitCalendar('<c:out value="${contentData.fileRole }"/>_date', '<c:out value="${contentData.fileRole }"/>_date_btn');
																divPageLoad('<c:out value="${contentData.fileRole }"/>_div', '<c:out value="${contentData.fileRole }"/>', '<c:out value="${rohsData.oid }" />');
																</script>
																
																
															</c:forEach>
														</c:if>
													</tbody>
													
												</table>
											</td>
										</tr>
									</table>
								<!-- ROHS 파일 END-->
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 관련 품목 -->
<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
	<jsp:param name="moduleType" value="rohs"/>
	<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
	<jsp:param name="paramName" value="partOid"/>
</jsp:include>

<!-- 관련 rohs -->
<jsp:include page="/eSolution/rohs/include_RohsSelect.do">
	<jsp:param value="${f:getMessage('관련물질')}" name="title"/>
	<jsp:param value="rohsOid" name="paramName"/>
</jsp:include>
</form>
