package com.e3ps.admin.form.loader;

import com.e3ps.admin.form.service.FormTemplateHelper;

public class FormTemplateLoader {

	public static void main(String[] args) throws Exception {

		FormTemplateLoader loader = new FormTemplateLoader();
		loader.initialize();
		System.exit(0);
	}

	private void initialize() throws Exception {
		FormTemplateHelper.service.loader();
	}
}
