package com.e3ps.rohs.controller;

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

import com.e3ps.change.beans.ECRData;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.rohs.service.RohsHelper;

import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/rohs/**")
public class RohsController extends BaseController {
	
	@ResponseBody
	@RequestMapping("/rohsFileType")
	public List<Map<String,String>> rohsFileType(HttpServletRequest request, HttpServletResponse response) {
		return RohsHelper.service.rohsFileType();
	}
	
	@Description(value = "물질 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception{
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		List<Map<String,String>> typeList = RohsHelper.manager.rohsFileType();
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-create.jsp");
		model.addObject("manufactureList", manufactureList);
		model.addObject("typeList", typeList);
		return model;
	}
	
	@Description(value = "물질 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String,Object> create(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RohsHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질검색")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		List<Map<String,String>> lifecycleList = WFItemHelper.manager.lifecycleList("LC_Default", "");
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("manufactureList", manufactureList);
		model.addObject("lifecycleList", lifecycleList);
		model.setViewName("/extcore/jsp/rohs/rohs-list.jsp");
		return model;
	}
	
	@Description(value = "물질검색")
	@GetMapping(value = "/listPopup")
	public ModelAndView listPopup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/rohs/rohs-list-popup");
		return model;
	}
	
	@Description(value = "물질 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RohsHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 상세 페이지")
	@GetMapping(value =  "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		RohsData dto = new RohsData(rohs);
		
		List<Map<String,Object>> list = RohsHelper.manager.getRohsContent(oid);
		model.addObject("list", list);
		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/rohs/rohs-view.jsp");
		return model;
	}
	
	@Description(value = "물질 수정 페이지")
	@GetMapping(value =  "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		RohsData data = new RohsData(rohs);
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		List<Map<String,String>> typeList = RohsHelper.manager.rohsFileType();
		
		List<Map<String,Object>> contentList = RohsHelper.manager.getRohsContent(oid);
		
		model.addObject("data", data);
		model.addObject("contentList", contentList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("typeList", typeList);
		model.setViewName("/extcore/jsp/rohs/rohs-update.jsp");
		return model;
	}
	
	@Description(value = "물질 수정 함수")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String,Object> update(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RohsHelper.service.update(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 삭제")
	@ResponseBody
	@PostMapping(value = "/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
        return RohsHelper.service.delete(oid);
    }
	
	@Description(value = "물질 중복체크")
	@ResponseBody
	@PostMapping(value = "/rohsCheck")
	public Map<String, Object> rohsCheck(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result.put("count", RohsHelper.manager.rohsCheck(params));
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 중복체크")
	@ResponseBody
	@PostMapping(value = "/rohsNameCheck")
	public Map<String, Object> rohsNameCheck(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result.put("duplicate", RohsHelper.manager.rohsNameCheck(params));
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질복사 페이지")
	@GetMapping(value = "/copyRohs")
	public ModelAndView copyRohs(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		RohsData data = new RohsData(rohs);
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		model.addObject("data", data);
		model.addObject("manufactureList", manufactureList);
		model.setViewName("popup:/rohs/copyRohs");
		return model;
	}
	
	@Description(value = "물질복사 함수")
	@ResponseBody
	@PostMapping(value = "/copyRohs")
	public Map<String,Object> copyRohs(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RohsHelper.service.copyRohs(params);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 개정 페이지")
	@GetMapping(value = "/reviseRohs")
	public ModelAndView reviseRohs(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-revise.jsp");
		return model;
	}
	
	@Description(value = "물질 개정 함수")
	@ResponseBody
	@PostMapping(value = "/reviseRohs")
	public Map<String,Object> reviseRohs(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RohsHelper.service.reviseRohs(params);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 일괄등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception{
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<RohsData> rohsList = RohsHelper.manager.totalList();
		ModelAndView model = new ModelAndView();
		model.addObject("manufactureList", manufactureList);
		model.addObject("rohsList", rohsList);
		model.setViewName("/extcore/jsp/rohs/rohs-batch.jsp");
		return model;
	}
	
	@Description(value = "물질 일괄등록 실행")
	@ResponseBody
	@PostMapping(value = "/batch")
	public Map<String,Object> batch(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RohsHelper.service.batch(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "물질 일괄등록 첨부파일 페이지")
	@GetMapping(value = "/attachFile")
	public ModelAndView attachFile() throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/common/attach-multi");
		return model;
	}
	
	@Description(value = "물질 일괄링크")
	@GetMapping(value = "/link")
	public ModelAndView link() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-link.jsp");
		return model;
	}
	
	@Description(value = "물질 일괄링크 실행")
	@ResponseBody
	@PostMapping(value = "/link")
	public Map<String,Object> link(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RohsHelper.service.rohsLink(params);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "파일검색 페이지")
	@GetMapping(value = "/listRohsFile")
	public ModelAndView listRohsFile() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohsFile-list.jsp");
		return model;
	}
	
	@Description(value = "파일검색 조회 함수")
	@ResponseBody
	@PostMapping(value = "/listRohsFile")
	public Map<String, Object> listRohsFile(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RohsHelper.manager.listRohsFile(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "부품현황 페이지")
	@GetMapping(value = "/listAUIRoHSPart")
	public ModelAndView listAUIRoHSPart() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-listAUIRoHSPart.jsp");
		return model;
	}
	
	@Description(value = "부품현황 조회 함수")
	@ResponseBody
	@PostMapping(value = "/listAUIRoHSPart")
	public Map<String, Object> listAUIRoHSPart(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RohsHelper.manager.listAUIRoHSPart(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "제품현황 페이지")
	@GetMapping(value = "/listRoHSProduct")
	public ModelAndView listRoHSProduct() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-listRoHSProduct.jsp");
		return model;
	}
	
	@Description(value = "제품현황 조회 함수")
	@ResponseBody
	@PostMapping(value = "/listRoHSProduct")
	public Map<String, Object> listRoHSProduct(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RohsHelper.manager.listRoHSProduct(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "일괄결재 페이지")
	@GetMapping(value = "/all")
	public ModelAndView all() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-all.jsp");
		return model;
	}
	
	/**	관련 문서 추가
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_RohsSelect")
	public ModelAndView include_RohsSelect(HttpServletRequest request, HttpServletResponse response) {
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String type = request.getParameter("type");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"),"");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"),"");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "rohs");
		List<RohsData> list = new ArrayList<RohsData>();
		List<RohsData> templist = new ArrayList<RohsData>();
		try {
			templist = RohsHelper.service.include_RohsView(oid, module, "composition");
			for(RohsData data : templist){
				
				//최신 버전만 select
				if(data.isLatest()){
					list.add(data);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<RohsData>();
			
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/rohs/include_RohsSelect");
		model.addObject("list", list);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("type", type);
		model.addObject("state",state);
		model.addObject("searchType",searchType);
		model.addObject("lifecycle",lifecycle);
		return model;
	}
	
	/** 문서 RoHS 팝업
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectRohsPopup")
	public ModelAndView selectRohsPopup(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		String type = StringUtil.checkReplaceStr(request.getParameter("type"), "select");
		String state = StringUtil.checkReplaceStr(request.getParameter("state"), "");
		String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"),"");
		String lifecycle = StringUtil.checkReplaceStr(request.getParameter("lifecycle"), "LC_Default");
		
		model.addObject("mode", mode);
		model.addObject("type", type);
		model.addObject("state", state);
		model.addObject("searchType",searchType);
		model.addObject("lifecycle", lifecycle);
		model.setViewName("popup:/rohs/selectRohsPopup");
		return model;
	}
	
	/**	관련 RoHS 보기
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_RohsView")
	public ModelAndView include_RohsView(HttpServletRequest request, HttpServletResponse response) {
		String roleType = request.getParameter("roleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String module = StringUtil.checkReplaceStr(request.getParameter("module"), "rohs");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		//ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		List<RohsData> list = null;
		try {
			//list = RohsQueryHelper.service.getRepresentToLinkList(rohs,roleType);
			list = RohsHelper.service.include_RohsView(oid, module, roleType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("roleType", roleType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		model.setViewName("include:/rohs/include_RohsView");
		return model;
	}
	
	/**	ROHS 개정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
//	@ResponseBody
//	@RequestMapping("/reviseRohs")
//	public ResultData reviseRohs(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		ResultData data = RohsHelper.service.reviseUpdate(request, response);
//		return data;
//	}
	
	/** RoHS 자료검색
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listRoHSData")
	public ModelAndView listRoHSData(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module","rohs");
		model.setViewName("default:/rohs/listRoHSData");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/listRoHSDataAction")
	public Map<String,Object> listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = RohsHelper.service.listRoHSDataAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			result = new HashMap<String,Object>();
		}
		return result;
	}
	
	/** 부품의 BOM에서 ROHS 파일 다운로드
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="batchROHSDown", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData batchROHSDown(HttpServletRequest request, HttpServletResponse response) {
		ResultData returnData = new ResultData();
		//System.out.println("Controllor batchROHSDown");
		///System.out.println(" partNumber = "+request.getParameter("partNumber"));
		try {
			
			returnData = RohsHelper.service.batchROHSDown(request, response);
			//CommonHelper.service.batchSecondaryDown(request, response);//.service.batchSecondaryDown(request, response);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
				
		return returnData;
	}
}
