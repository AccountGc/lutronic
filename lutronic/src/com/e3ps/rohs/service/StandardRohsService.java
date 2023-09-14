package com.e3ps.rohs.service;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import wt.org.WTUser;
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
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeOrder;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
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
import com.e3ps.part.beans.ObjectComarator;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.VersionHelper;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.RepresentToLink;
import com.e3ps.rohs.beans.RoHSHolderData;
import com.e3ps.rohs.beans.RohsData;


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
	
	
	@Override
    public ResultData reviseUpdate(Map<String, Object> params)throws Exception {
    	ResultData data = new ResultData();
    	String reOid = "";
        Transaction trx = new Transaction();
        try {
            trx.start();

            ReferenceFactory f = new ReferenceFactory();
            String oid = StringUtil.checkNull((String) params.get("oid"));
            String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
            if(oid.length() > 0) {
            	
            	ROHSMaterial oldRohs = (ROHSMaterial)f.getReference(oid).getObject();
            	ROHSMaterial rohs = (ROHSMaterial) ObjectUtil.revise(oldRohs, lifecycle);
            	
                //System.out.println("[newVs][oid]"+rohs.getPersistInfo().getObjectIdentifier().toString());
                
                rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
               // System.out.println("[save][oid]"+rohs.getPersistInfo().getObjectIdentifier().toString());
                
                //관련 품목
                List<PartToRohsLink> partList = RohsQueryHelper.service.getPartToRohsLinkList(oldRohs);
                for(PartToRohsLink link : partList){
                	WTPart part = (WTPart) link.getRoleAObject();
                	PartToRohsLink partLink = PartToRohsLink.newPartToRohsLink(part, rohs);
                	PersistenceServerHelper.manager.insert(partLink);
                }
                
                //관련 물질 composition
                List<RepresentToLink> rohsList = RohsQueryHelper.service.getRepresentLink(oldRohs);
                for(RepresentToLink rohsLink : rohsList){
                	ROHSMaterial comRohs = (ROHSMaterial) rohsLink.getRoleBObject();
                	if(VersionHelper.service.isLastVersion(comRohs)){
                		RepresentToLink link = RepresentToLink.newRepresentToLink(rohs, comRohs);
                    	PersistenceServerHelper.manager.insert(link);
                	}
                	
                }
                
                //관련 물질 대표
                List<RohsData> representList = RohsQueryHelper.service.getRepresentToLinkList(oldRohs, "represent");
                for(RohsData representRohsData: representList){
                	ROHSMaterial representRohs = representRohsData.rohs;
                	if(representRohsData.isLatest()){
                		RepresentToLink link = RepresentToLink.newRepresentToLink(representRohs, rohs);
                    	PersistenceServerHelper.manager.insert(link);
                	}
                }
                		
                //관련 첨부파일
                copyROHSContHOlder(oldRohs, rohs);
                
                String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
                if("LC_Default_NonWF".equals(lifecycle)){
                	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
                	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
                }
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("approvalType", approvalType);
                CommonHelper.service.changeIBAValues(rohs, map);
                
                reOid = rohs.getPersistInfo().getObjectIdentifier().toString();
            }

            trx.commit();
            trx = null;
            data.setResult(true);
            data.setOid(reOid);
       } catch(Exception e) {
    	   data.setResult(false);
    	   data.setMessage(e.getLocalizedMessage());
    	   e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
        return data;
    }
	
	/**	RoHS 등록 Action
	 * @param map
	 * @param 
	 * @return result
	 */
	@Override
	public ResultData createRohsAction(HttpServletRequest request, HttpServletResponse response) {
		
		ResultData result = new ResultData();
		
		Transaction trx = new Transaction();
		ROHSMaterial rohs = null;
		try {
			trx.start();
			String lifecycle = StringUtil.checkNull(request.getParameter("lifecycle"));
	    	String rohsName = StringUtil.checkNull(request.getParameter("rohsName"));
			DocumentType docType = DocumentType.toDocumentType("$$ROHS");
			String rohsNumber = StringUtil.checkNull(request.getParameter("rohsNumber"));
			String manufacture = StringUtil.checkNull(request.getParameter("manufacture"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			// 문서 기본 정보 설정
			rohs = ROHSMaterial.newROHSMaterial();
			rohs.setName(rohsName);
			if("".equals(rohsNumber)){
				rohs.setNumber(getRohsNumberSeq(manufacture));
			}else{
				rohs.setNumber(rohsNumber);
			}
			
	        rohs.setDocType(docType);
			rohs.setDescription(description);
	        // 문서 분류쳬게 설정
	        String location = StringUtil.checkNull(request.getParameter("location"));
	        Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
	        FolderHelper.assignLocation((FolderEntry)rohs, folder);
	        
	        // 문서 Container 설정
	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
	        rohs.setContainer(e3psProduct);
	        
	        // 문서 lifeCycle 설정
	        LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
            
	        rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
	        
	        String[] roleTypes = request.getParameterValues("roleType");
	        if(roleTypes != null) {
		        for(String roleType: roleTypes) {
			        String file = StringUtil.checkNull(request.getParameter(roleType));
			        String fileType = StringUtil.checkNull(request.getParameter(roleType + "_fileType"));
			        String date = StringUtil.checkNull(request.getParameter(roleType + "_date"));
			        if(date.length()>0 && !isDateValid(date)){
			        	throw new Exception("발행일자 날짜 형식이 맞지 않습니다.");
			        }
			        String fileName = file.split("/")[1];
			        
			        HashMap<String, Object> map = new HashMap<String, Object>();
			        map.put("roleType", roleType);
			        map.put("file", file);
			        map.put("fileName", fileName);
			        map.put("fileType", fileType);
			        map.put("publicationDate", date);
			        
			        createROHSContHolder(rohs, map);
		        }
	        }
	        
            //관련 부품            
	        String[] partOids = request.getParameterValues("partOid");
			createROHSToPartLink(rohs, partOids);
			
			//관련 물질
			String[] rohsOids = request.getParameterValues("rohsOid");
			createROHSToROHSLink(rohs, rohsOids);
			
	        
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
            result.setResult(true);
            result.setOid(CommonUtil.getOIDString(rohs));
		} catch(Exception e) {
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
	
	/**	RoHS 리스트  Action
	 * @param longDescription
	 * @param 
	 * @return number
	 */
	@Override
	public Map<String,Object> listRohsAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = RohsQueryHelper.service.getListQuery(request, response);
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
	    
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) o[0];
			RohsData data = new RohsData(rohs);
			
			xmlBuf.append("<row id='"+ data.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.getManufactureDisplay(true) + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.createDate.substring(0, 10) + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.modifyDate.substring(0, 10) + "]]></cell>" );
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
	
	/** 관련 RoHS 첨부파일, 파일종류, 발행일
	 * @param rohsOid
	 * @param 
	 * @return list
	 */
	@Override
	public List<Map<String,Object>> getRohsContent(String oid) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		
		List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(oid);
		
		for(ROHSContHolder holder : holderList){
			
			ApplicationData data = holder.getApp();
			String url="<a href='";
			url = url +"/Windchill/jsp/common/DownloadGW.jsp?holderOid="+CommonUtil.getOIDString(rohs)+"&appOid="+CommonUtil.getOIDString(data);
			url = url+"'>"+holder.getFileName() ;
			url = url +"<a>";
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("fileDown", url);
			map.put("fileType", RohsUtil.getRohsDocTypeName(holder.getFileType()));
			map.put("fileDate", holder.getPublicationDate());
			map.put("fileAppOid", CommonUtil.getOIDString(data));
			map.put("fileOid", CommonUtil.getOIDString(holder));
			map.put("fileRole", data.getRole().toString());
			map.put("fileTypeCode", holder.getFileType());
			list.add(map);
		}
		return list;
	}

	@Override
	public ResultData updateRohsAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		ROHSMaterial new_material = null;
		try {
			
			trx.start();
			
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			
			if(oid.length() > 0) {
			
				ROHSMaterial old_material = (ROHSMaterial)CommonUtil.getObject(oid);
				new_material = (ROHSMaterial) RohsUtil.getWorkingCopy(old_material);
				new_material = (ROHSMaterial) PersistenceHelper.manager.refresh(new_material);
				String description = StringUtil.checkNull(request.getParameter("description"));
				new_material.setDescription(description);
				new_material = (ROHSMaterial)PersistenceHelper.manager.modify(new_material);
				/**
				 * 
				 *   첨부파일 관련 작업 수행
				 *   
				 */
				String[] roleTypes = request.getParameterValues("roleType");
				
				if(roleTypes != null) {
					for(String roleType : roleTypes) {
						String delocIds = StringUtil.checkNull(request.getParameter(roleType + "_delocIds"));
						
						/**
						 * 
						 *   기존 첨부파일 유지
						 *   
						 */
						if(delocIds.length() > 0) {
							String appOid = StringUtil.checkNull(request.getParameter(roleType+"_AppOid"));
							
							/**
							 * 
							 *   기존 첨부파일(ApplicationData)를 복사해서 새로운 ROHSContHolder 생성
							 *   
							 */
							if(appOid.length() > 0){
								// RoleType 에 따른 ROHSContHolder 카피
								
								String file = CommonContentHelper.service.copyApplicationData(appOid);
								String fileType = StringUtil.checkNull(request.getParameter(roleType + "_fileType"));
								String date = StringUtil.checkNull(request.getParameter(roleType + "_date"));
								
								String fileName = file.split("/")[1];
						        
						        ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
						        
						        ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)new_material, roleType, file,  false);
						        
						        holder.setFileName(fileName);
						        holder.setFileType(fileType);
						        holder.setPublicationDate(date);
						        holder.setApp(app);
						        holder.setRohs(new_material);
						        
						        PersistenceHelper.manager.save(holder);
						        
								
							}
							
							/**
							 *   이 부분에 도달하면 버그가 발생한것입니다.
							 */
							else {
								// RoleType 에 따른 ROHSContHolder 삭제
								//System.out.println("이 부분에 도달하면 버그가 발생한것입니다.");
							}
							
						}
						
						/**
						 * 
						 *   새로운 첨부파일 생성
						 *   
						 */
						
						else {
							// 신규 생성
							String file = StringUtil.checkNull(request.getParameter(roleType));
							String fileType = StringUtil.checkNull(request.getParameter(roleType + "_fileType"));
							String date = StringUtil.checkNull(request.getParameter(roleType + "_date"));

							if(file.length() > 0){
								String fileName = file.split("/")[1];
								
								ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
								
								ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)new_material, roleType, file,  false);
								
								holder.setFileName(fileName);
								holder.setFileType(fileType);
								holder.setPublicationDate(date);
								holder.setApp(app);
								holder.setRohs(new_material);
								
								PersistenceHelper.manager.save(holder);
								
							}
							
						}
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
				String[] partOids = request.getParameterValues("partOid");
				createROHSToPartLink(new_material, partOids);
				
				/**
				 * 
				 *   관련 물질 관련 작업 수행
				 */
				/* 관련 물질 링크 삭제 */
				deleteROHSToROHSLink(new_material);
				
				/* 관련 물질 링크 생성*/
				String[] rohsOids = request.getParameterValues("rohsOid");
	            createROHSToROHSLink(new_material, rohsOids);
				
	            Map<String,Object> map = new HashMap<String,Object>();
	            String manufacture = StringUtil.checkNull(request.getParameter("manufacture"));
	            map.put("manufacture", manufacture);
	            CommonHelper.service.changeIBAValues(new_material, map);
	            
            	String rohsName = StringUtil.checkNull(request.getParameter("rohsName"));
	            
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
			
			data.setResult(true);
			data.setOid(CommonUtil.getOIDString(new_material));
		} catch(Exception e) {
			e.printStackTrace();
			data.setMessage(e.getLocalizedMessage());
			data.setResult(false);
		} finally {
			if(trx != null){
				trx.rollback();
			}
		}
		
		return data;
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
		
		List<PartToRohsLink> list = RohsQueryHelper.service.getPartToRohsLinkList(rv);
		
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
		List<RepresentToLink> list =  RohsQueryHelper.service.getRepresentLink(represent);
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
		
		String oid = CommonUtil.getOIDString(oldRohs);
		List<ROHSContHolder> list = RohsQueryHelper.service.getROHSContHolder(oid);
		
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
	
	@Override
	public Map<String, Object> listRoHSPartAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		String partOid = StringUtil.checkNull(request.getParameter("partOid"));
		
		WTPart part = (WTPart)CommonUtil.getObject(partOid);
		
		list = RohsQueryHelper.service.childPartPutMap(part, list,0);
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
	    
	    HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	    int count = 1;
		for(int i=0; i<list.size(); i++){
			Map<String,Object> map = list.get(i);
			
			String oid = (String)map.get("partOid");
			String key = String.valueOf(count) + "_"+oid;
			WTPart supart = (WTPart)CommonUtil.getObject(oid);
			List<RohsData> rohslist = RohsQueryHelper.service.getPartToROHSList(supart);
			
			
			stateMap = RohsUtil.getRohsState(stateMap,oid);
			int rohsState = stateMap.get(oid);
			String state ="<img src='/Windchill/jsp/portal/images/tree/task_ready.gif'>";
			if(rohsState == RohsUtil.STATE_NOT_APPROVED){
				state ="<img src='/Windchill/jsp/portal/images/tree/task_red.gif'>";
			}else if(rohsState ==RohsUtil.STATE_NONE_ROHS){
				state ="<img src='/Windchill/jsp/portal/images/tree/task_orange.gif'>";
			}else if(rohsState ==RohsUtil.STATE_ALL_APPROVED){
				state ="<img src='/Windchill/jsp/portal/images/tree/task_complete.gif'>";
			}
			
			if(rohslist.size()>0){
				for(RohsData rohsData : rohslist){
					
					List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(rohsData.oid);
					
					if(holderList.size() > 0){
						
						for(ROHSContHolder rohsHolder : holderList){
							xmlBuf.append("<row id='" + CommonUtil.getOIDString(rohsHolder) +"'>");
							xmlBuf.append("<cell><![CDATA[" + (count++) + "]]></cell>");
							xmlBuf.append("<cell><![CDATA[" + map.get("level") + "]]></cell>");
							xmlBuf.append("<cell><![CDATA[" + map.get("partNumber") + "]]></cell>");
							xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + oid + "')>" + map.get("partName") + "</a>]]></cell>");
							//xmlBuf.append("<cell><![CDATA[" + map.get("partCreator") + "]]></cell>");
							//xmlBuf.append("<cell><![CDATA[" + map.get("partCreateDate") + "]]></cell>");
							xmlBuf.append("<cell><![CDATA[" + map.get("partState") + "]]></cell>");
							xmlBuf.append("<cell><![CDATA[" + state + "]]></cell>");
							xmlBuf.append("<cell><![CDATA["+rohsData.name+"]]></cell>");
							xmlBuf.append("<cell><![CDATA["+rohsData.getLifecycle()+"]]></cell>");
							xmlBuf.append("<cell><![CDATA["+rohsHolder.getFileName()+"]]></cell>");
							xmlBuf.append("<cell><![CDATA["+RohsUtil.getRohsDocTypeName(rohsHolder.getFileType())+"]]></cell>");
							xmlBuf.append("</row>" );
						}
					}else{
						key = String.valueOf(count) +"_" +oid;
						xmlBuf.append("<row id='" + key +"'>");
						xmlBuf.append("<cell><![CDATA[" + (count++) + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("level") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("partNumber") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + oid + "')>" + map.get("partName") + "</a>]]></cell>");
						//xmlBuf.append("<cell><![CDATA[" + map.get("partCreator") + "]]></cell>");
						//xmlBuf.append("<cell><![CDATA[" + map.get("partCreateDate") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("partState") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + state + "]]></cell>");
						xmlBuf.append("<cell><![CDATA["+rohsData.name+"]]></cell>");
						xmlBuf.append("<cell><![CDATA["+rohsData.getLifecycle()+"]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("</row>" );
					}
				}
			}else{
				xmlBuf.append("<row id='" + key +"'>");
				xmlBuf.append("<cell><![CDATA[" + (count++) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + map.get("level") + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + map.get("partNumber") + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + oid + "')>" + map.get("partName") + "</a>]]></cell>");
				//xmlBuf.append("<cell><![CDATA[" + map.get("partCreator") + "]]></cell>");
				//xmlBuf.append("<cell><![CDATA[" + map.get("partCreateDate") + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + map.get("partState") + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + state + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[]]></cell>");
				xmlBuf.append("<cell><![CDATA[]]></cell>");
				xmlBuf.append("<cell><![CDATA[]]></cell>");
				xmlBuf.append("<cell><![CDATA[]]></cell>");
				xmlBuf.append("</row>" );
			}
			
				
			
			
		}
		xmlBuf.append("</rows>");
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	@Override
	public Map<String, Object> listAUIRoHSPartAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String,Object>> partRohlist = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> partlist  = new ArrayList<Map<String,Object>>();
		//String partOid = StringUtil.checkNull(request.getParameter("partOid"));
		
		String partNumber = StringUtil.checkNull(request.getParameter("partNumber"));
		WTPart part = PartHelper.service.getPart(partNumber);
		String partOid = CommonUtil.getOIDString(part);
		//WTPart part = (WTPart)CommonUtil.getObject(partOid);
		if(part == null){
			return returnMap;
		}
		
		Map<String,Object>  productStateMap = RohsUtil.getProductRoHsState(partOid);
		returnMap.put("totalState", productStateMap.get("totalState"));
		returnMap.put("passCount", productStateMap.get("passCount"));
		returnMap.put("totalCount", productStateMap.get("totalCount"));
		returnMap.put("greenCount", productStateMap.get("greenCount"));
		returnMap.put("blackCount", productStateMap.get("blackCount"));
		returnMap.put("dumyCount", productStateMap.get("dumyCount"));
		returnMap.put("continueCount", productStateMap.get("continueCount"));
		returnMap.put("redCount", productStateMap.get("redCount"));
		returnMap.put("orangeCount", productStateMap.get("orangeCount"));
		returnMap.put("listCount", productStateMap.get("listCount"));
		returnMap.put("isDumy_SonPartsCount", productStateMap.get("isDumy_SonPartsCount"));
		
		partlist = RohsQueryHelper.service.childPartPutMap(part, partlist,0);
	    HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	   
	    int totalLevl = 0;
		for(int i=0; i<partlist.size(); i++){
			//Map<String,Object> partRohsMap = new HashMap<String, Object>();
			Map<String,Object> partMap = partlist.get(i);
			
			String oid = (String)partMap.get("partOid");
			int level = (Integer)partMap.get("level");
			
			if(totalLevl < level){
				totalLevl = level;
			}
			//String key = String.valueOf(count) + "_"+oid;
			WTPart supart = (WTPart)CommonUtil.getObject(oid);
			
			//System.out.println("1.supart =" + supart.getNumber() +"," + "Level"+level+"="+partMap.get("Level"+level));
			
			List<RohsData> rohslist = RohsQueryHelper.service.getPartToROHSList(supart);
			
			
			stateMap = RohsUtil.getRohsState(stateMap,oid);
			int rohsState = stateMap.get(oid);
			String state ="검은색";
			if(rohsState == RohsUtil.STATE_NOT_APPROVED){
				state ="빨강색";
			}else if(rohsState ==RohsUtil.STATE_NONE_ROHS){
				state ="주황색";
			}else if(rohsState ==RohsUtil.STATE_ALL_APPROVED){
				state ="녹색";
			}else if(rohsState ==RohsUtil.STATE_NOT_ROHS){
				state ="검은색";
			}
			
			if(rohslist.size()>0){
				for(RohsData rohsData : rohslist){
					//System.out.println("rohslist.size >0 .supart =" + supart.getNumber());
					List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(rohsData.oid);
					
					if(holderList.size() > 0){
						
						for(ROHSContHolder rohsHolder : holderList){
							Map<String,Object> partRohsMap = new HashMap<String, Object>();
							//partRohsMap1 = partMap;
							partRohsMap.put("rohsNumber", rohsData.number);
							partRohsMap.put("rohsName", rohsData.name);
							partRohsMap.put("rohsState", rohsState);
							partRohsMap.put("rohsStateName", state);
							partRohsMap.put("rohslifeState", rohsData.getLifecycle());
							partRohsMap.put("fileName", rohsHolder.getFileName());
							partRohsMap.put("docType", RohsUtil.getRohsDocTypeName(rohsHolder.getFileType()));
							
							partRohsMap = setPartROHMap(partRohsMap, partMap);
							//System.out.println("holderList.size >0 .supart =" + partRohsMap.get("partNumber") +", getFileName" +partRohsMap.get("fileName"));
							partRohlist.add(partRohsMap);
						}
					}else{
						//System.out.println("holderList.size =0 .supart =" + supart.getNumber());
						Map<String,Object> partRohsMap = new HashMap<String, Object>();
						partRohsMap.put("rohsNumber", rohsData.number);
						partRohsMap.put("rohsName", rohsData.name);
						partRohsMap.put("rohsState", rohsState);
						partRohsMap.put("rohslifeState", rohsData.getLifecycle());
						partRohsMap.put("rohsStateName", state);
						partRohsMap = setPartROHMap(partRohsMap, partMap);
						partRohlist.add(partRohsMap);
					}
				}
			}else{
				//System.out.println("rohslist.size =0 .supart =" + supart.getNumber());
				Map<String,Object> partRohsMap = new HashMap<String, Object>();
			
				partRohsMap.put("rohsState", rohsState);
				partRohsMap.put("rohsStateName", state);
				partRohsMap = setPartROHMap(partRohsMap, partMap);
				partRohlist.add(partRohsMap);
			}
			
		}
		
		returnMap.put("totalLevel", totalLevl);
		returnMap.put("partRohlist", partRohlist);
		/*
		System.out.println("list =" + partRohlist.size());
		
		for(int i=0; i<partRohlist.size(); i++){
			Map<String,Object> partMap2 = partRohlist.get(i);
			System.out.println("partRohlist.get(i) =" + partRohlist.get(i));
			//System.out.println(partMap2.get("fileName")+"," + partMap2.get("docType"));
		}
		*/
		return returnMap;
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
	public Map<String, Object> listRoHSProductAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] partOids = request.getParameterValues("partOids");
		
		int count = 1;
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
	    
		for(String partOid : partOids) {
			WTPart part = (WTPart)CommonUtil.getObject(partOid);
			PartData data = new PartData(part);
			
			xmlBuf.append("<row id='" + partOid +"'>");
			xmlBuf.append("<cell><![CDATA[" + count + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.name + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + DateUtil.subString(data.getCreateDate(), 0, 10) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
			Map<String, Object> dataMap = RohsUtil.getProductRoHsState(partOid);
			Double totalState = (Double) dataMap.get("totalState");
			Double totalCount = (Double) dataMap.get("totalCount");
			Double passCount = (Double) dataMap.get("passCount");
			xmlBuf.append("<cell><![CDATA[" + totalState + " % ("+passCount+"/"+totalCount+")]]></cell>");
			xmlBuf.append("</row>" );
		}

		xmlBuf.append("</rows>");
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("xmlString"      , xmlBuf);
		
		return result;
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
                List<PartToRohsLink> partList = RohsQueryHelper.service.getPartToRohsLinkList(rohs);
				if (isDelete && partList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질과 연계된 품목이 존재합니다.");
				}
				
				// 관련물질
				List<RepresentToLink> rohsList = RohsQueryHelper.service.getRepresentLink(rohs);
				if (isDelete && rohsList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질과 연계된 물질이 존재합니다.");
				}
				
        		// 관련 첨부파일
				List<ROHSContHolder> contentList = RohsQueryHelper.service.getROHSContHolder(oid);
				if (isDelete && contentList.size() > 0) {
					isDelete = false;
					result = false;
					msg = Message.get("물질에 첨부파일이 존재합니다.");
				}
        		
				if (isDelete) {
					WFItemHelper.service.deleteWFItem(rohs);
					PersistenceHelper.manager.delete(rohs);
				}
                trx.commit();
            }
            trx = null;
        } finally {
            if(trx!=null) trx.rollback();
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
				list = RohsQueryHelper.service.getRepresentToLinkList(rohs,roleType);
			}else if("part".equals(module)){
				WTPart part = (WTPart)CommonUtil.getObject(oid);
				list = RohsQueryHelper.service.getPartToROHSList(part);
			}else {
				list = new ArrayList<RohsData>();
			}
		}else {
			list = new ArrayList<RohsData>();
		}
		//System.out.println("include_RohsView ObjectComarator START =" + list.size());
		Collections.sort(list, new ObjectComarator());
		//System.out.println("include_RohsView ObjectComarator end");
		return list;
	}
	
	@Override
	public void revisePartToROHSLink(WTPart oldPart,WTPart newPart) throws Exception{
		
		List<PartToRohsLink> linkList = RohsQueryHelper.service.getPartToRohsLinkList(oldPart);
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
	
	@Override
	public ResultData duplicateName(String rohsName) {
		ResultData data = new ResultData();
		
		try {
			int count = RohsQueryHelper.service.duplicateName(rohsName);
			
			data.setMessage(count + Message.get("개가 존재합니다."));
		} catch(Exception e) {
			e.printStackTrace();
			data.setMessage(e.getLocalizedMessage());
		}
		return data;
	}
	
	@Override
	public ResultData copyRohsAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			
			trx.start();
			
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			
			ROHSMaterial orgRohs = (ROHSMaterial)CommonUtil.getObject(oid);
			
			String lifecycle = StringUtil.checkNull(request.getParameter("lifecycle"));
	    	String rohsName = StringUtil.checkNull(request.getParameter("rohsName"));
			DocumentType docType = DocumentType.toDocumentType("$$ROHS");
			String rohsNumber = StringUtil.checkNull(request.getParameter("rohsNumber"));
			String manufacture = StringUtil.checkNull(request.getParameter("manufacture"));
			
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
	        String location = StringUtil.checkNull(request.getParameter("location"));
	        Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
	        FolderHelper.assignLocation((FolderEntry)rohs, folder);
	        
	        // 문서 Container 설정
	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
	        rohs.setContainer(e3psProduct);
	        
	        // 문서 lifeCycle 설정
	        LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
            
	        rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
	        
	        //관련 품목
	        /*
	        List<PartToRohsLink> partList = RohsQueryHelper.service.getPartToRohsLinkList(orgRohs);
	        for(PartToRohsLink link : partList){
	        	WTPart part = (WTPart) link.getRoleAObject();
	        	PartToRohsLink partLink = PartToRohsLink.newPartToRohsLink(part, rohs);
	        	PersistenceServerHelper.manager.insert(partLink);
	        }
			*/
	        //관련 물질 composition
	        List<RepresentToLink> rohsList = RohsQueryHelper.service.getRepresentLink(orgRohs);
	        for(RepresentToLink rohsLink : rohsList){
	        	ROHSMaterial comRohs = (ROHSMaterial) rohsLink.getRoleBObject();
	        	if(VersionHelper.service.isLastVersion(comRohs)){
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
	        
	        data.setResult(true);
	        
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
	
	@Override
	public String createPackageRoHSAction(HttpServletRequest request, HttpServletResponse response) {
		Transaction trx = new Transaction();
		
		Map<String, List<Map<String,String>>> gridMap = new HashMap<String, List<Map<String,String>>>();
		
		boolean validation = true;
		int failCount = 0;
		
		Map<String,String> fileMap = new HashMap<String,String>();
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		try {
			trx.start();
			
			FileRequest req = new FileRequest(request);
			String excelFile = req.getFileLocation("excelFile");
			
			File file = new File(excelFile);
			XSSFWorkbook workbook = POIUtil.getWorkBook(file);
			XSSFSheet rohsSheet = POIUtil.getSheet(workbook, 0);
			XSSFSheet attachSheet = POIUtil.getSheet(workbook, 1);
			
			Map<String,Map<String,String>> rohsMap = readRoHSSheetData(rohsSheet);
			Map<String,List<Map<String,String>>> attachMap = readAttachSheetData(attachSheet);
			
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
			
			Set<String> keys = rohsMap.keySet();
			
			//20161110 PJT EDIT Start
			String[] rohsCountArray = new String[keys.size()];
			
			int keyCount = 0;
			for(String key : keys) {
				Map<String,String> rohs = rohsMap.get(key);
				String rohsName = StringUtil.checkNull(rohs.get("rohsName"));
				List<Map<String,String>> attach = attachMap.get(key);
				//System.out.println("attach!=null"+(null!=attach));
				if(null!=attach){
					rohsCountArray[keyCount] = rohsName + ";"+attach.size();
					//System.out.println("rohsCountArray["+keyCount+"]="+rohsCountArray[keyCount]);
				}
				keyCount++;
			}
			//20161110 PJT EDIT End
			String attachfail = "";String fail = "";
			for(String key : keys) {
			
				List<Map<String,String>> gridList = null;
				
				 fail = "";
				validation = true;
				
				Map<String,String> rohs = rohsMap.get(key);
				List<Map<String,String>> attach = attachMap.get(key);
				
				// 결재 방식
				String lifecycleName = StringUtil.checkNull(rohs.get("lifecycleName"));
				String lifecycle = "";
				if(lifecycleName.length() == 0) {
					validation = false;
					fail += Message.get("결재방식이 없습니다.");
				}else {
					if("기본결재".equals(lifecycleName)) {
						lifecycle = "LC_Default";
					}else if("일괄결재".equals(lifecycleName)){
						lifecycle = "LC_Default_NonWF";
					}else if("선택".equals(lifecycleName)) {
						validation = false;
						fail += Message.get("결재방식이 선택되지 않았습니다.");
					}
				}
				rohs.put("lifecycle", lifecycle);
				
				// 협력업체
				String manufactureName = StringUtil.checkNull(rohs.get("manufactureName"));
				String manufacture = StringUtil.checkNull(rohs.get("manufacture"));
				if(manufacture.length() == 0) {
					validation = false;
					fail += Message.get("협력업체가 선택되지 않았습니다.");
				}else {
					NumberCode code = NumberCodeHelper.service.getNumberCode(IBAKey.IBA_MANUFACTURE, manufacture);
					if(code == null) {
						validation = false;
						fail += Message.get("등록되지 않은 렵력업체 코드입니다.");
					}
				}
				
				// 물질번호
				String rohsNumber = StringUtil.checkNull(rohs.get("rohsNumber"));
				
				// 물질명
				String rohsName = StringUtil.checkNull(rohs.get("rohsName"));
				if(rohsName.length() == 0) {
					validation = false;
					fail += Message.get("물질명이 입력되지 않았습니다.");
				}
				
				ROHSMaterial material = null;
				String rohsOid = "";
				String fontColor = "black";
				
				if(validation) {
					
					ResultData resultData = createAction(rohs);
					//System.out.println("resultData Crate ERROR : " + resultData.isResult());
					if(resultData.isResult()) {
						rohsOid = resultData.oid;
						fontColor = "black";
						fail = resultData.message;
						material = (ROHSMaterial)CommonUtil.getObject(rohsOid);
						rohsNumber = material.getNumber();
					}else {
						
						fail = resultData.message;
						//System.out.println("validation Crate ERROR : " + fail);
						validation = false;
						fontColor = "red";
						failCount = failCount + 1;
					}
				}else {
					fontColor = "red";
					failCount = failCount + 1;
				}
				
				int attachCount = 1;
				
				String id = "";
				String fileTypeName = "";
				String fileType = "";
				String publicationDate = "";
				String fileName = "";
				
				if(attach != null && attach.size() > 0) {
					
					for(Map<String,String> aMap : attach) {
						// 파일구분
						
						attachfail = "";
						fileTypeName = StringUtil.checkNull(aMap.get("fileTypeName"));
						fileType = StringUtil.checkNull(aMap.get("fileType"));
						
						if(fileType.length() == 0) {
							validation = false;
							attachfail += Message.get("파일구분이 선택되지 않았습니다.");
						}else {
							
							boolean check = checkFileType(fileType);
							if(!check) {
								validation = false;
								attachfail += Message.get("등록된 파일구분코드가 아닙니다.");
							}
						}
						
						
						// 발행일
						publicationDate = StringUtil.checkNull(aMap.get("publicationDate"));
						//20161108 PJT EDIT
						if(fileTypeName.equals("TR")){
							if(publicationDate.length() == 0) {
								validation = false;
								if(!attachfail.contains(Message.get("발행일이 입력되지 않았습니다.")))
									attachfail += Message.get("발행일이 입력되지 않았습니다.");
							}
							
							if(publicationDate.length() > 0 && !isDateValid(publicationDate)){
								validation = false;
								if(!attachfail.contains(Message.get("발행일이 형식에 맞지 않습니다. 예:)2018-05-01")))
									attachfail += Message.get("발행일이 형식에 맞지 않습니다. 예:)2018-05-01");
							}
						}
						
						
						// 파일명
						fileName = StringUtil.checkNull(aMap.get("fileName"));
						String primary = "";
						if(fileName.length() == 0) {
							validation = false;
							if(!attachfail.contains(Message.get("파일명이 입력되지 않았습니다.")))
								attachfail += Message.get("파일명이 입력되지 않았습니다.");
						}else {
							//System.out.println("  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.     " + fileName.toUpperCase());
							if(fileMap.get(fileName.toUpperCase()) == null) {
								validation = false;
								if(!attachfail.contains(Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.")))
									attachfail += Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							}else {
								primary = fileMap.get(fileName.toUpperCase());
								fileMap.remove(fileName.toUpperCase());
							}
						}
						
						if(validation) {
							
							if(material != null) {
								
								//20161110 PJT EDIT Start
								boolean isCountOver = false;
								ResultData resultData =null;
								for (int i = 0; i < rohsCountArray.length; i++) {
									//System.out.println("rohsCountArray["+i+"]="+rohsCountArray[i]);
									if(null!=rohsCountArray[i]){
										String[] tmp = rohsCountArray[i].split(";");
										String rohsN = tmp[0];
										//System.out.println("rohsN="+rohsN);
										//System.out.println("tmp[1]="+tmp[1]);
										int rohsAttachCount = Integer.parseInt(tmp[1]); 
										//System.out.println("rohsN="+rohsN+"\trohsName="+rohsName+"\trohsAttachCount"+rohsAttachCount);
										if(rohsN.equals(rohsName) && rohsAttachCount>20){
											isCountOver = true;
											attachCount =0;
											break;
											
										}
									}
								}
								try{
									if(attachCount<=20 && !isCountOver){
										resultData = createAttachAction(material, aMap, primary, attachCount);
									}
									if(resultData.result) {
										if(!attachfail.contains(Message.get("첨부파일 등록 성공!!!!!")))
											attachfail += "\t첨부파일 등록 성공!!!!!";
									}else {
										attachfail += resultData.message;
										validation = false;
										fontColor = "red";
										failCount = failCount + 1;
									}
								}catch(NullPointerException npe){
									attachfail += rohsName+"의 첨부파일이 한계 20개 이상입니다.";
									validation = false;
									fontColor = "red";
									failCount = failCount + 1;
								}
								//20161110 PJT EDIT End
							}else {
								validation = false;
								fontColor = "red";
								failCount = failCount + 1;
							}
							
						}else {
							fontColor = "red";
							failCount = failCount + 1;
						}
						
						id =  key + "_" + rohsOid + "_" + attachCount;
						
						Map<String,String> rMap = new HashMap<String,String>();
						rMap.put("id", id);
						rMap.put("oid", rohsOid);
						rMap.put("key", key);
						rMap.put("lifecycleName", lifecycleName);
						rMap.put("manufactureName", manufactureName);
						rMap.put("manufacture", manufacture);
						rMap.put("rohsNumber", rohsNumber);
						rMap.put("rohsName", rohsName);
						rMap.put("fileTypeName", fileTypeName);
						rMap.put("fileType", fileType);
						rMap.put("publicationDate", publicationDate);
						rMap.put("fileName", fileName);
						rMap.put("fontColor", fontColor);
						rMap.put("validation", String.valueOf(validation));
						rMap.put("fail", fail+"\t"+attachfail);
						
						if(gridMap.get(key) == null) {
							gridList = new ArrayList<Map<String,String>>();
						}else {
							gridList = gridMap.get(key);
						}
						
						gridList.add(rMap);
						gridMap.put(key, gridList);
						
						attachCount++;
					}
				} else {
					
					id =  key + "_" + rohsOid + "_" + attachCount;

					Map<String,String> rMap = new HashMap<String,String>();
					rMap.put("id", id);
					rMap.put("oid", rohsOid);
					rMap.put("key", key);
					rMap.put("lifecycleName", lifecycleName);
					rMap.put("manufactureName", manufactureName);
					rMap.put("manufacture", manufacture);
					rMap.put("rohsNumber", rohsNumber);
					rMap.put("rohsName", rohsName);
					rMap.put("fileTypeName", fileTypeName);
					rMap.put("fileType", fileType);
					rMap.put("publicationDate", publicationDate);
					rMap.put("fileName", fileName);
					rMap.put("fontColor", fontColor);
					rMap.put("validation", String.valueOf(validation));
					rMap.put("fail", fail);
					
					if(gridMap.get(key) == null) {
						gridList = new ArrayList<Map<String,String>>();
					}else {
						gridList = gridMap.get(key);
					}
					
					gridList.add(rMap);
					gridMap.put(key, gridList);
				}
			}
			
			Set<String> gKeys = gridMap.keySet();
			
			for(String key : gKeys) {
				
				List<Map<String,String>> gList = gridMap.get(key);
				
				int rowSpan = gList.size();
				
				for(int i=0; i<rowSpan; i++) {
					
					Map<String,String> map = gList.get(i);
					
					if(i==0) {
						
						String name = map.get("rohsName");
						
						if(failCount == 0 ) {
							name = "<a href=javascript:openView('" + map.get("oid") + "')>" + name + "</a>";
						}
						
						xmlBuf.append("<row id='" + map.get("id") + "'>");
						xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + map.get("key") + "]]></cell>");
						xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + map.get("lifecycleName") + "]]></cell>");
						xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + map.get("manufactureName") + "]]></cell>");
						xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + map.get("manufacture") + "]]></cell>");
						xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + map.get("rohsNumber") + "]]></cell>");
						if(failCount == 0 ) {
							xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + name + "]]></cell>");
						}else{
							xmlBuf.append("<cell rowSpan='" + rowSpan + "'><![CDATA[" + WebUtil.getHtml(name) + "]]></cell>");
						}
						xmlBuf.append("<cell><![CDATA[" + map.get("fileTypeName") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("fileType") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("publicationDate") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("fileName") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[<font color='" + map.get("fontColor") + "'>" + map.get("validation") + "</font>]]></cell>");
						xmlBuf.append("<cell><![CDATA[<font color='" + map.get("fontColor") + "'>" + map.get("fail") + "</font>]]></cell>");
			        	xmlBuf.append("</row>");
					}else {
						xmlBuf.append("<row id='" + map.get("id") + "'>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("fileTypeName") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("fileType") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("publicationDate") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[" + map.get("fileName") + "]]></cell>");
						xmlBuf.append("<cell><![CDATA[<font color='" + map.get("fontColor") + "'>" + map.get("validation") + "</font>]]></cell>");
						xmlBuf.append("<cell><![CDATA[<font color='" + map.get("fontColor") + "'>" + map.get("fail") + "</font>]]></cell>");
			        	xmlBuf.append("</row>");
					}
				}
			}
			
			if(failCount == 0) {
				trx.commit();
				trx = null;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			xmlBuf.append("<row id='error'>");
			xmlBuf.append("<cell colspan='8'><![CDATA[" + e.getLocalizedMessage() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
        	xmlBuf.append("</row>\n");
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		xmlBuf.append("</rows>");
		
		//System.out.println("###############################################################");
		//System.out.println(xmlBuf);
	//	String sss = "<?xml version='1.0' encoding='iso-8859-1'?><rows><row id='3_com.e3ps.rohs.ROHSMaterial:8800949_1'><cell rowSpan='2'><![CDATA[3]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0001]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8800949')>N-SF11_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2015-05-04]]></cell><cell><![CDATA[N-SF11_옵틱라인즈_ETC_2015.05.04.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='3_com.e3ps.rohs.ROHSMaterial:8800949_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2014-07-16]]></cell><cell><![CDATA[N-SF11_옵틱라인즈_RoHS_2014.07.16.PDF]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='2_com.e3ps.rohs.ROHSMaterial:8800970_1'><cell rowSpan='2'><![CDATA[2]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0002]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8800970')>Ag_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2015-01-16]]></cell><cell><![CDATA[Ag_옵틱라인즈_MSDS_2015.01.16.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='2_com.e3ps.rohs.ROHSMaterial:8800970_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2014-11-03]]></cell><cell><![CDATA[Ag_옵틱라인즈_RoHS_2014.11.03.PDF]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='1_com.e3ps.rohs.ROHSMaterial:8801012_1'><cell rowSpan='2'><![CDATA[1]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0004]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801012')>SiO2_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2013-07-16]]></cell><cell><![CDATA[SiO2_옵틱라인즈_MSDS_2013.07.16.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='1_com.e3ps.rohs.ROHSMaterial:8801012_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2014-10-13]]></cell><cell><![CDATA[SiO2_옵틱라인즈_RoHS_2014.10.13.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='10_com.e3ps.rohs.ROHSMaterial:8800991_1'><cell rowSpan='2'><![CDATA[10]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0003]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8800991')>N-BK7(H-K9L)_옵틱라인즈</a></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2013-11-15]]></cell><cell><![CDATA[H-K9L_옵틱라인즈_RoHS_2013.11.15.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='10_com.e3ps.rohs.ROHSMaterial:8800991_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[N-BK7_옵틱라인즈_ETC_2011.07.21.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='7_com.e3ps.rohs.ROHSMaterial:8801033_1'><cell rowSpan='2'><![CDATA[7]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0005]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801033')>NG4_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2013-08-01]]></cell><cell><![CDATA[NG4_옵틱라인즈_ETC_2013.08.01.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='7_com.e3ps.rohs.ROHSMaterial:8801033_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2014-08-07]]></cell><cell><![CDATA[NG4_옵틱라인즈_RoHS_2014.08.07.PDF]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='6_com.e3ps.rohs.ROHSMaterial:8801054_1'><cell rowSpan='1'><![CDATA[6]]></cell><cell rowSpan='1'><![CDATA[일괄결재]]></cell><cell rowSpan='1'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='1'><![CDATA[MF114]]></cell><cell rowSpan='1'><![CDATA[MF114_0006]]></cell><cell rowSpan='1'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801054')>Product of PHOTOP_옵틱라인즈</a></cell><cell><![CDATA[보증서]]></cell><cell><![CDATA[DOC]]></cell><cell><![CDATA[2013-08-13]]></cell><cell><![CDATA[PHOTOP_옵틱라인즈_DoC_2013.08.13.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='5_com.e3ps.rohs.ROHSMaterial:8801072_1'><cell rowSpan='2'><![CDATA[5]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0007]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801072')>L-BAL42P_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2008-05-30]]></cell><cell><![CDATA[L-BAL42_옵틱라인즈_MSDS_2008.05.30.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='5_com.e3ps.rohs.ROHSMaterial:8801072_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2013-11-05]]></cell><cell><![CDATA[L-BAL42P_옵틱라인즈_RoHS_2013.11.05.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='4_com.e3ps.rohs.ROHSMaterial:8801093_1'><cell rowSpan='2'><![CDATA[4]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0008]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801093')>ZrO2_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2013-03-28]]></cell><cell><![CDATA[ZrO2_옵틱라인즈_MSDS_2013.03.28.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='4_com.e3ps.rohs.ROHSMaterial:8801093_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2015-01-05]]></cell><cell><![CDATA[ZrO2_옵틱라인즈_RoHS_2015.01.05.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='9_com.e3ps.rohs.ROHSMaterial:8801114_1'><cell rowSpan='2'><![CDATA[9]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0009]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801114')>AL2O3(SAPPHIRE)_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2013-05-08]]></cell><cell><![CDATA[Al2O3_옵틱라인즈_MSDS_2013.05.08.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='9_com.e3ps.rohs.ROHSMaterial:8801114_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2012-06-20]]></cell><cell><![CDATA[Al2O3_옵틱라인즈_RoHS_2012.06.20.PDF]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='8_com.e3ps.rohs.ROHSMaterial:8801135_1'><cell rowSpan='2'><![CDATA[8]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0010]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801135')>Ta2O5_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[2013-06-26]]></cell><cell><![CDATA[Ta2O5_옵틱라인즈_MSDS_2013.06.26.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='8_com.e3ps.rohs.ROHSMaterial:8801135_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2012-12-18]]></cell><cell><![CDATA[Ta2O5_옵틱라인즈_RoHS_2012.12.18.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='11_com.e3ps.rohs.ROHSMaterial:8801156_1'><cell rowSpan='2'><![CDATA[11]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0011]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801156')>MACOR GLASS(Ceramic)_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[Alumina_Hydrate_옵틱라인즈_MSDS.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='11_com.e3ps.rohs.ROHSMaterial:8801156_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2013-12-18]]></cell><cell><![CDATA[Ceramic(MACOR_GLASS)_옵틱라인즈_RoHS_2013.12.18.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='12_com.e3ps.rohs.ROHSMaterial:8801177_1'><cell rowSpan='2'><![CDATA[12]]></cell><cell rowSpan='2'><![CDATA[일괄결재]]></cell><cell rowSpan='2'><![CDATA[OPTIC LINES]]></cell><cell rowSpan='2'><![CDATA[MF114]]></cell><cell rowSpan='2'><![CDATA[MF114_0012]]></cell><cell rowSpan='2'><a href=javascript:openView('com.e3ps.rohs.ROHSMaterial:8801177')>TI3O5_옵틱라인즈</a></cell><cell><![CDATA[MSDS, 성분분석표]]></cell><cell><![CDATA[MSDS]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[Ti3O5_옵틱라인즈_MSDS.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row><row id='12_com.e3ps.rohs.ROHSMaterial:8801177_2'><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[]]></cell><cell><![CDATA[시험성적서]]></cell><cell><![CDATA[TR]]></cell><cell><![CDATA[2012-11-02]]></cell><cell><![CDATA[Ti3O5_옵틱라인즈_RoHS_2012.11.02.pdf]]></cell><cell><![CDATA[<font color='black'>true</font>]]></cell><cell><![CDATA[<font color='black'>Create Success !!!!</font>]]></cell></row></rows>";
		//return sss;
		return xmlBuf.toString();
		
	}
	
	private Map<String, Map<String,String>> readRoHSSheetData(XSSFSheet rohsSheet) {
		Map<String, Map<String,String>> rohsMap = new HashMap<String, Map<String,String>> ();
		
		for(int i = 1; i < POIUtil.getSheetRow(rohsSheet); i++) {
			Map<String,String> map = new HashMap<String,String>();
			
			XSSFRow row = rohsSheet.getRow(i);
			
			// 순번
			String no = StringUtil.checkNull(POIUtil.getRowStringValue(row,0));
			
			if(no.length() > 0) {
				
				// 결재 방식
				String lifecycleName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 1));
				map.put("lifecycleName", lifecycleName);
				
				// 협력업체
				String manufactureName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
				String manufacture = StringUtil.checkNull(POIUtil.getRowStringFomularValue(row, 3));
				map.put("manufactureName", manufactureName);
				map.put("manufacture", manufacture);
				
				// 물질번호
				String rohsNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 4));
				map.put("rohsNumber", rohsNumber);
				
				// 물질명
				String rohsName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 5));
				map.put("rohsName", rohsName);
				
				// 설명
				String description = StringUtil.checkNull(POIUtil.getRowStringValue(row, 6));
				map.put("description", description);
				
				rohsMap.put(no, map);
			}
		}
		
		return rohsMap;
	}
	
	private Map<String, List<Map<String,String>>> readAttachSheetData(XSSFSheet attachSheet) {
		
		Map<String, List<Map<String,String>>> attachMap = new HashMap<String, List<Map<String,String>>>();
		
		for(int i = 1; i < POIUtil.getSheetRow(attachSheet); i++) {
			
			Map<String,String> map = new HashMap<String,String>();
			
			XSSFRow row = attachSheet.getRow(i);
			
			
			// 관련 물질
			String index = StringUtil.checkNull(POIUtil.getRowStringValue(row,0));
			
			// 파일구분
			String fileTypeName = StringUtil.checkNull(POIUtil.getRowStringValue(row,1));
			String fileType = StringUtil.checkNull(POIUtil.getRowStringFomularValue(row,2));
			map.put("fileTypeName", fileTypeName);
			map.put("fileType", fileType);
			
			// 발행일
			String publicationDate = StringUtil.checkNull(POIUtil.getRowStringValue(row,3));
			map.put("publicationDate", publicationDate);
			
			// 파일명
			String fileName = StringUtil.checkNull(POIUtil.getRowStringValue(row,4));
			map.put("fileName", fileName);
			
			List<Map<String,String>> list = null;
			if(attachMap.get(index) == null) {
				list = new ArrayList<Map<String,String>>();
			}else {
				list = attachMap.get(index);
			}
			
			list.add(map);
			attachMap.put(index, list);
		}
		
		return attachMap;
	}
	
	private ResultData createAction(Map<String,String> map) {
		
		ResultData data = new ResultData();
		ROHSMaterial rohs = null;
		String rohsNumber = StringUtil.checkNull((String)map.get("rohsNumber"));
		try {
			String lifecycle = StringUtil.checkNull((String)map.get("lifecycle"));
			String rohsName = StringUtil.checkNull((String)map.get("rohsName"));
			DocumentType docType = DocumentType.toDocumentType("$$ROHS");
			
			String manufacture = StringUtil.checkNull((String)map.get("manufacture"));
			String description = StringUtil.checkNull((String)map.get("description"));
			// 문서 기본 정보 설정
			rohs = ROHSMaterial.newROHSMaterial();
			rohs.setName(rohsName);
			if("".equals(rohsNumber)){
				rohs.setNumber(getRohsNumberSeq(manufacture));
			}else{
				rohs.setNumber(rohsNumber);
			}
			
			rohs.setDocType(docType);
			rohs.setDescription(description);
			
			// 문서 분류쳬게 설정
			String location = StringUtil.checkNull("/Default/ROHS");
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry)rohs, folder);
	
			// 문서 Container 설정
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			rohs.setContainer(e3psProduct);
	
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
	
			rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
	
			String approvalType = AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
			if("LC_Default_NonWF".equals(lifecycle)){
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
				approvalType = AttributeKey.CommonKey.COMMON_BATCH;
			}
			
			Map<String,Object> ibaMap = new HashMap<String,Object>();
			
			ibaMap.put("approvalType", approvalType);
			ibaMap.put("manufacture", manufacture);
			CommonHelper.service.changeIBAValues(rohs, ibaMap);
			
			data.setResult(true);
			data.setMessage("문서 부분 등록 완료!!!! 첨부파일 진행 중 *****");
			data.setOid(CommonUtil.getOIDString(rohs));
			
		} catch(Exception e) {
			//e.printStackTrace();
			data.setResult(false);
			data.setMessage("생성시 에러가 발생 하였습니다.[중복확인]");
			//data.setMessage(e.getMessage());
		}
		
		return data;
	}
	
	private ResultData createAttachAction(ROHSMaterial material, Map<String,String> aMap, String primary, int count) {
		ResultData data = new ResultData();
		
		try {
			
			//System.out.println("===============================   " + primary);
			
			ROHSContHolder holder = ROHSContHolder.newROHSContHolder();
	        
			String roleType = "ROHS" + count;
			
			String fileName = (String)aMap.get("fileName");
			String fileType = (String)aMap.get("fileType");
			String publicationDate = (String)aMap.get("publicationDate");

			ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)material, roleType, primary,  false);

			holder.setFileName(fileName);
			holder.setFileType(fileType);
			holder.setPublicationDate(publicationDate);
			holder.setApp(app);
			holder.setRohs(material);

			PersistenceHelper.manager.save(holder);

			data.setResult(true);
			data.setOid(CommonUtil.getOIDString(holder));
			
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}
		
		return data;
	}
	
	private boolean checkFileType(String fileType) {
		
		Map<String,String> map = new HashMap<String,String>();
		
		String[] fileCodes = AttributeKey.RohsKey.ROHS_CODE;
		
		for(String fileCode : fileCodes) {
			map.put(fileCode, fileCode);
		}
		
		return map.get(fileType)!=null;
		
	}
	
	@Override
	public String createPackageRoHSLinkAction(HttpServletRequest request, HttpServletResponse response) {
		Transaction trx = new Transaction();
		
		boolean validation = true;
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
			
			for(int i = 1; i < POIUtil.getSheetRow(sheet); i++) {
				
				XSSFRow row = sheet.getRow(i);
				String fail = "";
				
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
							partOid = CommonUtil.getVROID(part);
						}
					}
					
					// 물질 코드
					String rohsNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
					ROHSMaterial rohs = null;
					String rohsOid = "";
					if(rohsNumber.length() == 0) {
						validation = false;
						fail += Message.get("물질코드가 입력되지 않았습니다.");
					}else {
						rohs = getRoHs(rohsNumber);
						if(rohs == null) {
							validation = false;
							fail += Message.get("물질이 존재하지 않습니다.");
						}else {
							rohsOid = CommonUtil.getOIDString(rohs);
						}
					}
					
					String fontColor = "black";
					
					if(validation) {
						try {
							if(!RohsQueryHelper.service.duplicateNumber(partOid,rohsNumber)){
								PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, rohs);
								PersistenceHelper.manager.save(link);
							}else {
								fail += Message.get("이미 연관관계가 되어 있습니다.");
							}
						} catch(Exception e) {
							e.printStackTrace();
							validation = false;
							fail += e.getLocalizedMessage();
							fontColor = "red";
							failCount++;
						}
					}else {
						failCount++;
						fontColor = "red";
					}
					
					if(partOid.length() > 0) {
						partNumber = "<a href=javascript:openView('" + partOid + "')>" + partNumber + "</a>";
					}
					
					if(rohsOid.length() > 0) {
						rohsNumber = "<a href=javascript:openView('" + rohsOid + "')>" + rohsNumber + "</a>";
					}
					
					xmlBuf.append("<row id='" + no + "'>");
					xmlBuf.append("<cell><![CDATA[" + (i+1) + "]]></cell>");
					xmlBuf.append("<cell><![CDATA[" + partNumber + "]]></cell>");
					xmlBuf.append("<cell><![CDATA[" + rohsNumber + "]]></cell>");
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
	    	
			DocumentType docType = DocumentType.toDocumentType((String) params.get("docType"));
			String rohsNumber = StringUtil.checkNull((String) params.get("rohsNumber"));
			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
			String description = StringUtil.checkNull((String) params.get("description"));
			// 문서 기본 정보 설정
			rohs = ROHSMaterial.newROHSMaterial();
			rohs.setName(rohsName);
			if("".equals(rohsNumber)){
				rohs.setNumber(getRohsNumberSeq(manufacture));
			}else{
				rohs.setNumber(rohsNumber);
			}
			
	        rohs.setDocType(docType);
			rohs.setDescription(description);
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
            
	        rohs.setOwnership(Ownership.newOwnership(SessionHelper.manager.getPrincipalReference()));
	        rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
	        
//	        String[] roleTypes = request.getParameterValues("roleType");
//	        if(roleTypes != null) {
//		        for(String roleType: roleTypes) {
//			        String file = StringUtil.checkNull(request.getParameter(roleType));
//			        String fileType = StringUtil.checkNull(request.getParameter(roleType + "_fileType"));
//			        String date = StringUtil.checkNull(request.getParameter(roleType + "_date"));
//			        if(date.length()>0 && !isDateValid(date)){
//			        	throw new Exception("발행일자 날짜 형식이 맞지 않습니다.");
//			        }
//			        String fileName = file.split("/")[1];
//			        
//			        HashMap<String, Object> map = new HashMap<String, Object>();
//			        map.put("roleType", roleType);
//			        map.put("file", file);
//			        map.put("fileName", fileName);
//			        map.put("fileType", fileType);
//			        map.put("publicationDate", date);
//			        
//			        createROHSContHolder(rohs, map);
//		        }
//	        }
	        
            //관련 부품            
//	        String[] partOids = request.getParameterValues("partOid");
//			createROHSToPartLink(rohs, partOids);
			
			//관련 물질
//			String[] rohsOids = request.getParameterValues("rohsOid");
//			createROHSToROHSLink(rohs, rohsOids);
			
	        
            String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
            if("LC_Default_NonWF".equals(lifecycle)){
            	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) rohs, "BATCHAPPROVAL");
            	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
            }
            
            Map<String,Object> map = new HashMap<String,Object>();
            
            
            map.put("approvalType", approvalType);
            map.put("manufacture", manufacture);
            CommonHelper.service.changeIBAValues(rohs, map);
            
            trs.commit();
			trs = null;
//            result.setResult(true);
//            result.setOid(CommonUtil.getOIDString(rohs));
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
	public void batch(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			ArrayList<Map<String, Object>> gridList = (ArrayList<Map<String, Object>>) params.get("gridList");
    		for(Map<String, Object> map : gridList) {
    			ArrayList<String> secondarys = (ArrayList<String>) map.get("secondary");
    			String rohsNumber = (String) map.get("rohsNumber");
    			ROHSMaterial rm = RohsHelper.manager.getRohs(rohsNumber);
    			
    			Map<String,String> rohsMap = new HashMap<String,String>();
    			String fileType = (String) map.get("fileType");
    			String publicationDate = (String) map.get("publicationDate");
    			rohsMap.put("fileType", fileType);
    			rohsMap.put("publicationDate", publicationDate);
    			if(rm==null) {
    				ROHSMaterial rohs = ROHSMaterial.newROHSMaterial();
        			rohs.setNumber(rohsNumber);
        			String rohsName = (String) map.get("rohsName");
        			rohs.setName(rohsName);
        			String lifecycle = (String) map.get("lifecycleName");
        			// 물질 Container 설정
        	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
        	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
        	        rohs.setContainer(e3psProduct);
        	        // 물질 lifeCycle 설정
        			LifeCycleHelper.setLifeCycle(rohs, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef));
        			
        			rohs = (ROHSMaterial)PersistenceHelper.manager.save(rohs);
        			
        			String manufacture = (String) map.get("manufacture");
        			
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
	}
	
}
