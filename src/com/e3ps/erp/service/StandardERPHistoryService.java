package com.e3ps.erp.service;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.e3ps.erp.BOMERP;
import com.e3ps.erp.ECOERP;
import com.e3ps.erp.PARTERP;
import com.sap.conn.jco.JCoTable;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.services.StandardManager;

@SuppressWarnings("serial")
public class StandardERPHistoryService extends StandardManager implements ERPHistoryService{
	
	public static StandardERPHistoryService newStandardERPHistoryService() throws Exception {
		final StandardERPHistoryService instance = new StandardERPHistoryService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void createHistoryPart(JCoTable codes,JCoTable returnCodes) throws Exception {
		
		codes.firstRow();
		returnCodes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow(),returnCodes.nextRow()  ){
		
			String ZIFNO = (String)codes.getValue("ZIFNO");
			String MATNR = (String)codes.getValue("MATNR");
			String MAKTX = (String)codes.getValue("MAKTX");
			String MEINS = (String)codes.getValue("MEINS");
			String MATKL = (String)codes.getValue("MATKL");
			BigDecimal tempNTGEW = (BigDecimal)codes.getValue("NTGEW");
			String NTGEW = String.valueOf(tempNTGEW.intValue()) ;
			String GEWEI = (String)codes.getValue("GEWEI");
			String WRKST = (String)codes.getValue("WRKST");
			String ZSPEC = (String)codes.getValue("ZSPEC");
			String ZFINSH = (String)codes.getValue("ZFINSH");
			String ZMODEL = (String)codes.getValue("ZMODEL");
			String ZPRODM = (String)codes.getValue("ZPRODM");
			String ZDEPT = (String)codes.getValue("ZDEPT");
			String ZMAT1 = (String)codes.getValue("ZMAT1");
			String ZMAT2 = (String)codes.getValue("ZMAT2");
			String ZMAT3 = (String)codes.getValue("ZMAT3");
			String AENNR = (String)codes.getValue("AENNR");
			String ZEIVR = (String)codes.getValue("ZEIVR");
			String ZIFSTA = "";
			String ZIFMSG = "";
			if(returnCodes.getNumRows() != 0){
				ZIFSTA = (String)returnCodes.getValue("ZIFSTA");
				ZIFMSG = (String)returnCodes.getValue("ZIFMSG");
			}
			
			//System.out.println("createHistoryPart ZIFNO = " + ZIFNO);
			
			PARTERP part = new PARTERP();
			part.setZifno(ZIFNO);
			part.setMatnr(MATNR);
			part.setMaktx(MAKTX);
			part.setMeins(MEINS);
			part.setMatkl(MATKL);
			part.setNtgew(NTGEW);
			part.setGewei(GEWEI);
			part.setWrkst(WRKST);
			part.setZspec(ZSPEC);
			part.setZfinsh(ZFINSH);
			part.setZmodel(ZMODEL);
			part.setZprodm(ZPRODM);
			part.setZdept(ZDEPT);
			part.setZmat1(ZMAT1);
			part.setZmat2(ZMAT2);
			part.setZmat3(ZMAT3);
			part.setAennr(AENNR);
			part.setZeivr(ZEIVR);
			part.setZifsta(ZIFSTA);
			part.setZifmsg(ZIFMSG);
			
			part = (PARTERP)PersistenceHelper.manager.save(part);
			//System.out.println("createHistoryPart Check PARTERP = " + (null!=part));
			
		}
	}
	
	@Override
	public void createHistoryECO(JCoTable codes,JCoTable returnCodes) throws Exception {
		
		codes.firstRow();
		returnCodes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow(),returnCodes.nextRow()  ){
			String ZIFNO = (String)codes.getValue("ZIFNO");
			String AENNR = (String)codes.getValue("AENNR");
			String AETXT = (String)codes.getValue("AETXT");
			String DATUV = (String)codes.getValue("DATUV");
			String AEGRU = (String)codes.getValue("AEGRU");
			String ZECMID = (String)codes.getValue("ZECMID");
			String ZIFSTA = "";
			String ZIFMSG = "";
			if(returnCodes.getNumRows() != 0){
				ZIFSTA = (String)returnCodes.getValue("ZIFSTA");
				ZIFMSG = (String)returnCodes.getValue("ZIFMSG");
			}
			
			ECOERP eco = ECOERP.newECOERP();
			
			eco.setZifno(ZIFNO);
			eco.setAennr(AENNR);
			eco.setAetxt(AETXT);
			eco.setDatuv(DATUV);
			eco.setAegru(AEGRU);
			eco.setZecmid(ZECMID);
			eco.setZifsta(ZIFSTA);
			eco.setZifmsg(ZIFMSG);
			
			eco = (ECOERP)PersistenceHelper.manager.save(eco);
			//System.out.println("Check ECOERP = "+(null!=eco));
		}
	}
	
	@Override
	public void updateHistory(Class cls,String startZifno,String endZifno,JCoTable returnCodes) throws Exception {
		
		
		HashMap<String, Object> map = ERPSearchHelper.service.getZINFOData(cls, startZifno, endZifno);
		//System.out.println("updateHistory ="+ returnCodes.getNumRows() +":" +map.size());
		for(int j = 1 ; j<=returnCodes.getNumRows() ; j++,returnCodes.nextRow()  ){
			if(returnCodes.getNumRows() != 0){
				String ZIFNO = (String)returnCodes.getValue("ZIFNO");
			
				String ZIFSTA = (String)returnCodes.getValue("ZIFSTA");
				String ZIFMSG = (String)returnCodes.getValue("ZIFMSG");
				//System.out.println(ZIFNO +","+","+ZIFSTA+","+ZIFMSG);
				Object ob = map.get(ZIFNO);
				if(ob != null){
					
					if(ob instanceof PARTERP){
						
						PARTERP part = (PARTERP)ob;
						part.setReturnZifsta(ZIFSTA);
						part.setReturnZifmsg(ZIFMSG);
						PersistenceHelper.manager.modify(part);
					}else if(ob instanceof ECOERP){
						ECOERP eco = (ECOERP)ob;
						eco.setReturnZifsta(ZIFSTA);
						eco.setReturnZifmsg(ZIFMSG);
						PersistenceHelper.manager.modify(eco);
					}else{
						BOMERP bom = (BOMERP)ob;
						bom.setReturnZifsta(ZIFSTA);
						bom.setReturnZifmsg(ZIFMSG);
						PersistenceHelper.manager.modify(bom);
						
					}
					
				}
				
				
			}
		}
	}
	
	@Override
	public void createHistoryBOM(JCoTable codes,JCoTable returnCodes,HashMap<String, Object> map) throws Exception {
		codes.firstRow();
		returnCodes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow(),returnCodes.nextRow()  ){
			String ZIFNO = (String)codes.getValue("ZIFNO");
			String ZITMSTA = (String)codes.getValue("ZITMSTA");
			String MATNR = (String)codes.getValue("MATNR");
			//BigDecimal tempPOSNR = (BigDecimal)codes.getValue("POSNR");
			//String POSNR= String.valueOf(tempPOSNR.intValue());
			String POSNR = (String)codes.getValue("POSNR");
			String IDNRK = (String)codes.getValue("IDNRK");
			BigDecimal temMENGE = (BigDecimal)codes.getValue("MENGE");
			String MENGE = String.valueOf(temMENGE.intValue());
			String MEINS = (String)codes.getValue("MEINS");
			String AENNR = (String)codes.getValue("AENNR");
			String ZIFSTA = "";
			String ZIFMSG = "";
			if(returnCodes.getNumRows() != 0){
				ZIFSTA = (String)returnCodes.getValue("ZIFSTA");
				ZIFMSG = (String)returnCodes.getValue("ZIFMSG");
			}
			HashMap<String, String> mapVer= (HashMap)map.get(ZIFNO);
			
			String PVER = mapVer.get("PVER");
			String CVER = mapVer.get("CVER");
			BOMERP bom = BOMERP.newBOMERP();
			bom.setZifno(ZIFNO);
			bom.setZitmsta(ZITMSTA);
			bom.setMatnr(MATNR);
			bom.setPosnr(Integer.valueOf(POSNR));
			bom.setIdnrk(IDNRK);
			bom.setMenge(MENGE);
			bom.setMeins(MEINS);
			bom.setAennr(AENNR);
			bom.setZifsta(ZIFSTA);
			bom.setZifmsg(ZIFMSG);
			bom.setPver(PVER);
			bom.setCver(CVER);
			
			bom = (BOMERP)PersistenceHelper.manager.save(bom);
			//System.out.println("isExcute :"+(null!=bom) +"==============");
			
		}
	}
	
	
}