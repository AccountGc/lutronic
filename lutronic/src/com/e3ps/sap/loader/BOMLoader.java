package com.e3ps.sap.loader;

import java.io.File;

import com.e3ps.sap.service.SAPHelper;

public class BOMLoader {

	public static void main(String[] args) throws Exception {

		// ERP 전송용 샘플 데이터 만들기 로더
		if (args.length != 1) {
			System.out.println("엑셀 파일을 추가하세요.");
			System.exit(0);
		}
		String path = args[0];

		BOMLoader loader = new BOMLoader();
		loader.initialize(path);
		System.exit(0);
	}

	/**
	 * BOM 샘플 업로더
	 */
	private void initialize(String path) throws Exception {
		File f = new File(path);
		SAPHelper.service.upload(f);
	}

}
