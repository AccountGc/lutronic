/**
* @(#) ParamHash.java
* Copyright (c) e3ps. All rights reserverd
* 
 *	@version 1.00
 *	@since jdk 1.4.02 && Windchill 7.0
 *	@createdate 2005. 8. 8.
 *	@author Cho Sung Ok, jerred@e3ps.com
 *	@desc	
 */
package com.e3ps.common.web;

import java.util.Hashtable;

public class ParamHash extends Hashtable {
	public synchronized Object get(Object arg0) {
		Object returnObj = null;
		returnObj = super.get(arg0);
		if ( returnObj == null ) returnObj = "";
		return returnObj;
	}
	
	public synchronized Object get(Object arg0,boolean returnArray) {
		Object returnObj = null;
		returnObj = super.get(arg0);
		if ( returnArray ) {
			if ( returnObj instanceof String[]) {
				return returnObj;
			} else {
				String[] tempArr = {(String)returnObj};
				return tempArr;	
			}	
		} else {
			if ( returnObj == null ) returnObj = "";
			return returnObj;	
		}
	}
	
	public synchronized String getStr(Object arg0) {
		return getStr(arg0,"");
	}
	
	public synchronized String getStr(Object arg0,String defaultValue) {
		String returnObj = null;
		returnObj = (String)super.get(arg0);
		if ( returnObj == null ) returnObj = defaultValue;
		return returnObj;
	}
	
	public synchronized int getInt(Object arg0,int defaultValue) {
		String value = null;
		value = (String)super.get(arg0);
		if ( value == null ) value = defaultValue + "";
		int returnValue = 0;
		try {
			returnValue = Integer.parseInt(value);
		} catch ( Exception e ) {
			returnValue = -1;
		}
		return returnValue;
	}
}
