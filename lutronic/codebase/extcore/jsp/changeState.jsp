<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="com.e3ps.change.editor.BEContext"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.Date"%>
<%@page import="wt.vc.baseline.ManagedBaseline"%>
<%@page import="com.e3ps.change.service.ChangeHelper"%>
<%@page import="com.e3ps.common.obj.ObjectUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.common.iba.AttributeKey.IBAKey"%>
<%@page import="wt.iba.value.IBAHolder"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="com.e3ps.drawing.util.EpmPublishUtil"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.e3ps.drawing.service.EpmSearchHelper"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.util.PartUtil"%>
<%@page import="com.e3ps.part.dto.PartData"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.folder.Folder"%>
<%@page import="java.util.Vector"%>
<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.service.ChangeUtil"%>
<%@page import="com.e3ps.erp.service.ERPHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.part.WTPartStandardConfigSpec"%>
<%@page import="wt.part.WTPartConfigSpec"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.vc.views.View"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.pom.Transaction"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>




<%
// 객체 상태 변경

String oids[] = {
	"wt.epm.EPMDocument:197156752",
};
//String state = "INWORK";//작업중
String state = "APPROVED"; // 승인됨
String terminate = "false"; // 기본 false

for(String oid : oids){
	Transaction trx = new Transaction();
	try{
		trx.start();
		Object obj = CommonUtil.getObject(oid);
		
		LifeCycleManaged lcm = (LifeCycleManaged) obj;
		lcm = LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state), "true".equals(terminate.toLowerCase()));
		
		trx.commit();
	    trx = null;
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(trx!=null){
			trx.rollback();
	   }
	}
}
out.println("상태변경 완료");


%>



<%-- <%
String HOLDEROID = "holderOid";
String ADOID = "appOid";

String contentOid = "wt.epm.EPMDocument:192998319";
String addOid = "";

ReferenceFactory rf = new ReferenceFactory();
    
ContentHolder contentHolder = (ContentHolder)rf.getReference(contentOid).getObject();
ApplicationData ad = (ApplicationData)rf.getReference(addOid).getObject();


%> --%>



<%-- <%
String ecoOid = "com.e3ps.change.EChangeOrder:192256335";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(ecoOid);

String view = "Design";
List<WTPart> BomList = new ArrayList<WTPart>();
List<WTPart> productList =ECOSearchHelper.service.getCompletePartList(eco);

for(WTPart productPart : productList){
	BomList.add(productPart);
	BomList = getBomList(productPart, ViewHelper.service.getView(view), BomList);
}
try{
	completeProduct(BomList,eco);
	
	//ERP 완제품의 전송
	//System.out.println("3.[ChangdeECOHelper]{completeECO} = " +eco.getEoNumber() +","+eoType +" ERP 전송");
	ERPHelper.service.sendERP(eco);
	
	//Baseline 생성
	//System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber() +" Baseline 생성");
	createECOBaseline(eco);
}catch(Exception e){
	e.printStackTrace();
}
out.println("fn");
%>

<%!
public   List<WTPart>  getBomList(WTPart part,View view,List<WTPart> BomList) throws Exception{
	ArrayList list = descentLastPart(part, view,null);

	for (int i = 0; i < list.size(); i++) {
		
		Object[] o = (Object[]) list.get(i);
		WTPartUsageLink linko =(WTPartUsageLink) o[0];
		WTPart cPart = (WTPart)o[1];
		
		BomList.add(cPart);
		getBomList((WTPart) o[1],view,BomList);
	}
	
	
	return BomList;
}


private static ArrayList descentLastPart(WTPart part, wt.vc.views.View view, State state)
		throws WTException {
	ArrayList v = new ArrayList();
	if (!PersistenceHelper.isPersistent(part))
		return v;
	try {
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec
						.newWTPartStandardConfigSpec(view, state));
		QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part,
				configSpec);
		while (re.hasMoreElements()) {
			Object oo[] = (Object[]) re.nextElement();

			if (!(oo[1] instanceof WTPart)) {
				continue;
			}
			v.add(oo);
		}
	} catch (Exception ex) {
		ex.printStackTrace();
		throw new WTException();
	}
	return v;
}

public void completeProduct(List<WTPart> list,EChangeOrder eco) throws Exception{	 
	
	/*최종 결재자,검토자*/
	Map<String, String> map = ChangeUtil.approvedSetData(eco);
	String eoType = eco.getEoType();
	String state ="APPROVED";
	if(eoType.equals(ECOKey.ECO_DEV)){
		state="DEV_APPROVED";
	}
	map.put("state", state);
	map.put("eoType", eoType);
	
	//중복 되는 부붐 제외
	Vector vecTemp = new Vector();
	for(WTPart part : list){
		
		if(vecTemp.contains(part)){
			continue;
		}else{
			vecTemp.add(part);
			completePartStateChange(part, map);
		}
		
	}
}

public void completePartStateChange(WTPart part,Map<String, String> map) throws Exception {
	
	
	Folder folder = null;
	String partState = part.getLifeCycleState().toString();
	String eoType = map.get("eoType");
	//System.out.println("number="+part.getNumber()+"    PartSate="+partState);
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
	//System.out.println("number="+part.getNumber()+"    PartSate="+partState+"     isStateChange="+isStateChange+"\t state="+state);
	WTUser sessUser = (WTUser) SessionHelper.manager.getPrincipal();
	
	if(isStateChange){
		SessionHelper.manager.setAdministrator();
		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)part, State.toState(state));
		SessionHelper.manager.setPrincipal(sessUser.getName());
	}
	
	
	EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
	//속성 전파
	
	approvedIBAChange(part, map);
	if(epm != null){
		/*3D EPM State Change*/
		if(isStateChange){
			//System.out.println("number="+epm.getNumber()+"    state="+state);
			SessionHelper.manager.setAdministrator();
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm, State.toState(state));
			SessionHelper.manager.setPrincipal(sessUser.getName());
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
				//System.out.println("number="+epm2D.getNumber()+"    state="+state);
			SessionHelper.manager.setAdministrator();
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm2D, State.toState(state));
			SessionHelper.manager.setPrincipal(sessUser.getName());
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
	
	//map = ChangeUtil.changeApprove(rc, map);
	//IBAHolder
	//ECONO,ECODATE,CHK,APR
	String ecoNO = map.get("ECONO");
	String ecoDate = map.get("ECODATE");
	String approveName = map.get("APR");
	String checkerName = map.get("CHK");
	String eoType = map.get("eoType");
	String oid = CommonUtil.getOIDString(rc);
	String location = rc.getLocation();
	//System.out.println("###########approvedIBAChange #$$$$$ approveName="+approveName+"\toid="+oid);
	if(!ChangeUtil.isMeca(location)){
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
	}
	String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_ECONO));
	//if(ecoNo.length()==0 || ecoNo.equals("-")){ //최초 한번만 등록
	boolean isECO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT) || eoType.equals(AttributeKey.ECOKey.ECO_CHANGE); 
	boolean isFirst = ecoNo.length()== 0 || ecoNo.equals("-"); 
	if(isECO && isFirst){ //최초 한번만 등록 ,양산 이나 설계변경  , 개발 EO 제외
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_ECONO, ecoNO , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_ECODATE, ecoDate , "string");
	}
	
	//EO,ECO시 누적으로 등록
	String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGENO));
	String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGEDATE));
	
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


public void createECOBaseline(EChangeOrder eco) throws Exception
{
	String eoType = eco.getEoType();
	List<WTPart>  partList = new ArrayList<WTPart>();
	
	if(eoType.equals(ECOKey.ECO_CHANGE)){
		partList = ECOSearchHelper.service.ecoPartReviseList(eco);
		
		//설계 변경시 완제품에 대한 Baseline 추가 tsuam 2017-04024
		List<WTPart>  completePartList = ECOSearchHelper.service.getCompletePartList(eco);
		
		for(WTPart part : completePartList){
			
			if(partList.contains(part)){
				//System.out.println("1."+eco.getEoNumber() +"," +part.getNumber() +" 설계 대상 부품에 포함 ");
				continue;
			}
			//System.out.println("1."+eco.getEoNumber() +" ,completePartList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());
			
			//완제품인 승인된 완제품 , 작업중이면 전버전 완제품, A버전이면 Skip
			if(part.getLifeCycleState().toString().equals("APPROVED")){
				partList.add(part);
				//System.out.println("2."+eco.getEoNumber() +" ,completePartList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());
			}else{
				//System.out.println("3."+eco.getEoNumber() +" ,completePartList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());
				if(!part.getVersionIdentifier().getValue().equals("A")){
					part =  (WTPart) ObjectUtil.getPreviousVersion(part);
					partList.add(part);
				}
				
			}
			//System.out.println("4."+eco.getEoNumber() +" ,completePartList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());

		}
	}else{
		partList = ECOSearchHelper.service.getCompletePartList(eco);
	}
	
	
	for(WTPart part : partList){
		//System.out.println("1.partList."+eco.getEoNumber() +" ,partList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());
		if(!part.getLifeCycleState().toString().equals("APPROVED")){
			if(!part.getVersionIdentifier().getValue().equals("A")){
				part =  (WTPart) ObjectUtil.getPreviousVersion(part);
			}
		}
		
		//System.out.println("2.partList."+eco.getEoNumber() +" ,partList =" + part.getNumber() +","+part.getLifeCycleState().toString() +","+ part.getVersionIdentifier().getValue());
		boolean isBaselineCheck = true;
		String partoid = CommonUtil.getOIDString(part);
		List<Map<String,String>> list = ChangeHelper.service.getGroupingBaseline(partoid, "", "");
		String[] partOids = new String[list.size()];
		String[] baseNames = new String[list.size()];
		String[] baseOids = new String[list.size()];
		int idx = 0;
		for(Map<String,String> map : list){
			//partOids[idx] = (String)map.get("partOid");
			baseNames[idx]= (String)map.get("baseName");
			//baseOids[idx]= (String)map.get("baseOid");
			//System.out.println("isBaselineCheck Check 1 : "+baseNames[idx]+";"+(baseNames[idx].equals(eco.getEoNumber())));
			if(baseNames[idx].equals(eco.getEoNumber())){
				isBaselineCheck = false;
				break;
			}
			idx++;
		}
		//System.out.println("isBaselineCheck Check 2 : "+isBaselineCheck);
		if(isBaselineCheck) createBaseline(part,eco);
	}
	
}

public  ManagedBaseline createBaseline(WTPart wtpart,EChangeOrder eco) throws Exception
{
    Date date = new Date();
    String baselineName = eco.getEoNumber()+":" + wtpart.getNumber();
    
    //System.out.println("createBaseline =" + baselineName);
    //System.out.println("1.createBaseline."+eco.getEoNumber() +" ,partList =" + wtpart.getNumber() +","+wtpart.getLifeCycleState().toString() +","+ wtpart.getVersionIdentifier().getValue());
    String eoType = eco.getEoType();

    WTProperties wtproperties = WTProperties.getLocalProperties();
    String s = "/Default/설계변경/Baseline";
    String s2 = wtproperties.getProperty("baseline.lifecycle");

    Folder folder = null;
    LifeCycleTemplate lifecycletemplate = null;
	
    if (s != null) folder = FolderHelper.service.getFolder(s, WCUtil.getWTContainerRef());
	else folder = FolderTaskLogic.getFolder(wtpart.getLocation(), WCUtil.getWTContainerRef());

    if (s2 != null) lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2, WCUtil.getWTContainerRef());
	else lifecycletemplate = (LifeCycleTemplate) wtpart.getLifeCycleTemplate().getObject();
    
    ManagedBaseline mb = null;
   // if(eoType.equals("CHANGE")){
   // 	mb = BEContext.createOneLevelBaseline(wtpart, baselineName, folder, lifecycletemplate);
   // }else{
    	mb = BEContext.createBaseline(wtpart, baselineName, folder, lifecycletemplate);
  //  }
	
	
	return mb;
}
%> --%>