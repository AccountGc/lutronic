<%@page import="com.e3ps.common.mail.MailException"%>
<%@page import="com.e3ps.common.mail.SendMail"%>
<%@page import="com.e3ps.common.jdf.config.ConfigEx"%>
<%@page import="com.e3ps.common.jdf.config.ConfigExImpl"%>
<%@page import="com.e3ps.common.jdf.config.ConfigImpl"%>
<%@page import="com.e3ps.common.mail.MailUtil"%>
<%@page import="com.e3ps.common.mail.MailHtmlContentTemplate"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="wt.pom.DBProperties"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="wt.pom.WTConnection"%>
<%@page import="wt.method.MethodContext"%>
<%@page import="com.e3ps.doc.service.DocumentQueryHelper"%>
<%@page import="com.e3ps.common.web.PageControl"%>
<%@page import="com.e3ps.common.web.PageQueryBroker"%>
<%@page import="com.e3ps.part.service.PartQueryHelper"%>
<%@page import="wt.fc.PagingSessionHelper"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="com.e3ps.change.EChangeActivity"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="com.e3ps.drawing.service.EpmSearchHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.change.service.ChangeHelper"%>
<%@page import="wt.vc.baseline.ManagedBaseline"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@page import="com.e3ps.change.service.ChangeWfHelper"%>
<%@page import="wt.vc.views.ViewReference"%>
<%@page import="com.e3ps.common.message.Message"%>
<%@page import="wt.vc.wip.CheckoutLink"%>
<%@page import="wt.clients.vc.CheckInOutTaskLogic"%>
<%@page import="wt.vc.wip.Workable"%>
<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="wt.iba.value.StringValue"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.iba.value.IBAHolder"%>
<%@page import="com.e3ps.change.service.ChangeUtil"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="com.e3ps.org.beans.PeopleData"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.groupware.workprocess.service.WFItemHelper"%>
<%@page import="com.e3ps.part.util.PartUtil"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.epm.structure.EPMReferenceType"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SQLFunction"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFCell"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFRow"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFSheet"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.poi.openxml4j.opc.OPCPackage"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.build.BuildRule"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.util.WTException"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%


String subject = "";

subject =  "test description.";

String content = "";

HashMap<String, String> to = new HashMap<String, String>();
to.put("gmji@e3ps.com","지광민" );

HashMap<String, String> from = new HashMap<String, String>();
from.put("EMAIL", "pdm-admin@lutronic.com");
from.put("NAME", "PDM ADMIN");

Hashtable<String, String> hash = new Hashtable<String, String>();
hash.put("gubun", "gubun");
hash.put("workName", "workName");
hash.put("viewString", "viewString");
hash.put("DM", "DM");
hash.put("description", "description");

ResourceBundle bundle =ResourceBundle.getBundle("wt");

String url = bundle.getString("wt.rmi.server.hostname");
out.println("<br>url -> " + url);
hash.put("url", url);

MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();
content = template.htmlContent(hash,"development_notice.html");

Hashtable<String, Object> mailHash = new Hashtable<String, Object>();
mailHash.put("FROM", from);
mailHash.put("TO", to);
mailHash.put("SUBJECT", subject);
mailHash.put("CONTENT", content);

sendMail3(mailHash);
//MailUtil.sendMail2(mailHash);

out.println("<br/> test1 check");
%>

<%!
static final boolean VERBOSE = ConfigImpl.getInstance().getBoolean("develop.verbose", false); 
static final boolean enableMail = ConfigImpl.getInstance().getBoolean("e3ps.mail.enable", true);

/**
 * host, sendId, sendPass 설정하기...
 * @param hash
 * @return
 */
public static boolean sendMail3 (Hashtable hash) throws Exception{
	
	ConfigExImpl conf = ConfigEx.getInstance("eSolution");
	String host = conf.getString("mail.smtp.host");
	System.out.println("host >>> " + host);
	if(enableMail){
		HashMap to = (HashMap)hash.get("TO");
		HashMap from = (HashMap)hash.get("FROM");
		String subject = (String)hash.get("SUBJECT");
		String content = (String)hash.get("CONTENT");
		Vector attache = (Vector)hash.get("ATTACHE");

		try {
			SendMail mail = new SendMail() ;
			//WTUser from = (WTUser)SessionHelper.manager.getPrincipal();
			//System.out.println("Sender : " + from.getFullName() + "," + from.getEMail());
			mail.setFromMailAddress((String)from.get("EMAIL"), (String)from.get("NAME"));
			if (to != null && to.size() > 0) {
				Object[] objArr = to.keySet().toArray();
				String emails = "";
				String toname = "";
				for ( int i = 0 ; i < objArr.length ; i++ ) {
					emails = (String)objArr[i];
					toname = (String)to.get(emails);
//					System.out.println("To Mail :" + emails);
//					System.out.println("To name :" + toname);

					if( emails.indexOf("@") < 0 )
						continue;

					mail.setToMailAddress(emails, toname);
				}

			} else {
				throw new MailException("받는 사람 설정오류");
			}

			mail.setSubject(subject);
			String message = " Text 메일 메시지 내용 " ; 
			String htmlMessage = "<html><font color='red'> HTML 메일 메시지 내용</font></html>" ;
			// String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt"   } ;
			String[] fileNames = {   } ;

			if( content != null ) {
				mail.setHtmlAndFile(content,fileNames);
			} else {
				mail.setHtmlAndFile(htmlMessage,fileNames);
			}
			//mail.setHtml(htmlMessage);
			//mail.setText(message);  

			/**
			 * @Todo 개인 서버에서 주석처리함.
			 */
			mail.send();  // 메일 전송 

			return true;
		} catch (Exception e) {
			throw e;
			//return false;
		}
	}else{
		return false;
	}
}
%> 