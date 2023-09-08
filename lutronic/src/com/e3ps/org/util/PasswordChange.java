package com.e3ps.org.util;

import java.util.HashMap;

import com.infoengine.SAK.Task;
import com.infoengine.au.NamingService;

import wt.federation.PrincipalManager.DirContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTProperties;

public class PasswordChange implements RemoteAccess {

	private static String dir;
	private static String defaultAdapter;
	private static String defaultDirectoryUser;
	private static String userSearchBase;
	private HashMap typeMap;
	final static boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static void dirInit() throws WTException {
		if (defaultAdapter != null)
			return;
		try {
			WTProperties wtproperties = WTProperties.getServerProperties();
			String s = wtproperties.getProperty("wt.federation.ie.namingService", "namingService");
			String s1 = wtproperties.getProperty("wt.federation.ie.propertyResource");
			NamingService namingservice = NamingService.newInstance(s, s1);
			String s2 = wtproperties.getProperty("wt.federation.taskRootDirectory");
			String s3 = wtproperties.getProperty("wt.federation.taskCodebase");
			String s4 = wtproperties.getProperty("wt.federation.task.mapCredentials");
			defaultDirectoryUser = wtproperties.getProperty("wt.federation.org.defaultDirectoryUser",
					wtproperties.getProperty("wt.admin.defaultAdministratorName"));
			String s5 = wtproperties.getProperty("wt.federation.ie.VMName");
			if (s5 != null)
				System.setProperty("com.infoengine.vm.name", s5);
			if (s3 != null)
				System.setProperty("com.infoengine.taskProcessor.codebase", s3);
			if (s2 != null)
				System.setProperty("com.infoengine.taskProcessor.templateRoot", s2);
			if (s4 != null && System.getProperty("com.infoengine.credentialsMapper") == null)
				System.setProperty("com.infoengine.credentialsMapper", s4);
			defaultAdapter = DirContext.getDefaultJNDIAdapter();
			userSearchBase = DirContext.getJNDIAdapterSearchBase(defaultAdapter);

		} catch (Exception exception) {
			exception.printStackTrace();

		}
	}

	public static void changePassword(String id, String password) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { String.class, String.class };
			Object args[] = new Object[] { id, password };
			Object obj = null;

			obj = RemoteMethodServer.getDefault().invoke("changePassword", PasswordChange.class.getName(), null,
					argTypes, args);

			return;
		}
		dirInit();
		String s5 = DirContext.getMapping(defaultAdapter, "user.uniqueIdAttribute",
				DirContext.getMapping(defaultAdapter, "user.uid"));
		String object = s5 + '=' + id + ',' + userSearchBase;
		Task task = new Task("/wt/federation/UpdatePrincipal.xml");
		task.addParam("object", object);
		task.addParam("field", DirContext.getMapping(defaultAdapter, "user.userPassword") + '=' + password);
		task.addParam("modification", "replace");
		task.addParam("instance", defaultAdapter);
		task.setUsername(defaultDirectoryUser);
		task.invoke();

	}

}
