<%@page import="com.e3ps.common.iba.AttributeKey.IBAKey"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.iba.value.IBAHolder"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="com.e3ps.drawing.util.EpmPublishUtil"%>
<%@page import="com.e3ps.drawing.service.EpmSearchHelper"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="com.e3ps.part.util.PartUtil"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="org.apache.tools.ant.taskdefs.Basename"%>
<%@page import="com.e3ps.change.service.ChangeHelper"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.erp.service.ERPHelper"%>
<%@page import="com.e3ps.change.service.ECOHelper"%>
<%@page import="com.e3ps.change.service.ChangeUtil"%>
<%@page import="wt.fc.IdentityHelper"%>
<%@page import="wt.fc.Identified"%>
<%@page import="wt.doc.WTDocumentMasterIdentity"%>
<%@page import="com.e3ps.doc.service.DocumentQueryHelper"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.manager.RemoteServerManager"%>
<%@page import="java.net.URL"%>
<%@page import="wt.method.RemoteMethodServer"%>
<%@page import="com.e3ps.common.query.SearchUtil"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
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

<form name="mainForm" method="post">

<div id="tablehead"></div>
<table>
<tr>
<td style="vertical-align:top;">
<div>
	<div id="list" style="height:150px;width:100%;">
	<%
	try{
		String oid = "com.e3ps.change.EChangeOrder:91840548"; //com.e3ps.change.EChangeOrder:11492494,com.e3ps.change.EChangeOrder:11688754
		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
		//String partoid = "wt.part.WTPart:10639385";
		//List<Map<String,String>> list = ChangeHelper.service.getGroupingBaseline(partoid, "", "");
		String partOid = "";
		String baseName = "";
		String baseOid = "";
				
		/* for(Map map : list){
			partOid = (String)map.get("partOid");
			baseName= (String)map.get("baseName");
			baseOid= (String)map.get("baseOid");
		} */
		//  String eoType =eco.getEoType();
		//out.println("eoType = " +eoType);
		//out.println("partOid = " +partOid);
		//out.println("baseName = " +baseName);
		//out.println("baseOid = " +baseOid);
		//System.out.println("3.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber() +" ERP 전송");
		//ECOHelper.service.completeECOTEST(eco);
		ECOHelper.service.completeECO(eco);
		//ECOHelper.service.completePart(eco);
		//ERPHelper.service.sendERP(eco);
		//ECOHelper.service.createECOBaseline(eco);
		// QueryResult qr = ECOSearchHelper.service.ecoPartLink(eco);
		 
	
	}catch(Exception e){
		e.printStackTrace();
		out.print(e.getMessage());
	}
	%>
	</div>

</div>
</td>
</tr>
</table>
</form>
<%!
public void completePart(EChangeOrder eco) throws Exception{
	
	/*최종 결재자,검토자*/
	Map<String, String> map = ChangeUtil.approvedSetData(eco);
	
	System.out.println("[ChangeECOHelper] completePart = " + eco.getEoNumber());
	String eoType = eco.getEoType();
	List<WTPart>  partList = new ArrayList<WTPart>();
	
	partList = ECOSearchHelper.service.ecoPartReviseList(eco);
	
	//"APPROVED";state
	map.put("state", "APPROVED");
	map.put("eoType",eoType);
	for(WTPart part : partList){
		System.out.println("[ChangeECOHelper] completePart = " + part.getNumber());
		completePartStateChange(part,map);
	}
	
}
public void completePartStateChange(WTPart part,Map<String, String> map) throws Exception {
	
	
	Folder folder = null;
	String partState = part.getLifeCycleState().toString();
	String eoType = map.get("eoType");
	if(partState.equals("APPROVED")){
		return;
	}
	
	//EO인경우 하위 구조가 작업중이고 설계 변경중이면 승인 처리 하지 않음
	boolean isEO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT);
	if(isEO){
		boolean isNotECO = PartSearchHelper.service.isSelectEO(part);
		if(!isNotECO){
			return;
		}
	}
	
	PartData partData = new PartData(part);
	//Generic instance, 더미 파일은 승인 처리 하지 않음
	boolean isStateChange = !(partData.isGENERIC() || partData.isINSTANCE()) || !PartUtil.isChange(part.getNumber());
	/*Part State change*/
	
	String state = map.get("state");//"APPROVED";state
	
	if(isStateChange){
		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)part, State.toState(state));
	}
	
	
	EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
	//속성 전파
	
	approvedIBAChange(part, map);
	if(epm != null){
		/*3D EPM State Change*/
		if(isStateChange){
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm, State.toState(state));
		}
		
		//속성 전파
		approvedIBAChange(epm, map);
		/*2D EPM State Change */
		Vector<EPMReferenceLink> vec =EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
		for(int i = 0 ; i < vec.size() ; i++){
			EPMReferenceLink link = vec.get(i);
			EPMDocument epm2D = link.getReferencedBy();
			
			//if(epm2D.getAuthoringApplication().toString().equals("DRAWING")){
				EpmPublishUtil.publish(epm2D); 
			//}
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm2D, State.toState(state));
		}
	}
	//관련 도면
	/*
	Vector<EPMDescribeLink> vecDesc =EpmSearchHelper.service.getEPMDescribeLink(part, true);
	for(EPMDescribeLink likDesc : vecDesc){
		EPMDocument epmDesc = likDesc.getDescribedBy();
		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epmDesc, State.toState(state));
	}
	*/
}
public void approvedIBAChange(RevisionControlled rc,Map<String,String> map) throws Exception{
	//RevisionControlled rc = null;
	
	map = ChangeUtil.changeApprove(rc, map);
	//IBAHolder
	//ECONO,ECODATE,CHK,APR
	String ecoNO = map.get("ECONO");
	String ecoDate = map.get("ECODATE");
	String approveName = map.get("APR");
	String checkerName = map.get("CHK");
	String eoType = map.get("eoType");
	
	IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
	IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
	String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_ECONO));
	//if(ecoNo.length()==0 || ecoNo.equals("-")){ //최초 한번만 등록
	boolean isECO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT) || eoType.equals(AttributeKey.ECOKey.ECO_CHANGE); 
	boolean isFirst = ecoNo.length()== 0 || ecoNo.equals("-"); 
	if(isECO && isFirst){ //최초 한번만 등록 ,양산 이나 설계변경  , 개발 EO 제외
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_ECONO, ecoNO , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_ECODATE, ecoDate , "string");
	}
	
	//EO,ECO시 누적으로 등록
	String changeNo = IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGENO);
	String changeDate = IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGEDATE);
	
	//중복 체크 
	boolean iNoNDouble = changeNo.indexOf(ecoNO)<0;
	
	
	if(iNoNDouble ){
		if(StringUtil.checkString(changeNo)){
			changeNo = changeNo+","+ecoNO;
		}else {
			changeNo = ecoNO;
		}
		
		if(StringUtil.checkString(changeDate)){
			changeDate = changeDate+","+ecoDate;
		}else {
			changeDate = ecoDate;
		}
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_CHANGENO, changeNo , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHANGEDATE, changeDate , "string");
	}
		
	//}
	
	
	
}
%>