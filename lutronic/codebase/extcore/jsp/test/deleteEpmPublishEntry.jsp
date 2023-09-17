<%@page import="com.ptc.wvs.server.publish.PublishJob"%>
<%@page import="wt.queue.MethodArgument"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.wvs.WVSLoggerHelper"%>
<%@page import="wt.queue.QueueEntry"%>
<%@page import="java.util.Enumeration"%>
<%@page import="wt.queue.ProcessingQueue"%>
<%@page import="wt.queue.QueueHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.util.WTException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.e3ps.common.db.DBConnectionManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.part.dto.PartData"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<%@page import="org.json.JSONObject"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>


<%
/*
대기중인 목록 중복 제거 로직
*/
List<String> exists = new ArrayList<String>();
ProcessingQueue queue = QueueHelper.manager.getQueue("PublisherQueueL");
Enumeration<QueueEntry> entrys = QueueHelper.manager.queueEntries(queue);
int count = 0;

while(entrys.hasMoreElements()){
	QueueEntry q = entrys.nextElement();
	
	Vector vector = q.getArgs();
	for(int i=0; i< vector.size(); i++){
		MethodArgument o = (MethodArgument)vector.get(i);
		PublishJob job = (PublishJob)o.getArg();
		String state = job.getJobStatus();
		String state2 = q.getDisplayString();
		String state3 = q.getType();
		String jobNumber = job.getTargetNumber();
		String jobVersion = job.getTargetVersion()+"."+job.getTargetIteration();
		String jobString = job.getRepName();
		String key = jobNumber + ":" + jobVersion+":"+state+":"+state2+":"+state3+":"+jobString;
		out.println(key+"<br>");
	}
}
out.println("총 " + count + " 개의 목록이 삭제 되었습니다.");
%>

