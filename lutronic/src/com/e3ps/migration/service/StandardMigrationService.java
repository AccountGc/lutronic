package com.e3ps.migration.service;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import jxl.Cell;
import jxl.Sheet;
import wt.epm.EPMDocument;
import wt.fc.PersistenceServerHelper;
import wt.part.Quantity;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.rohs.service.StandardRohsService;

@SuppressWarnings("serial")
public class StandardMigrationService extends StandardManager implements MigrationService{
	
	
	public static StandardMigrationService newStandardMigrationService() throws WTException {
		final StandardMigrationService instance = new StandardMigrationService();
		instance.initialize();
		return instance;
	}
	
	
	@Override
	public void  createBom(WTPart parentPart,WTPart childPart,String qstr) throws Exception {
		
		
		
		WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
		
        link.setQuantity(Quantity.newQuantity(Double.parseDouble(qstr), childPart.getDefaultUnit()));
		PersistenceServerHelper.manager.insert(link);
		
	}


	@Override
	public void publish(EPMDocument epm) throws Exception {
		EpmPublishUtil.publish(epm);
	}
}
