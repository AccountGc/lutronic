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

import com.e3ps.change.beans.ECRData;
import com.e3ps.change.service.ECRHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.beans.RohsData;
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
	
//	/**	rohs 등록 페이지
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/createRohs")
//	public ModelAndView createRohs(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.addObject("menu", "menu2");
//		model.addObject("module","rohs");
//		model.setViewName("default:/rohs/createRohs");
//		return model;
//	}
	
	@Description(value = "물질 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception{
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/rohs/rohs-create.jsp");
		model.addObject("manufactureList", manufactureList);
		return model;
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
	
//	/** rohs 검색 페이지
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/listRohs")
//	public ModelAndView listRohs(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.addObject("menu", "menu1");
//		model.addObject("module","rohs");
//		model.setViewName("default:/rohs/listRohs");
//		return model;
//	}
	
	@Description(value = "물질검색")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/rohs/rohs-list.jsp");
		return model;
	}
	
	@Description(value = "rohs 상세 페이지")
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
	
//	/**	rohs 상세보기
//	 * @param request
//	 * @param response
//	 * @param oid
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/viewRohs")
//	public ModelAndView viewRohs(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
//		RohsData rohsData = new RohsData(rohs);
//		
//		List<Map<String,Object>> list = RohsHelper.service.getRohsContent(oid); 
//		
//		model.setViewName("popup:/rohs/viewRohs");
//		model.addObject("isAdmin", CommonUtil.isAdmin());
//		model.addObject("rohsData", rohsData);
//		model.addObject("list", list);
//		return model;
//	}
	@Description(value = "RoHS 등록 함수")
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
	
	/**	rohs 등록
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createRohsAction")
	public ResultData createRohsAction(HttpServletRequest request, HttpServletResponse response) {
		return RohsHelper.service.createRohsAction(request, response);
	}
	
	@Description(value = "rohs 조회 함수")
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
	
//	/** rohs 검색
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping("/listRohsAction")
//	public Map<String,Object> listRohsAction(HttpServletRequest request, HttpServletResponse response){
//		Map<String,Object> result = null;
//		try {
//			result = RohsHelper.service.listRohsAction(request, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
	
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
	
//	@ResponseBody
//	@RequestMapping("/listRoHSDataAction")
//	public Map<String,Object> listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) {
//		Map<String,Object> result = null;
//		try {
//			result = RohsHelper.service.listRoHSDataAction(request, response);
//		} catch(Exception e) {
//			e.printStackTrace();
//			result = new HashMap<String,Object>();
//		}
//		return result;
//	}
	
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
	
	/**	문서 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateRohs")
	public ModelAndView updateRohs(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		RohsData rohsData = new RohsData(rohs);
		
		List<Map<String,Object>> list = RohsHelper.service.getRohsContent(oid); 
		
		model.setViewName("popup:/rohs/updateRohs");
		model.addObject("rohsData", rohsData);
		model.addObject("list", list);
		return model;
	}
	
	/**	문서 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateRohsAction")
	public ResultData updateRohsAction(HttpServletRequest request, HttpServletResponse response) {
		//Map<String, Object> map = DocumentHelper.service.requestDocumentMapping(request, response);
		return RohsHelper.service.updateRohsAction(request,response);
	}
	
	/**	ROHS 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deleteRohsAction")
	public Map<String, Object> deleteRohsAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		return RohsHelper.service.delete(oid);
		
	}
	
	/**	ROHS 개정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/reviseRohs")
	public ResultData reviseRohs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultData data = RohsHelper.service.reviseUpdate(request, response);
		return data;
	}
	
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
	
//	/**  부품현황
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/listAUIRoHSPart")
//	public ModelAndView listAUIRoHSPart(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.addObject("menu", "menu5");
//		model.addObject("module","rohs");
//		model.setViewName("default:/rohs/listAUIRoHSPart");
//		return model;
//	}
	
	@ResponseBody
	@RequestMapping("/listRoHSPartAction")
	public Map<String, Object> listRoHSPartAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = null;
		try {
			//map = TestQueryHelper.service.listRoHSPartAction(request, response);
			map = RohsHelper.service.listRoHSPartAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/listAUIRoHSPartAction")
	public Map<String, Object> listAUIRoHSPartAction(HttpServletRequest request, HttpServletResponse response) {
		//List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> returnMap =new HashMap<String, Object>();
		try {
			//map = TestQueryHelper.service.listRoHSPartAction(request, response);
			returnMap= RohsHelper.service.listAUIRoHSPartAction(request, response);
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return returnMap;
	}
	
//	/**  제품현황
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/listRoHSProduct")
//	public ModelAndView listPartsRoHSState(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		model.addObject("menu", "menu6");
//		model.addObject("module","rohs");
//		model.setViewName("default:/rohs/listRoHSProduct");
//		return model;
//	}
	
	@ResponseBody
	@RequestMapping("/listRoHSProductAction")
	public Map<String, Object> listPartsRoHSStateAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = null;
		try {
			map = RohsHelper.service.listRoHSProductAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
		}
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/duplicateName")
	public ResultData duplicateName(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="rohsName") String rohsName) {
		return RohsHelper.service.duplicateName(rohsName);
	}
	
	@Description(value = "물질복사 페이지")
	@GetMapping(value = "/copyRohs")
	public ModelAndView copyRohs(@RequestParam String oid) throws Exception{
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
		RohsData data = new RohsData(rohs);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/rohs/copyRohs.jsp");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/copyRohsAction")
	public ResultData copyRohsAction(HttpServletRequest request, HttpServletResponse response) {
		return RohsHelper.service.copyRohsAction(request, response);
	}
	
	/** rohs 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/checkList")
	public ModelAndView checkList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String rohsName = StringUtil.checkNull(request.getParameter("rohsName"));
		String rohsNumber = StringUtil.checkNull(request.getParameter("rohsNumber"));
		model.addObject("rohsName", rohsName);
		model.addObject("rohsNumber",rohsNumber);
		model.setViewName("popup:/rohs/checkList");
		return model;
	}
	
	@RequestMapping("/createPackageRoHS")
	public ModelAndView createPackageRoHS(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu7");
		model.addObject("module","rohs");
		model.setViewName("default:/rohs/createPackageRoHS");
		return model;
	}
	
	@RequestMapping("/createPackageRoHSAction")
	public ModelAndView createPackageRoHSAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = RohsHelper.service.createPackageRoHSAction(request, response);
		
		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/rohs/createPackageRoHSAction");
		return model;
	}
	
	@RequestMapping("/createPackageRoHSLink")
	public ModelAndView createPackageRoHSLink(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu8");
		model.addObject("module","rohs");
		model.setViewName("default:/rohs/createPackageRoHSLink");
		return model;
	}
	
	@RequestMapping("/createPackageRoHSLinkAction")
	public ModelAndView createPackageRoHSLinkAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = RohsHelper.service.createPackageRoHSLinkAction(request, response);
		
		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/rohs/createPackageRoHSLinkAction");
		return model;
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
	
	//
}
