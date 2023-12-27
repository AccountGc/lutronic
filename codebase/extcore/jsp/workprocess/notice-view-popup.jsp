<%@page import="com.e3ps.groupware.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
NoticeDTO data = (NoticeDTO) request.getAttribute("data");
%>

<input type="hidden" name="oid" id="oid" value="<%= data.getOid() %>" >

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항
			</div>
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="10%">
		<col width="40%">
		<col width="10%">
		<col width="40%">
	</colgroup>
	<tr>
			<th class="lb">제목</th>
			<td colspan="3" class="indent5"><%=data.getTitle()%></td>
		</tr>
		<tr>
			<th class="lb">등록자</th>
			<td class="indent5"><%=data.getCreator()%></td>
			<th>등록일</th>
			<td class="indent5">
				<%= data.getCreatedDate() %>
			</td>
		</tr>
		<tr>
			<th class="lb">조회수</th>
			<td class="indent5"><%=data.getCount()%></td>
			<th>팝업</th>
			<td class="indent5">
				<%
				//=data.isPopup()
				%>
			</td>
		</tr>
		<tr>
			<th class="lb">내용</th>
			<td colspan="3" class="indent5">
				<textarea rows="10" readonly="readonly"><%=data.getContents()%></textarea>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
					<jsp:param value="<%=data.getOid()%>" name="oid" />
				</jsp:include>
			</td>
		</tr>
</table>

<table class="button-table">
	<tr>
		<td align="left">
			<div style="display: flex; justify-content: flex-start; align-items: center;">
					<input type="checkbox" name="checkId" id="checkId" value="checkbox" onclick="setCookie();">
					<span>
						&nbsp;일주일 간 열지 않기
					</span>
			</div>
		</td>
		<td align="right"> 
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<script>
function setCookie(){
	const oid = "<%=data.getOid()%>";
	const cDay = 7;	
    let expire = new Date();
	
    expire.setDate(expire.getDate() + cDay);
    cookies = oid + '=' + escape(oid) + '; path=/ ';
    
    if(typeof cDay != 'undefined'){
    	cookies += ';expires=' + expire.toGMTString() + ';';
    }
    document.cookie = cookies;
    self.close();
}
</script>