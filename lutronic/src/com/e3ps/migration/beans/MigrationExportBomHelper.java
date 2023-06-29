package com.e3ps.migration.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.e3ps.common.excelDown.beans.ExcelDownData;
import com.e3ps.common.util.AuthHandler;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.beans.BomData;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.ptc.core.foundation.associativity.common.BOMHelper;

public class MigrationExportBomHelper {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String userId = "wcadmin";
			wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
			methodServer.setAuthenticator(AuthHandler.getMethodAuthenticator(userId));
			
			String number =StringUtil.checkNull(args[0]);
			//String number ="1010100000";
			if(number.length()==0){
				
				//System.out.println(" 품목 번호를 입력해 주세요");
				return;
			}
			
			exportBom(number);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void exportBom(String number) throws Exception{
		
		WTPart part = PartHelper.service.getPart(number);
		List<PartTreeData> dataList = new ArrayList<PartTreeData>();
		
		
		// 엑셀 파일명 설정
		String fileUrl = WTProperties.getServerProperties().getProperty("wt.home");
		fileUrl = fileUrl +"\\loadFiles\\lutronic\\migration\\"+number+".xls";
		File file = new File(fileUrl);
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		
		WritableSheet sheet= createExcelSheet(workbook);
		List<BomData> listData = new ArrayList<BomData>();
		listData = getBOM(part, listData);
		
		
		int row = 1;
		for(BomData data : listData){
			
			
			String parent = data.parent;
			String child  = data.child;
			double amount =data.amount;
			
			sheet.addCell(new Label(0, row, parent)); 
			sheet.addCell(new Label(1, row, child));
			sheet.addCell(new Label(2, row, String.valueOf(amount)));
			row++;
		}
		
		workbook.write();
        workbook.close();
	}
	
	public static  List<BomData> getBOM(WTPart part,List<BomData> listData){
		
		try{
			List<Object[]> list = BomSearchHelper.service.descentLastPart(part,getView(),null);
			
			for(Object[] ob : list){
				
				
				WTPartUsageLink linko =(WTPartUsageLink) ob[0];
				WTPart pPart = (WTPart)linko.getRoleAObject();
				WTPart cPart = (WTPart)ob[1];
				BomData data = new BomData();
				data.setParent(pPart.getNumber());
				data.setChild(cPart.getNumber());
				data.setAmount(linko.getQuantity().getAmount());
				//System.out.println(pPart.getNumber() + ":"+cPart.getNumber() + linko.getQuantity().getAmount());
				listData.add(data);
				listData = getBOM(cPart,listData);
							
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return listData;
		
	}
	
	private static View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}
	
	private static WritableSheet createExcelSheet(WritableWorkbook workbook) throws Exception {
		
		int row = 0;
		
		WritableSheet sheet = workbook.createSheet("목록", 1);
        WritableCellFormat titleformat = JExcelUtil.getCellFormat(Alignment.CENTRE, Colour.LIGHT_GREEN);
        WritableCellFormat cellformat = new WritableCellFormat();
        cellformat.setAlignment(Alignment.CENTRE);
		
		int i=0;
		sheet.setColumnView(0, 20);
		sheet.addCell(new Label(0, row, "모품목", titleformat));
		
		sheet.setColumnView(1, 20);
		sheet.addCell(new Label(1, row, "자품목", titleformat));
		
		sheet.setColumnView(2, 5);
		sheet.addCell(new Label(2, row, "수량", titleformat));
		
		return sheet;
	}

}
