/*
 * @(#) E3PSAuthFlag.java  Create on 2006. 2. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.auth.beans;

import com.e3ps.common.message.Message;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2006. 2. 9.
 * @since 1.4
 */
public class E3PSAuthFlag
{
    /** ALL : 모든 권한    */
    public final static String ALL = "A";
    
    /** CREATE : 생성     */
    public final static String CREATE = "C";
    
    /** REVISION : 개정/수정    */
    public final static String REVISION = "N";
    
    /** VIEW : 파일 내용보기    */
    public final static String VIEW = "V";
    
    /** PRODUCT_VIEW : Product View 보기   */
    public final static String PRODUCT_VIEW = "P";
    
    public final static String DOWNLOAD ="D";
    
    public static String getName(String _code)
    {
        if(ALL.equals(_code))
            return Message.get("모든권한");
        else if(CREATE.equals(_code))
            return Message.get("등록");
        else if(REVISION.equals(_code))
            return Message.get("개정") + "/" + Message.get("수정");
        else if(VIEW.equals(_code))
            return Message.get("보기");
        else if(PRODUCT_VIEW.equals(_code))
            return Message.get("Product View 보기"); 
        else
            return "";
    }
}
