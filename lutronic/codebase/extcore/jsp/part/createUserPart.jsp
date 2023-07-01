<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
$(document).ready(function() {
	getQuantityUnit();
	numberCodeList('SUPPLIER');
});

<%----------------------------------------------------------
*                      단위 리스트 가져오기
----------------------------------------------------------%>
window.getQuantityUnit = function() {
	var form = $("form[name=createUserPart]").serialize();
	var url	= getURLString("part", "getQuantityUnit", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('단위 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			for(var i=0; i<data.length; i++) {
				$("#Unit").append("<option value='" + data[i] + "'>" + data[i] + "</option>");
			}
		}
	});
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(type) {
	var form = $("form[name=createUserPart]").serialize();
	var url	= getURLString("common", "numberCodeList", "do") + "?type="+type;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			addSelectList(type,data);
		}
	});
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(type,data){
	var id = "";
	if(type == "PARTGROUP") {
		id = "partType";
	}else if(type == "MODELGROUP") {
		id = "product";
	}else if(type == "SUPPLIER") {
		id = "Supplier";
	}else if(type == "PRODUCTTYPE") {
		id = "productTypeSelect";
	}
	
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			$("#"+ id).append("<option value='" + data[i].code + "'> [" + data[i].code + "] " + data[i].name + "</option>");
		}
	}
}

$(function() {
	<%----------------------------------------------------------
	*                      등록버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		    if($.trim($("#partName").val()) == "" ){
				alert("${f:getMessage('품목명')}${f:getMessage('을(를) 입력하세요.')}");
				$("#partName").focus();
				return;
			}
		    if($("#userNumber").val().length <= 6){
				alert("${f:getMessage('품번을 6자리 이상 입력하세요.')}");
				$("#userNumber").focus();
				return;
			}
		    
		    if(!textAreaLengthCheckId('Comment','4000','${f:getMessage('설명')}')){
				return;
			}
		    
		    if($("#productTypePart").val() == "") {
		    	alert("ProductType${f:getMessage('을(를) 선택하세요.')}");
		    	$("#productTypePart").focus();
		    	return;
		    }
		    
		    if($("#productSubtypePart").val() == "") {
		    	alert("productSubtypePart${f:getMessage('을(를) 선택하세요.')}");
		    	$("#productSubtypePart").focus();
		    	return;
		    }
		    
		    if($("#productSubtypePart").val() == "Product master") {
		    	if($("#productDimensionGroupPart").val() == "") {
			    	alert("productDimensionGroupPart${f:getMessage('을(를) 선택하세요.')}");
			    	$("#productDimensionGroupPart").focus();
			    	return;
		    	}
		    }
		    if($("#docBody > tr").length > 0) {
		    	if($("#id_location").val() == "/Default/Document") {
		    		alert("${f:getMessage('문서분류')}${f:getMessage('을(를) 선택하세요.')}");
		    		return;
		    	}
			    for(var i=0; i<$("#docBody > tr").length; i++) {
			    	if($.trim($("input[name='docName']").eq(i).val()) == "" || $("input:file[name='docFile']").eq(i).val() == "") {
			    		alert((i+1) + "${f:getMessage('번째 문서를 확인하세요..')}");
			    		return;
			    	}
			    }
		    }
		    
		    if($("#fileTableDrw > tr").length > 0) {
		    	for(var i=0; i<$("#fileTableDrw > tr").length; i++) {
		    		if($("input:file[name='drwFile']").eq(i).val() == "") {
						alert((i+1) + "${f:getMessage('번째 도면 파일을 입력하세요.')}");
						return;
					}
		    	}
		    }
		
		if(!confirm("${f:getMessage('등록하시겠습니까?')}")){
			return;
		}
		gfn_StartShowProcessing();
	    $('#createUserPart').attr("action", getURLString("part", "createPartAction", "do")).submit();

	})
	<%----------------------------------------------------------
	*                      관련 도면 추가
	----------------------------------------------------------%>
	$("#addDrw").click(function() {
		
		var html = "";
		
		html += "<tr align=center bgcolor=#FFFFFF>";
		html += "	<td id=tb_gray width=3% height=22px>";
		html += "		<input type=checkbox name=fileDeleteDrw id=fileDeleteDrw>";
		html += "	</td>";
		html += "	<td id=tb_gray width=97% height=22px>";
		html += "		<input type=file name=drwFile id=drwFile style='width:99%'>";
		html += "	</td>";
		html += "</tr>";
		
		$("#fileTableDrw").append(html);
	})
	
	<%----------------------------------------------------------
	*                      관련 도면 삭제
	----------------------------------------------------------%>
	$("#delDrw").click(function() {
		var obj = $("input[name='fileDeleteDrw']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
	
	<%----------------------------------------------------------
	*                     부품 ERP 속성
	----------------------------------------------------------%>
	$(function() {
		$("#productSubtypePart").change(function() {
			if(this.value == "Product master") {
				$("#productDimensionGroupPart").removeAttr("disabled");
			}else{
				$("#productDimensionGroupPart").attr("disabled", "true");
				$("#productDimensionGroupPart").val("");
			}
		})
	})
})
</script>

<body>

<form name="createUserPart" id="createUserPart" method="post" style="padding:0px;margin:0px" enctype="multipart/form-data">
<input type="hidden" name="wtPartType"		id="wtPartType"		 	value="separable"     />
<input type="hidden" name="source"			id="source"	      		value="make"            />
<input type="hidden" name="lifecycle"   	id="lifecycle"			value="LC_PART"  />
<input type="hidden" name="view"			id="view"        		value="Design" />
<input type="hidden" name="pdmNumber" 		id="pdmNumber" 			value="" />
<input type="hidden" name="islastversion"	id="islastversion" 		value="" />
<input type="hidden" name="fid" 			id="fid"				value="" >
<input type="hidden" name="location"		id="location" 			value="/Default/Drawing_PART" >
<input type="hidden" name="createType" 		id="createType" 		value="part">
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('품목')}${f:getMessage('관리')} > ${f:getMessage('사용자')}${f:getMessage('등록')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<!-- 채번 정보 -->
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('채번')}${f:getMessage('정보')}</b></td>
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
					<td width="10%"></td>
					<td width="10%"></td>
					<td width="350"></td>
					<td width="10%"></td>
					<td width="10%"></td>
					<td width="350"></td>
				</tr>
                            
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" colspan="2" >${f:getMessage('품목분류')} <span style="COLOR: red;">*</span></td>
					<td class="tdwhiteL" colspan="5" >
						<b>
							<span id="locationName">
								/Default/Drawing_PART
							</span>
						</b>
				    </td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
				    <td class="tdblueM" colspan="2" >
							${f:getMessage('품목명')} <span style="color:red;">*</span>
					</td>					
					<td class="tdwhiteM25">
						<input type="text" id="partName" name="partName" value="" class="txt_field" style="width:95%" maxlength="60">
					</td>
					
					<td class="tdblueM" colspan="2" >
							${f:getMessage('품번')} <span style="color:red;">*</span>
					</td>					
					<td class="tdwhiteM25">
						<input type="text" id="userNumber" name="userNumber" value="" class="txt_field" style="width:95%" maxlength="60">
					</td>
				</tr>
           	</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>${f:getMessage('속성')}${f:getMessage('정보')}</b>
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
				<!-- Material, SPEC -->
              	<tr>
               		<td class="tdblueM" colspan="2" width="20%">
						Material
					</td>
					
					<td class="tdwhiteM25" width="388">
						<input type="text" id="Material" name="Material" value="" class="txt_field" style="width:95%" maxlength="30">
					</td>
					
					<td class="tdblueM" colspan="2" width="20%">
						SPEC
					</td>					
					
					<td class="tdwhiteM25" height="25" width="388">
						<input type="text" id="SPEC" name="SPEC" value="" class="txt_field" style="width:95%" maxlength="30">
					</td>
				</tr>
                
				<!-- 표면처리, 사급/도급 -->
				<tr>
					<td class="tdblueM" colspan="2" width="20%">
						${f:getMessage('표면처리')}
					</td>
					
					<td class="tdwhiteM25" width="388">
						<input type="text" id="Surface" name="Surface" value="" class="txt_field" style="width:95%" maxlength="30">
					</td>
					
					<td class="tdblueM" colspan="2" width="20%">
						${f:getMessage('사급')}/${f:getMessage('도급')}
					</td>
					
					<td class="tdwhiteM25" width="388">
						<SELECT name="Subcontract" id="Subcontract" style="width:95%" >
							<OPTION value=''>${f:getMessage('선택')}</OPTION>
							<OPTION value='사급'>${f:getMessage('사급')}</OPTION>
							<OPTION value='도급'>${f:getMessage('도급')}</OPTION>
						</SELECT>
					</td>
				</tr>
				
				<!-- 단위, Supplier -->
				<tr>
					<td class="tdblueM" colspan="2" width="20%">
						${f:getMessage('단위')}
					</td>
					
					<td class="tdwhiteM25">
					    <select name="Unit" id="Unit" style="width:95%">
					    	<OPTION value=''>${f:getMessage('선택')}</OPTION>
					    </select>
					</td>
					
					<td class="tdblueM" colspan="2" width="20%">
						Supplier
					</td>
					
					<td class="tdwhiteM25" width="388">
						<SELECT name="Supplier" id="Supplier" style="width:95%" >
							<OPTION value=''>${f:getMessage('선택')}</OPTION>
                        </SELECT>
					</td>
				</tr>
				
				<!-- Vendor, Weight -->
				<tr>
					<td class="tdblueM" colspan="2" width="20%">
						Vendor
					</td>
					
					<td class="tdwhiteM25" width="388">
						<input type="text" id="Vendor" name="Vendor" value="" class="txt_field" style="width:95%" maxlength="30">
					</td>
					
					<td class="tdblueM" colspan="2" width="20%">
						Weight
					</td>
					
					<td class="tdwhiteM25" width="388">
						<input type="text" id="Weight" name="Weight" value="" class="txt_field" style="width:95%" maxlength="30">
					</td>
				</tr>
				
				<!-- 설명 comment -->			
				<tr>
					<td class="tdblueM" colspan="2">
						${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteM25" colspan="4" >
						<textarea name="Comment" id="Comment" cols="80" rows="5" class="fm_area" style="width:98%" onchange="textAreaLengthCheckName('Comment', '4000', '${f:getMessage('설명')}')"></textarea>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<!-- 채번 정보 -->
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>ERP ${f:getMessage('정보')}</b></td>
	</tr>
    <tr>
        <td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
			    <tr>
			    	<td height="1" width="100%"></td>
			    </tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                            
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" width="20%">
						ProductType<span style="COLOR: red;">*</span>
					</td>
					
					<td class="tdwhiteL" width="13%">
						<select name='productTypePart' id='productTypePart' style="width:95%">
							<option value="">${f:getMessage('선택')}</option>
							<option value="Item">Item</option>
							<option value="Service">Service</option>
						</select>
				    </td>
				    
				    <td class="tdblueM" width="20%">
				    	ProductSubtype<span style="COLOR: red;">*</span>
				    </td>
				    
					<td class="tdwhiteL" width="13%">
						<select name='productSubtypePart' id='productSubtypePart' style="width:95%">
							<option value="">${f:getMessage('선택')}</option>
							<option value="Product">Product</option>
							<option value="Product master">Product master</option>
						</select>
				    </td>
				    
				    <td class="tdblueM" width="20%">
				    	ProductDimensionGroup
				    </td>
				    
					<td class="tdwhiteL" width="13%">
						<select name='productDimensionGroupPart' id='productDimensionGroupPart' style="width:95%" disabled="disabled">
							<option value="">${f:getMessage('선택')}</option>
							<option value="LNB">LNB</option>
							<option value="LNB&BUC">LNB&BUC</option>
						</select>
				    </td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 관련 문서 -->
<jsp:include page="/eSolution/part/include_DocumentFilePath.do">
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
	<jsp:param value="false" name="control"/>
</jsp:include>

<!-- 관련 도면 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>                
          <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('관련도면')}</b></td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM" colspan="2" width="20%">${f:getMessage('관련도면')}<span style="COLOR: red; display: none;" id="drawingPath">*</span></td>
					<td class="tdwhiteL" colspan="4">
						<table width="100%" align="center">
							<tr> 
								<td height="25">
						
									<table border=0 cellpadding="0" cellspacing=2 align="left">
						                <tr>
						                    <td>
						                    	<button type="button" name="" value="" class="btnCustom" title="추가" id="addDrw" name="addDrw">
									            	<span></span>
									            	${f:getMessage('추가')}
									           	</button>
						                    </td>
						                    <td>
						                    	<button type="button" name="" value="" class="btnCustom" title="삭제" id="delDrw" name="delDrw">
									            	<span></span>
									            	${f:getMessage('삭제')}
									           	</button>
						                    </td>
						                </tr>
						            </table>		
								</td>											
							</tr>
						</table>
						
						<table width="100%" cellspacing="0" cellpadding="1" border="0" align="center">
							<tr bgcolor="#f1f1f1"  align=center>
								<td width="100%" height="22" colspan="2" id=tb_inner>${f:getMessage('경로')}</td>
							</tr>
							
							<tbody id="fileTableDrw" >
							</tbody>
							
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 첨부 파일 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>     
	           
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('첨부파일')}</b></td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM" colspan="2" width="20%">${f:getMessage('첨부파일')}</td>
					<td class="tdwhiteL" colspan="4">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createUserPart"/>
						</jsp:include>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 버튼 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td align="center" colspan="2">
			<table border="0" cellpadding="0" cellspacing="4" align="center">
				<tr>
					<td>
						<button type="button" name="" value="" class="btnCRUD" title="${f:getMessage('등록')}" id="createBtn" name="createBtn">
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
						<button title="Reset" class="btnCustom" type="reset" name="listBtn" id="listBtn">
							<span></span>
							${f:getMessage('목록')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>