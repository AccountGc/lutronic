package com.e3ps.part.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.ptc.wvs.server.util.PublishUtils;

import net.sf.json.JSONArray;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.fc.ReferenceFactory;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Controller
@RequestMapping(value = "/part/**")
public class PartController extends BaseController {

	
	@Description(value = "EO 완제품 품목 팝업 페이지")
	@GetMapping(value = "/complete")
	public ModelAndView complete(@RequestParam String method, @RequestParam String multi,
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
		model.setViewName("popup:/part/complete-part-list-popup");
		return model;
	}

	
	@Description(value = "품목 썸네일 팝업")
	@GetMapping(value = "/viewThumb")
	public ModelAndView viewThumb(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/common/thumbnail-popup-3d.jsp");
		model.addObject("oid", oid);
		return model;
	}

	@Description(value = "썸네일 여부 체크")
	@ResponseBody
	@GetMapping(value = "/checkThumb")
	public Map<String, Object> checkThumb(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			Representation representation = PublishUtils.getRepresentation(part);
			if (representation == null) {
				result.put("exist", false);
			} else {
				result.put("exist", true);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			result.put("msg", e.toString());
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "새이름으로 저장")
	@GetMapping(value = "/saveAs")
	public ModelAndView saveAs() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-saveAs.jsp");
		return model;
	}

	@Description(value = "크레오 뷰 URL 얻기")
	@ResponseBody
	@GetMapping(value = "/getCreoViewUrl")
	public Map<String, Object> getCreoViewUrl(HttpServletRequest request, @RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			String url = PartHelper.manager.getCreoViewUrl(request, oid);
			result.put("result", SUCCESS);
			result.put("url", url);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "새 채번 페이지")
	@GetMapping(value = "/order")
	public ModelAndView order(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<NumberCodeDTO> partType = CodeHelper.service.topCodeToList("PARTTYPE");
		List<Map<String, Object>> list = null;
		try {
			list = BomSearchHelper.service.updateAUIPartChangeListGrid(oid, false);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("size", list.size());
		model.addObject("partType", partType);

		model.setViewName("/extcore/jsp/part/part-order.jsp");
		return model;
	}

	@Description(value = "품목 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_PART");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();

		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/part/part-list.jsp");
		return model;
	}

	@Description(value = "관련 품목 팝업 페이지")
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
		model.setViewName("popup:/part/part-list-popup");
		return model;
	}

	@Description(value = "품목 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("/extcore/jsp/part/part-create.jsp");
		return model;
	}

	@Description(value = "일괄 등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception {
		JSONArray folderList = PartHelper.manager.recurcive();
		JSONArray modelList = NumberCodeHelper.manager.toJson("MODEL");
		JSONArray deptcodeList = NumberCodeHelper.manager.toJson("DEPTCODE");
		JSONArray matList = NumberCodeHelper.manager.toJson("MAT");
		JSONArray productmethodList = NumberCodeHelper.manager.toJson("PRODUCTMETHOD");
		JSONArray partType1List = NumberCodeHelper.manager.toPartType1();
		JSONArray unitList = PartHelper.manager.toUnitJson();
		ModelAndView model = new ModelAndView();
		model.addObject("folderList", folderList);
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("partType1List", partType1List);
		model.addObject("unitList", unitList);
		model.setViewName("/extcore/jsp/part/part-batch.jsp");
		return model;
	}

	@Description(value = "일괄등록 실행")
	@ResponseBody
	@PostMapping(value = "/batch")
	public Map<String, Object> batch(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.batch(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	/**
	 * 품목 단위 리스트 리턴
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getQuantityUnit")
	public List<String> getQuantityUnit(HttpServletRequest request, HttpServletResponse response) {
		List<String> list = PartHelper.service.getQuantityUnit();
		return list;
	}

	@Description(value = "품목 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "부품 등록시 SEQ 버튼")
	@GetMapping(value = "/seq")
	public ModelAndView seq(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String partNumber = request.getParameter("partNumber");
		model.addObject("partNumber", partNumber);
		model.setViewName("popup:/part/seq-list");
		return model;
	}

	@Description(value = "품목 데이터 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = PartHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

//	/** 품목 데이터 검색
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listPartAction")
//	public Map<String,Object> listPartAction(HttpServletRequest request, HttpServletResponse response) {
//		Map<String,Object> result = null;
//		try {
//			result = PartHelper.service.listPartAction(request, response);
//		} catch(Exception e) {
//			e.printStackTrace();
//			result = new HashMap<String,Object>();
//		}
//		return result;
//	}

	@Description(value = "품목 상세보기")
	@RequestMapping("/view")
	public ModelAndView view(@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartDTO dto = new PartDTO(part);
		Map<String, String> map = CommonHelper.manager.getAttributes(oid, "view");

		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("dto", dto);
		model.addAllObjects(map);
		model.setViewName("popup:/part/part-view");
		return model;
	}

	/**
	 * 품목 상세보기
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewPart")
	public ModelAndView viewPart(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartData partData = new PartData(part);
		model.addObject("oid", oid);
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("partData", partData);
		model.setViewName("popup:/part/viewPart");
		return model;
	}

	@Description(value = "댓글 등록 함수")
	@ResponseBody
	@PostMapping(value = "/createComments")
	public Map<String, Object> createComments(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.createComments(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "댓글 수정 함수")
	@ResponseBody
	@PostMapping(value = "/updateComments")
	public Map<String, Object> updateComments(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.updateComments(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "댓글 삭제 함수")
	@ResponseBody
	@GetMapping(value = "/deleteComments")
	public Map<String, Object> deleteComments(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.deleteComments(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "품목 변경이력 조회")
	@RequestMapping("/changeList")
	public ModelAndView changeList(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartData partData = new PartData(part);

		model.addObject("oid", oid);
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("data", partData);
		model.setViewName("popup:/part/part-changeList");
		return model;
	}

	@Description(value = "품목 삭제")
	@ResponseBody
	@GetMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = PartHelper.service.delete(oid);
		if ((boolean) result.get("success")) {
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} else {
			result.put("result", FAIL);
			result.put("msg", (String) result.get("msg"));
		}
		return result;
	}

	@Description(value = "품목 수정 페이지")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartData partData = null;
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		try {
			partData = new PartData(part);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("data", partData);
		model.addObject("modelList", modelList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("popup:/part/part-update");
		return model;
	}

	@Description(value = "품목 수정")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> update(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.service.updatePartAction(params);
		if ((boolean) result.get("result")) {
			result.put("oid", result.get("oid"));
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} else {
			result.put("result", FAIL);
			result.put("msg", (String) result.get("msg"));
		}
		return result;
	}

	/**
	 * 일괄등록 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackagePart")
	public ModelAndView createPackagePart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "part");
		model.setViewName("default:/part/createPackagePart");
		return model;
	}

	/**
	 * 일괄등록 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createAUIPackagePart")
	public ModelAndView createAUIPackagePart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "part");
		model.setViewName("default:/part/createAUIPackagePart");
		return model;
	}

	/**
	 * 일괄등록 실행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackagePartAction")
	public ModelAndView createPackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = "";

		xmlString = PartHelper.service.createPackagePartAction(request, response);

		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/part/createPackagePartAction");
		return model;
	}

	/**
	 * 일괄등록 AUI 실행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	// @RequestMapping("/createAUIPackagePartAction")
	// public ModelAndView createAUIPackagePartAction(@RequestBody
	// Map<String,Object> param) {
	@ResponseBody
	@RequestMapping(value = "/createAUIPackagePartAction", method = RequestMethod.POST)
	public ResultData createAUIPackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.createAUIPackagePartAction(request, response);
	}

	@Description(value = "채번 페이지 이동")
	@GetMapping(value = "/change")
	public ModelAndView change(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, String>> list = NumberCodeHelper.manager.getOneLevel("PARTTYPE");
		ArrayList<Map<String, String>> partName1 = NumberCodeHelper.manager.getOneLevel("PARTNAME1");
		ArrayList<Map<String, String>> partName2 = NumberCodeHelper.manager.getOneLevel("PARTNAME2");
		ArrayList<Map<String, String>> partName3 = NumberCodeHelper.manager.getOneLevel("PARTNAME3");
		model.addObject("partName1", JSONArray.fromObject(partName1));
		model.addObject("partName2", JSONArray.fromObject(partName2));
		model.addObject("partName3", JSONArray.fromObject(partName3));
		model.addObject("list", JSONArray.fromObject(list));
		model.addObject("oid", oid);
		model.setViewName("popup:/part/part-change");
		return model;
	}

	@Description(value = "채번 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/order")
	public Map<String, Object> order(@RequestBody Map<String, Object> params) throws Exception {
		List<Map<String, Object>> list = null;
		Map<String, Object> result = new HashMap<String, Object>();
		List<NumberCodeDTO> partType = CodeHelper.service.topCodeToList("PARTTYPE");
		String oid = (String) params.get("oid");
		try {
			list = PartHelper.manager.order(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		result.put("oid", oid);
		result.put("list", list);
		result.put("partType", partType);
		return result;
	}

	@Description(value = "채번(새버전) 페이지 이동")
	@GetMapping(value = "/updateAUIPartChange")
	public ModelAndView updateAUIPartChange(@RequestParam String oid) {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/part/updateAUIPartChange");
		return model;
	}

	@Description(value = "채번(새버전) 페이지 이동")
	@ResponseBody
	@PostMapping(value = "/updateAUIPartChange")
	public Map<String, Object> updateAUIPartChange(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");
		/*
		 * List<Map<String,Object>> list = null;
		 * 
		 * try { list = PartHelper.service.partChange(partOid); } catch(Exception e) {
		 * e.printStackTrace(); list = new ArrayList<Map<String,Object>>(); }
		 */
		List<NumberCodeDTO> partType = CodeHelper.service.topCodeToList("PARTTYPE");

		String checkDummy = (String) params.get("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String, Object>> list = null;
		// System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIPartChangeListGrid(oid, false);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		result.put("oid", oid);
		result.put("list", list);
		result.put("size", list.size());
		result.put("partType", partType);

		return result;
	}
//	/**
//	 * 채번 페이지 AUI Greid 적용 이동
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/updateAUIPartChange")
//	public ModelAndView updateAUIPartChange(HttpServletRequest request, HttpServletResponse response) {

//	}

	/**
	 * 채번 페이지 AUI 수정 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAUIPartChangeAction")
	public ResultData updateAUIPartChangeAction(@RequestBody Map<String, Object> param) {
		return PartHelper.service.updateAUIPartChangeAction(param);
	}

	@ResponseBody
	@RequestMapping("/updateAUIPartChangeSearchAction")
	public List<Map<String, Object>> updateAUIPartChangeSearchAction(@RequestBody Map<String, Object> params) {
		String oid = (String) params.get("oid");
		String checkDummy = (String) params.get("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String, Object>> list = null;
		// System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIPartChangeListGrid(oid, false);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		return list;
	}

	/**
	 * 채번 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/actionBom")
	public ResultData actionBom(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.changeNumber(request);
	}

	/**
	 * 일괄수정 페이지 이동
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updatePackagePart")
	public ModelAndView updatePackagePart(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");

		List<Map<String, Object>> list = null;

		try {
			list = PartHelper.service.partBomListGrid(oid);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		List<NumberCodeDTO> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeDTO> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeDTO> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeDTO> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeDTO> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeDTO> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
		List<String> unit = PartHelper.service.getQuantityUnit();

		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("productmethod", productmethod);
		model.addObject("deptCode", deptCode);
		model.addObject("model", modelcode);
		model.addObject("manufacture", manufacture);
		model.addObject("mat", mat);
		model.addObject("finish", finish);
		model.addObject("unit", unit);

		model.setViewName("popup:/part/updatePackagePart");
		return model;
	}

	@Description(value = "일괄 수정 ")
	@GetMapping(value = "/updateAUIPackagePart")
	public ModelAndView updateAUIPackagePart(@RequestParam String oid) {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/part/updateAUIPackagePart");
		return model;
	}

	@ResponseBody
	@RequestMapping("/updateAUIPackageSearchAction")
	public List<Map<String, Object>> updateAUIPackageSearchAction(HttpServletRequest request,
			HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String, Object>> list = null;
		// System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIBomListGrid(oid, isCheckDummy);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		return list;
	}

	/**
	 * 일괄 수정
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchAtttribute")
	public ModelAndView batchAtttribute(HttpServletRequest request, HttpServletResponse response) {

		List<NumberCodeDTO> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeDTO> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeDTO> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeDTO> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeDTO> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeDTO> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
		List<String> unit = PartHelper.service.getQuantityUnit();

		ModelAndView model = new ModelAndView();

		model.addObject("productmethod", productmethod);
		model.addObject("deptCode", deptCode);
		model.addObject("model", modelcode);
		model.addObject("manufacture", manufacture);
		model.addObject("mat", mat);
		model.addObject("finish", finish);
		model.addObject("unit", unit);

		model.setViewName("popup:/part/batchAtttribute");
		return model;
	}

	/**
	 * 일괄 채번 수정
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchPartNumber")
	public ModelAndView batchPartNumber(HttpServletRequest request, HttpServletResponse response) {
		List<NumberCodeDTO> gubunList = CodeHelper.service.numberCodeList("PARTTYPE", "", false);

		ModelAndView model = new ModelAndView();
		model.addObject("gubunList", gubunList);
		model.setViewName("popup:/part/batchPartNumber");
		return model;
	}

	/**
	 * 일괄 등록
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchCreate")
	public ModelAndView batchCreate(HttpServletRequest request, HttpServletResponse response) {

		String auiId = StringUtil.checkNull(request.getParameter("auiId"));
		String mode = StringUtil.checkNull(request.getParameter("mode")); // single

		String title = "일괄 추가 ";
		if (mode.equals("single")) {
			title = "수정[" + auiId + "]";
		}
		// System.out.println("batchCreate auiId =" + auiId);

		ModelAndView model = new ModelAndView();
		model.addObject("auiId", auiId);
		model.addObject("mode", mode);
		model.addObject("title", title);

		model.setViewName("popup:/part/batchCreate");
		return model;
	}

	/**
	 * 부품 상태 수정
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/partStateChange")
	public ResultData partStateChange(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.partStateChange(request, response);
	}

	/**
	 * 부품 일괄 수정 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePackagePartAction")
	public ResultData updatePackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.updatePackagePartAction(request, response);
	}

	/**
	 * 부품 일괄 수정 수행
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAUIPackagePartAction")
	public ResultData updateAUIPackagePartAction(@RequestBody Map<String, Object> params) {
		return PartHelper.service.updateAUIPackagePartAction(params);
	}

	/**
	 * EO에서 완제품 품번 선택
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_CompletePartSelect")
	public ModelAndView include_CompletePartSelect(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String maxcnt = StringUtil.checkNull(request.getParameter("maxcnt"));
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String form = request.getParameter("form");
		boolean isBom = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("isBom"), "false"));
		boolean selectBom = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("selectBom"), "false"));
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");

		List<PartData> list = null;

		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		list = ECOSearchHelper.service.getCompletePartDataList(eco);

		ModelAndView model = new ModelAndView();
		model.setViewName("include:/part/include_CompletePartSelect");
		model.addObject("list", list);
		model.addObject("mode", mode);
		model.addObject("moduleType", moduleType);
		model.addObject("maxcnt", maxcnt);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("form", form);
		model.addObject("isBom", isBom);
		model.addObject("selectBom", selectBom);
		model.addObject("state", state);

		return model;
	}

	/**
	 * 
	 * LUTRONIC 추가 끝
	 * 
	 * 
	 */

	/**
	 * 관련 품목 추가
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_PartSelect")
	public ModelAndView include_PartList(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String maxcnt = StringUtil.checkNull(request.getParameter("maxcnt"));
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String form = request.getParameter("form");
		boolean isBom = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("isBom"), "false"));
		boolean selectBom = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("selectBom"), "false"));
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");

		List<PartDTO> list = null;
		list = PartHelper.service.include_PartList(oid, moduleType);

		ModelAndView model = new ModelAndView();
		model.setViewName("include:/part/include_PartSelect");
		model.addObject("list", list);
		model.addObject("mode", mode);
		model.addObject("moduleType", moduleType);
		model.addObject("maxcnt", maxcnt);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("form", form);
		model.addObject("isBom", isBom);
		model.addObject("selectBom", selectBom);
		model.addObject("state", state);

		return model;
	}

	/**
	 * 관련 품목 보기
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_PartView")
	public ModelAndView include_PartView(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String maxcnt = StringUtil.checkNull(request.getParameter("maxcnt"));
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<PartData> list = null;
		list = PartHelper.service.include_PartList(oid, moduleType);

		ModelAndView model = new ModelAndView();
		model.setViewName("include:/part/include_PartView");
		model.addObject("list", list);
		model.addObject("mode", mode);
		model.addObject("modeulType", moduleType);
		model.addObject("maxcnt", maxcnt);
		model.addObject("title", title);
		model.addObject("distribute", distribute);
		model.addObject("paramName", paramName);

		return model;
	}

	/**
	 * 품목 검색 팝업
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectPartPopup")
	public ModelAndView selectPartPopup(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");
		ModelAndView model = new ModelAndView();
		model.addObject("mode", mode);
		model.addObject("moduleType", moduleType);
		model.addObject("state", state);
		model.setViewName("popup:/part/selectPartPopup");
		return model;
	}

	@Description(value = "END ITEM 상세보기")
	@GetMapping(value = "/bomPartList")
	public ModelAndView bomPartList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();

		String oid = request.getParameter("oid");
		String bomType = request.getParameter("bomType");
		String title = null;

		Map<String, Object> params = new HashMap<>();
		params.put("oid", oid);
		params.put("bomType", bomType);

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		if ("up".equals(bomType)) {
			title = Message.get("상위품목");
		} else if ("down".equals(bomType)) {
			title = Message.get("하위품목");
		} else if ("end".equals(bomType)) {
			title = Message.get("END ITEM");
		}

		model.setViewName("popup:/part/bomPartList");
		model.addObject("oid", oid);
		model.addObject("bomType", bomType);
		model.addObject("partNumber", part.getNumber());
		model.addObject("title", title);
		return model;
	}

	@Description(value = "END ITEM 상세보기")
	@ResponseBody
	@PostMapping(value = "/bomPartList")
	public Map<String, Object> bomPartList(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.manager.bomPartList(params);
		return result;
	}
//	/** END ITEM 상세보기
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/bomPartList")
//	public ModelAndView bomPartList(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ModelAndView model = new ModelAndView();
//		
//		Map<String,Object> map = PartHelper.service.bomPartList(request,response);
//		
//		model.setViewName("popup:/part/bomPartList");
//		model.addAllObjects(map);
//		return model;
//	}

	/**
	 * BOM 상세보기
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/PartTree")
	public ModelAndView PartTree(HttpServletRequest request, HttpServletResponse response) {

		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");

		View[] views = null;

		Baseline bsobj = null;

		try {
			ReferenceFactory rf = new ReferenceFactory();

			if (baseline != null && baseline.length() > 0) {
				bsobj = (Baseline) rf.getReference(baseline).getObject();
			}

			views = ViewHelper.service.getAllViews();
		} catch (Exception e) {
			e.printStackTrace();
			bsobj = null;
		}

		if (view == null) {
			view = views[0].getName();
		}

		List<Map<String, String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch (Exception e) {
			list = new ArrayList<Map<String, String>>();
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/part/PartTree");
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		return model;
	}

	/**
	 * BOM 데이터 리스트 리턴
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPartTreeAction")
	public Map<String, Object> getPartTreeAction(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map = PartHelper.service.getPartTreeAction(request, response);
			map.put("result", true);
			map.put("msg", "OK");
		} catch (Exception e) {
			map.put("result", false);
			map.put("msg", e.getLocalizedMessage());
			e.printStackTrace();
		}

		return map;
	}

	@RequestMapping("/partTreeCompare")
	public ModelAndView partTreeCompare(HttpServletRequest request, HttpServletResponse response)
			throws WTRuntimeException, WTException {

		String oid = request.getParameter("oid");
		String oid2 = request.getParameter("oid2");
		String baseline = request.getParameter("baseline");
		String baseline2 = request.getParameter("baseline2");

		String title1 = "";
		String title2 = "";

		ManagedBaseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (ManagedBaseline) CommonUtil.getObject(baseline);
		}

		ManagedBaseline bsobj2 = null;
		if (baseline2 != null && baseline2.length() > 0) {
			bsobj2 = (ManagedBaseline) CommonUtil.getObject(baseline2);
		}

		if (bsobj == null) {
			title1 = "최신BOM 전개";
		} else {
			title1 = "Baseline전개  - " + bsobj.getName();
		}

		if (bsobj2 == null) {
			title2 = "최신BOM 전개";
		} else {
			title2 = "Baseline전개  - " + bsobj2.getName();
		}
		// System.out.println("sssssssssss");
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("oid2", oid2);
		model.addObject("baseline", baseline);
		model.addObject("baseline2", baseline2);
		model.addObject("title1", title1);
		model.addObject("title2", title2);
		model.setViewName("popup:/part/partTreeCompare");
		return model;
	}

	@ResponseBody
	@RequestMapping("/getBaseLineCompare")
	public Map<String, Object> getBaseLineCompare(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {

			map = PartHelper.service.getBaseLineCompare(request, response);

			map.put("result", true);

		} catch (Exception e) {
			map.put("result", false);
			map.put("msg", e.getLocalizedMessage());
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * BOM 일괄등록
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/excelBOMLoad")
	public ModelAndView excelBOMLoad(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu5");
		model.addObject("module", "part");
		model.setViewName("default:/part/excelBOMLoad");
		return model;
	}

	@RequestMapping("/excelBomLoadAction")
	public ModelAndView excelBomLoadAction(HttpServletRequest request, HttpServletResponse response) {

		String xmlString = "";

		try {
			xmlString = PartHelper.service.excelBomLoadAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/part/excelBOMLoadAction");
		return model;
	}

	/**
	 * 대상 품목
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ChangePartView")
	public ModelAndView include_ChangePartView(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<PartData> list = null;
		// list = PartHelper.service.include_ChangePartList(oid);
		model.setViewName("popup:/part/include_ChangePartView");
		// model.addObject("list", list);

		return model;
	}

	@RequestMapping("/partExpand")
	public ModelAndView partExpand(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		String moduleType = StringUtil.checkReplaceStr(request.getParameter("moduleType"), "");

		ModelAndView model = new ModelAndView();
		model.addObject("partOid", partOid);
		model.addObject("moduleType", moduleType);
		model.setViewName("popup:/part/partExpand");
		return model;
	}

	@RequestMapping("/partAUIExpand")
	public ModelAndView partAUIExpand(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		String moduleType = StringUtil.checkReplaceStr(request.getParameter("moduleType"), "");

		ModelAndView model = new ModelAndView();
		model.addObject("partOid", partOid);
		model.addObject("moduleType", moduleType);
		model.setViewName("popup:/part/partAUIExpand");
		return model;
	}

	@ResponseBody
	@RequestMapping("/partExpandAction")
	public List<Map<String, Object>> partExpandAction(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		String moduleType = request.getParameter("moduleType");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");

		List<Map<String, Object>> list = null;
		try {
			list = BomSearchHelper.service.partExpandAUIBomListGrid(partOid, moduleType, desc);
		} catch (Exception e) {
			list = new ArrayList<Map<String, Object>>();
			e.printStackTrace();
		}
		return list;
	}

	@RequestMapping("/selectEOPart")
	public ModelAndView selectEOPart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/part/selectEOPart");
		return model;
	}

	@ResponseBody
	@RequestMapping("/selectEOPartAction")
	public Map<String, Object> selectEOPartAction(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = null;
		try {
			map = PartHelper.service.selectEOPartAction(request, response);
		} catch (Exception e) {
			map = new HashMap<String, Object>();
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping("/include_DocumentFilePath")
	public ModelAndView include_DocumentFilePath(HttpServletRequest request, HttpServletResponse response) {
		String title = request.getParameter("title");
		boolean control = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("control"), "true"))
				.booleanValue();

		ModelAndView model = new ModelAndView();
		model.addObject("title", title);
		model.addObject("control", control);
		model.setViewName("include:/part/include_DocumentFilePath");
		return model;
	}

	@RequestMapping("/createUserPart")
	public ModelAndView createUserPart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module", "part");
		model.setViewName("default:/part/createUserPart");
		return model;
	}

	@RequestMapping("/viewPartBom")
	public ModelAndView viewPartBom(HttpServletRequest request, HttpServletResponse response) {

		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");

		View[] views = null;

		Baseline bsobj = null;

		try {
			ReferenceFactory rf = new ReferenceFactory();

			if (baseline != null && baseline.length() > 0) {
				bsobj = (Baseline) rf.getReference(baseline).getObject();
			}

			views = ViewHelper.service.getAllViews();
		} catch (Exception e) {
			e.printStackTrace();
			bsobj = null;
		}

		if (view == null) {
			view = views[0].getName();
		}

		List<Map<String, String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch (Exception e) {
			list = new ArrayList<Map<String, String>>();
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/part/viewPartBom");
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		return model;
	}

	@ResponseBody
	@RequestMapping("/viewPartBomAction")
	public List<Map<String, Object>> viewPartBomAction(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> list = null;
		try {
			list = PartHelper.service.viewPartBomAction(request, response);
		} catch (Exception e) {
			list = new ArrayList<Map<String, Object>>();
		}
		return list;
	}

	@Description(value = "품목등록 SEQ 검색 메서드")
	@ResponseBody
	@PostMapping(value = "/seq")
	public Map<String, Object> seq(@RequestBody Map<String, Object> params) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = PartHelper.manager.seq(params);
			map.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", e.toString());
		}

		return map;
	}

	@RequestMapping("/include_partLink")
	public ModelAndView include_partLink(HttpServletRequest request, HttpServletResponse response) {
		String module = request.getParameter("module");
		String oid = request.getParameter("oid");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 품목"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");

		List<PartData> list = PartHelper.service.include_partLink(module, oid);

		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/part/include_partLink");
		model.addObject("module", module);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("list", list);
		model.addObject("enabled", Boolean.valueOf(enabled));
		return model;

	}

	@ResponseBody
	@RequestMapping("/linkPartAction")
	public ResultData linkPartAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.linkPartAction(request, response);
	}

	@ResponseBody
	@RequestMapping("/deletePartLinkAction")
	public ResultData deletePartLinkAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.deletePartLinkAction(request, response);
	}

	@Description(value = "일괄수정  페이지 이동")
	@GetMapping(value = "/updatefamilyPart")
	public ModelAndView updatefamilyPart(@RequestParam String oid) {

		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);

		model.setViewName("/extcore/jsp/part/updatefamilyPart.jsp");
		return model;
	}

	@Description(value = "일괄수정  페이지 리스트 불러오기")
	@ResponseBody
	@PostMapping(value = "/updatefamilyPart")
	public Map<String, Object> updatefamilyPart(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		String oid = (String) params.get("oid");

		List<Map<String, Object>> list = null;

		try {
			list = PartHelper.service.partInstanceGrid(oid);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String, Object>>();
		}

		List<NumberCodeDTO> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeDTO> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeDTO> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeDTO> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeDTO> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeDTO> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
		List<String> unit = PartHelper.service.getQuantityUnit();

		result.put("productmethod", productmethod);
		result.put("deptCode", deptCode);
		result.put("model", modelcode);
		result.put("manufacture", manufacture);
		result.put("mat", mat);
		result.put("finish", finish);
		result.put("unit", unit);
		result.put("list", list);
		return result;
	}

	/**
	 * BOM 에서 선택적 부품 첨부 파일 다운로드
	 * 
	 * @param request
	 * @param response
	 */
	/*
	 * @RequestMapping(value="partTreeSelectAttachDown") public ModelAndView
	 * partTreeSelectAttachDown(HttpServletRequest request, HttpServletResponse
	 * response) { ModelAndView model = new ModelAndView();
	 * 
	 * try { PartHelper.service.partTreeSelectAttachDown(request, response);
	 * 
	 * 
	 * 
	 * } catch(Exception e) { e.printStackTrace(); } //model.addObject("xmlString",
	 * xmlString); model.setViewName("empty:/drawing/createPackageDrawingAction");
	 * return model; }
	 */

	@RequestMapping(value = "/partTreeSelectAttachDown")
	public ModelAndView partTreeSelectAttachDown(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> param) {

		ModelAndView model = new ModelAndView();

		try {
			PartHelper.service.partTreeSelectAttachDown(request, response, param);
			// ExcelDownHelper.service.partExcelDown(null, response);

		} catch (Exception e) {
			e.printStackTrace();

		}
		model.setViewName("empty:/drawing/createPackageDrawingAction");
		return model;
	}

	/**
	 * AUI 그리드 BOM
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/viewAUIPartBom2")
	public ModelAndView viewAUIPartBom2(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		String title = "";
		String number = "";

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String, String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline) CommonUtil.getObject(baseline);
			if (line != null) {
				title = line.getName();
			} else {

				title = number;

			}

		} catch (Exception e) {
			list = new ArrayList<Map<String, String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if (!PartSearchHelper.service.isLastPart(part)) {
			try {
				lastedpart = (WTPart) ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title", title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);

		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		// model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom2");

		return model;
	}

	/**
	 * AUI 그리드 BOM
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/viewAUIPartBom3")
	public ModelAndView viewAUIPartBom3(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		String title = "";
		String number = "";

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String, String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline) CommonUtil.getObject(baseline);
			if (line != null) {
				title = line.getName();
			} else {

				title = number;

			}

		} catch (Exception e) {
			list = new ArrayList<Map<String, String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if (!PartSearchHelper.service.isLastPart(part)) {
			try {
				lastedpart = (WTPart) ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title", title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);

		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		// model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom3");

		return model;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/viewAUIPartBomAction")
	public List<Map<String, Object>> viewAUIPartBomAction(@RequestBody Map<String, Object> params) throws Exception {
		List<Map<String, Object>> list = BomSearchHelper.manager.getAUIPartTreeAction(params);
		return list;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/viewAUIPartBomChildAction")
	public List<Map<String, Object>> viewAUIPartBomChildAction(@RequestBody Map<String, Object> params)
			throws Exception {
		List<Map<String, Object>> list = BomSearchHelper.manager.viewAUIPartBomChildAction(params);
		return list;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/viewAUIPartBomChildAction2")
	public Map<String, Object> viewAUIPartBomChildAction2(@RequestBody Map<String, Object> params) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("list", BomSearchHelper.manager.viewAUIPartBomChildAction(params));
		result.put("grideItem", params.get("grideItem"));

		return result;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getAUIBOMRootChildAction")
	public List<Map<String, Object>> getAUIBOMRootChildAction(@RequestBody Map<String, Object> params)
			throws Exception {
		List<Map<String, Object>> list = BomSearchHelper.manager.getAUIBOMRootChildAction(params);

		return list;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getAUIBOMPartChildAction")
	public List<Map<String, Object>> getAUIBOMPartChildAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Map<String, Object>> list = BomSearchHelper.service.getAUIBOMPartChildAction(request, response);

		return list;
	}

	/**
	 * BOMEditor 팝업 화면
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/bomEditor")
	public ModelAndView bomEditor(@RequestParam String oid) {
		ModelAndView model = new ModelAndView();
		try {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			String number = part.getNumber();
			List<Map<String, String>> list = null;
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			String title = number;

			String lastedoid = oid;
			WTPart lastedpart = part;
			if (!PartSearchHelper.service.isLastPart(part)) {
				lastedpart = (WTPart) ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			}

			model.addObject("title", title);
			model.addObject("oid", oid);
			model.addObject("lastedoid", lastedoid);

			model.addObject("number", number);
			model.addObject("list", list);
			model.setViewName("popup:/part/bomEditor");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	/**
	 * AUI BOM Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/bomEditorList")
	public List<Map<String, Object>> bomEditorList(@RequestBody Map<String, Object> params) throws Exception {
		List<Map<String, Object>> list = BomSearchHelper.manager.bomEditorList(params);
		return list;
	}

	/**
	 * 부품 체크인
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/partCheckIn")
	public Map<String, Object> partCheckIn(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.service.partCheckIn(params);
		return result;
	}

	/**
	 * 부품 체크 아웃
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/partCheckOut")
	public Map<String, Object> partCheckOut(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.service.partCheckOut(params);
		return result;
	}

	/**
	 * 부품 체크 아웃 취소
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/partUndoCheckOut")
	public Map<String, Object> partUndoCheckOut(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.service.partUndoCheckOut(params);
		return result;
	}

	@RequestMapping("/viewAUIPartBom")
	public ModelAndView viewAUIPartBom(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();

		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		String title = "";
		String number = "";

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String, String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			title = number;
			if (!"".equals(baseline)) {
				ManagedBaseline line = (ManagedBaseline) CommonUtil.getObject(baseline);
				if (line != null) {
					title = line.getName();
				}
			}
		} catch (Exception e) {
			list = new ArrayList<Map<String, String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if (!PartSearchHelper.service.isLastPart(part)) {
			try {
				lastedpart = (WTPart) ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title", title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);

		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		// model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom");
		return model;
	}

	@Description(value = "품목 최신버전 이동")
	@GetMapping(value = "/latest")
	public ModelAndView latest(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart latest = PartHelper.manager.latest(oid);
		boolean isAdmin = CommonUtil.isAdmin();
		PartDTO dto = new PartDTO(latest);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/part/part-view");
		return model;
	}

	@Description(value = "상위 품목")
	@GetMapping(value = "/upper")
	public ModelAndView upper(@RequestParam String oid, @RequestParam(required = false) String baseline)
			throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		JSONArray upper = PartHelper.manager.upper(oid, baseline);
		model.addObject("upper", upper);
		model.addObject("part", part);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/part/part-upper");
		return model;
	}

	@Description(value = "하위 품목")
	@GetMapping(value = "/lower")
	public ModelAndView lower(@RequestParam String oid, @RequestParam(required = false) String baseline)
			throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		JSONArray lower = PartHelper.manager.lower(oid, baseline);
		model.addObject("lower", lower);
		model.addObject("part", part);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/part/part-lower");
		return model;
	}

	@Description(value = "완제품")
	@GetMapping(value = "/end")
	public ModelAndView end(@RequestParam String oid, @RequestParam(required = false) String baseline)
			throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		JSONArray end = PartHelper.manager.end(oid, baseline);
		model.addObject("end", end);
		model.addObject("part", part);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/part/part-end");
		return model;
	}

	@Description(value = "품목속성")
	@GetMapping(value = "/attr")
	public ModelAndView attr(@RequestParam String oid) throws Exception {
		ModelAndView view = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		Map<String, Object> attr = PartHelper.manager.attr(part);
		ArrayList<NumberCode> manufacture = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> model = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> productmethod = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> deptcode = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		view.addObject("deptcode", deptcode);
		view.addObject("manufacture", manufacture);
		view.addObject("productmethod", productmethod);
		view.addObject("model", model);
		view.addObject("oid", oid);
		view.addObject("attr", attr);
		view.addObject("part", part);
		view.addObject("isAdmin", isAdmin);
		view.setViewName("popup:/part/part-attr");
		return view;
	}

	@Description(value = "품목 속성 클린")
	@ResponseBody
	@PostMapping(value = "/_clean")
	public Map<String, Object> _clean(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			PartHelper.service._clean(params);
			result.put("msg", "속성이 CLEANING 되었습니다..");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "품목 속성 수정")
	@ResponseBody
	@PostMapping(value = "/attrUpdate")
	public Map<String, Object> attrUpdate(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			PartHelper.service.attrUpdate(params);
			result.put("msg", "속성이 변경되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "썸네일")
	@GetMapping(value = "/thumbnail")
	public ModelAndView thumbnail(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("oid", oid);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/part/part-thumbnail.jsp");
		return model;
	}

	@Description(value = "품목 재변환")
	@ResponseBody
	@GetMapping(value = "/publish")
	public Map<String, Object> publish(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			PartHelper.service.publish(oid);
			result.put("msg", "재변환이 요청 되었습니다.\n잠시 후 데이터 확인을 해주세요.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "채번용 페이지")
	@PostMapping(value = "/load")
	@ResponseBody
	public Map<String, Object> load(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = PartHelper.manager.load(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "채번 뷰 LAZY LOAD")
	@PostMapping(value = "/lazyLoad")
	@ResponseBody
	public Map<String, Object> lazyLoad(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = PartHelper.manager.lazyLoad(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM에서 등록 후 바로 연결")
	@GetMapping(value = "/append")
	public ModelAndView append(@RequestParam String method) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("modelList", modelList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("popup:/part/part-append");
		return model;
	}

	@Description(value = "BOM 품목 신규 등록")
	@ResponseBody
	@PostMapping(value = "/append")
	public Map<String, Object> append(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WTPart part = PartHelper.service.append(params);
			result.put("oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "품목 변경이력보기")
	@RequestMapping("/viewHistory")
	public ModelAndView viewHistory(@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		JSONArray data = PartHelper.manager.viewHistory(part);
		model.addObject("data", data);
		model.addObject("part", part);
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("popup:/part/part-viewHistory");
		return model;
	}
}