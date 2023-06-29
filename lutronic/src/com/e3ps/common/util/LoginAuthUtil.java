package com.e3ps.common.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.oreilly.servlet.Base64Encoder;

import wt.util.WTProperties;

/** 
 * AuthUtil ������� Utility Class
 * @author  Y. J. JEON
 * @version 1.0
 **/
public class LoginAuthUtil{
        
    private static boolean VERBOSE              = true;
    private static boolean DEFAULT_ENCODE_FLAG  = true;
    private static String HOSTNAME              = "";
    private static String WEBAPPNAME            = "";
    private static String CODEBASE              = "";
    private static WTProperties wtproperties    = null;
    private static Properties IEPROP            = new Properties();
    
    private static String JNDIADAPTER           = "";
    private static String LDAPHOST              = "";
    private static String LDAPPORT              = "";
    private static String LDAPURL               = "";
    private static String LDAPDN                = "";
    private static String LDAPPW                = "";
    private static String SEARCHBASE1            = "";
    private static String SEARCHBASE2            = "";
    
    private static boolean isInitialized        = false;
    private static DirContext ctx               = null;
   
    private static String host =  "";
    
    static{
    	Config conf = ConfigImpl.getInstance();
    	host =  conf.getString("HTTP.HOST.URL");
    }
            
    public static void initialize(){
        try{
            Hashtable userHash  = new Hashtable();          
            wtproperties    = WTProperties.getLocalProperties();
            HOSTNAME        = wtproperties.getProperty("java.rmi.server.hostname","");
            WEBAPPNAME      = wtproperties.getProperty("wt.webapp.name","");
            CODEBASE        = wtproperties.getProperty("wt.server.codebase","");

            // Windchill Properties
            JNDIADAPTER = wtproperties.getProperty("wt.federation.org.defaultAdapter","com.ptc.Ldap");
            
            // Customizing Properties
            //e3ps.common.jdf.config.Config myConfig = e3ps.common.jdf.config.ConfigImpl.getInstance();
            
            //DEFAULT_ENCODE_FLAG = (new Boolean(myConfig.getString("auth.isauth"))).booleanValue();
            LDAPPORT= "389";//myConfig.getString("ldap.port");

            // InfoEngine Properties
            IEPROP.load( new FileInputStream(wtproperties.getProperty("wt.federation.ie.propertyResource")) );
            String ldapSeeAlso  = IEPROP.getProperty("seeAlso");

            int startIdx    = ldapSeeAlso.indexOf("/")+2;
            int endIdx      = ldapSeeAlso.indexOf("@");

            String ieIdPw   = ldapSeeAlso.substring(startIdx, endIdx);

            LDAPDN          = ieIdPw.substring(0, ieIdPw.indexOf(":"));
			//System.out.println("----->>>"+LDAPDN);
            LDAPPW          = "ldapadmin";//ieIdPw.substring(ieIdPw.indexOf(":")+1);
            ldapSeeAlso     = ldapSeeAlso.substring(endIdx+1);
            endIdx          = ldapSeeAlso.indexOf("/");
            LDAPHOST        = ldapSeeAlso.substring(0,endIdx);
            LDAPURL         = "ldap://"+LDAPHOST+":"+LDAPPORT;
//            SEARCHBASE      = "ou=people,"+ldapSeeAlso.substring(endIdx+1);
			SEARCHBASE1      = "ou=people,cn=AdministrativeLdap,cn=Windchill_10.1,o=ptc";
			SEARCHBASE2      = "ou=people,cn=EnterpriseLdap,cn=Windchill_10.1,o=ptc";

            // Setting the DirContext parameters
            userHash.put( Context.SECURITY_PRINCIPAL, LDAPDN );
            userHash.put( Context.SECURITY_CREDENTIALS, LDAPPW );
            userHash.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");                       
            userHash.put(Context.PROVIDER_URL, LDAPURL);
            //-- inform the LDAP provider that the value of 'loginAllowedTimeMap'           
            
            if( VERBOSE ){
                //System.out.println("### AuthUtil initialize ###");
                //System.out.println("==HOSTNAME : "+HOSTNAME);
                //System.out.println("==WEBAPPNAME : "+WEBAPPNAME);
                //System.out.println("==CODEBASE : "+CODEBASE);
                //System.out.println("==SEARCHBASE1 : "+SEARCHBASE1);
                //System.out.println("==SEARCHBASE2 : "+SEARCHBASE2);
                //System.out.println("==LDAPDN : "+LDAPDN);
                //System.out.println("==LDAPPW : "+LDAPPW);
                //System.out.println("==JNDIADAPTER : "+JNDIADAPTER);
                //System.out.println("==LDAPURL : "+LDAPURL);
                //System.out.println("==DEFAULT_ENCODE_FLAG : "+DEFAULT_ENCODE_FLAG);
            }
            
            try{                    
                ctx             = new InitialDirContext(userHash);                  
                isInitialized   = true;
                
                if (VERBOSE) {
                    //System.out.println("Jinyoung LdapService initialized...");
                }
            }catch( NamingException ne ){
                if( ctx!=null )
                    ctx.close();                
                ne.printStackTrace();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }       
    }
    
    public static void main(String args[])throws Exception{
    	
    	getUserInfo("wcadmin");
    	//System.out.println("wcadmin = " + getUserPassword("wcadmin"));
    	
    }
    
    
    /** 
    * ����� ������ Ldap���� ���� �о� ���δ�.
    * @param    userId      (����ڻ��)     
    * @return   Attributes
    * @since    2005.02
    */
    public static Attributes getUserInfo(String userId){

        initialize();

        Attributes attrs    = null;
        String filter       = "uid="+userId+", "+SEARCHBASE1;

        try{
            attrs   = ctx.getAttributes(filter);

			if(attrs == null) {
				filter       = "uid="+userId+", "+SEARCHBASE2;
				attrs   = ctx.getAttributes(filter);
			}

            if( VERBOSE ){
                try{
                    //System.out.println("### AuthUtil getUserInfo ###");
                    //System.out.println("==uid : "+attrs.get("uid").get());
                    //System.out.println("==sn : "+attrs.get("sn").get());
                    ////System.out.println("==mail : "+attrs.get("mail").get());
                    //System.out.println("==userPassword : "+attrs.get("userPassword").get());                
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                ctx.close();
            }catch(NamingException ne){}
        }
        return attrs;
    }
    
    
    /** 
    * ������� Windchill password�� ��ȯ�Ѵ�.(�ӽ�Password�� labeledUri�� ��ü��)
    * @param    userId      (����ڻ��) 
    * @param    encodeFlag  (��ȣȭ ��뿩��)
    * @return   String type�� ������� ���� Password ��(��ȣȭ���� ����)
    * @since    2005.02
    */
    public static String getUserPassword(String userId, boolean encodeFlag)
    throws Exception{       

        initialize();

        String password     = "";
        try{
            Attributes attrs    = getUserInfo(userId);
            if( attrs!=null ){
                String bulkString   = "http://";
                password    = attrs.get("userPassword")==null?"":attrs.get("userPassword").get().toString();                
                if(password.indexOf(bulkString)>-1){
                    password    = password.substring(bulkString.length());                  
                }
                if( encodeFlag )
                    password    = Base64.decodeToString(password);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return password;
    }
    
    
    /** 
    * ������� Windchill password�� ��ȯ�Ѵ�.
    * (Default�� ��ȣȭ ó���� �߰���).
    * @param    userId      (����ڻ��)     
    * @return   String type�� ������� ���� Password ��(��ȣȭ���� ����)
    * @since    2005.02
    * @see      getAuthorization(String userId, boolean encodeFlag)
    */
    public static String   getUserPassword(String userId){      
        String password     = "";
        try{
            password    = LoginAuthUtil.getUserPassword(userId, DEFAULT_ENCODE_FLAG);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return password;
    }
    
    
    /** 
    * ������� ID�� ��ȣ�� Ldap�� ���� ���Ͽ� ��ȿ������ �˻�   
    * @param    userId      (����ڻ��)     
    * @param    passwd      (�����Password)       
    * @return   String type�� ������� ���� Password ��(��ȣȭ���� ����)
    * @since    2005.02
    * @see      getAuthorization(String userId, boolean encodeFlag)
    */
//    public static boolean validatePassword(String userId, String passwd) throws Exception {
//        
//        initialize();
//        
//        boolean isValidUser = false;
//		String semUserObjectId = "uid="+userId+"," + SEARCHBASE1;
//        
//        try {           
//            //System.out.println("validatePassword ... " + userId + "/" + passwd);
//            SearchControls ctls = new SearchControls();
//            ctls.setSearchScope( SearchControls.OBJECT_SCOPE );
//            ctls.setReturningAttributes( new String[0] );
//            
//        
//            String passStr  = "userPassword=" + (DEFAULT_ENCODE_FLAG ? passwd : passwd);
//            
//            if( VERBOSE ){
//                //System.out.println("### semUserObjectId - " + semUserObjectId);
//                //System.out.println("### validatePassword - "+passStr);
//                //System.out.println("### validatePassword - SearchControls : "+ctls);
//                
//            }
//            NamingEnumeration sre = ctx.search( semUserObjectId, passStr, ctls );           
//
//			//System.out.println("------->>>>>>>"+sre);
//			
//			if ( sre != null && sre.hasMoreElements()){
//                isValidUser = true;
//                SearchResult sResult    = (SearchResult)sre.nextElement();                  
//                Attributes attrs        = (Attributes)sResult.getAttributes();
//                ctx.modifyAttributes(semUserObjectId,2,attrs);
//            }          
//            
//            
//			//System.out.println("------->>>>>>>"+isValidUser);
//        }catch(NamingException ne){
//
//			SearchControls ctls = new SearchControls();
//			ctls.setSearchScope( SearchControls.OBJECT_SCOPE );
//			ctls.setReturningAttributes( new String[0] );
//
//			String passStr  = "userPassword=" + (DEFAULT_ENCODE_FLAG ? passwd : passwd);
//			semUserObjectId = "uid="+userId+"," + SEARCHBASE2;
//
//			try{
//
//				NamingEnumeration sre = ctx.search( semUserObjectId, passStr, ctls );
//				//System.out.println("-------###>>>>>>>"+sre);
//				
//				if ( sre != null && sre.hasMoreElements()){
//				isValidUser = true;
//				SearchResult sResult    = (SearchResult)sre.nextElement();                  
//				Attributes attrs        = (Attributes)sResult.getAttributes();
//				ctx.modifyAttributes(semUserObjectId,2,attrs);
//				}
//				
//				//System.out.println("-------###>>>>>>>"+isValidUser);
//				
//	        }catch(NamingException ne2){
//				ne2.printStackTrace();
//				isValidUser = false;
//			}
//
//        }finally{
//            
//        }
//        
//        return isValidUser;
//    }
    
 public static boolean validatePassword(String userId, String passwd) throws Exception {
     boolean isAuth = false;
     String param = userId + ":" + passwd;
     ////System.out.println("validatePassword..param =" + param);
	 try {
		 
			URL url = new URL(host + "/Windchill/jsp/loginOK.jsp");
			
			//System.out.println("validatePassword..1");
			String encoding = Base64Encoder.encode(param);
			//System.out.println("validatePassword..2");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			InputStream content = (InputStream) connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					content));
			String line = "kkkk";
			//System.out.println("validatePassword..3");
			if ((line = in.readLine()) != null) {
				//System.out.println(line);
				
			}
			//System.out.println("validatePassword..4");
			 isAuth = "OK".equalsIgnoreCase(line);
			 
			 if(isAuth){
				 //System.out.println("auth sucess..");
			 }else{
				 //System.out.println("auth fail" + passwd);
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(host + "  ,log in fail== " + param);
		}
	 
	 	return isAuth;
    }
    
    public static void getAuthentication(String id, String pw){
        try{
            Hashtable userHash  = new Hashtable();

            // Setting the DirContext parameters
            userHash.put( Context.SECURITY_PRINCIPAL, LDAPDN );
            userHash.put( Context.SECURITY_CREDENTIALS, LDAPPW );
            userHash.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");                       
            userHash.put(Context.PROVIDER_URL, LDAPURL);
            
            //System.out.println("### AuthUtil initialize ###");
            //System.out.println("==LDAPDN : "+LDAPDN);
            //System.out.println("==LDAPPW : "+LDAPPW);
            //System.out.println("==LDAPURL : "+LDAPURL);
            
            try{                    
                ctx             = new InitialDirContext(userHash);                  
                isInitialized   = true;             
                //System.out.println("############## LdapService initialized...");
            
            }catch( NamingException ne ){
                if( ctx!=null )
                    ctx.close();                
                ne.printStackTrace();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    

	/** 
    * ����� ������ �ش��ϴ� Ldap ������ �����Ѵ�.
    * @param    userId      (����ڻ��)     
    * @return   String
    * @since    2009.03
    */
    public static String getLdapInfo(String userId){

        initialize();

        Attributes attrs    = null;
		String SEARCHBASE = SEARCHBASE1;
        String filter       = "uid="+userId+", "+SEARCHBASE;

        try{
            attrs   = ctx.getAttributes(filter);

			if(attrs == null) {
				SEARCHBASE = SEARCHBASE2;
				filter       = "uid="+userId+", "+SEARCHBASE;
				attrs   = ctx.getAttributes(filter);
			}

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                ctx.close();
            }catch(NamingException ne){}
        }
        return SEARCHBASE;
    }

}