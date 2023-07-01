<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<style>
#documentTitle{border:1px solid #D2D2D2;cursor:default;width:810px;height:30px;margin-left:15px;line-height:30px;vertical-align:middle;background-color:#244B9C;}
#documentTitle span{color:#FFFFFF;font-family:맑은 고딕;font-weight:bold;}
#documentTitle .moreSpan{float:right;cursor:pointer;height:30px;line-height:30px;vertical-align:middle;padding-right:10px;}
#documentList {border:1px solid #D2D2D2;width:810px;height:130px;margin-left:15px;margin-bottom:10px;}
#documentList li {padding-bottom:5px;}
#documentList li span{font-family:맑은 고딕;font-size:12px;float:left;}
#documentList li .dateSpan{float:right;padding-right:10px;font-family:맑은 고딕;font-size:12px;}
</style>
<div id="documentTitle">
<span style="margin-left:10px;">${f:getMessage('신규문서')}</span>
<span class="moreSpan" onclick="JavaScript:gotoMoreMenu(4);">MORE</span>
</div>
<div id="documentList">
	<ul>
		<c:forEach items="${list }" var="list">
		<li>
			<span style="width:30%;"><c:out value="${list.number }" /></span>
			<span style="width:40%;">
				<a href="javascript:openView('<c:out value="${list.oid }" />')">
					<c:out value="${list.name }" />
				</a>
			</span>
			<span style="width:20%;"><c:out value="${list.cretor }" /></span>
			<span class="dateSpan"><c:out value="${list.date }"/></span>
		</li>
		</c:forEach>
	</ul>
</div>
