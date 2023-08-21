package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.e3ps.change.service.ECOHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.rohs.service.RohsHelper;
import com.infoengine.util.Base64;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {
	
	/**
	 * 
	 * 		LUTRONIC 추가 시작
	 * 
	 * 
	 */
	
	
	/** NumberCode 데이터 리턴
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/numberCodeList")
	public List<NumberCodeData> numberCodeList(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("codeType") String codeType,
			@RequestParam("parentOid") String parentOid,
			@RequestParam("search") String isSearch) {
		boolean search = ("true").equals(isSearch);
		List<NumberCodeData> list = CodeHelper.service.numberCodeList(codeType, parentOid, search);
		return list;
	}
	
	/** NumberCode 데이터 리턴
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/numberParentCodeList")
	public List<NumberCodeData> numberParentCodeList(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("codeType") String codeType,
			@RequestParam("pCode") String pCode,
			@RequestParam("search") String isSearch) {
		boolean search = ("true").equals(isSearch);
		List<NumberCodeData> list = CodeHelper.service.numberParentCodeList(codeType, pCode, search);
		return list;
	}
	
	/** NumberCode 데이터 리턴
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/autoSearchName")
	public List<NumberCodeData> autoSearchName(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("codeType") String codeType,
			@RequestParam("name") String name) {
		List<NumberCodeData> list = CodeHelper.service.autoSearchName(codeType, name);
		return list;
	}
	
	/** NumberCode 데이터 리턴
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/autoSearchNameRtnName")
	public List<String> autoSearchNameRtnName(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("codeType") String codeType,
			@RequestParam("name") String name) {
		List<String> list = CodeHelper.service.autoSearchNameRtnName(codeType, name);
		return list;
	}
	@Description(value = "팝업 코드 리스트")
	@RequestMapping(value = "/popup_numberCodes")
	public ModelAndView popup_numberCodes(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
//		String codeType = request.getParameter("codeType");
//		String disable= StringUtil.checkReplaceStr(request.getParameter("disable"),"false"); //false 사용,true 미사용
//		//boolean disabled = disable.equals("true")?false:true;
//		NumberCodeType cType = NumberCodeType.toNumberCodeType(codeType);
		model.setViewName("/extcore/jsp/common/popup_numberCodes.jsp");
//		model.addObject("codeType", codeType);
//		model.addObject("title", cType.getDisplay());
//		model.addObject("disable", disable);
		return model;
	}
//	/**
//	 * 팝업 코드 리스트
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/popup_numberCodeList")
//	public ModelAndView popup_numberCodeList(HttpServletRequest request, HttpServletResponse response) {
//		ModelAndView model = new ModelAndView();
//		String codeType = request.getParameter("codeType");
//		String disable= StringUtil.checkReplaceStr(request.getParameter("disable"),"false"); //false 사용,true 미사용
//		//boolean disabled = disable.equals("true")?false:true;
//		NumberCodeType cType = NumberCodeType.toNumberCodeType(codeType);
//		model.setViewName("popup:/common/popup_numberCodeList");
//		model.addObject("codeType", codeType);
//		model.addObject("title", cType.getDisplay());
//		model.addObject("disable", disable);
//		return model;
//	}
	/**
	 * popup NumberCode 리스트
	 * @param request
	 * @param response
	 * @param codeType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/popup_numberCodeAction")
	public Map<String,Object> popup_numberCodeAction(HttpServletRequest request, HttpServletResponse response) {
		
		String disable= StringUtil.checkReplaceStr(request.getParameter("disable"),"false"); //false 사용,true 미사용
		boolean disabled = disable.equals("true")?false:true;
		Map<String, Object> result =new HashMap<String,Object>();
		
		try {
			result = CodeHelper.service.popup_numberCodeAction(request, response, disabled);
		} catch (Exception e) {
		
			e.printStackTrace();
			
		}
		//System.out.println("result ="+result);
		return result; 
	}
	
	@Description(value = "품목 속성 보기")
	@PostMapping(value = "/include_Attributes")
	public ModelAndView include_Attributes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String module = request.getParameter("module");
		String mode = request.getParameter("mode");
		
		ModelAndView model = new ModelAndView();
		Map<String,String> map = CommonHelper.manager.getAttributes(oid, mode);
		model.setViewName("/extcore/jsp/common/attributes_include.jsp");
		model.addAllObjects(map);
		model.addObject("module", module);
		return model;
	}
//	/**	품목 속성 보기
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/include_Attributes")
//	public ModelAndView include_Attributes(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String oid = request.getParameter("oid");
//		String module = request.getParameter("module");
//		String mode = request.getParameter("mode");
//		
//		ModelAndView model = new ModelAndView();
//		Map<String,String> map = CommonHelper.service.getAttributes(oid, mode);
//		model.setViewName("include:/common/include_Attributes");
//		model.addAllObjects(map);
//		model.addObject("module", module);
//		return model;
//	}
	
	@Description(value = "픔목 속성 등록/수정")
	@RequestMapping("/include_createAttributes")
	public ModelAndView include_createAttributes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String module = request.getParameter("module");
		//System.out.println("oid = "+ oid);
		//System.out.println("module = "+ module);
		ModelAndView model = new ModelAndView();
		Map<String,String> map = null;
		if(null!=module){
			map = CommonHelper.service.getAttributes(oid);
		}
		//System.out.println("map = "+ (null!=map));
		model.setViewName("include:/common/include_createAttributes");
		model.addObject("module", module);
		model.addAllObjects(map);
		return model;
	}
//	/**  픔목 속성 등록/수정
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/include_createAttributes")
//	public ModelAndView include_createAttributes(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String oid = request.getParameter("oid");
//		String module = request.getParameter("module");
//		//System.out.println("oid = "+ oid);
//		//System.out.println("module = "+ module);
//		ModelAndView model = new ModelAndView();
//		Map<String,String> map = null;
//		if(null!=module){
//			map = CommonHelper.service.getAttributes(oid);
//		}
//		//System.out.println("map = "+ (null!=map));
//		model.setViewName("include:/common/include_createAttributes");
//		model.addObject("module", module);
//		model.addAllObjects(map);
//		return model;
//	}
	
	@ResponseBody
	@RequestMapping("/documentTypeList")
	public List<Map<String,String>> documentTypeList(HttpServletRequest request, HttpServletResponse response) {
		return CommonHelper.service.documentTypeList(request, response);
	}
	
	@RequestMapping("/include_MyDevelopment")
	public ModelAndView include_MyDevelopment(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.include_MyDevelopment(request, response);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.setViewName("include:/common/include_MyDevelopment");
		return model;
	}
	
	@RequestMapping("/userSearchForm")
	public ModelAndView userSearchForm(HttpServletRequest request, HttpServletResponse response) {
		
		String searchMode = StringUtil.checkReplaceStr(request.getParameter("searchMode"), "single");
		
		String hiddenParam = StringUtil.checkReplaceStr(request.getParameter("hiddenParam"), "creator");
		String hiddenValue = StringUtil.checkNull(request.getParameter("hiddenValue"));
		
		String textParam = StringUtil.checkReplaceStr(request.getParameter("textParam"), "creatorName");
		String textValue = StringUtil.checkNull(request.getParameter("textValue"));
		
		String extraParam = StringUtil.checkNull(request.getParameter("extraParam"));
		String extraValue = StringUtil.checkNull(request.getParameter("extraValue"));
		
		String userType = StringUtil.checkReplaceStr(request.getParameter("userType"), "people");
		String returnFunction = StringUtil.checkNull(request.getParameter("returnFunction"));
		
		ModelAndView model = new ModelAndView();
		model.addObject("searchMode", searchMode);
		
		model.addObject("hiddenParam", hiddenParam);
		model.addObject("hiddenValue", hiddenValue);
		
		model.addObject("textParam", textParam);
		model.addObject("textValue", textValue);
		
		model.addObject("extraParam", extraParam);
		model.addObject("extraValue", extraValue);
		
		model.addObject("userType", userType);
		model.addObject("returnFunction", returnFunction);
		
		model.setViewName("empty:/common/userSearchForm");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/autoSearchUserName")
	public List<Map<String,String>> autoSearchUserName(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.autoSearchUserName(request,response);
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		return list;
	}
	
	/**
	 * 
	 * 		LUTRONIC 추가 끝
	 * 
	 * 
	 */
	
	
	
	/**
	 * @param request
	 * @param response
	 */
	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response){
	}
	
	/**
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/logout")
	public Map<String, Object> logout(HttpServletRequest req, HttpServletResponse res) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		req.getSession().invalidate();
		
		map.put("result", true);
		map.put("redirectUrl", "/Windchill/eSolution/groupware/main");
		
		return map;
	}
	
	/** 로그인 정보 검사
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/loginCheck")
	public ModelAndView loginCheck(HttpServletRequest request, HttpServletResponse response) {
		String viewName = "default:/workprocess/main";
		
		String id=request.getParameter("id");
		String pw=request.getParameter("pw");
		
		String authString = Base64.encode(id+":"+pw);
		if(authString.endsWith("=")){
			authString = authString.substring(0,authString.length()-1);
		}
		
		Cookie authCookie = new Cookie("AuthCookie",authString);
		authCookie.setPath("/Windchill");
		response.addCookie(authCookie);
		HttpSession session = request.getSession();
		session.removeAttribute("Logout");
		
		CommonHelper.service.createLoginHistoty();
		
		ModelAndView model = new ModelAndView();
		model.setViewName(viewName);
		return model;
	}

	/** 관리자 true/false
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/isAdmin")
	public boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return CommonUtil.isAdmin();
	}
	
	@Description(value = "다운로드 이력 상세보기")
	@GetMapping(value = "/downloadHistory")
	public ModelAndView downloadHistory(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid") String oid) {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("/extcore/jsp/common/downloadHistory.jsp");
		return model;
	}
	
	@Description(value = "다운로드 이력 상세보기")
	@ResponseBody
	@PostMapping(value = "/downloadHistory")
	public Map<String, Object> downloadHistory(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = CommonHelper.service.downloadHistory(params);
			result.put("result", SUCCESS);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		
		return result;
	}
	
//	/** 다운로드 이력 상세보기
//	 * @param request
//	 * @param response
//	 * @param oid
//	 * @return
//	 */
//	@RequestMapping("/downloadHistory")
//	public ModelAndView downloadHistory(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid") String oid) {
//		
//		List<Map<String,Object>> list = null;
//		
//		try {
//			list = CommonHelper.service.downloadHistory(oid);
//		}catch(Exception e ) {
//			e.printStackTrace();
//			list = new ArrayList<Map<String,Object>>();
//		}
//		
//		ModelAndView model = new ModelAndView();
//		model.setViewName("popup:/common/downloadHistory");
//		model.addObject("list", list);
//		
//		return model;
//	}

	
	/** parent 코드값과 code type 으로 하위 NumberCode 데이터 리턴
	 * @param request
	 * @param response
	 * @param type
	 * @param parentCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/childNumberCodeList")
	public List<NumberCodeData> childNumberCodeList(HttpServletRequest request, HttpServletResponse response, @RequestParam("type") String type, @RequestParam("parentCode") String parentCode)	 {
		List<NumberCodeData> list = CodeHelper.service.childNumberCodeList(type,parentCode);
		return list;
	}
	
	@Description(value = "버전이력 페이지 이동")
	@GetMapping(value = "/versionHistory")
	public ModelAndView versionHistory(HttpServletRequest request, @RequestParam("oid")String oid) {
		ModelAndView model = new ModelAndView();
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		model.addObject("distribute", distribute);
		model.setViewName("/extcore/jsp/common/versionHistory.jsp");
		return model;
	}
	
	@Description(value = "버전이력 상세보기")
	@ResponseBody
	@PostMapping(value = "/versionHistory")
	public Map<String, Object> versionHistory(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");
		try {
			result = CommonHelper.service.versionHistory(oid);	
			result.put("result", SUCCESS);
		}catch(Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		
		return result;
	}
	
//	/** 버전이력 상세보기
//	 * @param request
//	 * @param response
//	 * @param oid
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("/versionHistory")
//	public ModelAndView versionHistory(HttpServletRequest request, HttpServletResponse response,@RequestParam("oid")String oid) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
//		//System.out.println("versionHistory distribute =" + distribute);
//		List<Map<String,Object>> list = CommonHelper.service.versionHistory(oid);
//		
//		model.addObject("list", list);
//		model.addObject("distribute", distribute);
//		
//		model.setViewName("popup:/common/versionHistory");
//		
//		return model;
//	}
	/** 객체 삭체 시 재확인 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteCheck")
	public ModelAndView deleteCheck(HttpServletRequest request, HttpServletResponse response,@RequestParam("oid")String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/common/deleteCheck");
		
		return model;
	}
	
	/** view 리스트 가져오기
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getViews")
	public List<String> getViews(HttpServletRequest request, HttpServletResponse response) throws Exception {
		View[] views = ViewHelper.service.getAllViews();
		List<String> list = new ArrayList<String>();
		
		for(int i=0; i< views.length ; i++){
			list.add(views[i].getName());
		}
		return list;
	}
	
	/** 메인 화면 공지사항
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_Notice")
	public ModelAndView include_Notice(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.include_Notice();
		}catch(Exception e) {
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView();
		
			
		model.setViewName("include:/common/include_Notice");
		
		
		model.addObject("list", list);
		
		return model;
	}
	
	/** 메인 화면 결재&작업
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_Approve")
	public ModelAndView include_Approve(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.include_Approve();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/include_Approve");
		model.addObject("list",list);
		return model;
	}
	
	/** 메인 화면 신규도면
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_Drawing")
	public ModelAndView include_Drawing(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.include_Drawing(request);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/include_Drawing");
		model.addObject("list",list);
		return model;
	}
	
	/** 메인 화면 신규문서
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_Document")
	public ModelAndView include_Document(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonHelper.service.include_Document();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/include_Document");
		model.addObject("list",list);
		return model;
	}
	
	@RequestMapping("/include_adminAttribute")
	public ModelAndView include_adminAttribute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String oid = request.getParameter("oid");
		Object object = CommonUtil.getObject(oid);
		
		String module = request.getParameter("module");
		
		HashMap has = null;
		if("part".equals(module)) {
			has = IBAUtil.getAttributes((WTPart)object);
		}else if("drawing".equals(module)) {
			has = IBAUtil.getAttributes((EPMDocument)object);
		}else if("doc".equals(module)) {
			has = IBAUtil.getAttributes((WTDocument)object);
		}
		
		String temp = has.toString();
		StringTokenizer tokens = new StringTokenizer(temp,", ");
		
		List<String> list = new ArrayList<String>();
		
        while(tokens.hasMoreTokens()){
            String badT = (String)tokens.nextToken();
            list.add(badT);
        }
        
        ModelAndView model = new ModelAndView();
        model.addObject("list", list);
        model.addObject("module", module);
        model.setViewName("include:/common/include_adminAttribute");
        return model;
        
	}
	
	@ResponseBody
	@RequestMapping("/getVROid")
	public String getVROid(HttpServletRequest request, HttpServletResponse response) {
		String oid = request.getParameter("oid");
		String vrOid = CommonUtil.getVROID(oid);
		return vrOid;
	}
	
	@ResponseBody
	@RequestMapping("/getPDMLinkProductOid")
	public String getPDMLinkProductOid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PDMLinkProduct product =  WCUtil.getPDMLinkProduct();
		return product.getPersistInfo().getObjectIdentifier().toString();
	}
	
	@RequestMapping("/withDrawPopup")
	public ModelAndView withDrawPoup(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid") String oid) {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/common/withDrawPopup");
		return model;
	}
	
	/**
	 * 회수 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/withDrawAction")
	public ResultData withDrawAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String withDrawType = request.getParameter("withDrawType"); //init,keep
		
		boolean isInit = withDrawType.equals("init");
		//System.out.println("1 withDrawType :" + withDrawType +" ,isInit :" + isInit);
		return CommonHelper.service.withDrawAction(oid,isInit);
		//return DocumentHelper.service.deleteDocumentAction(request,response);
		
	}
	
	/**
	 * 일괄 다운로드 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchEODownLoad")
	public ModelAndView batchEODownLoad(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("============== batchEODownLoad =================");
		ModelAndView model = new ModelAndView();
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String downType =StringUtil.checkNull(request.getParameter("downType"));
		EChangeOrder eo = (EChangeOrder)CommonUtil.getObject(oid);
		model.setViewName("popup:/common/batchEODownLoad");
		
		model.addObject("oid", oid);
		model.addObject("downType", downType);
		String title = eo.getEoNumber()+" 도면 다운로드";
		if(downType.equals("attach")){
			title = eo.getEoNumber()+"산출물 다운로드";
		}
		
		model.addObject("title",title );
	
	
		return model;
	}
	
	
	
	/**  일괄 다운로드 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="batchEODownAction", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData batchEODownAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		try {
			//System.out.println("11111111111111111 batchEoDownAction 11111111111111111111");
			String oid = request.getParameter("oid");
			String describe = request.getParameter("describe");
			String describeType = request.getParameter("describeType");
			String downType =request.getParameter("downType");
			Map<String, String> map = CommonUtil.getBatchDescribe();
			describeType = StringUtil.checkNull(map.get(describeType));
			
			describe = "["+describeType+"]"+describe;
			if(downType.equals("attach")){
				data = ECOHelper.service.batchEOAttachDownAction(oid, describe);
			}else{
				data = ECOHelper.service.batchEODrawingDownAction(oid, describe);
			}
			
			
			//ExcelDownHelper.service.loginHistoryxcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	/**
	 * BOM 도면 일괄 다운로드 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchBOMDrawingDown")
	public ModelAndView batchBOMDrawingDown(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("============== batchBOMDrawingDown =================");
		ModelAndView model = new ModelAndView();
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String ecoOid =StringUtil.checkNull(request.getParameter("ecoOid"));
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		model.setViewName("popup:/common/batchBOMDrawingDownLoad");
		
		model.addObject("oid", oid);
		model.addObject("ecoOid", ecoOid);
		String title = part.getNumber()+" 도면 다운로드";
		
		model.addObject("title",title );
	
	
		return model;
	}
	
	
	/**  BOM 도면 일괄 다운로드
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="batchBOMDrawingDownAction", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData batchBOMDrawingDownAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		try {
			//System.out.println("11111111111111111 batchBOMDrawingDownAction 11111111111111111111");
			String oid = request.getParameter("oid");
			String describe = request.getParameter("describe");
			String describeType = request.getParameter("describeType");
			Map<String, String> map = CommonUtil.getBatchDescribe();
			describeType = StringUtil.checkNull(map.get(describeType));
			describe = "["+describeType+"]"+describe;
			String ecoOid =StringUtil.checkNull(request.getParameter("ecoOid"));
			//boolean  isDistribute = StringUtil.checkNull(request.getParameter("dis")).equals("true") ? true : false;
			data = PartHelper.service.batchBomDrawingDownAction(oid, describe, ecoOid);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	/**
	 * BOM 도면 일괄 다운로드 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/batchBOMSelectDownLoad")
	public ModelAndView batchBOMSelectDownLoad(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("============== batchBOMSelectDownLoad =================");
		ModelAndView model = new ModelAndView();
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		String downType =StringUtil.checkNull(request.getParameter("downType"));
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		model.setViewName("popup:/common/batchBOMSelectDownLoad");
		
		model.addObject("oid", oid);
		model.addObject("downType", downType);
		String title = part.getNumber()+" 도면 다운로드";
		if(downType.equals("attach")){
			title = part.getNumber()+"  산출물 다운로드";
		}
		
		model.addObject("title",title );
	
	
		return model;
	}
	
	/**  BOM 도면 일괄 다운로드
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	@ResponseBody
	@RequestMapping(value="batchBOMSelectDownLoadAction")
	public ResultData batchBOMSelectDownLoadAction(@RequestBody Map<String, Object> reqMap, HttpServletResponse res) {
		ResultData data = new ResultData();
		try {
			List<Map<String,Object>> itemList = (List<Map<String, Object>>) reqMap.get("itemList");
			String oid = (String)reqMap.get("oid");
			String describe = (String)reqMap.get("describe");
			String downType =(String)reqMap.get("downType");
			String describeType = (String)reqMap.get("downType");
			//boolean  isDistribute = StringUtil.checkNull(request.getParameter("dis")).equals("true") ? true : false;
			data = PartHelper.service.batchBomSelectDownAction(oid, itemList, describe, downType,describeType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping("/batchDownDescribe")
	public List<Map<String,String>> batchDownDescribe(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = null;
		
		try {
			list = CommonUtil.getBatchDescribeList();
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		
		return list;
	}
	
	/** 부첨부 파일 일괄 다운로드
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="batchSecondaryDown", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData batchSecondaryDown(HttpServletRequest request, HttpServletResponse response) {
		ResultData returnData = new ResultData();
		try {
			returnData =  CommonHelper.service.batchSecondaryDown(request, response);//.service.batchSecondaryDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return returnData;
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
		//System.out.println(" partNumber = "+request.getParameter("partNumber"));
		try {
			
			returnData = RohsHelper.service.batchROHSDown(request, response);
			//CommonHelper.service.batchSecondaryDown(request, response);//.service.batchSecondaryDown(request, response);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
				
		return returnData;
	}
}
