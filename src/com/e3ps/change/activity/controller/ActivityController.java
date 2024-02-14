package com.e3ps.change.activity.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.org.service.OrgHelper;
import com.e3ps.part.bom.service.BomHelper;
import com.e3ps.workspace.dto.EcaDTO;

import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.part.WTPart;

@Controller
@RequestMapping(value = "/activity/**")
public class ActivityController extends BaseController {

	@Description(value = "설계변경 활동 리스트")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray slist = NumberCodeHelper.manager.toJson("EOSTEP");
		JSONArray ulist = OrgHelper.manager.toJsonWTUser();
		JSONArray alist = ActivityHelper.manager.toJsonActMap();
		ArrayList<DefDTO> list = ActivityHelper.manager.root();
		model.addObject("slist", slist);
		model.addObject("ulist", ulist);
		model.addObject("list", list);
		model.addObject("alist", alist);
		model.setViewName("/extcore/jsp/change/activity/activity-list.jsp");
		return model;
	}

	@Description(value = "설계변경 활동 실행")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ActivityHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변활동 삭제")
	@ResponseBody
	@DeleteMapping(value = "/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ActivityHelper.service.delete(params);
			if ((boolean) result.get("success")) {
				result.put("msg", DELETE_MSG);
				result.put("result", SUCCESS);
			} else {
				result.put("result", FAIL);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변루트 및 활동 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam String type, @RequestParam(required = false) String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		if ("act".equals(type)) {
			ArrayList<NumberCode> list = NumberCodeHelper.manager.getArrayCodeList("EOSTEP");
			Map<String, String> actMap = ActivityHelper.manager.getActMap();
			model.addObject("oid", oid);
			model.addObject("list", list);
			model.addObject("actMap", actMap);
			model.setViewName("popup:/change/activity/activity-create");
		} else if ("root".equals(type)) {
			model.setViewName("popup:/change/activity/root-create");
		}
		return model;
	}

	@Description(value = "설계변경 루트 및 활동 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변활동 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			HashMap<String, ArrayList<LinkedHashMap<String, Object>>> dataMap = new HashMap<>();
			dataMap.put("editRows", editRows); // 수정행
			dataMap.put("removeRows", removeRows); // 삭제행

			ActivityHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변루트 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		DefDTO dto = new DefDTO(oid);
		model.addObject("dto", dto);
		model.setViewName("popup:/change/activity/root-modify");
		return model;
	}

	@Description(value = "설계변경 루트 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.modify(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "나의업무 -> ECA 활동 리스트")
	@GetMapping(value = "/eca")
	public ModelAndView eca() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/eca-list.jsp");
		return model;
	}

	@Description(value = "나의업무 -> ECA 활동함 조회 함수")
	@ResponseBody
	@PostMapping(value = "/eca")
	public Map<String, Object> eca(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ActivityHelper.manager.eca(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "나의업무 -> ECA 활동 업무 페이지")
	@GetMapping(value = "/info")
	public ModelAndView info(@RequestParam String activityType, @RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EcaDTO dto = new EcaDTO(oid);
		EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
		EChangeOrder eco = (EChangeOrder) eca.getEo();
		if (activityType.equals("ORDER_NUMBER")) {
			model.setViewName("/extcore/jsp/workspace/activity/orderNumber.jsp");
		} else if (activityType.equals("REVISE_BOM")) {
			ArrayList<Map<String, Object>> list = ActivityHelper.manager.getEcoRevisePart(oid);
			ArrayList<Map<String, String>> clist = ActivityHelper.manager.getEcoRefCr(oid);
			model.addObject("list", list);
			model.addObject("clist", JSONArray.fromObject(clist));
			model.setViewName("/extcore/jsp/workspace/activity/reviseBom.jsp");
		} else if (activityType.equals("DOCUMENT")) {
			model.setViewName("/extcore/jsp/workspace/activity/document.jsp");
		}
		model.addObject("eco", eco);
		model.addObject("dto", dto);
		return model;
	}

	@Description(value = "설계변경 활동 산출물 링크등록")
	@ResponseBody
	@PostMapping(value = "/saveLink")
	public Map<String, Object> saveLink(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.saveLink(params);
			result.put("msg", "산출물이 등록 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설계변경 활동 산출물 링크삭제")
	@ResponseBody
	@DeleteMapping(value = "/deleteLink")
	public Map<String, Object> deleteLink(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.deleteLink(oid);
			result.put("msg", "산출물 링크가 삭제 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설계변경 활동 완료")
	@ResponseBody
	@PostMapping(value = "/complete")
	public Map<String, Object> complete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.complete(params);
			result.put("msg", "설계변경 활동이 완료되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 품목 교체 페이지 설변활동 중")
	@GetMapping(value = "/replace")
	public ModelAndView replace(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
		EChangeOrder eco = (EChangeOrder) eca.getEo();
		JSONArray list = ActivityHelper.manager.getEcoParts(oid);
		model.addObject("list", list);
		model.addObject("eco", eco);
		model.addObject("oid", eco.getPersistInfo().getObjectIdentifier().getStringValue());
		model.setViewName("popup:/change/activity/activity-replace-part");
		return model;
	}

	@Description(value = "ECO 품목 교체 함수 설변활동 중")
	@PostMapping(value = "/replace")
	@ResponseBody
	public Map<String, Object> replace(@RequestBody Map<String, Object> params) throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = (ArrayList<LinkedHashMap<String, Object>>) params
				.get("addRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = (ArrayList<LinkedHashMap<String, Object>>) params
				.get("removeRows");
		String oid = (String) params.get("oid");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			result = ActivityHelper.service.replace(addRows, removeRows, oid);

			if ((boolean) result.get("isExist")) {
				result.put("result", FAIL);
				return result;
			}

			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 품목 개정 페이지 설변활동 중")
	@GetMapping(value = "/revise")
	public ModelAndView revise(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/change/activity/activity-revise-part");
		return model;
	}

	@Description(value = "ECO 개정 품목 데이터 가져오기")
	@PostMapping(value = "/load")
	@ResponseBody
	public Map<String, Object> load(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = ActivityHelper.manager.load(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 품목 일괄 개정 함수")
	@PostMapping(value = "/revise")
	@ResponseBody
	public Map<String, Object> revise(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.revise(params);
			result.put("msg", "일괄 개정 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 품목 그리드서 개정 함수")
	@PostMapping(value = "/_revise")
	@ResponseBody
	public Map<String, Object> _revise(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service._revise(params);
			result.put("msg", "개정 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 이전 품목 추가 페이지, 설변활동 중")
	@GetMapping(value = "/prePart")
	public ModelAndView prePart(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/change/activity/activity-prev-part");
		return model;
	}

	@Description(value = "ECO 이전품목")
	@PostMapping(value = "/prev")
	@ResponseBody
	public Map<String, Object> prev(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.prev(params);
			result.put("msg", "이전품목이 추가 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECO 그룹핑 함수")
	@PostMapping(value = "/saveData")
	@ResponseBody
	public Map<String, Object> saveGroup(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ActivityHelper.service.saveData(params);
			result.put("msg", "저장되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설변활동중 주도면 및 참조도면")
	@GetMapping(value = "/reference")
	public ModelAndView reference(@RequestParam String pre, @RequestParam String next) throws Exception {
		ModelAndView model = new ModelAndView();
		Map<String, ArrayList<Map<String, Object>>> preData = ActivityHelper.manager.reference(pre);
		Map<String, ArrayList<Map<String, Object>>> nextData = ActivityHelper.manager.reference(next);
		model.addObject("preData", preData);
		model.addObject("nextData", nextData);
		model.setViewName("popup:/change/activity/activity-reference");
		return model;
	}

	@Description(value = "설변활동중 BOM 에디터")
	@GetMapping(value = "/editor")
	public ModelAndView editor(@RequestParam String oid, @RequestParam String eoid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(eoid);
		EChangeOrder eco = (EChangeOrder) eca.getEo();
		JSONArray tree = BomHelper.manager.loadEditor(root, true);
		model.addObject("oid", oid);
		model.addObject("eco", eco);
		model.addObject("eoid", eco.getPersistInfo().getObjectIdentifier().getStringValue());
		model.addObject("tree", tree);
		model.addObject("root", root);
		model.setViewName("popup:/change/activity/activity-bom-editor");
		return model;
	}

	@Description(value = "결재관련 함들 개수 업데이트")
	@ResponseBody
	@GetMapping(value = "/update")
	public Map<String, Object> update() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			int count = ActivityHelper.manager.count();
			result.put("count", count);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "설계변경 활동 위임")
	@ResponseBody
	@GetMapping(value = "/reassign")
	public Map<String, Object> reassign(@RequestParam String oid, @RequestParam String aoid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WTUser user = (WTUser) CommonUtil.getObject(oid);
			EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(aoid);
			ActivityHelper.service.reassign(eca, user);
			result.put("msg", user.getFullName() + "사용자 에게 ECA활동이 위임 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "ECA 관련 품목 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi,
			@RequestParam(required = false) String complete) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_PART");
		ModelAndView model = new ModelAndView();
		model.addObject("complete", Boolean.parseBoolean(complete));
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/change/activity/activity-part-list-popup");
		return model;
	}

	@Description(value = "설변품목 더미 제외여부")
	@ResponseBody
	@GetMapping(value = "/reloadData")
	public Map<String, Object> reloadData(@RequestParam String oid, @RequestParam String skip) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			boolean isSkip = Boolean.parseBoolean(skip);
			ArrayList<Map<String, Object>> list = ActivityHelper.manager.getEcoRevisePart(oid, isSkip);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
