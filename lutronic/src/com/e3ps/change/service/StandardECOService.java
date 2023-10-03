package com.e3ps.change.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.editor.BEContext;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.excelDown.service.ExcelDownHelper;
import com.e3ps.common.excelDown.service.ExcelDownService;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.distribute.util.MakeZIPUtil;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.erp.service.ERPHelper;
import com.e3ps.groupware.service.GroupwareHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.PartUtil;

@SuppressWarnings("serial")
public class StandardECOService extends StandardManager implements ECOService {

	public static StandardECOService newStandardECOService() throws Exception {
		final StandardECOService instance = new StandardECOService();
		instance.initialize();
		return instance;
	}

	@Override
	public ResultData createEOAction(HttpServletRequest req) {
		ResultData result = new ResultData();
		Transaction trx = new Transaction();

		try {

			trx.start();
			String name = StringUtil.checkNull(req.getParameter("name"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			String[] models = req.getParameterValues("model");
			// String[] partOids = req.getParameterValues("partOid");
			String[] completeOids = req.getParameterValues("completeOid");
			String eoCommentA = StringUtil.checkNull(req.getParameter("eoCommentA"));
			String eoCommentB = StringUtil.checkNull(req.getParameter("eoCommentB"));
			String eoCommentC = StringUtil.checkNull(req.getParameter("eoCommentC"));
			String[] secondarys = req.getParameterValues("SECONDARY");

			//// System.out.println("partOids =" + partOids);
			//// System.out.println("completeOids =" + completeOids);
			String model = ChangeUtil.getArrayList(models);
			// EO-1605-0001
			// String number = "E" +DateUtil.getCurrentDateString("ym");
			String eoTypeNumber = "E"; // 양산
			if (eoType.equals("DEV")) {
				eoTypeNumber = "D"; // 개발
			}
			String number = eoTypeNumber + DateUtil.getCurrentDateString("ym");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

			EChangeOrder eco = EChangeOrder.newEChangeOrder();

			eco.setEoName(name);
			eco.setEoNumber(number);
			eco.setEoType(eoType);
			eco.setModel(model);
			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);

			String location = "/Default/설계변경/EO";
			String lifecycle = "LC_ECO";

			// 분류체계,lifecycle 셋팅
			CommonHelper.service.setManagedDefaultSetting(eco, location, lifecycle);// eoDefaultSetting(eco, location,
																					// lifecycle);

			eco = (EChangeOrder) PersistenceHelper.manager.save(eco);

			// 완제품 생성
			createCompleteLink(eco, completeOids);

			// 첨부파일
			CommonContentHelper.service.attach(eco, null, secondarys);

			// 활동 생성
			boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// eco 상태 설정
			/*
			 * if(isActivity){
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("ACTIVITY")); }else{
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("APPROVE_REQUEST")); }
			 */

			trx.commit();
			trx = null;

			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(eco));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			// System.out.println("EO Action Error : "+result.getMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return result;
	}

	@Override
	public ResultData updateEOAction(HttpServletRequest req) throws Exception {

		ResultData result = new ResultData();
		Transaction trx = new Transaction();

		try {
			trx.start();
			Enumeration enums = (Enumeration) req.getParameterNames();
			while (enums.hasMoreElements()) {
				Object object = (Object) enums.nextElement();
				// System.out.println("object.toString()="+object.toString());
			}
			String oid = StringUtil.checkNull(req.getParameter("oid"));
			String name = StringUtil.checkNull(req.getParameter("name"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			String[] models = req.getParameterValues("model");
			String[] partOids = req.getParameterValues("partOid");
			String[] isSelectBoms = req.getParameterValues("isSelectBom");
			String[] ecrOids = req.getParameterValues("ecrOid");
			String[] completeOids = req.getParameterValues("completeOid");
			String licensing = StringUtil.checkNull(req.getParameter("licensing"));
			String riskType = StringUtil.checkNull(req.getParameter("riskType")); // 0 :
			String eoCommentA = StringUtil.checkNull(req.getParameter("eoCommentA"));
			String eoCommentB = StringUtil.checkNull(req.getParameter("eoCommentB"));
			String eoCommentC = StringUtil.checkNull(req.getParameter("eoCommentC"));
			String eoCommentD = StringUtil.checkNull(req.getParameter("eoCommentD"));
			String[] secondarys = req.getParameterValues("SECONDARY");
			/*
			 * for (int j = 0; j < secondarys.length; j++) {
			 * //System.out.println("secondarys["+j+"]="+secondarys[j]); }
			 */
			String[] pdeloids = (String[]) req.getParameterValues("ECO_delocIds");
			String[] sdeloids = (String[]) req.getParameterValues("delocIds");
			String model = ChangeUtil.getArrayList(models);

			// 설계 대상 부품으로 제품,완제품,설계 변경 부품 수집
			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
			boolean isECO = eco.getEoType().equals(ECOKey.ECO_CHANGE);
			/*
			 * Map ecoMap= getEODataCollection(partOids); List<WTPart> completeList = null;
			 * List<WTPart> changeList = null; if(isECO){ model =
			 * (String)ecoMap.get("model"); //제품명 completeList =
			 * (List<WTPart>)ecoMap.get("completeList"); //완제품 changeList =
			 * (List<WTPart>)ecoMap.get("changeList"); //변경 대상 }
			 */

			/// PJT EDIT 20161028
			deleteRequestOrderLink(eco);
			createReauestOrderLink(eco, ecrOids);
			eco.setEoName(name);
			eco.setEoType(eoType);
			eco.setModel(model);
			eco.setLicensingChange(licensing);

			eco.setRiskType(riskType);

			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);
			eco.setEoCommentD(eoCommentD);
			// System.out.println("modify before.!!!="+eco.getName());
			eco = (EChangeOrder) PersistenceHelper.manager.modify(eco);
			eco = (EChangeOrder) PersistenceHelper.manager.refresh(eco);
			// System.out.println("modify Complete.!!!="+eco.getName());
			// System.out.println("modify Complete.!!! isECO="+isECO);
			if (isECO) { // ECO

				addPartAction(req);
				// System.out.println("modify Complete.!!! addPartAction");
				/*
				 * //완제품 삭제 deleteCompleteLink(eco); //완제품 생성 createCompleteLink(eco,
				 * completeList); //설계 변경 부품 삭제 deletePartLink(eco); //설계 변경 부품
				 * createPartLink(eco, changeList, null);
				 */
			} else { // EO

				if (completeOids != null) {
					// 완제품 삭제
					deleteCompleteLink(eco);
					// System.out.println("modify Complete.!!! 완제품 삭제");
					// 완제품 생성
					createCompleteLink(eco, completeOids);
					// System.out.println("modify Complete.!!! 완제품 생성");
				}
				if (partOids != null) {
					// 설계 변경 부품 삭제
					deletePartLink(eco);
					// System.out.println("modify Complete.!!! 설계 변경 부품 삭제");
					// 설계 변경 부품 생성
					createPartLink(eco, partOids, isSelectBoms);
					// System.out.println("modify Complete.!!! 설계 변경 부품 생성");
				}
			}

			// 활동 생성
			// 2016.11.08 PJT EDIT
			// 2016.11.21 tsuam
			if (!CommonUtil.isAdmin()) {
				boolean isActivity = ECAHelper.service.createActivity(req, eco);
				// System.out.println("modify Complete.!!! isActivity="+isActivity);
				/*
				 * if (isActivity) {
				 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
				 * State.toState("ACTIVITY"), true); }else{
				 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
				 * State.toState("APPROVE_REQUEST"), true); }
				 */
			}
			if (null != sdeloids)
				for (int i = 0; i < sdeloids.length; i++) {
					// System.out.println("sdeloids["+i+"]="+sdeloids[i]);
				}
			// 첨부
			CommonContentHelper.service.attach(eco, null, secondarys, sdeloids, false);
			// 설계변경 부품 내역파일
			String ecoFile = req.getParameter("ECO");
			String delFileName = StringUtil.checkNull(req.getParameter("ECO_delocIds"));
			// ApplicationData app =
			// CommonContentHelper.service.attachADDRole((ContentHolder)eco, "ECO", ecoFile,
			// false);
			ApplicationData app = null;
			app = CommonContentHelper.service.attachADDRole((ContentHolder) eco, "ECO", ecoFile, delFileName, false);
			// System.out.println("Commit 전 완료.!!!="+eco.getName());
			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(eco));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			// System.out.println("EO Action Error : "+result.getMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return result;

	}

	@Override
	public String deleteECOAction(String oid) throws Exception {

		Transaction trx = new Transaction();
		try {
			trx.start();
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				EChangeOrder eco = (EChangeOrder) f.getReference(oid).getObject();
				WFItemHelper.service.deleteWFItem(eco);

				List<EChangeActivity> vec = ECAHelper.service.getECAList(eco);
				for (EChangeActivity eca : vec) {

					WFItemHelper.service.deleteWFItem(eca);
					PersistenceHelper.manager.delete(eca);
				}

				PersistenceHelper.manager.delete(eco);
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return Message.get("삭제 되었습니다.");
	}

	@Override
	public ResultData createECOAction(HttpServletRequest req) {
		ResultData result = new ResultData();
		Transaction trx = new Transaction();

		try {
			trx.start();

			String name = StringUtil.checkNull(req.getParameter("name"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			String[] ecrOids = req.getParameterValues("ecrOid");
			;
			// String[] models = req.getParameterValues("model");
			String[] partOids = req.getParameterValues("partOid");
			String[] isSelectBoms = req.getParameterValues("isSelectBom");
			// String[] completeOids = req.getParameterValues("completeOid");
			String licensing = StringUtil.checkNull(req.getParameter("licensing"));
			String eoCommentA = StringUtil.checkNull(req.getParameter("eoCommentA"));
			String eoCommentB = StringUtil.checkNull(req.getParameter("eoCommentB"));
			String eoCommentC = StringUtil.checkNull(req.getParameter("eoCommentC"));
			String eoCommentD = StringUtil.checkNull(req.getParameter("eoCommentD"));

			String riskType = StringUtil.checkNull(req.getParameter("riskType"));
			String[] secondarys = req.getParameterValues("SECONDARY");

			// 21.12.30_shjeong 기존 YYMM 으로 사용 시 12월 마지막주에는 다음 년도로 표기되는 오류로 인해 수정.
			Date currentDate = new Date();
			String number = "C" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

			EChangeOrder eco = EChangeOrder.newEChangeOrder();

			// 설계 대상 부품으로 제품,완제품,설계 변경 부품 수집
			Map ecoMap = getEODataCollection(partOids);

			String model = (String) ecoMap.get("model"); // 제품명
			List<WTPart> completeList = (List<WTPart>) ecoMap.get("completeList"); // 완제품
			List<WTPart> changeList = (List<WTPart>) ecoMap.get("changeList"); // 변경 대상

			eco.setEoName(name);
			eco.setEoNumber(number);
			eco.setEoType(eoType);
			eco.setModel(model);
			eco.setLicensingChange(licensing);
			eco.setEoCommentA(eoCommentA);
			eco.setEoCommentB(eoCommentB);
			eco.setEoCommentC(eoCommentC);
			eco.setEoCommentD(eoCommentD);
			eco.setRiskType(riskType);

			String location = "/Default/설계변경/ECO";
			String lifecycle = "LC_ECO";

			// 분류체계,lifecycle 셋팅
			CommonHelper.service.setManagedDefaultSetting(eco, location, lifecycle);

			eco = (EChangeOrder) PersistenceHelper.manager.save(eco);

			// 활동 생성
			boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// 관련 ECR
			createReauestOrderLink(eco, ecrOids);

			// 완제품 생성
			createCompleteLink(eco, completeList);

			// 설계 변경 부품
			createPartLink(eco, changeList, null);

			// 첨부파일
			CommonContentHelper.service.attach(eco, null, secondarys);

			// 설계변경 부품 내역파일
			String ecoFile = req.getParameter("ECO");

			ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder) eco, "ECO", ecoFile, false);

			trx.commit();
			trx = null;

			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(eco));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			// System.out.println("ECO Action Error : "+result.getMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return result;
	}

	/**
	 * 완제품 생성
	 * 
	 * @param eo
	 * @param partOids
	 * @throws VersionControlException
	 */
	private void createCompleteLink(EChangeOrder eco, String[] partOids) throws Exception {

		List<WTPart> list = new ArrayList<WTPart>();

		boolean isEO = !eco.getEoType().equals(ECOKey.ECO_CHANGE);
		for (int i = 0; partOids != null && i < partOids.length; i++) {

			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);

			if (isEO) {
				boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
				if (!isSelect) {
					throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
				}
			}
			list.add(part);
		}

		createCompleteLink(eco, list);
	}

	private void createCompleteLink(EChangeOrder eco, List<WTPart> list) throws Exception {

		if (list == null)
			return;

		for (WTPart part : list) {

			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			String state = part.getState().toString();
			// ECO 이면서 A 면서 작업중인 것은 제외 한다.
			if ("CHANGE".equals(eco.getEoType())) {
				if (version.equals("A") && "INWORK".equals(state)) {
					continue;
				} else {
					EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
							eco);
					link.setVersion(version);

					PersistenceHelper.manager.save(link);
				}
			} else {
				EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster) part.getMaster(),
						eco);
				link.setVersion(version);

				PersistenceHelper.manager.save(link);
			}
		}

	}

	/**
	 * 완제품 삭제
	 * 
	 * @param eo
	 * @param partOids
	 * @throws VersionControlException
	 */
	private void deleteCompleteLink(EChangeOrder eco) throws Exception {

		List<EOCompletePartLink> list = ECOSearchHelper.service.getCompletePartLink(eco);

		for (EOCompletePartLink link : list) {
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * 관련 ECR 링크
	 * 
	 * @param eco
	 * @param ecrOid
	 * @throws Exception
	 */
	private void createReauestOrderLink(EChangeOrder eco, String[] ecrOids) throws Exception {

		// System.out.println("ecrOids = " + ecrOids);
		if (null != ecrOids) {

			for (String ecrOid : ecrOids) {
				EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(ecrOid);

				if (ecr != null) {
					RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, ecr);
					PersistenceHelper.manager.save(link);
				}
			}
		}

	}

	/**
	 * 20161025 pjt edit 관련 ECR 링크 삭제
	 * 
	 * @param eco
	 * @throws Exception
	 */
	private void deleteRequestOrderLink(EChangeOrder eco) throws Exception {
		Vector<RequestOrderLink> vec = ChangeHelper.service.getRelationECR(eco);

		for (int i = 0; i < vec.size(); i++) {
			RequestOrderLink link = (RequestOrderLink) vec.get(i);
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * 설계 변경 대상 부품 링크
	 * 
	 * @param eco
	 * @param partOids
	 */
	private void createPartLink(EChangeOrder eco, String[] partOids, String[] isSelectBoms) throws Exception {

		Vector vecBom = new Vector();
		if (isSelectBoms != null) {

			// System.out.println("isSelectBoms.length ="+isSelectBoms.length);
			for (int i = 0; i < isSelectBoms.length; i++) {
				// System.out.println("isDelete[i] ="+isSelectBoms[i]);
				vecBom.add(isSelectBoms[i]);
			}
		}

		for (int i = 0; partOids != null && i < partOids.length; i++) {

			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);
			boolean _isSelectBom = false;

			boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
			if (!isSelect) {
				throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
			}

			if (vecBom.contains(part.getNumber()))
				_isSelectBom = true;

			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
			link.setVersion(version);
			link.setBaseline(_isSelectBom);
			PersistenceHelper.manager.save(link);

		}

	}

	/**
	 * 설계 변경 대상 부품 링크 생성
	 * 
	 * @param eco
	 * @param partOids
	 */
	private void createPartLink(EChangeOrder eco, List<WTPart> list, Vector vecBom) throws Exception {

		if (list == null)
			return;

		for (WTPart part : list) {
			boolean _isSelectBom = false;
			// if(vecBom.contains(part.getNumber())) _isSelectBom = true;
			_isSelectBom = true;
			boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
			if (!isSelect) {
				throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
			}
			String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
			EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
			link.setVersion(version);
			link.setBaseline(_isSelectBom);
			PersistenceHelper.manager.save(link);

		}

	}

	/**
	 * EO - ,ECO 제품명,완제품,변경 대상 부품리스트
	 * 
	 * @param partOids
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getEODataCollection(String[] partOids) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<WTPart> completeList = new ArrayList<WTPart>();
		List<WTPart> changeList = new ArrayList<WTPart>();
		List<String> modelList = new ArrayList<String>();
		List<Integer> delIndexList = new ArrayList<Integer>();
		String model = "";

		for (int i = 0; partOids != null && i < partOids.length; i++) {

			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);

			// 설계 변경 대상
			changeList.add(part);

			// 제품 수집
			String partModel = IBAUtil.getAttrValue(part, IBAKey.IBA_MODEL);
			// System.out.println("getEODataCollection part = "+part.getNumber()+"partModel
			// = " + partModel +"="+ !modelList.contains(partModel));
			if (!modelList.contains(partModel)) {
				model = model + "," + partModel;
				modelList.add(partModel);
			}

			// 완제품 수집
			completeList = PartSearchHelper.service.getPartEndItem(part, completeList);
			// System.out.println("getEODataCollection completeList = 완제품수집 완료.");
		}

		int cnt = 0;
		// 완제품 품목도 프로젝트 코드를 가져옴 shjeong 2021/10/6
		for (WTPart comPart : completeList) {
			String version = comPart.getVersionInfo().getIdentifier().getValue();
			String stete = comPart.getState().toString();
			// A 버전이면서 작업중인 품목의 코드는 가져오지 않는다. shjeong 2021/10/6
			if (version.equals("A") && stete.equals("INWORK")) {
				// 23.01.04 gmji
				delIndexList.add(cnt);
				continue;
			}
			String partModel = IBAUtil.getAttrValue(comPart, IBAKey.IBA_MODEL);
			if (!modelList.contains(partModel)) {
				model = model + "," + partModel;
				modelList.add(partModel);
			}
			cnt++;
		}
		// 버전 A이면서 INWORK 인 품목을 완제품 목록에서 제외
		for (int k = 0; k < delIndexList.size(); k++) {
			completeList.remove(delIndexList.get(k));
		}
		// System.out.println("getEODataCollection completeList = " +
		// completeList.size());
		map.put("model", model);
		map.put("completeList", completeList);
		map.put("changeList", changeList);
		return map;
	}

	/**
	 * 설계 변경 대상 부품 링크 삭제
	 * 
	 * @param eo
	 * @param partOids
	 * @throws VersionControlException
	 */
	private void deletePartLink(EChangeOrder eco) throws Exception {

		Vector<EcoPartLink> list = ECOSearchHelper.service.ecoPartLinkList(eco); // ECOSearchHelper.service.ecoPartLinkList(eco)

		for (EcoPartLink link : list) {
			PersistenceHelper.manager.delete(link);
		}
	}

	/**
	 * eco Default 생성
	 * 
	 * @param eco
	 * @param location
	 * @param lifecycle
	 * @throws Exception
	 */
	private void eoDefaultSetting(EChangeOrder eco, String location, String lifecycle) throws Exception {

		// ECO 분류쳬게 설정
		PDMLinkProduct product = WCUtil.getPDMLinkProduct();
		WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());

		FolderHelper.assignLocation((FolderEntry) eco, folder);

		// ECO Container 설정
		eco.setContainer(product);

		// ECO lifeCycle 설정
		LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef);
		LifeCycleHelper.setLifeCycle(eco, tmpLifeCycle);

	}

	/**
	 * EO,ECO 승인시 후속 처리
	 * 
	 * @param eco
	 */
	@Override
	public void completeECO(EChangeOrder eco) {

		HashMap returnMap = new HashMap();
		Transaction trx = new Transaction();
		try {
			trx.start();
			String eoType = eco.getEoType();

			// BOM 반영
			//// System.out.println("0.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
			// +" BOM 반영");
			// EulHelper.service.completeEBOM(eco);

			if (eoType.equals(ECOKey.ECO_CHANGE)) {

				// 완제품 제수집
				createCompleteProduction(eco);

				// Part,CAD 상태 변경
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" 부품,도면 상태 변경");
				this.completePart(eco);

				// ERP 전송
				// System.out.println("3.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" ERP 전송");
				ERPHelper.service.sendERP(eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");

				createECOBaseline(eco);

			} else if (eoType.equals(ECOKey.ECO_PRODUCT)) {

				// 완제품의 하위 구조 승인 처리
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" 완제품의 하위 구조 승인 처리");
				List<WTPart> partList = productPartList(eco);
				completeProduct(partList, eco);

				// ERP 완제품의 전송
				// System.out.println("3.[ChangdeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" ERP 전송");
				ERPHelper.service.sendERP(eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");
				createECOBaseline(eco);

			} else if (eoType.equals(ECOKey.ECO_DEV)) {

				// 완제품의 하위 구조 승인 처리
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" 완제품의 하위 구조 승인 처리");
				List<WTPart> partList = productPartList(eco);
				completeProduct(partList, eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");
				createECOBaseline(eco);

			}

			// ECO 승인이후 산출물 등록 Activity 자동 생성
			ECAHelper.service.createAutoActivity(eco);

			// 승인일 등록
			eco.setEoApproveDate(DateUtil.getToDay());
			PersistenceHelper.manager.modify(eco);

			trx.commit();
			trx = null;
		} catch (Exception e) {
			// System.out.println("[ChangeECOHelper]{completeECO} ERROR = " +
			// e.getLocalizedMessage());
			e.printStackTrace();

		} finally {

			if (trx != null) {
				trx.rollback();
			}
		}
	}

	/**
	 * 설계 변경 완료시 부품,도면 속성 전파 및 승인처리
	 * 
	 * @param eco
	 * @throws Exception
	 */
	@Override
	public void completePart(EChangeOrder eco) throws Exception {

		/* 최종 결재자,검토자 */
		Map<String, String> map = ChangeUtil.approvedSetData(eco);

		// System.out.println("[ChangeECOHelper] completePart = " + eco.getEoNumber());
		String eoType = eco.getEoType();
		List<WTPart> partList = new ArrayList<WTPart>();

		partList = ECOSearchHelper.service.ecoPartReviseList(eco);

		// "APPROVED";state
		map.put("state", "APPROVED");
		map.put("eoType", eoType);
		for (WTPart part : partList) {
			// System.out.println("[ChangeECOHelper] completePart = " + part.getNumber());
			completePartStateChange(part, map);
		}

	}

	/**
	 * 부품,3D, 2D 상태 변경,최종 결재자 입력
	 * 
	 * @param part
	 * @return
	 * @throws Exception
	 */
	@Override
	public void completePartStateChange(WTPart part, Map<String, String> map) throws Exception {

		Folder folder = null;
		String partState = part.getLifeCycleState().toString();
		String eoType = map.get("eoType");
		// System.out.println("number="+part.getNumber()+" PartSate="+partState);
		if (partState.equals("APPROVED")) {
			return;
		}

		// EO인경우 하위 구조가 작업중이고 설계 변경중이면 승인 처리 하지 않음
		boolean isEO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT);
		if (isEO) {
			boolean isNotECO = PartSearchHelper.service.isSelectEO(part);
			if (!isNotECO) {
				return;
			}
		}

		PartData partData = new PartData(part);
		// Generic instance, 더미 파일은 승인 처리 하지 않음
		boolean isStateChange = !(partData.isGENERIC() || partData.isINSTANCE())
				|| !PartUtil.isChange(part.getNumber());
		/* Part State change */

		String state = map.get("state");// "APPROVED";state
		// System.out.println("number="+part.getNumber()+" PartSate="+partState+"
		// isStateChange="+isStateChange+"\t state="+state);
		WTUser sessUser = (WTUser) SessionHelper.manager.getPrincipal();

		if (isStateChange) {
			SessionHelper.manager.setAdministrator();
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(state));
			SessionHelper.manager.setPrincipal(sessUser.getName());
		}

		EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
		// 속성 전파

		approvedIBAChange(part, map);
		if (epm != null) {
			/* 3D EPM State Change */
			if (isStateChange) {
				// System.out.println("number="+epm.getNumber()+" state="+state);
				SessionHelper.manager.setAdministrator();
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm, State.toState(state));
				SessionHelper.manager.setPrincipal(sessUser.getName());
			}

			// 속성 전파
			approvedIBAChange(epm, map);
			/* 2D EPM State Change */
			Vector<EPMReferenceLink> vec = EpmSearchHelper.service
					.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());
			for (int i = 0; i < vec.size(); i++) {
				EPMReferenceLink link = vec.get(i);
				EPMDocument epm2D = link.getReferencedBy();

				// if(epm2D.getAuthoringApplication().toString().equals("DRAWING")){
				EpmPublishUtil.publish(epm2D);
				// }
				// System.out.println("number="+epm2D.getNumber()+" state="+state);
				SessionHelper.manager.setAdministrator();
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm2D, State.toState(state));
				SessionHelper.manager.setPrincipal(sessUser.getName());
			}
		}
		// 관련 도면
		/*
		 * Vector<EPMDescribeLink> vecDesc
		 * =EpmSearchHelper.service.getEPMDescribeLink(part, true); for(EPMDescribeLink
		 * likDesc : vecDesc){ EPMDocument epmDesc = likDesc.getDescribedBy();
		 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epmDesc,
		 * State.toState(state)); }
		 */
	}

	/**
	 * 결재 승인후 Part와 EPMDocument에 ApprovedBy 추가 삭
	 * 
	 * @param eco
	 */
	@Override
	public void approvedIBAChange(EChangeOrder eco) throws Exception {

		try {

			Map<String, String> map = ChangeUtil.approvedSetData(eco);

			List<WTPart> partList = ECOSearchHelper.service.ecoPartReviseList(eco);
			for (WTPart part : partList) {

				approvedIBAChange(part, map);

				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);

				if (epm != null) {
					approvedIBAChange(epm, map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * EO,ECO 승인시 승인시 결재자 정보 입력
	 * 
	 * @param part
	 * @param epm
	 * @param approveName
	 * @param checkerName
	 */
	@Override
	public void approvedIBAChange(RevisionControlled rc, Map<String, String> map) throws Exception {
		// RevisionControlled rc = null;

		// map = ChangeUtil.changeApprove(rc, map);
		// IBAHolder
		// ECONO,ECODATE,CHK,APR
		String ecoNO = map.get("ECONO");
		String ecoDate = map.get("ECODATE");
		String approveName = map.get("APR");
		String checkerName = map.get("CHK");
		String eoType = map.get("eoType");
		String oid = CommonUtil.getOIDString(rc);
		String location = rc.getLocation();
		// System.out.println("###########approvedIBAChange #$$$$$
		// approveName="+approveName+"\toid="+oid);
		if (!ChangeUtil.isMeca(location)) {
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_APR, approveName, "string");
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_CHK, checkerName, "string");
		}
		String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_ECONO));
		// if(ecoNo.length()==0 || ecoNo.equals("-")){ //최초 한번만 등록
		boolean isECO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT) || eoType.equals(AttributeKey.ECOKey.ECO_CHANGE);
		boolean isFirst = ecoNo.length() == 0 || ecoNo.equals("-");
		if (isECO && isFirst) { // 최초 한번만 등록 ,양산 이나 설계변경 , 개발 EO 제외
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_ECONO, ecoNO, "string");
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_ECODATE, ecoDate, "string");
		}

		// EO,ECO시 누적으로 등록
		String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGENO));
		String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGEDATE));

		// 중복 체크
		boolean iNoNDouble = changeNo.indexOf(ecoNO) < 0;

		if (iNoNDouble) {
			if (StringUtil.checkString(changeNo)) {
				changeNo = changeNo + "," + ecoNO;
			} else {
				changeNo = ecoNO;
			}

			if (StringUtil.checkString(changeDate)) {
				changeDate = changeDate + "," + ecoDate;
			} else {
				changeDate = ecoDate;
			}
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_CHANGENO, changeNo, "string");
			IBAUtil.changeIBAValue((IBAHolder) rc, AttributeKey.IBAKey.IBA_CHANGEDATE, changeDate, "string");
		}

		// }

	}

	/**
	 * Bom 전송 대상 Baseline 생성
	 * 
	 * @param wtpart
	 * @param eulb
	 * @return
	 * @throws Exception
	 */
	@Override
	public void createECOBaseline(EChangeOrder eco) throws Exception {
		String eoType = eco.getEoType();
		List<WTPart> partList = new ArrayList<WTPart>();

		if (eoType.equals(ECOKey.ECO_CHANGE)) {
			partList = ECOSearchHelper.service.ecoPartReviseList(eco);

			// 설계 변경시 완제품에 대한 Baseline 추가 tsuam 2017-04024
			List<WTPart> completePartList = ECOSearchHelper.service.getCompletePartList(eco);

			for (WTPart part : completePartList) {

				if (partList.contains(part)) {
					// System.out.println("1."+eco.getEoNumber() +"," +part.getNumber() +" 설계 대상 부품에
					// 포함 ");
					continue;
				}
				// System.out.println("1."+eco.getEoNumber() +" ,completePartList =" +
				// part.getNumber() +","+part.getLifeCycleState().toString() +","+
				// part.getVersionIdentifier().getValue());

				// 완제품인 승인된 완제품 , 작업중이면 전버전 완제품, A버전이면 Skip
				if (part.getLifeCycleState().toString().equals("APPROVED")) {
					partList.add(part);
					// System.out.println("2."+eco.getEoNumber() +" ,completePartList =" +
					// part.getNumber() +","+part.getLifeCycleState().toString() +","+
					// part.getVersionIdentifier().getValue());
				} else {
					// System.out.println("3."+eco.getEoNumber() +" ,completePartList =" +
					// part.getNumber() +","+part.getLifeCycleState().toString() +","+
					// part.getVersionIdentifier().getValue());
					if (!part.getVersionIdentifier().getValue().equals("A")) {
						part = (WTPart) ObjectUtil.getPreviousVersion(part);
						partList.add(part);
					}

				}
				// System.out.println("4."+eco.getEoNumber() +" ,completePartList =" +
				// part.getNumber() +","+part.getLifeCycleState().toString() +","+
				// part.getVersionIdentifier().getValue());

			}
		} else {
			partList = ECOSearchHelper.service.getCompletePartList(eco);
		}

		for (WTPart part : partList) {
			// System.out.println("1.partList."+eco.getEoNumber() +" ,partList =" +
			// part.getNumber() +","+part.getLifeCycleState().toString() +","+
			// part.getVersionIdentifier().getValue());
			if (!part.getLifeCycleState().toString().equals("APPROVED")) {
				if (!part.getVersionIdentifier().getValue().equals("A")) {
					part = (WTPart) ObjectUtil.getPreviousVersion(part);
				}
			}

			// System.out.println("2.partList."+eco.getEoNumber() +" ,partList =" +
			// part.getNumber() +","+part.getLifeCycleState().toString() +","+
			// part.getVersionIdentifier().getValue());
			boolean isBaselineCheck = true;
			String partoid = CommonUtil.getOIDString(part);
			List<Map<String, String>> list = ChangeHelper.service.getGroupingBaseline(partoid, "", "");
			String[] partOids = new String[list.size()];
			String[] baseNames = new String[list.size()];
			String[] baseOids = new String[list.size()];
			int idx = 0;
			for (Map<String, String> map : list) {
				// partOids[idx] = (String)map.get("partOid");
				baseNames[idx] = (String) map.get("baseName");
				// baseOids[idx]= (String)map.get("baseOid");
				// System.out.println("isBaselineCheck Check 1 :
				// "+baseNames[idx]+";"+(baseNames[idx].equals(eco.getEoNumber())));
				if (baseNames[idx].equals(eco.getEoNumber())) {
					isBaselineCheck = false;
					break;
				}
				idx++;
			}
			// System.out.println("isBaselineCheck Check 2 : "+isBaselineCheck);
			if (isBaselineCheck)
				createBaseline(part, eco);
		}

	}

	/**
	 * Bom 전송 대상 ManagedBaseline 생성
	 * 
	 * @param wtpart
	 * @param eulb
	 * @return
	 * @throws Exception
	 */
	@Override
	public ManagedBaseline createBaseline(WTPart wtpart, EChangeOrder eco) throws Exception {
		Date date = new Date();
		String baselineName = eco.getEoNumber() + ":" + wtpart.getNumber();

		// System.out.println("createBaseline =" + baselineName);
		// System.out.println("1.createBaseline."+eco.getEoNumber() +" ,partList =" +
		// wtpart.getNumber() +","+wtpart.getLifeCycleState().toString() +","+
		// wtpart.getVersionIdentifier().getValue());
		String eoType = eco.getEoType();

		WTProperties wtproperties = WTProperties.getLocalProperties();
		String s = "/Default/설계변경/Baseline";
		String s2 = wtproperties.getProperty("baseline.lifecycle");

		Folder folder = null;
		LifeCycleTemplate lifecycletemplate = null;

		if (s != null)
			folder = FolderHelper.service.getFolder(s, WCUtil.getWTContainerRef());
		else
			folder = FolderTaskLogic.getFolder(wtpart.getLocation(), WCUtil.getWTContainerRef());

		if (s2 != null)
			lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2, WCUtil.getWTContainerRef());
		else
			lifecycletemplate = (LifeCycleTemplate) wtpart.getLifeCycleTemplate().getObject();

		ManagedBaseline mb = null;
		// if(eoType.equals("CHANGE")){
		// mb = BEContext.createOneLevelBaseline(wtpart, baselineName, folder,
		// lifecycletemplate);
		// }else{
		mb = BEContext.createBaseline(wtpart, baselineName, folder, lifecycletemplate);
		// }

		return mb;
	}

	/**
	 * EO에서 완제품 BOM 일괄 승인
	 * 
	 * @param list
	 * @param eco
	 * @throws Exception
	 */
	@Override
	public void completeProduct(List<WTPart> list, EChangeOrder eco) throws Exception {

		/* 최종 결재자,검토자 */
		Map<String, String> map = ChangeUtil.approvedSetData(eco);
		String eoType = eco.getEoType();
		String state = "APPROVED";
		if (eoType.equals(ECOKey.ECO_DEV)) {
			state = "DEV_APPROVED";
		}
		map.put("state", state);
		map.put("eoType", eoType);

		// 중복 되는 부붐 제외
		Vector vecTemp = new Vector();
		for (WTPart part : list) {

			if (vecTemp.contains(part)) {
				continue;
			} else {
				vecTemp.add(part);
				completePartStateChange(part, map);
			}

		}
	}

	/**
	 * 완제품 하위 구조 승인 처리
	 * 
	 * @param eco
	 * @throws Exception
	 */
	@Override
	public List<WTPart> productPartList(EChangeOrder eco) throws Exception {

		String view = "Design";
		List<WTPart> BomList = new ArrayList<WTPart>();
		List<WTPart> productList = ECOSearchHelper.service.getCompletePartList(eco);

		for (WTPart productPart : productList) {
			BomList.add(productPart);
			BomList = getBomList(productPart, ViewHelper.service.getView(view), BomList);
		}
		return BomList;
	}

	/**
	 * BOM 리스트
	 * 
	 * @param part
	 * @param link
	 * @param view
	 * @param BomList
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<WTPart> getBomList(WTPart part, View view, List<WTPart> BomList) throws Exception {
		ArrayList list = descentLastPart(part, view, null);

		for (int i = 0; i < list.size(); i++) {

			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko = (WTPartUsageLink) o[0];
			WTPart cPart = (WTPart) o[1];

			BomList.add(cPart);
			getBomList((WTPart) o[1], view, BomList);
		}

		return BomList;
	}

	/**
	 * BOM 구조
	 * 
	 * @param part
	 * @param view
	 * @param state
	 * @return
	 * @throws WTException
	 */
	private static ArrayList descentLastPart(WTPart part, wt.vc.views.View view, State state) throws WTException {
		ArrayList v = new ArrayList();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
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

	/**
	 * 도면/BOM 변경시 품목 추가
	 * 
	 * @param map
	 * @throws Exception
	 */
	@Override
	public String addPart(HttpServletRequest request) throws Exception {

		Transaction trx = new Transaction();
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		try {
			trx.start();

			addPartAction(request);

			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return oid;
	}

	@Override
	public ResultData deleteCompletePartAction(String linkOid) {
		ResultData data = new ResultData();

		String msg = Message.get("삭제  되었습니다.");
		data.setResult(true);
		try {
			EOCompletePartLink link = (EOCompletePartLink) CommonUtil.getObject(linkOid);
			PersistenceHelper.manager.delete(link);

		} catch (Exception e) {

			e.printStackTrace();
			msg = e.getMessage();
			data.setResult(false);
		}
		data.setMessage(msg);

		return data;

	}

	/**
	 * 반려시 재작업 또는 종료 및 ECA 시작 true 이면 재작업 ,false 이면 workflow 종료 ,ECA Start,
	 * WB_ECO_Basic 표현식에 있음
	 * 
	 * @param wtobject
	 * @return
	 */
	@Override
	public boolean setReworkEOCheck(WTObject ps) {
		boolean isResult = true;
		try {
			if (ps instanceof ECOChange) {
				ECOChange eo = (ECOChange) ps;
				List<EChangeActivity> list = ECAHelper.service.getECAList(eo);

				if (list.size() == 0) {
					return true;
				}

				String tempStep = "";
				for (EChangeActivity eca : list) {

					if (tempStep.length() == 0) {
						tempStep = eca.getStep();
					}

					if (tempStep.equals(eca.getStep())) {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) eca, State.toState("INWORK"));
					} else {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) eca, State.toState("INTAKE"));
					}
				}

				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) eo, State.toState("ACTIVITY"));

				isResult = false;

				// 결재선 초기황
				WFItemHelper.service.setReworkAppLine(ps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isResult;
	}

	private void addPartAction(HttpServletRequest request) throws Exception {

		// Transaction trx = new Transaction();
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		// try {
		/// trx.start();

		String[] partOids = request.getParameterValues("partOid");
		String[] isSelecctBom = request.getParameterValues("isSelecctBom");
		Object objOid = CommonUtil.getObject(oid);
		EChangeOrder eco = null;
		if (objOid instanceof EChangeOrder)
			eco = (EChangeOrder) objOid;
		else if (objOid instanceof EChangeActivity) {
			eco = (EChangeOrder) ((EChangeActivity) objOid).getEo();
		}
		if (null == eco)
			return;
		QueryResult qr = ECOSearchHelper.service.ecoPartLink(eco);

		Vector ecoAddPart = new Vector();
		HashMap updatePart = new HashMap();
		Vector isSelecctBomVec = new Vector();

		/*
		 * for(int i = 0 ; isSelecctBom!=null && i <isSelecctBom.length; i++){
		 * isSelecctBomVec.add(isSelecctBom[i]); //System.out.println("isDisuse = " +
		 * isSelecctBom[i]); }
		 */
		for (int i = 0; partOids != null && i < partOids.length; i++) {
			// System.out.println("partOids ================"+partOids[i]);
			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);
			ecoAddPart.add(part.getNumber());

		}
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();

			EcoPartLink link = (EcoPartLink) o[0];

			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster) link.getPart();

			if (ecoAddPart.contains(master.getNumber())) {
				Object[] obj = new Object[2];
				obj[0] = link;
				obj[1] = true;
				/*
				 * if(isSelecctBomVec.contains(master.getNumber())){
				 * 
				 * 
				 * }else{ obj[0] = link; obj[1] = false;
				 * 
				 * }
				 */
				updatePart.put(master.getNumber(), obj);
			} else {
				PersistenceHelper.manager.delete(link);
			}
		}
		// 부품 수집
		for (int i = 0; partOids != null && i < partOids.length; i++) {
			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);
			// System.out.println("wtpart name ============="+ part.getName());
			if (updatePart.containsKey(part.getNumber())) {
				// if(isDisuseVec.contains(part.getNumber())){
				Object[] obj = (Object[]) updatePart.get(part.getNumber());

				EcoPartLink link = (EcoPartLink) obj[0];
				link.setBaseline((Boolean) obj[1]);
				PersistenceHelper.manager.modify(link);

				// }else{

				// }
				// continue;

			} else {

				boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
				if (!isSelect) {
					throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
				}

				String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
				EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
				link.setVersion(version);
				/*
				 * if(isSelecctBomVec.contains(part.getNumber())){ link.setBaseline(true);
				 * }else{ link.setBaseline(false); }
				 */
				link.setBaseline(true);
				PersistenceHelper.manager.save(link);
			}
		}

		String eoType = eco.getEoType();

		// 설계 대상 부품으로 제품,완제품,설계 변경 부품 수집
		if (eoType.equals(ECOKey.ECO_CHANGE)) {
			Map ecoMap = getEODataCollection(partOids);

			String model = (String) ecoMap.get("model"); // 제품명
			List<WTPart> completeList = (List<WTPart>) ecoMap.get("completeList"); // 완제품
			List<WTPart> changeList = (List<WTPart>) ecoMap.get("changeList"); // 변경 대상

			// 완제품 삭제
			deleteCompleteLink(eco);
			// System.out.println("getEODataCollection 이후 완제품 삭제 실행완료");
			// 완제품 생성
			createCompleteLink(eco, completeList);
			// System.out.println("getEODataCollection 이후 완제품 생성 실행완료");

			// 제품명 수정
			eco.setModel(model);
			PersistenceHelper.manager.modify(eco);
			// System.out.println("getEODataCollection 이후 ECO 수정 완료 실행완료");
		}

		/*
		 * trx.commit(); trx = null; }catch(Exception e) { e.printStackTrace(); throw
		 * new Exception(e.getLocalizedMessage()) ; }finally{ if(trx!=null){
		 * trx.rollback(); } }
		 */
	}

	@Override
	public ResultData excelDown(String oid, String eoType) {
		ResultData data = null;
		if ("eco".equals(eoType)) {
			data = ecoExcelDown(oid);
		} else if ("eo".equals(eoType)) {
			data = eoExcelDown(oid);
		} else {
			data = new ResultData();
			data.setResult(false);
			data.setMessage("엑셀 쓰기에 실패하였습니다");
		}
		return data;
	}

	private ResultData ecoExcelDown(String oid) {
		ResultData data = new ResultData();

		try {
			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
			ECOData ecoData = new ECOData(eco);
			Map<String, String> map = ChangeUtil.getApproveInfo(eco);
			List<Map<String, Object>> partList = ecoData.getCompletePartList();

			String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
			String path = WTProperties.getServerProperties().getProperty("wt.temp");

			File orgFile = new File(wtHome + "/codebase/com/e3ps/change/beans/eco.xlsx");

			File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + ecoData.number + ".xlsx"));

			FileInputStream file = new FileInputStream(newFile);

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			XSSFSheet sheet = workbook.getSheetAt(0);

			workbook.setSheetName(0, ecoData.number);

			// System.out.println("쓰기 시작....." + POIUtil.getSheetRow(sheet));

			// 문서번호 (9-D ,8-3)
			XSSFCell documentNumber = sheet.getRow(8).getCell(5);
			documentNumber.setCellValue(ecoData.number);

			// 작성자 (17-I, 16-8)
			XSSFCell creator = sheet.getRow(16).getCell(8);
			creator.setCellValue(ecoData.creator);

			// 검토자 (17-K, 16-10)
			XSSFCell chk = sheet.getRow(16).getCell(10);
			chk.setCellValue(map.get("checkerName"));

			// 승인자 (17-M 16-12)
			XSSFCell approver = sheet.getRow(16).getCell(12);
			approver.setCellValue(map.get("approveName"));

			// 작성일 (18-I, 17-8)
			XSSFCell creatDate = sheet.getRow(17).getCell(8);
			creatDate.setCellValue(ecoData.dateSubString(true));

			// 검토일 (18-K, 17-10)
			XSSFCell chkDate = sheet.getRow(17).getCell(10);
			chkDate.setCellValue(map.get("checkDate"));

			// 승인일 (18-M, 17-12)
			XSSFCell approveDate = sheet.getRow(17).getCell(12);
			approveDate.setCellValue(map.get("approveDate"));

			// 문서 번호 (29-D, 28-3)
			XSSFCell documentNumber2 = sheet.getRow(28).getCell(3);
			documentNumber2.setCellValue(ecoData.number);

			// 제목 (30-D, 29-3)
			XSSFCell documentName = sheet.getRow(29).getCell(3);
			documentName.setCellValue(ecoData.name);

			// 작성일 (32-D, 31-3)
			XSSFCell creatDate2 = sheet.getRow(31).getCell(3);
			creatDate2.setCellValue(ecoData.dateSubString(true));

			// 작성부서 (32-I, 31-8)
			XSSFCell createDept = sheet.getRow(31).getCell(8);
			createDept.setCellValue(ecoData.getCreatorDepartment());

			// 승인일 (33-D, 32-3)
			XSSFCell approveDate2 = sheet.getRow(32).getCell(3);
			approveDate2.setCellValue(map.get("approveDate"));

			// 작성자 (33-I, 32-8)
			XSSFCell creator2 = sheet.getRow(32).getCell(8);
			creator2.setCellValue(ecoData.creator);

			// 제품명 (35-D, 34-3)
			XSSFCell modelName = sheet.getRow(34).getCell(3);
			modelName.setCellValue(ecoData.getModelDisplay());

			// 완제품 품번 (36-D, 35-3)
			String completePart = "";
			for (int i = 0; i < partList.size(); i++) {
				Map<String, Object> cmap = partList.get(i);
				completePart = completePart + cmap.get("Number");
				if (i != (partList.size() - 1)) {
					completePart += ",";
				}
			}
			XSSFCell completePartName = sheet.getRow(35).getCell(3);
			completePartName.setCellValue(completePart);

			// 변경사유(37-D, 36-3)
			XSSFCell eoCommentA = sheet.getRow(36).getCell(3);
			// PJT EDIT 20161122
			XSSFCellStyle cellStyle_UP = workbook.createCellStyle();
//			cellStyle_UP.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			cellStyle_UP.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_UP.setShrinkToFit(true);
			XSSFFont sfont = workbook.createFont();
			sfont.setFontHeightInPoints((short) 10);
			cellStyle_UP.setFont(sfont);
//			cellStyle_UP.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			cellStyle_UP.setVerticalAlignment(VerticalAlignment.TOP);
//			cellStyle_UP.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle_UP.setBorderBottom(BorderStyle.THIN);
//			cellStyle_UP.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle_UP.setBorderTop(BorderStyle.THIN);
			cellStyle_UP.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle_UP.setBottomBorderColor(HSSFColor.BLACK.index);

			cellStyle_UP.setWrapText(true);
			eoCommentA.setCellStyle(cellStyle_UP);
			eoCommentA.setCellValue(ecoData.commentA);
			XSSFRow comARow = (XSSFRow) sheet.getRow(36);
			int height = comARow.getHeight();
			String com = ecoData.commentA;
			if (null != com) {
				for (int i = 0; i < com.length(); i++) {
					char ca = com.charAt(i);
					Character careCa = new Character('\n');
					if (ca == careCa) {
						height += 150;
					}
				}
			}
			// System.out.println("heigh2t="+height);
			comARow.setHeight((short) height);
			// 변경근거(38-D, 37-3)
			XSSFCell ecrNo = sheet.getRow(37).getCell(3);
			// to enable newlines you need set a cell styles with wrap=true
			XSSFCellStyle cellStyleEcrNo_UP = workbook.createCellStyle();
//			cellStyleEcrNo_UP.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			cellStyleEcrNo_UP.setAlignment(HorizontalAlignment.LEFT);
			cellStyleEcrNo_UP.setShrinkToFit(true);
			cellStyleEcrNo_UP.setFont(sfont);
//			cellStyleEcrNo_UP.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			cellStyleEcrNo_UP.setVerticalAlignment(VerticalAlignment.TOP);
//			cellStyleEcrNo_UP.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			cellStyleEcrNo_UP.setBorderBottom(BorderStyle.MEDIUM);
//			cellStyleEcrNo_UP.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyleEcrNo_UP.setBorderTop(BorderStyle.THIN);
			cellStyleEcrNo_UP.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyleEcrNo_UP.setBottomBorderColor(HSSFColor.BLACK.index);

			cellStyleEcrNo_UP.setWrapText(true);
			ecrNo.setCellStyle(cellStyleEcrNo_UP);

			String ecrNumbersNames = "";
			ArrayList<String> list = ecoData.getECRNumberAndNames();
			for (String data_ecrNumbersNames : list) {
				ecrNumbersNames += (data_ecrNumbersNames + "\r\n");
			}
			XSSFRow ecrNumbersNamesRow = (XSSFRow) sheet.getRow(37);
			if (null != ecrNumbersNames) {
				for (int i = 0; i < ecrNumbersNames.length(); i++) {
					char ca = ecrNumbersNames.charAt(i);
					Character careCa = new Character('\n');
					if (ca == careCa) {
						height += 150;
					}
				}
			}
			ecrNumbersNamesRow.setHeight((short) height);
			ecrNo.setCellValue(ecrNumbersNames);

			/**
			 * 설계변경 부품 내역 46 Line(45 Index) 부터 ~
			 */

			int row = 45;

			List<Map<String, String>> excelList = getECO_RoleTypeExcelData(eco);

			for (Map<String, String> excelMap : excelList) {

				if (row > 45) {
					POIUtil.copyRow(workbook, sheet, (row - 1), 1);
				}

				// NO (B, 1)
				XSSFCell excelNo = sheet.getRow(row).getCell(1);
				excelNo.setCellValue((String) excelMap.get("no"));

				// 변경 전 품번 (C, 2)
				XSSFCell oldPartNumber = sheet.getRow(row).getCell(2);
				oldPartNumber.setCellValue((String) excelMap.get("oldPartNumber"));

				// 변경 후 품번 (F, 5)
				XSSFCell newPartNumber = sheet.getRow(row).getCell(5);
				newPartNumber.setCellValue((String) excelMap.get("newPartNumber"));

				// 품명 (G, 6)
				XSSFCell partName = sheet.getRow(row).getCell(6);
				partName.setCellValue((String) excelMap.get("partName"));

				// 부품 상태 코드 (I, 8)
				XSSFCell stateCode = sheet.getRow(row).getCell(8);
				stateCode.setCellValue((String) excelMap.get("stateCode"));

				// 납품 장비 (J, 9)
				XSSFCell deliveryProduct = sheet.getRow(row).getCell(9);
				deliveryProduct.setCellValue((String) excelMap.get("deliveryProduct"));

				// 완성 장비 (K, 10)
				XSSFCell completeProduct = sheet.getRow(row).getCell(10);
				completeProduct.setCellValue((String) excelMap.get("completeProduct"));

				// 사내 재고 (L, 11)
				XSSFCell companyStock = sheet.getRow(row).getCell(11);
				companyStock.setCellValue((String) excelMap.get("companyStock"));

				// 발주 부품 (M, 12)
				XSSFCell orderProduct = sheet.getRow(row).getCell(12);
				orderProduct.setCellValue((String) excelMap.get("orderProduct"));

				// 중량 (N, 13)
				XSSFCell wegiht = sheet.getRow(row).getCell(13);
				wegiht.setCellValue((String) excelMap.get("wegiht"));

				row++;
			}

			/**
			 * 설계변경 부품 내역 끝
			 */

			if (row == 45)
				row++;

			row++;
			row++;
			row++;

			// 설계변경 세부내용 (blank)
			sheet.getRow(row).setHeight((short) 90);
			row++;

			// 설계변경 세부내용 (B, 1)
			sheet.getRow(row).setHeight((short) 1050);

			XSSFCell eoCommentB = sheet.getRow(row).getCell(1);
			eoCommentB.setCellValue(ecoData.commentB);
			XSSFRow comBRow = (XSSFRow) sheet.getRow(row);
			int bheight = comBRow.getHeight();
			String comB = ecoData.commentB;
			if (null != comB) {
				for (int i = 0; i < comB.length(); i++) {
					char ca = comB.charAt(i);
					Character careCa = new Character('\n');
					if (ca == careCa) {
						bheight += 300;
					}
				}
			}

			// System.out.println("bheight="+bheight);
			comBRow.setHeight((short) bheight);
			row++;

			row++;
			row++;
			row++;

			/**
			 * 변경 문건 시작
			 */
			int startRow = row;
			int documentIndex = 1;

			List<Map<String, Object>> docList = ECAHelper.service.viewECA(eco);

			for (Map<String, Object> dmap : docList) {

				ECAData ecaData = (ECAData) dmap.get("ecaData");

				List<DocumentDTO> documentList = ecaData.getDocList();

				for (DocumentDTO docData : documentList) {
					if (row > startRow) {
						POIUtil.copyRow(workbook, sheet, (row - 1), 1);
					}

					// NO (B, 1)
					XSSFCell docNo = sheet.getRow(row).getCell(1);
					docNo.setCellValue(documentIndex);

					// 문서명 (E, 4)
					XSSFCell docName = sheet.getRow(row).getCell(4);
					// docName.setCellStyle(cellStyle_LEFT);
					docName.setCellValue(docData.name);

					// 문서번호 (I, 8)
					XSSFCell docNumber = sheet.getRow(row).getCell(8);
					docNumber.setCellValue(docData.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));

					// 버전 (M, 12)
					XSSFCell docRev = sheet.getRow(row).getCell(12);
					docRev.setCellValue(docData.version + "." + docData.iteration);

					documentIndex++;

					row++;
				}
			}

			if (startRow == row)
				row++;
			row++;
			// 위험관리
			XSSFCell licensing = sheet.getRow(row).getCell(4);
			// System.out.println("licensing =" + licensing);
			licensing.setCellValue(ecoData.getlicensingDisplay(false));
			// 위험 통제
			XSSFCell riskType = sheet.getRow(row).getCell(9);
			riskType.setCellValue(ChangeUtil.getRiskTypeName(ecoData.riskType, false));

			row++;
			row++;

			// 특기사항 (blank)
			sheet.getRow(row).setHeight((short) 90);
			row++;

			// 특기사항 (E, 4)
			sheet.getRow(row).setHeight((short) 1050);

			XSSFCell eoCommentC = sheet.getRow(row).getCell(4);
			eoCommentC.setCellValue(ecoData.commentC);
			XSSFRow comCRow = (XSSFRow) sheet.getRow(row);
			int cheight = comCRow.getHeight();
			String comC = ecoData.commentC;
			if (null != comC) {
				for (int i = 0; i < comC.length(); i++) {
					char ca = comC.charAt(i);
					Character careCa = new Character('\n');
					if (ca == careCa) {
						cheight += 350;
					}
				}
			}
			// System.out.println("bheight="+cheight);
			comCRow.setHeight((short) cheight);
			row++;

			// 기타사항 Blank
			sheet.getRow(row).setHeight((short) 90);
			row++;

			// 기타사항 (E, 4)
			sheet.getRow(row).setHeight((short) 1050);
			XSSFCell eoCommentD = sheet.getRow(row).getCell(4);
			eoCommentD.setCellValue(ecoData.commentD);
			XSSFRow comDRow = (XSSFRow) sheet.getRow(row);
			int dheight = comDRow.getHeight();
			String comd = ecoData.commentD;
			if (null != comd) {
				for (int i = 0; i < comd.length(); i++) {
					char ca = comd.charAt(i);
					Character careCa = new Character('\n');
					if (ca == careCa) {
						dheight += 350;
					}
				}
			}
			// System.out.println("dheight="+dheight);
			comDRow.setHeight((short) dheight);
			row++;

			row++;
			row++;
			row++;

			/**
			 * 첨부파일 시작 (기타사항 +4)
			 */

			startRow = row;
			int attachCount = 1;

			QueryResult attachQr = ContentHelper.service.getContentsByRole(eco, ContentRoleType.SECONDARY);

			while (attachQr.hasMoreElements()) {

				ContentItem item = (ContentItem) attachQr.nextElement();

				if (item != null) {

					if (row > startRow) {
						POIUtil.copyRow(workbook, sheet, (row - 1), 1);
					}

					ApplicationData appData = (ApplicationData) item;

					// NO (B, 1)
					XSSFCell attchNo = sheet.getRow(row).getCell(1);
					attchNo.setCellValue(attachCount);

					// 파일명 (C, 2)
					XSSFCell attchName = sheet.getRow(row).getCell(2);
					attchName.setCellValue(appData.getFileName());

					attachCount++;
					row++;
				}
			}

			/**
			 * 첨부파일 끝
			 */

			if (startRow == row)
				row++;
			row++;
			row++;
			row++;

			/**
			 * 결재자 의견 시작(첨부파일 +2)
			 */

			startRow = row;

			List<Map<String, Object>> appList = GroupwareHelper.service.getApprovalList(oid);

			for (Map<String, Object> amap : appList) {

				if (row > startRow) {
					POIUtil.copyRow(workbook, sheet, (row - 1), 1);
				}

				// 이름 (B, 1)
				XSSFCell name = sheet.getRow(row).getCell(1);
				name.setCellValue((String) amap.get("userName"));

				// 날짜 (D, 3)
				String processDate = "";
				if (amap.get("processDate") != null) {
					// System.out.println((amap.get("processDate")).getClass());
					processDate = DateUtil.subString(((Object) amap.get("processDate")).toString(), 0, 10);
				}

				XSSFCell date = sheet.getRow(row).getCell(3);
				date.setCellValue(processDate);

				// 내용 (F, 5)
				XSSFCell description = sheet.getRow(row).getCell(5);
				description.setCellValue((String) amap.get("comment"));
				XSSFRow comDescRow = (XSSFRow) sheet.getRow(row);
				int descheight = comDescRow.getHeight();
				String comdescd = (String) amap.get("comment");
				if (null != comdescd) {
					for (int i = 0; i < comdescd.length(); i++) {
						char ca = comdescd.charAt(i);
						Character careCa = new Character('\n');
						if (ca == careCa) {
							descheight += 350;
						}
					}
				}
				// System.out.println("descheight="+descheight);
				comDescRow.setHeight((short) descheight);

				row++;
			}

			/**
			 * 결재자 의견 끝
			 */

			// System.out.println("쓰기 종료....." + POIUtil.getSheetRow(sheet));

			file.close();

			FileOutputStream outFile = new FileOutputStream(newFile);
			workbook.write(outFile);
			outFile.close();

			workbook.close();

			data.setResult(true);
			data.setMessage(newFile.getName());

		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}

		return data;
	}

	private ResultData eoExcelDown(String oid) {

		ResultData data = new ResultData();
		try {

			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
			ECOData ecoData = new ECOData(eco);
			Map<String, String> map = ChangeUtil.getApproveInfo(eco);
			List<Map<String, Object>> list = ecoData.getCompletePartList();

			String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
			String path = WTProperties.getServerProperties().getProperty("wt.temp");

			File orgFile = new File(wtHome + "/codebase/com/e3ps/change/beans/eo.xlsx");

			File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + ecoData.number + ".xlsx"));

			FileInputStream file = new FileInputStream(newFile);

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			XSSFSheet sheet = workbook.getSheetAt(0);

			workbook.setSheetName(0, ecoData.number);

			// System.out.println("쓰기 시작....." + POIUtil.getSheetRow(sheet));

			XSSFCellStyle cellStyle_LEFT = workbook.createCellStyle();
//			cellStyle_LEFT.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			cellStyle_LEFT.setAlignment(HorizontalAlignment.LEFT);
//			cellStyle_LEFT.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			cellStyle_LEFT.setBorderBottom(BorderStyle.THIN);

			// 문서번호 (9-D ,8-3)
			XSSFCell documentNumber = sheet.getRow(8).getCell(6);
			documentNumber.setCellValue(ecoData.number);

			// 작성자 (17-K, 16-10)
			XSSFCell creator = sheet.getRow(16).getCell(10);
			creator.setCellValue(ecoData.creator);

			// 검토자 (17-M, 16-12)
			XSSFCell chk = sheet.getRow(16).getCell(12);
			chk.setCellValue(map.get("checkerName"));

			// 승인자 (17-O, 16-14)
			XSSFCell approver = sheet.getRow(16).getCell(14);
			approver.setCellValue(map.get("approveName"));

			// 작성일 (18-K, 17-10)
			XSSFCell creatDate = sheet.getRow(17).getCell(10);
			creatDate.setCellValue(ecoData.dateSubString(true));

			// 검토일 (18-M, 17-12)
			XSSFCell chkDate = sheet.getRow(17).getCell(12);
			chkDate.setCellValue(map.get("checkDate"));

			// 승인일 (18-O, 17-14)
			XSSFCell approveDate = sheet.getRow(17).getCell(14);
			approveDate.setCellValue(map.get("approveDate"));

			// 문서 번호 (29-D, 28-3)
			XSSFCell documentNumber2 = sheet.getRow(28).getCell(3);
			documentNumber2.setCellValue(ecoData.number);

			// 제목 (30-D, 29-3)
			XSSFCell documentName = sheet.getRow(29).getCell(3);
			documentName.setCellValue(ecoData.name);

			// 작성일 (32-D, 31-3)
			XSSFCell creatDate2 = sheet.getRow(31).getCell(3);
			creatDate2.setCellValue(ecoData.dateSubString(true));

			// 작성부서 (32-K, 31-10)
			XSSFCell createDept = sheet.getRow(31).getCell(10);
			createDept.setCellValue(ecoData.getCreatorDepartment());

			// 승인일 (33-D, 32-3)
			XSSFCell approveDate2 = sheet.getRow(32).getCell(3);
			approveDate2.setCellValue(map.get("approveDate"));

			// 작성자 (33-K, 32-10)
			XSSFCell creator2 = sheet.getRow(32).getCell(10);
			creator2.setCellValue(ecoData.creator);

			// 제품명 (35-D, 34-3)
			XSSFCell partName = sheet.getRow(34).getCell(3);
			partName.setCellValue(ecoData.getModelDisplay());

			// 완제품 품번 (36-D, 35-3)
			String completePart = "";
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> cmap = list.get(i);
				completePart = completePart + cmap.get("Number");
				if (i != (list.size() - 1)) {
					completePart += ",";
				}
			}
			XSSFCell completePartName = sheet.getRow(35).getCell(3);
			completePartName.setCellValue(completePart);

			// 제품 설계 개요(40-B, 39-2)
			XSSFCell eoCommentA = sheet.getRow(39).getCell(1);
			eoCommentA.setCellValue(ecoData.commentA);

			/**
			 * 이관 문건 시작 44 Line(43 Index) 부터 ~
			 */

			List<Map<String, Object>> docList = ECAHelper.service.viewECA(eco);

			int row = 43;

			int documentNo = 1;

			for (Map<String, Object> dmap : docList) {

				ECAData ecaData = (ECAData) dmap.get("ecaData");

				List<DocumentDTO> documentList = ecaData.getDocList();

				for (DocumentDTO docData : documentList) {

					POIUtil.copyRow(workbook, sheet, (row - 1), 1);

					// NO (B, 1)
					XSSFCell docNo = sheet.getRow(row).getCell(1);
					docNo.setCellValue(documentNo);

					// 문서명 (D, 3)
					XSSFCell docName = sheet.getRow(row).getCell(3);
					docName.setCellStyle(cellStyle_LEFT);
					docName.setCellValue(docData.name);

					// 문서번호 (J, 9)
					XSSFCell docNumber = sheet.getRow(row).getCell(9);
					docNumber.setCellValue(docData.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));

					// 버전 (N,13)
					XSSFCell docRev = sheet.getRow(row).getCell(13);
					docRev.setCellValue(docData.version + "." + docData.iteration);

					documentNo++;
					row++;
				}

			}
			sheet.removeRow(sheet.getRow(row));
			row++;

			/**
			 * 이관 문건 끝
			 */

			// 특기사항 Blank (46-F, 45-5)
			sheet.getRow(row).setHeight((short) 90);
			row++;

			// 특기사항 (47-F, 46-5)
			sheet.getRow(row).setHeight((short) 1050);
			XSSFCell eoCommentB = sheet.getRow(row).getCell(5);
			eoCommentB.setCellValue(ecoData.commentB);
			row++;

			// 기타사항 Blank (47-F, 46-5)
			sheet.getRow(row).setHeight((short) 90);
			row++;

			// 기타사항 (49-F, 48-5)
			sheet.getRow(row).setHeight((short) 1050);
			XSSFCell eoCommentC = sheet.getRow(row).getCell(5);
			eoCommentC.setCellValue(ecoData.commentC);
			row++;

			row++;
			row++;
			row++;

			/**
			 * 첨부파일 시작 (기타사항 +4)
			 */

			int attachCount = 1;

			QueryResult qr = ContentHelper.service.getContentsByRole(eco, ContentRoleType.SECONDARY);

			while (qr.hasMoreElements()) {

				ContentItem item = (ContentItem) qr.nextElement();

				if (item != null) {

					ApplicationData appData = (ApplicationData) item;

					// System.out.println("############### 첨부파일 시작 row = "+row);

					POIUtil.copyRow(workbook, sheet, (row), 1);

					// NO (B, 1)
					XSSFCell attchNo = sheet.getRow(row).getCell(1);
					attchNo.setCellValue(attachCount);

					// 파일명 (C, 2)
					XSSFCell attchName = sheet.getRow(row).getCell(2);
					attchName.setCellValue(appData.getFileName());

					attachCount++;
					row++;
				}
			}
			sheet.removeRow(sheet.getRow(row));
			row++;

			/**
			 * 첨부파일 끝
			 */

			row++;
			row++;

			row++;
			row++;
			row++;
			/**
			 * 결재자 의견 시작(첨부파일 +2)
			 */

			List<Map<String, Object>> appList = GroupwareHelper.service.getApprovalList(oid);
			for (Map<String, Object> amap : appList) {

				POIUtil.copyRow(workbook, sheet, (row - 1), 1);
				// PJT EDIT 강신우 요청으로 수정 해당 문서의 결재자 의견이 Data는 있으나 한칸 밀려있음.
				// 이름 (B, 1)
				XSSFCell name = sheet.getRow(row).getCell(1);
				String userName = StringUtil.checkNull((String) amap.get("userName"));
				name.setCellValue(userName);

				// 날짜 (D, 3)
				String processDate = "";
				if (amap.get("processDate") != null) {
					// System.out.println((amap.get("processDate")).getClass());
					processDate = DateUtil.subString(((Object) amap.get("processDate")).toString(), 0, 10);
				}

				XSSFCell date = sheet.getRow(row).getCell(3);
				date.setCellValue(processDate);

				// 내용 (G, 6)
				XSSFCell description = sheet.getRow(row).getCell(6);
				description.setCellValue((String) amap.get("comment"));

				row++;
			}
			// sheet.removeRow(sheet.getRow(row));

			/**
			 * 결재자 의견 끝
			 */

			// System.out.println("쓰기 종료....." + POIUtil.getSheetRow(sheet));

			file.close();

			FileOutputStream outFile = new FileOutputStream(newFile);
			workbook.write(outFile);
			outFile.close();

			workbook.close();

			data.setResult(true);
			data.setMessage(newFile.getName());

		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}

		return data;
	}

	private List<Map<String, String>> getECO_RoleTypeExcelData(EChangeOrder eco) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QueryResult roleQr = ContentHelper.service.getContentsByRole(eco, ContentRoleType.toContentRoleType("ECO"));

		while (roleQr.hasMoreElements()) {
			ContentItem item = (ContentItem) roleQr.nextElement();
			if (item != null) {
				ApplicationData pAppData = (ApplicationData) item;
				CommonContentHelper.service.fileDown(CommonUtil.getOIDString(pAppData));

				String path = WTProperties.getServerProperties().getProperty("wt.temp");
				File file = new File(path + "/" + pAppData.getFileName());

				// System.out.println("yhkim LL path ::" + file);

				XSSFWorkbook workbook = POIUtil.getWorkBook(file);
				XSSFSheet sheet = POIUtil.getSheet(workbook, 0);

				for (int i = 6; i < POIUtil.getSheetRow(sheet); i++) {

					XSSFRow row = sheet.getRow(i);

					// No
					String no = StringUtil.checkNull(POIUtil.getRowStringValue(row, 1));

					if (no.length() > 0) {

						Map<String, String> map = new HashMap<String, String>();

						map.put("no", no);

						// 변경 전 품번
						String oldPartNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
						map.put("oldPartNumber", oldPartNumber);

						// 변경 후 품번
						String newPartNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 3));
						map.put("newPartNumber", newPartNumber);

						// 품명
						String partName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 4));
						map.put("partName", partName);

						// 부품 상태 코드
						String stateCode = StringUtil.checkNull(POIUtil.getRowStringValue(row, 5));
						map.put("stateCode", stateCode);

						// 납품장비
						String deliveryProduct = StringUtil.checkNull(POIUtil.getRowStringValue(row, 6));
						map.put("deliveryProduct", deliveryProduct);

						// 완성 장비
						String completeProduct = StringUtil.checkNull(POIUtil.getRowStringValue(row, 7));
						map.put("completeProduct", completeProduct);

						// 사내재고
						String companyStock = StringUtil.checkNull(POIUtil.getRowStringValue(row, 8));
						map.put("companyStock", companyStock);

						// 발주 부품
						String orderProduct = StringUtil.checkNull(POIUtil.getRowStringValue(row, 9));
						map.put("orderProduct", orderProduct);

						// 중량
						String wegiht = StringUtil.checkNull(POIUtil.getRowStringValue(row, 10));
						map.put("wegiht", wegiht);

						list.add(map);
					}
				}
			}
		}
		return list;
	}

	/**
	 * eo 산출물 일괄 다운로드
	 */
	@Override
	public ResultData batchEOAttachDownAction(String oid, String describe) {
		ResultData returnData = new ResultData();
		try {
			EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
			List<BatchDownData> list = new ArrayList<BatchDownData>();
			list = CommonContentHelper.service.getAttachFileList((ContentHolder) eo, list, describe);

			// ECA 의 산출물
			List<EChangeActivity> ecalist = ECAHelper.service.getECAList(eo);
			for (EChangeActivity eca : ecalist) {

				list = CommonContentHelper.service.getAttachFileList((ContentHolder) eca, list, describe);

				List<WTDocument> docList = ECAHelper.service.getECADocument(eca);

				for (WTDocument doc : docList) {
					list = CommonContentHelper.service.getAttachFileList((ContentHolder) doc, list, describe);
				}
			}
			// HashMap<ApplicationData, String> mapTemp = new HashMap<ApplicationData,
			// String>();

			File zipFile = MakeZIPUtil.batcchAttchFileZip(list, eo.getEoNumber());
			// System.out.println("zipFile.getAbsolutePath() = " +
			// zipFile.getAbsolutePath());
			returnData.setResult(true);
			returnData.setMessage(zipFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
		}

		return returnData;
	}

	/**
	 * eo 도면 일괄 다운로드
	 */
	@Override
	public ResultData batchEODrawingDownAction(String oid, String describe) {
		ResultData returnData = new ResultData();
		try {
			EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
			List<BatchDownData> list = new ArrayList<BatchDownData>();

			List<WTPart> partList = ECOSearchHelper.service.ecoPartReviseList(eo);
			list = PartHelper.service.getEPMBatchDownList(list, partList, describe);

			File zipFile = MakeZIPUtil.batcchAttchFileZip(list, eo.getEoNumber());
			// System.out.println("zipFile.getAbsolutePath() = " +
			// zipFile.getAbsolutePath());
			returnData.setResult(true);
			returnData.setMessage(zipFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
		}

		return returnData;
	}

	/**
	 * ECO 완료시 완제품 재수집후 링크 생성
	 * 
	 * @param eco
	 * @throws Exception
	 */
	private void createCompleteProduction(EChangeOrder eco) throws Exception {

		// 설계 변경 대상 리스트
		List<WTPart> partList = new ArrayList<WTPart>();
		List<WTPart> completeList = new ArrayList<WTPart>();
		partList = ECOSearchHelper.service.ecoPartReviseList(eco);
		// 완제품 수집
		for (WTPart part : partList) {
			completeList = PartSearchHelper.service.getPartEndItem(part, completeList);
		}

		// 완제품 삭제
		deleteCompleteLink(eco);

		// 완제품 링크
		createCompleteLink(eco, completeList);
	}

	@Override
	public void completeECOTEST(EChangeOrder eco) {

		HashMap returnMap = new HashMap();
		Transaction trx = new Transaction();
		try {
			trx.start();
			String eoType = eco.getEoType();

			// BOM 반영
			//// System.out.println("0.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
			// +" BOM 반영");
			// EulHelper.service.completeEBOM(eco);

			if (eoType.equals(ECOKey.ECO_CHANGE)) {

				// 완제품 제수집
				createCompleteProduction(eco);

				// Part,CAD 상태 변경
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" 부품,도면 상태 변경");
				this.completePart(eco);

				// ERP 전송
				// System.out.println("3.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" ERP 전송");
				ERPHelper.service.sendERP(eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");

				createECOBaseline(eco);

			} else if (eoType.equals(ECOKey.ECO_PRODUCT)) {

				// 완제품의 하위 구조 승인 처리
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" 완제품의 하위 구조 승인 처리");
				List<WTPart> partList = productPartList(eco);
				completeProduct(partList, eco);

				// ERP 완제품의 전송
				//// System.out.println("3.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" ERP 전송");
				// ERPHelper.service.sendERP(eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");
				createECOBaseline(eco);

			} else if (eoType.equals(ECOKey.ECO_DEV)) {

				// 완제품의 하위 구조 승인 처리
				// System.out.println("1.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +","+eoType +" 완제품의 하위 구조 승인 처리");
				List<WTPart> partList = productPartList(eco);
				completeProduct(partList, eco);

				// Baseline 생성
				// System.out.println("2.[ChangeECOHelper]{completeECO} = " +eco.getEoNumber()
				// +" Baseline 생성");
				createECOBaseline(eco);

			}

			// ECO 승인이후 산출물 등록 Activity 자동 생성
			ECAHelper.service.createAutoActivity(eco);

			// 승인일 등록
			eco.setEoApproveDate(DateUtil.getToDay());
			PersistenceHelper.manager.modify(eco);

			trx.commit();
			trx = null;
		} catch (Exception e) {
			// System.out.println("[ChangeECOHelper]{completeECO} ERROR = " +
			// e.getLocalizedMessage());
			e.printStackTrace();

		} finally {

			if (trx != null) {
				trx.rollback();
			}
		}
	}

	@Override
	public void create(ECOData data) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

//	    	String name = StringUtil.checkNull((String) params.get("name"));
//	    	String eoType = StringUtil.checkNull((String) params.get("eoType"));
//	    	String[] ecrOids = params.get("ecrOid");
			// String[] models = params.get("model");
//	    	String[] partOids = params.get("partOid");
//	    	String[] isSelectBoms =params.get("isSelectBom");
			// String[] completeOids = params.get("completeOid");
//	    	String licensing = StringUtil.checkNull((String) params.get("licensing"));
//	    	String eoCommentA = StringUtil.checkNull((String) params.get("eoCommentA"));
//			String eoCommentB = StringUtil.checkNull((String) params.get("eoCommentB"));
//			String eoCommentC = StringUtil.checkNull((String) params.get("eoCommentC"));
//			String eoCommentD = StringUtil.checkNull((String) params.get("eoCommentD"));

//			String riskType = StringUtil.checkNull((String) params.get("riskType"));
//			String[] secondarys = params.get("SECONDARY");

			// 21.12.30_shjeong 기존 YYMM 으로 사용 시 12월 마지막주에는 다음 년도로 표기되는 오류로 인해 수정.
			Date currentDate = new Date();
			String number = "C" + new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

			EChangeOrder eco = EChangeOrder.newEChangeOrder();

			// 설계 대상 부품으로 제품,완제품,설계 변경 부품 수집
//	    	Map ecoMap=  getEODataCollection(partOids);

//	    	String model = (String)ecoMap.get("model");								//제품명
//	    	List<WTPart> completeList = (List<WTPart>)ecoMap.get("completeList"); 	//완제품
//	    	List<WTPart> changeList = (List<WTPart>)ecoMap.get("changeList");	  	//변경 대상

			eco.setEoName(data.getEoName());
			eco.setEoNumber(number);
			eco.setEoType(data.getEoType());
//	    	eco.setModel(model);
//	    	eco.setLicensingChange(data.get);
			eco.setEoCommentA(data.getEoCommentA());
			eco.setEoCommentB(data.getEoCommentB());
			eco.setEoCommentC(data.getEoCommentC());
			eco.setEoCommentD(data.getEoCommentD());
			eco.setRiskType(data.getRiskType());
			eco.setOwner(SessionHelper.manager.getPrincipalReference());

			String location = "/Default/설계변경/ECO";
			String lifecycle = "LC_ECO";

			// 분류체계,lifecycle 셋팅
			CommonHelper.service.setManagedDefaultSetting(eco, location, lifecycle);

			eco = (EChangeOrder) PersistenceHelper.manager.save(eco);

			// 활동 생성
//	    	boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// 관련 ECR
//	    	createReauestOrderLink(eco, ecrOids);

			// 완제품 생성
//	    	createCompleteLink(eco, completeList);

			// 설계 변경 부품
//	    	createPartLink(eco, changeList, null);

			// 첨부파일
//			CommonContentHelper.service.attach(eco, null, secondarys);

			// 설계변경 부품 내역파일
//	    	String ecoFile = params.get("ECO");

//			ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)eco, "ECO", ecoFile, false);

			trs.commit();
			trs = null;

//	    	result.setResult(true);
//			result.setOid(CommonUtil.getOIDString(eco));
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