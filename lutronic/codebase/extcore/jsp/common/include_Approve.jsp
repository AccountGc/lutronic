<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<style>
#approveTitle{border:1px solid #D2D2D2;border-bottom:3px solid #244b9c;cursor:default;width:400px;height:30px;line-height:30px;vertical-align:middle;background-color:white;}
#approveTitle span{color:black;font-family:맑은 고딕;font-weight:bold;}
#approveTitle .moreSpan{float:right;cursor:pointer;height:30px;line-height:30px;vertical-align:middle;padding-right:10px;}
#approveList {border:1px solid #D2D2D2;width:400px;height:130px;margin-bottom:10px;}
#approveList li{font-family:맑은 고딕;font-size:12px;padding-bottom:5px;}
#approveList li div {margin: 0px; padding: 0px; width: 250px; float: left; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
#approveList li span{float:right;padding-right:10px;font-family:맑은 고딕;font-size:12px;}
</style>

<div id="approveTitle">
	<span style="margin-left:10px;">
		<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
		${f:getMessage('결재')}&${f:getMessage('작업')}
	</span>
	<span class="moreSpan" onclick="JavaScript:gotoMoreMenu(2);">
		MORE
	</span>
</div>

<div id="approveList">
	<ul>
		<c:forEach items="${list }" var="list">
			<li>
				<div title="<c:out value="${list.title }"/>">
					<a href="javascript:gotoWorkItem('<c:out value="${list.url }" />','<c:out value="${list.oid }" />','<c:out value="${list.viewOid }" />')">
						<c:out value="${list.title }"/>
					</a>
				</div>
				<span>
					<c:out value="${list.date }"/>
				</span>
			</li>
		</c:forEach>
	</ul>
</div>