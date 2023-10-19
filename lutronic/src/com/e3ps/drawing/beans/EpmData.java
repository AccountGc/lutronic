package com.e3ps.drawing.beans;

import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.service.PartHelper;

import lombok.Getter;
import lombok.Setter;
import wt.enterprise.BasicTemplateProcessor;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.lifecycle.State;
import wt.part.WTPart;

@Getter
@Setter
public class EpmData {

//	public EPMDocument epm;
	public String oid; // 도면번호
	public String name; // 도면번호
	public String number; // 도면번호
	public String linkRefernceType;
	public String icon;
	public WTPart part;
	public String cadType;
	public String creator;
	private String modifier;
	public String createDate;
	public String modifyDate;
	public boolean isNameSyschronization = true;
	public boolean isUpdate = false;
	public boolean isLatest = false;
	public String location;
	public String state;
	private String stateDisplay;
	public String cadName;
	public String pNum;
	public String applicationType;
	private String version;
	private String pdf;
	private String step;
	private String dxf;

	public EpmData(EPMDocument epm) throws Exception {
//		super(epm);
//		setEpm(epm);
		setOid(CommonUtil.getOIDString(epm));
		setName(epm.getName());
		setNumber(epm.getNumber());
		setIcon(BasicTemplateProcessor.getObjectIconImgTag(epm));
		setCadType(epm.getDocType().toString());
		setCreator(epm.getCreatorFullName());
		setModifier(epm.getModifierFullName());
		setCreateDate(DateUtil.getDateString(epm.getCreateTimestamp(), "a"));
		setModifyDate(DateUtil.getDateString(epm.getModifyTimestamp(), "a"));
		// true이면 동기화 버튼 활성화, false 이면 비활성
		boolean isDRW = epm.getDocType().toString().equals("CADDRAWING");
		if (!isDRW) {
			setNameSyschronization(false);
		}
		// 수정 가능여부
		boolean wgm = false;
		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
		if ((State.INWORK).equals(epm.getLifeCycleState()) && CommonUtil.isLatestVersion(epm) && !wgm) {
			setUpdate(true);
		}
		// 최신객체여부
		setLatest(CommonUtil.isLatestVersion(epm));

		setLocation(StringUtil.checkNull(epm.getLocation()).replaceAll("/Default", ""));
		setState(epm.getLifeCycleState().toString());
		setStateDisplay(epm.getLifeCycleState().getDisplay());
		EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
		String cadName = master.getCADName();
		setCadName(cadName);
		setAttach(epm);
		// Creo의 드로잉은 경우 3D의 WTPArt
		if (EpmUtil.isCreoDrawing(epm)) {
			String number = epm.getNumber();
			String version = epm.getVersionIdentifier().getValue();
			number = EpmUtil.getFileNameNonExtension(number);
			part = PartHelper.manager.getPart(number, version);
		} else {
			part = DrawingHelper.service.getWTPart(epm);
		}

		// 연관 파트 번호
		if (part == null) {
			part = getDrawingPart();
		}
		if (part != null) {
			pNum = part.getNumber();
		}
		setPNum(pNum);

		setApplicationType(epm.getOwnerApplication().toString());
		setVersion(epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue());
	}

	// 연관 파트 번호
	public String getpNum() {
		String pNum = "";
		try {
			if (part == null) {
				part = getDrawingPart();
			}

			if (part != null) {
				pNum = part.getNumber();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pNum;
	}

	private void setAttach(EPMDocument epm) throws Exception {
		setStep(AUIGridUtil.step(epm));
		EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
		if (epm2D != null) {
			setPdf(AUIGridUtil.pdf(epm2D));
			setDxf(AUIGridUtil.dxf(epm2D));
		}
	}

	/**
	 * Creo의 드로잉은 경우 3D의 WTPArt
	 * 
	 * @return
	 */
	public WTPart getDrawingPart() {

		try {

			return part;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 승인여부
//	public boolean getApprove() throws Exception{
//		boolean approve = false;
//		WFItem wfItem =  WFItemHelper.service.getWFItem(epm);
//		if( wfItem != null){
//			approve = true;
//		}
//		return approve;
//	}
	//
//	public boolean getWgm() throws Exception{
//		boolean wgm = false;
//		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
//		return wgm;
//	}
//	//수정 가능여부
//	public boolean getUpdate() throws Exception{
//		boolean update = false;
//		boolean wgm = false;
//		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
//		if((State.INWORK).equals(epm.getLifeCycleState()) && VersionHelper.service.isLastVersion(epm) && ! wgm) {
//			update = true;
//		}
//		return update;
//	}
//	//삭제 가능여부
//	public boolean getDelete() throws Exception{
//		boolean delete = false;
//		boolean wgm = false;
//		wgm = epm.getOwnerApplication().toString().equals("EPM") ? true : false;
//		if((State.INWORK).equals(epm.getLifeCycleState()) && VersionHelper.service.isLastVersion(epm) && ! wgm) {
//			delete = true;
//		}
//		return delete;
//	}
//	//최신 객체 여부
//	public boolean getLatest() throws Exception{
//		boolean latest = false;
//		if(!VersionHelper.service.isLastVersion(epm)) {
//			latest = true;
//		}
//		return latest;
//	}
//	
//	//Cad File
//    public String getCADName() throws Exception{
//    	
//    	String cadFile ="&nbsp;";
//    	ReferenceFactory rf = new ReferenceFactory();
//    	ContentHolder holder = null;
//    	holder = (ContentHolder)rf.getReference(oid).getObject();
//    	QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
//    	
//    	ContentItem primaryFile = null;
//    	while (result.hasMoreElements ()) {
//    		primaryFile = (ContentItem) result.nextElement();
//    	}
//    	
//    	EPMDocumentMaster master = (EPMDocumentMaster)epm.getMaster();
//    	String cadName = master.getCADName();
//
//    	String nUrl= WebUtil.getHost()+"jsp/common/DownloadGW.jsp?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(primaryFile);
//    	cadFile = "<a href='"+nUrl+"'>&nbsp;"+ cadName +"</a>";
//    	
//    	return cadFile;
//    }
//    
//    //PDF 파일
//    public String getPDFFile(){
//    	
//    	String pdfFile ="";
//    	try{
//    		
//    		if(!"PROE".equals(epm.getAuthoringApplication().toString())) return pdfFile;
//			Representation representation = PublishUtils.getRepresentation(epm); 
//			if(representation == null) return  pdfFile;
//			representation = (Representation) ContentHelper.service.getContents(representation);
//	        Vector contentList = ContentHelper.getContentList(representation);
//	        for (int l = 0; l < contentList.size(); l++) {
//	            ContentItem contentitem = (ContentItem) contentList.elementAt(l);
//	            if( contentitem instanceof ApplicationData){
//	            	ApplicationData drawAppData = (ApplicationData) contentitem;
//	            	String fileName= EpmUtil.getPublishFile(epm, drawAppData.getFileName());
//	            	if(drawAppData.getRole().toString().equals("SECONDARY") && drawAppData.getFileName().lastIndexOf("dxf")>0){
//	            		String nUrl="/Windchill/jsp/common/DownloadPDF.jsp?&appOid="+CommonUtil.getOIDString(drawAppData)+"&appType=PROE&empOid="+this.oid;
//	            		pdfFile = pdfFile + "<a href='"+nUrl+"'>&nbsp;"+fileName+"</a><br>";
//	            	}
//	            }
//	        }
//	        if(pdfFile.length()>0){
//	        	pdfFile = pdfFile.substring(0,pdfFile.length()-1);
//	        }
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    	return pdfFile;
//    }
//    
//    //주 도면의 파일 타입
//	public String getCadType() {
//		return epm.getAuthoringApplication().getDisplay(Message.getLocale());
//	}
//	
//	//썸네일 작은사이즈
//	public String getThum_mini() {
//		String thum_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL_SMALL);
//		if(thum_mini==null){ 
//			thum_mini= WebUtil.getHost() + "netmarkets/images/blank24x16.gif"; 
//		}
//		return thum_mini;
//	}
//	//썸네일 큰사이즈
//	public String getThum() {
//		return FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL);
//	}
//	//
//	public String getCopyTag() {
//		Representable representable = PublishUtils.findRepresentable(epm);
//		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
//		String copyTag = "";
//		if(representation!=null){
//			copyTag = PublishUtils.getRefFromObject(representation);
//		}
//		return copyTag;
//	}
//	//상태
//	public String getState() {
//		return epm.getLifeCycleState().getDisplay(Message.getLocale());
//	}
//	//설명
//	public String getDescription() {
//		return StringUtil.checkNull(epm.getDescription());
//	}
//	
//	public boolean isCreoDrawing(){
//		
//		return EpmUtil.isCreoDrawing(epm);
//	}
//	
//	/**
//	 * Creo의 드로잉은 경우 3D의 WTPArt
//	 * @return
//	 */
//	public WTPart getDrawingPart() {
//		
//		try {
//			
//			if(isCreoDrawing()){
//				String number= epm.getNumber();
//				String version = epm.getVersionIdentifier().getValue();
//				number = EpmUtil.getFileNameNonExtension(number);
//				part = PartHelper.service.getPart(number, version);
//			}else{
//				part = DrawingHelper.service.getWTPart(epm);
//			}
//			
//			return part;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	//관련 파트
//	public WTPart getPart() {
//		try {
//			
//			return DrawingHelper.service.getWTPart(epm);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	//연관 파트 OID
//	public String getpOid() {
//		String pOid = "";
//		try {
//			
//			if(part == null){
//				part = getDrawingPart();
//			}
//			if(part != null) {
//				pOid = CommonUtil.getOIDString(part);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return pOid;
//	}
//	//연관 파트 번호
//	public String getpNum() {
//		String pNum = "";
//		try {
//			if(part == null){
//				part = getDrawingPart();
//			}
//			
//			if(part != null) {
//				pNum = part.getNumber();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return pNum;
//	}
//	//연관 파트 이름
//	public String getpName() {
//		String pName = "";
//		try {
//			if(part == null){
//				part = getDrawingPart();
//			}
//			
//			if(part != null) {
//				pName = part.getName();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return pName;
//	}
//	
//	//연관 파트 승인 유무
//	public boolean isPApproved() {
//		boolean isPApproved = false;
//		try {
//			if(DrawingHelper.service.getWTPart(epm) != null) {
//				WTPart part = getPart();
//				isPApproved = part.getLifeCycleState().toString().equals("APPROVED");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return isPApproved;
//	}
//	
//	//객체 생성 방식
//	public String getApplicationType() {
//		return epm.getOwnerApplication().toString();
//	}
//	
//	// 
//	public String getCadName() {
//		return ((EPMDocumentMaster)epm.getMaster()).getCADName();
//	}
//
//	public String getCadNameUrl() {
//		try {
//			return getCADName();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//	public String getLepmOid() {
//		EPMDocument lepm;
//		String lepmOid = "";
//		try {
//			lepm = (EPMDocument)ObjectUtil.getLatestObject((Master)epm.getMaster());
//			lepmOid = lepm.getPersistInfo().getObjectIdentifier().toString();
//		} catch (WTException e) {
//			e.printStackTrace();
//		}
//		return lepmOid;
//	}
//
//	public String getLinkRefernceType() {
//		return linkRefernceType;
//	}
//
//	public void setLinkRefernceType(String linkRefernceType) {
//		this.linkRefernceType = linkRefernceType;
//	}
//	//html 형식 변환 설명
//	public String getvDesc() {
//		return WebUtil.getHtml(epm.getDescription());
//	}
//	

//		
//		
//		boolean isLastest = isLatest();
//		boolean isWorking = isWorking();
//		String name = epm.getName();
//		String epm2DNumber = EpmUtil.getNumberNonExtension(epm.getNumber());
//		boolean isNumber = PartUtil.isChange(epm2DNumber); //true 이면 가도번 또는 더미 파일
//		
//		if(isNumber){
//			return false;
//		}
//		//System.out.println("epm2DNumber="+epm2DNumber);
//		EPMDocument epm3D = EpmSearchHelper.service.getDrawingToCad(epm2DNumber);
//		
//		if(epm3D == null){
//			return false;
//		}
//		//System.out.println("epm3D Name="+epm3D.getName());
//		//System.out.println("Name="+name);
//		//System.out.println("epm3D Oid="+CommonUtil.getOIDString(epm3D));
//		boolean isName = !name.equals(epm3D.getName());
//		
//		boolean isSys = isName && isLastest && isWorking;
//		
//		return isSys;
//	}
//	@Override
//	public int hashCode(){
//	    return (this.number.hashCode() + this.number.hashCode());
//	}
//
//	@Override
//	public boolean equals(Object obj){
//	    if(obj instanceof EpmData){
//	    	EpmData temp = (EpmData) obj;
//	        if(this.number.equals(temp.number)){
//	            return true;
//	        }
//	    }
//	    return false;
//	}
}