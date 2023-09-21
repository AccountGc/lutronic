package com.e3ps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

//import com.e3ps.change.ECOChange;
//import com.e3ps.common.code.beans.NumberCodeData;
//import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.util.CommonUtil;
//import com.e3ps.part.beans.PartData;
//import com.e3ps.part.service.PartHelper;

//import wt.part.WTPart;

//import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
//import com.e3ps.change.EChangeOrder;
//import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ECNData;
import com.e3ps.change.beans.ECOData;
//import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
//import com.e3ps.change.service.ECAHelper;
import com.e3ps.change.service.ECNHelper;
import com.e3ps.change.service.ECNSearchHelper;
//import com.e3ps.change.service.ECOHelper;
//import com.e3ps.change.service.ECRHelper;
import com.e3ps.change.service.ECOHelper;

@Controller
@RequestMapping(value = "/changeECN/**")
public class ChangeECNController extends BaseController {
	
	@Description(value = "ECN 검색")
	@GetMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/ecn-list.jsp");
		return model;
	}
	
	@Description(value = "ECN 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ECNHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
		
	}
	
	@Description(value = "ECN 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/ecn-create.jsp");
		return model;
	}
	
	@Description(value = "ECN 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String,Object> create(@RequestBody ECNData data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ECNHelper.service.create(data);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "관련 ECO 검색 페이지")
	@GetMapping(value = "/select_ecoPopup")
	public ModelAndView select_ecrPopup() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/select_ecoPopup.jsp");
		return model;
	}
	
	@Description(value = "ECN 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		EChangeNotice ecn = (EChangeNotice)CommonUtil.getObject(oid);
		ECNData data = new ECNData(ecn);
		
		model.addObject("data", data);
		model.setViewName("popup:/change/ecn-view");
		return model;
	}
	
	/** ECO 검색
	 * @param request
	 * @param response
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/listECNAction")
//	public Map<String,Object> listECNAction(HttpServletRequest request, HttpServletResponse response){
//		Map<String,Object> result = null;
//		try {
//			result = ECNSearchHelper.service.listECNAction(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//		
//	}
	
	/**	관련 ECA
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ECNView")
	public ModelAndView include_ECNView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<ECNData> list = null;
		list = ECNHelper.service.include_ecnList(oid);
		model.setViewName("popup:/change/include_ECNView");
		model.addObject("list", list);
		
		return model;
	}
	
	/**	ECN 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewECN")
	public ModelAndView viewECO(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EChangeNotice ecn = (EChangeNotice)CommonUtil.getObject(oid);
		EOData eoData = new EOData(ecn);
		model.setViewName("popup:/change/viewECN");
		model.addObject("eoData", eoData);
		return model;
	}
	
	
}
