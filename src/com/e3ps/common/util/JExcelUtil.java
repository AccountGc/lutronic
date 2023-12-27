/*
 * @(#) JExcelUtil.java  Create on 2005. 2. 21.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jxl.Cell;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2005. 2. 21.
 * @since 1.4
 */
public class JExcelUtil
{
    public static Workbook getWorkbook(byte[] bytes)
    {
        Workbook wb = null;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            wb = Workbook.getWorkbook(bais);
        }
        catch (BiffException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return wb;
    }

    public static Workbook getWorkbook(File file)
    {
        if (file == null)
            return null;
        try
        {
            if (!file.getName().endsWith(".xls"))
                return null;
        }
        catch (Exception e)
        {
            return null;
        }

        Workbook wb = null;
        try
        {
            wb = Workbook.getWorkbook(file);
        }
        catch (BiffException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return wb;
    }

    public static String getContent(Cell[] cell, int idx)
    {
        try
        {
            String val = cell[idx].getContents();
            if (val == null)
                return "";
            return val.trim();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {}
        return "";
    }

    public static Timestamp getTimestamp(Cell[] _cell, int _idx)
    {
        try
        {
            String val = _cell[_idx].getContents();
            val = val == null ? "" : val.trim();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy:HH-mm-ss", Locale.KOREA);
            return new Timestamp(format.parse(val + ":12-00-00").getTime());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {}
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Timestamp getTimestamp2(Cell[] _cell, int _idx)
    {
      try {
        String val = _cell[_idx].getContents();
        val = val == null ? "" : val.trim();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy:HH-mm-ss", Locale.KOREA);
        return new Timestamp(format.parse(val + ":23-59-59").getTime());
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
      }
      catch (ParseException e) {
        e.printStackTrace();
      }
      return null;
    }
    
    public static boolean checkLine(Cell[] cell)
    {
        String value = null;
        try
        {
            value = cell[1].getContents().trim();	
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.getMessage();
            return false;
        }
        if (value == null || value.length() == 0)
            return false;
        return true;
    }

	public static boolean checkLine(Cell[] cell,int line)
    {
        String value = null;
        try
        {
            value = cell[line].getContents().trim();	
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.getMessage();
            return false;
        }
        if (value == null || value.length() == 0)
            return false;
        return true;
    }
	
	public static WritableCellFormat getCellFormat(Alignment alignment, Colour color)
   {
    WritableCellFormat format = null;
    try {
      format = new WritableCellFormat();
      if (color != null) {
        format.setBackground(color);
      }
      format.setBorder(Border.ALL, BorderLineStyle.THIN);

      if (alignment != null)
        format.setAlignment(alignment);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return format;
   }
}