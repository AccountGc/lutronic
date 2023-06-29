package com.e3ps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.iba.AttributeData;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.development.devActive;
import com.e3ps.org.Department;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.service.RohsQueryHelper;

public class Test {
	public static void main(String[] args)  {
		try {
			//createDept();
			//deleteMaterial();
			//test2();
			//test3();
			writeExcel();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	// 파일복사
	private static File copyFile(File source, File dest) {
		long startTime = System.currentTimeMillis();

		int count = 0;
		long totalSize = 0;
		byte[] b = new byte[128];

		FileInputStream in = null;
		FileOutputStream out = null;
		// 성능향상을 위한 버퍼 스트림 사용
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try {
			in = new FileInputStream(source);
			bin = new BufferedInputStream(in);

			out = new FileOutputStream(dest);
			bout = new BufferedOutputStream(out);
			while ((count = bin.read(b)) != -1) {
				bout.write(b, 0, count);
				totalSize += count;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {// 스트림 close 필수
			try {
				if (bout != null) {
					bout.close();
				}
				if (out != null) {
					out.close();
				}
				if (bin != null) {
					bin.close();
				}
				if (in != null) {
					in.close();
				}

			} catch (IOException r) {
				// TODO: handle exception
				//System.out.println("close 도중 에러 발생.");
			}
		}
		
		return dest;
	 }

	public static void writeExcel() throws Exception {
		String oid = "com.e3ps.change.EChangeOrder:426419";
		
		File orgFile = new File("D:/workspace/LUTRONIC/src/com/e3ps/change/eo.xlsx");
		
		File newFile = copyFile(orgFile, new File("D:/workspace/LUTRONIC/src/com/e3ps/change/eo_copy2.xlsx"));
		
		FileInputStream file = new FileInputStream(newFile);
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		workbook.setSheetName(0, "SheetName");
		
		//System.out.println("쓰기 시작....." + POIUtil.getSheetRow(sheet));

		//EChangeOrder eo = (EChangeOrder)CommonUtil.getObject(oid);
		//EOData data = new EOData(eo);
		
		Font font = workbook.createFont();
		font.setColor(Font.COLOR_RED);
		
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		// 문서번호 (9-D ,8-3)
		XSSFCell documentNumber = sheet.getRow(8).getCell(3);
		documentNumber.setCellValue("문서번호");
		documentNumber.setCellStyle(style);
		
		// 작성자 (17-K, 16-10)
		XSSFCell creator = sheet.getRow(16).getCell(10);
		creator.setCellValue("작성자");
		
		// 검토자 (17-M, 16-12)
		XSSFCell chk = sheet.getRow(16).getCell(12);
		chk.setCellValue("검토자");
		
		// 승인자 (17-O, 16-14)
		XSSFCell approver = sheet.getRow(16).getCell(14);
		approver.setCellValue("승인자");
		
		// 작성일 (18-K, 17-10)
		XSSFCell creatDate = sheet.getRow(17).getCell(10);
		creatDate.setCellValue("작성일");
		
		// 검토일 (18-M, 17-12)
		XSSFCell chkDate = sheet.getRow(17).getCell(12);
		chkDate.setCellValue("검토일");
		
		// 승인일 (18-O, 17-14)
		XSSFCell approveDate = sheet.getRow(17).getCell(14);
		approveDate.setCellValue("승인일");
		
		// 문서 번호 (29-D, 28-3)
		XSSFCell documentNumber2 = sheet.getRow(28).getCell(3);
		documentNumber2.setCellValue("문서번호2");
		
		// 제목 (30-D, 29-3)
		XSSFCell documentName = sheet.getRow(29).getCell(3);
		documentName.setCellValue("제목");
		
		// 작성일 (32-D, 31-3)
		XSSFCell creatDate2 = sheet.getRow(31).getCell(3);
		creatDate2.setCellValue("작성일2");
		
		// 작성부서 (32-K, 31-10)
		XSSFCell createDept = sheet.getRow(31).getCell(10);
		createDept.setCellValue("작성부서");
		
		// 승인일 (33-D, 32-3)
		XSSFCell approveDate2 = sheet.getRow(32).getCell(3);
		approveDate2.setCellValue("승인일2");
		
		// 작성자 (33-K, 32-10)
		XSSFCell creator2 = sheet.getRow(32).getCell(10);
		creator2.setCellValue("작성자");
		
		// 제품명 (35-D, 34-3)
		XSSFCell partName = sheet.getRow(34).getCell(3);
		partName.setCellValue("제품명");
		
		// 완제품 품번 (36-D, 35-3)
		XSSFCell completePartName = sheet.getRow(35).getCell(3);
		completePartName.setCellValue("완제품 품번");
		
		// 제품 설계 개요(40-B, 39-2)
		XSSFCell eoCommentA = sheet.getRow(39).getCell(1);
		eoCommentA.setCellValue("제품 설계 개요");
		
		/**
		 *  이관 문건 시작
		 */
		
		// NO (44-B, 43-2)
		XSSFCell docNo = sheet.getRow(43).getCell(1);
		docNo.setCellValue("NO");
		
		// 문서번호 (44-C, 43-3)
		XSSFCell docNumber = sheet.getRow(43).getCell(2);
		docNumber.setCellValue("문서번호");
		
		// 문서명 (44-F, 43-5)
		XSSFCell docName = sheet.getRow(43).getCell(5);
		docName.setCellValue("문서명");
		
		// 문서구분 (44-H, 43-7)
		XSSFCell docType = sheet.getRow(43).getCell(7);
		docType.setCellValue("문서구분");
		
		/**
		 *  이관 문건 끝
		 */
		
		
		
		
		
		if(false){
			
			// 특기사항 (47-F, 46-5)
			XSSFCell eoCommentB = sheet.getRow(46).getCell(5);
			eoCommentB.setCellValue("특기사항");
			
			// 기타사항 (49-F, 48-5)
			XSSFCell eoCommentC = sheet.getRow(48).getCell(5);
			eoCommentC.setCellValue("기타사항");
			
			/**
			 *  첨부파일 시작
			 */
			
			// NO (53-B, 52-2)
			XSSFCell attchNo = sheet.getRow(52).getCell(1);
			attchNo.setCellValue("NO");
			
			// 파일명 (53-C, 52-3)
			XSSFCell attchName = sheet.getRow(52).getCell(2);
			attchName.setCellValue("파일명");
			
			/**
			 *  첨부파일 끝
			 */
			
			/**
			 * 	결재자 의견 시작
			 */
			
			// 이름 (57-B, 56-2)
			XSSFCell name = sheet.getRow(56).getCell(1);
			name.setCellValue("이름");
			
			// 날짜 (57-D, 56-3)
			XSSFCell date = sheet.getRow(56).getCell(3);
			date.setCellValue("날짜");
			
			// 내용 (57-G, 56-6)
			XSSFCell description = sheet.getRow(56).getCell(6);
			description.setCellValue("내용");
			
			/**
			 *  결재자 의견 끝
			 */
			
		}
		
		//System.out.println("쓰기 종료....." + POIUtil.getSheetRow(sheet));
		
		file.close();
		
		FileOutputStream outFile = new FileOutputStream(newFile);
		workbook.write(outFile);
		outFile.close();
		
		workbook.close();
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void test3() throws Exception {
		String oid = "wt.part.WTPart:420337";
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		IBAHolder iba = (IBAHolder)part;
		
		//System.out.println(iba);
		
	} 
	
	
	public static void test2() throws Exception {
		String[] ibas = new String[]{"part","cad","document","mold"};
		
		for(String iba : ibas) {
			//System.out.println("===================================================  " + iba + " ========================================");
			Hashtable<String, AttributeData> table = IBAUtil.getIBAAttributes(iba);
			//System.out.println(table.size());
	
			Enumeration<String> eu = table.keys();
			while (eu.hasMoreElements()) {
	            String key = (String) eu.nextElement();
	            AttributeData value = (AttributeData)table.get(key);
	            AttributeDefDefaultView aview = value.aview;
	            //System.out.println(key + " +++++++  " + value.displayName + " ======  " + value.dataType + " ////////////////  " + value.aview);
			}
		}
		
	}
	
	
	public static  void deleteMaterial() throws Exception {
		String oid = "com.e3ps.development.devActive:314141";
		
		devActive ac = (devActive)CommonUtil.getObject(oid);
		
		PersistenceHelper.manager.delete(ac);
		
	}
	
	
	public static void test1() throws Exception {
		String oid = "com.e3ps.rohs.ROHSMaterial:344271";
		List<ROHSContHolder> holderList = RohsQueryHelper.service.getROHSContHolder(oid);
		//System.out.println(holderList.size());
	}
	

	public static void partDhtmlXTreeSetting(PartTreeData parent, Map<String, Object> map, int rowNum) throws Exception {

		ArrayList<PartTreeData> childList = parent.children;
		ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		for (PartTreeData child : childList) {
			rowNum++;
			Map<String, Object> childMap = getDhtmlXPartData(child, rowNum);
			dataList.add(childMap);
			partDhtmlXTreeSetting(child, childMap, rowNum);
		}
		if (childList.size() > 0) {
			map.put("rows", dataList);
		}
	}

	public static Map<String, Object> getDhtmlXPartData(PartTreeData bomPart, int rowNum) throws Exception {

		Map<String, Object> partData = new HashMap<String, Object>();
		ArrayList<Object> data = new ArrayList<Object>();
		String oid = CommonUtil.getOIDString(bomPart.part);

		String partNo = bomPart.number;
		int level = bomPart.level;
		String name = bomPart.name;

		data.add(partNo);
		data.add(level);
		data.add(name);

		// oid
		partData.put("id", rowNum + "_" + oid);
		if (bomPart.children.size() > 0) {
			partData.put("open", "1");
		}

		// cell values
		partData.put("data", data);

		return partData;
	}
	
	
	public static void test() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = "wt.part.WTPart:326718";
		
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		QuerySpec spec = new QuerySpec();
		
		int idx = spec.addClassList(WTPartUsageLink.class, false);
		spec.setAdvancedQueryEnabled(true);
		
		spec.appendJoin(idx, "roleA", part);
		
		QueryResult result = PersistenceServerHelper.manager.query(spec);
		
		//System.out.println(result);
		
		/*
		String oid="com.e3ps.development.devMaster:308027";
		
		QuerySpec spec = new QuerySpec();
		
		int idx = spec.addClassList(devActive.class, false);
		spec.setAdvancedQueryEnabled(true);
		
		//spec.appendSelect(new ClassAttribute(devActive.class, devActive.WORKER_REFERENCE + ".key.id"), true);
		
		spec.appendWhere(new SearchCondition(devActive.class, devActive.MASTER_REFERENCE + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)), new int[] {idx});

		spec.appendGroupBy(new ClassAttribute(devActive.class, devActive.WORKER_REFERENCE + ".key.id"), idx, true);
		
		//System.out.println(spec);
		
		QueryResult result = PersistenceServerHelper.manager.query(spec);
		
		while(result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			int totalCount = ((BigDecimal) obj[0]).intValue();
			//System.out.println(totalCount);
		}
		
		

			query.appendOrderBy(new OrderBy(new ClassAttribute(devMaster.class, devMaster.MODEL), true), new int[] { idx_m });
			query.appendOrderBy(new OrderBy(new ClassAttribute(devActive.class, devActive.CREATE_TIMESTAMP), true), new int[] { idx_a });
		
		int idx_p = spec.addClassList(WTPart.class, false);
		int idx_d = spec.addClassList(WTDocument.class, false);
		int idx_l = spec.addClassList(WTPartDescribeLink.class, false);
		
		ClassAttribute part = new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id");
		ClassAttribute link_part = new ClassAttribute(WTPartDescribeLink.class, WTPartDescribeLink.ROLE_AOBJECT_REF + ".key.id");
		
		ClassAttribute link_document = new ClassAttribute(WTPartDescribeLink.class, WTPartDescribeLink.ROLE_BOBJECT_REF + ".key.id");
		ClassAttribute document = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
		
		spec.appendWhere(new SearchCondition(part, SearchCondition.EQUAL, link_part), new int[] {idx_p, idx_l});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(document, SearchCondition.EQUAL, link_document), new int[] {idx_d, idx_l});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)),new int[] {idx_p});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, "$$APDocument"), new int[] {idx_d});
		spec.setAdvancedQueryEnabled(true);
		ClassAttribute ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
		SQLFunction count = SQLFunction.newSQLFunction(SQLFunction.COUNT, ca);
		spec.appendSelect(count, new int[] {idx_d}, false);
		
		spec.appendJoin(idx_l, "roleA", idx_p);
		spec.appendJoin(idx_l, "roleB", idx_d);
		spec.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(oid)),new int[] {idx_p});
		
		//System.out.println("Query >>>>  " + spec);
		
		QueryResult result = PersistenceServerHelper.manager.query(spec);
		//System.out.println(result.size());
		 */
		
	}
	
	public static void createDept() throws Exception {
		Department dept = new Department();
		dept.setCode("ROOT");
		dept.setName("intellian");
		dept.setSort(0);
		
		Department root = (Department)PersistenceHelper.manager.save(dept);
		
		Department test = Department.newDepartment();
		test.setCode("TEST1");
		test.setName("Test1");
		test.setSort(1);
		test.setParent(root);
		
		test = (Department)PersistenceHelper.manager.save(test);
		
		Department ds1 = Department.newDepartment();
		ds1.setCode("DS1");
		ds1.setName("영업 1팀");
		ds1.setSort(1);
		ds1.setParent(test);
		
		PersistenceHelper.manager.save(ds1);
		
		Department ds2 = Department.newDepartment();
		ds2.setCode("DS2");
		ds2.setName("영업 2팀");
		ds2.setSort(2);
		ds2.setParent(test);
		
		PersistenceHelper.manager.save(ds2);
		
		Department test2 = Department.newDepartment();
		test2.setCode("TEST2");
		test2.setName("Test2");
		test2.setSort(2);
		test2.setParent(root);
		
		test2 = (Department)PersistenceHelper.manager.save(test2);
		
		Department ed1 = Department.newDepartment();
		ed1.setCode("ED1");
		ed1.setName("설계 1팀");
		ed1.setSort(1);
		ed1.setParent(test2);
		
		PersistenceHelper.manager.save(ed1);
		
		Department ed2 = Department.newDepartment();
		ed2.setCode("ED2");
		ed2.setName("설계 2팀");
		ed2.setSort(2);
		ed2.setParent(test2);
		
		PersistenceHelper.manager.save(ed2);
		
		Department test3 = Department.newDepartment();
		test3.setCode("TEST3");
		test3.setName("Test3");
		test3.setSort(3);
		test3.setParent(root);
		
		test3 = (Department)PersistenceHelper.manager.save(test3);
		
		Department de1 = Department.newDepartment();
		de1.setName("개발 1팀");
		de1.setCode("DE1");
		de1.setSort(1);
		de1.setParent(test3);
		
		PersistenceHelper.manager.save(de1);
		
		Department test4 = Department.newDepartment();
		test4.setCode("TEST4");
		test4.setName("유지보수");
		test4.setSort(4);
		test4.setParent(root);
		
		PersistenceHelper.manager.save(test4);
	}
}
