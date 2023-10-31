package com.e3ps.sap.service;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPDevConnection;
import com.e3ps.sap.dto.SAPBomDTO;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
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
	public void sendERP(EChangeOrder eco) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void loaderBom(String path) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			File file = new File(path);

			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows(); // 시트의 행 개수 가져오기
			DataFormatter df = new DataFormatter();
			// 모든 행(row)을 순회하면서 데이터 가져오기

			HashMap<Integer, WTPart> parentMap = new HashMap<>();
			for (int i = 1; i < rows; i++) {
				Row row = sheet.getRow(i);

				String number = df.formatCellValue(row.getCell(3));
				if (!StringUtil.checkString(number)) {
					System.out.println("값이 없어서 패스 시킨다.");
					continue;
				}

				int level = (int) row.getCell(2).getNumericCellValue();
				double qty = row.getCell(11).getNumericCellValue();
				String name = df.formatCellValue(row.getCell(5));
				String version = df.formatCellValue(row.getCell(6));
				String model = df.formatCellValue(row.getCell(13));
				String dept = df.formatCellValue(row.getCell(14));

				String spec = df.formatCellValue(row.getCell(10));
				String product = df.formatCellValue(row.getCell(16));

				// 지금은 이름...
				String modelCode = SAPHelper.manager.get(model, "MODEL");
				String deptCode = SAPHelper.manager.get(dept, "DEPTCODE");

				String specCode = SAPHelper.manager.get(spec, "SPECIFICATION");
				String productCode = SAPHelper.manager.get(product, "PRODUCTMETHOD");

				System.out.println("version == " + version + ", number=" + number + ", name = " + name + ", model + "
						+ modelCode + ", dept = " + deptCode + ", spec + " + specCode + ", produc = " + productCode);

				WTPart part = create(number, name, version);

				IBAUtil.createIba(part, "string", "MODEL", modelCode);
				IBAUtil.createIba(part, "string", "DEPTCODE", deptCode);
				IBAUtil.createIba(part, "string", "SPECIFICATION", specCode);
				IBAUtil.createIba(part, "string", "PRODUCTMETHOD", productCode);

				WTPart parentPart = parentMap.get(level - 1);
				if (parentPart != null) {
					WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(parentPart,
							(WTPartMaster) part.getMaster());
					QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit("ea");
					usageLink.setQuantity(Quantity.newQuantity(qty, quantityUnit));
					PersistenceServerHelper.manager.insert(usageLink);
				}
				// 만들어진게 부모 파트로 ...
				parentMap.put(level, part);
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
	public WTPart create(String number, String name, String version) throws Exception {
		// 하나의 프로세스상 트랜잭션 하나로 유지시킨다.
		WTPart part = null;
		int idx = version.indexOf(".");
		String first = version.substring(0, idx);
		String end = version.substring(idx + 1);
		try {

			part = SAPHelper.manager.getPart(number.trim(), first, end);
			if (part == null) {

				WTPartMaster master = SAPHelper.manager.getMaster(number);
				if (master != null) {

					part = WTPart.newWTPart();

					WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
					identity.setNumber(number);
					identity.setName(name);
					master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, identity);

					part.setMaster(master);
//					part.setName(name);
//					part.setNumber(number);
					part.setDefaultUnit(QuantityUnit.toQuantityUnit("ea"));
					View view = ViewHelper.service.getView("Design");
					ViewHelper.assignToView(part, view);

					VersionIdentifier vc = VersionIdentifier.newVersionIdentifier(
							MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries", first));
					part.getMaster().setSeries("wt.series.HarvardSeries");
					VersionControlHelper.setVersionIdentifier(part, vc);
					// set iteration as "3"
					Series ser = Series.newSeries("wt.vc.IterationIdentifier", end);
					IterationIdentifier iid = IterationIdentifier.newIterationIdentifier(ser);
					VersionControlHelper.setIterationIdentifier(part, iid);

					Folder folder = FolderHelper.service.getFolder("/Default/PART_Drawing/TEST",
							WCUtil.getWTContainerRef());
					FolderHelper.assignLocation((FolderEntry) part, folder);

					PersistenceHelper.manager.save(part);
				} else {
					part = WTPart.newWTPart();
					part.setName(name);
					part.setNumber(number);
					part.setDefaultUnit(QuantityUnit.toQuantityUnit("ea"));
					View view = ViewHelper.service.getView("Design");
					ViewHelper.assignToView(part, view);

					VersionIdentifier vc = VersionIdentifier.newVersionIdentifier(
							MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries", first));
					part.getMaster().setSeries("wt.series.HarvardSeries");
					VersionControlHelper.setVersionIdentifier(part, vc);
					Series ser = Series.newSeries("wt.vc.IterationIdentifier", end);
					IterationIdentifier iid = IterationIdentifier.newIterationIdentifier(ser);
					VersionControlHelper.setIterationIdentifier(part, iid);

					Folder folder = FolderHelper.service.getFolder("/Default/PART_Drawing/TEST",
							WCUtil.getWTContainerRef());
					FolderHelper.assignLocation((FolderEntry) part, folder);

					PersistenceHelper.manager.save(part);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return part;
	}

	@Override
	public void sendSapToEo(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		// 결재완료 안에서 동작하기에 트랜젝션 제외
		System.out.println("EO SAP SEND START");

		// 자재마스터 전송
		sendToSapEoPart(e, completeParts);
		// BOM 전송
		sendToSapEoBom(e, completeParts);

		// 자재마스터 전송 시작
		System.out.println("EO SAP SEND END");
	}

	/**
	 * EO 전용 BOM 전송 함수
	 */
	private void sendToSapEoBom(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDevConnection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
		System.out.println("SAP BOM INTERFACE START!");

		int seq = 1; // ??
		DecimalFormat df = new DecimalFormat("0000");

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트

		// EO/ECO 헤더 정보 전송
		JCoTable eoTable = function.getTableParameterList().getTable("ET_ECM");

		eoTable.appendRow();
		eoTable.setValue("AENNR8", e.getEoNumber() + df.format(seq)); // 변경번호 12자리??
		eoTable.setValue("ZECMID", "EO"); // EO/ECO 구분
		eoTable.setValue("DATUV", "");
		eoTable.setValue("AEGRU", "초도발행"); // 변경사유 테스트 일단 한줄
		eoTable.setValue("AETXT", "첫줄 테스트"); // 변경 내역 첫줄만 일단 테스트
		eoTable.setValue("AETXT_L", "테스트1<br>테스트2<br>테스트3"); // 변경 내역 전체 내용

		// 완제품으로 품목을 담는다.
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			list.add(root);
		}

		JCoTable bomTable = function.getTableParameterList().getTable("ET_BOM");
		// BOM 전송 한번 돌고 호출하고 하는식???
		int idx = 1;
		for (WTPart part : list) {
			// 완제품에 해당하는 BOM 목록들..
//			System.out.println("root=" + part.getNumber());
			ArrayList<SAPBomDTO> dataList = SAPHelper.manager.getterBomData(part);

			System.out.println("BOM 리스트 항목 개수  = " + dataList.size());

			for (SAPBomDTO dto : dataList) {
				System.out.println("p = " + dto.getParentPartNumber() + ", c = " + dto.getChildPartNumber());
				// System.out.println(dto.toString());
				bomTable.insertRow(idx);
				bomTable.setValue("AENNR8", e.getEoNumber() + df.format(seq)); // 변경번호 12자리?
				bomTable.setValue("SEQNO", df.format(seq)); // 항목번호
				// EO 일경우 NEW 에만
				bomTable.setValue("MATNR_NEW", dto.getParentPartNumber()); // 기존 모품번
				bomTable.setValue("IDNRK_NEW", dto.getChildPartNumber()); // 기존 자품번
//				bomTable.setValue("MATNR_NEW", dto.getNewParentPartNumber()); // 신규 모품번
//				bomTable.setValue("IDNRK_NEW", dto.getNewChildPartNumber()); // 신규 자품번
				bomTable.setValue("MENGE", dto.getQty()); // 수량
				bomTable.setValue("MEINS", dto.getUnit()); // 단위
				seq++;
				idx++;
			}
		}

		function.execute(destination);
		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);

	}

	/**
	 * EO 전용 자재마스터 리스트 전송 함수
	 */
	private void sendToSapEoPart(EChangeOrder e, ArrayList<EOCompletePartLink> completeParts) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDevConnection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		// 완제품으로 하위에 있는 모든 품목을 조회 해서 전송한다
		System.out.println("SAP PART INTERFACE START!");
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for (EOCompletePartLink link : completeParts) {
			String version = link.getVersion();
			WTPart root = PartHelper.manager.getPart(link.getCompletePart().getNumber(), version);
			// 중복 품목 제외를 한다.
			list = SAPHelper.manager.getterSkip(root);
		}

		System.out.println("수집된거=" + list.size());

		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		int idx = 1;
		// 자재는 한번에 넘기고 함수 호출
		for (WTPart part : list) {
			insertTable.insertRow(idx);

			// 샘플로 넣기
			insertTable.setValue("AENNR8", e.getEoNumber()); // 변경번호 8자리
			insertTable.setValue("MATNR", part.getNumber()); // 자재번호
			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
			insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위

			String ZSPEC = IBAUtil.getStringValue(part, "SPECIFICATION");
			insertTable.setValue("ZSPEC", ZSPEC); // 사양

			String ZMODEL = IBAUtil.getStringValue(part, "MODEL");
			insertTable.setValue("ZMODEL", ZMODEL); // Model:프로젝트

			String ZPRODM_CODE = IBAUtil.getStringValue(part, "PRODUCTMETHOD");
			NumberCode ZPRODM_N = NumberCodeHelper.manager.getNumberCode(ZPRODM_CODE, "PRODUCTMETHOD");
			if (ZPRODM_N != null) {
				insertTable.setValue("ZPRODM", ZPRODM_N.getName()); // 제작방법
			} else {
				insertTable.setValue("ZPRODM", ""); // 제작방법
			}

			String ZDEPT = IBAUtil.getStringValue(part, "DEPTCODE");
			insertTable.setValue("ZDEPT", ZDEPT); // 설계부서

			// 샘플링 실제는 2D여부 확인해서 전송
			insertTable.setValue("ZDWGNO", part.getNumber() + ".DRW"); // 도면번호

			String v = part.getVersionIdentifier().getSeries().getValue();
			insertTable.setValue("ZEIVR", v); // 버전
			// 테스트 용으로 전송
			insertTable.setValue("ZPREPO", "X"); // 선구매필요
//			System.out.println("number = " + part.getNumber() + ", version = " + v);
			idx++;
		}

		function.execute(destination);
		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
	}

	@Override
	public void sendSapToEco(EChangeOrder e) throws Exception {
		// 결재완료 안에서 동작하기에 트랜젝션 제외
		System.out.println("ECO SAP SEND START");
	}
}
