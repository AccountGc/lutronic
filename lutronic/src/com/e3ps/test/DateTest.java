package com.e3ps.test;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ntp.TimeStamp;

import com.e3ps.common.util.DateUtil;

public class DateTest {

	public static void main(String[] args) {
		
		String date = DateUtil.getPreviousMonth(3);
		
		//System.out.println(date);
		// TODO Auto-generated method stub
		//2015-11-10~2015-12-11
		
		
		//int intStart = Integer.parseInt(startDate);
		//int intEnd = Integer.parseInt(endDate);
		
		//System.out.println(intStart + "="+intEnd +"="+(intEnd - intStart));
		/*
		String startYear = startDate.substring(0,4);
		String endYear = endDate.substring(0,4);
		String startMonth = startDate.substring(5,7);
		String endMonth = endDate.substring(5,7);
		
		int startIntMonth = Integer.parseInt(startMonth);
		int enthIntMonth = Integer.parseInt(endMonth);
		System.out.println(startIntMonth +","+enthIntMonth);
		//startYear ="20";
		//endYear ="20";
		//System.out.println(startYear+","+endYear +"=" +startMonth+","+endMonth);
		ArrayList list = StatusUtil.getYearList(startYear, endYear);
		
		for(int i = 0 ; i <list.size() ; i++){
			
			System.out.println(list.get(i));
		}
		
		String colspan="#rspan,#rspan,#rspan,#rspan,#rspan,#rspan,";
		
		System.out.println("colspan="+colspan.substring(0, colspan.length()-1));
		
		/*
		Timestamp stampStart=DateUtil.convertDate(startDate);
		Timestamp stampDate=DateUtil.convertDate(endDate);
		//DateUtil.getWeekdaysInMonth(year, month, weekDay)
		Calendar caStart = Calendar.getInstance();
		caStart.setTime(new Date(stampStart.getTime()));
		Calendar caEnd = Calendar.getInstance();
		caEnd.setTime(new Date(stampDate.getTime()));
		*/
		
		
	}
	
	public static int startBlankCount(String startDate,String endDate,String nodeStartDate ,String nodeEndDate){
		
		int startBlank = 0;
		int middeColor = 0;
		int endBlank = 0;
		int totalcount = 37;
		try{
			
			//String nodeStartDate = DateUtil.getDateString(node.getPlanStartDate(), "d") ;
			//String nodeEndDate = DateUtil.getDateString(node.getPlanEndDate(), "d") ;
					
			int startYear = Integer.parseInt(startDate.substring(0,4));
			int endYear = Integer.parseInt(endDate.substring(0,4));
			int startMonth = Integer.parseInt(startDate.substring(5,7)) ;
			int endMonth = Integer.parseInt(endDate.substring(5,7));
			
			int nodeStartYear = Integer.parseInt(nodeStartDate.substring(0,4));
			int nodeEndYear = Integer.parseInt(nodeEndDate.substring(0,4));
			int nodeStartMonth = Integer.parseInt(nodeStartDate.substring(5,7));
			int nodeEndMonth =  Integer.parseInt(nodeEndDate.substring(5,7));
			
			//System.out.println("nodeStartYear = " + nodeStartYear);
			//System.out.println("nodeEndYear = " + nodeEndYear);
			//System.out.println("nodeStartMonth = " + nodeStartMonth);
			//System.out.println("nodeEndMonth = " + nodeEndMonth);
			//System.out.println("totalcount = " + totalcount);
			if(startYear == nodeStartYear){
				startBlank = nodeStartMonth - startMonth;
				//System.out.println("startBlank 1 = " + startBlank);
			}else if((nodeStartYear-startYear)==1){
				startBlank=(12 - startMonth )+nodeStartMonth;
				//System.out.println("startBlank 2 = " + startBlank);
				
			}else{
				int yearMonth = (nodeStartYear-startYear -1)*12;
				startBlank=(12 - startMonth  +1+ yearMonth )+nodeStartMonth;
				//System.out.println("startBlank 3 = " + startBlank);
			}
			
			
			if(nodeStartYear == nodeEndYear){
				middeColor = nodeEndMonth - nodeStartMonth + 1;
				//System.out.println("middeColor 1 = " + middeColor);
			}else if((nodeEndYear-nodeStartYear)==1){
				middeColor = (12 - nodeStartMonth +1) + nodeEndMonth;
				//System.out.println("middeColor 2 = " + middeColor);
			}else{
				
				int yearMonth = (nodeEndYear - nodeStartYear - 1)*12;
				middeColor=(12 - nodeStartMonth + 1  + yearMonth )+ nodeEndMonth;
				//System.out.println("middeColor 3 = " + middeColor);
			}
			
			endBlank = totalcount - middeColor - startBlank;
			
			//System.out.println("endBlank  = " + endBlank);
			
			 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return middeColor;
	}
	
	
	
	
}
