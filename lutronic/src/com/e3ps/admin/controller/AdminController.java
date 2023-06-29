package com.e3ps.admin.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.fc.EnumeratedType;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;

import com.e3ps.admin.service.AdminHelper;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.beans.DEFData;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ROOTData;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.GenNumberHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.beans.CompanyState;
import com.e3ps.org.service.MailUserHelper;
import com.ptc.core.adapter.server.impl.CollectObjectsWebjectDelegate.OutputType;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	/*
	 * 
	 * 부서 관리
	 * 
	 */
	
	@RequestMapping("/admin_mainCompany")
	public ModelAndView admin_mainCompany(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_mainCompany");
		model.addObject("module", "part");
		return model;
	}
	
	@RequestMapping("/admin_menuCompany")
	public ModelAndView admin_menuCompany(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_menuCompany");
		return model;
	}
	
	@RequestMapping("/admin_listCompany")
	public ModelAndView admin_listCompany(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_listCompany");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_listCompanyAction")
	public Map<String,Object> admin_listCompanyAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = null;
		try {
			map = AdminHelper.service.admin_listCompanyAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		return map;
	}
	
	@RequestMapping("/admin_actionDepartment")
	public ModelAndView admin_actionDepartment(HttpServletRequest request, HttpServletResponse response) {
		try {
			AdminHelper.service.admin_actionDepartment(request, response);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/admin/admin_mainCompany.do");
	}
	
	@ResponseBody
	@RequestMapping("/admin_actionChief")
	public boolean admin_actionChief(HttpServletRequest request, HttpServletResponse response,@RequestParam("userOid")String userOid) {
		boolean result = false;
		try {
			result = AdminHelper.service.admin_actionChief(userOid);
		} catch(Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping("/admin_setDuty")
	public ModelAndView admin_setDuty(HttpServletRequest request, HttpServletResponse response) {
		
		Vector dutyNameList = CompanyState.dutyNameList;
		Vector dutyCodeList = CompanyState.dutyCodeList;
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for ( int i = 0; i < dutyCodeList.size() ; i++ ) {
			String dutyCode = (String)dutyCodeList.get(i);
			String dutyName = (String)dutyNameList.get(i);
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("code", dutyCode);
			map.put("name", dutyName);
			
			list.add(map);
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/admin/admin_setDuty");
		model.addObject("list", list);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_getDutyListAction")
	public Map<String,List<Map<String,String>>> admin_getDutyListAction(HttpServletRequest request, HttpServletResponse response) {
		String duty = StringUtil.checkReplaceStr(request.getParameter("duty"), "");
		
		Map<String,List<Map<String,String>>> map = null;
		try {
			map = AdminHelper.service.admin_getDutyListAction(duty);
		} catch(Exception e) {
			map = new HashMap<String,List<Map<String,String>>>();
			e.printStackTrace();
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/admin_setDutyAction")
	public boolean admin_setDutyAction(HttpServletRequest request, HttpServletResponse response){
		boolean result = false;
		try {
			AdminHelper.service.admin_setDutyAction(request, response);
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping("/admin_setDepartment")
	public ModelAndView admin_setDepartment(HttpServletRequest request, HttpServletResponse response) {
		String departmentOid = request.getParameter("departmentOid");
		
		ModelAndView model = new ModelAndView();
		model.addObject("departmentOid", departmentOid);
		model.addObject("departmentName", ((Department)CommonUtil.getObject(departmentOid)).getName());
		model.setViewName("popup:/admin/admin_setDepartment");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_getDepartmentListAction")
	public Map<String,List<Map<String,String>>> admin_getDepartmentListAction(HttpServletRequest request, HttpServletResponse response) {
		String oid = StringUtil.checkReplaceStr(request.getParameter("oid"), "");
		
		Map<String,List<Map<String,String>>> map = null;
		try {
			map = AdminHelper.service.admin_getDepartmentListAction(oid);
		} catch(Exception e) {
			map = new HashMap<String,List<Map<String,String>>>();
			e.printStackTrace();
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/admin_setDeptAction")
	public boolean admin_setDeptAction(HttpServletRequest request, HttpServletResponse response) {
		boolean result = false;
		try {
			AdminHelper.service.admin_setDeptAction(request, response);
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/*
	 * 
	 * 다운로드 이력관리
	 * 
	 */
	
	
	@RequestMapping("/admin_downLoadHistory")
	public ModelAndView admin_downLoadHistory(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_downLoadHistory");
		model.addObject("module", "download");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_downLoadHistoryAction")
	public Map<String,Object> admin_downLoadHistoryAction(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = null;
		
		try {
			map = AdminHelper.service.admin_downLoadHistoryAction(request, response);
		} catch(Exception e) {
			map = new HashMap<String,Object>();
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	/*
	 * 
	 * 외부 메일
	 * 
	 */
	
	
	@RequestMapping("/admin_mail")
	public ModelAndView admin_mail(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_mail");
		model.addObject("module", "mail");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_mailAction")
	public Map<String,Object> admin_mailAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = null;
		
		try {
			map = AdminHelper.service.admin_mailAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/actionMailUser")
	public Map<String,String> actionMailUser(HttpServletRequest request, HttpServletResponse response) {
		
		String command = StringUtil.checkNull(request.getParameter("command"));
		String oid = StringUtil.checkNull(request.getParameter("oid"));    
		String name = StringUtil.checkNull(request.getParameter("name"));
		String email = StringUtil.checkNull(request.getParameter("email"));
		String enable = StringUtil.checkNull(request.getParameter("enable"));
		
		HashMap map = new HashMap();
	    map.put("name", name);
	    map.put("email", email);
	    map.put("enable", enable);
	    map.put("oid", oid);
	    map.put("command", command);
	    
	    //String msg = "";
	    Map<String,String> msg = null;
		if ("create".equals(command)) {
			msg = MailUserHelper.service.createMailUser(map);
		} else if ("update".equals(command)) {
			msg = MailUserHelper.service.modifyMailUser(map);
		} else if ("delete".equals(command)) {
			msg = MailUserHelper.service.deleteMailUser(oid);
		}
		return msg;
	}
	
	
	/*
	 * 
	 * 접속 이력관리
	 * 
	 */
	
	@RequestMapping("/admin_loginhistory")
	public ModelAndView admin_loginhistory(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_loginhistory");
		model.addObject("module", "loginhistory");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_loginHistoryAction")
	public Map<String,Object> admin_loginHistoryAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = null;
		
		try {
			map = AdminHelper.service.admin_loginHistoryAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	
	
	/*
	 * 
	 *   코드 체계관리
	 * 
	 */
	@RequestMapping("/admin_numberCode")
	public ModelAndView admin_numberCode(HttpServletRequest request, HttpServletResponse response) {
		
		NumberCodeType[] codeType = NumberCodeType.getNumberCodeTypeSet();

		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		for(int i=0; i < codeType.length; i++){	
			Map<String,String> map = new HashMap<String,String>();
			map.put("value", codeType[i].toString());
			map.put("name", codeType[i].getDisplay());
			map.put("seq", codeType[i].getShortDescription());	//자동이냐(fals),수동이냐(true)
			map.put("seqNm", codeType[i].getLongDescription());
			map.put("tree", codeType[i].getAbbreviatedDisplay()); //tree 형태(true)l
			list.add(map);
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_numberCode");
		model.addObject("module", "code");
		model.addObject("list",list);
		return model;
	}
	
	@RequestMapping("/admin_numberCodeList")
	public ModelAndView admin_numberCodeList(HttpServletRequest request, HttpServletResponse response) {
		String codeType =request.getParameter("codeType");
		NumberCodeType NCodeType = NumberCodeType.toNumberCodeType(codeType);
		String parentOid = StringUtil.checkReplaceStr(request.getParameter("parentOid"),"");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), codeType);
		String isSeq = NCodeType.getShortDescription();//자동(true),수동(false);
		String seqNm = NCodeType.getLongDescription(); //SEQ NM;
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_numberCodeList");
		model.addObject("codeType", codeType);
		model.addObject("parentOid", parentOid);
		model.addObject("title", title);
		model.addObject("isSeq",isSeq);
		model.addObject("seqNm",seqNm);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_numberCodeAction")
	public Map<String,Object> admin_numberCodeAction(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = null;
		try {
			map = AdminHelper.service.admin_numberCodeAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/numberCodeAction")
	public String numberCodeAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String msg = "";
		String result = "";
		
		String command = request.getParameter("command");
		String codetype = request.getParameter("codeType");
		String oid = request.getParameter("oid");
		String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
		String name = StringUtil.checkNull(request.getParameter("name"));
		String engName = StringUtil.checkNull(request.getParameter("engName"));
		String code = StringUtil.checkNull(request.getParameter("code"));
		String sort = StringUtil.checkNull(request.getParameter("sort"));
		String description = StringUtil.checkNull(request.getParameter("description"));
		String enabled = request.getParameter("enabled");
		
		NumberCodeType ctype = NumberCodeType.toNumberCodeType(codetype);
		
		if("create".equals(command)) {
			boolean isSeq = ctype.getShortDescription().equals("true") ? true : false;
			String seqNm = ctype.getLongDescription();
	        if ( !isSeq && GenNumberHelper.service.checkCode(codetype, parentOid,request.getParameter("code").toUpperCase()) ) {	
	           	msg=Message.get("입력하신 코드가 이미(PDM) 등록되어 있습니다. 다시 확인 후 등록해 주세요.");
	           	result ="F";
	        }
	        else {
	        	 
	        	
	        	 String codeNum = "";
            	 NumberCode nCode = NumberCode.newNumberCode();
            	 if(isSeq){
            		 codeNum=SequenceDao.manager.getNumberCodeSeqNo(codetype,seqNm, "000", "NumberCode", "code");
            		 codeNum=seqNm+codeNum;
            	 }else{
            		codeNum = code.toUpperCase();
            	 }
            	 
                 nCode.setName(name);
                 nCode.setEngName(engName);
                 nCode.setCode(codeNum);
                 nCode.setSort(sort);
                 nCode.setDescription(description);
                 nCode.setCodeType(ctype);
                 nCode.setDisabled(!"true".equals(enabled));
                 
                 //String parentOid = StringUtil.checkReplaceStr(request.getParameter("parentOid"),"");
                 if(parentOid!= null && parentOid.length()>0){
                	 NumberCode pCode = (NumberCode)CommonUtil.getObject(parentOid);
                	 nCode.setParent(pCode);
                 }
                 
                 nCode = (NumberCode) PersistenceHelper.manager.save(nCode);
     			 msg = Message.get("등록 되었습니다.");
	        }
		}else if("update".equals(command)){        
	        
	        ReferenceFactory rf = new ReferenceFactory();
	        NumberCode nCode = (NumberCode)rf.getReference(oid).getObject();
	        String oldCode = nCode.getCode();
	        nCode.setName(name);
	        nCode.setEngName(engName);
	        //nCode.setCode(code.toUpperCase());
	        nCode.setSort(sort);
	        nCode.setDescription(description);
	        nCode.setDisabled(!"true".equals(enabled));
	        nCode = (NumberCode) PersistenceHelper.manager.modify(nCode);
	        
			msg = Message.get("수정 되었습니다.");
		}else if("delete".equals(command)){
			
	        ReferenceFactory rf = new ReferenceFactory();
	        NumberCode nCode = (NumberCode)rf.getReference(oid).getObject();
	        boolean isUseCheck=CodeHelper.service.isUseCheck(nCode);
	        
	        if(isUseCheck){
	        	msg = Message.get("ECO, CR/ECPR에서 사용하고 있는 코드는 삭제 할수 없습니다.");
	        }else{
	        	
	        	WTPrincipal curUser = SessionHelper.manager.getPrincipal();
	        	String userName = curUser.getName();
	        	
	        	String log = "user : "+userName+", code : "+nCode.getCode()+", oid : "+oid;
	        	
	        	createLog(log, "CodeDelete");
	        	
	        	nCode = (NumberCode) PersistenceHelper.manager.delete(nCode);
	            oid = null;
	            msg = Message.get("삭제 되었습니다.");
	        }
		}
		return msg;
	}
	
	@RequestMapping("/admin_numberCodeTree")
	public ModelAndView admin_numberCodeTree(HttpServletRequest request, HttpServletResponse response) {
		String codeType = request.getParameter("codeType");
		NumberCodeType NcodeType = NumberCodeType.toNumberCodeType(codeType);
		List<Map<String,String>> list = null;
		try {
			list = AdminHelper.service.admin_numberCodeTree(codeType);
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/admin/admin_numberCodeTree");
		model.addObject("codeType", codeType);
		model.addObject("codeTypeName", NcodeType.getDisplay());
		model.addObject("list", list);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_numberCodeTreeAction")
	public List<Map<String,String>> admin_numberCodeTreeAction(HttpServletRequest request, HttpServletResponse response) {
		String codeType = request.getParameter("codeType");
		NumberCodeType ctype = NumberCodeType.toNumberCodeType(codeType);
		List<Map<String,String>> list = null;
		try {
			list = AdminHelper.service.admin_numberCodeTree(codeType);
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		
		return list;
	}
	
	/*
	 * 
	 *   Windchill
	 * 
	 * 
	 */
	@RequestMapping("/admin_Windchill")
	public ModelAndView admin_Windchill(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_Windchill");
		model.addObject("module", "windchill");
		return model;
	}
	
	//2016.03.02 이태용차장 문의 넘버코드 사라짐 현상으로 인해 로그 추가
	public void createLog(String log,String fileName) {
//		System.out.println("========== "+fileName+" ===========");
		String filePath = "D:\\e3ps\\numbercode";
		
		File folder = new File(filePath);
		
		if(!folder.isDirectory()){
			
			folder.mkdirs();
		}
		fileName = fileName.replace(",", "||");
		fileName  = "NumberCode"+"_"+fileName;
		//System.out.println("fileName= " + fileName +",isChange =" + isChange);
		String toDay = com.e3ps.common.util.DateUtil.getCurrentDateString("date");
		toDay = com.e3ps.common.util.StringUtil.changeString(toDay, "/", "-");
		String logFileName = fileName+"_" + toDay.concat(".log");
		String logFilePath = filePath.concat(File.separator).concat(logFileName);
		File file = new File(logFilePath);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter out = new PrintWriter(new BufferedWriter(fw), true);
		out.write(log);
		//System.out.println(log);
		out.write("\n");
		out.close();
	}
	
	//루트뢰닉 추가
	
	
	
	
	/**	RootDefinition 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createRootDefinition")
	public ModelAndView createRootDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		
		model.setViewName("popup:/admin/createRootDefinition");
		return model;
	}
	
	
	
	/**	RootDefinition 등록 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/createRootDefinitionAction")
	public ResultData createRootDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return ECAHelper.service.createRootDefinitionAction(request);
	}
	
	/**
	 * 설계변경 활동 리스트
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/admin_listChangeActivity")
	public ModelAndView admin_listChangeActivity(HttpServletRequest request, HttpServletResponse response) {
		
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("admin:/admin/admin_listChangeActivity");
		
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/admin_listChangeActivityAction")
	public Map<String,Object> admin_listChangeActivityAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			
			String rootOid = StringUtil.checkNull(request.getParameter("rootOid"));
//			System.out.println("admin admin_listChangeActivityAction = "+rootOid);
			map = ECAHelper.service.listActiveDefinitionAction(request,response);
			
		} catch(Exception e) {
			map = new HashMap<String,Object>();
			e.printStackTrace();
		}
		return map;
	}
	
	/**	RootDefinition 수정 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateRootDefinition")
	public ModelAndView updateRootDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		EChangeActivityDefinitionRoot root =(EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
		ROOTData rootdata = new ROOTData(root);
		model.addObject("rootdata", rootdata);
		model.setViewName("popup:/admin/updateRootDefinition");
		return model;
	}
	
	/**	RootDefinition 수정 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/updateRootDefinitionAction")
	public ResultData updateRootDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return ECAHelper.service.updateRootDefinitionAction(request);
	}
	
	/**	RootDefinition 삭제 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/deleteRootDefinition")
	public Map<String,Object> deleteRootDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		String oid = request.getParameter("rootOid");
//		System.out.println("admin deleteRootDefinition oid =" +oid);
		boolean result = false;
		String msg = Message.get("삭제 실패하였습니다.");
		try {
			msg = ECAHelper.service.deleteRootDefinition(oid);
			result = true;
		}catch(Exception e) {
			result = false;
			msg = Message.get("삭제 실패하였습니다.");
			e.printStackTrace();
		}
		map.put("result", result);
		map.put("msg", msg);
		
		return map;
	}
	
	/**
	 * RootDefinition 목록 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getRootDefinition")
	public List<ROOTData> getRootDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
//		System.out.println("admin getRootDefinition");
		return ECAHelper.service.getRootDefinition();
		
	}
	
	/**	ActivityDefinition 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createActivityDefinition")
	public ModelAndView createActivityDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		model.addObject("oid", oid);
		model.setViewName("popup:/admin/createActivityDefinition");
		
		return model;
	}
	
	
	
	/**	ActivityDefinition 등록 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/createActivityDefinitionAction")
	public ResultData createActivityDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return ECAHelper.service.createActivityDefinitionAction(request);
	}
	
	/**	ActivityDefinition 수정 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateActivityDefinition")
	public ModelAndView updateActivityDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		EChangeActivityDefinition def =(EChangeActivityDefinition) CommonUtil.getObject(oid);
		EADData eadData = new EADData(def);
		model.addObject("eadData", eadData);
		model.setViewName("popup:/admin/updateActivityDefinition");
		return model;
	}
	
	/**	ActivityDefinition 수정 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/updateActivityDefinitionAction")
	public ResultData updateActivityDefinitionAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return ECAHelper.service.updateActivityDefinitionAction(request);
	}
	
	
	
	
	/**	ActivityDefinition 삭제 Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/deleteActivityDefinition")
	public Map<String,Object> deleteActivityDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> map = new HashMap<String,Object>();
		String deleteOid = request.getParameter("deleteOid");
//		System.out.println("admin deleteRootDefinition oid =" +deleteOid);
		boolean result = false;
		String msg = Message.get("삭제 실패하였습니다.");
		try {
			msg = ECAHelper.service.deleteActivityDefinition(deleteOid);
			result = true;
		}catch(Exception e) {
			result = false;
			msg = Message.get("삭제 실패하였습니다.");
			e.printStackTrace();
		}
		map.put("result", result);
		map.put("msg", msg);
		
		return map;
	}
	
	@RequestMapping("/admin_package")
	public ModelAndView admin_package(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("admin:/admin/admin_package");
		model.addObject("module", "package");
		return model;
	}
	
	@RequestMapping("/admin_packageAction")
	public ModelAndView admin_packageAction(HttpServletRequest request, HttpServletResponse response){
		String message = "";
		try {
			AdminHelper.service.admin_packageAction(request, response);
			message = Message.get("업로드 되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			message = Message.get("업로드 실패하였습니다.") + "\n" + e.getLocalizedMessage();
		}
		return ControllerUtil.messageAlert(message);
	}
}
