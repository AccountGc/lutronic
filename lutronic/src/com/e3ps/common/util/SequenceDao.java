package com.e3ps.common.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTProperties;

public class SequenceDao implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static final SequenceDao manager = new SequenceDao();

	static String dataStore = "Oracle"; //"SQLServer" ....
	static {
		try{
			dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
		}catch(Exception ex){
			dataStore = "Oracle";
		}
	}

	protected SequenceDao() {
	}
	
    /**
     * 
     * 
     * @param seqName
     *            sequence name
     * @param format
     *            sequence format
     * @param tabName
     *            DB table name
     * @param colName
     *            DB column name
     * @return
     */
    public String getSeqNo(String seqName, String format, String tabName, String colName)throws Exception
    {
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[]{String.class,String.class,String.class,String.class};
				Object args[] = new Object[]{seqName,format,tabName,colName};
				return (String)RemoteMethodServer.getDefault().invoke("getSeqNo", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}


		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

        /*
         * ORACLE �ʿ� format�� ���̿� ��� subString �ϵ��� ���� ����.	2012-09-19 Suk
         * 
			ORACLE : select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(WTDOCUMENTNUMBER),'DOC-0'),5,8))+1,'00000000')  FROM WTDOCUMENTMASTER WHERE WTDOCUMENTNUMBER LIKE 'DOC-%';
			MSSQL : SELECT convert( bigint, SUBSTRING( ISNULL( MAX(WTDocumentNumber),'DOC-0' ), 5, len( ISNULL( MAX(WTDocumentNumber),'DOC-0' ) ) ) )+1 FROM wtadmin.WTDocumentMaster WHERE WTDocumentNumber LIKE 'DOC-%';

		*/

			StringBuffer sb = null;
			if("Oracle".equals(dataStore)) {
				if("WTPartMaster".equals(tabName)){
					sb = new StringBuffer()
					.append("select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(")
					.append(colName)
					.append("),?),?,?))+1,?) FROM ")
					.append(tabName)
					.append(" WHERE ")
					.append(colName)
					.append(" LIKE ?")
					.append(" AND LENGTH(WTPARTNUMBER) = 10");
				}else{
					sb = new StringBuffer()
					.append("select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(")
					.append(colName)
					.append("),?),?,?))+1,?) FROM ")
					.append(tabName)
					.append(" WHERE ")
					.append(colName)
					.append(" LIKE ?");
				}
			}else {
				sb = new StringBuffer()
				.append("SELECT convert( bigint, SUBSTRING( ISNULL( MAX(")
				.append(colName)
				.append("),?),?, len( ISNULL( MAX(")
				.append(colName)
				.append("),?) ) ) )+1 FROM ")
				.append(tabName)
				.append(" WHERE ")
				.append(colName)
				.append(" LIKE ?");
				
			}

			st = con.prepareStatement(sb.toString());
			
			st.setString(1, seqName);
			st.setInt(2, seqName.length()+1);
			
			if("Oracle".equals(dataStore)) {
				st.setInt(3, format.length());
				st.setString(4, format);
				st.setString(5, seqName+"%");
			}else {
				st.setString(3, seqName);
				st.setString(4, seqName+"%");
			}
			
			rs = st.executeQuery();
			
			String seqNum = null;
			while (rs.next()) {
				seqNum = rs.getString(1);
			}
			if(seqNum == null) {
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(format)+1);
			}else if(!"Oracle".equals(dataStore)) {
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(seqNum));
			}

			seqNum = seqNum.trim();

			return seqNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
    }
    
    public String getNumberCodeSeqNo(String codeType,String seqName, String format, String tabName, String colName)throws Exception
    {
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[]{String.class,String.class,String.class,String.class};
				Object args[] = new Object[]{seqName,format,tabName,colName};
				return (String)RemoteMethodServer.getDefault().invoke("getSeqNo", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}


		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

        /*
         * ORACLE �ʿ� format�� ���̿� ��� subString �ϵ��� ���� ����.	2012-09-19 Suk
         * 
			ORACLE : select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(WTDOCUMENTNUMBER),'DOC-0'),5,8))+1,'00000000')  FROM WTDOCUMENTMASTER WHERE WTDOCUMENTNUMBER LIKE 'DOC-%';
			MSSQL : SELECT convert( bigint, SUBSTRING( ISNULL( MAX(WTDocumentNumber),'DOC-0' ), 5, len( ISNULL( MAX(WTDocumentNumber),'DOC-0' ) ) ) )+1 FROM wtadmin.WTDocumentMaster WHERE WTDocumentNumber LIKE 'DOC-%';

		*/

			StringBuffer sb = null;
			if("Oracle".equals(dataStore)) {
				
					sb = new StringBuffer()
					.append("select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(")
					.append(colName)
					.append("),?),?,?))+1,?) FROM ")
					.append(tabName)
					.append(" WHERE ")
					.append(colName)
					.append(" LIKE ?")
					.append(" and codeType = ?")
					.append("  ")
					;
				
			}else {
				sb = new StringBuffer()
				.append("SELECT convert( bigint, SUBSTRING( ISNULL( MAX(")
				.append(colName)
				.append("),?),?, len( ISNULL( MAX(")
				.append(colName)
				.append("),?) ) ) )+1 FROM ")
				.append(tabName)
				.append(" WHERE ")
				.append("codeType = ?")
				.append(" and ")
				.append(colName)
				.append(" LIKE ?");
				
			}

			//System.out.println(sb.toString());

			st = con.prepareStatement(sb.toString());
			
			st.setString(1, seqName);
			st.setInt(2, seqName.length()+1);
			
			
			if("Oracle".equals(dataStore)) {
				st.setInt(3, format.length());
				st.setString(4, format);
				st.setString(5, seqName+"%");
				st.setString(6, codeType);
			}else {
				st.setString(3, seqName);
				st.setString(4, codeType);
				st.setString(5, seqName+"%");
			}
			/*
			System.out.println("1 = "+seqName);
			System.out.println("2 = "+ seqName.length()+1);
			System.out.println("3 = "+format.length());
			System.out.println("4 = "+format);
			System.out.println("5 = "+seqName+"%");
			System.out.println("6 = "+codeType);
			*/
			rs = st.executeQuery();
			
			String seqNum = null;
			while (rs.next()) {
				seqNum = rs.getString(1);
			}
			if(seqNum == null) {
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(format)+1);
			}else if(!"Oracle".equals(dataStore)) {
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(seqNum));
			}

			seqNum = seqNum.trim();

			return seqNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
    }
    
    public  String getERPSeq(String sendTable)throws Exception
    {
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[]{String.class};
				Object args[] = new Object[]{sendTable};
				return (String)RemoteMethodServer.getDefault().invoke("getERPSeq", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}


		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

        
			StringBuffer sb = null;
			
			sb = new StringBuffer();

			
			sb.append("SELECT "+sendTable +"_SEQ.NEXTVAL from dual" );
			
			
			
			//System.out.println(sb.toString());
			st = con.prepareStatement(sb.toString());
		
			rs = st.executeQuery();
			
			String seqNum = "";
			while (rs.next()) {
				
				BigDecimal bd = rs.getBigDecimal(1);
				int count = bd.intValue();
				seqNum = String.valueOf(bd);
			}
			
			//System.out.println("getECOSeq seqNum1 = " + seqNum);
			String format = "00000000";
			if(seqNum == null) {
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(format)+1);
			}else{
		        DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(seqNum));
			}
			
			//System.out.println("seqNum2 = " + seqNum);
			return seqNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
    }

	public static void main(String[] args)throws Exception{
		SequenceDao.manager.getERPSeq("PART");
	}
}