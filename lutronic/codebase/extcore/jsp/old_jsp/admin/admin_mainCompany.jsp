<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<table width="100%" border="0" cellpadding="0" cellspacing="20" > <!--//여백 테이블-->
	<tr align=center height=5>
		<td>
			<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td valign=top width=180px  background="/Windchill/portal/images/ds_sub.gif" bgcolor=ffffff >
						<!-- //메뉴// -->
						<jsp:include page="/eSolution/admin/admin_menuCompany.do" flush="true"/>				
					</td>
					<td valign=top>
						<!-- //바디// -->
						<jsp:include page="/eSolution/admin/admin_listCompany.do" flush="true"/>	
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>