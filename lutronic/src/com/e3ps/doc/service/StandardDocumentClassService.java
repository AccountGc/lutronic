package com.e3ps.doc.service;

import java.util.HashMap;
import java.util.List;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.dto.DocumentClassDTO;

import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardDocumentClassService extends StandardManager implements DocumentClassService {

	public static StandardDocumentClassService newStandardDocumentClassService() throws WTException {
		StandardDocumentClassService instance = new StandardDocumentClassService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, Object> dataMap) throws Exception {
		List<DocumentClassDTO> addRows = (List<DocumentClassDTO>) dataMap.get("addRows");
		List<DocumentClassDTO> editRows = (List<DocumentClassDTO>) dataMap.get("editRows");
		List<DocumentClassDTO> removeRows = (List<DocumentClassDTO>) dataMap.get("removeRows");
		String classType = (String) dataMap.get("classType");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (DocumentClassDTO dto : addRows) {
				String name = dto.getName();
				String clazz = dto.getClazz();
				String description = dto.getDescription();
				String sort = dto.getSort();
				boolean enabled = dto.isEnabled();
				String parentRowId = dto.getParentRowId();

				DocumentClass parent = null;
				if (StringUtil.checkString(parentRowId)) {
					parent = (DocumentClass) CommonUtil.getObject(parentRowId);
				}

				DocumentClass n = DocumentClass.newDocumentClass();
				n.setParent(parent);
				n.setName(name);
				n.setClazz(clazz);
				n.setDescription(description);
				n.setSort(sort);
				n.setEnabled(!enabled);
				n.setClassType(DocumentClassType.toDocumentClassType(classType));
				PersistenceHelper.manager.save(n);
			}

			for (DocumentClassDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String clazz = dto.getClazz();
				String description = dto.getDescription();
				String sort = dto.getSort();
				boolean enabled = dto.isEnabled();

				DocumentClass n = (DocumentClass) CommonUtil.getObject(oid);
				n.setName(name);
				n.setClazz(clazz);
				n.setDescription(description);
				n.setSort(sort);
				n.setEnabled(!enabled);
				PersistenceHelper.manager.modify(n);
			}

			for (DocumentClassDTO dto : removeRows) {
				String oid = dto.getOid();
				DocumentClass n = (DocumentClass) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(n);
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
		}
	}
}
