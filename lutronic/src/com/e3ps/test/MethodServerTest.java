package com.e3ps.test;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Vector;

import wt.method.RemoteAccess;
import wt.part.WTPart;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.erp.service.ERPHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.service.PartSearchService;
import com.e3ps.part.service.PartService;

public class MethodServerTest implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	 public static MethodServerTest manager = new MethodServerTest();
	 
	 public static void main(String[] args) {
		 
		 try{
			 Vector<WTPart> vecPart = new Vector<WTPart>();
			 String oid = "wt.part.WTPart:530705";//"wt.part.WTPart:530504";
			 String number1 ="A06077";
			 WTPart part1 = PartHelper.service.getPart(number1);
			 vecPart.add(part1);
			 
			 String number2 ="A60063";
			 WTPart part2 = PartHelper.service.getPart(number2);
			 //System.out.println("part2=" +part2.getNumber());
			 vecPart.add(part2);
			 /*
			 String number3 ="B20003";
			 WTPart part3 = PartHelper.service.getPart(number3);
			 vecPart.add(part3);
			 */
			 
			 MethodServerTest.manager.sendSAPMaterial(vecPart);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public void previousVerionTest (Vector<WTPart> vecPart) {
		 
	 }
	 
	 public void sendSAPMaterial(Vector<WTPart> vecPart) {
		 //System.out.println(" ================= TEST sendSAPMaterial TEST");
			if(!SERVER) {
	            Class argTypes[] = new Class[]{Vector.class};
	            Object args[] = new Object[]{vecPart};
	            try {
	               wt.method.RemoteMethodServer.getDefault().invoke(
	                        "sendSAPMaterial",
	                        null,
	                        this,
	                        argTypes,
	                        args);
	               return ;
	            } catch(RemoteException e) {
	                e.printStackTrace();
	               /// throw new WTException(e);
	            } catch(InvocationTargetException e) {
	                e.printStackTrace();
	               // throw new WTException(e);
	            } catch(Exception e){
	                e.printStackTrace();
	               // throw e;
	            }
	            
	        }
			try{
				//System.out.println(" TEST sendSAPMaterial START");
				
				//System.out.println(" TEST sendSAPMaterial END");
			}catch(Exception e){
				e.printStackTrace();
			}
			 //System.out.println(" TEST sendSAPMaterial TEST ================= ");
			
	 }
}
