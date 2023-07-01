<%@page import="com.e3ps.common.history.LoginHistory"%>
<%@page import="java.util.Date"%>
<%@page import="wt.pom.Transaction"%>
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
<%-- <%!




public int getPartQuery(JspWriter out) throws WTException {
    MethodContext methodcontext = null;
    WTConnection wtconnection = null;

    PreparedStatement st = null;
    ResultSet rs = null;
    
    String dataStore = "Oracle";
    long startTime = System.currentTimeMillis();
    
    DateFormat df = new SimpleDateFormat("HH:mm:ss"); 
    String str = df.format(startTime);
    try {
	    out.println("<br>startTime"+str);

        methodcontext = MethodContext.getContext();
        wtconnection = (WTConnection) methodcontext.getConnection();
        Connection con = wtconnection.getConnection();

        StringBuffer sql = null;

       /*  try {
            dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
        }
        catch ( Exception ex ) {
            dataStore = "Oracle";
        } */
        int cnt = 0;
		String query = " SELECT A3.name,A3.wtpartnumber  FROM WTPart A0,ControlBranch A1,ControlBranch A2,WTPartMaster A3 WHERE (A0.latestiterationInfo = 1) " 
				+ " AND (A0.ida3masterreference = A3.ida2a2) "
              +  " AND (A0.branchIditerationInfo = A1.idA2A2) " 
               + " AND (A1.idA2A2 = A2.idA3A5(+)) " 
                + " AND ((A2.idA2A2 IS NULL ) OR (A2.wipState LIKE '%to wrk%')) " 
                + " AND (UPPER(A0.statecheckoutInfo) <> 'WRK') " 
                + " ORDER BY A0.modifyStampA2 DESC " ;
        
        
        //sql = new StringBuffer().append(query);
        
        
        out.println("<br>"+query);
        st = con.prepareStatement(query);
        //st.setLong(1, part.getPersistInfo().getObjectIdentifier().getId());

        rs = st.executeQuery();
        while ( rs.next() ) {
        String name =	rs.getString(1);
        String number=		rs.getString(2);
        out.println("<br>name = "+name + "/ number = " + number);
            cnt+= 1;
        }
        out.println("<br>cnt ="+cnt);
        long  lTime = System.currentTimeMillis();
        out.println("<br>time ="+(lTime-startTime)/1000);
        out.println("<br>lTime"+df.format(lTime));
        return 0;
        
    }
    catch ( Exception e ) {
        e.printStackTrace();
        throw new WTException(e);
    }
    finally {
        try {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
        } catch(Exception e) {
            throw new WTException(e);
        }
        if ( DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive() ) {
            MethodContext.getContext().freeConnection();
        }
    }
}
%>

<%
getPartQuery(out);
%> --%>

<%

	Transaction trx = new Transaction();
	try{
		trx.start();
		
		//id, �̸�, �μ�, �ֱ����ӽð�
		String userName = "노승현";
		String userId = "";
		String loginTime = "";
		
		
		//WTUser user = (WTUser)SessionHelper.getPrincipal();
		userName = "노승현";
		userId = "shnoh";
		
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		loginTime = dayTime.format(new Date(time));
		
		String dateTime = DateUtil.getToDay("yyyy-MM-dd hh:mm:ss");
		
		LoginHistory login = LoginHistory.newLoginHistory();
		
		login.setName(userName);
		login.setId(userId);
		login.setConTime(loginTime);
		out.println("Name : " + login.getName() + "\t Id : " + login.getId() + "\t ConTime : " + login.getConTime() + "<br/>");
		login = (LoginHistory)PersistenceHelper.manager.save(login);
		out.println("완");
		trx.commit();
       trx = null;
		
	}catch(Exception e){
		e.printStackTrace();
	} finally {
      if(trx!=null){
           trx.rollback();
      	}
	}


%>