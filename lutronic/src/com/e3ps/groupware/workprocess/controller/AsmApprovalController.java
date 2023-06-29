package com.e3ps.groupware.workprocess.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.change.service.ECRSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.beans.AsmData;
import com.e3ps.groupware.workprocess.service.AsmApprovalHelper;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;

@Controller
@RequestMapping("/asmApproval")
public class AsmApprovalController {
	
	/**	일괄 결제 등록 메뉴 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createAsm")
	public ModelAndView approvalPackageDocument(HttpServletRequest request, HttpServletResponse response) {
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"), "document");
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu15");
		model.addObject("module","workprocess");
		model.addObject("searchType", searchType);
		model.setViewName("default:/workprocess/createAsm");
		return model;
	}
	
	/**	일괄 결제 실행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createAsmAction")
	public ResultData createAsmAction(HttpServletRequest request, HttpServletResponse response) {
		return AsmApprovalHelper.service.createAsmAction(request, response);
	}
	
	/**	일괄결재 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listAsm")
	public ModelAndView listECR(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu15");
		model.addObject("module","groupware");
		model.setViewName("default:/workprocess/listAsm");
		return model;
	}
	
	/** 일괄결재 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listAsmAction")
	public Map<String,Object> listAsmAction(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> result = null;
		try {
			 result = AsmSearchHelper.service.listAsmAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	/**	일괄 결재 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewAsm")
	public ModelAndView viewAsm(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		AsmApproval asm = (AsmApproval)CommonUtil.getObject(oid);
		AsmData asmData = new AsmData(asm);
		model.setViewName("popup:/workprocess/viewAsm");
		model.addObject("asmData", asmData);
		return model;
	}
	
	/**	일괄결재 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteAsmAction")
	public Map<String,Object> deleteAsmAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		boolean result = false;
		String msg = Message.get("삭제 실패하였습니다.");
		try {
			msg = AsmApprovalHelper.service.deleteAsmAction(oid);
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
	
	
	/**	일괄 결재 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateAsm")
	public ModelAndView updateECR(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		AsmApproval AsmData = (AsmApproval)CommonUtil.getObject(oid);
		AsmData asmData = new AsmData(AsmData);
		
		model.addObject("asmData", asmData);
		model.setViewName("popup:/workprocess/updateAsm");
		return model;
	}
	
	/**	일괄 결재 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updateAsmAction")
	public ResultData updateAsmAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return AsmApprovalHelper.service.updateAsmAction(request);
		//return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/changeECR/viewECR.do?oid="+oid, Message.get("수정 하였습니다."));
	
	}
}
