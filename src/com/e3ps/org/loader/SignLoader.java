package com.e3ps.org.loader;

import com.e3ps.org.service.OrgHelper;

public class SignLoader {

	public static void main(String[] args) throws Exception {
		SignLoader loader = new SignLoader();
		System.out.println("서명 로더 시작");
		loader.initialize();
		System.out.println("서명 로더 끝");
		System.exit(0);
	}

	private void initialize() throws Exception {
		OrgHelper.service.loaderSign();
	}
}
