package com.e3ps.common.aspose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.SaveFormat;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.google.common.io.Files;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.FileUtil;
import wt.util.WTProperties;

public class AsposeUtils {
	private static final String wordToPdflicPath = "D:\\ptc\\license\\Aspose.Words.Java.lic";
	private static final String pdflicPath = "D:\\ptc\\license\\Aspose.PDF.Java.lic";

	private AsposeUtils() {

	}

	/**
	 * Aspose Word 라이센스 세팅
	 */
	public static void setAsposeWordLic() throws Exception {
		com.aspose.words.License license = new com.aspose.words.License();
		license.setLicense(wordToPdflicPath);
	}

	/**
	 * Aspose Pdf 라이센스 세팅
	 */
	public static void setAsposePdfLic() throws Exception {
		com.aspose.pdf.License license = new com.aspose.pdf.License();
		license.setLicense(pdflicPath);
	}

	public static void saveWordToPdfTest(String wordPath, String pdfPath) throws Exception {
		System.out.println("문서 워드 TO PDF 컨버전 시작 = " + new Timestamp(new Date().getTime()));
		setAsposeWordLic();
		Document doc = new Document(wordPath);
		doc.save(pdfPath, SaveFormat.PDF);
		System.out.println("문서 워드 TO PDF 컨버전 종료 = " + new Timestamp(new Date().getTime()));
	}

	public static void wordToPdf(Hashtable<String, String> hash) throws Exception {
		System.out.println("문서 워드 TO PDF 컨버전 시작 = " + new Timestamp(new Date().getTime()));
		setAsposeWordLic();
		String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
		String savePath = tempDir + File.separator + "pdf";
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		String oid = hash.get("oid");
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		String name = "";
		String wordPath = tempDir;
		boolean pass = false;
		if (qr.hasMoreElements()) {
			ApplicationData data = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
			name = data.getFileName();
			String ext = FileUtil.getExtension(name);
			if (!ext.equalsIgnoreCase("docx") && !ext.equalsIgnoreCase("doc")) {
				pass = true;
			}

//			name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");

			File file = new File(wordPath + File.separator + name);
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		if (!pass) {

			int idx = name.lastIndexOf(".");
			String pdfName = name.substring(0, idx) + ".pdf";
			String pdfFilePath = savePath + File.separator + pdfName;
			String wordFilePath = wordPath + File.separator + name;

			Document pdf = new Document(wordFilePath);
			pdf.save(pdfFilePath, SaveFormat.PDF);

			File pdfFIle = new File(pdfFilePath);
			ApplicationData dd = ApplicationData.newApplicationData(doc);
			dd.setRole(ContentRoleType.toContentRoleType("PDF"));
			PersistenceHelper.manager.save(dd);
			ContentServerHelper.service.updateContent(doc, dd, pdfFIle.getPath());

			File[] files = saveDir.listFiles();
			for (File ff : files) {
				System.out.println("삭제되는 파일들 = " + ff.getName());
				ff.delete();
			}

		} else {
			System.out.println("지원 대상의 파일이 아님");
		}
		System.out.println("문서 워드 TO PDF 컨버전 종료 = " + new Timestamp(new Date().getTime()));
	}

	public static void genWordAndPdf(Hashtable<String, String> hash) throws Exception {
		System.out.println("문서 워드 생성 및 PDF 컨버전 시작 = " + new Timestamp(new Date().getTime()));
		setAsposeWordLic();
		String savePath = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "save";
		File saveFolder = new File(savePath);
		if (!saveFolder.exists()) {
			saveFolder.mkdirs();
		}

		String oid = hash.get("oid");
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

		String content = doc.getTypeInfoWTDocument().getPtc_rht_1();

		// 워드 생성
		Document word = new Document();
		DocumentBuilder builder = new DocumentBuilder(word);
		builder.insertHtml(content);

		// Word 문서로 저장
		String wordOutpUtPath = savePath + File.separator + doc.getName() + ".docx";
		word.save(wordOutpUtPath, SaveFormat.DOCX);

		ApplicationData applicationData = ApplicationData.newApplicationData(doc);
		applicationData.setRole(ContentRoleType.SECONDARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(doc, applicationData, wordOutpUtPath);

		String pdfOutputPath = savePath + File.separator + doc.getName() + ".pdf";
		Document pdf = new Document(wordOutpUtPath);
		pdf.save(pdfOutputPath, SaveFormat.PDF);

		ApplicationData dd = ApplicationData.newApplicationData(doc);
		dd.setRole(ContentRoleType.SECONDARY);
		PersistenceHelper.manager.save(dd);
		ContentServerHelper.service.updateContent(doc, dd, pdfOutputPath);

		File[] files = saveFolder.listFiles();
		for (File ff : files) {
			System.out.println("삭제되는 파일들 = " + ff.getName());
			ff.delete();
		}

		System.out.println("문서 워드 생성 및 PDF 컨버전 종료 = " + new Timestamp(new Date().getTime()));
	}
}
