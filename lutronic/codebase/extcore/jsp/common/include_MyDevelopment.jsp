<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<style>
#developmentTitle{border:1px solid #D2D2D2;border-bottom:3px solid #244b9c;cursor:default;width:812px;height:30px;margin-left:15px;line-height:30px;vertical-align:middle;background-color: white;}
#developmentTitle span{color:black;font-family:맑은 고딕;font-weight:bold;}
#developmentTitle .moreSpan{float:right;cursor:pointer;height:30px;line-height:30px;vertical-align:middle;padding-right:10px;}
#developmentList {border:1px solid #D2D2D2;width:812px;height:260px;margin-left:15px;margin-bottom:10px;}
#developmentList li {padding-bottom:5px;}
#developmentList li div {margin: 0px; padding: 0px; width: 300px; float: left; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
#developmentList li span{font-family:맑은 고딕;font-size:12px;float:left;}
#developmentList li .dateSpan{float:right;padding-right:10px;font-family:맑은 고딕;font-size:12px;}
</style>

<div id="developmentTitle">
	<span style="margin-left:10px;">
		<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
		${f:getMessage('나의 개발업무')}
	</span>
	<span class="moreSpan" onclick="JavaScript:gotoMoreMenu(5);">
		MORE
	</span>
</div>

<div id="developmentList">
	<ul>
		<c:forEach items="${list }" var="list">
		<li>
			<div title="<c:out value="${list.masterName }"/>">
				<a href="javascript:openView('<c:out value="${list.masterOid }" />')">
					<c:out value="${list.masterName }" />
				</a>
			</div>
			
			<div title="<c:out value="${list.activityName }"/>">
				<a href="javascript:openView('<c:out value="${list.activityOid }" />')">
					<c:out value="${list.activityName }" />
				</a>
			</div>
			
			<span class="dateSpan">
				<c:out value="${list.activeDate }"/>
			</span>
		</li>
		</c:forEach>
	</ul>
</div>
