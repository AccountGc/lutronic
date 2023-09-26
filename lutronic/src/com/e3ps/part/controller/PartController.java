package com.e3ps.part.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.e3ps.common.comments.CommentsData;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.controller.BaseController;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.enterprise.Master;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Controller
@RequestMapping(value = "/part/**")
public class PartController extends BaseController {

	@Description(value = "품목 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.setViewName("/extcore/jsp/part/part-list.jsp");
		return model;
	}

	@Description(value = "관련 품목 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
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
		ArrayList<NumberCode> manufactureList = NumberCodeHelper.manager.getArrayCodeList("MANUFACTURE");
		ArrayList<NumberCode> finishList = NumberCodeHelper.manager.getArrayCodeList("FINISH");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		ModelAndView model = new ModelAndView();
		model.addObject("modelList", modelList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("manufactureList", manufactureList);
		model.addObject("finishList", finishList);
		model.addObject("unitList", unitList);
		model.setViewName("/extcore/jsp/part/part-create.jsp");
		return model;
	}

	@Description(value = "일괄 등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() throws Exception {

		String location = DrawingHelper.ROOTLOCATION;
		String container = "product";

		ArrayList<Folder> folderList = FolderUtils.loadAllFolder(location, container);
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> matList = NumberCodeHelper.manager.getArrayCodeList("MAT");
		ArrayList<NumberCode> productmethodList = NumberCodeHelper.manager.getArrayCodeList("PRODUCTMETHOD");
		ArrayList<NumberCode> partName1List = NumberCodeHelper.manager.getArrayCodeList("PARTNAME1");
		ArrayList<NumberCode> partName2List = NumberCodeHelper.manager.getArrayCodeList("PARTNAME2");
		ArrayList<NumberCode> partName3List = NumberCodeHelper.manager.getArrayCodeList("PARTNAME3");
		List<NumberCodeDTO> partType1List = new ArrayList<NumberCodeDTO>();
		partType1List = NumberCodeHelper.manager.getArrayPartTypeList("PARTTYPE", "");
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		ModelAndView model = new ModelAndView();
		model.addObject("folderList", folderList);
		model.addObject("modelList", modelList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("matList", matList);
		model.addObject("productmethodList", productmethodList);
		model.addObject("partName1List", partName1List);
		model.addObject("partName2List", partName2List);
		model.addObject("partName3List", partName3List);
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

	@Description(value = "BOM EDITOR 페이지")
	@GetMapping(value = "/bom")
	public ModelAndView bom() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-bom.jsp");
		return model;
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
	@GetMapping(value = "/searchSeqList")
	public ModelAndView searchSeqList(HttpServletRequest request, HttpServletResponse response) {
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
	@RequestMapping("/delete")
	public Map<String, Object> delete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = PartHelper.service.delete(params);
		if ((boolean) result.get("result")) {
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
	public ModelAndView update(@RequestParam String oid) {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		PartData partData = null;
		try {
			partData = new PartData(part);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("data", partData);
		model.setViewName("popup:/part/updatePart");
		return model;
	}

	@Description(value = "품목 수정")
	@ResponseBody
	@PostMapping(value = "/update")
	public Map<String, Object> updatePartAction(@RequestBody Map<String, Object> params) throws Exception {
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
	@GetMapping(value = "/partChange")
	public ModelAndView partChange(@RequestParam String oid) {

		List<Map<String, Object>> list = null;

//		try {
//			list = PartHelper.service.partChange(oid);
//		} catch(Exception e) {
//			e.printStackTrace();
//			list = new ArrayList<Map<String,Object>>();
//		}
//		
//		List<NumberCodeData> partType = CodeHelper.service.topCodeToList("PARTTYPE");

		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
//		model.addObject("list", list);
//		model.addObject("partType", partType);
		model.setViewName("popup:/part/partChange");
		return model;
	}

	@Description(value = "채번 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/partChange")
	public Map<String, Object> partChange(@RequestBody Map<String, Object> params) {

		List<Map<String, Object>> list = null;
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");

		try {
			list = PartHelper.service.partChange(oid);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			list = new ArrayList<Map<String, Object>>();
		}

		List<NumberCodeDTO> partType = CodeHelper.service.topCodeToList("PARTTYPE");

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
	public List<Map<String, Object>> updateAUIPartChangeSearchAction(HttpServletRequest request,
			HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String checkDummy = request.getParameter("checkDummy");
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

	@Description(value = "품목등록 seqList 검색 메서드")
	@ResponseBody
	@PostMapping(value = "/searchSeqAction")
	public Map<String, Object> searchSeqAction(@RequestBody Map<String, Object> params) {
		Map<String, Object> map = null;
		try {
			map = PartHelper.service.searchSeqAction(params);
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
	 * 속성 Cleaning
	 * 
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/attributeCleaning", method = RequestMethod.POST)
	public ResultData attributeCleaning(@RequestBody Map<String, Object> param) throws Exception {

		ResultData result = PartHelper.service.attributeCleaning(param);

		return result;
	}

	/**
	 * AUI 그리드 BOM
	 * 
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
	/*
	 * @ResponseBody
	 * 
	 * @RequestMapping("/isCheckNumber") public boolean
	 * isCheckNumber(HttpServletRequest request, HttpServletResponse response) {
	 * 
	 * String num = request.getParameter("num"); boolean chk =
	 * PartHelper.service.isDubleCheck(num); return chk; }
	 */

	/**
	 * 속성 수정
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/attributeChange")
	public ResultData attributeChange(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		try {
			String oid = request.getParameter("oid");
			String value = request.getParameter("value");
			String numberCodeType = request.getParameter("numberCodeType");

			WTPart part = (WTPart) CommonUtil.getObject(oid);
			// System.out.println(part.getNumber()+","+oid +" ," + value+ ","
			// +numberCodeType);

			IBAUtil.changeIBAValue(part, numberCodeType, value);

			data.setResult(true);
			data.setMessage("속성이 수정 되었습니다.");
		} catch (Exception e) {
			data.setResult(true);
			data.setMessage("속성 수정시 에러가 발생 하였습니다.");
			e.printStackTrace();
		}

		return data;
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

	/**
	 * 엑셀 다운로드
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/partExcel", method = RequestMethod.GET)
	public void partExcel(@RequestParam String oid, @RequestParam String view, @RequestParam String desc,
			@RequestParam String baseline2, @RequestParam String checkDummy, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("oid", oid);
		param.put("view", view);
		param.put("desc", desc);
		param.put("baseline2", baseline2);
		param.put("checkDummy", checkDummy);
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		List<Map<String, Object>> bomList = BomSearchHelper.manager.getAllBomList(param);

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		// Part Thumbnail
		String path = "C:\\ptc\\thumb";
		// String path = "/opt/ptc/partlistExcelImages";
		Representation representation = PublishUtils.getRepresentation(part);
		FileOutputStream fos = null;
		if (StringUtil.isNotNull(representation)) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.THUMBNAIL);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				String ext = FileUtil.getExtension(data.getFileName());
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);

				File file = new File(path + File.separator + part.getNumber().toUpperCase() + ".jsp");

				// FileOutputStream fos = new FileOutputStream(file);
				fos = new FileOutputStream(file);

				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		}

		try {
			// 새로운 워크북(엑셀 파일) 생성
			XSSFWorkbook workbook = new XSSFWorkbook();

			// 워크북에 시트 생성
			Sheet sheet = workbook.createSheet("Sheet1");

			sheet.setColumnWidth(1, 30 * 256);
	
			// 헤더
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("Bom Editor");

			CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 7);
			sheet.addMergedRegion(mergedRegion);
			style(workbook, cell);

			Row row1 = sheet.createRow(2);

			Cell cell00 = row1.createCell(0);
			cell00.setCellValue("No.");
			style2(workbook, cell00);

			Cell cell01 = row1.createCell(1);
			cell01.setCellValue("Thumbanil");
			style2(workbook, cell01);

			Cell cell02 = row1.createCell(2);
			cell02.setCellValue("Level");
			style2(workbook, cell02);

			Cell cell03 = row1.createCell(3);
			cell03.setCellValue("부품번호");
			style2(workbook, cell03);

			Cell cell04 = row1.createCell(4);
			cell04.setCellValue("도면번호");
			style2(workbook, cell04);

			Cell cell05 = row1.createCell(5);
			cell05.setCellValue("부품명");
			style2(workbook, cell05);

			Cell cell06 = row1.createCell(6);
			cell06.setCellValue("REV");
			style2(workbook, cell06);

			Cell cell07 = row1.createCell(7);
			cell07.setCellValue("OEM Info.");
			style2(workbook, cell07);

			Cell cell08 = row1.createCell(8);
			cell08.setCellValue("체크아웃 상태");
			style2(workbook, cell08);

			Cell cell09 = row1.createCell(9);
			cell09.setCellValue("상태");
			style2(workbook, cell09);

			Cell cell10 = row1.createCell(10);
			cell10.setCellValue("수정자");
			style2(workbook, cell10);

			Cell cell11 = row1.createCell(11);
			cell11.setCellValue("사양");
			style2(workbook, cell11);

			Cell cell12 = row1.createCell(12);
			cell12.setCellValue("수량");
			style2(workbook, cell12);

			Cell cell13 = row1.createCell(13);
			cell13.setCellValue("ECO NO.");
			style2(workbook, cell13);

			Cell cell14 = row1.createCell(14);
			cell14.setCellValue("부서");
			style2(workbook, cell14);

			Cell cell15 = row1.createCell(15);
			cell15.setCellValue("MANUFACTURER");
			style2(workbook, cell15);

			Cell cell14 = row1.createCell(14);
			cell14.setCellValue("프로젝트코드");
			style2(workbook, cell14);
			
			Cell cell15 = row1.createCell(15);
			cell15.setCellValue("부서");
			style2(workbook, cell15);
	
			Cell cell16 = row1.createCell(16);
			cell16.setCellValue("MANUFACTURER");
			style2(workbook, cell16);

			int rowCellCnt = 1;
			int rowCnt = 3;
			for (Map<String, Object> item : bomList) {
				int level = 1;
				String number = item.get("number") == null ? "" : item.get("number").toString();
				String dwgNo = item.get("dwgNo") == null ? "" : item.get("dwgNo").toString();
				String name = item.get("name") == null ? "" : item.get("name").toString();
				String rev = item.get("rev") == null ? "" : item.get("rev").toString();
				String remarks = item.get("remarks") == null ? "" : item.get("remarks").toString();
				String checkOutSts = item.get("checkOutSts") == null ? "" : item.get("checkOutSts").toString();
				String state = item.get("state") == null ? "" : item.get("state").toString();
				String modifier = item.get("modifier") == null ? "" : item.get("modifier").toString();
				String spec = item.get("spec") == null ? "" : item.get("spec").toString();
				String quantity = item.get("quantity") == null ? "" : item.get("quantity").toString();
				String ecoNo = item.get("ecoNo") == null ? "" : item.get("ecoNo").toString();
				String model = item.get("model") == null ? "" : item.get("model").toString();
				String deptcode = item.get("deptcode") == null ? "" : item.get("deptcode").toString();
				String manufacture = item.get("manufacture") == null ? "" : item.get("manufacture").toString();
				String productmethod = item.get("productmethod") == null ? "" : item.get("productmethod").toString();

=======
			
			Cell cell17 = row1.createCell(17);
			cell17.setCellValue("제작방법");
			style2(workbook, cell17);
			
			int rowCellCnt = 1;
			int rowCnt = 3;
			for (Map<String, Object> item : bomList) {
				String number = item.get("number") ==null ?"":item.get("number").toString();
				String dwgNo = item.get("dwgNo") ==null ?"":item.get("dwgNo").toString();
				String level = item.get("level") ==null ?"":   String.valueOf(Integer.parseInt(item.get("level").toString()) +1);
				String name = item.get("name") ==null ?"":item.get("name").toString();
				String rev = item.get("rev") ==null ?"":item.get("rev").toString();
				String remarks = item.get("remarks") ==null ?"":item.get("remarks").toString();
				String checkOutSts = item.get("checkOutSts") ==null ?"":item.get("checkOutSts").toString();
				String state = item.get("state") ==null ?"":item.get("state").toString();
				String modifier = item.get("modifier") ==null ?"":item.get("modifier").toString();
				String spec = item.get("spec") ==null ?"":item.get("spec").toString();
				String quantity = item.get("quantity") ==null ?"":item.get("quantity").toString();
				String ecoNo = item.get("ecoNo") ==null ?"":item.get("ecoNo").toString();
				String model = item.get("model") ==null ?"":item.get("model").toString();
				String deptcode = item.get("deptcode") ==null ?"":item.get("deptcode").toString();
				String manufacture = item.get("manufacture") ==null ?"":item.get("manufacture").toString();
				String productmethod = item.get("productmethod") ==null ?"":item.get("productmethod").toString();
				
				
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
				Row bomRow = sheet.createRow(rowCnt);
				// 로우 높이 조절
				bomRow.setHeightInPoints(100);
				Cell bomCell00 = bomRow.createCell(0);
				bomCell00.setCellValue(rowCellCnt);
				style2(workbook, bomCell00);
<<<<<<< HEAD
=======
	
				 // 이미지 파일 경로 설정 (예시: "image.jpg")
//		        String thumbnail = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(childPart),
//						ContentRoleType.THUMBNAIL);
//		        if(thumbnail ==null) {
//		        }else {
		        	// 이미지 확인만 하고 추후 썸네일로 교체 예정..
//		        	System.out.println("thumbnail     :     "   + thumbnail);
//		        	imagePath = "/opt/ptc/Windchill_12.0/Windchill/codebase/jsp/images/productview_openin_250.png";
//		        }
				
		        //이미지 확인만 하고 추후 썸네일로 교체 예정..
//				InputStream is = new FileInputStream(
//						"/opt/ptc/Windchill_12.0/Windchill/codebase/jsp/images/productview_openin_250.png");
		        InputStream is = new FileInputStream("D:\\ptc\\Windchill_11.1\\Windchill\\codebase\\extcore\\images\\productview_openin_250.png");
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
				is.close();
				

				CreationHelper helper = workbook.getCreationHelper();
				// Create the drawing patriarch. This is the top level container for all shapes.
				Drawing drawing = sheet.createDrawingPatriarch();

				// add a picture shape
				ClientAnchor anchor = helper.createClientAnchor();
				// set top-left corner of the picture,
				// subsequent call of Picture#resize() will operate relative to it
				anchor.setCol1(1);
				anchor.setRow1(rowCnt);
				anchor.setCol2(2);
				anchor.setRow2(rowCnt + 1);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				// auto-size picture relative to its top-left corner
				pict.resize(1, 1);
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce

				Cell bomCell01 = bomRow.createCell(1);
				bomCell01.setCellValue("");
				style2(workbook, bomCell01);
<<<<<<< HEAD

=======
				
				
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
				Cell bomCell02 = bomRow.createCell(2);
				bomCell02.setCellValue(level);
				style2(workbook, bomCell02);
				
				Cell bomCell03 = bomRow.createCell(3);
				bomCell03.setCellValue(number);
				style2(workbook, bomCell03);

				Cell bomCell04 = bomRow.createCell(4);
				bomCell04.setCellValue(dwgNo);
				style2(workbook, bomCell04);

				Cell bomCell05 = bomRow.createCell(5);
				bomCell05.setCellValue(name);
				style2(workbook, bomCell05);
<<<<<<< HEAD

=======
	
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
				Cell bomCell06 = bomRow.createCell(6);
				bomCell06.setCellValue(rev);
				style2(workbook, bomCell06);

				Cell bomCell07 = bomRow.createCell(7);
				bomCell07.setCellValue(remarks);
				style2(workbook, bomCell07);

				Cell bomCell08 = bomRow.createCell(8);
				bomCell08.setCellValue(checkOutSts);
				style2(workbook, bomCell08);

				Cell bomCell09 = bomRow.createCell(9);
				bomCell09.setCellValue(state);
				style2(workbook, bomCell09);

				Cell bomCell10 = bomRow.createCell(10);
				bomCell10.setCellValue(modifier);
				style2(workbook, bomCell10);

				Cell bomCell11 = bomRow.createCell(11);
				bomCell11.setCellValue(spec);
				style2(workbook, bomCell11);
<<<<<<< HEAD

				Cell bomCell12 = bomRow.createCell(12);
				bomCell12.setCellValue(ecoNo);
=======
				
				Cell bomCell12 = bomRow.createCell(12);
				bomCell12.setCellValue(quantity);
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
				style2(workbook, bomCell12);

				Cell bomCell13 = bomRow.createCell(13);
				bomCell13.setCellValue(ecoNo);
				style2(workbook, bomCell13);

				Cell bomCell14 = bomRow.createCell(14);
				bomCell14.setCellValue(model);
				style2(workbook, bomCell14);

				Cell bomCell15 = bomRow.createCell(15);
				bomCell15.setCellValue(deptcode);
				style2(workbook, bomCell15);

				Cell bomCell16 = bomRow.createCell(16);
				bomCell16.setCellValue(manufacture);
				style2(workbook, bomCell16);
<<<<<<< HEAD

				rowCnt++;
				rowCellCnt++;
				// Map<String, Integer> cntMap = excelTree(childPartlist, masterHis, sheet,
				// workbook, rowCnt, rowCellCnt);
				// if (cntMap.get("rowCnt") != rowCellCnt) {
				// rowCnt = cntMap.get("rowCnt");
				// rowCellCnt = cntMap.get("rowCellCnt");
				// }
=======
				
				Cell bomCell17 = bomRow.createCell(17);
				bomCell17.setCellValue(productmethod);
				style2(workbook, bomCell17);
		       
				
				rowCnt++;
				rowCellCnt++;
				System.out.println(item.get("children"));
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
			}

			// 행 넓이 자동
<<<<<<< HEAD
			// sheet.autoSizeColumn(1);
//			sheet.autoSizeColumn(2);
//			sheet.autoSizeColumn(6);
=======
	//        sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(5);
>>>>>>> aeef8d4adb7d2c201e932fb386c877ad4d724cce
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			LocalDate date = LocalDate.now();
			String now = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			now = now.replaceAll("-", "");
			now = now.substring(0, 8);

			response.setHeader("Content-Disposition",
					"attachment; filename=" + bomList.get(0).get("number") + "_" + now + ".xlsx");

			try {
				System.out.println("성공.");
				workbook.write(response.getOutputStream());
			} catch (Exception e) {
				System.out.println("실패.");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private static void style(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 15); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 가운데 정렬 설정
		style.setAlignment(HorizontalAlignment.CENTER);

		// 스타일 적용
		cell.setCellStyle(style);

	}

	private static void style2(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 테두리 설정
		style.setBorderTop(BorderStyle.THIN); // 상단 테두리
		style.setBorderBottom(BorderStyle.THIN); // 하단 테두리
		style.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
		style.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 가운데 정렬 설정
		style.setAlignment(HorizontalAlignment.CENTER);

		// 높이 가운데 정렬 설정
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		// 스타일 적용
		cell.setCellStyle(style);

	}

	private static void style3(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 테두리 설정
		style.setBorderTop(BorderStyle.THIN); // 상단 테두리
		style.setBorderBottom(BorderStyle.THIN); // 하단 테두리
		style.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
		style.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 스타일 적용
		cell.setCellStyle(style);

	}
}
