package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.clients.folder.FolderTaskLogic;
import wt.fc.PersistenceHelper;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.beans.ECNData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.WCUtil;

@SuppressWarnings("serial")
public class StandardECNService extends StandardManager implements ECNService {

	public static StandardECNService newStandardECNService() throws Exception {
		final StandardECNService instance = new StandardECNService();
		instance.initialize();
		return instance;
	}
	
	
	
	@Override
	public void createECN(EChangeOrder _eco)throws Exception{
		
		//if(false) throw new Exception("ECO 완료 처리 오류 발생");
		/*
		if(!_eco.getEcoType().equals("CHANGE")) {
			return;
		}
		*/	
		/*ECN Create START*/
		
		String ecnType= ChangeKey.ecnType;
		if(_eco.getEoType().equals("MCO")){
			ecnType= ChangeKey.mcnType;
		}
		
		SessionHelper.manager.setPrincipal(_eco.getWorker().getName());
		EChangeNotice ecn = EChangeNotice.newEChangeNotice();
		ecn.setEoNumber(_eco.getEoNumber());
		ecn.setEoName(_eco.getEoName());
		ecn.setEco(_eco);
		ecn.setEoType(ecnType);
		FolderHelper.assignLocation(ecn,
				FolderTaskLogic.getFolder((String) "/Default/EO/ECN", WCUtil.getWTContainerRef()));
		
		LifeCycleHelper.setLifeCycle(
				ecn,
				LifeCycleHelper.service.getLifeCycleTemplate((String) "LC_ECN",
						WCUtil.getWTContainerRef()));
		ecn.setContainer(WCUtil.getPDMLinkProduct());
		
		ecn=(EChangeNotice)PersistenceHelper.manager.save(ecn);
		
		
		/*ECN Create END*/
		
		/*ECN 활동 Start*/
		
		Vector<EChangeActivityDefinition> ecaVec=ChangeHelper.service.getEOActivityDefinition(ecnType,"STEP1");
		String stae ="FOLLOWING_ACTIVITY"; //후속조치
		for(int i = 0 ; i < ecaVec.size() ; i++){
			
			EChangeActivityDefinition ead = ecaVec.get(i);
			
			EChangeActivity changeActivity = null;//ECAHelper.service.createECA(ecn, ead, _eco.getWorker(), "/Default/EO/ECN/ECA",DateUtil.getToDay());
			if(changeActivity == null) throw new Exception(_eco.getEoNumber() + " = ECN CREATE : " + Message.get("ECA 생성시 오류가 발생 했습니다."));
			LifeCycleHelper.service.setLifeCycleState(changeActivity, State.toState("INWORK"), false);
		}
		
		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)ecn, State.toState(stae));
		
		/*ECN 활동 END*/
		SessionHelper.manager.setAdministrator();
	}
	
	/**	관련 ECR
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ECNData> include_ecnList(String oid) throws Exception {
		List<ECNData> list = new ArrayList<ECNData>();
		if (oid.length() > 0) {
			EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
			/*
			QuerySpec qs = new QuerySpec(EChangeNotice.class);
			
			qs.appendWhere(new SearchCondition
					(EChangeNotice.class,"ecoReference.key.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(eco)), new int[] {0});
			
			QueryResult result = PersistenceHelper.manager.find(qs);
			
            while(result.hasMoreElements()){
            	
            	Object[] resultObj = (Object[])result.nextElement();
            	EChangeNotice nextECR = (EChangeNotice)resultObj[0];
            	ECNData data = new ECNData(nextECR);
            	
            	list.add(data);
            }
            */
			ECOData eoData = new ECOData(eco);
			EChangeNotice ecn = null;
			if(ecn != null) {
				ECNData data = new ECNData(ecn);
				list.add(data);
			}
		}
		return list;
		
	}



	@Override
	public void create(ECNData data) throws Exception {
		Transaction trs = new Transaction();
	    try{
	    	trs.start();
	    	
	    	EChangeNotice ecn = EChangeNotice.newEChangeNotice();
			ecn.setEoNumber(data.getNumber());
			ecn.setEoName(data.getName());
			ecn.setEoCommentA(data.getEoCommentA());
			ecn.setEoCommentB(data.getEoCommentB());
			
			FolderHelper.assignLocation(ecn, FolderTaskLogic.getFolder((String) "/Default/설계변경/ECN", WCUtil.getWTContainerRef()));
			LifeCycleHelper.setLifeCycle(ecn,LifeCycleHelper.service.getLifeCycleTemplate((String) "LC_ECN",WCUtil.getWTContainerRef()));
			ecn.setContainer(WCUtil.getPDMLinkProduct());
			PersistenceHelper.manager.save(ecn);
			
			/*ECN 활동 Start*/
			
//			Vector<EChangeActivityDefinition> ecaVec=ChangeHelper.service.getEOActivityDefinition(ecnType,"STEP1");
//			String stae ="FOLLOWING_ACTIVITY"; //후속조치
//			for(int i = 0 ; i < ecaVec.size() ; i++){
//				
//				EChangeActivityDefinition ead = ecaVec.get(i);
//				
//				EChangeActivity changeActivity = null;//ECAHelper.service.createECA(ecn, ead, _eco.getWorker(), "/Default/EO/ECN/ECA",DateUtil.getToDay());
//				if(changeActivity == null) throw new Exception(_eco.getEoNumber() + " = ECN CREATE : " + Message.get("ECA 생성시 오류가 발생 했습니다."));
//				LifeCycleHelper.service.setLifeCycleState(changeActivity, State.toState("INWORK"), false);
//			}
//			
//			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)ecn, State.toState(stae));
			
			/*ECN 활동 END*/
//			SessionHelper.manager.setAdministrator();
			
	    	trs.commit();
	    	trs = null;
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
			trs.rollback();
			throw e;
        } finally {
        	if (trs != null) {
				trs.rollback();
			}
        }
	}
}
