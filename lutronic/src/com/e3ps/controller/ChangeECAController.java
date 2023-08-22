package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashMap;
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

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.beans.ECAData;
//import com.e3ps.change.beans.EOData;
//import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.beans.ResultData;
//import com.e3ps.common.content.FileRequest;
//import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
//import com.e3ps.part.beans.PartData;
//import com.e3ps.part.service.PartHelper;

import wt.fc.ReferenceFactory;
//import wt.part.WTPart;
//import com.e3ps.change.service.ECOHelper;

@Controller
@RequestMapping("/changeECA")
public class ChangeECAController extends BaseController {
	
	/**	EChangeActivityDefinitionRoot 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createRootDefinition")
	public ModelAndView RootDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		
		model.setViewName("popup:/change/createRootDefinition");
		return model;
	}
	
	//publ
	
	/**	관련 ECA EO,ECO View
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ECAView")
	public ModelAndView include_ECAView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<ECAData> list = null;
		list = ECAHelper.service.include_ecaList(oid);
		model.setViewName("include:/change/include_ECAView");
		model.addObject("list", list);
		model.addObject("isAdmin", CommonUtil.isAdmin());
		
		return model;
	}
	
	/**	ECO 품목 추가
	 * @param request
	 * @param response
	 * @return
	 */
	/*@RequestMapping("/createECOPartAction")
	
	public ModelAndView createECOPartAction(HttpServletRequest request, HttpServletResponse response) {
		FileRequest req = null;
		String message = Message.get("수정 실패하였습니다");
		try{
			req =  new FileRequest(request);
			
			ECOHelper.service.createEco(req);
			
			message = Message.get("수정 성공하였습니다");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	@ResponseBody
	@RequestMapping("/getActivityTypeList")
	public List<Map<String, String>> getActivityTypeList(){
		return ChangeUtil.getActivityTypeList();
	}
	
	/**
	 * ECA 수정
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_UpdateActivity")
	public ModelAndView include_UpdateActivity(HttpServletRequest request, HttpServletResponse response) {
		String eoOid = request.getParameter("eoOid");
		String eoType = request.getParameter("eoType");
		ModelAndView model = new ModelAndView();
		model.addObject("eoOid", eoOid);
		model.addObject("eoType", eoType);
		model.setViewName("include:/change/include_UpdateActivity");
		return model;
	}
	
	/**
	 * 활동 등록 리스트
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_CreateActivity")
	public ModelAndView include_CreateActivity(HttpServletRequest request, HttpServletResponse response) {
		
		String oid = request.getParameter("oid");
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("include:/change/include_CreateActivity");
		return model;
	}
	
	/**	Activity 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createActivity")
	public ModelAndView createActivityDefinition(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		model.addObject("oid", oid);
		model.setViewName("popup:/change/createActivity");
		
		return model;
	}
	
	/**
	 * ead 셋팅
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/setActiveDefinition")
	public List<EADData> setActiveDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String oid = request.getParameter("oid");
//		System.out.println("setActiveDefinition = oid" + oid);
		List<EADData> list = ECAHelper.service.setActiveDefinition(oid);
		return list;
	}
	
	@Description(value = "ECA별 산출물 ")
	@GetMapping(value = "/viewECA")
	public ModelAndView viewECA(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String oid = request.getParameter("oid");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		ReferenceFactory rf = new ReferenceFactory();
		ECOChange eo = (ECOChange)rf.getReference(oid).getObject();
		
		ModelAndView model = new ModelAndView();
		model.addObject("eoNumber", eo.getEoNumber());
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("distribute",distribute);
		model.setViewName("/extcore/jsp/change/viewECA.jsp");
		return model;
	}

	@Description(value = "ECA별 산출물 ")
	@ResponseBody
	@PostMapping(value = "/viewECA")
	public Map<String, Object> viewECA(@RequestBody Map<String, Object> params) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");
		String distribute = StringUtil.checkNull((String) params.get("distribute"));
		ReferenceFactory rf = new ReferenceFactory();
		
		ECOChange eo = (ECOChange)CommonUtil.getObject(oid);
		
		List<Map<String,Object>> list = null;
		
		try {
			//list = ECAHelper.service.viewECA(eo);
			list = ECAHelper.service.viewECA_Doc(eo);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			list = new ArrayList<Map<String,Object>>();
		}
		
		ModelAndView model = new ModelAndView();
		result.put("eoNumber", eo.getEoNumber());
		result.put("admin", CommonUtil.isAdmin());
		result.put("list", list);
		result.put("distribute",distribute);
		return result;
	}
//	/**
//	 * ECA별 산출물 
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/viewECA")
//	public ModelAndView viewECA(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		String oid = request.getParameter("oid");
//		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
//		ReferenceFactory rf = new ReferenceFactory();
//		ECOChange eo = (ECOChange)rf.getReference(oid).getObject();
//		
//		List<Map<String,Object>> list = null;
//		
//		try {
//			//list = ECAHelper.service.viewECA(eo);
//			list = ECAHelper.service.viewECA_Doc(eo);
//		} catch(Exception e) {
//			e.printStackTrace();
//			list = new ArrayList<Map<String,Object>>();
//		}
//		
//		ModelAndView model = new ModelAndView();
//		model.addObject("eoNumber", eo.getEoNumber());
//		model.addObject("admin", CommonUtil.isAdmin());
//		model.addObject("list", list);
//		model.addObject("distribute",distribute);
//		model.setViewName("popup:/change/viewECA");
//		return model;
//	}
	
	
	/**
	 * ECA의 산출물 등록
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/outPutCreate")
	public ModelAndView outPutCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(oid);
		ECOChange eo = eca.getEo();
		model.setViewName("popup:/change/outPutCreate");
		model.addObject("ecaOid", oid);
		return model;
	}
	
	/**
	 * eca 셋팅
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getEOActivity")
	public List<ECAData> getEOActivity(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String oid = request.getParameter("oid");
		List<ECAData> list = ECAHelper.service.getEOActivity(oid);
		return list;
	}
	
	/**
	 * ECA 의견 및 첨부 파일 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/modifyECA")
	public ModelAndView modifyECA(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(oid);
		ECOChange eo = eca.getEo();
		model.setViewName("popup:/change/modifyECA");
		model.addObject("ecaOid", oid);
		model.addObject("comments",eca.getComments());
		return model;
	}
	/**
	 * eca 셋팅
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/modifyECAction")
	public ResultData modifyECAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String oid = request.getParameter("oid");
		ResultData returnData = ECAHelper.service.modifyECAction(request);
		return returnData;
	}
	
}
