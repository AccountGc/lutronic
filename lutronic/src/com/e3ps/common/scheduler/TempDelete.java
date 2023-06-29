package com.e3ps.common.scheduler;

import wt.util.WTException;

import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.DateUtil;
import com.ptc.wvs.common.util.WVSProperties;

public class TempDelete {
	
	
	   static {
	      boolean verbose = false;
	      String propValue = WVSProperties.getPropertyValue("publish.service.verbose");
	      if ((propValue != null) && propValue.equalsIgnoreCase("TRUE")) {
	         verbose = true;
	      }
	     
	   }

	   /**
	    * Temp 파일 삭제 Schedule
	    */
	   public static void excuteJob() {
	      try{
	         //System.out.println("[Schedule] TempFileDelete.class Excute!!");
	         //System.out.println("[Schedule] Excute Date : " + DateUtil.getCurrentDateString("d") + " " + DateUtil.getCurrentDateString("t"));
	         
	         CommonHelper.service.doTempDelete(false);	// temp 폴더 삭제
	         CommonHelper.service.doTempDelete(true);	// temp/upload 폴더 삭제
	         
	         //System.out.println("[Schedule] TempFileDelete Done..!");
	      }catch(WTException e){
	         e.printStackTrace();
	      }
	      //System.out.println("excuteJob :::::" + TempDelete.class);

	   }

}
