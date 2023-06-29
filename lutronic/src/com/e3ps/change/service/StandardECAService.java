package com.e3ps.change.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ROOTData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.scheduler.SchedulingMethod;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.common.workflow.StandardE3PSWorkflowService;
import com.e3ps.common.workflow.WorkProcessHelper;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.org.service.PeopleHelper;
import com.e3ps.org.service.UserHelper;
import com.e3ps.part.beans.PartData;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;

import wt.admin.AdministrativeDomain;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

@SuppressWarnings("serial")
public class StandardECAService extends StandardManager implements ECAService {

	public static StandardECAService newStandardECAService() throws Exception {
		final StandardECAService instance = new StandardECAService();
		instance.initialize();
		return instance;
	}
	
	/**
	 * RootDefinition 생성
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultData createRootDefinitionAction(HttpServletRequest req) throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
	    	
	    	String name =StringUtil.checkNull(req.getParameter("name"));
	    	String name_eng =StringUtil.checkNull(req.getParameter("name_eng"));
	    	String sortNumber =StringUtil.checkNull(req.getParameter("sortNumber"));
	    	int sort = 0;
	    	if(sortNumber.length()>0){
	    		sort = Integer.parseInt(sortNumber);
	    	}
	    	String description =StringUtil.checkNull(req.getParameter("description"));
	    
	    	EChangeActivityDefinitionRoot def = EChangeActivityDefinitionRoot.newEChangeActivityDefinitionRoot();
			def.setName(name);
			def.setName_eng(name_eng);
			def.setDescription(description);
			def.setSortNumber(sort);
			def = (EChangeActivityDefinitionRoot)PersistenceHelper.manager.save(def);
		
			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(def));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			//System.out.println("ECR Action Error : "+result.getMessage());
	    } finally {
	        if(trx!=null){
					trx.rollback();
			   }
	    }
		return result;
		
	}
	
	/**
	 * RootDefinition 수정
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultData updateRootDefinitionAction(HttpServletRequest req) throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
	    	String oid = StringUtil.checkNull(req.getParameter("oid"));
	    	String name = StringUtil.checkNull(req.getParameter("name"));
	    	String name_eng =StringUtil.checkNull(req.getParameter("name_eng"));
	    	String sortNumber =StringUtil.checkNull(req.getParameter("sortNumber"));
	    	int sort = 0;
	    	if(sortNumber.length()>0){
	    		sort = Integer.parseInt(sortNumber);
	    	}
	    	String description =StringUtil.checkNull(req.getParameter("description"));
	    
	    	EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot)CommonUtil.getObject(oid);
			def.setName(name);
			def.setName_eng(name_eng);
			def.setDescription(description);
			def.setSortNumber(sort);
			def = (EChangeActivityDefinitionRoot)PersistenceHelper.manager.modify(def);
		
			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(def));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			//System.out.println("ECR Action Error : "+result.getMessage());
	    } finally {
	        if(trx!=null){
					trx.rollback();
			   }
	    }
		return result;
		
	}
	
	/**
	 * RootDefinition 삭제
	 * @return
	 * @throws Exception
	 */
	@Override
	public String deleteRootDefinitionAction(HttpServletRequest req) throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
	    	String oid = StringUtil.checkNull(req.getParameter("oid"));
	    	EChangeActivityDefinitionRoot root = (EChangeActivityDefinitionRoot)CommonUtil.getObject(oid);
			
			List<EChangeActivityDefinition> list = getActiveDefinition(root.getPersistInfo().getObjectIdentifier().getId());
			if(list!=null){
				for(EChangeActivityDefinition def : list){
					PersistenceHelper.manager.delete(def);
				}
			}
			root = (EChangeActivityDefinitionRoot)PersistenceHelper.manager.delete(root);
		
			trx.commit();
			trx = null;
			
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
	    } finally {
	        if(trx!=null){
					trx.rollback();
			   }
	    }
		return Message.get("삭제 되었습니다.");
		
	}
	
	/**
	 * ActivityDefinition 등록
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultData createActivityDefinitionAction(HttpServletRequest req) throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
	    	String oid = StringUtil.checkNull(req.getParameter("oid"));
	    	String name =StringUtil.checkNull(req.getParameter("name"));
	    	String name_eng =StringUtil.checkNull(req.getParameter("name_eng"));
	    	
	    	String step =StringUtil.checkNull(req.getParameter("eoStep"));
	    	String activeType =StringUtil.checkNull(req.getParameter("activeType"));
	    	String activeUser =StringUtil.checkNull(req.getParameter("activeUser"));
	    	WTUser user = (WTUser)CommonUtil.getObject(activeUser);
	    	String sortNumber =StringUtil.checkNull(req.getParameter("sortNumber"));
	    	
	    	int sort = 0;
	    	if(sortNumber.length()>0){
	    		sort = Integer.parseInt(sortNumber);
	    	}
	    	String description =StringUtil.checkNull(req.getParameter("description"));
	    	EChangeActivityDefinitionRoot root = (EChangeActivityDefinitionRoot)CommonUtil.getObject(oid);
	    	EChangeActivityDefinition def = EChangeActivityDefinition.newEChangeActivityDefinition();
			def.setName(name);
			def.setName_eng(name_eng);
			def.setStep(step);
			def.setActiveType(activeType);
			def.setActiveUser(user);
			def.setDescription(description);
			def.setSortNumber(sort);
			def.setRoot(root);
			def = (EChangeActivityDefinition)PersistenceHelper.manager.save(def);
		
			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(def));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			//System.out.println("Activity Action Error : "+result.getMessage());
	    } finally {
	        if(trx!=null){
					trx.rollback();
			   }
	    }
		return result;
		
	}
	
	/**
	 * ActivityDefinition 등록
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultData updateActivityDefinitionAction(HttpServletRequest req) throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
	    	String oid = StringUtil.checkNull(req.getParameter("oid"));
	    	String name =StringUtil.checkNull(req.getParameter("name"));
	    	String name_eng =StringUtil.checkNull(req.getParameter("name_eng"));
	    	
	    	String step =StringUtil.checkNull(req.getParameter("eoStep"));
	    	String activeType =StringUtil.checkNull(req.getParameter("activeType"));
	    	String activeUser =StringUtil.checkNull(req.getParameter("activeUser"));
	    	WTUser user = (WTUser)CommonUtil.getObject(activeUser);
	    	String sortNumber =StringUtil.checkNull(req.getParameter("sortNumber"));
	    	
	    	int sort = 0;
	    	if(sortNumber.length()>0){
	    		sort = Integer.parseInt(sortNumber);
	    	}
	    	String description =StringUtil.checkNull(req.getParameter("description"));
	    	EChangeActivityDefinition def = (EChangeActivityDefinition)CommonUtil.getObject(oid);
	    	
			def.setName(name);
			def.setName_eng(name_eng);
			def.setStep(step);
			def.setActiveType(activeType);
			def.setActiveUser(user);
			def.setDescription(description);
			def.setSortNumber(sort);
			def = (EChangeActivityDefinition)PersistenceHelper.manager.modify(def);
		
			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(def));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
			//System.out.println("Activity Action Update Error : "+result.getMessage());
	    } finally {
	        if(trx!=null){
					trx.rollback();
			   }
	    }
		return result;
		
	}
	
	/** Root별 활동 리스트
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<EChangeActivityDefinition> getActiveDefinition(long rootOid) throws Exception{
		List<EChangeActivityDefinition> list= new ArrayList<EChangeActivityDefinition>();
		
		
        ClassAttribute classattribute1 = null;
        ClassAttribute classattribute2 = null;
        SearchCondition sc = null;
		QuerySpec qs = new QuerySpec();
		Class cls1 = EChangeActivityDefinition.class;
		Class cls2 = NumberCode.class;
		
		int idx1 = qs.addClassList(EChangeActivityDefinition.class, true);
		int idx2 = qs.addClassList(NumberCode.class, false);
		
		//Join 
		classattribute1 = new ClassAttribute(cls1,"step" );
	    classattribute2= new ClassAttribute(cls2, "code");
		sc = new SearchCondition(classattribute1, "=", classattribute2);
		sc.setFromIndicies(new int[] {idx1, idx2}, 0);
        sc.setOuterJoin(0);
        qs.appendWhere(sc, new int[] {idx1, idx2});
        
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,"rootReference.key.id","=",rootOid),new int[]{idx1});
		
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls2, "sort"), false),
				new int[] { idx2 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				EChangeActivityDefinition.class, "sortNumber"), false),
				new int[] { idx1 });
		//System.out.println(qs.toString());
		QueryResult result = PersistenceHelper.manager.find(qs);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			list.add((EChangeActivityDefinition) o[0]);
		}

		return list;
	}
	
	/** Root별 활동 리스트
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ROOTData> getRootDefinition() throws Exception{
		List<ROOTData> list= new ArrayList<ROOTData>();
		//System.out.println("ECAHelpr getRootDefinition");
		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(EChangeActivityDefinitionRoot.class, true);
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				EChangeActivityDefinitionRoot.class, "sortNumber"), false),
				new int[] { ii });
		QueryResult result = PersistenceHelper.manager.find(qs);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinitionRoot root = (EChangeActivityDefinitionRoot) o[0];
			ROOTData data = new ROOTData(root);
			list.add(data);
		}

		//System.out.println("list.size() ="+ list.size());
		return list;
	}
	@Override
	public Map<String,Object> listActiveDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		
		String rootOid = StringUtil.checkNull(request.getParameter("rootOid"));
		//System.out.println("ECAHelper listActiveDefinitionAction = "+rootOid);
		if(rootOid.length()==0){
			return result;
		}
		
		long logRootOid = CommonUtil.getOIDLongValue(rootOid);
		
		StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		List<EChangeActivityDefinition> list = getActiveDefinition(logRootOid);
		
		for(EChangeActivityDefinition def : list){
			
			String oid = CommonUtil.getOIDString(def);
			xmlBuf.append("<row id='"+ oid +"'>");
			String activeUser = "";
			String activeUserDepart = "";
			if(def.getActiveUser() != null){
				People pp =UserHelper.service.getPeople(def.getActiveUser());
				activeUser = def.getActiveUser().getFullName();
				if(pp != null && pp.getDepartment() != null){
					activeUserDepart = pp.getDepartment().getName();
				}
				
			}
			NumberCode code = NumberCodeHelper.service.getNumberCode("EOSTEP", def.getStep());
			String codeName ="";
			if(code != null){
				codeName = code.getName();
			}
			xmlBuf.append("<cell><![CDATA["+codeName+"]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			//xmlBuf.append("<cell><![CDATA["+def.getName()+"]]></cell>");
			xmlBuf.append("<cell><![CDATA[<a href=javascript:updateActivityDefinition('" + oid+ "')>" + def.getName() + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA["+ChangeUtil.getActivityName(def.getActiveType()) +"]]></cell>");
			xmlBuf.append("<cell><![CDATA["+activeUserDepart+"]]></cell>");
			xmlBuf.append("<cell><![CDATA["+activeUser+"]]></cell>");
			xmlBuf.append("<cell><![CDATA["+def.getSortNumber()+"]]></cell>");
			xmlBuf.append("</row>");
		}
		xmlBuf.append("</rows>");
		
		
		
		result.put("xmlString"      , xmlBuf);
		
		return result;
	}
	
	
	@Override
	public String deleteRootDefinition(String oid)throws Exception{

		Transaction trx = new Transaction();
		try{
			trx.start();
			//System.out.println("ECAHELPER deleteRootDefinition oid =" +oid);
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				EChangeActivityDefinitionRoot root = (EChangeActivityDefinitionRoot) f.getReference(oid).getObject();
				
				long longOid = CommonUtil.getOIDLongValue(oid);
				List<EChangeActivityDefinition> list = ECAHelper.service.getActiveDefinition(longOid);
				for(EChangeActivityDefinition activity : list ){
					
					PersistenceHelper.manager.delete(activity);
				}
				
				PersistenceHelper.manager.delete(root);
			}
			trx.commit();
			trx = null;
		}catch(Exception e){
			throw e;
		}finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return Message.get("삭제 되었습니다.");
	}
	
	//deleteActivityDefinition
	
	@Override
	public String deleteActivityDefinition(String oids)throws Exception{

		Transaction trx = new Transaction();
		try{
			trx.start();
			oids = StringUtil.checkNull(oids);
			if(oids.length()==0){
				return "삭제 대상이 없습니다.";
			}
			String[] oidList = oids.split(",");
			
			for(int i = 0 ; i < oidList.length ; i++){
			
				ReferenceFactory f = new ReferenceFactory();
				EChangeActivityDefinition def = (EChangeActivityDefinition)CommonUtil.getObject(oidList[i]);
				PersistenceHelper.manager.delete(def);
			}
			trx.commit();
			trx = null;
		}catch(Exception e){
			throw e;
		}finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return Message.get("삭제 되었습니다.");
	}
	
	@Override
	public List<EADData> setActiveDefinition(String oid) throws Exception{
		long rootOid = CommonUtil.getOIDLongValue(oid);
		List<EChangeActivityDefinition> list = getActiveDefinition(rootOid);
		List<EADData> ecaList = new ArrayList<EADData>();
		for(EChangeActivityDefinition ead : list){
			
			EADData ecaData = new EADData(ead);
			
			
			NumberCode code =NumberCodeHelper.service.getNumberCode("EOSTEP", ecaData.step);
			String stepName ="";
			if(code != null){
				stepName = code.getName();
			}
			
			ecaData.setStepName(stepName);
			ecaData.setStepSort(code.getSort());
			String departName = "";
			if(ead.getActiveUser() !=null){
				
				People pp = UserHelper.service.getPeople(ead.getActiveUser());
				if(pp != null && pp.getDepartment() != null){
					departName = pp.getDepartment().getName();
				}
			}
			ecaData.setDepartName(departName);
			ecaList.add(ecaData);
			
			//ecaData.getSt
		}
		return ecaList;
	}
	
	/**
	 * ECO,EO ECA 생성
	 * @param req
	 * @param eco
	 * @throws Exception
	 */
	@Override
	public boolean createActivity(HttpServletRequest req,ECOChange eo) throws Exception{
		
		boolean isActivity = false;
		getParmater(req);
		String cmd = req.getParameter("cmd");
		boolean isUpdate = cmd.equals("update");
		String[] steps =req.getParameterValues("step");
		String[] active_names =req.getParameterValues("active_name");
		String[] active_types =req.getParameterValues("active_type");
		String[] activeUsers =req.getParameterValues("activeUser");
		String[] finishDates =req.getParameterValues("finishDate");
		String[] ecaOids = req.getParameterValues("ecaOid");
		
		
		//System.out.println("cmd =" + cmd + ":"+isUpdate);
		
		List<EChangeActivity> ecalist = new ArrayList<EChangeActivity>();
		HashMap<String, EChangeActivity> ecaMap = new HashMap<String, EChangeActivity>();
		if(isUpdate){
			ecalist = ECAHelper.service.getECAList(eo);
			ecaMap = new HashMap<String, EChangeActivity>();
			for(EChangeActivity activity : ecalist){
				String activityOid =CommonUtil.getOIDString(activity);
				ecaMap.put(activityOid, activity);
				
			}
		}
		if(steps != null){
			WTUser sessionUser  = (WTUser) SessionHelper.manager.getPrincipal();
			for(int i = 0; i < steps.length ; i++){
				boolean isModify =false; 
				String ecaOid = ecaOids[i];
				EChangeActivity eca = null;
				WTUser beforeUser = null;
				boolean isCreateECA = true;
				WTUser user  = (WTUser)CommonUtil.getObject(activeUsers[i]);
				if(!ecaMap.containsKey(ecaOid)){
					
					eca = EChangeActivity.newEChangeActivity();
					isActivity = true;
				}else{
					
					eca = ecaMap.get(ecaOid);
					if(eca.getLifeCycleState().toString().equals("COMPLETED")) continue;
					isActivity = true;
					beforeUser = eca.getActiveUser();
					ecaMap.remove(ecaOid);
					isCreateECA = false;
					
					
					SessionHelper.manager.setAdministrator();
					// WFItemUserLink 재설정
					QuerySpec query = new QuerySpec();
					Class target = WorkItem.class;
					int idx = query.addClassList(target, false);
					
					query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.classname"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "primaryBusinessObject.key.classname"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "source.key.classname"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "source.key.id"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "thePersistInfo.createStamp"), new int[] { idx }, false);
					query.appendSelect(new ClassAttribute(target, "taskURLPathInfo"), new int[] { idx }, false);
					
					query.appendOrderBy(new OrderBy(new ClassAttribute(target,"thePersistInfo.createStamp"), true), new int[] { idx });   
					query.appendWhere(new SearchCondition(target, "status", "=", "POTENTIAL"), new int[] { idx });
					if(query.getConditionCount()>0) query.appendAnd();
					query.appendWhere(new SearchCondition(target, "primaryBusinessObject.key.classname", SearchCondition.LIKE, "%"+ecaOid+"%"), new int[] { idx });
					//"primaryBusinessObject.key.classname"
					//System.out.println("query="+query);
					QueryResult qr = PersistenceHelper.manager.find(query); 
					Object obj[] = null;
					
					ReferenceFactory rf = new ReferenceFactory();
					LifeCycleManaged pbo = null;
					String oid = "";
					WorkItem workitem = null;
					//System.out.println("qr.size()="+qr.size());
					while(qr.hasMoreElements()){
						
						obj = (Object[])qr.nextElement();
						oid = obj[0]+":"+obj[1];
						
						WorkItem item = (WorkItem) rf.getReference(oid).getObject();
						try{
							pbo = (LifeCycleManaged)item.getPrimaryBusinessObject().getObject();
						}catch(Exception e){
							e.printStackTrace();
						}
						
						if(pbo instanceof EChangeActivity){
							EChangeActivity pboeca = (EChangeActivity)pbo;
							String pboOid = CommonUtil.getOIDString(pboeca);
							//System.out.println("pboOid  "+ pboOid + "\tecaOid   "+ecaOid);
							if(pboOid.equals(ecaOid)){
								workitem = item;
								break;
							}
						}
					}
					
					//System.out.println("null != workitem   "+ (null != workitem));
					if (null != workitem) {
					WTUser oldUser = (WTUser) workitem.getOwnership().getOwner().getPrincipal();
					E3PSWorkflowHelper.service.reassignWorkItem(workitem, user);
					WTObject wtobject = (WTObject) WorklistHelper.service.getPBO(workitem);
					// Role에 할당된 사용자도 수정
					String assignRoleName = "ASSIGNEE";
					//System.out.println("assignrolename = " + assignRoleName);
					//System.out.println("wtobject = " + wtobject);

						/* ECA 담당자 변경 */
						if (wtobject instanceof EChangeActivity) {
							isModify = true;
						}
						WFItem wfitem = WFItemHelper.service.getWFItem(wtobject);
						//System.out.println("null != wfitem   "+ (null != wfitem));
						if (wfitem != null) {

							WfActivity activity = (WfActivity) workitem.getSource().getObject();
							String activityName = WFItemHelper.service.getWFItemActivityName(activity.getName());
							WFItemUserLink link = WFItemHelper.service.getOwnerApplineLink(oldUser, wfitem, activityName);
							//System.out.println("null != link   "+ (null != link));
							if (link != null) {

								// 기존객체 수정
								link.setState("위임");
								String comment = "'" + oldUser.getFullName() + "'로 부터 '" + user.getFullName() + "'으로 업무 위임";
								link.setComment(comment);
								link.setProcessDate(new java.sql.Timestamp(new java.util.Date().getTime()));
								int order = link.getProcessOrder();
								PersistenceHelper.manager.save(link);
								int seq = link.getSeqNo();

								// 새로운 객체 생성.
								WFItemUserLink newlink = WFItemUserLink.newWFItemUserLink(user, wfitem);
								PeopleData pData = new PeopleData(user);
								newlink.setDepartmentName(pData.departmentName);
								newlink.setActivityName(link.getActivityName());
								newlink.setProcessOrder(order);
								newlink.setDisabled(false);
								newlink.setSeqNo(seq);

								PersistenceHelper.manager.save(newlink);
							}

						}
					}
				}
				
				
				eca.setStep(steps[i]);
				eca.setName(active_names[i]);
				eca.setActiveType(active_types[i]);
				
				//System.out.println("user =" + user);
				eca.setActiveUser(user);
				eca.setFinishDate(DateUtil.convertDate(finishDates[i]));
				eca.setSortNumber(i);
				eca.setEo(eo);
				
				String location="/Default/설계변경/ECA";
		    	String lifecycle="LC_ECA_PROCESS";
		    	
		    	//분류체계,lifecycle 셋팅
		    	if(isCreateECA){
		    		CommonHelper.service.setManagedDefaultSetting(eca, location, lifecycle);
		    	}
		    	
		    	if(isModify) eca = (EChangeActivity)PersistenceHelper.manager.modify(eca);
		    	else eca = (EChangeActivity)PersistenceHelper.manager.save(eca);
		    	
		    	String state = eca.getLifeCycleState().toString();
		    	boolean isWorking = state.equals("INWORK");
		    //	System.out.println("isWorking="+isWorking);
		    	if(isWorking){
		    		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca, State.toState("INTAKE"), true);
		    	}
		    	//SessionHelper.manager.setAdministrator();
		    	
		    	if(isCreateECA){
		    		WorkProcessHelper.service.addPrincialToRole(eca, "ASSIGNEE", user.getName());
			    	
		    	}else{
		    		//System.out.println("!beforeUser.equals(user)="+!beforeUser.equals(user));
		    		if(!beforeUser.equals(user) ){
		    			workingUserChange(eca, beforeUser, user);
		    		}
		    	}
		    	
		    	//SessionHelper.manager.setPrincipal(eo.getCreatorName());
		    	//접수중 변경 프로세스 종료
		    	//LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca, State.toState("INTAKE"));
		    	SessionHelper.manager.setPrincipal(sessionUser.getName());
			}
		}
		
		
		if(isUpdate){
			deleteActivity(ecaMap);
		}
		//SessionHelper.manager.setAdministrator();
		stateChange(eo);
		//SessionHelper.manager.setPrincipal(eo.getCreatorName());
		return isActivity;
	}
	
	@Override
	public List<EChangeActivity> getECAList(ECOChange eo) throws Exception{
		
		List<EChangeActivity> list = new ArrayList<EChangeActivity>();
		
		ClassAttribute classattribute1 = null;
        ClassAttribute classattribute2 = null;
        ClassAttribute classattribute3 = null;
		SearchCondition sc = null;
		
		long longOid = CommonUtil.getOIDLongValue(eo);
		QuerySpec qs = new QuerySpec();
		
		Class cls1 = EChangeActivity.class;
		Class cls2 = NumberCode.class;
		
		int idx1 = qs.appendClassList(cls1, true);
		int idx2 = qs.addClassList(cls2, false);
		
		//Join 
		classattribute1 = new ClassAttribute(cls1,"step" );
	    classattribute2= new ClassAttribute(cls2, "code");
		sc = new SearchCondition(classattribute1, "=", classattribute2);
		sc.setFromIndicies(new int[] {idx1, idx2}, 0);
        sc.setOuterJoin(0);
        qs.appendWhere(sc, new int[] {idx1, idx2});
		
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(cls1,"eoReference.key.id",SearchCondition.EQUAL,longOid),new int[] {idx1});
        
        qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls2, "sort"), false),
				new int[] { idx2 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				cls1, "sortNumber"), false),
				new int[] { idx1 });
//        System.out.println(qs);
        QueryResult result = PersistenceHelper.manager.find(qs);
		
        while(result.hasMoreElements()){
        	
        	Object[] resultObj = (Object[])result.nextElement();
        	EChangeActivity eca = (EChangeActivity)resultObj[0];
        	
        	
        	list.add(eca);
        }
		return list;
		
	}
	
	@Override
	public List<ECAData> include_ecaList(String oid) throws Exception {
		List<ECAData> list = new ArrayList<ECAData>();
		if (oid.length() > 0) {
			ECOChange eo = (ECOChange)CommonUtil.getObject(oid);
			List<EChangeActivity> ecalist=getECAList(eo);
			
			for(EChangeActivity eca :ecalist ){
				
				ECAData data = new ECAData(eca);
				ImageIcon icon = ChangeUtil.getECAStateImg(eca.getFinishDate(), data.state);
				data.setIcon(icon);
				list.add(data);
			}
		}
		return list;
		
	}
	
	
	
	/**
	 * ECA 완료시 Next ECA
	 * @param activity
	 * @return
	 * @throws Exception
	 */
	
	@Override
	public List<EChangeActivity> getNextActivity(EChangeActivity eca) throws Exception {
			
		List<EChangeActivity> list = new ArrayList<EChangeActivity>();
		QuerySpec qs = new QuerySpec();
		int jj = qs.addClassList(EChangeActivity.class, true);

		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"eoReference.key.id", "=", eca.getEo()
						.getPersistInfo().getObjectIdentifier().getId()),
				new int[] { jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"sortNumber", ">", eca.getSortNumber()),
				new int[] { jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"step", "<>", eca.getStep()),
				new int[] { jj });

		qs.appendOrderBy(new OrderBy(new ClassAttribute(
				EChangeActivity.class, "sortNumber"), false),
				new int[] { jj });

		QueryResult result = PersistenceHelper.manager.find(qs);

//		System.out.println(qs.toString());
		String firstStep = null;
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivity ed = (EChangeActivity) o[0];
			String step = ed.getStep();
			if (firstStep == null) {
				firstStep = step;
			}
			if (step.equals(firstStep)) {
				list.add(ed);
			} else {
				break;
			}
		}
		return list;
	}
	
	@Override
	public boolean isStepComplete(EChangeActivity eca) throws Exception {
		
		List<EChangeActivity> list = new ArrayList<EChangeActivity>();
		QuerySpec qs = new QuerySpec();
		int jj = qs.addClassList(EChangeActivity.class, true);

		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"eoReference.key.id", "=", eca.getEo()
						.getPersistInfo().getObjectIdentifier().getId()),
				new int[] { jj });
		
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"step", "=", eca.getStep()),
				new int[] { jj });
		
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				EChangeActivity.LIFE_CYCLE_STATE, "<>", "COMPLETED"),
				new int[] { jj });
		
		QueryResult result = PersistenceHelper.manager.find(qs);
		
		return result.size() == 0 ? true : false;
		
	}
	
	/**
	 * ECA 산출물 링크 리스트  DocumentActivityLink
	 * @param eca
	 * @return
	 */
	@Override
	public List<DocumentActivityLink> getECADocumentLink(EChangeActivity eca){
		
		List<DocumentActivityLink> list = new ArrayList<DocumentActivityLink>();
		try{
			QueryResult rt = PersistenceHelper.manager.navigate(eca, "doc",DocumentActivityLink.class,false);
			
			while(rt.hasMoreElements()){
				DocumentActivityLink link = (DocumentActivityLink)rt.nextElement();
				list.add(link);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * ECA 산출물 리스트 WTDocument
	 * @param eca
	 * @return
	 */
	@Override
	public List<WTDocument> getECADocument(EChangeActivity eca){
		
		List<WTDocument> list = new ArrayList<WTDocument>();
		try{
			QueryResult rt = PersistenceHelper.manager.navigate(eca, "doc",DocumentActivityLink.class);
			
			while(rt.hasMoreElements()){
				WTDocumentMaster master = (WTDocumentMaster)rt.nextElement();
				
				WTDocument doc =DocumentHelper.service.getLastDocument(master.getNumber());
				
				if(doc != null){
					list.add(doc);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * ECA 산출물 리스트 DocumentData
	 * @param eca
	 * @return
	 */
	@Override
	public List<DocumentData> getECADocumentData(EChangeActivity eca){
		
		List<DocumentData> list = new ArrayList<DocumentData>();
		try{
			QueryResult rt = PersistenceHelper.manager.navigate(eca, "doc",DocumentActivityLink.class);
			
			while(rt.hasMoreElements()){
				WTDocumentMaster master = (WTDocumentMaster)rt.nextElement();
				
				WTDocument doc =DocumentHelper.service.getLastDocument(master.getNumber());
				
				if(doc != null){
					DocumentData data = new DocumentData(doc);
					list.add(data);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * ECA 산출물 등록
	 * @param eca
	 * @return
	 */
	@Override
	public void createDocumentActivityLink(WTDocument doc ,String parentOid  ) throws Exception {
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(parentOid);
		DocumentActivityLink link = DocumentActivityLink.newDocumentActivityLink((WTDocumentMaster)doc.getMaster(), eca);
		PersistenceHelper.manager.save(link);
	}
	
	/**
	 * ECA 상세 보기
	 */
	@Override
	public List<Map<String,Object>> viewECA(ECOChange eo) throws Exception {
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		List<EChangeActivity> ecaList = ECAHelper.service.getECAList(eo);
		
		for(EChangeActivity eca : ecaList) {
			ECAData ecaData = new ECAData(eca);
			
			List<DocumentData> docList = new ArrayList<DocumentData>();
			
			if(eca.getActiveType().equals("DOCUMENT")){
				docList = ECAHelper.service.getECADocumentData(eca);
			}
//			System.out.println("docList =" + docList.size());
			ecaData.setDocList(docList);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("ecaData", ecaData);
			map.put("isActTypeDoc", eca.getActiveType().equals("DOCUMENT"));
			//map.put("docList", docList);
			list.add(map);
		}
		
		return list;
	}
	
	/**
	 * ECA 첨부파일 등록,삭제
	 * @param hash
	 * @throws Exception 
	 */
	@Override
	public boolean ecaAttachFile(Hashtable hash) throws Exception{
		
		boolean isAttach = false;
		Transaction trx = new Transaction();
		try{
			trx.start();
			String oid = (String)hash.get("oid");
			String[] files = (String[])hash.get("files");
			String[] secondaryDelFile = (String[])hash.get("secondaryDelFile");
			
			EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(oid);
			ContentHolder ch= (ContentHolder)eca;
			//if(eca.getActiveCode().equals(ECAHelper.ECACode.ECN_CHECK_APPLY)){
			//	ch =(ContentHolder)eca.getEo();
			//}
			
			
			//첨부
			CommonContentHelper.service.attach(ch, null, files, secondaryDelFile, false);
			
			
	  		isAttach = true;
	  		trx.commit();
			trx = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
	           if(trx!=null){
					trx.rollback();
			   }
	    }
		
		
		return isAttach;
		
		
	}
	
	/**
	 * EO,ECO 완료시 자동 생성
	 * EO,ECO 승인 완료후 추가 산출물 등록
	 */
	@Override
	 public void createAutoActivity(EChangeOrder eco) throws Exception {
		 
		EChangeActivity eca = EChangeActivity.newEChangeActivity();
		List<EChangeActivity> list = getECAList(eco);
		String step = getECAMaxStep(eco);
		String name = eco.getEoNumber() +"의 승인이후 산출물";
		
		boolean isActivity = getEOActivity(eco, name);
		
		if(isActivity) return;
		
		String activeType = "DOCUMENT";
		Timestamp finishDate = eco.getModifyTimestamp();
		int idx = getECAMaxSortNumber(eco);
		WTUser user = (WTUser)SessionHelper.getPrincipal();
		String state = "COMPLETED";
		eca.setStep(step);
		eca.setName(name);
		eca.setActiveType(activeType);
		
		eca.setActiveUser(user);
		eca.setFinishDate(finishDate);
		eca.setSortNumber(idx);
		eca.setEo(eco);
		
		String location="/Default/설계변경/ECA";
	   	String lifecycle="LC_ECA_PROCESS";
	   	
	   	//분류체계,lifecycle 셋팅
	   	CommonHelper.service.setManagedDefaultSetting(eca, location, lifecycle);
	   	
	   	eca = (EChangeActivity)PersistenceHelper.manager.save(eca);
	   	
	   	LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca, State.toState(state));
		 
	 }
	
	/**
	 * EO,ECO 완료시 자동 생성을 위한 MaxStep
	 * 
	 */
	@Override
	public String getECAMaxStep(ECOChange eo) throws Exception {
		String number ="";
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(EChangeActivity.class, false);
		
		 SQLFunction maxFunction = SQLFunction.newSQLFunction(
		 SQLFunction.MAXIMUM, new ClassAttribute( EChangeActivity.class, EChangeActivity.STEP ));
		
		qs.appendSelect( maxFunction, idx, false );
		qs.setAdvancedQueryEnabled( true );
		qs.setDescendantQuery(false);

		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"eoReference.key.id", "=", eo.getPersistInfo().getObjectIdentifier().getId()),
				new int[] { idx });
		
//		System.out.println(qs);
		QueryResult rt = PersistenceServerHelper.manager.query(qs);//PersistenceHelper.manager.find(qs);
		
		//System.out.println(qs.toString());
		if(rt.size() ==0 ){
			number ="ES001";
		}else{
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				
				number = (String) obj[0];
				
				
			}
		}
		
		if(number == null){
			number ="ES001";
		}
		
		return number;
		
	}
	
	/**
	 * EO,ECO 완료시 자동 생성을 위한 Max SortNumber
	 * 
	 */
	@Override
	public int getECAMaxSortNumber(ECOChange eo) throws Exception {
		int number = 0;
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(EChangeActivity.class, false);
		
		 SQLFunction maxFunction = SQLFunction.newSQLFunction(
		 SQLFunction.MAXIMUM, new ClassAttribute( EChangeActivity.class, EChangeActivity.SORT_NUMBER ));
		
		qs.appendSelect( maxFunction, idx, false );
		qs.setAdvancedQueryEnabled( true );
		qs.setDescendantQuery(false);

		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"eoReference.key.id", "=", eo.getPersistInfo().getObjectIdentifier().getId()),
				new int[] { idx });
		
//		System.out.println(qs);
		QueryResult rt = PersistenceServerHelper.manager.query(qs);//PersistenceHelper.manager.find(qs);
		
		//System.out.println(qs.toString());
		if(rt.size()==0){
			number = 0;
		}else{
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				BigDecimal aa = (BigDecimal)obj[0];
				if(aa == null){
					number = -1;
				}else{
					number =  aa.intValue();
				}
				
			}
			number = number+1;
		}
		return number;
		
	}
	
	/**
	 * EO,ECO 완료시 자동 생성을 위해 동일 이름 존해 유무 체크
	 * 
	 */
	@Override
	public boolean getEOActivity(ECOChange eo,String activityName) throws Exception {
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(EChangeActivity.class, true);
		
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				"eoReference.key.id", "=", eo.getPersistInfo().getObjectIdentifier().getId()),
				new int[] { idx });
		qs.appendAnd();
		
		qs.appendWhere(new SearchCondition(EChangeActivity.class,
				EChangeActivity.NAME, "=", activityName,false),
				new int[] { idx });
		
//		System.out.println(qs.toString());
		QueryResult rt = PersistenceHelper.manager.find(qs);
		
		return rt.size()>0 ? true : false;
		
	}
	
	@Override
	public List<ECAData> getEOActivity(String oid) throws Exception{
		ECOChange eo = (ECOChange)CommonUtil.getObject(oid);
		List<EChangeActivity> list = getECAList(eo);
		List<ECAData> ecaList = new ArrayList<ECAData>();
		for(EChangeActivity eca : list){
			
			
			ECAData ecaData = new ECAData(eca);
			//if(ecaData.state.equals("COMPLETED")) continue;
			NumberCode code =NumberCodeHelper.service.getNumberCode("EOSTEP", ecaData.step);
			String stepName ="";
			if(code != null){
				stepName = code.getName();
			}
			
			ecaData.setStepName(stepName);
			ecaData.setStepSort(code.getSort());
			String departName = "";
			if(eca.getActiveUser() !=null){
				
				People pp = UserHelper.service.getPeople(eca.getActiveUser());
				if(pp != null && pp.getDepartment() != null){
					departName = pp.getDepartment().getName();
				}
			}
			ecaData.setDepartName(departName);
			
			ecaList.add(ecaData);
			
			
			//ecaData.getSt
		}
		return ecaList;
	}
	
	/**
	 * 진행중인 ECA에 대해 유저 변경시 
	 * @param wtobject
	 * @param currentUser
	 * @param newUser
	 * @throws WTException 
	 */
	private void _workingUserChange(EChangeActivity eca,WTUser currentUser,WTUser newUser) throws Exception {
			/*
			WTProperties prover = WTProperties.getServerProperties();
			String server = prover.getProperty("wt.cache.master.codebase");
			
			if (server == null) {
				server = prover.getProperty("wt.server.codebase");
			}
			Object obj = RemoteMethodServer.getInstance(new URL(server + "/"),
					"BackgroundMethodServer").invoke("_workingUserChange",
					SchedulingMethod.class.getName(), null, null, null);
		
			
			System.out.println(eca.getName()+ " Forground ressign = "+ currentUser.getFullName() +","+newUser.getFullName() );
			String assignRoleName =  "ASSIGNEE";
			//WorkItem workItem= getWorkItem(eca);
			//E3PSWorkflowHelper.service.reassignWorkItem(workItem, newUser);
			/*
			E3PSWorkflowHelper.service.deleteParticipant( (LifeCycleManaged) eca, assignRoleName, currentUser.getName());
			E3PSWorkflowHelper.service.addParticipant( (LifeCycleManaged) eca, assignRoleName, newUser.getName());
			*/
	}
	
	private void workingUserChange(EChangeActivity eca,WTUser currentUser,WTUser newUser) throws Exception {
		/*
		WTProperties prover = WTProperties.getServerProperties();
		String server = prover.getProperty("wt.cache.master.codebase");
		
		if (server == null) {
			server = prover.getProperty("wt.server.codebase");
		}
		Object obj = RemoteMethodServer.getInstance(new URL(server + "/"),
				"BackgroundMethodServer").invoke("_workingUserChange",
				SchedulingMethod.class.getName(), null, null, null);
		*/
		//Session
		
		
		//SessionHelper.manager.setAdministrator();
		//SessionHelper.manager.setPrincipal(eca.getEo().getCreatorName());
//		System.out.println(eca.getName()+ "Background  ressign = "+ currentUser.getFullName() +","+newUser.getFullName() );
		String assignRoleName =  "ASSIGNEE";
		//WorkItem workItem= getWorkItem(eca);
		//E3PSWorkflowHelper.service.reassignWorkItem(workItem, newUser);
		E3PSWorkflowHelper.service.deleteParticipant( (LifeCycleManaged) eca, assignRoleName, currentUser.getName());
		E3PSWorkflowHelper.service.addParticipant( (LifeCycleManaged) eca, assignRoleName, newUser.getName());
		//SessionHelper.manager.setPrincipal(eca.getEo().getCreatorName());
	}
	
	private void getParmater(HttpServletRequest req){
		
		Iterator<String> it=req.getParameterMap().keySet().iterator();
		
		while(it.hasNext()){
			String key = it.next();
			
			System.out.println("key =" + key);
			
		}
	}
	
	private WorkItem getWorkItem(EChangeActivity eca){
		WorkItem workItem = null;
		try{
			String oid = CommonUtil.getOIDString(eca);
			oid = "OR:"+oid;
			QuerySpec qs = new QuerySpec();
			Class cls =WorkItem.class;
			int idx = qs.appendClassList(cls, true);
			
			qs.appendWhere(new SearchCondition(cls, "primaryBusinessObject.key.classname", SearchCondition.EQUAL, oid), new int[] {idx});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, WorkItem.STATUS, SearchCondition.EQUAL, "POTENTIAL"), new int[] {idx});
			
			QueryResult rt =PersistenceHelper.manager.find(qs);
//			System.out.println(qs.toString());
//			System.out.println("rt.size() =" +rt.size());
			while(rt.hasMoreElements()){
				Object[] obj = (Object[])rt.nextElement();
				workItem = (WorkItem)obj[0];
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return workItem;
		
	}
	
	/**
	 * ECA 삭제
	 * @param ecaMap
	 * @throws Exception
	 */
	private void deleteActivity(HashMap<String,EChangeActivity> ecaMap) throws Exception{
//		System.out.println("=== deleteActivity === " );
		Iterator<String> it = ecaMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			EChangeActivity deleteEca = ecaMap.get(key);
			
			String deleteState = deleteEca.getLifeCycleState().toString();
			
			if(deleteState.equals("COMPLETED")){
				continue;
			}
			
			WFItemHelper.service.deleteWFItem(deleteEca);
			PersistenceHelper.manager.delete(deleteEca);
			
		}
	}
	
	private void stateChange(ECOChange eo) throws Exception{
		//System.out.println("=== stateChange === " );
		List<EChangeActivity> list = ECAHelper.service.getECAList(eo);
		
		boolean isInwork = false;
		List<EChangeActivity> inTaskList =new ArrayList<EChangeActivity>() ;//INTAKE
		//System.out.println(list +"," + list.size());
//		System.out.println("stateChange eo state = " +eo.getLifeCycleState().toString()); //결재선 지정
		
		// ECA 작업중이 없을시 다음 Step Activity 작업중 변경
		int idx=0;
		String inTaskStep ="";
	
		for(EChangeActivity eca :  list){
			
			String state = eca.getLifeCycleState().toString();
			String step = eca.getStep();
			if(state.equals("COMPLETED")) continue;
			
			if(idx == 0){
				inTaskStep = step;
			}
			
			if(inTaskStep.equals(step)){
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eca, State.toState("INWORK"));
				isInwork = true;
			}
			
			idx++;
		}
		//}
		
		// ECA 작업중이 존재 하지 않을시 ECO 승인 요청
		
		boolean isAPPROVE_REQUEST = eo.getLifeCycleState().toString().equals("APPROVE_REQUEST"); //결재선 지정
//		System.out.println("ECO  상태값 변경 1 stateChange : " + eo.getEoNumber() +":" + isInwork +":eo isAPPROVE_REQUEST = " + isAPPROVE_REQUEST);
		if(!isInwork){
			if(!isAPPROVE_REQUEST){
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eo, State.toState("APPROVE_REQUEST"),true);
			}else{
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eo, State.toState("ACTIVITY"),true);
			}
			
		}else{
			if(isAPPROVE_REQUEST){ //결재선 지정 상태 이면 WF 종료
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eo, State.toState("ACTIVITY"),true);
			}else{
				if(eo.getLifeCycleState().toString().equals("INWORK")){
					
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eo, State.toState("ACTIVITY"),true);
				}else{
//					System.out.println("APPROVE_REQUEST State Change 소스 주석");
					//LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eo, State.toState("APPROVE_REQUEST"),true);
				}
			}
			
				
			
			
		}
		
	}
	
	
	@Override
	public ResultData modifyECAction(HttpServletRequest req)throws Exception{
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
       
       try {
    	    trx.start();
    	    
    	    String oid = StringUtil.checkNull(req.getParameter("oid"));
    	    String comments = StringUtil.checkNull(req.getParameter("comments"));
			String[] secondarys = req.getParameterValues("SECONDARY");
			String[] sdeloids	= (String[]) req.getParameterValues("delocIds");
			EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(oid);
			eca.setComments(comments);
			eca = (EChangeActivity)PersistenceHelper.manager.modify(eca);
	    	
	    	
//	    	System.out.println("secondarys =" + secondarys);
//	    	System.out.println("sdeloids =" + sdeloids);
	    	
	    	//첨부
			CommonContentHelper.service.attach(eca, null, secondarys, sdeloids, false);
			//설계변경 부품 내역파일
	    	String ecoFile = req.getParameter("ECO");
		    
		
			
			trx.commit();
			trx = null;
    		result.setResult(true);
			result.setOid(CommonUtil.getOIDString(eca));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
//			System.out.println("ECA Action Error : "+result.getMessage());
        } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return result;    
		
	}

	@Override
	public List<Map<String, Object>> viewECA_Doc(ECOChange eo) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		List<EChangeActivity> ecaList = ECAHelper.service.getECAList(eo);
		
		for(EChangeActivity eca : ecaList) {
			if(eca.getActiveType().equals("DOCUMENT")){
				
				ECAData ecaData = new ECAData(eca);
				List<DocumentData> docList = new ArrayList<DocumentData>();

				docList = ECAHelper.service.getECADocumentData(eca);
				
//				System.out.println("docList =" + docList.size());
				ecaData.setDocList(docList);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ecaData", ecaData);
				//map.put("docList", docList);
				list.add(map);
				
			}
		}
		
		return list;
	}
}
