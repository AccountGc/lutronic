package com.e3ps.common.excelDown.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.write.WritableWorkbook;

import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ExcelDownService {

	void documentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void partExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void drawingExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void ECRExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void ECOExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void EOExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void partTreeExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void rohsExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void moldExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void developmentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void myDevelopmentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception;

	void loginHistoryxcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	File batchDownloadexcelDown(String fileName,
			List<BatchDownData> targetlist) throws Exception;

	void usetToGroupExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	void admin_downLoadHistoryExcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	

}
