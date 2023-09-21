package com.e3ps.controller;

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
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeWfHelper;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.StringUtil;
//import com.e3ps.doc.beans.DocumentData;
//import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.beans.EpmUtil;

import wt.part.WTPart;
//import wt.vc.baseline.ManagedBaseline;

@Controller
@RequestMapping(value = "/changeECO/**")
public class ChangeECOController extends BaseController {
	
	@Description(value = "ECO 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/eco-create.jsp");
		return model;
	}
	
	@Description(value = "ECO 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String,Object> create(@RequestBody ECOData data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ECOHelper.service.create(data);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	/**	ECO 등록 Action
	 * @param request
	 * @param response
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/createECOAction")
//	public ResultData createECOAction(HttpServletRequest request, HttpServletResponse response) {
//		return ECOHelper.service.createECOAction(request);//ECRHelper.service.createECRAction(request);
//	}
	
	@Description(value = "ECO 검색")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("/extcore/jsp/change/eco-list.jsp");
		return model;
	}
	
	@Description(value = "관련 ECO 팝업 페이지")
	@GetMapping(value = "/listPopup")
	public ModelAndView listPopup() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("popup:/change/eco-list-popup");
		return model;
	}
	
	@Description(value = "ECO 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ECOHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
		
	}
//	/** ECO 검색
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listECOAction")
//	public Map<String,Object> listECOAction(HttpServletRequest request, HttpServletResponse response){
//		Map<String,Object> result = null;
//		try {
//			result = ECOSearchHelper.service.listECOAction(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//		
//	}
	
	/**	viewECO 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping("/viewECO")
//	public ModelAndView viewECO(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
//		ECOData ecoData = new ECOData(eco);
//		model.setViewName("popup:/change/viewECO");
//		model.addObject("ecoData", ecoData);
//		return model;
//	}
	
	@Description(value = "ECO 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		EChangeOrder order = (EChangeOrder) CommonUtil.getObject(oid);
		ECOData dto = new ECOData(order);
		
//		JSONArray versionHistory = PartHelper.manager.versionHistory(part);
//		boolean isSupervisor = CommonUtils.isSupervisor();
//		model.addObject("isSupervisor", isSupervisor);
//		model.addObject("versionHistory", versionHistory);
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/change/eco-view.jsp");
		return model;
	}
	
	@Description(value = "EO 등록 페이지")
	@GetMapping(value = "/createEO")
	public ModelAndView createEO(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/eo-create.jsp");
		return model;
	}
	
	/**	EO 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/createEO")
//	public ModelAndView createEO(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.addObject("menu", "menu6");
//		model.addObject("module", "change");
//		model.setViewName("/extcore/jsp/change/eo-create.jsp");
//		return model;
//	}
	
	@Description(value = "EO 등록 함수")
	@ResponseBody
	@PostMapping(value = "/createEO")
	public Map<String,Object> createEO(@RequestBody ECOData data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ECOHelper.service.createEO(data);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	/**	EO 등록 Action
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createEOAction")
	public ResultData createEOAction(HttpServletRequest request, HttpServletResponse response) {
		
		
		return ECOHelper.service.createEOAction(request);//ECRHelper.service.createECRAction(request);
	}

	@Description(value = "EO 검색 페이지")
	@GetMapping(value = "/listEO")
	public ModelAndView listEO() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("/extcore/jsp/change/eo-list.jsp");
		return model;
	}
	
	@Description(value = "관련 EO 팝업 페이지")
	@GetMapping(value = "/listEOPopup")
	public ModelAndView listEOPopup() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("popup:/change/eo-list-popup");
		return model;
	}
	
	@Description(value="EO 검색  Action")
	@ResponseBody
	@PostMapping(value = "/listEO")
	public Map<String,Object> listEO(@RequestBody Map<String, Object> params){
		Map<String,Object> result = null;
		try {
			result = ECOHelper.manager.listEO(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
		
	}
//	/** EO 검색  Action
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listEOAction")
//	public Map<String,Object> listEOAction(HttpServletRequest request, HttpServletResponse response){
//		Map<String,Object> result = null;
//		try {
//			result = ECOSearchHelper.service.listEOAction(request, response);//ECRSearchHelper.service.listECRAction(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//		
//	}
	
	/**	EO,ECO 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateEO")
	public ModelAndView updateEO(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
		ECOData ecoData = new ECOData(eco);
		if(eco.getEoType().equals("CHANGE")){
			model.setViewName("popup:/change/updateECO");
		}else{
			model.setViewName("popup:/change/updateEO");
		}
		
		model.addObject("ecoData", ecoData);
		return model;
	}
	
	/**	EO 수정 Action
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updateEOAction")
	public ResultData updateEOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return ECOHelper.service.updateEOAction(request);
		
	}
	
	/**	EO,ECO 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteECOAction")
	public Map<String,Object> deleteECOAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		boolean result = false;
		String msg = Message.get("삭제 실패하였습니다.");
		try {
			msg = ECOHelper.service.deleteECOAction(oid);
			result = true;
		}catch(Exception e) {
			result = false;
			msg = Message.get("삭제 실패하였습니다.");
			e.printStackTrace();
		}
		map.put("result", result);
		map.put("msg", msg);
        
		return map;
	}
	
	
	
	//------------------------- 루트로닉 ENN -----------------------------------------//
	
	
	
	
	
	
	
	
	
	/** Activity List
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ecaList")
	public List<NumberCodeDTO> numberCodeList(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type) {
		List<NumberCodeDTO> list = CodeHelper.service.topCodeToList(type);
		return list;
	}
	
	
	
	
	
	/** 대상 품목
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_EulView")
	public ModelAndView include_EulView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<Map<String, Object>> list = null;
		try {
			//list = ECOHelper.service.include_EulViewAction(oid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		model.addObject("list", list);
		model.setViewName("popup:/change/include_EulView");
		
		return model;
	}
	
	/**	관련 관련 품목 목록
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("include_ChangePartView")
	public ModelAndView include_ChangePartView(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String oid = request.getParameter("oid");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
		String type = eco.getEoType();
		boolean checkDummy = StringUtil.checkReplaceStr(request.getParameter("checkDummy"), "false").equals("true") ? true : false;
		List<Map<String,Object>> list = null;
		try {
			list = ChangeWfHelper.service.wf_CheckPart(oid,checkDummy,distribute);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("type", type);
		model.addObject("eoOid",oid);
		model.addObject("distribute",distribute);
		model.setViewName("include:/change/include_ChangePartView");
		return model;
	}
	
	/**	완제품 품목 목록
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("include_CompletePartView")
	public ModelAndView include_CompletePartView(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String oid = request.getParameter("oid");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
		ECOData ecoData = new ECOData(eco);
		List<Map<String,Object>> list = ecoData.getCompletePartList();
//		System.out.println(" include_CompletePartView distribute =" + distribute);
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("type", eco.getEoType());
		model.addObject("eoOid",oid);
		model.addObject("distribute",distribute);
		model.setViewName("include:/change/include_CompletePartView");
		return model;
	}
	
	/**	관련 ECO
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ECOView")
	public ModelAndView include_ECOView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<ECOData> list = null;
		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
		list = ECOSearchHelper.service.getRequestOrderLinkECOData(ecr);
		model.setViewName("popup:/change/include_ECOView");
		model.addObject("list", list);
		model.addObject("distribute",distribute);
		
		//model.addObject("eoType", eoType);
		
		return model;
	}
	
	/**	등록된 을지목록
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_EulList")
	public ModelAndView include_EulList(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="eoOid") String oid) throws Exception {
		List<Map<String,Object>> list = null;
		try {
			//list = ECOHelper.service.include_EulList(oid);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("oid",oid);
		model.setViewName("include:/change/include_EulList");
		return model;
	}
	
	@RequestMapping("/eoEulB")
	   public ModelAndView viewEoEulB(HttpServletRequest request){

	      Map<String, Object> map = new HashMap<String, Object>();
	      ModelAndView model = new ModelAndView("popup:/change/eoEulB");

	      //map = ECOHelper.service.viewEulB(request);

	      model.addAllObjects(map);

	      return model;
	   }
	
	
	/**	일괄개정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/batchRevision")
	public ModelAndView batchRevision(HttpServletRequest request) {

		String eoOid = StringUtil.checkNull(request.getParameter("eoOid"));
		String revisableArr = request.getParameter("revisableOid");

		List<Map<String, Object>> list = null;

		try{
			String[] arr = revisableArr.split(",");
			list = ECOSearchHelper.service.batchRevision(arr);//ECOHelper.service.batchRevision(arr);
		}catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("eoOid", eoOid);
		model.setViewName("include:/change/batchRevision");

		return model;

	}
	
	@RequestMapping("/batchRevisionAction")
	public ModelAndView batchRevisionAction(HttpServletRequest request, HttpServletResponse response) {
		String[] revisableArr = request.getParameterValues("revisableOid");
		String msg = EpmUtil.batchRevisePartAndDoc(revisableArr, false);
		
		return ControllerUtil.openerRefresh(msg);
	}
	
	@RequestMapping("/include_ChangeECOView")
	public ModelAndView include_ChangeECOView(HttpServletRequest request, HttpServletResponse response){
		String oid = request.getParameter("oid");
		String moduleType = request.getParameter("moduleType");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 ECO"));
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<ECOData> list = new ArrayList<ECOData>();
		try {
			list = ECOSearchHelper.service.include_ChangeECOView(oid,moduleType);
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		if(distribute.equals("true")){
			list.clear();
			WTPart part = (WTPart)CommonUtil.getObject(oid);
			List<EChangeOrder> eolist = null;
			try {
				eolist = ECOSearchHelper.service.getPartTOECOList(part);
//				System.out.println("list Size = "+eolist.size());
				for(EChangeOrder eco : eolist){
					ECOData data = new ECOData(eco);
					list.add(data);
				}
			} catch(Exception e) {
				eolist = new ArrayList<EChangeOrder>();
				e.printStackTrace();
			}
			/*List<Map<String,String>> listDBECO = null;
			try {
				listDBECO = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			} catch(Exception e) {
				listDBECO = new ArrayList<Map<String,String>>();
				e.printStackTrace();
			}
			try {
				for (int i = 0; i < listDBECO.size(); i++) {
					Map<String,String> map = listDBECO.get(i);
					String baseName = map.get("baseName");
					if(null!=baseName){
						System.out.println("baseName = "+baseName);
						EChangeOrder eco = ECOSearchHelper.service.getEChangeOrder(baseName);
						System.out.println("eco Check = "+(null!=eco));
						if(null!=eco){
							System.out.println("eco Check = "+(eco.getEoNumber()));
							ECOData data = new ECOData(eco);
							list.add(data);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
//		System.out.println("include_ChangeECOView distribute =" + distribute);
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("moduleType", moduleType);
		model.addObject("title", title);
		model.addObject("distribute", distribute);
		model.setViewName("include:/change/include_ChangeECOView");
		
		return model;
	}
	
	/**	ECO 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateECO")
	public ModelAndView updateECO(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
		ECOData ecoData = new ECOData(eco);
		
		model.setViewName("popup:/change/updateECO");
		model.addObject("ecoData", ecoData);
		return model;
	}
	/**	ECO 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateECOAction")
	public ModelAndView updateECOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileRequest req =  new FileRequest(request);
		String oid ="";
		//String oid =ECOHelper.service.modifyECO(req);
		return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/changeECO/viewECO.do?oid="+oid, Message.get("수정하였습니다."));
	
	}
	
	/**	일괄수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/batchCheckInOut")
	public ModelAndView batchCheckInOut(HttpServletRequest request) {

		String eoOid = StringUtil.checkNull(request.getParameter("eoOid"));
		String revisableArr = request.getParameter("revisableOid");

		List<Map<String, Object>> list = null;

		try{
			String[] arr = revisableArr.split(",");
			//list = ECOHelper.service.batchRevision(arr);
		}catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("eoOid", eoOid);
		model.setViewName("include:/change/batchCheckInOut");

		return model;

	}
	
	/**
	 * 일괄 개정
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchCheckInOutAction")
	public ModelAndView batchCheckInOutAction(HttpServletRequest request, HttpServletResponse response) {
		String[] revisableArr = request.getParameterValues("revisableOid");
		String msg = EpmUtil.batchCheckInOutPartAndDoc(revisableArr, false);
		
		return ControllerUtil.openerRefresh(msg);
	}
	
	/**
	 * ECO View 에서 완제품 링크 삭제
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/deleteCompletePartAction")
	public ResultData deleteCompletePartAction(HttpServletRequest request, HttpServletResponse response)  {
		String linkOid = request.getParameter("linkOid");
		return	ECOHelper.service.deleteCompletePartAction(linkOid);
	}
	
	/**	문서 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="excelDown", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData excelDown(HttpServletRequest request, HttpServletResponse response) {
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String eoType = StringUtil.checkNull(request.getParameter("eoType"));
		return ECOHelper.service.excelDown(oid, eoType);
	}
	
	/**	관련 ECO 보기(ECA)
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_ECOView_ECA")
	public ModelAndView include_ECOView_ECA(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<ECOData> list = null;
		try {
			list = ChangeHelper.service.include_ECOList(oid,moduleType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		model.setViewName("include:/change/include_ECOView_ECA");
		return model;
	}
	
	@Description(value = "관련 CR/ECPR 검색 페이지")
	@GetMapping(value = "/select_ecrPopup")
	public ModelAndView select_ecrPopup() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/select_ecrPopup.jsp");
		return model;
	}
}
