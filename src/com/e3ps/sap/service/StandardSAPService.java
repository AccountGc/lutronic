package com.e3ps.sap.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.PartToSendLink;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPDev600Connection;
import com.e3ps.sap.dto.SAPBomDTO;
import com.e3ps.sap.dto.SAPSendBomDTO;
import com.e3ps.sap.util.SAPUtil;
import com.e3ps.system.service.SystemHelper;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import wt.epm.EPMDocument;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class StandardSAPService extends StandardManager implements SAPService {

	public static StandardSAPService newStandardSAPService() throws WTException {
		StandardSAPService instance = new StandardSAPService();
		instance.initialize();
		return instance;
	}

	@Override
	public void sendSapToEo(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts, ArrayList<WTPart> list)
			throws Exception {
		// 결재완료 안에서 동작하기에 트랜젝션 제외
		System.out.println("EO SAP 인터페이스 시작");
		// 자재마스터 전송
		sendToSapEoPart(e, completeParts);
		// BOM 전송
		sendToSapEoBom(e, completeParts);
		System.out.println("EO SAP 인터페이스 종료");
		// 모든 대상 품목 상태값 승인됨 처리 한다.
	}

	/**
	 * EO 전용 BOM 전송 함수
	 */
	private void sendToSapEoBom(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		System.out.println("시작 SAP 인터페이스 - EO BOM FUN : ZPPIF_PDM_002");
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		DecimalFormat df = new DecimalFormat("0000");
		Timestamp t = new Timestamp(new Date().getTime());
		String today = t.toString().substring(0, 10).replaceAll("-", "");

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트

		// EO/ECO 헤더 정보 전송
		JCoTable eoTable = function.getTableParameterList().getTable("ET_ECM");

		eoTable.appendRow();
		eoTable.setValue("AENNR8", e.getEoNumber()); // 변경번호 12자리??
		eoTable.setValue("ZECMID", "EO"); // EO/ECO 구분
		eoTable.setValue("DATUV", today); // 보내는 날짜
		eoTable.setValue("AEGRU", "초도 BOM"); // 변경사유 테스트 일단 한줄
		eoTable.setValue("AETXT", e.getEoName()); // 변경 내역 첫줄만 일단 테스트
		String AETXT_L = e.getEoCommentA() != null ? e.getEoCommentA() : "";
		eoTable.setValue("AETXT_L", AETXT_L.replaceAll("<br>", "\n")); // 변경 내역 전체 내용

		// 완제품으로 품목을 담는다.
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			list.add(root);
		}

		JCoTable bomTable = function.getTableParameterList().getTable("ET_BOM");
		// BOM 전송 한번 돌고 호출하고 하는식???
		for (WTPart part : list) {
			// 완제품에 해당하는 BOM 목록들..
			ArrayList<SAPBomDTO> dataList = SAPHelper.manager.getterBomData(part);

			System.out.println("BOM 리스트 항목 개수  = " + dataList.size());

			// 완제품 기준으로 다시 SEQ 리셋..?
			int idx = 1;
			for (SAPBomDTO dto : dataList) {
				System.out.println("상위품번 = " + dto.getNewParentPartNumber() + ", 하위품번 = " + dto.getNewChildPartNumber()
						+ ", 단위 = " + dto.getUnit());
				bomTable.insertRow(idx);
				bomTable.setValue("AENNR8", e.getEoNumber()); // 변경번호 12자리?
				bomTable.setValue("SEQNO", df.format(idx)); // 항목번호 ?? 고정인지.. 애매한데
				// EO 일경우 NEW 에만
				bomTable.setValue("MATNR_NEW", dto.getNewParentPartNumber()); // 기존 모품번
				bomTable.setValue("IDNRK_NEW", dto.getNewChildPartNumber()); // 기존 자품번
				bomTable.setValue("MENGE", dto.getQty()); // 수량
				bomTable.setValue("MEINS", dto.getUnit()); // 단위
				bomTable.setValue("AENNR12", e.getEoNumber() + df.format(idx)); // 변경번호 12자리
				// AENNR12
				idx++;
			}
		}

		function.execute(destination);
		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_BOM");
		rtnTable.firstRow();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
			Object IDNRK_NEW = rtnTable.getValue("IDNRK_NEW");
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			System.out.println("EO BOM 결과 ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG + ", IDNRK_NEW=" + IDNRK_NEW);
		}

		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
		System.out.println("종료 SAP 인터페이스 - EO BOM FUN : ZPPIF_PDM_002");

	}

	/**
	 * EO 전용 자재마스터 리스트 전송 함수
	 */
	private void sendToSapEoPart(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		System.out.println("시작 SAP 인터페이스 - EO 자재마스터 FUN : ZPPIF_PDM_001");
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		System.out.println("destination=" + destination);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		// 완제품으로 하위에 있는 모든 품목을 조회 해서 전송한다
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			// 중복 품목 제외를 한다.
			list = SAPHelper.manager.getterSkip(root);
		}

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		int idx = 1;
		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
		// 자재는 한번에 넘기고 함수 호출
		for (WTPart part : list) {
			Map<String, Object> params = new HashMap<>();
			String number = part.getNumber();
			// 전송 제외 품목
			if (SAPHelper.manager.skipEight(number)) {
				continue;
			}

			if (SAPHelper.manager.skipLength(number)) {
				continue;
			}

			System.out.println("전송된 자재 번호 = " + number + ", 단위 = " + part.getDefaultUnit().toString().toUpperCase());

			// 샘플로 넣기
			insertTable.insertRow(idx);
			insertTable.setValue("AENNR8", e.getEoNumber()); // 변경번호 8자리
			params.put("AENNR8", e.getEoNumber());
			insertTable.setValue("MATNR", number); // 자재번호
			params.put("MATNR", number);
			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
			params.put("MAKTX", part.getName());

			if (part.getDefaultUnit().toString().toUpperCase().equals("EA")) {
				insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
				params.put("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
			} else {
				insertTable.setValue("MEINS", "EA"); // 기본단위
				params.put("MEINS", "EA");
			}

			insertTable.setValue("ZSPEC", IBAUtil.getStringValue(part, "SPECIFICATION")); // 사양
			params.put("ZSPEC", IBAUtil.getStringValue(part, "SPECIFICATION")); // 사양

			String ZMODEL_CODE = SAPHelper.manager.convertSapValue(part, "MODEL");
			String ZMODEL_VALUE = SAPUtil.sapValue(ZMODEL_CODE, "MODEL"); // Model:프로젝트
			insertTable.setValue("ZMODEL", ZMODEL_VALUE);
			params.put("ZMODEL", ZMODEL_VALUE);

			String ZPRODM_CODE = SAPHelper.manager.convertSapValue(part, "PRODUCTMETHOD");
			String ZPRODM_VALUE = SAPUtil.sapValue(ZPRODM_CODE, "PRODUCTMETHOD"); // 제작방법
			insertTable.setValue("ZPRODM", ZPRODM_VALUE);
			params.put("ZPRODM", ZPRODM_VALUE);

			String ZDEPT_CODE = SAPHelper.manager.convertSapValue(part, "DEPTCODE");
			String ZDEPT_VALUE = SAPUtil.sapValue(ZDEPT_CODE, "DEPTCODE"); // 부서
			insertTable.setValue("ZDEPT", ZDEPT_VALUE);
			params.put("ZDEPT", ZDEPT_VALUE);

			// 샘플링 실제는 2D여부 확인해서 전송

			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			if (epm != null) {
				EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
				if (epm2D != null) {
					insertTable.setValue("ZDWGNO", epm2D.getNumber());
					params.put("ZDWGNO", "");
				}
			} else {
				insertTable.setValue("ZDWGNO", "");
				params.put("ZDWGNO", "");
			}

			String v = part.getVersionIdentifier().getSeries().getValue();
			insertTable.setValue("ZEIVR", v); // 버전
			params.put("ZEIVR", v);
			// 테스트 용으로 전송
			insertTable.setValue("ZPREPO", ""); // 선구매필요 EO 시에는 어떻게 처리??
			params.put("ZPREPO", "");

			// ?? 코드: 단위 형태인지
			String weight = IBAUtil.getAttrfloatValue(part, "WEIGHT"); // 중량
			insertTable.setValue("BRGEW", weight);
			params.put("BRGEW", weight);

			insertTable.setValue("GEWEI", "G");
			params.put("GEWEI", "G");

			String ZMATLT = SAPHelper.manager.convertSapValue(part, "MAT");
			insertTable.setValue("ZMATLT", ZMATLT); // 재질
			params.put("ZMATLT", ZMATLT);

			String ZPOSTP = SAPHelper.manager.convertSapValue(part, "FINISH");
			insertTable.setValue("ZPOSTP", ZPOSTP); // 후처리
			params.put("ZPOSTP", ZPOSTP);

			String ZDEVND = SAPHelper.manager.convertSapValue(part, "MANUFACTURE");
			insertTable.setValue("ZDEVND", ZDEVND); // 개발공급업체
			params.put("ZDEVND", ZDEVND);
			params.put("sendResult", true);
			idx++;
			SystemHelper.service.saveSendPartLogger(params);
		}

		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		// 에러 리턴
		if (true) {
			// 테스트
//			MailUtils.manager.sendSAPErrorMail(e, "EO SAP 전송", (String) r_msg);
//		if ("E".equals((String) r_type)) {

		}

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_MAT");
		rtnTable.firstRow();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			System.out.println("EO 자재마스터 결과 ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG);
		}

		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
		System.out.println("종료 SAP 인터페이스 - EO 자재마스터 FUN : ZPPIF_PDM_001");
	}

	@Override
	public void sendSapToEco(EChangeOrder eco) throws Exception {
		System.out.println("ECO SAP 인터페이스 시작");
		// 신규로 발생한 자재 전송??
		sendToSapEcoPart(eco);
		// ECO BOM
		sendToSapEcoBom(eco);
		System.out.println("ECO SAP 인터페이스 종료");
		System.out.println("EO 대상품목 상태값 변경 시작");
		EcoHelper.service.ecoPartApproved(eco);
		System.out.println("EO 대상품목 상태값 변경 완료");
	}

	/**
	 * ECO BOM 전송
	 */
	private void sendToSapEcoBom(EChangeOrder eco) throws Exception {
		System.out.println("시작 SAP 인터페이스 - ECO BOM FUN : ZPPIF_PDM_002");
		// 결재완료 안에서 동작하기에 트랜젝션 제외
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		int idx = 1;

		DecimalFormat df = new DecimalFormat("0000");
		Timestamp t = new Timestamp(new Date().getTime());
		String today = t.toString().substring(0, 10).replaceAll("-", "");

		// ECO 헤더 정보 전송
		JCoTable ecoTable = function.getTableParameterList().getTable("ET_ECM");
		ecoTable.appendRow();
		ecoTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리??
		ecoTable.setValue("ZECMID", "ECO"); // EO/ECO 구분
		ecoTable.setValue("DATUV", today); // 보내는 날짜
		ecoTable.setValue("AEGRU", eco.getEoName()); // 변경사유 테스트 일단 한줄
		ecoTable.setValue("AETXT", eco.getEoName()); // 변경 내역 첫줄만 일단 테스트

		if (StringUtil.checkString(eco.getEoCommentA())) {
			String AETXT_L = eco.getEoCommentA() != null ? eco.getEoCommentA() : "";
			ecoTable.setValue("AETXT_L", AETXT_L.replaceAll("\n", "<br>"));
		} else {
			ecoTable.setValue("AETXT_L", ""); // 변경 내역 전체 내용
		}

		// 변경 대상품목을 가져온다..
		JCoTable bomTable = function.getTableParameterList().getTable("ET_BOM");

		// ECO 대상품목 정전개 하는게 맞는거 같음..
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		System.out.println("정전개 대상 몇번인가 = " + qr.size());
		while (qr.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) qr.nextElement();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isPast = link.getPast();

			WTPart next_part = null;
			WTPart pre_part = null;

			if (!isPast) { // 과거 아닐경우 과거 데이터는 어떻게 할지..???
				boolean isRight = link.getRightPart();
				boolean isLeft = link.getLeftPart();
				// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
				if (isLeft) {
					// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
					next_part = (WTPart) EChangeUtils.manager.getNext(target);
					pre_part = target;
				} else if (isRight) {
					// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
					next_part = target;
					pre_part = SAPHelper.manager.getPre(target, eco);
				}

				ArrayList<SAPSendBomDTO> sendList = new ArrayList<SAPSendBomDTO>();
				// 둘다 있을 경우
				if (pre_part != null && next_part != null) {
					ArrayList<Object[]> rights = SAPHelper.manager.sendList(next_part);
					ArrayList<Object[]> lefts = SAPHelper.manager.sendList(pre_part);

					ArrayList<Map<String, Object>> addList = SAPHelper.manager.addList(lefts, rights);
					ArrayList<Map<String, Object>> removeList = SAPHelper.manager.removeList(lefts, rights);

					// 추가 항목 넣음
					for (Map<String, Object> add : addList) {
						String addOid = (String) add.get("oid");
						WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
						SAPSendBomDTO addDto = new SAPSendBomDTO();
						addDto.setParentPartNumber(null);
						addDto.setChildPartNumber(null);
						addDto.setNewParentPartNumber(next_part.getNumber());
						addDto.setNewChildPartNumber(addPart.getNumber());
						addDto.setQty((int) add.get("qty"));
						addDto.setUnit((String) add.get("unit"));
						sendList.add(addDto);

						link.setSendType("ADD");
						PersistenceHelper.manager.modify(link);

					}

					// 삭제 항목 넣음
					for (Map<String, Object> remove : removeList) {
						String removeOid = (String) remove.get("oid");
						WTPart removePart = (WTPart) CommonUtil.getObject(removeOid);
						SAPSendBomDTO removeDto = new SAPSendBomDTO();
						removeDto.setParentPartNumber(pre_part.getNumber());
						removeDto.setChildPartNumber(removePart.getNumber());
						removeDto.setNewParentPartNumber(null);
						removeDto.setNewChildPartNumber(null);
						removeDto.setQty((int) remove.get("qty"));
						removeDto.setUnit((String) remove.get("unit"));
						sendList.add(removeDto);

						EcoPartLink removeLink = EcoPartLink.newEcoPartLink((WTPartMaster) removePart.getMaster(), eco);
						removeLink.setSendType("REMOVE");
						removeLink.setVersion(removePart.getVersionIdentifier().getSeries().getValue());
						removeLink.setBaseline(true);
						removeLink.setPreOrder(false);
						removeLink.setRightPart(false);
						removeLink.setLeftPart(true);
						removeLink.setPast(false);
						PersistenceHelper.manager.save(removeLink);
						// 삭제품 저장
					}

					// 변경 대상 리스트..
					ArrayList<SAPSendBomDTO> changeList = SAPHelper.manager.getOneLevel(next_part, eco);
					Iterator<SAPSendBomDTO> iterator = changeList.iterator();
					while (iterator.hasNext()) {
						SAPSendBomDTO dto = iterator.next();
						String compNum = dto.getNewChildPartNumber();

						// addList에서 같은 newChildPartNumber를 찾으면 changeList에서 제거
						for (Map<String, Object> addMap : addList) {
							String addOid = (String) addMap.get("oid");
							WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
							if (addPart.getNumber().equals(compNum)) {
								iterator.remove();
							}
						}
						link.setSendType("CHANGE");
						PersistenceHelper.manager.modify(link);
					}
					sendList.addAll(changeList);
				}

				for (SAPSendBomDTO dto : sendList) {

					System.out.println("이전부모품번 = " + dto.getParentPartNumber() + ", " + " 이전자식품번 =  "
							+ dto.getChildPartNumber() + ", 신규부모품번 = " + dto.getNewParentPartNumber() + ", 신규자식품번 = "
							+ dto.getNewChildPartNumber());

					bomTable.insertRow(idx);
					bomTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리?
					bomTable.setValue("SEQNO", df.format(idx)); // 항목번호 ?? 고정인지.. 애매한데
					bomTable.setValue("MATNR_OLD", dto.getParentPartNumber()); // 이전 모품번
					bomTable.setValue("IDNRK_OLD", dto.getChildPartNumber()); // 이전 자품번
					bomTable.setValue("MATNR_NEW", dto.getNewParentPartNumber()); // 기존 모품번
					bomTable.setValue("IDNRK_NEW", dto.getNewChildPartNumber()); // 기존 자품번
					bomTable.setValue("MENGE", dto.getQty()); // 수량
					bomTable.setValue("MEINS", dto.getUnit()); // 단위
					bomTable.setValue("AENNR12", eco.getEoNumber() + df.format(idx)); // 변경번호 12자리

					idx++;
				}
			}
		}
		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_BOM");
		rtnTable.firstRow();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
//			Object IDNRK_NEW = rtnTable.getValue("IDNRK_NEW");

			Object MATNR_OLD = rtnTable.getValue("MATNR_OLD");
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			System.out.println("MATNR_OLD+" + MATNR_OLD + ", ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG);
		}
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
		System.out.println("종료 SAP 인터페이스 - ECO BOM FUN : ZPPIF_PDM_002");
	}

	/**
	 * ECO 자재 전송
	 */
	private void sendToSapEcoPart(EChangeOrder eco) throws Exception {
		System.out.println("시작 SAP 인터페이스 - ECO 자재마스터 FUN : ZPPIF_PDM_001");
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		// 완제품으로 하위에 있는 모든 품목을 조회 해서 전송한다

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		int idx = 1;
		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
		// 자재는 한번에 넘기고 함수 호출

//		ArrayList<WTPart> list = EChangeUtils.manager.getEcoParts(eco);
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (qr.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) qr.nextElement();
			boolean preOrder = link.getPreOrder();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isApproved = target.getLifeCycleState().toString().equals("APPROVED");
			boolean isFour = target.getName().startsWith("4"); // 4번 품목..
			boolean isPast = link.getPast();

			// 신규 데이터
			WTPart part = null;
			Map<String, Object> params = new HashMap<>();
			if (!isPast) {
				// 개정 케이스 - 이전품목을 가여와야한다.
				// 변경 후 품목이냐 변경 대상 푼목이냐
				boolean isLeft = link.getLeftPart();
				boolean isRight = link.getRightPart();
//				if (isApproved) {

				// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
				if (isLeft) {
					// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
					WTPart next_part = (WTPart) EChangeUtils.manager.getNext(target);
					part = next_part;
				} else if (isRight) {
					// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
					part = target;
				}

				String number = part.getNumber();
				if (SAPHelper.manager.skipEight(number)) {
					continue;
				}

				if (SAPHelper.manager.skipLength(number)) {
					continue;
				}
			} else {
				// 과거 어떻게 전송할것인지

			}

			// 변경 대상이 없다..

			insertTable.insertRow(idx);
			String v = part.getVersionIdentifier().getSeries().getValue();
			System.out.println("ECO(" + eco.getEoNumber() + ") 자재 마스터 전송 품번 : " + part.getNumber() + ", 변경 버전 = " + v);

			// 샘플로 넣기
			insertTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 8자리
			params.put("AENNR8", eco.getEoNumber());
			insertTable.setValue("MATNR", part.getNumber()); // 자재번호
			params.put("MATNR", part.getNumber());
			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
			params.put("MAKTX", part.getName());
			insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
			params.put("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위

			String ZSPEC_CODE = IBAUtil.getStringValue(part, "SPECIFICATION");
			insertTable.setValue("ZSPEC", SAPUtil.sapValue(ZSPEC_CODE, "SPECIFICATION")); // 사양

			String ZMODEL_CODE = SAPHelper.manager.convertSapValue(part, "MODEL");
			insertTable.setValue("ZMODEL", SAPUtil.sapValue(ZMODEL_CODE, "MODEL")); // Model:프로젝트

			String ZPRODM_CODE = SAPHelper.manager.convertSapValue(part, "PRODUCTMETHOD");
			insertTable.setValue("ZPRODM", SAPUtil.sapValue(ZPRODM_CODE, "PRODUCTMETHOD")); // 제작방법

			String ZDEPT_CODE = SAPHelper.manager.convertSapValue(part, "DEPTCODE");
			insertTable.setValue("ZDEPT", SAPUtil.sapValue(ZDEPT_CODE, "DEPTCODE")); // 부서

			// 샘플링 실제는 2D여부 확인해서 전송
			insertTable.setValue("ZDWGNO", part.getNumber() + ".DRW"); // 도면번호

			insertTable.setValue("ZEIVR", v); // 버전
			// 테스트 용으로 전송
			if (preOrder) {
				insertTable.setValue("ZPREPO", "X"); // 선구매필요
			} else {
				insertTable.setValue("ZPREPO", "");
			}

			insertTable.setValue("BRGEW", IBAUtil.getAttrfloatValue(part, "WEIGHT")); // 중량
//			insertTable.setValue("GEWEI", part.getDefaultUnit().toString().toUpperCase()); // 중량 단위
			insertTable.setValue("GEWEI", "G"); // 고정..

			String ZMATLT = SAPHelper.manager.convertSapValue(part, "MAT");
			insertTable.setValue("ZMATLT", ZMATLT); // 재질

			String ZPOSTP = SAPHelper.manager.convertSapValue(part, "FINISH");
			insertTable.setValue("ZPOSTP", ZPOSTP); // 후처리

			String ZDEVND = SAPHelper.manager.convertSapValue(part, "MANUFACTURE");
			insertTable.setValue("ZDEVND", ZDEVND); // 개발공급업체

			idx++;
			params.put("sendResult", true);
			SystemHelper.service.saveSendPartLogger(params);
		}

		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_MAT");
		rtnTable.firstRow();
//			for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
//				Object ZIFSTA = rtnTable.getValue("ZIFSTA");
//				Object ZIFMSG = rtnTable.getValue("ZIFMSG");
//				System.out.println("ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG);
//			}
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
		System.out.println("종료 SAP 인터페이스 - ECO 자재마스터 FUN : ZPPIF_PDM_001");
	}

	@Override
	public void sendSapToEcn(Map<String, Object> params) throws Exception {
		System.out.println("시작 SAP 인터페이스 - ECN 확정인허가일 FUN : ZPPIF_PDM_003");
		String oid = (String) params.get("oid");

		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_003");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);

			JCoParameterList importTable = function.getImportParameterList();
			importTable.setValue("IV_WERKS", "1000"); // 플랜트

			int idx = 1;
			JCoTable insertTable = function.getTableParameterList().getTable("ET_ECN");
			for (Map<String, Object> editRow : editRows) {
				String link_oid = (String) editRow.get("oid");
				String coid = (String) editRow.get("coid");
				String part_oid = (String) editRow.get("poid");
				EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(coid);
				WTPart part = (WTPart) CommonUtil.getObject(part_oid);

				ArrayList<Map<String, String>> countrys = NumberCodeHelper.manager.getCountry();
				for (Map<String, String> country : countrys) {
					String code = country.get("code");
					String sendDate = (String) editRow.get(code + "_date");
					boolean send = (boolean) editRow.get(code + "_isSend");
					if (StringUtil.checkString(sendDate) && !send) {
						PartToSendLink link = PartToSendLink.newPartToSendLink();
						String name = country.get("name");
						link.setEcr(ecr);
						link.setNation(code);
						link.setPart(part);
						link.setEcn(ecn);
						link.setIsSend(true);

						if ("N/A".equalsIgnoreCase(sendDate)) {
							link.setSendDate(DateUtil.convertDate("3000-12-31"));
						} else {
							link.setSendDate(DateUtil.convertDate(sendDate));
						}
						PersistenceHelper.manager.save(link);

						WTUser sessionUser = CommonUtil.sessionUser();
						System.out.println("전송된 ECN = " + ecn.getEoNumber() + ", 자재번호 = " + part.getNumber()
								+ ", 확정인허가일 = " + sendDate + ", 국가 = " + name + "(" + code + ")");

						String v = part.getVersionIdentifier().getSeries().getValue();
						insertTable.insertRow(idx);
						insertTable.setValue("ECNNO", ecn.getEoNumber());
						insertTable.setValue("MATNR", part.getNumber());
						insertTable.setValue("ZEIVR", part.getVersionIdentifier().getSeries().getValue());
						insertTable.setValue("CNTRY", code);

						if ("N/A".equalsIgnoreCase(sendDate)) {
							insertTable.setValue("APRDT", "30001231");
						} else {
							insertTable.setValue("APRDT", sendDate.toString().substring(0, 10).replaceAll("-", ""));
						}

						insertTable.setValue("ENNAM", ecn.getCreatorFullName());
						insertTable.setValue("ENDAT",
								ecn.getCreateTimestamp().toString().substring(0, 10).replaceAll("-", ""));
						insertTable.setValue("TRNAM", sessionUser.getFullName());

//						insertTable.setValue("",v);
						idx++;
					}
				}
				// 진행율 업데이트??
//				EcnHelper.service.update(link_oid);
			}
			function.execute(destination);
			JCoParameterList result = function.getExportParameterList();
			Object r_type = result.getValue("EV_STATUS");
			Object r_msg = result.getValue("EV_MESSAGE");
			System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
			System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
			System.out.println("종료 SAP 인터페이스 - ECN 확정인허가일 FUN : ZPPIF_PDM_003");
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

		System.out.println("ECN SAP SEND END!!");
	}
}
