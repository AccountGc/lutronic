/*
 * @(#) IBAUtil.java  Create on 2004. 9. 8.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.common.iba;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import wt.clients.iba.container.NewValueCreator;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.csm.navigation.litenavigation.LikeQFElementValueDefaultView;
import wt.csm.navigation.litenavigation.RangeQFElementValueDefaultView;
import wt.csm.query.CSMQueryException;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.IdentificationObject;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.CabinetBased;
import wt.iba.constraint.IBAConstraintException;
import wt.iba.definition.AttributeDefinitionReference;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.BooleanDefinitionReference;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.FloatDefinitionReference;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.IntegerDefinitionReference;
import wt.iba.definition.RatioDefinitionReference;
import wt.iba.definition.ReferenceDefinitionReference;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.StringDefinitionReference;
import wt.iba.definition.TimestampDefinitionReference;
import wt.iba.definition.URLDefinitionReference;
import wt.iba.definition.UnitDefinitionReference;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerNodeView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.AttributeOrgNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IBAUtility;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.IBAValueException;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.part.WTPart;
import wt.part.WTPartMasterIdentity;
import wt.query.ArrayExpression;
import wt.query.AttributeRange;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.RangeExpression;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.query.specification.AttributeSearchExp;
import wt.query.specification.AttributeValueCriteria;
import wt.query.specification.BinaryOperator;
import wt.query.specification.BinaryValue;
import wt.query.specification.InstanceAttributeIdentifier;
import wt.query.specification.NaryOperator;
import wt.query.specification.NaryValue;
import wt.query.specification.OperatorType;
import wt.query.specification.UnaryOperator;
import wt.query.specification.UnaryValue;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.QuerySpecUtils;

public class IBAUtil {
	/**
	 * 해당 객체의 속성 컨테이너를 반환한다.
	 *
	 * @param attrs  key가 속성이름이고, value가 속성객체인 Hashtable
	 * @param values key가 속성이름이고, value가 저장할 속성값인 Hashtable
	 * @return 속성값이 저장된 컨테이너를 반환
	 * @throws IBAConstraintException
	 * @throws WTPropertyVetoException
	 */
	public static DefaultAttributeContainer getAttrContainer(Hashtable attrs, Hashtable values)
			throws IBAConstraintException, WTPropertyVetoException {
		DefaultAttributeContainer container = new DefaultAttributeContainer();
		Enumeration eu = attrs.keys();

		String key = null;
		Object value = null;
		AttributeDefDefaultView defDefaultView;
		AbstractValueView valueView;

		while (eu.hasMoreElements()) {
			key = (String) eu.nextElement();
			value = values.get(key);
			defDefaultView = (AttributeDefDefaultView) attrs.get(key);

			if (defDefaultView != null && value != null) {
				valueView = (AbstractValueView) NewValueCreator.createNewValueObject(defDefaultView);
				setContainerValue(container, valueView, value);
			}
		}
		return container;
	}

	/**
	 * 컨테이너에 각각의 속성에 값을 저장한다.
	 *
	 * @param container
	 * @param valueView
	 * @param valObj
	 * @throws WTPropertyVetoException
	 * @throws IBAConstraintException
	 */
	private static void setContainerValue(DefaultAttributeContainer container, AbstractValueView valueView,
			Object valObj) throws WTPropertyVetoException, IBAConstraintException {
		if (valueView instanceof StringValueDefaultView) {
			StringValueDefaultView view = (StringValueDefaultView) valueView;
			view.setValue((String) valObj);
			container.addAttributeValue(view);
		} else if (valueView instanceof FloatValueDefaultView) {
			FloatValueDefaultView view = (FloatValueDefaultView) valueView;
			view.setValue(Double.parseDouble((String) valObj));
			container.addAttributeValue(view);
		} else if (valueView instanceof TimestampValueDefaultView) {
			TimestampValueDefaultView view = (TimestampValueDefaultView) valueView;
			view.setValue((Timestamp) valObj);
			container.addAttributeValue(view);
		} else if (valueView instanceof BooleanValueDefaultView) {
			BooleanValueDefaultView view = (BooleanValueDefaultView) valueView;
			view.setValue(Boolean.parseBoolean((String) valObj));
			container.addAttributeValue(view);
		}
	}

	private static AttributeDefDefaultView getDefaultViewObject(Object obj) throws Exception {
		AttributeDefDefaultView defDefaultView = null;
		if (obj instanceof AttributeDefNodeView)
			defDefaultView = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) obj);
		return defDefaultView;
	}

	public static boolean getBooleanValue(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		boolean returnStr = false;
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(BooleanValue.class, false);
		int idx_StrDef = select.addClassList(BooleanDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(BooleanValue.class, BooleanValue.VALUE), new int[] { idx_StrVal },
				false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(BooleanValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(BooleanValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(BooleanDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(BooleanDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			BigDecimal bd = (BigDecimal) obj[0];
			returnStr = bd.intValue() == 1;
		}

		return returnStr;
	}

	public static String getAttrValue2(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value2"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}
		return returnStr;
	}

	public static String getAttrValue_Part_Desc(IBAHolder ibaHolder, String attrName)
			throws RemoteException, WTException {
		String returnStr = "";
		if (ibaHolder == null) {
			return returnStr;
		}
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		select.appendOrderBy(new OrderBy(new ClassAttribute(StringValue.class, "thePersistInfo.modifyStamp"), true),
				new int[] { idx_StrVal });
		// System.out.println("IBA select : "+select);
		QueryResult re = PersistenceHelper.manager.find(select);
		if (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}
		return returnStr;
	}

	public static String getAttrValue(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		if (ibaHolder == null) {
			return returnStr;
		}
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}
		return returnStr;
	}

	/**
	 * Float
	 * 
	 * @param ibaHolder
	 * @param attrName
	 * @return
	 * @throws RemoteException
	 * @throws WTException
	 */
	public static String getAttrfloatValue(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		if (ibaHolder == null) {
			return returnStr;
		}
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(FloatValue.class, false);
		int idx_StrDef = select.addClassList(FloatDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(FloatValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(FloatValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(FloatValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(FloatDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(FloatDefinition.class, "name", "=", attrName), new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			BigDecimal oo = (BigDecimal) obj[0];
			returnStr = String.valueOf(oo);
		}
		return returnStr;
	}

	/**
	 * Integer
	 * 
	 * @param ibaHolder
	 * @param attrName
	 * @return
	 * @throws RemoteException
	 * @throws WTException
	 */
	public static String getAttrIntegerValue(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		if (ibaHolder == null) {
			return returnStr;
		}
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(IntegerValue.class, false);
		int idx_StrDef = select.addClassList(IntegerDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(IntegerValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(IntegerValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(IntegerValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(IntegerDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			BigDecimal oo = (BigDecimal) obj[0];
			returnStr = String.valueOf(oo);
		}
		return returnStr;
	}

	/**
	 *
	 * @param ibaHolder
	 * @return key:attr_name,value:attr_value
	 * @throws RemoteException
	 * @throws WTException
	 */
	public static HashMap getAttributes(IBAHolder ibaHolder) throws RemoteException, WTException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();

		HashMap hash = new HashMap();
		if (container == null)
			return hash;

		AbstractValueView[] avv = container.getAttributeValues();
		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				hash.put(sv.getDefinition().getName(), sv.getValueAsString());
			} else if (avv[i] instanceof FloatValueDefaultView) {
				FloatValueDefaultView fv = (FloatValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getName(), fv.getValueAsString());
			} else if (avv[i] instanceof TimestampValueDefaultView) {
				TimestampValueDefaultView fv = (TimestampValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getName(), fv.getValueAsString().substring(0, 10));
			} else if (avv[i] instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView fv = (BooleanValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getName(), fv.getValueAsString());
			}
		}
		return hash;
	}

	public static HashMap getAttributesHidNViewObject(IBAHolder ibaHolder)
			throws RemoteException, WTException, NumberFormatException, WTPropertyVetoException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		HashMap hash = new HashMap();
		if (container == null)
			return hash;

		AbstractValueView[] avv = container.getAttributeValues();
		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				hash.put(sv.getDefinition().getHierarchyID(), sv);
			} else if (avv[i] instanceof FloatValueDefaultView) {
				FloatValueDefaultView fv = (FloatValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getHierarchyID(), fv);
			} else if (avv[i] instanceof TimestampValueDefaultView) {
				TimestampValueDefaultView fv = (TimestampValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getHierarchyID(), fv);
			} else if (avv[i] instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView fv = (BooleanValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getHierarchyID(), fv);
			}
		}
		return hash;
	}

	/* tsum 2013.04.16 */
	/**
	 * 
	 * @param ibaHolder
	 * @param varName
	 * @param value
	 * @param ibaType
	 */
	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value, String ibaType) {

		try {
			if (value == null) {
				return;
			} else if (value.length() == 0) {
				deleteIBA(ibaHolder, varName, ibaType);
				return;
			}
			Object obj = getIBAObject(ibaHolder, varName, ibaType);
			if (obj == null) {
				if ("string".equals(ibaType)) {
					createIba(ibaHolder, "string", varName, value);
				} else if ("float".equals(ibaType)) {
					createIba(ibaHolder, "float", varName, value);
				} else if ("boolean".equals(ibaType)) {
					createIba(ibaHolder, "boolean", varName, value);
				} else if ("integer".equals(ibaType)) {
					createIba(ibaHolder, "integer", varName, value);
				}
			} else {
				if (obj instanceof StringValue) {
					StringValue sv = (StringValue) obj;
					sv.setValue(value);
					PersistenceHelper.manager.modify(sv);
				} else if (obj instanceof FloatValue) {
					FloatValue fv = (FloatValue) obj;
					fv.setValue(Double.parseDouble(value));
					PersistenceHelper.manager.modify(fv);
				} else if (obj instanceof BooleanValue) {
					boolean boolval = value.equals("Y") ? true : false;
					BooleanValue bv = (BooleanValue) obj;
					bv.setValue(boolval);
					PersistenceHelper.manager.modify(bv);
				} else if (obj instanceof BooleanValue) {
					IntegerValue fv = (IntegerValue) obj;
					fv.setValue(Integer.parseInt(value));
					PersistenceHelper.manager.modify(fv);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getIBAObject(IBAHolder ibaHolder, String varName, String ibaType) {

		Object obj = null;
		try {
			Class cls = null;
			Class cls2 = null;
			ClassAttribute ca = null;
			ClassAttribute ca2 = null;
			QuerySpec qs = new QuerySpec();

			if ("string".equals(ibaType)) {
				cls = StringValue.class;
				cls2 = StringDefinition.class;
			} else if ("float".equals(ibaType)) {
				cls = FloatValue.class;
				cls2 = FloatDefinition.class;
			} else if ("boolean".equals(ibaType)) {
				cls = BooleanValue.class;
				cls2 = BooleanDefinition.class;
			} else if ("integer".equals(ibaType)) {
				cls = IntegerValue.class;
				cls2 = IntegerDefinition.class;
			} else {
				return obj;
			}

			long longOid = 0;
			if (ibaHolder instanceof WTPart) {
				WTPart part = (WTPart) ibaHolder;
				longOid = CommonUtil.getOIDLongValue(part);
			} else if (ibaHolder instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) ibaHolder;
				longOid = CommonUtil.getOIDLongValue(epm);
			} else if (ibaHolder instanceof WTDocument) {
				WTDocument doc = (WTDocument) ibaHolder;
				longOid = CommonUtil.getOIDLongValue(doc);

			} else {
				return obj;
			}

			int idx = qs.appendClassList(cls, true);
			int idx2 = qs.appendClassList(cls2, false);

			ca = new ClassAttribute(cls, "definitionReference.key.id");
			ca2 = new ClassAttribute(cls2, "thePersistInfo.theObjectIdentifier.id");
			SearchCondition sc2 = new SearchCondition(ca, "=", ca2);
			qs.appendWhere(sc2, new int[] { idx, idx2 });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "theIBAHolderReference.key.id", SearchCondition.EQUAL, longOid),
					new int[] { idx });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls2, "name", SearchCondition.EQUAL, varName), new int[] { idx2 });
			QueryResult rt = PersistenceHelper.manager.find(qs);

			while (rt.hasMoreElements()) {
				Object[] objs = (Object[]) rt.nextElement();
				obj = objs[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;
	}
	/* tsum 2013.04.16 */

	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value)
			throws NumberFormatException, RemoteException, WTPropertyVetoException, WTException {
		if (value == null) {
			return;
		} else if (value.length() == 0) {
			deleteIBA(ibaHolder, varName);
			return;
		}
		// HashMap map = getAttributes(ibaHolder);
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(varName);
		// AbstractValueView valueView = (AbstractValueView)
		// NewValueCreator.createNewValueObject(aview);

		HashMap my = getAttributesHidNViewObject(ibaHolder);
		String hid = IBAUtility.numericID(varName);

		if (!my.containsKey(hid)) {
			if (aview instanceof StringDefView)
				createIba(ibaHolder, "string", varName, value);
			else if (aview instanceof FloatDefView)
				createIba(ibaHolder, "float", varName, value);
			else if (aview instanceof BooleanDefView)
				createIba(ibaHolder, "boolean", varName, value);
		} else {
			Object obj = my.get(hid);
			if (obj instanceof StringValueDefaultView) {

				StringValueDefaultView sv = (StringValueDefaultView) obj;
				ReferenceFactory rf = new ReferenceFactory();
				StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
				s.setValue(value);
				PersistenceHelper.manager.modify(s);

			} else if (obj instanceof FloatValueDefaultView) {
				FloatValueDefaultView sv = (FloatValueDefaultView) obj;
				ReferenceFactory rf = new ReferenceFactory();
				FloatValue s = (FloatValue) rf.getReference(sv.getObjectID().toString()).getObject();
				s.setValue(Double.parseDouble(value));
				PersistenceHelper.manager.modify(s);

			} else if (obj instanceof BooleanValueDefaultView) {
				boolean boolval = value.equals("Y") ? true : false;
				BooleanValueDefaultView sv = (BooleanValueDefaultView) obj;
				ReferenceFactory rf = new ReferenceFactory();
				BooleanValue s = (BooleanValue) rf.getReference(sv.getObjectID().toString()).getObject();
				s.setValue(boolval);
				PersistenceHelper.manager.modify(s);
			}
		}
	}

	/**
	 * IBA 속성을 삭제한다.
	 *
	 * @param ibaHolder
	 * @param varName   속성명
	 * @throws NumberFormatException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void deleteIBA(IBAHolder ibaHolder, String varName)
			throws NumberFormatException, RemoteException, WTPropertyVetoException, WTException {
		if (ibaHolder == null) {
			return;
		}
		HashMap my = getAttributesHidNViewObject(ibaHolder);
		String hid = IBAUtility.numericID(varName);
		if (my.containsKey(hid)) {
			Object obj = my.get(hid);
			ReferenceFactory rf = new ReferenceFactory();
			if (obj instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) obj;
				StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);

			} else if (obj instanceof FloatValueDefaultView) {
				FloatValueDefaultView sv = (FloatValueDefaultView) obj;
				FloatValue s = (FloatValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);

			} else if (obj instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView sv = (BooleanValueDefaultView) obj;
				BooleanValue s = (BooleanValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);
			}
		}
	}

	public static void deleteIBA(IBAHolder ibaHolder, String varName, String ibaType) {
		try {

			Object obj = getIBAObject(ibaHolder, varName, ibaType);
			if (obj != null) {
				if (obj instanceof StringValue) {
					StringValue sv = (StringValue) obj;
					PersistenceHelper.manager.delete(sv);
				} else if (obj instanceof FloatValue) {
					FloatValue fv = (FloatValue) obj;
					PersistenceHelper.manager.delete(fv);
				} else if (obj instanceof BooleanValue) {
					BooleanValue bv = (BooleanValue) obj;
					PersistenceHelper.manager.delete(bv);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupQuerySpecForInstance(QuerySpec query, AttributeSearchExp searchExp, int idx) {
		try {
			Class target = null;
			String s2 = "value";
			AttributeDefinitionReference attrDefRef = ((InstanceAttributeIdentifier) searchExp.getIdentifier())
					.getAttributeDefinition();
			boolean flag = ((InstanceAttributeIdentifier) searchExp.getIdentifier()).isDescendHierarchy();
			if (attrDefRef instanceof StringDefinitionReference) {
				target = wt.iba.value.StringValue.class;
			} else if (attrDefRef instanceof IntegerDefinitionReference) {
				target = wt.iba.value.IntegerValue.class;
			} else if (attrDefRef instanceof FloatDefinitionReference) {
				target = wt.iba.value.FloatValue.class;
			} else if (attrDefRef instanceof BooleanDefinitionReference) {
				target = wt.iba.value.BooleanValue.class;
			} else if (attrDefRef instanceof RatioDefinitionReference) {
				target = wt.iba.value.RatioValue.class;
			} else if (attrDefRef instanceof TimestampDefinitionReference) {
				target = wt.iba.value.TimestampValue.class;
			} else if (attrDefRef instanceof UnitDefinitionReference) {
				target = wt.iba.value.UnitValue.class;
			} else if (attrDefRef instanceof URLDefinitionReference) {
				target = wt.iba.value.URLValue.class;
			} else if (attrDefRef instanceof ReferenceDefinitionReference) {
				target = wt.iba.value.ReferenceValue.class;
				s2 = "theIBAReferenceableReference.referenceID";
			} else {
				// System.out.println("This type is currently not supported!");
				// System.out.println("Type : " + attrDefRef.getClass().getName());
			}
			query.appendWhere(getDefInstancedAttrSC(target, attrDefRef.getHierarchyID(), flag), new int[] { idx });
			query.appendAnd();
			query.appendWhere(getValueInstancedAttrSC(target, s2, searchExp), new int[] { idx });
		} catch (QueryException e) {
			e.printStackTrace();
		}
	}

	private static SearchCondition getDefInstancedAttrSC(Class target, String hid, boolean flag) {
		SearchCondition where = null;
		try {
			// where = new SearchCondition(new ClassAttribute(target,
			// "definitionReference.hierarchyID"), flag ? " LIKE "
			// : "=", ConstantExpression.newExpression(hid + (flag ? "%" :
			// "")));
			where = new SearchCondition(new ClassAttribute(target, "definitionReference.hierarchyID"), "=",
					ConstantExpression.newExpression(hid));
		} catch (QueryException e) {
			e.printStackTrace();
		}
		return where;
	}

	private static SearchCondition getValueInstancedAttrSC(Class target, String s, AttributeSearchExp searchExp) {
		SearchCondition where = null;
		// AttributeDefinitionReference attrDefRef =
		// ((InstanceAttributeIdentifier) searchExp.getIdentifier())
		// .getAttributeDefinition();
		// AttributeValueCriteria valueCriteria = searchExp.getValue();
		try {
			Object obj = new ClassAttribute(target, s);
			try {
				// if (!((CaseSensitiveValue)
				// searchExp.getValue()).isCaseSensitive())
				// {
				// SQLFunction sqlFunc = SQLFunction.newSQLFunction("UPPER");
				// sqlFunc.setArgumentAt((RelationalExpression) obj, 0);
				// obj = sqlFunc;
				// }
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			where = new SearchCondition((RelationalExpression) obj, getOperator(searchExp), getRelationExp(searchExp));

			if (searchExp instanceof LikeQFElementValueDefaultView) {
				UnaryOperator unaryOper = (UnaryOperator) searchExp.getOperator();
				if (((LikeQFElementValueDefaultView) searchExp).getQueryOperator() != 3
						&& (unaryOper.getLikeEscapeString() != null
								&& (unaryOper == UnaryOperator.LIKE || unaryOper == UnaryOperator.NOT_LIKE)))
					where.setOption("ESCAPE '" + unaryOper.getLikeEscapeString() + "'");
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (QueryException e1) {
			e1.printStackTrace();
		} catch (ClassCastException e3) {
			e3.printStackTrace();
		}
		return where;
	}

	private static String getOperator(AttributeSearchExp searchExp) {
		OperatorType operType = searchExp.getOperator();
		String oper = null;
		if (operType.equals(UnaryOperator.EQUAL))
			oper = "=";
		else if (operType.equals(UnaryOperator.GREATER))
			oper = ">";
		else if (operType.equals(UnaryOperator.GREATER_EQUAL))
			oper = ">=";
		else if (operType.equals(UnaryOperator.IS_NOT_NULL))
			oper = "IS NOT NULL";
		else if (operType.equals(UnaryOperator.IS_NULL))
			oper = "IS NULL";
		else if (operType.equals(UnaryOperator.LESS))
			oper = "<";
		else if (operType.equals(UnaryOperator.LESS_EQUAL))
			oper = "<=";
		else if (operType.equals(UnaryOperator.LIKE))
			oper = " LIKE ";
		else if (operType.equals(UnaryOperator.NOT_EQUAL))
			oper = "<>";
		else if (operType.equals(UnaryOperator.NOT_LIKE))
			oper = " NOT LIKE ";
		else if (operType.equals(BinaryOperator.BETWEEN))
			oper = "BETWEEN";
		else if (operType.equals(BinaryOperator.NOT_BETWEEN))
			oper = "NOT BETWEEN";
		else if (operType.equals(NaryOperator.IN))
			oper = "IN";
		else if (operType.equals(NaryOperator.NOT_IN))
			oper = "NOT IN";
		return oper;
	}

	private static RelationalExpression getRelationExp(AttributeSearchExp searchExp) {
		Object obj = null;
		AttributeValueCriteria attrValueCriteria = searchExp.getValue();
		if (attrValueCriteria instanceof UnaryValue) {
			Object obj1 = ((UnaryValue) attrValueCriteria).getValue();
			if (obj1 instanceof ObjectReference)
				obj = new ConstantExpression(new Long(((ObjectIdentifier) ((ObjectReference) obj1).getKey()).getId()));
			else
				try {
					obj = (RelationalExpression) obj1;
				} catch (ClassCastException e) {
					try {
						obj = new ConstantExpression(obj1);
					} catch (ClassCastException e1) {
						e1.printStackTrace();
					}
				}
		} else if (attrValueCriteria instanceof BinaryValue) {
			BinaryValue binaryvalue = (BinaryValue) attrValueCriteria;
			if (attrValueCriteria instanceof AttributeRange)
				obj = new RangeExpression((AttributeRange) attrValueCriteria);
			else
				try {
					AttributeRange attributerange = new AttributeRange();
					attributerange.setStart(binaryvalue.getStart());
					attributerange.setEnd(binaryvalue.getEnd());
					obj = new RangeExpression(attributerange);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
		} else if (attrValueCriteria instanceof NaryValue)
			obj = new ArrayExpression(((NaryValue) attrValueCriteria).getValue());
		return (RelationalExpression) obj;
	}

	/**
	 * EPMDocument의 번호와 이름을 변경한다.
	 *
	 * @param epm
	 * @param number
	 * @param name
	 * @param cadName
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void changeNumber(EPMDocument epm, String number, String name, String cadName)
			throws WTException, WTPropertyVetoException {
		// String pre_number = epm.getNumber();
		// String pre_name = epm.getName();
		String pre_cadName = epm.getCADName();
		// Logger.user.println("> IBAUtil.changeNumber : EPM no = " + epm.getNumber());
		// Logger.user.println("> IBAUtil.changeNumber : cadName = " + pre_cadName);

		boolean isChange = false;
		if (!epm.getNumber().equals(number) || !epm.getName().equals(name))
			isChange = true;

		if (isChange) {
			Identified identified = (Identified) epm.getMaster();

			EPMDocumentMasterIdentity emid = (EPMDocumentMasterIdentity) identified.getIdentificationObject();
			emid.setNumber(number.trim());
			emid.setName(name.trim());
			QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData ad = (ApplicationData) item;
					// Logger.user.println("> IBAUtil.changeNumber : ad.getFileName() = " +
					// ad.getFileName());
					if (!pre_cadName.equals(ad.getFileName()))
						EPMDocumentHelper.service.changeCADName((EPMDocumentMaster) epm.getMaster(), ad.getFileName());
				}
			}
			IdentityHelper.service.changeIdentity(identified, emid);
			PersistenceHelper.manager.refresh(epm);
		}
	}

	/**
	 * 부품 및 문서의 번호와 이름을 변경한다.
	 *
	 * @param oid
	 * @param number
	 * @param name
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void changeNumber(String oid, String number, String name)
			throws WTException, WTPropertyVetoException {
		number = number.trim();
		name = name.trim();
		ReferenceFactory rf = new ReferenceFactory();
		CabinetBased cabinetbased = (CabinetBased) rf.getReference(oid).getObject();
		IdentificationObject obj = ((Identified) ((Iterated) cabinetbased).getMaster()).getIdentificationObject();
		boolean flag = !name.equals(cabinetbased.getName()) && name.length() != 0;
		boolean flag1 = number.length() != 0;

		if (obj instanceof WTDocumentMasterIdentity) {
			String tempNumber = ((WTDocumentMasterIdentity) obj).getNumber();
			flag1 = flag1 && !tempNumber.equals(number);
			if (flag)
				((WTDocumentMasterIdentity) obj).setName(name);
			if (flag1)
				((WTDocumentMasterIdentity) obj).setNumber(number);
		} else if (obj instanceof WTPartMasterIdentity) {
			String tempNumber = ((WTPartMasterIdentity) obj).getNumber();
			flag1 = flag1 && !tempNumber.equals(number);
			if (flag)
				((WTPartMasterIdentity) obj).setName(name);
			if (flag1)
				((WTPartMasterIdentity) obj).setNumber(number);
		}
		if (flag || flag1) {
			IdentityHelper.service.changeIdentity((Identified) ((Iterated) cabinetbased).getMaster(), obj);
		}
	}

	public static void createIba(IBAHolder ibaHolder, String type, String attrName, String value) throws WTException {
		if (value == null)
			return;
		try {

			if ("string".equalsIgnoreCase(type)) {
				StringValue sv = new StringValue();
				StringDefinition sd = getStringDefinition(attrName);
				if (sd == null)
					return;

				sv.setValue(value);
				sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
				sv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));
				PersistenceHelper.manager.save(sv);
			} else if ("float".equalsIgnoreCase(type)) {
				FloatValue fv = new FloatValue();
				FloatDefinition sd = getFloatDefinition(attrName);
				if (sd == null)
					return;

				fv.setValue(Double.parseDouble(value));
				fv.setDefinitionReference((FloatDefinitionReference) sd.getAttributeDefinitionReference());
				fv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));

				PersistenceHelper.manager.save(fv);
			} else if ("boolean".equalsIgnoreCase(type)) {
				BooleanValue bv = new BooleanValue();
				BooleanDefinition sd = getBooleanDefinition(attrName);
				if (sd == null)
					return;
				boolean boolval = value.equals("Y") ? true : false;
				bv.setValue(boolval);
				bv.setDefinitionReference((BooleanDefinitionReference) sd.getAttributeDefinitionReference());
				bv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));

				PersistenceHelper.manager.save(bv);
			} else if ("integer".equalsIgnoreCase(type)) {
				IntegerValue bv = new IntegerValue();
				IntegerDefinition sd = getIntegerDefinition(attrName);
				if (sd == null)
					return;
				int intval = Integer.getInteger(value);
				bv.setValue(intval);
				bv.setDefinitionReference((IntegerDefinitionReference) sd.getAttributeDefinitionReference());
				bv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	private static StringDefinition getStringDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(StringDefinition.class, true);
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			StringDefinition sv = (StringDefinition) obj[0];

			return sv;
		}
		return null;
	}

	private static FloatDefinition getFloatDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(FloatDefinition.class, true);
		select.appendWhere(new SearchCondition(FloatDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			FloatDefinition sv = (FloatDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static BooleanDefinition getBooleanDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(BooleanDefinition.class, true);
		select.appendWhere(new SearchCondition(BooleanDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			BooleanDefinition sv = (BooleanDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static IntegerDefinition getIntegerDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(IntegerDefinition.class, true);
		select.appendWhere(new SearchCondition(IntegerDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			IntegerDefinition sv = (IntegerDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static double parseDouble(String str) {
		double dd;
		try {
			dd = Double.parseDouble(str);
		} catch (Exception e) {
			dd = 0;
		}
		return dd;
	}

	public static Hashtable settingAttribute(String orgName) {
		Hashtable hash = null;
		// if (orgName.equals("Part") || orgName.equals("PRODUCT"))
		// {
		// hash = settingAttribute("SHARE");
		// }
		// if (hash == null)
		hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		String hid = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				String nodeName = orgNodeView[i].getName();
				if (nodeName.equalsIgnoreCase(orgName)) {
					AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
							.getAttributeChildren(orgNodeView[i]);
					// softtype attributes
					for (int j = 0; j < defNodeView.length; j++) {
						aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
						valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
						hid = aview.getHierarchyID();
						if (valueView instanceof UnitValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getLocalizedDisplayString(), aview,
									((UnitValueDefaultView) valueView).toUnit().getUnits()));
						} else if (valueView instanceof StringValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"string"));
						} else if (valueView instanceof FloatValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"float"));
						} else if (valueView instanceof TimestampValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"timestamp"));
						} else if (valueView instanceof BooleanValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"boolean"));
						}
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	/**
	 * 특정 속성 노드에 연결된 속성을 찾는다.
	 *
	 * @param orgName
	 * @return key:속성이름,value:AttributeData
	 */
	public static Hashtable getIBAAttributes(String orgName) {
		Hashtable hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				String nodeName = orgNodeView[i].getName();
				if (nodeName.equalsIgnoreCase(orgName)) {
					AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
							.getAttributeChildren(orgNodeView[i]);
					// softtype attributes
					for (int j = 0; j < defNodeView.length; j++) {
						aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
						valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
						if (valueView instanceof UnitValueDefaultView)
							hash.put(aview.getName(),
									new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
											((UnitValueDefaultView) valueView).toUnit().getUnits()));
						else if (valueView instanceof StringValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "string"));
						else if (valueView instanceof FloatValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "float"));
						else if (valueView instanceof TimestampValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "timestamp"));
						else if (valueView instanceof BooleanValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "boolean"));
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	/**
	 * 모든 IBA 리턴
	 * 
	 * @return
	 */
	public static Hashtable getIBAAttributes() {
		Hashtable hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
						.getAttributeChildren(orgNodeView[i]);
				for (int j = 0; j < defNodeView.length; j++) {
					aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
					valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
					if (valueView instanceof UnitValueDefaultView)
						hash.put(aview.getName(), new AttributeData(aview.getName(), aview.getLocalizedDisplayString(),
								aview, ((UnitValueDefaultView) valueView).toUnit().getUnits()));
					else if (valueView instanceof StringValueDefaultView)
						hash.put(aview.getName(),
								new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview, "string"));
					else if (valueView instanceof FloatValueDefaultView)
						hash.put(aview.getName(),
								new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview, "float"));
					else if (valueView instanceof TimestampValueDefaultView)
						hash.put(aview.getName(), new AttributeData(aview.getName(), aview.getLocalizedDisplayString(),
								aview, "timestamp"));
					else if (valueView instanceof BooleanValueDefaultView)
						hash.put(aview.getName(), new AttributeData(aview.getName(), aview.getLocalizedDisplayString(),
								aview, "boolean"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	public static Hashtable setValueToAttributeData(Hashtable ibaHash, Hashtable values) {
		String key = null;
		AttributeData adata = null;
		Enumeration eu = ibaHash.keys();
		while (eu.hasMoreElements()) {
			key = (String) eu.nextElement();
			adata = (AttributeData) ibaHash.get(key);
			adata.value = (String) values.get(key);
		}
		return ibaHash;
	}

	public static DefaultAttributeContainer updateAttribute(IBAHolder ibaHolder, Hashtable hash) {
		DefaultAttributeContainer container = null;
		try {
			IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
			container = (DefaultAttributeContainer) iba.getAttributeContainer();
			if (container == null)
				container = new DefaultAttributeContainer();

			AttributeDefDefaultView definitions[] = container.getAttributeDefinitions();
			Enumeration eu = hash.keys();

			String key = null;
			AttributeData data = null;
			String dataType = null;
			AttributeDefDefaultView aview = null;
			while (eu.hasMoreElements()) {
				key = (String) eu.nextElement();
				data = (AttributeData) hash.get(key);
				if (data.value == null)
					continue;

				dataType = data.dataType;
				// Logger.user.println("> IBAUtil.updateAttribute : dataType = " + dataType + "
				// data.value = " + data.value);

				aview = data.aview;
				boolean flag = false;
				if (definitions != null) {
					// Logger.user.println("> IBAUtil.updateAttribute : definitions.length " +
					// definitions.length);
					for (int i = 0; i < definitions.length; i++) {
						if (definitions[i].isPersistedObjectEqual(aview)) {
							flag = true;
							AbstractValueView aabstractvalueview[] = container.getAttributeValues(definitions[i]);
							if (data.value.trim().length() == 0) {
								container.deleteAttributeValues(definitions[i]);
								break;
							}
							if (dataType.equals("string")) {
								StringValueDefaultView sview = (StringValueDefaultView) aabstractvalueview[0];
								sview.setValue(data.value);
								container.updateAttributeValue(sview);
								break;
							} else if (dataType.equals("float")) {
								FloatValueDefaultView fview = (FloatValueDefaultView) aabstractvalueview[0];
								fview.setValue(parseDouble(data.value));
								container.updateAttributeValue(fview);
								break;
							} else if (dataType.equals("timestamp")) {
								TimestampValueDefaultView view = (TimestampValueDefaultView) aabstractvalueview[0];
								view.setValue(DateUtil.convertDate(data.value));
								container.updateAttributeValue(view);
								break;
							} else if (dataType.equals("boolean")) {
								BooleanValueDefaultView view = (BooleanValueDefaultView) aabstractvalueview[0];
								view.setValue(Boolean.parseBoolean(data.value));
								container.updateAttributeValue(view);
								break;
							} else {
								UnitValueDefaultView uview = (UnitValueDefaultView) aabstractvalueview[0];
								uview.setValue(parseDouble(data.value));
								container.updateAttributeValue(uview);
								break;
							}
						} // if
					} // for
				} // if

				if (!flag) {
					if (data.value == null || data.value.trim().length() == 0)
						continue;

					if (dataType.equals("string")) {
						StringValueDefaultView sview = (StringValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						sview.setValue(data.value);
						container.addAttributeValue(sview);
					} else if (dataType.equals("float")) {
						FloatValueDefaultView fview = (FloatValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						fview.setValue(parseDouble(data.value));
						container.addAttributeValue(fview);
					} else if (dataType.equals("timestamp")) {
						TimestampValueDefaultView view = (TimestampValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						view.setValue(DateUtil.convertDate(data.value));
						container.addAttributeValue(view);
					} else if (dataType.equals("boolean")) {
						BooleanValueDefaultView view = (BooleanValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						view.setValue(Boolean.parseBoolean(data.value));
						container.addAttributeValue(view);
					} else {
						UnitValueDefaultView uview = (UnitValueDefaultView) NewValueCreator.createNewValueObject(aview);
						uview.setValue(parseDouble(data.value));
						container.addAttributeValue(uview);
					}
				}
			} // while
		} catch (WTException e) {
			e.printStackTrace();
			container = null;
		} catch (RemoteException e) {
			container = null;
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			container = null;
			e.printStackTrace();
		}
		return container;
	}

	public static Hashtable setAttribute(IBAHolder ibaHolder, Hashtable achash) throws RemoteException, WTException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		Hashtable thash = new Hashtable();
		if (container == null)
			return thash;

		AttributeDefDefaultView definitions[] = container.getAttributeDefinitions();

		for (int i = definitions.length - 1; i > -1; i--) {
			String key = definitions[i].getHierarchyID();
			AttributeData data = (AttributeData) achash.get(key);
			if (data == null)
				continue;
			AttributeDefDefaultView av = data.aview;
			if (definitions[i].isPersistedObjectEqual(av)) {
				AbstractValueView aabstractvalueview[] = container.getAttributeValues(definitions[i]);
				if (aabstractvalueview[0] instanceof StringValueDefaultView) {
					StringValueDefaultView bb = (StringValueDefaultView) aabstractvalueview[0];
					// if (bb.getValue() == null)
					// thash.put(key, "");
					// else
					thash.put(key, bb.getValueAsString());
				} else if (aabstractvalueview[0] instanceof FloatValueDefaultView) {
					FloatValueDefaultView bb = (FloatValueDefaultView) aabstractvalueview[0];
					thash.put(key, bb.getValueAsString());
				} else if (aabstractvalueview[0] instanceof BooleanValueDefaultView) {
					BooleanValueDefaultView bb = (BooleanValueDefaultView) aabstractvalueview[0];
					thash.put(key, bb.getValueAsString());
				} else if (aabstractvalueview[0] instanceof UnitValueDefaultView) {
					UnitValueDefaultView bb = (UnitValueDefaultView) aabstractvalueview[0];
					thash.put(key, bb.getValueAsString());
				}
			}
		}
		return thash;
	}

	public static void appendIBAWhere(QuerySpec query, Class target, int idx, Hashtable params, boolean isLike)
			throws QueryException, WTPropertyVetoException, IBAValueException, CSMQueryException {
		Hashtable attr = IBAAttributes.PART;
		Enumeration ee = attr.keys();

		String key = null;
		AttributeData attributeData = null;
		String mode = null;
		AttributeDefDefaultView aview = null;
		int idx_IbaValue = 0;
		Object searchValue = null;
		while (ee.hasMoreElements()) {
			key = (String) ee.nextElement();
			searchValue = params.get(key);

			String[] value = null;
			;
			if (searchValue instanceof String[])
				value = (String[]) searchValue;
			else
				value = new String[] { (String) searchValue };
			if (value[0].length() == 0)
				continue;

			attributeData = (AttributeData) attr.get(key);
			mode = attributeData.dataType;
			if ("string".equals(mode))
				idx_IbaValue = query.addClassList(wt.iba.value.StringValue.class, false);
			else if ("float".equals(mode))
				idx_IbaValue = query.addClassList(wt.iba.value.FloatValue.class, false);
			else if (mode.equals("timestamp"))
				idx_IbaValue = query.addClassList(wt.iba.value.TimestampValue.class, false);
			else if (mode.equals("boolean"))
				idx_IbaValue = query.addClassList(wt.iba.value.BooleanValue.class, false);
			else
				idx_IbaValue = query.addClassList(wt.iba.value.UnitValue.class, false);

			aview = (AttributeDefDefaultView) attributeData.aview;
			if (mode.equals("string")) {
				LikeQFElementValueDefaultView likeQF = new LikeQFElementValueDefaultView();
				StringValueDefaultView stringDefaultView = new StringValueDefaultView((StringDefView) aview);

				if (isLike) {
					likeQF.setQueryOperator(0);
				} else {
					boolean flag1 = value[0].startsWith("*");
					if (flag1)
						value[0] = value[0].substring(1);
					boolean flag2 = value[0].endsWith("*");
					if (flag2)
						value[0] = value[0].substring(0, value[0].length() - 1);
					if (flag1 && flag2)
						likeQF.setQueryOperator(0);
					else if (flag1)
						likeQF.setQueryOperator(2);
					else if (flag2)
						likeQF.setQueryOperator(1);
					else
						likeQF.setQueryOperator(3);
				}

				stringDefaultView.setValue(value[0]);
				likeQF.setValue(stringDefaultView);
				if (query.getConditionCount() > 0)
					query.appendAnd();

				SearchCondition sc = new SearchCondition(
						new ClassAttribute(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
				query.appendWhere(sc, new int[] { idx_IbaValue, idx });
				query.appendAnd();
				setupQuerySpecForInstance(query, likeQF.getSearchExpression(aview, null), idx_IbaValue);

			} else if (mode.equals("float")) {
				RangeQFElementValueDefaultView range = new RangeQFElementValueDefaultView();
				double dd = parseDouble(value[0]);
				FloatValueDefaultView bb = new FloatValueDefaultView((FloatDefView) aview);
				bb.setValue(dd);
				range.setLowValue(bb);

				if (value[1] != null && value[1].length() > 0) {
					dd = parseDouble(value[1]);
					bb = new FloatValueDefaultView((FloatDefView) aview);
					bb.setValue(dd);
					range.setHighValue(bb);
				}

				if (query.getConditionCount() > 0)
					query.appendAnd();
				SearchCondition sc = new SearchCondition(
						new ClassAttribute(wt.iba.value.FloatValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
				query.appendWhere(sc, new int[] { idx_IbaValue, idx });
				query.appendAnd();
				setupQuerySpecForInstance(query, range.getSearchExpression(aview, null), idx_IbaValue);

			} else if (mode.equals("timestamp")) {
				RangeQFElementValueDefaultView range = new RangeQFElementValueDefaultView();
				Timestamp dd = DateUtil.convertStartDate(value[0]);
				TimestampValueDefaultView bb = new TimestampValueDefaultView((TimestampDefView) aview);
				bb.setValue(dd);
				range.setLowValue(bb);

				if (value[1] != null && value[1].length() > 0) {
					dd = DateUtil.convertEndDate(value[1]);
					bb = new TimestampValueDefaultView((TimestampDefView) aview);
					bb.setValue(dd);
					range.setHighValue(bb);
				}
				if (query.getConditionCount() > 0)
					query.appendAnd();
				SearchCondition sc = new SearchCondition(
						new ClassAttribute(wt.iba.value.TimestampValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
				query.appendWhere(sc, new int[] { idx_IbaValue, idx });
				query.appendAnd();
				setupQuerySpecForInstance(query, range.getSearchExpression(aview, null), idx_IbaValue);

			} else if (mode.equals("boolean")) {
				RangeQFElementValueDefaultView range = new RangeQFElementValueDefaultView();
				boolean dd = Boolean.parseBoolean(value[0]);
				BooleanValueDefaultView bb = new BooleanValueDefaultView((BooleanDefView) aview);
				bb.setValue(dd);
				range.setLowValue(bb);

				if (value[1] != null && value[1].length() > 0) {
					dd = Boolean.parseBoolean(value[1]);
					bb = new BooleanValueDefaultView((BooleanDefView) aview);
					bb.setValue(dd);
					range.setHighValue(bb);
				}
				if (query.getConditionCount() > 0)
					query.appendAnd();
				SearchCondition sc = new SearchCondition(
						new ClassAttribute(wt.iba.value.BooleanValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
				query.appendWhere(sc, new int[] { idx_IbaValue, idx });
				query.appendAnd();
				setupQuerySpecForInstance(query, range.getSearchExpression(aview, null), idx_IbaValue);

			} else {
				RangeQFElementValueDefaultView range = new RangeQFElementValueDefaultView();
				double dd = parseDouble(value[0]);
				UnitValueDefaultView bb = new UnitValueDefaultView((UnitDefView) aview);
				bb.setValue(dd);
				range.setLowValue(bb);

				if (value[1] != null && value[1].length() > 0) {
					dd = parseDouble(value[1]);
					bb = new UnitValueDefaultView((UnitDefView) aview);
					bb.setValue(dd);
					range.setHighValue(bb);
				}

				if (query.getConditionCount() > 0)
					query.appendAnd();
				SearchCondition sc = new SearchCondition(
						new ClassAttribute(wt.iba.value.UnitValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
				query.appendWhere(sc, new int[] { idx_IbaValue, idx });
				query.appendAnd();
				setupQuerySpecForInstance(query, range.getSearchExpression(aview, null), idx_IbaValue);
			}
		}
	}

	/**
	 * IBA String 값 가져오기
	 */

	public static String getStringValue(IBAHolder holder, String name) throws Exception {
		String rtn = "";
		if (holder == null) {
			return rtn;
		}
		Class<?> target = holder.getClass();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(target, false);
		int idx_sv = query.appendClassList(StringValue.class, true);
		int idx_sd = query.appendClassList(StringDefinition.class, false);

		QuerySpecUtils.toEqualsAnd(query, idx, target, WTAttributeNameIfc.ID_NAME, holder);
		QuerySpecUtils.toInnerJoin(query, target, StringValue.class, "thePersistInfo.theObjectIdentifier.id",
				"theIBAHolderReference.key.id", idx, idx_sv);
		QuerySpecUtils.toInnerJoin(query, StringValue.class, StringDefinition.class, "definitionReference.hierarchyID",
				"hierarchyID", idx_sv, idx_sd);
		QuerySpecUtils.toEqualsAnd(query, idx_sd, StringDefinition.class, StringDefinition.NAME, name);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			StringValue sv = (StringValue) obj[0];
			rtn = sv.getValue();
		}
		return rtn;
	}
}