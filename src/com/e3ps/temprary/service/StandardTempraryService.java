package com.e3ps.temprary.service;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTempraryService extends StandardManager implements TempraryService {

	public static StandardTempraryService newStandardTempraryService() throws WTException {
		StandardTempraryService instance = new StandardTempraryService();
		instance.initialize();
		return instance;
	}
