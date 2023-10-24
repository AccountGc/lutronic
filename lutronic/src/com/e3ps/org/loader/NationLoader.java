package com.e3ps.org.loader;
import com.e3ps.org.service.OrgHelper;

public class NationLoader {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("엑셀 파일을 추가하세요.");
			System.exit(0);
		}
		String path = args[0];

		NationLoader loader = new NationLoader();
		loader.initialize(path);
		System.exit(0);
	}

	private void initialize(String path) throws Exception {
		OrgHelper.service.loaderNation(path);
	}
}
