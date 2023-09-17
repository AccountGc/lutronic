<%@page import="java.io.IOException"%>
<%@page import="com.e3ps.common.obj.ObjectUtil"%>
<%@page import="wt.vc.baseline.BaselineMember"%>
<%@page import="com.e3ps.part.util.BomBroker"%>
<%@page import="com.e3ps.part.service.BomSearchHelper"%>
<%@page import="com.e3ps.erp.service.ERPSearchHelper"%>
<%@page import="com.e3ps.part.dto.PartTreeData"%>
<%@page import="com.e3ps.erp.beans.BomData"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="com.e3ps.change.service.ECOHelper"%>
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
public ManagedBaseline getLastManagedBaseline(String partOid) throws Exception {
	ReferenceFactory rf = new ReferenceFactory();
	WTPart part = (WTPart)rf.getReference(partOid).getObject();
	
	QuerySpec qs = new QuerySpec();
	int ii = qs.appendClassList(ManagedBaseline.class, true);
	int jj = qs.appendClassList(BaselineMember.class,false);
	int kk = qs.appendClassList(WTPart.class,true);
	
	qs.appendWhere(new SearchCondition(ManagedBaseline.class,"thePersistInfo.theObjectIdentifier.id",BaselineMember.class,"roleAObjectRef.key.id"),new int[]{ii,jj});
	qs.appendAnd();
	qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,kk});
	qs.appendAnd();
	qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
	
	qs.appendOrderBy(new OrderBy(new ClassAttribute(ManagedBaseline.class,"thePersistInfo.createStamp"),true),new int[]{ii});
	//System.out.println(qs);
	QueryResult qr = PersistenceHelper.manager.find(qs);
	
	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	ManagedBaseline bl = null;
	while(qr.hasMoreElements()){
		Object[] o = (Object[])qr.nextElement();
		bl = (ManagedBaseline)o[0];
		WTPart pp = (WTPart)o[1];
		return bl;
		
	}
	return bl;
}


public WTPart getBaselinePart(String oid, ManagedBaseline bsobj) throws Exception {
	ReferenceFactory rf = new ReferenceFactory();

	WTPart part = (WTPart)rf.getReference(oid).getObject();

	if(bsobj!=null){
	    QuerySpec qs = new QuerySpec();
	    int ii = qs.addClassList(WTPart.class,true);
	    int jj = qs.addClassList(BaselineMember.class,false);
	    qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,ii});
	    qs.appendAnd();
	    qs.appendWhere(new SearchCondition(BaselineMember.class,"roleAObjectRef.key.id","=",bsobj.getPersistInfo().getObjectIdentifier().getId()),new int[]{jj});
	    qs.appendAnd();
	    qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{ii});
	    QueryResult qr = PersistenceHelper.manager.find(qs);
	    if(qr.hasMoreElements()){
	        Object[] o = (Object[])qr.nextElement();
	        part = (WTPart)o[0];
	    }
	}
	
	return part;
}


public ArrayList<PartTreeData[]> getBaseLineCompare2(String oid,JspWriter out) throws Exception {
	
	WTPart part = (WTPart)CommonUtil.getObject(oid);
	
	WTPart prePart = (WTPart) ObjectUtil.getPreviousVersion(part);
	
	//최신 베이스 라인 가져옴.(본인이 포함된 base라인중에.
	//변경 후 최신 버전 베이스라인 가져오는 거
	ManagedBaseline afterBaseline = getLastManagedBaseline(oid);
	
	if(null==prePart){
		afterBaseline = null;
	}
	
	/* 
	ManagedBaseline beforeBaseline = null;
	
	if(null!=prePart){
		String preOid = CommonUtil.getFullOIDString(prePart);
		beforeBaseline= getLastManagedBaseline(preOid);
	} */
	
	
	List<PartTreeData> list = BomSearchHelper.service.getBOM(part, true, null,false);
	
	
	List<PartTreeData> afterBaselinelist = new ArrayList<PartTreeData>();
	
	
	if(afterBaseline != null){
		afterBaselinelist = BomSearchHelper.service.getBOM(part, true, afterBaseline,false);
	}
	
	
	BomBroker broker = new BomBroker();
	
	PartTreeData root = broker.getOneleveTree(part , null);
	PartTreeData root2 = null;
	out.println(CommonUtil.getFullOIDString(afterBaseline)+ "<br/>");
	/* 
	if(beforeBaseline!=null){
		out.println(" beforeBaseline ="+beforeBaseline.getName()+ "<br/>");
		root = broker.getOneleveTree(prePart , beforeBaseline);
	} */
	
	if(afterBaseline == null){
		root2 = new PartTreeData(part, null, 0,"");
	}else{
		out.println(" afterBaseline ="+afterBaseline.getName()+ "<br/>");
		WTPart part2 = getBaselinePart(oid, afterBaseline);
		root2 = broker.getOneleveTree(part2 ,afterBaseline);
	}
	//out.println("root 2 >>>" + (null!=root2) + "<br/>");
	
	
	ArrayList<PartTreeData[]> result = new ArrayList<PartTreeData[]>();
	broker.compareBom(root, root2, result);
	
	//out.println("result.size =" + result.size()+ "<br/>");
	
	
	return result;
}



public  Vector<BomData> compareBom(WTPart part , Vector<BomData> bomList,JspWriter out) throws Exception{
	
	String oid = CommonUtil.getOIDString(part);
	//ArrayList<PartTreeData[]> result = ERPSearchHelper.service.getBaseLineCompare(oid);//getBaseLineCompare(oid);//ERPSearchHelper.service.getBaseLineCompare(oid);//getBaseLineCompare
	
	ArrayList<PartTreeData[]> result = getBaseLineCompare2(oid,out);
	out.println("part  =" + part.getNumber()+ " start <br/>");
	for(int i=0; i< result.size(); i++){
	    PartTreeData[] o  = (PartTreeData[])result.get(i);
	   
	    PartTreeData data = o[0];
	    PartTreeData data2 = o[1];
	    
	    BomData bomData = new BomData();
	    String partNumber = "";
	    String state = "E";	//기존 
	    bomData.setParent(part.getNumber());
	    bomData.setVer(part.getVersionIdentifier().getValue());
	    double amount = 0;
	    String unit = "";
	    String childPart = "";
	    int level = 0;
	    String ver = "";
	    
	    /* if(data.number.equals("2040106010")){
	    	out.println( data.baseQuantity+"\t"+data.quantity+"\t"+"case 3 <br/>" );
	    } */
	    
	    if(data==null){
	    	//out.println( "case 1 <br/>" );
	    	state ="D";//"#D3D3D3";		//삭제
	    	amount = data2.quantity;
	    	unit = data2.unit;
	    	childPart = data2.number;
	    	level = data2.level;
	    	ver = data2.version;
	    }else {
	    	//out.println( "case 2 <br/>" );
	    	amount = data.quantity;
		    unit = data.unit;
		    childPart =  data.number;
		    level = data.level;
		    ver = data.version;
		    //out.println("data2  =" + data2+ "  <br/>");
	    	if(data2==null){
	    		//out.println( "case 3 <br/>" );
	    		state ="A";//"#8FBC8F";		//추가
	    	}else{
	    		//out.println(data.baseQuantity+"\t" +data2.baseQuantity +"\t"+childPart + "\tcase 4 <br/>" );
	    		if(!data.compare(data2)){
	    			state = "C";		//변경
	    		}
	    	}
	    	/* WTPart prevPart = (WTPart)ObjectUtil.getPreviousVersion(data.part);
	    	if(null==prevPart){
	    		state ="A";
	    	} */
	    }
	    
	    if(childPart.equals(partNumber)){
	    	//out.println( "case 1 <br/>" );
	    	continue;
	    }
	    
	    if(state.equals("E")){
	    	//out.println( "case 2 <br/>" );
	    	continue;
	    }
	    
	    bomData.setChild(childPart);
	    bomData.setUnit(unit);
	    bomData.setAmount(amount);
	    bomData.setChangeType(state);
	    bomData.setVer(ver);
	    bomData.setCver(ver);;
	    bomList.add(bomData);
	    //out.println( bomData.parent+":"+bomData.child +":"+ bomData.changeType );
	}
	    out.println("part  =" + part.getNumber()+"\t"+bomList.size()+ " END <br/>");
		
	//Collections.sort(bomList, new ObjectComarator());
	//System.out.println("=========== compare END========== ");;
	
	
	return bomList;
	
}

public static List<BomData> removeChkBOM(Vector<BomData> vecBOM,JspWriter out) throws IOException{
	
	Vector<String> vecCheck = new Vector();
	List<BomData> removeData = new ArrayList<>();
	//for(int i = 0 ; i <vecBOM.size() ;i++ ){
	for(int i = vecBOM.size() - 1; i >= 0; i--){
		
		BomData  data = vecBOM.get(i);
		String bomKey=data.parent+":"+data.child;
		out.println(bomKey + "\t "+PartUtil.isChange(data.parent)+ "\t "+PartUtil.isChange(data.child)+ "\t "+vecCheck.contains(bomKey)+"<br/>");
		if(PartUtil.isChange(data.parent) || PartUtil.isChange(data.child)){
			removeData.add(data);
			vecBOM.remove(i);
		}else{
			if(vecCheck.contains(bomKey)){
				removeData.add(data);
				vecBOM.remove(i);
			}else{
				vecCheck.add(bomKey);
			}
		}
		
		
	}
	
	return removeData;
}

%>
<%
String oid = "com.e3ps.change.EChangeOrder:187255600";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

List<WTPart> changePartLis = ECOSearchHelper.service.ecoPartReviseList(eco);

Vector<BomData> vecBom = new Vector<BomData>();
String ecoNumber = eco.getEoNumber(); 	

//BOM 리스트
for(WTPart part : changePartLis){
	
	vecBom = compareBom(part,vecBom,out);
	//tempBom = getBom(part, null, getView(), tempBom,false);
}
out.println("*************** 삭제 로직 전 vecBom *******************************<br/>");
out.println("Size >>>> " + vecBom.size()+ "<br/>");
out.println("changePartLis >> " + changePartLis.size());
for(int i=0; i<vecBom.size(); i++){
	BomData bomData = vecBom.get(i);
	out.println(i+"번 째 Bom >>>> "+bomData.getParent() +"\t"+ bomData.getChild() +"\t"+ bomData.getAmount()+"\t"+ bomData.getChangeType() +"\t"+ bomData.getPver()
	 +"\t"+ bomData.getVer()+"\t"+ bomData.getUnit()+ "<br/>");
}
out.println("*************** 삭제 로직 전 vecBom *******************************<br/>");
//제거된 봄 데이터
List<BomData> removeData = removeChkBOM(vecBom,out);


out.println("*************** 삭제 된 vecBom *******************************<br/>");
out.println("Size >>>> " + removeData.size()+"<br/>");
out.println("*************** 삭제 된 vecBom *******************************<br/>");
out.println("Size >>>> " + vecBom.size());
for(int i=0; i<removeData.size(); i++){
	out.println(i+"번 째 Bom >>>> " + removeData.get(i) + "<br/>");
}
out.println("*************** 삭제 된 vecBom *******************************<br/>");
%>
<%-- <%

//EChangeOrder eco = (EChangeOrder)CommonUtil.getObject("com.e3ps.change.EChangeOrder:186005986");
/* List<WTPart>  partList = new ArrayList<WTPart>();
partList = ECOSearchHelper.service.ecoPartReviseList(eco);
for(WTPart part : partList){
	EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
	if( epm != null){
	out.print("<br> 3D 상태 : " + epm.getNumber() + "/" + epm.getCheckoutInfo().getState().getDisplay() + "/" + epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue());
	Vector<EPMReferenceLink> vec =EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
	for(int i = 0 ; i < vec.size() ; i++){
		EPMReferenceLink link = vec.get(i);
		EPMDocument epm2D = link.getReferencedBy();
		if( epm2D != null){
		out.print("<br> 2D 상태 : " + epm2D.getNumber() + "/" + epm2D.getCheckoutInfo().getState().getDisplay()+ "/" + epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue());
		}
	}
	}
} */
/* LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco, State.toState("APPROVED"));

ECOHelper.service.completeECO(eco); */


%> --%>