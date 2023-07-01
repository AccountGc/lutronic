<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.org.beans.PeopleData"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.org.Department"%>
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

<%!

public List<Map<String,Object>> approveUserSearch(String userKey,JspWriter out) throws Exception {
	 long startTime = System.currentTimeMillis();
	    
	    DateFormat df = new SimpleDateFormat("HH:mm:ss"); 
	    String str = df.format(startTime);
		out.println("<br>startTime"+str);
	
	if(userKey==null)
		userKey = "";

		QuerySpec qs = new QuerySpec(People.class);
		int ii = qs.addClassList(People.class,true);
		int jj = qs.addClassList(WTUser.class,true);
		int kk = qs.addClassList(Department.class,true);
					
		qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});
		
		if (userKey != null && !userKey.equals("")) {
		    if (qs.getConditionCount() > 0)
		    	qs.appendAnd();

			qs.appendOpenParen();
		    qs.appendWhere(new SearchCondition(People.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { ii });
		    qs.appendOr();
		    qs.appendWhere(new SearchCondition(WTUser.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { jj });
		    qs.appendCloseParen();
		}
		qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class, "name"),false),new int[]{ii});
		//qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,"dutyCode"),false),new int[]{ii});

		//System.out.println("@@@ qs = " + qs.toString());
		
		QueryResult qr = PersistenceHelper.manager.find(qs);

		Object[] o = null;
		People people = null;
		Department dept1 = null;
		WTUser user = null;

		//request.setCharacterEncoding("utf-8");

		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		if( qr != null ){ 
			//System.out.println("@@@ people size = " + qr.size());
			while(qr.hasMoreElements()){
				o = (Object[])qr.nextElement();
				people = (People)o[0];
				user = (WTUser)o[1]; 
				dept1 = (Department)o[2]; 
				PeopleData data = new PeopleData(people);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("userOid", data.wtuserOID);
				map.put("peopleOid", data.peopleOID);
				//map.put("deptOid", data.department);
				map.put("id", data.id);
				map.put("name", data.name);
				map.put("deptName", data.departmentName);
				map.put("duty", data.duty);
				map.put("dutyCode", data.dutycode);
				map.put("email", data.email);
				
				/* map.put("userOid", user.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("peopleOid", people.getPersistInfo().getObjectIdentifier().getStringValue());
				//map.put("deptOid", data.department);
				map.put("id", user.getName());
				map.put("name", people.getName());
				map.put("deptName", dept1.getName());
				map.put("duty", people.getDuty());
				map.put("dutyCode", people.getDutyCode());
				map.put("email", user.getEMail()); */
				
				list.add(map);
			}
		}
		
		long  lTime = System.currentTimeMillis();
        out.println("<br>time ="+(lTime-startTime)/1000);
        out.println("<br>lTime"+df.format(lTime));
		
		return list;
}
%>

<% 
List<Map<String,Object>> res = approveUserSearch("",out);

for(int i = 0; i < res.size(); i ++ ){
	 out.println("<br>" + res.get(i).get("id") + " / " + res.get(i).get("deptName") + " / " + res.get(i).get("name") );
}
%>