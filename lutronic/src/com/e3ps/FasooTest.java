package com.e3ps;

import java.io.File;
import java.util.Hashtable;

import com.fasoo.adk.packager.WorkPackager;

public class FasooTest {

	public static void main(String[] args) throws Exception {
		boolean bret = false;
		boolean nret = false;

		int retVal = 0;

		String strFsdinitPath = "D:" + File.separator + "key"; // 첨부 드리는 키로 복호화 키 경로 변경
		String CPIDValue = "";
		String strEncFilePath = "D:" + File.separator;
		String strFileName = "D:" + File.separator + "test.xlsx";
		String strDecFilePath = "D:" + File.separator + "de" + File.separator;
		Hashtable GetHeaderstr = null;
		WorkPackager objWorkPackager = new WorkPackager();

		objWorkPackager.setOverWriteFlag(false);

		retVal = objWorkPackager.GetFileType(strFileName);

		System.out.println("파일형태는 " + FileTypeStr(retVal) + "[" + retVal + "]" + " 입니다.");

		if (retVal == 103) {

			GetHeaderstr = objWorkPackager.GetFileHeader(strFileName); // DRM문서 헤더 정보 추출
			int i = 0;
			CPIDValue = (String) GetHeaderstr.get("CPID"); // DRM문서 SERVER ID값 추출

			System.out.println("CPIDValue:" + CPIDValue);

			bret = objWorkPackager.DoExtract(strFsdinitPath, CPIDValue, // 고객사 Key(default)
					strFileName, strDecFilePath);

			System.out.println("복호화 문서 : " + objWorkPackager.getContainerFilePathName());
			System.out.println("오류코드 : " + objWorkPackager.getLastErrorNum());
			System.out.println("오류값 : " + objWorkPackager.getLastErrorStr());
			System.out.println("getfileheader: " + GetHeaderstr);
		} else {
			System.out.println("FSN 파일이 아닌경우 복호화 불가능 합니다.[" + retVal + "]");
		}

		System.exit(0);
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

}
