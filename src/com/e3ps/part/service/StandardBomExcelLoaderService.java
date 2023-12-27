package com.e3ps.part.service;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.fc.ObjectToObjectLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.occurrence.OccurrenceHelper;
import wt.part.PartUsesOccurrence;
import wt.part.Quantity;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;

public class StandardBomExcelLoaderService extends StandardManager implements BomExcelLoaderService {

	public static StandardBomExcelLoaderService newStandardBomExcelLoaderService() throws Exception {
		final StandardBomExcelLoaderService instance = new StandardBomExcelLoaderService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public Vector<Hashtable> loadData(String fileName) throws WTException {
		String sheetName = "";
		Vector<Hashtable> vec = new Vector<Hashtable>();
		try {
			
			//trx.start();
			
			File file = new File(fileName);
			if(file == null) {
				//System.out.println("File not found!!!");
				return vec;
			}
			
			Workbook wb = JExcelUtil.getWorkbook(file);
            Sheet[] sheets = wb.getSheets();
            int rows = sheets[0].getRows();
			
            vec = loadSheetData(sheets[0]);
			
			
		}catch(Exception e) {
			e.printStackTrace();
			
			//System.out.println("error fileName = " + fileName + " sheetName = " + sheetName);
			//trx.rollback();
			//throw new WTException("fileName = " + fileName + " sheetName = " + sheetName);
		}
		finally {
			//if(trx != null) {
				//trx = null;
			//}	
			
		}
		
		return vec;
	}
	
	public static Vector<Hashtable>  loadSheetData(Sheet sheet) throws Exception {
		
		Vector<Hashtable> vec = new Vector<Hashtable>();
		//System.out.println("######### Sheet [" + sheet.getName() + "] begin ########");
		
		int rows = sheet.getRows();
		
		//System.out.println("####################### row :::::: " + rows);
		
		Cell ckeys[] = sheet.getRow(0);
		
		Hashtable hk = new Hashtable();
		
		String wt_home = WTProperties.getServerProperties().getProperty("wt.home");
        
		int createCount =1;
		int errCount =1;
		Vector<WTPart> workingVec = new Vector<WTPart>();
		for(int i = 1; i < rows; i++) {
			
			Cell columns[] = sheet.getRow(i);
			Hashtable data = getData(columns, ckeys);
			Enumeration e = data.keys();
			while(e.hasMoreElements()){
				String key = (String)e.nextElement();
				//System.out.println(key + " = " + data.get(key));
			}
		
			String pNumber = (String)data.get("모부품코드");
			if(pNumber == null || pNumber.trim().length() == 0){
				
				data.put("msg", Message.get("모부품코드 의 값이 없습니다."));
				data.put("rslt", false);
				vec.add(data);
				
				continue;
			}
			String cNumber = (String)data.get("자부품코드");
			if(cNumber == null || cNumber.trim().length() == 0){
				
				//System.out.println(i+"child err ====");
				
				data.put("msg", Message.get("자부품코드 의 값이 없습니다."));
				data.put("rslt", false);
				vec.add(data);
				continue;
			}
			
			
			WTPart part = getPart(pNumber);
			
			WTPart childPart = getPart(cNumber);;
			boolean isContinue = false;
			
			if(part == null){
				
				data.put("msg",(String)data.get("모부품코드") + Message.get("의 부품이 존재하지 않습니다."));
				data.put("rslt", false);
				vec.add(data);
				continue;
			}
			String oid = CommonUtil.getOIDString(part);
			data.put("oid", oid);
			if(childPart == null){
				
				data.put("msg",(String)data.get("자부품코드") + Message.get("의 부품이 존재하지 않습니다."));
				data.put("rslt", false);
				vec.add(data);
				continue;
			}
			
			if(part.getLifeCycleState().toString().equals("")){
				
				data.put("msg",(String)data.get("모부품코드") + " " + Message.get("승인된 부품입니다."));
				data.put("rslt", false);
				vec.add(data);
				continue;
			}
				
			WTPartUsageLink alreadyLink = getLink(childPart, part);
			
			String qstr = (String)data.get("수량");
			
			if(qstr.lastIndexOf("-") > 0){
				
				qstr = "-" + qstr.substring(0, qstr.lastIndexOf("-"));
		
			}
			
			if(  alreadyLink != null){
				data.put("msg",(String)data.get("모부품코드") +" :::: "+ (String)data.get("자부품코드") +" ::::: " + Message.get("존재합니다."));
				data.put("rslt", false);
				vec.add(data);
				continue;
			}
			
			part = (WTPart) getWorkingCopy(part);
			
			if(!workingVec.contains(part)){
				workingVec.add(part);
			}
			
			WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(part, (WTPartMaster) childPart.getMaster());
			
            link.setQuantity(Quantity.newQuantity(Double.parseDouble(qstr), childPart.getDefaultUnit()));
			PersistenceServerHelper.manager.insert(link);
            
            /*Location */
			/*
            link = (WTPartUsageLink)PersistenceHelper.manager.refresh(link);
            System.out.println("link==========="+link);
            String location = StringUtil.checkNull((String)data.get("Location")) ;
            Vector locVec = new Vector();
            if(location.length()>0){
            	
            	 StringTokenizer st = new StringTokenizer(location, ",");
                 while (st.hasMoreTokens()) {
                   String loc = st.nextToken();
                   if (loc.length() == 0)
                   {
                     continue;
                   }
                   PartUsesOccurrence po = PartUsesOccurrence.newPartUsesOccurrence(link);
                   po.setName(loc);
                   OccurrenceHelper.service.saveUsesOccurrenceAndData(po, null);
                   
                 }
            }
            
            System.out.println(createCount+" CREATE  ��: " + part.getNumber() + " ��: " + childPart.getNumber());
           */
            data.put("msg", "");
            data.put("rslt", true);
            vec.add(data);
			
            //createCount++;
		}
		
		for(int i = 0 ;  i < workingVec.size() ; i++){
			WTPart workingPart = workingVec.get(i);
			
			if ( WorkInProgressHelper.isCheckedOut(workingPart) ) {
				workingPart = (WTPart) WorkInProgressHelper.service.checkin(workingPart, "Bom Loader에서 변경");
            }
		}
		
		return vec;
	}
	
	private static Hashtable getData(Cell[] columns, Cell[] ckeys) {
        Hashtable data = new Hashtable();
        
        for(int i = 0; i < ckeys.length; i++){
        	data.put(JExcelUtil.getContent(ckeys, i).trim(), JExcelUtil.getContent(columns, i).trim());
        }
        return data;
    }
	
	public static WTPart getPart(String number) {
		try{
			return PartHelper.service.getPart(number);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static WTPartUsageLink getLink(WTPart child, WTPart parent) throws WTException {
        return (WTPartUsageLink) getLinkObject((WTPartMaster) child.getMaster(), parent, WTPartUsageLink.class);
    }

    public static ObjectToObjectLink getLinkObject(WTObject roleA, WTObject roleB, Class linkClz) throws WTException {
        QuerySpec query = new QuerySpec();
        int linkIndex = query.appendClassList(linkClz, true);
        SearchCondition cond1 = new SearchCondition(linkClz, WTAttributeNameIfc.ROLEB_OBJECT_ID, "=", new Long(PersistenceHelper.getObjectIdentifier(roleA).getId()));
        SearchCondition cond2 = new SearchCondition(linkClz, WTAttributeNameIfc.ROLEA_OBJECT_ID, "=", new Long(PersistenceHelper.getObjectIdentifier(roleB).getId()));
        query.appendWhere(cond1, new int[] { linkIndex });
        query.appendAnd();
        query.appendWhere(cond2, new int[] { linkIndex });
        QueryResult result = PersistenceHelper.manager.find(query);
        if (result.size() == 0) return null;
        Object[] obj = (Object[]) result.nextElement();
        return (ObjectToObjectLink) obj[0];
    }
    
	private static Workable getWorkingCopy(Workable _obj) {
		try {
			if (!WorkInProgressHelper.isCheckedOut(_obj)) {

				if (!CheckInOutTaskLogic.isCheckedOut(_obj)) {
					CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_obj, CheckInOutTaskLogic.getCheckoutFolder(), "");
				}

				_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
			} else {
				if (!WorkInProgressHelper.isWorkingCopy(_obj))
					_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
			}
		} catch (WorkInProgressException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
        return _obj;
    }
}
