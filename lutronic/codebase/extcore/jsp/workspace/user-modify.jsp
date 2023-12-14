<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.org.dto.PeopleDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PeopleDTO dto = (PeopleDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
HashMap<String, String> hash = dto.getSignature();
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getPoid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">이름</th>
		<td class="indent5"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th class="lb">아이디</th>
		<td class="indent5"><%=dto.getId()%></td>
	</tr>
	<tr>
		<th class="lb">권한</th>
		<td class="indent5"><%=dto.getAuth()%></td>
	</tr>
	<tr>
		<th class="lb">부서</th>
		<td class="indent5"><%=dto.getDepartment_name()%></td>
	</tr>
	<tr>
		<th class="lb">직위</th>
		<td class="indent5"><%=dto.getDuty()%></td>
	</tr>
	<tr>
		<th class="lb">이메일</th>
		<td class="indent5"><%=dto.getEmail()%></td>
	</tr>
	<tr>
		<th class="lb">퇴사여부</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="isFire" value="false" <%if(!dto.isFire()) { %> checked="checked" <%] %>>
				<div class="state p-success">
					<label>
						<b>재직</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="latest" value="true" <%if(dto.isFire()) { %> checked="checked" <%] %>>
				<div class="state p-success">
					<label>
						<b>퇴사</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<th class="req lb">서명</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const primary = document.querySelector("input[name=primary]");

		if (primary == null) {
			alert("주 첨부파일을 첨부해주세요.");
			return false;
		}
		const oid = document.getElementById("oid").value;
		const isFire = document.querySelector("input[name=isFire]:checked").value;
		const params = {
			primary : primary.value,
			oid : oid,
			isFire : JSON.parse(isFire)
		};

		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/org/modify");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				self.close();
			}
		})
	}
</script>
