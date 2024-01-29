package com.e3ps.common.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aspose.cells.Cell;
import com.aspose.cells.Range;
import com.aspose.cells.Row;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

public class POIUtil {

	/**
	 * HSSFWorkbook 객체 가져오기
	 * 
	 * @param file
	 * @return HSSFWorkbook
	 */
	public static XSSFWorkbook getWorkBook(File file) {
		XSSFWorkbook workbook = null;
		try {

			if (file == null) {
				return null;
			}

			// POIFSFileSystem fs = null;
			// fs = new POIFSFileSystem(new FileInputStream(file));
			// workbook = new XSSFWorkbook(fs);
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	public static XSSFWorkbook getWorkBook(String filePath) {
		XSSFWorkbook workbook = null;
		try {
			File file = new File(filePath);
			if (file == null) {
				return null;
			}

			// POIFSFileSystem fs = null;
			// fs = new POIFSFileSystem(new FileInputStream(file));
			// workbook = new XSSFWorkbook(fs);
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	/**
	 * sheet명으로 sheet 가져 오기
	 * 
	 * @param workbook
	 * @param sheetName
	 * @return HSSFSheet
	 */
	public static XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
		XSSFSheet sheet = workbook.getSheet(sheetName);
		return sheet;
	}

	/**
	 * sheet index로 sheet 가져 오기
	 * 
	 * @param workbook
	 * @param sheetNo
	 * @return HSSFSheet
	 */
	public static XSSFSheet getSheet(XSSFWorkbook workbook, int sheetNo) {
		XSSFSheet sheet = workbook.getSheetAt(sheetNo);
		return sheet;
	}

	/**
	 * sheet row의 수
	 * 
	 * @param sheet
	 * @return
	 */
	public static int getSheetRow(XSSFSheet sheet) {

		return sheet.getPhysicalNumberOfRows();
	}

	/**
	 * row의 idx의 값
	 * 
	 * @param row
	 * @param idx
	 * @return
	 */
	public static String getRowStringValue(XSSFRow row, int idx) {

		String value = "";
		try {
			XSSFCell cell = (XSSFCell) row.getCell(idx);

			// value = cell.getStringCellValue().trim();
			value = getCellValue(cell);
		} catch (IllegalStateException e) {
			// double aa = row.getCell(idx).getNumericCellValue();
			// value = Double.toString(aa);
			row.getCell(idx).setCellType(1);
			value = row.getCell(idx).getStringCellValue().trim();
		} catch (NullPointerException e) {
			value = "";
		}

		return value;
	}

	/**
	 * row의 idx의 값
	 * 
	 * @param row
	 * @param idx
	 * @return
	 */
	public static String getRowStringFomularValue(XSSFRow row, int idx) {
		String value = "";
		try {
			XSSFCell cell = (XSSFCell) row.getCell(idx);
			value = cell.getStringCellValue().trim();
			// value = getCellValue(evaluator,cell);
		} catch (IllegalStateException e) {
			// double aa = row.getCell(idx).getNumericCellValue();
			// value = Double.toString(aa);
			row.getCell(idx).setCellType(1);
			value = row.getCell(idx).getStringCellValue().trim();
		} catch (NullPointerException e) {
			value = "";
		}

		return value;
	}

	public static String getCellValue(/* FormulaEvaluator formulaEval, */ XSSFCell cell) {

		String cellString = "";

		if (cell != null) {
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
				cellString = cell.getStringCellValue().trim();
			else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					cellString = new SimpleDateFormat("yyyy-MM-dd").format(date);
				} else
					cellString = String.valueOf(cell.getNumericCellValue());
			}
			/*
			 * else { CellValue evaluate = formulaEval.evaluate(cell); if( evaluate != null
			 * ) cellString = evaluate.formatAsString(); }
			 */
		}

		return cellString.trim();
	}

	public static String getCellValue(FormulaEvaluator formulaEval, XSSFCell cell) {

		String cellString = "";

		if (cell != null) {
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
				cellString = cell.getStringCellValue().trim();
			else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					cellString = new SimpleDateFormat("yyyy-MM-dd").format(date);
				} else
					cellString = String.valueOf(cell.getNumericCellValue());
			} else {
				CellValue evaluate = formulaEval.evaluate(cell);
				if (evaluate != null)
					cellString = evaluate.formatAsString();
			}
		}

		return cellString.trim();
	}

	public static double getRowNumValue(XSSFRow row, int idx) {
		return row.getCell(idx).getNumericCellValue();
	}

	/**
	 * Row 복사
	 * 
	 * @param workbook
	 * @param worksheet
	 * @param sourceRowNum
	 * @param count
	 */
	public static void copyRow(Workbook workbook, Worksheet worksheet, int sourceRowNum, int count) {
		try {
			for (int a = 0; a < count; a++) {
				// Get the source row
				Row sourceRow = worksheet.getCells().getRow(sourceRowNum);

				// Insert a new row
				worksheet.getCells().insertRows(sourceRowNum + 1, 1, true);

				// Get the new row
				Row newRow = worksheet.getCells().getRow(sourceRowNum + 1);

				// Loop through source columns to add to the new row
				for (int i = 0; i <= sourceRow.getLastCell().getColumn() : -1; i++) {
					// Grab a copy of the old/new cell
					Cell oldCell = sourceRow.getCellOrNull(i);
					Cell newCell = newRow.getCellOrNull(i);

					// If the old cell is null, jump to the next cell
					if (oldCell == null || newCell == null) {
						continue;
					}

					// Copy style from old cell and apply to the new cell
					Style newCellStyle = workbook.createStyle();
					newCellStyle.copy(oldCell.getStyle());
					newRow.get(i).setStyle(newCellStyle);


                    newCell.putValue(oldCell.getStringValue()); // 여기에서 값을 설정하도록 수정

				}

				// If there are any merged regions in the source row, copy to the new row
			       for (int i = 0; i < worksheet.getCells().getMergedCells().size() ; i++) {
	                    Range mergedCell = (Range)worksheet.getCells().getMergedCells().get(i);
	                    if (mergedCell.getFirstRow() == sourceRow && mergedCell.getFirstColumn() == 0) {
	                        Range newMergedCell = worksheet.getCells().createRange(newRow, newRow.getRow() + mergedCell.getRowCount() - 1,
	                                mergedCell.getFirstColumn(), mergedCell.getl());
	                        worksheet.getCells().addMergedRegion(newMergedCell);
	                    }
	                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String filePath = "D:\\TestExcel1.xlsx";
		File file = new File(filePath);
		XSSFWorkbook workbook = getWorkBook(file);

		XSSFSheet sheet = getSheet(workbook, 0);

		// System.out.println("sheet.getSheetName() : "+sheet.getSheetName());

		int rows = getSheetRow(sheet);

		for (int i = 2; i < rows; i++) {
			XSSFRow row = sheet.getRow(i);
			int cell = row.getFirstCellNum();

			int lcell = row.getLastCellNum();

			for (int j = cell; j < lcell; j++) {
				// System.out.println("JJJJJ >>>> " + j + " :::::: " + getRowStringValue(row,
				// j));
			}

			String partType = getRowStringValue(row, cell++);

			String part1 = getRowStringValue(row, cell++);
			String part2 = getRowStringValue(row, cell++);
			String part3 = getRowStringValue(row, cell++);
			String seq = getRowStringValue(row, cell++);
			String customer = getRowStringValue(row, cell++);
			String matCode = getRowStringValue(row, cell++);
			String matName = getRowStringValue(row, cell++);

			// System.out.println(i+" :
			// "+partType+","+part1+","+part2+","+part3+","+seq+","+customer+","+matCode+","+matName);

			// System.out.println(i+" : "+partType);
		}

	}

}
