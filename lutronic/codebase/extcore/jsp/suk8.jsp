<%@page import="wt.vc.wip.CheckoutInfo"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
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

/* private void createPartLink(EChangeOrder eco,String[] partOids,String[] isSelectBoms,JspWriter out) throws Exception{
	
	Vector vecBom = new Vector();
	if(isSelectBoms != null){
		out.println("isSelectBoms : ? <br>");
		//System.out.println("isSelectBoms.length ="+isSelectBoms.length);
		for(int i = 0 ; i < isSelectBoms.length ; i++){
			//System.out.println("isDelete[i] ="+isSelectBoms[i]);
			vecBom.add(isSelectBoms[i]);
		}
	}
	
	for(int i=0; partOids!=null && i<partOids.length; i++){

		WTPart part = (WTPart)CommonUtil.getObject(partOids[i]);
		boolean _isSelectBom = false;
		
		boolean isSelect = PartSearchHelper.service.isSelectEO(part,eco.getEoType());
		if(!isSelect){
			throw new Exception(Message.get(part.getNumber()+"은 EO,ECO가 진행중입니다.")) ;
		}
		
		if(vecBom.contains(part.getNumber())) _isSelectBom = true;
		
		String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
		EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster)part.getMaster(), eco);
		link.setVersion(version);
		link.setBaseline(true);
		PersistenceHelper.manager.save(link);
		out.println("create link : " + link.getPersistInfo().getObjectIdentifier().getStringValue() + " <br>");
		
	}

} */
%>
<%
	/* String[] oidArr = {"wt.part.WTPart:167250107",
		"wt.part.WTPart:167250108",
		"wt.part.WTPart:167266968",
		"wt.part.WTPart:167267143",
		"wt.part.WTPart:167250105",
		"wt.part.WTPart:167250104",
		"wt.part.WTPart:167250102",
		"wt.part.WTPart:167250103",
		"wt.part.WTPart:167885016",
		"wt.part.WTPart:167635459",
		"wt.part.WTPart:167640510",
		"wt.part.WTPart:169726368",
		"wt.part.WTPart:169800515",
		"wt.part.WTPart:169795391",
		"wt.part.WTPart:167999036",
		"wt.part.WTPart:168272206",
		"wt.part.WTPart:168217812",
		"wt.part.WTPart:168231450",
		"wt.part.WTPart:168217823",
		"wt.part.WTPart:168217809",
		"wt.part.WTPart:168217808",
		"wt.part.WTPart:168217819",
		"wt.part.WTPart:168217821",
		"wt.part.WTPart:168217824",
		"wt.part.WTPart:168217815",
		"wt.part.WTPart:168217813",
		"wt.part.WTPart:168217806",
		"wt.part.WTPart:168230951",
		"wt.part.WTPart:168217807",
		"wt.part.WTPart:168217825",
		"wt.part.WTPart:168217826",
		"wt.part.WTPart:168217818",
		"wt.part.WTPart:168217810",
		"wt.part.WTPart:168217817",
		"wt.part.WTPart:167899245",
		"wt.part.WTPart:167887263",
		"wt.part.WTPart:167888728",
		"wt.part.WTPart:167889323",
		"wt.part.WTPart:167889393",
		"wt.part.WTPart:167889801",
		"wt.part.WTPart:167889889",
		"wt.part.WTPart:167890375",
		"wt.part.WTPart:167890460",
		"wt.part.WTPart:167891084",
		"wt.part.WTPart:167891277",
		"wt.part.WTPart:167891362",
		"wt.part.WTPart:167891687",
		"wt.part.WTPart:167892272",
		"wt.part.WTPart:167892457",
		"wt.part.WTPart:167982756",
		"wt.part.WTPart:168127254",
		"wt.part.WTPart:167897462",
		"wt.part.WTPart:167893854",
		"wt.part.WTPart:167982755",
		"wt.part.WTPart:167892979",
		"wt.part.WTPart:167894348",
		"wt.part.WTPart:167892978",
		"wt.part.WTPart:167892975",
		"wt.part.WTPart:167892973",
		"wt.part.WTPart:168133024",
		"wt.part.WTPart:167998019",
		"wt.part.WTPart:167999038",
		"wt.part.WTPart:167999039",
		"wt.part.WTPart:167993387",
		"wt.part.WTPart:168001361",
		"wt.part.WTPart:167895153",
		"wt.part.WTPart:168217822",
		"wt.part.WTPart:172468271",
		"wt.part.WTPart:172468910",
		"wt.part.WTPart:168174597",
		"wt.part.WTPart:168174596",
		"wt.part.WTPart:167794874",
		"wt.part.WTPart:159162530",
		"wt.part.WTPart:1789202",
		"wt.part.WTPart:167897582",
		"wt.part.WTPart:167887267",
		"wt.part.WTPart:168127255",
		"wt.part.WTPart:167892977",
		"wt.part.WTPart:167798580",
		"wt.part.WTPart:167892976",
		"wt.part.WTPart:9171259",
		"wt.part.WTPart:159267083",
		"wt.part.WTPart:159267099",
		"wt.part.WTPart:159268315",
		"wt.part.WTPart:159328426",
		"wt.part.WTPart:159344201",
		"wt.part.WTPart:159270792",
		"wt.part.WTPart:159333385",
		"wt.part.WTPart:159328468",
		"wt.part.WTPart:159293994",
		"wt.part.WTPart:167984161",
		"wt.part.WTPart:159106915",
		"wt.part.WTPart:172490725",
		"wt.part.WTPart:159162515",
		"wt.part.WTPart:159268331",
		"wt.part.WTPart:159268352",
		"wt.part.WTPart:159268377",
		"wt.part.WTPart:172479884",
		"wt.part.WTPart:159269612",
		"wt.part.WTPart:159285244",
		"wt.part.WTPart:2736074",
		"wt.part.WTPart:167887264",
		"wt.part.WTPart:167887268",
		"wt.part.WTPart:167887265",
		"wt.part.WTPart:167887269",
		"wt.part.WTPart:167901558",
		"wt.part.WTPart:167901557",
		"wt.part.WTPart:167901556",
		"wt.part.WTPart:167897461",
		"wt.part.WTPart:167897460"

};

	String oid = "com.e3ps.change.EChangeOrder:166992102";
	EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid); */
	/* String distribute = "false";
	boolean checkDummy = true;
	
	
	
	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	
	Object obj = CommonUtil.getObject(oid);
	EChangeOrder eco = null;
	if(obj instanceof EChangeOrder)
	 eco = (EChangeOrder)obj;
	else if(obj instanceof EChangeActivity){
		eco = (EChangeOrder) ((EChangeActivity) obj).getEo();
	}
	String type = eco.getEoType();
	boolean  isDistribute = StringUtil.checkNull(distribute).equals("true") ? true : false;
	System.out.println("wf_CheckPart isDistribute =" + isDistribute);
	int idx = 0;
	QueryResult qr = ECOSearchHelper.service.ecoPartLink(eco);
	out.println("qr Size : " + qr.size() + "<br>");
	while(qr.hasMoreElements()){
		
		idx++;
		String idxTemp = String.valueOf(idx);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("idxPart", idxTemp);
		
		Object[] o = (Object[])qr.nextElement();
		
		EcoPartLink link = (EcoPartLink)o[0];
		
		String linkOid = CommonUtil.getOIDString(link);
		WTPartMaster master =link.getPart();
		boolean isDummy =PartUtil.isChange(master.getNumber());
		if(checkDummy){
			if(isDummy){
				continue;
			}
		}
		String version = link.getVersion();
		WTPart part = PartHelper.service.getPart(master.getNumber(),version);
		String partOid = CommonUtil.getOIDString(part);
		String iteration = part.getIterationIdentifier().getSeries().getValue();
		String nextOid = null;
		boolean revisable = "APPROVED".equals(part.getLifeCycleState().toString()) 
				|| "DEV_APPROVED".equals(part.getLifeCycleState().toString());
		WTPart nextPart = null;
		EPMDocument epm3d = null;
		Vector<EPMReferenceLink> vec2D = new Vector<EPMReferenceLink>();
		String isDisuse ="";
		boolean isBOM = link.isBaseline();
		String isBOMCheck ="";
		String BOMValueCheck = "";
		if(isBOM) {
			isBOMCheck ="<b><font color='red'>√</font></b>";
			BOMValueCheck = "checked";
		}
		if(link.isRevise()) {
			nextPart = (WTPart)com.e3ps.common.obj.ObjectUtil.getNextVersion(part);
			partOid = CommonUtil.getOIDString(nextPart);
			PartData nextData = new PartData(nextPart);
			nextOid = CommonUtil.getOIDString(nextPart);
			if(nextPart != null) {
				EPMBuildRule buildRule=PartSearchHelper.service.getBuildRule(nextPart);
				if(buildRule != null){
					epm3d =(EPMDocument)buildRule.getBuildSource();
					map.put("epm3dOid", CommonUtil.getOIDString(epm3d));
					map.put("epm3dNumber", epm3d.getNumber());
				}
				map.put("nextOid", nextData.oid);
				map.put("nextVROid", nextData.vrOid);
				WTUser createUser = (WTUser) nextPart.getCreator().getPrincipal();
				String nextUserCreaterName = createUser.getFullName();
				map.put("nextUserCreaterName", nextUserCreaterName);
				map.put("nextName", nextData.name);
				map.put("nextNumber", nextData.number);
				map.put("nextVersion", nextData.version);
				map.put("nextIteration", nextData.iteration);
				map.put("nextState", nextData.getLifecycle());
				
			}else{
				EPMBuildRule buildRule =PartSearchHelper.service.getBuildRule(part);
				if(buildRule != null){
					epm3d =(EPMDocument)buildRule.getBuildSource();
					map.put("epm3dOid", CommonUtil.getOIDString(epm3d));
					map.put("epm3dNumber", epm3d.getNumber());
				}
			}
			
			if(epm3d != null){
				vec2D =  EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm3d.getMaster());
			}
			revisable = false;
		}
		List<Map<String,Object>> list2d = new ArrayList<Map<String,Object>>();
		if(epm3d != null) { 
			for(int h= 0 ; h <vec2D.size() ; h++){ 
				EPMReferenceLink epmlink = vec2D.get(h);
				EPMDocument epm2d = epmlink.getReferencedBy();
				Map<String,Object> map2d = new HashMap<String,Object>();
				
				map2d.put("epm2dOid", CommonUtil.getOIDString(epm2d));
				map2d.put("epm2Number", epm2d.getNumber());
				list2d.add(map2d);
			}
			
		}
		
		String rowSpan = String.valueOf(vec2D.size());
		
		String revisableTemp = String.valueOf(revisable); 
		
	
		String bom = "<input type='button' onclick=javascript:viewBom('" + partOid+ "') value='BOM 전개'>";
		String baselineOid = "";
		if(isDistribute){
			ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
			baselineOid = CommonUtil.getOIDString(baseline);
			bom = "<input type='button' onclick=javascript:distributeBOM('"+partOid+"','"+CommonUtil.getOIDString(baseline)+"') value='BOM 전개' >";	
		}
		
		PartData nowPartData = new PartData(part);
		WTUser createUser = (WTUser) part.getCreator().getPrincipal();
		String userCreatorName = createUser.getFullName();
		map.put("oid", oid);
		map.put("partOid", nowPartData.oid);
		map.put("linkOid", linkOid);
		map.put("rowSpan", rowSpan);
		map.put("revisable", revisableTemp);
		map.put("number", nowPartData.number);
		map.put("userCreatorName", userCreatorName);
		map.put("name", nowPartData.name);
		map.put("nowVROid", nowPartData.vrOid);
		map.put("productOid", nowPartData.getPDMLinkProductOid());
		//map.put("partOid", CommonUtil.getOIDString(part));
		map.put("version", nowPartData.version);
		map.put("iteration", nowPartData.iteration);
		map.put("state", nowPartData.getLifecycle());
		map.put("stateKey", nowPartData.stateKey);
		map.put("isDisuse", isDisuse);
		map.put("nextPart", nextOid);
		map.put("bom", bom);
		map.put("list", list2d);
		map.put("type", type);
		map.put("BOMCheck",isBOMCheck);
		map.put("BOMValueCheck" , BOMValueCheck);
		map.put("isDummy", isDummy);
		map.put("baselineOid", baselineOid);
		
		
		list.add(map);
		
	}
	
	
	
	out.println("list Size : " + list.size() + "<br>"); */
	/* out.println("start<br>");
	createPartLink(eco,oidArr,null,out);
	
	out.println("end<br>"); */
%>



<%!
%>
<%
String oid = "com.e3ps.change.EChangeOrder:186276973";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

List<WTPart>  partList = new ArrayList<WTPart>();
partList = ECOSearchHelper.service.ecoPartReviseList(eco);
for(WTPart part : partList){
	out.println("===========================================<br/>");
	out.println("part >> " + part + "<br/>");
	PartData partData = new PartData(part);
	//Generic instance, 더미 파일은 승인 처리 하지 않음
	boolean isStateChange = !(partData.isGENERIC() || partData.isINSTANCE()) || !PartUtil.isChange(part.getNumber());
	out.println("isStateChange >> " + isStateChange + "<br/>");
	String state = "APPROVED";
	
	EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
	WTUser sessUser = (WTUser) SessionHelper.manager.getPrincipal();
	if(epm != null){
		/*3D EPM State Change*/
		if(isStateChange){
			//System.out.println("number="+epm.getNumber()+"    state="+state);
			SessionHelper.manager.setAdministrator();
			//LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm, State.toState(state));
			out.println("epm >>>> " + (LifeCycleManaged)epm + "\t state >>>>> " + State.toState(state) + "<br/>");
			out.println("epm.getCADName() >>>> " + epm.getCADName() + "\t epm.getCheckoutInfo() >>>> "+  epm.getCheckoutInfo().getState() + "<br/>");
			out.println("sessUser >>>> " +sessUser.getName() + "<br/>");
			//SessionHelper.manager.setPrincipal(sessUser.getName());
			out.println("===========================================<br/>");
		}
	}
}
%>