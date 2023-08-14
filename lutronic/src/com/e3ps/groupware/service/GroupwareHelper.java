package com.e3ps.groupware.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.beans.UserData;

import wt.enterprise.Master;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

public class GroupwareHelper {
	public static final GroupwareService service = ServiceFactory.getService(GroupwareService.class);
	public static final GroupwareHelper manager = new GroupwareHelper();
	
	public Map<String, Object> listNotice(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<NoticeData> list = new ArrayList<>();
		
		QuerySpec query = new QuerySpec();
	    int idx = query.addClassList(Notice.class, true);
	    
	    try {
	    	String nameValue = StringUtil.checkNull((String) params.get("name"));
		    String creator = StringUtil.checkNull((String) params.get("creator"));

			if(nameValue != null && nameValue.trim().length() > 0){
				query.appendWhere(new SearchCondition(Notice.class, "title", SearchCondition.LIKE, "%" + nameValue.trim() + "%", false), new int[]{idx});
		    }	

			if(creator!=null && creator.length()>0) {
				ReferenceFactory rf = new ReferenceFactory();
				People people = (People)rf.getReference(creator).getObject();
				WTUser user = people.getUser();

				if(query.getConditionCount()>0)query.appendAnd();
				query.appendWhere(new SearchCondition(Notice.class,"owner.key","=", PersistenceHelper.getObjectIdentifier( user )), new int[]{idx});
			}

			query.appendOrderBy(new OrderBy(new ClassAttribute(Notice.class,"thePersistInfo.createStamp"), true), new int[] { idx });
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				NoticeData data = new NoticeData((Notice) obj[0]);
				list.add(data);
			}

			map.put("list", list);
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
	    
	}
	
	public Map<String,Object> listItem(Map<String, Object> params) throws Exception {
		int page = StringUtil.getIntParameter((String)params.get("page"), 1);
		int rows = StringUtil.getIntParameter((String)params.get("rows"), 10);
		int formPage = StringUtil.getIntParameter((String)params.get("formPage"), 10);
		boolean isDistribute = StringUtil.checkNull((String)params.get("distribute")).equals("true");
		
		String sessionId = (String)params.get("sessionId");
		
		String state = (String)params.get("state");
		String className = (String)params.get("className");
		
		PagingQueryResult qr = null;

		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {

			if(state==null)state = "own";
			
			long longOid =  CommonUtil.getOIDLongValue((WTUser)SessionHelper.manager.getPrincipal());
			
	        QuerySpec query = new QuerySpec();
	        int idx = query.addClassList(WFItem.class, true);
	        int idx_Link = query.addClassList(WFItemUserLink.class, true);
	        
	        if(query.getConditionCount()>0) query.appendAnd();
	        query.appendWhere(new SearchCondition(WFItem.class,"thePersistInfo.theObjectIdentifier.id",WFItemUserLink.class,"roleBObjectRef.key.id"),new int[]{idx,idx_Link});
			
			if(!CommonUtil.isAdmin()) {
				if(query.getConditionCount()>0) query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "roleAObjectRef.key.id", "=", longOid), new int[] { idx_Link });
			}
			
			if(query.getConditionCount()>0) query.appendAnd();
	        query.appendWhere(SearchUtil.getSearchCondition(WFItemUserLink.class, "deleteFlag", "false"),new int[] { idx_Link });
	        
	        if(query.getConditionCount()>0) query.appendAnd();
	        query.appendWhere(SearchUtil.getSearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED, SearchCondition.IS_FALSE),new int[] { idx_Link });
	       
	        if(query.getConditionCount()>0) query.appendAnd();
			query.appendWhere(new SearchCondition(WFItem.class, "wfObjectReference.key.classname", SearchCondition.NOT_EQUAL, "com.e3ps.change.EChangeActivity"), new int[] { idx });
			
			/* 3개월 이전 데이터 제외
			if(query.getConditionCount()>0) query.appendAnd();
			query.appendWhere(new SearchCondition(WFItem.class, "thePersistInfo.updateStamp", SearchCondition.GREATER_THAN, DateUtil.getOneMonthBefore(90)), new int[] { idx });
			*/
			if("receive".equals(state)){
				if(query.getConditionCount()>0) query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "activityName", SearchCondition.EQUAL, "수신"), new int[] { idx_Link });

				if(query.getConditionCount()>0) query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", SearchCondition.EQUAL, "수신"), new int[] { idx_Link });

				if(className!=null && className.length()>0){
				if(query.getConditionCount()>0) query.appendAnd();
				query.appendWhere(new SearchCondition(WFItem.class, "wfObjectReference.key.classname", SearchCondition.EQUAL,className ), new int[] { idx });
				}

				SearchUtil.setOrderBy(query, WFItem.class, idx, "thePersistInfo.updateStamp", true);   
			}else {
			
				if(query.getConditionCount()>0) query.appendAnd();
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", SearchCondition.NOT_EQUAL, "위임"), new int[] { idx_Link });
				query.appendOr();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { idx_Link });
				query.appendCloseParen();
				if("ing".equals(state)){
					if(query.getConditionCount()>0) query.appendAnd();
					query.appendOpenParen();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "COMPLETED"), new int[] { idx });
		            query.appendAnd();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "APPROVED"), new int[] { idx });
		            query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "CANCELLED"), new int[] { idx });
		            query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "INWORK"), new int[] { idx });
		            query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "RETURN"), new int[] { idx });
		            query.appendAnd();
		            //승인 요청 제외
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "APPROVE_REQUEST"), new int[] { idx });
		            query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "REWORK"), new int[] { idx });
		            
		            
		            query.appendCloseParen();
				
		            if(query.getConditionCount()>0) query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME, SearchCondition.NOT_EQUAL, "수신"), new int[] { idx_Link });
				}else if ("complete".equals(state)){
				
					if(query.getConditionCount()>0) query.appendAnd();
					query.appendOpenParen();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "COMPLETED"), new int[] { idx });
		            query.appendOr();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "APPROVED"), new int[] { idx });
					query.appendOr();
		            query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "CANCELLED"), new int[] { idx });
		            query.appendCloseParen();
		            
		            if(query.getConditionCount()>0) query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME, SearchCondition.NOT_EQUAL, "수신"), new int[] { idx_Link });
		        }
				
				
				
				if(className!=null && className.length()>0){
		            query.appendAnd();
		            query.appendWhere(new SearchCondition(WFItem.class, "wfObjectReference.key.classname", "=",className ), new int[] { idx });
				}
				
		        SearchUtil.setOrderBy(query, WFItem.class, idx, "thePersistInfo.updateStamp", true);   
			}
			//System.out.println("======================================================");
			//System.out.println(query);
			//System.out.println("======================================================");
	        qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
			
		}
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    
	    //2016.03.07 row id 가 중복일 경우 오류방지를 위해 index 추가
	    int index = 0;
		
	    List<WFItem> wfList = new ArrayList<WFItem>();
	    Map<String,Object> map = new HashMap<String,Object>();
	    
		while(qr.hasMoreElements()) {
			index++;
			
			Object o[] = (Object[])qr.nextElement();
			WFItem wfItem = (WFItem)o[0];
			WFItemUserLink wfItemLink = (WFItemUserLink)o[1];
			WTObject obj = null;
			ArrayList users = null;
			try{
				obj = wfItem.getWfObject();
				if(obj==null) {
					PersistenceHelper.manager.delete(wfItem);
				}
			}catch(Exception e){
			    //System.out.println(" Error : 객체가 존재하지 않습니다.");
			    PersistenceHelper.manager.delete(wfItem);
			    continue;
			}
			String viewOid = CommonUtil.getOIDString(obj);
			if(obj instanceof Master) {
				obj = ObjectUtil.getVersionObject((wt.enterprise.Master)obj, wfItem.getObjectVersion());
				
				viewOid = CommonUtil.getOIDString(obj);
			}
			
			boolean isECO = false;
			Timestamp endTime = null;
			/*
			if(obj instanceof EChangeOrder){
				EChangeOrder eco = (EChangeOrder)obj;
				if(eco.getLifeCycleState().toString().equals("BOM_CHANGE") || 
						eco.getLifeCycleState().toString().equals("VALIDITYCHECK")||
						eco.getLifeCycleState().toString().equals("RELATEDWORK")||
						eco.getLifeCycleState().toString().equals("RELATEDCONFIRM")){
					users = ECAHelper.service.getWorkingECAUser(eco);
					isECO = true;
				}
				
				EChangeActivity eca = ECAHelper.service.getLastStepECA(eco);
				
				QueryResult result = E3PSWorkflowHelper.service.getWfProcess((LifeCycleManaged)eca);
				if(result.hasMoreElements()) {
					endTime = ((WfProcess)result.nextElement()).getEndTime();
				}
	        }else{
	        */
			Persistable per = null;
			State rState = null;
			if(wfItem.getObjectState() != null) {
				rState = State.toState(wfItem.getObjectState());
				per = wfItem.getWfObject();
			}
			if(rState == null) {
				rState = ((LifeCycleManaged)obj).getState().getState();
			}
			if(obj instanceof LifeCycleManaged) {
				QueryResult result = E3PSWorkflowHelper.service.getWfProcess((LifeCycleManaged)obj);
				//System.out.println("result.Size = "+result.size());
				if(result.size()>1){
					ArrayList<Date> list = new ArrayList<Date>();
					while(result.hasMoreElements()){
						WfProcess wfprocess = (WfProcess)result.nextElement();
						Timestamp tp =wfprocess.getEndTime();
						String stateSTR = wfprocess.getState().toString();
						//System.out.println("stateSTR="+stateSTR);
						if(null!=tp){
							Date date = new Date(tp.getTime());
							//System.out.println(date.toString());
							if(!stateSTR.equals("OPEN_RUNNING"))
								list.add(date);
								
						}
					}
					
					Collections.sort(list,new Comparator<Date>() {
				        @Override
				        public int compare(Date object1, Date object2) {
				            return (int) (object2.compareTo(object1));
				        }
				    });
					Date endDate = (Date)list.get(0);
					Timestamp data = new Timestamp(endDate.getTime());
					String statestr = rState.getDisplay(Locale.KOREA);
					if("승인됨".equals(statestr)){
						endTime = data;
					}
				}else{
					if(result.hasMoreElements()) {
						endTime = ((WfProcess)result.nextElement()).getEndTime();
					}
				}
			}
	        
			if(!isECO ){
				users = WFItemHelper.service.getProcessingUser(wfItem);
			}
			
			String[] objName = null;
			
			try{
				if(WorklistHelper.service.getWorkItemName(obj) != null){
					 objName = WorklistHelper.service.getWorkItemName(obj);
				}
			}catch(Exception e){e.printStackTrace();}
			
			String objName0 = "객체 구분 없음";
			String objName1 = "객체 번호 없음";
			String objName2 = "객체 이름 없음";
			
			if(objName != null){
				objName0 = objName[0];
				objName1 = objName[1];
				objName2 = objName[2];
			}
			wfList.add(wfItem);
		}
		map.put("list", wfList);
		return map;
	}
	
	public Map<String,Object> listCompanyTree(Map<String,Object> params) throws Exception {
		int page = StringUtil.getIntParameter((String) params.get("page"), 1);
		int rows = StringUtil.getIntParameter((String) params.get("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) params.get("formPage"), 10);
		String sessionId = (String) params.get("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			String oid = StringUtil.checkNull((String) params.get("oid"));
			String sortType = StringUtil.checkReplaceStr((String) params.get("sortType"), "false");
			String name = StringUtil.checkNull((String) params.get("name"));
			String command = StringUtil.checkNull((String) params.get("command"));
			
			Department dept = null;
			
			if(oid!=null && oid.length()>0 && !oid.equals("null") && !oid.equals("root") ){
				dept = (Department)CommonUtil.getObject(oid);
			}
			
			QuerySpec qs = new QuerySpec();
			
			int ii = qs.addClassList(People.class,true);
			int jj = qs.addClassList(WTUser.class,false);
			int kk = qs.addClassList(Department.class,false);

			qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
			qs.appendAnd();

		    qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});	
		    qs.appendAnd();
		    
		    qs.appendWhere(new SearchCondition(WTUser.class,WTUser.DISABLED,SearchCondition.IS_FALSE),new int[] {jj});
		    
			if(dept!=null){
				if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
				qs.appendWhere(new SearchCondition(Department.class,"thePersistInfo.theObjectIdentifier.id","=",dept.getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
			}

			if (name.length() > 0){
		        if (qs.getConditionCount() > 0)
		        	qs.appendAnd();
		        qs.appendWhere(new SearchCondition(People.class, People.NAME, SearchCondition.LIKE, "%" + name + "%"),
		                         new int[] { ii });
		    }
		    
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, qs, true);
		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    int rowCount    = control.getTopListCount();
		
	    List<PeopleData> list = new ArrayList<PeopleData>();
	    Map<String,Object> result = new HashMap<String, Object>();
	    while(qr.hasMoreElements()) {
	    	Object[] obj = (Object[])qr.nextElement();
			PeopleData data = new PeopleData((People) obj[0]);
			list.add(data);
	    }
	    result.put("list", list);
		
		return result;
	}
}
