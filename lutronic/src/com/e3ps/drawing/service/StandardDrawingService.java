package com.e3ps.drawing.service;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.interfaces.EPMDependencyMaster;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.util.EPMSearchHelper;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.FloatValue;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.distribute.util.MakeZIPUtil;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentQueryHelper;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.e3ps.drawing.EpmLocation;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.org.People;
import com.e3ps.part.dto.ExcelData;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsQueryHelper;
import com.ptc.wvs.server.ui.UIHelper;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

@SuppressWarnings("serial")
public class StandardDrawingService extends StandardManager implements DrawingService {
	
	static final String BLANK_IMG = WebUtil.getHost()+"netmarkets/images/blank24x16.gif";
	public static final String ROOTLOCATION = "/Default/PART_Drawing";
	
	public static StandardDrawingService newStandardDrawingService() throws Exception {
		final StandardDrawingService instance = new StandardDrawingService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public EPMDocument getEPMDocument(WTPart _part) throws Exception {
        if (_part == null) { return null; }
        QueryResult qr = null;
        if (VersionControlHelper.isLatestIteration(_part))
            qr = PersistenceHelper.manager.navigate(_part, "buildSource", EPMBuildRule.class);
        else
            qr = PersistenceHelper.manager.navigate(_part, "builtBy", EPMBuildHistory.class);
        while (qr != null && qr.hasMoreElements())
            return (EPMDocument) qr.nextElement();
        return null;
    }
	
	@Override
	public String getPrefix(String fileName){
		//System.out.println("subStirng = = = = = ="+changeName.substring(changeName.length()-3));
		return EpmUtil.getExtension(fileName);
		//return changeName.substring(changeName.length()-3,changeName.length());
		
	}
	
	@Override
	public boolean isFileNameCheck(String fileName){
    	boolean check = false;
    	try{
    		QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);
    		
    		qs.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.CADNAME, SearchCondition.EQUAL, fileName, false ), new int[] {0});
    		
    		QueryResult rt=PersistenceHelper.manager.find(qs);
    		
    		if(rt.size()>0) return true; 
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return check;
    }
	
	@Override
	public EPMDocumentType getEPMDocumentType(String fileName){
		/*
		CADASSEMBLY.value=�����
		CADCOMPONENT.value=CAD ��ǰ
		CADDRAWING.value=�����
		DIAGRAM.value=���̾�׷�
		ECAD-ASSEMBLY.value=ECAD - �����
		ECAD-BOARD.value=ECAD - ����
		ECAD-COMPONENT.value=ECAD - ������Ʈ
		ECAD-CONTENT.value=ECAD - ����Ʈ
		ECAD-SCHEMATIC.value=ECAD - ����
		ECAD-SOURCE.value=ECAD - �ҽ�
		FORMAT.value=����
		LAYOUT.value=���̾ƿ�
		MANUFACTURING.value=����
		MARKUP.value=��ũ��
		OTHER.value=��Ÿ
		REPORT.value=����
		SKETCH.value=����ġ
		UDF.value=����� ���� ���
		DESIGN.value=����
		RENDERING.value=������
		PUB_COMPOUNDTEXT.value=�Խ� �ҽ�
		PUB_GRAPHIC.value=�Խ� �׷���
		PUB_CADVIEWABLE.value=���� ������ CAD �׸� �Խ� 
		CADDRAWINGTEMPL.value=����� ���ø�
		ANALYSIS.value=�м�
		IGES.value=Iges
		STEP.value=Step
		VDA.value=VDA-FS
		ACIS.value=ACIS
		PARASOLID.value=Parasolid
		ZIP.value=Zip ����
		DXF.value=DXF
		NOTE.value=�޸�
		WORKSHEET.value=Mathcad ��ũ��Ʈ
		EDA_DIFF_CONFIG.value=ECAD Compare ���� ������
		EDA_DIFF_REPORT.value=ECAD Compare IDX ������
		CALCULATION_DATA.value=��� ������
		MANIKIN_POSTURE.value=��ü ���� �ڼ�
		WORKPLANE.value=�۾� ���
		WORKPLANE_SET.value=�۾� ��� ��Ʈ
		CUTTER_LOCATION.value=Ŀ�� ��ġ
		MACHINE_CONTROL.value=�ӽ� ���� ������
		INSTANCEDATA.value=�ν��Ͻ� ������
		MECHANICARESULTS.value=�м� �� ���� ���� ���
		MECHANICAREPORT.value=HTML ��� ����
		*/
		
		String type = "OTHER";

		if (".drw".equals(fileName.toLowerCase())){
			type = "CADDRAWING";
		}
		else if (".prt".equals(fileName.toLowerCase())){
			type = "CADCOMPONENT";
		}
		else if (".asm".equals(fileName.toLowerCase())){
			type = "CADASSEMBLY";
		}
		else if (".frm".equals(fileName.toLowerCase())){
			type = "FORMAT";
		}
		else if (".dwg".equals(fileName.toLowerCase())){
			type = "CADDRAWING";
		}
		else if (".igs".equals(fileName.toLowerCase())){
			type = "IGES";
		}
		else if (".iges".equals(fileName.toLowerCase())){
			type = "IGES";
		}
		else if(".gif".equals(fileName.toLowerCase())){
			type = "PUB_GRAPHIC";
		}
		else if(".jpg".equals(fileName.toLowerCase())){
			type = "PUB_GRAPHIC";
		}
		else if(".zip".equals(fileName.toLowerCase())){
			type = "ZIP";
		}
		
        return EPMDocumentType.toEPMDocumentType(type);
    }
	
	@Override
	public Map<String,Object> listDrawingAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {

			QuerySpec query = DrawingHelper.service.getListQuery(request);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    int rowCount    = control.getTopListCount();
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
	    
	    String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");
	    //PJT TEST EDIT 20170103 추가시 
	    /*(wt.intersvrcom.intersvrcomResource/46) wt.wrmf.transport.WTTransportException:
	    	"http://pdm.lutronic.com/Windchill/servlet/WindchillGW"(으)로부터의 서명(사인 시
	    	기: "Tue Jan 03 17:18:27 JST 2017", 접수 시기: "Tue Jan 03 17:18:32 JST 2017")은
	    	 이미 사용 중입니다. 작업을 다시 시도하십시오. 오류가 계속 발생하면 시스템 관리
	    	자에게 연락하십시오.
	                     상기 에러 발생
	    */
	   // System.setProperty("java.awt.headless", "true"); 
	    while(qr.hasMoreElements()){
			Object[] o = (Object[]) qr.nextElement();
			EPMDocument epm = (EPMDocument) o[0];
			EpmData data = new EpmData(epm);
			String thum = "<img src = '" + data.getThum_mini() + "' border=0 id='" + data.oid + "'> ";
			/*
			if(data.getThum() != null) {
				thum = "<a href=javascript:showThum(\"" + data.number.replaceAll(" ", "_") + "\""
						+ ",\"" + data.getThum() + "\""
						+ ",\"" + data.oid + "\""
						+ ",\"" + data.getCopyTag() + "\")>" + thum + "</a>";
			}
			*/
			if(data.getThum() != null) {
				thum ="<a href=javascript:showThumAction(\""+data.oid+"\")>" + thum + "</a>";
				
			}
			xmlBuf.append("<row id='"+ data.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.icon + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + thum + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid +"')>" + data.name + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.location + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getState() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.dateSubString(true) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.dateSubString(false) + "]]></cell>");
			
			xmlBuf.append("</row>");
	    }
	    
	    xmlBuf.append("</rows>");
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("formPage"       , formPage);
		result.put("rows"           , rows);
		result.put("totalPage"      , totalPage);
		result.put("startPage"      , startPage);
		result.put("endPage"        , endPage);
		result.put("listCount"      , listCount);
		result.put("totalCount"     , totalCount);
		result.put("currentPage"    , currentPage);
		result.put("param"          , param);
		result.put("sessionId"      , qr.getSessionId()==0 ? "" : qr.getSessionId());
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public QuerySpec getListQuery(HttpServletRequest request) throws Exception{
		
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, true);
		ReferenceFactory rf = new ReferenceFactory();
		
		try {
			String foid 	        = StringUtil.checkNull(request.getParameter("foid"));	
			String location         = StringUtil.checkNull(request.getParameter("location"));		
			String cadDivision      = StringUtil.checkNull(request.getParameter("cadDivision")); 	
			String cadType          = StringUtil.checkNull(request.getParameter("cadType"));		
			String number           = StringUtil.checkNull(request.getParameter("number"));		
			String name             = StringUtil.checkNull(request.getParameter("name"));		
			String predate 	        = StringUtil.checkNull(request.getParameter("predate"));	
			String postdate         = StringUtil.checkNull(request.getParameter("postdate"));	
			String predate_modify 	= StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify  = StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator 		    = StringUtil.checkNull(request.getParameter("creator"));
			String state 	        = StringUtil.checkNull(request.getParameter("state"));	
			String islastversion 	= StringUtil.checkNull(request.getParameter("islastversion"));
			String sortValue 	    = StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck 	    = StringUtil.checkNull(request.getParameter("sortCheck"));
			String autoCadLink 	    = StringUtil.checkNull(request.getParameter("autoCadLink"));
			String unit             = StringUtil.checkNull(request.getParameter("unit"));     
			String model            = StringUtil.checkNull(request.getParameter("model"));   
			String productmethod    = StringUtil.checkNull(request.getParameter("productmethod"));
			String deptcode         = StringUtil.checkNull(request.getParameter("deptcode"));
			String weight1           = StringUtil.checkNull(request.getParameter("weight1"));
			String weight2           = StringUtil.checkNull(request.getParameter("weight2"));
			String manufacture      = StringUtil.checkNull(request.getParameter("manufacture"));
			String mat              = StringUtil.checkNull(request.getParameter("mat"));
			String finish           = StringUtil.checkNull(request.getParameter("finish"));
			String remarks          = StringUtil.checkNull(request.getParameter("remarks"));
			String specification    = StringUtil.checkNull(request.getParameter("specification"));

			
			String temp = "";
			Folder folder = null;
			if(foid!=null && foid.length() > 0){
				folder = (Folder)rf.getReference(foid).getObject();
				location = FolderHelper.getFolderPath( folder );
				temp = folder.getName();
			}else{
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
				foid="";
			}
			
			// 최신 이터레이션
			if(query.getConditionCount() > 0) { query.appendAnd(); }
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
			
			// 버전 검색
			if(!StringUtil.checkString(islastversion)) islastversion = "true";
			if("true".equals(islastversion)) {
				SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);;
			}
			
			//Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });

			
			//�����ȣ
			if(number.length() > 0){
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>number", SearchCondition.LIKE , "%"+number+"%", false), new int[]{idx});
			}else number = "";
			
			//�����
			if(name.length() > 0){
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>name", SearchCondition.LIKE , "%"+name+"%", false), new int[]{idx});
			}else name = "";	
			
			//�ۼ���
			if(creator.length()>0){
				People people = (People)rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"iterationInfo.creator.key","=", PersistenceHelper.getObjectIdentifier( user )), new int[]{idx});
			} else creator = "";
			
			//���°˻�
			if(StringUtil.checkString(state)) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class, "state.state" , SearchCondition.EQUAL, state), new int[]{idx});
			}
			
			
			
			//�ۼ����� (predate)
			if(StringUtil.checkString(predate)) {
			    if(query.getConditionCount()>0) { query.appendAnd(); }
			    query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.createStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[]{idx});
			}
			//�ۼ����� (postdate)
			if(StringUtil.checkString(postdate)) {
			    if(query.getConditionCount()>0) { query.appendAnd(); }
			    query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.createStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[]{idx});
			}
			
			//��������
			if(predate_modify.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[]{idx});
			}
			if(postdate_modify.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[]{idx});
			}
			
			// 프로젝트 코드
			if (model.length() > 0) {
				
				
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + model + "%").toUpperCase()), new int[] { _idx });
				}
				
			} else {
				model = "";
			}

			// 제작방법
			if (productmethod.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + productmethod + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				productmethod = "";
			}

			// 부서
			if (deptcode.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				deptcode = "";
			}

			// 무게
			boolean searchWeight = weight1.length()> 0 || weight2.length() >0;
			//System.out.println("weight1 =" + weight1 +",weight2=" + weight2);
			if (searchWeight) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_WEIGHT);
				if (aview != null) {
					
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					query.appendOpenParen();
					
					//System.out.println("aview.getHierarchyID() =" + aview.getHierarchyID()); 
					int _idx = query.appendClassList(FloatValue.class, false);
					query.appendWhere(new SearchCondition(FloatValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(FloatValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					
					
					if(weight1.length()>0){
						query.appendAnd();
						query.appendWhere(new SearchCondition(FloatValue.class, "value", SearchCondition.GREATER_THAN_OR_EQUAL , Double.parseDouble(weight1)), new int[] { _idx });
					}
					
					if(weight2.length()>0){
						query.appendAnd();
						query.appendWhere(new SearchCondition(FloatValue.class, "value", SearchCondition.LESS_THAN_OR_EQUAL , Double.parseDouble(weight2)), new int[] { _idx });
					}
					query.appendCloseParen();
				}
			} 

			// MANUFACTURE
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + manufacture + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				manufacture = "";
			}

			// 재질
			if (mat.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MAT);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + mat + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				mat = "";
			}

			// 후처리
			if (finish.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_FINISH);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + finish + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				finish = "";
			}

			// 비고
			if (remarks.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_REMARKS);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + remarks + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				remarks = "";
			}

			// 사양
			if (specification.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_SPECIFICATION);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + specification + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				specification = "";
			}	
			
			//Folder Search
			if (location.length() > 0) {
				int l = location.indexOf(ROOTLOCATION);

				if (l >= 0) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					location = location
							.substring((l + ROOTLOCATION.length()));
					// Folder Search
					int folder_idx = query.addClassList(EpmLocation.class,
							false);
					query.appendWhere(new SearchCondition(EpmLocation.class,
							EpmLocation.EPM, EPMDocument.class,
							"thePersistInfo.theObjectIdentifier.id"),
							new int[] { folder_idx, idx });
					query.appendAnd();

					query.appendWhere(new SearchCondition(EpmLocation.class,
							"loc", SearchCondition.LIKE, location + "%"),
							new int[] { folder_idx });
				}

			}
			//CAD ����
			if(cadDivision.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , cadDivision, false), new int[]{idx});
			}
			
			//CAD Ÿ��
			if(cadType.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , cadType, false), new int[]{idx});
			}
			
			//autoCadLink
			if(autoCadLink.equals("true")){
				if(query.getConditionCount() > 0) query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "SOLIDWORKS", false), new int[]{idx});
				query.appendAnd();
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADCOMPONENT", false), new int[]{idx});
				query.appendOr();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADASSEMBLY", false), new int[]{idx});
				query.appendCloseParen();
			
			}
				
			//����
			if(sortCheck == null) sortCheck = "true";
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class,sortValue), true), new int[] { idx });
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EPMDocument.class, "iterationInfo.creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class,sortValue), false), new int[] { idx });
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EPMDocument.class, "iterationInfo.creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP), true), new int[] { idx });    
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return query;
	}
	
	@Override
	public List<Map<String,String>> cadDivisionList(){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		EPMAuthoringAppType[] appType = EPMAuthoringAppType.getEPMAuthoringAppTypeSet();
		
		for(EPMAuthoringAppType type : appType) {
			if(!type.isSelectable()) continue;
			Map<String,String> map = new HashMap<String,String>();
			map.put("value", type.toString());
			map.put("name", type.getDisplay(Message.getLocale()));
			list.add(map);
		}
		
		return list;
	}
	
	@Override
	public List<Map<String,String>> cadTypeList(){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		EPMDocumentType[] appType = EPMDocumentType.getEPMDocumentTypeSet();
		
		for(EPMDocumentType type : appType) {
			if(!type.isSelectable()) continue;
			Map<String,String> map = new HashMap<String,String>();
			map.put("value", type.toString());
			map.put("name", type.getDisplay(Message.getLocale()));
			list.add(map);
		}
		
		return list;
	}
	
	@Override
	public WTPart getWTPart(EPMDocument _epm) throws Exception {
        if (_epm == null) { return null; }
        QueryResult qr = null;
        if (VersionControlHelper.isLatestIteration(_epm))
            qr = PersistenceHelper.manager.navigate(_epm, "buildTarget", EPMBuildRule.class);
        else
            qr = PersistenceHelper.manager.navigate(_epm, "built", EPMBuildHistory.class);
        while (qr != null && qr.hasMoreElements())
            return (WTPart) qr.nextElement();

        return null;
    }
	
	/**
	 * 품목, 도면 일괄등록 (품목에서 호출)
	 * @param hash
	 * @param loc
	 * @return
	 * @throws Exception
	 */
	@Override
	public EPMDocument createEPM(Map<String,Object> hash) throws Exception {
		
		EPMDocument epm = EPMDocument.newEPMDocument();
		
		String partOid 			= StringUtil.checkNull((String) hash.get("oid")); 			
		String lifecycle 		= StringUtil.checkNull((String) hash.get("lifecycle"));
		String primaryFile 		= StringUtil.checkNull((String) hash.get("primary"));
		String descript 		= StringUtil.checkNull((String) hash.get("descript"));
		String unit				= StringUtil.checkNull((String) hash.get("unit"));
		String location				= StringUtil.checkNull((String) hash.get("location"));
		
		int dotIndex = primaryFile.lastIndexOf(".");
		
        String fileName = primaryFile.substring(0, dotIndex);
		String fileEnd = fileName.substring(dotIndex).toLowerCase();
		
		if (primaryFile.length() == 0) {
			primaryFile = "";
			throw new Exception(Message.get("파일이 존재 하지 않습니다."));
		}
		
		String applicationType = "MANUAL";
		
		String extName = "";
		String authoringType ="";
		if(primaryFile.length() > 0) {
			File file = new File(fileName);
			extName = file.getName();
			authoringType = EpmUtil.getAuthoringType(extName);
		}
		String extentionName = getPrefix(extName);
		WTPart part = (WTPart)CommonUtil.getObject(partOid);
		String number = part.getNumber() + "." + extentionName;
		String name = part.getName();
		
		epm.setNumber(number);
		epm.setName(name);
		epm.setDescription(descript); 
		EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
		epmMaster.setOwnerApplication(EPMApplicationType.toEPMApplicationType(applicationType));
		EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
		epmMaster.setAuthoringApplication(appType);
		epmMaster.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
		
		//Folder  && LifeCycle  Setting
		ReferenceFactory rf = new ReferenceFactory();
		Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		FolderHelper.assignLocation((FolderEntry) epm, folder);
		
		PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
		WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
		epm.setContainer(e3psProduct);
		LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef));
		
		String newFileName = "";
		String orgFileName = "";
		String fileDir = WTProperties.getServerProperties().getProperty("wt.temp");
		File file = null;
		File rfile = null;
		if (!primaryFile.equals("")) {
			file = new File(fileDir + File.separator + fileName);
			orgFileName = file.getAbsolutePath();
			fileName = file.getName();
			
			if(isFileNameCheck(fileName)){
				throw new Exception(Message.get("이미 등록된 파일입니다."));
            }
			newFileName = fileDir + File.separator +fileName;
			rfile = new File(newFileName);
			file.renameTo(rfile);
			file = rfile;

			epm.setCADName(fileName);
			EPMDocumentType docType = getEPMDocumentType(fileEnd);
			epm.setDocType(docType);
		}
		
		epm = (EPMDocument)PersistenceHelper.manager.save(epm);
		
		// IBA 설정
		CommonHelper.service.changeIBAValues(epm, hash);
		
		// ��÷�� ����
		if (!newFileName.equals("")) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primaryFile);
			ApplicationData applicationData = ApplicationData.newApplicationData(epm);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(epm, applicationData, vault.getPath());
		}
		
		EpmPublishUtil.publish(epm); 
		File orgfile = new File(orgFileName);
		file.renameTo(orgfile);
		return epm;
	}
	
	@Override
	public Map<String,Object> delete(String oid){
		Map<String,Object> map = new HashMap<String,Object>();
		boolean result = true;
		String msg = Message.get("삭제되었습니다.");
		try{
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				EPMDocument epm = (EPMDocument) f.getReference(oid).getObject();
				
				if(WorkInProgressHelper.isCheckedOut(epm)){
					result = false;
					msg = Message.get("체크아웃되어 있어서 삭제할 수 없습니다.");
					//return msg;	
				}
				
				EpmData data = new EpmData(epm);
				if(data.getPart() != null){
					result = false;
					msg = Message.get("도면과 연계된 품목이 존재하여 삭제할 수 없습니다.");
					//return msg;
				}
				
				List<EPMDependencyMaster> vecRef =EpmSearchHelper.service.getRef(oid);
				if(vecRef.size()>0){
					result = false;
					msg=Message.get("참조 도면이 있어서 삭제할 수 없습니다.");
					//return msg;
				}
			  
				PersistenceHelper.manager.delete(epm);
			}
		}catch(Exception e){
			e.printStackTrace();
			result = false;
			msg = Message.get("삭제 중 오류가 발생하였습니다.")+"\\n" + e.getLocalizedMessage();
			//return "삭제 중 오류가 발생하였습니다. \\n" + e.getLocalizedMessage();
		}
		
		map.put("result", result);
		map.put("msg", msg);
		
		return map;
	}
	
	@Override
	public Hashtable modify(Hashtable hash , String[] loc , String[] deloc, String[] partOid)throws Exception{
		
		Hashtable rtnVal = new Hashtable();

       Transaction trx = new Transaction();
       try {
    	    trx.start();
    	    
    	    String oid = StringUtil.checkNull((String) hash.get("oid"));
    		ReferenceFactory f = new ReferenceFactory();
    		EPMDocument epm = (EPMDocument) f.getReference(oid).getObject();

    		rtnVal = modifyEpm(epm, hash , loc , deloc, partOid);			
			
			if("F".equals(rtnVal.get("rslt"))){
            	rtnVal.put("oid", oid);
            	trx.rollback();
            	return rtnVal;
            }
			
			rtnVal.put("rslt", "S");
			rtnVal.put("msg", Message.get("수정 되었습니다."));
            if(((String)rtnVal.get("oid")) == null || ((String)rtnVal.get("oid")).length() == 0){
            	rtnVal.put("oid", hash.get("oid"));
            }
            
			trx.commit();
			trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
    	   
    	   rtnVal.put("rslt", "F");
           rtnVal.put("oid", hash.get("oid"));
           rtnVal.put("msg", e.getMessage());
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return rtnVal;
	}
	
	public Hashtable modifyEpm(EPMDocument orgEpm, Hashtable hash , String[] loc , String[] deloc, String[] partOid)throws Exception{
		Hashtable rtnVal = new Hashtable();
		
		if(orgEpm!=null){
			String location 	 = StringUtil.checkNull((String) hash.get("location")); //���� �з�
			String primary 		 = StringUtil.checkNull((String) hash.get("primary")); //�� ÷������
			String description 	 = StringUtil.checkNull((String) hash.get("description")); //����
			String iterationNote = StringUtil.checkNull((String) hash.get("iterationNote"));
			
			ReferenceFactory rf = new ReferenceFactory();
			EPMDocument newEpm = (EPMDocument)getWorkingCopy(orgEpm);
			newEpm.setDescription(description);
			
			System.out.println("newEpm :: "+newEpm);
			System.out.println("primary :: "+primary);
			newEpm = (EPMDocument) PersistenceHelper.manager.modify(newEpm);
			
			if(primary.length() > 0) {
				ContentItem item = null;
				QueryResult result = ContentHelper.service.getContentsByRole ((ContentHolder)newEpm ,ContentRoleType.PRIMARY );
				
				while (result.hasMoreElements ()) {
					item = (ContentItem) result.nextElement ();
					CommonContentHelper.service.delete(newEpm, item);
				}
				
				String fileName = primary.split("/")[1];
				String TempCadName = newEpm.getCADName();
				System.out.println("primary :: "+primary);
				System.out.println("fileName :: "+fileName);
				System.out.println("TempCadName :: "+TempCadName);
				
				if(!TempCadName.equals(fileName)){
					throw new Exception(Message.get("파일명이 다릅니다."));
	            }
				//CommonContentHelper.service.attachPrimary(newEpm, file.getAbsolutePath());
			}
			
			//관련도면 연결
            QueryResult partResults = PersistenceHelper.manager.navigate(newEpm, "describes", EPMDescribeLink.class, false);
            while ( partResults.hasMoreElements() ) {
            	EPMDescribeLink link = (EPMDescribeLink) partResults.nextElement();
                PersistenceServerHelper.manager.remove(link);
            }
			if ( partOid != null ) {
            	WTPart part = null;
                EPMDescribeLink epmLink = null;
                for ( int i = 0; i < partOid.length; i++ ) {
                	part = (WTPart) rf.getReference(partOid[i]).getObject();
                    epmLink = EPMDescribeLink.newEPMDescribeLink(part, newEpm);
                    PersistenceServerHelper.manager.insert(epmLink);
                }
            }
			
			CommonContentHelper.service.attach(newEpm, primary, loc, deloc);
			
			/*
			CommonContentHelper.service.delete(newEpm);
			if(deloc != null){
				for(int j=0; j< deloc.length; j++){
					ApplicationData ad = (ApplicationData) rf.getReference(deloc[j]).getObject();
					CommonContentHelper.service.attach(newEpm, ad, false);
				}
			}
			
			if(loc != null){
				for(int i=0; i< loc.length; i++){
					CommonContentHelper.service.attach(newEpm, loc[i] , "N");
				}
			}
			*/
			
			// Check-in
			if(WorkInProgressHelper.isCheckedOut(newEpm)){
				newEpm = (EPMDocument) WorkInProgressHelper.service.checkin(newEpm, StringUtil.checkNull(iterationNote));
			}
			
			if(location.length() > 0){
				Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
				newEpm = (EPMDocument) FolderHelper.service.changeFolder((FolderEntry) newEpm, folder);
			}
			
			/*BuildRule History*/
			EPMBuildRule buildRule=PartSearchHelper.service.getBuildRule(newEpm);
			if(buildRule !=null){
				WTPart part=(WTPart)buildRule.getBuildTarget();
				EPMBuildHistory history = EPMBuildHistory.newEPMBuildHistory(orgEpm, part, buildRule.getUniqueID(),
						buildRule.getBuildType());
				PersistenceServerHelper.manager.insert(history);
			}
			
			rtnVal.put("oid", CommonUtil.getOIDString(newEpm));
			
			if( newEpm != null ){
				EpmPublishUtil.publish(newEpm);  //��ǥ�۾�
			}
		}
		
		return rtnVal;
	}
	
	private Workable getWorkingCopy(Workable _obj) {
		try {
            if( !WorkInProgressHelper.isCheckedOut(_obj)) {
            	   
					if (!CheckInOutTaskLogic.isCheckedOut(_obj)) {
						CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_obj, CheckInOutTaskLogic.getCheckoutFolder(), "");
					}

					_obj = (Workable)WorkInProgressHelper.service.workingCopyOf(_obj);
               }
               else {
                   if(!WorkInProgressHelper.isWorkingCopy(_obj))
                       _obj = (Workable)WorkInProgressHelper.service.workingCopyOf(_obj);
               }
        }
        catch (WorkInProgressException e) {
            e.printStackTrace();
        }
        catch (WTException e) {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
           return _obj;
	}
	
	@Override
	public EPMDocument getLastEPMDocument(String number){
		
		EPMDocument epm = null;
		try{
			Class class1 = EPMDocument.class;
			
			 QuerySpec qs = new QuerySpec();
			 int i = qs.appendClassList(class1, true);
			 
			 //�ֽ� ���ͷ��̼�
			 qs.appendWhere(VersionControlHelper.getSearchCondition(class1, true), new int[] { i });
			 
			 // �ֽ� ����
			 SearchUtil.addLastVersionCondition(qs, class1, i);
			
			 qs.appendAnd();
			 qs.appendWhere(new SearchCondition(class1, EPMDocument.NUMBER, SearchCondition.EQUAL,number), new int[] { i });
			 
			 QueryResult qr = PersistenceHelper.manager.find(qs);
			 
			 while(qr.hasMoreElements()){
				 
				 Object obj[] = (Object[])qr.nextElement();
				 epm = (EPMDocument)obj[0];
				 
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		 
		return epm;
	}
	
	@Override
	public String getThumbnailSmallTag(EPMDocument epm)throws Exception{
		
		String eoid = "";
		
		ContentHolder holder = null;
		if(epm==null){
			return "<img src='"+BLANK_IMG+"'  border=0 >";
		}else{
			eoid = epm.getPersistInfo().getObjectIdentifier().toString();
			holder = PublishUtils.findRepresentable(epm);
		}
		String thum = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL);
		
		String thum_mini = FileHelper.getViewContentURLForType(holder, ContentRoleType.THUMBNAIL_SMALL);
		
		if(thum_mini==null)thum_mini= BLANK_IMG;
		Representable representable = PublishUtils.findRepresentable(epm);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
		String copyTag =  "";
		if(representation!=null){
			copyTag = PublishUtils.getRefFromObject(representation);
		}
		
		String result = "<img src='"+thum_mini+"'  border=0  ";
		if(thum!=null){
			//result += "onmouseover=\"showThum(this,'" + epm.getNumber() +"','"+thum+"','"+eoid+"','"+copyTag+"')\"";
		}
		return result += ">";
	}
	
	@Override
	public List<EpmData> include_Reference(String oid, String moduleType) throws Exception {
		List<EpmData> list = new ArrayList<EpmData>();
		if(StringUtil.checkString(oid)) {
			if("drawing".equals(moduleType)) {
				EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
				List<EPMReferenceLink> vecRef = EpmSearchHelper.service.getReferenceDependency(epm, "references");
				//System.out.println("StandardDrawingService ::: include_Refence.jsp ::: vecRef.size() = "+vecRef.size());
				for(EPMReferenceLink link : vecRef){
					EPMDocumentMaster master = (EPMDocumentMaster)link.getReferences();
					EPMDocument epmdoc = EpmSearchHelper.service.getLastEPMDocument(master);
					EpmData ref = new EpmData(epmdoc);
					ref.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));
					list.add(ref);
				}
			}
		}
		return list;
	}
	
	@Override
	   public Map<String, Object> thumbView(HttpServletRequest request) throws WTException {

	      Map<String, Object> map = new HashMap<String, Object>();

	      String oid = request.getParameter("oid");
	      EPMDocument epm = (EPMDocument) WCUtil.getObject(oid);

	      String[] sss = UIHelper.getDefaultVisualizationData(oid, false,   Locale.KOREA);
	      
	      String thum = null;

	      String publishURL = sss[17];
	      if(epm != null){
	    	  thum = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm),  ContentRoleType.THUMBNAIL);
	      }

	      if (thum == null) {
	         if (sss[17].length() == 0) {
	            thum = "/Windchill/wt/clients/images/wvs/productview_publish_288.png";
	         } else {
	            thum = "/Windchill/wt/clients/images/wvs/productview_openin_288.png";
	         }

	      }

	      if (sss[17].length() == 0) {
	         publishURL = "javascript:void(openCreoViewWVSPopup('" + CommonUtil.getOIDString(epm) + "'))";
	      } else {
	         String[] count = publishURL.split(" ");
	         if (count.length > 1) {

	            String third = count[2];
	            String fourth = count[3];
	            String fifth = count[4];
	            String sixth = count[5];
	            String sum = third + fourth + fifth + sixth;

	            publishURL = sum.substring(6, sum.length() - 6);
	         }

	      }

	      map.put("visualizationData", publishURL);
	      map.put("thumb", thum);

	      return map;
	   }
	
	@Override
	public String updateIBA(EPMDocument epm, WTPart part)throws WTException {
		
		try {
			String Material = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_MATERIAL));
			String SPEC = StringUtil.checkNull(IBAUtil.getAttrValue2(part,AttributeKey.EPMKey.IBA_SPEC));
			String Surface = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_SURFACE));
			String Subcontract = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_SUBCONTRACT));
			String Unit = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_UNIT));
			String Supplier = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_SUPPLIER));
			String Vendor = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_VENDOR));
			String Weight = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_WEIGHT));
			String Comment = StringUtil.checkNull(IBAUtil.getAttrValue2(part, AttributeKey.EPMKey.IBA_COMMENT));
			
			/*IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_MATERIAL, Material, "string");*/
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SPEC, SPEC, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SURFACE, Surface, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUBCONTRACT, Subcontract, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_UNIT, Unit, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUPPLIER, Supplier, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_VENDOR, Vendor, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_WEIGHT, Weight, "string");
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_COMMENT, Comment, "string");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String,Object> requestDrawingMapping(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			String fid 	       = StringUtil.checkNull(request.getParameter("fid")); //도면 분류
			String primary 	   = StringUtil.checkNull(request.getParameter("PRIMARY")); //주 첨부파일
			String description = StringUtil.checkNull(request.getParameter("description")); //설명
			String lifecycle   = StringUtil.checkNull(request.getParameter("lifecycle")); //라이프 사이클
			String partOid 	   = StringUtil.checkNull(request.getParameter("partOid"));   //참조부품
			String name		   = StringUtil.checkNull(request.getParameter("name"));
			String[] secondary = request.getParameterValues("SECONDARY");
			String[] partOids    = request.getParameterValues("partOid");
			
			String fileName = primary;
			if (fileName.length() > 0) {
		        fileName = fileName.split("/")[1];
				//fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
			} else { 
				fileName = ""; 
			}
			map.put("fid" , fid);
			map.put("primary" , primary);
			map.put("fileName" , fileName);
			map.put("description" , description);
			map.put("lifecycle" , lifecycle);
			map.put("partOid", partOid);
			map.put("name", name);
			map.put("partOids", partOids);
			map.put("secondary", secondary);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}

	@Override
	public ResultData createDrawing(Map<String, Object> map) {
		ResultData result = new ResultData();
		Transaction trx = new Transaction();
		try{
			trx.start();
			
			String fid			 = StringUtil.checkNull((String) map.get("fid")); //����з�
			String location		 = StringUtil.checkNull((String) map.get("location"));
			String primary		 = StringUtil.checkNull((String) map.get("primary")); //�� ÷������
			String description 	 = StringUtil.checkNull((String) map.get("description")); //����
			String lifecycle 	 = StringUtil.checkNull((String) map.get("lifecycle")); //������ ����Ŭ
			String drwName		 = StringUtil.checkNull((String) map.get("name"));
			String number 		 = StringUtil.checkNull((String) map.get("number"));
			String partToDrwOid  = StringUtil.checkNull((String) map.get("partToDrwOid"));
			
            if (primary.length() == 0) {
            	primary = "";
				throw new Exception(Message.get("파일이 존재 하지 않습니다."));
			}
            
			String applicationType = "MANUAL";
			
			String extName = "";
			
			String authoringType ="";
			if(primary.length() > 0) {
				extName = primary.split("/")[1];
				authoringType = EpmUtil.getAuthoringType(extName);
			}
			
			String extentionName = getPrefix(extName);
			String name = drwName;
			EPMDocument epm = EPMDocument.newEPMDocument();
			
			boolean partToDrwOidTemp = false;
			if(!partToDrwOid.equals("")){
				partToDrwOidTemp = true;
			}
			
			epm.setNumber(number);
			epm.setName(name);
			epm.setDescription(description); 
			
			EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType(applicationType));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
			epmMaster.setAuthoringApplication(appType);
			
			//Folder  && LifeCycle  Setting
			ReferenceFactory rf = new ReferenceFactory();
			Folder folder = null;
			if(StringUtil.checkString(fid)) {
				folder = (Folder)rf.getReference(fid).getObject();
			}else {
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			}
			FolderHelper.assignLocation((FolderEntry)epm, folder);
			
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			epm.setContainer(e3psProduct);
			LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
			
			String newFileName = "";
			String orgFileName = "";
			String fileName = "";
			String fileDir = "";
			File file = null;
			File rfile = null;
			if (primary.length() > 0) {
				String cacheId = primary.split("/")[0];
				CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);

				file = new File(cacheDs.getContentIdentity());
				orgFileName = file.getAbsolutePath();
				fileDir = file.getParent();
				
				fileName = primary.split("/")[1];
				
				if(isFileNameCheck(fileName)){
	            	throw new Exception(Message.get("중복된 파일입니다."));
	            }
				
				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();
				newFileName = fileDir + File.separator +fileName;
				rfile = new File(newFileName);
				file.renameTo(rfile);
				file = rfile;

				epm.setCADName(fileName);
				EPMDocumentType docType = getEPMDocumentType(fileEnd);
				epm.setDocType(docType);
			}
			
			epm = (EPMDocument)PersistenceHelper.manager.save(epm);
			
			// 첨부 파일
			String[] secondary = (String[])map.get("secondary");
			if(secondary != null) {
				CommonContentHelper.service.attach(epm, null, secondary);
			}else {
				CommonContentHelper.service.attach(epm, primary, secondary);
			}
			
			//EpmUtil.createRelation(epm3D, epm);
			String[] partOids = (String[])map.get("partOids");
			if(partOids != null) {
				for(String partOid : partOids) {
					WTPart part = (WTPart)CommonUtil.getObject(partOid);
					EPMDescribeLink describeLink = EPMDescribeLink.newEPMDescribeLink(part, epm);
					PersistenceServerHelper.manager.insert(describeLink);
				}
			}
			
			EpmPublishUtil.publish(epm);  //��ǥ�۾�
			File orgfile = new File(orgFileName);
			file.renameTo(orgfile);
			trx.commit();
			result.setResult(true);
			//System.out.println("--------------------EPM Document Create Success------------------------");
		}catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Map<String,String> create(Map<String,Object> hash, String[] loc){
		Map<String,String> rtnVal = new Hashtable<String,String>();
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			String fid			 = StringUtil.checkNull((String) hash.get("fid")); //����з�
			String location		 = StringUtil.checkNull((String) hash.get("location"));
			String primary		 = StringUtil.checkNull((String) hash.get("primary")); //�� ÷������
			String description 	 = StringUtil.checkNull((String) hash.get("description")); //����
			String lifecycle 	 = StringUtil.checkNull((String) hash.get("lifecycle")); //������ ����Ŭ
			String drwName		 = StringUtil.checkNull((String) hash.get("name"));
			String partToDrwOid  = StringUtil.checkNull((String) hash.get("partToDrwOid"));
			String createType  = StringUtil.checkNull((String) hash.get("createType"));
			
			String partOid 		 = StringUtil.checkNull((String) hash.get("partOid"));
			WTPart part          = new WTPart();
			
			String Material		= StringUtil.checkNull((String) hash.get("Material"));				//Material 
            String SPEC		    = StringUtil.checkNull((String) hash.get("SPEC"));				    //SPEC 
            String Surface		= StringUtil.checkNull((String) hash.get("Surface"));				//표면처리 
            String Subcontract  = StringUtil.checkNull((String) hash.get("Subcontract"));			//사급/도급
            String Unit         = StringUtil.checkNull((String) hash.get("Unit")); 			        //단위
            String Supplier		= StringUtil.checkNull((String) hash.get("Supplier"));				//Supplier 
            String Vendor		= StringUtil.checkNull((String) hash.get("Vendor"));				//Vendor 
            String Weight		= StringUtil.checkNull((String) hash.get("Weight"));				//Weight 
            String Comment	 	= StringUtil.checkNull((String) hash.get("Comment"));   			//설명
			
            /*
            String authoringType = EpmUtil.getExtension(primary);
            authoringType = authoringType.toUpperCase();
            
            if (authoringType.equals("DSN") || authoringType.equals("BRD") || authoringType.equals("ZIP")){
				authoringType = "ORCAD";
            }else if(authoringType.equals("DWG")){
            	authoringType = "ACAD";
            }else{
				authoringType = "OTHER";
			}
            */
            
            if (primary.length() == 0) {
            	primary = "";
				throw new Exception(Message.get("파일이 존재 하지 않습니다."));
			}
            
			String applicationType = "MANUAL";
			
			String extName = "";
			
			String authoringType ="";
			if(primary.length() > 0) {
				//File file = new File(primary);
				//extName = file.getName();
				//String authoringType
				extName = primary.split("/")[1];
				authoringType = EpmUtil.getAuthoringType(extName);
			}
			
			String extentionName = getPrefix(extName);
			String number = "";
			String name = "";
			EPMDocument epm = EPMDocument.newEPMDocument();
			if("0".equals(createType)){//도면 등록 신규
				name = drwName;
				
			}else if("1".equals(createType)){//도면 등록 품목
				part = (WTPart)CommonUtil.getObject(partOid);
				name = part.getName();
				number = part.getNumber();
				number = number + "." + extentionName;
				epm.setNumber(number);
			}
			
			
			boolean partToDrwOidTemp = false;
			if(!partToDrwOid.equals("")){
				partToDrwOidTemp = true;
			}
			//품목 등록
			if(partToDrwOidTemp){
				part = (WTPart)CommonUtil.getObject(partToDrwOid);
				name = part.getName();
				number = part.getNumber();
				number = number + "." + extentionName;
				epm.setNumber(number);
			}
			epm.setName(name);
			epm.setDescription(description); 
			
			EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType(applicationType));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
			epmMaster.setAuthoringApplication(appType);
			
			//Folder  && LifeCycle  Setting
			ReferenceFactory rf = new ReferenceFactory();
			/*
			Folder folder = (Folder)rf.getReference(fid).getObject();
			FolderHelper.assignLocation((FolderEntry)epm, folder);
			*/
			Folder folder = null;
			if(StringUtil.checkString(fid)) {
				folder = (Folder)rf.getReference(fid).getObject();
			}else {
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			}
			FolderHelper.assignLocation((FolderEntry)epm, folder);
			
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			epm.setContainer(e3psProduct);
			LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
			
			String newFileName = "";
			String orgFileName = "";
			String fileName = "";
			String fileDir = "";
			File file = null;
			File rfile = null;
			if (primary.length() > 0) {
				String cacheId = primary.split("/")[0];
				CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);

				file = new File(cacheDs.getContentIdentity());
				orgFileName = file.getAbsolutePath();
				fileDir = file.getParent();
				//fileName = file.getName();
				
				fileName = primary.split("/")[1];
				
				
				if(isFileNameCheck(fileName)){
	            	throw new Exception(Message.get("중복된 파일입니다."));
	            }
				
				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();
				newFileName = fileDir + File.separator +fileName;
				rfile = new File(newFileName);
				file.renameTo(rfile);
				file = rfile;

				epm.setCADName(fileName);
				EPMDocumentType docType = getEPMDocumentType(fileEnd);
				epm.setDocType(docType);
			}
			
			epm = (EPMDocument)PersistenceHelper.manager.save(epm);
			
			if("1".equals(createType) ||!"".equals(partToDrwOid)){
				//IBA Attribute
				String material = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_MATERIAL));
				String SPEC1 = StringUtil.checkNull(IBAUtil.getAttrValue(part,AttributeKey.EPMKey.IBA_SPEC));
				String surface = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SURFACE));
				String subcontract = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUBCONTRACT));
				String unit = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_UNIT));
				String supplier = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUPPLIER));
				String vendor = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_VENDOR));
				String weight = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_WEIGHT));
				String comment = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_COMMENT));
				
				if(material.length() > 0) {
	            	/*IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_MATERIAL, material, "string");*/
	            }//설계자
	            if(SPEC1.length() > 0) {
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SPEC, SPEC1, "string");
	            }//설계자
	            if(surface.length() > 0) {
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SURFACE, surface, "string");
	            }//표면처리
	            if(subcontract.length() > 0) {
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUBCONTRACT, subcontract, "string");
	            }//표면처리
	            if(unit.length() > 0 ) { 
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_UNIT, unit, "string"); 
	            }//Unit
	            if(supplier.length() > 0 ) { 
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUPPLIER, supplier, "string"); 
	            }//Unit
	            if(vendor.length() > 0 ) { 
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_VENDOR, vendor, "string"); 
	            }//Vendor
	            if(weight.length() > 0 ) { 
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_WEIGHT, weight, "string"); 
	            }//Weight
	            if(comment.length() > 0 ) { 
	            	IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_COMMENT, comment, "string"); 
	            }//Comment
			}
			//E3PSRENameObject.manager.EPMReName(epm, "", "", number, false);
			
			/*
			if (!"".equals(newFileName)) {
				CommonContentHelper.service.attachPrimary(epm, newFileName);
			}
			
			if(loc != null){
				for(int i=0; i< loc.length; i++){
					CommonContentHelper.service.attach(epm, loc[i], "N");
				}
			}
			*/
			CommonContentHelper.service.attach(epm, primary, loc);
			
			//EpmUtil.createRelation(epm3D, epm);
			if("1".equals(createType) ||!"".equals(partToDrwOid)){
				EPMDescribeLink describeLink = EPMDescribeLink.newEPMDescribeLink(part, epm);
				PersistenceServerHelper.manager.insert(describeLink);
			}
			EpmPublishUtil.publish(epm);  //��ǥ�۾�
			File orgfile = new File(orgFileName);
			file.renameTo(orgfile);
			
			rtnVal.put("rslt", "S");
            rtnVal.put("msg", Message.get("등록 되었습니다."));
            rtnVal.put("oid", CommonUtil.getOIDString(epm));
		     
			trx.commit();
			trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
           rtnVal.put("rslt", "F");
           rtnVal.put("msg", e.getMessage());
           rtnVal.put("oid", "");
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return rtnVal;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public List<EpmData> include_drawingLink(String module, String oid) {
		List<EpmData> list = new ArrayList<EpmData>();
		
		try {
			if(StringUtil.checkString(oid)){
	    		if("active".equals(module)) {
	        		devActive m = (devActive)CommonUtil.getObject(oid);
	        		QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class);
	        		
	        		while(qr.hasMoreElements()){ 
	            		Object p = (Object)qr.nextElement();
	            		if(p instanceof EPMDocument) {
	            			EpmData data = new EpmData((EPMDocument)p);
	                		list.add(data);
	            		}
	        		}
	    		}
	    	}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public ResultData linkDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
    	Transaction trx = new Transaction();
    	
    	String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
    	String drawingOid = StringUtil.checkNull(request.getParameter("drawingOid"));
    	String type = StringUtil.checkNull(request.getParameter("type"));
    	
    	try {
    		trx.start();
	    	if("active".equals(type)) {
	    		devActive active = (devActive)CommonUtil.getObject(parentOid);
	    		EPMDocument doc = (EPMDocument)CommonUtil.getObject(drawingOid);
	    		
	    		devOutPutLink link = devOutPutLink.newdevOutPutLink(active, doc);
	    		PersistenceServerHelper.manager.insert(link);
	    	}
	    	trx.commit();
	    	trx = null;
	    	data.setResult(true);
    	} catch(Exception e) {
    		e.printStackTrace();
    		data.setResult(false);
    		data.setMessage(e.getLocalizedMessage());
    	} finally {
    		if(trx != null){
    			trx.rollback();
    		}
    	}
    	return data;
	}
	
	@Override
	public ResultData deleteDrawingLinkAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
    	Transaction trx = new Transaction();
    	
    	String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
    	String drawingOid = StringUtil.checkNull(request.getParameter("drawingOid"));
    	String type = StringUtil.checkNull(request.getParameter("type"));
    	
    	try {
    		trx.start();
    		
    		if("active".equals(type)) {
        		QuerySpec spec = DocumentQueryHelper.service.devActiveLinkDocument(parentOid, drawingOid);
        		QueryResult result = PersistenceHelper.manager.find(spec);
        		if(result.hasMoreElements()) {
        			Object[] o = (Object[]) result.nextElement();
        			devOutPutLink link = (devOutPutLink)o[0];
        			PersistenceHelper.manager.delete(link);
        		}
	    	}
    		
	    	trx.commit();
	    	trx = null;
	    	data.setResult(true);
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    		data.setResult(false);
    		data.setMessage(e.getLocalizedMessage());
    	}
    	
    	return data;
    }
	
	@Override
	public ResultData updateNameAction(HttpServletRequest request, HttpServletResponse response){
		ResultData data = new ResultData();
		
		try{
			String oid = request.getParameter("oid");
			EPMDocument epm2D = (EPMDocument)CommonUtil.getObject(oid);
			String epm2DNumber = EpmUtil.getNumberNonExtension(epm2D.getNumber());
			EPMDocument epm3D = EpmSearchHelper.service.getDrawingToCad(epm2DNumber);
			
			E3PSRENameObject.manager.EPMReName(epm2D, "", epm3D.getName(), "", false);
			String msg = Message.get("도면명 동기화 완료 하였습니다.");
			data.setMessage(msg);
			data.setOid(oid);
			data.setResult(true);
		}catch(Exception e){
			e.printStackTrace();
			String msg = e.getLocalizedMessage();
			data.setResult(false);
			data.setMessage(msg);
		}
		return data;
	}
	
	@Override
	public String createPackageDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		Transaction trx = new Transaction();
				
		Map<String,String> fileMap = new HashMap<String,String>();
		int failCount = 0;
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		try {
			trx.start();
			
			FileRequest req = new FileRequest(request);
			String excelFile = req.getFileLocation("excelFile");
			
			File file = new File(excelFile);
			XSSFWorkbook workbook = POIUtil.getWorkBook(file);
			XSSFSheet sheet = POIUtil.getSheet(workbook, 0);
			
			String[] secondary = req.getParameterValues("SECONDARY");
			if(secondary != null) {
				for(String attachFile : secondary) {
			        String fileName = attachFile.split("/")[1].toUpperCase();
			        if(fileMap.get(fileName) == null){
			        	fileMap.put(fileName, attachFile);
			        }else {
			        	fileMap.remove(fileName);
			        }
				}
			}
			
			for(int i = 1; i < POIUtil.getSheetRow(sheet); i++) {
				
				XSSFRow row = sheet.getRow(i);
				boolean validation = true;
				String fail = "";
				String fontColor = "black";
				
				// 순번
				String no = StringUtil.checkNull(POIUtil.getRowStringValue(row,0));
				
				if(no.length() > 0) {
					
					// 부품코드
					String partNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 1));
					WTPart part = null;
					String partOid = "";
					if(partNumber.length() == 0) {
						validation = false;
						fail += Message.get("부품코드가 입력되지 않았습니다.");
					}else {
						part = PartHelper.service.getPart(partNumber);
						if(part == null){
							validation = false;
							fail += Message.get("부품이 존재하지 않습니다.");
						}else {
							if((State.INWORK).equals(part.getLifeCycleState())) {
								partOid = CommonUtil.getOIDString(part);
							}else {
								validation = false;
								fail += Message.get("부품이 작업중 상태가 아닙니다.");
							}
						}
					}
					
					// 주도면 여부
					EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
					String mainEpm = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
					String primary = "";
					if (epm != null){
						validation = false;
        				fail = Message.get("주 도면이 존재합니다.");
					} else {
						// 주 도면
	            		if(mainEpm.length() > 0) {
	            			primary = StringUtil.checkNull((String)fileMap.get(mainEpm.toUpperCase()));
	            			if(primary.length() == 0) {
	            				validation = false;
	            				fail = Message.get("주 도면이 첨부되지 않았습니다.");
	            			}else {
	            				fileMap.remove(mainEpm.toUpperCase());
	            			}
	            		}
					}
            		
            		if(validation) {
            			try {
            				Map<String,Object> epmMap = new HashMap<String,Object>();
            				
            				String location = part.getLocation();
            				Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
            				String epmfid = CommonUtil.getOIDString(folder);
            				
            				String unit = part.getDefaultUnit().toString();
            				
            				epmMap.put("epmfid", epmfid);
            				epmMap.put("oid", partOid);
            				epmMap.put("lifecycle", "LC_PART");
            				epmMap.put("primary", primary);
            				epmMap.put("unit", unit);
            				
            				EPMDocument main = DrawingHelper.service.createEPM(epmMap);
            				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(main, part);
            				PersistenceServerHelper.manager.insert(link);
            				
            				CommonHelper.service.copyIBAAttributes(part, main);
            				
            			} catch(Exception e) {
            				e.printStackTrace();
            				fail = e.getLocalizedMessage();
            				failCount++;
            				fontColor = "red";
            				validation = false;
            			}
            		}else {
            			failCount++;
        				fontColor = "red";
        				validation = false;
            		}

					xmlBuf.append("<row id='" + no + "'>");
					xmlBuf.append("<cell><![CDATA[" + (i+1) + "]]></cell>");
					xmlBuf.append("<cell><![CDATA[" + partNumber + "]]></cell>");
					xmlBuf.append("<cell><![CDATA[" + mainEpm + "]]></cell>");
					xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + validation + "</font>]]></cell>");
					xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + fail + "</font>]]></cell>");
					xmlBuf.append("</row>");
				}
			}
			
			if(failCount == 0) {
				trx.commit();
				trx = null;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			xmlBuf.append("<row id='error'>");
			xmlBuf.append("<cell colspan='5'><![CDATA[<font color='red'>" + e.getLocalizedMessage() + "</font>]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		xmlBuf.append("</rows>");
		
		return xmlBuf.toString();
	}
	//PJT EDIT 20161130
	@Override
	public void partTreeDrawingDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String baselineOid = request.getParameter("baseline"); 
		String viewName = request.getParameter("viewName");
		String desc = request.getParameter("desc");
		Baseline baseline = null;
		if (baselineOid != null)
			baseline = (Baseline) CommonUtil.getObject(baselineOid);
		//System.out.println("partTreeDrawingDown 실행 ");
		View view = null;
		if (viewName != null)
			view = ViewHelper.service.getView(viewName);
		
		Object obj = baseline == null ? null : baseline;
		if (obj == null)
			obj = view == null ? null : view;

		WTPart part = (WTPart)CommonUtil.getObject(oid);
		if (baseline != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=", baseline.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=", part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}
		String fileName = part.getNumber()+"_BOM_Drawing";
		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		Vector<ApplicationData> vecApp =new Vector<ApplicationData>();
		PartTreeData root = null;
		if(obj instanceof Baseline){
			root = broker.getTree(part, !"false".equals(desc), (Baseline) obj, null);
		}else{
			root = broker.getTree(part, !"false".equals(desc), null, (View) obj);
		}
		broker.setHtmlForm(root, result);
		//System.out.println("add appData result =" +result.size());
		String appType = null;
		for(int i=0; i< result.size(); i++){
			PartTreeData pData = (PartTreeData)result.get(i);
		    WTPart pPart = pData.part;
		    
		    if(PartUtil.isChange(pPart.getNumber())){
		    	continue;
		    }
		    
		    
		    
			EPMDocument epm = DrawingHelper.service.getEPMDocument(pPart);
			if(null!=epm){
				EpmData epmData = new EpmData(epm);
				appType = epmData.getApplicationType();
				//System.out.println("appType="+appType);
				if("MANUAL".equals(appType)){
					
					addPrimaryList(vecApp, epm);
					
				}else{
					//참조 항목 Start
					List<EpmData> list2 = new ArrayList<EpmData>();
						List<EPMReferenceLink> refList = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
						for(EPMReferenceLink link : refList) {
							EPMDocument epmdoc = link.getReferencedBy();
							EpmData data = new EpmData(epmdoc);
							
							data.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));
							
							list2.add(data);
						}
					//System.out.println("EPM 일 경우 list2 Size="+list2.size());
					for (int j = 0; j < list2.size(); j++) {
						EpmData referencEpmdata = (EpmData)list2.get(j);
						EPMDocument referencEpm =referencEpmdata.epm;
						addSecondList(vecApp, referencEpm, true);
					}
					//참조 항목
				}
			}
		}
		
		
		if(vecApp.size()>0){
			ArrayList<String> list = getList(vecApp);
			MakeZIPUtil.drawingSaveZip(vecApp, fileName,list,response);
		}else{
			 response.setContentType("text/html; charset=UTF-8");   //한글설정
			    request.setCharacterEncoding("utf-8");                        //한글설정
			    PrintWriter writer = response.getWriter(); 

			     writer.println("<script type='text/javascript'>");                    
			     writer.println("function window::onload(){alert('도면이 없습니다.');"     //alert() 호출
			       + "history.back();}");                                                                           //다른 자바스크립트 메서드 호출
			     writer.println("</script>");
			    writer.flush();
		}
		
	}

	private ArrayList<String> getList(Vector<ApplicationData> vecApp) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < vecApp.size(); i++) {
			ApplicationData applicationData = (ApplicationData) vecApp.get(i);
			list.add(applicationData.getFileName());
		}
		return list;
	}

	private void addPrimaryList(Vector<ApplicationData> vecApp, EPMDocument epm) throws WTException, PropertyVetoException {
		ContentHolder holder = ContentHelper.service.getContents((ContentHolder)epm);
		
		WTUser user = (WTUser)SessionHelper.getPrincipal();
		Hashtable hash = new Hashtable();
		hash.put("dOid", CommonUtil.getOIDString(epm));
		hash.put("userId", user.getFullName());
		try {
			DownloadHistoryHelper.service.createDownloadHistory(hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ContentRoleType roleType = ContentRoleType.PRIMARY ;
		QueryResult qr = ContentHelper.service.getContentsByRole (holder ,roleType );
		while(qr.hasMoreElements()) {
			ContentItem item = (ContentItem) qr.nextElement ();
			if(item != null) {
				ApplicationData appData = (ApplicationData)item;
				//System.out.println("add appData Name =" +appData.getFileName());
				if("{$CAD_NAME}".equals(appData.getFileName())){
					EPMDocument epms = (EPMDocument)holder;
					appData.setFileName(epms.getCADName());
				}
				//System.out.println("add appData Name =" +appData.getFileName());
				vecApp.add(appData);
			}
		}
	}
	private void addSecondList(Vector<ApplicationData> vecApp, EPMDocument epm,boolean isCheck) throws WTException, PropertyVetoException {
		ContentHolder holder = ContentHelper.service.getContents((ContentHolder)epm);
		
		WTUser user = (WTUser)SessionHelper.getPrincipal();
		Hashtable hash = new Hashtable();
		hash.put("dOid", CommonUtil.getOIDString(epm));
		hash.put("userId", user.getFullName());
		try {
			DownloadHistoryHelper.service.createDownloadHistory(hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ContentRoleType roleType = ContentRoleType.SECONDARY;
		QueryResult qr = ContentHelper.service.getContentsByRole (holder ,roleType );
		if(isCheck){
			/*while(qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement ();
				if(item != null) {
					ApplicationData appData = (ApplicationData)item;
					if("{$CAD_NAME}".equals(appData.getFileName())){
						EPMDocument epms = (EPMDocument)holder;
						appData.setFileName(epms.getCADName());
						System.out.println("addSecondList ::: isCheck = "+isCheck+"\tadd appData Name =" +appData.getFileName());
					}
					if(appData.getFileName().lastIndexOf("dxf")>0){
						System.out.println("addSecondList ::: isCheck = "+isCheck+"\tadd After appData Name =" +appData.getFileName());
					}
					//vecApp.add(appData);
				}
			}*/
			Representation representation = PublishUtils.getRepresentation(epm); 
			//System.out.println("representation="+(null!=representation));
			if(null!=representation){
			representation = (Representation) ContentHelper.service.getContents(representation);
		        Vector contentList = ContentHelper.getContentList(representation);
		        for (int l = 0; l < contentList.size(); l++) {
		            ContentItem contentitem = (ContentItem) contentList.elementAt(l);
		            if( contentitem instanceof ApplicationData){
		            	ApplicationData drawAppData = (ApplicationData) contentitem;
		            	if(drawAppData.getRole().toString().equals("SECONDARY") && drawAppData.getFileName().lastIndexOf("dxf")>0){
		            		//System.out.println("addSecondList ::: isCheck = "+isCheck+"\tadd appData Name =" +drawAppData.getFileName());
		            		vecApp.add(drawAppData);
		            	}
		            }
		        }	
			}
		}else{
			while(qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement ();
				if(item != null) {
					ApplicationData appData = (ApplicationData)item;
					//System.out.println("addSecondList ::: isCheck = "+isCheck+"\tadd appData Name =" +appData.getFileName());
					if("{$CAD_NAME}".equals(appData.getFileName())){
						EPMDocument epms = (EPMDocument)holder;
						appData.setFileName(epms.getCADName());
					}
					//System.out.println("addSecondList ::: isCheck = "+isCheck+"\tadd appData Name =" +appData.getFileName());
					vecApp.add(appData);
				}
			}
		}
	}

	@Override
	public void create(Map<String, Object> map) throws Exception {
		Transaction trs = new Transaction();
		try{
			trs.start();
			
			String fid			 = StringUtil.checkNull((String) map.get("fid"));
			String location		 = StringUtil.checkNull((String) map.get("location"));
			String primary		 = StringUtil.checkNull((String) map.get("primary"));
			String description 	 = StringUtil.checkNull((String) map.get("description"));
			String lifecycle 	 = StringUtil.checkNull((String) map.get("lifecycle"));
			String drwName		 = StringUtil.checkNull((String) map.get("name"));
			String number 		 = StringUtil.checkNull((String) map.get("number"));
			String partToDrwOid  = StringUtil.checkNull((String) map.get("partToDrwOid"));
			
			
			
			
//			if (primary.length() == 0) {
//            	primary = "";
//				throw new Exception(Message.get("파일이 존재 하지 않습니다."));
//			}
//            
//			String applicationType = "MANUAL";
//			
//			String extName = "";
//			
//			String authoringType ="";
//			if(primary.length() > 0) {
//				extName = primary.split("/")[1];
//				authoringType = EpmUtil.getAuthoringType(extName);
//			}
//			
//			String extentionName = getPrefix(extName);
			EPMDocument epm = EPMDocument.newEPMDocument();
			
//			boolean partToDrwOidTemp = false;
//			if(!partToDrwOid.equals("")){
//				partToDrwOidTemp = true;
//			}
			
			epm.setNumber(number);
			epm.setName(drwName);
			epm.setDescription(description); 
			// 수정필요
			EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType("CATIAV5");
			epmMaster.setAuthoringApplication(appType);
			epmMaster.setCADName(drwName);
			EPMDocumentType documetType = EPMDocumentType.toEPMDocumentType("CADCOMPONENT");
			epmMaster.setDocType(documetType);
			System.out.println("setDocType      : " + epmMaster.getDocType());
			
			
			//Folder  && LifeCycle  Setting
			ReferenceFactory rf = new ReferenceFactory();
			Folder folder = null;
			if(StringUtil.checkString(fid)) {
				folder = (Folder)rf.getReference(fid).getObject();
			}else {
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			}
			FolderHelper.assignLocation((FolderEntry)epm, folder);
			
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			epm.setContainer(e3psProduct);
			LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
			
			String newFileName = "";
			String orgFileName = "";
			String fileName = "";
			String fileDir = "";
			File file = null;
			File rfile = null;
			if (primary.length() > 0) {
				String cacheId = primary.split("/")[0];
				CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);

				
				file = new File(cacheDs.getContentIdentity());
				orgFileName = file.getAbsolutePath();
				fileDir = file.getParent();
				System.out.println("primary.split1                   : "+primary.split("\\\\").length);
				System.out.println("primary.spl2                   : "+file);
				fileName = primary.split("/")[1];
				
//				file = CommonContentHelper.manager.getFileFromCacheId(cacheId);
//				orgFileName = file.getAbsolutePath();
//				fileDir = file.getParent();
//				fileName = file.getName();
				
				
				if(isFileNameCheck(fileName)){
	            	throw new Exception(Message.get("중복된 파일입니다."));
	            }
				
				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();
				newFileName = fileDir + File.separator +fileName;
				rfile = new File(newFileName);
				file.renameTo(rfile);
				file = rfile;

				epm.setCADName(fileName);
				EPMDocumentType docType = getEPMDocumentType(fileEnd);
				epm.setDocType(docType);
			}
			epm = (EPMDocument)PersistenceHelper.manager.save(epm);
			
			// 첨부 파일
			String[] secondary = (String[])map.get("secondary");
			if(secondary != null) {
				CommonContentHelper.service.attach(epm, null, secondary);
			}else {
				CommonContentHelper.service.attach(epm, primary, secondary);
			}
			
			//EpmUtil.createRelation(epm3D, epm);
			String[] partOids = (String[])map.get("partOids");
			if(partOids != null) {
				for(String partOid : partOids) {
					WTPart part = (WTPart)CommonUtil.getObject(partOid);
					EPMDescribeLink describeLink = EPMDescribeLink.newEPMDescribeLink(part, epm);
					PersistenceServerHelper.manager.insert(describeLink);
				}
			}
			
			System.out.println("orgFileName       :   "+orgFileName);
			EpmPublishUtil.publish(epm);  //��ǥ�۾�
			File orgfile = new File(orgFileName);
			file.renameTo(orgfile);
			
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
		
	}

	@Override
	public List<EpmData> include_DrawingList(String oid, String moduleType, String epmType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void batch(ArrayList<Map<String, Object>> gridData) throws Exception {
		Transaction trs = new Transaction();
		try{
			trs.start();
			for (Map<String, Object> data : gridData) {
				String primary = (String) data.get("primary");
				String primaryName = (String) data.get("primaryName");
				int dotIndex = primaryName.lastIndexOf(".");
		        String fileName = primaryName.substring(0, dotIndex);
				String fileEnd = fileName.substring(dotIndex).toLowerCase();
				String extName = "";
				String authoringType ="";
				if (StringUtil.checkString(primary)) {
					File file = new File(fileName);
					extName = file.getName();
					authoringType = EpmUtil.getAuthoringType(extName);
				}
				String extentionName = getPrefix(extName);
				// 품목
				ArrayList<Map<String, Object>> rows91 = (ArrayList<Map<String, Object>>) data.get("rows91");
				for (Map<String, Object> row : rows91) {
					String partOid = (String) row.get("part_oid");
					WTPart part = (WTPart) CommonUtil.getObject(partOid);
					String number = part.getNumber() + "." + extentionName;
					String name = part.getName();
					String unit = part.getDefaultUnit().toString();
					String location = part.getLocation();
					
					String lifecycle = "LC_PART";
					String applicationType = "MANUAL";
					
					EPMDocument epm = EPMDocument.newEPMDocument();
					epm.setNumber(number);
					epm.setName(name);
					EPMDocumentMaster epmMaster = (EPMDocumentMaster)epm.getMaster();
					epmMaster.setOwnerApplication(EPMApplicationType.toEPMApplicationType(applicationType));
					EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
					epmMaster.setAuthoringApplication(appType);
					epmMaster.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
					
					//Folder  && LifeCycle  Setting
					ReferenceFactory rf = new ReferenceFactory();
					Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
					FolderHelper.assignLocation((FolderEntry) epm, folder);
					
					PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
					WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
					epm.setContainer(e3psProduct);
					LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef));
					
					String newFileName = "";
					String orgFileName = "";
					String fileDir = WTProperties.getServerProperties().getProperty("wt.temp");
					File file = null;
					File rfile = null;
					if (StringUtil.checkString(primary)) {
						file = new File(fileDir + File.separator + fileName);
						orgFileName = file.getAbsolutePath();
						fileName = file.getName();
						
						if(isFileNameCheck(fileName)){
							throw new Exception(Message.get("이미 등록된 파일입니다."));
			            }
						newFileName = fileDir + File.separator +fileName;
						rfile = new File(newFileName);
						file.renameTo(rfile);
						file = rfile;

						epm.setCADName(fileName);
						EPMDocumentType docType = getEPMDocumentType(fileEnd);
						epm.setDocType(docType);
					}
					epm = (EPMDocument)PersistenceHelper.manager.save(epm);
					
					if (!newFileName.equals("")) {
						File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
						ApplicationData applicationData = ApplicationData.newApplicationData(epm);
						applicationData.setRole(ContentRoleType.PRIMARY);
						PersistenceHelper.manager.save(applicationData);
						ContentServerHelper.service.updateContent(epm, applicationData, vault.getPath());
					}
					
					EpmPublishUtil.publish(epm); 
					File orgfile = new File(orgFileName);
					file.renameTo(orgfile);
					
					EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
    				PersistenceServerHelper.manager.insert(link);
    				
    				CommonHelper.service.copyIBAAttributes(part, epm);
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}
}
