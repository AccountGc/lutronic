<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
$(function() {
	$('#apply').click(function() {
		
		var dataMap = new Object();
		
		dataMap["model"] = $('#model').val();
		dataMap["productmethod"] = $('#productmethod').val();
		dataMap["deptcode"] = $('#deptcode').val();
		dataMap["unit"] = $('#unit').val();
		dataMap["manufacture"] = $('#manufacture').val();
		dataMap["mat"] = $('#mat').val();
		dataMap["finish"] = $('#finish').val();
		dataMap["remark"] = $('#remark').val();
		dataMap["weight"] = $('#weight').val();
		dataMap["specification"] = $('#specification').val();
		opener.apply(dataMap);
		
	})
})

</script>

<body>

<form name="PartTreeForm"  method="post" >
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
			    		${f:getMessage('프로젝트 코드')}
			    	</td>
					<td class="tdwhiteL" >
						<select name="model" id="model">
							<option value=''> ${f:getMessage('선택')} </option>
							<c:forEach items="${model }" var="model">
								<option value="<c:out value='${model.code }'/>" title=<c:out value="${model.oid }"/>
									<c:if test="${list.model eq model.code }">
										selected
									</c:if>
								>
									[<c:out value='${model.code }'/>] <c:out value='${model.name }'/>
								</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="tdblueM" >
			    		${f:getMessage('제작방법')}
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="productmethod" id="productmethod">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${productmethod }" var="productmethod">
									<option value="<c:out value='${productmethod.code }'/>" title=<c:out value="${productmethod.oid }"/>>
										[<c:out value='${productmethod.code }'/>] <c:out value='${productmethod.name }'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
			    </tr>
				<tr>
			    	<td class="tdblueM" >
			    		${f:getMessage('부서')}
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="deptcode" id="deptcode">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${deptCode }" var="deptCode">
									<option value="<c:out value='${deptCode.code }'/>" title=<c:out value="${deptCode.oid }"/> >
										[<c:out value='${deptCode.code }'/>] <c:out value='${deptCode.name }'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		${f:getMessage('단위')}
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="unit" id="unit">
								<c:forEach items="${unit }" var="unit">
									<option value="<c:out value='${unit}'/>" title=<c:out value="${unit}"/>>
										
									
										<c:out value='${unit}'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		MANUFATURER
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="manufacture" id="manufacture">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${manufacture }" var="manufacture">
									<option value="<c:out value='${manufacture.code }'/>" title=<c:out value="${manufacture.oid }"/>>
										[<c:out value='${manufacture.code }'/>] <c:out value='${manufacture.name }'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		재질
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="mat" id="mat">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${mat }" var="mat">
									<option value="<c:out value='${mat.code }'/>" title=<c:out value="${mat.oid }"/>>
										[<c:out value='${mat.code }'/>] <c:out value='${mat.name }'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		후처리
			    	</td>
			    	<td class="tdwhiteL" >
			    		<select name="finish" id="finish">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${finish }" var="finish">
									<option value="<c:out value='${finish.code }'/>" title=<c:out value="${finish.oid }"/>>
										[<c:out value='${finish.code }'/>] <c:out value='${finish.name }'/>
									</option>
								</c:forEach>
							</select>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		비고
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="remark" id="remark" class="txt_field" value="" class="txt_field" style="width: 95%" />
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		무게
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="weight" id="weight" value="" class="txt_field" style="width: 95%"/>
			    	</td>
				</tr>
				<tr>
			    	<td class="tdblueM" >
			    		사양
			    	</td>
			    	<td class="tdwhiteL" >
			    		<input type="text" name="specification" id="specification" class="txt_field" value="" class="txt_field" style="width: 95%" />
			    	</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>