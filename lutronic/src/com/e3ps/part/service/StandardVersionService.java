package com.e3ps.part.service;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;

@SuppressWarnings("serial")
public class StandardVersionService extends StandardManager implements VersionService {

	public static StandardVersionService newStandardVersionService() throws Exception {
		final StandardVersionService instance = new StandardVersionService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public boolean isLastVersion(Versioned versioned) {

		try {

			Class cls = null;

			if (versioned instanceof WTPart) {
				cls = WTPart.class;
			} else if (versioned instanceof EPMDocument) {
				cls = EPMDocument.class;
			} else if (versioned instanceof WTDocument) {
				cls = WTDocument.class;
			} else {
				return false;
			}
			long longOid = CommonUtil.getOIDLongValue(versioned);

			QuerySpec qs = new QuerySpec();

			int idx = qs.appendClassList(cls, true);

			// 최신 이터레이션
			qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true),
					new int[] { idx });

			// 최신 버젼
			SearchUtil.addLastVersionCondition(qs, cls, idx);

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longOid), new int[] { idx });
			QueryResult rt = PersistenceHelper.manager.find(qs);

			if (rt.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	@Override
	public boolean isLastIteration(Versioned versioned) {

		try {

			Class cls = null;

			if (versioned instanceof WTPart) {
				cls = WTPart.class;
			} else if (versioned instanceof EPMDocument) {
				cls = EPMDocument.class;
			} else if (versioned instanceof WTDocument) {
				cls = WTDocument.class;
			} else {
				return false;
			}
			long longOid = CommonUtil.getOIDLongValue(versioned);

			QuerySpec qs = new QuerySpec();

			int idx = qs.appendClassList(cls, true);

			// 최신 이터레이션
			qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true),
					new int[] { idx });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longOid), new int[] { idx });
			QueryResult rt = PersistenceHelper.manager.find(qs);

			if (rt.size() > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
