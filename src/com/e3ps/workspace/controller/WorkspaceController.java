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
import com.e3ps.workspace.service.WorkDataHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/workspace/**")
public class WorkspaceController extends BaseController {

	@Description(value = "결재선 삭제(중복)")
	@ResponseBody
	@PostMapping(value = "/removeLine")
	public Map<String, Object> removeLine(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.removeLine(params);
			result.put("msg", "삭제 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "외부 메일 다시 가져오기")
	@ResponseBody
	@GetMapping(value = "/reloadMail")
	public Map<String, Object> reloadMail(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = WorkspaceHelper.manager.getExternalMail(oid);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "외부 메일 삭제")
	@ResponseBody
	@PostMapping(value = "/removeMail")
	public Map<String, Object> removeMail(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.removeMail(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "외부 메일 저장")
	@ResponseBody
	@PostMapping(value = "/mailSave")
	public Map<String, Object> mailSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.mailSave(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "합의함 리스트 페이지")
	@GetMapping(value = "/agree")
	public ModelAndView agree() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/agree-list.jsp");
		return model;
	}

	@Description(value = "합의함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/agree")
	public Map<String, Object> agree(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.agree(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/approval", "결재함 조회 함수");
		}
		return result;
	}

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
	public ModelAndView lineView(@RequestParam String oid, @RequestParam String columnType) throws Exception {
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
	public ModelAndView masterView(@RequestParam String oid, @RequestParam String columnType) throws Exception {
		ModelAndView model = new ModelAndView();
		ApprovalMaster master = (ApprovalMaster) CommonUtil.getObject(oid);
		ApprovalLineDTO dto = new ApprovalLineDTO(master);
//		if (columnType.equals("COLUMN_PROGRESS")) { // 진행
//			dto = WorkspaceHelper.manager.ingLine(master);
//		} else if (columnType.equals("COLUMN_COMPLETE")) { // 완료
//			dto = WorkspaceHelper.manager.completeLine(master);
//		} else if (columnType.equals("COLUMN_REJECT")) { // 반려
//			dto = WorkspaceHelper.manager.rejectLine(master);
//		}
		System.out.println("dto=" + dto);
		model.addObject("dto", dto);
		model.addObject("oid", oid);
		model.setViewName("popup:/workspace/master-view");
		return model;
	}

	@Description(value = "수신 처리")
	@ResponseBody
	@PostMapping(value = "/_receive")
	public Map<String, Object> _receive(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._receive(params);
			result.put("msg", "수신확인 처리 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수신함 일괄 수신 처리")
	@ResponseBody
	@PostMapping(value = "/receives")
	public Map<String, Object> receives(@RequestBody Map<String, ArrayList<Map<String, String>>> params)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.receives(params);
			result.put("msg", "일괄 수신확인 처리 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/reject", "반려함 조회 함수");
		}
		return result;
	}

	@Description(value = "결재 읽음 처리")
	@ResponseBody
	@GetMapping(value = "/read")
	public Map<String, Object> read(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.read(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "외부 메일 페이지")
	@GetMapping(value = "/mail")
	public ModelAndView mail() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/workspace/mail-list-popup");
		return model;
	}

	@Description(value = "합의 함수")
	@ResponseBody
	@PostMapping(value = "/_agree")
	public Map<String, Object> _agree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._agree(params);
			result.put("msg", AGREE_SUCCESS_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "위임 함수")
	@ResponseBody
	@PostMapping(value = "/delegate")
	public Map<String, Object> delegate(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.delegate(params);
			result.put("result", SUCCESS);
			result.put("msg", "결재가 위임 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/delete", "개인결재선 삭제 함수");
		}
		return result;
	}

	@Description(value = "합의반려 함수")
	@ResponseBody
	@PostMapping(value = "/_unagree")
	public Map<String, Object> _unagree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service._unagree(params);
			result.put("result", SUCCESS);
			result.put("msg", AGREE_REJECT_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "결재 회수")
	@ResponseBody
	@GetMapping(value = "/withdraw")
	public Map<String, Object> withdraw(@RequestParam String oid, @RequestParam String remove) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkspaceHelper.service.withdraw(oid, remove);
			result.put("msg", "결재가 회수 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
//			ErrorLogHelper.service.create(e.toString(), "/workspace/_reset", "결재 초기화 함수");
		}
		return result;
	}

	@Description(value = "결재 이력 팝업")
	@GetMapping(value = "/history")
	public ModelAndView history(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("oid", oid);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/workspace/include/approval-history");
		return model;
	}

	@Description(value = "결재관련 함들 개수 업데이트")
	@ResponseBody
	@GetMapping(value = "/update")
	public Map<String, Object> update() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Integer> count = WorkspaceHelper.manager.count();

			result.put("approval", count.get("approval"));
			result.put("agree", count.get("agree"));
			result.put("receive", count.get("receive"));
			result.put("complete", count.get("complete"));
			result.put("reject", count.get("reject"));
			result.put("progress", count.get("progress"));
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
