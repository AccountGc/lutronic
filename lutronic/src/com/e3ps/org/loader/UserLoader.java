package com.e3ps.org.loader;

import com.e3ps.admin.form.service.FormTemplateHelper;
import com.e3ps.org.service.OrgHelper;

public class UserLoader {

	public static void main(String[] args) throws Exception {

		String path = args[0];

		UserLoader loader = new UserLoader();
		loader.initialize(path);
		System.exit(0);
	}

	private void initialize(String path) throws Exception {
		OrgHelper.service.loader(path);
	}
}
