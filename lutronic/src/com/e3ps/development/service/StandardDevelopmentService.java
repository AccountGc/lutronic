package com.e3ps.development.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.mail.MailHtmlContentTemplate;
import com.e3ps.common.mail.MailUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.devOutPutLink;
import com.e3ps.development.devTask;
import com.e3ps.development.beans.DevActiveData;
import com.e3ps.development.beans.DevTaskData;
import com.e3ps.development.beans.MasterData;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.service.UserHelper;

public class StandardDevelopmentService extends StandardManager implements DevelopmentService {

	public String LC_Development = "LC_Development";
	
	public static StandardDevelopmentService newStandardDevelopmentService() throws Exception {
		final StandardDevelopmentService instance = new StandardDevelopmentService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public Map<String,String> requestDevelopmentMapping(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		
		String model = StringUtil.checkNull(request.getParameter("model"));
		String developmentStart = StringUtil.checkNull(request.getParameter("developmentStart"));
		String developmentEnd = StringUtil.checkNull(request.getParameter("developmentEnd"));
		String description = StringUtil.checkNull(request.getParameter("description"));
		
		map.put("oid", oid);
		map.put("model", model);
		map.put("developmentStart", developmentStart);
		map.put("developmentEnd", developmentEnd);
		map.put("description", description);
		
		return map;
	}
	
	@Override
	public ResultData createDevelopmentAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		
		try {
			trx.start();
			
			devMaster master = devMaster.newdevMaster();
			
			String name = StringUtil.checkNull(request.getParameter("name"));
			String model = StringUtil.checkNull(request.getParameter("model"));
			String developmentStart = StringUtil.checkNull(request.getParameter("developmentStart"));
			String developmentEnd = StringUtil.checkNull(request.getParameter("developmentEnd"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String dm = StringUtil.checkNull(request.getParameter("dm"));
			
			master.setName(name);
			master.setModel(model);
			master.setStartDay(developmentStart);
			master.setEndDay(developmentEnd);
			master.setDescription(description);
			
			PDMLinkProduct product = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
			master.setContainer(product);
			
			// 폴더 설정
			Folder folder = FolderTaskLogic.getFolder("/Default/DEVWORK/Master", WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) master, folder);
			
			// 라이프사이클 셋팅
			LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(LC_Development, wtContainerRef);
			master = (devMaster) LifeCycleHelper.setLifeCycle(master, tmpLifeCycle);
			
			// DmMaster Link 생성
			WTUser dmUser = (WTUser)CommonUtil.getObject(dm);// (WTUser)SessionHelper.manager.getPrincipal();
			master.setDm(dmUser);
			
			master = (devMaster) PersistenceHelper.manager.save(master);
			
			trx.commit();
			trx = null;
			data.setResult(true);
			data.setOid(CommonUtil.getOIDString(master));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null){
				trx.rollback();
			}
		}
		
		return data;
	}
	
	
	@Override
	public Map<String,Object> listDevelopmentAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");
		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = DevelopmentQueryHelper.service.listDevelopmentSearchQuery(request, response);
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
		
		while(qr.hasMoreElements()) {
			Object[] o = (Object[])qr.nextElement();
			devMaster master = (devMaster)o[0];
			MasterData data = new MasterData(master);
			
			xmlBuf.append("<row id='" + data.oid + "'>");

			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.model + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:viewDevelopment('" + data.oid + "')>" + data.fullName + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.dmName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.developmentStart + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.developmentEnd + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.dateSubString(true) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.state + "]]></cell>");
			
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
	public ResultData deleteDevelopmentAction(String oid) {
		ResultData data = new ResultData();
		
		try {
			devMaster master = (devMaster)CommonUtil.getObject(oid);
			
			QuerySpec spec = DevelopmentQueryHelper.service.getTaskListFormMasterOid(oid);
			
			QueryResult result = PersistenceHelper.manager.find((StatementSpec)spec);
			
			while(result.hasMoreElements()){
				Object[] o = (Object[]) result.nextElement();
				devTask task = (devTask)o[0];
				
				QuerySpec qs = DevelopmentQueryHelper.service.getActiveListFromTaskOid(CommonUtil.getOIDString(task));
				QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
				
				while(qr.hasMoreElements()) {
					Object[] oo = (Object[])qr.nextElement();
					devActive active = (devActive)oo[0];
					
					PersistenceHelper.manager.delete(active);
				}
				
				
				PersistenceHelper.manager.delete(task);
			}
			
			PersistenceHelper.manager.delete(master);
			data.setResult(true);
			data.setMessage(Message.get("삭제 되었습니다."));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}
		return data;
	}
	
	@Override
	public ResultData updateDevelopmentAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		try{
			trx.start();
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			
		 	devMaster master = (devMaster)CommonUtil.getObject(oid);
		 	
		 	String model = StringUtil.checkNull(request.getParameter("model"));
			String developmentStart = StringUtil.checkNull(request.getParameter("developmentStart"));
			String developmentEnd = StringUtil.checkNull(request.getParameter("developmentEnd"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String dm = StringUtil.checkNull(request.getParameter("dm"));
			String name = StringUtil.checkNull(request.getParameter("name"));
			
			master.setModel(model);
			master.setStartDay(developmentStart);
			master.setEndDay(developmentEnd);
			master.setDescription(description);
			master.setDm((WTUser)CommonUtil.getObject(dm));
			master.setName(name);
			
			master = (devMaster)PersistenceHelper.manager.modify(master);
			
			trx.commit();
			trx = null;
			data.setResult(true);
			data.setOid(CommonUtil.getOIDString(master));
			
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}
		return data;
	}
	
	@Override
	public ResultData editTaskAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			trx.start();

			/* Task 삭제 로직 */
			String deleteTaskOids = StringUtil.checkNull(request.getParameter("deleteTaskOids"));
			String[] deleteOids = deleteTaskOids.split(",");
			
			for(String deleteTaskOid : deleteOids) {
				if(!"".equals(deleteTaskOid)){
					devTask task = (devTask)CommonUtil.getObject(deleteTaskOid);
					PersistenceHelper.manager.delete(task);
				}
			}
			
			/* 기존 Task 수정 */
			String[] taskOids = request.getParameterValues("taskOids");
			
			if(taskOids != null) {
				for(String taskOid : taskOids) {
					
					if(!"".equals(taskOid)) {
						devTask task = (devTask)CommonUtil.getObject(taskOid);
						
						String taskName = StringUtil.checkNull(request.getParameter(taskOid + "_taskNames"));
						String taskDescription = StringUtil.checkNull(request.getParameter(taskOid + "_taskDescriptions"));
						
						task.setName(taskName);
						task.setDescription(taskDescription);
						
						PersistenceHelper.manager.modify(task);
					}
				}
			}
			
			/* 새로운 Task 추가 */
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			devMaster master = (devMaster)CommonUtil.getObject(oid);
			
			String[] taskNames = request.getParameterValues("taskNames");
			String[] taskDescriptions = request.getParameterValues("taskDescriptions");
			
			if(taskNames != null) {
				for(int i=0; i < taskNames.length; i++) {
					String taskName = taskNames[i];
					String taskDescription = taskDescriptions[i];
					
					devTask task = devTask.newdevTask();
					task.setName(taskName);
					task.setDescription(taskDescription);
					task.setMaster(master);
					
					// 폴더 설정
					Folder folder = FolderTaskLogic.getFolder("/Default/DEVWORK/Master", WCUtil.getWTContainerRef());
					FolderHelper.assignLocation((FolderEntry) task, folder);
					
					// 라이프사이클 셋팅
					PDMLinkProduct product = WCUtil.getPDMLinkProduct();
					WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
					LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(LC_Development, wtContainerRef);
					task = (devTask) LifeCycleHelper.setLifeCycle(task, tmpLifeCycle);
					
					PersistenceHelper.manager.save(task);
				}
			}
			
			trx.commit();
			trx = null;
			data.setResult(true);
			data.setMessage(Message.get("저장되었습니다"));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		return data;
	}
	
	@Override
	public List<DevTaskData> getTaskDataList(String oid) {
		List<DevTaskData> list = new ArrayList<DevTaskData>();
		
		try{
		
			QuerySpec query = DevelopmentQueryHelper.service.getTaskListFormMasterOid(oid);
			
			QueryResult result = PersistenceHelper.manager.find((StatementSpec)query);
			
			while(result.hasMoreElements()) {
				Object[] o = (Object[])result.nextElement();
				devTask task = (devTask)o[0];
				DevTaskData data = new DevTaskData(task);
				
				list.add(data);
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public List<DevActiveData> getActiveDataList(String oid) {
		List<DevActiveData> list = new ArrayList<DevActiveData>();
		
		try {
			QuerySpec spec = DevelopmentQueryHelper.service.getActiveListFromTaskOid(oid);
			
			QueryResult result = PersistenceHelper.manager.find((StatementSpec)spec);
			
			while(result.hasMoreElements()){
				Object[] o = (Object[])result.nextElement();
				devActive active = (devActive)o[0];
				DevActiveData data = new DevActiveData(active);
				
				list.add(data);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public ResultData editActiveAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			List<devActive> sendMailActives = new ArrayList<devActive>();
			
			/* Active 삭제 로직 */
			String deleteActiveOids = StringUtil.checkNull(request.getParameter("deleteActiveOids"));
			String[] deleteOids = deleteActiveOids.split(",");
			
			for(String deleteActiveOid : deleteOids) {
				if(!"".equals(deleteActiveOid)) {
					devActive active = (devActive)CommonUtil.getObject(deleteActiveOid);
					PersistenceHelper.manager.delete(active);
				}
			}
			
			/* 기존 Active 수정 */
			String[] activeOids = request.getParameterValues("activeOids");
			if(activeOids != null) {
				for(String activeOid : activeOids) {
					if(!"".equals(activeOid)) {
						
						devActive active = (devActive)CommonUtil.getObject(activeOid);
						
						String activeName = StringUtil.checkNull(request.getParameter(activeOid + "_activeNames"));
						String activeDate = StringUtil.checkNull(request.getParameter(activeOid + "_activeDates"));
						String activeWorker = StringUtil.checkNull(request.getParameter(activeOid + "_activeWorkers"));
						
						WTUser worker = (WTUser)CommonUtil.getObject(activeWorker);
						
						String oidWorker = CommonUtil.getOIDString(active.getWorker());
						
						active.setName(activeName);
						active.setActiveDate(activeDate);
						active.setWorker(worker);
						
						PersistenceHelper.manager.modify(active);
						
						if(!(oidWorker).equals(activeWorker)) {
							sendMailActives.add(active);
						}
					}
				}
			}
			
			
			/* 새로운 Active 등록 */
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			devTask task = (devTask)CommonUtil.getObject(oid);
			
			String[] activeNames = request.getParameterValues("activeNames");
			String[] activeDates = request.getParameterValues("activeDates");
			String[] activeWorkers = request.getParameterValues("activeWorkers");
			
			if(activeNames != null) {
				for(int i=0; i < activeNames.length; i++) {
					String activeName = activeNames[i];
					String activeDate = activeDates[i];
					
					devActive active = devActive.newdevActive();
					active.setName(activeName);
					active.setActiveDate(activeDate);
					active.setTask(task);
					active.setMaster(task.getMaster());
					active.setDm(task.getMaster().getDm());
					active.setWorker((WTUser)CommonUtil.getObject(activeWorkers[i]));
					
					// 폴더 설정
					Folder folder = FolderTaskLogic.getFolder("/Default/DEVWORK/Active", WCUtil.getWTContainerRef());
					FolderHelper.assignLocation((FolderEntry) active, folder);
					
					// 라이프사이클 셋팅
					PDMLinkProduct product = WCUtil.getPDMLinkProduct();
					WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
					LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(LC_Development, wtContainerRef);
					active = (devActive) LifeCycleHelper.setLifeCycle(active, tmpLifeCycle);
					
					PersistenceHelper.manager.save(active);
					sendMailActives.add(active);
				}
			}
			
			trx.commit();
			trx = null;
			
			for(devActive active : sendMailActives) {
				devActivityMailSend(active);
			}
			
			data.setResult(true);
			data.setMessage(Message.get("저장되었습니다"));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		return data;
	}
	
	@Override
	public ResultData deleteTaskAction(String oid) {
		ResultData data = new ResultData();
		
		try {
			devTask task = (devTask)CommonUtil.getObject(oid);
			
			QuerySpec spec = DevelopmentQueryHelper.service.getActiveListFromTaskOid(oid);
			
			QueryResult result = PersistenceHelper.manager.find((StatementSpec)spec);
			
			while(result.hasMoreElements()){
				Object[] o = (Object[]) result.nextElement();
				devActive active = (devActive)o[0];
				
				PersistenceHelper.manager.delete(active);
			}
			
			PersistenceHelper.manager.delete(task);
			data.setResult(true);
			data.setMessage(Message.get("삭제 되었습니다."));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}
		return data;
	}
	
	@Override
	public ResultData updateTaskAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			String taskOid = StringUtil.checkNull(request.getParameter("taskOid"));
			
			devTask task = (devTask)CommonUtil.getObject(taskOid);
			
			String taskName = request.getParameter("taskName");
			String taskDescription = request.getParameter("taskDescription");
			
			task.setName(taskName);
			task.setDescription(taskDescription);
			
			task = (devTask)PersistenceHelper.manager.modify(task);
			
			trx.commit();
			trx = null;
			data.setResult(true);
			data.setOid((String)CommonUtil.getOIDString(task));
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		return data;
	}
	
	
	@Override
	public ResultData updateActiveAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		
		try {
			trx.start();
			
			boolean sendMailCheck = false;
			
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			
			devActive active = (devActive)CommonUtil.getObject(oid);
			
			String activeName = StringUtil.checkNull(request.getParameter("activeName"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String activeWorker = StringUtil.checkNull(request.getParameter("activeWorker"));
			String activeDate = StringUtil.checkNull(request.getParameter("activeDate"));
			
			if(!((CommonUtil.getOIDString(active.getWorker())).equals(activeWorker))) {
				sendMailCheck = true;
			}
			
			active.setName(activeName);
			active.setDescription(description);
			active.setWorker((WTUser)CommonUtil.getObject(activeWorker));
			active.setActiveDate(activeDate);
			
			active = (devActive) PersistenceHelper.manager.modify(active);
			
			String[] secondary = request.getParameterValues("SECONDARY");
			String[] delocIds = request.getParameterValues("delocIds");
			
			CommonContentHelper.service.attach(active, null, secondary, delocIds, false);
			
			trx.commit();
			trx = null;
			
			if(sendMailCheck) {
				devActivityMailSend(active);
			}
			
			data.setResult(true);
			data.setOid(oid);
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
	
	@Override
	public ResultData changeStateAction(String oid, String state) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		
		try {
			trx.start();
			
			Object obj = CommonUtil.getObject(oid);
			
			if(obj instanceof devMaster
					|| obj instanceof devTask) {
				
				changeState((LifeCycleManaged)obj, state);
				
			}else if(obj instanceof devActive) {
				
				devActive active = (devActive)obj;
				
				if("COMPLETED".equals(state)) {
					active.setFinishDate(DateUtil.getCurrentTimestamp());
				}else {
					active.setFinishDate(null);
				}
				active = (devActive)PersistenceHelper.manager.modify(active);
				changeState((LifeCycleManaged)obj, state);
			}
				
			
			trx.commit();
			trx = null;
			data.setResult(true);
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
	
	private void changeState(LifeCycleManaged managed, String state) throws Exception {
		LifeCycleHelper.service.setLifeCycleState(managed, State.toState(state), true);
	}
	
	@Override
	public List<Map<String,String>> viewUserList(String oid) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		try {
			
			Map<String,String> map = new HashMap<String,String>();
			
			devMaster master = (devMaster)CommonUtil.getObject(oid);
			WTUser dm = master.getDm();
			People people = UserHelper.service.getPeople(dm);
			Department dept = people.getDepartment();
			
			map.put("Role", "DM");
			map.put("name", dm.getFullName());
			map.put("duty", people.getDuty());
			map.put("department", dept == null ? "" : StringUtil.checkNull(dept.getName()));
			list.add(map);
			
			QuerySpec userSpec = DevelopmentQueryHelper.service.getDevelopmentUsers(oid);
			QueryResult userResult = PersistenceServerHelper.manager.query(userSpec);
			
			while(userResult.hasMoreElements()) {
				map = new HashMap<String,String>();
				Object obj[] = (Object[]) userResult.nextElement();
				long userOid = ((BigDecimal) obj[0]).intValue();
				
				WTUser worker = (WTUser)CommonUtil.getObject("wt.org.WTUser:"+userOid);
				people = UserHelper.service.getPeople(worker);
				dept = people.getDepartment();
				
				map.put("Role", Message.get("수행자"));
				map.put("name", worker.getFullName());
				map.put("duty", people.getDuty());
				map.put("department", dept == null ? "" : StringUtil.checkNull(dept.getName()));
				list.add(map);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public ResultData deleteActiveAction(String oid) {
		ResultData data = new ResultData();
		
		Transaction trx = new Transaction();
		
		try {
			trx.start();
			
			devActive active = (devActive)CommonUtil.getObject(oid);
			
			PersistenceHelper.manager.delete(active);
			
			trx.commit();
			trx = null;
			
			data.setResult(true);
			
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
	
	@Override
	public Map<String, Object> listMyDevelopmentAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");
		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = DevelopmentQueryHelper.service.listMyDevelopmentSearchQuery(request, response);
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
		
		while(qr.hasMoreElements()) {
			Object[] o = (Object[])qr.nextElement();
			devActive active = (devActive)o[0];
			DevActiveData data = new DevActiveData(active);
			
			xmlBuf.append("<row id='" + data.oid + "'>");
			
			String select = StringUtil.checkNull(request.getParameter("select"));
			if(select.length() > 0){
				xmlBuf.append("<cell><![CDATA[]]></cell>");
			}

			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getMaster().getModel() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:viewDevelopment('" + data.getMaster().getPersistInfo().getObjectIdentifier().toString() + "')>" 
										+ data.masterName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getMaster().getLifeCycleState().getDisplay(Message.getLocale()) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getTask().getName() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.dmName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.workerName + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.activeDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + DateUtil.subString(data.finishDate, 0, 10) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.state + "]]></cell>");
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
	public boolean buttonControll(String oid) {
		
		try {
			if(CommonUtil.isAdmin()) {
				return true;
			}else {
				Object obj = CommonUtil.getObject(oid);
				
				if(obj instanceof devMaster) {
					MasterData data = new MasterData((devMaster)obj);
					return data.isDm();
				}else if(obj instanceof devTask) {
					DevTaskData data = new DevTaskData((devTask)obj);
					return data.isDm();
				}else if(obj instanceof devActive) {
					DevActiveData data = new DevActiveData((devActive)obj);
					return data.isDm() || data.isWorker();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void createDocumentToDevelopmentLink(WTDocument document, String parentOid) throws Exception {
		if(parentOid.length() > 0) {
    		devActive active = (devActive)CommonUtil.getObject(parentOid);
    		
    		devOutPutLink link = devOutPutLink.newdevOutPutLink(active, document);
    		PersistenceServerHelper.manager.insert(link);
		}
	}
	
	@Override
	public List<DevActiveData> include_DevelopmentView(String moduleType, String oid) throws Exception {
		
		List<DevActiveData> list = null;
		
		if(oid.length() > 0) {
			if("doc".equals(moduleType) || "drawing".equals(moduleType) || "part".equals(moduleType)){
				RevisionControlled rev = (RevisionControlled)CommonUtil.getObject(oid);
				list = DevelopmentQueryHelper.service.getActiveDataFromRevisionControlled(rev);
			}
		}
		
		return list;
	}
	
	public boolean devActivityMailSend(devActive active) throws Exception {
		
		DevActiveData data = new DevActiveData(active);
		
		String subject = "";
		
		subject = "[" + data.masterName + "] " + data.name + " 수행자 선정";
		
		String content = "";
		
		HashMap<String, String> to = new HashMap<String, String>();
		WTUser worker = active.getWorker();
		to.put(worker.getEMail(), worker.getFullName());
		
		HashMap<String, String> from = new HashMap<String, String>();
		from.put("EMAIL", data.dm.getEMail());
		from.put("NAME", data.dmName);
		
		Hashtable<String, String> hash = new Hashtable<String, String>();
		hash.put("gubun", "프로젝트");
		hash.put("workName", "수행자 요청");
		hash.put("viewString", data.name);
		hash.put("DM", data.dmName);
		hash.put("description", data.getDescription(true));
		
		ResourceBundle bundle =ResourceBundle.getBundle("wt");
 
		String url = bundle.getString("wt.rmi.server.hostname");
		
		hash.put("url", url);
		
		MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();
		content = template.htmlContent(hash,"development_notice.html");
		
		Hashtable<String, Object> mailHash = new Hashtable<String, Object>();
		mailHash.put("FROM", from);
		mailHash.put("TO", to);
		mailHash.put("SUBJECT", subject);
		mailHash.put("CONTENT", content);
		
		return MailUtil.sendMail2(mailHash);
	}
	
	@Override
	public ResultData requestCompleteAction(String oid){
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try{
			trx.start();
			
			devActive active = (devActive)CommonUtil.getObject(oid);
			changeState((LifeCycleManaged)active, "REQUEST_COMPLETED");
			
			DevActiveData activeData = new DevActiveData(active);
			
			String subject = "";
			
			subject = "[" + activeData.name + "] 완료 요청";
			
			HashMap<String, String> to = new HashMap<String, String>();
			to.put(activeData.dm.getEMail(), activeData.dmName);
			
			HashMap<String, String> from = new HashMap<String, String>();
			from.put("EMAIL", activeData.worker.getEMail());
			from.put("NAME", activeData.workerName);
			
			Hashtable<String, String> hash = new Hashtable<String, String>();
			hash.put("gubun", "프로젝트");
			hash.put("workName", "완료 요청");
			hash.put("viewString", activeData.name);
			hash.put("DM", activeData.dmName);
			hash.put("description", activeData.getDescription(true));
			
			ResourceBundle bundle =ResourceBundle.getBundle("wt");
	
			String url = bundle.getString("wt.rmi.server.hostname");
			
			hash.put("url", url);
			
			MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();
			String content = template.htmlContent(hash,"development_notice.html");
			
			Hashtable<String, Object> mailHash = new Hashtable<String, Object>();
			mailHash.put("FROM", from);
			mailHash.put("TO", to);
			mailHash.put("SUBJECT", subject);
			mailHash.put("CONTENT", content);
			
			boolean result = MailUtil.sendMail2(mailHash);
			
			/*
			if(result) {
				data.setResult(true);
				data.setMessage(Message.get("완료 요청 되었습니다."));
			}else {
				data.setResult(false);
				data.setMessage(Message.get("완료 요청이 실패하였습니다."));
			}
			*/
			
			trx.commit();
			trx = null;
			
			data.setResult(true);
			data.setMessage(Message.get("완료 요청 되었습니다."));
			
		}catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		return data;
	}
	
	@Override
	public ResultData updatCommentAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		
		try {
			trx.start();
			
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			String comment = StringUtil.checkNull(request.getParameter("comment"));
			
			devActive active = (devActive)CommonUtil.getObject(oid);
			
			active.setWorker_comment(comment);
			
			PersistenceHelper.manager.modify(active);
			
			trx.commit();
			trx = null;
			
			data.setResult(true);
			data.setMessage(Message.get("입력되었습니다."));
			
		} catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
	
	@Override
	public ResultData updatAttachAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		
		try {
			
			trx.start();
			
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			String[] secondary = request.getParameterValues("worker");
			String[] delocIds = request.getParameterValues("workerdelocIds");
			
			devActive active = (devActive)CommonUtil.getObject(oid);
			
			CommonContentHelper.service.attach(active, null, secondary, delocIds, "worker", false);
			
			trx.commit();
			trx = null;
			
			data.setResult(true);
			data.setMessage(Message.get("첨부되었습니다."));
			
		}catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}finally {
			if(trx != null) {
				trx.rollback();
			}
		}
		
		return data;
	}
}
