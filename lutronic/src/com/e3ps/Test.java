package com.e3ps;

import java.io.File;

import com.e3ps.common.aspose.AsposeUtils;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;

import wt.part.WTPart;

public class Test {

	public static void main(String[] args) throws Exception {

//		String name = "asdfasdf.docx";
//		int idx = name.lastIndexOf(".");
//
//		System.out.println(name.substring(0, idx + 1));

		String wordPath = "D:" + File.separator + "1.docx";
		String pdfPath = "D:" + File.separator + "test.pdf";
		AsposeUtils.saveWordToPdfTest(wordPath, pdfPath);

//		WTPart p = (WTPart) CommonUtil.getObject("wt.part.WTPart:1342091");
//		IBAUtils.createIBA(p, "SPECIFICATION", "2023-12-12 사양 테스트", "s");
//		IBAUtils.createIBA(p, "MODEL", "ML16", "s");
//		IBAUtils.createIBA(p, "PRODUCTMETHOD", "PM001", "s");
//		IBAUtils.createIBA(p, "DEPTCODE", "Q", "s");
//		IBAUtils.createIBA(p, "WEIGHT", "3", "f");
//		IBAUtils.createIBA(p, "MAT", "MA002", "s");
//		IBAUtils.createIBA(p, "FINISH", "FI011", "s");
//		IBAUtils.createIBA(p, "MANUFACTURE", "MF101", "s");
//
//		System.out.println("IBA 속성 생성!");
		System.exit(0);
	}
}