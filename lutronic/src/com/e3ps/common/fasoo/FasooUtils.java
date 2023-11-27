package com.e3ps.common.fasoo;

import java.io.File;
import java.util.Hashtable;

import com.fasoo.adk.packager.WorkPackager;

import wt.util.WTProperties;

public class FasooUtils {

	private FasooUtils() {

	}

	public static String FileTypeStr(int i) {
		String ret = null;
		switch (i) {
		case 20:
			ret = "파일을 찾을 수 없습니다.";
			break;
		case 21:
			ret = "파일 사이즈가 0 입니다.";
			break;
		case 22:
			ret = "파일을 읽을 수 없습니다.";
			break;
		case 29:
			ret = "암호화 파일이 아닙니다.";
			break;
		case 26:
			ret = "FSD 파일입니다.";
			break;
		case 105:
			ret = "Wrapsody 파일입니다.";
			break;
		case 106:
			ret = "NX 파일입니다.";
			break;
		case 101:
			ret = "MarkAny 파일입니다.";
			break;
		case 104:
			ret = "INCAPS 파일입니다.";
			break;
		case 103:
			ret = "FSN 파일입니다.";
			break;
		}
		return ret;
	}

	/**
	 * 업로드시 파일 복호화 처리
	 */
	public static File decryptedFile(String savePath, String name) throws Exception {
		File f = null;
		String codebase = WTProperties.getLocalProperties().getProperty("wt.codebase.location");
		String temp = WTProperties.getLocalProperties().getProperty("wt.temp");
		int retVal = 0;
		String strFsdinitPath = codebase + File.separator + "key"; // 첨부 드리는 키로 복호화 키 경로 변경
		String CPIDValue = "";
		String strFileName = savePath + File.separator + name;
		String strDecFilePath = temp + "decryptedFile";

		if (!new File(strDecFilePath).exists()) {
			new File(strDecFilePath).mkdirs();
		}

		Hashtable GetHeaderstr = null;
		WorkPackager objWorkPackager = new WorkPackager();
		objWorkPackager.setOverWriteFlag(false);
		retVal = objWorkPackager.GetFileType(strFileName);
		System.out.println("파일형태는 " + FileTypeStr(retVal) + "[" + retVal + "]" + " 입니다.");

		if (retVal == 103) {
			GetHeaderstr = objWorkPackager.GetFileHeader(strFileName); // DRM문서 헤더 정보 추출
			int i = 0;
			CPIDValue = (String) GetHeaderstr.get("CPID"); // DRM문서 SERVER ID값 추출
			boolean bret = objWorkPackager.DoExtract(strFsdinitPath, CPIDValue, // 고객사 Key(default)
					strFileName, strDecFilePath + File.separator + name);
			if (bret) {
				System.out.println("복호화 성공!");
				f = new File(strDecFilePath + File.separator + name);
			}
		} else {
			System.out.println("암호화된 첨부파일이 아닙니다!");
			f = new File(savePath + File.separator + name);
		}
		return f;
	}
}
