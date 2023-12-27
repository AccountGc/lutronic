package com.e3ps.part.service;

import jxl.write.WritableWorkbook;
import wt.part.WTPart;

public interface PartExcelService {

	WritableWorkbook createExcel(WritableWorkbook workbook, String sheetName, WTPart part, String desc, Object obj) throws Exception;

}
