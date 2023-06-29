package com.e3ps.change.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.fc.ReferenceFactory;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.beans.EADData;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

@Controller
@RequestMapping("/change")
public class ChangeController {
	
	/**	ECR 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listECR")
	public ModelAndView listECR(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu1");
		model.addObject("module","change");
		model.setViewName("default:/change/listECR");
		return model;
	}
	
	/**	ECR 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createECR")
	public ModelAndView createECR(HttpServletRequest request, HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu2");
		model.addObject("module", "change");
		model.setViewName("default:/change/createECR");
		return model;
	}
	
	/**	ECO 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listECO")
	public ModelAndView listECO(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu3");
		model.addObject("module", "change");
		model.setViewName("default:/change/listECO");
		return model;
	}
	
	/**	ECO 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 
	@RequestMapping("/createECO")
	public ModelAndView createECO(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu4");
		model.addObject("module", "change");
		model.setViewName("default:/change/createECO");
		return model;
	}
	*/
	/**	ECN 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listECN")
	public ModelAndView listECN(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu5");
		model.addObject("module", "change");
		model.setViewName("default:/change/listECN");
		return model;
	}
	
	
	/**	신제품 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listNewProduct")
	public ModelAndView listNesProduct(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu6");
		model.addObject("module", "change");
		model.setViewName("default:/change/listNewProduct");
		return model;
	}
	
	/**	신제품 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createNewProduct")
	public ModelAndView createNewProduct(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu7");
		model.addObject("module", "change");
		model.setViewName("default:/change/createNewProduct");
		return model;
	}
	
	/**	Minor 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listMinor")
	public ModelAndView listMinor(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu8");
		model.addObject("module", "change");
		model.setViewName("default:/change/listMinor");
		return model;
	}
	
	/**	Minor 등록 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/createMinor")
	public ModelAndView createMinor(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu9");
		model.addObject("module", "change");
		model.setViewName("default:/change/createMinor");
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/getEOActivityList")
	public List<EADData> getEOActivityList(HttpServletRequest request, HttpServletResponse response) {
		String eoType = null;
		String ecaOid = request.getParameter("ecaOid");
		
		List<EADData> list = null;
		if(StringUtil.checkString(ecaOid)) {
			EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);
			ECOChange eo = eca.getEo();
			eoType = eo.getEoType();
			list = ChangeHelper.service.getEOActivityList(eoType,ecaOid);
		}else {
			eoType = request.getParameter("eoType");
			list = ChangeHelper.service.getEOActivityList(eoType,ecaOid);
		}
	
		return list;
	}
	
	
	
	@RequestMapping("/listEulB_include")
	public ModelAndView listEulB_include(HttpServletRequest request, HttpServletResponse response) {
		
		String partOid = request.getParameter("partOid");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"),"");
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/change/listEulB_include");
		model.addObject("partOid", partOid);
		model.addObject("allBaseline", allBaseline);
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/listEulB_IncludeAction")
	public List<Map<String,String>> listEulB_IncludeAction(HttpServletRequest request, HttpServletResponse response) {
		String partOid = request.getParameter("partOid");
		String allBaseline = StringUtil.checkReplaceStr(request.getParameter("allBaseline"),"false");
		String baseline = request.getParameter("baseline");
		
		List<Map<String,String>> list = null;
		try {
			list = ChangeHelper.service.listEulB_IncludeAction(partOid, allBaseline, baseline);
		} catch(Exception e) {
			list = new ArrayList<Map<String,String>>();
			e.printStackTrace();
		}
		return list;
	}
	
	@ResponseBody
	@RequestMapping("/addECRPartCheck")
	public List<String[]> addECRPartCheck(HttpServletRequest request, HttpServletResponse response) {
		String ecrOid = request.getParameter("ecrOid");
		List<String[]> list = null;
		try {
			list = ChangeHelper.service.addECRPartCheck(ecrOid);
		}catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<String[]>();
		}
		return list;
	}
	
	@ResponseBody
	@RequestMapping("/getEOActivityUpdateList")
	public List<EADData> getEOActivityUpdateList(HttpServletRequest request, HttpServletResponse response) {
		String eoOid = request.getParameter("eoOid");
		String eoType = request.getParameter("eoType");
		List<EADData> list = null;
		if(StringUtil.checkString(eoOid)) {
			ECOChange eo = (ECOChange)CommonUtil.getObject(eoOid);
			list = ChangeHelper.service.getEOActivityUpdateList(eo, eoType);
		}
		return list;
	}
	
}
