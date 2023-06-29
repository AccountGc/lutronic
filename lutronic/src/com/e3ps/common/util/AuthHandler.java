package com.e3ps.common.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.auth.AuthenticationServer;
import wt.method.MethodAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

public class AuthHandler implements RemoteAccess{
	
	
	
	public static MethodAuthenticator getMethodAuthenticator(String userName) throws RemoteException, InvocationTargetException{
		if(!wt.method.RemoteMethodServer.ServerFlag){
			Class c[] = new Class[]{String.class};
			Object o[] = new Object[]{userName};
			
			return (MethodAuthenticator)RemoteMethodServer.getDefault().invoke("getMethodAuthenticator", AuthHandler.class.getName(), null, c, o);
		}
		
		return AuthenticationServer.newMethodAuthenticator(userName);
	}
}
