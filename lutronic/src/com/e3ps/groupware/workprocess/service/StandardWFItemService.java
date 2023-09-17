package com.e3ps.groupware.workprocess.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.PhaseTemplate;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.pds.DatabaseInfoUtilities;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.ECOService;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.jdf.log.Logger;
import com.e3ps.common.mail.MailUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.workprocess.ApprovalLineTemplate;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.WFObjectWFItemLink;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.UserHelper;

@SuppressWarnings("serial")
public class StandardWFItemService extends StandardManager implements WFItemService {

	public static StandardWFItemService newStandardWFItemService() throws WTException {
		final StandardWFItemService instance = new StandardWFItemService();
		instance.initialize();
		return instance;
	}
	
	/**워크플로 상태변경 로봇 이벤트 실행시 
     * @param _obj
     * @param _event
     */
	@Override
    public void eventListener(Object _obj, String _event){
		
    	if (_event.equals("STATE_CHANGE")) {
	    	setState((WTObject)_obj);
		//위임시 이메일 송부 keyedevent.getEventType() = POST_STORE and userlink disable true인 경우에만 발생.
    	}else if (_event.equals("POST_STORE") && _obj instanceof WFItemUserLink)  {
    			WTObject obj = (WTObject)_obj;
	    		//System.out.println("위임시 이메일 mail start ....." + obj);
	    		try {
	    			WFItemUserLink link = (WFItemUserLink) obj;
	    			String linkState = link.getState();
	    			boolean isDisable = link.isDisabled();
	    			boolean isComm = link.isCommissionDabled();//위임 이벤트인지 유무 체크
	    			//System.out.println("위임시 이메일 mail start ..... isDisable =" + isDisable+"\tisComm="+isComm+"\tlinkState="+linkState);
	    			if(!isDisable && isComm && "위임받음".equals(linkState)){
		    			WFItem wfitem = link.getWfitem();
		    			Persistable per = wfitem.getWfObject();
		    			//System.out.println("111per="+per);
		    			if(per instanceof WTDocumentMaster){
		    				String number= ((WTDocumentMaster) per).getNumber();
		    				WTDocument document = DocumentHelper.service.getLastDocument(number);
		    				String vrOid = CommonUtil.getVROID(document);
		    				document = (WTDocument)CommonUtil.getObject(vrOid);
		    				per = document;
		    			}
		    			WorkItem item = getWorkItem(per);
		    			//System.out.println("item null check " + (null!=item)+"\tper="+per);
		    			if(null!=item){
		         		boolean mmmm = MailUtil.manager.taskNoticeMail(item);
		         		//System.out.println("mail end ....." + mmmm);
		    			}
	    			}
	         	} catch (Exception e) {
	         		e.printStackTrace();
	         	}
    	}
    }
	
	/**
     * 워크플로 상태변경 로봇 실행시 WFItem 상태 변경
     * @param _obj
     */
	@Override
    public void setState(WTObject _obj){
    	WFItem wfItem = WFItemHelper.service.getWFItem((WTObject)_obj);
        try {
        	LifeCycleManaged lm = (LifeCycleManaged)_obj;
            if (wfItem != null) {
                // WFItem 수정 //
            	wfItem.setObjectState(lm.getLifeCycleState().toString());
            	PersistenceHelper.manager.modify(wfItem);
            }
            
        }catch(Exception ex){
        	ex.printStackTrace();
        }
    }
	
	@Override
    public List<Map<String,String>> getLifeCycleState(String lifeCycle, String state) throws Exception {
    	List<Map<String,String>> stateVec = new ArrayList<Map<String,String>>();
    	
		LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(lifeCycle, WCUtil.getWTContainerRef());
		Vector states = LifeCycleHelper.service.getPhaseTemplates(lct);
		PhaseTemplate pt = null;
		State lcState = null;
		for (int i = 0; i < states.size(); i++) {
			pt = (PhaseTemplate) states.get(i);
			lcState = pt.getPhaseState();
			// stateVec.add(lcState);
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", lcState.getDisplay(Message.getLocale()));
			map.put("value", lcState.toString());
			if(StringUtil.checkString(state)) {
				
				for(String a : state.split(",")) {
					if(a.equals(lcState.toString())) {
						stateVec.add(map);
					}
				}
				
				/*
				if(state.equals(lcState.toString())) {
					stateVec.add(map);
				}
				*/
			}else {
				stateVec.add(map);
			}
			
		}
    	
    	return stateVec;
    	
    }
	
	/**
     * 결재 정보를 모두 지운다.
     * 
     * @param persistable
     */
	@Override
    public void deleteWFItem(Persistable persistable) {
		try{
			if (persistable instanceof WTObject)
	        {
	        	//SessionHelper.manager.setAdministrator();
	            WFItem wfitem = getWFItem((WTObject) persistable);
	            deleteWFItem(wfitem);
	           // SessionHelper.manager.setPrincipal(SessionHelper.manager.getPrincipal().getName());
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
        
    }
    
	@Override
    public WorkItem getWorkItem(Persistable per) throws WTException{
         QueryResult qr = WorkflowHelper.service.getWorkItems(per);
         if(qr.hasMoreElements()){
                return (WorkItem)qr.nextElement();
         }
         return null;
    }

	
	
    /**
     * 결재 객체에서 WFItem을 찾는다.
     * 
     * @param wtobject:결재객체
     * @return WFItem
     */
    @Override
    public WFItem getWFItem(WTObject wtobject) {
        WFItem item = null;
        String oid = CommonUtil.getOIDString(wtobject);
        long oidLong = 0;
        String versionStr = " ";
        try {
            if (wtobject instanceof LifeCycleManaged) {
                if (wtobject instanceof RevisionControlled) {
                    RevisionControlled rc = (RevisionControlled) wtobject;
                    versionStr = rc.getVersionIdentifier().getValue();
                    oid = CommonUtil.getOIDString((Master) rc.getMaster());
                    oidLong = CommonUtil.getOIDLongValue((Master) rc.getMaster());
                }
                else {
                    oidLong = CommonUtil.getOIDLongValue(wtobject);
                }
            }

            Class target = WFItem.class;
            QuerySpec query = new QuerySpec();
            int idx = query.appendClassList(target, true);
            query.appendWhere(new SearchCondition(target, "wfObjectReference.key.classname", "=", oid.substring(0, oid.lastIndexOf(":"))),
                              new int[] { idx });
            query.appendAnd();
            query.appendWhere(new SearchCondition(target, "wfObjectReference.key.id", "=", oidLong), new int[] { idx });
            query.appendAnd();
            if (!" ".equals(versionStr)) query.appendWhere(new SearchCondition(target, "objectVersion", "=", versionStr), new int[] { idx });
            else query.appendWhere(new SearchCondition(target, "objectVersion", true), new int[] { idx });
            
            QueryResult qr = PersistenceHelper.manager.find(query);
            while (qr.hasMoreElements())
            {
                Object[] obj = (Object[]) qr.nextElement();
                WFItem element = (WFItem) obj[0];
                item = element;
            }
            Logger.user.println("> WFItemHelper.manager.getWFItem : query = " + query);
            //System.out.println("> WFItemHelper.manager.getWFItem : query = " + query);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return item;
    }
    
    /**
     * WFItem의 state를 set한다.
     * 
     * @param wtobject:결재객체
     * @param state
     */
    @Override
    public void setWFItemState(WTObject wtobject, String state) {
        E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) wtobject, state);
        
        WFItem wfItem = getWFItem(wtobject);
        try {
            if (wfItem != null) {
            	
            	
            	LifeCycleManaged lm = (LifeCycleManaged)wtobject;
            	/*
            	if(wtobject instanceof EChangeOrder){
            		if(lm.getLifeCycleState().toString().equals("APPROVED")){
            			return;
            		}
            		E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) wfItem, state);
            	}else{
            		wfItem.setObjectState(lm.getLifeCycleState().toString());
                	PersistenceHelper.manager.modify(wfItem);
            	}
            	*/
            	wfItem.setObjectState(lm.getLifeCycleState().toString());
            	PersistenceHelper.manager.modify(wfItem);
                //System.out.println("[WFItemHelper][WFItem][oid]"+wfItem.getPersistInfo().getObjectIdentifier().toString());
                //System.out.println("[WFItemHelper][WFItem][state]"+state);
                //System.out.println("[WFItemHelper][WFItem][lifecycle]"+lm.getLifeCycleState().toString());
            }
            
        }                    
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * WFItem 에 결재 라인이 남아있는지를 체크한다.
     * 
     * @param wtobject:결재객체
     * @return true:결재라인이 남음, false:결재라인이 없음
     */
    @Override
    public boolean isRemainProcessLine(WTObject wtobject) {
        //System.out.println("## isRemainProcessLink##");
        boolean result = false;

        // WFItemUserLink 가 유무 검사
        WFItem wfItem = getWFItem(wtobject);
        if (wfItem != null) {
            try {
                QuerySpec query = new QuerySpec();
                query.addClassList(WTUser.class, true);
                int linkIdx = query.addClassList(WFItemUserLink.class, true);
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "processOrder", ">", 0), new int[] { linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "activityName", "<>", "수신"), new int[] { linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "activityName", "<>", "기안"), new int[] { linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "disabled", SearchCondition.IS_FALSE), new int[]{ linkIdx });
                
                QueryResult qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
                
                //System.out.println("[isRemainProcessLine]:"+qr.size());
                result = qr.hasMoreElements() ? true : false;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * WFItem 의 다음 결재라인의 ActivityName을 반환한다.
     * 
     * @param wtobject:결재객체
     * @return WFItemUserLink의 ActivityName
     */
    @Override
    public String getNextActivityName(WTObject wtobject)
    {
        String result = "결재";

        WFItem wfItem = getWFItem(wtobject);
        WFItemUserLink link = null;
        //System.out.println("[wfItem OID]: " + wfItem.getPersistInfo().getObjectIdentifier().toString());
        if (wfItem != null) {
            try {
                QuerySpec query = new QuerySpec();
                query.addClassList(WTUser.class, true);
                int linkIdx = query.addClassList(WFItemUserLink.class, true);
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "disabled", SearchCondition.IS_FALSE), new int[]{ linkIdx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "processOrder", ">", 0), new int[] { linkIdx });
                SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, "processOrder", "asc");

                
                QueryResult qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
                int tempOrder = -1;
                while (qr.hasMoreElements()) {
                    link = (WFItemUserLink) qr.nextElement();
                    if (tempOrder == -1) {
                    	//System.out.println("wfItem tempOrder == -1: " + wfItem.getPersistInfo().getObjectIdentifier().toString());
                        tempOrder = link.getProcessOrder();
                        result = link.getActivityName();
                        E3PSWorkflowHelper.service.deleteRole((LifeCycleManaged) wtobject, "APPROVER");
                        E3PSWorkflowHelper.service.addParticipant((LifeCycleManaged) wtobject, "APPROVER", link.getUser().getName()); 
                        link.setReceiveDate(new java.sql.Timestamp(new java.util.Date().getTime()));
                        PersistenceHelper.manager.modify(link);
                    /*2016.04.26 병렬 합의 추가 (엄부장님이 주신 소스 수정함)*/
	                }else if (tempOrder == link.getProcessOrder()) {
	                	//System.out.println("wfItem link.getProcessOrder(): " + link.getProcessOrder());
	                	tempOrder = link.getProcessOrder();
	                    // 협조, 합의 일때 세팅됨.
	                    E3PSWorkflowHelper.service.addParticipant((LifeCycleManaged) wtobject, "APPROVER", link.getUser().getName());
	                    result = link.getActivityName();
	                    link.setReceiveDate(new java.sql.Timestamp(new java.util.Date().getTime()));
	                    PersistenceHelper.manager.modify(link);
	                }else{
	                	if (tempOrder != link.getProcessOrder()) {
							break;
						}
	                	
	                	 E3PSWorkflowHelper.service.addParticipant((LifeCycleManaged) wtobject, "APPROVER", link.getUser().getName());
	                     result = link.getActivityName();
	                     link.setReceiveDate(new java.sql.Timestamp(new java.util.Date().getTime()));
	                     PersistenceHelper.manager.modify(link);
	                }
                    
                    /*
                    else if (tempOrder == link.getProcessOrder()) {
                    	System.out.println("wfItem link.getProcessOrder(): " + link.getProcessOrder());
                        // 협조, 합의 일때 세팅됨.
                        E3PSWorkflowHelper.service.addParticipant((LifeCycleManaged) wtobject, "APPROVER", link.getUser().getName());
                        result = link.getActivityName();
                        link.setReceiveDate(new java.sql.Timestamp(new java.util.Date().getTime()));
                        PersistenceHelper.manager.modify(link);
                    }
                    else break;
                    */
                    //System.out.println(">> =" + link.getActivityName() + ":" + link.getUser().getName() + ":" + link.getProcessOrder());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println("wfItem link.result(): " + result);
        return result;
    }
    
    /**
     * 반려후 재작업 진행시 (워크플러워에서 실행);
     * 
     * @param wtobject
     */
    @Override
    public void setReworkAppLine(WTObject wtobject){
    	
    	//System.out.println(":::::::::::: WFItemHelper.setReworkAppLine START ::::::::::::::");
    	try{
    		Config conf = ConfigImpl.getInstance();
    		WFItem wfItem = getWFItem(wtobject);
        	
    		int j=WFItemHelper.service.getMaxOrderNumber(wfItem, "") + 1;
    		int seq = WFItemHelper.service.getMaxSeq(wfItem) + 1;
    		
    		
        	Vector<WFItemUserLink> vec = getAppline(wfItem, false, "", ""); //결재라인 사용 
        	for(int i = 0 ; i < vec.size() ; i++){
        		WFItemUserLink link = vec.get(i);
        		
        		if("위임".equals(link.getState())) {
        			link.setDisabled(true);
            		PersistenceHelper.manager.modify(link);
        			continue;  //위임의 결재이력은 제외
        		}
        		
        		if(link.getActivityName().equals("기안") ){ //기안
        			this.newWFItemUserLink(link.getUser(), wfItem, "기안", j++, seq);
        		}else if(link.getActivityName().equals("결재")){ //결재
        			this.newWFItemUserLink(link.getUser(), wfItem, "결재", j++, seq);
        		}else if(link.getActivityName().equals("합의")){ //합의
        			this.newWFItemUserLink(link.getUser(), wfItem, "합의", j++, seq);
        		}else if(link.getActivityName().equals("수신")){ //수신
        			this.newWFItemUserLink(link.getUser(), wfItem, "수신", j++, seq);
        		}
        		link.setDisabled(true);
        		PersistenceHelper.manager.modify(link);
        	
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	//System.out.println(":::::::::::: WFItemHelper.setReworkAppLine END ::::::::::::::");
    }
    
	/**
	 * Order Max 값
	 * 
	 * @param item
	 * @return
	 */
    @Override
	public int getMaxOrderNumber(WFItem item, String StateYn) {

		int maxCount = 0;

		try {

			QuerySpec qs = new QuerySpec();

			int idx = qs.appendClassList(WFItemUserLink.class, false);

			// SQLFunction maxFunction = SQLFunction.newSQLFunction(
			// SQLFunction.MAXIMUM, new ClassAttribute( WFItemUserLink.class,
			// WFItemUserLink.PROCESS_ORDER ));
			// qs.appendSelect( maxFunction, idx, false );
			// qs.setAdvancedQueryEnabled( true );
			// qs.setDescendantQuery(false);

			ClassInfo classinfo = WTIntrospector.getClassInfo(WFItemUserLink.class);
			String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, WFItemUserLink.PROCESS_ORDER);
			qs.setAdvancedQueryEnabled(true);
			qs.setDescendantQuery(false);
			qs.addClassList(WFItemUserLink.class, false);
			KeywordExpression ke = new KeywordExpression("NVL(Max(" + task_seqColumnName + "), 0) ");
			ke.setColumnAlias("task_seq");
			qs.appendSelect(ke, new int[] { 0 }, false);

			qs.appendWhere( new SearchCondition(WFItemUserLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(item)), new int[] { idx });
			if ("Y".equals(StateYn)) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WFItemUserLink.class,
						"state", false), new int[] { idx });
			}

			QueryResult rt = PersistenceHelper.manager.find(qs);
			Object obj[] = (Object[]) rt.nextElement();
			maxCount = ((java.math.BigDecimal) obj[0]).intValue();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return maxCount;
	}
    
	/**
	 * Seq Max 값 구하기
	 * 
	 * @param parent
	 * @return
	 * @throws Exception
	 */
    @Override
	public int getMaxSeq(WFItem item) throws Exception {

		int result = 0;

		try {

			Class taskClass = WFItemUserLink.class;
			ClassInfo classinfo = WTIntrospector.getClassInfo(taskClass);
			String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, WFItemUserLink.SEQ_NO);

			QuerySpec spec = new QuerySpec();
			spec.setAdvancedQueryEnabled(true);
			spec.setDescendantQuery(false);
			spec.addClassList(taskClass, false);
			KeywordExpression ke = new KeywordExpression("NVL(Max(" + task_seqColumnName + "), 0) ");
			ke.setColumnAlias("task_seq");
			spec.appendSelect(ke, new int[] { 0 }, false);

			long id = 0;

			if (item != null) {
				id = item.getPersistInfo().getObjectIdentifier().getId();
			}

			spec.appendWhere(new SearchCondition(taskClass, "roleBObjectRef.key.id", "=", id), new int[] { 0 });

			QueryResult qr = PersistenceHelper.manager.find(spec);

			if (qr.hasMoreElements()) {
				Object o[] = (Object[]) qr.nextElement();
				BigDecimal number = (BigDecimal) o[0];
				result = number.intValue();
				//System.out.println("** BigDecimal result : " + result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
    /**
     * 결재선 
     * @param workItem
     * @param disabled disabled : 0 (결재라인 사용) , disabed :1 history(결재라인 사용)
     * @param state 수신,합의,결재,위임(결재 여부)
     * @param activityName 수신,합의,결재
     * @return
     */
    @Override
	public Vector<WFItemUserLink> getAppline(WFItem wfItem, boolean disabled, String state, String activityName) throws Exception {

		Vector<WFItemUserLink> vec = new Vector<WFItemUserLink>();
		try {

			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(WTUser.class, true);
			int linkIdx = query.addClassList(WFItemUserLink.class, true);

			if (disabled) {
				query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED, SearchCondition.IS_TRUE), new int[] { linkIdx });
			} else {
				query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED, SearchCondition.IS_FALSE), new int[] { linkIdx });
			}

			if (state.length() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.STATE, SearchCondition.EQUAL, state), new int[] { linkIdx });
			}

			if (activityName.length() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME, SearchCondition.EQUAL, activityName), new int[] { linkIdx });
			}

			SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, WFItemUserLink.PROCESS_ORDER, "asc");
			SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, "thePersistInfo.createStamp", "asc");
			QueryResult qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
			while (qr.hasMoreElements()) {
				WFItemUserLink link = (WFItemUserLink) qr.nextElement();
				vec.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		return vec;
	}
    
    /**WFItemUserLink 객체 생성
     * @param user
     * @param item
     * @param activity
     * @param order
     */
    @Override
    public void newWFItemUserLink(WTUser user, WFItem item, String activity, int order){
    	
		try {
			int seq = getMaxSeq(item);
			newWFItemUserLink(user, item, activity, order, seq);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * WFItemUserLink객체를 생성한다.
     * 
     * @param user
     *            결재라인에 포함된 WTUser
     * @param item
     *            WFItem
     * @param activity
     *            WFItemUserLink.activityName
     */
    @Override
	public void newWFItemUserLink(WTUser user, WFItem item, String activity, int order, int seq) throws Exception {
			//System.out.println("owner=" + item.getOwnership().getOwner().getFullName());
			WFItemUserLink link = WFItemUserLink.newWFItemUserLink(user, item);

			PeopleDTO pData = new PeopleDTO(user);
			link.setDepartmentName(pData.departmentName);
			link.setActivityName(activity);
			link.setProcessOrder(order);

			// history 생성을 위해.
			link.setDisabled(false);
			link.setSeqNo(seq);

			PersistenceHelper.manager.save(link);
	}
    
    @Override
    public void reworkDataInit(WFItem wfItem) {
    	
    	reworkDataInit(wfItem, true);
    }
    
    @Override
    public void reworkDataInit(WFItem wfItem,boolean isInit) {
		try {
			WTUser creator = null;
			//System.out.println("=====   reworkDataInit START ====== isInit :" + isInit);

			QueryResult qr = null;
			QuerySpec query = getLinkQuerySpec(wfItem, "asc");
			qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
			//System.out.println("===================WFItemHelper::setWFItemState:qr.size=" + qr.size());
			int i = 0 ; 
			while (qr.hasMoreElements()) {
				WFItemUserLink link = (WFItemUserLink) qr.nextElement();
				//System.out.println("BBB.i= "+i );
				//System.out.println("Approver :" + link.getApprover()+" : "+link.getState()+":"+link.getProcessOrder()+" : !isInit =" + !isInit +"," + link.getState() != null);
				//System.out.println("CCC.i= "+i +"" link.getApprover());
				i++;
				if (link.getProcessOrder() == 0) {
					creator = link.getUser();
					//System.out.println("REWORK CREATOR FOUND - " + creator.getName());
				} else {
					
					//결재선 초기화 단계에서 위임은 제외
					if("위임".equals(link.getState())) {
						continue; 
					}
					
					if( !isInit && (link.getState() != null)){
						continue;
					}
					//System.out.println("change comment FOUND - " + link.getComment()+"\t User = "+link.getApprover());
					link.setComment(null);
					link.setState(null);
					link.setProcessDate(null);
					link.setReceiveDate(null);
					PersistenceHelper.manager.modify(link);
					//System.out.println("change after comment FOUND - " + link.getComment()+"\t User = "+link.getApprover());
				}

			}
			//System.out.println("=====   reworkDataInit END ======");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public QuerySpec getLinkQuerySpec(WFItem wfItem, String order) {
        QuerySpec query = null;
        try
        {
            query = new QuerySpec();
            query.addClassList(WTUser.class, true);
            int linkIdx = query.addClassList(WFItemUserLink.class, true);
            
            //query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", false), new int[] { linkIdx });
            //query.appendAnd();
            query.appendWhere(new SearchCondition(WFItemUserLink.class, "disabled", SearchCondition.IS_FALSE), new int[]{linkIdx});
            //query.appendAnd();
            //query.appendWhere(new SearchCondition(WFItemUserLink.class, "processOrder", ">", 0), new int[] { linkIdx });
            SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, "processOrder", order);
            // qr = PersistenceHelper.manager.navigate(wfItem, "user", query,
            // false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            query = null;
        }
        return query;
    }
    
    /**
     * 전체 결재라인 
     * @param wfItem
     * @param disabled
     * @param state
     * @param activityName
     * @return
     */
    @Override
	public Vector<WFItemUserLink> getTotalAppline(WFItem wfItem) {

		Vector<WFItemUserLink> vec = new Vector<WFItemUserLink>();
		try {

			QuerySpec query = new QuerySpec();
			int linkIdx = query.addClassList(WFItemUserLink.class, true);
			SearchCondition sc = new SearchCondition(WFItemUserLink.class, "roleBObjectRef.key.id", "=", CommonUtil.getOIDLongValue(wfItem));
			query.appendWhere(sc, new int[] { linkIdx });
			SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, WFItemUserLink.PROCESS_ORDER, "asc");
			QueryResult qr = PersistenceHelper.manager.find(query);
			while (qr.hasMoreElements()) {
				Object[] ob = (Object[]) qr.nextElement();
				WFItemUserLink link = (WFItemUserLink) ob[0];
				vec.add(link);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}
    
    /**
     * WfActivity Active명으로 WfItem Active명으로 치환
     * @param activityName
     * @return
     */
    @Override
	public String getWFItemActivityName(String activityName) {

		if (activityName.equals("결재선지정")) {
			activityName = "기안";
		} else if (activityName.equals("결재")) {
			activityName = activityName;
		} else if (activityName.equals("합의")) {
			activityName = activityName;
		} else if (activityName.equals("협조")) {
			activityName = activityName;
		} else if (activityName.equals("반려확인")) {
			activityName = activityName;
		} else if (activityName.equals("재작업")) {
			activityName = "기안";
		} else {
			activityName = activityName;
		}

		return activityName;
	}
    
	/**
	 * 결재자 WFItem 객체 찾기
	 * 
	 * @param owner
	 * @param wfItem
	 * @param activityName
	 * @return
	 */
    @Override
	public WFItemUserLink getOwnerApplineLink(WTUser owner, WFItem wfItem, String activityName) {
    	//System.out.println("--------------------------------     getOwnerApplineLink   -------------------------");
		try {

			QuerySpec query = new QuerySpec();

			int linkIdx = query.addClassList(WFItemUserLink.class, true);

			query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { linkIdx });

			SearchCondition sc = new SearchCondition(WFItemUserLink.class, "roleBObjectRef.key.id", "=", CommonUtil.getOIDLongValue(wfItem));
			query.appendAnd();
			query.appendWhere(sc, new int[] { linkIdx });

			query.appendAnd();
			query.appendWhere(new SearchCondition(WFItemUserLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(owner)), new int[] { linkIdx });

			query.appendAnd();
			query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED, SearchCondition.IS_FALSE), new int[] { linkIdx });

			query.appendAnd();
			query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME, SearchCondition.EQUAL, activityName), new int[] { linkIdx });

			SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, WFItemUserLink.PROCESS_ORDER, "asc");

			QueryResult qr = PersistenceHelper.manager.find(query);

			//System.out.println("WFItemHelper.getOwnerApplineLink = " + query);
			while (qr.hasMoreElements()) {
				Object[] ob = (Object[]) qr.nextElement();
				WFItemUserLink link = (WFItemUserLink) ob[0];
				return link;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
    
    /**
     * 결재라인의 상태와 comment를 설정한다.(사용하지 않음)
     * 
     * @param workitem
     * @param state
     * @param comment
     */
    @Override
	public void setWFItemUserLinkState(WorkItem workitem, String activityName, String comment) {

		setWFItemUserLinkState(workitem, activityName, comment, "수신");
		//System.out.println("###################### WItemHelper.setWFItemUserLinkState 사용하지 않음 ################################");
	}
    
    @Override
	public void setWFItemUserLinkState(WorkItem workitem, String activityName, String comment, String state) {

		try {
			activityName = this.getWFItemActivityName(activityName);
			//System.out.println("===================WFItemHelper::setWFItemUserLinkState START =================");
			Persistable persistable = WorklistHelper.service.getPBO(workitem);
			WFItem wfItem = getWFItem((WTObject) persistable);
			if (wfItem == null) {
				return;
			}

			WTUser owner = (WTUser) workitem.getOwnership().getOwner().getPrincipal();
			WFItemUserLink link = this.getOwnerApplineLink(owner, wfItem, activityName);
			//System.out.println("=================== activityName =" + activityName);
			//System.out.println("=================== link =" + link);
			if (link == null) {
					Vector<WFItemUserLink> vec = WFItemHelper.service.getTotalAppline(wfItem);
					//System.out.println("=================== vec size =" + vec.size());
					for (WFItemUserLink wfLink : vec) {
						if (wfLink.getProcessOrder() == 0) {
							//System.out.println("wfLink.getComment()="+wfLink.getComment());
							wfLink.setComment(StringUtil.checkNull(comment) + "\n");
							PersistenceHelper.manager.modify(wfLink);
						}
					}
						
				return;
			} else {
				//System.out.println("============================ else   " + state);
				link.setState(state);
				link.setApprover(((WTUser) SessionHelper.manager.getPrincipal()).getFullName());
				link.setComment(StringUtil.checkNull(comment) + "\n");
				link.setProcessDate(new java.sql.Timestamp(new java.util.Date().getTime()));
				PersistenceHelper.manager.modify(link);
			}

			//System.out.println("===================WFItemHelper::setWFItemUserLinkState END =================");
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}
    
    @Override
	public WFItemUserLink newWFItemUserLink(WTUser user, WFItem wfItem, String actName, String actYn) {

		WFItemUserLink newlink = null;

		try {
			if (wfItem != null) {
				newlink = WFItemUserLink.newWFItemUserLink(user, wfItem);

				int porder = getMaxOrderNumber(wfItem, actYn);
				int seqNo = getMaxSeq(wfItem);
				People people = UserHelper.service.getPeople(user);

				newlink.setActivityName(actName);
				if (people.getDepartment() != null) {
					newlink.setDepartmentName(people.getDepartment().getName());
				}
				newlink.setSeqNo(seqNo);
				newlink.setProcessOrder(++porder);
				newlink.setDisabled(true);

				PersistenceHelper.manager.save(newlink);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newlink;
	}
    
    /**
     * WFItem 객체를 생성하고 WFObjectWFItemLink를 생성한다.
     * 
     * @param pbo
     *            결재객체
     * @param status
     *            결재객체의 속성
     * @return item WFItem
     */
    @Override
	public WFItem newWFItem(WTObject obj, WTPrincipal owner) {
		if (obj == null) {
			return null;
		}
		WFItem wfItem = null;
		WTObject tempObj = null;
		try {
			wfItem = WFItem.newWFItem();

			if (obj instanceof RevisionControlled) {
				RevisionControlled rc = (RevisionControlled) obj;
				wfItem.setObjectVersion(VersionControlHelper.getVersionIdentifier((Versioned) rc).getValue());
				wfItem.setObjectState(rc.getLifeCycleState().toString());
				tempObj = (WTObject) rc.getMaster();
				wfItem.setWfObject(tempObj);
			} else {
				LifeCycleManaged lc = (LifeCycleManaged) obj;

				wfItem.setObjectVersion("");
				wfItem.setObjectState(lc.getLifeCycleState().toString());
				wfItem.setWfObject(obj);
			}
			wfItem = (WFItem) PersistenceHelper.manager.save(wfItem);
			wfItem = (WFItem) OwnershipHelper.service.assignOwnership(wfItem, owner);
			WFObjectWFItemLink link = null;
			if (obj instanceof RevisionControlled)
				link = WFObjectWFItemLink.newWFObjectWFItemLink(wfItem, tempObj);
			else
				link = WFObjectWFItemLink.newWFObjectWFItemLink(wfItem, obj);
			PersistenceHelper.manager.save(link);
			int seq = getMaxSeq(wfItem);
			newWFItemUserLink((WTUser) owner, wfItem, "기안", 0, seq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wfItem;
	}
    
    /**수신 라인
     * @param wtobject
     * @throws Exception
     */
    @Override
	public void setRecipientLine(WTObject wtobject) throws Exception {
		WFItem wfItem = getWFItem(wtobject);

		try {

			Vector<WFItemUserLink> vec = getAppline(wfItem, false, "", "수신");
			for (int i = 0; i < vec.size(); i++) {
				WFItemUserLink link = vec.get(i);
				//System.out.println("## setRecipientLine##" + link.getUser().getPersistInfo().getObjectIdentifier().toString());
				E3PSWorkflowHelper.service.addParticipant((LifeCycleManaged) wtobject, "RECIPIENT", link.getUser().getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}
    
    /**수신 결재프로세스 유무 체크
     * @param wtobject
     * @return
     */
    @Override
    public boolean isRecipientLine(WTObject wtobject) {
		//System.out.println("## isRecipientLine##");
		boolean result = false;

		WFItem wfItem = getWFItem(wtobject);
		if (wfItem != null) {
			try {
				Config conf = ConfigImpl.getInstance();

				QuerySpec query = new QuerySpec();
				query.addClassList(WTUser.class, true);
				int linkIdx = query.addClassList(WFItemUserLink.class, true);
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { linkIdx });
				query.appendAnd();
                query.appendWhere(new SearchCondition(WFItemUserLink.class, "disabled", SearchCondition.IS_FALSE), new int[]{linkIdx});
				query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "processOrder", ">", 0), new int[] { linkIdx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "activityName", "=", "수신"), new int[] { linkIdx });
				QueryResult qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
				
				//System.out.println("Receive Line People Size = " + qr.size());
				
				result = qr.hasMoreElements();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
    
    /**
     * 결재중인 유저를 반환하는 메소드
     * 
     * @param wfItem
     * @return WTUser의 ArrayList
     */
    @Override
    public ArrayList getProcessingUser(WFItem wfItem)
    {
        WFItemUserLink link = null;
        ArrayList result = new ArrayList();
        if (wfItem != null)
        {
            Logger.user.println("ObjectState = " + wfItem.getObjectState());
            if ("REWORK".equals(wfItem.getObjectState()) || "RETURN".equals(wfItem.getObjectState()))
            {
                try
                {
                    result.add((WTUser) wfItem.getOwnership().getOwner().getPrincipal());
                    return result;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    QuerySpec query = new QuerySpec();
                    query.addClassList(WTUser.class, true);
                    int linkIdx = query.addClassList(WFItemUserLink.class, true);
                    query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { linkIdx });
                    query.appendAnd();
                    query.appendWhere(new SearchCondition(WFItemUserLink.class, "disabled", SearchCondition.IS_FALSE), new int[]{ linkIdx });
                    query.appendAnd();
                    query.appendWhere(new SearchCondition(WFItemUserLink.class, "processOrder", ">", 0), new int[] { linkIdx });
                    SearchUtil.setOrderBy(query, WFItemUserLink.class, linkIdx, "processOrder", "asc");
                    QueryResult qr = PersistenceHelper.manager.navigate(wfItem, "user", query, false);
                    // System.out.println("WFItemHelper::getProcessingUser:
                    // "+qr.size()+" User(s)");
                    
                    int tempOrder = -1;
                    while (qr.hasMoreElements())
                    {
                        link = (WFItemUserLink) qr.nextElement();
                        if (tempOrder == -1)
                        {
                            tempOrder = link.getProcessOrder();
                            result.add(link.getUser());
                        }
                        else if (tempOrder == link.getProcessOrder())
                        {
                            result.add(link.getUser());
                        }
                        else break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean createAppLine(HashMap<String,String> map) {
    	//System.out.println(":::::::::::: createAppLine START ::::::::::::::");
    	Transaction trx = new Transaction();
    	boolean result = false;
    	try{
    		trx.start();
    		
    		Config conf = ConfigImpl.getInstance();
            String activity = "기안"; //"결재"; //기안
    		String appLine = (String)map.get("appLine");
    		String agrLine = (String)map.get("agrLine");
    		String tempLine = (String)map.get("tempLine");
    		String workItemOid = (String)map.get("workItemOid");
    		
    		WorkItem workItem = (WorkItem)CommonUtil.getObject(workItemOid);
    		WTObject obj = (WTObject)workItem.getPrimaryBusinessObject().getObject();
    		WFItem wfItem =  WFItemHelper.service.getWFItem(obj);
    		
    		if(wfItem != null){
    			
    			Vector<WFItemUserLink> vec = getAppline(wfItem, false, "", ""); //결재라인 사용중인 link
    			for(int i = 0 ; i < vec.size() ; i++ ){
    				WFItemUserLink link = vec.get(i);
    				if(link.getActivityName().equals(activity)) continue;
    				
    				PersistenceHelper.manager.delete(link);
    			}
    		}else{
    			wfItem = WFItemHelper.service.newWFItem(obj, workItem.getOwnership().getOwner().getPrincipal());
    		}
    		
    		int j=WFItemHelper.service.getMaxOrderNumber(wfItem, "") + 1;
    		int seq = WFItemHelper.service.getMaxSeq(wfItem) + 1;
    	
    		//합의라인
    		if(agrLine.length()>0){
    			String[] userOid = agrLine.split(",");
    			for(int i=0; i<userOid.length; i++){
    				WTUser wtuser = (WTUser)CommonUtil.getObject(userOid[i]);
    				//System.out.println("[create " + "합의" + " Link Line] "+"["+j+"] :" +userOid[i] );
    				//WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "합의", j, seq);
    				WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "합의", j, seq);
    			}
    			j++;
    		}
    		
    		//결재라인
    		if(appLine.length()>0){
    			String[] userOid2 = appLine.split(",");
    			for(int i=0; i<userOid2.length; i++){
    				WTUser wtuser = (WTUser)CommonUtil.getObject(userOid2[i]);
    				//System.out.println("[create " + "결재" + " Link Line] "+"["+j+"] : "+userOid2[i] );
    				//WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "결재", j++, seq);
    				WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "결재", j++, seq);
    			}
    		}
    		
    		//수신라인
    		if(tempLine.length()>0){
    			String[] tempOid = tempLine.split(",");
    			for(int i=0; i<tempOid.length; i++){
    				WTUser wtuser = (WTUser)CommonUtil.getObject(tempOid[i]);
    				//System.out.println("[create " + "수신" + " Link Line] "+"["+j+"] :" +tempOid[i] );
    				WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "수신", j++, seq);
//    				WFItemHelper.service.newWFItemUserLink(wtuser, wfItem, "수신", j++, seq);
    				
    			}
    		}
    		
    		result = true;
    		trx.commit();
            trx = null;
    	}catch(Exception e){
    		result = false;
    		trx.rollback();
            e.printStackTrace();
          
    	}finally{
            if (trx != null)
                trx.rollback();
        }
    	//System.out.println(":::::::::::: createAppLine END ::::::::::::::");
		return result;
    }
    
    /**
     * 결재선 저장
     * @param title
     * @param preDiscussUser
     * @param discussUser
     * @param postDiscussUser
     * @param tempUser
     * @throws Exception
     */
    @Override
    public void createApprovalTemplate(String title, String[] preDiscussUser, String[] discussUser, String[] postDiscussUser,
            String[] tempUser) throws Exception{

        Transaction trx = new Transaction();
        try{
            trx.start();

            ReferenceFactory rf = new ReferenceFactory();

            ApprovalLineTemplate template = ApprovalLineTemplate.newApprovalLineTemplate();
            template.setTitle(title);

            WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
            template.setOwner(user.getName());

            ArrayList approveList = new ArrayList();

            for(int i=0; preDiscussUser!=null && i< preDiscussUser.length; i++){
                approveList.add(preDiscussUser[i]);
            }
            template.setPreDiscussList(approveList);
            approveList = new ArrayList();

            for(int i=0; discussUser!=null && i< discussUser.length; i++){
                approveList.add(discussUser[i]);
            }
            template.setDiscussList(approveList);
            approveList = new ArrayList();

            for(int i=0; postDiscussUser!=null && i< postDiscussUser.length; i++){
                approveList.add(postDiscussUser[i]);
            }
            template.setPostDiscussList(approveList);
            approveList = new ArrayList();

            for(int i=0;tempUser!=null &&  i< tempUser.length; i++){
                approveList.add(tempUser[i]);
            }
            template.setTempList(approveList);
            PersistenceHelper.manager.save(template);

            trx.commit();
            trx = null;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new WTException(ex);
        }finally{
            if (trx != null)
                trx.rollback();
        }
    }
    
    @Override
    public List<Map<String,String>> loadLineAction() throws Exception {
    	
    	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    	
    	QuerySpec qs = new QuerySpec(ApprovalLineTemplate.class);
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
		qs.appendWhere(new SearchCondition(ApprovalLineTemplate.class,"owner","=", user.getName()),new int[]{0});

		QueryResult qr = PersistenceHelper.manager.find(qs);
		 
		String title = "";
		String oid = "";
		ApprovalLineTemplate template = null;
		
		if( qr != null ){ 
			while(qr.hasMoreElements()){
				template = (ApprovalLineTemplate)qr.nextElement();
				title = template.getTitle();
				oid = StringUtil.checkNull( PersistenceHelper.getObjectIdentifier ( template ).getStringValue());
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("title", title);
				map.put("oid" , oid);
				list.add(map);
				
			}
		}
		return list;
    }
    
    /**
     * 결재선 삭제
     * @param oid
     * @throws Exception
     */
    @Override
    public void deleteApprovalTemplate(String oid) throws Exception{

        Transaction trx = new Transaction();
        try{
            trx.start();
            ReferenceFactory rf = new ReferenceFactory();
            ApprovalLineTemplate template = (ApprovalLineTemplate)CommonUtil.getObject(oid);
            PersistenceHelper.manager.delete(template);

            trx.commit();
            trx = null;
        }catch(Exception ex){
        	trx.rollback();
            ex.printStackTrace();
            throw new WTException(ex);
        }finally{
            if (trx != null)
                trx.rollback();
        }
    }
	
    /**
     * 검토자,최종 승인자 마지막 결재자 2명
     * @param obj
     * @return
     */
    @Override
    public Vector<WTUser> getApprover(WTObject obj) throws Exception {
    	Vector<WTUser> vecUser = new Vector<WTUser>();
    	Vector<WFItemUserLink> vec = getApproverLink(obj);
    	
    	for(WFItemUserLink link : vec){
    		vecUser.add(link.getUser());
    	}
    	
    	return  vecUser;
    }
    
    
    @Override
    public Vector<WFItemUserLink> getApproverLink(WTObject obj) throws Exception{
    	
    	Vector<WFItemUserLink> vec = new Vector<WFItemUserLink>();
    	
		Config conf = ConfigImpl.getInstance();
        String activity = "결재";
		if(obj == null) return null;
 		WFItem wfItem =  WFItemHelper.service.getWFItem(obj);
 		
 		if(wfItem == null) return null;
 		Long wfItemOid = CommonUtil.getOIDLongValue(wfItem);
		QuerySpec qs = new QuerySpec(WFItemUserLink.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc = new SearchCondition(WFItemUserLink.class, "roleBObjectRef.key.id", "=", wfItemOid);
 		qs.appendWhere(sc, new int[]{0});
 		
 		qs.appendAnd();
 		sc = new SearchCondition(WFItemUserLink.class, "activityName", "=", activity);
 		qs.appendWhere(sc, new int[]{0});
 		
 		//qs.appendAnd();
 		//qs.appendRowNumCondition(3);
 		
 		//2016.02.01 최종 결재자/최종 앞 결재자에서 최초결재자/2번결재자로 변경. order by 만 true에서 false로...
 		qs.appendOrderBy(new OrderBy(new ClassAttribute(WFItemUserLink.class,"processOrder"), true), new int[] { 0 });//기존소스주석
 		//qs.appendOrderBy(new OrderBy(new ClassAttribute(WFItemUserLink.class,"processOrder"), false), new int[] { 0 });
 		
 		
 		QueryResult rt = PersistenceHelper.manager.find(qs);
 		
 		//System.out.println(":::::: getLastApprover ::::::");
 		//System.out.println(qs);
 		while(rt.hasMoreElements()){
 			WFItemUserLink wfLink = (WFItemUserLink)rt.nextElement();
 			//wfLink.getProcessDate()
 			//vec.add(wfLink.getUser());
 			vec.add(wfLink);
 		}
    	
    	return vec;
    	
    }
    
    
    /**
     * 반려시 재작업 또는 종료 및 ECA 시작
     * true 이면 재작업 ,false 이면 workflow 종료 ,ECA Start WB_ECO_Basic 표현식에 있음
     * @param wtobject
     * @return
     */
    @Override
    public  boolean setReworkEOCheck(WTObject ps){
    	
    	return ECOHelper.service.setReworkEOCheck(ps);
    }
}
