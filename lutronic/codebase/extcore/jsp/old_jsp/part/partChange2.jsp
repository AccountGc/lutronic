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
$(document).ready(function() {
	
})

$(function() {
	$("input[name='partType']").click(function() {
		var number = this.value;
		$("#partType1_" + number).attr("disabled", !this.checked);
		$("#partType2_" + number).attr("disabled", !this.checked);
		$("#partType3_" + number).attr("disabled", !this.checked);
		$("#seq_" + number).attr("disabled", !this.checked);
		$("#etc_" + number).attr("disabled", !this.checked);
	})
	
	$('select').on('change', '', function (e) {
		var id;
		var number = this.name.substring(this.name.indexOf("_")+1);
		var name = this.name.substring(0,this.name.indexOf("_"));
		//console.log(name);
		if(name == "partType1") {
			$("#codeNum_"+number).html($("#partType1_"+number).val() + $("#partType2_"+number).val() + $("#partType3_"+number).val()+ $("#seq_"+number).val()+ $("#etc_"+number).val());
			id = "partType2";
		}else if(name == "partType2") {
			$("#codeNum_"+number).html($("#partType1_"+number).val() + $("#partType2_"+number).val() + $("#partType3_"+number).val()+ $("#seq_"+number).val()+ $("#etc_"+number).val());
			id = "partType3";
		}else if(name == "partType3") {
			$("#codeNum_"+number).html($("#partType1_"+number).val() + $("#partType2_"+number).val() + $("#partType3_"+number).val()+ $("#seq_"+number).val()+ $("#etc_"+number).val());
			return;
		}
		
		numberCodeList(id+"_"+number, $("#" + this.id + " option:selected").attr("title"));
	});
	
	$('input[type="text"]').on('keypress','', function(event) {
		var id = this.id.split("_")[0];
		
		if(id == 'seq') {
			return common_isNumber(event, this);
		}else if(id == 'etc') {
			return common_isNumber(event, this);
		}
	})
	
	$('input[type="text"]').on('keyup','', function(event) {
		var id = this.id.split("_")[0];
		console.log(id);
		if(id == 'seq' || id == 'etc') {
			//var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
			$("#"+ this.id.replace(id,'codeNum')).html($("#"+this.id.replace(id,'partType1')).val() + $("#"+this.id.replace(id,'partType2')).val() + $("#"+this.id.replace(id,'partType3')).val()+ $("#"+this.id.replace(id,'seq')).val()+ $("#"+this.id.replace(id,'etc')).val());
		}else if(id == "partName1" || id == "partName2" || id == "partName3") {
			var number = this.id.substring(this.id.indexOf("_")+1);
			//console.log(this.id.indexOf("_")+1);
			//autoSearchPartName(id, this.value, number);
			var charCode = (event.which) ? event.which : event.keyCode;
			if((charCode == 38 || charCode == 40) ) {
				if(!$( "#"+id+"Search_"+number ).is( ":hidden" )){
					var isAdd = false;
					if(charCode == 38){
						isAdd = true;
					}
					movePartNameFocus(id, isAdd, number);
				}
			} else if(charCode == 13 || charCode == 27){
				$("#" + id + "Search_"+number).hide();
			} else {
				autoSearchPartName(id, this.value, number);
			}
		}
	})
	
	$('input[type="text"]').on('focusout','', function(event) {
		var id = this.id.split("_")[0];
		
		if(id == "partName1" || id == "partName2" || id == "partName3") {
			
			var number = this.id.replace(id,'')
			
			$("#" +  id + "Search"+number).hide();
		}else if(id == 'partName4') {
			console.log(this.value)
			$(this).val(this.value.toUpperCase());
		}
	})
	
	$("#setNumber").click(function() {
		if(validationCheck()) {
			return;
		}
		
		if (confirm("${f:getMessage('변경하시겠습니까?')}")){
			
			var form = $("form[name=partChange]").serialize();
			var url	= getURLString("part", "actionBom", "do");
			
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('변경 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('변경 성공하였습니다.')}");
						document.location.reload();
					}else {
						alert("${f:getMessage('변경 실패하였습니다.')}\n" + data.message);
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
window.numberCodeList = function(id, parentCode1) {
	var data = common_numberCodeList("PARTTYPE", parentCode1, false);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			$("#"+ id).append("<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>");
		}
	}
}

<%----------------------------------------------------------
*                      품목명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchPartName = function(id, value, number) {
	if($.trim(value) == "") {
		addSearchList(id, '', number, true);
	} else {
		var codeType = id.toUpperCase();
		var data = common_autoSearchName(codeType, value);
		
		addSearchList(id, eval(data.responseText), number, false);
	}
}

<%----------------------------------------------------------
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, number, isRemove) {
	
	$("#" + id + "UL_" + number + " li").remove();
	if(isRemove) {
		$("#" + this.id + "Search_"+number).hide();
	}else{
		//console.log(data.length > 0);
		if(data.length > 0) {
			console.log("#" + id + "Search_"+number);
			$("#" + id + "Search_"+number).show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL_"+number).append("<li title='" + id + "_" + number + "'>" + data[i].name);
			}
		}else {
			$("#" + id + "Search_"+number).hide();
		}
	}
}

function validationCheck() {
	var total = $("input[name='number']").length;
	var check = $("input[name='checkOid']:checked").length;
	if(check > 0) {
		for(var i=0; i<total; i++) {
			if($("input[name='checkOid']").eq(i).is(":checked")) {
				var number = $("input[name='number']").eq(i).val();
				
				if($("input[name='partType']").eq(i).is(":checked")){
					
					
					if($("#partType1_"+number).val() == ""){
						alert("${f:getMessage('부품구분')}${f:getMessage('을(를) 선택하세요.')}");
						$("#partType1_"+number).focus();
						return true;
					}
					
					if($("#partType2_"+number).val() == ""){
						alert("${f:getMessage('대분류')}${f:getMessage('을(를) 선택하세요.')}");
						$("#partType2_"+number).focus();
						return true;
					}
					
					if($("#partType3_"+number).val() == ""){
						alert("${f:getMessage('중분류')}${f:getMessage('을(를) 선택하세요.')}");
						$("#partType3_"+number).focus();
						return true;
					}
				}
					
				if($.trim($("#partName1_"+number).val()) == ""
						&& $.trim($("#partName2_"+number).val()) == ""
						&& $.trim($("#partName3_"+number).val()) == ""
						&& $.trim($("#partName4_"+number).val()) == ""){
					alert("${f:getMessage('품목명')}${f:getMessage('을(를) 입력하세요.')}");
					$("#partName1_"+number).focus();
					return true;
				}else if(checkTextLength(number) > 40) {
					alert("${f:getMessage('품목명')}${f:getMessage('은(는) 40자 이내로 입력하세요.')}");
					$("#partName1_"+number).focus();
					return true;
				}
			}
		}
	}else {
		alert("${f:getMessage('변경할')} ${f:getMessage('품목')}${f:getMessage('을(를) 선택하세요.')}");
		return true;
	}
	return false;
}

window.checkTextLength = function(number) {
	var name = '';
	
	if(!$.trim($('#partName1_' + number).val()) == '') {
		name += $('#partName1_' + number).val();
	}
	
	if(!$.trim($('#partName2_' + number).val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName2_' + number).val();
	}
	
	if(!$.trim($('#partName3_' + number).val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName3_' + number).val();
	}
	
	if(!$.trim($('#partName4_' + number).val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName4_' + number).val();
	}
	
	return name.length;
}

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.movePartNameFocus = function(id,isAdd, number) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL_" + number + " li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL_" + number + " li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL_" + number + " li").eq(i).removeClass("hover");
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
	$("#" + id + "UL_" + number + " li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL_" + number + " li").eq(addCount).text());
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

<form name=partChange id=partChange  method=post  >

<input type="hidden" name="rootOid" id="rootOid" value="<c:out value='${partOid }'/>">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white>${f:getMessage('부품')}${f:getMessage('진채번')}</font></B></td>
		   		</tr>
			</table>
			
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									<button type="button" class="btnCRUD" id="setNumber">
										<span></span>
										${f:getMessage('저장')}
									</button>
								</td>
								
								<td>
									<button type="button" class="btnClose" onclick="self.close();" >
										<span></span>
										${f:getMessage('닫기')}
									</button>
								</td>
							</tr>
						</table>
		    		</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%"  border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	    		<tr>
	    			<td height=1 width=100%></td>
	    		</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  >
			    <tr height=25>
			    	<td class="tdblueM" width="1%"></td>
		        	<td class="tdblueM" width="12%">${f:getMessage('가도번')}</td>
		        	<td class="tdblueM" width="">${f:getMessage('품목명')}</td>
		        	<td class="tdblueM" width="5%">${f:getMessage('진도번')}</td>
					<td class="tdblueM" width="3%">Rev.</td>
					<td class="tdblueM" width="1%"></td>       
		        	<td class="tdblueM" width="10%">${f:getMessage('부품구분')}</td>
		        	<td class="tdblueM" width="10%">${f:getMessage('대분류')}</td>
	      			<td class="tdblueM" width="10%">${f:getMessage('중분류')}</td>
	      			<td class="tdblueM" width="5%">SEQ</td>
	      			<td class="tdblueM" width="5%">${f:getMessage('기타')}</td>
	      			<td class="tdblueM" width="10%">${f:getMessage('부품명1')}</td>
	      			<td class="tdblueM" width="10%">${f:getMessage('부품명2')}</td>
	      			<td class="tdblueM" width="10%">${f:getMessage('부품명3')}</td>
	      			<td class="tdblueM" width="10%">${f:getMessage('부품명4')}</td>
	    		</tr>
	
				<c:forEach items="${list }" var="list">
	
				<tr>
					<td class="tdwhiteM">
						<input type="hidden" name="number" value="<c:out value='${list.number }'/>">
						<input type ="checkbox" name="checkOid" value="<c:out value='${list.partOid }'/>" <c:out value='${list.checked }'/> <c:out value='${list.disabled }'/>>
					</td>	
					
					<td class="tdwhiteL" >
						<c:out value='${list.img }' escapeXml="false"/>
						<c:out value='${list.img2 }' escapeXml="false"/>
						<c:out value='${list.icon }' escapeXml="false"/>
						<a href="javascript:void(0);" onClick="JavaScript:openView('<c:out value='${list.partOid }'/>');" >
							<c:out value='${list.number }'/>
						</a>
						
					</td>
					
					<td class="tdwhiteL">
						<div style="width:230px;border:0px;padding:0px;margin:0px;text-overflow:ellipsis;overflow:hidden;">
							<c:out value='${list.name }'/>
						</div>
					</td>
					
					<td class="tdwhiteL" >
						<div id="codeNum_<c:out value='${list.number }'/>" style="font-weight:bold;">&nbsp;</div>
					</td>
					
					<td  class="tdwhiteM" >
						<c:out value='${list.level }'/>
					</td>
					
					<td  class="tdwhiteL" >
						<input type="checkbox" name="partType" value="<c:out value='${list.number }'/>" checked="checked" <c:out value='${list.disabled }'/>>
						
					</td>
					
					<td  class="tdwhiteM" >
						<SELECT name="partType1_<c:out value='${list.number }'/>" id="partType1_<c:out value='${list.number }'/>" style="width:95%" <c:out value='${list.disabled }'/> >
							<OPTION value=''>${f:getMessage('선택')}</OPTION>
							
							<c:forEach items="${partType }" var="partType">
								<option value="<c:out value='${partType.code }'/>" title=<c:out value="${partType.oid }"/>>
									[<c:out value='${partType.code }'/>] <c:out value='${partType.name }'/>
								</option>
							</c:forEach>
						
                        </SELECT>
					</td>
					
					<td class="tdwhiteM">
						<SELECT name="partType2_<c:out value='${list.number }'/>" id="partType2_<c:out value='${list.number }'/>" style="width:95%" <c:out value='${list.disabled }'/>>
							<OPTION value=''>${f:getMessage('선택')}</OPTION>
                        </SELECT>
                        
					</td>
					
					<td class="tdwhiteM">
						<SELECT name="partType3_<c:out value='${list.number }'/>" id="partType3_<c:out value='${list.number }'/>" style="width:95%" <c:out value='${list.disabled }'/>>
							<OPTION value=''>${f:getMessage('선택')}</OPTION>
                        </SELECT>
					</td>
					
					<td class="tdwhiteM">
						<input type="text" name="seq_<c:out value='${list.number }'/>" id="seq_<c:out value='${list.number }'/>" maxlength="3" style="width: 50%" <c:out value='${list.disabled }'/>>
					</td>
					
					<td class="tdwhiteM">
						<input type="text" name="etc_<c:out value='${list.number }'/>" id="etc_<c:out value='${list.number }'/>" maxlength="2" style="width: 50%" <c:out value='${list.disabled }'/>>
					</td>
					
					<td class="tdwhiteM">
						<input type="text" name="partName1_<c:out value='${list.number }'/>" id="partName1_<c:out value='${list.number }'/>" style="width: 90%" value="<c:out value='${list.partName1 }'/>" <c:out value='${list.disabled }'/>/>
						<div id="partName1Search_<c:out value='${list.number }'/>" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName1UL_<c:out value='${list.number }'/>" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
					</td>
					<td class="tdwhiteM">
						<input type="text" name="partName2_<c:out value='${list.number }'/>" id="partName2_<c:out value='${list.number }'/>" style="width: 90%" value="<c:out value='${list.partName2 }'/>" <c:out value='${list.disabled }'/>/>
						<div id="partName2Search_<c:out value='${list.number }'/>" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName2UL_<c:out value='${list.number }'/>" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
					</td>
					
					<td class="tdwhiteM">
						<input type="text" name="partName3_<c:out value='${list.number }'/>" id="partName3_<c:out value='${list.number }'/>" style="width: 90%" value="<c:out value='${list.partName3 }'/>" <c:out value='${list.disabled }'/>/>
						<div id="partName3Search_<c:out value='${list.number }'/>" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName3UL_<c:out value='${list.number }'/>" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
					</td>
					<td class="tdwhiteM">
						<input type="text" name="partName4_<c:out value='${list.number }'/>" id="partName4_<c:out value='${list.number }'/>" style="width: 90%; text-transform: uppercase;" value="<c:out value='${list.partName4 }'/>" <c:out value='${list.disabled }'/>/>
					</td>
					
				</tr>
				
				</c:forEach>
				
			</table>
		</td>
	</tr>
</table>
</form>

</body>
</html>