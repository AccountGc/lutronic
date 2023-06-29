package com.e3ps.groupware.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.net.aso.a;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.work.WorkItem;

import com.e3ps.admin.service.AdminHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.iba.AttributeData;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.distribute.util.DistributeUtil;
import com.e3ps.groupware.notice.Notice;
import com.e3ps.groupware.notice.beans.NoticeData;
import com.e3ps.groupware.notice.service.NoticeHelper;
import com.e3ps.groupware.service.GroupwareHelper;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.org.MailWTobjectLink;
import com.e3ps.org.service.MailUserHelper;

@Controller
@RequestMapping("/groupware")
public class GroupwareController {
	
	/** 메인 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/main")
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView model = new ModelAndView();
		String viewName = "default:/workprocess/main";
		int distributeType = 3;
		
		distributeType = DistributeUtil.distributeInnerType();
		if(distributeType == DistributeUtil.Distribute_Inner){
			viewName = "distribute:/distribute/main";
			model.addObject("module","distribute");
		}
		
		model.setViewName(viewName);
		return model;
	}
	
	/** 공지사항 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listNotice")
	public ModelAndView notice(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module", "workprocess");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("default:/workprocess/listNotice");
		return model;
	}
	
	
	
	/** 공지사항 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listNoticeAction")
	public Map<String,Object> listNoticeAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = GroupwareHelper.service.listNoticeAction(request,response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** 팝업 대상 공지사항 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/popupNoticeAction")
	public List<NoticeData> popupNoticeAction(HttpServletRequest request, HttpServletResponse response) {
		List<NoticeData> returnData = new ArrayList<NoticeData>();
		try {
			returnData = NoticeHelper.service.getPopUpNotice();
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return returnData;
	}
	
	/** 공지사항 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/createNotice")
	public ModelAndView createNotice(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module", "workprocess");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("default:/workprocess/createNotice");
		return model;
	}
	
	/** 공지사항 등록
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createNoticeAction")
	public ModelAndView createNoticeAction(HttpServletRequest request, HttpServletResponse response){
		String msg = "";
		try {
		
			FileRequest req = new FileRequest(request);
			
			String title = req.getParameter("title");
			String contents = req.getParameter("contents");
			String isPopup = StringUtil.checkNull(req.getParameter("isPopup"));
			//System.out.println("isPopup =" + isPopup);
			
			Hashtable<String,String> hash = new Hashtable<String,String>();
			hash.put("title" , title);
			if(contents == null) contents = "";
			hash.put("contents" , contents);
			hash.put("isPopup", isPopup);
			String[] loc = req.getParameterValues("SECONDARY");//req.getFileLocations("secondary");
			msg = NoticeHelper.service.create(hash , loc);
			
		}catch (Exception e ){
			e.printStackTrace();
			msg = e.getLocalizedMessage();
			msg = msg.replaceAll("\r\n", "\\\\n");
			msg = msg.replaceAll("\r", "\\\\n");
			msg = msg.replaceAll("\n", "\\\\n");
			msg = Message.get("작업 중 다음과 같은 오류가 발생했습니다. ") + msg;
		}
		
		return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/groupware/listNotice.do", msg);
	}
	
	/** 공지사항 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewNotice")
	public ModelAndView viewNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) throws Exception{
		
		NoticeHelper.service.updateCount(oid);
		boolean isPopup = StringUtil.checkNull(request.getParameter("isPopup")).equals("true");
		Notice notice = (Notice)CommonUtil.getObject(oid);
		NoticeData noticeData = new NoticeData(notice);
		
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module", "workprocess");
		model.addObject("noticeData", noticeData);
		if(isPopup){
			model.setViewName("popup:/workprocess/viewPopUpNotice");
		}else{
			model.setViewName("default:/workprocess/viewNotice");
		}
		
		return model;
	}
	
	/** 공지사항 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteNoticeAction")
	public String deleteNoticeAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) throws Exception{
		String msg = NoticeHelper.service.delete(oid);
		return msg;
	}
	
	/** 공지사항 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateNotice")
	public ModelAndView updateNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) throws Exception {
		Notice notice = (Notice)CommonUtil.getObject(oid);
		NoticeData noticeData = new NoticeData(notice);
		
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module", "workprocess");
		model.addObject("noticeData", noticeData);
		model.setViewName("default:/workprocess/updateNotice");
		return model;
	}
	
	/** 공지사항 수정
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateNoticeAction")
	public ModelAndView updateNoticeAction(HttpServletRequest request, HttpServletResponse response) {
		String msg = "";
		try {
		
			FileRequest req = new FileRequest(request);
			
			String title = req.getParameter("title");
			String contents = req.getParameter("contents");
			String oid = req.getParameter("oid");
			String isPopup = StringUtil.checkNull(req.getParameter("isPopup"));
			
			Hashtable<String,String> hash = new Hashtable<String,String>();
			hash.put("title" , title);
			if(contents == null) contents = "";
			hash.put("contents" , contents);
			hash.put("oid", oid);
			hash.put("isPopup", isPopup);
			String[] loc = req.getParameterValues("SECONDARY");;
			String[] deloc = req.getParameterValues("delocIds");
			msg = NoticeHelper.service.modify(hash , loc, deloc);
			
		}catch (Exception e ){
			e.printStackTrace();
			msg = e.getLocalizedMessage();
			msg = msg.replaceAll("\r\n", "\\\\n");
			msg = msg.replaceAll("\r", "\\\\n");
			msg = msg.replaceAll("\n", "\\\\n");
			msg = Message.get("작업 중 다음과 같은 오류가 발생했습니다. ") + msg;
		}
		return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/groupware/listNotice.do", msg);
	}

	/** 작업합 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listWorkItem")
	public ModelAndView listWorkItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","workprocess");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("default:/workprocess/listWorkItem");
		return model;
	}
	
	/** 작업함 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listWorkItemAction")
	public Map<String,Object> listWorkItemAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = GroupwareHelper.service.listWorkItemAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			result = new HashMap<String,Object>();
		}
		return result;
	}
	
	/** 결재 상세화면
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/approval")
	public ModelAndView approval(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView model = null;
		try {
			model = GroupwareHelper.service.approval(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			model = new ModelAndView();
		}
		model.addObject("menu", "menu3");
		model.addObject("module","workprocess");
		return model;
	}
	
	/** 결재 상세화면의 결재객체 상세정보
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/processInfo")
	public ModelAndView processInfo(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
		
		try{
			model = GroupwareHelper.service.processInfo(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			model = new ModelAndView();
		}
		
		model.setViewName("include:/workprocess/processInfo");
		return model;
	}
	
	/** 결재이력 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@RequestMapping("/historyWork")
	public ModelAndView historyWork(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid") String oid) {
		boolean isAuth = false;
		List<Map<String,Object>> appList = null;
		try {
			appList = GroupwareHelper.service.getApprovalList(oid);
			isAuth = CommonUtil.isAdmin();
			WTUser suer = (WTUser) SessionHelper.manager.getPrincipal();
			String sUserName = suer.getFullName();
			for (int i = 0; i < appList.size(); i++) {
				Map<String,Object> map = (Map<String, Object>) appList.get(i);
				String userName = String.valueOf(map.get("userName"));
				//if(sUserName.equals(userName)){ isAuth = true; break;}
			}
		} catch(Exception e) {
			appList = new ArrayList<Map<String,Object>>();
		}
		
		List<Map<String,Object>> mailList = GroupwareHelper.service.getMailList(oid);
		
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("isAuth", isAuth);
		model.addObject("appList", appList);
		model.addObject("mailList", mailList);
		model.setViewName("popup:/workprocess/historyWork");
		return model;
	}
	
	
	/** 결재이력 의견 수정 액션
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateHistoryWorkCommentAction")
	public ResultData updateHistoryWorkCommentAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("historyLinkOid") String historyLinkOid,
			@RequestParam("newComment") String newComment) {
		ResultData data = new ResultData();
		try {
			//System.out.println("historyLinkOid="+historyLinkOid);
			//System.out.println("newComment="+newComment);
			String oid = StringUtil.checkNull(historyLinkOid);
			String comment = StringUtil.checkNull(newComment);
			WFItemUserLink itemUserLink = (WFItemUserLink) CommonUtil.getObject(oid);
			if(null!=itemUserLink && comment.length()>0){
				//System.out.println("updateHistoryWorkCommentAction Comment Change before: "+itemUserLink.getComment());
				itemUserLink.setComment(comment);
				itemUserLink=(WFItemUserLink)PersistenceHelper.manager.modify(itemUserLink);
				itemUserLink=(WFItemUserLink)PersistenceHelper.manager.refresh(itemUserLink);
				//System.out.println("updateHistoryWorkCommentAction Comment Change after: "+itemUserLink.getComment());
				data.setResult(true);
				data.setMessage("결재의견이 수정 되었습니다.");
			}	
		} catch (Exception e) {
			data.setResult(true);
			data.setMessage("결재의견 수정시 에러가 발생 하였습니다.");
			e.printStackTrace();
		}
		return data;
	}
	
	
	/**	결재 진행
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/approveAction")
	public ModelAndView approveAction(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			GroupwareHelper.service.approveAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String distributeType = StringUtil.checkNull(request.getParameter("distributeType"));
		ModelAndView model = new ModelAndView();
		if(distributeType.equals("1")){  // 배포 
			model = ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/distribute/listWorkItem.do");
		}else{
			model = ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/groupware/listWorkItem.do");
		}
		return model;
	}
	
	/** 결재선 지정 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/approvalLine")
	public ModelAndView approvalLine(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String workOid = request.getParameter("workOid");
		WorkItem workItem = (WorkItem)CommonUtil.getObject(workOid);
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/workprocess/approvalLine");
		model.addObject("workOid", workOid);
		model.addObject("ownUser", workItem.getOwnership().getOwner().getObjectId().toString());
		return model;
	}
	
	/** 진행중, 완료함, 수신함 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listItem")
	public ModelAndView listItem(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("module","workprocess");
		
		String state = request.getParameter("state");
		
		if("ing".equals(state)) {
			model.addObject("menu","menu4");
		}else if("complete".equals(state)) {
			model.addObject("menu","menu5");
		}else if("receive".equals(state)){
			model.addObject("menu","menu6");
		}
		model.setViewName("default:/workprocess/listItem");
		model.addObject("state", state);
		
		return model;
	}
	
	/** 진행중, 완료함, 수신함 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listItemAction")
	public Map<String,Object> listItemAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = GroupwareHelper.service.listItemAction(request,response);
		} catch(Exception e ) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**  결재 객체에 대한 결재선 저장
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveApprovalLineAction")
	public boolean saveApprovalLineAction(HttpServletRequest request, HttpServletResponse response) {
		String workItemOid = StringUtil.checkNull(request.getParameter("workItemOid"));
		String appLine = StringUtil.checkNull(request.getParameter("appLine")) ;  //결재라인
		String agrLine = StringUtil.checkNull(request.getParameter("agrLine"));   //합의라인
		String tempLine = StringUtil.checkNull(request.getParameter("tempLine"));		  //수신라인
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		map.put("appLine", appLine);
		map.put("agrLine", agrLine);
		map.put("tempLine", tempLine);
		map.put("workItemOid", workItemOid);
		
		boolean isResult = WFItemHelper.service.createAppLine(map);
		
		return isResult;
	}
	
	/**	저장된 결재라인 가져오기
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadApprovalLine")
	public List<Map<String,String>> loadApprovalLine(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		try {
			list = GroupwareHelper.service.loadApprovalLine(request,response);
		}catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>> ();
		}
		return list;
	}
	
	/** 결재선 위임 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_Reassign")
	public ModelAndView includeReassign(HttpServletRequest request, HttpServletResponse response) {
		String workItemOid = request.getParameter("workItemOid");
		ModelAndView model = new ModelAndView();
		Map<String,String> map = null;
		try{
			map = GroupwareHelper.service.includeReassing(workItemOid);
		}catch (Exception e) {
			map = new HashMap<String,String>();
		}
		model.addAllObjects(map);
		model.setViewName("include:/workprocess/include_Reassign");
		
		return model;
	}
	
	/**	비밀번호 변경 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/changePassword")
	public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String id = request.getParameter("id");
		
		if(!StringUtil.checkString(id)) {
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			
			id = user.getName();
			if ( id.equals("Administrator")) id="wcadmin";
		}
		
		String isPop = request.getParameter("isPop");
		
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu11");
		model.addObject("module", "workprocess");
		model.addObject("id", id);
		model.addObject("isPop", isPop);
		
		String viewName = "default:/workprocess/changePassword";
		if("true".equals(isPop)) {
			viewName = "popup:/workprocess/changePassword";
		}
		
		model.setViewName(viewName);
		return model;
	}
	
	/** 비밀번호 변경
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/changePasswordAction")
	public ModelAndView changePasswordAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String id = request.getParameter("id");
		String isPop = request.getParameter("isPop");
		
		boolean result = GroupwareHelper.service.changePasswordAction(request, response);
		
		String msg = "";
		String viewName = "";
		
		if(result) {
			msg = id + Message.get("비밀번호가 변경 되었습니다.");
			viewName = "/Windchill/login/index.html";
		}else {
			msg = id + Message.get("비밀번호 변경을 실패했습니다.");
			viewName = "/Windchill/" + CommonUtil.getOrgName() + "/groupware/changePassword.do";
		}
		
		if("false".equals(isPop)) {
			return ControllerUtil.redirect(viewName, msg + " " + Message.get("로그인을 다시해 주시기 바랍니다."));
		}else {
			return ControllerUtil.close(msg);
		}
	}
	
	/**	조직도 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/companyTree")
	public ModelAndView companyTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu12");
		model.addObject("module", "workprocess");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("default:/workprocess/companyTree");
		return model;
	}
	
	/**  관라지 메뉴
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wfProcessInfo")
	public ModelAndView wfProcessInfo(HttpServletRequest request, HttpServletResponse response) {
		
		String oid = request.getParameter("oid");
		ModelAndView model = new ModelAndView();
		if(StringUtil.checkString(oid)) {
			model.addObject("oid",oid);
			model.setViewName("popup:/workprocess/wfProcessInfo");
			model.addObject("isPoup", true);
		}else {
			model.setViewName("default:/workprocess/wfProcessInfo");
			model.addObject("isPoup", false);
		}
		
		model.addObject("menu", "menu10");
		model.addObject("module", "workprocess");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/wfProcessInfoAction")
	public Map<String,Object> wfProcessInfoAction(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = null;
		try {
			map = GroupwareHelper.service.wfProcessInfoAction(request, response);
		}catch(Exception e) {
			map = new HashMap<String, Object>();
			map.put("command", "error");
			map.put("message", Message.get("작업에 실패했습니다."));
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**  속성값 변환
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/changeIBA")
	public ModelAndView changeIBA(HttpServletRequest request, HttpServletResponse response) {
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		String[] ibas = AttributeKey.ibas;
		
		for(String iba : ibas) {
			Hashtable<String, AttributeData> table = IBAUtil.getIBAAttributes(iba);
	
			Enumeration<String> eu = table.keys();
			while (eu.hasMoreElements()) {
				Map<String,String> map = new HashMap<String,String>();
	            String key = (String) eu.nextElement();
	            AttributeData value = (AttributeData)table.get(key);
	            map.put("key", key);
	            map.put("name", value.displayName);
	            map.put("type", value.dataType);
	            
	            list.add(map);
			}
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu13");
		model.addObject("module", "workprocess");
		model.setViewName("default:/workprocess/changeIBA");
		model.addObject("list", list);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/changeIBAAction")
	public ResultData changeIBAAction(HttpServletRequest request, HttpServletResponse response) {
		return GroupwareHelper.service.changeIBAAction(request, response);
	}
	
	@RequestMapping("/multiPublishing")
	public ModelAndView multiPublishing(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu14");
		model.addObject("module", "workprocess");
		model.setViewName("default:/workprocess/multiPublishing");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/multiPublishingAction")
	public Map<String,String> multiPublishingAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> map = GroupwareHelper.service.multiPublishingAction(request,response);
		return map;
	}
	
	@RequestMapping("/include_mailUser")
	public ModelAndView include_mailUser(HttpServletRequest request, HttpServletResponse response){
		String workOid = request.getParameter("workOid");
		List<Map<String,String>> list = null;
		try {
			list = GroupwareHelper.service.include_mailUser(workOid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("workOid", workOid);
		model.setViewName("include:/workprocess/include_mailUser");
		return model;
	}
	
	@RequestMapping("/emailUserList")
	public ModelAndView emailUserList(HttpServletRequest request, HttpServletResponse response) {
		String workOid = request.getParameter("workOid");
		ModelAndView model = new ModelAndView();
		model.addObject("workOid", workOid);
		model.setViewName("popup:/workprocess/emailUserList");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/emailUserListAction")
	public String emailUserListAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String xml = GroupwareHelper.service.emailUserListAction(request, response);
		return xml;
	}
	
	@ResponseBody
	@RequestMapping("/emailUserListNewAction")
	public Map<String,Object> admin_mailNewAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = null;
		
		try {
			map = AdminHelper.service.admin_mailNewAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/emailUserAction")
	public Map<String,Object> emailUserAction(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		boolean result = false;
		
		String workOid = StringUtil.checkNull(request.getParameter("workOid"));
		String userOid = StringUtil.checkNull(request.getParameter("userOid"));
		String command = StringUtil.checkNull(request.getParameter("command"));
		String linkOid = StringUtil.checkNull(request.getParameter("linkOid"));
		
		//System.out.println("-=============================  command =========================================");
		//System.out.println("== Command >>>  " + command);
		//System.out.println("== userOid >>>  " + userOid);
		//System.out.println("== workOid >>>  " + workOid);
		//System.out.println("== linkOid >>>  " + linkOid);
		
		try {
		
			if("addMaileUser".equals(command)){
				
				HashMap hash = new HashMap();
				hash.put("workOid", workOid);
				hash.put("userOid", userOid);
				Vector<MailWTobjectLink> vec = MailUserHelper.service.createMailUserLink(hash);
				//message = "true";
				
				List<Map<String,String>> list = new ArrayList<Map<String,String>>();
				for(MailWTobjectLink link : vec){
					Map<String,String> m = new HashMap<String,String>();
					m.put("oid", link.getPersistInfo().getObjectIdentifier().toString());
					m.put("name", link.getUser().getName());
					m.put("email", link.getUser().getEmail());
					
					list.add(m);
				}
				
				map.put("list", list);
				
			}else if ("delMailUser".equals(command)){
				
				String[] linkOids = linkOid.split(",");
				for(String a : linkOids) {
					//System.out.println("----------------------  a :: " + a + "|||||||||||||||||||||||");
				}
				
				HashMap hash = new HashMap();
				hash.put("linkOid", linkOid);
				MailUserHelper.service.deleteMailUserLink(hash);
				
				map.put("linkOid", linkOid);
			}
		
			result = true;
		}catch(Exception e) {
			e.printStackTrace();
			result = false;
		}
		map.put("result", result);
		return map;
		
	}
	
	@ResponseBody
	@RequestMapping(value="batchReceiveAction")
	public ResultData batchReceiveAction(@RequestBody Map<String, Object> reqMap, HttpServletResponse response) {
		ResultData returnData = new ResultData();
		try {
			
			GroupwareHelper.service.batchReceiveAction(reqMap);
			
			returnData.setResult(true);
			returnData.setMessage("일괄 수신 완료 하였습니다.");
			
		} catch(Exception e) {
			
			returnData.setResult(false);
			returnData.setMessage(e.getLocalizedMessage());
			
			e.printStackTrace();
		}
		
		
		return returnData;
	}
	
}
