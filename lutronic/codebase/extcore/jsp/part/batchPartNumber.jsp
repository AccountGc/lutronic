<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
var gubunList = [{"code":"-","oid":"-","value":"-"}];
var mainvalueList = [{"code":"-","oid":"-","value":"-"}];
var middleValueList = [{"code":"-","oid":"-","value":"-"}];

/* var partName1List = ["-"];
var partName2List =["-"];
var partName3List =["-"]; */

$(function() {
	/* setNumberCodelList("PARTNAME1",null,"partName1List","");
	setNumberCodelList("PARTNAME2",null,"partName2List","");
	setNumberCodelList("PARTNAME3",null,"partName3List",""); */
	$('input[type="text"]').on('keypress','', function(event) {
		var id = this.id;
		
		if(id == 'seq') {
			return common_isNumber(event, this);
		}else if(id == 'etc') {
			return common_isNumber(event, this);
		}
	});
	$('#apply').click(function() {
		
		var dataMap = new Object();
		var option = "";
		
		
		dataMap["gubun"] = $('#gubun').val();
		var obj = $('#gubun');
		if(null!=obj)
			option = $('option:selected', obj).attr('pOid');
		dataMap["gubunOid"] = option;
		dataMap["main"] = $('#main').val();
		obj = $('#main');
		if(null!=obj)
			option = $('option:selected', obj).attr('pOid');
		dataMap["mainOid"] = option;
		dataMap["middle"] = $('#middle').val();
		opener.apply(dataMap);
		
	});
	/* $('input[type="text"]').on('keyup','', function(event) {
		var id = this.id;
		if(id == "partName1" || id == "partName2" || id == "partName3") {
			var number = this.id.substring(this.id.indexOf("_")+1);
			var charCode = (event.which) ? event.which : event.keyCode;
			if((charCode == 38 || charCode == 40) ) {
				if(!$( "#"+id+"Search").is( ":hidden" )){
					var isAdd = false;
					if(charCode == 38){
						isAdd = true;
					}
					movePartNameFocus(id, isAdd, number);
				}
			} else if(charCode == 13 || charCode == 27){
				$("#" + id + "Search").hide();
			} else {
				autoSearchPartName(id, this.value, number);
			}
		}
	}) */
});
<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.movePartNameFocus = function(id,isAdd, number) {
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
function setNumberCodelList2(type, obj,calllistStr,pList){ 

	var option = "";
	if(null!=obj)
		option = $('option:selected', obj).attr('pOid');
	console.log(option);
	var data = common_numberCodeList(type,option, false);
	
	var data2 =eval(data.responseText);
	for(var i=0; i<data2.length; i++) {
		
		var dataMap = new Object();
		
		dataMap["code"] = data2[i].code;
		dataMap["oid"] = data2[i].oid;
		dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		
		var code = data2[i].code;
		var value = data2[i].name;
		if(type =="PARTTYPE"){
			if(calllistStr=="gubunList")
				gubunList[i] = dataMap;
			else if(calllistStr=="mainvalueList")
				mainvalueList[i] = dataMap;
			else if(calllistStr=="middleValueList")
				middleValueList[i] = dataMap;
		} 
		
	}
}
<%----------------------------------------------------------
*                      NumberCode Setup
----------------------------------------------------------%>
function setNumberCodelList(type, obj,calllistStr,pList){ //numberCodeList
	
	var option = "";
	if(null!=obj)
		option = $('option:selected', obj).attr('pOid');
	console.log(option);
	var data = common_numberCodeList(type,option, false);
	
	var data2 =eval(data.responseText);
	 mainvalueList = [{"code":"-","oid":"-","value":"-"}];
	  middleValueList = [{"code":"-","oid":"-","value":"-"}];

	for(var i=0; i<data2.length; i++) {
		
		var dataMap = new Object();
		
		dataMap["code"] = data2[i].code;
		dataMap["oid"] = data2[i].oid;
		dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		
		var code = data2[i].code;
		var value = data2[i].name;
		if(type =="PARTTYPE"){
			if(calllistStr=="gubunList")
				gubunList[i] = dataMap;
			else if(calllistStr=="mainvalueList")
				mainvalueList[i] = dataMap;
			else if(calllistStr=="middleValueList")
				middleValueList[i] = dataMap;
		}/* else if(type == "PARTNAME1" || type == "PARTNAME2" || type == "PARTNAME3"){
			if(calllistStr=="partName1List")
				partName1List[i] = value;
			else if(calllistStr=="partName2List")
				partName2List[i] = value;
			else if(calllistStr=="partName3List")
				partName3List[i] = value;
		} */
		
	}
	if(type =="PARTTYPE"){
		if(calllistStr=="mainvalueList"){
			callList(mainvalueList,"main");
		    fnResetSelectBox("middle");
		}else if(calllistStr=="middleValueList"){
			fnResetSelectBox("middle");
			callList(middleValueList,"middle");
		}
	}/* else if(type == "PARTNAME1" || type == "PARTNAME2" || type == "PARTNAME3"){
		if(calllistStr=="partName1List")
			callList(partName1List,"partName1");
		else if(calllistStr=="partName2List")
			callList(partName2List,"partName2");
		else if(calllistStr=="partName3List")
			callList(partName3List,"partName3");
	} */
}
function callList(objList,objName){
	fnResetSelectBox(objName);
	console.log($("form[name='batchPartNumberForm'] select[name='"+objName+"']")[0]);
	console.log($("form[name='batchPartNumberForm'] select[name='"+objName+"']")[0].length);
	for(var i=0,len=objList.length; i<len; i++) {
		$("form[name='batchPartNumberForm'] select[name='"+objName+"']").append("<option value='"+objList[i].code+"' pOid='"+objList[i].oid+"'> "+objList[i].value+" </option>");
	}
}
//select box 초기화 하는 함수.
function fnResetSelectBox(objName)
{
     $("form[name='batchPartNumberForm'] select[name='"+objName+"'] option").remove();   
     $("form[name='batchPartNumberForm'] select[name='"+objName+"']").append("<option value=''> ${f:getMessage('선택')} </option>");
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
	console.log("#" + id + "UL li");
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search_"+number).hide();
	}else{
		//console.log(data.length > 0);
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id+"'>" + data[i].name);
			}
		}else {
			$("#" + id + "Search").hide();
		}
	}
}

</script>

<body>

<form name="batchPartNumberForm"  method="post" >
<input type="hidden" name="oid" 		id="oid"		value="">
<input type="hidden" name="bomType" 	id="bomType"	value="">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color="white">속성 일괄 적용</font></B></td>
		   		</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
				<tr height="30">
					<td>
						<b>
							${f:getMessage('속성 일괄 적용')}
						</b>
					</td>
					<td align="right">
		  				<button type="button" name="apply" id="apply" class="btnClose" >
							<span></span>
							${f:getMessage('적용')}
						</button>
		  				<button type="button" name="" id="" class="btnClose" onclick="self.close()">
							<span></span>
							${f:getMessage('닫기')}
						</button>
		  			</td>
		  			
		  			
		    	</tr>
		    </table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%"  border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	    		<tr><td height="1" width="100%"></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				<col width="30%"/>
				<col width="70%"/>
				<!-- 프로젝트 코드 -->
			    <tr height=25>
			    	<td class="tdblueM" width="2%" >
			    		${f:getMessage('부품 구분')}
			    	</td>
					<td class="tdwhiteL" colspan="1"=>
						<select name="gubun" id="gubun" onchange="javascript:setNumberCodelList('PARTTYPE',this,'mainvalueList','gubunList');">
							<option value=''> ${f:getMessage('선택')} </option>
							<c:forEach items="${gubunList }" var="gubunList">
									<option value="<c:out value='${gubunList.code }'/>" pOid=<c:out value="${gubunList.oid }"/>>
										[<c:out value='${gubunList.code }'/>] <c:out value='${gubunList.name }'/>
									</option>
								</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="tdblueM" >
			    		${f:getMessage('대 분류')}
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="main" id="main" onchange="javascript:setNumberCodelList('PARTTYPE',this,'middleValueList','mainvalueList');">
								<option value=''> ${f:getMessage('선택')} </option>
						</select>
			    	</td>
			    </tr>
				<tr>
			    	<td class="tdblueM" >
			    		${f:getMessage('중 분류')}
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="middle" id="middle">
							<option value=''> ${f:getMessage('선택')} </option>
						</select>
			    	</td>
				</tr>
				<!-- 
				<tr>
			    	<td class="tdblueM" >
			    		Seq
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="seq" id="seq" class="txt_field" maxlength="3"  value="" class="txt_field" style="width: 95%" />
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		기타
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="weight" id="weight" value="" maxlength="2"  class="txt_field" style="width: 95%"/>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		부품명1
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="partName1" id="partName1" class="txt_field" value="" class="txt_field" style="width: 95%" />
			    		<div id="partName1Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName1UL" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		부품명2
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="partName2" id="partName2" class="txt_field" value="" class="txt_field" style="width: 95%" />
			    		<div id="partName2Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName2UL" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		부품명3
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="partName3" id="partName3" class="txt_field" value="" class="txt_field" style="width: 95%" />
			    		<div id="partName3Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white;">
							<ul id="partName3UL" style="list-style-type: none; padding-left: 5px; text-align: left">
							</ul>
						</div>
			    	</td>
				</tr> -->
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>