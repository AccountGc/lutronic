package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.beans.EOData;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentQueryHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.org.People;

import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

@SuppressWarnings("serial")
public class StandardECRService extends StandardManager implements ECRService {

	public static StandardECRService newStandardECRService() throws Exception {
		final StandardECRService instance = new StandardECRService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public ArrayList getWorkingECAUser(EChangeOrder eco){
		ArrayList list = new ArrayList();
		try{
			QuerySpec qs = new QuerySpec();
			int idx = qs.addClassList(EChangeActivity.class, true);
		    qs.appendWhere(new SearchCondition(EChangeActivity.class,"eoReference.key.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(eco)),new int[] {idx});
		    qs.appendAnd();
		    qs.appendWhere(new SearchCondition(EChangeActivity.class,EChangeActivity.LIFE_CYCLE_STATE,SearchCondition.EQUAL,"INWORK"),new int[] {idx});
		    
		    QueryResult rt = PersistenceHelper.manager.find(qs);
		    while(rt.hasMoreElements()){
		    	Object[] obj = (Object[])rt.nextElement();
		    	EChangeActivity eca = (EChangeActivity)obj[0];
		    	list.add(eca.getActiveUser());
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Last ECA Search
	 * @param eo
	 * @return
	 */
	@Override
	public EChangeActivity getLastStepECA(ECOChange eo){
		
		Vector<EChangeActivity> ecaList = new Vector<EChangeActivity>();
		try{
			ClassAttribute classattribute = null;
            ClassAttribute classattribute2 = null;
            ClassAttribute classattribute3 = null;
			SearchCondition sc = null;
			
			long longOid = CommonUtil.getOIDLongValue(eo);
			QuerySpec qs = new QuerySpec();
			
			Class active = EChangeActivity.class;
			Class define = EChangeActivityDefinition.class;
			
			int activeIdx = qs.appendClassList(active, true);
			int defineIdx = qs.appendClassList(define, false);
			
			classattribute2 = new ClassAttribute(active,"definitionReference.key.id" );
		    classattribute3= new ClassAttribute(define, "thePersistInfo.theObjectIdentifier.id");
			sc = new SearchCondition(classattribute2, "=", classattribute3);
			sc.setFromIndicies(new int[] {activeIdx, defineIdx}, 0);
            sc.setOuterJoin(0);
            qs.appendWhere(sc, new int[] {activeIdx, defineIdx});
			
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EChangeActivity.class,"eoReference.key.id",SearchCondition.EQUAL,longOid),new int[] {activeIdx});
            
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class,EChangeActivityDefinition.STEP,SearchCondition.EQUAL,"STEP1"),new int[] {defineIdx});
        	qs.appendOrderBy(new OrderBy(new ClassAttribute(define,"step"), true), new int[] { defineIdx });
            QueryResult rt = PersistenceHelper.manager.find(qs);
			
            while(rt.hasMoreElements()){
            	
            	Object[] obj = (Object[])rt.nextElement();
            	EChangeActivity nextECA = (EChangeActivity)obj[0];
            	return nextECA;
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	
	}

	@Override
	public ResultData createECRAction(HttpServletRequest req) {
		
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
	   
	    try{
	    	
	    	trx.start();
			
			String name = StringUtil.checkNull(req.getParameter("name"));
			String number = StringUtil.checkNull(req.getParameter("number"));
			String createDate = StringUtil.checkNull(req.getParameter("createDate"));
			String approveDate = StringUtil.checkNull(req.getParameter("approveDate"));
			
			String createDepart = StringUtil.checkNull(req.getParameter("createDepart"));
			String writer = StringUtil.checkNull(req.getParameter("writer"));
			
			String[] models = req.getParameterValues("model");
			
			String proposer = StringUtil.checkNull(req.getParameter("proposer"));
			String[] changeSections = req.getParameterValues("changeSection");
			String eoCommentA = StringUtil.checkNull(req.getParameter("eoCommentA"));
			String eoCommentB = StringUtil.checkNull(req.getParameter("eoCommentB"));
			String eoCommentC = StringUtil.checkNull(req.getParameter("eoCommentC"));
			String[] secondarys = req.getParameterValues("SECONDARY");
			
			
			String model = ChangeUtil.getArrayList(models);
			String changeSection = ChangeUtil.getArrayList(changeSections);
			
			//System.out.println("=======writer =" + writer);
			//System.out.println("=======proposer =" + proposer);
			//System.out.println("=======changeSections =" + changeSections);
			//System.out.println("=======approveDate =" + approveDate);
			//String number = "ECR-" +DateUtil.getCurrentDateString("year")+"-"+DateUtil.getCurrentDateString("month")+ "-";
			//String seqNo = SequenceDao.manager.getSeqNo(number, "00000", "EChangeRequest", EChangeRequest.EO_NUMBER);
			
			//number = number + seqNo;
			
			
			
			
			EChangeRequest ecr = null;
			ecr = EChangeRequest.newEChangeRequest();
			ecr.setEoName(name);
			ecr.setEoNumber(number);
			
			ecr.setCreateDate(createDate);
			ecr.setWriter(writer);
			ecr.setApproveDate(approveDate);
			ecr.setCreateDepart(createDepart);
			ecr.setModel(model);
			ecr.setProposer(proposer);
			ecr.setChangeSection(changeSection);
			ecr.setEoCommentA(eoCommentA);
			ecr.setEoCommentB(eoCommentB);
			ecr.setEoCommentC(eoCommentC);
			
			
			
			
			// ECR 분류쳬게 설정
			PDMLinkProduct product = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
			Folder folder = FolderTaskLogic.getFolder("/Default/설계변경/ECR", WCUtil.getWTContainerRef());
	        
			FolderHelper.assignLocation((FolderEntry)ecr, folder);
	        
	        // ECR Container 설정
	        ecr.setContainer(product);
	        
	        // ECR lifeCycle 설정
	        LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", wtContainerRef);
	        LifeCycleHelper.setLifeCycle(ecr, tmpLifeCycle); //Lifecycle
						
			ecr=(EChangeRequest) PersistenceHelper.manager.save(ecr);
			String[] ecrOids 	= (String[]) req.getParameterValues("ecrOid"); 
			updateECRToECRLink(ecr, ecrOids, false);
			//첨부파일
			CommonContentHelper.service.attach(ecr, null, secondarys);
			//주첩부 파일
			String ecrFile = req.getParameter("ECR");
		    //System.out.println("ecrFile =" + ecrFile);
			
			ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)ecr, "ECR", ecrFile, false);
			//System.out.println("app =" + app);
			 
			
			
	    	trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(ecr));
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
	public void updateECRToECRLink(EChangeRequest ecr, String[] ecrOids, boolean isDelete) throws Exception {
    	if(isDelete) {
    		deleteECRToECRLink(ecr);
    	}
        
        //관련 문서
		for(int i=0; ecrOids!=null && i<ecrOids.length; i++){
			EChangeRequest linkECR = (EChangeRequest)CommonUtil.getObject(ecrOids[i]);
			EcrToEcrLink link = EcrToEcrLink.newEcrToEcrLink(ecr, linkECR);
			PersistenceServerHelper.manager.insert(link);
		}
    }
	
    public void deleteECRToECRLink(EChangeRequest ecr) throws Exception {
    	List<EcrToEcrLink> list = ECRHelper.service.getEcrToEcrLinks(ecr, "useBy");
		for(EcrToEcrLink link : list){
			PersistenceServerHelper.manager.remove(link);
		}
		list = ECRHelper.service.getEcrToEcrLinks(ecr, "used");
		for(EcrToEcrLink link : list){
			PersistenceServerHelper.manager.remove(link);
		}
    }
	@Override
	public ResultData updateECRAction(HttpServletRequest req)throws Exception{
       
		ResultData result = new ResultData();
	    Transaction trx = new Transaction();
       
       try {
    	    trx.start();
    	    
    	    String oid = (String)req.getParameter("oid");
    	    String name = StringUtil.checkNull(req.getParameter("name"));
    	    String number = StringUtil.checkNull(req.getParameter("number"));
			String createDate = StringUtil.checkNull(req.getParameter("createDate"));
			String approveDate = StringUtil.checkNull(req.getParameter("approveDate"));
			
			String createDepart = StringUtil.checkNull(req.getParameter("createDepart"));
			String writer = StringUtil.checkNull(req.getParameter("writer"));
			
			String[] models = req.getParameterValues("model");
			
			String proposer = StringUtil.checkNull(req.getParameter("proposer"));
			String[] changeSections = req.getParameterValues("changeSection");
			String eoCommentA = StringUtil.checkNull(req.getParameter("eoCommentA"));
			String eoCommentB = StringUtil.checkNull(req.getParameter("eoCommentB"));
			String eoCommentC = StringUtil.checkNull(req.getParameter("eoCommentC"));
			String[] secondarys = req.getParameterValues("SECONDARY");
			String[] deloids	= (String[]) req.getParameterValues("delocIds");
			
			
			
			String model = ChangeUtil.getArrayList(models);
			String changeSection = ChangeUtil.getArrayList(changeSections);
			
			//System.out.println("=======writer =" + writer);
			//System.out.println("=======proposer =" + proposer);
			//System.out.println("=======changeSections =" + changeSections);
			//System.out.println("=======approveDate =" + approveDate);
			
			EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
    	   
			ecr.setEoName(name);
			ecr.setEoNumber(number);
			ecr.setCreateDate(createDate);
			ecr.setWriter(writer);
			ecr.setApproveDate(approveDate);
			ecr.setCreateDepart(createDepart);
			ecr.setModel(model);
			ecr.setProposer(proposer);
			ecr.setChangeSection(changeSection);
			ecr.setEoCommentA(eoCommentA);
			ecr.setEoCommentB(eoCommentB);
			ecr.setEoCommentC(eoCommentC);			
			
			ecr=(EChangeRequest) PersistenceHelper.manager.modify(ecr);
			String[] ecrOids 	= (String[]) req.getParameterValues("ecrOid"); 
			updateECRToECRLink(ecr, ecrOids, true);
			//첨부
			CommonContentHelper.service.attach(ecr, null, secondarys, deloids, false);
			
			
			//주첨부
			String roleType = req.getParameter("ECR");
			CommonContentHelper.service.attachADDRole(ecr, "ECR", roleType, false);
			 
            trx.commit();
			trx = null;
			
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(ecr));
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
	
	@Override
	public String deleteEcr(String oid)throws Exception{

		Transaction trx = new Transaction();
		try{
			trx.start();
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				EChangeRequest ecr = (EChangeRequest) f.getReference(oid).getObject();
				
				 List<EcrToEcrLink> used = ECRHelper.service.getEcrToEcrLinks(ecr,"used");
	                List<EcrToEcrLink> useBy = ECRHelper.service.getEcrToEcrLinks(ecr, "useBy");
	                
	                if(used.size() > 0 || useBy.size() > 0){
	                	throw new Exception(Message.get("관련 CR/ECPR") + Message.get("이(가) 있을 때 삭제할 수 없습니다."));
	                }
	                
				
				
				WFItemHelper.service.deleteWFItem(ecr);
				Vector<EChangeActivity> vec = new Vector<EChangeActivity>();//ECAHelper.service.getECAList(ecr);
				for(int i = 0 ; i <vec.size() ;i++){
					EChangeActivity eca = vec.get(i);
					PersistenceHelper.manager.delete(eca);
				}
				
				PersistenceHelper.manager.delete(ecr);
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
	
	/**	관련 ECR
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<EOData> include_ecrList(String oid) throws Exception {
		List<EOData> list = new ArrayList<EOData>();
		if (oid.length() > 0) {
			EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
			
            QueryResult result = PersistenceHelper.manager.navigate(eco,"ecr",RequestOrderLink.class,false);
			
            while(result.hasMoreElements()){
            	RequestOrderLink link = (RequestOrderLink) result.nextElement();
				//if(link.getEoype().equals(ChangeKey.ecrReference)) continue;
				EChangeRequest ecr = link.getEcr();
				EOData data = new EOData(ecr);
				
            	list.add(data);
            }
		}
		return list;
		
	}

	@Override
	public List<EcrToEcrLink> getEcrToEcrLinks(EChangeRequest ecr, String roleName)
			throws Exception {
		List<EcrToEcrLink> list = new ArrayList<EcrToEcrLink>();
		
		QueryResult qr = PersistenceHelper.manager.navigate(ecr, roleName, EcrToEcrLink.class, false);
		while(qr.hasMoreElements()){ 
			EcrToEcrLink link = (EcrToEcrLink)qr.nextElement();
			list.add(link);
    	}
		return list;
	}

	@Override
	public void create(ECRData data) throws Exception {
		Transaction trs = new Transaction();
	    try{
	    	trs.start();
			EChangeRequest ecr = null;
			ecr = EChangeRequest.newEChangeRequest();
			ecr.setEoName(data.getEoName());
			ecr.setEoNumber(data.getEoNumber());
			ecr.setEoType(data.getEoType());
			ecr.setCreateDate(data.getCreateDate());
			ecr.setWriter(data.getWriter());
			ecr.setApproveDate(data.getApproveDate());
			ecr.setCreateDepart(data.getCreateDepart());
			ecr.setModel(data.getModel());
			ecr.setProposer(data.getProposer());
			ecr.setChangeSection(data.getChangeSection());
			ecr.setEoCommentA(data.getEoCommentA());
			ecr.setEoCommentB(data.getEoCommentB());
			ecr.setEoCommentC(data.getEoCommentC());
			ecr.setOwner(SessionHelper.manager.getPrincipalReference());
			
			// ECR 분류쳬게 설정
			PDMLinkProduct product = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
			Folder folder = FolderTaskLogic.getFolder("/Default/설계변경/ECR", WCUtil.getWTContainerRef());
	        
			FolderHelper.assignLocation((FolderEntry)ecr, folder);
	        
	        // ECR Container 설정
	        ecr.setContainer(product);
	        
	        // ECR lifeCycle 설정
	        LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", wtContainerRef);
	        LifeCycleHelper.setLifeCycle(ecr, tmpLifeCycle); //Lifecycle
						
			ecr=(EChangeRequest) PersistenceHelper.manager.save(ecr);
//			String[] ecrOids 	= (String[]) req.getParameterValues("ecrOid"); 
//			updateECRToECRLink(ecr, ecrOids, false);
			//첨부파일
//			CommonContentHelper.service.attach(ecr, null, secondarys);
			//주첩부 파일
//			String ecrFile = req.getParameter("ECR");
		    //System.out.println("ecrFile =" + ecrFile);
			
//			ApplicationData app = CommonContentHelper.service.attachADDRole((ContentHolder)ecr, "ECR", ecrFile, false);
			//System.out.println("app =" + app);
			
	    	trs.commit();
			trs = null;
//			result.setResult(true);
//			result.setOid(CommonUtil.getOIDString(ecr));
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
	
}
