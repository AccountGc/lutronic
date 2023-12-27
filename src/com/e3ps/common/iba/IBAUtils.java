package com.e3ps.common.iba;

import java.util.HashMap;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.mks.webservice._2009.integrity.GetItem;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.BooleanDefinitionReference;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.FloatDefinitionReference;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.IntegerDefinitionReference;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.StringDefinitionReference;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.query.QuerySpec;

public class IBAUtils {

	private IBAUtils() {

	}

	/**
	 * IBA String 속성 값
	 */
	public static String getStringValue(IBAHolder holder, String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IBAHolder.class, false);
		int idx_s = query.appendClassList(StringValue.class, true);
		int idx_d = query.appendClassList(StringDefinition.class, false);
		QuerySpecUtils.toEqualsAnd(query, idx, IBAHolder.class, "thePersistInfo.theObjectIdentifier.id", holder);
		QuerySpecUtils.toInnerJoin(query, StringValue.class, IBAHolder.class, "theIBAHolderReference.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_s, idx);
		QuerySpecUtils.toInnerJoin(query, StringValue.class, StringDefinition.class, "definitionReference.hierarchyID",
				"hierarchyID", idx_s, idx_d);
		QuerySpecUtils.toEqualsAnd(query, idx_d, StringDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			StringValue sv = (StringValue) obj[0];
			return sv.getValue();
		}
		return null;
	}

	/**
	 * IBA Float 속성 값
	 */
	public static float getFloatValue(IBAHolder holder, String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IBAHolder.class, false);
		int idx_f = query.appendClassList(FloatValue.class, true);
		int idx_d = query.appendClassList(FloatDefinition.class, false);
		QuerySpecUtils.toEqualsAnd(query, idx, IBAHolder.class, "thePersistInfo.theObjectIdentifier.id", holder);
		QuerySpecUtils.toInnerJoin(query, FloatValue.class, IBAHolder.class, "theIBAHolderReference.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_f, idx);
		QuerySpecUtils.toInnerJoin(query, FloatValue.class, FloatDefinition.class, "definitionReference.hierarchyID",
				"hierarchyID", idx_f, idx_d);
		QuerySpecUtils.toEqualsAnd(query, idx_d, FloatDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			FloatValue fv = (FloatValue) obj[0];
			return (float) fv.getValue();
		}
		return 0f;
	}

	/**
	 * IBA Integer 속성 값
	 */
	public static int getIntegerValue(IBAHolder holder, String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IBAHolder.class, false);
		int idx_i = query.appendClassList(IntegerValue.class, true);
		int idx_d = query.appendClassList(IntegerDefinition.class, false);
		QuerySpecUtils.toEqualsAnd(query, idx, IBAHolder.class, "thePersistInfo.theObjectIdentifier.id", holder);
		QuerySpecUtils.toInnerJoin(query, IntegerValue.class, IBAHolder.class, "theIBAHolderReference.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_i, idx);
		QuerySpecUtils.toInnerJoin(query, IntegerValue.class, IntegerDefinition.class,
				"definitionReference.hierarchyID", "hierarchyID", idx_i, idx_d);
		QuerySpecUtils.toEqualsAnd(query, idx_d, IntegerDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			IntegerValue iv = (IntegerValue) obj[0];
			return (int) iv.getValue();
		}
		return 0;
	}

	/**
	 * IBA Boolean 속성 값
	 */
	public static boolean getBooleanValue(IBAHolder holder, String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IBAHolder.class, false);
		int idx_b = query.appendClassList(BooleanValue.class, true);
		int idx_d = query.appendClassList(BooleanDefinition.class, false);
		QuerySpecUtils.toEqualsAnd(query, idx, IBAHolder.class, "thePersistInfo.theObjectIdentifier.id", holder);
		QuerySpecUtils.toInnerJoin(query, BooleanValue.class, IBAHolder.class, "theIBAHolderReference.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_b, idx);
		QuerySpecUtils.toInnerJoin(query, BooleanValue.class, BooleanDefinition.class,
				"definitionReference.hierarchyID", "hierarchyID", idx_b, idx_d);
		QuerySpecUtils.toEqualsAnd(query, idx_d, BooleanDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			BooleanValue bv = (BooleanValue) obj[0];
			return bv.isValue();
		}
		return false;
	}

	/**
	 * IBA 속성값 삭제
	 */
	public static void deleteIBA(IBAHolder holder, String attrName, String type) throws Exception {
		QueryResult qr = null;
		if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
			StringDefinition sd = getStringDefinition(attrName);
			qr = stringQuery(holder, sd);
		} else if ("f".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type)) {
			FloatDefinition fd = getFloatDefinition(attrName);
			qr = floatQuery(holder, fd);
		} else if ("i".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
			IntegerDefinition id = getIntegerDefinition(attrName);
			qr = integerQuery(holder, id);
		} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			BooleanDefinition bd = getBooleanDefinition(attrName);
			qr = booleanQuery(holder, bd);
		}

		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			if (obj[0] instanceof StringValue) {
				StringValue value = (StringValue) obj[0];
				PersistenceHelper.manager.delete(value);
			} else if (obj[0] instanceof FloatValue) {
				FloatValue value = (FloatValue) obj[0];
				PersistenceHelper.manager.delete(value);
			} else if (obj[0] instanceof IntegerValue) {
				IntegerValue value = (IntegerValue) obj[0];
				PersistenceHelper.manager.delete(value);
			} else if (obj[0] instanceof BooleanValue) {
				BooleanValue value = (BooleanValue) obj[0];
				PersistenceHelper.manager.delete(value);
			}
		}

	}

	private static QueryResult booleanQuery(IBAHolder holder, BooleanDefinition bd) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(BooleanValue.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, BooleanValue.class, "definitionReference.key.id", bd);
		QuerySpecUtils.toEqualsAnd(query, idx, BooleanValue.class, "theIBAHolderReference.key.id", holder);
		return PersistenceHelper.manager.find(query);
	}

	private static QueryResult integerQuery(IBAHolder holder, IntegerDefinition id) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IntegerValue.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, IntegerValue.class, "definitionReference.key.id", id);
		QuerySpecUtils.toEqualsAnd(query, idx, IntegerValue.class, "theIBAHolderReference.key.id", holder);
		return PersistenceHelper.manager.find(query);
	}

	private static QueryResult floatQuery(IBAHolder holder, FloatDefinition fd) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FloatValue.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, FloatValue.class, "definitionReference.key.id", fd);
		QuerySpecUtils.toEqualsAnd(query, idx, FloatValue.class, "theIBAHolderReference.key.id", holder);
		return PersistenceHelper.manager.find(query);
	}

	private static QueryResult stringQuery(IBAHolder holder, StringDefinition sd) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringValue.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, StringValue.class, "definitionReference.key.id", sd);
		QuerySpecUtils.toEqualsAnd(query, idx, StringValue.class, "theIBAHolderReference.key.id", holder);
		return PersistenceHelper.manager.find(query);
	}

	/**
	 * IBA 속성 생성 및 수정
	 */
	public static void createIBA(IBAHolder holder, String attrName, String value, String type) throws Exception {

		QueryResult qr = null;
		if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
			StringDefinition sd = getStringDefinition(attrName);
			qr = stringQuery(holder, sd);
		} else if ("f".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type)) {
			FloatDefinition fd = getFloatDefinition(attrName);
			qr = floatQuery(holder, fd);
		} else if ("i".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
			IntegerDefinition id = getIntegerDefinition(attrName);
			qr = integerQuery(holder, id);
		} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			BooleanDefinition bd = getBooleanDefinition(attrName);
			qr = booleanQuery(holder, bd);
		}

		// 값이 없으면 생성
		if (qr.size() == 0) {
			if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
				StringValue sv = new StringValue();
				StringDefinition sd = getStringDefinition(attrName);
				if (sd == null)
					return;

				sv.setValue(value);
				sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
				sv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
				PersistenceHelper.manager.save(sv);
			} else if ("f".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type)) {
				FloatValue fv = new FloatValue();
				FloatDefinition fd = getFloatDefinition(attrName);
				if (fd == null)
					return;

				fv.setValue(Float.parseFloat(value));
				fv.setDefinitionReference((FloatDefinitionReference) fd.getAttributeDefinitionReference());
				fv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
				PersistenceHelper.manager.save(fv);
			} else if ("i".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
				IntegerValue iv = new IntegerValue();
				IntegerDefinition id = getIntegerDefinition(attrName);
				if (id == null)
					return;

				iv.setValue(Integer.parseInt(value));
				iv.setDefinitionReference((IntegerDefinitionReference) id.getAttributeDefinitionReference());
				iv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
				PersistenceHelper.manager.save(iv);
			} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
				BooleanValue bv = new BooleanValue();
				BooleanDefinition bd = getBooleanDefinition(attrName);
				if (bd == null)
					return;

				bv.setValue(Boolean.parseBoolean(value));
				bv.setDefinitionReference((BooleanDefinitionReference) bd.getAttributeDefinitionReference());
				bv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
				PersistenceHelper.manager.save(bv);
			}
		}

		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			if (obj[0] instanceof StringValue) {
				StringValue s = (StringValue) obj[0];
				s.setValue(value);
				PersistenceHelper.manager.modify(s);
			} else if (obj[0] instanceof FloatValue) {
				FloatValue f = (FloatValue) obj[0];
				f.setValue(Float.parseFloat(value));
				PersistenceHelper.manager.modify(f);
			} else if (obj[0] instanceof IntegerValue) {
				IntegerValue i = (IntegerValue) obj[0];
				i.setValue(Integer.parseInt(value));
				PersistenceHelper.manager.modify(i);
			} else if (obj[0] instanceof BooleanValue) {
				BooleanValue b = (BooleanValue) obj[0];
				b.setValue(Boolean.parseBoolean(value));
				PersistenceHelper.manager.modify(b);
			}
		}
	}

	/**
	 * IBA 속성 중복 생성
	 */
	public static void appendIBA(IBAHolder holder, String attrName, String value, String type) throws Exception {

		if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
			StringValue sv = new StringValue();
			StringDefinition sd = getStringDefinition(attrName);
			if (sd == null)
				return;

			sv.setValue(value);
			sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
			sv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
			PersistenceHelper.manager.save(sv);
		} else if ("f".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type)) {
			FloatValue fv = new FloatValue();
			FloatDefinition fd = getFloatDefinition(attrName);
			if (fd == null)
				return;

			fv.setValue(Float.parseFloat(value));
			fv.setDefinitionReference((FloatDefinitionReference) fd.getAttributeDefinitionReference());
			fv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
			PersistenceHelper.manager.save(fv);
		} else if ("i".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
			IntegerValue iv = new IntegerValue();
			IntegerDefinition id = getIntegerDefinition(attrName);
			if (id == null)
				return;

			iv.setValue(Integer.parseInt(value));
			iv.setDefinitionReference((IntegerDefinitionReference) id.getAttributeDefinitionReference());
			iv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
			PersistenceHelper.manager.save(iv);
		} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			BooleanValue bv = new BooleanValue();
			BooleanDefinition bd = getBooleanDefinition(attrName);
			if (bd == null)
				return;

			bv.setValue(Boolean.parseBoolean(value));
			bv.setDefinitionReference((BooleanDefinitionReference) bd.getAttributeDefinitionReference());
			bv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(holder)));
			PersistenceHelper.manager.save(bv);
		}
	}

	private static BooleanDefinition getBooleanDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(BooleanDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, BooleanDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			BooleanDefinition bd = (BooleanDefinition) obj[0];
			return bd;
		}
		return null;
	}

	private static IntegerDefinition getIntegerDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IntegerDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, IntegerDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			IntegerDefinition id = (IntegerDefinition) obj[0];
			return id;
		}
		return null;
	}

	private static FloatDefinition getFloatDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FloatDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, FloatDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			FloatDefinition fv = (FloatDefinition) obj[0];
			return fv;
		}
		return null;
	}

	private static StringDefinition getStringDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, StringDefinition.class, "name", attrName);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			StringDefinition sv = (StringDefinition) obj[0];
			return sv;
		}
		return null;
	}

	/**
	 * IBA 값 삭제후 생성
	 */
	public static void updateIBA(IBAHolder holder, String attrName, String value, String type) throws Exception {
		deleteIBA(holder, attrName, type);
		createIBA(holder, attrName, value, type);
	}
}
