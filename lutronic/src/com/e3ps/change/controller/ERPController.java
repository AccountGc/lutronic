package com.e3ps.change.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.erp.service.ERPHelper;
import com.e3ps.erp.service.ERPSearchHelper;


@Controller
@RequestMapping("/erp")
public class ERPController {
	
	/**	PARTERP 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listPARTERP")
	public ModelAndView listPARTERP(HttpServletRequest request, HttpServletResponse response) {
//		System.out.println("======== listPARTERP =============");
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu7");
		model.addObject("module","change");
		model.setViewName("default:/change/listPARTERP");
		return model;
	}
	
	/** PARTERP 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listPARTERPAction")
	public Map<String,Object> listPARTERPAction(HttpServletRequest request, HttpServletResponse response){
//		System.out.println("======== listPARTERPction =============");
		Map<String,Object> result = null;
		try {
			result = ERPSearchHelper.service.listPARTERPAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("======== result =============");
		//System.out.println(result);
		return result;
		
	}
	
	/**	ECOERP 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listECOERP")
	public ModelAndView listECOERP(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu8");
		model.addObject("module","change");
		model.setViewName("default:/change/listECOERP");
		return model;
	}
	
	/** ECOERPP 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listECOERPAction")
	public Map<String,Object> listECOERPAction(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> result = null;
		try {
			result = ERPSearchHelper.service.listECOERPAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	/**	listBOMERP 검색 페이지
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/listBOMERP")
	public ModelAndView listBOMERP(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.addObject("menu", "menu9");
		model.addObject("module","change");
		model.setViewName("default:/change/listBOMERP");
		return model;
	}
	
	/** BOMERP 검색
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listBOMERPAction")
	public Map<String,Object> listBOMERPAction(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> result = null;
		try {
			result = ERPSearchHelper.service.listBOMERPAction(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	/**
	 * 인터페이스 데이터 ERP 적용 유무 체크
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/erpCheckAction")
	public ResultData erpCheckAction(HttpServletRequest request, HttpServletResponse response){
		ResultData data = new ResultData();
		try{
			data = ERPHelper.service.erpCheckAction(request);
		}catch(Exception e){
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * 인터페이스 데이터 ERP 적용 유무 체크
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/erpSendAction")
	public ResultData erpSendAction(HttpServletRequest request, HttpServletResponse response){
		ResultData data = new ResultData();
		try{
			String oid = request.getParameter("oid");
			EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
			boolean isSend = ERPSearchHelper.service.checkSendEO(eco.getEoNumber());
			if(!isSend){
				ERPHelper.service.sendERP(eco);
				data.setResult(true);
				data.setMessage("ERP 전송이 완료 되었습니다.");
			}else{
				data.setResult(false);
				data.setMessage("ERP 전송이 이미 완료 되었습니다.");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			data.setResult(false);
			data.setMessage("ERP 전송시 에러가 발생 하였습니다.");
		}
		return data;
	}
}
