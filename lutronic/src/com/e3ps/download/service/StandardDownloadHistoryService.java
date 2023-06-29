package com.e3ps.download.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;

import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.download.DownloadHistory;

public class StandardDownloadHistoryService extends StandardManager implements DownloadHistoryService {

	public static StandardDownloadHistoryService newStandardDownloadHistoryService() throws Exception {
		final StandardDownloadHistoryService instance = new StandardDownloadHistoryService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public synchronized String createDownloadHistory(Hashtable hash) throws Exception{
		 
		 Transaction trx = new Transaction();
		 
		try{
			trx.start();
			
			DownloadHistory history = null;
			
			String dOid = "";
			String userId = "";
			String describe ="";
			dOid = (String)hash.get("dOid");
			userId = (String)hash.get("userId");
			describe = StringUtil.checkNull((String)hash.get("describe"));
			
			WTUser user = CommonUtil.findUserID(userId);
			if( user == null){
				user = (WTUser)SessionHelper.getPrincipal();
			}
			/*
			QuerySpec qs = new QuerySpec();
			
			int idx = qs.appendClassList(DownloadHistory.class, true);
			
			qs.appendWhere(new SearchCondition(DownloadHistory.class, DownloadHistory.D_OID, "=", dOid), new int[] {idx });
			
			qs.appendAnd();

			qs.appendWhere(new SearchCondition(DownloadHistory.class, "userReference.key.id", "=", CommonUtil.getOIDLongValue(user)), new int[] {idx });
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			
			
			if(qr.hasMoreElements()){
				Object o[] = (Object[])qr.nextElement();
				 history = (DownloadHistory)o[0];
				 history.setDCount(history.getDCount()+1);
			}else{
				history = DownloadHistory.newDownloadHistory();
				history.setDCount(1);
			}
			*/
			history = DownloadHistory.newDownloadHistory();
			history.setDCount(1);
			
			
			history.setDOid(dOid);
			history.setUser(user);
			history.setDescribe(describe);
			
			PersistenceHelper.manager.save(history);
			
			
			trx.commit();
            trx = null;
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
           if(trx!=null){
                trx.rollback();
           	}
		}
		return "";
	}
	
	@Override
	public synchronized void createBatchDownloadHistory(List<BatchDownData> targetlist) throws Exception{
		
		String userId= SessionHelper.getPrincipal().getName();
		List<String> doubleCheckList = new ArrayList<String>();
		for(BatchDownData data: targetlist){
			if(data.getAppData() == null ||  !data.isResult()){
				continue;
			}
			
			//중복 제거
			if(doubleCheckList.contains(data.getOid())){
				continue;
			}else{
				doubleCheckList.add(data.getOid());
			}
			
			
			Hashtable hash = new Hashtable();
			String oid =data.getOid();
			String describe = data.getDescribe();
			hash.put("dOid", oid);
			hash.put("userId", userId);
			hash.put("describe", describe);
			
			
			createDownloadHistory(hash);
		}
		
	}
	
}
