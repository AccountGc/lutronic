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
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;

import wt.enterprise.Master;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/part/**")
public class PartController extends BaseController {
	
	/**
	 * 
	 * 		LUTRONIC 추가 시작
	 * 
	 * 
	 */
	
	@Description(value = "품목 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-list.jsp");
		return model;
	}
	
	@Description(value = "품목 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-create.jsp");
		return model;
	}
	
	@Description(value = "일괄 등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-batch.jsp");
		return model;
	}
	
	@Description(value = "BOM EDITOR 페이지")
	@GetMapping(value = "/bom")
	public ModelAndView bom() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-bom.jsp");
		return model;
	}
	
	/** 품목 단위 리스트 리턴
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
	public ResultData create(@RequestBody Map<String, Object> params){
		Map<String,Object> map = PartHelper.service.requestPartMapping(params);
		return PartHelper.manager.create(map);
	}
	
	/**  부품 등록시 SEQ 버튼
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/searchSeqList")
	public ModelAndView searchSeqList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String partNumber = request.getParameter("partNumber");
		model.addObject("partNumber", partNumber);
		model.setViewName("popup:/part/searchSeqList");
		return model;
	}
	
	@Description(value = "품목 데이터 검색")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String,Object> list(@RequestBody Map<String, Object> params) {
		Map<String,Object> result = null;
		try {
			result = PartHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch(Exception e) {
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

	/**	품목 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewPart")
	public ModelAndView viewPart(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		PartData partData = new PartData(part);
		model.addObject("oid",oid);
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("partData", partData);
		model.setViewName("popup:/part/viewPart");
		return model;
	}

	/** 품목 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/deletePartAction")
	public Map<String,Object> deletePartAction(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		Map<String,String> hash = new HashMap<String,String>();
		
		hash.put("oid", oid);
        
		Map<String,Object> result = PartHelper.service.delete(hash);
		
		return result;
	}

	/** 품목 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updatePart")
	public ModelAndView updatePart(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		PartData partData = new PartData(part);
		model.addObject("partData", partData);
		model.setViewName("popup:/part/updatePart");
		return model;
	}
	
	/** 품목 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updatePartAction")
	public ResultData updatePartAction(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> map = PartHelper.service.requestPartMapping(params);
		return PartHelper.service.updatePartAction(map);
	}
	
	/**  일괄등록 페이지 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createPackagePart")
	public ModelAndView createPackagePart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","part");
		model.setViewName("default:/part/createPackagePart");
		return model;
	}
	
	/**  일괄등록 페이지 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createAUIPackagePart")
	public ModelAndView createAUIPackagePart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","part");
		model.setViewName("default:/part/createAUIPackagePart");
		return model;
	}
	
	/**  일괄등록 실행
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
	
	/**  일괄등록  AUI 실행
	 * @param request
	 * @param response
	 * @return
	 */
	//@RequestMapping("/createAUIPackagePartAction")
	//public ModelAndView createAUIPackagePartAction(@RequestBody Map<String,Object> param) {
	@ResponseBody
	@RequestMapping(value = "/createAUIPackagePartAction", method=RequestMethod.POST)
	public ResultData createAUIPackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.createAUIPackagePartAction(request, response);
	}
	
	/**  채번 페이지 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/partChange")
	public ModelAndView partChange(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		
		List<Map<String,Object>> list = null;
		
		try {
			list = PartHelper.service.partChange(partOid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		
		List<NumberCodeData> partType = CodeHelper.service.topCodeToList("PARTTYPE");
		
		ModelAndView model = new ModelAndView();
		model.addObject("partOid", partOid);
		model.addObject("list", list);
		model.addObject("partType", partType);
		model.setViewName("popup:/part/partChange");
		return model;
	}
	
	/**
	 * 채번 페이지 AUI Greid 적용 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateAUIPartChange")
	public ModelAndView updateAUIPartChange(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		/*
		List<Map<String,Object>> list = null;
		
		try {
			list = PartHelper.service.partChange(partOid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		*/
		List<NumberCodeData> partType = CodeHelper.service.topCodeToList("PARTTYPE");
		

		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String,Object>> list = null;
		//System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIPartChangeListGrid(partOid,false);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}

		ModelAndView model = new ModelAndView();
		model.addObject("partOid", partOid);
		model.addObject("list", list);
		model.addObject("size", list.size());
		model.addObject("partType", partType);
		
		model.setViewName("popup:/part/updateAUIPartChange");
		return model;
	}
	
	/**  채번 페이지 AUI 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAUIPartChangeAction")
	public ResultData updateAUIPartChangeAction(@RequestBody Map<String,Object> param) {
		return PartHelper.service.updateAUIPartChangeAction(param);
	}
	
	@ResponseBody
	@RequestMapping("/updateAUIPartChangeSearchAction")
	public List<Map<String,Object>> updateAUIPartChangeSearchAction(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String,Object>> list = null;
		//System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIPartChangeListGrid(oid,false);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		
		return list;
	}
	
	/** 채번 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/actionBom")
	public ResultData actionBom(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.changeNumber(request);
	}
	
	/**  일괄수정  페이지 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updatePackagePart")
	public ModelAndView updatePackagePart(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		
		List<Map<String,Object>> list = null;
		
		try {
			list = PartHelper.service.partBomListGrid(oid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		
		List<NumberCodeData> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeData> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeData> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeData> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeData> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeData> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
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
	
	/**
	 * 일괄 수정 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateAUIPackagePart")
	public ModelAndView updateAUIPackagePart(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/part/updateAUIPackagePart");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/updateAUIPackageSearchAction")
	public List<Map<String,Object>> updateAUIPackageSearchAction(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String,Object>> list = null;
		//System.out.println("updateAUIPackagePartAction oid =" + oid);
		try {
			list = BomSearchHelper.service.updateAUIBomListGrid(oid,isCheckDummy);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		
		return list;
	}
	
	/**
	 * 일괄 수정 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchAtttribute")
	public ModelAndView batchAtttribute(HttpServletRequest request, HttpServletResponse response) {
		
		List<NumberCodeData> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeData> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeData> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeData> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeData> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeData> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
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
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchPartNumber")
	public ModelAndView batchPartNumber(HttpServletRequest request, HttpServletResponse response) {
		List<NumberCodeData> gubunList = CodeHelper.service.numberCodeList("PARTTYPE","", false);
		
		ModelAndView model = new ModelAndView();
		model.addObject("gubunList", gubunList);
		model.setViewName("popup:/part/batchPartNumber");
		return model;
	}
	
	
	
	/**
	 * 일괄 등록
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchCreate")
	public ModelAndView batchCreate(HttpServletRequest request, HttpServletResponse response) {
		
		String auiId = StringUtil.checkNull(request.getParameter("auiId"));
		String mode = StringUtil.checkNull(request.getParameter("mode")); //single
		
		String title ="일괄 추가 ";
		if(mode.equals("single")){
			title = "수정["+auiId+"]";
		}
		//System.out.println("batchCreate auiId =" + auiId);
		
		ModelAndView model = new ModelAndView();
		model.addObject("auiId", auiId);
		model.addObject("mode",mode);
		model.addObject("title",title);
		
		
		model.setViewName("popup:/part/batchCreate");
		return model;
	}
	
	
	/**  부품 상태 수정
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/partStateChange")
	public ResultData partStateChange(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.partStateChange(request, response);
	}
	
	/**  부품 일괄 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePackagePartAction")
	public ResultData updatePackagePartAction(HttpServletRequest request, HttpServletResponse response) {
		return PartHelper.service.updatePackagePartAction(request, response);
	}
	
	/**  부품 일괄 수정 수행
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAUIPackagePartAction")
	public ResultData updateAUIPackagePartAction(@RequestBody Map<String,Object> param) {
		return PartHelper.service.updateAUIPackagePartAction(param);
	}
	
	/**
	 * EO에서 완제품 품번 선택
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_CompletePartSelect")
	public ModelAndView include_CompletePartSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		
		EChangeOrder eco  = (EChangeOrder)CommonUtil.getObject(oid);
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
	 * 		LUTRONIC 추가 끝
	 * 
	 * 
	 */
	
	
	
	
	
	/** 관련 품목 추가
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
		
		List<PartData> list = null;
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
	
	/** 관련 품목 보기
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
		model.addObject("distribute",distribute);
		model.addObject("paramName", paramName);

		return model;
	}
	
	/** 품목 검색 팝업
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
	
	/** END ITEM 상세보기
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/bomPartList")
	public ModelAndView bomPartList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		
		Map<String,Object> map = PartHelper.service.bomPartList(request,response);
		
		model.setViewName("popup:/part/bomPartList");
		model.addAllObjects(map);
		return model;
	}
	
	/** BOM 상세보기
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
		} catch(Exception e) {
			e.printStackTrace();
			bsobj = null;
		}

		if(view==null){
			view = views[0].getName();
		}

		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
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
	
	/** BOM 데이터 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getPartTreeAction")
	public Map<String,Object> getPartTreeAction(HttpServletRequest request, HttpServletResponse response){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			map = PartHelper.service.getPartTreeAction(request,response);
			map.put("result", true);
			map.put("msg", "OK");
		}catch(Exception e) {
			map.put("result", false);
			map.put("msg",e.getLocalizedMessage());
			e.printStackTrace();
		}

		return map;
	}
	
	@RequestMapping("/partTreeCompare")
	public ModelAndView partTreeCompare(HttpServletRequest request, HttpServletResponse response) throws WTRuntimeException, WTException {
		
		String oid = request.getParameter("oid");
		String oid2 = request.getParameter("oid2");
		String baseline = request.getParameter("baseline");
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
		//System.out.println("sssssssssss");
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
	public Map<String,Object> getBaseLineCompare(HttpServletRequest request, HttpServletResponse response){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			map = PartHelper.service.getBaseLineCompare(request, response);
			
			map.put("result", true);

		}catch(Exception e) {
			map.put("result", false);
			map.put("msg",e.getLocalizedMessage());
			e.printStackTrace();
		}

		return map;
	}
	
	/**	BOM 일괄등록
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/excelBOMLoad")
	public ModelAndView excelBOMLoad(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu","menu5");
		model.addObject("module","part");
		model.setViewName("default:/part/excelBOMLoad");
		return model;
	}
	
	@RequestMapping("/excelBomLoadAction")
	public ModelAndView excelBomLoadAction(HttpServletRequest request, HttpServletResponse response) {
		
		String xmlString = "";
		
		try {
			xmlString = PartHelper.service.excelBomLoadAction(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/part/excelBOMLoadAction");
		return model;
	}
	
	
	/** 대상 품목
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ChangePartView")
	public ModelAndView include_ChangePartView(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="oid") String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		List<PartData> list = null;
		//list = PartHelper.service.include_ChangePartList(oid);
		model.setViewName("popup:/part/include_ChangePartView");
		//model.addObject("list", list);
		
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
	public List<Map<String,Object>> partExpandAction(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		String moduleType = request.getParameter("moduleType");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"),"true");
		
		List<Map<String,Object>> list = null;
		try {
			list = BomSearchHelper.service.partExpandAUIBomListGrid(partOid, moduleType, desc);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
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
	public Map<String,Object> selectEOPartAction(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> map = null;
		try {
			map = PartHelper.service.selectEOPartAction(request, response);
		} catch(Exception e) {
			map = new HashMap<String,Object>();
			e.printStackTrace();
		}
		return map;
	}
	
	@RequestMapping("/include_DocumentFilePath")
	public ModelAndView include_DocumentFilePath(HttpServletRequest request, HttpServletResponse response) {
		String title = request.getParameter("title");
		boolean control = Boolean.valueOf(StringUtil.checkReplaceStr(request.getParameter("control"),"true")).booleanValue();
		
		ModelAndView model = new ModelAndView();
		model.addObject("title", title);
		model.addObject("control", control);
		model.setViewName("include:/part/include_DocumentFilePath");
		return model;
	}
	
	@RequestMapping("/createUserPart")
	public ModelAndView createUserPart(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu","menu4");
		model.addObject("module","part");
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
		} catch(Exception e) {
			e.printStackTrace();
			bsobj = null;
		}

		if(view==null){
			view = views[0].getName();
		}

		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(oid, "", "");
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
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
	public List<Map<String,Object>> viewPartBomAction(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,Object>> list = null;
		try {
			list = PartHelper.service.viewPartBomAction(request, response);
		} catch(Exception e) {
			list = new ArrayList<Map<String,Object>>();
		}
		return list;
	}
	
	@ResponseBody
	@RequestMapping("/searchSeqAction")
	public Map<String,Object> searchSeqAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = null;
		
		try {
			map = PartHelper.service.searchSeqAction(request,response);
			
		}catch(Exception e) {
			e.printStackTrace();
			map = new HashMap<String,Object>();
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
	
	
	/**  일괄수정  페이지 이동
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updatefamilyPart")
	public ModelAndView updatefamilyPart(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		
		List<Map<String,Object>> list = null;
		
		try {
			list = PartHelper.service.partInstanceGrid(oid);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,Object>>();
		}
		
		List<NumberCodeData> productmethod = CodeHelper.service.numberCodeList("PRODUCTMETHOD", "", false);
		List<NumberCodeData> deptCode = CodeHelper.service.numberCodeList("DEPTCODE", "", false);
		List<NumberCodeData> modelcode = CodeHelper.service.numberCodeList("MODEL", "", false);
		List<NumberCodeData> manufacture = CodeHelper.service.numberCodeList("MANUFACTURE", "", false);
		List<NumberCodeData> mat = CodeHelper.service.numberCodeList("MAT", "", false);
		List<NumberCodeData> finish = CodeHelper.service.numberCodeList("FINISH", "", false);
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
		
		model.setViewName("popup:/part/updatefamilyPart");
		return model;
	}
	
	/**  BOM 에서 선택적 부품 첨부 파일 다운로드
	 * @param request
	 * @param response
	 */
	/*
	@RequestMapping(value="partTreeSelectAttachDown")
	public ModelAndView partTreeSelectAttachDown(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		
		try {
			PartHelper.service.partTreeSelectAttachDown(request, response);
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
				//model.addObject("xmlString", xmlString);
				model.setViewName("empty:/drawing/createPackageDrawingAction");
		return model;
	}
	*/
	
	@RequestMapping(value = "/partTreeSelectAttachDown")
	public ModelAndView partTreeSelectAttachDown(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> param) {
		
		ModelAndView model = new ModelAndView();
		
		try {
			PartHelper.service.partTreeSelectAttachDown(request,response,param);
			//ExcelDownHelper.service.partExcelDown(null, response);
			
		} catch(Exception e ){
			e.printStackTrace();
			
		}
		model.setViewName("empty:/drawing/createPackageDrawingAction");
		return model;
	}
	
	/** 속성 Cleaning
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/attributeCleaning")
	public ResultData attributeCleaning(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="oid") String oid) throws Exception {
		
		ResultData result = PartHelper.service.attributeCleaning(oid);
		
		return result;
	}
	
	/**
	 * AUI 그리드 BOM
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/viewAUIPartBom")
	public ModelAndView viewAUIPartBom(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String oid = request.getParameter("oid");
		String baseline = request.getParameter("baseline");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"), "false");
		String desc = StringUtil.checkReplaceStr(request.getParameter("desc"), "true");
		String view = request.getParameter("view");
		String title = "";
		String number="";
		
		WTPart part =(WTPart)CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline)CommonUtil.getObject(baseline);
			if(line != null){
				title = line.getName();
			}else{
				
				title = number;
				
			}
			
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if(!PartSearchHelper.service.isLastPart(part)){
			try {
				lastedpart = (WTPart)ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title",title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);
		
		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom");
	
	
		return model;
	}
	
	/**
	 * AUI 그리드 BOM
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
		String number="";
		
		WTPart part =(WTPart)CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline)CommonUtil.getObject(baseline);
			if(line != null){
				title = line.getName();
			}else{
				
				title = number;
				
			}
			
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if(!PartSearchHelper.service.isLastPart(part)){
			try {
				lastedpart = (WTPart)ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title",title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);
		
		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom2");
	
	
		return model;
	}
	/**
	 * AUI 그리드 BOM
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
		String number="";
		
		WTPart part =(WTPart)CommonUtil.getObject(oid);
		number = part.getNumber();
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			ManagedBaseline line = (ManagedBaseline)CommonUtil.getObject(baseline);
			if(line != null){
				title = line.getName();
			}else{
				
				title = number;
				
			}
			
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		String lastedoid = oid;
		WTPart lastedpart = part;
		if(!PartSearchHelper.service.isLastPart(part)){
			try {
				lastedpart = (WTPart)ObjectUtil.getLatestObject((Master) part.getMaster());
				lastedoid = CommonUtil.getOIDString(lastedpart);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addObject("title",title);
		model.addObject("oid", oid);
		model.addObject("lastedoid", lastedoid);
		
		model.addObject("number", number);
		model.addObject("list", list);
		model.addObject("baseline", baseline);
		model.addObject("allBaseline", allBaseline);
		model.addObject("desc", desc);
		//model.addObject("bsobj", bsobj);
		model.addObject("view", view);
		model.setViewName("popup:/part/viewAUIPartBom3");
	
	
		return model;
	}
	
	/**
	 * AUI BOM Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/viewAUIPartBomAction")
	public List<Map<String, Object>> viewAUIPartBomAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		List<Map<String, Object>> list = BomSearchHelper.service.getAUIPartTreeAction(request, response);
		
		return list;
	}
	
	/**
	 * AUI BOM Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getAUIBOMRootChildAction")
	public List<Map<String, Object>> getAUIBOMRootChildAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		List<Map<String, Object>> list = BomSearchHelper.service.getAUIBOMRootChildAction(request, response);
		
		return list;
	}
	
	/**
	 * AUI BOM Action
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getAUIBOMPartChildAction")
	public List<Map<String, Object>> getAUIBOMPartChildAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 
		List<Map<String, Object>> list = BomSearchHelper.service.getAUIBOMPartChildAction(request, response);
		
		return list;
	}
	/*
	@ResponseBody
	@RequestMapping("/isCheckNumber")
	public boolean isCheckNumber(HttpServletRequest request, HttpServletResponse response) {
		
		String num = request.getParameter("num");
		boolean chk = PartHelper.service.isDubleCheck(num);
		return chk;
	}
	*/
	
	/** 속성 수정
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/attributeChange")
	public ResultData attributeChange(HttpServletRequest request, HttpServletResponse response){
		ResultData data = new ResultData();
		try{
			String oid = request.getParameter("oid");
			String value = request.getParameter("value");
			String numberCodeType = request.getParameter("numberCodeType");
			
			WTPart part = (WTPart)CommonUtil.getObject(oid);
			//System.out.println(part.getNumber()+","+oid +" ," + value+ "," +numberCodeType);
			
			
			IBAUtil.changeIBAValue(part, numberCodeType, value);
			
			data.setResult(true);
			data.setMessage("속성이 수정 되었습니다.");
		}catch(Exception e){
			data.setResult(true);
			data.setMessage("속성 수정시 에러가 발생 하였습니다.");
			e.printStackTrace();
		}
		
		return data;
	}
	
}
