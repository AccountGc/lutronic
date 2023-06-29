
package com.e3ps.common.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

import wt.util.WTProperties;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;


public class FileRequest {

	String home ;
	String saveDirectory;
	int maxPostSize; 
	String encoding ;

  Hashtable parameters = new Hashtable();  // name - Vector of values
  Hashtable fhash = new Hashtable();	// name - ArrayList

  
  public FileRequest(HttpServletRequest request) throws IOException{

	home = WTProperties.getLocalProperties().getProperty("wt.home");
	saveDirectory= home +  File.separator +"temp"+ File.separator+"upload"; // 저장할 디렉토리 (절대경로)
	maxPostSize = Integer.MAX_VALUE ; // 20메가까지 제한 넘어서면 예외발생
	encoding = "utf-8";
	FileRenamePolicy policy = new DefaultFileRenamePolicy();

    // Sanity check values
    if (request == null)
      throw new IllegalArgumentException("request cannot be null");
    if (saveDirectory == null)
      throw new IllegalArgumentException("saveDirectory cannot be null");
    if (maxPostSize <= 0) {
      throw new IllegalArgumentException("maxPostSize must be positive");
    }

    // Save the dir
    File dir = new File(saveDirectory);

	if(!dir.exists()){
		dir.mkdir();
	}


    // Check saveDirectory is truly a directory
    if (!dir.isDirectory())
      throw new IllegalArgumentException("Not a directory: " + saveDirectory);

    // Check saveDirectory is writable
    if (!dir.canWrite())
      throw new IllegalArgumentException("Not writable: " + saveDirectory);

    // Parse the incoming multipart, storing files in the dir provided, 
    // and populate the meta objects which describe what we found
    MultipartParser parser =
      new MultipartParser(request, maxPostSize, true, true, encoding);

    // Some people like to fetch query string parameters from
    // MultipartRequest, so here we make that possible.  Thanks to 
    // Ben Johnson, ben.johnson@merrillcorp.com, for the idea.
    if (request.getQueryString() != null) {
      // Let HttpUtils create a name->String[] structure
      Hashtable queryParameters =
        HttpUtils.parseQueryString(request.getQueryString());
      // For our own use, name it a name->Vector structure
      Enumeration queryParameterNames = queryParameters.keys();
      while (queryParameterNames.hasMoreElements()) {
        Object paramName = queryParameterNames.nextElement();
        String[] values = (String[])queryParameters.get(paramName);
        Vector newValues = new Vector();
        for (int i = 0; i < values.length; i++) {
          newValues.add(values[i]);
        }
        parameters.put(paramName, newValues);
      }
    }

    Part part;
    while ((part = parser.readNextPart()) != null) {
      String name = part.getName();
      if (name == null) {
        throw new IOException(
          "Malformed input: parameter name missing (known Opera 7 bug)");
      }
      if (part.isParam()) {
        // It's a parameter part, add it to the vector of values
        ParamPart paramPart = (ParamPart) part;
        String value = paramPart.getStringValue();
        Vector existingValues = (Vector)parameters.get(name);
        if (existingValues == null) {
          existingValues = new Vector();
          parameters.put(name, existingValues);
        }
        existingValues.addElement(value);
      }
      else if (part.isFile()) {
        // It's a file part
        FilePart filePart = (FilePart) part;
        String fileName = filePart.getFileName();
        if (fileName != null) {
          filePart.setRenamePolicy(policy);  // null policy is OK
          // The part actually contained a file
          filePart.writeTo(dir);
          
        /****fasoo DRM 적용 - 2010.09.14 ******/
          /*
      	WTProperties props = WTProperties.getLocalProperties();
      	String wthome = props.getProperty("wt.home");
      	
      	
      	String DRMFilePath = wthome + File.separator + "temp"+File.separator + "upload"+File.separator;
        String DRMFileName = filePart.getFileName();
        
      	String strFsdinitDir =  wthome + File.separator +"codebase" + File.separator  + "fsdinit" + File.separator;
      	String FileName = DRMFileName;
      	String PackageFilePath = DRMFilePath + DRMFileName;
      	String extractFilePath = DRMFilePath + DRMFileName;
      	
      	System.out.println(strFsdinitDir);
      	System.out.println(PackageFilePath);
      	System.out.println(extractFilePath);

      	boolean bret = true;
      	WorkPackager oWorkPackager = new WorkPackager();
      	oWorkPackager.setOverWriteFlag(true);

      	bret = oWorkPackager.DoExtract(strFsdinitDir, 
      		                                "0000000000002014", 
      		                                PackageFilePath, 
      		                                extractFilePath);

      	if ( ! bret )
      	{
      		if(oWorkPackager.getLastErrorNum()!=29){ //29는 암호화 파일이 아닐경우에 해당됨
      			System.out.println("    ["+ oWorkPackager.getLastErrorNum()+"] "+oWorkPackager.getLastErrorStr());
      			System.out.println("<br> 복호화 중 오류가 발생하였습니다. 재시도하여 주시기 바랍니다.계속 같은 현상이 발생하면 관리자에게 연락하시기 바랍니다." + oWorkPackager.getLastErrorNum() + ":" + oWorkPackager.getLastErrorStr() + "");
      		}
      	}
      	*/
          
		  
          /***** MarkAny DRM 적용 Test - 2010.03.17 *****
          String DRMFilePath = filePart.getFilePath();
          String DRMFileName = filePart.getFileName();
          String DRMFile = DRMFilePath + DRMFileName;
          String DRMFileServerPath = dir.getPath();
          String DRMW = DRMFileServerPath + "\\" + DRMFileName;
          
          try{
        	  JNICipher JNICipher = new JNICipher ();
              //e.m_strSrcFilePath = DRMFileServerPath; // 복호화 할 원본 암호화 파일 경로
              //e.m_strDestFilePath = DRMFileServerPath; // 복호화 된 새로운 파일이 생길 경로

        	  //JNICipher.CompanyID   = "LGCHEM";     //PLM암복호화 Comapny_ID : LGCHEM암복호화 Comapny_ID , CPC : LGCHEMCPC
        	  int nRet = JNICipher.DecipherFile(DRMW);
        	  
              System.out.println("DecipherFile Return Value : " + nRet );
              if (nRet == 0){
            	  System.out.println("파일 복호화 성공!!" );
              }
              else{
            	  System.out.println("파일 복호화 실패!!" );
              }
          }
          catch (Exception e){
        	  System.out.println("DecipherFile Exception..." + e);
          }          
          /*******************************************************************/

		  // yhjang e3ps 수정된 부분
			  UploadedFile ufile = new UploadedFile(dir.toString(),
											   filePart.getFileName(),
											   fileName,
											   filePart.getContentType());

			  ArrayList ulist = (ArrayList)fhash.get(name);
			  if(ulist==null)ulist = new ArrayList();

			  ulist.add(ufile);
			  fhash.put(name,ulist);


        }
      }
    }
  }


  public Hashtable getParameters(){
	return parameters;
}
	
	
	/**
   * fhash 값을 이용하여 같은 이름의 파일들을 사용할 수 있도록 함 yhjang e3ps
   */

  	public String getFileLocation(String name){
		ArrayList list = (ArrayList)fhash.get(name);
		
		if(list==null || list.size()>1 )return null;

		UploadedFile ufile = (UploadedFile)list.get(0);
		return saveDirectory  + File.separator+ ufile.getFilesystemName();
  		
	}
/**
   * fhash 값을 이용하여 같은 이름의 파일들을 사용할 수 있도록 함 yhjang e3ps
   */
	public String[] getFileLocations(String name){
		
		ArrayList list = (ArrayList)fhash.get(name);
		
		if(list==null)return null;

		String[] result = new String[list.size()];

		for(int i=0; i< list.size(); i++){
			UploadedFile ufile = (UploadedFile)list.get(i);
			result[i] = saveDirectory  + File.separator+ ufile.getFilesystemName();
		}
		return result;
	}
/**
   * 생성 되어진 모든 파일을 삭제 yhjang e3ps
   */
	public void removeTempFiles(){
		Enumeration formNames= getFileNames();

		while(formNames.hasMoreElements()){
			String formName=(String)formNames.nextElement();

			ArrayList list = (ArrayList)fhash.get(formName);
		
			if(list==null)continue;

			for(int i=0; i< list.size(); i++){
				UploadedFile ufile = (UploadedFile)list.get(i);
				String fileLocation = saveDirectory  + File.separator+ ufile.getFilesystemName();
				File file = new File(fileLocation);
				file.delete();
			}
		}
	}


  /**
   * Returns the names of all the parameters as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no parameters.
   *
   * @return the names of all the parameters as an Enumeration of Strings.
   */
  public Enumeration getParameterNames() {
    return parameters.keys();
  }

  /**
   * Returns the names of all the uploaded files as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no file input 
   * fields on the form.  Any file input field that's left empty will result 
   * in a FilePart with null contents.  Each file name is the name specified
   * by the form, not by the user.
   *
   * @return the names of all the uploaded files as an Enumeration of Strings.
   */
  public Enumeration getFileNames() {
    return fhash.keys();
  }

  /**
   * Returns the value of the named parameter as a String, or null if 
   * the parameter was not sent or was sent without a value.  The value 
   * is guaranteed to be in its normal, decoded form.  If the parameter 
   * has multiple values, only the last one is returned (for backward 
   * compatibility).  For parameters with multiple values, it's possible
   * the last "value" may be null.
   *
   * @param name the parameter name.
   * @return the parameter value.
   */
  public String getParameter(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String value = (String)values.elementAt(values.size() - 1);
      return value;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the values of the named parameter as a String array, or null if 
   * the parameter was not sent.  The array has one entry for each parameter 
   * field sent.  If any field was sent without a value that entry is stored 
   * in the array as a null.  The values are guaranteed to be in their 
   * normal, decoded form.  A single value is returned as a one-element array.
   *
   * @param name the parameter name.
   * @return the parameter values.
   */
  public String[] getParameterValues(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String[] valuesArray = new String[values.size()];
      values.copyInto(valuesArray);
      return valuesArray;
    }
    catch (Exception e) {
      return null;
    }
  }


}
class UploadedFile {

  private String dir;
  private String filename;
  private String original;
  private String type;

  UploadedFile(String dir, String filename, String original, String type) {
    this.dir = dir;
    this.filename = filename;
    this.original = original;
    this.type = type;
  }

  public String getContentType() {
    return type;
  }

  public String getFilesystemName() {
    return filename;
  }

  public String getOriginalFileName() {
    return original;
  }

  public File getFile() {
    if (dir == null || filename == null) {
      return null;
    }
    else {
      return new File(dir + File.separator + filename);
    }
  }
}

