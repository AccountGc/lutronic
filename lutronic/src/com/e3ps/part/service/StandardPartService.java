package com.e3ps.part.service;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.beans.VersionData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.distribute.util.MakeZIPUtil;
import com.e3ps.doc.service.DocumentQueryHelper;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.drawing.service.EpmUtilHelper;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.part.column.PartColumn;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.enterprise.BasicTemplateProcessor;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
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
import wt.method.MethodContext;
import wt.part.PartDocHelper;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.DBProperties;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

@SuppressWarnings("serial")
public class StandardPartService extends StandardManager implements PartService {

	public static StandardPartService newStandardPartService() throws Exception {
		final StandardPartService instance = new StandardPartService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Transaction trx = new Transaction();
		try {

			trx.start();

			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle")); // LifeCycle
			String view = StringUtil.checkNull((String) params.get("view")); // view
			String location = StringUtil.checkNull((String) params.get("location")); // 분류체계
			String wtPartType = StringUtil.checkNull((String) params.get("wtPartType"));
			String source = StringUtil.checkNull((String) params.get("source"));

			// String[] partNames = request.getParameterValues("partName");
			String partName1 = StringUtil.checkNull((String) params.get("partName1")); // 품목명1 (NumberCode)
			String partName2 = StringUtil.checkNull((String) params.get("partName2")); // 품목명2 (NumberCode)
			String partName3 = StringUtil.checkNull((String) params.get("partName3")); // 품목명3 (NumberCode)
			String partName4 = StringUtil.checkNull((String) params.get("partName4")); // 품목명4 (Key In)

			String partType1 = StringUtil.checkNull((String) params.get("partType1")); // 품목구분 (NumberCode)
			String partType2 = StringUtil.checkNull((String) params.get("partType2")); // 대 분류 (NumberCode)
			String partType3 = StringUtil.checkNull((String) params.get("partType3")); // 중 분류 (NumberCode)
			String seq = StringUtil.checkNull((String) params.get("seq")); // SEQ
			String etc = StringUtil.checkNull((String) params.get("etc")); // 기타

			// 품목 속성
			String unit = StringUtil.checkNull((String) params.get("unit")); // 단위 (NumberCode)
			String model = StringUtil.checkNull((String) params.get("model")); // 프로젝트 코드 (NumberCode, IBA)
			String productmethod = StringUtil.checkNull((String) params.get("productmethod")); // 제작방법 (NumberCode, IBA)
			String deptcode = StringUtil.checkNull((String) params.get("deptcode")); // 부서 (NumberCode, IBA)
			String weight = StringUtil.checkNull((String) params.get("weight")); // 무게 (Key IN, IBA)
			String manufacture = StringUtil.checkNull((String) params.get("manufacture")); // MANUTACTURE (NumberCode,
																							// IBA)
			String mat = StringUtil.checkNull((String) params.get("mat")); // 재질 (NumberCode, IBA)
			String finish = StringUtil.checkNull((String) params.get("finish")); // 후처리 (NumberCode, IBA)
			String remarks = StringUtil.checkNull((String) params.get("remarks")); // 비고 (Key IN, IBA)
			String specification = StringUtil.checkNull((String) params.get("specification")); // 사양 (Key IN, iBA)
			boolean temprary = (boolean) params.get("temprary");
			// 결재
			ArrayList<Map<String, String>> approvalRows = (ArrayList<Map<String, String>>) params.get("approvalRows");
			ArrayList<Map<String, String>> agreeRows = (ArrayList<Map<String, String>>) params.get("agreeRows"); 
			ArrayList<Map<String, String>> receiveRows = (ArrayList<Map<String, String>>) params.get("receiveRows");

			// 주 도면
			String primary = StringUtil.checkNull((String) params.get("primary"));

			// 관련 문서
			ArrayList<Map<String, String>> rows90 = (ArrayList<Map<String, String>>) params.get("rows90");

			// 관련 RoHs
			ArrayList<Map<String, String>> rows106 = (ArrayList<Map<String, String>>) params.get("rows106");

			// 첨부파일
			ArrayList<String> secondarys = (ArrayList<String>) params.get("secondary");

			// 첨부 추가
			String[] delocIds = (String[]) params.get("delocIds");

			String partName = "";
			String[] partNames = new String[] { partName1, partName2, partName3, partName4 };
			for (int i = 0; i < partNames.length; i++) {
				if (StringUtil.checkString(partNames[i])) {
					if (i != 0 && partName.length() != 0) {
						partName += "_";
					}
					partName += partNames[i];
				}
			}
			String partNumber = partType1 + partType2 + partType3;

			if (seq.length() == 0) {
				seq = SequenceDao.manager.getSeqNo(partNumber, "000", "WTPartMaster", "WTPartNumber");
			} else if (seq.length() == 1) {
				seq = "00" + seq;
			} else if (seq.length() == 2) {
				seq = "0" + seq;
			}

			if (etc.length() == 0) {
				etc = "00";
			} else if (etc.length() == 1) {
				etc = "0" + etc;
			}
			partNumber += seq + etc;

			if (partNumber.length() > 10) {
				throw new Exception(Message.get("허용된 품목번호의 길이가 아닙니다."));
			}

			WTPart part = WTPart.newWTPart();
			PDMLinkProduct product = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
			part.setContainer(product);

			part.setNumber(partNumber);
			part.setName(partName.trim());
			part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));

			if (wtPartType.length() > 0) {
				part.setPartType(PartType.toPartType(wtPartType));
			}
			if (source.length() > 0) {
				part.setSource(Source.toSource(source));
			}

			// 뷰 셋팅(Design 고정임)
			if (view.length() > 0) {
				ViewHelper.assignToView(part, ViewHelper.service.getView(view));
			}

			
			// 라이프사이클 셋팅
			
			// 폴더 셋팅
			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) part, folder);

			// 문서 lifeCycle 설정
			LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle,
					wtContainerRef);
			part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);
			

			part = (WTPart) PersistenceHelper.manager.save(part);

			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(part, state);
			}

			// IBA 설정
			CommonHelper.service.changeIBAValues(part, params);
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, partName, "string");

			// 주 도면
			if (primary.length() > 0) {
				params.put("oid", CommonUtil.getOIDString(part));
				params.put("epmfid", location);
				EPMDocument epm = DrawingHelper.service.createEPM(params);
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
				PersistenceServerHelper.manager.insert(link);
			}

			// 관련 문서 연결
			for (Map<String, String> row90 : rows90) {
				String gridState = row90.get("gridState");
				// 신규 혹은 삭제만 있다. (added, removed
				if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
					String oid = row90.get("oid");
					WTDocument ref = (WTDocument) CommonUtil.getObject(oid);
					WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, ref);
					PersistenceServerHelper.manager.insert(link);
				}
			}

			// 관련 ROHS 연결
			for (Map<String, String> row106 : rows106) {
				String gridState = row106.get("gridState");
				// 신규 혹은 삭제만 있다. (added, removed
				if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
					String oid = row106.get("oid");
					ROHSMaterial ref = (ROHSMaterial) CommonUtil.getObject(oid);
					PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, ref);
					PersistenceHelper.manager.save(link);
				}
			}

			// 첨부 파일
			for (String secondary : secondarys) {
				File vault = CommonContentHelper.manager.getFileFromCacheId(secondary);
				ApplicationData applicationData = ApplicationData.newApplicationData(part);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(part, applicationData, vault.getPath());
			}

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(part, agreeRows, approvalRows, receiveRows);
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.toString());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	@Override
	public void batch(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ArrayList<Map<String, Object>> gridData = (ArrayList<Map<String, Object>>) params.get("gridData");

			// 화면에서 모든 검증 처리 진행
			for (Map<String, Object> data : gridData) {
				NumberCode partType1 = (NumberCode) CommonUtil
						.getObject(StringUtil.checkNull((String) data.get("partType1")));
				data.put("partType1", partType1.getCode());
				NumberCode partType2 = (NumberCode) CommonUtil
						.getObject(StringUtil.checkNull((String) data.get("partType2")));
				data.put("partType2", partType2.getCode());
				NumberCode partType3 = (NumberCode) CommonUtil
						.getObject(StringUtil.checkNull((String) data.get("partType3")));
				data.put("partType3", partType3.getCode());
				data.put("temprary", false);
				create(data);
			}

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

	public WTPart createPart(Map<String, Object> map) throws Exception {
		String lifecycle = StringUtil.checkNull((String) map.get("lifecycle")); // LifeCycle
		String view = StringUtil.checkNull((String) map.get("view")); // view
		String fid = StringUtil.checkNull((String) map.get("fid")); // 분류체계
		String wtPartType = StringUtil.checkNull((String) map.get("wtPartType"));
		String source = StringUtil.checkNull((String) map.get("source"));

		String partName = StringUtil.checkNull((String) map.get("partName")); // 품목명
		String partNumber = StringUtil.checkNull((String) map.get("partNumber")); // 품목번호

		String seq = StringUtil.checkNull((String) map.get("seq")); // SEQ

		if (seq.length() == 0) {
			seq = SequenceDao.manager.getSeqNo(partNumber, "000", "WTPartMaster", "WTPartNumber");
		} else if (seq.length() == 1) {
			seq = "00" + seq;
		} else if (seq.length() == 2) {
			seq = "0" + seq;
		}

		String etc = StringUtil.checkNull((String) map.get("etc")); // etc
		if (etc.length() == 0) {
			etc = "00";
		} else if (etc.length() == 1) {
			etc = "0" + etc;
		}
		partNumber += seq + etc;

		if (partNumber.length() > 10) {
			throw new Exception(Message.get("허용된 품목번호의 길이가 아닙니다."));
		}

		String unit = StringUtil.checkNull((String) map.get("unit")); // 단위

		WTPart part = WTPart.newWTPart();
		PDMLinkProduct product = WCUtil.getPDMLinkProduct();
		WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
		part.setContainer(product);

		part.setNumber(partNumber);
		part.setName(partName.trim());
		part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));

		part.setPartType(PartType.toPartType(wtPartType));
		part.setSource(Source.toSource(source));

		// 뷰 셋팅(Design 고정임)
		ViewHelper.assignToView(part, ViewHelper.service.getView(view));

		// 폴더 셋팅
		Folder folder = null;
		if (StringUtil.checkString(fid)) {
			folder = (Folder) CommonUtil.getObject(fid);
		} else {
			folder = FolderTaskLogic.getFolder("/Default/PART_Drawing", WCUtil.getWTContainerRef());
		}
		FolderHelper.assignLocation((FolderEntry) part, folder);

		// 라이프사이클 셋팅
		LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef);
		part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);

		part = (WTPart) PersistenceHelper.manager.save(part);

		// IBA 설정
		CommonHelper.service.changeIBAValues(part, map);
		IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, partName, "string");

		// 주 도면
		String primary = StringUtil.checkNull((String) map.get("primary"));
		if (primary.length() > 0) {
			map.put("oid", CommonUtil.getOIDString(part));
			map.put("epmfid", fid);
			EPMDocument epm = DrawingHelper.service.createEPM(map);
			EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
			PersistenceServerHelper.manager.insert(link);

			IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, partName, "string");
			/*
			 * ResultData data = DrawingHelper.service.createDrawing(map); if(data.result) {
			 * String epmOid = data.oid; EPMDocument epm =
			 * (EPMDocument)CommonUtil.getObject(epmOid); EPMBuildRule link =
			 * EPMBuildRule.newEPMBuildRule(epm, part);
			 * PersistenceServerHelper.manager.insert(link); }else { throw new
			 * Exception(data.message); }
			 */
		}

		// 관련 문서 연결
		String[] docOids = (String[]) map.get("docOids");
		if (docOids != null) {
			for (String docOid : docOids) {
				WTDocument doc = (WTDocument) CommonUtil.getObject(docOid);
				WTPartDescribeLink dlink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(dlink);
			}
		}

		// 관련 ROHS 연결
		String[] rohsOid = (String[]) map.get("rohsOid");
		if (rohsOid != null) {
			RohsHelper.service.createROHSToPartLink(part, rohsOid);
		}

		// 첨부 파일
		String[] secondary = (String[]) map.get("secondary");
		if (secondary != null) {
			CommonContentHelper.service.attach(part, null, secondary);
		}

		return part;
	}

	@Override
	public Map<String, Object> updatePartAction(Map<String, Object> params) {
		Map<String, Object> data = new HashMap<String, Object>();

		ReferenceFactory rf = new ReferenceFactory();
		Transaction trx = new Transaction();

		WTPart part = null;
		// WTPart oldPart = null;

		try {
			String oid = StringUtil.checkNull((String) params.get("oid"));

			if (oid.length() > 0) {

				trx.start();

				part = (WTPart) rf.getReference(oid).getObject();
				PartData partData = new PartData(part);
				boolean isNonCheckout = !(partData.isGENERIC(part) || partData.isINSTANCE(part));
				String oldPartName = part.getName();

				part = (WTPart) getWorkingCopy(part);
				boolean temprary = (boolean) params.get("temprary");
				String partName1 = StringUtil.checkNull((String) params.get("partName1")); // 품목명1 (NumberCode)
				String partName2 = StringUtil.checkNull((String) params.get("partName2")); // 품목명2 (NumberCode)
				String partName3 = StringUtil.checkNull((String) params.get("partName3")); // 품목명3 (NumberCode)
				String partName4 = StringUtil.checkNull((String) params.get("partName4")); // 품목명4 (Key In)

				// 결재
				ArrayList<Map<String, String>> approvalRows = (ArrayList<Map<String, String>>) params.get("approvalRows");
				ArrayList<Map<String, String>> agreeRows = (ArrayList<Map<String, String>>) params.get("agreeRows");
				ArrayList<Map<String, String>> receiveRows = (ArrayList<Map<String, String>>) params.get("receiveRows");
				
				String partName = "";
				String[] partNames = new String[] { partName1, partName2, partName3, partName4 };
				for (int i = 0; i < partNames.length; i++) {
					if (StringUtil.checkString(partNames[i])) {
						if (i != 0 && partName.length() != 0) {
							partName += "_";
						}
						partName += partNames[i];
					}
				}

				if (!oldPartName.equals(partName)) {
					partReName(part, partName);
					IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, partName, "string");
				}

				// 단위 정보 셋팅
				String unit = StringUtil.checkNull((String) params.get("unit")); // 단위 (NumberCode)
				updateQuantityUnit(part, unit);

				part = (WTPart) PersistenceHelper.manager.modify(part);

				// IBA 설정
				CommonHelper.service.changeIBAValues(part, params);

				// 체크인

				if (WorkInProgressHelper.isCheckedOut(part)) {
					part = (WTPart) WorkInProgressHelper.service.checkin(part, Message.get("PDM에서 체크인 되었습니다"));
				}

				part = (WTPart) PersistenceHelper.manager.refresh(part);

				// 제품분류에 따라 폴더설정
				String fid = StringUtil.checkNull((String) params.get("fid")); // 분류체계

				if (fid.length() > 0) {
					Folder folder = (Folder) CommonUtil.getObject(fid);
					part = (WTPart) FolderHelper.service.changeFolder((FolderEntry) part, folder);
				}

				// 주도면
				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {
					if (isNonCheckout) {
						epm = (EPMDocument) getWorkingCopy(epm);

						// 체크인
						if (WorkInProgressHelper.isCheckedOut(epm)) {
							epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm,
									Message.get("PDM에서 체크인 되었습니다"));
						}
					}

					epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

					EpmUtilHelper.service.changeEPMName(epm, partName);

					CommonHelper.service.changeIBAValues(epm, params);
				} else {
					// 주 도면
					String primary = StringUtil.checkNull((String) params.get("primary"));
					if (primary.length() > 0) {
						params.put("oid", CommonUtil.getOIDString(part));
						params.put("epmfid", fid);
						params.put("lifecycle", "LC_Default");
						epm = DrawingHelper.service.createEPM(params);
						EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
						PersistenceServerHelper.manager.insert(link);
					}
				}

				// 관련 문서
				ArrayList<Map<String, String>> rows90 = (ArrayList<Map<String, String>>) params.get("rows90");
				// 기존 관련 문서 연결 해제
				QueryResult results = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class,
						false);
				while (results.hasMoreElements()) {
					WTPartDescribeLink link = (WTPartDescribeLink) results.nextElement();
					PersistenceServerHelper.manager.remove(link);
				}
				// 관련 문서 연결
				for (Map<String, String> row90 : rows90) {
					String gridState = row90.get("gridState");
					// 신규 혹은 삭제만 있다. (added, removed
					if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
						String docOid = row90.get("oid");
						WTDocument ref = (WTDocument) CommonUtil.getObject(docOid);
						WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, ref);
						PersistenceServerHelper.manager.insert(link);
					}
				}

				// 관련 ROHS
				ArrayList<Map<String, String>> rows106s = (ArrayList<Map<String, String>>) params.get("rows106");
				// 기존 관련 ROHS 연결 해제
				List<PartToRohsLink> list = RohsHelper.manager.getPartToRohsLinkList(part);
				for (PartToRohsLink link : list) {
					PersistenceServerHelper.manager.remove(link);
				}
				// 관련 ROHS 연결
				for (Map<String, String> rows106 : rows106s) {
					String gridState = rows106.get("gridState");
					// 신규 혹은 삭제만 있다. (added, removed
					if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
						String rohsOid = rows106.get("oid");
						ROHSMaterial ref = (ROHSMaterial) CommonUtil.getObject(rohsOid);
						PartToRohsLink link = PartToRohsLink.newPartToRohsLink(part, ref);
						PersistenceHelper.manager.save(link);
					}
				}

				// 첨부파일
				ArrayList<String> secondarys = (ArrayList<String>) params.get("secondary");

				QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
				while (qr.hasMoreElements()) {
					ContentItem item = (ContentItem) qr.nextElement();
					ContentServerHelper.service.deleteContent(part, item);
				}

				for (int i = 0; i < secondarys.size(); i++) {
					String cacheId = secondarys.get(i);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					ApplicationData applicationData = ApplicationData.newApplicationData(part);
					applicationData.setRole(ContentRoleType.SECONDARY);
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(part, applicationData, vault.getPath());
				}

				// Instance 속성 전파
				if (partData.isGENERIC(part)) {
					copyInstanceAttribute(part, params);
				}
				
				if (temprary) {
					State state = State.toState("TEMPRARY");
					LifeCycleHelper.service.setLifeCycleState(part, state);
				}else {
					State state = State.toState("INWORK");
	 				LifeCycleHelper.service.setLifeCycleState(part, state);
	 				
	 				// 결재시작
					if (approvalRows.size() > 0) {
						WorkspaceHelper.service.register(part, agreeRows, approvalRows, receiveRows);
					}
				}
				
				trx.commit();
				trx = null;

				data.put("result", true);
				data.put("oid", CommonUtil.getOIDString(part));
			}

		} catch (Exception e) {
			data.put("result", false);
			data.put("msg", e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return data;
	}

	@Override
	public String createPackagePartAction(HttpServletRequest request, HttpServletResponse response) {

		Transaction trx = new Transaction();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		boolean validation = true;
		int failCount = 0;

		try {

			trx.start();

			FileRequest req = new FileRequest(request);

			Map<String, String> fileMap = new HashMap<String, String>();
			String[] secondary = req.getParameterValues("SECONDARY");

			if (secondary != null) {
				for (String attachFile : secondary) {
					String fileName = attachFile.split("/")[1].toUpperCase();
					if (fileMap.get(fileName) == null) {
						fileMap.put(fileName, attachFile);
					} else {
						fileMap.remove(fileName);
					}
				}
			}

			String excelFile = req.getFileLocation("excelFile");
			String fid = req.getParameter("fid");

			File file = new File(excelFile);
			XSSFWorkbook workbook = POIUtil.getWorkBook(file);
			XSSFSheet sheet = POIUtil.getSheet(workbook, 0);

			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

			for (int i = 2; i < POIUtil.getSheetRow(sheet); i++) {

				validation = true;

				String fail = "";

				Map<String, Object> map = new HashMap<String, Object>();
				XSSFRow row = sheet.getRow(i);

				// NO
				String no = StringUtil.checkNull(POIUtil.getRowStringValue(row, 0));
				if (no.length() > 0) {

					String parentOid = "";

					// 품목구분
					String partType1 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 1));
					if (partType1.length() == 0) {
						validation = false;
						fail = Message.get("품목구분 코드값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("PARTTYPE", partType1, "0");
						if (code == null) {
							validation = false;
							fail = Message.get("품목구분에 적합한 코드가 없습니다.");
						}
						parentOid = CommonUtil.getOIDString(code);
					}

					// 대분류
					String partType2 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
					if (partType2.length() == 0) {
						validation = false;
						fail = Message.get("대분류 코드값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("PARTTYPE", partType2, parentOid);
						if (code == null) {
							validation = false;
							fail = Message.get("대분류에 적합한 코드가 없습니다.");
						}
						parentOid = CommonUtil.getOIDString(code);
					}

					// 중분류
					String partType3 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 3));
					if (partType3.length() == 0) {
						validation = false;
						fail = Message.get("중분류 코드값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("PARTTYPE", partType3, parentOid);
						if (code == null) {
							validation = false;
							fail = Message.get("중분류에 적합한 코드가 없습니다.");
						}
					}
					String partNumber = partType1 + partType2 + partType3;
					// 공백 제거
					partNumber = partNumber.replace(" ", "");
					map.put("partNumber", partNumber);

					// SEQ
					String seq = StringUtil.checkNull(POIUtil.getRowStringValue(row, 4));
					seq = seq.replace(" ", "");
					if (seq.length() == 1) {
						seq = "00" + seq;
					} else if (seq.length() == 2) {
						seq = "0" + seq;
					}
					map.put("seq", seq);

					// CUSTOM
					String etc = StringUtil.checkNull(POIUtil.getRowStringValue(row, 5));
					if (etc.length() == 0) {
						etc = "00";
					} else if (etc.length() == 1) {
						etc = "0" + etc;
					}
					etc = etc.replace(" ", "");
					map.put("etc", etc);

					WTPart part = PartHelper.service.getPart(partNumber + seq + etc);
					if (part != null) {
						validation = false;
						fail = Message.get("품목번호가 중복됩니다.");
					}

					// 재질 코드, 재질명
					String mat = StringUtil.checkNull(POIUtil.getRowStringValue(row, 6));
					if (mat.length() > 0) {
						NumberCode code = NumberCodeHelper.service.getNumberCode("MAT", mat);
						if (code == null) {
							validation = false;
							fail = Message.get("재질 코드에 적합한 코드가 없습니다.");
						}
					}
					String matName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 7));
					map.put("mat", mat);
					map.put("matName", matName);

					String partName = "";
					String partName1 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 8));
					if ("선택".equals(partName1)) {
						partName1 = "";
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCodeFormName("PARTNAME1", partName1);
						if (code == null) {
							validation = false;
							fail = Message.get("부품명1에 적합한 코드가 없습니다.");
						}
						if (partName.length() > 0) {
							partName = partName + "_";
						}
						partName = partName1;
					}

					String partName2 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 9));
					if ("선택".equals(partName2)) {
						partName2 = "";
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCodeFormName("PARTNAME2", partName2);
						if (code == null) {
							validation = false;
							fail = Message.get("부품명2에 적합한 코드가 없습니다.");
						}
						if (partName.length() > 0) {
							partName = partName + "_";
						}
						partName = partName + partName2;
					}

					String partName3 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 10));
					if ("선택".equals(partName3)) {
						partName3 = "";
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCodeFormName("PARTNAME3", partName3);
						if (code == null) {
							validation = false;
							fail = Message.get("부품명3에 적합한 코드가 없습니다.");
						}
						if (partName.length() > 0) {
							partName = partName + "_";
						}
						partName = partName + partName3;
					}

					String partName4 = StringUtil.checkNull(POIUtil.getRowStringValue(row, 11));
					if (partName4.length() > 0) {
						if (partName.length() > 0) {
							partName += "_";
						}
						partName += partName4;
					}

					if (partName.length() == 0) {
						validation = false;
						fail = Message.get("부품명이 없습니다.");
					} else if (partName.length() > 40) {
						validation = false;
						fail = Message.get("부품명은 40자 이내로 입력하여 주세요.(" + partName.length() + ")");
					}
					map.put("partName", partName);

					String unit = StringUtil.checkNull(POIUtil.getRowStringValue(row, 12));
					if (unit.length() == 0) {
						validation = false;
						fail = Message.get("단위 값이 없습니다.");
					}
					map.put("unit", unit.toLowerCase());

					// 부서코드, 부서명
					String deptCode = StringUtil.checkNull(POIUtil.getRowStringFomularValue(row, 13));
					// System.out.println("deptCode="+deptCode);
					if (deptCode.length() == 0) {
						validation = false;
						fail = Message.get("부서 코드 값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("DEPTCODE", deptCode);
						if (code == null) {
							validation = false;
							fail = Message.get("부서 코드에 적합한 코드가 없습니다.");
						}
					}
					String deptCodeName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 14));
					map.put("deptcode", deptCode);
					map.put("deptName", deptCodeName);

					// 프로젝트 코드, 프로젝트 명
					String model = StringUtil.checkNull(POIUtil.getRowStringFomularValue(row, 15));
					// System.out.println("model="+model);
					if (model.length() == 0) {
						validation = false;
						fail = Message.get("프로젝트코드 값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("MODEL", model);
						if (code == null) {
							validation = false;
							fail = Message.get("프로젝트코드에 적합한 코드가 없습니다.");
						}
					}
					String modelName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 16));
					map.put("model", model);
					map.put("modelName", modelName);

					// 제작방법 코드, 제작방법
					String productmethod = StringUtil.checkNull(POIUtil.getRowStringFomularValue(row, 17));
					// System.out.println("productmethod="+productmethod);
					if (productmethod.length() == 0) {
						validation = false;
						fail = Message.get("제작방법 코드 값이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("PRODUCTMETHOD", productmethod);
						if (code == null) {
							validation = false;
							fail = Message.get("제작방법 코드에 적합한 코드가 없습니다.");
						}
					}
					String productmethodName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 18));
					map.put("productmethod", productmethod);
					map.put("productmethodName", productmethodName);

					// 사양
					String specification = StringUtil.checkNull(POIUtil.getRowStringValue(row, 19));
					map.put("specification", specification);

					// 주 도면
					String mainEpm = StringUtil.checkNull(POIUtil.getRowStringValue(row, 20));
					if (mainEpm.length() > 0) {
						String primary = StringUtil.checkNull((String) fileMap.get(mainEpm.toUpperCase()));
						if (primary.length() == 0) {
							validation = false;
							fail = Message.get("주 도면이 첨부되지 않았습니다.");
						} else {
							fileMap.remove(mainEpm.toUpperCase());
							map.put("primary", primary);
						}
					}

					map.put("mainEpm", mainEpm);

					map.put("fid", fid);
					map.put("lifecycle", "LC_PART");
					map.put("view", "Design");
					map.put("wtPartType", "separable");
					map.put("source", "make");

					String oid = "";
					String fontColor = "";
					String lineValidation = "";

					if (validation) {

						try {
							WTPart ablePart = createPart(map);

							oid = CommonUtil.getOIDString(ablePart);
							partNumber = ablePart.getNumber();
							fontColor = "black";
							lineValidation = "true";

						} catch (Exception e) {
							e.printStackTrace();

							fontColor = "red";
							lineValidation = "fail";
							partNumber = partNumber + seq + etc;
							fail = e.getLocalizedMessage();
							failCount = failCount + 1;
						}
					} else {
						fontColor = "red";
						lineValidation = "fail";
						partNumber = partNumber + seq + etc;
						failCount = failCount + 1;
					}

					map.put("index", (i + 1));
					map.put("oid", oid);
					map.put("partNumber", partNumber);
					map.put("fontColor", fontColor);
					map.put("lineValidation", lineValidation);
					map.put("fail", fail);

					resultList.add(map);
				}
			}

			for (Map<String, Object> map : resultList) {

				int index = (Integer) map.get("index");
				String partNumber = (String) map.get("partNumber");
				String partName = (String) map.get("partName");
				String unit = (String) map.get("unit");
				String model = (String) map.get("model");
				String modelName = (String) map.get("modelName");
				String deptCode = (String) map.get("deptcode");
				String deptCodeName = (String) map.get("deptName");
				String productmethod = (String) map.get("productmethod");
				String productmethodName = (String) map.get("productmethodName");
				String mat = (String) map.get("mat");
				String matName = (String) map.get("matName");
				String specification = (String) map.get("specification");
				String mainEpm = (String) map.get("mainEpm");
				String fontColor = (String) map.get("fontColor");
				String lineValidation = (String) map.get("lineValidation");
				String fail = (String) map.get("fail");

				if (failCount == 0) {
					String oid = (String) map.get("oid");
					partNumber = "<a href=javascript:openView('" + oid + "')>" + partNumber + "</a>";
				}

				xmlBuf.append("<row id='" + index + "'>");
				xmlBuf.append("<cell><![CDATA[" + index + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + partNumber + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + WebUtil.getHtml(partName) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + unit + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + model + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + modelName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + deptCode + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + deptCodeName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + productmethod + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + productmethodName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + mat + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + matName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + specification + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + mainEpm + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + lineValidation + "</font>]]></cell>");
				xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + fail.replace("\"", "\'")
						+ "</font>]]></cell>");
				xmlBuf.append("</row>");

			}

			if (failCount == 0) {
				trx.commit();
				trx = null;
			} else {
				trx.rollback();
			}

		} catch (Exception e) {
			e.printStackTrace();
			xmlBuf.append("<row id='error'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[<font color='red'>fail</font>]]></cell>");
			xmlBuf.append("<cell><![CDATA[<font color='red'>" + e.getLocalizedMessage() + "</font>]]></cell>");
			xmlBuf.append("</row>");
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		xmlBuf.append("</rows>");

		return xmlBuf.toString();
	}

	@Override
	public ResultData createAUIPackagePartAction(HttpServletRequest request, HttpServletResponse response) {

		Transaction trx = new Transaction();
		ResultData data = new ResultData();
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
		try {

			trx.start();
			boolean totalValidation = true;
			FileRequest req = new FileRequest(request);
			// xmlString = PartHelper.service.createPackagePartAction(request, response);

			String fid = req.getParameter("fid");
			Map<String, String> fileMap = new HashMap<String, String>();
			String[] secondary = req.getParameterValues("SECONDARY");
			// System.out.println("SECONDARY =" + secondary);
			if (secondary != null) {
				for (String attachFile : secondary) {
					String fileName = attachFile.split("/")[1].toUpperCase();
					// System.out.println("fileName = " + fileName);
					if (fileMap.get(fileName) == null) {
						fileMap.put(fileName, attachFile);
					} else {
						fileMap.remove(fileName);
					}
				}
			}

			JSONArray item_json = new JSONArray(req.getParameter("rowList"));

			// System.out.println("item_json.length() = " + item_json.length());
			ArrayList<String> list = new ArrayList<String>();
			int cnt = 1;
			for (int i = 0; i < item_json.length(); i++) {

				Map<String, String> returnMap = new HashMap<String, String>();
				boolean validation = true;
				String msg = "";
				JSONObject item = item_json.getJSONObject(i);
				//// System.out.println("item =" + item);
				String id = (String) item.get("id");
				String state = (String) item.get("state");

				if (state.equals("S")) {
					continue;
				}
				//// System.out.println(i+".id =" + id);

				String partType1 = (String) item.get("partType1");
				String partType2 = (String) item.get("partType2");
				String partType3 = (String) item.get("partType3");
				String seq = (String) item.get("seq");
				String etc = (String) item.get("etc");

				String partName1 = ((String) item.get("partName1")).toUpperCase();
				String partName2 = ((String) item.get("partName2")).toUpperCase();
				String partName3 = ((String) item.get("partName3")).toUpperCase();
				String partName4 = ((String) item.get("partName4")).toUpperCase();

				String model = (String) item.get("model");
				String productmethod = (String) item.get("productmethod");
				String deptcode = (String) item.get("deptcode");
				String unit = (String) item.get("unit");
				String manufacture = (String) item.get("manufacture");
				String mat = (String) item.get("mat");
				String finish = (String) item.get("finish");
				String remarks = (String) item.get("remarks");
				Object tempweight = (Object) item.get("weight");
				String weight = "";
				if (tempweight instanceof Double) {
					weight = String.valueOf((double) tempweight);
				} else if (tempweight instanceof Integer) {
					weight = String.valueOf((int) tempweight);
				} else if (tempweight instanceof String) {
					String tempStringWeight = (String) tempweight;
					tempStringWeight = StringUtil.checkNull(tempStringWeight);
					if (tempStringWeight.length() == 0 || tempStringWeight.equals("0")) {
						weight = "0";
					} else {
						validation = false;
						totalValidation = false;
						msg = Message.get("무게는 숫자만 입력 가능합니다.");
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					}

				} else {
					validation = false;
					totalValidation = false;
					msg = Message.get("무게는 숫자만 입력 가능합니다.");
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnMap.put("msg", msg);
					returnList.add(returnMap);
					continue;
				}

				// double weight = (double)item.get("weight");
				String specification = (String) item.get("specification");
				String primary = (String) item.get("primary");

				// System.out.println("weight =" + weight);

				// Part 생성
				Map<String, Object> mapPart = new HashMap<String, Object>();

				String partNumber = partType1 + partType2 + partType3;

				seq = seq.replace(" ", "");
				if (seq.length() == 1) {
					seq = "00" + seq;
				} else if (seq.length() == 2) {
					seq = "0" + seq;
				} else if (seq.length() == 0) {
					String seqStr = SequenceDao.manager.getSeqNo(partNumber, "000", "WTPartMaster", "WTPartNumber");
					// System.out.println("list Size= "+list.size());
					// System.out.println("chk1 = "+(partNumber+":"+seqStr)+"=>"+
					// list.contains(partNumber+":"+seqStr));
					if (list.contains(partNumber + ":" + seqStr)) {
						for (int j = 0; j < list.size(); j++) {
							String tmp = list.get(j);
							// System.out.println("tmp:"+tmp+"\tchk="+(partNumber+":"+seqStr)+"\tCnt:"+cnt);
							if (tmp.contains(partNumber + ":" + seqStr)) {
								String seqG = tmp.substring(tmp.lastIndexOf(":") + 1);
								int tmpSeqG = Integer.parseInt(seqG);
								int resultSeqG = tmpSeqG + cnt;
								// System.out.println(seqG+"\t >>>> list set ["+i+"]="+resultSeqG);
								seq = "" + resultSeqG;
								if (seq.length() == 1) {
									seq = "00" + seq;
								} else if (seq.length() == 2) {
									seq = "0" + seq;
								}
								break;
							}
						}

						cnt++;
					} else {
						seq = seqStr;
						if (seq.length() == 1) {
							seq = "00" + seq;
						} else if (seq.length() == 2) {
							seq = "0" + seq;
						}
						String addSeqStr = partNumber + ":" + seqStr;
						// System.out.println(" >>>> list add ="+addSeqStr);
						list.add(addSeqStr);
					}

				}
				// System.out.println("final Seq number="+seq);
				mapPart.put("seq", seq);

				// CUSTOM

				if (etc.length() == 0) {
					etc = "00";
				} else if (etc.length() == 1) {
					etc = "0" + etc;
				}
				etc = etc.replace(" ", "");
				mapPart.put("etc", etc);
				// System.out.println("partNumber =" + partNumber+",seq =" +seq+",etc = "+etc);
				WTPart part = PartHelper.service.getPart(partNumber + seq + etc);
				if (part != null) {
					validation = false;
					totalValidation = false;
					msg = Message.get("품목번호가 중복됩니다.");
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnMap.put("msg", msg);
					returnList.add(returnMap);
					continue;
				}

				String partName = "";
				partName = PartUtil.partNameCheck(partName, partName1);
				partName = PartUtil.partNameCheck(partName, partName2);
				partName = PartUtil.partNameCheck(partName, partName3);
				partName = PartUtil.partNameCheck(partName, partName4);

				if (partName.length() == 0) {
					validation = false;
					totalValidation = false;
					msg = Message.get("부품명이 없습니다.");
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnMap.put("msg", msg);
					returnList.add(returnMap);
					continue;
				} else if (partName.length() > 40) {
					validation = false;
					totalValidation = false;
					msg = Message.get("부품명은 40자 이내로 입력하여 주세요.(" + partName.length() + ")");
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnMap.put("msg", msg);
					returnList.add(returnMap);
				}

				// 주 도면
				// String primary= "";
				if (primary.length() > 0) {
					primary = StringUtil.checkNull((String) fileMap.get(primary.toUpperCase()));
					if (primary.length() == 0) {
						validation = false;
						totalValidation = false;
						msg = Message.get("주 도면이 첨부되지 않았습니다.");
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						fileMap.remove(primary.toUpperCase());
						mapPart.put("primary", primary);
					}
				}

				// System.out.println(id+" , partName =" +partName);
				partNumber = partNumber.replace(" ", "");
				mapPart.put("partNumber", partNumber);
				mapPart.put("partName", partName);
				mapPart.put("model", model);
				mapPart.put("productmethod", productmethod);
				mapPart.put("deptcode", deptcode);
				mapPart.put("unit", unit);
				mapPart.put("manufacture", manufacture);
				mapPart.put("mat", mat);
				mapPart.put("finish", finish);
				mapPart.put("remarks", remarks);
				mapPart.put("weight", weight);
				mapPart.put("specification", specification);

				mapPart.put("fid", fid);
				mapPart.put("lifecycle", "LC_PART");
				mapPart.put("view", "Design");
				mapPart.put("wtPartType", "separable");
				mapPart.put("source", "make");

				// mapPart.put("mainEpm", mainEpm);

				try {
					WTPart newPart = createPart(mapPart);
					returnMap.put("number", newPart.getNumber());
					returnMap.put("oid", CommonUtil.getOIDString(newPart));
					returnMap.put("msg", "등록 성공");
					returnMap.put("state", "S");
					returnMap.put("id", id);
					returnList.add(returnMap);
				} catch (Exception e) {
					totalValidation = false;
					e.printStackTrace();
					returnMap.put("number", "");
					returnMap.put("oid", "");
					returnMap.put("msg", e.getLocalizedMessage());
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnList.add(returnMap);
				}

			}

			data.setReturnList(returnList);

			if (totalValidation) {
				trx.commit();
				trx = null;
				data.setResult(true);
			} else {
				data.setResult(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
				trx = null;
			}
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> partBomListGrid(String oid) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		View[] views = ViewHelper.service.getAllViews();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		PartTreeData root = broker.getTree(part, !"false".equals(null), null,
				ViewHelper.service.getView(views[0].getName()));
		broker.setHtmlForm(root, result);

		String[] lineStack = new String[50];

		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			PartTreeData data = (PartTreeData) result.get(i);

			String img = "";
			for (int j = 1; j < data.level; j++) {
				String line = lineStack[j];
				if (line == null) {
					line = "empty";
				}
				img += "<img src='/Windchill/jsp/part/images/tree/" + line + ".gif'></img>";
			}
			map.put("img", img);

			String img2 = "";
			if (data.level > 0) {
				if ("join".equals(data.lineImg)) {
					lineStack[data.level] = "line";
				} else
					lineStack[data.level] = "empty";
				img2 += "<img src='/Windchill/jsp/part/images/tree/" + data.lineImg + ".gif' border=0></img>";
			}
			map.put("img2", img2);
			String icon = CommonUtil.getObjectIconImageTag(data.part);
			map.put("icon", icon);

			map.put("partOid", data.part.getPersistInfo().getObjectIdentifier().toString());
			map.put("number", data.number);
			map.put("name", data.name);
			map.put("rev", data.version);

			map.put("productmethod",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_PRODUCTMETHOD)));
			map.put("model", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MODEL)));
			map.put("deptcode",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_DEPTCODE)));
			map.put("unit", part.getDefaultUnit().toString());
			map.put("weight",
					StringUtil.checkNull(IBAUtil.getAttrfloatValue(data.part, AttributeKey.IBAKey.IBA_WEIGHT)));
			map.put("manufacture",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MANUFACTURE)));
			map.put("mat", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MAT)));
			map.put("finish", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_FINISH)));
			map.put("remark", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_REMARKS)));
			map.put("specification",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_SPECIFICATION)));

			list.add(map);
		}

		return list;

	}

	@Override
	public ResultData partStateChange(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();

		try {
			String oid = request.getParameter("oid");
			String state = request.getParameter("state");

			ReferenceFactory rf = new ReferenceFactory();
			Object object = null;
			if (oid.length() > 0) {
				object = rf.getReference(oid).getObject();
			}

			LifeCycleManaged lcm = (wt.lifecycle.LifeCycleManaged) object;
			WFItem wfItem = WFItemHelper.service.getWFItem((WTObject) lcm);
			boolean isUseChage = false;
			if (wfItem != null) {
				isUseChage = true;
				String id = wfItem.getOwnership().getOwner().getName();
				SessionHelper.manager.setPrincipal(id);
			}
			if ("INWORK".equals(state)) {
				// System.out.println("********** 결재선 초기화 시작 ********** " + wfItem);

				if (wfItem != null) {
					wfItem.setObjectState(state);
					wfItem = (WFItem) PersistenceHelper.manager.modify(wfItem);
					WFItemHelper.service.reworkDataInit(wfItem);
				}
				// System.out.println("********** 결재선 초기화 끝 **********");

				EPMDocument epm = DrawingHelper.service.getEPMDocument((WTPart) object);
				if (epm != null) {
					EpmUtilHelper.service.changeEPMState(epm, state);
				}
			}

			lcm = LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state), true);
			if (isUseChage) {
				SessionHelper.manager.setAdministrator();
			}

			data.setResult(true);

		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getMessage());
		}

		return data;
	}

	@Override
	public ResultData updatePackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();

		Transaction trx = new Transaction();

		String[] modifyChecks = request.getParameterValues("modifyChecks");
		// String[] numbers = request.getParameterValues("number");
		// String[] oids = request.getParameterValues("oids");
		String oidGENERIC = StringUtil.checkNull(request.getParameter("oid"));
		WTPart part = null;
		try {
			trx.start();

			for (int i = 0; i < modifyChecks.length; i++) {
				String modifyNumber = modifyChecks[i];

				String oid = StringUtil.checkNull(request.getParameter("oids_" + modifyNumber));

				// System.out.println("=================================== modifyNumber
				// =========================== " + modifyNumber);
				// System.out.println("=================================== oid
				// ==================================== " + oid);

				part = (WTPart) CommonUtil.getObject(oid);
				PartData partData = new PartData(part);

				// GENERIC,INSTANCE 는 체크인 체크 아웃을 하지 않는다.
				boolean isNonCheckout = !(partData.isGENERIC() || partData.isINSTANCE());

				if (isNonCheckout) {
					part = (WTPart) getWorkingCopy(part);
					// 체크인
					if (WorkInProgressHelper.isCheckedOut(part)) {
						part = (WTPart) WorkInProgressHelper.service.checkin(part, Message.get("PDM 일괄수정에서 체크인 되었습니다"));
					}
				}

				part = (WTPart) PersistenceHelper.manager.refresh(part);

				String model = StringUtil.checkNull(request.getParameter("model_" + modifyNumber));
				String deptcode = StringUtil.checkNull(request.getParameter("deptcode_" + modifyNumber));
				String productmethod = StringUtil.checkNull(request.getParameter("productmethod_" + modifyNumber));
				String unit = StringUtil.checkNull(request.getParameter("unit_" + modifyNumber));

				String manufacture = StringUtil.checkNull(request.getParameter("manufacture_" + modifyNumber));
				String finish = StringUtil.checkNull(request.getParameter("finish_" + modifyNumber));
				String weight = StringUtil.checkNull(request.getParameter("weight_" + modifyNumber));
				String mat = StringUtil.checkNull(request.getParameter("mat_" + modifyNumber));
				String remarks = StringUtil.checkNull(request.getParameter("remarks_" + modifyNumber));
				String specification = StringUtil.checkNull(request.getParameter("specification_" + modifyNumber));

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("model", model);
				map.put("productmethod", productmethod);
				map.put("deptcode", deptcode);
				map.put("unit", unit);
				map.put("manufacture", manufacture);
				map.put("finish", finish);
				map.put("weight", weight);
				map.put("mat", mat);
				map.put("remarks", remarks);
				map.put("specification", specification);

				updateQuantityUnit(part, unit);
				CommonHelper.service.changeIBAValues((IBAHolder) part, map);

				// Instance 속성 전파
				if (partData.isGENERIC()) {
					copyInstanceAttribute(part, map);
				}

				// 주도면
				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {

					if (isNonCheckout) {
						epm = (EPMDocument) getWorkingCopy(epm);
						// 체크인
						if (WorkInProgressHelper.isCheckedOut(epm)) {
							epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm,
									Message.get("PDM에서 체크인 되었습니다"));
						}
					}

					epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

					CommonHelper.service.changeIBAValues(epm, map);
					updateQuantityUnit(epm, unit);
				}

			}

			trx.commit();
			trx = null;
			data.setResult(true);
			String returnOid = PartSearchHelper.service.isLastPart(oidGENERIC);
			if (returnOid != null) {
				data.setOid(oidGENERIC);
			} else {
				data.setOid(CommonUtil.getOIDString(part));
			}

		} catch (Exception e) {
			data.setResult(false);
			data.setMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return data;
	}

	@Override
	public ResultData updateAUIPackagePartAction(Map<String, Object> param) {
		ResultData data = new ResultData();

		Transaction trx = new Transaction();

		// String[] numbers = request.getParameterValues("number");
		// String[] oids = request.getParameterValues("oids");
		// System.out.println("=========== updateAUIPackagePartAction ============");
		WTPart part = null;
		try {
			trx.start();
			List<Map<String, Object>> itemList = (List<Map<String, Object>>) param.get("rowList");
			String pOid = (String) param.get("oid");
			WTPart pPart = (WTPart) CommonUtil.getObject(pOid);

			for (Map<String, Object> itemMap : itemList) {

				//// System.out.println("updateAUIPackagePartAction itemMap =" + itemMap);
				// Map<String,Object> item=(Map<String,Object>)itemMap.get("item");
				//// System.out.println("updateAUIPackagePartAction item =" + item);
				String cOid = (String) itemMap.get("oid");

				part = (WTPart) CommonUtil.getObject(cOid);
				PartData partData = new PartData(part);

				// 승인된 품목 수정 불가
				/*
				 * if(partData.isApproved()){ throw new
				 * Exception(partData.number+"는 승인됨 상태라 수정 할수 없습니다."); }
				 */

				String number = (String) itemMap.get("number");
				String model = (String) itemMap.get("model");
				String productmethod = (String) itemMap.get("productmethod");
				String deptcode = (String) itemMap.get("deptcode");
				String unit = (String) itemMap.get("unit");
				String manufacture = (String) itemMap.get("manufacture");
				String mat = (String) itemMap.get("mat");
				String finish = (String) itemMap.get("finish");
				String weight = (String) itemMap.get("weight");
				String remark = (String) itemMap.get("remark");

				String specification = (String) itemMap.get("specification");

				// System.out.println(cOid+","+number+","+model+","+productmethod+","+deptcode+","+unit+","+manufacture+","+mat+","+remark+","+specification);

				// GENERIC,INSTANCE 는 체크인 체크 아웃을 하지 않는다.
				// boolean isNonCheckout = !(partData.isGENERIC() || partData.isINSTANCE());

				/*
				 * if(isNonCheckout){ part = (WTPart)getWorkingCopy(part); // 체크인 if
				 * (WorkInProgressHelper.isCheckedOut(part)) { part = (WTPart)
				 * WorkInProgressHelper.service.checkin(part,
				 * Message.get("PDM 일괄수정에서 체크인 되었습니다")); } }
				 */
				// part = (WTPart) PersistenceHelper.manager.refresh(part);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("model", model);
				map.put("productmethod", productmethod);
				map.put("deptcode", deptcode);
				map.put("unit", unit);
				map.put("manufacture", manufacture);
				map.put("finish", finish);
				map.put("weight", weight);
				map.put("mat", mat);
				map.put("remarks", remark);
				map.put("specification", specification);

				// updateQuantityUnit(part, unit);
				CommonHelper.service.changeIBAValues((IBAHolder) part, map);

				// 주도면
				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {
					/*
					 * if(isNonCheckout){ epm = (EPMDocument)getWorkingCopy(epm); // 체크인 if
					 * (WorkInProgressHelper.isCheckedOut(epm)) { epm = (EPMDocument)
					 * WorkInProgressHelper.service.checkin(epm, Message.get("PDM에서 체크인 되었습니다")); }
					 * 
					 * }
					 */
					epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

					CommonHelper.service.changeIBAValues(epm, map);
					// updateQuantityUnit(epm, unit);
				}
			}

			trx.commit();
			trx = null;
			data.setResult(true);
			WTPart plPart = PartHelper.service.getPart(pPart.getNumber());

			data.setOid(CommonUtil.getOIDString(plPart));

		} catch (Exception e) {
			data.setResult(false);
			data.setMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return data;
	}

	@Override
	public Vector<String> getQuantityUnit() {
		Vector<String> vec = new Vector<String>();

		QuantityUnit[] qa = QuantityUnit.getQuantityUnitSet();
		for (int i = 0; i < qa.length; i++) {
			if (!qa[i].isSelectable())
				continue;
			if ("ea".equals(qa[i].toString())) {
				vec.add(qa[i].toString());
			}
		}
		return vec;
	}

	@Override
	public List<PartDTO> include_PartList(String oid, String moduleType) throws Exception {
		List<PartDTO> list = new ArrayList<PartDTO>();
		if (oid.length() > 0) {
			QueryResult rt = null;
			Object obj = (Object) CommonUtil.getObject(oid);
			if ("doc".equals(moduleType)) {
				WTDocument doc = (WTDocument) obj;
				rt = PartDocHelper.service.getAssociatedParts(doc);
				while (rt.hasMoreElements()) {
					WTPart part = (WTPart) rt.nextElement();
					PartDTO data = new PartDTO(part);
					list.add(data);
				}
			} else if ("drawing".equals(moduleType)) {
				EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(epm, "describes", EPMDescribeLink.class);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					PartDTO data = new PartDTO(part);
					list.add(data);
				}
			} else if ("ecr".equals(moduleType)) {
				EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(ecr, "part", EcrPartLink.class, false);
				while (qr.hasMoreElements()) {
					EcrPartLink link = (EcrPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.service.getPart(master.getNumber());
					PartDTO data = new PartDTO(part);

					list.add(data);
				}
			} else if ("eco".equals(moduleType.toLowerCase())) {
				EChangeOrder eco = (EChangeOrder) obj;
				rt = ECOSearchHelper.service.ecoPartLink(eco);
				while (rt.hasMoreElements()) {
					Object[] o = (Object[]) rt.nextElement();

					EcoPartLink link = (EcoPartLink) o[0];

					WTPartMaster master = (WTPartMaster) link.getPart();
					String version = link.getVersion();

					WTPart part = PartHelper.service.getPart(master.getNumber());
					PartDTO data = new PartDTO(part);
					// if(link.isBaseline()) data.setBaseline("checked");

					list.add(data);
				}
			} else if ("rohs".equals(moduleType)) {
				ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
				list = RohsHelper.manager.getROHSToPartList(rohs);
				Collections.sort(list, new ObjectComarator());
			}
		}
		return list;
	}

	@Override
	public Map<String, String> getAttributes(String oid) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartData data = new PartData(part);

		// IBA Attribute
		String partNo = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_PARTNO));
		String dwgNo = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DWGNO));
		String description = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DESCRIPTION));
		String version = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_VERSION));
		String date = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DATE));
		String approval = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_APPROVAL));
		String check = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_CHECK));
		String drawing = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DRAWING));

		String material = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_MATERIAL));
		String SPEC = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SPEC));
		String surface = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SURFACE));
		String subcontract = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUBCONTRACT));
		String supplierTemp = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUPPLIER));
		String supplier = "";
		if (supplierTemp.length() != 0) {
			supplier = CodeHelper.service.getName("SUPPLIER", supplierTemp);
		}
		String vendor = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_VENDOR));
		String weight = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_WEIGHT));
		String comment = StringUtil.checkNull(IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_COMMENT));

		Map<String, String> map = new HashMap<String, String>();

		map.put("partNo", partNo);
		map.put("dwgNo", dwgNo);
		map.put("description", description);
		map.put("version", version);
		map.put("date", date);
		map.put("approval", approval);
		map.put("check", check);
		map.put("drawing", drawing);
		map.put("material", material);
		map.put("SPEC", SPEC);
		map.put("surface", surface);
		map.put("subcontract", subcontract);
		// map.put("unit", data.getUnit());
		map.put("supplier", supplier);
		map.put("vendor", vendor);
		map.put("weight", weight);
		map.put("comment", comment);

		return map;
	}

	@Override
	public Map<String, Object> delete(Map<String, Object> params) throws Exception {

		Map<String, Object> rtnVal = new HashMap<String, Object>();
		Transaction trx = new Transaction();
		String oid = (String) params.get("oid");
		boolean isDelete = true;

		boolean result = true;
		String msg = Message.get("삭제되었습니다");

		try {
			trx.start();

			if (StringUtil.checkString(oid)) {
				ReferenceFactory rf = new ReferenceFactory();
				WTPart part = (WTPart) rf.getReference(oid).getObject();

				// LENI_TODO 삭제 관련정보 점검
				PartData data = new PartData(part);
				QueryResult linkQr = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class);
				QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
				List<PartToRohsLink> list = RohsHelper.manager.getPartToRohsLinkList(part);

				// 도면
				if (isDelete && !"".equals(data.getEpmOid())) { // && (data.epmDoc != null)) {
					isDelete = false;
					// rtnVal.put("msg", "품목과 연계된 도면이 존재합니다.");
					// rtnVal.put("oid", oid);
					result = false;
					msg = Message.get("품목과 연계된 도면이 존재합니다.");
				}

				// 관련문서
				if (isDelete && linkQr.hasMoreElements()) {
					isDelete = false;
					// rtnVal.put("msg", "품목과 연계된 문서가 존재합니다.");
					// rtnVal.put("oid", oid);
					result = false;
					msg = Message.get("품목과 연계된 문서가 존재합니다.");
				}

				if (isDelete && !list.isEmpty()) {
					isDelete = false;
					result = false;
					msg = Message.get("품목과 연계된 물질이 존재합니다.");
				}

				// 관련EO
				if (isDelete && eolinkQr.hasMoreElements()) {
					isDelete = false;
					// rtnVal.put("msg", "품목과 연계된 EO가 존재합니다.");
					// rtnVal.put("oid", oid);
					result = false;
					msg = Message.get("품목과 연계된 EO가 존재합니다.");
				}

				if (isDelete && WorkInProgressHelper.isCheckedOut(part)) {
					isDelete = false;
					// rtnVal.put("msg", "체크아웃되어 있어서 삭제하실 수 없습니다.");
					// rtnVal.put("oid", oid);
					result = false;
					msg = Message.get("체크아웃되어 있어서 삭제하실 수 없습니다.");
				}

				if (isDelete) {
					part = (WTPart) PersistenceHelper.manager.delete(part);

					result = true;
					msg = Message.get("삭제되었습니다");

					// rtnVal.put("rslt", "S");
					// rtnVal.put("oid", "");
					// rtnVal.put("msg", "삭제되었습니다");

					trx.commit();
					trx = null;
				}
			}
		} catch (Exception e) {
			// rtnVal.put("rslt", "F");
			// rtnVal.put("oid", oid);
			// rtnVal.put("msg", "삭제 실패하였습니다");
			result = false;
			msg = Message.get("삭제 실패하였습니다. ") + "\n" + e.getLocalizedMessage();

			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		rtnVal.put("result", result);
		rtnVal.put("msg", msg);

		return rtnVal;
	}

	/**
	 * �섏젙泥섎━
	 * 
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	@Override
	public Hashtable modify(Map hash, String[] loc, String[] deloc) throws Exception {

		Hashtable rtnVal = new Hashtable();
		Transaction trx = new Transaction();

		try {
			trx.start();

			rtnVal = modify(hash, false, loc, deloc);

			if ("F".equals(rtnVal.get("rslt"))) {
				rtnVal.put("oid", hash.get("oid"));
				trx.rollback();
				return rtnVal;
			}

			rtnVal.put("rslt", "S");
			rtnVal.put("msg", Message.get("수정 성공하였습니다."));
			if (((String) rtnVal.get("oid")) == null || ((String) rtnVal.get("oid")).length() == 0) {
				rtnVal.put("oid", hash.get("oid"));
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			rtnVal.put("rslt", "F");
			rtnVal.put("msg",
					e.getLocalizedMessage() == null ? Message.get("수정시 오류가 발생하였습니다.") : e.getLocalizedMessage());
			rtnVal.put("oid", "");
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return rtnVal;
		// ##end createPart%3E48C16400F2.body
	}

	/**
	 * �섏젙泥섎━
	 * 
	 * @param hash
	 * @param reviseFlag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public Hashtable modify(Map hash, final boolean reviseFlag, String[] loc, String[] deloc) throws Exception {
		Hashtable rtnVal = new Hashtable();

		ReferenceFactory rf = new ReferenceFactory();

		WTPart part = null;
		WTPart oldPart = null;
		WTPartMaster partMaster = null;
		boolean changeNameCheck = false;

		try {

			String oid = StringUtil.checkNull((String) hash.get("oid")); // oid
			String partName = StringUtil.checkNull((String) hash.get("partName")); // 품명
			String quantityUnit = StringUtil.checkNull((String) hash.get("Unit")); // 단위
			/*
			 * 
			 * 
			 * String oldNumber = StringUtil.checkNull((String) hash.get("oldNumber")); //구
			 * 번호 String designBy = StringUtil.checkNull((String) hash.get("designBy"));
			 * //설계자 String drawnDate = StringUtil.checkNull((String)
			 * hash.get("drawnDate")); //설계일자 String surface = StringUtil.checkNull((String)
			 * hash.get("surface")); //표면처리 String heat = StringUtil.checkNull((String)
			 * hash.get("heat")); //열처리 String processType = StringUtil.checkNull((String)
			 * hash.get("processType")); //가공유형 String maker = StringUtil.checkNull((String)
			 * hash.get("maker")); //제조사 String comment = StringUtil.checkNull((String)
			 * hash.get("comment")); //설명
			 */
			String Material = StringUtil.checkNull((String) hash.get("Material")); // Material
			String SPEC = StringUtil.checkNull((String) hash.get("SPEC")); // SPEC
			String Surface = StringUtil.checkNull((String) hash.get("Surface")); // 표면처리
			String Subcontract = StringUtil.checkNull((String) hash.get("Subcontract")); // 사급/도급
			String Unit = StringUtil.checkNull((String) hash.get("Unit")); // 단위
			String Supplier = StringUtil.checkNull((String) hash.get("Supplier")); // Supplier
			String Vendor = StringUtil.checkNull((String) hash.get("Vendor")); // Vendor
			String Weight = StringUtil.checkNull((String) hash.get("Weight")); // Weight
			String Comment = StringUtil.checkNull((String) hash.get("Comment")); // 설명

			String[] docOids = (String[]) hash.get("docOids"); // 관련 문서

			String[] epmOids = (String[]) hash.get("epmOids"); // 관련 도면

			String primary = StringUtil.checkNull((String) hash.get("primary")); // AutoCAD
																					// 2D
																					// 도면파일

			String lifecycle = StringUtil.checkNull((String) hash.get("lifecycle")); // 라이프
																						// 사이클
			String fid = StringUtil.checkNull((String) hash.get("fid")); // part
																			// folder
																			// oid
			String location = StringUtil.checkNull((String) hash.get("location")); // part
																					// folder
																					// location
			String epmfid = StringUtil.checkNull((String) hash.get("epmfid")); // EPMDocument
																				// folder
																				// oid
			String epmLocation = StringUtil.checkNull((String) hash.get("epmLocation")); // EPMDocument
																							// folder
																							// location
			String iterationNote = StringUtil.checkNull((String) hash.get("iterationNote")); // 수정사유

			if (oid.length() > 0) {
				oldPart = (WTPart) rf.getReference(oid).getObject();
				String oldPartName = oldPart.getName();
				// Working Copy
				part = (WTPart) getWorkingCopy(oldPart);
				if (!oldPartName.equals(partName)) {
					// E3PSRENameObject.manager.PartReName(part, oldPart.getNumber(), partName,
					// false);
					// E3PSRENameObject.manager.PartReName(part, oldPart.getNumber(), partName,
					// false);
					partReName(part, partName);
					changeNameCheck = true;
				}
				// part.setDefaultUnit(QuantityUnit.toQuantityUnit(Unit));

				part = (WTPart) PersistenceHelper.manager.modify(part);

				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_MATERIAL, Material, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SPEC, SPEC, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SURFACE, Surface, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SUBCONTRACT, Subcontract, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_UNIT, Unit, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SUPPLIER, Supplier, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_VENDOR, Vendor, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_WEIGHT, Weight, "float");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_COMMENT, Comment, "string");

				CommonContentHelper.service.attach(part, null, loc, deloc);

				/*
				 * CommonContentHelper.service.delete(part); if (deloc != null) { for (int j =
				 * 0; j < deloc.length; j++) { ApplicationData ad = (ApplicationData)
				 * rf.getReference(deloc[j]).getObject();
				 * CommonContentHelper.service.attach(part, ad, false); } } if (loc != null) {
				 * for (int i = 0; i < loc.length; i++) {
				 * CommonContentHelper.service.attach(part, loc[i], "N"); } }
				 */

				// 주도면
				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {
					DrawingHelper.service.updateIBA(epm, part);
				}

				// 관련문서 연결
				QueryResult results = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class,
						false);
				while (results.hasMoreElements()) {
					WTPartDescribeLink link = (WTPartDescribeLink) results.nextElement();
					PersistenceServerHelper.manager.remove(link);
				}

				WTDocument doc = null;
				WTPartDescribeLink dlink = null;
				if (docOids != null) {
					for (int i = 0; i < docOids.length; i++) {
						doc = (WTDocument) rf.getReference(docOids[i]).getObject();
						dlink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
						dlink = (WTPartDescribeLink) PersistenceHelper.manager.save(dlink);
					}
				}

				// 관련도면 연결
				QueryResult epmResults = PersistenceHelper.manager.navigate(part, "describedBy", EPMDescribeLink.class,
						false);
				while (epmResults.hasMoreElements()) {
					EPMDescribeLink link = (EPMDescribeLink) epmResults.nextElement();
					PersistenceServerHelper.manager.remove(link);
				}

				EPMDocument epmSub = null;
				EPMDescribeLink epmLink = null;
				if (epmOids != null) {

					for (int i = 0; i < epmOids.length; i++) {
						epmSub = (EPMDocument) rf.getReference(epmOids[i]).getObject();
						epmLink = EPMDescribeLink.newEPMDescribeLink(part, epmSub);
						epmLink = (EPMDescribeLink) PersistenceHelper.manager.save(epmLink);

						DrawingHelper.service.updateIBA(epmSub, part);
					}
				}

				// 단위 정보 셋팅
				if (Unit != null && Unit.length() > 0) {
					part.setDefaultUnit(QuantityUnit.toQuantityUnit(Unit));
				} else {
					part.setDefaultUnit(QuantityUnit.getQuantityUnitDefault());
				}

				// 체크인
				if (WorkInProgressHelper.isCheckedOut(part)) {
					part = (WTPart) WorkInProgressHelper.service.checkin(part,
							StringUtil.checkNull((String) hash.get("iterationNote")));
				}
				part = (WTPart) PersistenceHelper.manager.refresh(part);

				// 제품분류에 따라 폴더설정
				if (fid.length() > 0) {
					// Folder folder = (Folder)CommonUtil.getObject(fid);
					Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
					part = (WTPart) FolderHelper.service.changeFolder((FolderEntry) part, folder);
				}

				// 자재명
				partMaster = (WTPartMaster) (part.getMaster());
			}

			// 단위변경
			// 2016.01.09 Unit 이 ""일 때 로직 타지 않도록 수정.
			// System.out.println("!!!!!! partMaster.getDefaultUnit().toString() = " +
			// partMaster.getDefaultUnit().toString());
			// System.out.println("!!!!!! Unit = " + Unit);
			// if (Unit!=null && !Unit.equals("") &&
			// !(partMaster.getDefaultUnit().toString()).equals(Unit)) {
			// 엄태식 부장님 설명 듣고 일부러 오류가 발생하도록 재차 수정. 오류 발생 시, ParkCheck = PART 와 SeqCheck =
			// true를 입력 해주면 됨. 관리자 속성 수정 메뉴 사용 (마이그레이션 데이터만 오류가 발생한다 함.)
			if (!(partMaster.getDefaultUnit().toString()).equals(Unit)) {
				partMaster.setDefaultUnit(QuantityUnit.toQuantityUnit(Unit));
				partMaster = (WTPartMaster) PersistenceHelper.manager.modify(partMaster);
			}

			// 품목과 AutoCAD 2D도면 연결
			EPMDocument epmdoc = DrawingHelper.service.getEPMDocument(part);
			if (epmdoc == null && primary.length() > 0) {
				hash.put("epmfid", epmfid);
				EPMDocument epm = DrawingHelper.service.createEPM(hash);
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
				PersistenceServerHelper.manager.insert(link);
			}

			/* IBA Copy */
			EPMBuildRule buildRule = PartSearchHelper.service.getBuildRule(part);
			if (buildRule != null) {
				EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
				// String epmType =
				// epm.getAuthoringApplication().getStringValue();
				String epmType = epm.getAuthoringApplication().getFullDisplay();
				if ("SolidWorks".equals(epmType)) {
					EpmUtilHelper.service.copyIBA(CommonUtil.getOIDString(part));
				}
			}

			/* EPM Document Change Name */
			if (buildRule != null) {
				EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
				if (changeNameCheck) {
					EpmUtilHelper.service.changeEPMName(epm, partName);
				}
			}

			/* BuildRule History */
			if (buildRule != null) {
				EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
				EPMBuildHistory history = EPMBuildHistory.newEPMBuildHistory(epm, oldPart, buildRule.getUniqueID(),
						buildRule.getBuildType());
				PersistenceServerHelper.manager.insert(history);
			}

			rtnVal.put("oid", CommonUtil.getOIDString(part));
		} catch (Exception e) {
			throw new WTException(e);
		}

		return rtnVal;
		// ##end createPart%3E48C16400F2.body
	}

	private Workable getWorkingCopy(Workable _obj) throws Exception {
		/*
		 * try {
		 */
		if (!WorkInProgressHelper.isCheckedOut(_obj)) {

			if (!CheckInOutTaskLogic.isCheckedOut(_obj)) {
				CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_obj,
						CheckInOutTaskLogic.getCheckoutFolder(), "");
			}

			_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
		} else {
			if (!WorkInProgressHelper.isWorkingCopy(_obj))
				_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
		}
		/*
		 * } catch (WorkInProgressException e) { e.printStackTrace(); } catch
		 * (WTException e) { e.printStackTrace(); } catch (WTPropertyVetoException e) {
		 * e.printStackTrace(); }
		 */
		return _obj;
	}

	public int rowNum2 = 0;

	@Override
	public Map<String, Object> getPartTreeAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		String oid = request.getParameter("oid");
		String view = request.getParameter("view");
		String desc = request.getParameter("desc");
		String baseline = request.getParameter("baseline");

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		Baseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (Baseline) rf.getReference(baseline).getObject();
		}
		if (bsobj != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					bsobj.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=",
					part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}

		ArrayList<PartTreeData> treeDataList = new ArrayList<PartTreeData>();

		BomBroker broker = new BomBroker();

		View[] views = ViewHelper.service.getAllViews();

		if (view == null) {
			view = views[0].getName();
		}

		PartTreeData root = broker.getTree(part, !"false".equals(desc), bsobj, ViewHelper.service.getView(view));
		broker.setHtmlForm(root, treeDataList);

		ArrayList<Map<String, Object>> bomList = new ArrayList<Map<String, Object>>();

		Map<String, Object> rootMap = getDhtmlXPartData(root, rowNum2);
		partDhtmlXTreeSetting(root, rootMap, rowNum2);
		bomList.add(rootMap);
		map.put("rows", bomList);
		return map;
	}

	public void partDhtmlXTreeSetting(PartTreeData parent, Map<String, Object> map, int rowNum) throws Exception {

		ArrayList<PartTreeData> childList = parent.children;
		ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (PartTreeData child : childList) {
			rowNum2++;
			Map<String, Object> childMap = getDhtmlXPartData(child, rowNum2);
			dataList.add(childMap);
			partDhtmlXTreeSetting(child, childMap, rowNum2);
		}
		if (childList.size() > 0) {
			map.put("rows", dataList);
		}
	}

	public Map<String, Object> getDhtmlXPartData(PartTreeData bomPart, int rowNum) throws Exception {

		Map<String, Object> partData = new HashMap<String, Object>();
		ArrayList<Object> data = new ArrayList<Object>();
		Map<String, String> nameData = new HashMap<String, String>();

		PartData pData = new PartData(bomPart.part);

		String oid = CommonUtil.getOIDString(bomPart.part);
		String partIcon = CommonUtil.getIconResource(bomPart.part);

		String partNo = bomPart.number;

		String dwgNo = bomPart.getDwgNo();

		if ((!"ND".equals(dwgNo) || !"AP".equals(dwgNo)) && dwgNo != null && dwgNo.length() > 0) {
			dwgNo = "<a href='#' onclick='javascript:openView(\"" + bomPart.getDwgOid() + "\")'>" + bomPart.getDwgNo()
					+ "</a>";
		}
		int level = bomPart.level;
		String name = bomPart.name;
		String state = bomPart.part.getLifeCycleState().getDisplay(Message.getLocale());
		String version = bomPart.version + "." + bomPart.iteration;
		String infoIcon = "<img src='/Windchill/netmarkets/images/details.gif'  border=0>^javascript:openView(\"" + oid
				+ "\");^_self";
		String specification = pData.getSpecification();
		String unit = pData.unit;
		double quantity = bomPart.quantity;
		String weight = pData.getWeight();
		String ecoNo = bomPart.ecoNo;
		String deptcode = NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_DEPTCODE, bomPart.deptcode);

		nameData.put("image", partIcon);
		nameData.put("value", "<a href='#' onclick='javascript:openView(\"" + oid + "\")'>" + partNo + "</a>");
		//// System.out.println("specification="+specification);
		data.add(nameData);
		data.add(dwgNo);
		data.add(level);
		data.add(name);
		data.add(state);
		data.add(version);
		// data.add(infoIcon);
		data.add(specification);
		data.add(unit);
		data.add(quantity);
		data.add(weight);
		data.add(ecoNo);
		data.add(deptcode);

		// oid
		partData.put("id", rowNum2 + "_" + oid);
		if (bomPart.children.size() > 0) {
			partData.put("open", "1");
		}

		// cell values
		partData.put("data", data);

		return partData;
	}

	@Override
	public QuerySpec getEPMNumber(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, true);

		try {
			String num = number;
			if (num != null && num.trim().length() > 0) {

				String tempNumber = num + "_2D";

				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idx });

				QuerySpecUtils.toLatest(query, idx, EPMDocument.class);

				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				query.appendWhere(
						new SearchCondition(EPMDocument.class, "master>number", SearchCondition.EQUAL, tempNumber),
						new int[] { idx });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return query;
	}

	/**
	 * 자재검색(최종버전)
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("deprecation")
	public WTPart getPart(final String number) throws Exception {

		QuerySpec qs = new QuerySpec(WTPart.class);

		qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[] { 0 });

		QuerySpecUtils.toLatest(qs, 0, WTPart.class);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			Object o = qr.nextElement();
			if (o instanceof WTPart) {
				return (WTPart) o;
			} else {
				Object[] arry = (Object[]) o;
				return (WTPart) arry[0];
			}
		}

		return null;
	}

	@Override
	public String excelBomLoadAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Vector vec = new Vector();
		FileRequest req = new FileRequest(request);

		String excelFile = req.getFileLocation("excelFile");
		String cmd = (String) req.getParameter("cmd");

		if ("excelLoad".equals(cmd)) {
			vec = BomExcelLoaderHelper.service.loadData(excelFile);
		}

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		for (int i = 0; i < vec.size(); i++) {
			String bgcolor = "#ffffff";
			Hashtable hash = (Hashtable) vec.get(i);
			String parentNumber = StringUtil.checkNull((String) hash.get("모부품코드"));
			String childNumber = StringUtil.checkNull((String) hash.get("자부품코드"));
			String unit = StringUtil.checkNull((String) hash.get("단위"));
			String qstr = StringUtil.checkNull((String) hash.get("수량"));
			String location = StringUtil.checkNull((String) hash.get("Location"));
			String oid = StringUtil.checkNull((String) hash.get("oid"));
			boolean rslt = ((boolean) hash.get("rslt"));
			String msg = StringUtil.checkNull((String) hash.get("msg"));

			String logResult = "S".equals(rslt) ? Message.get("등록성공") : Message.get("등록실패");
			if ("F".equals(rslt)) {
				bgcolor = "red";
			} else {
				bgcolor = "black";
			}

			String bom = "";
			if (oid.length() > 0) {
				bom = "<button type='button' class='btnCustom' onclick=javascript:viewBom('" + oid
						+ "')><span></span>BOM</buttom>";
			}

			xmlBuf.append("<row id='" + oid + "'>");
			xmlBuf.append("<cell><![CDATA[" + (i + 1) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + parentNumber + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + childNumber + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + unit + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + qstr + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + location + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + rslt + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + msg + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + bom + "]]></cell>");
			xmlBuf.append("</row>");

		}

		xmlBuf.append("</rows>");

		return xmlBuf.toString();
	}

	/**
	 * ECO 관련 ECN Search
	 * 
	 * @param eco
	 * @return
	 */
	@Override
	public WTPart include_ChangePartList(EChangeOrder eco) {

		try {
			String oid = CommonUtil.getOIDString(eco);
			String eoType = eco.getEoType();
			QueryResult qr = ECOSearchHelper.service.ecoPartLink(eco);
			HashMap<String, String> applyMap = CodeHelper.service.getCodeMap("APPLY");

			Object[] o = (Object[]) qr.nextElement();

			EcoPartLink link = (EcoPartLink) o[0];

			String version = link.getVersion();
			// boolean isBOM = link.isBaseline();
			String isBOMCheck = "";
			// if(isBOM) isBOMCheck ="<b><font color='red'>√</font></b>";
			WTPartMaster master = (WTPartMaster) link.getPart();
			WTPart part = PartHelper.service.getPart(master.getNumber());
			WTPart nextPart = null;
			String nextNumber = "";
			String nextState = "";
			String nextVer = "";
			String nextOid = "";
			// String apply = StringUtil.checkNull(link.getStock()) ;
			// apply = (String)applyMap.get(apply);
			// apply = StringUtil.checkNull(apply);
			// String serial = StringUtil.checkNull(link.getSerialNumber()) ;
			if (link.isRevise()) {
				nextPart = (WTPart) com.e3ps.common.obj.ObjectUtil.getNextVersion(part);
				nextNumber = nextPart.getNumber();
				nextState = nextPart.getLifeCycleState().getDisplay(Message.getLocale());
				nextVer = nextPart.getVersionIdentifier().getValue();
				nextOid = CommonUtil.getOIDString(nextPart);
			}
			String partoid = part.getPersistInfo().getObjectIdentifier().toString();
			boolean isSelect = partoid.equals(oid);
			String imgUrl = DrawingHelper.service.getThumbnailSmallTag(DrawingHelper.service.getEPMDocument(part));
			String icon = CommonUtil.getObjectIconImageTag(part);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return null;
	}

	@Override
	public List<Map<String, Object>> partChange(String partOid) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		if ((partOid.split(":").length) > 0)
			part = (WTPart) rf.getReference(partOid).getObject();
		else
			return list;
		View[] views = ViewHelper.service.getAllViews();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		PartTreeData root = broker.getTree(part, !"false".equals(null), null,
				ViewHelper.service.getView(views[0].getName()));
		broker.setHtmlForm(root, result);

		String[] lineStack = new String[50];

		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			PartTreeData data = (PartTreeData) result.get(i);

			String checked = "";
			String disabled = "";

			if (!data.isChange()) {
				disabled = "disabled";
			} else {
				checked = "checked";
			}

			map.put("disabled", disabled);
			map.put("checked", checked);

			String img = "";
			for (int j = 1; j < data.level; j++) {
				String line = lineStack[j];
				if (line == null) {
					line = "empty";
				}
				img += "<img src='/Windchill/jsp/part/images/tree/" + line + ".gif'></img>";
			}
			map.put("img", img);

			String img2 = "";
			if (data.level > 0) {
				if ("join".equals(data.lineImg)) {
					lineStack[data.level] = "line";
				} else
					lineStack[data.level] = "empty";
				img2 += "<img src='/Windchill/jsp/part/images/tree/" + data.lineImg + ".gif' border=0></img>";
			}
			map.put("img2", img2);

			String icon = CommonUtil.getObjectIconImageTag(data.part);
			map.put("icon", icon);

			map.put("partOid", data.part.getPersistInfo().getObjectIdentifier().toString());
			map.put("number", data.number);
			map.put("name", data.name);
			map.put("level", data.version);

			PartData partData = new PartData(data.part);
//		 	map.put("partName1", partData.getPartName(1));
//		 	map.put("partName2", partData.getPartName(2));
//		 	map.put("partName3", partData.getPartName(3));
//		 	map.put("partName4", partData.getPartName(4));

			list.add(map);
		}
		return list;
	}

	@Override
	public ResultData changeNumber(HttpServletRequest req) {
		ResultData result = new ResultData();
		Transaction trx = new Transaction();

		try {
			trx.start();

			String[] checkOid = req.getParameterValues("checkOid");
			String[] productchk = req.getParameterValues("partType");
			if (checkOid == null) {
				result.setResult(false);
				return result;
			}

			int chkCount = 0;

			for (int i = 0; i < checkOid.length; i++) {
				String oid = checkOid[i];

				// 부품 변경
				WTPart oldpart = (WTPart) CommonUtil.getObject(oid);
				String number = oldpart.getNumber();
				WTPart part = (WTPart) getWorkingCopy(oldpart);
				String partNumber = "";
				if (productchk != null && StringUtil.checkString(productchk[chkCount])) {

					if (productchk[chkCount].equals(number)) {
						String partType1 = StringUtil.checkNull(req.getParameter("partType1_" + number));
						String partType2 = StringUtil.checkNull(req.getParameter("partType2_" + number));
						String partType3 = StringUtil.checkNull(req.getParameter("partType3_" + number));

						partNumber = partType1 + partType2 + partType3;
						String seq = StringUtil.checkNull(req.getParameter("seq_" + number));
						if (seq.length() == 0) {
							seq = SequenceDao.manager.getSeqNo(partNumber, "000", "WTPartMaster", "WTPartNumber");
						} else if (seq.length() == 1) {
							seq = "00" + seq;
						} else if (seq.length() == 2) {
							seq = "0" + seq;
						}

						String etc = StringUtil.checkNull(req.getParameter("etc_" + number));
						if (etc.length() == 0) {
							etc = "00";
						} else if (etc.length() == 1) {
							etc = "0" + etc;
						}

						partNumber += seq + etc;
						if (chkCount < (productchk.length - 1))
							chkCount++;
					} else {
						partNumber = "TEMP";
						String seq = SequenceDao.manager.getSeqNo(partNumber, "000000", "WTPartMaster", "WTPartNumber");
						partNumber = partNumber + seq;
					}
				} else {
					partNumber = "TEMP";
					String seq = SequenceDao.manager.getSeqNo(partNumber, "000000", "WTPartMaster", "WTPartNumber");
					partNumber = partNumber + seq;
				}

				String partName1 = StringUtil.checkNull(req.getParameter("partName1_" + number));
				String partName2 = StringUtil.checkNull(req.getParameter("partName2_" + number));
				String partName3 = StringUtil.checkNull(req.getParameter("partName3_" + number));
				String partName4 = StringUtil.checkNull(req.getParameter("partName4_" + number));

				String[] partNames = new String[] { partName1, partName2, partName3 }; // req.getParameterValues("partName_"
																						// + number);
				String partName = "";
				if (partNames != null) {
					for (int j = 0; j < partNames.length; j++) {
						if (StringUtil.checkString(partNames[j])) {
							if (j != 0 && partName.length() != 0) {
								partName += "_";
							}
							partName += partNames[j];
						}
					}
				}

				// System.out.println("[" + i + "] 번째 PartNumber >>>> " + partNumber);
				// System.out.println("[" + i + "] 번째 PartName >>>> " + partName);

				E3PSRENameObject.manager.PartReName(part, partNumber, partName, false);
				// partReName(part, partName);
				if (WorkInProgressHelper.isCheckedOut(part)) {
					part = (WTPart) WorkInProgressHelper.service.checkin(part, "PDM에서 채번을 하였습니다.");
				}

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("partName1", partName1);
				map.put("partName2", partName2);
				map.put("partName3", partName3);
				map.put("partName4", partName4);

				CommonHelper.service.changeIBAValues(part, map);

				// 부품 진채번시 도면 번호 및 명 변경
				EPMDocument oldEpm = DrawingHelper.service.getEPMDocument(part);
				String dsgn = part.getCreatorFullName();
				String ver = part.getVersionIdentifier().getValue();
				String des = part.getName();

				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DSGN, dsgn, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_REV, ver, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, des, "string");

				if (oldEpm != null) {
					// 3D 변경
					EPMDocument epm = (EPMDocument) getWorkingCopy(oldEpm);
					String extention = EpmUtil.getExtension(epm.getCADName());
					extention = extention.toUpperCase();
					String epmNumber = partNumber + "." + extention;
					E3PSRENameObject.manager.EPMReName(epm, epmNumber, des, partName, false);
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DSGN, dsgn, "string");
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_REV, ver, "string");
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, des, "string");

					if (WorkInProgressHelper.isCheckedOut(epm)) {
						epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm, "PDM에서 채번을 하였습니다.");
					}
					// 2D 변경
					Vector<EPMReferenceLink> vec = EpmSearchHelper.service
							.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());

					for (EPMReferenceLink link : vec) {

						EPMDocument oldEpm2d = link.getReferencedBy();
						String docType = oldEpm2d.getDocType().toString();
						if (docType.equals("CADDRAWING") || docType.equals("")) {
							EPMDocument epm2D = (EPMDocument) getWorkingCopy(oldEpm2d);
							extention = EpmUtil.getExtension(epm2D.getCADName());
							extention = extention.toUpperCase();
							epmNumber = partNumber + "." + extention;
							E3PSRENameObject.manager.EPMReName(epm2D, epmNumber, des, partName, false);
							if (WorkInProgressHelper.isCheckedOut(epm2D)) {
								epm2D = (EPMDocument) WorkInProgressHelper.service.checkin(epm2D, "PDM에서 채번을 하였습니다.");
							}
							EpmPublishUtil.publish(epm2D);
						}
					}
				}
			}
			trx.commit();
			trx = null;
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> partExpandAction(String partOid, String moduleType, String desc) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(partOid).getObject();

		View[] views = ViewHelper.service.getAllViews();

		String view = views[0].getName();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		PartTreeData root = broker.getTree(part, !"false".equals(desc), null, ViewHelper.service.getView(view));
		broker.setHtmlForm(root, result);

		String[] lineStack = new String[50];
		for (int i = 0; i < result.size(); i++) {
			PartTreeData data = (PartTreeData) result.get(i);
			boolean isSelect = true;

			if ("ECO".equals(moduleType) || "EO".equals(moduleType)) {
				isSelect = PartSearchHelper.service.isSelectEO(data.part, moduleType);
			}
			if (part.getNumber().equals(data.part.getNumber())) {
				isSelect = false;
			}

			//// System.out.println("data.part =" + data.part.getNumber() + ":"+isSelect);
			String icon = CommonUtil.getObjectIconImageTag(data.part);

			String proudctOid = "";
			String plocation = "";

			String line = "";
			for (int j = 1; j < data.level; j++) {

				String empty = lineStack[j];
				if (empty == null) {
					empty = "empty";
				}
				line += "<img src='/Windchill/jsp/part/images/tree/" + empty + ".gif'></img>";
			}

			String lineImg = "";
			if (data.level > 0) {
				if ("join".equals(data.lineImg)) {
					lineStack[data.level] = "line";
				} else
					lineStack[data.level] = "empty";

				lineImg += "<img src='/Windchill/jsp/part/images/tree/" + data.lineImg + ".gif' border=0></img>";
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isSelect", isSelect);
			map.put("line", line);
			map.put("lineImg", lineImg);
			map.put("icon", icon);
			map.put("partOid", data.part.getPersistInfo().getObjectIdentifier().toString());
			map.put("partNumber", data.number);
			map.put("level", data.level);
			map.put("partName", data.name);
			map.put("partState", data.part.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("partVersion", data.version);
			map.put("partIteration", part.getIterationIdentifier().getSeries().getValue());
			map.put("partView", data.part.getViewName());
			map.put("partUnit", data.unit);
			map.put("partQuantity", data.quantity);

			list.add(map);
		}
		return list;
	}

	@Override
	public Map<String, Object> selectEOPartAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");
		Hashtable<String, String> hash = null;

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {

			QuerySpec query = ECOSearchHelper.service.getEOPartList(request);
			// System.out.println(query);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			WTPart part = (WTPart) o[0];
			PartData data = new PartData(part);

			xmlBuf.append("<row id='" + data.oid + "'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.icon + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
			xmlBuf.append(
					"<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.createDate.substring(0, 10) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.modifyDate.substring(0, 10) + "]]></cell>");
			String bom = "<button type='button' class='btnCustom' onclick=javascript:viewBom('" + data.oid
					+ "')><span></span>BOM</buttom>";
			xmlBuf.append("<cell><![CDATA[" + bom + "]]></cell>");
			xmlBuf.append("</row>");

		}
		xmlBuf.append("</rows>");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;
	}

	@Override
	public Map<String, Object> getBaseLineCompare(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String oid = request.getParameter("oid");
		String oid2 = request.getParameter("oid2");
		String baseline = request.getParameter("baseline");
		String baseline2 = request.getParameter("baseline2");
		String initialize = request.getParameter("initialize");
		Map<String, Object> map = new HashMap<String, Object>();

		if ("true".equals(initialize)) {
			ArrayList<Map<String, Object>> bomList = new ArrayList<Map<String, Object>>();
			map.put("rows", bomList);
			return map;
		}

		ReferenceFactory rf = new ReferenceFactory();

		ManagedBaseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (ManagedBaseline) rf.getReference(baseline).getObject();
		}

		ManagedBaseline bsobj2 = null;
		if (baseline2 != null && baseline2.length() > 0) {
			bsobj2 = (ManagedBaseline) rf.getReference(baseline2).getObject();
		}

		WTPart part = getBaselinePart(oid, bsobj);
		WTPart part2 = getBaselinePart(oid2, bsobj2);

		BomBroker broker = new BomBroker();

		PartTreeData root = broker.getTree(part, true, bsobj);
		PartTreeData root2 = broker.getTree(part2, true, bsobj2);

		//// System.out.println("getBaselineinfo root =" +root+","+ part +"," + bsobj);
		//// System.out.println("getBaselineinforoot2 ="+root2+"," + part2 +"," +
		//// bsobj2);

		ArrayList<PartTreeData[]> result = new ArrayList<PartTreeData[]>();
		broker.compareBom(root, root2, result);

		ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < result.size(); i++) {
			PartTreeData[] o = (PartTreeData[]) result.get(i);
			PartTreeData data = o[0];
			PartTreeData data2 = o[1];

			String bgcolor = "black"; // white

			if (data == null) {
				bgcolor = "red";// "#D3D3D3"; //삭제

			} else {
				if (data2 == null) {
					bgcolor = "blue";// "#8FBC8F"; //green
				} else {
					if (!data.compare(data2)) {
						bgcolor = "#FFD700"; // gold
					}
				}
			}

			Map<String, Object> baseLineData = new HashMap<String, Object>();

			baseLineData = getDhtmlXbaselineData(data, data2, i, bgcolor);

			Map<String, Object> userData = new HashMap<String, Object>();
			// userData.put("bgcolor", bgcolor);
			baseLineData.put("userdata", userData);

			rows.add(baseLineData);
		}

		map.put("rows", rows);

		return map;
	}

	public Map<String, Object> getDhtmlXbaselineData(PartTreeData bomPart, PartTreeData bomPart2, int idx,
			String bgcolor) throws Exception {

		Map<String, Object> partData = new HashMap<String, Object>();
		ArrayList<Object> data = new ArrayList<Object>();

		String partIcon = "";
		String partNo = "";
		String partName = "";
		String version = "";
		String unit = "";
		String quantity = "";
		// PJT EDIT 20161129
		String level = "";
		// String itemSeq = "";
		// String baseQuantity = "";

		String[] lineStack = new String[50];
		String img = "";
		if (bomPart != null) {

			for (int j = 1; j < bomPart.level; j++) {

				String line = lineStack[j];
				if (line == null) {
					line = "empty";
				}
				img += "<img src='/Windchill/jsp/part/images/tree/" + line + ".gif'></img>";
			}
			if (bomPart.level > 0) {
				if ("join".equals(bomPart.lineImg)) {
					lineStack[bomPart.level] = "line";
				} else
					lineStack[bomPart.level] = "empty";
				img += "<img src='/Windchill/jsp/part/images/tree/" + bomPart.lineImg + ".gif' border=0></img>";
			}

			partIcon = CommonUtil.getObjectIconImageTag(bomPart.part);
			partNo = "<font color='" + bgcolor + "'>" + bomPart.number + "</font>";
			partName = bomPart.name;
			version = bomPart.version;
			unit = bomPart.unit;
			quantity = String.valueOf(bomPart.quantity);
			level = String.valueOf(bomPart.level);
			// itemSeq = bomPart.itemSeq;
			// baseQuantity = String.valueOf(bomPart.baseQuantity);
		} else {
		}
		//

		data.add(level);
		if (partIcon.length() > 0) {
			data.add(img + partIcon + partNo);
		} else {
			data.add(partNo);
		}

		data.add(partName);
		data.add(version);
		data.add(unit);
		data.add(quantity);

		// data.add(itemSeq);
		// data.add(baseQuantity);

		partIcon = "";
		partNo = "";
		partName = "";
		version = "";
		unit = "";
		quantity = "";
		level = "";
		lineStack = new String[50];
		img = "";
		if (bomPart2 != null) {

			for (int j = 1; j < bomPart2.level; j++) {

				String line = lineStack[j];
				if (line == null) {
					line = "empty";
				}
				img += "<img src='/Windchill/jsp/part/images/tree/" + line + ".gif'></img>";
			}
			if (bomPart2.level > 0) {
				if ("join".equals(bomPart2.lineImg)) {
					lineStack[bomPart2.level] = "line";
				} else
					lineStack[bomPart2.level] = "empty";
				img += "<img src='/Windchill/jsp/part/images/tree/" + bomPart2.lineImg + ".gif' border=0></img>";
			}

			partIcon = CommonUtil.getObjectIconImageTag(bomPart2.part);
			// partNo = bomPart2.number;
			partNo = "<font color='" + bgcolor + "'>" + bomPart2.number + "</font>";
			partName = bomPart2.name;
			version = bomPart2.version;
			unit = bomPart2.unit;
			quantity = String.valueOf(bomPart2.quantity);
			level = String.valueOf(bomPart2.level);
		}

		data.add("&nbsp;");
		data.add(level);
		data.add(img + partIcon + partNo);
		data.add(partName);
		data.add(version);
		// data.add(unit);
		data.add(quantity);

		// oid
		partData.put("id", "tree" + idx);
		// partData.put("bgColor", bgcolor);

		//// System.out.println(partName +";" + bgcolor);
		// cell values
		partData.put("data", data);

		return partData;
	}

	/**
	 * Bom 비고 엑셀 출력
	 * 
	 * @param bomPart
	 * @param bomPart2
	 * @param idx
	 * @param bgcolor
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Object> getExcelbaselineData(PartTreeData bomPart, PartTreeData bomPart2, int idx, String bgcolor)
			throws Exception {

		ArrayList<Object> data = new ArrayList<Object>();

		String partIcon = "";
		String partNo = "";
		String partName = "";
		String version = "";
		String unit = "";
		String quantity = "";

		String level = "";

		String[] lineStack = new String[50];
		String img = "";
		if (bomPart != null) {

			partIcon = CommonUtil.getObjectIconImageTag(bomPart.part);
			partNo = bomPart.number;
			partName = bomPart.name;
			version = bomPart.version;
			unit = bomPart.unit;
			quantity = String.valueOf(bomPart.quantity);
			level = String.valueOf(bomPart.level);

		}

		data.add(level);
		data.add(partNo);
		data.add(partName);
		data.add(version);
		data.add(unit);
		data.add(quantity);

		partIcon = "";
		partNo = "";
		partName = "";
		version = "";
		unit = "";
		quantity = "";
		level = "";
		lineStack = new String[50];
		img = "";
		if (bomPart2 != null) {
			partNo = bomPart2.number;
			partName = bomPart2.name;
			version = bomPart2.version;
			unit = bomPart2.unit;
			quantity = String.valueOf(bomPart2.quantity);
			level = String.valueOf(bomPart2.level);
		}

		data.add(level);
		data.add(partNo);
		data.add(partName);
		data.add(version);
		data.add(unit);
		data.add(quantity);

		return data;
	}

	@Override
	public ResultData getBaseLineCompareExcelDown(HttpServletRequest request, HttpServletResponse response) {

		ResultData returnData = new ResultData();
		try {

			// 데이터 검색
			String oid = request.getParameter("oid");
			String oid2 = request.getParameter("oid2");
			String baseline = request.getParameter("baseline");
			String baseline2 = request.getParameter("baseline2");
			String initialize = request.getParameter("initialize");

			ReferenceFactory rf = new ReferenceFactory();

			ManagedBaseline bsobj = null;
			String baselineName = "";

			if (baseline != null && baseline.length() > 0) {
				bsobj = (ManagedBaseline) rf.getReference(baseline).getObject();
				baselineName = bsobj.getName();

			}

			ManagedBaseline bsobj2 = null;
			String baselineName2 = "";
			if (baseline2 != null && baseline2.length() > 0) {
				bsobj2 = (ManagedBaseline) rf.getReference(baseline2).getObject();
				baselineName2 = bsobj2.getName();
			}
			String fileName = baselineName + "_" + baselineName2;
			fileName = fileName.replace(":", "_");
			// 엑셀 파일 만들기
			String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
			String path = WTProperties.getServerProperties().getProperty("wt.temp");

			File orgFile = new File(wtHome + "/codebase/com/e3ps/part/service/compareBom.xlsx");

			File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + fileName + ".xlsx"));

			FileInputStream file = new FileInputStream(newFile);

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			// XSSFSheet sheet = workbook.getSheetAt(0);
			// XSSFSheet sheetStyle = workbook.getSheetAt(1);
			// workbook.setSheetName(0, "compare");
			XSSFSheet sheetStyle = workbook.getSheetAt(1);

			XSSFRow copyRow = sheetStyle.getRow(0);

			XSSFCell redCell = copyRow.getCell(12);
			XSSFCell greenCell = copyRow.getCell(13);
			XSSFCell goldCell = copyRow.getCell(13);
			XSSFCell defaultCell = copyRow.getCell(0);
			XSSFCellStyle defaultCellStyle = workbook.createCellStyle();
			defaultCellStyle.cloneStyleFrom(defaultCell.getCellStyle());

			Map<String, Object> map = new HashMap<String, Object>();

			if ("true".equals(initialize)) {
				ArrayList<Map<String, Object>> bomList = new ArrayList<Map<String, Object>>();
				map.put("rows", bomList);

			}

			WTPart part = getBaselinePart(oid, bsobj);
			WTPart part2 = getBaselinePart(oid2, bsobj2);

			BomBroker broker = new BomBroker();

			PartTreeData root = broker.getTree(part, true, bsobj);
			PartTreeData root2 = broker.getTree(part2, true, bsobj2);

			//// System.out.println("getBaselineinfo root =" +root+","+ part +"," + bsobj);
			//// System.out.println("getBaselineinforoot2 ="+root2+"," + part2 +"," +
			//// bsobj2);

			ArrayList<PartTreeData[]> result = new ArrayList<PartTreeData[]>();
			broker.compareBom(root, root2, result);

			XSSFCell baselineCell = sheet.getRow(0).getCell(0);
			baselineCell.setCellValue(baselineName);

			XSSFCell baselineCell2 = sheet.getRow(0).getCell(7);
			baselineCell2.setCellValue(baselineName2);

			int rowCount = 2;
			for (int i = 0; i < result.size(); i++) {
				PartTreeData[] o = (PartTreeData[]) result.get(i);
				PartTreeData data = o[0];
				PartTreeData data2 = o[1];

				String bgcolor = "black"; // white

				XSSFCellStyle numberCellStyle = workbook.createCellStyle();
				numberCellStyle.cloneStyleFrom(defaultCell.getCellStyle());
				if (data == null) {
					numberCellStyle.cloneStyleFrom(redCell.getCellStyle());// red
					//// System.out.println(data2.number+ " ,redCell =" + redCell);
				} else {
					if (data2 == null) {
						numberCellStyle.cloneStyleFrom(greenCell.getCellStyle());// green
						//// System.out.println(data.number+ " ,greenCell =" + greenCell);
					} else {
						if (!data.compare(data2)) {
							numberCellStyle.cloneStyleFrom(goldCell.getCellStyle());// gold
						}
					}
				}
				// //System.out.println("number color= "+numberCellStyle.getFont().getColor());
				ArrayList<Object> rowData = getExcelbaselineData(data, data2, i, bgcolor);

				createExcelBaseline(rowData, rowCount, workbook, numberCellStyle, defaultCellStyle);
				rowCount++;

			}

			workbook.removeSheetAt(1);

			file.close();
			FileOutputStream outFile = new FileOutputStream(newFile);
			workbook.write(outFile);
			outFile.close();

			workbook.close();
			returnData.setResult(true);
			returnData.setMessage(newFile.getName());

		} catch (Exception e) {
			e.printStackTrace();
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
		}

		return returnData;
	}

	private void createExcelBaseline(ArrayList<Object> rowData, int rowCount, XSSFWorkbook workbook,
			XSSFCellStyle numberCellStyle, XSSFCellStyle defaultCellStyle) {
		//// System.out.println("rowData =" + rowData);
		int cellCount = 0;
		XSSFSheet sheet = workbook.getSheetAt(0);

		XSSFRow newRow = sheet.createRow(rowCount);
		// level1
		XSSFCell level1 = newRow.createCell(0);
		level1.setCellValue((String) rowData.get(0));
		level1.setCellStyle(defaultCellStyle);

		// number1
		XSSFCell number1 = newRow.createCell(1);
		number1.setCellValue((String) rowData.get(1));
		number1.setCellStyle(numberCellStyle);

		// name1
		XSSFCell name1 = newRow.createCell(2);
		name1.setCellValue((String) rowData.get(2));
		name1.setCellStyle(defaultCellStyle);

		// rev1
		XSSFCell rev1 = newRow.createCell(3);
		rev1.setCellValue((String) rowData.get(3));
		rev1.setCellStyle(defaultCellStyle);

		// 단위1
		XSSFCell unit1 = newRow.createCell(4);
		unit1.setCellValue((String) rowData.get(4));
		unit1.setCellStyle(defaultCellStyle);

		// 수량
		XSSFCell quantity1 = newRow.createCell(5);
		quantity1.setCellValue((String) rowData.get(5));
		quantity1.setCellStyle(defaultCellStyle);
		// blank
		XSSFCell blank = newRow.createCell(6);
		blank.setCellValue("");
		blank.setCellStyle(defaultCellStyle);

		// level1
		XSSFCell level2 = newRow.createCell(7);
		level2.setCellValue((String) rowData.get(6));
		level2.setCellStyle(defaultCellStyle);

		// number1
		XSSFCell number2 = newRow.createCell(8);
		number2.setCellValue((String) rowData.get(7));
		number2.setCellStyle(numberCellStyle);

		// name1
		XSSFCell name2 = newRow.createCell(9);
		name2.setCellValue((String) rowData.get(8));
		name2.setCellStyle(defaultCellStyle);

		// rev1
		XSSFCell rev2 = newRow.createCell(10);
		rev2.setCellValue((String) rowData.get(9));
		rev2.setCellStyle(defaultCellStyle);

		// 수량
		XSSFCell quantity2 = newRow.createCell(11);
		quantity2.setCellValue((String) rowData.get(11));
		quantity2.setCellStyle(defaultCellStyle);
	}

	/*
	 * // @Override // public Map<String,Object> getBaselineinfo(String oid, String
	 * oid2, String baseline, String baseline2) throws Exception { // //
	 * Map<String,Object> map = new HashMap<String,Object>(); // // ReferenceFactory
	 * rf = new ReferenceFactory(); // // ManagedBaseline bsobj = null; //
	 * if(baseline!=null && baseline.length()>0){ // bsobj =
	 * (ManagedBaseline)rf.getReference(baseline).getObject(); // } // //
	 * ManagedBaseline bsobj2 = null; // if(baseline2!=null &&
	 * baseline2.length()>0){ // bsobj2 =
	 * (ManagedBaseline)rf.getReference(baseline2).getObject(); // } // // WTPart
	 * part = getBaselinePart(oid, bsobj); // WTPart part2 = getBaselinePart(oid2,
	 * bsobj2); // // BomBroker broker = new BomBroker(); // // // PartTreeData root
	 * = broker.getTree(part , true , bsobj); // PartTreeData root2 =
	 * broker.getTree(part2 , true , bsobj2); // //
	 * ////System.out.println("getBaselineinfo root =" +root+","+ part +"," +
	 * bsobj); // ////System.out.println("getBaselineinforoot2 ="+root2+"," + part2
	 * +"," + bsobj2); // // ArrayList result = new ArrayList(); //
	 * broker.compareBom(root,root2,result); // // String title1 = ""; // String
	 * title2 = ""; // // if(bsobj == null) { // title1 = "최신BOM 전개"; // }else { //
	 * title1 = "Baseline전개  - " + bsobj.getName(); // } // // if(bsobj2 == null) {
	 * // title2 = Message.get("최신BOM 전개"); // }else { // title2 =
	 * Message.get("Baseline전개")+"-" + bsobj2.getName(); // } // map.put("title1",
	 * title1); // map.put("title2", title2); // // List<Map<String,Object>> list =
	 * new ArrayList<Map<String,Object>>(); // boolean isData = false; // boolean
	 * isData2 = false; // for(int i=0; i< result.size(); i++){ //
	 * Map<String,Object> map2 = new HashMap<String,Object>(); // PartTreeData[] o =
	 * (PartTreeData[])result.get(i); // PartTreeData data = o[0]; // PartTreeData
	 * data2 = o[1]; // // String bgcolor = "white"; // // if(data==null){ //
	 * map2.put("isData", isData); // bgcolor ="gray"; // }else { //
	 * if(data2==null){ // map2.put("isData2", isData2); // bgcolor ="green"; //
	 * }else{ // if(!data.compare(data2)){ // bgcolor = "gold"; // } // } // } // //
	 * map2.put("bgcolor", bgcolor); // // if(data != null){ // String icon =
	 * CommonUtil.getObjectIconImageTag(data.part); // Map<String,String> data1Map =
	 * getBomList(data); // map2.put("icon", icon); // map2.put("data1Map",
	 * data1Map); // } // // if(data2 != null){ // String icon2 =
	 * CommonUtil.getObjectIconImageTag(data2.part); // Map<String,String> data2Map
	 * = getBomList(data2); // map2.put("icon2", icon2); // map2.put("data2Map",
	 * data2Map); // } // // // // // // // // // // list.add(map2); // } //
	 * map.put("list", list); // // return map; // } // // public Map<String,String>
	 * getBomList(PartTreeData data) { // String[] lineStack = new String[50]; //
	 * String img = ""; // for(int j=1; j< data.level; j++){ // // String line =
	 * lineStack[j]; // if(line==null){ // line="empty"; // } // img +=
	 * "<img src='/Windchill/jsp/part/images/tree/" + line +".gif'></img>"; // } //
	 * if(data.level>0){ //
	 * if("join".equals(data.lineImg)){lineStack[data.level]="line";} // else
	 * lineStack[data.level] = "empty"; // img +=
	 * "<img src='/Windchill/jsp/part/images/tree/" + data.lineImg +
	 * ".gif' border=0></img>"; // } // // Map<String,String> map = new
	 * HashMap<String,String>(); // map.put("img", img); // map.put("number",
	 * data.number); // map.put("name", data.name); // map.put("version",
	 * data.version); // map.put("unit", data.unit); // map.put("quantity",
	 * String.valueOf(data.quantity)); // map.put("itemSeq", data.itemSeq); //
	 * map.put("baseQuantity", String.valueOf(data.baseQuantity)); // return map; //
	 * // }
	 */
	public WTPart getBaselinePart(String oid, ManagedBaseline bsobj) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();

		WTPart part = (WTPart) rf.getReference(oid).getObject();

		if (bsobj != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					bsobj.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=",
					part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}

		return part;
	}

	@Override
	public List<Map<String, Object>> viewPartBomAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		boolean boolDesc = Boolean.valueOf(desc);
		String baseline = StringUtil.checkNull(request.getParameter("baseline"));
		String isTopAssy = StringUtil.checkNull(request.getParameter("isTopAssy"));
		String parentId = StringUtil.checkNull(request.getParameter("parentId"));
		boolean isTop = isTopAssy.equals("true");

		WTPart part = (WTPart) CommonUtil.getObject(oid);

		Baseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (Baseline) CommonUtil.getObject(baseline);
		}
		if (bsobj != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					bsobj.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=",
					part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}
		List<PartTreeData> partTrees = new ArrayList<PartTreeData>();
		/*
		 * if(isTop){ partTrees = BomSearchHelper.service.getNextBOM(part, boolDesc,
		 * bsobj, false, partTrees); }else{
		 * 
		 * }
		 */

		partTrees = BomSearchHelper.service.getBOM(part, boolDesc, bsobj, isTop, parentId);// (part, boolDesc,
																							// bsobj,parentID);

		for (PartTreeData data : partTrees) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("oid", CommonUtil.getOIDString(data.part));
			map.put("partIcon", CommonUtil.getIconResource(data.part));
			map.put("name", data.name);
			map.put("number", data.number);

			String dwgNo = data.getDwgNo();
			PartData pData = new PartData(data.part);

			if ((!"ND".equals(dwgNo) || !"AP".equals(dwgNo)) && dwgNo != null && dwgNo.length() > 0) {
				dwgNo = "<a href='#' onclick='javascript:openView(\"" + data.getDwgOid() + "\")'>" + data.getDwgNo()
						+ "</a>";
			}

			map.put("dwgNo", dwgNo);

			map.put("state", data.part.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("version", data.version + "." + data.iteration);
			map.put("infoIcon", "");
			map.put("specification", data.getSpecification());
			map.put("unit", data.unit);
			map.put("quantity", String.valueOf(data.quantity));
			map.put("weight", data.getWeight());
			map.put("ecoNo", data.ecoNo);
			map.put("deptcode",
					NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_DEPTCODE, data.getDeptcode()));

			boolean isChildren = BomSearchHelper.service.isChildren(data.part, boolDesc, bsobj);// isChildren(data.part,
																								// desc, baseline);
			data.setChildren(isChildren);
			map.put("parentId", data.parentId);
			map.put("isChildren", data.isChildren());

			list.add(map);
		}

		return list;
	}

	private void updateQuantityUnit(RevisionControlled rev, String unit) throws Exception {
		unit = StringUtil.checkNull(unit);

		if (rev instanceof WTPart) {
			WTPart part = (WTPart) rev;
			if (!part.getDefaultUnit().toString().equals(unit) && unit.length() > 0) {

				WTPartMaster master = (WTPartMaster) part.getMaster();
				master.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
				PersistenceHelper.manager.modify(master);
			}
		} else if (rev instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) rev;
			if (!epm.getDefaultUnit().toString().equals(unit) && unit.length() > 0) {

				EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
				master.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));
				PersistenceHelper.manager.modify(master);
			}
		}

	}

	@Override
	public Map<String, Object> searchSeqAction(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PartColumn> list = new ArrayList<>();

		String sessionId = (String) params.get("sessionId");

		QuerySpec query = PartQueryHelper.service.searchSeqAction(params);
		int idx = query.addClassList(WTPart.class, true);

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartColumn data = new PartColumn(part);
			list.add(data);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());

		return map;
	}

	@Override
	public List<PartData> include_partLink(String module, String oid) {
		List<PartData> list = new ArrayList<PartData>();

		try {
			if (StringUtil.checkString(oid)) {
				if ("active".equals(module)) {
					devActive m = (devActive) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class);

					while (qr.hasMoreElements()) {
						Object p = (Object) qr.nextElement();
						if (p instanceof WTPart) {
							PartData data = new PartData((WTPart) p);
							list.add(data);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public ResultData linkPartAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();

		String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
		String partOid = StringUtil.checkNull(request.getParameter("partOid"));
		String type = StringUtil.checkNull(request.getParameter("type"));

		try {
			trx.start();
			if ("active".equals(type)) {
				devActive active = (devActive) CommonUtil.getObject(parentOid);
				WTPart part = (WTPart) CommonUtil.getObject(partOid);

				devOutPutLink link = devOutPutLink.newdevOutPutLink(active, part);
				PersistenceServerHelper.manager.insert(link);
			}
			trx.commit();
			trx = null;
			data.setResult(true);
		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return data;
	}

	@Override
	public ResultData deletePartLinkAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();

		String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
		String partOid = StringUtil.checkNull(request.getParameter("partOid"));
		String type = StringUtil.checkNull(request.getParameter("type"));

		try {
			trx.start();

			if ("active".equals(type)) {
				QuerySpec spec = DocumentQueryHelper.service.devActiveLinkDocument(parentOid, partOid);
				QueryResult result = PersistenceHelper.manager.find(spec);
				if (result.hasMoreElements()) {
					Object[] o = (Object[]) result.nextElement();
					devOutPutLink link = (devOutPutLink) o[0];
					PersistenceHelper.manager.delete(link);
				}
			}

			trx.commit();
			trx = null;
			data.setResult(true);

		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}

		return data;
	}

	@Override
	public List<Map<String, Object>> partInstanceGrid(String oid) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<WTPart> partList = new ArrayList<WTPart>();
		partList = PartSearchHelper.service.getPartInstance(part, partList);

		for (WTPart subPart : partList) {
			Map<String, Object> map = new HashMap<String, Object>();
			PartData data = new PartData(subPart);

			map.put("img2", "");
			String icon = data.getIcon();
			map.put("icon", icon);

			map.put("partOid", data.oid);
			map.put("number", data.number);
			map.put("name", data.name);
			map.put("rev", data.version);

			map.put("productmethod",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_PRODUCTMETHOD)));
			map.put("model", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MODEL)));
			map.put("deptcode",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_DEPTCODE)));
			map.put("unit", data.part.getDefaultUnit().toString());
			map.put("weight",
					StringUtil.checkNull(IBAUtil.getAttrfloatValue(data.part, AttributeKey.IBAKey.IBA_WEIGHT)));
			map.put("manufacture",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MANUFACTURE)));
			map.put("mat", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_MAT)));
			map.put("finish", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_FINISH)));
			map.put("remark", StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_REMARKS)));
			map.put("specification",
					StringUtil.checkNull(IBAUtil.getAttrValue(data.part, AttributeKey.IBAKey.IBA_SPECIFICATION)));

			list.add(map);
		}

		return list;

	}

	public void copyInstanceAttribute(WTPart part, Map<String, Object> map) throws Exception {
		List<WTPart> partList = new ArrayList<WTPart>();
		partList = PartSearchHelper.service.getPartInstance(part, partList);

		for (WTPart subPart : partList) {
			CommonHelper.service.copyInstanceIBA(subPart, map);
			EPMDocument epm = DrawingHelper.service.getEPMDocument(subPart);
			if (epm != null) {
				CommonHelper.service.copyInstanceIBA(epm, map);
			}
		}

	}

	@Override
	public List<Map<String, Object>> listAUIPartAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		QueryResult qr = null;
		// QuerySpec query = PartQueryHelper.service.listPartSearchQuery(request,
		// response);
		QuerySpec query = PartQueryHelper.service.listPartApprovedSearchQuery(request, response);
		System.out.println("query : " + query);
		qr = PersistenceHelper.manager.find(query);
		Object[] o = null;
		WTPart part = null;
		VersionData data = null;

		String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> verMap = new HashMap<String, String>();
		String number = "";
		while (qr.hasMoreElements()) {
			Map<String, Object> result = new HashMap<String, Object>();
			o = (Object[]) qr.nextElement();
			part = (WTPart) o[0];
			System.out.println("part number : " + part.getNumber() + " / "
					+ part.getPersistInfo().getObjectIdentifier().getStringValue());
			data = new VersionData((RevisionControlled) part);
			String remarks = StringUtil
					.checkNull(IBAUtil.getAttrValue((IBAHolder) part, AttributeKey.IBAKey.IBA_REMARKS));
			number = part.getNumber();
			int kk = 0;
			if (verMap.containsKey(number)) {

				kk = verMap.get(number).compareTo(data.version);
				if (kk > 0) {
					continue;
				} else {
					verMap.put(number, data.version);
				}
			} else {
				verMap.put(number, data.version);
			}
			boolean isProduct = PartUtil.isProductCheck(number);
			String link = "<a href=javascript:openDistributeView('" + data.oid + "')>" + data.name + "</a>";
			String bom = "<button type='button' class='btnCustom' onclick=javascript:viewBom('" + data.oid
					+ "')><span></span>BOM</buttom>";
			result.put("icon", BasicTemplateProcessor.getObjectIconImgTag(part));
			result.put("number", number);
			result.put("name", data.name);
			result.put("oid", data.oid);
			result.put("location", data.getLocation());
			result.put("version", data.version);
			result.put("rev", data.version + "." + data.iteration);
			result.put("remarks", remarks);
			result.put("state", data.getLifecycle());
			result.put("creator", data.creator);
			result.put("createDate", data.createDate.substring(0, 10));
			result.put("modifyDate", data.modifyDate.substring(0, 10));
			result.put("bom", "");
			result.put("isProduct", isProduct);

			resultList.add(result);
		}

		return resultList;
	}

	@Override
	public Map<String, Object> listPagingAUIPartAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> map = new HashMap<>();

		// QuerySpec query = PartQueryHelper.service.listPartSearchQuery(request,
		// response);

		int page = StringUtil.getIntParameter((String) request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter((String) request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) request.getParameter("formPage"), 15);

		String sessionId = (String) request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = PartQueryHelper.service.listPartApprovedSearchQuery(request, response);

			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();
		long sessionIdLong = control.getSessionId();

		Object[] o = null;
		WTPart part = null;
		VersionData data = null;

		String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> verMap = new HashMap<String, String>();
		String number = "";
		while (qr.hasMoreElements()) {
			Map<String, Object> result = new HashMap<String, Object>();
			o = (Object[]) qr.nextElement();
			part = (WTPart) o[0];
			data = new VersionData((RevisionControlled) part);
			String remarks = StringUtil
					.checkNull(IBAUtil.getAttrValue((IBAHolder) part, AttributeKey.IBAKey.IBA_REMARKS));
			number = part.getNumber();
			int kk = 0;
//			if(verMap.containsKey(number)){
//				
//				kk = verMap.get(number).compareTo(data.version);
//				if(kk>0){
//					continue;
//				}else{
//					verMap.put(number, data.version);
//				}
//			}else{
//				verMap.put(number, data.version);
//			}
			boolean isProduct = PartUtil.isProductCheck(number);
			String link = "<a href=javascript:openDistributeView('" + data.oid + "')>" + data.name + "</a>";
			String bom = "<button type='button' class='btnCustom' onclick=javascript:viewBom('" + data.oid
					+ "')><span></span>BOM</buttom>";
			result.put("icon", BasicTemplateProcessor.getObjectIconImgTag(part));
			result.put("number", number);
			result.put("name", data.name);
			result.put("oid", data.oid);
			result.put("location", data.getLocation());
			result.put("version", data.version);
			result.put("rev", data.version + "." + data.iteration);
			result.put("remarks", remarks);
			result.put("state", data.getLifecycle());
			result.put("creator", data.creator);
			result.put("createDate", data.createDate.substring(0, 10));
			result.put("modifyDate", data.modifyDate.substring(0, 10));
			result.put("bom", "");
			result.put("isProduct", isProduct);

			resultList.add(result);
		}

		map.put("list", resultList);
		map.put("totalPage", totalPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("listCount", listCount);
		map.put("totalCount", totalCount);
		map.put("currentPage", currentPage);
		map.put("param", param);
		map.put("rowCount", rowCount);
		map.put("sessionId", sessionIdLong);

		return map;
	}

	/**
	 * BOM 에서 선택저 부품 첨부 파일 다운로드
	 */
	@Override
	public void partTreeSelectAttachDown(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> param) throws Exception {

		List<Map<String, Object>> rowItems = (List<Map<String, Object>>) param.get("rowItems");
		List<ApplicationData> listAppData = new ArrayList<ApplicationData>();
		// System.out.println("partTreeSelectAttachDown size =" +rowItems.size());
		for (Map<String, Object> rowMaps : rowItems) {
			Map<String, Object> rowMap = (Map) rowMaps.get("item");

			String oid = (String) rowMap.get("id");

			WTPart part = (WTPart) CommonUtil.getObject(oid);

			// System.out.println(part.getNumber());
			listAppData = CommonContentHelper.service.getAttachFileList((ContentHolder) part,
					ContentRoleType.SECONDARY.toString(), listAppData);

		}

		MakeZIPUtil.attchFileZip(listAppData, "첨부파일", response);

	}

	@Override
	public void partReName(WTPart part, String changeName) throws Exception {
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;
		// System.out.println("============partReName =================");
		try {
			if (!part.getName().equals(changeName)) {
				methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				Connection con = wtconnection.getConnection();

				WTPartMaster master = (WTPartMaster) part.getMaster();
				long longOid = CommonUtil.getOIDLongValue(master);

				StringBuffer sql = new StringBuffer();

				sql.append("UPDATE WTPartMaster set name= ? where ida2a2 = ? ");
				// System.out.println(sql.toString());
				// System.out.println(changeName + ","+longOid);
				st = con.prepareStatement(sql.toString());
				st.setString(1, changeName);
				st.setLong(2, longOid);
				st.executeQuery();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				throw new WTException(e);
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

	/**
	 * Part의 BOM 도면 일괄 다운로드
	 */
	@Override
	public ResultData batchBomDrawingDownAction(String oid, String describe, String ecoOid) {
		ResultData returnData = new ResultData();
		try {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(ecoOid);
			List<BatchDownData> list = new ArrayList<BatchDownData>();
			List<WTPart> partList = new ArrayList<WTPart>();
			String bsobj = "";
			ManagedBaseline baseline = null;
			BomBroker broker = new BomBroker();
			// if(isDistribute){
			baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
			// }
			// System.out.println("batchBomDrawingDownAction baseline
			// Name="+baseline.getName());

			partList = getTree(part, baseline, null, partList);// ECOSearchHelper.service.ecoPartReviseList(eo);

			getEPMBatchDownList(list, partList, describe);
			// ECA 의 산출물
			// List<EChangeActivity> ecalist = ECOHelper.service.

			// HashMap<ApplicationData, String> mapTemp = new HashMap<ApplicationData,
			// String>();

			File zipFile = MakeZIPUtil.batcchAttchFileZip(list, part.getNumber());
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
	 * Part의 도면 리스트
	 */
	@Override
	public List<BatchDownData> getEPMBatchDownList(List<BatchDownData> list, List<WTPart> partList, String describe)
			throws Exception {

		for (WTPart part : partList) {

			EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
			// System.out.println("part number = "+ part.getNumber()+"\t Start");
			// System.out.println("epm number = "+ (null!=epm?epm.getNumber():"Null")+"\t
			// Start");
			if (epm != null) {
				if ("PROE".equals(epm.getAuthoringApplication().toString())) {
					Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service
							.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());
					if (vec2D.size() == 0) {
						BatchDownData batchData = new BatchDownData();
						batchData = CommonContentHelper.service.setBatchDownData((ContentHolder) part, batchData);
						batchData.setAppData(null);
						batchData.setDescribe(describe);
						batchData.setFileName("");
						batchData.setAttachType("");
						batchData.setCreator(epm.getCreatorFullName());
						batchData.setModifier(epm.getModifierFullName());
						list.add(batchData);
					} else {
						for (int h = 0; h < vec2D.size(); h++) {
							EPMReferenceLink epmlink = vec2D.get(h);
							EPMDocument epm2d = epmlink.getReferencedBy();
							if (epm2d.getDocType().toString().equals("CADDRAWING")) {
								list = CommonContentHelper.service.getAttachFileList((ContentHolder) epm2d, list,
										describe);
							}

						}
					}

				} else {
					list = CommonContentHelper.service.getAttachFileList((ContentHolder) epm, list, describe);
				}
			} else {
				BatchDownData batchData = new BatchDownData();
				batchData = CommonContentHelper.service.setBatchDownData((ContentHolder) part, batchData);
				batchData.setAppData(null);
				batchData.setDescribe(describe);
				batchData.setFileName("");
				batchData.setAttachType("");
				list.add(batchData);
			}
			// System.out.println("part number = "+ part.getNumber()+"\t End");
		}

		return list;
	}

	/**
	 * Part의 BOM 도면 일괄 다운로드
	 */
	@Override
	public ResultData batchBomSelectDownAction(String oid, List<Map<String, Object>> itemList, String describe,
			String downType, String describeType) {
		ResultData returnData = new ResultData();
		try {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			List<BatchDownData> list = new ArrayList<BatchDownData>();
			List<WTPart> partList = new ArrayList<WTPart>();
			Map<String, String> map = CommonUtil.getBatchDescribe();
			describeType = StringUtil.checkNull(map.get(describeType));
			describe = "[" + describeType + "]" + describe;
			for (Map<String, Object> item : itemList) {

				String subOid = (String) item.get("oid");
				WTPart subPart = (WTPart) CommonUtil.getObject(subOid);
				if (partList.contains(subPart)) {
					continue;
				}
				partList.add(subPart);
				if (downType.equals("attach")) {
					list = CommonContentHelper.service.getAttachFileList((ContentHolder) subPart, list, describe);
				}

			}

			if (!downType.equals("attach")) {
				list = getEPMBatchDownList(list, partList, describe);
			}

			File zipFile = MakeZIPUtil.batcchAttchFileZip(list, part.getNumber());
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

	private List<WTPart> getTree(WTPart part, Baseline baseline, View view, List<WTPart> partList) throws Exception {
		ArrayList list = null;

		if (view == null) {
			view = getView();
		}
		// System.out.println("parent Part = "+part.getNumber());
		if (baseline == null) {
			list = descentLastPart(part, view, null);
		} else {
			list = descentLastPart(part, baseline, null);
		}
		for (int i = 0; i < list.size(); i++) {
			Object[] o = (Object[]) list.get(i);
			//// System.out.println(o[1]+","+o[2]+","+o[3]);
			WTPart cPart = (WTPart) o[1];
			if (!partList.contains(cPart)) {
				partList.add(cPart);
			}
			// System.out.println("getTree subPart = "+cPart.getNumber());
			partList = getTree(cPart, baseline, view, partList);
		}
		return partList;
	}

	private ArrayList descentLastPart(WTPart part, Baseline baseline, State state) throws WTException {
		ArrayList v = new ArrayList();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseline);
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);

			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();

				if (!(oo[1] instanceof WTPart)) {
					continue;
				}

				WTPartUsageLink link = (WTPartUsageLink) oo[0];
				WTPart subPart = (WTPart) oo[1];

				//// System.out.println("baseline1111 : "+subPart.getNumber()
				//// +":"+link.getQuantity().getAmount());
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

	private ArrayList descentLastPart(WTPart part, View view, State state) throws WTException {
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

	public View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}

	@Override
	public ResultData attributeCleaning(Map<String, Object> param) throws Exception {
		ResultData data = new ResultData();
		String oid = (String) param.get("oid");
		System.out.println(oid);
		try {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			// String ver =""
			System.out.println("part.getVersionIdentifier().getValue()                : "
					+ part.getVersionIdentifier().getValue());

			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_REV, part.getVersionIdentifier().getValue(), "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, part.getName(), "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_CHANGENO, "", "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_CHANGEDATE, "", "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_ECONO, "", "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_ECODATE, "", "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_APR, "", "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_CHK, "", "string");

			EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
			if (epm != null) {
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_REV, part.getVersionIdentifier().getValue(),
						"string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, part.getName(), "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_CHANGENO, "", "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_CHANGEDATE, "", "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_ECONO, "", "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_ECODATE, "", "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_APR, "", "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_CHK, "", "string");
			}

			data.setResult(true);
			data.setMessage("속성이 초기화 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage("속성 초기화시 에러가 발생 하였습니다.");
		}

		return data;
	}

	@Override
	public ResultData updateAUIPartChangeAction(Map<String, Object> param) {
		ResultData data = new ResultData();

		Transaction trx = new Transaction();

		// String[] numbers = request.getParameterValues("realPNumber");
		// String[] names = request.getParameterValues("realPName");
		// System.out.println("=========== updateAUIPackagePartAction ============");

		WTPart part = null;
		try {
			trx.start();
			String pOid = (String) param.get("oid");
			WTPart pPart = (WTPart) CommonUtil.getObject(pOid);
			String change_number = pPart.getNumber();
			// System.out.println("Before Oid = "+CommonUtil.getOIDString(pPart));
			boolean isChange = false;
			ArrayList<Map<String, Object>> paramListdata = getData(param);
			// System.out.println("Size = "+paramListdata.size());
			if (paramListdata.size() > 0) {
				for (int i = 0; i < paramListdata.size(); i++) {
					Map<String, Object> returnMap = paramListdata.get(i);
					String realPartNumber = (String) returnMap.get("realPNumber");
					String realPName = (String) returnMap.get("realPName");
					String realPName1 = (String) returnMap.get("realPName1");
					String realPName2 = (String) returnMap.get("realPName2");
					String realPName3 = (String) returnMap.get("realPName3");
					String realPName4 = (String) returnMap.get("realPName4");
					String cOid = (String) returnMap.get("cOid");
					WTPart oldPart = (WTPart) CommonUtil.getObject(cOid);
					boolean isPartNameEdit = true;
					boolean isPartNumberEdit = false;
					if (null == realPartNumber || realPartNumber.length() != 10) {
						isPartNumberEdit = true;
						realPartNumber = oldPart.getNumber();
					} else {
						isPartNumberEdit = false;
					}
					if (null == realPName || realPName.length() == 0) {
						isPartNameEdit = false;
						realPName = oldPart.getName();
					} else {
						isPartNameEdit = true;
					}
					if (oldPart.getNumber().equals(pPart.getNumber())) {
						change_number = realPartNumber;
					}
					// System.out.println("isPartNumberEdit="+isPartNumberEdit+"\tisPartNameEdit="+isPartNameEdit+"\trealPartNumber="+realPartNumber+"\trealPName="+realPName);
					int idx = 0;
					storeNumberANameValue(cOid, realPartNumber, realPName, realPName1, realPName2, realPName3,
							realPName4, isPartNameEdit);
				}
			} else {
				throw new Exception("Store Fail !!!");
			}
			// store

			trx.commit();
			trx = null;
			data.setResult(true);
			WTPart plPart = PartHelper.service.getPart(change_number);
			// System.out.println("Change Oid = "+CommonUtil.getOIDString(plPart));
			data.setOid(CommonUtil.getOIDString(plPart));

		} catch (Exception e) {
			data.setResult(false);
			String msg = e.getLocalizedMessage();
			if (numberFormCheck(msg))
				msg = "알수 없는 오류가 발생하였습니다. 관라자에게 문의 하세요.";
			data.setMessage(msg);
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return data;
	}

	private ArrayList<Map<String, Object>> getData(Map<String, Object> param) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String realPNumber = "";
		String realPName = "";
		String realPName1 = "";
		String realPName2 = "";
		String realPName3 = "";
		String realPName4 = "";
		WTPart oldPart = null;
		String seq = "";
		String etc = "";
		String pnumber5 = "";
		ArrayList<String> oidList = new ArrayList<String>();
		for (String key : param.keySet()) {
			if (key.endsWith("oid"))
				continue;
			// System.out.println( String.format("키 : %s, 값 : %s", key, param.get(key)) );
			String[] dataKeys = key.split("_");
			String changeField = dataKeys[0];
			String changeOid = dataKeys[1];
			if (!oidList.contains(changeOid)) {
				// System.out.println("ADD "+ changeOid);
				oidList.add(changeOid);
			}
		}
		ArrayList<Map<String, Object>> fiveNumberCheckList = new ArrayList<Map<String, Object>>();
		String compareSeq = "";
		String compareRealPNumber = "";
		String partNumberSeqs = "";
		// System.out.println("ADD Size = "+ oidList.size());
		for (int i = 0; i < oidList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String cOid = (String) oidList.get(i);
			// System.out.println("ADD cOid="+ cOid);
			map.put("cOid", cOid);
			oldPart = (WTPart) CommonUtil.getObject(cOid);
			map.put("oldPart", oldPart);
			realPNumber = (String) param.get("realPNumber_" + cOid);
			partNumberSeqs = (String) param.get("partNumberSeqs_" + cOid);
			if (null == realPNumber || realPNumber.equals("undefined")) {
				realPNumber = "";
			}
			// System.out.println("ADD realPNumber="+ realPNumber);
			boolean isNumberFormCheck = numberFormCheck(realPNumber);

			realPName = (String) param.get("realPName_" + cOid);

			if (null == realPName || realPName.equals("undefined")) {
				realPName = "";
			}
			if (realPName.length() > 0 && realPName.lastIndexOf("_") == (realPName.length() - 1)) {
				realPName = realPName.substring(0, realPName.length() - 1);
			}
			if (realPName.length() > 0 && realPName.indexOf("_") == 0) {
				realPName = realPName.substring(1, realPName.length() - 1);
			}
			// System.out.println("ADD realPName="+ realPName);
			map.put("realPName", realPName);

			realPName1 = (String) param.get("realPName1_" + cOid);

			if (null == realPName1 || realPName1.equals("undefined")) {
				realPName1 = "";
			}

			// System.out.println("ADD realPName1="+ realPName1);
			map.put("realPName1", realPName1);

			realPName2 = (String) param.get("realPName2_" + cOid);

			if (null == realPName2 || realPName2.equals("undefined")) {
				realPName2 = "";
			}

			// System.out.println("ADD realPName2="+ realPName2);
			map.put("realPName2", realPName2);

			realPName3 = (String) param.get("realPName3_" + cOid);

			if (null == realPName3 || realPName3.equals("undefined")) {
				realPName3 = "";
			}

			// System.out.println("ADD realPName3="+ realPName3);
			map.put("realPName3", realPName3);

			realPName4 = (String) param.get("realPName4_" + cOid);

			if (null == realPName4 || realPName4.equals("undefined")) {
				realPName4 = "";
			}

			// System.out.println("ADD realPName4="+ realPName4);
			map.put("realPName4", realPName4);

			// System.out.println("ADD isNumberFormCheck="+ isNumberFormCheck);
			// 0123456789
			// gubun = 0
			// main = 1,2
			// middle = 3,4
			// seq = 5~7
			// etc = 8,9
			if (null != realPNumber && realPNumber.length() == 10 && isNumberFormCheck) {
				map.put("realPNumber", realPNumber);
			} else if (null != realPNumber && realPNumber.length() == 10 && !isNumberFormCheck) {
				String gubun = realPNumber.substring(0, 1);
				String main = realPNumber.substring(1, 3);
				String middle = realPNumber.substring(3, 5);
				// System.out.println("realPNumber="+realPNumber);
				// System.out.println("gubun="+gubun);
				// System.out.println("main="+main);
				// System.out.println("middle="+middle);
				boolean isNumber5FormCheck = numberFormCheck(gubun) && numberFormCheck(main) && numberFormCheck(middle);
				// System.out.println("ADD isNumber5FormCheck="+ isNumber5FormCheck);
				if (!isNumber5FormCheck) {
					list.clear();
					throw new Exception("품목 구분을 3가지 모두 선택하세요");
				}

				String seqStr = realPNumber.substring(5, 8).replaceAll("_", "0");
				if (seqStr.equals("000")) {
					int cnt = 0;
					seq = SequenceDao.manager.getSeqNo(gubun + main + middle, "000", "WTPartMaster", "WTPartNumber");
					int partNumberSeqsint = 0;
					/*
					 * if(null!=partNumberSeqs &&partNumberSeqs.length()>0 ){ //seq=
					 * String.valueOf((Integer.parseInt(seq)+partNumberSeqsint));
					 * //System.out.println((gubun+main+middle)+"\tpartNumberSeqs="+partNumberSeqs);
					 * seq=
					 * String.valueOf((Integer.parseInt(seq)+Integer.parseInt(partNumberSeqs)));
					 * }else{
					 */
					int gubunIDx = 0;
					// System.out.println("fiveNumberCheckList Size = "+
					// fiveNumberCheckList.size());
					for (int j = 0; j < fiveNumberCheckList.size(); j++) {
						HashMap<String, Object> ma = (HashMap<String, Object>) fiveNumberCheckList.get(j);
						Object obj = ma.get(gubun + main + middle);
						if (null != obj) {
							cnt++;
						}
					}
					// System.out.println("seq = "+ seq);
					seq = String.valueOf((Integer.parseInt(seq) + cnt));
					// System.out.println("affter seq = "+ seq);
					HashMap<String, Object> e = new HashMap<String, Object>();
					e.put(gubun + main + middle, "ADD");
					fiveNumberCheckList.add(e);
					// }
					if (seq.length() == 1)
						seq = "00" + seq;
					else if (seq.length() == 2)
						seq = "0" + seq;
					compareRealPNumber = gubun + main + middle;
				} else {
					seq = seqStr;
				}
				// System.out.println("realPNumber Length ="+realPNumber.length());
				etc = realPNumber.substring(8, realPNumber.length()).replaceAll("_", "0");
				// System.out.println("seq="+seq);
				// System.out.println("etc="+etc);
				realPNumber = realPNumber.substring(0, 5) + seq + etc;
				// System.out.println("realPNumber="+realPNumber);
				map.put("realPNumber", realPNumber);
			}
			list.add(map);
		}
		return list;
	}

	private boolean numberFormCheck(String gubun) {
		try {
			Integer.parseInt(gubun);
			// System.out.println("numberform="+gubun);
			return true;
		} catch (NumberFormatException nfe) {
			// System.out.println("numberform error ");
			return false;
		}
	}

	private void storeNumberANameValue(String changeOid, String realPNumber, String partName, String partName1,
			String partName2, String partName3, String partName4, boolean isPartNameEdit) throws Exception {
		Object obj = CommonUtil.getObject(changeOid);
		if (null != obj) {
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				E3PSRENameObject.manager.PartReName(part, realPNumber, partName, false);
				if (WorkInProgressHelper.isCheckedOut(part)) {
					part = (WTPart) WorkInProgressHelper.service.checkin(part, "PDM에서 채번을 하였습니다.");
				}
				if (isPartNameEdit) {
					// String[] nameSplit = partName.split("_");
					Map<String, Object> map = new HashMap<String, Object>();
					if (partName1.length() > 0)
						map.put("partName1", partName1);
					if (partName2.length() > 0)
						map.put("partName2", partName2);
					if (partName3.length() > 0)
						map.put("partName3", partName3);
					if (partName4.length() > 0)
						map.put("partName4", partName4);
					CommonHelper.service.changeIBAValues(part, map);
				}
				// 부품 진채번시 도면 번호 및 명 변경
				EPMDocument oldEpm = DrawingHelper.service.getEPMDocument(part);
				String dsgn = part.getCreatorFullName();
				String ver = part.getVersionIdentifier().getValue();
				String des = part.getName();
				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DSGN, dsgn, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_REV, ver, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, des, "string");
				IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_SEQCHECK, "Y", "boolean");

				if (oldEpm != null) {
					// 3D 변경
					EPMDocument epm = (EPMDocument) getWorkingCopy(oldEpm);
					// System.out.println("epm.getCADName()="+epm.getCADName());
					String extention = EpmUtil.getExtension(epm.getCADName());

					extention = extention.toUpperCase();
					String docTypeEng = epm.getDocType().getDisplay(Locale.ENGLISH).toUpperCase();
					extention = checkExtention(extention, docTypeEng);
					// System.out.println("docTypeEng="+docTypeEng);
					// System.out.println("extention="+extention);
					String epmNumber = realPNumber + "." + extention;
					String fileName = epmNumber.toLowerCase();
					// System.out.println(oldEpm.getNumber()+"\tepmNumber="+epmNumber);
					E3PSRENameObject.manager.EPMReName(epm, epmNumber, partName, fileName, false);
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DSGN, dsgn, "string");
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_REV, ver, "string");
					IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, des, "string");

					if (WorkInProgressHelper.isCheckedOut(epm)) {
						epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm, "PDM에서 채번을 하였습니다.");
					}
					// 2D 변경
					Vector<EPMReferenceLink> vec = EpmSearchHelper.service
							.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());

					if (vec.size() > 1) {
						throw new Exception("해당 품목에 연결된 도면이 다수 존재합니다. 확인 후 재시도해주시기 바랍니다.");
					} else if (vec.size() == 1) {

						for (EPMReferenceLink link : vec) {

							EPMDocument oldEpm2d = link.getReferencedBy();
							String docType = oldEpm2d.getDocType().toString();
							// System.out.println("docType="+docType);
							if (docType.equals("CADDRAWING") || docType.equals("")) {
								// System.out.println("oldEpm2d.getCADName()="+oldEpm2d.getCADName()+"\toldEpm2d.getNumber()="+oldEpm2d.getNumber());
								EPMDocument epm2D = (EPMDocument) getWorkingCopy(oldEpm2d);
								// System.out.println("epm2D.getCADName()="+epm2D.getCADName());
								extention = EpmUtil.getExtension(epm2D.getCADName());
								extention = extention.toUpperCase();
								docTypeEng = epm2D.getDocType().getDisplay(Locale.ENGLISH).toUpperCase();
								// System.out.println("docTypeEng="+docTypeEng);
								extention = checkExtention(extention, docTypeEng);
								epmNumber = realPNumber + "." + extention;
								String fileName2D = epmNumber.toLowerCase();
								// System.out.println(oldEpm2d.getNumber()+"\tepmNumber="+epmNumber+"\tdes="+des+"\tpartName="+partName);
								E3PSRENameObject.manager.EPMReName(epm2D, epmNumber, partName, fileName2D, false);
								if (WorkInProgressHelper.isCheckedOut(epm2D)) {
									epm2D = (EPMDocument) WorkInProgressHelper.service.checkin(epm2D,
											"PDM에서 채번을 하였습니다.");
								}
								EpmPublishUtil.publish(epm2D);
							}
						}
					}
				}
			}
		}
	}

	private String checkExtention(String extention, String docTypeEng) {
		if (docTypeEng.equals("CAD PART") && !extention.toUpperCase().equals("PRT"))
			extention = "PRT";
		if (docTypeEng.equals("ASSEMBLY") && !extention.toUpperCase().equals("ASM"))
			extention = "ASM";
		if (docTypeEng.equals("DRAWING") && !extention.toUpperCase().equals("DRW"))
			extention = "DRW";
		return extention;
	}

	private void storeNameValue(String[] changeOids, String[] values) {
		for (int i = 0; i < changeOids.length; i++) {

		}
	}

	@Override
	public Map<String, Object> listPartAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createComments(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = StringUtil.checkNull((String) params.get("oid"));
			String comments = StringUtil.checkNull((String) params.get("comments"));
			int num = (int) params.get("num");
			int step = (int) params.get("step");
			String oPerson = StringUtil.checkNull((String) params.get("person"));

			WTPart part = (WTPart) CommonUtil.getObject(oid);

			Comments com = new Comments();
			com.setWtpart(part);
			com.setComments(comments);
			com.setCNum(num);
			com.setCStep(step);
			com.setOPerson(oPerson);
			com.setDeleteYN("N");
			com.setOwner(SessionHelper.manager.getPrincipalReference());

			PersistenceHelper.manager.save(com);

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
	public void updateComments(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = StringUtil.checkNull((String) params.get("oid"));
			String comments = StringUtil.checkNull((String) params.get("comments"));

			Comments com = (Comments) CommonUtil.getObject(oid);
			com.setComments(comments);

			PersistenceHelper.manager.modify(com);

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
	public void deleteComments(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Comments com = (Comments) CommonUtil.getObject(oid);

			int child = PartHelper.manager.getCommentsChild(com);
			if (child > 0) {
				com.setDeleteYN("Y");
				PersistenceHelper.manager.modify(com);
			} else {
				PersistenceHelper.manager.delete(com);
			}
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
	public Map<String, Object> partCheckIn(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		//////////////////////////////////////////////////
		// 체크인

		if (WorkInProgressHelper.isCheckedOut(part)) {
			part = (WTPart) WorkInProgressHelper.service.checkin(part, Message.get("BOM Editor에서 체크인 되었습니다"));
			result.put("msg", "체크인 하였습니다.");
		} else {
			result.put("msg", "체크아웃 후 가능합니다.");
		}

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.toString());

		}

		return result;
	}

	@Override
	public Map<String, Object> partCheckOut(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		//////////////////////////////////////////////////
		// 체크인
		if (!WorkInProgressHelper.isCheckedOut(part)) {

			if (!CheckInOutTaskLogic.isCheckedOut(part)) {
				CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(part,
						CheckInOutTaskLogic.getCheckoutFolder(), "부품 체크 아웃");
			}

			part = (WTPart) WorkInProgressHelper.service.workingCopyOf(part);
			result.put("msg", "체크아웃 하였습니다.");
		} else {
			result.put("msg", "체크아웃 중입니다.");
		}

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.toString());

		}

		return result;
	}

	@Override
	public Map<String, Object> partUndoCheckOut(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		// 체크인
		if (!WorkInProgressHelper.isCheckedOut(part)) {
			result.put("msg", "체크아웃 중이 아닙니다.");
		} else {
			ObjectUtil.undoCheckout(part);
			result.put("msg", "체크아웃 취소 하였습니다.");
		}

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.toString());
		}

		return result;
	}

}
