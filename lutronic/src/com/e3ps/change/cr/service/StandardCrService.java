package com.e3ps.change.cr.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.cr.dto.CrDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCrService extends StandardManager implements CrService {

	public static StandardCrService newStandardCrService() throws WTException {
		StandardCrService instance = new StandardCrService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(CrDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String approveDate = dto.getApproveDate();
		String writeDate = dto.getWriteDate();
		String createDepart_code = dto.getCreateDepart_code();
		String writer_name = dto.getWriter_name();
		String proposer_name= dto.getProposer_name();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 모델 배열 처리
			// US21,MD23,PN21,
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				if(oid != null) {
					NumberCode n = (NumberCode) CommonUtil.getObject(oid);
					if (rows300.size() - 1 == i) {
						model += n.getCode();
					} else {
						model += n.getCode() + ",";
					}					
				}
			}

			String changeSection = "";
			for (int i = 0; i < sections.size(); i++) {
				String value = sections.get(i);
				if (sections.size() - 1 == i) {
					changeSection += value;
				} else {
					changeSection += value + ",";
				}
			}

			EChangeRequest cr = EChangeRequest.newEChangeRequest();
			cr.setEoName(name);
			cr.setEoNumber(number);
			cr.setCreateDate(writeDate);

			cr.setWriter(writer_name);				
			cr.setApproveDate(approveDate);
			
//			NumberCode dept = NumberCodeHelper.manager.getNumberCode(createDepart_code, "DEPTCODE");
			cr.setCreateDepart(createDepart_code); // 코드 넣엇을듯..
			cr.setModel(model);

			cr.setProposer(proposer_name);				
			cr.setChangeSection(changeSection);
			cr.setEoCommentA(eoCommentA);
			cr.setEoCommentB(eoCommentB);
			cr.setEoCommentC(eoCommentC);

			String location = "/Default/설계변경/ECR";
			String lifecycle = "LC_Default";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) cr, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(cr,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			cr = (EChangeRequest) PersistenceHelper.manager.save(cr);

//			String[] ecrOids = (String[]) req.getParameterValues("ecrOid");
//			updateECRToECRLink(ecr, ecrOids, false);
			// 첨부 파일 저장
			saveAttach(cr, dto);

			// 관련 CR 링크
			saveLink(cr, rows101);

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

	/**
	 * 관련 CR링크
	 */
	private void saveLink(EChangeRequest cr, ArrayList<Map<String, String>> rows101) throws Exception {
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest ref = (EChangeRequest) CommonUtil.getObject(oid);
				EcrToEcrLink link = EcrToEcrLink.newEcrToEcrLink(cr, ref);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(EChangeRequest cr, CrDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(cr);
			applicationData.setRole(ContentRoleType.toContentRoleType("ECR"));
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(cr, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(cr);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(cr, applicationData, vault.getPath());
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(cr);

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

	@Override
	public void modify(CrDTO dto) throws Exception {
		
		String createDepart_code = dto.getCreateDepart_code();
		
		ArrayList<String> sections = dto.getSections();
		ArrayList<Map<String, String>> rows101 = dto.getRows101();
		ArrayList<Map<String, String>> rows300 = dto.getRows300();
//		String model_oid = dto.getModel_oid();
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			// 모델 배열 처리
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				if(n != null) {
					if (rows300.size() - 1 == i) {
						model += n.getCode();
					} else {
						model += n.getCode() + ",";
					}					
				}
			}
			
			String changeSection = "";
			for (int i = 0; i < sections.size(); i++) {
				String value = sections.get(i);
				if (sections.size() - 1 == i) {
					changeSection += value;
				} else {
					changeSection += value + ",";
				}
			}

			EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(dto.getOid());
			
			
			
			cr.setEoName(dto.getName());
			cr.setEoNumber(dto.getNumber());
			cr.setCreateDate(dto.getWriteDate());

			cr.setWriter(dto.getWriter_name());				
			cr.setApproveDate(dto.getApproveDate());
			
//			NumberCode dept = NumberCodeHelper.manager.getNumberCode(createDepart_code, "DEPTCODE");
			cr.setCreateDepart(createDepart_code); // 코드 넣엇을듯..
			cr.setModel(model);

			cr.setProposer(dto.getProposer_name());				
			cr.setChangeSection(changeSection);
			cr.setEoCommentA(dto.getEoCommentA());
			cr.setEoCommentB(dto.getEoCommentB());
			cr.setEoCommentC(dto.getEoCommentC());

			cr = (EChangeRequest) PersistenceHelper.manager.modify(cr);

			// 첨부 파일 삭제
			removeAttach(cr);
			saveAttach(cr, dto);

			// 링크 삭제
			deleteLink(cr);
			saveLink(cr, rows101);

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

	/**
	 * 첨부 파일 삭제
	 */
	private void removeAttach(EChangeRequest cr) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(cr, ContentRoleType.toContentRoleType("ECR"));
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(cr, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(cr, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(cr, item);
		}
	}

	/**
	 * 관련 CR 삭제
	 */
	private void deleteLink(EChangeRequest cr) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "useBy", EcrToEcrLink.class, false);
		while (result.hasMoreElements()) {
			EcrToEcrLink link = (EcrToEcrLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
}
