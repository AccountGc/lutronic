<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="com.e3ps.common.query.SearchUtil"%>
<%@page import="com.e3ps.change.beans.ECOData"%>
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
<%@page import="com.e3ps.part.dto.PartData"%>
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
<%@page import="com.e3ps.org.dto.PeopleDTO"%>
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
<%!
public QuerySpec getECOQuery(HttpServletRequest req,JspWriter out) throws Exception{
	QuerySpec qs = null; 
	try{
		String name = StringUtil.checkNull(req.getParameter("name"));
		String number = StringUtil.checkNull(req.getParameter("number"));
		String eoType = "CHANGE";
		
		String predate = StringUtil.checkNull(req.getParameter("predate"));
		String postdate = StringUtil.checkNull(req.getParameter("postdate"));
		
		String creator = StringUtil.checkNull(req.getParameter("creator"));
		String state = "APPROVED";
		
		String licensing = StringUtil.checkNull(req.getParameter("licensing"));
		
		String[] models = req.getParameterValues("model");
		
		String sortCheck =StringUtil.checkNull(req.getParameter("sortCheck"));
		String sortValue =StringUtil.checkNull(req.getParameter("sortValue"));
		
		String riskType = StringUtil.checkNull(req.getParameter("riskType"));
		String preApproveDate = StringUtil.checkNull(req.getParameter("preApproveDate"));
		String postApproveDate = StringUtil.checkNull(req.getParameter("postApproveDate"));
		
		out.println("<br>Query licensing ====="+licensing);
		out.println("<br>Query riskType ====="+riskType);
		
		qs = new QuerySpec(); 
		Class ecoClass = EChangeOrder.class;
		int ecoIdx = qs.appendClassList(ecoClass, true);
		//제목
		if(name.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
		}
		//번호
		if(number.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
		}
		
		
		//등록일
		if(predate.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}//>=
			qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
		}
		//등록일
		if(postdate.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}//<=
			qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
		}
		//상태
		if(state.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
		}//creator.key.id
		
		//등록자
		if(creator.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			People pp = (People)CommonUtil.getObject(creator);
			long longOid = CommonUtil.getOIDLongValue(pp.getUser());
			qs.appendWhere(new SearchCondition(ecoClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
		}
		
		
		//ECO 구분
		if(eoType.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
		}else{
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_DEV, false), new int[] {ecoIdx});
			
			qs.appendOr();
			
			qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_PRODUCT, false), new int[] {ecoIdx});
			qs.appendCloseParen();
		}
		
		//인허가 구분
		if(licensing.length()>0){
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			if(licensing.equals("NONE")){
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
			}else{
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.EQUAL, licensing, false), new int[] {ecoIdx});
			}
			
		}
		
		//인허가 구분
		if(riskType.length()>0){
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			if(riskType.equals("NONE")){
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
			}else{
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.EQUAL, riskType, false), new int[] {ecoIdx});
			}
			
		}
		
		//승인일
		if(preApproveDate.length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}//>=
			qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.GREATER_THAN_OR_EQUAL ,preApproveDate), new int[] {ecoIdx});
		}
		//승인일
		if(postApproveDate .length() > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}//<=
			qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
		}
		
		
		
		//제품명
		if(models != null){
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendOpenParen();
				for(int i = 0 ;i < models.length ;i++){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.MODEL, SearchCondition.LIKE, "%"+models[i]+"%", false), new int[] {ecoIdx});
					if(i== models.length-1) break;
					qs.appendOr();
					
				}
			qs.appendCloseParen();
		}
		
		String[] completeParts = req.getParameterValues("completeParts");
		if(completeParts != null && completeParts.length > 0) {
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			
			int idx_partLink = qs.appendClassList(EOCompletePartLink.class, false);
			qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.theObjectIdentifier.id", EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"), new int[] {ecoIdx, idx_partLink});

			qs.appendAnd();
			qs.appendOpenParen();
			for(int i=0; i < completeParts.length; i++) {
				
				if(i != 0) {
					qs.appendOr();
				}
				
				String completePart = completeParts[i];
				WTPart part = (WTPart)CommonUtil.getObject(completePart);
				qs.appendWhere(new SearchCondition(EOCompletePartLink.class, EOCompletePartLink.ROLE_AOBJECT_REF+".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part.getMaster())),new int[] {idx_partLink});
				
			}
			
			qs.appendCloseParen();
		}
		
		if(sortValue != null && sortValue.length() > 0) {
			out.println("<br>sortCheck="+sortCheck+"\tsortValue="+sortValue);
			if("true".equals(sortCheck)){
				
				if( !"creator.key.id".equals(sortValue)){
					if(!"PROCESSDATE".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), true), new int[] { ecoIdx });
					}
				}else{
					
					if(qs.getConditionCount() > 0) qs.appendAnd();
					int idx_user = qs.appendClassList(WTUser.class, false);
					int idx_people = qs.appendClassList(People.class, false);
					
		            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
		            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
					qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
					SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
				}
				
			}else{
				
				if( !"creator.key.id".equals(sortValue)){
					if(!"PROCESSDATE".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), false), new int[] { ecoIdx });
					}
				}else{
					
					if(qs.getConditionCount() > 0) qs.appendAnd();
					int idx_user = qs.appendClassList(WTUser.class, false);
					int idx_people = qs.appendClassList(People.class, false);
					
		            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
		            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
					qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
					SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
				}
			}
		}else{
			qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.EO_APPROVE_DATE), true), new int[] { ecoIdx }); 
			qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.CREATE_TIMESTAMP), true), new int[] { ecoIdx });
		}
		 
		//System.out.println(qs);
	}catch(Exception e){
		e.printStackTrace();
	}

return qs;
}


public List<Map<String,Object>> listAUIECOAction(HttpServletRequest request, HttpServletResponse response,JspWriter out) throws Exception {
	long beforeTime = System.currentTimeMillis();
	QuerySpec query = getECOQuery(request,out);
	out.println("<br> query ::: "+query);
	long secDiffTime = (System.currentTimeMillis() - beforeTime)/1000;
	out.println("<br>query 시간차이(s) : "+secDiffTime);
	QueryResult qr = PersistenceHelper.manager.find(query);
	out.println("<br> qr ::: "+qr.size());
	secDiffTime = (System.currentTimeMillis() - beforeTime)/1000;
	out.println("<br>query run 시간차이(s) : "+secDiffTime);
	List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
	
	
	while(qr.hasMoreElements()){	
		Object[] o = (Object[]) qr.nextElement();
		EChangeOrder eco = (EChangeOrder) o[0];
		String ecoOid = CommonUtil.getOIDString(eco);
		long beforeTime2 = System.currentTimeMillis();
		ECOData ecoData = new ECOData(eco);
		long secDiffTime2 = (System.currentTimeMillis() - beforeTime2)/1000;
		secDiffTime = (System.currentTimeMillis() - beforeTime)/1000;
		out.println("<br>ECOData run 시간차이(s) : "+secDiffTime);
		out.println("<br>ECOData run real 시간차이(s) : "+secDiffTime2);
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("oid", ecoData.oid);
		result.put("number", ecoData.number);
		result.put("name", ecoData.name);
		result.put("state", ecoData.getLifecycle());
		
		result.put("creator", ecoData.creator);
		result.put("eoApproveDate", ecoData.eoApproveDate);
		
		result.put("licensingDisplay", ecoData.getlicensingDisplay(false));
		result.put("riskTypeName", ChangeUtil.getRiskTypeName(ecoData.riskType, false));
		result.put("createDate", ecoData.dateSubString(true));
		resultList.add(result);
	}
	secDiffTime = (System.currentTimeMillis() - beforeTime)/1000;
	out.println("<br>whiele run 시간차이(s) : "+secDiffTime);
	
	
	return resultList;
}
%>

<%
	listAUIECOAction(request, response, out);
%>