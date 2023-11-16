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
$(function () {
	$('#attrName').change(function() {
		$('#type').val($(this).children("option:selected").attr('class'));
	})
	$("#doSubmit").click(function() {
		if($.trim($("#oid").val()) == ""){
			alert("OID ${f:getMessage('값')}${f:getMessage('을(를) 입력하세요.')}");
			$("#oid").focus();
			return;
		}else if($("#attrName").val() == "") {
			alert("${f:getMessage('속성명')}${f:getMessage('을(를) 선택하세요.')}");
			$("#attrName").focus();
			return;
		}else if($.trim($("#attrValue").val()) == "") {
			alert("${f:getMessage('속성값')}${f:getMessage('을(를) 입력하세요.')}");
			$("#attrValue").focus();
			return;
		}
		
		if(!confirm("${f:getMessage('변경하시겠습니까?')}")) {
			return;
		}
		
		var form = $("form[name=changeIBA]").serialize();
		var url	= getURLString("groupware", "changeIBAAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: false,
			cache: false,

			error:function(data){
				var msg = "${f:getMessage('데이터 검색오류')}";
				alert(msg);
			},

			success:function(data){
				if(data.result) {
					alert("${f:getMessage('변경 성공하였습니다.')}");
				}else {
					alert(data.message);
				}
			}
		});
	})
	
	$("#goMain").click(function() {
		document.location = getURLString("groupware", "main", "do");
	})
})
</script>


<body>

<form name="changeIBA" id="changeIBA" >

<input type='hidden' name='type' id='type' value='' />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('속성값 변경')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="400" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td align="center" valign="top" width=100% style="padding-right:4;padding-left:4">
			<table width="400" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
				<tr height=5>
					<td>
						<table width="400" border="0" cellpadding="0" cellspacing="3">
						    <tr align="center">
						        <td valign="top" style="padding:0px 0px 0px 0px">
						            <table width="400" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
						                <tr><td height="1" width="100%"></td></tr>
						            </table>
						            <table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
							            <tr>
							                <td class="tdblueM" width="100">OID</td>
							                <td class="tdwhiteL" width="300">
							                	<input type="text" id="oid" name="oid" id="oid" value="" style="width:95%" />
							                </td>
							            </tr>
							            
							            <tr>
							            	<td class="tdblueM">
							            		${f:getMessage('속성명')}
							            	</td>
							            	<td class="tdwhiteL">
							            		<%-- 
							            		<select name="attrName" id="attrName">
							            			<option value="">${f:getMessage('선택')}</option>
							            			<option value="PartNO">${f:getMessage('부품번호')}</option>
							            			<option value="DWGNO">${f:getMessage('도면번호')}</option>
							            			<option value="Description">${f:getMessage('도면명')}</option>
							            			<option value="Version">${f:getMessage('버전')}</option>
							            			<option value="Date">${f:getMessage('등록일')}</option>
							            			<option value="Approval">${f:getMessage('승인자')}</option>
							            			<option value="Check">${f:getMessage('검도자')}</option>
							            			<option value="Material">${f:getMessage('재질')}</option>
							            			<option value="Surface">${f:getMessage('표면처리')}</option>
							            			<option value="Subcontract">${f:getMessage('사급')}/${f:getMessage('도급')}</option>
							            			<option value="Supplier">Supplier</option>
							            			<option value="Vendor">Vendor</option>
							            			<option value="Weight">Weight</option>
							            			<option value="Comment">${f:getMessage('설명')}</option>
							            			<option value="ProductType">${f:getMessage('제품유형')}</option>
							            			<option value="ProductSubtype">${f:getMessage('제품하위유형')}</option>
							            			<option value="ProductDimensionGroup">${f:getMessage('제품차원그룹')}</option>
							            			<option value="LatestVersionFlag">${f:getMessage('최신버전')}</option>
							            			<option value="PartCheck">${f:getMessage('제품부품 구분')}</option>
							            			<option value="SeqCheck">${f:getMessage('채번유무')}</option>
							            		</select>
							            		--%>
							            		
							            		<select name="attrName" id="attrName">
							            			<option value="">${f:getMessage('선택')}</option>
							            			
							            			<c:forEach items="${list }" var="attrList">
							            				<option value="${attrList.key }" class="${attrList.type }">
							            					${attrList.name }
							            				</option>
							            			</c:forEach>
							            			
							            		</select>
							            		
							            		
							            	</td>
							            </tr>
							            
							            <tr>
							            	<td class="tdblueM">
							            		${f:getMessage('속성값')}
							            	</td>
							            	<td class="tdwhiteL">
							            		<input type="text" id="attrValue" name="attrValue" id="attrValue" value="" style="width:95%" />
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
						                    	<button type="button" class="btnCRUD" id="doSubmit">
						                    		<span></span>
						                    		${f:getMessage('변경')}
						                    	</button>
						                    </td>
						                    
						                    <td>
						                    	<button type="reset" class="btnCustom">
						                    		<span></span>
						                    		${f:getMessage('초기화')}
						                    	</button>
						                    </td>
						                    
						            		<td>
						            			<button type="button" class="btnCustom" id="goMain">
						                    		<span></span>
						                    		${f:getMessage('메인화면으로')}
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