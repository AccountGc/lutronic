package com.e3ps;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.Workbook;
import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.PageSetup;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.PreferredWidth;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.Section;
import com.aspose.words.WrapType;

public class Test {

	public static void main(String[] args) throws Exception {
		FileInputStream excelFile = new FileInputStream("D:\\제품표준서.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		
		// 이미지 사이즈 변경
		Image oldImg = ImageIO.read(new File("D:\\lutronic_s.png"));
		Image resizeImage = oldImg.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
		BufferedImage newImage = new BufferedImage(90, 90, BufferedImage.TYPE_INT_RGB );
		Graphics g = newImage.getGraphics();
		g.drawImage(resizeImage, 0, 0, null);
		g.dispose();
		ImageIO.write(newImage, "jpg", new File("D:\\lutronic_s.jpg"));
		
		//스트림으로 이미지 가져오기
		FileInputStream is = new FileInputStream("D:\\lutronic_s.jpg");
		
		byte[] bytes = IOUtils.toByteArray(is);
		int pictureIdx = workbook.addPicture(bytes, workbook.PICTURE_TYPE_PNG);
		is.close();
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		XSSFCreationHelper helper = workbook.getCreationHelper();
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		//이미지에 대한 크기와 위치 설정하는 앵커
		XSSFClientAnchor anchor = helper.createClientAnchor();
		anchor.setRow1(20);
		anchor.setCol1(2);

		//이미지 삽입
		XSSFPicture pic = drawing.createPicture(anchor, pictureIdx);

		//셀 크기에 고정하지 않고 배율에 따라 크기 재설정.
		pic.resize();
		
		FileOutputStream outStream = null;

		outStream = new FileOutputStream("D:\\제품표준서.xlsx");

		workbook.write(outStream);
		
		Workbook wb = new Workbook("D:\\제품표준서.xlsx");
		FileOutputStream fospdf = new FileOutputStream("D:\\제품표준서.pdf");
	    wb.save(fospdf, FileFormatType.PDF);
		
		// Open document
	    com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document("D:\\제품표준서.pdf");

	    // Create header
	    com.aspose.pdf.TextStamp headerTextStamp1 = new com.aspose.pdf.TextStamp("문서번호(개정 번호)");
	    // Set properties of the stamp
	    headerTextStamp1.setTopMargin(10);
	    headerTextStamp1.setLeftMargin(10);
	    headerTextStamp1.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Left);
	    headerTextStamp1.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Top);
	    // Create header
	    com.aspose.pdf.TextStamp headerTextStamp2 = new com.aspose.pdf.TextStamp("제품표준서(####)");
	    // Set properties of the stamp
	    headerTextStamp2.setTopMargin(10);
	    headerTextStamp2.setRightMargin(10);
	    headerTextStamp2.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Right);
	    headerTextStamp2.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Top);

	    // Create footer
	    com.aspose.pdf.TextStamp footerTextStamp1 = new com.aspose.pdf.TextStamp("QF-701-02(Rev0)");
	    // Set properties of the stamp
	    footerTextStamp1.setBottomMargin(10);
	    footerTextStamp1.setLeftMargin(10);
	    footerTextStamp1.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Left);
	    footerTextStamp1.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Bottom);
	    // Create footer
	    com.aspose.pdf.TextStamp footerTextStamp2 = new com.aspose.pdf.TextStamp("LUTRONIC");
	    // Set properties of the stamp
	    footerTextStamp2.setBottomMargin(10);
	    footerTextStamp2.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Center);
	    footerTextStamp2.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Bottom);
	    // Create footer
	    com.aspose.pdf.TextStamp footerTextStamp3 = new com.aspose.pdf.TextStamp("A4(210X297)");
	    // Set properties of the stamp
	    footerTextStamp3.setBottomMargin(10);
	    footerTextStamp3.setRightMargin(10);
	    footerTextStamp3.setHorizontalAlignment(com.aspose.pdf.HorizontalAlignment.Right);
	    footerTextStamp3.setVerticalAlignment(com.aspose.pdf.VerticalAlignment.Bottom);

	    // Add header and footer on all pages
	    for (com.aspose.pdf.Page page : pdfDocument.getPages()){
			page.addStamp(headerTextStamp1);
			page.addStamp(headerTextStamp2);
			page.addStamp(footerTextStamp1);
			page.addStamp(footerTextStamp2);
			page.addStamp(footerTextStamp3);
	    }

	    // Save updated document
	    pdfDocument.save("D:\\result.pdf");

	    System.out.println("Done");
		
	}
}