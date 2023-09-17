package com.e3ps.admin.form.service;

import java.util.Map;

import com.e3ps.admin.form.FormTemplate;

import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardFormTemplateService extends StandardManager implements FormTemplateService {

	public static StandardFormTemplateService newStandardFormTemplateService() throws WTException {
		StandardFormTemplateService instance = new StandardFormTemplateService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String description = (String) params.get("description");
		String formType = (String) params.get("formType");
		Transaction trs = new Transaction();
		try {

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			FormTemplate form = FormTemplate.newFormTemplate();
			form.setName(name);
			form.setOwnership(ownership);
			form.setNumber(number);
			form.setDescription(description);
			form.setFormType("테스트");
			PersistenceHelper.manager.save(form);

		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
