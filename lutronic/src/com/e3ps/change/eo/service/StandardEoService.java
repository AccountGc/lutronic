package com.e3ps.change.eo.service;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;

import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardEoService extends StandardManager implements EoService {

	public static StandardEoService newStandardEoService() throws WTException {
		StandardEoService instance = new StandardEoService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EoDTO dto) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String eoTypeNumber = "E";
			if(data.getEoType().equals("DEV")) {
				eoTypeNumber = "D";
			}
			String number = eoTypeNumber +DateUtil.getCurrentDateString("ym");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);
			
			number = number + seqNo;
			
	    	EChangeOrder eco = 	EChangeOrder.newEChangeOrder();
	    	
	    	eco.setEoName(data.getEoName());
	    	eco.setEoNumber(number);
	    	eco.setEoType(data.getEoType());
//	    	eco.setModel(model);
	    	eco.setEoCommentA(data.getEoCommentA());
	    	eco.setEoCommentB(data.getEoCommentB());
	    	eco.setEoCommentC(data.getEoCommentC());
	    	eco.setOwner(SessionHelper.manager.getPrincipalReference());
	    	
	    	String location="/Default/설계변경/EO";
	    	String lifecycle="LC_ECO";
	    	
	    	//분류체계,lifecycle 셋팅
	    	CommonHelper.service.setManagedDefaultSetting(eco, location, lifecycle);//eoDefaultSetting(eco, location, lifecycle);
	    	
	    	eco =(EChangeOrder)PersistenceHelper.manager.save(eco);
	    	
	    	//완제품 생성
//	    	createCompleteLink(eco, completeOids);
	    	
	    	//첨부파일
//			CommonContentHelper.service.attach(eco, null, secondarys);
	    	
	    	//활동 생성
//	    	boolean isActivity = ECAHelper.service.createActivity(req, eco);
	    	
	    	//eco 상태 설정
	    	/*
	    	if(isActivity){
	    		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco, State.toState("ACTIVITY"));
	    	}else{
	    		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco, State.toState("APPROVE_REQUEST"));
	    	}
	    	*/
	    	
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}

	}

}
