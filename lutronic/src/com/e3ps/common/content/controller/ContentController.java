package com.e3ps.common.content.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fv.uploadtocache.UploadToCacheHelper;
import wt.httpgw.HTTPServletRequest;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;


@Controller
@RequestMapping("/content")
public class ContentController {
	
	@Description(value = "파일 다운로드")
	@GetMapping(value = "/download")
	public ResponseEntity<byte[]> download(@RequestParam String oid) {
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;
		
		try {
			ApplicationData data = (ApplicationData) CommonUtil.getObject(oid);
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			
			bytes = byteArrayOutputStream.toByteArray();
			String name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
			
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentLength(bytes.length);
			headers.setContentDispositionFormData("attachment", name);
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	@Description(value = "zip파일 다운로드")
	@GetMapping(value = "/downloadZIP")
    public ResponseEntity<byte[]> downloadZIP(@RequestParam List<String> oids) {
        HttpHeaders headers = new HttpHeaders();
        byte[] zipBytes = null;
        String name = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            for (String oid : oids) {
	            WTDocument document = (WTDocument) CommonUtil.getObject(oid);
	            QueryResult result = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
	            
	            while (result.hasMoreElements()) {
	                ApplicationData data = (ApplicationData) result.nextElement();
	                InputStream is = ContentServerHelper.service.findLocalContentStream(data);
	
	                byte[] buffer = new byte[1024];
	                int length;
	                
	                ZipEntry zipEntry = new ZipEntry(data.getFileName());
	                zipOutputStream.putNextEntry(zipEntry);
	                
	                ByteArrayOutputStream entryByteArrayOutputStream = new ByteArrayOutputStream();
	
	                while ((length = is.read(buffer)) != -1) {
	                    entryByteArrayOutputStream.write(buffer, 0, length);
	                }
	
	                entryByteArrayOutputStream.close();
	                
	                byte[] entryBytes = entryByteArrayOutputStream.toByteArray();
	                zipOutputStream.write(entryBytes);
	
	                zipOutputStream.closeEntry();
	            }
	            
	            
	            result.reset();
				result = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
	                InputStream is = ContentServerHelper.service.findLocalContentStream(data);
	
	                byte[] buffer = new byte[1024];
	                int length;
	                
	                ZipEntry zipEntry = new ZipEntry(data.getFileName());
	                zipOutputStream.putNextEntry(zipEntry);
	                
	                ByteArrayOutputStream entryByteArrayOutputStream = new ByteArrayOutputStream();
	
	                while ((length = is.read(buffer)) != -1) {
	                    entryByteArrayOutputStream.write(buffer, 0, length);
	                }
	
	                entryByteArrayOutputStream.close();
	                
	                byte[] entryBytes = entryByteArrayOutputStream.toByteArray();
	                zipOutputStream.write(entryBytes);
	
	                zipOutputStream.closeEntry();
				}
            }

            zipOutputStream.close();

            zipBytes = byteArrayOutputStream.toByteArray();
            
            name = URLEncoder.encode("downloaded_files.zip", "UTF-8").replaceAll("\\+", "%20");

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipBytes.length);
            headers.setContentDispositionFormData("attachment", name);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }
	
	@Description(value = "첨부 파일 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/list")
	public JSONObject list(HttpServletRequest param) throws Exception {
		String oid = (String) param.getParameter("oid");
		String roleType = (String) param.getParameter("roleType");
		JSONObject list = CommonContentHelper.manager.list(oid, roleType);
		return list;
	}
	
	@Description(value = "첨부 파일 업로드")
	@ResponseBody
	@PostMapping(value = "/upload")
	public JSONObject upload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.manager.upload(request);
	}
	
	@Description(value = "첨부 파일 삭제(화면에서의 제거)")
	@ResponseBody
	@PostMapping(value = "/delete")
	public JSONObject delete(HttpServletRequest param) throws Exception {
		JSONObject result = new JSONObject();
		result.put("status", 0);
		result.put("result", "ok");
		return result;
	}
	
	/** 첨부파일 insert / update
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/includeAttachFile")
	public ModelAndView includeAttachFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView();
		
		// command : 첨부파일 Action type 
		// insert : 신규 등록
		// update : 수정
		
		String form = "";
		String command = "";
		String oid = "";
		String type = "";
		String ac = "";

		form = request.getParameter("form");
		command = request.getParameter("command");
		oid = request.getParameter("oid");
		type = request.getParameter("type");
		ac = request.getParameter("count");
		
		model.addObject("form", form);
		model.addObject("command", command);
		
		String isWG = request.getParameter("isWG");
		model.addObject("isWG", isWG);
		
		// command 가 update시에 update 할 ContentHolder OID

		// type : 첨부파일 타입
		// p or primary : (Primary)주요문서 파일
		// s or secondary : (Secondary)참조문서 파일
		
		if ( "p".equalsIgnoreCase(type) || "primary".equalsIgnoreCase(type)) type = "primary";
		else if( "document".equalsIgnoreCase(type)){
			type = "document";
		}
		else type = "secondary";
		
		model.addObject("type", type);
		
		// (Secondary)참조문서 파일일 경우 첨부할수 있는 갯수
		int attacheCount = 0;

		if(ac!=null && ac.length()>0){
			 attacheCount = Integer.parseInt(ac);
		}
		
		model.addObject("attacheCount", attacheCount);
		
		// (Secondary)참조문서 파일일 경우 설명 사용여부
		String desc = request.getParameter("desc");
		boolean canDesc = false;
		if ( "t".equalsIgnoreCase(desc) || "true".equalsIgnoreCase(desc) ) canDesc = true;
		
		model.addObject("canDesc", canDesc);
		
		ContentItem primaryFile = null;
		ContentHolder holder = null;
		if ( "primary".equals(type) && "update".equalsIgnoreCase(command) ) {
			ReferenceFactory rf = new ReferenceFactory();
			holder = (ContentHolder)rf.getReference(oid).getObject();
			if ( holder instanceof FormatContentHolder ) {
				QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
				if (result.hasMoreElements ()) {
					primaryFile = (ContentItem) result.nextElement ();
				}
			} else if ( holder instanceof ContentHolder ) {	//경쟁 제품 용(ContentHolder 일 경우)
				QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
				if (result.hasMoreElements ()) {
					primaryFile = (ContentItem) result.nextElement ();
				}
			}
			model.addObject("primaryFile", primaryFile);
		}
		
		if ( primaryFile != null ) {
			//String downURL = "";
			String nUrl = "";
			if(primaryFile instanceof ApplicationData){
				//downURL = ContentHelper.getDownloadURL ( holder , (ApplicationData)primaryFile ).toString();
				nUrl = WebUtil.getHost()+"servlet/DownloadGW?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(primaryFile);
				nUrl = "<a href=" + nUrl + ">&nbsp;";
				if("true".equals(isWG)){
					nUrl = nUrl + ((EPMDocument)holder).getCADName() + "</a>";
				}else{
					nUrl = nUrl + ((ApplicationData)primaryFile).getFileName() + "</a>";
				}
			}else{
				nUrl =  ((URLData)primaryFile).getUrlLocation ();
			}
			model.addObject("nUrl", nUrl);
		}

		List<Map<String,String>> secondaryList = new ArrayList<Map<String,String>>();
		QueryResult secondaryFiles =null;
		int deleteFileCnt = 0;
		if ( "secondary".equals(type) && "update".equalsIgnoreCase(command) ) {
			ReferenceFactory rf = new ReferenceFactory();
			holder = (ContentHolder)rf.getReference(oid).getObject();
			secondaryFiles = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.SECONDARY );
		}
		
		while(secondaryFiles!=null && secondaryFiles.hasMoreElements()) {
			ContentItem item = (ContentItem) secondaryFiles.nextElement ();
			String name = "";
			if (item instanceof URLData) {
				URLData url = (URLData) item;
				name = url.getUrlLocation ();
			} else if (item instanceof ApplicationData) {
				ApplicationData file = (ApplicationData) item;
				name = file.getFileName ();
			}
			
			Map<String,String> secondaryMap = new HashMap<String,String>();
			
			secondaryMap.put("name", name);
			secondaryMap.put("oid", item.getPersistInfo().getObjectIdentifier().toString());
			if(canDesc) {
				secondaryMap.put("description", item.getDescription());
			}
			
			secondaryList.add(secondaryMap);
			
		}
		model.addObject("secondaryList", secondaryList);
		
		model.setViewName("include:/portal/include_attacheFile");
		
		return model;
	}
	
	@RequestMapping("/includeAttachFileView")
	public ModelAndView includeAttachFileView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String type = request.getParameter("type");
		String description = request.getParameter("description");
		
		boolean primary = false;
		boolean isAddRole = false;
		if(StringUtil.checkString(type) && "P".equals(type.toUpperCase())) {
			primary = true;
		}
		//System.out.println("type =" + type);
		if (StringUtil.checkString(type) &&( type.equals("ECR") || type.equals("ECO") )){
			isAddRole = true;
		}
		
		Object obj = CommonUtil.getObject(oid);
		ContentHolder holder = ContentHelper.service.getContents((ContentHolder)obj);
		ContentRoleType roleType = primary ? ContentRoleType.PRIMARY : ContentRoleType.SECONDARY;
		if(isAddRole){
		
			roleType =  ContentRoleType.toContentRoleType(type);
		}
		QueryResult qr = ContentHelper.service.getContentsByRole (holder ,roleType );
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();

		while(qr.hasMoreElements()) {
			ContentItem item = (ContentItem) qr.nextElement ();
			
			if(item != null) {
			
				ApplicationData data = (ApplicationData)item;
				
				String url = "/Windchill/jsp/common/DownloadGW.jsp?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(data);
				
				Map<String,String> map = new HashMap<String, String>();
				
				map.put("name", data.getFileName());
				map.put("oid", data.getPersistInfo().getObjectIdentifier().toString());
				map.put("size", String.valueOf(data.getFileSize()));
				map.put("url", url);
				
				if(StringUtil.checkString(description)) {
					if(description.equals(item.getDescription())) {
						list.add(map);
					}
				}else {
					if(item.getDescription() == null) {
						list.add(map);
					}
				}
			}
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("list", list);
		model.setViewName("empty:/portal/include_attachFileView");
		return model;
	}
	
	@RequestMapping("/include_AttachSelect")
	public ModelAndView include_AttachSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oid = request.getParameter("oid");
		String type = request.getParameter("type");
		String isWG = request.getParameter("isWG");
		String description = request.getParameter("description");
		
		Object obj = CommonUtil.getObject(oid);
		
		ContentHolder holder = ContentHelper.service.getContents((ContentHolder)obj);
		
		if("p".equals(type)) {
			ContentItem item = null;
			QueryResult result = ContentHelper.service.getContentsByRole ((ContentHolder)obj, ContentRoleType.PRIMARY );
			while(result.hasMoreElements()) {
				item = (ContentItem) result.nextElement ();
			}
			
			if(item != null) {
				ApplicationData pAppData = (ApplicationData)item;
				String nUrl = "/Windchill/jsp/common/DownloadGW.jsp?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(pAppData);
				String pName = pAppData.getFileName();
				String icon = CommonUtil.getContentIconStr(item);
				String aoid = CommonUtil.getOIDString(pAppData);
			}
		}else {
			Vector<?> vec = ContentHelper.getContentList(holder);
			for ( int i = 0 ; i < vec.size() ; i++ ) { 
				ApplicationData appData = (ApplicationData)vec.get(i);
				String nUrl="/Windchill/jsp/common/DownloadGW.jsp?holderOid=" + CommonUtil.getOIDString(holder)+ "&appOid=" + CommonUtil.getOIDString(appData);
				String name = appData.getFileName();
				String aoid = CommonUtil.getOIDString(appData);
			}
		}
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/include_AttachSelect");
		model.addObject("oid", oid);
		model.addObject("type", type);
		model.addObject("isWG", isWG);
		model.addObject("description",description);
		return model;
	}
	
	/**
	 * <pre>
	 * @description  파일 업로드 컴포넌트 로드 (CREATE, UPDATE)
	 * @author dhkim
	 * @date 2016. 3. 10. 오후 1:43:06
	 * @method includeAttachFiles
	 * @param request
	 * @param response
	 * @return ModelAndView
	 * @throws Exception 
	 * </pre>
	 */
	@RequestMapping("/includeAttachFiles")
	public ModelAndView includeAttachFiles(@RequestParam Map<String,String> reqMap) throws WTException {
		
		String btnId = StringUtil.checkReplaceStr(reqMap.get("btnId"), "none");
		String location = StringUtil.checkReplaceStr(SessionHelper.getLocale().toString().toUpperCase(), "KO").substring(0,2);
		String type = StringUtil.checkReplaceStr(reqMap.get("type"), "SECONDARY").toUpperCase();
		//System.out.println("type="+(StringUtil.checkNull(type)));
		String oid = StringUtil.checkNull(reqMap.get("oid"));
		String description = StringUtil.checkNull(reqMap.get("description"));
		String formId = StringUtil.checkNull(reqMap.get("formId"));
		String savePath = null;
		try {
			savePath = WTProperties.getServerProperties().getProperty("wt.temp");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String m_fileFullPath = null;

		String url = UploadToCacheHelper.service.getUploadToCacheURL();
        URL hostUrl;
		try {
			hostUrl = new URL(url);
			url = "http://" + hostUrl.getHost();
			
		} catch (MalformedURLException e) {
			throw new WTException(e);
		}
		
		ContentRoleType roleType = ContentRoleType.toContentRoleType(type);
		
        ContentHolder holder = null;
        QueryResult result = new QueryResult();
        
        if (oid != null && !oid.isEmpty()) {
            holder = (ContentHolder) CommonUtil.getObject(oid);
            result = wt.content.ContentHelper.service.getContentsByRole(holder, roleType);
        }
        
        StringBuffer uploadedList = new StringBuffer();
        
        if(ContentRoleType.SECONDARY == roleType){
        	uploadedList.append("[");
        }
        
        
        List<ContentItem> items = new ArrayList<>();
        while (result.hasMoreElements()) {
           ContentItem item = (ContentItem) result.nextElement();
           /*
           if ("".equals(description) && description.isEmpty()) {
              items.add(item);
           } else {
              if (description.equals(item.getDescription())) {
                 items.add(item);
              }
           }
           */
           if(StringUtil.checkString(description)) {
				if(description.equals(item.getDescription())) {
					items.add(item);
				}
			}else {
				if(item.getDescription() == null) {
					items.add(item);
				}
			}
        }
        int cnt = 0;
        for (ContentItem item : items) {
          if (cnt != 0) {
        	  uploadedList.append(",");
          }
          ApplicationData appData = (ApplicationData) item;

          String delocId = item.getPersistInfo().getObjectIdentifier().toString();

          String fileName = appData.getFileName();
          fileName = fileName.replaceAll("'", "\\\\'");
          m_fileFullPath = savePath + "\\" + fileName;
          
          System.out.println("fileName="+fileName);
          System.out.println("savePath="+savePath);
          System.out.println("m_fileFullPath="+m_fileFullPath);
          
          long fileSize = appData.getFileSize();
          String fileExtType = fileName.split(".").length > 1 ? fileName.split(".")[1] : "";
          uploadedList.append("{");
          uploadedList.append("id : '" + type + description + cnt + "', ");
          uploadedList.append("name : '" + fileName + "', ");
          uploadedList.append("type : '" + fileExtType + "', ");
          uploadedList.append("saveName : '" + fileName + "', ");
          uploadedList.append("fileSize : '" + fileSize + "', ");
          uploadedList.append("uploadedPath : 'servlet/AttachmentsDownloadDirectionServlet?oid=" + CommonUtil.getFullOIDString(holder) + "&cioids=" + CommonUtil.getOIDString(appData)
                + "&role=" + type + "', ");
          uploadedList.append("thumbUrl : '', ");
          uploadedList.append("roleType : '" + type +"', ");
          uploadedList.append("formId : '" + formId + "', ");
          uploadedList.append(description + "delocId : '" + delocId + "', ");
          uploadedList.append("description : '" + description + "', ");
          uploadedList.append("cacheId : '' ");
          uploadedList.append("}");
          cnt++;
       }
       
       if(ContentRoleType.SECONDARY == roleType){
    	   uploadedList.append("]");
       }
       
       WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
       String userName = user.getName();
       String componentName = type + "_" + description;
		ModelAndView model = new ModelAndView();
		model.setViewName("empty:/common/content/includeAttachFiles");
		model.addObject("userName", userName);
		model.addObject("location", location);
		model.addObject("type", type);
		model.addObject("uploadedList", uploadedList.toString());
		model.addObject("description", description);
		model.addObject("formId", formId);
		model.addObject("componentName" , componentName);
		model.addObject("m_fileFullPath" , m_fileFullPath);
		model.addObject("url", url);
		model.addObject("btnId", btnId);
		
		return model;
	}
}
