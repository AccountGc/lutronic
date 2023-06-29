package com.e3ps.listener;

import wt.events.KeyedEvent;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipalReference;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceEventListenerAdapter;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import com.e3ps.common.mail.MailUtil;
import com.e3ps.groupware.workprocess.service.WorklistHelper;

public class MailNotyListener extends ServiceEventListenerAdapter {
	

	public MailNotyListener(String s) {
		super(s);
	}

	 public void notifyVetoableEvent(Object obj) throws WTException
     {
         KeyedEvent keyEvent = (KeyedEvent) obj;
         Object eventObj = keyEvent.getEventTarget();
         
         if(!(eventObj instanceof WfAssignedActivity)){
        	 //System.out.println("event Obj is not WfAssignedActivity...");
        	 return;
         }
         
         WfAssignedActivity wfAssignedActivity = (WfAssignedActivity)eventObj;  
         
         if(wfAssignedActivity.getState() != WfState.OPEN_RUNNING){
        	 //System.out.println("WfAssignedActivity State Open Running...");
     		return;
     	 }             
                    
         QuerySpec spec = new QuerySpec(WorkItem.class);
         spec.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", "=",wfAssignedActivity.getPersistInfo().getObjectIdentifier().getId()));
         spec.appendAnd();
         spec.appendWhere(new SearchCondition(WorkItem.class, "status", "=", WfAssignmentState.POTENTIAL));
         QueryResult rs = PersistenceHelper.manager.find(spec);
         
         if(rs.size() == 0) {
        	 //System.out.println("mail send is null");
         }
         
         while(rs.hasMoreElements()){            	
         	
         	WorkItem witem = (WorkItem)rs.nextElement();
         	LifeCycleManaged pbo = (LifeCycleManaged)witem.getPrimaryBusinessObject().getObject();
         	WfActivity activity = (WfActivity)witem.getSource().getObject();
         	String[] processTarget = WorklistHelper.service.getWorkItemName((WTObject)pbo);
         	WTPrincipalReference wp = witem.getOwnership().getOwner();
         	//System.out.print("누구(to) : " + wp.getFullName() );  
         	//System.out.print("----업무명 : " + activity.getName());
         	//System.out.println("----업무대상 : " + processTarget[1] + " ("+processTarget[2]+")");            	

         	//2016.05.12 병렬 합의 변경 이후, 합의시마다 메일 발송 방지. start
         	boolean isLastApprover = true; 
         	
         	if("합의".equals(activity.getName())){
         		
         		WfAssignedActivity activity2 = (WfAssignedActivity)witem.getSource().getObject();
         		QuerySpec qs = new QuerySpec(WorkItem.class);
        		//int idx = qs.appendClassList(WorkItem.class, true);
        		
        		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", SearchCondition.EQUAL, activity2.getPersistInfo().getObjectIdentifier().getId() ));
        		QueryResult qr = PersistenceHelper.manager.find(qs);
        		
        		while(qr.hasMoreElements()){
        			WorkItem workitem = (WorkItem)qr.nextElement();
         			//System.out.println("MailNotyListener ::: WfAssignmentState.COMPLETED = " + WfAssignmentState.COMPLETED);
         			//System.out.println("MailNotyListener ::: workitem.getStatus() = " + workitem.getStatus());
         			
         			if(WfAssignmentState.COMPLETED.equals(workitem.getStatus())){
         				isLastApprover = false;
         			}
         		}
         	}
         	//2016.05.12 병렬 합의 변경 이후, 합의시마다 메일 발송 방지. end
         	
         	if(isLastApprover){
	         	try {
	         		boolean mmmm = MailUtil.manager.taskNoticeMail(witem);
	         		//System.out.println("mail end ....." + mmmm); 
	         	} catch (Exception e) {
	         		e.printStackTrace();
	         	}
         	}
         }            
     }
}