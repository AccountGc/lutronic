<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<style>
#noticeTitle{border:1px solid #D2D2D2;border-bottom:3px solid #244b9c;cursor:default;width:400px;height:30px;margin-left:15px;line-height:30px;vertical-align:middle;background-color: white;}
#noticeTitle span{color:black;font-family:맑은 고딕;font-weight:bold;}
#noticeTitle .moreSpan{float:right;cursor:pointer;height:30px;line-height:30px;vertical-align:middle;padding-right:10px;}
#noticeList {border:1px solid #D2D2D2;width:400px;height:130px;margin-left:15px;margin-right:10px;margin-bottom:10px;}
#noticeList li{font-family:맑은 고딕;font-size:12px;padding-bottom:5px;}
#noticeList li div {margin: 0px; padding: 0px; width: 250px; float: left; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
#noticeList li span{float:right;padding-right:10px;font-family:맑은 고딕;font-size:12px;}
</style>


<div id="noticeTitle">
	<span style="margin-left:10px;">
		<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
		${f:getMessage('공지사항')}
	</span>
	<span class="moreSpan" onclick="JavaScript:gotoMoreMenu(1);">
		MORE
	</span>
</div>

<div id="noticeList">
	<ul>
		<c:forEach items="${list }" var="list">
			<li>
				<div title="<c:out value="${list.title }"/>">
					<a href="javascript:gotoNotice('<c:out value="${list.oid }" />')">
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