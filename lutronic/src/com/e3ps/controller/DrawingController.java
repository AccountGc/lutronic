package com.e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.clients.folder.FolderTaskLogic;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.folder.Folder;
import wt.util.WTException;

import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ControllerUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.service.PartHelper;

@Controller
@RequestMapping(value = "/drawing")
public class DrawingController {
	
	@Description(value = "도면 검색 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/drawing/drawing-list.jsp");
		return model;
	}
	
	@Description(value = "도면 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/drawing/drawing-create.jsp");
		return model;
	}
	
	@Description(value = "일괄 등록 페이지")
	@GetMapping(value = "/batch")
	public ModelAndView batch() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/drawing/drawing-batch.jsp");
		return model;
	}
	
	/** 도면 검색 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listDrawingAction")
	public Map<String,Object> listDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> result = null;
		try {
			result = DrawingHelper.service.listDrawingAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/** 도면 등록
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createDrawingAction")
	public ResultData createDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = DrawingHelper.service.requestDrawingMapping(request, response);
		return DrawingHelper.service.createDrawing(map);
	}
	
	/** CAD 구분 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cadDivisionList")
	public List<Map<String,String>> cadDivisionList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = DrawingHelper.service.cadDivisionList();
		return list;
	}
	
	/** CAD 타입 리스트 리턴
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cadTypeList")
	public List<Map<String,String>> cadTypeList(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String,String>> list = DrawingHelper.service.cadTypeList();
		return list;
	}
	
	/** 도면 상세보기
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewDrawing")
	public ModelAndView viewDrawing(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		
		EPMDocument doc = (EPMDocument)CommonUtil.getObject(oid);
		EpmData epmData = new EpmData(doc);
		
		model.setViewName("popup:/drawing/viewDrawing");
		model.addObject("isAdmin", CommonUtil.isAdmin());
		model.addObject("epmData", epmData);
		return model;
	}
	
	
	@RequestMapping("/include_Reference")
	public ModelAndView include_Reference(HttpServletRequest request, HttpServletResponse response) {
		String title = StringUtil.checkReplaceStr(request.getParameter("title"),"참조");
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_Reference(oid,moduleType);
			// HashSet 데이터 형태로 생성되면서 중복 제거됨
			HashSet<EpmData> hs = new HashSet<EpmData>(list);

			// ArrayList 형태로 다시 생성
			list = new ArrayList<EpmData>(hs);
		} catch(Exception e) {
			list = new ArrayList<EpmData>();
			e.printStackTrace();
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/drawing/include_Reference");
		model.addObject("title",title);
		model.addObject("moduleType", moduleType);
		model.addObject("list", list);
		model.addObject("distribute",distribute);
		return model;
	}
	
	/** 참조 항목
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_ReferenceBy")
	public ModelAndView include_ReferenceBy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String title = StringUtil.checkReplaceStr(request.getParameter("title"),"참조 항목");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/drawing/include_ReferenceBy");
		
		List<EpmData> list = new ArrayList<EpmData>();
		String oid = request.getParameter("oid");
		if(StringUtil.checkString(oid)) {
			EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
			List<EPMReferenceLink> refList = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
			for(EPMReferenceLink link : refList) {
				EPMDocument epmdoc = link.getReferencedBy();
				EpmData data = new EpmData(epmdoc);
				
				data.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));
				
				list.add(data);
			}
		}
		model.addObject("title",title);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		return model;
	}
	
	/** 도면 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteDrwaingAction")
	public Map<String,Object> deleteDrwaingAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) {
		Map<String,Object> map = DrawingHelper.service.delete(oid);
		return map;
	}
	
	/** 도면 선택 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/selectDrawingPopup")
	public ModelAndView selectDrawingPopup(HttpServletRequest request, HttpServletResponse response) {
		String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil");
		ModelAndView model = new ModelAndView();
		model.addObject("mode", mode);
		model.setViewName("popup:/drawing/selectDrawingPopup");
		return model;
	}
	
	/** 도면 수정 페이지
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateDrawing")
	public ModelAndView updateDrawing(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid ) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument doc = (EPMDocument)CommonUtil.getObject(oid);
		EpmData epmData = new EpmData(doc);
		
		model.setViewName("popup:/drawing/updateDrawing");
		model.addObject("epmData", epmData);
		return model;
	}
	
	/** 도면 수정
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/updateDrawingAction")
	public ModelAndView updateDrawingAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid ) {
		FileRequest req = null;
		
		String viewName = "";
		String msg = "";
		
		try {
			req = new FileRequest(request);
			oid 				 = StringUtil.checkNull(req.getParameter("oid"));
			String fid 			 = StringUtil.checkNull(req.getParameter("fid")); //folder oid
			String location 	 = StringUtil.checkNull(req.getParameter("location")); //도면 분류
			String pdmNumber 	 = StringUtil.checkNull(req.getParameter("pdmNumber")); //도면 번호
			String pdmName 		 = StringUtil.checkNull(req.getParameter("pdmName")); //도면 이름
			String primary 		 = StringUtil.checkNull(req.getParameter("PRIMARY")); //주 첨부파일
			String description 	 = StringUtil.checkNull(req.getParameter("description")); //설명
			String lifecycle 	 = StringUtil.checkNull(req.getParameter("lifecycle"));
			String iterationNote = StringUtil.checkNull(req.getParameter("iterationNote"));
			String isWG 		 = StringUtil.checkNull(req.getParameter("isWG"));
			
			System.out.println("primary :: "+primary);
			
			Hashtable<String,String> hash = new Hashtable<String,String>();
			hash.put("oid" , oid);
			hash.put("fid" , fid);
			hash.put("location", location);
			hash.put("number", pdmNumber);
			hash.put("name", pdmName);
			hash.put("primary" , primary);
			hash.put("description" , description);
			hash.put("iterationNote", iterationNote);
			hash.put("isWG", isWG);
			
			String[] loc = req.getParameterValues("SECONDARY");
			String[] deloc = req.getParameterValues("delocIds");
			String[] partOid = req.getParameterValues("partOid");
			
			Hashtable rtnVal = DrawingHelper.service.modify(hash , loc , deloc, partOid);
			
			msg = (String)rtnVal.get("msg");
	        String newOid = (String)rtnVal.get("oid");
	        
	        if("S".equals(StringUtil.checkNull((String)rtnVal.get("rslt")))) {
	        	viewName = "/Windchill/" + CommonUtil.getOrgName() + "/drawing/viewDrawing.do?oid="+newOid;
	        } else {
	        	viewName = "/Windchill/" + CommonUtil.getOrgName() + "/drawing/viewDrawing.do?oid="+oid;
	        }
	        
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(req!=null) req.removeTempFiles();
		}
        return ControllerUtil.redirect(viewName, msg);
	}
	
	/** 관련 도면 추가
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DrawingSelect")
	public ModelAndView include_DrawingSelect(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String epmType = StringUtil.checkReplaceStr(request.getParameter("epmType"),"");
		
		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_DrawingList(oid,moduleType, epmType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.setViewName("include:/drawing/include_DrawingSelect");
		return model;
	}
	
	/** 관련 도면 보기
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/include_DrawingView")
	public ModelAndView include_DrawingView(HttpServletRequest request, HttpServletResponse response) {
		String moduleType = request.getParameter("moduleType");
		String oid = request.getParameter("oid");
		String title = request.getParameter("title");
		String paramName = request.getParameter("paramName");
		String epmType = StringUtil.checkReplaceStr(request.getParameter("epmType"),"");
		String distribute = StringUtil.checkNull(request.getParameter("distribute"));
		//System.out.println("include_DrawingView distribute =" + distribute);
		List<EpmData> list = null;
		try {
			list = DrawingHelper.service.include_DrawingList(oid,moduleType,epmType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("moduleType", moduleType);
		model.addObject("epmType", epmType);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("paramName", paramName);
		model.addObject("list", list);
		model.addObject("distribute", distribute);
		model.setViewName("include:/drawing/include_DrawingView");
		return model;
	}
	
	/** 도면 미리보기
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/thumbview")
	public ModelAndView thumbview(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		
		Map<String, Object> thumbInfoMap;
		try {
			thumbInfoMap = DrawingHelper.service.thumbView(request);
			model.addAllObjects(thumbInfoMap);
		} catch (WTException e) {
			e.printStackTrace();
		}
		model.setViewName("include:/drawing/thumbview");
		return model;
	}
	
	@RequestMapping("/include_drawingLink")
	public ModelAndView include_drawingLink(HttpServletRequest request, HttpServletResponse response) {
		String module = request.getParameter("module");
		String oid = request.getParameter("oid");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), Message.get("관련 도면"));
		String enabled = StringUtil.checkReplaceStr(request.getParameter("enabled"), "false");
		
		List<EpmData> list = DrawingHelper.service.include_drawingLink(module, oid);
		
		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/drawing/include_drawingLink");
		model.addObject("module", module);
		model.addObject("oid", oid);
		model.addObject("title", title);
		model.addObject("list", list);
		model.addObject("enabled", Boolean.valueOf(enabled));
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/linkDrawingAction")
	public ResultData linkDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		return DrawingHelper.service.linkDrawingAction(request, response);
	}
	
	@ResponseBody
	@RequestMapping("/deleteDrwaingLinkAction")
	public ResultData deleteDrwaingLinkAction(HttpServletRequest request, HttpServletResponse response) {
		return DrawingHelper.service.deleteDrawingLinkAction(request, response);
	}
	
	
	
	/** 도면 삭제
	 * @param request
	 * @param response
	 * @param oid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateNameAction")
	public ResultData updateNameAction(HttpServletRequest request, HttpServletResponse response, @RequestParam("oid")String oid) {
	
		return DrawingHelper.service.updateNameAction(request, response);
	}
	
	@RequestMapping("/createPackageDrawing")
	public ModelAndView createPackageDrawing(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module","drawing");
		model.setViewName("default:/drawing/createPackageDrawing");
		return model;
	}
	
	@RequestMapping("/createPackageDrawingAction")
	public ModelAndView createPackageDrawingAction(HttpServletRequest request, HttpServletResponse response) {
		String xmlString = "";

		xmlString = DrawingHelper.service.createPackageDrawingAction(request, response);
		
		ModelAndView model = new ModelAndView();
		model.addObject("xmlString", xmlString);
		model.setViewName("empty:/drawing/createPackageDrawingAction");
		return model;
	}
	/**  BOM Drawing 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="partTreeDrawingDown")
	public ModelAndView partTreeDrawingDown(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		
		try {
			DrawingHelper.service.partTreeDrawingDown(request, response);
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
				//model.addObject("xmlString", xmlString);
				model.setViewName("empty:/drawing/createPackageDrawingAction");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/cadRePublish")
	public ResultData cadRePublish(HttpServletRequest request, HttpServletResponse response) {
		
		String oid = StringUtil.checkNull(request.getParameter("oid"));
		
		return EpmSearchHelper.service.cadRePublish(oid);
		
	}
	
	/**  부품 상태 수정
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/showThumAction")
	public Map<String,Object> showThumAction(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> map = new HashMap<String, Object>();
		
		try{
			String oid = request.getParameter("oid");
			EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
			EpmData data = new EpmData(epm);
			
			if(data.getThum() != null) {
				String num = data.number.replaceAll(" ", "_");
				String imgpath = data.getThum();
				String copyTag = data.getCopyTag();
				map.put("num", num);
				map.put("imgpath", imgpath);
				map.put("copyTag", copyTag);
				map.put("result", true);
			}else{
				map.put("result", false);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			map.put("result", false);
		}
		
		
		return map;
	}
}
