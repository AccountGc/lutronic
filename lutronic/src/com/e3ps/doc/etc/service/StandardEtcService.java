package com.e3ps.doc.etc.service;

import com.e3ps.doc.etc.dto.EtcDTO;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEtcService extends StandardManager implements EtcService {

	public static StandardEtcService newStandardEtcService() throws WTException {
		StandardEtcService instance = new StandardEtcService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EtcDTO dto) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
