<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%-- <%@page import="e3ps.project.dto.ProjectDTO"%> --%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentData data = (DocumentData) request.getAttribute("docData");
String oid = (String) request.getAttribute("oid");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%= oid %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<% if(data.getDocumentType().equals("금형문서")){ %>
					금형 상세보기
				<% }else{ %>
					문서 상세보기
				<% } %>
			</div>
		</td>
		<td class="right">
<%-- 			<% --%>
// 			if(data.isLatest()){
<%-- 			%> --%>
<%-- 				<% --%>
// 				if(data.isState("APPROVED")){
<%-- 				%> --%>
<!-- 				<input type="button" value="개정" title="개정" class="blue" onclick="update('revise');"> -->
<%-- 				<% --%>
// 				}
<%-- 				%> --%>
<%-- 				<% --%>
// 				if(data.isWithDraw()){
<%-- 				%> --%>
<!-- 				<input type="button" value="결재회수" title="결재회수" onclick=""> -->
<%-- 				<% --%>
// 				}
<%-- 				%> --%>
<%-- 				<% --%>
// 				if(data.isState("INWORK") || data.isState("BATCHAPPROVAL") || data.isState("REWORK")){
<%-- 				%> --%>
<!-- 				<input type="button" value="수정" title="수정" onclick="update('modify');"> -->
<%-- 				<% --%>
// 				}
<%-- 				%> --%>
<!-- 				<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();"> -->
<%-- 			<% --%>
// 			}
<%-- 			%> --%>
<%-- 			<% --%>
// 			if(!data.isLatest()){
<%-- 			%> --%>
<!-- 			<input type="button" value="최신Rev." title="최신Rev." onclick=""> -->
<%-- 			<% --%>
// 			}
<%-- 			%> --%>
			<input type="button" value="Rev.이력" title="Rev.이력" onclick="">
			<input type="button" value="다운로드이력" title="다운로드이력" onclick="">
			<input type="button" value="결재이력" title="결재이력" onclick="">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
<!-- 	<ul> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-1">개발업무 상세보기</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-2">구성원</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-3">TASK</a> -->
<!-- 		</li> -->
<!-- 	</ul> -->
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<% if(data.getDocumentType().equals("금형문서")){ %>
				<th class="lb">금형번호</th>
				<% }else{ %>
				<th class="lb">문서번호</th>
				<% } %>
				<td class="indent5"><%=data.getNumber() %></td>
				<% if(data.getDocumentType().equals("금형문서")){ %>
				<th class="lb">금형분류</th>
				<% }else{ %>
				<th class="lb">문서분류</th>
				<% } %>
				<td class="indent5">
<%-- 					<%= data.getLocation() %> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5">
<%-- 					<%= data.getLifecycle() %> --%>
				</td>
				<th>Rev</th>
				<td class="indent5">
<%-- 					<%= data.getVersion() %> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=data.getCreator() %></td>
				<th>수정자</th>
				<td class="indent5">
<%-- 					<%= data.getModifier() %> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=data.getCreateDate() %></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=data.getModifyDate() %></td>
			</tr>
			<tr>
				<th class="lb">문서유형</th>
				<td class="indent5"><%=data.getDocumentType() %></td>
				<th class="lb">결재방식</th>
				<td class="indent5">
<%-- 					<%= data.getApprovalType() %> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly"><%=data.getDescription() != null ? data.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="3" class="indent5">
					<jsp:include page="/eSolution/content/includeAttachFileView">
						<jsp:param value="p" name="type"/>
						<jsp:param value="<%= data.getOid() %>" name="oid"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일
					<br>
					<input type="button" value="일괄 다운" title="일괄 다운"  onclick="">
				</th>
				<td colspan="3" class="indent5">
					<jsp:include page="/eSolution/content/includeAttachFileView">
						<jsp:param value="<%= data.getOid() %>" name="oid"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					속성
				</div>
			</td>
		</tr>
	</table>
	<div id="tabs-2">
		<% if(data.getDocumentType().equals("금형문서")){ %>
		<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="mold" name="module"/>
		</jsp:include>
		<% } else{ %>
		<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="doc" name="module"/>
		</jsp:include>
		<% } %>
	</div>
	
<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
<!-- 				<div class="header"> -->
<!-- 					<img src="/Windchill/extcore/images/header.png"> -->
<!-- 					관련 부품 -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- 	<div id="tabs-3"> -->
<%-- 		<jsp:include page="/eSolution/part/include_PartView.do" flush="false" > --%>
<%-- 			<jsp:param name="moduleType" value="doc"/> --%>
<%-- 			<jsp:param name="title" value='관련 품목'"/> --%>
<%-- 			<jsp:param value="<%=data.getOid()%>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->

<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
<!-- 				<div class="header"> -->
<!-- 					<img src="/Windchill/extcore/images/header.png"> -->
<!-- 					관련 개발업무 -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- 	<div id="tabs-4"> -->
<%-- 		<jsp:include page="/eSolution/part/include_PartView.do" flush="false" > --%>
<%-- 			<jsp:param name="moduleType" value="doc"/> --%>
<%-- 			<jsp:param name="title" value='관련 개발업무'"/> --%>
<%-- 			<jsp:param value="<%=data.getOid()%>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
<!-- 				<div class="header"> -->
<!-- 					<img src="/Windchill/extcore/images/header.png"> -->
<!-- 					관련 문서 -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- 	<div id="tabs-5"> -->
<%-- 		<jsp:include page="/eSolution/part/include_PartView.do" flush="false" > --%>
<%-- 			<jsp:param name="moduleType" value="doc"/> --%>
<%-- 			<jsp:param name="title" value='관련 문서'"/> --%>
<%-- 			<jsp:param value="<%=data.getOid()%>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
<!-- 				<div class="header"> -->
<!-- 					<img src="/Windchill/extcore/images/header.png"> -->
<!-- 					관련 ECO -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- 	<div id="tabs-6"> -->
<%-- 		<jsp:include page="/eSolution/part/include_PartView.do" flush="false" > --%>
<%-- 			<jsp:param name="moduleType" value="doc"/> --%>
<%-- 			<jsp:param name="title" value='관련 ECO'"/> --%>
<%-- 			<jsp:param value="<%=data.getOid()%>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
<!-- 	<table class="button-table"> -->
<!-- 		<tr> -->
<!-- 			<td class="left"> -->
<!-- 				<div class="header"> -->
<!-- 					<img src="/Windchill/extcore/images/header.png"> -->
<!-- 					관리자 속성 -->
<!-- 				</div> -->
<!-- 			</td> -->
<!-- 		</tr> -->
<!-- 	</table> -->
<!-- 	<div id="tabs-7"> -->
<%-- 		<jsp:include page="/eSolution/part/include_PartView.do" flush="false" > --%>
<%-- 			<jsp:param name="moduleType" value="doc"/> --%>
<%-- 			<jsp:param value="<%=data.getOid()%>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
</div>

<script type="text/javascript">
</script>