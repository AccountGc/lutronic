package com.e3ps.common.excelDown.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3ps.change.service.ECOHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.excelDown.service.ExcelDownHelper;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.PartHelper;

@Controller
@RequestMapping("/excelDown")
public class ExcelDownController {
	
	/**	문서 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="documentExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void documentExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.documentExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 품목 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="partExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void partExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.partExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="downLoadHistoryExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void downLoadHistoryExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.admin_downLoadHistoryExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 도면 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="drawingExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void drawingExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.drawingExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**	RoHS 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="rohsExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void rohsExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.rohsExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**	금형 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="moldExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void moldExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.moldExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**	개발업무관리 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="developmentExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void developmentExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.developmentExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**	나의 개발업무관리 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="myDevelopmentExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void myDevelopmentExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.myDevelopmentExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** ECR 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="ECRExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void ECRExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.ECRExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** ECO 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="ECOExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void ECOExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.ECOExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** EO 리스트 엑셀 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="EOExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void EOExcelDown(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.EOExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**  BOM Excel 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="partTreeExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void partTreeExcelDown(HttpServletRequest request, HttpServletResponse response) {
		try {
			ExcelDownHelper.service.partTreeExcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**  BOM Excel 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="loginHistoryxcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public void loginHistoryxcelDown(HttpServletRequest request, HttpServletResponse response) {
		try {
			ExcelDownHelper.service.loginHistoryxcelDown(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	@ResponseBody
	@RequestMapping(value="compareBomExcelDown", method={RequestMethod.GET, RequestMethod.POST})
	public ResultData compareBomExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return PartHelper.service.getBaseLineCompareExcelDown(request, response);
	}
	
	/** 유저별 그루 리스트 Excel 다운
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="usetToGroupExcel", method={RequestMethod.GET, RequestMethod.POST})
	public void usetToGroupExcel(HttpServletRequest request, HttpServletResponse response){
		try {
			ExcelDownHelper.service.usetToGroupExcel(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
