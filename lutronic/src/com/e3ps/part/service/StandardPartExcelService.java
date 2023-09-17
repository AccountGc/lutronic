package com.e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import wt.part.WTPart;
import wt.services.StandardManager;
import wt.vc.baseline.Baseline;
import wt.vc.views.View;

import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.dto.ExcelData;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;

public class StandardPartExcelService extends StandardManager implements PartExcelService {

	private static int excelRow;
	
	public static StandardPartExcelService newStandardPartExcelService() throws Exception {
		final StandardPartExcelService instance = new StandardPartExcelService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public WritableWorkbook createExcel(WritableWorkbook workbook, String sheetName, WTPart part, String desc, Object obj) throws Exception {
		WritableSheet sheet2 = workbook.createSheet(sheetName + "1", 0);
		printBomData(sheet2, part, desc, obj);
		return workbook;
	}
	
	public void printBomData(WritableSheet sheet, WTPart part, String desc, Object obj) throws Exception {
		if (obj == null) {
			return;
		}

		excelRow = 1;
		Vector vec = new Vector();
		
		//System.out.println("###	obj	== " + obj);
		
		//System.out.println("###	part	== "+  part);
		
		printBomExcel(vec, part, 0, desc, obj, "");

		int max = 0;
		for (int i = 0; i < vec.size(); i++) {
			ExcelData data = (ExcelData) vec.get(i);
			
			if (data.level > max) {
				max = data.level;
			}
		}

		printBomTitle(sheet, max);
		int rowCount = 0;
		WTPart cPart = null;
		ArrayList<WTPart> listPart = new ArrayList<WTPart>();
		for (int i = 0; i < vec.size(); i++) {
			ExcelData data = (ExcelData) vec.get(i);
			
			
			/*
			if(PartUtil.isChange(data.part.getNumber())){
				WTPart tmp = data.part;
				getLIstPart(listPart, tmp);
				continue;
			}else{
				for (int m = 0; m < listPart.size(); m++) {
					cPart = listPart.get(m);
					if(null!=cPart && cPart.getNumber().equals(data.part.getNumber())){
						//System.out.println(cPart.getNumber());
						continue a;
					}
				}
			}
			*/
			/*for (int m = 0; m < listPart.size(); m++) {
				parentPart = listPart.get(m);
				//System.out.println("sonPart="+data.part.getNumber()+"\tParentPart = "+parentPart.getNumber());
			}*/
			printRow(sheet, data.part, data.level, data.quantity, rowCount + 1, data.location, max);
			setColumnView(sheet, data.level, max);
			rowCount++;
			
		}
	}
	public static ArrayList<WTPart> getLIstPart(ArrayList<WTPart> listPart,WTPart tmp) throws Exception{
		BomBroker bomBroker = new BomBroker();
		PartTreeData partTreeData = bomBroker.getTree(tmp, true, null);
			for (int j = 0; j < partTreeData.children.size(); j++) {
				PartTreeData tree = (PartTreeData)partTreeData.children.get(j);
				   listPart.add(tree.part);
				   getLIstPart(listPart, tree.part);
			}
		return listPart;
	}
	@SuppressWarnings("rawtypes")
	public static void printBomExcel(Vector vec, WTPart part, int level, String desc, Object obj, String location) throws Exception {
		/*
		ExcelData data = new ExcelData();
		data.level = level++;
		data.part = part;
		data.quantity = quantity;
		data.location = location;
		vec.add(data);

		WTPartConfigSpec spec = WTPartConfigSpec.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, spec);
		
		//System.out.println("###	qr.size	==	"+qr.size());
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			if (obj[1] instanceof WTPart) {
				WTPart child = (WTPart) obj[1];
				if (child.getView() != null && view.getName().equals(child.getViewName())){
					//System.out.println("##	number	=="+child.getNumber()+"	//	link.getQuantity().getAmount()	==	"+link.getQuantity().getAmount());
					printBomExcel(vec, child, level, link.getQuantity().getAmount(), view, child.getLocation());
					
				}
			}
		}
		*/
		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		PartTreeData root = null;
		//############################################################################
		if(obj instanceof Baseline){
			root = broker.getTree(part, !"false".equals(desc), (Baseline) obj, null);
		}else{
			root = broker.getTree(part, !"false".equals(desc), null, (View) obj);
		}
		//############################################################################
		broker.setHtmlForm(root, result);
		
		for(int i=0; i< result.size(); i++){
			PartTreeData pData = (PartTreeData)result.get(i);
			//엑셀 다운로드시 더미 품목 제외 2017.03.21 tsuam
			if(pData.link != null){
				WTPart pPart = (WTPart)pData.link.getRoleAObject();
				boolean isChange = (PartUtil.isChange(pPart.getNumber()) || PartUtil.isChange(pData.number) );
				//System.out.println(pPart.getNumber() +" : " + PartUtil.isChange(pData.number) +" : " + isChange);
				if( isChange ){
					continue;
				}
			}
			
			ExcelData data = new ExcelData();
			data.level = pData.level;
			data.part = pData.part;
			data.quantity = pData.quantity;
			vec.add(data);
		}
		
	}
	
	public void setColumnView(WritableSheet sheet, int level, int maxLevel) throws Exception {
		sheet.setColumnView(0, new Integer(10));
		for (int i = 0; i <= maxLevel; i++) {
			if (i == level) {
				sheet.setColumnView(i, new Integer(10));
			} else {
				sheet.setColumnView(i, new Integer(10));
			}
		}

		int idx = maxLevel + 1;

		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(15));
		sheet.setColumnView(idx++, new Integer(20));
	}

	public void printRow(WritableSheet sheet, WTPart part, int level, double quantity, int row, String location, int maxLevel) throws Exception {
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, null);
		sheet.addCell(new Label(0, row, String.valueOf(level)));
		for (int i = 0; i <= maxLevel; i++) {
			if (i == level) {
				sheet.addCell(new Label(i, row, String.valueOf(level), format));
			} else {
				sheet.addCell(new Label(i, row, "", format));
			}
		}
		
	
		
		int idx = maxLevel + 1;
		PartData data = new PartData(part);
		
		HashMap partAttr = IBAUtil.getAttributes(part);
		
		//Part No (필수)
		sheet.addCell(new Label(idx++, row, data.number, format));
		
		//Part Name (필수)
		sheet.addCell(new Label(idx++, row, data.name, format));
		
		//상태
		sheet.addCell(new Label(idx++, row, data.getLifecycle(), format));
		
		//버전 (필수)
		sheet.addCell(new Label(idx++, row, data.version + "." + data.iteration + "(" + data.getViewName() + ")", format));
		
		//단위 (필수)
		sheet.addCell(new Label(idx++, row, data.getUnit(), format));
		
		//수량
		sheet.addCell(new Number(idx++, row, quantity, format));
		
		//순중량
		sheet.addCell(new Label(idx++, row, data.getWeight(), format));
		
		//중량 단위
		sheet.addCell(new Label(idx++, row, "g", format));
		
		//재질 코드:명칭
		String mat = PartUtil.getERPAttributeValue(IBAKey.IBA_MAT, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_MAT)));
		sheet.addCell(new Label(idx++, row,mat , format));
		
		//사양 (SPECIFICATION)
		sheet.addCell(new Label(idx++, row, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_SPECIFICATION)), format));
		
		//후처리
		String finish = PartUtil.getERPAttributeValue(IBAKey.IBA_FINISH, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_FINISH)));
		sheet.addCell(new Label(idx++, row, finish, format));
		
		//프로젝트 코드
		String model = PartUtil.getERPAttributeValue(IBAKey.IBA_MODEL, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_MODEL)));
		sheet.addCell(new Label(idx++, row, model, format));
		
		//제작 방법
		String method = PartUtil.getERPAttributeValue(IBAKey.IBA_PRODUCTMETHOD, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_PRODUCTMETHOD)));
		sheet.addCell(new Label(idx++, row, method, format));
		
		//부서 코드
		String departCode = PartUtil.getERPAttributeValue(IBAKey.IBA_DEPTCODE, StringUtil.checkNull((String)partAttr.get(IBAKey.IBA_DEPTCODE)));
		sheet.addCell(new Label(idx++, row, departCode, format));
		
		HashMap<String, String> numberMap = PartUtil.getPartNumberGoup(data.number);
		
		//map.put("PARTTYPE1", no1);
		//부품 분류: 코드
		sheet.addCell(new Label(idx++, row, StringUtil.checkNull(numberMap.get("PARTTYPE1")), format));
		
		//대분류 : 코드
		sheet.addCell(new Label(idx++, row, StringUtil.checkNull(numberMap.get("PARTTYPE2")), format));
		
		//중분류 : 코드
		sheet.addCell(new Label(idx++, row, StringUtil.checkNull(numberMap.get("PARTTYPE3")), format));
	
			
	}

	private void printBomTitle(WritableSheet sheet, int maxLevel) throws Exception {
		int idx = 0;
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, Colour.LIGHT_GREEN);
		for (int i = 0; i <= maxLevel; i++) {
			sheet.addCell(new Label(idx++, 0, "Level" + i, format));
		}

		WritableCellFormat format2 = getCellFormat(Alignment.CENTRE, null);
		sheet.addCell(new Label(idx++, 0, "Part No", format2));
		sheet.addCell(new Label(idx++, 0, "Part Name", format2));
		sheet.addCell(new Label(idx++, 0, "상태", format2));
		sheet.addCell(new Label(idx++, 0, "버전", format2));
		sheet.addCell(new Label(idx++, 0, "단위", format2));
		sheet.addCell(new Label(idx++, 0, "수량", format2));
		
		//루트로닉 추가
		sheet.addCell(new Label(idx++, 0, "순중량", format2));
		sheet.addCell(new Label(idx++, 0, "중량단위 (g)", format2));
		sheet.addCell(new Label(idx++, 0, "재질(기본자재)", format2));
		sheet.addCell(new Label(idx++, 0, "사양(SPECIFICATION)", format2));
		sheet.addCell(new Label(idx++, 0, "후처리", format2));
		sheet.addCell(new Label(idx++, 0, "프로젝트", format2));
		sheet.addCell(new Label(idx++, 0, "제작방법", format2));
		sheet.addCell(new Label(idx++, 0, "부서", format2));
		sheet.addCell(new Label(idx++, 0, "부품분류", format2));
		sheet.addCell(new Label(idx++, 0, "대분류", format2));
		sheet.addCell(new Label(idx++, 0, "중분류", format2));
		
		
	}

	private WritableCellFormat getCellFormat(Alignment alignment, Colour color) throws Exception {
		WritableCellFormat format = null;
			format = new WritableCellFormat();
			if (color != null)
				format.setBackground(color);
			format.setBorder(Border.ALL, BorderLineStyle.THIN);
			if (alignment != null)
				format.setAlignment(alignment);
		return format;
	}
}
