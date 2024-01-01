package com.e3ps.admin.form.service;

import java.util.Map;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.util.CommonUtil;

import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionContext;
import wt.session.SessionContext;
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
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			FormTemplate form = FormTemplate.newFormTemplate();
			form.setName(name);
			form.setOwnership(ownership);
			form.setDescription(description);
			form.setVersion(1);
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

	/**
	 * 문서 양식 샘플 로더
	 */
	public void loader() throws Exception {
		String[] name = new String[] { "변경관리요청서", "설계변경 사전검토 회의록", "테스트보고서", "제3자시험의뢰서", "설계변경 위험관리 보고서(ECRM)",
				"RA Plan", "Quality Requirement", "Service Requirement", "[공통]개발문서", "위험관리계획서", "위험관리보고서", "FMEA Table",
				"제품요구사양서(PRS)", "설계계획서 ", "설계명세보고서", "제품표준서", "주요부품목록(CCL)", "유효성확인보고서", "프로젝트 완료 보고서",
				"Validation Report", "사용적합성 사양서", "형성평가 계획서", "총괄평가 계획서", "총괄평가 보고서", "Engineering Order(EO)",
				"설계변경통보서(ECO)", "E-BOM List", };
		int i = 0;
		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			for (String s : name) {
				NumberCode n = NumberCode.newNumberCode();
				n.setName(s);
				n.setCode(s);
				n.setSort(String.valueOf(i));
				n.setDescription(s);
				n.setDisabled(false);
				n.setCodeType(NumberCodeType.toNumberCodeType("DOCFORMTYPE"));
				PersistenceHelper.manager.save(n);
				i++;
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String description = (String) params.get("description");
		String formType = (String) params.get("formType");
		Transaction trs = new Transaction();
		try {
			trs.start();

			FormTemplate form = (FormTemplate) CommonUtil.getObject(oid);
			form.setName(name);
			form.setNumber(number);
			form.setDescription(description);
			form.setFormType(formType);
			form.setVersion(form.getVersion() + 1);
			PersistenceHelper.manager.modify(form);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			FormTemplate form = (FormTemplate) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(form);

			trs.commit();
			trs = null;
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
