<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>


<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;"-->
    <table width="100%" cellspacing="0" cellpadding="1" border="0" id="partTable" align="center">
        <tbody>
            <tr>
            	<td class="tdblueM" width="5%">${f:getMessage('번호')}</td>
                <td class="tdblueM" width="25%">${f:getMessage('품목번호')}</td>
                <td class="tdblueM" width="30%">${f:getMessage('품명')}</td>
                <td class="tdblueM" width="5%">Rev.</td>
                <td class="tdblueM" width="5%">${f:getMessage('등록자')}</td>
                <td class="tdblueM" width="20%">${f:getMessage('적용구분')}<span style="color:red;">*</span></td>
               
                <td class="tdblueM" width="10%">${f:getMessage('S/N 관리')}<span style="color:red;">*</span></td>
            </tr>
            
            <c:forEach items="${list }" var="list" varStatus="i">
            	<tr>
	            	<td class="tdwhiteM">
	            		${i.index+1 }
	            		<input type=hidden name=linkOid value="<c:out value="${list.linkOid }"/>">
	            	</td>
	            	<td class="tdwhiteM">
	            		<input type="hidden" name="partNumber" value="<c:out value="${list.partNumber }"/>">
	            		<c:out value="${list.partNumber }"/>
	            	</td>
	            	
	            	<td class="tdwhiteM">
	            		<c:out value="${list.partName }"/>
	            	</td>
	            	
	            	<td class="tdwhiteM">
	            		<c:out value="${list.partVersion }"/>
	            	</td>
	            	<td class="tdwhiteM" ><c:out value="${partData.userCreatorName }" /></td>
	            	<td class="tdwhiteM" >
	            		
	            		<c:forEach items="${apply }" var="apply">
	            			<input type="radio" name="apply_<c:out value="${list.partNumber }"/>" value="<c:out value="${apply.code }"/>" 
	            				<c:if test="${list.apply eq apply.code }"> checked </c:if>
	            			><c:out value="${apply.name }"/>
	            		</c:forEach>
	            	
	            	</td>
	            	
	                <td class="tdwhiteM">
		                <input type="radio" name="serial_<c:out value="${list.partNumber }"/>" value="Y" <c:if test="${list.serial eq 'Y' }"> checked </c:if> >Y 
		                <input type="radio" name="serial_<c:out value="${list.partNumber }"/>" value="N" <c:if test="${list.serial eq 'N' }"> checked </c:if> >N
	                </td>
	                
	            </tr>
            
            </c:forEach>
            
        </tbody>
    </table>
</div>

</body>
</html>