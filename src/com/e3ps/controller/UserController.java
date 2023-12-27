package com.e3ps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.org.WTUser;
import wt.session.SessionHelper;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.UserHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping("/setAllUserName")
	public void setAllUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserHelper.service.setAllUserName();
	}
	
	/** 접속자 정보 리턴
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getUserData")
	public Map<String,String> getUserData(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
		PeopleDTO data = new PeopleDTO(user);
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", user.getFullName());
		map.put("wtOid", data.wtuserOID);
		map.put("ppOid", data.peopleOID);
		return map;
	}
	
	/** 유저 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/searchUserInfo")
	public ModelAndView searchUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "single");
		String userType = StringUtil.checkReplaceStr(request.getParameter("userType"), "people");
		String obj1 = request.getParameter("obj1");
		String obj2 = request.getParameter("obj2");
		String reFunc = request.getParameter("reFunc");
		
		ModelAndView model = new ModelAndView();
		model.addObject("mode", mode);
		model.addObject("obj1", obj1);
		model.addObject("obj2", obj2);
		model.addObject("userType", userType);
		model.addObject("reFunc", reFunc);
		model.setViewName("popup:/org/searchUserInfo");
		
		return model;
	}
	
	/** 유저 검색 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/searchUserAction")
	public Map<String,Object> searchUserAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		
		try {
			result = UserHelper.service.searchUserAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			result = new HashMap<String,Object>();
		}
		
		return result;
	}
	
	/** 결재선 지정 유저 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approveUserSearch")
	public List<Map<String,Object>> approveUserSearch(HttpServletRequest request, HttpServletResponse response) {
		String userKey = request.getParameter("userKey");
		List<Map<String,Object>> list = null;
		try {
			list = UserHelper.service.approveUserSearch(userKey);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/** 결재선 지정 부서별 유저 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approveUserOrg")
	public List<Map<String,Object>> approveUserOrg(HttpServletRequest request, HttpServletResponse response){
		String deptCode = request.getParameter("deptCode");
		List<Map<String,Object>> list = null;
		try {
			list = UserHelper.service.approveUserOrg(deptCode);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/** 조직도 유저 검색 데이터 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/companyTreeSearch")
	public Map<String,Object> companyTreeSearch(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> map = null;
		
		try {
			map = UserHelper.service.companyTreeSearch(request,response);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	/** 저장된 결재선 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadLineAction")
	public List<Map<String,String>> loadLineAction(HttpServletRequest request, HttpServletResponse response){
		List<Map<String,String>> list = null;
		
		try {
			list = WFItemHelper.service.loadLineAction();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/** 결재선 저장
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createLineAction")
	public String createLineAction(HttpServletRequest request, HttpServletResponse response){
		String title = request.getParameter("title");
		String[] approveUser2 = request.getParameterValues("Agree");
		String[] approveUser3 = request.getParameterValues("Approver");
		String[] approveUser4 = request.getParameterValues("Report");
		String[] tempUser = request.getParameterValues("tempUser");
		
		String msg = "";
		
		try {
			WFItemHelper.service.createApprovalTemplate(title, approveUser2, approveUser3, approveUser4, tempUser);
			msg = Message.get("저장 되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			msg = Message.get("저장 실패");
		}
		
		return msg;
	}
	
	/** 결재선 라인 삭제
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteLineAction")
	public String deleteLineAction(HttpServletRequest request, HttpServletResponse response) {
		String delOid = request.getParameter("delOid");
		String msg = Message.get("삭제할 수 없습니다.");
		try {
			WFItemHelper.service.deleteApprovalTemplate(delOid);
			msg = Message.get("삭제되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			msg = Message.get("삭제할 수 없습니다.");
		}
		return msg;
	}
	
	/** 결재선 상세보기
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/viewApproverTemplate")
	public ModelAndView viewApproverTemplate(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/workprocess/viewApproverTemplate");
		model.addObject("oid", oid);
		return model;
	}
	
	/** 결재선 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadApproverTemplate")
	public List<Map<String,String>> loadApproverTemplate(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String type = request.getParameter("type");
		
		List<Map<String,String>> list = null;
		
		try {
			list = UserHelper.service.viewApproverTemplate(oid, type);
		} catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}
