package com.e3ps.drawing.beans;

import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.enterprise.BasicTemplateProcessor;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTException;

import com.e3ps.common.beans.VersionData;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.VersionHelper;
import com.e3ps.part.util.PartUtil;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

public class EpmData extends VersionData{
	
	public EPMDocument epm;
	public String number;																							// 도면번호
	public String linkRefernceType;
	public String icon;
	public WTPart part;
	
	public EpmData(final EPMDocument epm) throws Exception {
		super(epm);
		this.epm = epm;
		this.number = epm.getNumber();
		this.icon = BasicTemplateProcessor.getObjectIconImgTag(epm);
	}
	//승인여부
	public boolean getApprove() throws Exception{
		boolean approve = false;
		WFItem wfItem =  WFItemHelper.service.getWFItem(epm);
		if( wfItem != null){
			approve = true;
		}
		return approve;
	}
	//
	public boolean getWgm() throws Exception{
		boolean wgm = false;
		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
		return wgm;
	}
	//수정 가능여부
	public boolean getUpdate() throws Exception{
		boolean update = false;
		boolean wgm = false;
		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
		if((State.INWORK).equals(epm.getLifeCycleState()) && VersionHelper.service.isLastVersion(epm) && ! wgm) {
			update = true;
		}
		return update;
	}
	//삭제 가능여부
	public boolean getDelete() throws Exception{
		boolean delete = false;
		boolean wgm = false;
		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
		if((State.INWORK).equals(epm.getLifeCycleState()) && VersionHelper.service.isLastVersion(epm) && ! wgm) {
			delete = true;
		}
		return delete;
	}
	//최신 객체 여부
	public boolean getLatest() throws Exception{
		boolean latest = false;
		if(!VersionHelper.service.isLastVersion(epm)) {
			latest = true;
		}
		return latest;
	}
	
	//Cad File
    public String getCADName() throws Exception{
    	
    	String cadFile ="&nbsp;";
    	ReferenceFactory rf = new ReferenceFactory();
    	ContentHolder holder = null;
    	holder = (ContentHolder)rf.getReference(oid).getObject();
    	QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
    	
    	ContentItem primaryFile = null;
    	while (result.hasMoreElements ()) {
    		primaryFile = (ContentItem) result.nextElement();
    	}
    	
    	EPMDocumentMaster master = (EPMDocumentMaster)epm.getMaster();
    	String cadName = master.getCADName();

    	String nUrl= WebUtil.getHost()+"jsp/common/DownloadGW.jsp?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(primaryFile);
    	cadFile = "<a href='"+nUrl+"'>&nbsp;"+ cadName +"</a>";
    	
    	return cadFile;
    }
    
    //PDF 파일
    public String getPDFFile(){
    	
    	String pdfFile ="";
    	try{
    		
    		if(!"PROE".equals(epm.getAuthoringApplication().toString())) return pdfFile;
			Representation representation = PublishUtils.getRepresentation(epm); 
			if(representation == null) return  pdfFile;
			representation = (Representation) ContentHelper.service.getContents(representation);
	        Vector contentList = ContentHelper.getContentList(representation);
	        for (int l = 0; l < contentList.size(); l++) {
	            ContentItem contentitem = (ContentItem) contentList.elementAt(l);
	            if( contentitem instanceof ApplicationData){
	            	ApplicationData drawAppData = (ApplicationData) contentitem;
	            	String fileName= EpmUtil.getPublishFile(epm, drawAppData.getFileName());
	            	if(drawAppData.getRole().toString().equals("SECONDARY") && drawAppData.getFileName().lastIndexOf("dxf")>0){
	            		String nUrl="/Windchill/jsp/common/DownloadPDF.jsp?&appOid="+CommonUtil.getOIDString(drawAppData)+"&appType=PROE&empOid="+this.oid;
	            		pdfFile = pdfFile + "<a href='"+nUrl+"'>&nbsp;"+fileName+"</a><br>";
	            	}
	            }
	        }
	        if(pdfFile.length()>0){
	        	pdfFile = pdfFile.substring(0,pdfFile.length()-1);
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return pdfFile;
    }
    
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public EPMDocument getEpm(){
    	return epm;
    }
    
    public void setEPM(EPMDocument epm){
    	this.epm = epm;
    }
    
    //주 도면의 파일 타입
	public String getCadType() {
		return epm.getAuthoringApplication().getDisplay(Message.getLocale());
	}
	
	//썸네일 작은사이즈
	public String getThum_mini() {
		String thum_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL_SMALL);
		if(thum_mini==null){ 
			thum_mini= WebUtil.getHost() + "netmarkets/images/blank24x16.gif"; 
		}
		return thum_mini;
	}
	//썸네일 큰사이즈
	public String getThum() {
		return FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL);
	}
	//
	public String getCopyTag() {
		Representable representable = PublishUtils.findRepresentable(epm);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
		String copyTag = "";
		if(representation!=null){
			copyTag = PublishUtils.getRefFromObject(representation);
		}
		return copyTag;
	}
	//상태
	public String getState() {
		return epm.getLifeCycleState().getDisplay(Message.getLocale());
	}
	//설명
	public String getDescription() {
		return StringUtil.checkNull(epm.getDescription());
	}
	
	public boolean isCreoDrawing(){
		
		return EpmUtil.isCreoDrawing(epm);
	}
	
	/**
	 * Creo의 드로잉은 경우 3D의 WTPArt
	 * @return
	 */
	public WTPart getDrawingPart() {
		
		try {
			
			if(isCreoDrawing()){
				String number= epm.getNumber();
				String version = epm.getVersionIdentifier().getValue();
				number = EpmUtil.getFileNameNonExtension(number);
				part = PartHelper.service.getPart(number, version);
			}else{
				part = DrawingHelper.service.getWTPart(epm);
			}
			
			return part;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//관련 파트
	public WTPart getPart() {
		try {
			
			return DrawingHelper.service.getWTPart(epm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//연관 파트 OID
	public String getpOid() {
		String pOid = "";
		try {
			
			if(part == null){
				part = getDrawingPart();
			}
			if(part != null) {
				pOid = CommonUtil.getOIDString(part);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pOid;
	}
	//연관 파트 번호
	public String getpNum() {
		String pNum = "";
		try {
			if(part == null){
				part = getDrawingPart();
			}
			
			if(part != null) {
				pNum = part.getNumber();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pNum;
	}
	//연관 파트 이름
	public String getpName() {
		String pName = "";
		try {
			if(part == null){
				part = getDrawingPart();
			}
			
			if(part != null) {
				pName = part.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pName;
	}
	
	//연관 파트 승인 유무
	public boolean isPApproved() {
		boolean isPApproved = false;
		try {
			if(DrawingHelper.service.getWTPart(epm) != null) {
				WTPart part = getPart();
				isPApproved = part.getLifeCycleState().toString().equals("APPROVED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPApproved;
	}
	
	//객체 생성 방식
	public String getApplicationType() {
		return epm.getOwnerApplication().toString();
	}
	
	// 
	public String getCadName() {
		return ((EPMDocumentMaster)epm.getMaster()).getCADName();
	}

	public String getCadNameUrl() {
		try {
			return getCADName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getLepmOid() {
		EPMDocument lepm;
		String lepmOid = "";
		try {
			lepm = (EPMDocument)ObjectUtil.getLatestObject((Master)epm.getMaster());
			lepmOid = lepm.getPersistInfo().getObjectIdentifier().toString();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return lepmOid;
	}

	public String getLinkRefernceType() {
		return linkRefernceType;
	}

	public void setLinkRefernceType(String linkRefernceType) {
		this.linkRefernceType = linkRefernceType;
	}
	//html 형식 변환 설명
	public String getvDesc() {
		return WebUtil.getHtml(epm.getDescription());
	}
	
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * true이면 동기화 버튼 활성화, false 이면 비활성
	 * @return
	 */
	public boolean isNameSyschronization(){
		
		boolean isDRW = this.epm.getDocType().toString().equals("CADDRAWING");
		if(!isDRW){
			return false;
		}
		
		
		boolean isLastest = isLatest();
		boolean isWorking = isWorking();
		String name = epm.getName();
		String epm2DNumber = EpmUtil.getNumberNonExtension(epm.getNumber());
		boolean isNumber = PartUtil.isChange(epm2DNumber); //true 이면 가도번 또는 더미 파일
		
		if(isNumber){
			return false;
		}
		//System.out.println("epm2DNumber="+epm2DNumber);
		EPMDocument epm3D = EpmSearchHelper.service.getDrawingToCad(epm2DNumber);
		
		if(epm3D == null){
			return false;
		}
		//System.out.println("epm3D Name="+epm3D.getName());
		//System.out.println("Name="+name);
		//System.out.println("epm3D Oid="+CommonUtil.getOIDString(epm3D));
		boolean isName = !name.equals(epm3D.getName());
		
		boolean isSys = isName && isLastest && isWorking;
		
		return isSys;
	}
	@Override
	public int hashCode(){
	    return (this.number.hashCode() + this.number.hashCode());
	}

	@Override
	public boolean equals(Object obj){
	    if(obj instanceof EpmData){
	    	EpmData temp = (EpmData) obj;
	        if(this.number.equals(temp.number)){
	            return true;
	        }
	    }
	    return false;
	}
}
