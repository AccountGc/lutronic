package com.e3ps.change.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
//import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
//import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
//import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOEul;
import com.e3ps.change.service.ChangeWfHelper;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.change.service.ECOHelper;
//import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.service.DocumentHelper;
//import com.e3ps.drawing.beans.EpmUtil;

@Controller
@RequestMapping("/change")
public class ChangeWFController {

	@RequestMapping("wf_Document")
	public ModelAndView wf_Document(HttpServletRequest request, HttpServletResponse response) {
		
		String ecaOid = request.getParameter("ecaOid");
		
		Map<String,Object> map = null;
		try{
			map = ChangeWfHelper.service.wf_Document(ecaOid);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		ModelAndView model = new ModelAndView();
		//model.addObject("map",map);
		model.addAllObjects(map);
		model.addObject("ecaOid", ecaOid);
		model.setViewName("include:/workprocess/wf_Document");
		return model;
	}
	
	@RequestMapping("wf_CreateEOEul")
	public ModelAndView wf_CreateEOEul(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("eoOid");
		List<Map<String,Object>> list = null;
		try {
			list = ChangeWfHelper.service.wf_CheckPart(oid);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("eoOid",oid);
		model.setViewName("include:/workprocess/wf_CreateEOEul");
		return model;
	}
	
	@RequestMapping("/wf_CheckDrawing")
	public ModelAndView wf_CheckDrawing(HttpServletRequest request, HttpServletResponse response) {
		String ecaOid = request.getParameter("ecaOid");
		List<Map> list = new ArrayList<Map>();
		try {
			list = ChangeWfHelper.service.wf_CheckDrawing(ecaOid);
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("ecaOid", ecaOid);
		model.addObject("list", list);
		model.setViewName("include:/workprocess/wf_CheckDrawing");
		return model;
	}
	
	@RequestMapping("/wf_OrderNumber")
	public ModelAndView wf_OrderNumber(HttpServletRequest request, HttpServletResponse response) {
		String ecaOid = request.getParameter("ecaOid");
		
		List<Map<String,Object>> list = null;
		try {
			list = ChangeWfHelper.service.wf_OrderNumber(ecaOid);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("ecaOid", ecaOid);
		model.addObject("list", list);
		model.setViewName("include:/workprocess/wf_OrderNumber");
		return model;
	}
	
	@RequestMapping("/wf_ECASetting")
	public ModelAndView wf_ECASetting(HttpServletRequest request, HttpServletResponse response) {
		String ecaOid = request.getParameter("ecaOid");
		
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);
		ECOChange eo = eca.getEo();
		String eoType = eo.getEoType();
		
		ModelAndView model = new ModelAndView();
		model.addObject("ecaOid", ecaOid);
		model.addObject("eoType", eoType);
		model.setViewName("include:/workprocess/wf_ECASetting");
		return model;
	}
	
	@RequestMapping("/wf_Part_Include")
	public ModelAndView wf_Part_Include(HttpServletRequest request, HttpServletResponse response) {
		String ecoOid = request.getParameter("ecoOid");
		
		List<Map<String,String>> list = null;
		try{
			list = ChangeWfHelper.service.wf_Part_Include(ecoOid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		List<NumberCodeData> apply = CodeHelper.service.topCodeToList("APPLY");
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("apply", apply);
		model.addObject("ecoOid", ecoOid);
		model.setViewName("include:/workprocess/wf_Part_Include");
		return model;
	}
	
	@RequestMapping("actionECA")
	public ModelAndView actionECA(HttpServletRequest request, HttpServletResponse response) {
		FileRequest req = null;
		
		String ecaAction =  StringUtil.checkNull(request.getParameter("ecaAction"));
		String ecaOid =  StringUtil.checkNull(request.getParameter("ecaOid"));
		String comment = StringUtil.checkNull(request.getParameter("comment"));
		String eulOid = StringUtil.checkNull(request.getParameter("eulOid"));
		String activeComment =StringUtil.checkNull(request.getParameter("activeComment"));
		
		
		String[] partOids = request.getParameterValues("partOid");
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String docOid = StringUtil.checkNull(request.getParameter("docOid")) ;
		String applyDate =StringUtil.checkNull(request.getParameter("applyDate"));
		String docLinkOid = StringUtil.checkNull(request.getParameter("docLinkOid"));
		String[] isSelecctBom = request.getParameterValues("isSelecctBom");
		String msg ="";
		
		try {
		
			if(ecaAction.length()==0) {
				req = new FileRequest(request);
				ecaAction = StringUtil.checkNull(req.getParameter("ecaAction"));
				comment = StringUtil.checkNull(req.getParameter("comment"));
				ecaOid = StringUtil.checkNull(req.getParameter("ecaOid"));
				activeComment =StringUtil.checkNull(request.getParameter("activeComment"));
				partOids =req.getParameterValues("partOid");
				oid = StringUtil.checkNull(req.getParameter("oid"));
				eulOid  = StringUtil.checkNull(req.getParameter("eulOid"));
				docOid = StringUtil.checkNull(req.getParameter("docOid"));
				applyDate = StringUtil.checkNull(req.getParameter("applyDate"));
				docLinkOid = StringUtil.checkNull(req.getParameter("docLinkOid"));
				
			}
			
			boolean isSave = false;
			if(ecaAction.equals("activeDoc")){
				Hashtable hash = new Hashtable();
				
				if(docOid.length()>0 ){
					WTDocument doc = (WTDocument)CommonUtil.getObject(docOid);
					hash.put("outputOid", ecaOid);
					DocumentHelper.service.createOutPutLink(doc, hash);
					msg = Message.get("산출물이 등록 되었습니다.");
						
				}
				
			}else if(ecaAction.equals("checkResult")){
				
				//isSave = ECAHelper.service.saveCheckResult(req);
				if(isSave){
					
					msg = Message.get("검토결과가 등록 되었습니다.");
				}else{
					msg = Message.get("검토결과 등록시 오류가 발생 하였습니다.");
				}
			
			}else if(ecaAction.equals("ecoConfirm")){
				//isSave=ECAHelper.service.saveEcoConfirm(req);
				if(isSave){
					
					msg = Message.get("ECO 확정이 등록 되었습니다.");
				}else{
					msg = Message.get("ECO 확정 등록시 오류가 발생 하였습니다.");
				}
			}else if(ecaAction.equals("eulDelet")){
				
				//eulOid = java.net.URLDecoder.decode(eulOid==null?"":eulOid,"euc-kr");
				ReferenceFactory rf = new ReferenceFactory();
	
				msg = Message.get("을지 삭제시 오류가 발생 하였습니다");
				try {
					EOEul eoeul = (EOEul)rf.getReference(eulOid).getObject();
					if(eoeul != null) {
						PersistenceHelper.manager.delete(eoeul);
						msg =  Message.get("을지가 삭제 되었습니다.");
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					
				}
			}else if(ecaAction.equals("ecaSetting")){
				
				//isSave = ECAHelper.service.saveECA(req);
				
				if(isSave){
					
					msg =  Message.get("ECA 활동이 등록 되었습니다.");
				}else{
					msg =  Message.get("ECA 활동이 등록시 오류가 발생 하였습니다.");
				}
				
			}else if(ecaAction.equals("associateDelete")){ //WTPart EPMDocument 연관 해제
				boolean isDelete = true;
				//boolean isDelete = EpmUtil.deleteBuildeRule(oid);
				if(isDelete){
					msg =  Message.get("연관 해제 되었습니다.");
				}else{
					msg = Message.get("연관 해제 실패 하였습니다.");
				}
			}else if(ecaAction.equals("relationCreate")){
				boolean isDelete = true;
				//boolean isDelete = EpmUtil.createRelation(oid);
				if(isDelete){
					msg = Message.get("연결 되었습니다.");
				}else{
					msg = Message.get("연결 실패 하였습니다.");
				}
			}else if(ecaAction.equals("docDelete")){
				
				DocumentActivityLink link =(DocumentActivityLink)CommonUtil.getObject(docLinkOid);
				try{
					PersistenceHelper.manager.delete(link);
					msg = Message.get("산출물 링크를 삭제 하였습니다.");
				}catch(Exception e){
					msg =  Message.get("산출물 링크 삭제 실패 하였습니다.");
				}
				
			}else if(ecaAction.equals("addPart")){
				
				HashMap hash = new HashMap();
				
				//HashMap returnMap = ChangeECOHelper.service.addPart(request);//addPart(hash);//addPart(hash);
				//msg = (String)returnMap.get("msg");
				
			}else if(ecaAction.equals("attachFile")){
				
				String[] loc = req.getParameterValues("SECONDARY");
				String[] secondaryDelFile = req.getParameterValues("delocIds");
				
				Hashtable hash = new Hashtable();
				hash.put("oid", ecaOid);
				if(loc !=null) hash.put("files", loc);
				if(secondaryDelFile !=null) hash.put("secondaryDelFile", secondaryDelFile);
				
				boolean isAttach = ECAHelper.service.ecaAttachFile(hash);
				
				if(isAttach){
					msg =  Message.get("첨부 파일이 등록 및 삭제 되었습니다.");
				}else{
					msg =  Message.get("첨부 파일 등록이 실패 하였습니다.");
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
			msg =  Message.get("작업실패");
		}
		
		if("docDelete".equals(ecaAction) || "activeDoc".equals(ecaAction) || "eulDelet".equals(ecaAction) || "attachFile".equals(ecaAction)){
			return ControllerUtil.parentRefresh(msg);
		}else if("ecaSetting".equals(ecaAction) || "ecoConfirm".equals(ecaAction) || "checkResult".equals(ecaAction)){
			//var isSave ="<%=isSave%>"
			//if(isSave =="true"){
			//	parent.document.location.reload();
			//	parent.document.approve.ecaCheck.value =="true";
			//}
			return ControllerUtil.parentRefresh(msg);
		}else if("addPart".equals(ecaAction)){
			//parent.opener.location.reload();
			//parent.self.close();
		}
		return null;
	}
	
	@RequestMapping("AddECOPart")
	public ModelAndView AddECOPart(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		List<Map<String,Object>> list = null;
		try {
			list = ChangeWfHelper.service.wf_CheckPart(oid);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("oid", oid);
		model.setViewName("popup:/workprocess/AddECOPart");
		return model;
	}
	
	@RequestMapping("AddPartAction")
	public ModelAndView AddPartAction(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String msg =  Message.get("품목 변경시 오류가 발생 하였습니다.");
		try {
			
			oid = ECOHelper.service.addPart(request);
			
			
			msg =  Message.get("품목 변경이 완료 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/workprocess/AddECOPart");
		//return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/workprocess/AddECOPart.do?oid="+oid, msg);
		return ControllerUtil.openerRefresh(msg);
	}
	
	@RequestMapping("wf_CheckInEOEul")
	public ModelAndView wf_CheckInEOEul(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("eoOid");
		List<Map<String,Object>> list = null;
		try {
			list = ChangeWfHelper.service.wf_CheckPart(oid);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("eoOid",oid);
		model.setViewName("include:/workprocess/wf_CheckInEOEul");
		return model;
	}
}
