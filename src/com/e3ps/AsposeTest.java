package com.e3ps;

import com.aspose.pdf.HtmlFragment;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.Page;
import com.e3ps.change.ECPRRequest;
import com.e3ps.common.util.CommonUtil;

public class AsposeTest {

	public static void main(String[] args) {
		ECPRRequest req = (ECPRRequest)CommonUtil.getObject("com.e3ps.change.ECPRRequest:1458709");
		String html = req.getContents();
		// HTML 문자열 정의
		com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document();
		// A4 크기로 페이지 생성
		Page pdfPage = pdfDocument.getPages().add();
		// Set the page size as A4 (11.7 x 8.3 in) and in Aspose.Pdf, 1 inch = 72 points
		// So A4 dimensions in points will be (842.4, 597.6)
		pdfPage.setPageSize(597.6, 842.4);

		// HTML 문자열을 PDF 페이지에 추가
		HtmlFragment htmlFragment = new HtmlFragment(html);
		pdfPage.getParagraphs().add(htmlFragment);

		// PDF 파일로 저장
		pdfDocument.save("D:\\output.pdf");
		System.out.println("저장");
		System.exit(0);
	}
}