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
Enumeration<QueueEntry> entrys = QueueHelper.manager.queueEntries(queue,"READY");
int count = 0;
while(entrys.hasMoreElements()){
	QueueEntry q = entrys.nextElement();
	
	Vector vector = q.getArgs();
	for(int i=0; i< vector.size(); i++){
		MethodArgument o = (MethodArgument)vector.get(i);
		PublishJob job = (PublishJob)o.getArg();
		String jobNumber = job.getTargetNumber();
		String jobVersion = job.getTargetVersion();
		String key = jobNumber + ":" + jobVersion;
		if(exists.contains(key)){
			QueueHelper.manager.deleteEntry(queue, q);
			count ++;
			break;
		}else{
			exists.add(key);
		}
	}
}
out.println("총 " + count + " 개의 목록이 삭제 되었습니다.");
%>

