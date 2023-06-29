package com.e3ps.common.message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

public class Message{

	static Map<String,MessageResource> messageMap;
	static String OUT_FILE_LOCATION;
	
	static{
		try {
			OUT_FILE_LOCATION = WTProperties.getLocalProperties().getProperty("wt.home");
			OUT_FILE_LOCATION += "\\wtCustom\\com\\e3ps\\common\\message\\MessageResource_add.txt";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:26
	 * @method getMessageMap
	 * @return Map<String,MessageResource>
	 * </pre>
	 */
	public static  Map<String,MessageResource> getMessageMap(){
		if(messageMap == null){
			messageMap = new HashMap<String, MessageResource>();
			MessageResource[] mr = MessageResource.getMessageResourceSet();
			for(MessageResource m : mr){
				//String key = m.toString();
				
				String ko = m.getDisplay(Locale.KOREA);
				//System.out.println("key =" + ko +", value="+m);
				messageMap.put(ko, m);
			}
		}
		return messageMap;
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:23
	 * @method getMessage
	 * @param key
	 * @return String
	 * </pre>
	 */
	public static String getMessage(String key) {
	    return Message.get(key);
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 29. 오전 10:37:18
	 * @method getLocaleString
	 * @return
	 * @throws WTException String
	 * </pre>
	 */
	public static String getLocaleString() throws WTException {
	    return SessionHelper.manager.getLocale().toString();
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:18
	 * @method getLocation
	 * @return Locale
	 * </pre>
	 */
	public static Locale getLocale() {
	    try {
			return SessionHelper.manager.getLocale();
		} catch (WTException e) {
			System.err.println("Locale을 받아올 수 없습니다.");
		}
	    return Locale.KOREA;
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:15
	 * @method get
	 * @param key
	 * @return String
	 * </pre>
	 */
	@SuppressWarnings("unused")
	public static  String get(String key) {
		String display = key;
		try {
			
			MessageResource message = getMessageMap().get(key);
			
			if(message==null){
				setMessageResource(key);
			}else{
				display = message.getDisplay(SessionHelper.manager.getLocale());
				
				if(CommonUtil.isUSLocale() && "NOENGILSH".equals(display)){
					display = message.getDisplay(Locale.KOREA);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		//display = "M.L"+display;
		return display;
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:11
	 * @method setMessageResource
	 * @param key void
	 * </pre>
	 */
	public static void setMessageResource(String key){
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			
			File outFile = new File(OUT_FILE_LOCATION);
			if(!outFile.exists()){
				outFile.createNewFile();
			}
			
			in = new BufferedReader(new FileReader(outFile));
			String read = "";
			int Cnt = MessageResource.getMessageResourceSet().length + 1;
			
			while((read = in.readLine()) != null){
				
				if(read.indexOf(".value=") >= 0){
					
					String value = read.substring(read.indexOf("=") + 1,read.length());
					
					if(key.equals(value)){
						return;
					}
					Cnt++;
				}
			}

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter( new FileOutputStream(outFile, true), "EUC-KR")));
			
			//#MSG1 - 문서분류
			//MSG1.value=문서분류
			//MSG1.order=1001

			out.println("#MSG" + Cnt + " - " + key);
			out.println("MSG" + Cnt + ".value=" + key);
			out.println("MSG" + Cnt + ".order=" + (1000 + Cnt));
			out.println("");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(out!=null){
				out.close();
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * <pre>
	 * @description  
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:45:03
	 * @method getImg
	 * @return String
	 * </pre>
	 */
	public static String getImg(){
		if(CommonUtil.isUSLocale()){
			return "_en";
		}
		return "";
	}
	
	/**
	 * <pre>
	 * @description NumberCode 용 다국어
	 * @author Administrator
	 * @date 2016. 1. 28. 오후 5:25:47
	 * @method getNC
	 * @param code
	 * @return String
	 * </pre>
	 */
	public static String getNC(NumberCode code){
		
		String display = code.getName();
		
		if(!Locale.KOREA.equals(getLocale())){
			display = code.getEngName();
			
			if(!StringUtil.checkString(display)){
				display = code.getName();
			}
		}
		return display;
	}
	
}
