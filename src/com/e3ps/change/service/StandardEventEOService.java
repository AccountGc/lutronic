package com.e3ps.change.service;

import java.util.List;
import java.util.Vector;

import wt.lifecycle.LifeCycleManaged;
import wt.services.StandardManager;
import wt.session.SessionHelper;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.workflow.E3PSWorkflowHelper;

public class StandardEventEOService extends StandardManager implements EventEOService {

	public static StandardEventEOService newStandardEventEOService() throws Exception {
		final StandardEventEOService instance = new StandardEventEOService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void eventListener(Object _obj, String _event) throws Exception{ 
		
		if(_event.equals("STATE_CHANGE")) {	
			
			if (_obj instanceof EChangeActivity) {
				EChangeActivity eca = (EChangeActivity)_obj;
				String state = eca.getLifeCycleState().toString();
				//System.out.println("[EventEOManager] "+eca.getName() +" : "+ eca.getLifeCycleState().toString() +" : "+ eca.getStep()+":"+eca.getName());
				if( state.equals("COMPLETED")){ //ECA 완료
					
					//Next ECA start,모든 ECA 완료시 ECO 결재 진행
					completeECA(eca);
				}
				
			}else if(_obj instanceof EChangeOrder){
				EChangeOrder eco = (EChangeOrder)_obj;
				//System.out.println("[EventEOManager] "+eco.getEoNumber() +" : "+ eco.getLifeCycleState().toString());
			
				
				if(eco.getLifeCycleState().toString().equals("APPROVED")){ //ECO 승인완료
					
					ECOHelper.service.completeECO(eco);
				}
					
			}
			
		}
	}
	
	private void completeECA(EChangeActivity eca) throws Exception{
			
			
			boolean isStepComplete = ECAHelper.service.isStepComplete(eca); // 해당 step의 진행중인  eca 존재시 return;
			
			if(!isStepComplete) return;
				
			List<EChangeActivity> list = ECAHelper.service.getNextActivity(eca);
			if(list.size()==0){
				
				//EO,ECO 결재 진행
				ECOChange eo = eca.getEo();
				String ecaName = eca.getName();
				
				//EO 완료후 자동으로 생성 되는 ECA는 제외 하기 위해 추가
				if(!ecaName.startsWith(eo.getEoNumber())){
					//System.out.println("ECO  상태값 변경 2 completeECA : " + eo.getEoNumber() +":" + eo.getLifeCycleState().toString() );
					//System.out.println("ECO  상태값 변경 2 completeECA : " + eo.getEoNumber() +":" + SessionHelper.getPrincipal().getName() );		
					SessionHelper.manager.setPrincipal(eo.getCreatorName());
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) eo, "APPROVE_REQUEST");
					SessionHelper.manager.setAdministrator();
				}
				
				
			}else{
				
				//Next eca 진행
				for(EChangeActivity nexEca : list){
					//System.out.println("Next eca 진행 : " + nexEca.getEo().getEoNumber() +":" + SessionHelper.getPrincipal().getName() );	
					SessionHelper.manager.setPrincipal(nexEca.getEo().getCreatorName());
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) nexEca, "INWORK");
					SessionHelper.manager.setAdministrator();
				}
				
			}
		}
}
