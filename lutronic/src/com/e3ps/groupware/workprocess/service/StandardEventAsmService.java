package com.e3ps.groupware.workprocess.service;

import com.e3ps.groupware.workprocess.AsmApproval;

import wt.services.StandardManager;



@SuppressWarnings("serial")
public class StandardEventAsmService extends StandardManager implements EventAsmService{
	
	public static StandardEventAsmService newStandardEventAsmService() throws Exception {
		final StandardEventAsmService instance = new StandardEventAsmService();
		instance.initialize();
		return instance;
	}

	@Override
	public void eventListener(Object _obj, String _event) throws Exception {
		
		//System.out.println("_obj = "  + _obj);
		//System.out.println("_event = "  + _event);
		if(_event.equals("STATE_CHANGE") && _obj  instanceof AsmApproval) {	
			///System.out.println("----- StandardEventAsmService ----");
			AsmApproval asm = (AsmApproval)_obj;
			String state = asm.getLifeCycleState().toString();
			
			AsmApprovalHelper.service.setAsmApprovalState(_obj, state);
		}
		
	}
	
	

}
