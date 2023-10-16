package com.e3ps.workspace.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.org.Department;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.dto.ApprovalLineDTO;
import com.e3ps.workspace.dto.ApprovalMasterDTO;
import com.e3ps.workspace.service.WorkspaceHelper;

@Controller
@RequestMapping(value = "/workspace/**")
public class WorkspaceController extends BaseController {

	@Description(value = "결재함 리스트 페이지")
	@GetMapping(value = "/approval")
	public ModelAndView approval() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/approval-list.jsp");
		return model;
	}

	@Description(value = "결재함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/approval")
	public Map<String, Object> approval(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.approval(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/approval", "결재함 조회 함수");
		}
		return result;
	}

	@Description(value = "수신함 페이지")
	@GetMapping(value = "/receive")
	public ModelAndView receive() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/receive-list.jsp");
		return model;
	}

	@Description(value = "수신함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/receive")
	public Map<String, Object> receive(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.receive(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/receive", "수신함 조회 함수");
		}
		return result;
	}

	@Description(value = "진행함 리스트 페이지")
	@GetMapping(value = "/progress")
	public ModelAndView progress() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/progress-list.jsp");
		return model;
	}

	@Description(value = "진행함 조회 함수")
	@PostMapping(value = "/progress")
	@ResponseBody
	public Map<String, Object> progress(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.progress(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/progress", "결재함 조회 함수");
		}
		return result;
	}

	@Description(value = "완료함 리스트 페이지")
	@GetMapping(value = "/complete")
	public ModelAndView complete() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/complete-list.jsp");
		return model;
	}

	@Description(value = "완료함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/complete")
	public Map<String, Object> complete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.complete(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/complete", "완료함 조회 함수");
		}
		return result;
	}

	@Description(value = "반려함 페이지")
	@GetMapping(value = "/reject")
	public ModelAndView reject() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/reject-list.jsp");
		return model;
	}

	@Description(value = "반려함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/reject")
	public Map<String, Object> reject(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.reject(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/reject", "반려함 조회 함수");
		}
		return result;
	}

	@Description(value = "결재선 지정 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup() throws Exception {
		ModelAndView model = new ModelAndView();
		Department root = DepartmentHelper.manager.getRoot();
		model.addObject("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		model.setViewName("popup:/workspace/register-popup");
		return model;
	}

	@Description(value = "개인결재선 조회 함수")
	@ResponseBody
	@PostMapping(value = "/loadLine")
	public Map<String, Object> loadLine(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.loadLine(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "개인결재선 저장 함수")
	@ResponseBody
	@PostMapping(value = "/save")
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			result = WorkspaceHelper.manager.validate(params);
			if ((boolean) result.get("validate")) {
				result.put("result", FAIL);
				return result;
			}

			WorkspaceHelper.service.save(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "개인결재선 삭제 함수")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.delete(oid);
			result.put("result", SUCCESS);
			result.put("msg", DELETE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/delete", "개인결재선 삭제 함수");
		}
		return result;
	}

	@Description(value = "개인결재선 즐겨찾기 저장 함수")
	@ResponseBody
	@PostMapping(value = "/favorite")
	public Map<String, Object> favorite(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.favorite(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/favorite", "개인결재선 즐겨찾기 저장 함수");
		}
		return result;
	}

	@Description(value = "개인결재선 즐겨찾기 불러오는 함수")
	@ResponseBody
	@PostMapping(value = "/loadFavorite")
	public Map<String, Object> loadFavorite() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.loadFavorite();
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/loadFavorite", "개인결재선 즐겨찾기 불러오는 함수");
		}
		return result;
	}

	@Description(value = "개인결재선 불러오는 함수")
	@ResponseBody
	@GetMapping(value = "/loadFavorite")
	public Map<String, Object> loadFavorite(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.loadFavorite(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/loadFavorite", "개인결재선 불러오는 함수");
		}
		return result;
	}

	@Description(value = "결재 초기화 함수")
	@ResponseBody
	@PostMapping(value = "/_reset")
	public Map<String, Object> _reset(@RequestBody Map<String, ArrayList<String>> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._reset(params);
			result.put("msg", "결재가 초기화 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/_reset", "결재 초기화 함수");
		}
		return result;
	}

	@Description(value = "승인 함수")
	@ResponseBody
	@PostMapping(value = "/_approval")
	public Map<String, Object> _approval(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._approval(params);
			result.put("msg", APPROVAL_SUCCESS_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/_approval", "승인 함수");
		}
		return result;
	}

	@Description(value = "반려 함수")
	@ResponseBody
	@PostMapping(value = "/_reject")
	public Map<String, Object> _reject(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._reject(params);
			result.put("msg", REJECT_SUCCESS_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/_reject", "반려 함수");
		}
		return result;
	}

	@Description(value = "결재 정보 보기")
	@GetMapping(value = "/lineView")
	public ModelAndView lineView(@RequestParam String oid, @RequestParam String columnType, @RequestParam String poid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
		ApprovalLineDTO dto = new ApprovalLineDTO(line);
		model.addObject("dto", dto);
		model.addObject("oid", oid);
		model.setViewName("popup:/workspace/line-view");
		return model;
	}

	@Description(value = "결재 정보 보기")
	@GetMapping(value = "/masterView")
	public ModelAndView masterView(@RequestParam String oid, @RequestParam String columnType, @RequestParam String poid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		ApprovalMaster master = (ApprovalMaster) CommonUtil.getObject(oid);
		ApprovalMasterDTO dto = new ApprovalMasterDTO(master);
		model.addObject("dto", dto);
		model.addObject("oid", oid);
		model.setViewName("popup:/workspace/master-view");
		return model;
	}

}
