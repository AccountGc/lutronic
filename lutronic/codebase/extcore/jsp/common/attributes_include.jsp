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
		<col width="500">
		<col width="130">
		<col width="500">
	</colgroup>
	<% if("doc".equals(module)){ %>
	<tr>
		<th class="lb">프로젝트코드</th>
		<td class="indent5"><%= request.getAttribute("model") == null ? "" : request.getAttribute("model")%></td>
		<th class="lb">보존기간</th>
		<td class="indent5"><%= request.getAttribute("preseration") == null ? "" : request.getAttribute("preseration")%></td>
	</tr>
	<tr>
		<th class="lb">내부 문서번호</th>
		<td class="indent5"><%= request.getAttribute("interalnumber") == null ? "" : request.getAttribute("interalnumber")%></td>
		<th class="lb">부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5" colspan="3"><%= creator%></td>
	</tr>
	<% }else if("mold".equals(module)){ %>
	<tr>
		<th class="lb">MANUFACTURER</th>
		<td class="indent5"><%= request.getAttribute("manufacture") == null ? "" : request.getAttribute("manufacture")%></td>
		<th class="lb">금형타입</th>
		<td class="indent5"><%= request.getAttribute("moldtype") == null ? "" : request.getAttribute("moldtype")%></td>
	</tr>
	<tr>
		<th class="lb">내부 문서번호</th>
		<td class="indent5"><%= request.getAttribute("interalnumber") == null ? "" : request.getAttribute("interalnumber")%></td>
		<th class="lb">부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
	</tr>
	<tr>
		<th class="lb">업체 금형번호</th>
		<td class="indent5"><%= request.getAttribute("moldnumber") == null ? "" : request.getAttribute("moldnumber")%></td>
		<th class="lb">금형개발비</th>
		<td class="indent5"><%= request.getAttribute("moldcost") == null ? "" : request.getAttribute("moldcost")%></td>
	</tr>
	<% }else{ %>
	<tr>
		<th class="lb">프로젝트코드</th>
		<td class="indent5"><%= request.getAttribute("model") == null ? "" : request.getAttribute("model")%></td>
		<th class="lb">제작방법</th>
		<td class="indent5"><%= request.getAttribute("productmethod") == null ? "" : request.getAttribute("productmethod")%></td>
	</tr>
	<tr>
		<th class="lb">부서</th>
		<td class="indent5"><%= request.getAttribute("deptcode") == null ? "" : request.getAttribute("deptcode")%></td>
		<th class="lb">단위</th>
		<td class="indent5"><%= request.getAttribute("unit") == null ? "" : request.getAttribute("unit")%></td>
	</tr>
	<tr>
		<th class="lb">무게(g)</th>
		<td class="indent5"><%= request.getAttribute("weight") == null ? "" : request.getAttribute("weight")%></td>
		<th class="lb">MANUFACTURER</th>
		<td class="indent5"><%= request.getAttribute("manufacture") == null ? "" : request.getAttribute("manufacture")%></td>
	</tr>
	<tr>
		<th class="lb">재질</th>
		<td class="indent5"><%= request.getAttribute("mat") == null ? "" : request.getAttribute("mat")%></td>
		<th class="lb">후처리</th>
		<td class="indent5"><%= request.getAttribute("finish") == null ? "" : request.getAttribute("finish")%></td>
	</tr>
	<tr>
		<th class="lb">OEM Info.</th>
		<td class="indent5"><%= request.getAttribute("remarks") == null ? "" : request.getAttribute("remarks")%></td>
		<th class="lb">사양</th>
		<td class="indent5"><%= request.getAttribute("specification") == null ? "" : request.getAttribute("specification")%></td>
	</tr>
	<tr>
		<th class="lb">EO No.</th>
		<td class="indent5"><%= request.getAttribute("ecoNo") == null ? "" : request.getAttribute("ecoNo")%></td>
		<th class="lb">EO Date</th>
		<td class="indent5"><%= request.getAttribute("ecoDate") == null ? "" : request.getAttribute("ecoDate")%></td>
	</tr>
	<tr>
		<th class="lb">검토자</th>
		<td class="indent5"><%= request.getAttribute("chk") == null ? "" : request.getAttribute("chk")%></td>
		<th class="lb">승인자</th>
		<td class="indent5"><%= request.getAttribute("apr") == null ? "" : request.getAttribute("apr")%></td>
	</tr>
	<tr>
		<th class="lb">Rev.</th>
		<td class="indent5"><%= request.getAttribute("rev") == null ? "" : request.getAttribute("rev")%></td>
		<th class="lb">DES</th>
		<td class="indent5"><%= request.getAttribute("des") == null ? "" : request.getAttribute("des")%></td>
	</tr>
	<tr>
		<th class="lb">ECO No.</th>
		<td class="indent5"><%= request.getAttribute("changeNo") == null ? "" : request.getAttribute("changeNo")%></td>
		<th class="lb">ECO Date</th>
		<td class="indent5"><%= request.getAttribute("changeDate") == null ? "" : request.getAttribute("changeDate")%></td>
	</tr>
	<% } %>
</table>