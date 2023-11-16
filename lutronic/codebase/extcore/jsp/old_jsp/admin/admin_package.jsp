<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">

$(function() {
	$('#uploadFile').click(function() {
		if(confirm("${f:getMessage('업로드하시겠습니까?')}")){
			$("#admin_package").attr("target", "hiddenFrame");
			$("#admin_package").attr("action", getURLString("admin", "admin_packageAction", "do")).submit();
		}
	})
	$('#btnReset').click(function() {
		var str = navigator.userAgent.toLowerCase();
		if((str.indexOf("msie") > 0) || (str.indexOf("trident") > 0)){
		    $("#document").replaceWith( $("#document").clone(true) );
			$("#part").replaceWith( $("#part").clone(true) );
			$("#rohs").replaceWith( $("#rohs").clone(true) );
			$("#rohsLink").replaceWith( $("#rohsLink").clone(true) );
			$("#eco").replaceWith( $("#eco").clone(true) );
			$("#drawing").replaceWith( $("#eco").clone(true) );
		}else {
			$("#document").val("");
			$("#part").val("");
			$("#rohs").val("");
			$("#rohsLink").val("");
			$("#eco").val("");
			$("#drawing").val("");
		}
	})
})

</script>

<body>

<form name="admin_package" id="admin_package" method="post" enctype="multipart/form-data">

<iframe name='hiddenFrame' id='hiddenFrame' style='display: none;'></iframe>

<!--//여백 테이블-->
<table width="100%" border="0" cellpadding="0" cellspacing="20" > 
    <tr align=center height=5>       
        <td>
            <table width="100%" border="0" cellpadding="0" cellspacing="3" >
                <tr  align=center height=5>
                    <td valign="top" style="padding:0px 0px 0px 0px" height=3 >

						<!-- 상단 제목 -->
						<div style="overflow: auto;">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="Sub_TBG" >
										<div id="mainTitle">${f:getMessage('등록양식관리')}</div>
									</td>
									<td class="Sub_TRight"></td>
								</tr>
							</table>
						</div>
						
	                 </td>
	             </tr>
             
	             <tr align=center height=5>
	                 <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
	
						<!-- 파란 라인 -->
	                 	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr><td height=1 width=100%></td></tr>
						</table>
	                 	
	                 	<!-- input 테이블 -->
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
							<col width='10%'>
							<col width='40%'>
							<col width='10%'>
							<col width='40%'>
							
						    <tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('문서')}</td>  
						                                               
						        <td class="tdwhiteL">
						        	<input type=file name=document id=document style='width: 95%'>
								</td>
	           				</tr>
	           				
	           				<tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('제품')}</td>  
						                                               
						        <td class="tdwhiteL">
						        	<input type=file name=part id=part style='width: 95%'>
								</td>
	           				</tr>
	           				
	           				<tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('도면')}</td>  
						                                               
						        <td class="tdwhiteL">
						        	<input type=file name=drawing id=drawing style='width: 95%'>
								</td>
	           				</tr>
	           				
	           				<tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('물질')}</td>  
						                                               
						        <td class="tdwhiteL">
						        
						        	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
										<col width='10%'>
										<col width='40%'>
										
										<tr bgcolor="ffffff" height="35">
									    	<td class="tdblueM">
									    		${f:getMessage('일괄등록')}
									    	</td>
									    	<td class="tdwhiteL">
									    		<input type=file name=rohs id=rohs style='width: 95%'>
									    	</td>
								        </tr>
								        
								        <tr bgcolor="ffffff" height="35">
									        <td class="tdblueM">
									        	${f:getMessage('링크등록')}
									        </td>
									        <td class="tdwhiteL">
									        	<input type=file name=rohsLink id=rohsLink style='width: 95%'>
									    	</td>
								        </tr>
						        	
						        	</table>
						        
						        	
								</td>
	           				</tr>
	           				
	           				<tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('설계변경 부품 내역파일')}</td>  
						                                               
						        <td class="tdwhiteL">
						        	<input type=file name=eco id=eco style='width: 95%'>
								</td>
	           				</tr>
	           				
	        			</table>
	        
	   				 </td>
				</tr>
	       		
	       		<tr  align=center height=5>
				    <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
	
						<!-- 등록과 초기화 버튼 -->
		     			<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
	    						<td>
	    							<button type="button" id="uploadFile" class="btnCRUD">
	    								<span></span>
	    								${f:getMessage('등록')}
	    							</button>
	    						</td>
	    						
								<td>
									<button title="Reset" class="btnCustom" type="reset" id="btnReset">
				                 	 	<span></span>${f:getMessage('초기화')}
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

</form>

</body>
</html>