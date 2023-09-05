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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

//import wt.doc.WTDocument;
//import wt.fc.ReferenceFactory;
//import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.service.ECOHelper;
//import com.e3ps.change.beans.EOData;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.change.service.ECRSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
//import com.e3ps.common.content.FileRequest;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
//import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.StringUtil;
//import com.e3ps.doc.service.DocumentHelper;

@Controller
@RequestMapping(value = "/changeECR/**")
public class ChangeECRController extends BaseController {
	
	@Description(value = "ECR 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView createECR(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/ecr-create.jsp");
		model.addObject("deptcodeList", deptcodeList);
		return model;
	}
	
	@Description(value = "ECR 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String,Object> create(@RequestBody ECRData data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ECRHelper.service.create(data);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	/**	ECR 등록 Action
	 * @param request
	 * @param response
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/createECRtAction")
//	public ResultData createECRtAction(HttpServletRequest request, HttpServletResponse response) {
//		
//		//return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/change/listECR.do", message);
//		return ECRHelper.service.createECRAction(request);
//	}
	
	@Description(value = "ECR 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/change/ecr-list.jsp");
		return model;
	}
	
	@Description(value = "ECR 검색  Action")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params){
		Map<String,Object> result = null;
		try {
			 result = ECRHelper.manager.list(params);
			 result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
		
	}
//	/** ECR 검색  Action
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listECRAction")
//	public Map<String,Object> listECRAction(HttpServletRequest request, HttpServletResponse response){
//		Map<String,Object> result = null;
//		try {
//			result = ECRSearchHelper.service.listECRAction(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//		
//	}
	
	@Description(value = "ECR 상세 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
		ECRData dto = new ECRData(ecr);
		
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/change/ecr-view.jsp");
		return model;
	}
	
	/**	ECR 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping("/viewECR")
//	public ModelAndView viewECR(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
//		ECRData ecrData = new ECRData(ecr);
//		model.setViewName("popup:/change/viewECR");
//		model.addObject("ecrData", ecrData);
//		return model;
//	}
	
	/**	ECR 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateECR")
	public ModelAndView updateECR(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
		
		ECRData ecrData = new ECRData(ecr);
		
		model.setViewName("popup:/change/updateECR");
		
		model.addObject("ecrData", ecrData);
		return model;
	}
	
	/**	ECR 수정 Action
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updateECRAction")
	public ResultData updateECRAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return ECRHelper.service.updateECRAction(request);
		//return ControllerUtil.redirect("/Windchill/" + CommonUtil.getOrgName() + "/changeECR/viewECR.do?oid="+oid, Message.get("수정 하였습니다."));
	
	}
	
	/**	ECR 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteECRAction")
	public Map<String,Object> deleteECRAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		boolean result = false;
		String msg = Message.get("삭제 실패하였습니다.");
		try {
			msg = ECRHelper.service.deleteEcr(oid);
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
	
	/** ECR 검색 팝업
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectECRPopup")
	public ModelAndView selectECRPopup(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/change/selectECRPopup");
		return model;
	}
	
	/**	관련 ECR
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ECRView")
	public ModelAndView include_ECRView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<ECRData> list = new ArrayList<ECRData>();
		
		Object obj = CommonUtil.getObject(oid);
		if(obj instanceof EChangeOrder){
			EChangeOrder eco = (EChangeOrder)obj;
			list = ECRSearchHelper.service.getRequestOrderLinkECRData(eco);//ECRHelper.service.include_ecrList(oid);
		}else if(obj instanceof EChangeRequest){
			EChangeRequest ecr = (EChangeRequest)obj;
			List<EcrToEcrLink> list_ECRtoECRLINK = ECRHelper.service.getEcrToEcrLinks(ecr, "useBy");
			for(EcrToEcrLink link : list_ECRtoECRLINK){
				EChangeRequest linkECR = link.getUseBy();
				ECRData data = new ECRData(linkECR);
				list.add(data);
			}
			list_ECRtoECRLINK = ECRHelper.service.getEcrToEcrLinks(ecr, "used");
			for(EcrToEcrLink link : list_ECRtoECRLINK){
				EChangeRequest linkECR = link.getUsed();
				ECRData data = new ECRData(linkECR);
				list.add(data);
			}
		}
		model.setViewName("popup:/change/include_ECRView");
		model.addObject("list", list);
		model.addObject("distribute",distribute);
		
		return model;
	}
	
	/**	관련 ECR
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ECRSelect")
	public ModelAndView include_ECRSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		List<ECRData> list = new ArrayList<ECRData>();
		String oid = request.getParameter("oid");
//		System.out.println("include_ECRSelect oid =" + oid);
		Object obj = CommonUtil.getObject(oid);
		if(obj instanceof EChangeOrder){
			EChangeOrder eco = (EChangeOrder)obj;
			list = ECRSearchHelper.service.getRequestOrderLinkECRData(eco);
		}else if(obj instanceof EChangeRequest){
			EChangeRequest ecr = (EChangeRequest)obj;
			List<EcrToEcrLink> list_ECRtoECRLINK = ECRHelper.service.getEcrToEcrLinks(ecr, "useBy");
			for(EcrToEcrLink link : list_ECRtoECRLINK){
				EChangeRequest linkECR = link.getUseBy();
				ECRData data = new ECRData(linkECR);
				list.add(data);
			}
			list_ECRtoECRLINK = ECRHelper.service.getEcrToEcrLinks(ecr, "used");
			for(EcrToEcrLink link : list_ECRtoECRLINK){
				EChangeRequest linkECR = link.getUsed();
				ECRData data = new ECRData(linkECR);
				list.add(data);
			}
		}
		
		model.setViewName("include:/change/include_ECRSelect");
		model.addObject("list", list);
		return model;
	}
}
