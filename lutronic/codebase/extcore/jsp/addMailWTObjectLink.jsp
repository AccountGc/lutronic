<%@page import="com.e3ps.common.mail.MailUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.org.MailUser"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.e3ps.org.MailWTobjectLink"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>


<%
//MailWTObjectLink send Mail
String ecoOid = "com.e3ps.change.EChangeOrder:192256335";
WTObject wtobject = (WTObject) CommonUtil.getObject(ecoOid);
Vector<MailWTobjectLink> vecLink = getMailUserLinkList(CommonUtil.getOIDString(wtobject));

out.println("vecLink size > " + vecLink.size() + "<br/>");
HashMap mailUserMap = new HashMap();
for(int i = 0 ; i <vecLink.size() ; i++){
	MailWTobjectLink link = vecLink.get(i);
			
	String userName = link.getUser().getName();
	String userMail = link.getUser().getEmail();
			
	mailUserMap.put(userMail, userName);
}

out.println("mailUserMap size > " + mailUserMap.size() + "<br/>");
if(mailUserMap.size()>0){
	MailUtil.manager.sendeOutSideMail(wtobject, mailUserMap);
	out.println("메일전송 완료 ! ");
}
%>

<%-- <%
//addMailWTObjectLink
Vector<MailWTobjectLink> vec = new Vector<MailWTobjectLink>();
String ecoOid = "com.e3ps.change.EChangeOrder:192256335";
String[] userOids = {
		"com.e3ps.org.MailUser:174585396",
		"com.e3ps.org.MailUser:73049729",
		"com.e3ps.org.MailUser:73049733",
		"com.e3ps.org.MailUser:73049735",
		"com.e3ps.org.MailUser:73049737",
		"com.e3ps.org.MailUser:73065096",
		"com.e3ps.org.MailUser:73065617",
		"com.e3ps.org.MailUser:196606835",
		"com.e3ps.org.MailUser:174585398",
		"com.e3ps.org.MailUser:73049739",
		"com.e3ps.org.MailUser:73065093",
		"com.e3ps.org.MailUser:73049741",
		"com.e3ps.org.MailUser:73065815",
		"com.e3ps.org.MailUser:147767943",
		"com.e3ps.org.MailUser:73065639",
		"com.e3ps.org.MailUser:73049757",
		"com.e3ps.org.MailUser:73049759",
		"com.e3ps.org.MailUser:146740459",
		"com.e3ps.org.MailUser:147767945",
		"com.e3ps.org.MailUser:95227176",
		"com.e3ps.org.MailUser:73065609",
		"com.e3ps.org.MailUser:73049734",
		"com.e3ps.org.MailUser:73065613",
		"com.e3ps.org.MailUser:73065611",
		"com.e3ps.org.MailUser:147767951",
		"com.e3ps.org.MailUser:73049781",
		"com.e3ps.org.MailUser:73065607",
		"com.e3ps.org.MailUser:179709932",
		"com.e3ps.org.MailUser:73065818",
		"com.e3ps.org.MailUser:73049745",
		"com.e3ps.org.MailUser:73045478",
		"com.e3ps.org.MailUser:73049763",
		"com.e3ps.org.MailUser:73065625",
		"com.e3ps.org.MailUser:73049765",
		"com.e3ps.org.MailUser:140878760",
		"com.e3ps.org.MailUser:73049767",
		"com.e3ps.org.MailUser:73049738",
		"com.e3ps.org.MailUser:73049769",
		"com.e3ps.org.MailUser:73065627",
		"com.e3ps.org.MailUser:95227180",
		"com.e3ps.org.MailUser:73065601",
		"com.e3ps.org.MailUser:73065629",
		"com.e3ps.org.MailUser:73049773",
		"com.e3ps.org.MailUser:73049775",
		"com.e3ps.org.MailUser:73065633",
		"com.e3ps.org.MailUser:73065099",
		"com.e3ps.org.MailUser:73049777",
		"com.e3ps.org.MailUser:73049779",
		"com.e3ps.org.MailUser:73049736",
		"com.e3ps.org.MailUser:188162228",
		"com.e3ps.org.MailUser:73065635",
		"com.e3ps.org.MailUser:73049783",
		"com.e3ps.org.MailUser:73065637",
		"com.e3ps.org.MailUser:73065605"
};


/* WorkItem workItem = (WorkItem)CommonUtil.getObject(workOid);
WTObject obj = (WTObject)workItem.getPrimaryBusinessObject().getObject(); */
WTObject obj = (WTObject)CommonUtil.getObject(ecoOid);


/*기존 외부 유저 중복 체크*/
Vector<MailWTobjectLink> veclink = getMailUserLinkList(CommonUtil.getOIDString(obj));

out.println("기존 유저 링크 개수 >>> " + veclink.size() + "<br/>");
Vector vecUser = new Vector();
for(int i = 0 ; i < veclink.size() ; i++){
	MailWTobjectLink link = veclink.get(i);
	vecUser.add(CommonUtil.getOIDString(link.getUser()));
}

List<MailUser> userList = new ArrayList<>();
for(int i = 0 ; i < userOids.length ;i++){
	
	MailUser user = (MailUser)CommonUtil.getObject(userOids[i]);
	if(vecUser.contains(userOids[i])) continue;
	MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink(obj, user);
	PersistenceHelper.manager.save(link);
	vec.add(link);
	userList.add(user);
	
}
out.println("추가할 userList size >>> " + userList.size());
%> --%>

<%!
public Vector<MailWTobjectLink> getMailUserLinkList(String oid) {

	Vector<MailWTobjectLink> veclink = new Vector<MailWTobjectLink>();
	try {

		WTObject objTemp = null;
		WTObject obj = (WTObject) CommonUtil.getObject(oid);

		if (obj instanceof WorkItem) {
			WorkItem workItem = (WorkItem) obj;
			objTemp = (WTObject) workItem.getPrimaryBusinessObject()
					.getObject();

		} else {
			objTemp = obj;
		}

		QueryResult rt = PersistenceHelper.navigate(objTemp, "user",
				MailWTobjectLink.class, false);

		while (rt.hasMoreElements()) {
			MailWTobjectLink link = (MailWTobjectLink) rt.nextElement();
			veclink.add(link);
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	return veclink;
}

%>