package com.e3ps.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Cell;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	public static void copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int count) {

		for (int a = 0; a < count; a++) {
			// Get the source / new row
			XSSFRow newRow = worksheet.getRow((sourceRowNum + a));
			XSSFRow sourceRow = worksheet.getRow(sourceRowNum);

			// If the row exist in destination, push down all rows by 1 else
			// create a new row
			worksheet.shiftRows((sourceRowNum + a), worksheet.getLastRowNum(), 1);
			newRow = worksheet.createRow((sourceRowNum + a));

			// Loop through source columns to add to new row
			for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
				// Grab a copy of the old/new cell
				XSSFCell oldCell = sourceRow.getCell(i);
				XSSFCell newCell = newRow.createCell(i);

				// If the old cell is null jump to next cell
				if (oldCell == null) {
					newCell = null;
					continue;
				}

				// Copy style from old cell and apply to new cell
				XSSFCellStyle newCellStyle = workbook.createCellStyle();
				newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
				newCell.setCellStyle(newCellStyle);

				// If there is a cell comment, copy
				if (newCell.getCellComment() != null) {
					newCell.setCellComment(oldCell.getCellComment());
				}

				// If there is a cell hyperlink, copy
				if (oldCell.getHyperlink() != null) {
					newCell.setHyperlink(oldCell.getHyperlink());
				}

				// Set the cell data type
				newCell.setCellType(oldCell.getCellType());

				// Set the cell data value
				switch (oldCell.getCellType()) {
				case BLANK:
					newCell.setCellValue(oldCell.getStringCellValue());
					break;
				case BOOLEAN:
					newCell.setCellValue(oldCell.getBooleanCellValue());
					break;
				case ERROR:
					newCell.setCellErrorValue(oldCell.getErrorCellValue());
					break;
				case FORMULA:
					newCell.setCellFormula(oldCell.getCellFormula());
					break;
				case NUMERIC:
					newCell.setCellValue(oldCell.getNumericCellValue());
					break;
				case STRING:
					newCell.setCellValue(oldCell.getRichStringCellValue());
					break;
				}
			}

			// If there are are any merged regions in the source row, copy to
			// new row
			for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
				CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
				if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
					CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
							(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
							cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
					worksheet.addMergedRegion(newCellRangeAddress);
				}
			}
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
