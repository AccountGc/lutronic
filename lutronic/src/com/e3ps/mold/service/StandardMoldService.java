package com.e3ps.mold.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.mold.dto.MoldDTO;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

@SuppressWarnings("serial")
public class StandardMoldService extends StandardManager implements MoldService {
	
	public static StandardMoldService newStandardMoldService() throws WTException {
		final StandardMoldService instance = new StandardMoldService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(MoldDTO dto) throws Exception {
		String name = dto.getName();
		String location = dto.getLocation();
		System.out.println("location=>"+location);
		String description = dto.getDescription();
		String lifecycle = dto.getLifecycle();
		String documentType = dto.getDocumentType();
		
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			DocumentType docType = DocumentType.toDocumentType(documentType);
			String number = getDocumentNumberSeq(docType.getLongDescription());
			WTDocument doc = WTDocument.newWTDocument();
			doc.setDocType(docType);
			doc.setName(name);
			doc.setDescription(description);
			doc.setNumber(number);
			
			// 문서 분류쳬게 설정
	        Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
	        FolderHelper.assignLocation((FolderEntry)doc, folder);
	        
	        // 문서 Container 설정
	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
	        doc.setContainer(e3psProduct);
	        
	        // 문서 lifeCycle 설정
	        LifeCycleHelper.setLifeCycle(doc, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); //Lifecycle
			
//			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
//			FolderHelper.assignLocation((FolderEntry) doc, folder);
//			// 문서 lifeCycle 설정
//			LifeCycleHelper.setLifeCycle(doc,
//					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			
	        PersistenceHelper.manager.save(doc);
	        
	        // 첨부 파일 저장
 			saveAttach(doc, dto);
 			
 			// 문서 IBA 처리
			setIBAAttributes(doc, dto);
 			
 			// 문서 관련 객체 데이터 처리
			saveLink(doc, dto);
	        
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
	
	private String getDocumentNumberSeq(String longDescription) throws Exception {
		String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
		String number = longDescription.concat("-").concat(today).concat("-");
		String noFormat = "0000";
		String seqNo = SequenceDao.manager.getSeqNo(number, noFormat, "WTDocumentMaster", "WTDocumentNumber");
		number = number + seqNo;

		return number;
	}
	
	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(WTDocument doc, MoldDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}
	}
	
	/**
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, MoldDTO dto) throws Exception {
		// 내부 문서 번호
		String interalnumber = dto.getInteralnumber();
		// IBA 키값 어떻게 할지...
		dto.setIBAValue(doc, interalnumber, "INTERALNUMBER");
		// 협력업체
		String manufacture_code = dto.getManufacture_code();
		dto.setIBAValue(doc, manufacture_code, "MANUFACTURE");
		// 금형타입
		String moldtype_code = dto.getMoldtype_code();
		dto.setIBAValue(doc, moldtype_code, "MOLDTYPE");
		// 금형번호
		String moldnumber_code = dto.getMoldnumber_code();
		dto.setIBAValue(doc, moldnumber_code, "MOLDNUMBER");
		// 금형개발비
		String moldcost_code = dto.getMoldcost_code();
		dto.setIBAValue(doc, moldcost_code, "MOLDCOST");
		// 부서
		String deptcode_code = dto.getDeptcode_code();
		dto.setIBAValue(doc, deptcode_code, "DEPTCODE");
		// 결재 유형
		String approvalType_code = dto.getLifecycle().equals("LC_Default") ? "DEFAUT" : "BATCH";
		dto.setIBAValue(doc, approvalType_code, "APPROVALTYPE");
	}
	
	/**
	 * 문서 관련 객체 저장
	 */
	private void saveLink(WTDocument doc, MoldDTO dto) throws Exception {
		ArrayList<Map<String, String>> partList = dto.getPartList();
		// 관련품목
		for (Map<String, String> map : partList) {
			String oid = map.get("oid");
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
			PersistenceServerHelper.manager.insert(link);
		}
		
		ArrayList<Map<String, String>> docList = dto.getDocList();
		// 관련문서
		for (Map<String, String> map : docList) {
			String oid = map.get("oid");
			WTDocument ref = (WTDocument) CommonUtil.getObject(oid);
			DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc, ref);
			PersistenceServerHelper.manager.insert(link);
		}
	}
	
}
