package com.e3ps.test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import com.e3ps.change.service.ECOHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.FileDown;

public class Test1 implements RemoteAccess {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static void main(String[] args) throws Exception{
		
		String number ="1010100012"; //10101 000 12
		String endNumber = number.substring(5,8);
		
		//System.out.println("endNumber =" + endNumber);
		
		/*
			double a = 18.8342;

		  double b = Math.round(a*1000.0)/1000.0; //math. round로 소수점 지정

		  System.out.println(b);

		  System.out.println(ContentRoleType.SECONDARY.toString());
		  System.out.println("SECONDARY ="+ContentRoleType.SECONDARY.toString().equals("SECONDARY"));
		//Math.scalb(d, scaleFactor)

		

		/*
		ResultData rd = start();
		
		System.out.println("message ===== " + rd.getMessage());
		System.out.println("oid ===== " + rd.getOid());
		System.out.println("result ===== " + rd.isResult());
		*/
	}
	
	public static boolean completeProductCheck(String number){
		
		
		String firstNumber = number.substring(0,1);
		String endNumber = number.substring(5,number.length());
		//System.out.println(number + ":" + firstNumber +":" +endNumber +":" + (firstNumber.equals("1") && !endNumber.endsWith("00000")));
		if(firstNumber.equals("1") && !endNumber.endsWith("00000")){ //6,7,8이 000인경우
			return true;
		}
		
		return false;
		
	}
	
	public static ResultData start() throws Exception {
		   
	   if (!SERVER) {
			Class argTypes[] = new Class[]{};
			Object args[] = new Object[]{};
			try {
				ResultData rd = (ResultData) RemoteMethodServer.getDefault().invoke("start", Test1.class.getName(), null, argTypes, args);
				return rd;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}
	   
	   return ECOHelper.service.excelDown("com.e3ps.change.EChangeOrder:2560431", "eco");
	}
}
