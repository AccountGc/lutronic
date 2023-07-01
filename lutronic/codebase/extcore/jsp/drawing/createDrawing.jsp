<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
$(function() {
	<%----------------------------------------------------------
	*                      등록타입 변경시
	----------------------------------------------------------%>
	/* $("input[name=createType]").change(function() {
		if(this.value == "0") {
			$("#new").show();
			$("#product").hide();
		}else {
			$("#new").hide();
			$("#product").show();
		}
	}), */
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function () {
		
		if($("#location").val() == ""){
			alert("${f:getMessage('도면분류')}${f:getMessage('을(를) 선택하세요.')}");
			return ;
		}
		
		if($("#number").val() == "") {
			alert("${f:getMessage('도번')}${f:getMessage('을(를) 입력하세요.')}");
			return;
		}
		
		if($("#name").val() == "") {
			alert("${f:getMessage('도면명')}${f:getMessage('을(를) 입력하세요.')}");
			return;
		}
		if($.trim($("#PRIMARY").val()) == "") {
			alert("${f:getMessage('주 첨부파일')}${f:getMessage('을(를) 선택하세요.')}");
			return;		
		}
		/* var fileCheck = file.substring(file.indexOf(".")+1).toUpperCase();
		if(fileCheck != "DWG") {
			alert("주 첨부파일은 확장자가 DWG인 AutoCAD만 등록 가능합니다.");
			return;
		} */

		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			var data = common_submit("drawing", "createDrawingAction", "PartDrawingForm", "listDrawing");
			/* var form = $("form[name=PartDrawingForm]").serialize();
			var url	= getURLString("drawing", "createDrawingAction", "do");
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
					if(data) {
						alert("${f:getMessage('등록 성공하였습니다.')}");
					}else {
						alert("${f:getMessage('등록 실패하였습니다.')}");
					}
				}
				,beforeSend: function() {
					gfn_StartShowProcessing();
		        }
				,complete: function() {
					gfn_EndShowProcessing();
		        }
			}); */
		}
	}),
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("drawing", "listDrawing", "do");
	})
	
	$('#resetBtn').click(function() {
		resetFunction();
	})
})
</script>

<body>

<form name="PartDrawingForm" id="PartDrawingForm" method="post" enctype="multipart/form-data" >
<input type="hidden" name="lifecycle"	id="lifecycle" 	value="LC_PART" />
<input type="hidden" name="fid" 		id="fid"		value="" />
<input type="hidden" name="location" 	id="location"	value="" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('도면')}${f:getMessage('관리')} > ${f:getMessage('도면')}${f:getMessage('등록')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td align="center" valign="top" width=100% style="padding-right:4;padding-left:4">
			<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
				<tr height=5>
					<td>
						<table width="100%" border="0" cellpadding="0" cellspacing="3">
						    <tr align="center">
						        <td valign="top" style="padding:0px 0px 0px 0px">
						            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
						                <tr><td height="1" width="100%"></td></tr>
						            </table>
						            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							            <tr>
							            	<td width="150"></td>
											<td width="350"></td>
											<td width="150"></td>
											<td width="350"></td>
							            </tr>
							            
							            <%-- <tr bgcolor="ffffff" height="35">
							            	<td class="tdblueM" colspan="2">
							            		${f:getMessage('등록타입')}
							            	</td>
							            	
							            	<td class="tdwhiteL" colspan="3">&nbsp;
							            		<input type="radio" name="createType" value="0" checked="checked">
							            		<span></span>
							            		${f:getMessage('신규')}
							            		<input type="radio" name="createType" value="1">
							            		<span></span>
							            		${f:getMessage('품목')}
							            	</td>
							            </tr> --%>
							            
							            <tr bgcolor="ffffff" height="35">
							                <td class="tdblueM" >${f:getMessage('도면분류')} <span style="color: red;">*</span></td>
							                <td class="tdwhiteL" colspan="3">&nbsp;
							                	<b>
							                		<span id="locationName">
							                			/Default/PART_Drawing
							                		</span>
							                	</b>
							                </td>
							            </tr>
							            
							            <tr bgcolor="ffffff" height="35" id="new" style="display: ">
							            	<td class="tdblueM" >
							            	 	${f:getMessage('도번')} <span style="color: red;">*</span>
							            	 </td>
							            	 <td class="tdwhiteL" >
							            	 	<input name="number" id="number" class="txt_field" style="width:99%"/>
							            	 </td>
							            	 <td class="tdblueM" >
							            	 	${f:getMessage('도면명')} <span style="color: red;">*</span>
							            	 </td>
							            	 <td class="tdwhiteL" >
							            	 	<input name="name" id="name" class="txt_field" style="width:99%"/>
							            	 </td>
							            </tr>
							            
						                <tr bgcolor="ffffff" height="35">
						                    <td class="tdblueM" >
						                    	${f:getMessage('도면')}${f:getMessage('설명')}
						                    </td>
						                    <td class="tdwhiteL" colspan="5">
						                        <div class="textarea_autoSize">
													<textarea name="description" id="description" cols="10" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('문서')}${f:getMessage('설명')}')"></textarea>
												</div>
						                    </td>
						                </tr>
										
										<tr bgcolor="ffffff" height="35">
											<td class="tdblueM" >
												${f:getMessage('주 첨부파일')} <span style="color: red;">*</span>
											</td>
											
											<td class="tdwhiteL" colspan="3">
												<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
													<jsp:param name="formId" value="PartDrawingForm"/>
													<jsp:param name="type" value="PRIMARY"/>
												</jsp:include>
											</td>
										</tr>
										
										<tr bgcolor="ffffff" height="35">
											<td class="tdblueM" >
												${f:getMessage('첨부파일')}
											</td>
											
											<td class="tdwhiteL" colspan="3">
												<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
													<jsp:param name="formId" value="PartDrawingForm"/>
												</jsp:include>
											</td>
										</tr>
						                
						            </table>
						            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							            <tr bgcolor="ffffff" height="35" id="product">
											<td class="tdwhiteL" >
												<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
													<jsp:param name="moduleType" value="drawing"/>
													<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
													<jsp:param name="maxcnt" value="" />
													<jsp:param name="paramName" value="partOid" />
												</jsp:include>
											</td>
										</tr>
						            </table>
						        </td>
						    </tr>
						    <tr>
						        <td align="center" colspan="2">
						            <table border="0" cellpadding="0" cellspacing="4" align="center">
						                <tr>
						                    <td>
												<button type="button" class="btnCRUD" title="${f:getMessage('등록')}" id="createBtn" name="createBtn">
								                  	<span></span>
								                  	${f:getMessage('등록')}
							                  	</button>
											</td>
											<td>
												<button title="Reset" class="btnCustom" type="reset" name="resetBtn" id="resetBtn">
													<span></span>
													${f:getMessage('초기화')}
												</button>
											</td>
											<td>
												<button title="${f:getMessage('목록')}" class="btnCustom" type="button" name="listBtn" id="listBtn">
													<span></span>
													${f:getMessage('목록')}
												</button>
											</td>
						                </tr>
						            </table>
						        </td>
						    </tr>
						</table>
			        </td>
			    </tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>