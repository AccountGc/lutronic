package com.e3ps.part.bom.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.part.bom.service.BomHelper;

import net.sf.json.JSONArray;
import wt.part.WTPart;

@Controller
@RequestMapping(value = "/bom/**")
public class BomController extends BaseController {

	@Description(value = "BOM 보기 화면")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		ArrayList<Map<String, String>> baseline = EChangeUtils.manager.getBaseline(oid);
		model.addObject("baseline", baseline);
		model.addObject("part", part);
		model.setViewName("popup:/part/bom/bom-view");
		return model;
	}

	@Description(value = "BOM 에디터 LAZY 로드")
	@PostMapping(value = "/editorLazyLoad")
	@ResponseBody
	public JSONArray editorLazyLoad(@RequestParam Map<String, Object> params) throws Exception {
		JSONArray list = new JSONArray();
		try {
			list = BomHelper.manager.editorLazyLoad(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Description(value = "BOM 신규품목추가")
	@PostMapping(value = "/append")
	@ResponseBody
	public Map<String, Object> append(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.append(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 품목 제거")
	@PostMapping(value = "/removeLink")
	@ResponseBody
	public Map<String, Object> removeLink(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.removeLink(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 멀티 품목 제거")
	@PostMapping(value = "/removeMultiLink")
	@ResponseBody
	public Map<String, Object> removeMultiLink(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.removeMultiLink(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 붙여넣기")
	@PostMapping(value = "/paste")
	@ResponseBody
	public Map<String, Object> paste(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.paste(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 기존품목 교체")
	@PostMapping(value = "/replace")
	@ResponseBody
	public Map<String, Object> replace(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.replace(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 기존품목 추가")
	@PostMapping(value = "/exist")
	@ResponseBody
	public Map<String, Object> exist(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.exist(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 드래그 앤 드랍")
	@PostMapping(value = "/drop")
	@ResponseBody
	public Map<String, Object> drop(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.drop(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 체크아웃 취소")
	@GetMapping(value = "/undocheckout")
	@ResponseBody
	public Map<String, Object> undocheckout(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.undocheckout(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 체크인")
	@GetMapping(value = "/checkin")
	@ResponseBody
	public Map<String, Object> checkin(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = BomHelper.service.checkin(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 리로드")
	@GetMapping(value = "/reload")
	@ResponseBody
	public Map<String, Object> reload(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//			JSONArray tree = BomHelper.manager.loadEditor(oid, true);
//			result.put("tree", tree);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰 LAZY LOAD")
	@PostMapping(value = "/lazyLoad")
	@ResponseBody
	public Map<String, Object> lazyLoad(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = BomHelper.manager.lazyLoad(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰")
	@PostMapping(value = "/loadStructure")
	@ResponseBody
	public Map<String, Object> loadStructure(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = BomHelper.manager.loadStructure(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 뷰서 도면 및 첨부파일 일괄 다운로드")
	@GetMapping(value = "/batch")
	public ModelAndView batch(@RequestParam String oid, @RequestParam String target) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		String title = "";
		if ("attach".equals(target)) {
			model.addObject("title", "첨부파일");
		} else if ("epm".equals(target)) {
			model.addObject("title", "도면");
		}
		model.addObject("oid", oid);
		model.addObject("target", target);
		model.addObject("part", part);
		model.setViewName("popup:/part/bom/bom-batch");
		return model;
	}

	@Description(value = "BOM 첨부파일, 도면 일괄 다운로드")
	@PostMapping(value = "/batch")
	@ResponseBody
	public Map<String, Object> batch(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			File f = BomHelper.manager.batch(params);
			result.put("url", f.getPath());
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", false);
		}
		return result;
	}

	@Description(value = "BOM 에디터")
	@GetMapping(value = "/editor")
	public ModelAndView editor(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		model.addObject("oid", oid);
		model.addObject("root", root);
		model.setViewName("popup:/part/bom/bom-editor");
		return model;
	}

	@Description(value = "BOM 에디터 로드")
	@PostMapping(value = "/loadEditor")
	@ResponseBody
	public JSONArray loadEditor(@RequestParam Map<String, Object> params) throws Exception {
		JSONArray list = new JSONArray();
		try {
			list = BomHelper.manager.loadEditor(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Description(value = "BOM 에디터 체크아웃")
	@GetMapping(value = "/checkout")
	@ResponseBody
	public Map<String, Object> checkout(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = BomHelper.service.checkout(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 수량 변경")
	@PostMapping(value = "/update")
	@ResponseBody
	public Map<String, Object> update(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = BomHelper.service.update(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "BOM 새품번 저장")
	@PostMapping(value = "/saveAs")
	@ResponseBody
	public Map<String, Object> saveAs(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			result = BomHelper.service.saveAs(params);
			if ((boolean) result.get("exist")) {
				result.put("msg", "이미 품번이 존재합니다.");
				result.put("result", FAIL);
			} else {
				result.put("msg", SAVE_MSG);
				result.put("result", SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
