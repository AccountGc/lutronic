package com.e3ps.common.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.development.devActive;
import com.e3ps.development.beans.DevActiveData;
import com.e3ps.development.service.DevelopmentQueryHelper;
import com.e3ps.distribute.util.MakeZIPUtil;
import com.e3ps.doc.key.DocKey;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsUtil;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

@SuppressWarnings("serial")
public class StandardCommonService extends StandardManager implements CommonService {

	public static StandardCommonService newStandardCommonService() throws Exception {
		final StandardCommonService instance = new StandardCommonService();
		instance.initialize();
		return instance;
	}
	
	/**
	 * 
	 * 
	 * 		lutronic 추가
	 * 
	 */
	
	@Override
	public List<Map<String,String>> include_MyDevelopment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		QuerySpec query = DevelopmentQueryHelper.service.listMyDevelopmentSearchQuery(request, response);
		QueryResult qr = PageQueryBroker.openPagingSession(0, 10, query, true);
		
		while(qr.hasMoreElements()) {
			Object[] o = (Object[])qr.nextElement();
			devActive active = (devActive)o[0];
			DevActiveData data = new DevActiveData(active);
			
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("masterOid", data.master.toString());
			map.put("masterName", data.masterName);
			map.put("activityOid", data.oid);
			map.put("activityName", data.name);
			map.put("activeDate", data.activeDate);
			
			list.add(map);
		}
		
		return list;
	}
	
	@Override
	public Map<String,String> setRequestParamToMap(HttpServletRequest request) {
		Map<String,String> map = new HashMap<String,String>();
		
		Enumeration<String> attNames = request.getParameterNames();
		
		while(attNames.hasMoreElements()) {
			String name = (String)attNames.nextElement();
			String key = request.getParameter(name);
			
			map.put(name, key);
		}
		
		return map;
	}
	
	public void changeIBAValues(IBAHolder ibaHolder, Map<String,Object> map) throws Exception {

		// 무게
		String weight = (String)map.get("weight");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_WEIGHT, weight, "float");

		if(ibaHolder instanceof EPMDocument) {
			// 재질 - CAD
			String mat = (String)map.get("mat");
			String matName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MAT, mat);
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MAT, matName, "string");
			
			// 후처리 - CAD
			String finish = (String)map.get("finish");
			String finishName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_FINISH, finish);
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_FINISH, finishName, "string");
			
		}else {
			
			// 재질 - CAD
			String mat = (String)map.get("mat");
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MAT, mat, "string");
			
			// 후처리 - CAD
			String finish = (String)map.get("finish");
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_FINISH, finish, "string");
		}
		
		// 프로젝트 코드
		String model = (String)map.get("model");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MODEL, model, "string");
		
		// 비고
		String remarks = (String)map.get("remarks");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_REMARKS, remarks, "string");
		
		// 사양
		String specification = (String)map.get("specification");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_SPECIFICATION, specification, "string");
		
		// MANUFACTURE
		String manufacture = (String)map.get("manufacture");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture, "string");
		
		// 부서
		String deptcode = (String)map.get("deptcode");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode, "string");
		
		// 제작방법
		String productmethod = (String)map.get("productmethod");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod, "string");
		
		// 내부 문서 번호
		String interalnumber = (String)map.get("interalnumber");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_INTERALNUMBER, interalnumber, "string");
		
		// 보존 기간
		String preseration = (String)map.get("preseration");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PRESERATION, preseration, "string");
		
		//금형타입
		String moldType = (String)map.get("moldType");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MOLDTYPE, moldType, "string");
		
		//결재타입
		String approvalType = (String)map.get("approvalType");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_APPROVALTYPE, approvalType, "string");
		
		//외부 금형 번호
		String moldNumber = (String)map.get("moldNumber");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MOLDNUMBER, moldNumber, "string");
		
		//금형 개발비
		String moldCost = (String)map.get("moldCost");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MOLDCOST, moldCost, "string");
		
		//작성자
		String writer = (String)map.get("writer");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_DSGN, writer, "string");
		
		//partName1
		String partName1 = (String)map.get("partName1");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PARTNAME1, partName1, "string");
				
		//partName2
		String partName2 = (String)map.get("partName2");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PARTNAME2, partName2, "string");
		
		//partName3
		String partName3 = (String)map.get("partName3");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PARTNAME3, partName3, "string");
		
		//partName4
		String partName4 = (String)map.get("partName4");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PARTNAME4, partName4, "string");
		
		//documentName1
		String documentName = (String)map.get("documentName");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_DOCUMENTNAME1, documentName, "string");
		
		//documentName2
		String docName = (String)map.get("docName");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_DOCUMENTNAME2, docName, "string");
	}
	
	/**
	 * DES,무게 ,재질은 Instance속성
	 */
	@Override
	public void copyInstanceIBA(IBAHolder ibaHolder, Map<String,Object> map) throws Exception {
		
		if(ibaHolder instanceof EPMDocument) {
			
			// 재질 - CAD
			String mat = (String)map.get("mat");
			String matName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MAT, mat);
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MAT, matName, "string");
			
			// 후처리 - CAD
			String finish = (String)map.get("finish");
			String finishName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_FINISH, finish);
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_FINISH, finishName, "string");
			
		}else {
			
			// 재질 - CAD
			String mat = (String)map.get("mat");
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MAT, mat, "string");
			
			// 후처리 - CAD
			String finish = (String)map.get("finish");
			IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_FINISH, finish, "string");
		}
		
		// 프로젝트 코드
		String model = (String)map.get("model");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MODEL, model, "string");
		
		// 비고
		String remarks = (String)map.get("remarks");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_REMARKS, remarks, "string");
		
		// 사양
		String specification = (String)map.get("specification");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_SPECIFICATION, specification, "string");
		
		// MANUFACTURE
		String manufacture = (String)map.get("manufacture");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture, "string");
		
		// 부서
		String deptcode = (String)map.get("deptcode");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode, "string");
		
		// 제작방법
		String productmethod = (String)map.get("productmethod");
		IBAUtil.changeIBAValue(ibaHolder, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod, "string");
				
				
	}
	
	@Override
	public List<Map<String,String>> documentTypeList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		String documentType = StringUtil.checkNull(request.getParameter("documentType"));
		String searchType = StringUtil.checkNull(request.getParameter("searchType"));

		if(searchType.length() > 0){
			/*
			String[] approvalType = {"$$NDDocument","$$MMDocument","$$ROHS"};
			for(String type : approvalType) {
				Map<String,String> map = new HashMap<String,String>();
				DocumentType documentTypes = DocumentType.toDocumentType(type);
				map.put("code", documentTypes.toString());
				map.put("name", documentTypes.getDisplay(Locale.getDefault()));
				list.add(map);
			}
			*/
			Map<String,String> map = new HashMap<String,String>();
			if("document".equals(searchType)) {
				DocumentType documentTypes = DocumentType.toDocumentType("$$NDDocument");
				map.put("code", documentTypes.toString());
				map.put("name", documentTypes.getDisplay(Message.getLocale()));
				list.add(map);
			}else if("MOLD".equals(searchType)) {
				DocumentType documentTypes = DocumentType.toDocumentType("$$MMDocument");
				map.put("code", documentTypes.toString());
				map.put("name", documentTypes.getDisplay(Message.getLocale()));
				list.add(map);
			}
		}else {
			DocumentType[] documentTypes = DocumentType.getDocumentTypeSet();
			for(DocumentType type : documentTypes) {
				Map<String,String> map = new HashMap<String,String>();
				
				for(String docType : DocKey.docTypeKey) {
					String code = type.toString();
					if(docType.equals(code)) {
						String name = type.getDisplay(Message.getLocale());
						map.put("code", code);
						map.put("name", name);
						if("ROHS".equals(documentType.toUpperCase()) && "$$ROHS".equals(code)) {
							list.add(map);
						}else if("MOLD".equals(documentType.toUpperCase()) && "$$MMDocument".equals(code)) {
							list.add(map);
						}else if("GENERAL".equals(documentType.toUpperCase()) && !("$$ROHS".equals(code) || "$$MMDocument".equals(code))) {
							list.add(map);
						}
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public void createLoginHistoty() {
		Transaction trx = new Transaction();
		try{
			trx.start();
			
			//id, �̸�, �μ�, �ֱ����ӽð�
			String userName = "";
			String userId = "";
			String loginTime = "";
			
			
			WTUser user = (WTUser)SessionHelper.getPrincipal();
			userName = user.getFullName();
			userId = user.getName();
			
			long time = System.currentTimeMillis(); 
			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			loginTime = dayTime.format(new Date(time));
			
			String dateTime = DateUtil.getToDay("yyyy-MM-dd hh:mm:ss");
			
			LoginHistory login = LoginHistory.newLoginHistory();
			
			login.setName(userName);
			login.setId(userId);
			login.setConTime(loginTime);
			
			login = (LoginHistory)PersistenceHelper.manager.save(login);
			
			trx.commit();
           trx = null;
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
          if(trx!=null){
               trx.rollback();
          	}
		}
	}
	
	@Override
	public Map<String,Object> downloadHistory(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(DownloadHistory.class, true);
		SearchCondition sc = new SearchCondition(DownloadHistory.class, DownloadHistory.D_OID, "=", oid);
		qs.appendWhere(sc, new int[]{idx});
		qs.appendOrderBy(new OrderBy(new ClassAttribute(DownloadHistory.class,"thePersistInfo.createStamp"), false), new int[] { idx });  
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
			Object obj[] = (Object[])qr.nextElement();
			DownloadHistory history = (DownloadHistory)obj[0];
			WTUser user = history.getUser();
			String downTime = DateUtil.getTimeFormat(history.getPersistInfo().getModifyStamp(), "yyyy-MM-dd HH:mm:ss");
			PeopleData pd = new PeopleData(user);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("dept", pd.departmentName);
			map.put("duty", pd.duty);
			map.put("name", pd.name);
			map.put("time", downTime);
			map.put("count", history.getDCount());
			
			list.add(map);
			result.put("list", list);
		}
		
		return result;
	}
	@Override
    public Map<String,String> getAttributes(String oid) throws Exception {
		return getAttributes(oid, "");
	}
	@Override
    public Map<String,String> getAttributes(String oid, String mode) throws Exception {
    	IBAHolder iba = (IBAHolder)CommonUtil.getObject(oid);
    	
    	//IBA Attribute
    	String specification = StringUtil.checkNull(IBAUtil.getAttrValue2(iba, AttributeKey.IBAKey.IBA_SPECIFICATION));
    	String weight = StringUtil.checkNull(IBAUtil.getAttrfloatValue(iba, AttributeKey.IBAKey.IBA_WEIGHT));
    	String manufacture = "";
    	//System.out.println("oid ="+oid+"\tCheck ="+(oid.toUpperCase().contains("PART")));
    	if(oid.toUpperCase().contains("PART"))
    		manufacture = StringUtil.checkNull(IBAUtil.getAttrValue_Part_Desc(iba, AttributeKey.IBAKey.IBA_MANUFACTURE));
    	else
            manufacture = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MANUFACTURE));
    	String mat = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MAT));
    	String finish = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_FINISH));
    	String remarks = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_REMARKS));
    	String deptcode = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DEPTCODE));
    	String model = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MODEL));
    	String productmethod = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_PRODUCTMETHOD));
		String interalnumber = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_INTERALNUMBER));
		String preseration = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_PRESERATION));
    	String moldtype = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDTYPE));
    	String moldNumber = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDNUMBER));
    	String moldCost = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_MOLDCOST));
    	String writer = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DSGN));
    	
    	String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_ECONO));
    	String ecoDate = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_ECODATE));
    	String chk = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHK));
    	String apr = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_APR));
    	String rev = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_REV));
    	String des = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_DES));
    	String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHANGENO));
    	String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.IBAKey.IBA_CHANGEDATE));
    	
    	String unit = "";
    	if(iba instanceof WTPart) {
    		WTPart part = (WTPart)iba;
    		unit = part.getDefaultUnit().toString();
    	}else if(iba instanceof EPMDocument) {
    		//unit = StringUtil.checkNull(IBAUtil.getAttrValue(iba, AttributeKey.EPMKey.IBA_UNIT));
    		EPMDocument epm = (EPMDocument)iba;
    		unit = epm.getDefaultUnit().toString();
    	}
    	
    	Map<String,String> map = new HashMap<String,String>();
    	
    	map.put("specification", specification);
    	map.put("weight", weight);
    	map.put("unit", unit);
    	map.put("remarks", remarks);
    	map.put("interalnumber", interalnumber);
    	map.put("moldnumber", moldNumber);
    	map.put("moldcost", moldCost);
    	map.put("writer", writer);
    	
    	map.put("ecoNo", ecoNo);
    	map.put("ecoDate", ecoDate);
    	map.put("chk", chk);
    	map.put("apr", apr);
    	map.put("rev", rev);
    	map.put("des", des);
    	map.put("changeNo", changeNo);
    	map.put("changeDate", changeDate);
    	
    	if("view".equals(mode)) {
    		if(iba instanceof EPMDocument) {
    	    	map.put("finish", finish);
    	    	map.put("mat", mat);
    		}else {
    			map.put("finish", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_FINISH, finish));
    			map.put("mat", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MAT, mat));
    		}
    		map.put("model", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MODEL, model));
	    	map.put("manufacture", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture));
	    	map.put("productmethod", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod));
	    	map.put("deptcode", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_DEPTCODE, deptcode));
	    	map.put("preseration", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_PRESERATION, preseration));
	    	map.put("moldtype", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MOLDTYPE, moldtype));
    	}else {
    		map.put("model", model);
	    	map.put("finish", finish);
	    	map.put("mat", mat);
	    	map.put("manufacture", manufacture);
	    	map.put("productmethod", productmethod);
	    	map.put("deptcode", deptcode);
	    	map.put("preseration", preseration);
	    	map.put("moldtype", moldtype);
    	}
    	
    	return map;
    }
	
	@Override
	public Map<String, Object> versionHistory(String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		RevisionControlled rc = (RevisionControlled)CommonUtil.getObject(oid);
		QueryResult qr = null;
		//qr = VersionControlHelper.service.allVersionsOf(inputdoc.getMaster());
		qr = VersionControlHelper.service.allIterationsOf(rc.getMaster());
		while ( qr.hasMoreElements() ) {
			RevisionControlled obj = (RevisionControlled)qr.nextElement();
			
			Map<String,Object> map = new HashMap<String,Object>();
			boolean isApproved = obj.getLifeCycleState().toString().equals("APPROVED") ? true : false;
			
			map.put("oid", obj.getPersistInfo().getObjectIdentifier().toString());
			map.put("version", VersionControlHelper.getVersionIdentifier(obj).getSeries().getValue());
			map.put("iteration", obj.getIterationIdentifier().getSeries().getValue());
			map.put("creator", obj.getCreatorFullName());
			map.put("modifier", obj.getModifierFullName());
			map.put("createDate", obj.getCreateTimestamp());
			map.put("modifyDate", obj.getModifyTimestamp());
			map.put("note", obj.getIterationInfo().getNote());
			map.put("state", obj.getLifeCycleState().getDisplay());
			map.put("isApproved", isApproved);
			
			list.add(map);
		}
		result.put("list", list);
		return result;
	}
	
	@Override
	public List<Map<String,String>> include_Notice() throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		QuerySpec query = null;
	    query = new QuerySpec();
	    int idx = query.addClassList(Notice.class, true);
		query.appendOrderBy(new OrderBy(new ClassAttribute(Notice.class,"thePersistInfo.createStamp"), true), new int[] { idx });    
		QueryResult	taskQr = PagingSessionHelper.openPagingSession(0, 5, query);
		while(taskQr.hasMoreElements()){
			Object[] o = (Object[]) taskQr.nextElement();
			Notice notice = (Notice) o[0];
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("oid", CommonUtil.getOIDString(notice));
			map.put("title", notice.getTitle());
			map.put("date", DateUtil.getDateString(notice.getPersistInfo().getCreateStamp(), "D"));
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String,String>> include_Approve() throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();

		QuerySpec query = new QuerySpec();
		Class target = WorkItem.class;
		int idx = query.addClassList(target, false);
		
		query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.classname"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "primaryBusinessObject.key.classname"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "source.key.classname"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "source.key.id"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "thePersistInfo.createStamp"), new int[] { idx }, false);
		query.appendSelect(new ClassAttribute(target, "taskURLPathInfo"), new int[] { idx }, false);
		query.appendOrderBy(new OrderBy(new ClassAttribute(target,"thePersistInfo.createStamp"), true), new int[] { idx });   
    	SearchCondition where = OwnershipHelper.getSearchCondition(target,SessionHelper.manager.getPrincipal(), true);
    	query.appendWhere(where, new int[] { idx });
    	query.appendAnd();
       	query.appendWhere(new SearchCondition(target, "status", "=", "POTENTIAL"), new int[] { idx });
				    
		PagingQueryResult taskQr = PagingSessionHelper.openPagingSession(0, 5, query);
		Object obj[] = null;
    	ReferenceFactory rf = new ReferenceFactory();
    	LifeCycleManaged pbo = null;
    	String viewOid = "";
    	String oid = "";
    	
    	int totalSize = taskQr.getTotalSize();
    	
    	
    	String[] objName = null;
		
    	while(taskQr.hasMoreElements()) {
    		obj = (Object[])taskQr.nextElement();
    		oid = obj[0]+":"+obj[1];
    		
            WorkItem item = (WorkItem) rf.getReference(oid).getObject();
            WfActivity activity = (WfActivity)item.getSource().getObject();
            pbo = (LifeCycleManaged)item.getPrimaryBusinessObject().getObject();
            viewOid = pbo.getPersistInfo().getObjectIdentifier().toString();
            
            WTObject wobj = (WTObject)item.getPrimaryBusinessObject().getObject();
    		try{
    		if(WorklistHelper.service.getWorkItemName(wobj) != null){
    			 objName = WorklistHelper.service.getWorkItemName(wobj);
    		}
    		}catch(Exception e){e.printStackTrace();}
    		
    		String objName0 = "객체 구분 없음";
    		String objName1 = "객체 번호 없음";
    		String objName2 = "객체 이름 없음";
    		
    		if(objName != null){
    			objName0 = objName[0];
    			objName1 = objName[1];
    			objName2 = objName[2];
    		}
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("url", item.getTaskURLPathInfo());
    		map.put("oid", oid);
    		map.put("viewOid", viewOid);
    		map.put("title", objName1+"[" + objName2 + "]");
    		map.put("date", DateUtil.getDateString(pbo.getPersistInfo().getCreateStamp(), "d"));
    		list.add(map);
    	}
    	
    	return list;
	}
	
	@Override
	public List<Map<String,String>> include_Document() throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Hashtable hash = new Hashtable();
		hash.put("location", "/Default/Document");
		QuerySpec query = null; //DocumentQueryHelper.service.getListQuery(hash);  
		QueryResult	qr = PagingSessionHelper.openPagingSession(0, 5, query);
		while(qr.hasMoreElements()){
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("oid", CommonUtil.getOIDString(doc));
			map.put("number", doc.getNumber());
			map.put("name", doc.getName());
			map.put("creator", doc.getCreatorFullName());
			map.put("date", DateUtil.getDateString(doc.getModifyTimestamp(), "D"));
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String,String>> include_Drawing(HttpServletRequest request) throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		request.setAttribute("location", "/Default/PART_Drawing");
		/*Hashtable hash = new Hashtable();
		hash.put("location", "/Default/PART_Drawing");*/
		QuerySpec query=DrawingHelper.service.getListQuery(request);  
		QueryResult	qr = PagingSessionHelper.openPagingSession(0, 5, query);
		while(qr.hasMoreElements()){
			Object[] o = (Object[]) qr.nextElement();
			EPMDocument epm = (EPMDocument) o[0];
			Map<String,String> map = new HashMap<String,String>();
			map.put("oid", CommonUtil.getOIDString(epm));
			map.put("number", epm.getNumber());
			map.put("name", epm.getName());
			map.put("cretor", epm.getCreatorFullName());
			map.put("date", DateUtil.getDateString(epm.getModifyTimestamp(), "D"));
			list.add(map);
		}
		return list;
	}
	
	@Override
	public void setManagedDefaultSetting(LifeCycleManaged lm,String location,String lifecycle) throws Exception{

		//분류쳬게 설정 
		PDMLinkProduct product = WCUtil.getPDMLinkProduct();
		WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());

		FolderHelper.assignLocation((FolderEntry) lm, folder);
		
		//Container 설정
		((WTContained)lm).setContainer(product);

		//lifeCycle 설정
		LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef);
		LifeCycleHelper.setLifeCycle(lm, tmpLifeCycle);

	}
	
	/**
	 * 회수
	 * @param oid
	 */
	@Override
	public ResultData withDrawAction(String oid,boolean isInit) {
		
		ResultData data = new ResultData();
		String msg = Message.getMessage("결재를 회수 하였습니다.");
		boolean isResut = true;
		try{
			//설계 변경(ECO,EO)-LC_ECO, ECR,ROHS,문서,금형관리-LC_Defalut
			LifeCycleManaged lm = (LifeCycleManaged)CommonUtil.getObject(oid);
			
			String state = lm.getLifeCycleState().toString();
			if(state.equals("APPROVED")){
				data.setMessage(Message.getMessage("이미 승인 완료 되었습니다."));
				data.setResult(isResut);
				return data;
			}
			
			String lifecycleName = lm.getLifeCycleName();
			//결재선 지정으로 초기화
			//System.out.println("lifecycleName =" + lifecycleName +":" +lifecycleName.equals("LC_Default"));
			if(lifecycleName.equals("LC_Default")){
				
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)lm, State.toState("INWORK"), true);
			}else{
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)lm, State.toState("APPROVE_REQUEST"), true);
			}
			
			lm = (LifeCycleManaged)PersistenceHelper.manager.refresh(lm);
			//결재선 초기화
			//System.out.println("********** 결재선 초기화 시작  tsuam1**********");
			WFItem wfItem = WFItemHelper.service.getWFItem((WTObject)lm);
			boolean isUseChage = false;
			state = lm.getLifeCycleState().toString();
			if(wfItem != null) {
				wfItem.setObjectState(state);
				wfItem = (WFItem)PersistenceHelper.manager.modify(wfItem);
				WFItemHelper.service.reworkDataInit(wfItem,isInit);
			}
			//System.out.println("********** 결재선 초기화 끝  tsuam1**********");
			Object obj = CommonUtil.getObject(oid);
			//System.out.println("obj instanceof EChangeOrder Check " + (obj instanceof EChangeOrder));
			if(obj instanceof EChangeOrder){
			QueryResult qr = WorkflowHelper.service.getWorkItems((Persistable)obj);
				//System.out.println("workitem Check Size" + qr.size());
				
		         while(qr.hasMoreElements()){
		        	 WorkItem workItem =   (WorkItem)qr.nextElement();
		        	 //System.out.println("workitem Check Null Check" + (null!=workItem));
		        	 if(null!=workItem){
		 				state = workItem.getStatus().getStringValue();
		 				 //System.out.println("workitem Check state check" + (WfAssignmentState.POTENTIAL.equals(workItem.getStatus())));
		 				if(WfAssignmentState.POTENTIAL.equals(workItem.getStatus())){
		 					workItem.setStatus(WfAssignmentState.COMPLETED);
		 					PersistenceHelper.manager.modify(workItem);
		 					workItem = (WorkItem) PersistenceHelper.manager.refresh(workItem);
		 					state = workItem.getStatus().getStringValue();
		 					//System.out.println("workitem Check Chagne state" + (state));
		 				}
		 			}
		         }
			}
		}catch(Exception e){
			e.printStackTrace();
			msg = Message.getMessage("결재를 회수시 에러가 발생  \n"+e.getLocalizedMessage());
			isResut = false;
		}
		
		data.setMessage(msg);
		data.setResult(isResut);
		
		return data;
		
	}
	
	@Override
	public List<Map<String,String>> autoSearchUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		String userType = StringUtil.checkNull(request.getParameter("userType"));
		String value = StringUtil.checkNull(request.getParameter("value"));
		
		QuerySpec query = new QuerySpec();
		
		int idx = query.addClassList(People.class, true);
		
		query.appendWhere(new SearchCondition(People.class, People.NAME, SearchCondition.LIKE, "%"+value+"%"), new int[] {idx});
		
		SearchUtil.setOrderBy(query, People.class, idx, People.NAME, false);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[])result.nextElement();
			
			People people = (People)o[0];
			PeopleData data = new PeopleData(people);
			
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("name", data.name);
			map.put("duty", data.duty);
			map.put("department", data.departmentName);
			
			if("people".equals(userType)) {
				map.put("oid", data.peopleOID);
			}else if("wtuser".equals(userType)) {
				map.put("oid", data.wtuserOID);
			}
			
			list.add(map);
		}
		return list;
	}

	@Override
	public void doTempDelete(boolean uploadFolder) throws WTException {
		try {

			String wtTempFolderStr = WTProperties.getServerProperties().getProperty("wt.temp");
			if(uploadFolder) {
				wtTempFolderStr = wtTempFolderStr + "/upload";
			}
			File wtTempFolder = new File(wtTempFolderStr);

			if (wtTempFolder.isDirectory()) {
				File[] temp = wtTempFolder.listFiles();
				for (int i = 0; i < temp.length; i++) {
					temp[i].delete();
				}
			}
		} catch (IOException ioe) {

			throw new WTException(ioe);
		}
	}

	@Override
	public void copyIBAAttributes(IBAHolder fromIba, IBAHolder toIba) throws Exception {

		// 무게
		String weight = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_WEIGHT);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_WEIGHT, weight, "float");

		if (toIba instanceof EPMDocument) {
			// 재질 - CAD
			String mat = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MAT);
			String matName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MAT, mat);
			IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MAT, matName, "string");

			// 후처리 - CAD
			String finish = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_FINISH);
			String finishName = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_FINISH, finish);
			IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_FINISH, finishName, "string");

		} else {

			// 재질 - CAD
			String mat = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MAT);
			IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MAT, mat, "string");

			// 후처리 - CAD
			String finish = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_FINISH);
			IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_FINISH, finish, "string");
		}

		// 프로젝트 코드
		String model = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MODEL);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MODEL, model, "string");

		// 비고 -> OEM Info. 20190218 PJT
		String remarks = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_REMARKS);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_REMARKS, remarks, "string");

		// 사양
		String specification = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_SPECIFICATION);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_SPECIFICATION, specification, "string");

		// MANUFACTURE
		String manufacture = IBAUtil.getAttrValue(fromIba,
				AttributeKey.IBAKey.IBA_MANUFACTURE);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MANUFACTURE,
				manufacture, "string");

		// 부서
		String deptcode = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_DEPTCODE);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode, "string");

		// 제작방법
		String productmethod = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod, "string");

		// 내부 문서 번호
		String interalnumber = IBAUtil.getAttrValue(fromIba,
				AttributeKey.IBAKey.IBA_INTERALNUMBER);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_INTERALNUMBER,
				interalnumber, "string");

		// 보존 기간
		String preseration = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PRESERATION);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PRESERATION, preseration, "string");

		// 금형타입
		String moldType = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MOLDTYPE);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MOLDTYPE, moldType, "string");

		// 결재타입
		String approvalType = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_APPROVALTYPE);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_APPROVALTYPE, approvalType, "string");

		// 외부 금형 번호
		String moldNumber = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MOLDNUMBER);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MOLDNUMBER, moldNumber, "string");

		// 금형 개발비
		String moldCost = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_MOLDCOST);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_MOLDCOST, moldCost, "string");

		// 작성자
		String writer = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_DSGN);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_DSGN, writer, "string");

		// partName1
		String partName1 = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PARTNAME1);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PARTNAME1, partName1, "string");

		// partName2
		String partName2 = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PARTNAME2);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PARTNAME2, partName2, "string");

		// partName3
		String partName3 = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PARTNAME3);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PARTNAME3, partName3, "string");

		// partName4
		String partName4 = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_PARTNAME4);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_PARTNAME4, partName4, "string");

		// documentName1
		String documentName = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_DOCUMENTNAME1);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_DOCUMENTNAME1, documentName, "string");

		// documentName2
		String docName = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_DOCUMENTNAME2);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_DOCUMENTNAME2, docName, "string");
		
		// des
		String des = IBAUtil.getAttrValue(fromIba, AttributeKey.IBAKey.IBA_DES);
		IBAUtil.changeIBAValue(toIba, AttributeKey.IBAKey.IBA_DES, des, "string");
	}
	
	@Override
	public ResultData batchSecondaryDown(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("CommonService batchSecondaryDown");
		ResultData returnData = new ResultData();
		
		try{
			String oid = request.getParameter("oid");
			Object obj = CommonUtil.getObject(oid);
			Vector<BatchDownData> vecApp = new Vector<BatchDownData>();
			String number ="";
			
				
			if(obj instanceof ROHSMaterial){
				ROHSMaterial rohs = (ROHSMaterial)obj;
				number = rohs.getNumber();
				List<String> list = RohsUtil.getROHSContentRoleKey();
				
				for(String roleValue : list){
					ContentRoleType roleType= ContentRoleType.toContentRoleType(roleValue);
					QueryResult qr = ContentHelper.service.getContentsByRole (rohs ,roleType );
					
					while(qr.hasMoreElements()) {
						ContentItem item = (ContentItem) qr.nextElement ();
						BatchDownData downData = new BatchDownData();
						if(item != null) {
						
							ApplicationData appData = (ApplicationData)item;
							downData.setAppData(appData);
							downData.setPrimaryObject(rohs);
							vecApp.add(downData);
						}
					}
					
				}
				//List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(oid);
				
			}else{
				
				if(obj instanceof WTDocument){
					WTDocument doc = (WTDocument)obj;
					number = doc.getNumber();
				}else if(obj instanceof EChangeOrder ){
					EChangeOrder eo = (EChangeOrder)obj;
					number = eo.getEoNumber();
				}
				
				
				ContentHolder holder = ContentHelper.service.getContents((ContentHolder)obj);
				QueryResult qr = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.SECONDARY );
				
				List<Map<String,String>> list = new ArrayList<Map<String,String>>();

				while(qr.hasMoreElements()) {
					ContentItem item = (ContentItem) qr.nextElement ();
					BatchDownData downData = new BatchDownData();
					if(item != null) {
					
						ApplicationData appData = (ApplicationData)item;
						downData.setAppData(appData);
						downData.setPrimaryObject(obj);
						vecApp.add(downData);
					}
				}
				
				qr = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
				while(qr.hasMoreElements()) {
					ContentItem item = (ContentItem) qr.nextElement ();
					BatchDownData downData = new BatchDownData();
					if(item != null) {
					
						ApplicationData appData = (ApplicationData)item;
						downData.setAppData(appData);
						downData.setPrimaryObject(obj);
						vecApp.add(downData);
					}
				}
			}
			
			if(vecApp.size()==0){
				returnData.setResult(false);
				returnData.setMessage("다운로드 파일이 존재 하지 않습니다.");
			}else{
				File zipFile = MakeZIPUtil.attachFileSaveZip(vecApp, number);
				returnData.setResult(true);
				returnData.setMessage(zipFile.getName());
				
				//다운로드 history 등록
				Hashtable hash = new Hashtable();
				WTUser user = (WTUser)SessionHelper.getPrincipal();
				hash.put("dOid", oid);
				hash.put("userId", user.getFullName());
				DownloadHistoryHelper.service.createDownloadHistory(hash);
			}
		}catch(Exception e){
			e.printStackTrace();
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
		}
		
		return returnData;
		
	}

}
