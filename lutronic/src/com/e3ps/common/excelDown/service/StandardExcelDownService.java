package com.e3ps.common.excelDown.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.beans.EOData;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.change.service.ECRSearchHelper;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.excelDown.beans.ExcelDownData;
import com.e3ps.common.history.LoginHistory;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.dto.DevActiveData;
import com.e3ps.development.dto.MasterData;
import com.e3ps.development.service.DevelopmentQueryHelper;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.service.DocumentQueryHelper;
import com.e3ps.download.DownloadHistory;
import com.e3ps.download.beans.DownloadDTO;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.org.People;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.service.PartExcelHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartQueryHelper;
import com.e3ps.part.service.PartService;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.rohs.service.RohsQueryHelper;

@SuppressWarnings("serial")
public class StandardExcelDownService extends StandardManager implements ExcelDownService {

	private static final String List = null;

	public static StandardExcelDownService newStandardExcelDownService() throws Exception {
		final StandardExcelDownService instance = new StandardExcelDownService();
		instance.initialize();
		return instance;
	}
	
	private WritableWorkbook setExcelFileName(HttpServletResponse response, String name) throws Exception {
		WritableWorkbook workbook = null;
		String fileName = new String(name.getBytes("euc-kr"), "8859_1");
	
	    response.setContentType("application/vnd.ms-excel");
	    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	
	    OutputStream out = response.getOutputStream();
	
	    workbook = Workbook.createWorkbook(out);
	    
	    return workbook;
	}
	
	private void createExcelSheet(WritableWorkbook workbook, List<String[]> list, String[] title, int[] cellSize) throws Exception {
		
		int row = 0;
		
		WritableSheet sheet = null;
        WritableCellFormat titleformat = JExcelUtil.getCellFormat(Alignment.CENTRE, Colour.LIGHT_GREEN);
        WritableCellFormat cellformat = new WritableCellFormat();
        cellformat.setAlignment(Alignment.CENTRE);
		
		
		
		//for(String[] string : list) {
		int k=0;
		boolean isCreateSheet = false;
		for(int i = 0;i < list.size();i++)
		{
			 if(i % 60000 == 0)
			    {
			        workbook.createSheet("목록" + (k+1)+" Page",k);
			        sheet = workbook.getSheet(k);
			        isCreateSheet = true;
			        k++;
			    }
			//row++;
			 if(isCreateSheet){
				 for (int j = 0; j < title.length; j++) {
					sheet.setColumnView(j, cellSize[j]);
					sheet.addCell(new Label(j, 0, title[j], titleformat));
				}
				 isCreateSheet = false;
			 }
            int columnIndex = 0;
            row = (i % 60000)+1;
			String[] data = list.get(i);
            for (int j = 0; j < title.length; j++) {
            	sheet.addCell(new Label(columnIndex++, row, data[j])); 
            }
		}
		
	}
	
	
	@Override
	public void documentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentDTO data = new DocumentDTO(doc);
			
			String[] string = new String[ExcelDownData.documentTitle.length];
			
			string[0] = data.number;															// 문서 번호
			string[1] = data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER);				// 내부문서번호
			string[2] = data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL);						// 프로젝트코드
			string[3] = data.name;																// 문서명
			string[4] = data.location.substring(("/Default/Document").length());				// 문서 분류
			string[5] = data.version + "." + data.iteration;									// 문서 버전
			string[6] = data.getLifecycle();													// 문서 상태
			string[7] = data.getIBAValue(AttributeKey.IBAKey.IBA_DSGN);							// 작성자
			string[8] = data.creator;															// 문서 등록자
			string[9] = data.createDate;														// 문서 등록일
			string[10] = data.modifyDate;														// 문서 최종 수정일
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.docFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.documentTitle, ExcelDownData.documentSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void partExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec query = PartQueryHelper.service.listPartSearchQuery(request, response);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
		    WTPart part = (WTPart) o[0];
		    PartData data = new PartData(part);
		    
		    String[] string = new String[ExcelDownData.partTitle.length];
		    
		    string[0] = data.number;
		    string[1] = data.name;
		    string[2] = data.getLocation();
		    string[3] = data.version +"."+data.iteration+"("+part.getViewName()+")";
		    string[4] = data.getLifecycle();
		    string[5] = StringUtil.checkReplaceStr(data.creator, "&nbsp;");
		    string[6] = data.createDate;
		    string[7] = data.modifyDate;
		    
		    list.add(string);
		    
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.partFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.partTitle, ExcelDownData.partSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void drawingExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DrawingHelper.service.getListQuery(request);
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) o[0];
			String[] string = new String[ExcelDownData.drawingTitle.length];
			
			string[0] = epm.getAuthoringApplication().getDisplay(Message.getLocale());
		    string[1] = epm.getNumber();
		    string[2] = epm.getName();
		    string[3] = StringUtil.checkNull(epm.getLocation()).replaceAll("/Default","");
		    string[4] = VersionControlHelper.getVersionIdentifier(epm).getSeries().getValue()+"."+epm.getIterationIdentifier().getSeries().getValue();
		    string[5] = epm.getLifeCycleState().getDisplay(Message.getLocale());
		    string[6] = VersionControlHelper.getVersionCreator(epm).getFullName();
		    string[7] = DateUtil.getDateString(epm.getCreateTimestamp(),"d");
		    string[8] = DateUtil.getDateString(epm.getModifyTimestamp(),"d");
		    
		    list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.drawingFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.drawingTitle, ExcelDownData.drawingSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void rohsExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = RohsQueryHelper.service.getListQuery(request, response);
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) o[0];
			RohsData data = new RohsData(rohs);
			
			String[] string = new String[ExcelDownData.rohsTitle.length];
			
			string[0] = data.number;															// 물질번호
			string[1] = data.getManufactureDisplay(true);										// 협력업체
			string[2] = data.name;																// 물질명
			string[3] = data.version + "." + data.iteration;									// Rev.
			string[4] = data.getLifecycle();													// 상태
			string[5] = data.creator;															// 등록자
			string[6] = data.createDate;														// 등록일
			string[7] = data.modifyDate;														// 수정일
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.rohsFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.rohsTitle, ExcelDownData.rohsSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void moldExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentDTO data = new DocumentDTO(doc);
			
			String[] string = new String[ExcelDownData.moldTitle.length];
			
			string[0] = data.number;															// 문서 번호
			string[1] = data.name;																// 문서명
			string[2] = data.version + "." + data.iteration;									// 문서 버전
			string[3] = data.getLifecycle();																// 문서 상태
			string[4] = data.creator;													// 문서 등록자
			string[5] = data.createDate;													// 문서 등록일
			string[6] = data.modifyDate;													// 문서 최종 수정일
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.moldFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.moldTitle, ExcelDownData.moldSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void developmentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DevelopmentQueryHelper.service.listDevelopmentSearchQuery(request, response);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[])result.nextElement();
			devMaster master = (devMaster)o[0];
			MasterData data = new MasterData(master);
			
			String[] string = new String[ExcelDownData.devTitle.length];
			
			string[0] = data.model;															// 프로젝트 코드
			string[1] = data.fullName;																// 프로젝트 명
			string[2] = data.dmName;									// DM
			string[3] = data.developmentStart;																// 개발 예상 START
			string[4] = data.developmentEnd;													// 개발 예상 END
			string[5] = data.dateSubString(true);													// 등록일
			string[6] = data.state;													// 상태
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.devFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.devTitle, ExcelDownData.devSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void myDevelopmentExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DevelopmentQueryHelper.service.listMyDevelopmentSearchQuery(request, response);
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[])result.nextElement();
			devActive active = (devActive)o[0];
			DevActiveData data = new DevActiveData(active);
			
			String[] string = new String[ExcelDownData.myDevTitle.length];
			
			string[0] = data.getMaster().getModel();															// 프로젝트 코드
			string[1] = data.masterName;																// 프로젝트 명
			string[2] = data.getMaster().getLifeCycleState().getDisplay(Message.getLocale());									// DM
			string[3] = data.getTask().getName();																// 개발 예상 START
			string[4] = data.name;													// 개발 예상 END
			string[5] = data.dmName;													// 등록일
			string[6] = data.workerName;													// 상태
			string[7] = data.activeDate;
			string[8] = DateUtil.subString(data.finishDate, 0, 10);
			string[9] = data.state;
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.myDevFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.myDevTitle, ExcelDownData.myDevSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void ECRExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec query = ECRSearchHelper.service.getECRQuery(request);
		QueryResult result = PersistenceHelper.manager.find(query);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeRequest ecr = (EChangeRequest) o[0];
			ECRData ecrData = new ECRData(ecr);
			
			String[] string = new String[ExcelDownData.ecrTitle.length];
			
			string[0] = ecrData.number;
			string[1] = ecrData.name;
			string[2] = ecrData.getChangeDisplay();
			string[3] = ecrData.writer;
			string[4] = ecrData.createDepart;
			string[5] = ecrData.writeDate;
			string[6] = ecrData.getLifecycle();
			string[7] = ecrData.creator;
			string[8] = ecrData.dateSubString(true);
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.ecrFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.ecrTitle, ExcelDownData.ecrSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}

	@Override
	public void ECOExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec qs = ECOSearchHelper.service.getECOQuery(request); 
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder ecr = (EChangeOrder) o[0];
			ECOData ecoData = new ECOData(ecr);
			
			String[] string = new String[ExcelDownData.ecrTitle.length];
			
			string[0] = ecoData.number;
			string[1] = ecoData.name;
			string[2] = ecoData.getlicensingDisplay(false);
			string[3] = ChangeUtil.getRiskTypeName(ecoData.getRiskType(), false);
			string[4] = ecoData.getLifecycle();
			string[5] = ecoData.creator;
			string[6] = ecoData.eoApproveDate;
			string[7] = ecoData.dateSubString(true);
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.ecoFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.ecoTitle, ExcelDownData.ecoSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void EOExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec qs = ECOSearchHelper.service.getECOQuery(request); 
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			EOData eoData = new EOData(eco);
			
			String[] string = new String[ExcelDownData.ecrTitle.length];
			
			string[0] = eoData.number;
			string[1] = eoData.name;
			string[2] = eoData.getEoTypeDisplay();
			string[3] = eoData.getLifecycle();
			string[4] = eoData.creator;
			string[5] = eoData.eoApproveDate;
			string[6] = eoData.dateSubString(true);
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.eoFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.eoTitle, ExcelDownData.eoSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	@Override
	public void partTreeExcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String oid = request.getParameter("oid");
		String baselineOid = request.getParameter("baseline"); 
		String viewName = request.getParameter("viewName");
		String desc = request.getParameter("desc");
		
		Baseline baseline = null;
		if (baselineOid != null)
			baseline = (Baseline) CommonUtil.getObject(baselineOid);
		
		View view = null;
		if (viewName != null)
			view = ViewHelper.service.getView(viewName);
		
		Object obj = baseline == null ? null : baseline;
		if (obj == null)
			obj = view == null ? null : view;

		WTPart part = (WTPart)CommonUtil.getObject(oid);
		//############################################################################
		if (baseline != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=", baseline.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=", part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}
		//############################################################################
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA );
		String fileName = "BOM_"+part.getNumber()+"_" + formatter.format(date) + ".xls";
		
		//setExcelFileName(HttpServletResponse response, String name)
		
		//WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
	
		WritableWorkbook workbook = setExcelFileName(response,fileName);
		
		String sheetName = part.getViewName();
		workbook = PartExcelHelper.service.createExcel(workbook, sheetName, part, desc, obj);
		
		// 파일 다운
		workbook.write();
        workbook.close();
        
	}
	
	@Override
	public void loginHistoryxcelDown(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String userName = request.getParameter("userName");
		String userId = request.getParameter("userId");
			
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.appendClassList(LoginHistory.class, true);
	    	
        if(userName != null && userName.trim().length() > 0) {
        	if(qs.getConditionCount() > 0)
        		qs.appendAnd();
        	qs.appendWhere(new SearchCondition(LoginHistory.class, "name", SearchCondition.LIKE, "%" + userName + "%"), new int[] { idx });
        }
        
        if(userId != null && userId.trim().length() > 0) {
        	if(qs.getConditionCount()>0)qs.appendAnd();
        	qs.appendWhere(new SearchCondition(LoginHistory.class, "id", SearchCondition.LIKE, "%" + userId + "%"), new int[] { idx });
        }
	    
	    qs.appendOrderBy(new OrderBy(new ClassAttribute(LoginHistory.class,"thePersistInfo.createStamp"), true), new int[] { idx }); 
		
		QueryResult result = PersistenceHelper.manager.find(qs);
		
		List<String[]> list = new ArrayList<String[]>();
		
		while(result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
	    	LoginHistory history = (LoginHistory) o[0];
			
			String[] string = new String[ExcelDownData.loginHistoryTitle.length];
			
			string[0] = history.getName();
			string[1] = history.getId();
			string[2] = history.getPersistInfo().getCreateStamp().toString();
			
			
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.loginHistoryFileName);
		
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.loginHistoryTitle, ExcelDownData.loginHistorySize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}
	
	/**
	 * 일괄 다운로드 엑셀 파일 생성
	 */
	@Override
	public File batchDownloadexcelDown(String fileName,List<BatchDownData> targetlist) throws Exception {
		
		List<String[]> list = new ArrayList<String[]>();
		int rowCount = 1;
		for(BatchDownData data : targetlist){
		
			int i = 0 ;
			String[] string = new String[ExcelDownData.batchDownTitle.length];
			
			string[i++] = String.valueOf(rowCount++);
			string[i++] = data.getObjectType();
			string[i++] = data.getNumber();
			string[i++] = data.getRev();
			string[i++] = data.getName();
			string[i++] = data.getCreator();
			string[i++] = data.getModifier();
			string[i++] = data.getAttachType();
			string[i++] = data.getFileName();
			string[i++] = data.getMsg();
			list.add(string);
		}
		
		// 엑셀 파일명 설정
		
		String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
		String path = WTProperties.getServerProperties().getProperty("wt.temp");
		
		WritableWorkbook workbook = null;
		fileName = fileName + "_"+System.currentTimeMillis();
		File file = new File(path+"/"+fileName+".xls");
		workbook = Workbook.createWorkbook(file);
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.batchDownTitle, ExcelDownData.batchDownSize);
		
		
		workbook.write();
        workbook.close();
        
        return file;
	}
	
	/**
	 * 휴저별 그룹 목록
	 */
	@Override
	public void usetToGroupExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec qs= new QuerySpec();
		Class cls = People.class;
		qs.addClassList(cls, true);
		
		QueryResult qr =PersistenceHelper.manager.find(qs);
		List<String[]> list = new ArrayList<String[]>();
		while(qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			People pp = (People)o[0];
			WTUser user = pp.getUser();
			String[] string = new String[100];
			Enumeration en = user.parentGroups(false);
			int i = 0 ;
			string[i++] = user.getFullName();
	    	string[i++] = user.getName();
			
	    	HashMap<String, String> mapGroup = new HashMap<String, String>();
		    while (en.hasMoreElements ()) {
			
		    	Object ob2 = (Object)en.nextElement();
	        	WTPrincipalReference aa = (WTPrincipalReference)ob2;
	        	WTGroup group = (WTGroup)aa.getPrincipal();
	        	
	        	if(ob2 instanceof WTOrganization){
	        		continue;
	        	}
	        	
	        	if(!group.getDomainRef().equals(user.getDomainRef())){
	        		continue;
	        	}
	        	
	        	if(group.getName().equals("lutronic")){
	        		continue;
	        	}
		    	
		    	string[i++] = group.getName();
		    	mapGroup.put(group.getName(), group.getName());
			
		    }
		    
		    list.add(string);
		}
		
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.userToGroupFileName);
		
		
		String[] userToGroupTitle  = {"이름", "id", "그룹"};
		int[] userToGroupSize 	 = {20, 30, 25};
		
		int row = 0;
		
		WritableSheet sheet = workbook.createSheet("목록", 1);
        WritableCellFormat titleformat = JExcelUtil.getCellFormat(Alignment.CENTRE, Colour.LIGHT_GREEN);
        WritableCellFormat cellformat = new WritableCellFormat();
        cellformat.setAlignment(Alignment.CENTRE);
		
		for (int i = 0; i < userToGroupTitle.length; i++) {
			sheet.setColumnView(i, userToGroupSize[i]);
			sheet.addCell(new Label(i, row, userToGroupTitle[i], titleformat));
		}
		
		for(String[] string : list) {
			
			row++;
            int columnIndex = 0;
			
            for (int i = 0; i < 100; i++) {
            	if(string[i] == null){
            		break;
            	}
            	sheet.addCell(new Label(columnIndex++, row, string[i])); 
            }
		}
		
		// 엑셀 양식 등록
		
		//createExcelSheet(workbook, list, ExcelDownData.eoTitle, ExcelDownData.userToGroupSize);
		
		// 파일 다운
		workbook.write();
        workbook.close();
	}

	@Override
	public void admin_downLoadHistoryExcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		List<String[]> list = new ArrayList<String[]>();
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.appendClassList(DownloadHistory.class, true);
		String type = request.getParameter("type");
		String userId = request.getParameter("manager");
		String predate = StringUtil.checkNull(request.getParameter("predate"));
		String postdate = StringUtil.checkNull(request.getParameter("postdate"));
		if(type != null && type.trim().length() > 0 ) {
			if (qs.getConditionCount() > 0) qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, DownloadHistory.D_OID, SearchCondition.LIKE , "%"+type+"%"), new int[] { idx });
		}
		
		if(userId != null && userId.length() > 0 ){
			WTUser user = (WTUser)CommonUtil.getObject(userId);
			if (qs.getConditionCount() > 0) qs.appendAnd();
			qs.appendWhere(new SearchCondition(DownloadHistory.class, "userReference.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
		}
		//등록일
    	if(predate.length() > 0){
    		if(qs.getConditionCount() > 0) { qs.appendAnd(); }
    		qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.createStamp" ,SearchCondition.GREATER_THAN,DateUtil.convertStartDate(predate)), new int[]{idx});
    	}
    	
    	if(postdate.length() > 0){
    		if(qs.getConditionCount() > 0)qs.appendAnd();
    		qs.appendWhere(new SearchCondition(DownloadHistory.class, "thePersistInfo.createStamp",SearchCondition.LESS_THAN,DateUtil.convertEndDate(postdate)), new int[]{idx});
    	}
		qs.appendOrderBy(new OrderBy(new ClassAttribute(DownloadHistory.class, "thePersistInfo.createStamp"), true), new int[] { idx });  
	
		QueryResult qr = PersistenceHelper.manager.find(qs);


		while(qr.hasMoreElements()){	
			Object obj[] = (Object[])qr.nextElement();
			DownloadHistory history = (DownloadHistory)obj[0];
			DownloadDTO dataD = new DownloadDTO(history);
			String[] string = new String[ExcelDownData.downloadHistoryTitle.length];
			string[0] = dataD.getUserName();
			string[1] = dataD.getUserID();
			string[2] = dataD.getModuleInfo();
			string[3] = ""+dataD.getDownCount();
			string[4] = ""+dataD.getDownTime();
			string[5] = StringUtil.checkReplaceStr(dataD.getDescribe(), "-");
			list.add(string);
		}
		// 엑셀 파일명 설정
		WritableWorkbook workbook = setExcelFileName(response, ExcelDownData.downloadHistoryFileName);
				
		// 엑셀 양식 등록
		createExcelSheet(workbook, list, ExcelDownData.downloadHistoryTitle, ExcelDownData.downloadHistorySize);
				
		// 파일 다운
		workbook.write();
        workbook.close();		
	}
}
