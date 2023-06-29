package com.e3ps.groupware.workprocess.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.fc.Persistable;
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
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.workprocess.AppPerLink;
import com.e3ps.groupware.workprocess.AsmApproval;

@SuppressWarnings("serial")
public class StandardAsmApprovalService extends StandardManager implements AsmApprovalService {

	public static StandardAsmApprovalService newStandardAsmApprovalService() throws Exception {
		final StandardAsmApprovalService instance = new StandardAsmApprovalService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public ResultData createAsmAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			String searchType = StringUtil.checkNull(request.getParameter("searchType"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String number = "";
			if(searchType.length() > 0){
				if("DOC".equals(searchType)) {
					number = "NDBT";
				}else if("ROHS".equals(searchType)){
					number = "ROHSBT";
				}else if("MOLD".equals(searchType)){
					number = "MMBT";
				}
			}
			String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
			number = number.concat("-").concat(today).concat("-");
	        String seqNo = SequenceDao.manager.getSeqNo(number, "000", "WTDocumentMaster", "WTDocumentNumber");
	        number = number + seqNo;
	        
			AsmApproval asm = AsmApproval.newAsmApproval();
			
			String appName = StringUtil.checkNull(request.getParameter("appName"));
			asm.setNumber(number);
			asm.setName(appName);
			asm.setDescription(description);
			
			// 문서 분류쳬게 설정
	        Folder folder = FolderTaskLogic.getFolder("/Default/AsmApproval", WCUtil.getWTContainerRef());
	        FolderHelper.assignLocation((FolderEntry)asm, folder);
			
	        // 문서 lifeCycle 설정
	        PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
	        WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
	        LifeCycleHelper.setLifeCycle(asm, LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", wtContainerRef)); //Lifecycle

	        asm = (AsmApproval) PersistenceHelper.manager.save(asm);
			
			String[] docOids = request.getParameterValues("docOid");
			for(String docOid : docOids) {
				WTDocument doc = (WTDocument)CommonUtil.getObject(docOid);
				
				AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, asm.getLifeCycleState().toString());
				PersistenceServerHelper.manager.insert(link);
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
	
	@Override
	public void setAsmApprovalState(Object obj, String state) {
		AsmApproval asm = (AsmApproval)obj;
		
		List<WTDocument> list = AsmSearchHelper.service.getObjectForAsmApproval(asm);
		
		//System.out.println("setAsmApprovalState = "  + list.size());
		for(LifeCycleManaged loc : list) {
			E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) loc, state);
		}
		
	}
	
	@Override
	public String deleteAsmAction(String oid) throws Exception{
		
		Transaction trx = new Transaction();
		try{
			trx.start();
			if(oid != null){
				ReferenceFactory f = new ReferenceFactory();
				AsmApproval asm = (AsmApproval) f.getReference(oid).getObject();
				WFItemHelper.service.deleteWFItem(asm);
				
				setAsmApprovalState(asm, "BATCHAPPROVAL");
				
				PersistenceHelper.manager.delete(asm);
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
	public ResultData updateAsmAction(HttpServletRequest request) throws Exception{
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			trx.start();
			String oid = StringUtil.checkNull(request.getParameter("oid"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String appName = StringUtil.checkNull(request.getParameter("appName"));
			
			AsmApproval asm = (AsmApproval)CommonUtil.getObject(oid);
			asm.setName(appName);
			asm.setDescription(description);
			
			asm = (AsmApproval) PersistenceHelper.manager.save(asm);
			
			/*기존 삭제*/
			List<AppPerLink> list = AsmSearchHelper.service.getLinkForAsmApproval(asm);
			
			for(AppPerLink link : list){
				LifeCycleManaged lc =(LifeCycleManaged)link.getRoleAObject();
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) lc, "BATCHAPPROVAL");
				PersistenceHelper.manager.delete(link);
			}
			
			/*추가*/
			String[] docOids = request.getParameterValues("docOid");
			if(docOids != null){
				for(String docOid : docOids) {
					WTDocument doc = (WTDocument)CommonUtil.getObject(docOid);
					
					AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, asm.getLifeCycleState().toString());
					PersistenceServerHelper.manager.insert(link);
				}
			}
			trx.commit();
			trx = null;
			data.setResult(true);
			data.setOid(CommonUtil.getOIDString(asm));
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
	
	
		
	/*	
	private List<WTDocument> getWTDocumentForAsmApproval(AsmApproval asm) {
		List<WTDocument> list = new ArrayList<WTDocument>();
		
		try {
			System.out.println(CommonUtil.getOIDLongValue(asm));
			QuerySpec query = new QuerySpec();
			
			int idx_wt = query.addClassList(WTDocument.class, true);
			int idx = query.addClassList(AppPerLink.class, false);
			
			ClassAttribute att1 = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
			ClassAttribute att2 = new ClassAttribute(AppPerLink.class, "roleAObjectRef.key.id");
			
			query.appendWhere(new SearchCondition(att1, SearchCondition.EQUAL, att2),new int[] {idx_wt, idx});
			
			query.appendAnd();
			
			query.appendWhere(new SearchCondition(AppPerLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(asm)), new int[] {idx});
			
			System.out.println(query);
			
			QueryResult result = PersistenceHelper.manager.find(query);
			
			while(result.hasMoreElements()) {
				Object[] o = (Object[])result.nextElement();
				WTDocument doc = (WTDocument)o[0];
				list.add(doc);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	*/
}
