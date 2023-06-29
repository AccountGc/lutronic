package com.e3ps.load;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;

public class DepartmentLoader  {
	
	
	public static void main(String[] args)throws Exception{
		
		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
		String sFilePath = sWtHome + "\\loadFiles\\lutronic\\Department\\Department.xls";

		if (args != null && args.length > 0 && args[0].trim().length() > 0) {
			sFilePath = args[0];
		}

		setUser(args[1], args[2]);

		new DepartmentLoader().load(sFilePath);

	}
	
	public static void setUser(final String id, final String pw) {
		RemoteMethodServer.getDefault().setUserName(id);
		RemoteMethodServer.getDefault().setPassword(pw);
	}
	
	public void load(String sFilePath){
		
		try {
			
			File newfile =  new File(sFilePath);
	
			Workbook wb = JExcelUtil.getWorkbook(newfile);
	
			Sheet[] sheets = wb.getSheets();
	
	        for (int i = 0; i < sheets.length; i++){
	        	
	            int rows = sheets[i].getRows();
	            
	            for (int j = 1; j < rows; j++) {
	                if (JExcelUtil.checkLine(sheets[i].getRow(j),0)){
	                    Cell[] cell = sheets[i].getRow(j);
	                    
	                    String departmentCode = StringUtil.checkNull(JExcelUtil.getContent(cell, 0).trim());
	                    String parentCode = StringUtil.checkNull(JExcelUtil.getContent(cell, 1).trim());
	                    String departmentName = StringUtil.checkNull(JExcelUtil.getContent(cell, 2).trim());
	                    String sortNumber = StringUtil.checkNull(JExcelUtil.getContent(cell, 3).trim());
	                    
	                    if(departmentCode.length() > 0) {
	                    	
	                    	Department department = Department.newDepartment();
	                    	
	                    	department.setCode(departmentCode);
	                    	department.setName(departmentName);
	                    	//department.setSort(Integer.valueOf(sortNumber));
	                    	
	                    	if(parentCode.length() > 0) {
	                    		Department parentDepartment = getDepartment(parentCode);
	                    		
	                    		if(parentDepartment == null) {
	                    			throw new Exception("Parent Code에 대한 Department가 없습니다....... [" + i + "] Sheet [" + j + "] row");
	                    		}
	                    		
	                    		department.setParent(parentDepartment);
	                    	}
	                    	
	                    	PersistenceHelper.manager.save(department);
	                    	
	                    	//System.out.println("##########################################################");
	                    	//System.out.println("Department Name ::::  " + departmentName);
	                    	//System.out.println("Department Code ::::  " + departmentCode);
		                    
	                    }else {
	                    	throw new Exception("Department Code가 정의되어 있지 않습니다..... [" + i + "] Sheet [" + j + "] row");
	                    }
	                }
	            }
	        }
        
	        //System.out.println("\n\n-----------------------------------------------------");
	        //System.out.println(sheets[0].getRows() + " Line Insert Complete..............");
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Department getDepartment(String code) {
		QuerySpec qs = null;
		Department department = null;
		
		try {
			qs = new QuerySpec();
			
			int idx = qs.addClassList(Department.class, true);
			
			qs.appendWhere(new SearchCondition(Department.class, Department.CODE, SearchCondition.EQUAL, code), new int[] {idx});
			
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			
			if(qr.hasMoreElements()) {
				Object[] o = (Object[])qr.nextElement();
				department = (Department)o[0];
			}
			
		} catch(QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		return department;
		
	}
}

