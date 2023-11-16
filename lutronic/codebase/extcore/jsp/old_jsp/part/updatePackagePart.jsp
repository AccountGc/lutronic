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
	$("#updatePack").click(function() {
		if(validationCheck()) {
			return;
		}
		
		if (confirm("${f:getMessage('변경하시겠습니까?')}")){
			
			var form = $("form[name=updatePackagePart]").serialize();
			var url	= getURLString("part", "updatePackagePartAction", "do");
			
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
						location.href = getURLString("part", "updatePackagePart", "do") + "?oid=" + data.oid;
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
	$('#allCheck').click(function() {
		if(this.checked) {
			$("input[name='modifyChecks']").prop("checked", "checked");
		}else {
			$("input[name='modifyChecks']").prop("checked", "");
		}
	})
})

function validationCheck() {
	var check = $("input[name='modifyChecks']:checked").length;
	if(check > 0) {
		var total = $("input[name='modifyChecks']").length;
		for(var i=0; i<total; i++) {
			if($("input[name='modifyChecks']").eq(i).is(":checked")) {
				var number = $("input[name='modifyChecks']").eq(i).val();
				
				if($("#model_"+number).val() == ""){
					alert("${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
					$("#model_"+number).focus();
					return true;
				}
				
				if($("#deptcode_"+number).val() == ""){
					alert("${f:getMessage('부서')}${f:getMessage('을(를) 선택하세요.')}");
					$("#deptcode_"+number).focus();
					return true;
				}
				
				if($("#productmethod_"+number).val() == ""){
					alert("${f:getMessage('제작방법')}${f:getMessage('을(를) 선택하세요.')}");
					$("#productmethod_"+number).focus();
					return true;
				}
				
				if($("#unit_"+number).val() == ""){
					alert("${f:getMessage('단위')}${f:getMessage('을(를) 선택하세요.')}");
					$("#unit_"+number).focus();
					return true;
				}
			}
		}
		return false;
	} else {
		alert("${f:getMessage('수정할 품목')}${f:getMessage('을(를) 선택하세요.')}");
		return true;
	}
}
</script>

<body>

<form name=updatePackagePart id=updatePackagePart  method=post  >

<input type="hidden" name="oid" id="oid" value="<c:out value='${oid }'/>">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white>${f:getMessage('품목')}${f:getMessage('일괄')}${f:getMessage('수정')}</font></B></td>
		   		</tr>
			</table>
			
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									<button type="button" class="btnCRUD" id="updatePack">
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
			    	<td class="tdblueM" width="2%" rowspan="2">
			    		<input type="checkbox" id='allCheck'>
			    	</td>
			    	
		        	<td class="tdblueM" width="10%" rowspan="2">
		        		${f:getMessage('품목번호')}
		        	</td>
		        	
		        	<td class="tdblueM" width="8%" rowspan="2">
		        		${f:getMessage('품목명')}
		        	</td>
		        	
					<td class="tdblueM" width="10%">
						${f:getMessage('프로젝트코드')}
						<span style="color:red;">*</span>
					</td>
					
		        	<td class="tdblueM" width="6%">
			        	${f:getMessage('부서')}
			        	<span style="color:red;">*</span>
		        	</td>
		        	
	      			<td class="tdblueM" width="12%">
	      				MANUFATURER
	      			</td>
	      			
	      			<td class="tdblueM" width="8%">
	      				${f:getMessage('후처리')}
	      			</td>
	      			
	      			<td class="tdblueM" width="*">
	      				${f:getMessage('무게')}(g)
	      			</td>
	      		</tr>
	      		
	      		<tr>
		        	<td class="tdblueM" width="">
			        	${f:getMessage('제작방법')}
			        	<span style="color:red;">*</span>
		        	</td>
		        	
	      			<td class="tdblueM" width="">
		      			${f:getMessage('단위')}
		      			<span style="color:red;">*</span>
	      			</td>
	      			
	      			<td class="tdblueM" width="">
	      				${f:getMessage('재질')}
	      			</td>
	      			
	      			<td class="tdblueM" width="">
	      				${f:getMessage('비고')}
	      			</td>
	      			
	      			<td class="tdblueM" width="">
	      				${f:getMessage('사양')}
	      			</td>
	    		</tr>
	
				<c:forEach items="${list }" var="list">
	
					<tr>
						<td class="tdwhiteM" rowspan="2">
							<input type="checkbox" name='modifyChecks' value='<c:out value='${list.number }'/>' >
							<input type="hidden" name="oids_<c:out value='${list.number }'/>" value="<c:out value='${list.partOid }'/>"/>
							<input type="hidden" name="number_<c:out value='${list.number }'/>" value="<c:out value='${list.number }'/>"/>
						</td>
						
						
						<td class="tdwhiteL" rowspan="2">
							<c:out value='${list.img }' escapeXml="false"/>
							<c:out value='${list.img2 }' escapeXml="false"/>
							<c:out value='${list.icon }' escapeXml="false"/>
							<a href="javascript:void(0);" onClick="JavaScript:openView('<c:out value='${list.partOid }'/>');" >
								<c:out value='${list.number }'/>
							</a>
						</td>
						
						<td class="tdwhiteL" rowspan="2">
							<c:out value='${list.name }'/>
						</td>
						
						<!-- 프로젝트 코드 -->
						<td class="tdwhiteL" >
							<select name="model_<c:out value='${list.number }' />" id="model_<c:out value='${list.number }' />">
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
						
						<!-- 부서 -->
						<td class="tdwhiteL" >
							<select name="deptcode_<c:out value='${list.number }' />" id="deptcode_<c:out value='${list.number }' />">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${deptCode }" var="deptCode">
									<option value="<c:out value='${deptCode.code }'/>" title=<c:out value="${deptCode.oid }"/> 
										<c:if test="${list.deptcode eq deptCode.code }">
											selected
										</c:if>
									>
										[<c:out value='${deptCode.code }'/>] <c:out value='${deptCode.name }'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- MANUFACTURE -->
						<td class="tdwhiteL" >
							<select name="manufacture_<c:out value='${list.number }' />" id="manufacture_<c:out value='${list.number }' />">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${manufacture }" var="manufacture">
									<option value="<c:out value='${manufacture.code }'/>" title=<c:out value="${manufacture.oid }"/>
										<c:if test="${list.manufacture eq manufacture.code }">
											selected
										</c:if>
									>
										[<c:out value='${manufacture.code }'/>] <c:out value='${manufacture.name }'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- 후처리 -->
						<td class="tdwhiteL" >
							<select name="finish_<c:out value='${list.number }' />" id="finish_<c:out value='${list.number }' />">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${finish }" var="finish">
									<option value="<c:out value='${finish.code }'/>" title=<c:out value="${finish.oid }"/>
										<c:if test="${list.finish eq finish.code }">
											selected
										</c:if>
									>
										[<c:out value='${finish.code }'/>] <c:out value='${finish.name }'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- 무게 -->
						<td class="tdwhiteL" >
							<input type="text" name="weight_<c:out value='${list.number }' />" id="weight_<c:out value='${list.number }' />" 
							value="<c:out value='${list.weight }'/>" class="txt_field" style="width: 95%"/>
						</td>
					</tr>
					
					<tr>
						<!-- 제작방법 -->
						<td class="tdwhiteL" >
							<select name="productmethod_<c:out value='${list.number }' />" id="productmethod_<c:out value='${list.number }' />">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${productmethod }" var="productmethod">
									<option value="<c:out value='${productmethod.code }'/>" title=<c:out value="${productmethod.oid }"/>
										<c:if test="${list.productmethod eq productmethod.code }">
											selected
										</c:if>
									>
										[<c:out value='${productmethod.code }'/>] <c:out value='${productmethod.name }'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- 단위 -->
						<td class="tdwhiteL" >
							<select name="unit_<c:out value='${list.number }' />" id="unit_<c:out value='${list.number }' />">
								<c:forEach items="${unit }" var="unit">
									<option value="<c:out value='${unit}'/>" title=<c:out value="${unit}"/>
										<c:if test="${list.unit eq unit }">
											selected
										</c:if>
									>
										<c:out value='${unit}'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- 재질 -->
						<td class="tdwhiteL" >
							<select name="mat_<c:out value='${list.number }' />" id="mat_<c:out value='${list.number }' />">
								<option value=''> ${f:getMessage('선택')} </option>
								<c:forEach items="${mat }" var="mat">
									<option value="<c:out value='${mat.code }'/>" title=<c:out value="${mat.oid }"/>
										<c:if test="${list.mat eq mat.code }">
											selected
										</c:if>
									>
										[<c:out value='${mat.code }'/>] <c:out value='${mat.name }'/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<!-- 비고 -->
						<td class="tdwhiteL" >
							<input type="text" name="remarks_<c:out value='${list.number }' />" id="remarks_<c:out value='${list.number }' />" class="txt_field" 
							value="<c:out value='${list.remark }'/>" class="txt_field" style="width: 95%" />
						</td>
						
						<!-- 사양 -->
						<td class="tdwhiteL" >
							<input type="text" name="specification_<c:out value='${list.number }' />" id="specification_<c:out value='${list.number }' />" class="txt_field" 
							value="<c:out value='${list.specification }'/>" class="txt_field" style="width: 95%" />
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