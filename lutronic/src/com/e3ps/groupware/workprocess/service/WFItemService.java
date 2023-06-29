package com.e3ps.groupware.workprocess.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

@RemoteInterface
public interface WFItemService {

	List<Map<String,String>> getLifeCycleState(String lifeCycle, String state) throws Exception;

	void deleteWFItem(Persistable persistable);

	WFItem getWFItem(WTObject wtobject);

	void setState(WTObject _obj);

	void eventListener(Object _obj, String _event);

	void setWFItemState(WTObject wtobject, String state);

	boolean isRemainProcessLine(WTObject wtobject);

	String getNextActivityName(WTObject wtobject);

	void setReworkAppLine(WTObject wtobject);

	int getMaxOrderNumber(WFItem item, String StateYn);

	int getMaxSeq(WFItem item) throws Exception;

	Vector<WFItemUserLink> getAppline(WFItem wfItem, boolean disabled, String state, String activityName) throws Exception;

	void newWFItemUserLink(WTUser user, WFItem item, String activity, int order);

	void newWFItemUserLink(WTUser user, WFItem item, String activity,
			int order, int seq) throws Exception;

	void reworkDataInit(WFItem wfItem);

	QuerySpec getLinkQuerySpec(WFItem wfItem, String order);

	Vector<WFItemUserLink> getTotalAppline(WFItem wfItem);

	String getWFItemActivityName(String activityName);

	WFItemUserLink getOwnerApplineLink(WTUser owner, WFItem wfItem, String activityName);

	void setWFItemUserLinkState(WorkItem workitem, String activityName, String comment, String state);

	void setWFItemUserLinkState(WorkItem workitem, String activityName, String comment);

	WFItemUserLink newWFItemUserLink(WTUser user, WFItem wfItem, String actName, String actYn);

	WFItem newWFItem(WTObject obj, WTPrincipal owner);

	void setRecipientLine(WTObject wtobject) throws Exception;

	boolean isRecipientLine(WTObject wtobject);

	ArrayList getProcessingUser(WFItem wfItem);

	boolean createAppLine(HashMap<String, String> map);

	void createApprovalTemplate(String title, String[] preDiscussUser,
			String[] discussUser, String[] postDiscussUser, String[] tempUser)
			throws Exception;

	List<Map<String, String>> loadLineAction() throws Exception;

	void deleteApprovalTemplate(String oid) throws Exception;

	WorkItem getWorkItem(Persistable per) throws WTException;

	Vector<WTUser> getApprover(WTObject obj) throws Exception;

	boolean setReworkEOCheck(WTObject ps);

	Vector<WFItemUserLink> getApproverLink(WTObject obj) throws Exception;

	void reworkDataInit(WFItem wfItem, boolean isInit);

}
