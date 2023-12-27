package com.e3ps.part.loader;

import com.e3ps.part.service.PartHelper;

public class PartLoader {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("엑셀 파일을 추가하세요.");
			System.exit(0);
		}
		String path = args[0];

		PartLoader loader = new PartLoader();
		loader.initialize(path);
		System.exit(0);
	}

	private void initialize(String path) throws Exception {
		PartHelper.service.loaderPart(path);
	}
}
