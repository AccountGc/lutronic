package com.e3ps.common.code.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberCodeService extends StandardManager implements NumberCodeService {

	public static StandardNumberCodeService newStandardNumberCodeService() throws Exception {
		final StandardNumberCodeService instance = new StandardNumberCodeService();
		instance.initialize();
		return instance;
	}

	@Override
	public String getValue(String codeType, String code) throws Exception {
		NumberCode nc = getNumberCode(codeType, code);

		// System.out.println("NumberCode nc : "+nc);

		if (nc == null) {
			return null;
		}
		return Message.getNC(nc);
	}

	/**
	 * Ư�� �ڵ� Ÿ�Կ� �ڵ尡 code�� NumberCode�� ��ȯ
	 * 
	 * @param codeType
	 * @param code
	 * @return
	 */
	@Override
	public NumberCode getNumberCode(String codeType, String code) {
		return getNumberCode(codeType, code, "");
	}

	@Override
	public NumberCode getNumberCode(String codeType, String code, String parentOid) {
		if (code == null) {
			return null;
		}

		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });

			if (StringUtil.checkString(parentOid)) {
				select.appendAnd();
				select.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", "=",
						CommonUtil.getOIDLongValue(parentOid)), new int[] { 0 });
			}
			select.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, "thePersistInfo.modifyStamp"), true),
					new int[] { 0 });
			// System.out.println("GetNumberCode : "+select);
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public NumberCode getNumberCodeFormName(String codeType, String name) {
		if (name == null) {
			return null;
		}

		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, "=", name), new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(HashMap<String, Object> dataMap) throws Exception {
		List<NumberCodeDTO> addRows = (List<NumberCodeDTO>) dataMap.get("addRows");
		List<NumberCodeDTO> editRows = (List<NumberCodeDTO>) dataMap.get("editRows");
		List<NumberCodeDTO> removeRows = (List<NumberCodeDTO>) dataMap.get("removeRows");
		String codeType = (String) dataMap.get("codeType");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (NumberCodeDTO dto : addRows) {
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				String sort = dto.getSort();
				boolean enabled = dto.isEnabled();
				String parentRowId = dto.getParentRowId();

				NumberCode parent = null;
				if (StringUtil.checkString(parentRowId)) {
					parent = (NumberCode) CommonUtil.getObject(parentRowId);
				}

				NumberCode n = NumberCode.newNumberCode();

				n.setParent(parent);
				n.setName(name);
				n.setCode(code);
				n.setDescription(description);
				n.setSort(sort);
				n.setDisabled(!enabled);
				n.setCodeType(NumberCodeType.toNumberCodeType(codeType));
				PersistenceHelper.manager.save(n);
			}

			for (NumberCodeDTO dto : editRows) {
				// 단순 수정만..
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				String sort = dto.getSort();
				boolean enabled = dto.isEnabled();

				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				n.setName(name);
				n.setCode(code);
				n.setDescription(description);
				n.setSort(sort);
				n.setDisabled(!enabled);
				PersistenceHelper.manager.modify(n);
			}

			for (NumberCodeDTO dto : removeRows) {
				String oid = dto.getOid();
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
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

	@Override
	public void loaderNation(String path) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			File file = new File(path);

			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows(); // 시트의 행 개수 가져오기

			// 모든 행(row)을 순회하면서 데이터 가져오기
			for (int i = 1; i < rows; i++) {
				Row row = sheet.getRow(i);

				String code = row.getCell(0).getStringCellValue();
				// 값없을 경우 리턴
				if (!StringUtil.checkString(code)) {
					return;
				}
				String name = row.getCell(1).getStringCellValue();
				// 값없을 경우 리턴
				if (!StringUtil.checkString(name)) {
					return;
				}

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(NumberCode.class, true);
				QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE, code);
				QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "NATION");
				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					return;
				}
				NumberCode n = NumberCode.newNumberCode();
				n.setCode(code);
				n.setName(name);
				n.setSort(String.valueOf(i));
				n.setDescription(name);
				n.setCodeType(NumberCodeType.toNumberCodeType("NATION"));
				n.setDisabled(false);
				PersistenceHelper.manager.save(n);
			}

			workbook.close();

			trs.commit();
			trs = null;
		} catch (

		Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
