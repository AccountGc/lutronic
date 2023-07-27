<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%
String map = (String) request.getAttribute("map");
String module = (String) request.getAttribute("module");
DocumentData data = (DocumentData) request.getAttribute("docData");
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
		<td class="indent5"><%=data.getModel()%></td>
		<th>보존기간</th>
		<td class="indent5"><%=data.getPreseration()%></td>
	</tr>
	<tr>
		<th>내부 문서번호</th>
		<td class="indent5"><%= data.getInteralnumber()%></td>
		<th>부서</th>
		<td class="indent5"><%= data.getDeptcode()%></td>
	</tr>
	<tr>
		<th>작성자</th>
		<td class="indent5" colspan="3"><%= data.getCreator()%></td>
	</tr>
	<% }else if("mold".equals(module)){ %>
	<tr>
		<th>MANUFACTURER</th>
		<td class="indent5"><%= data.getManufacture()%></td>
		<th>금형타입</th>
		<td class="indent5"><%= data.getMoldtype()%></td>
	</tr>
	<tr>
		<th>내부 문서번호</th>
		<td class="indent5"><%= data.getInteralnumber()%></td>
		<th>부서</th>
		<td class="indent5"><%= data.getDeptcode()%></td>
	</tr>
	<tr>
		<th>업체 금형번호</th>
		<td class="indent5"><%= data.getMoldnumber()%></td>
		<th>금형개발비</th>
		<td class="indent5"><%= data.getMoldcost()%></td>
	</tr>
	<% }else{ %>
	<tr>
		<th>프로젝트코드</th>
		<td class="indent5"><%= data.getModel()%></td>
		<th>제작방법</th>
		<td class="indent5"><%= data.getProductmethod()%></td>
	</tr>
	<tr>
		<th>부서</th>
		<td class="indent5"><%= data.getDeptcode()%></td>
		<th>단위</th>
		<td class="indent5"><%= data.getUnit()%></td>
	</tr>
	<tr>
		<th>무게(g)</th>
		<td class="indent5"><%= data.getWeight()%></td>
		<th>MANUFACTURER</th>
		<td class="indent5"><%= data.getManufacture()%></td>
	</tr>
	<tr>
		<th>재질</th>
		<td class="indent5"><%= data.getMat()%></td>
		<th>후처리</th>
		<td class="indent5"><%= data.getFinish()%></td>
	</tr>
	<tr>
		<th>OEM Info.</th>
		<td class="indent5"><%= data.getRemarks()%></td>
		<th>사양</th>
		<td class="indent5"><%= data.getSpecification()%></td>
	</tr>
	<tr>
		<th>EO No.</th>
		<td class="indent5"><%= data.getEcoNo()%></td>
		<th>EO Date</th>
		<td class="indent5"><%= data.getEcoDate()%></td>
	</tr>
	<tr>
		<th>검토자</th>
		<td class="indent5"><%= data.getChk()%></td>
		<th>승인자</th>
		<td class="indent5"><%= data.getApr()%></td>
	</tr>
	<tr>
		<th>Rev.</th>
		<td class="indent5"><%= data.getRev()%></td>
		<th>DES</th>
		<td class="indent5"><%= data.getDes()%></td>
	</tr>
	<tr>
		<th>ECO No.</th>
		<td class="indent5"><%= data.getChangeNo()%></td>
		<th>ECO Date</th>
		<td class="indent5"><%= data.getChangeDate()%></td>
	</tr>
	<% } %>
</table>