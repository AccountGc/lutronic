package com.e3ps.part.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import wt.epm.E3PSRENameObject;
import wt.part.WTPart;
import wt.session.SessionServerHelper;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.service.PartHelper;

public class PartUtil {
	
	/**
	 * PDM에서 채번 유무 체크 true이면 가도번 또는 더미
	 * @param partNumber 
	 * @return
	 */
	public static boolean isChange(String partNumber) {
			boolean reValue = true;
			
			if(partNumber != null ){
			
				if( partNumber.length() == 10 ){
					if(Pattern.matches("^[0-9]+$", partNumber)){
						//숫자임
						reValue = false;
							
						}else{
						//숫자아님
							reValue = true;
						}
				}else{
					reValue = true;
				}
			}else{
				//입력값 없음.
				reValue = true;
			}
			return reValue;
	}
	/**
	 * PDM에서 채번 유무 체크 true이면 가도번 또는 더미
	 * @param partNumber 
	 * @return
	 */
	public static boolean isChange(String partNumber,WTPart parentPart) {
		boolean rtBoolean = false;
				WTPart part =null;
				try {
					part = PartHelper.service.getPart(partNumber);
				
				BomBroker bomBroker = new BomBroker();
				PartTreeData partTreeData = bomBroker.getOneleveTreeAsc(part, null);
					for (int i = 0; i < partTreeData.children.size(); i++) {
						PartTreeData tmp = (PartTreeData)partTreeData.children.get(i);
						if(tmp.part.getNumber().equals(parentPart.getNumber())){
							//System.out.println("tmpN = "+ tmp.part.getNumber());
							rtBoolean = isChange(tmp.part.getNumber());
							if(rtBoolean) break;
						}
					}
				} catch (Exception e) {
				}
			return rtBoolean;
	}
	public static HashMap<String, String> getPartNumberGoup(String number){
		HashMap<String, String> map = new HashMap<String, String>();
		
		if(isChange(number)){
			return map;
		}
		
		String no1 = number.substring(0,1); //부품 분류
		String no2 = number.substring(1,3); //대분류
		String no3 = number.substring(3,5);//중분류
		
		map.put("PARTTYPE1", no1);
		map.put("PARTTYPE2", no2);
		map.put("PARTTYPE3", no3);
		
		return map;
	}
	
	public static String getERPAttributeValue(String codeType,String value){
		
		NumberCode code =NumberCodeHelper.service.getNumberCode(codeType, value);
		if(code != null){
			value = value+":"+code.getName();
		}
		
		return value;
		
	}
	

	/**
	 * 최상위 품번 체므
	 * @param number
	 * @return
	 */
	public static boolean completeProductCheck(String number){
		
		
		String firstNumber = number.substring(0,1);
		String endNumber = number.substring(5,8);// number.substring(5,number.length());
		
		//System.out.println(number + ":" + firstNumber +":" +endNumber +":" + (firstNumber.equals("1") && !endNumber.endsWith("00000")));
		if(firstNumber.equals("1") && !endNumber.endsWith("000")){ //6,7,8이 000인경우
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * 완제품 체크 
	 * 전체 길이 10 자리  이고 앞자리가 1
	 * @param number
	 * @return
	 */
	public static boolean isProductCheck(String number){
		
		int len = number.length();
		int idx = number.indexOf("1");
		
		if(len == 10 && idx == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 부품 Name 체크
	 * @param partName
	 * @param tempName
	 * @return
	 */
	public static String partNameCheck(String partName,String tempName){
		
		if(tempName.length()==0){
			return partName;
		}
		
		if(partName.length() > 0) {
			partName = partName + "_";
		}
		partName = partName+tempName;
		
		return partName;
	}
	
	
}
