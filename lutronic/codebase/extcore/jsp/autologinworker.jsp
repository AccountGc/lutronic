<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.rmi.server.RemoteServer"%>
<%@page session="false"
%><%@page contentType="text/html" pageEncoding="UTF-8"

%><%@page import="wt.login.loginResource"

%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util"

%>
<%
wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
	out.println("<br>세션 확인 ::: "+(methodServer.getUserName() == null));
	HttpSession session = request.getSession(false);
	int maxTime = session.getMaxInactiveInterval();
	long createTimeL = session.getCreationTime();
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
       String creationTimeString = dateFormat.format(new Date(createTimeL));
       out.println("<br>creationTimeString ::: "+creationTimeString);
       // 서버로 클라이언트가 마지막으로 요청한 시간
       long accessedTime = session.getLastAccessedTime();
       
       String accessedTimeString = dateFormat.format(new Date(accessedTime));
       out.println("<br>accessedTimeString ::: "+accessedTimeString);
	out.println("<br>세션 남은시간 ::: "+(maxTime)+"분");
if (methodServer.getUserName() == null)
{
	out.println("<br>세션 셋팅 ::: "+(methodServer.getUserName() == null));
    methodServer.setUserName("wcadmin");
    methodServer.setPassword("lutadmin321!");
}
out.println("<br>세션 유저 네임 ::: "+(methodServer.getUserName()));

%>

<script type="text/javascript">
var a = 1;
function pagestart() {
	//25분 한번씩
	//window.setTimeout("pagereload()", 60000*60*12);
	window.setInterval("pagereload()",60000*60);
}
function pagereload() {
	location.reload();
}

</script>
<body onLoad="pagestart()">
