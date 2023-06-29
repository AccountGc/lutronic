package com.e3ps.groupware.workprocess.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.workprocess.service.WFItemHelper;

@Controller
@RequestMapping("/WFItem")
public class WFItemController {
	/*
	public String[] projectStateValue = {StateFlag.TASK_STATE_READY, 
			StateFlag.TASK_STATE_PROGRESS, 
			StateFlag.TASK_STATE_MODIFY, 
			StateFlag.TASK_STATE_COMPLETED, 
			StateFlag.TASK_STATE_STOP,
			StateFlag.TASK_STATE_STOPPING,
			StateFlag.TASK_STATE_CANCELLED
			};
	public String[] projectStateName = {
			Message.get("준비중"),
			Message.get("진행중"),
			Message.get("수정중"),
			Message.get("완료됨"),
			Message.get("중단됨"),
			Message.get("중단중"),
			Message.get("취소됨")
			};
	*/
	/** lifecycle 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/lifecycleList")
	public List<Map<String,String>> lifecycleList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		String lifecycle = request.getParameter("lifecycle");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"),"");
		
		try {
			if(!StringUtil.checkString(lifecycle)) {
				lifecycle = "LC_Default";
			}
			list = WFItemHelper.service.getLifeCycleState(lifecycle,state);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		return list;
	}
	
}
