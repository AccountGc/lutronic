package com.e3ps.rohs.service;

import java.io.File; 
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.web.WebUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.distribute.util.MakeZIPUtil;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSAttr;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.RepresentToLink;
import com.e3ps.rohs.dto.RoHSHolderData;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.RevisionControlled;
import wt.fc.IdentityHelper;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;


@SuppressWarnings("serial")
public class StandardRohsService extends StandardManager implements RohsService {
	final static String DATE_FORMAT = "yyyy-mm-dd";
	public static StandardRohsService newStandardRohsService() throws WTException {
		final StandardRohsService instance = new StandardRohsService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public List<Map<String,String>> rohsFileType() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String[] fileCode = AttributeKey.RohsKey.ROHS_CODE;
		String[] fileName = AttributeKey.RohsKey.ROHS_NAME;
		
		for(int i=0; i < fileCode.length; i++) {
			Map<String,String> map = new HashMap<String,String>();
			
			map.put("code", fileCode[i]);
			map.put("name", fileName[i]);
			
			list.add(map);
		}
		return list;
	}
	
	/**	RoHS sequence
	 * @param longDescription
	 * @param 
	 * @return number
	 */
	private String getRohsNumberSeq(String code) throws Exception {
		
		String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
		
		String number = code.concat("_");
		String noFormat = "0000";
        String seqNo = SequenceDao.manager.getSeqNo(number, noFormat, "WTDocumentMaster", "WTDocumentNumber");
        number = number + seqNo;
        
		return number;
	}
	
	/** 관련 RoHS
	 * @param oid
	 * @param 
	 * @return number
	 */
	@Override
    public List<RohsData> include_RohsList(String oid) throws Exception {
    	List<RohsData> list = new ArrayList<RohsData>();
    	if(StringUtil.checkString(oid)){
    		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
    		QueryResult qr = PersistenceHelper.manager.navigate(rohs, "represent", RepresentToLink.class);
    		while(qr.hasMoreElements()){ 
    			ROHSMaterial rohsConnet = (ROHSMaterial)qr.nextElement();
    			RohsData data = new RohsData(rohsConnet);
        		list.add(data);
        	}
    	}
    	return list;
    }
	
	/**
	 * 물질 의 관련 부품 등록, 부품의 관련 물질 등록
	 * @param rv
	 * @throws Exception
	 */
	@Override
	public void createROHSToPartLink(RevisionControlled rv,String[] oids) throws WTException{
		
		if(rv instanceof ROHSMaterial){
			
			if(oids != null){
				for(int i = 0 ; i < oids.length ; i++){
					WTPart part = (WTPart)CommonUtil.getObject(oids[i]);
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, (ROHSMaterial)rv);
					PersistenceHelper.manager.save(link);
				}
			}
			
		}else{
			
			if(oids != null){
				for(int i = 0 ; i < oids.length ; i++){
					ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oids[i]);
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink((WTPart)rv, rohs);
					PersistenceHelper.manager.save(link);
				}
			}
			
		}
	}
	
	/**
	 * 물질 의 관련 부품 삭제, 부품의 관련 물질 삭제
	 * @param rv
	 * @throws Exception
	 */
	public void deleteROHSToPartLink(RevisionControlled rv) throws Exception{
		
		List<PartToRohsLink> list = RohsHelper.manager.getPartToRohsLinkList(rv);
		
		for(PartToRohsLink link : list){
			PersistenceHelper.manager.delete(link);
		}
	}
	
	/**
	 * 물질의 구성 물질 등록
	 * @param rohs
	 * @param oids
	 * @throws WTException
	 */
	public void createROHSToROHSLink(ROHSMaterial rohs,String[] oids) throws WTException{
		
		if(oids != null){
			for(int i = 0 ; i < oids.length ; i++){
				ROHSMaterial composition = (ROHSMaterial)CommonUtil.getObject(oids[i]);
				RepresentToLink link = RepresentToLink.newRepresentToLink(rohs, composition);
				PersistenceHelper.manager.save(link);
			}
		}
	}
	
	/**
	 * 물질의 구성 물질 삭제
	 * @param rohs
	 * @throws Exception 
	 */
	public void deleteROHSToROHSLink(ROHSMaterial represent) throws Exception{
		List<RepresentToLink> list =  RohsHelper.manager.getRepresentLink(represent);
		for(RepresentToLink link : list){
			PersistenceHelper.manager.delete(link);
		}
	}
	
	/*
	public void reviseROHsMaterial(String oid) throws Exception {
		
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		
		ROHSMaterial newRohs= (ROHSMaterial)ObjectUtil.revise(rohs);

	}
	*/
	
	/**
	 * 전버전의 OHSContHOlder new 버전으로 Copy
	 * @param oldRohs
	 * @param newRohs
	 * @throws Exception
	 */
	public void copyROHSContHOlder(ROHSMaterial oldRohs,ROHSMaterial newRohs) throws Exception{
		CommonContentHelper.service.delete(newRohs);
		
		List<ROHSContHolder> list = RohsHelper.manager.getROHSContHolder(oldRohs);
		for(ROHSContHolder rHolder :  list){
			ApplicationData app = rHolder.getApp();
			String appOid = CommonUtil.getOIDString(app);
			String file = CommonContentHelper.service.copyApplicationData(appOid);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleType", app.getRole().toString());
			map.put("file", file);
			map.put("fileName", rHolder.getFileName());
			map.put("fileType", rHolder.getFileType());
			map.put("publicationDate", rHolder.getPublicationDate());
			createROHSContHolder(newRohs, map);
		}
	}
	
	/**
	 * ROHSContHolder 생성 
	 * @param rohs
	 * @param map
	 * @throws Exception
	 */
	public void createROHSContHolder(ROHSMaterial rohs,HashMap<String, Object> map) throws Exception{
		 	ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
	        
		 	String roleType = (String)map.get("roleType");
		 	String file = (String)map.get("file");
		 	String fileName = (String)map.get("fileName");
		 	String fileType = (String)map.get("fileType");
		 	String publicationDate = (String)map.get("publicationDate");
		 	
	        ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)rohs, roleType, file,  false);
	        
	        holder.setFileName(fileName);
	        holder.setFileType(fileType);
	        holder.setPublicationDate(publicationDate);
	        holder.setApp(app);
	        holder.setRohs(rohs);
	        
	        PersistenceHelper.manager.save(holder);
		
	}
	
	@Override
	public Map<String,Object> listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = RohsQueryHelper.service.listRoHSDataAction(request, response);
			
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
			
		}

		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    int rowCount    = control.getTopListCount();
	    String param    = control.getParam();
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
	    
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			ROHSContHolder holder = (ROHSContHolder) o[0];
			
			RoHSHolderData data = new RoHSHolderData(holder);
			RohsData rData = new RohsData(data.getRohs());
			
			xmlBuf.append("<row id='"+ data.oid +"'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + rData.getManufactureDisplay(true) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + rData.oid + "')>" + rData.name + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.fileName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.publicationDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + DateUtil.subString(rData.createDate, 0, 10) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + rData.creator + "]]></cell>");
			xmlBuf.append("</row>" );
			
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
	
	private Map<String,Object> setPartROHMap(Map<String,Object> partRohsMap, Map<String,Object> partMap){
		
		partRohsMap.put("partOid", partMap.get("partOid"));
		partRohsMap.put("partNumber", partMap.get("partNumber"));
		partRohsMap.put("partName", partMap.get("partName"));
		partRohsMap.put("partCreator", partMap.get("partCreateDate"));
		partRohsMap.put("partState", partMap.get("partState"));
		partRohsMap.put("level", partMap.get("level"));
		int level =(Integer)partMap.get("level");
		partRohsMap.put("L"+level, level);
		return partRohsMap;
	}
	
	@Override
	public Map<String, Object> delete(String oid) throws Exception {
        Transaction trx = new Transaction();
        Map<String, Object> rtnVal = new HashMap<String, Object>();
        boolean isDelete = true;
        boolean result = true;
		String msg = Message.get("삭제되었습니다");
        try{
            trx.start();
            if(oid != null){
                ReferenceFactory f = new ReferenceFactory();
                ROHSMaterial rohs = (ROHSMaterial) f.getReference(oid).getObject();
                
                // 관련품목
                List<PartToRohsLink> partList = RohsHelper.manager.getPartToRohsLinkList(rohs);
				if (isDelete && partList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질과 연계된 품목이 존재합니다.");
				}
				
				// 관련물질
				List<RepresentToLink> rohsList = RohsHelper.manager.getRepresentLink(rohs);
				if (isDelete && rohsList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질과 연계된 물질이 존재합니다.");
				}
				
        		// 관련 첨부파일
				List<ROHSContHolder> contentList = RohsHelper.manager.getROHSContHolder(rohs);
				if (isDelete && contentList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질에 첨부파일이 존재합니다.");
				}
        		
				if (isDelete) {
					WFItemHelper.service.deleteWFItem(rohs);
					PersistenceHelper.manager.delete(rohs);
					msg = Message.get("삭제되었습니다.");
				}
            }
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
        } finally {
        	if (trx != null) {
        		trx.rollback();
			}
        }
        
        rtnVal.put("result", result);
		rtnVal.put("msg", msg);
		
        return  rtnVal;
    }
	
	@Override
	public List<RohsData> include_RohsView(String oid, String module, String roleType) throws Exception {
		List<RohsData> list = null;
		
		if(oid.length() > 0){
			if("rohs".equals(module)){
				ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
				list = RohsHelper.manager.getRepresentToLinkList(rohs,roleType);
			}else if("part".equals(module)){
				WTPart part = (WTPart)CommonUtil.getObject(oid);
				list = RohsHelper.manager.getPartToROHSList(part);
			}else {
				list = new ArrayList<RohsData>();
			}
		}else {
			list = new ArrayList<RohsData>();
		}
		Collections.sort(list, new ObjectComarator());
		return list;
	}
	
	@Override
	public void revisePartToROHSLink(WTPart oldPart,WTPart newPart) throws Exception{
		
		List<PartToRohsLink> linkList = RohsHelper.manager.getPartToRohsLinkList(oldPart);
		String[] oids = new String[linkList.size()];
		int idx = 0;
		for(PartToRohsLink link : linkList){
			ROHSMaterial rohs = link.getRohs();
			 String oid = CommonUtil.getOIDString(rohs);
			 oids[idx] = oid;
			 idx++;
		}
		
		createROHSToPartLink(newPart,oids);
	}
	
	private ROHSMaterial getRoHs(String rohsNumber) throws Exception {
		QuerySpec qs = new QuerySpec(ROHSMaterial.class);

        qs.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[] { 0 });
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NUMBER, SearchCondition.EQUAL, rohsNumber), new int[] { 0 });

        SearchUtil.addLastVersionCondition(qs, ROHSMaterial.class, 0);

        QueryResult qr = PersistenceHelper.manager.find(qs);
        if ( qr.hasMoreElements() ) {
        	Object o = qr.nextElement();
            if ( o instanceof ROHSMaterial ) {
                return (ROHSMaterial) o;
            } else {
                Object[] arry = (Object[]) o;
                return (ROHSMaterial) arry[0];
            }
        }

        return null;
	}
	
	@Override
	public ResultData batchROHSDown(HttpServletRequest request, HttpServletResponse response)  {
		//System.out.println("batchROHSDown");
		String partNumber = request.getParameter("partNumber");
		ResultData returnData = new ResultData();
		
		try{
			Vector<BatchDownData> vecApp = new Vector<BatchDownData>();
			List<WTPart> partList = new ArrayList<WTPart>();
			
			BomBroker broker = new BomBroker();
			WTPart pPart = PartHelper.service.getPart(partNumber);
			if(pPart == null){
				throw new Exception(partNumber +"의 번호가 존재 하지 않습니다.");
			}
			PartTreeData root = broker.getTree(pPart, true, null, null);
			partList.add(pPart);
			partList = partBomList(root, partList);
			List<String> contentRolelist = RohsUtil.getROHSContentRoleKey();
			String number = pPart.getNumber()+"_ROHS";
			
			
			List<BatchDownData> targetlist = new ArrayList<BatchDownData>();
			Map<String, String> doubleChekcMap = new HashMap<String, String>();
			for(WTPart supart : partList){
				List<RohsData> rohslist = RohsQueryHelper.service.getPartToROHSList(supart);
				
				for(RohsData rohsData : rohslist){
					ROHSMaterial rohs = rohsData.rohs;
					String oid = rohsData.oid;
					//System.out.println("subROHS = " + rohs.getNumber());
					if(doubleChekcMap.containsKey(rohs.getNumber())){
						continue;
					}
					
					doubleChekcMap.put(rohs.getNumber(), rohs.getNumber());
					
					//자신의 첨부 파일
					for(String roleValue : contentRolelist){
						ContentRoleType roleType= ContentRoleType.toContentRoleType(roleValue);
						QueryResult qr = ContentHelper.service.getContentsByRole (rohs ,roleType );
						
						while(qr.hasMoreElements()) {
							ContentItem item = (ContentItem) qr.nextElement ();
							BatchDownData downData = new BatchDownData();
							if(item != null) {
							
								ApplicationData appData = (ApplicationData)item;
								downData.setOid(oid);
								downData.setAppData(appData);
								downData.setPrimaryObject(rohs);
								vecApp.add(downData);
								targetlist.add(downData);
								
							}
						}
					}
					
					//대표 물질 첨부 파일
					List<RohsData> representList = new ArrayList<RohsData>();
					representList = RohsQueryHelper.service.getRepresentStructure(rohs,representList);
					for(RohsData representRohsData : representList){
						
						ROHSMaterial representRohs = representRohsData.rohs;
						String representOid = representRohsData.oid;
						
						if(doubleChekcMap.containsKey(representRohs.getNumber())){
							continue;
						}
						
						doubleChekcMap.put(representRohs.getNumber(), representRohs.getNumber());
						
						for(String roleValue : contentRolelist){
							ContentRoleType roleType= ContentRoleType.toContentRoleType(roleValue);
							QueryResult qr = ContentHelper.service.getContentsByRole (representRohs ,roleType );
							
							while(qr.hasMoreElements()) {
								ContentItem item = (ContentItem) qr.nextElement ();
								BatchDownData downData = new BatchDownData();
								if(item != null) {
								
									ApplicationData appData = (ApplicationData)item;
									downData.setOid(oid);
									downData.setAppData(appData);
									downData.setPrimaryObject(representRohs);
									vecApp.add(downData);
									targetlist.add(downData);
									
								}
							}
						}
					}
					
					
				}
			}
			
			//File zipFile =MakeZIPUtil.attachFileSaveZip(vecApp, number);
			//returnData.setResult(true);
			//returnData.setMessage(zipFile.getName());
			
			if(vecApp.size()==0){
				returnData.setResult(false);
				returnData.setMessage("다운로드 파일이 존재 하지 않습니다.");
			}else{
				File zipFile = MakeZIPUtil.attachFileSaveZip(vecApp, number);
				returnData.setResult(true);
				returnData.setMessage(zipFile.getName());
				
				//다운로드 history 등록
				DownloadHistoryHelper.service.createBatchDownloadHistory(targetlist);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
		}
		
		return returnData;
	}
	
	private List<WTPart> partBomList(PartTreeData parent, List<WTPart> list ) throws Exception{
		   
		ArrayList<PartTreeData> childList = parent.children;
		
	    for(PartTreeData child : childList){
	    	
	    	//System.out.println("part = " + child.number);
	    	
    		boolean isChange = (PartUtil.isChange(parent.number) || PartUtil.isChange(child.number) );
	    	if( isChange  ){
				continue;
			}
	    	if(list.contains(child.part)){
	    		continue;
	    	}
			list.add(child.part);
			
			partBomList(child, list);
			
	     
	    }
	    
	    return list;
	    
	}
	

	public static boolean isDateValid(String date) 
	{
	        try {
	            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(date);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		ROHSMaterial rohs = null;
		try {
			trs.start();
			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
	    	String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
	    	String rohsNumber = StringUtil.checkNull((String) params.get("rohsNumber"));
	    	
			DocumentType docType = DocumentType.toDocumentType((String) params.get("docType"));
			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
			String description = StringUtil.checkNull((String) params.get("description"));
			boolean temprary = (boolean) params.get("temprary");
			
			// 결재
			ArrayList<Map<String, String>> approvalRows = (ArrayList<Map<String, String>>) params.get("approvalRows");
			ArrayList<Map<String, String>> agreeRows = (ArrayList<Map<String, String>>) params.get("agreeRows");
			ArrayList<Map<String, String>> receiveRows = (ArrayList<Map<String, String>>) params.get("receiveRows");
			// 외부 메일
			ArrayList<Map<String, String>> external =  (ArrayList<Map<String, String>>) params.get("external");
			boolean isSelf = false;
			
			// 문서 기본 정보 설정
			rohs = ROHSMaterial.newROHSMaterial();
			rohs.setName(rohsName);
			if("".equals(rohsNumber)) {
				rohs.setNumber(getRohsNumberSeq(manufacture));
			}else {
				rohs.setNumber(rohsNumber);
			}
			
	        rohs.setDocType(docType);
			rohs.setDescription(description);
			
			String location = StringUtil.checkNull((String) params.get("location"));
			// 문서 분류쳬게 설정
			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) rohs, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(rohs,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			
	        rohs.setOwnership(Ownership.newOwnership(SessionHelper.manager.getPrincipalReference()));
	        rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
	        
	        if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(rohs, state);
			}
	        
	        ArrayList<String> secondarys = (ArrayList<String>) params.get("secondary");
	        Map<String,String> rohsMap = new HashMap<String,String>();
	        String fileType =  StringUtil.checkNull((String) params.get("fileType"));
			String publicationDate =  StringUtil.checkNull((String) params.get("publicationDate"));
			rohsMap.put("fileType", fileType);
			rohsMap.put("publicationDate", publicationDate);
	        int attachCount = 1;
			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				if(attachCount<=20) {
					createAttach(rohs, rohsMap, secondarys.get(i), attachCount);
				}
				attachCount++;
			}
	        
            //관련 부품            
			List<Map<String, Object>> partList = (List<Map<String, Object>>) params.get("partList");
			createROHSToPartLink(rohs, partList);
			
			//관련 물질
			List<Map<String, Object>> rohsList = (List<Map<String, Object>>) params.get("rohsList");
			createROHSToROHSLink(rohs, rohsList);
			
	        
            String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
            if("LC_Default_NonWF".equals(lifecycle)){
            	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
            	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
            }
            
            Map<String,Object> map = new HashMap<String,Object>();
            
            
            map.put("approvalType", approvalType);
            map.put("manufacture", manufacture);
            CommonHelper.service.changeIBAValues(rohs, map);
            
            // 외부 메일 링크 저장
 			MailUserHelper.service.saveLink(rohs, external);
 			
 			// 결재 시작
			if (isSelf) {
				// 자가결재시
				WorkspaceHelper.service.self(rohs);
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(rohs, agreeRows, approvalRows, receiveRows);
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
	
	public void createROHSToPartLink(RevisionControlled rv, List<Map<String, Object>> partList) throws WTException{
		
		if(rv instanceof ROHSMaterial){
			if(partList.size()>0){
				for(Map<String, Object> map : partList){
					String oid = (String) map.get("oid");
					WTPart part = (WTPart)CommonUtil.getObject(oid);
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, (ROHSMaterial)rv);
					PersistenceHelper.manager.save(link);
				}
			}
			
		}else{
			if(partList.size()>0){
				for(Map<String, Object> map : partList){
					String oid = (String) map.get("oid");
					ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink((WTPart)rv, rohs);
					PersistenceHelper.manager.save(link);
				}
			}
			
		}
	}
	
	public void createROHSToROHSLink(ROHSMaterial rohs, List<Map<String, Object>> rohsList) throws WTException{
		
		if(rohsList.size()>0){
			for(Map<String, Object> map : rohsList){
				String oid = (String) map.get("oid");
				ROHSMaterial composition = (ROHSMaterial)CommonUtil.getObject(oid);
				RepresentToLink link = RepresentToLink.newRepresentToLink(rohs, composition);
				PersistenceHelper.manager.save(link);
			}
		}
	}

	@Override
	public void batch(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			ArrayList<Map<String, Object>> gridList = (ArrayList<Map<String, Object>>) params.get("gridList");
    		for(Map<String, Object> map : gridList) {
    			ArrayList<String> secondarys = (ArrayList<String>) map.get("secondary");
    			String manufacture = (String) map.get("manufacture");
    			String rohsName = (String) map.get("rohsName");
    			ROHSMaterial rm = RohsHelper.manager.getRohs(rohsName);
    			
    			Map<String,String> rohsMap = new HashMap<String,String>();
    			String fileType = StringUtil.checkNull((String) map.get("fileType"));
    			String publicationDate = StringUtil.checkNull((String) map.get("publicationDate"));
    			rohsMap.put("fileType", fileType);
    			rohsMap.put("publicationDate", publicationDate);
    			if(rm==null) {
    				ROHSMaterial rohs = ROHSMaterial.newROHSMaterial();
    				rohs.setNumber(getRohsNumberSeq(manufacture));
        			rohs.setName(rohsName);
        			String lifecycle = (String) map.get("lifecycleName");
        			// 물질 Container 설정
        	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
        	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
        	        rohs.setContainer(e3psProduct);
        	        // 물질 lifeCycle 설정
        			LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef));
        			
        			rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
        			Map<String,Object> commonMap = new HashMap<String,Object>();
                    
        			String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
                    if("LC_Default_NonWF".equals(lifecycle)){
                    	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
                    	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
                    }
                    commonMap.put("approvalType", approvalType);
                    commonMap.put("manufacture", manufacture);
        			CommonHelper.service.changeIBAValues(rohs, commonMap);
        			
        			int attachCount = 1;
        			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
        				if(attachCount<=20) {
        					createAttach(rohs, rohsMap, secondarys.get(i), attachCount);
        				}
        				attachCount++;
        			}
    			}else {
    				int attachCount = 1;
        			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
        				if(attachCount<=20) {
        					createAttach(rm, rohsMap, secondarys.get(i), attachCount);
        				}
        				attachCount++;
        			}
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
	
	private void createAttach(ROHSMaterial rohs, Map<String,String> aMap, String secondary, int count) throws Exception {
		String roleType = "ROHS" + count;
		String cacheId = secondary;
		String fileType = (String)aMap.get("fileType");
		String publicationDate = (String)aMap.get("publicationDate");
		File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
		ApplicationData applicationData = ApplicationData.newApplicationData(rohs);
		ContentRoleType contentroleType = ContentRoleType.toContentRoleType(roleType);
		applicationData.setRole(contentroleType);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(rohs, applicationData, vault.getPath());
		ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
		String fileName = applicationData.getFileName().toUpperCase();
		holder.setFileName(fileName);
		holder.setFileType(fileType);
		holder.setPublicationDate(publicationDate);
		holder.setApp(applicationData);
		holder.setRohs(rohs);
		PersistenceHelper.manager.save(holder);
		
		ROHSAttr rohsAttr = ROHSAttr.newROHSAttr();
		rohsAttr.setPublicationDate(publicationDate);
		rohsAttr.setApp(applicationData);
		PersistenceHelper.manager.save(rohsAttr);
		
	}

	@Override
	public void update(Map<String, Object> params) throws Exception {
		Transaction trx = new Transaction();
		ROHSMaterial new_material = null;
		try {
			trx.start();
			String oid = StringUtil.checkNull((String) params.get("oid"));
			
			if(oid.length() > 0) {
			
				ROHSMaterial old_material = (ROHSMaterial)CommonUtil.getObject(oid);
				new_material = (ROHSMaterial) RohsUtil.getWorkingCopy(old_material);
				new_material = (ROHSMaterial) PersistenceHelper.manager.refresh(new_material);
				String description = StringUtil.checkNull((String) params.get("description"));
				new_material.setDescription(description);
				new_material = (ROHSMaterial)PersistenceHelper.manager.modify(new_material);
				/**
				 * 
				 *   첨부파일 관련 작업 수행
				 *   
				 */
				ArrayList<String> secondarys = (ArrayList<String>) params.get("secondary");
				
				if(secondarys != null) {
					int count = 1;	
					for(String secondary : secondarys) {
						/**
						 * 
						 *   새로운 첨부파일 생성
						 *   
						 */	
						String roleType = "ROHS" + count;
						String cacheId = secondary;
						String fileType =  StringUtil.checkNull((String)params.get("fileType"));
						String publicationDate =  StringUtil.checkNull((String)params.get("publicationDate"));
						File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
						ApplicationData applicationData = ApplicationData.newApplicationData(new_material);
						ContentRoleType contentroleType = ContentRoleType.toContentRoleType(roleType);
						applicationData.setRole(contentroleType);
						PersistenceHelper.manager.save(applicationData);
						ContentServerHelper.service.updateContent(new_material, applicationData, vault.getPath());
						ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
						String fileName = applicationData.getFileName().toUpperCase();
						holder.setFileName(fileName);
						holder.setFileType(fileType);
						holder.setPublicationDate(publicationDate);
						holder.setApp(applicationData);
						holder.setRohs(new_material);
						PersistenceHelper.manager.save(holder);
						count++;
					}
				}
				
				new_material = (ROHSMaterial) PersistenceHelper.manager.refresh(new_material);
				
				/**
				 * 
				 *   CheckOut 상태 검사
				 *   
				 */
				if(WorkInProgressHelper.isCheckedOut(new_material)){
					//System.out.println("CheckOut 상태입니다.......................");
					new_material = (ROHSMaterial) WorkInProgressHelper.service.checkin(new_material, "");
				}
				
				/**
				 * 
				 *   관련 부품 관련 작업 수행
				 *   
				 */
				
				// 관련 부품 링크 삭제
				deleteROHSToPartLink(new_material);
				
				// 관련 부품 링크 생성
				List<Map<String, Object>> partList = (List<Map<String, Object>>) params.get("partList");
				createROHSToPartLink(new_material, partList);
				
				/**
				 * 
				 *   관련 물질 관련 작업 수행
				 */
				/* 관련 물질 링크 삭제 */
				deleteROHSToROHSLink(new_material);
				
				/* 관련 물질 링크 생성*/
				List<Map<String, Object>> rohsList = (List<Map<String, Object>>) params.get("rohsList");
				createROHSToROHSLink(new_material, rohsList);
				
	            Map<String,Object> map = new HashMap<String,Object>();
	            String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
	            map.put("manufacture", manufacture);
	            CommonHelper.service.changeIBAValues(new_material, map);
	            
            	String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
	            
	            if (rohsName.length() > 0 && !new_material.getName().equals(rohsName)) {
		            WTDocumentMaster docMaster = (WTDocumentMaster)(new_material.getMaster());
	                WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity)docMaster.getIdentificationObject();
	                identity.setNumber(new_material.getNumber());
	                identity.setName(rohsName);
	                docMaster = (WTDocumentMaster)IdentityHelper.service.changeIdentity(docMaster, identity);
	            }
	            new_material = (ROHSMaterial) PersistenceHelper.manager.refresh(new_material);
			}
			
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
        } finally {
        	if (trx != null) {
        		trx.rollback();
			}
        }
	}

	@Override
	public void copyRohs(Map<String, Object> params) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			String oid = StringUtil.checkNull((String) params.get("oid"));
			ROHSMaterial orgRohs = (ROHSMaterial)CommonUtil.getObject(oid);
			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
	    	String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
			DocumentType docType = DocumentType.toDocumentType((String) params.get("docType"));
			String rohsNumber = StringUtil.checkNull((String) params.get("rohsNumber"));
			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
			
			// 문서 기본 정보 설정
			ROHSMaterial rohs = ROHSMaterial.newROHSMaterial();
			rohs.setName(rohsName);
			if("".equals(rohsNumber)){
				rohs.setNumber(getRohsNumberSeq(manufacture));
			}else{
				rohs.setNumber(rohsNumber);
			}
			rohs.setDescription(orgRohs.getDescription());
	        rohs.setDocType(docType);
	        
	        // 문서 분류쳬게 설정
	        String location = StringUtil.checkNull((String) params.get("location"));
	        Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
	        FolderHelper.assignLocation((FolderEntry)rohs, folder);
			
	        // 문서 Container 설정
	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
	        rohs.setContainer(e3psProduct);
	        
	        // 문서 lifeCycle 설정
	        LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
            
	        PersistenceHelper.manager.save(rohs);
	        
	        // 관련 품목
	        List<PartToRohsLink> partList = RohsHelper.manager.getPartToRohsLinkList(orgRohs);
	        for(PartToRohsLink link : partList){
	        	WTPart part = (WTPart) link.getRoleAObject();
	        	PartToRohsLink partLink = PartToRohsLink.newPartToRohsLink(part, rohs);
	        	PersistenceServerHelper.manager.insert(partLink);
	        }
	        
	        //관련 물질
	        List<RepresentToLink> rohsList = RohsHelper.manager.getRepresentLink(orgRohs);
	        for(RepresentToLink rohsLink : rohsList){
	        	ROHSMaterial comRohs = (ROHSMaterial) rohsLink.getRoleBObject();
	        	if(CommonUtil.isLatestVersion(comRohs)){
	        		RepresentToLink link = RepresentToLink.newRepresentToLink(rohs, comRohs);
		        	PersistenceServerHelper.manager.insert(link);
	        	}
	        }
	        
	        //관련 첨부파일
	        copyROHSContHOlder(orgRohs, rohs);
			
	        String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
            if("LC_Default_NonWF".equals(lifecycle)){
            	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
            	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
            }
            
	        Map<String,Object> map = new HashMap<String,Object>();
            
            map.put("approvalType", approvalType);
            map.put("manufacture", manufacture);
            CommonHelper.service.changeIBAValues(rohs, map);
	        
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
        } finally {
        	if (trx != null) {
        		trx.rollback();
			}
        }
	}

	@Override
	public void reviseRohs(Map<String, Object> params) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			String oid = StringUtil.checkNull((String) params.get("oid"));
			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
			if(oid.length() > 0) {
				ROHSMaterial oldRohs = (ROHSMaterial) CommonUtil.getObject(oid);
            	ROHSMaterial rohs = (ROHSMaterial) ObjectUtil.revise(oldRohs, lifecycle);
            	rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
            	
            	//관련 품목
                List<PartToRohsLink> partList = RohsHelper.manager.getPartToRohsLinkList(oldRohs);
                for(PartToRohsLink link : partList){
                	WTPart part = (WTPart) link.getRoleAObject();
                	PartToRohsLink partLink = PartToRohsLink.newPartToRohsLink(part, rohs);
                	PersistenceServerHelper.manager.insert(partLink);
                }
                
                //관련 물질 composition
                List<RepresentToLink> rohsList = RohsHelper.manager.getRepresentLink(oldRohs);
                for(RepresentToLink rohsLink : rohsList){
                	ROHSMaterial comRohs = (ROHSMaterial) rohsLink.getRoleBObject();
                	if(CommonUtil.isLatestVersion(comRohs)){
                		RepresentToLink link = RepresentToLink.newRepresentToLink(rohs, comRohs);
                    	PersistenceServerHelper.manager.insert(link);
                	}
                }
                
                //관련 물질 대표
                List<RohsData> representList = RohsHelper.manager.getRepresentToLinkList(oldRohs, "represent");
                for(RohsData representRohsData: representList){
                	ROHSMaterial representRohs = (ROHSMaterial) CommonUtil.getObject(representRohsData.getOid());
                	if(representRohsData.isLatest()){
                		RepresentToLink link = RepresentToLink.newRepresentToLink(representRohs, rohs);
                    	PersistenceServerHelper.manager.insert(link);
                	}
                }
                		
                //관련 첨부파일
                copyAttach(oldRohs, rohs);
                
                String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
                if("LC_Default_NonWF".equals(lifecycle)){
                	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
                	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
                }
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("approvalType", approvalType);
                CommonHelper.service.changeIBAValues(rohs, map);
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
        } finally {
        	if (trx != null) {
        		trx.rollback();
			}
        }
	}
	
	public void copyAttach(ROHSMaterial oldRohs,ROHSMaterial newRohs) throws Exception{
		CommonContentHelper.service.delete(newRohs);
		
		List<ROHSContHolder> list = RohsHelper.manager.getROHSContHolder(oldRohs);
		for(ROHSContHolder holder :  list){
			ApplicationData app = holder.getApp();
			String roleType = app.getRole().toString();
			ContentRoleType contentroleType = ContentRoleType.toContentRoleType(roleType);
			ApplicationData newApp = ApplicationData.newApplicationData(newRohs);
			newApp.setRole(contentroleType);
			PersistenceHelper.manager.save(newApp);
			ContentServerHelper.service.updateContent(newRohs, newApp, app.getUploadedFromPath());
			
			ROHSContHolder newHolder = ROHSContHolder.newROHSContHolder();
			String fileName = newApp.getFileName().toUpperCase();
			holder.setFileName(fileName);
			holder.setFileType(holder.getFileType());
			holder.setPublicationDate(holder.getPublicationDate());
			holder.setApp(newApp);
			holder.setRohs(newRohs);
			PersistenceHelper.manager.save(newHolder);
		}
	}

	@Override
	public Map<String, Object> rohsLink(Map<String, Object> params) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		Transaction trx = new Transaction();
		ArrayList<Map<String, Object>> gridList = (ArrayList<Map<String, Object>>) params.get("gridList");
		try {
			trx.start();
			
    		for(Map<String, Object> map : gridList) {
    			// 부품코드
    			String partNumber = StringUtil.checkNull((String) map.get("partNumber"));
    			WTPart part = null;
    			String partOid = "";
    			if(!"".equals(partNumber)) {
    				part = PartHelper.service.getPart(partNumber);
    				if(part == null){
    					result.put("result", false);
    					result.put("msg", partNumber+" 부품이 존재하지 않습니다.");
    					return result;
    				}else {
    					partOid = CommonUtil.getVROID(part);
    				}
    			}
    			
    			// 물질 코드
    			String rohsNumber = StringUtil.checkNull((String) map.get("rohsNumber"));
    			ROHSMaterial rohs = null;
    			String rohsOid = "";
    			if(!"".equals(rohsNumber)) {
    				rohs = getRoHs(rohsNumber);
    				if(rohs == null) {
    					result.put("result", false);
    					result.put("msg", rohsNumber+ " 물질이 존재하지 않습니다.");
    					return result;
    				}else {
    					rohsOid = CommonUtil.getOIDString(rohs);
    				}
    			}
    			
				if(!RohsHelper.manager.duplicateNumber(partOid,rohsNumber)){
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, rohs);
					PersistenceHelper.manager.save(link);
				}else {
					result.put("result", false);
					result.put("msg", partNumber+"와 "+rohsNumber+ "는 이미 연관관계가 되어 있습니다.");
					return result;
				}
    		}
			
    		result.put("result", true);
			result.put("msg", "등록 되었습니다.");
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
        } finally {
        	if (trx != null) {
        		trx.rollback();
			}
        }
		return result;
	}
	
}
