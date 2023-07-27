<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%
String map = (String) request.getAttribute("map");
String module = (String) request.getAttribute("module");
Map<String,String> data = (Map<String,String>) request.getAttribute("data");
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
				<td class="indent5"><%= data.get("model")  %></td>
				<th>보존기간</th>
				<td class="indent5"><%= data.get("preseration")  %></td>
			</tr>
			<tr>
				<th>내부 문서번호</th>
				<td class="indent5"><%= data.get("interalnumber")  %></td>
				<th>부서</th>
				<td class="indent5"><%= data.get("deptcode")  %></td>
			</tr>
			<tr>
				<th>작성자</th>
				<td class="indent5" colspan="3"><%= data.get("writer")  %></td>
			</tr>
			<% }else if("mold".equals(module)){ %>
			<tr>
				<th>MANUFACTURER</th>
				<td class="indent5"><%= data.get("manufacture")  %></td>
				<th>금형타입</th>
				<td class="indent5"><%= data.get("moldtype")  %></td>
			</tr>
			<tr>
				<th>내부 문서번호</th>
				<td class="indent5"><%= data.get("interalnumber")  %></td>
				<th>부서</th>
				<td class="indent5"><%= data.get("deptcode")  %></td>
			</tr>
			<tr>
				<th>업체 금형번호</th>
				<td class="indent5"><%= data.get("moldnumber")  %></td>
				<th>금형개발비</th>
				<td class="indent5"><%= data.get("moldcost")  %></td>
			</tr>
			<% }else{ %>
			<tr>
				<th>프로젝트코드</th>
				<td class="indent5"><%= data.get("model")  %></td>
				<th>제작방법</th>
				<td class="indent5"><%= data.get("productmethod")  %></td>
			</tr>
			<tr>
				<th>부서</th>
				<td class="indent5"><%= data.get("deptcode")  %></td>
				<th>단위</th>
				<td class="indent5"><%= data.get("unit")  %></td>
			</tr>
			<tr>
				<th>무게(g)</th>
				<td class="indent5"><%= data.get("weight")  %></td>
				<th>MANUFACTURER</th>
				<td class="indent5"><%= data.get("MANUFACTURER")  %></td>
			</tr>
			<tr>
				<th>재질</th>
				<td class="indent5"><%= data.get("mat")  %></td>
				<th>후처리</th>
				<td class="indent5"><%= data.get("finish")  %></td>
			</tr>
			<tr>
				<th>OEM Info.</th>
				<td class="indent5"><%= data.get("remarks")  %></td>
				<th>사양</th>
				<td class="indent5"><%= data.get("specification")  %></td>
			</tr>
			<tr>
				<th>EO No.</th>
				<td class="indent5"><%= data.get("ecoNo")  %></td>
				<th>EO Date</th>
				<td class="indent5"><%= data.get("ecoDate")  %></td>
			</tr>
			<tr>
				<th>검토자</th>
				<td class="indent5"><%= data.get("chk")  %></td>
				<th>승인자</th>
				<td class="indent5"><%= data.get("apr")  %></td>
			</tr>
			<tr>
				<th>Rev.</th>
				<td class="indent5"><%= data.get("rev")  %></td>
				<th>DES</th>
				<td class="indent5"><%= data.get("des")  %></td>
			</tr>
			<tr>
				<th>EO No.</th>
				<td class="indent5"><%= data.get("changeNo")  %></td>
				<th>EO Date</th>
				<td class="indent5"><%= data.get("changeDate")  %></td>
			</tr>
			<% } %>
		</table>