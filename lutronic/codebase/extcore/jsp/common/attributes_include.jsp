<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%
String module = (String) request.getParameter("module");
String creator = (String) request.getParameter("creator");
%>
<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="*">
		<col width="130">
		<col width="*">
	</colgroup>
	<% if("doc".equals(module)){ %>
	<tr>
		<th>프로젝트코드</th>
		<td class="indent5"><%= request.getAttribute("model") == null ? "" : request.getAttribute("model")%></td>
		<th>보존기간</th>
		<td class="indent5"><%= request.getAttribute("preseration") == null ? "" : request.getAttribute("preseration")%></td>
	</tr>
	<tr>
		<th>내부 문서번호</th>
		<td class="indent5"><%= request.getAttribute("interalnumber") == null ? "" : request.getAttribute("interalnumber")%></td>
		<th>부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
	</tr>
	<tr>
		<th>작성자</th>
		<td class="indent5" colspan="3"><%= creator%></td>
	</tr>
	<% }else if("mold".equals(module)){ %>
	<tr>
		<th>MANUFACTURER</th>
		<td class="indent5"><%= request.getAttribute("manufacture") == null ? "" : request.getAttribute("manufacture")%></td>
		<th>금형타입</th>
		<td class="indent5"><%= request.getAttribute("moldtype") == null ? "" : request.getAttribute("moldtype")%></td>
	</tr>
	<tr>
		<th>내부 문서번호</th>
		<td class="indent5"><%= request.getAttribute("interalnumber") == null ? "" : request.getAttribute("interalnumber")%></td>
		<th>부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
	</tr>
	<tr>
		<th>업체 금형번호</th>
		<td class="indent5"><%= request.getAttribute("moldnumber") == null ? "" : request.getAttribute("moldnumber")%></td>
		<th>금형개발비</th>
		<td class="indent5"><%= request.getAttribute("moldcost") == null ? "" : request.getAttribute("moldcost")%></td>
	</tr>
	<% }else{ %>
	<tr>
		<th>프로젝트코드</th>
		<td class="indent5"><%= request.getAttribute("model") == null ? "" : request.getAttribute("model")%></td>
		<th>제작방법</th>
		<td class="indent5"><%= request.getAttribute("productmethod") == null ? "" : request.getAttribute("productmethod")%></td>
	</tr>
	<tr>
		<th>부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
		<th>단위</th>
		<td class="indent5"><%= request.getAttribute("unit") == null ? "" : request.getAttribute("unit")%></td>
	</tr>
	<tr>
		<th>무게(g)</th>
		<td class="indent5"><%= request.getAttribute("weight") == null ? "" : request.getAttribute("weight")%></td>
		<th>MANUFACTURER</th>
		<td class="indent5"><%= request.getAttribute("manufacture") == null ? "" : request.getAttribute("manufacture")%></td>
	</tr>
	<tr>
		<th>재질</th>
		<td class="indent5"><%= request.getAttribute("mat") == null ? "" : request.getAttribute("mat")%></td>
		<th>후처리</th>
		<td class="indent5"><%= request.getAttribute("finish") == null ? "" : request.getAttribute("finish")%></td>
	</tr>
	<tr>
		<th>OEM Info.</th>
		<td class="indent5"><%= request.getAttribute("remarks") == null ? "" : request.getAttribute("remarks")%></td>
		<th>사양</th>
		<td class="indent5"><%= request.getAttribute("specification") == null ? "" : request.getAttribute("specification")%></td>
	</tr>
	<tr>
		<th>EO No.</th>
		<td class="indent5"><%= request.getAttribute("ecoNo") == null ? "" : request.getAttribute("ecoNo")%></td>
		<th>EO Date</th>
		<td class="indent5"><%= request.getAttribute("ecoDate") == null ? "" : request.getAttribute("ecoDate")%></td>
	</tr>
	<tr>
		<th>검토자</th>
		<td class="indent5"><%= request.getAttribute("chk") == null ? "" : request.getAttribute("chk")%></td>
		<th>승인자</th>
		<td class="indent5"><%= request.getAttribute("apr") == null ? "" : request.getAttribute("apr")%></td>
	</tr>
	<tr>
		<th>Rev.</th>
		<td class="indent5"><%= request.getAttribute("rev") == null ? "" : request.getAttribute("rev")%></td>
		<th>DES</th>
		<td class="indent5"><%= request.getAttribute("des") == null ? "" : request.getAttribute("des")%></td>
	</tr>
	<tr>
		<th>ECO No.</th>
		<td class="indent5"><%= request.getAttribute("changeNo") == null ? "" : request.getAttribute("changeNo")%></td>
		<th>ECO Date</th>
		<td class="indent5"><%= request.getAttribute("changeDate") == null ? "" : request.getAttribute("changeDate")%></td>
	</tr>
	<% } %>
</table>