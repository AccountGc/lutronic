package com.e3ps;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Picture;
import com.aspose.cells.Row;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.doc.WTDocument;
import wt.org.WTUser;

public class Test {

	public static void main(String[] args) throws Exception {

		String oid = "wt.doc.WTDocument:1423614";
		WTDocument d = (WTDocument) CommonUtil.getObject(oid);
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(d);
		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(m);

		Workbook workbook = new Workbook("D:\\DMR.xlsx");
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.getPageSetup().setFooter(0, "문서양식번호넣는곳");

		String imgPath = "D:\\test.PNG";

		Cell modelCell = worksheet.getCells().get(4, 5);
		modelCell.putValue(IBAUtil.getStringValue(d, "MODEL"));

		Cell nameCell = worksheet.getCells().get(5, 0);
		nameCell.putValue(d.getName());

		Cell numberCell = worksheet.getCells().get(11, 3);
		numberCell.putValue(IBAUtil.getStringValue(d, "INTERALNUMBER"));

		int rowIndex = 16;
		int rowHeight = 30;
		for (ApprovalLine agreeLine : agreeLines) {
			Row row = worksheet.getCells().getRows().get(rowIndex);
			row.setHeight(rowHeight);

			Cell cell = worksheet.getCells().get(rowIndex, 1); // 결재타입
			cell.putValue("합의");
			setCellStyle(cell);

			WTUser user = (WTUser) agreeLine.getOwnership().getOwner().getObject();
			PeopleDTO pdata = new PeopleDTO(user);
			cell = worksheet.getCells().get(rowIndex, 2); // 이름+팀
			cell.putValue(pdata.getName() + "[" + pdata.getDepartment_name() + "]");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 3); // 결재일
			cell.putValue(
					agreeLine.getCompleteTime() != null ? agreeLine.getCompleteTime().toString().substring(0, 10) : "");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 4);
			setCellStyle(cell);
//			int picIndex = worksheet.getPictures().add(rowIndex, 4, imgPath);
//			Picture picture = worksheet.getPictures().get(picIndex);

			int picIndex = worksheet.getPictures().add(rowIndex, 4, imgPath);
			Picture picture = worksheet.getPictures().get(picIndex);
			picture.setHeightCM(1);
			picture.setWidthCM(4);

			rowIndex++;
		}

		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(m);
		for (ApprovalLine approvalLine : approvalLines) {
			Row row = worksheet.getCells().getRows().get(rowIndex);
			row.setHeight(rowHeight);

			Cell cell = worksheet.getCells().get(rowIndex, 1); // 결재타입
			cell.putValue("결재");
			setCellStyle(cell);

			WTUser user = (WTUser) approvalLine.getOwnership().getOwner().getObject();
			PeopleDTO pdata = new PeopleDTO(user);
			cell = worksheet.getCells().get(rowIndex, 2); // 이름+팀
			cell.putValue(pdata.getName() + "[" + pdata.getDepartment_name() + "]");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 3); // 결재일
			cell.putValue(
					approvalLine.getCompleteTime() != null ? approvalLine.getCompleteTime().toString().substring(0, 10)
							: "");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 4);
//			setCellStyle(cell);

			rowIndex++;
		}

		// Save output Excel file
		workbook.save("D:\\DMR2.xlsx");

		System.out.println("Done");

	}

	private static void setCellStyle(Cell cell) throws Exception {
		Style style = cell.getStyle();
		Font font = style.getFont();
		font.setBold(true);
		style.setHorizontalAlignment(TextAlignmentType.CENTER);
		style.setVerticalAlignment(TextAlignmentType.CENTER);
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
		cell.setStyle(style);
	}

}