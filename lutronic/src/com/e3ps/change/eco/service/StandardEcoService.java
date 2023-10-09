package com.e3ps.change.eco.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartSearchHelper;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class StandardEcoService extends StandardManager implements EcoService {

	public static StandardEcoService newStandardEcoService() throws WTException {
		StandardEcoService instance = new StandardEcoService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcoDTO dto) throws Exception {
		String name = dto.getName();
		String riskType = dto.getRiskType();
		String licensing = dto.getLicensing();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		String eoCommentD = dto.getEoCommentD();
		ArrayList<Map<String, String>> rows200 = dto.getRows200(); // 활동
		ArrayList<Map<String, String>> rows500 = dto.getRows500(); // 변경대상 품목
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 21.12.30_shjeong 기존 YYMM 으로 사용 시 12월 마지막주에는 다음 년도로 표기되는 오류로 인해 수정.
			Date currentDate = new Date();
			String number = "C" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

			Map<String, Object> dataMap = EcoHelper.manager.dataMap(rows500);

			EChangeOrder eco = EChangeOrder.newEChangeOrder();
			eco.setEoName(name);
			eco.setEoNumber(number);
			eco.setEoType("CHANGE");
			eco.setModel((String) dataMap.get("model"));
			eco.setLicensingChange(licensing);
			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);
			eco.setEoCommentD(eoCommentD);
			eco.setRiskType(riskType);

			String location = "/Default/설계변경/ECO";
			String lifecycle = "LC_ECO";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) eco, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(eco,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			eco = (EChangeOrder) PersistenceHelper.manager.save(eco);

			ArrayList<WTPart> clist = (ArrayList<WTPart>) dataMap.get("clist"); // 변경대상
			ArrayList<WTPart> plist = (ArrayList<WTPart>) dataMap.get("plist"); // 완제품

			// 첨부 파일 저장
			saveAttach(eco, dto);

			// 관련 CR 및 ECPR
			saveLink(eco, dto);

			// 완제품 연결
			saveCompletePart(eco, plist);

			// 변경 대상 품목 링크
			saveEcoPart(eco, clist);

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
	 * ECO 변경대상 품목 링크 생성
	 */
	private void saveEcoPart(EChangeOrder eco, ArrayList<WTPart> clist) throws Exception {
		// 그룹핑필요함...
		for (WTPart part : clist) {
			// EO진행 여부 체크
//			boolean _isSelectBom = false;
//			// if(vecBom.contains(part.getNumber())) _isSelectBom = true;
//			_isSelectBom = true;
//			boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
//			if (!isSelect) {
//				throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
//			}
			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
			link.setVersion(version);
			link.setBaseline(true);
			PersistenceServerHelper.manager.insert(link);
		}

	}

	/**
	 * 완제품 연결
	 */
	private void saveCompletePart(EChangeOrder eco, ArrayList<WTPart> plist) throws Exception {
		for (WTPart part : plist) {
			// ECO 이면서 A 면서 작업중인 것은 제외 한다.
			if ("CHANGE".equals(eco.getEoType())) {
				if (EcoHelper.manager.isSkip(part)) {
					continue;
				} else {
					EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
							eco);
					link.setVersion(part.getVersionIdentifier().getSeries().getValue());
					PersistenceServerHelper.manager.insert(link);
				}
			} else {
				EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
						eco);
				link.setVersion(part.getVersionIdentifier().getSeries().getValue());
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 관련 객체 연결
	 */
	private void saveLink(EChangeOrder eco, EcoDTO dto) throws Exception {
		// ECPR 구분 있어야 할듯??
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		for (Map<String, String> row101 : rows101) {
			String oid = row101.get("oid");
			EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(oid);
			RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, ecr);
			PersistenceServerHelper.manager.insert(link);
		}
	}

	/**
	 * ECO 첨부파일
	 */
	private void saveAttach(EChangeOrder eco, EcoDTO dto) throws Exception {
		String primary = dto.getPrimary();
		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(eco);
			applicationData.setRole(ContentRoleType.toContentRoleType("ECO"));
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(eco, applicationData, vault.getPath());
		}

		ArrayList<String> secondarys = dto.getSecondarys();
		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(eco);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(eco, applicationData, vault.getPath());
		}
	}
}
