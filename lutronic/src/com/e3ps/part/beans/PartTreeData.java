/*
 * @(#) PartData.java  Create on 2004. 12. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.part.beans;

import java.util.ArrayList;
import java.util.HashMap;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.PartUtil;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;

@Getter
@Setter
public class PartTreeData implements java.io.Serializable{
    public int level;
    public WTPart part;
    public WTPartUsageLink link;
    public ArrayList children = new ArrayList();
    public String unit = "";
    public double quantity = 1;
    public String itemSeq="";
    public String lineImg = "joinbottom";
    public String number = "";
    public String name = "";
    public String plant = "";
    public String version = "";
    public String iteration = "";
    public double baseQuantity= 1;
    public String plmCode = "";
    public String maker ="";
    public String spec ="";
    public String purpose = "";
    public String location = "";
    public PartTreeData sap;
   
    
    
    public String dwgNo = "";
    public String dwgOid="";
    public String ecoNo = "";
    public String parentId = "";
    public String zitmsta = ""; //bom 비굥
    public boolean isChildren;
    
    public String model = ""; //프로젝트 코드
    public String productmethod=""; //제작 방법
    public String deptcode = ""; //부서
    public String manufacture="";
    public String mat = ""; //재질;
    public String finish = "";//후처리
    public String remark = ""; //비고
    public String weight = "";	//무게
    public String specification = ""; //사양
    
    public String locationOid = "";  //idx+oid;
    
	public PartTreeData(WTPart part, WTPartUsageLink link,int level,String rowID) throws Exception{
		
        setPart(part);
        setLink(link);
        setLevel(level);
        PartData data = new PartData(part);
        
        setNumber(data.number);
        setName(data.name);
        setVersion(data.version);
        setIteration(data.iteration);
        
        if(link!=null){
              double qs = (double)link.getQuantity().getAmount();
              setUnit( link.getQuantity().getUnit().toString());
              setQuantity(qs);
              try{
                  setItemSeq(data.number);
             }catch(Exception ex){ex.printStackTrace();}
        
        }else {
        	setUnit(part.getDefaultUnit().toString());
        }
        setParentId(rowID);
        String ecoNumber ="";
        /*
        weight = IBAUtil.getAttrfloatValue(part, AttributeKey.IBAKey.IBA_WEIGHT);
    	specification = IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_SPECIFICATION);
    	deptcode = IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_DEPTCODE);
    	manufacture = IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_MANUFACTURE);
    	productmethod = IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
    	ecoNumber = IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_CHANGENO);
    	*/
        HashMap map =IBAUtil.getAttributes(part);
        setModel((String)map.get(AttributeKey.IBAKey.IBA_MODEL));  //프로젝트 코드
        setProductmethod((String)map.get(AttributeKey.IBAKey.IBA_PRODUCTMETHOD));//제작방법
        setDeptcode((String)map.get(AttributeKey.IBAKey.IBA_DEPTCODE));	//부서 코드
        setManufacture((String)map.get(AttributeKey.IBAKey.IBA_MANUFACTURE));//MANUFATURER
        setMat((String)map.get(AttributeKey.IBAKey.IBA_MAT));//재질
        setFinish((String)map.get(AttributeKey.IBAKey.IBA_FINISH));//재질
        setRemark((String)map.get(AttributeKey.IBAKey.IBA_REMARKS));//비고
        setWeight((String)map.get(AttributeKey.IBAKey.IBA_WEIGHT));  //무게
        setSpecification((String)map.get(AttributeKey.IBAKey.IBA_SPECIFICATION));//사양
       
       
       
        
        
        ecoNumber =  (String)map.get(AttributeKey.IBAKey.IBA_CHANGENO); //ECO NO
    	if( ecoNumber != null && ecoNumber.length()> 0 ){
    		setEcoNo(ecoNumber);
    	}else{
    		setEcoNo(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_ECONO));
    	}
    	
    	/*
    	List<EChangeOrder> eolist = ECOSearchHelper.service.getPartTOECOList(part);
    	for(EChangeOrder eco : eolist){
			ecoNo += eco.getEoNumber()+",";
		}
    	*/
    }
    
    public boolean compare(PartTreeData dd){
        if( !unit.equals(dd.unit)){
            return false;
        }
        if( quantity != dd.quantity){
            return false;
        }
        if( baseQuantity != dd.baseQuantity){
        	return false;
        }
        return true;
    }
    
    public boolean equalsNumber(PartTreeData data){
    	return part.getNumber().equals(data.part.getNumber());
    }
    
    public boolean isChange() {
    	
    	return PartUtil.isChange(this.number);
    	/*
		String parttype = String.valueOf(this.number.charAt(0));
		return (("1".equals(parttype) 
				|| "2".equals(parttype) 
				|| "3".equals(parttype) 
				|| "4".equals(parttype) 
				|| "7".equals(parttype) 
				|| "8".equals(parttype) 
				|| "9".equals(parttype) ) 
				&& (this.number.length() == 10));
		*/
	}
    
    public String getDwgNo() {
    	String dwgNo = "";
    	boolean isDwg = false;
    	try {
    		EPMBuildRule buildRule = PartSearchHelper.service.getBuildRule(this.part);
    		if(buildRule != null) {
    			EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
    			EPMDocument epm2D = EpmSearchHelper.service.getEPM2D((EPMDocumentMaster)epm.getMaster());
    			//System.out.println("check 1"+(null != epm2D));
    			if(epm2D != null) {
    				dwgNo = epm2D.getNumber();
    				dwgOid = CommonUtil.getOIDString(epm2D);
    			}else {
    				isDwg = true;
    				if(epm != null){
    					
    					EpmData epmData = new EpmData(epm);
    					String appType = epmData.getApplicationType();
    					if("MANUAL".equals(appType)){
    						//System.out.println("check 2");
	        				dwgNo = epm.getNumber();
	        				dwgOid = CommonUtil.getOIDString(epm);
	        				isDwg = false;
    					}
        			}
    			}
    			
    			
    			
    		}else {
    			isDwg = true;
    		}
    		
    		if(isDwg) {
    			boolean isAP = PartSearchHelper.service.isHasDocumentType(part, "$$APDocument");
    			if(isAP){
    				dwgNo = "AP";
    				dwgOid = PartSearchHelper.service.getHasDocumentOid(part, "$$APDocument");
    			}else {
    				dwgNo = "ND";
    			}
    		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		dwgNo = "";
    	}
    	
    	return dwgNo;
    }
    
    public String getDwgOid() {
    	String dwgNo = "";
    	try {
    		EPMBuildRule buildRule = PartSearchHelper.service.getBuildRule(this.part);
    		if(buildRule != null) {
    			EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
    			EPMDocument epm2D = EpmSearchHelper.service.getEPM2D((EPMDocumentMaster)epm.getMaster());
    			if(epm2D != null) {
    				dwgNo =CommonUtil.getOIDString(epm2D);
    			}else {
    				if(epm != null){
        				dwgNo = CommonUtil.getOIDString(epm);
        			}
    			}
    			
    			
    			
    		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		dwgNo = "";
    	}
    	
    	return dwgNo;
    }
	
    /**
     * 무게
     * @return
     * @throws Exception
     */
    public String getWeight() throws Exception{
    	return IBAUtil.getAttrfloatValue(part, AttributeKey.IBAKey.IBA_WEIGHT);
    }
    
    /**
     * 사양
     * @return
     * @throws Exception
     */
    public String getSpecification() throws Exception{
    	return IBAUtil.getAttrValue2(part, AttributeKey.IBAKey.IBA_SPECIFICATION);
    }
    
    /**
     * 부서코드
     * @return
     * @throws Exception
     */
    public String getDeptcode() throws Exception{
    	
    	return IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_DEPTCODE);
    }

}
