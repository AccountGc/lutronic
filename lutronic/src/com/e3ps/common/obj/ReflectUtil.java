/*
 * @(#) ReflectUtil.java  Create on 2004. 11. 23.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.common.obj;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2004. 11. 23.
 * @since 1.4
 */
public class ReflectUtil
{
    public static void callSetMethod(Object obj, String methodName, Object[] args)
    {
        callSetMethod(obj, methodName, args, new Class[]{String.class});
    }
    
    public static void callSetMethod(Object obj, String methodName, Object[] args, Class[] classes)
    {
        try
        {
            Method method = obj.getClass().getMethod("set"+methodName, classes);
            method.invoke(obj, args);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * String �� �⺻���� ��ȯ�� ����Ѵ�.
     * @param obj
     * @param methodName
     * @return
     */
    public static String callGetMethod(Object obj, String methodName)
    {
        return (String)callGetMethod(obj,methodName,true);
    }
    
    public static Object callGetMethod(Object obj, String methodName, boolean flag)
    {
        Object result=null;
        try
        {
            Method method = obj.getClass().getMethod("get"+methodName, null);
            result = method.invoke(obj, null);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            //System.out.println("Exception = " + e.getLocalizedMessage());
        }
        return result;
    }
    
    
    
}
