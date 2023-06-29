package com.e3ps.load;

import java.io.File;
import java.sql.SQLException;

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

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;

public class CodeLoader {

    /**
    * @param args
    */
    public static void main(final String[] args)throws Exception{
        //System.out.println("Initializing...");
        if ((args == null) || (args.length < 3)){
            //System.out.println("CodeLoader version 1.0\n Usage [Excel File Path] [User Name] [User Password]]");
            System.exit(0);
        }
        setUser(args[1], args[2]);
        new CodeLoader().loadCode(args[0]);
    }

    public static void setUser(final String id, final String pw)
    {
        RemoteMethodServer.getDefault().setUserName(id);
        RemoteMethodServer.getDefault().setPassword(pw);
    }

    public void loadCode(final String filePath)throws Exception{

        //String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
        //String sFilePath = sWtHome + "/loadFiles/" + filePath ;

        File newfile =  new File(filePath);
        Workbook wb = JExcelUtil.getWorkbook(newfile);
        Sheet[] sheets = wb.getSheets();

        int p1 = 0;
        int p2 = 0;
        int p3 = 0;
        
        for (int i = 1; i < sheets.length; i++) {
            int rows = sheets[i].getRows();

            //Cell[] cell = sheets[i].getRow(1);

            NumberCodeType ctype = null;
            String tempCodeType ="";
            int tempCount = 0;
            for (int j = 1; j < rows; j++) {
                String codeType = "";
                String codeTypeName = "";
                String code = "";
                String codeName = "";
                String codeEngName = "";
                String description = "";
                String parentCode1 = "";
                String parentCode2 = "";
                String sort = "";
                String disabled = "";

                Cell[] cell = sheets[i].getRow(j);
                
                cell = sheets[i].getRow(j);
                codeType = StringUtil.checkNull(JExcelUtil.getContent(cell, 0).trim());
                codeTypeName = StringUtil.checkNull(JExcelUtil.getContent(cell, 1).trim());
                code = StringUtil.checkNull(JExcelUtil.getContent(cell, 2).trim());
                codeName = StringUtil.checkNull(JExcelUtil.getContent(cell, 3).trim());
                codeEngName = StringUtil.checkNull(JExcelUtil.getContent(cell, 4).trim());
                description = StringUtil.checkNull(JExcelUtil.getContent(cell, 5).trim());
                parentCode1 = StringUtil.checkNull(JExcelUtil.getContent(cell, 6).trim());
                parentCode2 = StringUtil.checkNull(JExcelUtil.getContent(cell, 7).trim());
                sort = StringUtil.checkNull(JExcelUtil.getContent(cell, 8).trim());
                disabled = StringUtil.checkNull(JExcelUtil.getContent(cell, 9).trim());
                
                //System.out.println("#####################################################");
                //System.out.println("CodeType >>>>>  " + codeType +":"+code.length() + " : "+(codeType.length() > 0));
                //System.out.println("CodeTypeName >>>>>  " + codeTypeName);
                //System.out.println("Code >>>>>  " + code);
                ///System.out.println("CodeName >>>>>  " + codeName);
                
                if(codeType.length() > 0) {
                	tempCodeType = codeType;
                }else{
                	codeType = tempCodeType;
                }
                if(code.length() > 0) {
                	//System.out.println("codeType.length() > 0  " + codeName);
                	ctype = NumberCodeType.toNumberCodeType(codeType);
                	
                	NumberCode nCode = null;
                	
                	if(code.length() > 0) {
                		nCode = getNumberCode(codeType, code, parentCode1, parentCode2);
                	}else {
                		throw new Exception("Number Code가 정의되어 있지 않습니다..... [" + i + "] Sheet [" + j + "] row");
                	}
                	
                	if(nCode == null) {
                		nCode = new NumberCode();
                		nCode.setCodeType(ctype);
                	}
                	
                	nCode.setCode(code);
                	nCode.setName(codeName);
                	nCode.setEngName(codeEngName);
                	nCode.setDescription(description);
                	nCode.setSort(sort);
                	
                	if("T".equals(disabled)) {
                		nCode.setDisabled(true);
                	}else {
                		nCode.setDisabled(false);
                	}
                	
                	NumberCode parentNumberCode = null;
                	if(parentCode1.length() > 0 && parentCode2.length() > 0) {
                		parentNumberCode = getParentCode(codeType, parentCode2, parentCode1);
                		nCode.setParent(parentNumberCode);
                		p3++;
                	}
                	
                	if(parentCode1.length() > 0 && parentCode2.length() == 0) {
                		parentNumberCode = getParentCode(codeType, parentCode1, "");
                		nCode.setParent(parentNumberCode);
                		p2++;
                	}
                	
                	if(parentCode1.length() == 0 && parentCode2.length() == 0) {
                		p1++;
                	}
                	
                	
                	PersistenceHelper.manager.save(nCode);
                	tempCount++;
                }else {
                	break;
                	//throw new Exception("Code Type이 정의되어 있지 않습니다..... [" + i + "] Sheet [" + j + "] row");
                }
                
             }//for  j
            //System.out.println("################ sheet ["+sheets[i].getName()+"] ################");
            //System.out.println("Code Type =" +ctype.getDisplay() +":"+ctype.toString() );
            //System.out.println("totalCount =" +tempCount);

        }//for i
        
        /*
        System.out.println("###########################");
        System.out.println("Code Loader.");
        System.out.println("부품 분류 >>>  " + p1);
        System.out.println("대    분류 >>>  " + p2);
        System.out.println("중    분류 >>>  " + p3);
        System.out.println("Total  >>> " + (p1+p2+p3));
        System.out.println("###########################");
        */

    }//class
    
    public NumberCode getParentCode(String codeType, String code, String parentCode) {
    	NumberCode numberCode = null;
    	QuerySpec qs = null;
    	
    	try {
    		qs = new QuerySpec();
    		
    		int idx = qs.addClassList(NumberCode.class, true);
    		
    		qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE_TYPE, SearchCondition.EQUAL, codeType), new int[] {idx});
    		
    		if(qs.getConditionCount() >0) {
    			qs.appendAnd();
    		}
    		qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE, SearchCondition.EQUAL, code), new int[] {idx});
    		
    		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
    		
    		while(qr.hasMoreElements()) {
    			Object[] o = (Object[]) qr.nextElement();
    			NumberCode nCode = (NumberCode)o[0];
    			
    			if(parentCode.length() > 0 ) {
	    			if(parentCode.equals(nCode.getParent().getCode())) {
	    				numberCode = nCode;
	    			}
    			}else {
    				numberCode = nCode;
    			}
    			
    		}
    	
    	}catch(QueryException e) {
    		e.printStackTrace();
    	}catch(WTException e) {
    		e.printStackTrace();
    	}
    	
    	return numberCode;
    }
    
    public NumberCode getNumberCode(String codeType, String code, String parentCode1, String parentCode2) {
    	NumberCode numberCode = null;
    	QuerySpec qs = null;
    	
    	try {
    		qs = new QuerySpec();
    		
    		int idx = qs.addClassList(NumberCode.class, true);
    		
    		qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE_TYPE, SearchCondition.EQUAL, codeType), new int[] {idx});
    		
    		if(qs.getConditionCount() >0) {
    			qs.appendAnd();
    		}
    		qs.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE, SearchCondition.EQUAL, code), new int[] {idx});
    		
    		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
    		
    		while(qr.hasMoreElements()) {
    			Object[] o = (Object[]) qr.nextElement();
    			NumberCode nCode = (NumberCode)o[0];
    			
    			if(parentCode1.length() > 0 && parentCode2.length() > 0) {
    				
    				NumberCode parent2NumberCode = nCode.getParent();
    				
    				if(parentCode2.equals(parent2NumberCode.getCode())) {
    					NumberCode parent1NumberCode = parent2NumberCode.getParent();
    					
    					if(parentCode1.equals(parent1NumberCode.getCode())) {
    						numberCode = nCode;
    					}
    					
    				}
    				
    			}else if(parentCode1.length() > 0) {
    				NumberCode parent1NumberCode = nCode.getParent();
					
					if(parentCode1.equals(parent1NumberCode.getCode())) {
						numberCode = nCode;
					}
    			}else {
    				numberCode = nCode;
    			}
    			
    		}
    	
    	}catch(QueryException e) {
    		e.printStackTrace();
    	}catch(WTException e) {
    		e.printStackTrace();
    	}
    	
    	return numberCode;
    }

}
