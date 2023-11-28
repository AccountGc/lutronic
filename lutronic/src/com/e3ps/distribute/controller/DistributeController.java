package com.e3ps.distribute.controller;

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

import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.distribute.util.DistributeUtil;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.erp.beans.BOMERPData;
import com.e3ps.erp.beans.PARTERPData;
import com.e3ps.erp.service.ERPSearchHelper;
import com.e3ps.groupware.service.GroupwareHelper;
import com.e3ps.mold.dto.MoldDTO;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.ManagedBaseline;

@Controller
@RequestMapping(value = "/distribute/**")
public class DistributeController extends BaseController {
	
	/** 배포 메인 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/main")
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String viewName = "distribute:/distribute/main";
		
		ModelAndView model = new ModelAndView();
		model.addObject("module","distribute");
		model.setViewName(viewName);
		return model;
	}
	
	/** 작업합 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listWorkItem")
	public ModelAndView listWorkItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module","distribute");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("distribute:/distribute/listWorkItem");
		return model;
	}
	
	/** 작업함 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listWorkItemAction")
	public Map<String,Object> listWorkItemAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = GroupwareHelper.service.listWorkItemAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			result = new HashMap<String,Object>();
		}
		return result;
	}
	
	/** 결재 상세화면
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/approval")
	public ModelAndView approval(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView model = null;
		try {
			model = GroupwareHelper.service.approval(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			model = new ModelAndView();
		}
		String action = request.getParameter("action");
		model.setViewName("distribute:/workprocess/"+ action);
		model.addObject("menu", "menu1");
		model.addObject("module","distribute");
		model.addObject("distributeType",DistributeUtil.Distribute_Inner);
		return model;
	}
	
	/** 진행중, 완료함, 수신함 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listItem")
	public ModelAndView listItem(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("module","distribute");
		
		String state = request.getParameter("state");
		
		
		model.addObject("menu","menu2");
		
		model.setViewName("distribute:/distribute/listItem");
		model.addObject("state", state);
		
		return model;
	}
	
	/**	비밀번호 변경 페이지
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/changePassword")
	public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String id = request.getParameter("id");
		
		if(!StringUtil.checkString(id)) {
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			
			id = user.getName();
			if ( id.equals("Administrator")) id="wcadmin";
		}
		
		String isPop = request.getParameter("isPop");
		
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "distribute");
		model.addObject("id", id);
		model.addObject("isPop", isPop);
		
		String viewName = "distribute:/distribute/changePassword";
		if("true".equals(isPop)) {
			viewName = "popup:/distribute/changePassword";
		}
		
		model.setViewName(viewName);
		return model;
	}
	
	/**	조직도 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/companyTree")
	public ModelAndView companyTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module", "distribute");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.setViewName("distribute:/distribute/companyTree");
		return model;
	}
	
	@Description(value = "품목 검색 페이지")
	@GetMapping(value = "/listPart")
	public ModelAndView listPart() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("/extcore/jsp/distribute/distribute-part-list.jsp");
		return model;
	}
	
	@Description(value = "품목 상세보기")
	@RequestMapping("/partView")
	public ModelAndView partView(@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartDTO dto = new PartDTO(part);
		Map<String, String> map = CommonHelper.manager.getAttributes(oid, "view");

		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("dto", dto);
		model.addAllObjects(map);
		model.setViewName("popup:/distribute/part-view");
		return model;
	}
	
	@Description(value = "완제품 검색 페이지")
	@GetMapping(value = "/listProduction")
	public ModelAndView listProduction() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("/extcore/jsp/distribute/part-list.jsp");
		return model;
	}
	
	@Description(value = "완제품 검색 실행")
	@ResponseBody
	@PostMapping(value = "/listProduction")
	public Map<String, Object> listProduction(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = null;
		try {
			result = PartHelper.manager.listProduction(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description(value = "완제품 상세보기")
	@RequestMapping("/productView")
	public ModelAndView productView(@RequestParam(value = "oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartDTO dto = new PartDTO(part);
		Map<String, String> map = CommonHelper.manager.getAttributes(oid, "view");

		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("dto", dto);
		model.addAllObjects(map);
		model.setViewName("popup:/distribute/product-view");
		return model;
	}
	
	@Description(value = "EO 검색 페이지")
	@GetMapping(value = "/listEO")
	public ModelAndView listEO() throws Exception{
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.setViewName("/extcore/jsp/distribute/distribute-eo-list.jsp");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/listPagingEOAction")
	public Map<String,Object> listPagingEOAction(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> map = new HashMap<>();
		try {
			 map = ECOSearchHelper.service.listPagingAUIEOAction(request, response);//ECRSearchHelper.service.listECRAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	@Description(value = "EO 상세보기")
	@GetMapping(value = "/eoView")
	public ModelAndView eoView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EoDTO dto = new EoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/distribute/eo-view");
		return model;
	}
		
	@Description(value="ECO 검색 페이지")
	@GetMapping(value = "/listECO")
	public ModelAndView listECO(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/distribute/distribute-eco-list.jsp");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/listPagingECOAction")
	public Map<String,Object> listPagingECOAction(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			map = ECOSearchHelper.service.listPagingAUIECOAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	@Description(value = "ECO 상세보기")
	@GetMapping(value = "/ecoView")
	public ModelAndView ecoView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		EcoDTO dto = new EcoDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/distribute/eco-view");
		return model;
	}
	
	@Description(value = "문서검색 페이지")
	@GetMapping(value = "/listDocument")
	public ModelAndView listDocument() throws Exception{
		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		ModelAndView model = new ModelAndView();
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.setViewName("/extcore/jsp/distribute/distribute-document-list.jsp");
		return model;
	}
	
	@Description(value = "문서 상세보기")
	@GetMapping(value = "/documentView")
	public ModelAndView documentView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		DocumentDTO dto = new DocumentDTO(oid);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/distribute/document-view");
		return model;
	}
	
	@Description(value = "금형 검색 페이지")
	@RequestMapping("/listMold")
	public ModelAndView listMold() throws Exception{
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> moldTypeList = NumberCodeHelper.manager.getArrayCodeList("MOLDTYPE");
		List<Map<String, String>> lifecycleList = CommonUtil.getLifeCycleState("LC_Default");
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtil.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("moldTypeList", moldTypeList);
		model.addObject("lifecycleList", lifecycleList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/distribute/distribute-mold-list.jsp");
		return model;
	}
	
	@Description(value = "금형 상세 페이지")
	@GetMapping(value = "/moldView")
	public ModelAndView moldView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		MoldDTO dto = new MoldDTO(doc);

		boolean isAdmin = CommonUtil.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("popup:/distribute/mold-view");
		return model;
	}
	
	/***********************************************************************************************/
	
	@RequestMapping("/include_DistributeECOList")
	public ModelAndView include_DistributeECOList(HttpServletRequest request, HttpServletResponse response){
		String oid = request.getParameter("oid");
		String moduleType = request.getParameter("moduleType");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 ECO"));
		
		List<ECOData> list = new ArrayList<ECOData>();
		try {
			list = ECOSearchHelper.service.include_DistributeEOList(oid,moduleType);
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.addObject("moduleType", moduleType);
		model.addObject("title", title);
		model.setViewName("include:/distribute/include_DistributeECOList");
		return model;
	}
	
	@RequestMapping("/viewChangePart")
	public ModelAndView viewChangePart(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="ecoNumber") String ecoNumber) throws Exception {
		
		
		ModelAndView model = new ModelAndView();
		//String  ecoNumber = request.getParameter("ecoNumber");
		
		model.addObject("ecoNumber",ecoNumber);
		model.setViewName("popup:/distribute/viewChangePart");
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "/listPARTERPAction")
	public List<PARTERPData> listPARTERPAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		String ecoNumber = request.getParameter("ecoNumber");
		List<PARTERPData> itemList = ERPSearchHelper.service.listPARTERPAction(ecoNumber);
		
		return itemList;
	}
	
	@ResponseBody
	@RequestMapping(value = "/listBOMERPAction")
	public List<BOMERPData> listBOMERPAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		String ecoNumber = request.getParameter("ecoNumber");
		List<BOMERPData> itemList = ERPSearchHelper.service.listBOMERPAction(ecoNumber);
		
		return itemList;
	}
	
	
	@RequestMapping("/viewPartBom")
	public ModelAndView viewPartBom(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		String baseName = "";
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline)CommonUtil.getObject(baseline);
			if(line != null){
				baseName = line.getName();
			}
			
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		model.addObject("baseName",baseName);
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/distribute/viewPartBom");
	
	
		return model;
	}
	/*
	@RequestMapping("/viewAUIBOMDwon")
	public ModelAndView viewPartBaselineBOM(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = "";
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/distribute/viewAUIBOMDwon");
	
	
		return model;
	}
	*/
	@ResponseBody
	@RequestMapping(value = "/viewPartBomAction")
	public List<Map<String, Object>> viewPartBomAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		List<Map<String, Object>> list = BomSearchHelper.service.getAUIPartTreeAction(request, response);
		
		return list;
	}
	
	@RequestMapping("/viewAUIBOMDwon")
	public ModelAndView viewAUIBOMDwon(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/distribute/viewAUIBOMDwon");
	
	
		return model;
	}
	
	/**
	 * 최신 Baseline
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewBaselineBom")
	public ModelAndView viewBaselineBom(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		ManagedBaseline line = ChangeHelper.service.getLastBaseline(oid);
		String baseline = request.getParameter("baseline");
		String baseName ="";
		if(line != null){
			baseline = CommonUtil.getOIDString(line);
			baseName = line.getName();
		}
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		//System.out.println("baseName =" + baseName);
		model.addObject("baseName",baseName);
		model.addObject("oid", oid);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/distribute/viewPartBom");
	
	
		return model;
	}
	
	@RequestMapping("/partTreeCompare")
	public ModelAndView partTreeCompare(HttpServletRequest request, HttpServletResponse response) throws WTRuntimeException, WTException {
		
		String oid = request.getParameter("oid");
		String oid2 = request.getParameter("oid2");
		String baseline = null;//request.getParameter("baseline");
		String baseline2 = request.getParameter("baseline2");
		
		
		
		
		String title1 = "";
		String title2 = "";
		
		ManagedBaseline bsobj = null;
		if(baseline!=null && baseline.length()>0){
		    bsobj = (ManagedBaseline)CommonUtil.getObject(baseline);
		}
		
		ManagedBaseline bsobj2 = null;
		if(baseline2!=null && baseline2.length()>0){
		   bsobj2 = (ManagedBaseline)CommonUtil.getObject(baseline2);
		}
		
		if(bsobj == null) {
			title1 = "최신BOM 전개";
		}else {
			title1 = "Baseline전개  - " + bsobj.getName();
		}
		
		if(bsobj2 == null) {
			title2 = "최신BOM 전개";
		}else {
			title2 = "Baseline전개  - " + bsobj2.getName();
		}
		
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
	
	
}
