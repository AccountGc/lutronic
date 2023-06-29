/**
* @(#) WebUtil.java
* Copyright (c) e3ps. All rights reserverd
* 
 *	@version 1.00
 *	@since jdk 1.4.02 && Windchill 7.0
 *	@createdate 2005. 6. 27.
 *	@author Cho Sung Ok, jerred@e3ps.com
 *	@desc	
 */
package com.e3ps.common.web;

import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import wt.httpgw.WTURLEncoder;
import wt.util.WTProperties;

import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.util.CharUtil;

public class WebUtil {
	private static boolean isDecode = ConfigImpl.getInstance ().getBoolean ( "http.parameter.isdecode" );
	
	public static URL host;
	
	public static void main(String[] args){
		WebUtil.getHost();
	}
	
	public static URL getHost(){
		try{
			
			if(host==null){
				WTProperties props = WTProperties.getLocalProperties();
				host = props.getServerCodebase();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return host;
	}
	/**
     * Request�� ���� key, value�� Hashtabled���� ��ȯ�Ѵ�.
     * 
     * @param req
     * @return
     */
	
	public static ParamHash getHttpParamsFromAjax(HttpServletRequest req){
		ParamHash returnHash = new ParamHash();
        Enumeration eu = req.getParameterNames();
        String key = null;
        String[] value = null;
        
        
        while ( eu.hasMoreElements() ) {
            key = (String) eu.nextElement();
            value = req.getParameterValues(key);
            if (value == null) returnHash.put(key, "");
            else if (value.length == 1) {
            	 returnHash.put(key , WTURLEncoder.decode(value[0]));
            } else {
        	    String[] returnValue = new String[value.length];
    			for (int i = 0; i < value.length; i++) {
    				returnValue[i] = WTURLEncoder.decode(value[i]);
    			}
    			returnHash.put(key, returnValue);
            }
        }
      
        return returnHash;
		
	}
	
    public static ParamHash getHttpParams(HttpServletRequest req) {
    	ParamHash returnHash = new ParamHash();
        Enumeration eu = req.getParameterNames();
        String key = null;
        String[] value = null;
        while ( eu.hasMoreElements() ) {
            key = (String) eu.nextElement();
            
            value = req.getParameterValues(key);
            if (value == null) returnHash.put(key, "");
            else if (value.length == 1) {
            	if (isDecode) returnHash.put(key , CharUtil.E2K(value[0]));
            	else returnHash.put(key , value[0]);
            } else {
            	if (isDecode) {
        			String[] returnValue = new String[value.length];
        			for (int i = 0; i < value.length; i++) {
        				returnValue[i] = CharUtil.E2K(value[i]);
        			}
        			returnHash.put(key, returnValue);
        		} else {
        			returnHash.put(key, value);
        		}
            }
        }
        return returnHash;
    }
    
    public static String getHtml(String ss){
    	if(ss==null)return "";
    	
    	
    	ss = ss.replaceAll("<","&lt;");
    	ss = ss.replace(">", "&gt;");
    	ss = ss.replace("\"", "&quot;");
    	ss = ss.replaceAll("\n","<br>");
    	return ss;
    }
}
