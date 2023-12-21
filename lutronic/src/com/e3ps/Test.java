package com.e3ps;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.common.aspose.AsposeUtils;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;

import wt.part.WTPart;

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
		CellStyle style= workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		
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

		outStream = new FileOutputStream("D:\\result.xlsx");

		workbook.write(outStream);
		
		System.out.println("완료");
		
		
	}
}