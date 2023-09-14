package com.e3ps.groupware.service;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.common.active.ActivityWork;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.DeleteWTObject;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.web.WebUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.Department;
import com.e3ps.org.MailUser;
import com.e3ps.org.MailWTobjectLink;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.org.service.UserHelper;
import com.e3ps.org.util.PasswordChange;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.OrganizationServicesServerHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.vc.Iterated;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

@SuppressWarnings("serial")
public class StandardGroupwareService extends StandardManager implements GroupwareService {

	public static StandardGroupwareService newStandardGroupwareService() throws Exception {
		final StandardGroupwareService instance = new StandardGroupwareService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> listNoticeAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String nameValue = request.getParameter("name");
		String creator = request.getParameter("creator");

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 10);
		String sessionId = request.getParameter("sessionId");

		PagingQueryResult qr = null;
		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(Notice.class, true);

			if (nameValue != null && nameValue.trim().length() > 0) {
				query.appendWhere(new SearchCondition(Notice.class, "title", SearchCondition.LIKE,
						"%" + nameValue.trim() + "%", false), new int[] { idx });
			}

			if (creator != null && creator.length() > 0) {
				ReferenceFactory rf = new ReferenceFactory();
				People people = (People) rf.getReference(creator).getObject();
				WTUser user = people.getUser();

				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(Notice.class, "owner.key", "=",
						PersistenceHelper.getObjectIdentifier(user)), new int[] { idx });
			}

			query.appendOrderBy(new OrderBy(new ClassAttribute(Notice.class, "thePersistInfo.createStamp"), true),
					new int[] { idx });

			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			Notice notice = (Notice) o[0];
			NoticeData data = new NoticeData(notice);

			xmlBuf.append("<row id='" + data.oid + "'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append(
					"<cell><![CDATA[<a href=javascript:gotoView('" + data.oid + "')>" + data.title + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.createDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.count + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.isPopup + "]]></cell>");
			xmlBuf.append("</row>");
		}
		xmlBuf.append("</rows>");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;
	}

	@Override
	public Map<String, Object> listWorkItemAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 10);
		boolean isDistribute = StringUtil.checkNull(request.getParameter("distribute")).equals("true");

		// System.out.println("request.getParameter(\"page\") = " +
		// request.getParameter("page"));
		// System.out.println("StanardGroupwareService ::: page = "+ page);
		// System.out.println("StanardGroupwareService ::: rows = "+ rows);
		// System.out.println("StanardGroupwareService ::: formPage = "+ formPage);

		boolean isAdmin = CommonUtil.isAdmin();

		String sessionId = request.getParameter("sessionId");

		PagingQueryResult qr = null;
		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {

			String userId = StringUtil.checkNull(request.getParameter("userOid"));
			String fullname = StringUtil.checkNull(request.getParameter("tempnewUser"));

			QuerySpec query = new QuerySpec();
			Class target = WorkItem.class;
			int idx = query.addClassList(target, false);

			query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.classname"),
					new int[] { idx }, false);
			query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx },
					false);
			query.appendSelect(new ClassAttribute(target, "primaryBusinessObject.key.classname"), new int[] { idx },
					false);
			query.appendSelect(new ClassAttribute(target, "source.key.classname"), new int[] { idx }, false);
			query.appendSelect(new ClassAttribute(target, "source.key.id"), new int[] { idx }, false);
			query.appendSelect(new ClassAttribute(target, "thePersistInfo.createStamp"), new int[] { idx }, false);
			query.appendSelect(new ClassAttribute(target, "taskURLPathInfo"), new int[] { idx }, false);

			query.appendOrderBy(new OrderBy(new ClassAttribute(target, "thePersistInfo.createStamp"), true),
					new int[] { idx });

			if (!"".equals(userId)) {
				People people = (People) CommonUtil.getObject(userId);
				fullname = people.getUser().getFullName();

				SearchCondition where = OwnershipHelper.getSearchCondition(target, (WTUser) people.getUser(), true);
				query.appendWhere(where, new int[] { idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(target, "status", "=", "POTENTIAL"), new int[] { idx });
			} else {
				if (!isAdmin) {
					SearchCondition where = OwnershipHelper.getSearchCondition(target,
							SessionHelper.manager.getPrincipal(), true);
					query.appendWhere(where, new int[] { idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(target, "status", "=", "POTENTIAL"), new int[] { idx });
				} else {
					query.appendWhere(new SearchCondition(target, "status", "=", "POTENTIAL"), new int[] { idx });
				}
			}
			// System.out.println("query =" + query);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");
		Object obj[] = null;

		ReferenceFactory rf = new ReferenceFactory();
		LifeCycleManaged pbo = null;
		String viewOid = "";
		String oid = "";

		String[] objName = null;
		String order = request.getParameter("order");

		// System.out.println("StandardGroupwareService ::: qr.size() = " + qr.size());

		// 2016.03.07 row id 가 중복일 경우 오류방지를 위해 index 추가
		int index = 0;

		while (qr.hasMoreElements()) {

			index++;

			obj = (Object[]) qr.nextElement();
			oid = obj[0] + ":" + obj[1];

			WorkItem item = (WorkItem) rf.getReference(oid).getObject();
			WfActivity activity = (WfActivity) item.getSource().getObject();
			try {
				pbo = (LifeCycleManaged) item.getPrimaryBusinessObject().getObject();
				// System.out.println("StandardGroupwareService ::: pbo = " + pbo);
			} catch (Exception e) {
				// System.out.println("StandardGroupwareService pbo is null");
				PersistenceHelper.manager.delete(item);
				continue;
			}

			viewOid = pbo.getPersistInfo().getObjectIdentifier().toString();

			if (pbo instanceof EChangeActivity) {
				EChangeActivity eca = (EChangeActivity) pbo;
				ECOChange eco = (ECOChange) eca.getEo();
				viewOid = CommonUtil.getOIDString(eco);
			}

			WTObject wobj = (WTObject) item.getPrimaryBusinessObject().getObject();
			try {
				if (WorklistHelper.service.getWorkItemName(wobj) != null) {
					objName = WorklistHelper.service.getWorkItemName(wobj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// model.addObject("fullName"
			// ,((WTUser)OwnershipHelper.getOwner(item)).getFullName());

			String objName0 = "객체 구분 없음";
			String objName1 = "객체 번호 없음";
			String objName2 = "객체 이름 없음";

			if (objName != null) {
				objName0 = objName[0];
				objName1 = objName[1];
				objName2 = objName[2];
			}

			Config conf = ConfigImpl.getInstance();
			String productName = conf.getString("product.context.name");
			String orgName = conf.getString("org.context.name");

			WTContainerRef containerRef = null;
			containerRef = WTContainerHelper.service.getByPath(
					"/wt.inf.container.OrgContainer=" + orgName + "/wt.pdmlink.PDMLinkProduct=" + productName);

			QueryResult result = WfEngineHelper.service.getAssociatedProcesses((Persistable) wobj, null, containerRef);

			/*
			 * if(result!=null){ System.out.println("StandardGroupwareService ::: result = "
			 * + result.size()); }else{
			 * System.out.println("StandardGroupwareService ::: result is null"); }
			 */

			String wfProcessOid = "";
			WfProcess wfprocess = null;

			if (result.hasMoreElements()) {
				wfprocess = (WfProcess) result.nextElement();

				/*
				 * wfProcessOid =
				 * wfprocess.getPersistInfo().getObjectIdentifier().getStringValue(); String
				 * processState = wfprocess.getState().getFullDisplay();
				 * System.out.println("processState="+processState);
				 * if("종료됨".equals(processState)) continue;
				 */
			}
			xmlBuf.append("<row id='" + index + "_" + wfProcessOid + "'>");
			if (!isDistribute) {
				xmlBuf.append("<cell><![CDATA[]]></cell>");
			}
//			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + objName0 + "]]></cell>");
			// xmlBuf.append("<cell><![CDATA[<a href=javascript:gotoView('" +
			// item.getTaskURLPathInfo() + "&order=" + order + "');>" + activity.getName() +
			// "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:gotoView('" + item.getTaskURLPathInfo() + "','" + oid
					+ "','" + viewOid + "');>" + activity.getName() + "</a>]]></cell>");
			if (isDistribute) {
				xmlBuf.append("<cell><![CDATA[<a href=javascript:openDistributeView('" + viewOid + "');>" + objName1
						+ "[" + objName2 + "]" + "</a>]]></cell>");
			} else {
				xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + viewOid + "');>" + objName1 + "["
						+ objName2 + "]" + "</a>]]></cell>");
			}

			if ("수신".equals(activity.getName())) {
				String creatorName = WorklistHelper.service.getCreatorName(wobj);
				xmlBuf.append("<cell><![CDATA[" + creatorName + "]]></cell>");
			} else {
				xmlBuf.append(
						"<cell><![CDATA[" + activity.getParentProcess().getCreator().getFullName() + "]]></cell>");
			}

			if (wfProcessOid == null || wfProcessOid.equals("") || wfProcessOid == "") {
				xmlBuf.append(
						"<cell><![CDATA[" + pbo.getLifeCycleState().getDisplay(Message.getLocale()) + "]]></cell>");
			} else {
				if (isAdmin) {
					xmlBuf.append(
							"<cell><![CDATA[<a href='javascript:void(0);' onclick=javascript:processHistory('/Windchill/ptc1/process/info?oid="
									+ wfProcessOid + "');>" + pbo.getLifeCycleState().getDisplay(Message.getLocale())
									+ "</a>]]></cell>");
				} else {
					xmlBuf.append(
							"<cell><![CDATA[" + pbo.getLifeCycleState().getDisplay(Message.getLocale()) + "]]></cell>");
				}
			}
			xmlBuf.append("<cell><![CDATA[" + DateUtil.getDateString(item.getPersistInfo().getCreateStamp(), "d")
					+ "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ((WTUser) OwnershipHelper.getOwner(item)).getFullName() + "]]></cell>");
			if ("수신".equals(activity.getName())) {
				xmlBuf.append("<cell><![CDATA[" + true + "]]></cell>");
			} else {
				xmlBuf.append("<cell><![CDATA[]]></cell>");
			}
			xmlBuf.append("<cell><![CDATA[" + oid + "]]></cell>");

			xmlBuf.append("</row>");
		}

		xmlBuf.append("</rows>");

		// System.out.println("StandardGroupwareService ::: xmlBuf start");
		// System.out.println(xmlBuf);
		// System.out.println("StandardGroupwareService ::: xmlBuf end");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;
	}

	@Override
	public ModelAndView processInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String workItemOid = request.getParameter("workitemoid");
		String wfItemOid = request.getParameter("wfitemoid");

		// taskDescription
		String taskDescription = StringUtil.checkNull(request.getParameter("taskDescription"));
		WFItem item = null;
		WorkItem workitem = null;
		WfActivity activity = null;
		Persistable per = null;
		String pboOid = null;
		WTObject obj = null;

		if (workItemOid != null && workItemOid.length() > 0) {
			workitem = (WorkItem) CommonUtil.getObject(workItemOid);
			activity = (WfActivity) workitem.getSource().getObject();
			per = WorklistHelper.service.getPBO(workitem);
			pboOid = CommonUtil.getOIDString(per);
		} else if (wfItemOid != null && wfItemOid.length() > 0) {
			item = (WFItem) CommonUtil.getObject(wfItemOid);
			per = item.getWfObject();
			pboOid = CommonUtil.getOIDString(per);
		} else {
			pboOid = CommonUtil.getOIDString(per);
		}

		String[] processTarget = WorklistHelper.service.getWorkItemName((WTObject) per);

		String workname = activity != null ? activity.getName() : "";
		if ("재작업".equals(workname)) {
			// 재작업시는 객체에 대한 링크 정보를 체크아웃된 객체 OID 가져온다
			if (per instanceof Workable) {
				Workable workable = ((Workable) per);
				if (WorkInProgressHelper.isCheckedOut(workable) && !WorkInProgressHelper.isWorkingCopy(workable)) {
					workable = WorkInProgressHelper.service.workingCopyOf(workable);
					pboOid = CommonUtil.getOIDString(workable);
				}
			}
		}

		String infoViewOid = per.getPersistInfo().getObjectIdentifier().toString();

		EChangeActivity eca = null;
		if (per instanceof EChangeActivity) {
			eca = (EChangeActivity) per;
			infoViewOid = CommonUtil.getOIDString(eca.getEo());
		}
		ActivityWork aw = null;
		if (per instanceof ActivityWork) {
			aw = (ActivityWork) per;
			Persistable ps = (Persistable) aw.getWork();
			infoViewOid = CommonUtil.getOIDString(ps);
		}

		if (item != null) {
			try {
				obj = item.getWfObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ModelAndView model = new ModelAndView();
		model.addObject("infoViewOid", infoViewOid);
		model.addObject("processTarget", processTarget);
		model.addObject("workname", workname);

		if (workitem != null) {
			model.addObject("fullName", ((WTUser) OwnershipHelper.getOwner(workitem)).getFullName());
			model.addObject("createStamp", DateUtil.getDateString(workitem.getPersistInfo().getCreateStamp(), "a"));
		} else {
			model.addObject("fullName", ((WTUser) OwnershipHelper.getOwner(item)).getFullName());
			model.addObject("createStamp", DateUtil.getDateString(item.getPersistInfo().getCreateStamp(), "a"));
		}

		boolean isDelay = false;
		if (activity != null) {
			java.sql.Timestamp deadline = activity.getDeadline();
			if (deadline != null) {
				if (deadline.after(new java.util.Date())) {
					model.addObject("deadline",
							"<b><font color='green'>" + ("" + deadline).substring(0, 10) + "</font></b>");
				} else {
					isDelay = true;
					model.addObject("deadline",
							"<b><font color='red'>" + ("" + deadline).substring(0, 10) + "</font></b>");
				}
			}
		}
		model.addObject("isDelay", isDelay);

		Team team = TeamHelper.service.getTeam((TeamManaged) per);
		Role role = null;

		Vector vecRole = team.getRoles();
		String rString = "";
		for (int i = 0; i < vecRole.size(); i++) {
			role = (Role) vecRole.get(i);
			rString += role.getDisplay(Message.getLocale()) + " : ";
			Enumeration r = team.getPrincipalTarget(role);
			while (r.hasMoreElements()) {
				WTPrincipalReference ref = (WTPrincipalReference) r.nextElement();
				// out.println(ref.getPrincipal().getName());
				rString += ref.getPrincipal().getName();
				if (ref.getPrincipal() instanceof WTUser) {
					// out.println("["+((WTUser)ref.getPrincipal()).getFullName()+ "]");
					rString += "[" + ((WTUser) ref.getPrincipal()).getFullName() + "]";
				}
				// out.println(",");
				rString += ",";
			}
			rString += "<br>";
			// out.println("<br>");
		}

		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("role", rString);

		QueryResult result = wt.workflow.engine.WfEngineHelper.service.getAssociatedProcesses(per, null,
				WCUtil.getWTContainerRef());

		String wfProcessOid = "";
		WfProcess wfprocess = null;
		WfState wfState = null;

		List<Map<String, String>> plist = new ArrayList<Map<String, String>>();

		while (result.hasMoreElements()) {
			wfprocess = (WfProcess) result.nextElement();
			wfProcessOid = wfprocess.getPersistInfo().getObjectIdentifier().getStringValue();
			wfState = wfprocess.getState();

			Map<String, String> map = new HashMap<String, String>();
			map.put("wfProcessOid", wfProcessOid);
			map.put("wfProcessName", wfprocess.getName());
			map.put("wfProcessState", wfState.getDisplay(Message.getLocale()));
			map.put("wfProcessCreate", wfprocess.getCreateTimestamp().toLocaleString());

			plist.add(map);
		}

		model.addObject("plist", plist);

		return model;

	}

	@Override
	public ModelAndView approval(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		String action = request.getParameter("action");
		// System.out.println("action====="+action);
		model.setViewName("default:/workprocess/" + action);

		/** 객체 정보 설정 *******/
		String oid = request.getParameter("oid");
		String pboOid = request.getParameter("pboOid");
		model.addObject("oid", oid);
		model.addObject("pboOid", pboOid);
		// System.out.println("++ oid :: " + oid);
		// System.out.println("++ pboOid :: " + pboOid);
		String workoid = request.getParameter("workoid");
		String wfitemoid = request.getParameter("wfitemoid");
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

		// System.out.println("++++++++++++++++++++++++++++++++++++++");
		// System.out.println("++ action :: " + action);
		// System.out.println("++ wrkoid :: " + workoid);

		WFItem wfItem = null;
		WorkItem workItem = null;

		if (workoid != null && workoid.length() > 0) {
			workItem = (WorkItem) CommonUtil.getObject(workoid);
			WTObject obj = (WTObject) workItem.getPrimaryBusinessObject().getObject();
			wfItem = WFItemHelper.service.getWFItem(obj);

		} else {
			if (oid != null && oid.length() > 0) {
				Persistable ps = (Persistable) CommonUtil.getObject(oid);
				QueryResult qr = WorkflowHelper.service.getWorkItems(ps);
				if (qr.hasMoreElements()) {
					workItem = (WorkItem) qr.nextElement();
					workoid = workItem.getPersistInfo().getObjectIdentifier().toString();
				}
			}
		}

		if (wfitemoid != null && wfitemoid.length() > 0) {
			wfItem = (WFItem) CommonUtil.getObject(wfitemoid);
		}

		if (wfItem != null) {
			// WFItemHelper.service.reworkDataInit(wfItem);
		}

		model.addObject("workitemoid", workoid);

		/** WorkItem 상태 설정 **/
		WfActivity activity = (WfActivity) workItem.getSource().getObject();
		String actname = activity.getName(); // 결재 & 반려 확인
		model.addObject("actname", actname);

		/** 결재선 리스트 설정 **/
		boolean isObjection = false;
		if (wfItem != null) {
			List<Map<String, Object>> appline = new ArrayList<Map<String, Object>>();
			Vector<WFItemUserLink> vec = WFItemHelper.service.getAppline(wfItem, false, "", "");
			// for(int i = 0 ; i < vec.size() ; i++){
			// WFItemUserLink wfLink = vec.get(i);
			for (WFItemUserLink wfLink : vec) {
				String state = StringUtil.checkNull(wfLink.getState());
				if ("".equals(state)) {
					state = "미결";
					if ("수신".equals(wfLink.getActivityName())) {
						state = "미수신";
					}
				}
				if (wfLink.getProcessOrder() == 0)
					state = "기안";

				Map<String, Object> map = new HashMap<String, Object>();
				if (state.startsWith("이의")) {
					isObjection = true;
				}
				map.put("ProcessOrder", wfLink.getProcessOrder());
				map.put("ActivityName", wfLink.getActivityName());
				map.put("DepartmentName", wfLink.getDepartmentName());
				map.put("FullName", wfLink.getUser().getFullName());
				map.put("ID", wfLink.getUser().getName());
				map.put("State", state);
				map.put("ProcessDate", wfLink.getProcessDate() != null ? wfLink.getProcessDate() : "");
				map.put("Comment", wfLink.getComment() != null ? wfLink.getComment() : "");
				appline.add(map);
			}
			model.addObject("appline", appline);
			model.addObject("isObjection", isObjection);
		}

		/** 기안자 정보 설정 **/
		ReferenceFactory rf = new ReferenceFactory();
		String userOid = (String) workItem.getOwnership().getOwner().getObjectId().toString();
		WTUser us = (WTUser) rf.getReference(userOid).getObject();
		PeopleData pd = new PeopleData(us);
		Map<String, String> map = new HashMap<String, String>();
		map.put("deptName", pd.departmentName);
		map.put("name", workItem.getOwnership().getOwner().getFullName());
		map.put("id", workItem.getOwnership().getOwner().getName());
		map.put("duty", pd.duty);

		model.addAllObjects(map);

		/**
		 * 
		 * 결재 객체에 따른 분기
		 * 
		 */

		Persistable persistable = (Persistable) WorklistHelper.service.getPBO(workItem);
		EChangeActivity eca = null;

		/////////////////////////////////////////// EChangeActivity

		if (persistable instanceof EChangeActivity) {
			eca = (EChangeActivity) persistable;

			Map<String, Object> EChangeActivity = new HashMap<String, Object>();
			String activeCode = "";// eca.getActiveCode();
			String activeType = eca.getActiveType();
			// boolean isDocument = eca.getDefinition().isIsDocument(); //문서 등록 필수 여부 체크
			// boolean isDocState = eca.getDefinition().isIsDocState(); //문서 승인 여부 체크

			EChangeActivity.put("activeCode", activeCode);
			EChangeActivity.put("activeType", activeType);
			// EChangeActivity.put("isDocument", isDocument);
			// EChangeActivity.put("isDocState", isDocState);
			EChangeActivity.put("ecaOid", eca.getPersistInfo().getObjectIdentifier().toString());

			if (eca.getEo() != null) {
				EChangeActivity.put("eoOid", eca.getEo().getPersistInfo().getObjectIdentifier().toString());
			}

			model.addObject("EChangeActivity", EChangeActivity);

		}

		return model;
	}

	@Override
	public List<Map<String, Object>> getApprovalList(String oid) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		WTObject obj = (WTObject) CommonUtil.getObject(oid);

		// 일괄 결재
		if (obj instanceof WTDocument) {

			AsmApproval asm = AsmSearchHelper.service.getAsmApproval((WTDocument) obj);
			if (asm != null) {
				obj = asm;
			}
		}

		WFItem wfItem = WFItemHelper.service.getWFItem(obj);
		if (wfItem != null) {
			Vector<WFItemUserLink> vec = WFItemHelper.service.getTotalAppline(wfItem);

			for (WFItemUserLink wfLink : vec) {

				String state = StringUtil.checkNull(wfLink.getState());
				// System.out.println("state="+state);
				if ("".equals(state)) {
					state = "미결";
					if ("수신".equals(wfLink.getActivityName())) {
						state = "미수신";
					}
				}

				if (wfLink.getProcessOrder() == 0) {
					state = "기안";
				}

				// System.out.println("zzzz yyyyyy : " + wfLink.getActivityName());

				Map<String, Object> map = new HashMap<String, Object>();
				boolean isCommentEdit = false;
				boolean isAuthComment = false;
				map.put("historyLinkOid", CommonUtil.getOIDString(wfLink));
				map.put("processOrder", wfLink.getProcessOrder());
				map.put("activityName", wfLink.getActivityName());
				map.put("deptName", wfLink.getDepartmentName());
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				String sessionUserName = user.getFullName();
				isAuthComment = CommonUtil.isAdmin() /* || (sessionUserName.equals(wfLink.getUser().getFullName())) */;
				map.put("userName", wfLink.getUser().getFullName());

				if ("미결".equals(state) || "미수신".equals(state)) {
					map.put("processDate", "");
				} else {
					// System.out.println("state="+state);
					map.put("processDate", wfLink.getProcessDate() == null ? "" : wfLink.getProcessDate());
					if ("승인(결재)".equals(state) || "기안".equals(state)) {
						map.put("approveDate", wfLink.getProcessDate() == null ? "" : wfLink.getProcessDate());
					}
					isCommentEdit = true;
				}

				map.put("state", state);
				map.put("comment", wfLink.getComment() == null ? "" : wfLink.getComment());
				map.put("isCommentEdit", isCommentEdit);
				map.put("isAuthComment", isAuthComment);
				// System.out.println(wfLink.getUser().getFullName()+"\t"+state+"\tlink Oid =
				// "+CommonUtil.getOIDString(wfLink)+
				// "\tisCommentEdit="+isCommentEdit+"\tisAuthComment="+isAuthComment);
				list.add(map);
			}
		}

		return list;
	}

	@Override
	public List<Map<String, Object>> getMailList(String oid) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Vector<MailWTobjectLink> vecLink = MailUserHelper.service.getMailUserLinkList(oid);

		for (MailWTobjectLink link : vecLink) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("name", link.getUser().getName());
			map.put("email", link.getUser().getEmail());

			list.add(map);
		}

		return list;
	}

	@Override
	public void approveAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = StringUtil.checkNull(request.getParameter("workOid"));

		String WfUserEvent = StringUtil.checkNull(request.getParameter("WfUserEvent"));
		String cmd = StringUtil.checkNull(request.getParameter("cmd"));
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

		/* reassign */
		String newUser = StringUtil.checkNull(request.getParameter("newUser"));
		String assignrolename = StringUtil.checkNull(request.getParameter("assignrolename"));

		/* receive */
		String comment = StringUtil.checkNull(request.getParameter("comment"));
		String pboOid = null;
		FileRequest req = null;
		// System.out.println("1.[ActionApprove][cmd]:" + cmd);
		// System.out.println("================================");
		if (cmd.length() == 0) {
			req = new FileRequest(request);
			oid = StringUtil.checkNull(req.getParameter("workOid"));
			WfUserEvent = StringUtil.checkNull(req.getParameter("WfUserEvent"));
			cmd = StringUtil.checkNull(req.getParameter("cmd"));
			pboOid = StringUtil.checkNull(req.getParameter("pboOid"));
			// System.out.println("1.[ActionApprove][pboOid]:" + pboOid);
			newUser = StringUtil.checkNull(req.getParameter("newUser"));
			assignrolename = StringUtil.checkNull(req.getParameter("assignrolename"));
			comment = StringUtil.checkNull(req.getParameter("comment"));
		}

		// System.out.println("[ActionApprove][cmd]:" + cmd);
		// System.out.println("[ActionApprove][oid]:" + oid);
		// System.out.println("[ActionApprove][WfUserEvent]:" + WfUserEvent);
		// System.out.println("[ActionApprove][newUser]:" + newUser);
		// System.out.println("[ActionApprove][assignrolename]:" + assignrolename);
		// System.out.println("[ActionApprove][comment]:" + comment);
		if ("reassign".equals(cmd)) { // 위임

			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "START(위임)
			// ::::::::::::::::::::::::::::: ");
			Hashtable hash = new Hashtable();
			hash.put("oid", oid);
			hash.put("newUser", newUser);
			hash.put("assignrolename", assignrolename);
			E3PSWorkflowHelper.service.reassign(hash);
			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "END(위임)
			// ::::::::::::::::::::::::::::: ");

		} else if ("receive".equals(cmd)) { // 수신

			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "START(수신)
			// ::::::::::::::::::::::::::::: ");
			String status = WfUserEvent;
			try {
				WorkItem workItem = (WorkItem) CommonUtil.getObject(oid);
				WTPrincipalReference prinRef = WTPrincipalReference
						.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
				WFItemHelper.service.setWFItemUserLinkState(workItem, WfUserEvent, comment);
				workItem.setComplete(prinRef.getName());
				PersistenceHelper.manager.save(workItem);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "END(수신)
			// ::::::::::::::::::::::::::::: ");
		} else {

			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "START(....)
			// ::::::::::::::::::::::::::::: ");
			String status = WfUserEvent;
			if ("수신".equals(WfUserEvent)) {
				status = "확인";
			}

			WorkItem workItem = (WorkItem) CommonUtil.getObject(oid);

			try {
				WTObject wtobject = (WTObject) workItem.getPrimaryBusinessObject().getObject();
				WFItem wfItem = WFItemHelper.service.getWFItem(wtobject);
				Hashtable param = WebUtil.getHttpParams(request);

				// System.out.println(param);
				param.put("WfUserEvent", WfUserEvent);

				Vector vec = WorklistHelper.service.completeWfActivity(workItem, param);
				workItem = (WorkItem) PersistenceHelper.manager.refresh(workItem);
				WfActivity activity = (WfActivity) workItem.getSource().getObject();
				String activityName = activity.getName();

				boolean isECO = false;
				// System.out.println("[ActionApprove][wtobject instanceof EChangeActivity]:" +
				// (wtobject instanceof EChangeActivity));
				if (wtobject instanceof EChangeActivity) {

					EChangeActivity eca = (EChangeActivity) wtobject;

					eca.setComments(comment);
					PersistenceHelper.manager.modify(eca);

					isECO = true;

				} else {
					if (wfItem == null) {
						wfItem = WFItemHelper.service.newWFItem(wtobject,
								workItem.getOwnership().getOwner().getPrincipal());
					}
				}

				if (wfItem != null) {
					// System.out.println("[ActionApprove][wfItem]:" +
					// wfItem.getPersistInfo().getObjectIdentifier().toString());
				}

				// System.out.println("[ActionApprove][activityName]:" + activityName);

				if (StringUtil.checkString(status))
					status = status + "(" + activityName + ")";
				else {
					status = activityName;
				}
				if ((WfUserEvent == null) || (WfUserEvent.trim().length() == 0)) {
					WfUserEvent = status;
				}

				if ("결재취소".equals(WfUserEvent)) {

					// System.out.println("[ActionApprove][WfUserEvent]:" + WfUserEvent);

					WFItemUserLink newlink = WFItemHelper.service.newWFItemUserLink(user, wfItem, "기안", "");

					if (newlink != null) {

						newlink.setState(WfUserEvent);
						newlink.setApprover(user.getFullName());
						newlink.setComment(StringUtil.checkNull(comment) + "\n");
						newlink.setProcessDate(new java.sql.Timestamp(new java.util.Date().getTime()));
						PersistenceHelper.manager.modify(newlink);
					}

				} else {
					if (!isECO) {
						WFItemHelper.service.setWFItemUserLinkState(workItem, activityName, comment, status);
					}

				}

				WorklistHelper.service.completeWorkItem(workItem, vec);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("::::::::::::::::::::::::::::: " + cmd + "END(....)
			// ::::::::::::::::::::::::::::: ");
		}
	}

	private void ecoNoTaskAfter(String pboOid, String state, String terminate) throws Exception {
		// System.out.println("********** stateChange 시작1 **********");

		ReferenceFactory rf = new ReferenceFactory();

		// System.out.println("pboOid="+pboOid);
		if (null != pboOid && pboOid.indexOf("EChangeOrder") > -1) {
			Object object = null;
			if (pboOid.length() > 0) {
				try {
					object = rf.getReference(pboOid).getObject();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// System.out.println("********** stateChange 시작 **********");
			LifeCycleManaged lcm = (wt.lifecycle.LifeCycleManaged) object;
			// System.out.println("lcm != null="+(null != lcm));
			WFItem wItem = WFItemHelper.service.getWFItem((WTObject) lcm);
			boolean isUseChage = false;
			// System.out.println("********** 결재선 초기화 시작 **********");
			// System.out.println("wItem != null="+(null != wItem));
			if (wItem != null && state.equals("INWORK")) {
				wItem.setObjectState(state);
				wItem = (WFItem) PersistenceHelper.manager.modify(wItem);
				WFItemHelper.service.reworkDataInit(wItem);
			}
			// System.out.println("********** 결재선 초기화 끝 **********");
			if (wItem != null) {
				isUseChage = true;
				String id = wItem.getOwnership().getOwner().getName();
				SessionHelper.manager.setPrincipal(id);
			}
			lcm = LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state),
					"true".equals(terminate.toLowerCase()));
			if (isUseChage) {
				SessionHelper.manager.setAdministrator();
			}
		}
	}

	@Override
	public Map<String, Object> listItemAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 10);
		boolean isDistribute = StringUtil.checkNull(request.getParameter("distribute")).equals("true");
		// System.out.println("request.getParameter(\"page\") = " +
		// request.getParameter("page"));
		// System.out.println("StanardGroupwareService ::: page = "+ page);
		// System.out.println("StanardGroupwareService ::: rows = "+ rows);
		// System.out.println("StanardGroupwareService ::: formPage = "+ formPage);

		String sessionId = request.getParameter("sessionId");

		String state = request.getParameter("state");
		String className = request.getParameter("className");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {

			if (state == null)
				state = "own";

			long longOid = CommonUtil.getOIDLongValue((WTUser) SessionHelper.manager.getPrincipal());

			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(WFItem.class, true);
			int idx_Link = query.addClassList(WFItemUserLink.class, true);

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(new SearchCondition(WFItem.class, "thePersistInfo.theObjectIdentifier.id",
					WFItemUserLink.class, "roleBObjectRef.key.id"), new int[] { idx, idx_Link });

			if (!CommonUtil.isAdmin()) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "roleAObjectRef.key.id", "=", longOid),
						new int[] { idx_Link });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(SearchUtil.getSearchCondition(WFItemUserLink.class, "deleteFlag", "false"),
					new int[] { idx_Link });

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(SearchUtil.getSearchCondition(WFItemUserLink.class, WFItemUserLink.DISABLED,
					SearchCondition.IS_FALSE), new int[] { idx_Link });

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(new SearchCondition(WFItem.class, "wfObjectReference.key.classname",
					SearchCondition.NOT_EQUAL, "com.e3ps.change.EChangeActivity"), new int[] { idx });

			/*
			 * 3개월 이전 데이터 제외 if(query.getConditionCount()>0) query.appendAnd();
			 * query.appendWhere(new SearchCondition(WFItem.class,
			 * "thePersistInfo.updateStamp", SearchCondition.GREATER_THAN,
			 * DateUtil.getOneMonthBefore(90)), new int[] { idx });
			 */
			if ("receive".equals(state)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(
						new SearchCondition(WFItemUserLink.class, "activityName", SearchCondition.EQUAL, "수신"),
						new int[] { idx_Link });

				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", SearchCondition.EQUAL, "수신"),
						new int[] { idx_Link });

				if (className != null && className.length() > 0) {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendWhere(new SearchCondition(WFItem.class, "wfObjectReference.key.classname",
							SearchCondition.EQUAL, className), new int[] { idx });
				}

				SearchUtil.setOrderBy(query, WFItem.class, idx, "thePersistInfo.updateStamp", true);
			} else {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", SearchCondition.NOT_EQUAL, "위임"),
						new int[] { idx_Link });
				query.appendOr();
				query.appendWhere(new SearchCondition(WFItemUserLink.class, "state", true), new int[] { idx_Link });
				query.appendCloseParen();
				if ("ing".equals(state)) {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendOpenParen();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL,
							"COMPLETED"), new int[] { idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL,
							"APPROVED"), new int[] { idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL,
							"CANCELLED"), new int[] { idx });
					query.appendAnd();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "INWORK"),
							new int[] { idx });
					query.appendAnd();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "RETURN"),
							new int[] { idx });
					query.appendAnd();
					// 승인 요청 제외
					query.appendWhere(new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL,
							"APPROVE_REQUEST"), new int[] { idx });
					query.appendAnd();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.NOT_EQUAL, "REWORK"),
							new int[] { idx });

					query.appendCloseParen();

					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME,
							SearchCondition.NOT_EQUAL, "수신"), new int[] { idx_Link });
				} else if ("complete".equals(state)) {

					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendOpenParen();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "COMPLETED"),
							new int[] { idx });
					query.appendOr();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "APPROVED"),
							new int[] { idx });
					query.appendOr();
					query.appendWhere(
							new SearchCondition(WFItem.class, WFItem.OBJECT_STATE, SearchCondition.EQUAL, "CANCELLED"),
							new int[] { idx });
					query.appendCloseParen();

					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendWhere(new SearchCondition(WFItemUserLink.class, WFItemUserLink.ACTIVITY_NAME,
							SearchCondition.NOT_EQUAL, "수신"), new int[] { idx_Link });
				}

				if (className != null && className.length() > 0) {
					query.appendAnd();
					query.appendWhere(
							new SearchCondition(WFItem.class, "wfObjectReference.key.classname", "=", className),
							new int[] { idx });
				}

				SearchUtil.setOrderBy(query, WFItem.class, idx, "thePersistInfo.updateStamp", true);
			}
			// System.out.println("======================================================");
			// System.out.println(query);
			// System.out.println("======================================================");
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		// 2016.03.07 row id 가 중복일 경우 오류방지를 위해 index 추가
		int index = 0;

		while (qr.hasMoreElements()) {

			index++;

			Object o[] = (Object[]) qr.nextElement();
			WFItem wfItem = (WFItem) o[0];
			WFItemUserLink wfItemLink = (WFItemUserLink) o[1];
			WTObject obj = null;
			ArrayList users = null;
			try {
				obj = wfItem.getWfObject();
				if (obj == null) {
					PersistenceHelper.manager.delete(wfItem);
				}
			} catch (Exception e) {
				// System.out.println(" Error : 객체가 존재하지 않습니다.");
				PersistenceHelper.manager.delete(wfItem);
				continue;
			}
			String viewOid = CommonUtil.getOIDString(obj);
			if (obj instanceof Master) {
				obj = ObjectUtil.getVersionObject((wt.enterprise.Master) obj, wfItem.getObjectVersion());

				viewOid = CommonUtil.getOIDString(obj);
			}

			boolean isECO = false;
			Timestamp endTime = null;
			/*
			 * if(obj instanceof EChangeOrder){ EChangeOrder eco = (EChangeOrder)obj;
			 * if(eco.getLifeCycleState().toString().equals("BOM_CHANGE") ||
			 * eco.getLifeCycleState().toString().equals("VALIDITYCHECK")||
			 * eco.getLifeCycleState().toString().equals("RELATEDWORK")||
			 * eco.getLifeCycleState().toString().equals("RELATEDCONFIRM")){ users =
			 * ECAHelper.service.getWorkingECAUser(eco); isECO = true; }
			 * 
			 * EChangeActivity eca = ECAHelper.service.getLastStepECA(eco);
			 * 
			 * QueryResult result =
			 * E3PSWorkflowHelper.service.getWfProcess((LifeCycleManaged)eca);
			 * if(result.hasMoreElements()) { endTime =
			 * ((WfProcess)result.nextElement()).getEndTime(); } }else{
			 */
			Persistable per = null;
			State rState = null;
			if (wfItem.getObjectState() != null) {
				rState = State.toState(wfItem.getObjectState());
				per = wfItem.getWfObject();
			}
			if (rState == null) {
				rState = ((LifeCycleManaged) obj).getState().getState();
			}
			if (obj instanceof LifeCycleManaged) {
				QueryResult result = E3PSWorkflowHelper.service.getWfProcess((LifeCycleManaged) obj);
				// System.out.println("result.Size = "+result.size());
				if (result.size() > 1) {
					ArrayList<Date> list = new ArrayList<Date>();
					while (result.hasMoreElements()) {
						WfProcess wfprocess = (WfProcess) result.nextElement();
						Timestamp tp = wfprocess.getEndTime();
						String stateSTR = wfprocess.getState().toString();
						// System.out.println("stateSTR="+stateSTR);
						if (null != tp) {
							Date date = new Date(tp.getTime());
							// System.out.println(date.toString());
							if (!stateSTR.equals("OPEN_RUNNING"))
								list.add(date);

						}
					}

					Collections.sort(list, new Comparator<Date>() {
						@Override
						public int compare(Date object1, Date object2) {
							return (int) (object2.compareTo(object1));
						}
					});
					Date endDate = (Date) list.get(0);
					Timestamp data = new Timestamp(endDate.getTime());
					String statestr = rState.getDisplay(Locale.KOREA);
					if ("승인됨".equals(statestr)) {
						endTime = data;
					}
				} else {
					if (result.hasMoreElements()) {
						endTime = ((WfProcess) result.nextElement()).getEndTime();
					}
				}
			}
			// }

			if (!isECO) {
				users = WFItemHelper.service.getProcessingUser(wfItem);
			}

			String[] objName = null;

			try {
				if (WorklistHelper.service.getWorkItemName(obj) != null) {
					objName = WorklistHelper.service.getWorkItemName(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String objName0 = "객체 구분 없음";
			String objName1 = "객체 번호 없음";
			String objName2 = "객체 이름 없음";

			if (objName != null) {
				objName0 = objName[0];
				objName1 = objName[1];
				objName2 = objName[2];
			}

			xmlBuf.append("<row id='" + index + "_" + viewOid + "'>");
			xmlBuf.append("<cell><![CDATA[" + objName0 + "]]></cell>");
			if (isDistribute) {
				xmlBuf.append("<cell><![CDATA[<a href=JavaScript:openDistributeView('" + viewOid + "');>" + objName1
						+ "[" + objName2 + "]" + "</a>]]></cell>");
			} else {
				xmlBuf.append("<cell><![CDATA[<a href=JavaScript:openView('" + viewOid + "');>" + objName1 + "["
						+ objName2 + "]" + "</a>]]></cell>");
			}

			xmlBuf.append("<cell><![CDATA[" + DateUtil.getDateString(PersistenceHelper.getCreateStamp(wfItem), "d")
					+ "]]></cell>");
			String endTimeString = endTime == null ? "" : DateUtil.getDateString(endTime, "d");
			xmlBuf.append("<cell><![CDATA[" + endTimeString + "]]></cell>");

			WfProcess wfprocess = null;
			WfState wfState = null;
			String wfProcessOid = "";
			QueryResult wqr = wt.workflow.engine.WfEngineHelper.service.getAssociatedProcesses(per, null,
					WCUtil.getWTContainerRef());
			if (wqr.hasMoreElements()) {
				wfprocess = (WfProcess) wqr.nextElement();
				wfProcessOid = wfprocess.getPersistInfo().getObjectIdentifier().getStringValue();
				wfState = wfprocess.getState();
				if (CommonUtil.isAdmin()) {
					xmlBuf.append("<cell><![CDATA[<a href=javascript:processHistory('/Windchill/ptc1/process/info?oid="
							+ wfProcessOid + "');>" + rState.getDisplay(Message.getLocale()) + "</a>]]></cell>");
				} else {
					xmlBuf.append("<cell><![CDATA[" + rState.getDisplay(Message.getLocale()) + "]]></cell>");
				}
			} else {
				xmlBuf.append("<cell><![CDATA[" + rState.getDisplay(Message.getLocale()) + "]]></cell>");
			}

			String user = "";
			for (int i = 0; i < users.size(); i++) {
				user = (((WTUser) users.get(i)).getFullName() == null) ? "" : ((WTUser) users.get(i)).getFullName();
			}
			xmlBuf.append("<cell><![CDATA[" + user + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + wfItem.getOwnership().getOwner().getFullName() + "]]></cell>");
			xmlBuf.append("</row>");
		}

		xmlBuf.append("</rows>");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("formPage", formPage);
		map.put("rows", rows);
		map.put("totalPage", totalPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("listCount", listCount);
		map.put("totalCount", totalCount);
		map.put("currentPage", currentPage);
		map.put("param", param);
		map.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		map.put("xmlString", xmlBuf);

		return map;

	}

	@Override
	public List<Map<String, String>> loadApprovalLine(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String type = request.getParameter("type");

		String app = "수신";
		if ("app1".equals(type)) {
			app = "합의";
		} else if ("app2".equals(type)) {
			app = "결재";
		} else if ("app3".equals(type)) {
			app = "수신";
		}

		String workoid = request.getParameter("workoid");
		String wfitemoid = request.getParameter("wfitemoid");

		WFItem wfItem = null;
		WorkItem workItem = null;

		if (workoid != null && workoid.length() > 0) {
			workItem = (WorkItem) CommonUtil.getObject(workoid);
			WTObject obj = (WTObject) workItem.getPrimaryBusinessObject().getObject();
			wfItem = WFItemHelper.service.getWFItem(obj);
		}

		if (wfItem != null) {
			Vector<WFItemUserLink> vec = WFItemHelper.service.getAppline(wfItem, false, "", "");
			for (int i = 0; i < vec.size(); i++) {
				WFItemUserLink link = vec.get(i);
				if (link.getActivityName().equals("기안"))
					continue;
				WTUser linkUser = link.getUser();
				String processType = link.getActivityName();
				People people = UserHelper.service.getPeople(linkUser);
				String wtuserOid = linkUser.getPersistInfo().getObjectIdentifier().toString();
				Department dept = people.getDepartment();
				String deptOid = "";
				String deptName = "";
				String fullName = linkUser.getFullName();

				if (dept != null) {
					deptOid = dept.getPersistInfo().getObjectIdentifier().toString();
					deptName = dept.getName();
				}

				String duty = StringUtil.checkNull(people.getDuty());
				String dutycode = StringUtil.checkNull(people.getDutyCode());
				String email = StringUtil.checkNull(linkUser.getEMail());

				String userInfo = wtuserOid + "," + people.getPersistInfo().getObjectIdentifier().toString() + ","
						+ deptOid + "," + linkUser.getName() + "," + fullName + "," + deptName + "," + duty + ","
						+ dutycode + "," + email + "," + fullName;

				Map<String, String> map = new HashMap<String, String>();
				map.put("userOid", wtuserOid);
				map.put("name", fullName);
				map.put("deptName", deptName);
				map.put("id", linkUser.getName());
				map.put("duty", duty);

				if (app.equals(processType)) {
					list.add(map);
				} else if (app.equals(processType)) {
					list.add(map);
				} else if (app.equals(processType)) {
					list.add(map);
				}
			}
		}
		return list;
	}

	@Override
	public Map<String, String> includeReassing(String workItemOid) throws Exception {
		WorkItem workItem = (WorkItem) CommonUtil.getObject(workItemOid);
		WTObject obj = (WTObject) workItem.getPrimaryBusinessObject().getObject();
		WFItem wfItem = WFItemHelper.service.getWFItem(obj);
		String drafter = ""; // 기안자
		if (wfItem != null) {
			Vector<WFItemUserLink> vec = WFItemHelper.service.getAppline(wfItem, false, "", "");
			for (int i = 0; i < vec.size(); i++) {
				WFItemUserLink link = (WFItemUserLink) vec.get(i);
				if (link.getProcessOrder() == 0) {
					drafter = link.getApprover();
					WTUser user = (WTUser) link.getRoleAObject();
					PeopleData data = new PeopleData(user);
					drafter = data.peopleOID;
				}
			}
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("rolName", workItem.getRole().toString());
		map.put("workItemOid", workItemOid);
		map.put("drafter", drafter);
		return map;
	}

	@Override
	public void password(Map<String, String> params) throws Exception {
		String password = (String) params.get("password");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser sessionUser = CommonUtil.sessionUser();
			String id = sessionUser.getName();

			// 관리자 아이디 변경 Administrator -> wcadmin
			if ("Administrator".equals(id)) {
				id = "wcadmin";
			}

			PasswordChange.changePassword(id, password);

			QueryResult result = PersistenceHelper.manager.navigate(sessionUser, "people", WTUserPeopleLink.class);

			if (result.hasMoreElements()) {
				People people = (People) result.nextElement();
				Timestamp today = new Timestamp(new Date().getTime());
				people.setPwChangeDate(today);
				PersistenceHelper.manager.modify(people);
			}

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

	@Override
	public Map<String, Object> wfProcessInfoAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String oid = request.getParameter("oid");
		String command = request.getParameter("command");

		if (oid == null) {
			oid = "";
		}

		if (command == null) {
			command = "";
		}

		result.put("oid", oid);
		result.put("command", command);

		ReferenceFactory rf = new ReferenceFactory();

		Object object = null;
		if (oid.length() > 0) {
			try {
				object = rf.getReference(oid).getObject();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if ("".equals(command)) {

			if ((object == null)) {
				result.put("command", "error");
				result.put("message", Message.get("객체가 존재하지 않습니다."));
			}

			else if ((object != null) && !(object instanceof LifeCycleManaged)) {
				result.put("command", "error");
				result.put("message", Message.get("wt.lifecycle.LifeCycleManaged가 아닙니다."));
			} else {
				LifeCycleManaged lcm = (LifeCycleManaged) object;
				LifeCycleTemplate lct = (LifeCycleTemplate) lcm.getLifeCycleTemplate().getObject();

				QueryResult qr = WfEngineHelper.service.getAssociatedProcesses((Persistable) object, null,
						WCUtil.getWTContainerRef());

				String wfProcessOid = "";
				WfProcess wfprocess = null;
				WfState wfState = null;

				List<Map<String, String>> wfL = new ArrayList<Map<String, String>>();
				while (qr.hasMoreElements()) {
					wfprocess = (WfProcess) qr.nextElement();
					wfProcessOid = wfprocess.getPersistInfo().getObjectIdentifier().getStringValue();
					wfState = wfprocess.getState();

					Map<String, String> wfM = new HashMap<String, String>();
					wfM.put("wfOid", wfProcessOid);
					wfM.put("wfName", wfprocess.getName());
					wfM.put("wfState", wfState.getDisplay(Message.getLocale()));
					wfM.put("wfCreate", wfprocess.getCreateTimestamp().toLocaleString());
					wfL.add(wfM);
				}

				List<Map<String, String>> cL = new ArrayList<Map<String, String>>();
				if (object instanceof wt.content.ContentHolder) {
					ContentHolder holder = (ContentHolder) object;
					holder = wt.content.ContentHelper.service.getContents(holder);

					Vector cotents = wt.content.ContentHelper.getContentListAll(holder);
					if (cotents.size() > 0) {

						wt.content.ContentItem contentItem = null;
						for (int i = 0; i < cotents.size(); i++) {
							contentItem = (wt.content.ContentItem) cotents.get(i);
							if (contentItem instanceof wt.content.ApplicationData) {

								wt.content.ApplicationData appData = (ApplicationData) contentItem;

								URL downloadURL = ContentHelper.getDownloadURL(holder, appData);

								Map<String, String> cMap = new HashMap<String, String>();
								cMap.put("url", downloadURL.toString());
								cMap.put("name", appData.getFileName());

								cL.add(cMap);
							}
						}
					}
				}

				String targetOid = rf.getReferenceString(lcm);
				String targetLC = lct.getName();
				String targetState = lcm.getLifeCycleState().getDisplay(Message.getLocale());

				result.put("targetOid", targetOid);
				result.put("targetLC", targetLC);
				result.put("targetState", targetState);
				result.put("wf", wfL);
				result.put("content", cL);

			}

		} else {
			if ("delete".equals(command)) {
				String rtnMsg = DeleteWTObject.delete((Persistable) object);
				result.put("message", rtnMsg);
			} else if ("stateChange".equals(command)) {

				String state = request.getParameter("state");
				String terminate = request.getParameter("terminate");

				// System.out.println("********** stateChange 시작 **********");
				// System.out.println("terminate="+terminate);
				// System.out.println("state="+state);
				// System.out.println("oid="+oid);
				LifeCycleManaged lcm = (wt.lifecycle.LifeCycleManaged) object;
				// System.out.println("********** lcm **********" + lcm);
				WFItem wfItem = WFItemHelper.service.getWFItem((WTObject) lcm);

				boolean isUseChage = false;
				if (wfItem != null) {
					// System.out.println("********** safsafsa결재선 초기화 시작 **********");
					isUseChage = true;
					String id = wfItem.getOwnership().getOwner().getName();
					SessionHelper.manager.setPrincipal(id);
				}

				if ("INWORK".equals(state)) {
					// System.out.println("********** 결재선 초기화 시작 **********");

					if (wfItem != null) {
						wfItem.setObjectState(state);
						wfItem = (WFItem) PersistenceHelper.manager.modify(wfItem);
						WFItemHelper.service.reworkDataInit(wfItem);
					}
					// System.out.println("********** 결재선 초기화 끝 **********");

				}

				lcm = LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state),
						"true".equals(terminate.toLowerCase()));

				if (isUseChage) {
					SessionHelper.manager.setAdministrator();
				}

				result.put("targetState", State.toState(state).getDisplay(Message.getLocale()));
				result.put("message", Message.get("상태 완료."));

			} else if ("reassign".equals(command)) {
				String lifecycle = request.getParameter("lifecycle");

				LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle,
						WCUtil.getWTContainerRef());
				LifeCycleHelper.service.reassign((LifeCycleManaged) object, lct.getLifeCycleTemplateReference(),
						WCUtil.getWTContainerRef());

				result.put("targetLC", lifecycle);
				result.put("message", Message.get("Reassign 완료."));
			}
		}
		return result;
	}

	@Override
	public void modify(Map<String, String> params) throws Exception {
		String oid = params.get("oid");
		String attrName = params.get("attrName");
		String value = params.get("value");
		Transaction trs = new Transaction();
		try {
			trs.start();

			IBAHolder holder = (IBAHolder) CommonUtil.getObject(oid);

			String attrValue = value.split("&")[0];
			String type = value.split("&")[1];

			IBAUtil.changeIBAValue(holder, attrName, attrValue, type);

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

	@Override
	public List<Map<String, String>> include_mailUser(String workOid) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Vector<MailWTobjectLink> veclink = MailUserHelper.service.getMailUserLinkList(workOid);
		for (int i = 0; i < veclink.size(); i++) {
			MailWTobjectLink link = veclink.get(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("oid", link.getPersistInfo().getObjectIdentifier().toString());
			map.put("name", link.getUser().getName());
			map.put("email", link.getUser().getEmail());

			list.add(map);
		}
		return list;
	}

	@Override
	public String emailUserListAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap map = new HashMap();
		map.put("isDisable", "true");
		QuerySpec qs = MailUserHelper.service.getQuery(map);

		QueryResult qr = PersistenceHelper.manager.find(qs);

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		while (qr.hasMoreElements()) {
			MailUser user = (MailUser) qr.nextElement();

			xmlBuf.append("<row id='" + user.getPersistInfo().getObjectIdentifier().toString() + "'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + user.getName() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + user.getEmail() + "]]></cell>");
			xmlBuf.append("</row>");
		}
		xmlBuf.append("</rows>");

		return xmlBuf.toString();
	}

	@Override
	public void batchReceiveAction(Map<String, Object> reqMap) throws Exception {

		List<String> oidList = (List<String>) reqMap.get("oidList");
		String cmd = (String) reqMap.get("cmd");
		String WfUserEvent = (String) reqMap.get("WfUserEvent");
		String assignrolename = (String) reqMap.get("assignrolename");
		String comment = (String) reqMap.get("comment");
		// System.out.println("cmd =" +cmd);
		// System.out.println("WfUserEvent =" +WfUserEvent);
		// System.out.println("assignrolename =" +assignrolename);
		// System.out.println("comment =" +comment);

		for (String oid : oidList) {
			// System.out.println("oid =" +oid);
			WorkItem workItem = (WorkItem) CommonUtil.getObject(oid);
			WTPrincipalReference prinRef = WTPrincipalReference
					.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
			WFItemHelper.service.setWFItemUserLinkState(workItem, WfUserEvent, comment);
			workItem.setComplete(prinRef.getName());
			PersistenceHelper.manager.save(workItem);

		}

	}

	@Override
	public void userInfoEdit(PeopleData data) throws Exception {
		Transaction trs = new Transaction();
		ArrayList<String> primarys = data.getPrimarys();
		try {
			trs.start();

			People people = (People) CommonUtil.getObject(data.getOid());
			people.setCellTel(data.getCellTel());
			people.setPassword(data.getPassword());
			PersistenceHelper.manager.modify(people);

			WTUser user = (WTUser) CommonUtil.getObject(data.getWoid());
			user.setEMail(data.getEmail());
			PersistenceHelper.manager.modify(user);

			// 모든 첨부파일 삭제
			CommonContentHelper.service.clear(people);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				ApplicationData appData = ApplicationData.newApplicationData(people);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				appData.setRole(ContentRoleType.PRIMARY);
				PersistenceHelper.manager.save(appData);
				appData = (ApplicationData) ContentServerHelper.service.updateContent(people, appData, vault.getPath());
			}

//			if(primarys.size()==0) {
//				ReferenceFactory rf = new ReferenceFactory();
//				ContentHolder holder = (ContentHolder) rf.getReference(data.getOid()).getObject();
//				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
//				if (result.hasMoreElements()) {
//					ApplicationData appData = (ApplicationData) result.nextElement();
//					PersistenceHelper.manager.delete(appData);
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
	public void apply(Map<String, String> params) throws Exception {
		String oid = params.get("oid");
		String creatorOid = params.get("creatorOid");
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTReference ref = rf.getReference(oid);
			System.out.println("ref=" + ref);
			if (ref == null) {
				throw new Exception("해당 OID에 일치하는 객체가 없습니다.");
			}

			Persistable persistable = ref.getObject();
			if (!(persistable instanceof Iterated)) {
				throw new Exception("작성자를 구현한 객체가 아닙니다.");
			}

			// 사용자 변경은 안되는거로... 기안자 의미가 있어야함 
			Iterated iterated = (Iterated) persistable;
			iterated = (Iterated) PersistenceHelper.manager.refresh(iterated);

			System.out.println(PersistenceHelper.isPersistent(iterated));

			WTUser user = (WTUser) CommonUtil.getObject(creatorOid);

			WTPrincipalReference principalReference = WTPrincipalReference.newWTPrincipalReference(user);
			VersionControlHelper.assignIterationCreator(iterated, principalReference);

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
