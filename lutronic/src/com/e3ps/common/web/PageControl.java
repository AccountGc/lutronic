/**
 *	@(#) PageControl.java
 *	Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 17.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package com.e3ps.common.web;

import java.io.Serializable;

import wt.fc.PagingQueryResult;

/**
 * 
 */
public class PageControl implements Serializable
{
    public static final int PERPAGE = 15; // �⺻ ���������� �����ִ� ����Ʈ �� ( ��ü ��� ���)
    public static final int FORMPAGE = 10; // �⺻ ��ȭ�鿡 �����ִ� ��������ũ �� ( ��ü ��� ���)
    private int initPerPage = 15; // ���������� �����ִ� ����Ʈ ��
    private int initFormPage = 10; // ��ȭ�鿡 �����ִ� ��������ũ ��

    private PagingQueryResult result;
    private int topListCount; // ���� ������ �Խù��� ��, �� ��ü����Ʈ���� ���� ������ �Խù��� �� �� ����
                                // ���� ī��Ʈ
    private int pageScope; // ȭ�鿡 ���̴� ������ ��ũ

    private long sessionid; // Paging Session ID
    private int totalCount; // �� �Խù� ��
    private int currentPage; // ���� ������
    private int startPage; // ���� ������
    private int endPage; // ������ ������
    private int totalPage; // �� ������

    private String href; // ����¡ URL
    private String param = ""; // ����¡ URL �Ķ����

    private boolean isPostMethod = false; // ������ �ѱ�� ��� ( true : post ���,
                                            // false : get ��� )

    public PageControl()
    {}

    public PageControl(PagingQueryResult _result, int _pageNo, int _initFormPage, int _initPerPage)
    {
        this.initFormPage = _initFormPage;
        this.initPerPage = _initPerPage;

        this.result = _result;
        sessionid = _result.getSessionId();

        // ���� ������ ����
        if (_pageNo <= 0)
            this.currentPage = 1;
        else
            this.currentPage = _pageNo;

        // �������� ����
        this.pageScope = 0;
        if (currentPage == 1)
            pageScope = 0;
        else
            pageScope = (currentPage - 1) / this.initFormPage;
        this.startPage = pageScope * this.initFormPage + 1;
        this.totalPage = 0;
        if (_result.getTotalSize() % this.initPerPage == 0)
            this.totalPage = _result.getTotalSize() / this.initPerPage;
        else
            this.totalPage = _result.getTotalSize() / this.initPerPage + 1;
        this.endPage = this.startPage + this.initFormPage - 1;
        if (this.totalPage < this.endPage)
            this.endPage = this.totalPage;

        this.totalCount = _result.getTotalSize();
        this.topListCount = this.totalCount - ((this.currentPage - 1) * this.initPerPage);
    }

    /**
     * Non-Windchill PageControl
     * 
     * @param _pageNo ���� ������ ��ȣ
     * @param _initFormPage ��ȭ�鿡 �����ִ� ��������ũ ��
     * @param itemPerPage ���������� �����ִ� ����Ʈ ��
     * @param totalCount �� �Խù� ��
     */
    public PageControl(int _pageNo, int _initFormPage, int itemPerPage, int totalCount)
    {
        this.initFormPage = _initFormPage;
        this.initPerPage = itemPerPage;

        this.totalCount = totalCount;

        // ���� ������ ����
        if (_pageNo <= 0)
            this.currentPage = 1;
        else
            this.currentPage = _pageNo;

        // �������� ����
        this.pageScope = 0;
        if (currentPage == 1)
            pageScope = 0;
        else
            pageScope = (currentPage - 1) / this.initFormPage;
        this.startPage = pageScope * this.initFormPage + 1;
        this.totalPage = 0;
        if (totalCount % this.initPerPage == 0)
            this.totalPage = totalCount / this.initPerPage;
        else
            this.totalPage = totalCount / this.initPerPage + 1;
        this.endPage = this.startPage + this.initFormPage - 1;
        if (this.totalPage < this.endPage)
            this.endPage = this.totalPage;

        this.topListCount = this.totalCount - ((this.currentPage - 1) * this.initPerPage);
    }
    
    /**
     * Non-Windchill PageControl
     * @param pageNo ���� ������ ��ȣ
     * @param totalCount �� �Խù� ��
     */
    public PageControl(int pageNo, int totalCount)
    {
        this(pageNo, PageControl.FORMPAGE, PageControl.PERPAGE, totalCount);
    }

    public PageControl(PagingQueryResult _result, int _pageNo, int _initFormPage)
    {
        this(_result, _pageNo, _initFormPage, PageControl.PERPAGE);
    }

    public PageControl(PagingQueryResult _result, int _pageNo)
    {
        this(_result, _pageNo, PageControl.FORMPAGE);
    }

    public void setHref(String _href)
    {
        this.href = _href;
    }

    public void setParam(String _param)
    {
        this.param = _param;
    }

    public void setGetMethod()
    {
        this.isPostMethod = false;
    }

    public void setPostMethod()
    {
        this.isPostMethod = true;
    }

    /**
     * @return Returns the isPostMethod.
     */

    public PagingQueryResult getResult()
    {
        return result;
    } // �˻� ��� �����Ѵ�.

    public int getTopListCount()
    {
        return this.topListCount;
    } // ����Ʈ �������� �Խù� ī��Ʈ�� �����Ѵ�.

    public int getTotalPage()
    {
        return this.totalPage;
    } // �� �������� �����Ѵ�.

    public int getTotalCount()
    {
        return this.totalCount;
    } // �� �Խù� ���� �����Ѵ�.

    public int getCurrentPage()
    {
        return this.currentPage;
    } // ���� �������� �����Ѵ�.

    public int getStartPage()
    {
        return startPage;
    } // ���� �������� �����Ѵ�.

    public int getEndPage()
    {
        return endPage;
    } // ������ �������� �����Ѵ�.

    public String getHref()
    {
        return href;
    }

    public long getSessionId()
    {
        return this.sessionid;
    } 

    public String getParam()
    {
        return param;
    }

    public boolean isPostMethod()
    {
        return isPostMethod;
    }

    public int getInitFormPage()
    {
        return initFormPage;
    }

    public int getInitPerPage()
    {
        return initPerPage;
    }
}