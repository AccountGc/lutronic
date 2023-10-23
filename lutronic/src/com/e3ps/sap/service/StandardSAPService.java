package com.e3ps.sap.service;

import java.io.File;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
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
//				double number = row.getCell(3).getNumericCellValue();

				System.out.println("number=" + number + ", name = " + name);

				WTPart part = create(number, name, version);

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

			part = PartHelper.manager.getPart(number, first);
			if (part == null) {

				part = WTPart.newWTPart();
				part.setName(name);
				part.setNumber(number);
				part.setDefaultUnit(QuantityUnit.toQuantityUnit("ea"));
				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(part, view);

				VersionIdentifier vc = VersionIdentifier
						.newVersionIdentifier(MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries", first));
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

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return part;
	}
}
