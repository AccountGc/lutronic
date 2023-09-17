package com.e3ps.rohs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewException;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;

public class RohsUtil {
	
	public static final int STATE_NOT_ROHS = 0;			//물질이 없으면  0 - 검은색 	task_ready.gif
	public static final int STATE_NONE_ROHS = 1;		//NON 1 - 주황색 		task_orange.gif
	public static final int STATE_ALL_APPROVED = 2;		//전체 승인됨    2 - 녹색 	task_complete.gif
	public static final int STATE_NOT_APPROVED = 3;		//승인됨이 아닌상태 3 --빨강색  task_red.gif
	
	public static HashMap<String, String> getRohsDocType(){
		
		HashMap<String, String> map = new HashMap<>();
		
		String[] rohsCode = AttributeKey.RohsKey.ROHS_CODE;
		String[] rohsName = AttributeKey.RohsKey.ROHS_NAME;
		
		for(int i=0; i<rohsCode.length; i++){
			map.put(rohsCode[i], rohsName[i]);
		}
		return map;
	}
	
	public static String getRohsDocTypeName(String code){
		String name = "";
		
		String[] rohsCode = AttributeKey.RohsKey.ROHS_CODE;
		String[] rohsName = AttributeKey.RohsKey.ROHS_NAME;
		
		for(int i=0; i<rohsCode.length; i++){
			if(rohsCode[i].equals(code)){
				name = rohsName[i];
			}
		}
		return name;
	}
	
	
	public static List<ContentRoleType> getROHSContentRoleType(){
		
		List<ContentRoleType> list= new ArrayList<ContentRoleType>();
		ContentRoleType[] roleType = ContentRoleType.getContentRoleTypeSet();
		
		for(ContentRoleType type : roleType){
			
			String typeKey =type.toString();
			if(typeKey.startsWith("ROHS")){
			
				list.add(type);
			}
		}
		
		return list;
		
	}
	
	public static List<String> getROHSContentRoleKey(){
		
		List<String> list= new ArrayList<String>();
		List<ContentRoleType> typelist = getROHSContentRoleType();;
		
		for(ContentRoleType type : typelist){
			
			String typeKey =type.toString();
			if(typeKey.startsWith("ROHS")){
				
				list.add(typeKey);
			}
		}
		
		return list;
		
	}
	
	/**	부품 현황시의 RoHS 상태 설정
	 * @param oid
	 * @return
	 * @throws Exception 
	 */
	public static HashMap<String, Integer> getRohsState(HashMap<String, Integer> stateMap,String oid) throws Exception {
		
		
		if(stateMap.containsKey(oid)){
			
			return stateMap;
		}
		int state  = getPartROHSState(oid);
		
		stateMap.put(oid, state);
		
		return stateMap;
	}
	
	/**	부품 현황시의 RoHS 상태 설정
	 * @param oid
	 * @return
	 * @throws Exception 
	 */
	public static HashMap<String, Integer> getRohsState(HashMap<String, Integer> stateMap,WTPart part) throws Exception {
		
		String oid = CommonUtil.getOIDString(part);
		
		return getRohsState(stateMap, oid);
	}
	
	/**
	 * 부품 별 ROHS 상태 
	 * pass : 부품과 연결된 물질의 상태-승인됨 (N개 - N개 모두),
	 * fail : 부품과 연결된 물질의 상태가 승인됨이 안된 상태 (N개 - N개 증 1개)
	 * 대표물질의 하위 구성은 체크 하지 않는다.
	 * 물
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public static int getPartROHSState(String oid ) throws Exception{
		
		int rohsState = STATE_NOT_ROHS ;
		
		List<RohsData> list = getPartROHSList(oid);
		
		if(list.size() > 0){
			
			for(RohsData data : list){
				if(data.number.equals("NONE")){
					rohsState = STATE_NONE_ROHS;
					//System.out.println("PartNumber ::: " + ((WTPart)CommonUtil.getObject(oid)).getNumber()+"\tSTATE_NONE_ROHS");
					break;
				}else{
					if(!data.isState("APPROVED")){
						rohsState = STATE_NOT_APPROVED;
						//System.out.println("PartNumber ::: " + ((WTPart)CommonUtil.getObject(oid)).getNumber()+"\tSTATE_NOT_APPROVED");
						break;
					}
					//System.out.println("PartNumber ::: " + ((WTPart)CommonUtil.getObject(oid)).getNumber()+"\tSTATE_ALL_APPROVED");
					rohsState = STATE_ALL_APPROVED;
				}
				
			}
		}
		//System.out.println("PartNumber ::: " + ((WTPart)CommonUtil.getObject(oid)).getNumber()+"\tSTATE_NOT_ROHS");
		return rohsState;
	}
	
	/**
	 * 부품의 관련 ROHS List
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public static List<RohsData>  getPartROHSList(String oid) throws Exception {
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		return RohsQueryHelper.service.getPartROHSList(part, true);
		
		
	}
	
	/** 제품 현황시의 RoHS 상태 설정
	 * 제품에서  계산  = 파랑/(빨강+녹색+검은색) 2/(0+2+3)
	 * @param oid
	 * @return
	 * @throws Exception 
	 * @throws WTException 
	 * @throws ViewException 
	 */
	public static Map<String, Object> getProductRoHsState(String oid) throws ViewException, WTException, Exception{
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		double totalCount = 0;
		double greenCount = 0;
		double orangeCount = 0;
		double redCount = 0;
		double blackCount = 0;
		double passCount = 0;
		int failCount = 0;
		String view = "Design";
		List<WTPart> BomList = new ArrayList<WTPart>();
		
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		BomList.add(part);
		//BomList = getBom(part, null, ViewHelper.service.getView(view), BomList);
		BomList = getBom_NotDumy(part, null, ViewHelper.service.getView(view), BomList);
		HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
		HashMap<String,String> doubleCheckMap = new HashMap<String,String>();
		int dumyCount=0;
		int listCount=0;
		int continueCount=0;
		int isDumy_SonPartsCount = 0;
		ArrayList<String> partNumberList = new ArrayList<String>();
		for(WTPart supart : BomList){
			String subOid = CommonUtil.getOIDString(supart);
			listCount++;
			String subPartNumber = supart.getNumber();
			/*boolean isDumy_SonParts =  getAUIPartTreeAction(supart, false);
			if(isDumy_SonParts){
				isDumy_SonPartsCount++;
				System.out.println(";subPartNumber="+subPartNumber+"\t;isDumy_SonPartsCount="+isDumy_SonPartsCount);
				continue;
			}*/
			
			//더미 제외
			/*if(PartUtil.isChange(subPartNumber)){
				dumyCount++;
				continue;
			}*/
			
			//중복 체크 제외
			if(doubleCheckMap.containsKey(subPartNumber)){
				continueCount++;
				continue;
			}else{
				doubleCheckMap.put(subPartNumber, subPartNumber);
			}
			
			//System.out.println("getProductRoHsState BomList \tsubPartNumber = "+subPartNumber);
			stateMap = getRohsState(stateMap, supart);
			int state =stateMap.get(subOid);
			/*String state ="검은색";
			if(rohsState == RohsUtil.STATE_NOT_APPROVED){
				state ="빨강색";
			}else if(rohsState ==RohsUtil.STATE_NONE_ROHS){
				state ="주황색";
			}else if(rohsState ==RohsUtil.STATE_ALL_APPROVED){
				state ="녹색";
			}*/
			if(state == RohsUtil.STATE_NONE_ROHS ){
				orangeCount++;
				//System.out.println(";subPartNumber="+subPartNumber+"\t;orangeCount="+orangeCount);
			}else if(state == RohsUtil.STATE_ALL_APPROVED){
				greenCount++;
				//System.out.println(";subPartNumber="+subPartNumber+"\t;greenCount="+greenCount);
			}else if(state == RohsUtil.STATE_NOT_APPROVED){
				redCount++;
				//System.out.println(";subPartNumber="+subPartNumber+"\t;redCount="+redCount);
			}else if(state ==RohsUtil.STATE_NOT_ROHS){
				blackCount++;
			}
			
			if( state == RohsUtil.STATE_ALL_APPROVED ||  state == RohsUtil.STATE_NOT_APPROVED || state ==RohsUtil.STATE_NOT_ROHS){
				totalCount++;
			}
			
			if(state == RohsUtil.STATE_ALL_APPROVED){
				passCount++;
			}
		}
		//System.out.println("dumyCount="+dumyCount);
		//System.out.println("greenCount+redCount+blackCount="+(greenCount+redCount+blackCount)+"\ttotalCount="+totalCount);
		double avage = 0;
		totalCount = (greenCount+redCount+blackCount);
		if(totalCount ==0){
			 avage = 0;
		}else{
			 avage = (passCount/(greenCount+redCount+blackCount))*100;
		}
		
		double totalState = (double)avage;
		totalState = Math.round(totalState*100.0)/100.0;
		returnMap.put("totalState", totalState);
		returnMap.put("passCount", passCount);
		returnMap.put("orangeCount", orangeCount);
		returnMap.put("greenCount", greenCount);
		returnMap.put("redCount", redCount);
		returnMap.put("blackCount", blackCount);
		returnMap.put("dumyCount", dumyCount);
		returnMap.put("continueCount", continueCount);
		returnMap.put("totalCount", totalCount);
		returnMap.put("listCount", listCount);
		returnMap.put("isDumy_SonPartsCount", isDumy_SonPartsCount);
		
		
		return returnMap;
	}
	
	private static boolean getAUIPartTreeAction(
			WTPart supart, boolean b) throws Exception{
		WTPart part = (WTPart)supart;
		
		Baseline bsobj = null;
		List<PartTreeData> partTrees = new ArrayList<PartTreeData>();
		
		partTrees = BomSearchHelper.service.getBOM(part, b, bsobj, false, "");//(part, boolDesc, bsobj,parentID);
		
		for(PartTreeData data : partTrees) {
			WTPart p = data.part;
			if(PartUtil.isChange(p.getNumber())){
				return true;
			}
		}
		return false;
	}

	/**
	 * BOM 리스트
	 * @param part
	 * @param link
	 * @param view
	 * @param BomList
	 * @return
	 * @throws Exception
	 */
	public  static List<WTPart>  getBom(WTPart part,WTPartUsageLink link,View view,List<WTPart> BomList) throws Exception{
		ArrayList list = descentLastPart(part, view,null);
	
		for (int i = 0; i < list.size(); i++) {
			
			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko =(WTPartUsageLink) o[0];
			WTPart cPart = (WTPart)o[1];
			BomList.add(cPart);
			getBom((WTPart) o[1], (WTPartUsageLink) o[0],view,BomList);
		}
		
		
		return BomList;
	}
	
	public  static List<WTPart>  getBom_NotDumy(WTPart part,WTPartUsageLink link,View view,List<WTPart> BomList) throws Exception{
		ArrayList list = descentLastPart(part, view,null);
	
		for (int i = 0; i < list.size(); i++) {
			
			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko =(WTPartUsageLink) o[0];
			WTPart cPart = (WTPart)o[1];
			if(!PartUtil.isChange(cPart.getNumber())){
				BomList.add(cPart);
				getBom_NotDumy((WTPart) o[1], (WTPartUsageLink) o[0],view,BomList);
			}
		}
		
		
		return BomList;
	}
	/**
	 * BOM 리스트
	 * @param part
	 * @param link
	 * @param view
	 * @param BomList
	 * @return
	 * @throws Exception
	 */
	public  static List<WTPart>  getAsecBom(WTPart part,WTPartUsageLink link,View view,List<WTPart> BomList) throws Exception{
		ArrayList list = descentLastPart(part, view,null);
	
		for (int i = 0; i < list.size(); i++) {
			
			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko =(WTPartUsageLink) o[0];
			WTPart cPart = (WTPart)o[1];
			
			
			BomList.add(cPart);
			getBom((WTPart) o[1], (WTPartUsageLink) o[0],view,BomList);
		}
		
		
		return BomList;
	}
	/**
	 * BOM
	 * @param part
	 * @param view
	 * @param state
	 * @return
	 * @throws WTException
	 */
	public static ArrayList descentLastPart(WTPart part, wt.vc.views.View view, State state)
			throws WTException {
		ArrayList v = new ArrayList();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec
							.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part,
					configSpec);
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();

				if (!(oo[1] instanceof WTPart)) {
					continue;
				}
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}
	
	/** Working Copy
	 * @param _doc
	 * @return
	 */
	public static WTDocument getWorkingCopy(WTDocument _doc) throws Exception 
	{
       try
        {
            if( !WorkInProgressHelper.isCheckedOut(_doc))
               {
                    if (!CheckInOutTaskLogic.isCheckedOut(_doc))
                    {
                        CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_doc, CheckInOutTaskLogic.getCheckoutFolder(), "");
                    }
                    _doc = (WTDocument)WorkInProgressHelper.service.workingCopyOf(_doc);
               }
               else
               {
                   if(!WorkInProgressHelper.isWorkingCopy(_doc))
                       _doc = (WTDocument)WorkInProgressHelper.service.workingCopyOf(_doc);
               }
        }catch (WorkInProgressException e){
            e.printStackTrace();
            throw new Exception(e.getLocalizedMessage());
        }catch (WTException e){
            e.printStackTrace();
            throw new Exception(e.getLocalizedMessage());
        }catch (WTPropertyVetoException e){
            e.printStackTrace();
            throw new Exception(e.getLocalizedMessage());
        }
           return _doc;
    }
}
