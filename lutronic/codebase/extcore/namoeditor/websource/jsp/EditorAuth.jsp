<%@page contentType="text/html; charset=utf-8" %>
<%@page import="java.io.*"%>
<%@page import="java.net.*"%>
<%@page import="java.util.regex.PatternSyntaxException"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%	String ce_domain = ""; String ce_exp = ""; String ce_pm_exp = ""; String ce_serial_encrypt = ""; String ce_pm_key = ""; %>
<%@include file="EditorInformation.jsp"%>
<%@include file="Util.jsp"%>
<%@include file="SecurityTool.jsp"%>
<%!
	static public String getEncMD5(String str) {
	   String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			//e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	static public String getKey(String strExpire, String strSerial) {
		return strExpire + "|" + strSerial;
	}
%>
<%
	String check_uri = "http://crosseditor.namoeditor.co.kr/application/CELicenseCheck.php";
	String authHostInfo = "";
	/*
	String conkey = detectXSSEx(request.getParameter("connection"));
	
	if(conkey != null && conkey.equalsIgnoreCase("ServerGr")){
		authHostInfo = InetAddress.getLocalHost().getHostAddress();
	}
	else {
		authHostInfo =  request.getHeader("host");
	}
	*/
	authHostInfo =  request.getHeader("host");
	if(authHostInfo == null) 
			authHostInfo = request.getServerName();

	check_uri += "?editordomain=" + authHostInfo;
	check_uri += "&serial=" + ce_serial;
	check_uri += "&editorkey=" + ce_editorkey;
	String editorkey = request.getParameter("editorkey");	
	String conval = ce_domain + "|" + ce_use + "|" + ce_exp + "|" + authHostInfo;

	String exp_check = "true";
	if(ce_exp.length() > 0){
		String exp_date = new String(getBase64Decode(ce_exp), "ISO-8859-1");
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
		Date expDate = simpleDate.parse(exp_date);
		Date currentTime = new Date();
		String today = simpleDate.format(currentTime);
		Date currentDate = simpleDate.parse(today);
		if(currentDate.compareTo( expDate ) > 0) {
			exp_check = "false";
		}
	}else{
		exp_check = "false";
	}

	String strPluginModeKey = getEncMD5 (getKey (ce_pm_exp, ce_serial_encrypt));
	String strReturnVal = "";
	
	Date d1 = new Date();
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String formattedDate = df.format(d1);

	if (strPluginModeKey != null && strPluginModeKey.equalsIgnoreCase(ce_pm_key)) {
		String strPluginModeExpire = ce_pm_exp;
		strReturnVal = "{\"pm_exp\":\"" + strPluginModeExpire + "\",\"cur_date\":\"" + formattedDate + "\"}";
	}

	if (!"".equals(editorkey) && editorkey != null){
		if (editorkey.equalsIgnoreCase("ProductInfo")){
			String returnParam = ce_company + "|";
			returnParam += ce_use + "|";
			returnParam += ce_serial + "|";
			returnParam += ce_lkt;

			if(!"".equalsIgnoreCase(strReturnVal)){
				returnParam += "|" + strReturnVal;
			}

			out.println(detectXSSEx(returnParam));
		}else{
			if("false".equalsIgnoreCase(exp_check)){
				out.println("EXPIRE");
			}else if (createEncodeEditorKey(ce_editorkey).equalsIgnoreCase(editorkey)){
				out.println("SUCCESS");
			}else{
				out.println("NULL");
			}
		}
	}else{
		conval = ce_domain + "|" + exp_check + "|" + authHostInfo + "|" + createEncodeEditorKey(ce_editorkey);

		//conval = ce_domain + "|" + ce_exp + "|" + authHostInfo + "|" + createEncodeEditorKey(ce_editorkey);
		out.println(detectXSSEx(conval));
	}

%>