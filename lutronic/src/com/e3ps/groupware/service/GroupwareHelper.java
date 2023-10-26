package com.e3ps.groupware.service;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.dto.NoticeDTO;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.OrgHelper;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.enterprise.Master;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class GroupwareHelper {
	public static final GroupwareService service = ServiceFactory.getService(GroupwareService.class);
	public static final GroupwareHelper manager = new GroupwareHelper();

	
	public Map<String, Object> listItem(Map<String, Object> params) throws Exception {
		int page = StringUtil.getIntParameter((String) params.get("page"), 1);
		int rows = StringUtil.getIntParameter((String) params.get("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) params.get("formPage"), 10);
		boolean isDistribute = StringUtil.checkNull((String) params.get("distribute")).equals("true");

		String sessionId = (String) params.get("sessionId");

		String state = (String) params.get("state");
		String className = (String) params.get("className");

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

		// 2016.03.07 row id 가 중복일 경우 오류방지를 위해 index 추가
		int index = 0;

		List<WFItem> wfList = new ArrayList<WFItem>();
		Map<String, Object> map = new HashMap<String, Object>();

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
			wfList.add(wfItem);
		}
		map.put("list", wfList);
		return map;
	}

	public Map<String, Object> organization(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PeopleDTO> list = new ArrayList<PeopleDTO>();

		String name = (String) params.get("name");
		String userId = (String) params.get("userId");
		String oid = (String) params.get("oid"); // 부서 OID
		long dOid = CommonUtil.getOIDLongValue(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.ID, userId);
		QuerySpecUtils.toEqualsAnd(query, idx, People.class, "departmentReference.key.id", dOid);

		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			PeopleDTO data = new PeopleDTO(people);
			list.add(data);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public Map<String, Object> workItem(Map<String, Object> params) throws Exception {
		boolean isDistribute = StringUtil.checkNull((String) params.get("distribute")).equals("true");
		boolean isAdmin = CommonUtil.isAdmin();
		String sessionId = (String) params.get("sessionId");
		String userId = StringUtil.checkNull((String) params.get("userOid"));
		String fullname = StringUtil.checkNull((String) params.get("tempnewUser"));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
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

			ReferenceFactory rf = new ReferenceFactory();
			LifeCycleManaged pbo = null;
			String viewOid = "";
			String oid = "";

			String[] objName = null;
			String order = (String) params.get("order");

			// 2016.03.07 row id 가 중복일 경우 오류방지를 위해 index 추가
			int index = 0;
			Object obj[] = null;
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			while (result.hasMoreElements()) {

				index++;

				obj = (Object[]) result.nextElement();
				oid = obj[0] + ":" + obj[1];

				WorkItem item = (WorkItem) rf.getReference(oid).getObject();
				WfActivity activity = (WfActivity) item.getSource().getObject();
				try {
					pbo = (LifeCycleManaged) item.getPrimaryBusinessObject().getObject();
				} catch (Exception e) {
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

				QueryResult qrResult = WfEngineHelper.service.getAssociatedProcesses((Persistable) wobj, null,
						containerRef);

				String wfProcessOid = "";
				WfProcess wfprocess = null;

				if (qrResult.hasMoreElements()) {
					wfprocess = (WfProcess) qrResult.nextElement();

					/*
					 * wfProcessOid =
					 * wfprocess.getPersistInfo().getObjectIdentifier().getStringValue(); String
					 * processState = wfprocess.getState().getFullDisplay();
					 * System.out.println("processState="+processState);
					 * if("종료됨".equals(processState)) continue;
					 */
				}
				data.put("objName0", objName0);
				data.put("activityName", activity.getName());
				if (isDistribute) {
					data.put("objName2", objName1 + "[" + objName2 + "]");
				} else {
					data.put("objName2", objName1 + "[" + objName2 + "]");
				}
				if ("수신".equals(activity.getName())) {
					String worker = WorklistHelper.service.getCreatorName(wobj);
					data.put("worker", worker);
				} else {
					data.put("creatorName", activity.getParentProcess().getCreator().getFullName());
				}

				if (wfProcessOid == null || wfProcessOid.equals("") || wfProcessOid == "") {
					data.put("state", pbo.getLifeCycleState().getDisplay(Message.getLocale()));
				} else {
					data.put("state", pbo.getLifeCycleState().getDisplay(Message.getLocale()));
				}
				data.put("createDate", DateUtil.getDateString(item.getPersistInfo().getCreateStamp(), "d"));
				data.put("name", ((WTUser) OwnershipHelper.getOwner(item)).getFullName());
				data.put("oid", oid);
				list.add(data);
			}

			resultMap.put("list", list);
			resultMap.put("topListCount", pager.getTotal());
			resultMap.put("pageSize", pager.getPsize());
			resultMap.put("total", pager.getTotalSize());
			resultMap.put("sessionid", pager.getSessionId());
			resultMap.put("curPage", pager.getCpage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	public Map<String, Object> info(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String oid = (String) params.get("oid");

		if (oid.indexOf(":") <= -1) {
			map.put("msg", "OID 값의 형식이 맞지 않습니다.");
			return map;
		}

		ReferenceFactory rf = new ReferenceFactory();
		Persistable per = rf.getReference(oid).getObject();

		if (!(per instanceof LifeCycleManaged)) {
			map.put("msg", "라이프사이클 객체가 아닙니다.");
			return map;
		}

		if (per instanceof LifeCycleManaged) {
			LifeCycleManaged lcm = (LifeCycleManaged) per;
			LifeCycleTemplate lct = (LifeCycleTemplate) lcm.getLifeCycleTemplate().getObject();

			map.put("state", lcm.getLifeCycleState().getDisplay());
			map.put("oid", oid);
			map.put("name", lct.getName());

			QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(per, null, WCUtil.getWTContainerRef());

			List<Map<String, String>> workflow = new ArrayList<>();
			while (qr.hasMoreElements()) {
				WfProcess wfprocess = (WfProcess) qr.nextElement();
				Map<String, String> data = new HashMap<>();
				data.put("oid", wfprocess.getPersistInfo().getObjectIdentifier().getStringValue());
				data.put("name", wfprocess.getName());
				data.put("state", wfprocess.getState().getDisplay());
				data.put("createdTime", wfprocess.getCreateTimestamp().toString());
				workflow.add(data);
			}

			List<Map<String, String>> content = new ArrayList<>();
			if (per instanceof ContentHolder) {
				ContentHolder holder = (ContentHolder) per;
				Vector vector = ContentHelper.getContentListAll(holder);
				for (int i = 0; i < vector.size(); i++) {
					ContentItem item = (ContentItem) vector.get(i);
					if (item instanceof ApplicationData) {
						ApplicationData app = (ApplicationData) item;
						Map<String, String> data = new HashMap<>();
						URL url = ContentHelper.getDownloadURL(holder, app);
						data.put("url", url.toString());
						data.put("name", app.getFileName());
						content.add(data);
					}
				}
			}
			map.put("workflow", workflow);
			map.put("content", content);
		}

		// 라이프사이클 정보

		return map;
	}
}
