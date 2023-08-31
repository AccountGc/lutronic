package com.e3ps.groupware.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;

@SuppressWarnings("serial")
public class StandardNoticeService extends StandardManager implements NoticeService {

	public static StandardNoticeService newStandardNoticeService() throws Exception {
		final StandardNoticeService instance = new StandardNoticeService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void createNotice(NoticeData data) throws Exception {
		Transaction trs = new Transaction();
		try{
			trs.start();
			Notice b = Notice.newNotice();
			b.setTitle(data.getTitle());
			b.setContents(data.getContents());
			b.setIsPopup(data.isPopup());
			b.setOwner(SessionHelper.manager.getPrincipalReference());
			PersistenceHelper.manager.save(b);
			
//			if(loc != null){
//				for(int i=0; i< loc.length; i++){
//					String cacheId = loc[i].split("/")[0];
//			        String fileName = loc[i].split("/")[1];
//
//			        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);
//
//			        CommonContentHelper.service.attach(b, cacheDs, fileName, null, ContentRoleType.SECONDARY);
//				}
//			}
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
	
	@Override
	public String create(Hashtable hash,String[] loc)throws Exception{
	    Transaction trx = new Transaction();
		try{
			trx.start();
			Notice b = Notice.newNotice();
			boolean isPopup = ((String)hash.get("isPopup")).equals("true");
			//if(isPopup){
			//	updateIsPopupFalse();
			//}
			b.setTitle((String)hash.get("title"));
			b.setContents((String)hash.get("contents"));
			b.setIsPopup(isPopup);
			b.setOwner(SessionHelper.manager.getPrincipalReference());
			b = (Notice)PersistenceHelper.manager.save(b);
			
			if(loc != null){
				for(int i=0; i< loc.length; i++){
					String cacheId = loc[i].split("/")[0];
			        String fileName = loc[i].split("/")[1];

			        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);

			        b = (Notice) CommonContentHelper.service.attach(b, cacheDs, fileName, null, ContentRoleType.SECONDARY);
					//CommonContentHelper.service.attach(b, loc[i]);
				}
			}
			
			trx.commit();
			trx = null;
    } catch(Exception e) {
        throw e;
    } finally {
        if(trx!=null){
				trx.rollback();
		   }
    }
		return Message.get("등록 되었습니다.");
	}
	
	@Override
	public String delete(String oid){
		try{
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				Notice b = (Notice) f.getReference(oid).getObject();
				PersistenceHelper.manager.delete(b);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return Message.get("삭제 되었습니다.");
	}
	
	@Override
	public Map<String, Object> modify(Map<String, Object> params)throws Exception{
		Map<String, Object> result = new HashMap<>();
       Transaction trx = new Transaction();
       String oid = StringUtil.checkNull((String) params.get("oid"));
       boolean isPopup = StringUtil.checkNull((String) params.get("isPopup")).equals("true");
       String title = StringUtil.checkNull((String) params.get("title"));
       String contents = StringUtil.checkNull((String) params.get("contents"));
       
       String[] loc = (String[]) params.get("secondary");;
       String[] deloc = (String[]) params.get("delocIds");
       
       String reOid = "";
       try {
    	    trx.start();
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				System.out.println("=========================>" + oid);
				Notice b = (Notice) CommonUtil.getObject(oid);
				//if(isPopup){
				//	updateIsPopupFalse();
				//}
				b.setTitle(title);
				b.setContents(contents);
				b.setIsPopup(isPopup);
				b = (Notice) PersistenceHelper.manager.modify(b);
				// 기존 첨부 파일이 삭제 된 여부를 판단 하여 삭제 한다.
				// null 인 경우는 전부 삭제 된 경우다..
				if(deloc != null){
					ContentHolder holder = ContentHelper.service.getContents((ContentHolder)f.getReference(oid).getObject());
				    Vector files = ContentHelper.getContentList(holder);
		      		if ( files != null ) {
		      		    for ( int i = 0 ; i < files.size() ; i++ ) {
		      		    	// 이미 들어 있던 파일을 검색
		      		    	ApplicationData oldFile = (ApplicationData)files.get(i);
		      		    	boolean flag = true;
		      		    	//삭제 된 파일을 찾아 삭제하여 준다.
		      		    	for(int j=0; j< deloc.length; j++){
		      		    		String oidNo = deloc[j];
								if(oidNo.equals(oldFile.getPersistInfo().getObjectIdentifier().toString())){
									flag = false;
								}
							}
		      		    	if(flag){
		      		    		CommonContentHelper.service.delete(b, oldFile);
		      		    	}
		      		    }
		      		}
				}else{
					CommonContentHelper.service.delete(b);
				}
				
				if(loc != null){
					for(int i=0; i< loc.length; i++){
						String cacheId = loc[i].split("/")[0];
				        String fileName = loc[i].split("/")[1];

				        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);
				        b = (Notice) CommonContentHelper.service.attach(b, cacheDs, fileName, null, ContentRoleType.SECONDARY);
						//CommonContentHelper.service.attach(b, loc[i]);
					}
				}
				
				reOid = b.getPersistInfo().getObjectIdentifier().toString();
			}
			trx.commit();
			trx = null;
			result.put("oid", reOid);
	        result.put("result", true);
       } catch(Exception e) {
    	   e.printStackTrace();
           result.put("result", false);
           result.put("msg", e.getLocalizedMessage());
           throw e;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return result;
	}
	
	@Override
	public void updateCount(String oid) throws Exception{
		
		Notice notice = (Notice)CommonUtil.getObject(oid);
		int count = notice.getCount() + 1;
		notice.setCount(count);
		PersistenceHelper.manager.modify(notice);
		
	}
	
	@Override
	public List<NoticeData> getPopUpNotice(){
		
		List<NoticeData> returnData = new ArrayList<NoticeData>();
		
		try{
			QuerySpec query = new QuerySpec();
		    int idx = query.addClassList(Notice.class, true);
			query.appendWhere(new SearchCondition(Notice.class, Notice.IS_POPUP, SearchCondition.IS_TRUE), new int[]{idx});
			QueryResult rt = PersistenceHelper.manager.find(query);
			while(rt.hasMoreElements()){
				Object[] o = (Object[]) rt.nextElement();
				Notice notice = (Notice) o[0];
				NoticeData data = new NoticeData(notice);
				String oid = CommonUtil.getOIDString(notice);
				returnData.add(data);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return returnData;
	}
	
	public void updateIsPopupFalse(){
		
		try{
			QuerySpec query = new QuerySpec();
		    int idx = query.addClassList(Notice.class, true);
			query.appendWhere(new SearchCondition(Notice.class, Notice.IS_POPUP, SearchCondition.IS_TRUE), new int[]{idx});
			QueryResult rt = PersistenceHelper.manager.find(query);
			while(rt.hasMoreElements()){
				Object[] o = (Object[]) rt.nextElement();
				Notice notice = (Notice) o[0];
				notice.setIsPopup(false);
				PersistenceHelper.manager.modify(notice);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
			
	}

}
