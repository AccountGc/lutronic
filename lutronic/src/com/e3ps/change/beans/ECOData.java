package com.e3ps.change.beans;

import java.util.Comparator;
import java.util.Map;

//import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ECOData{
	
//	private EChangeOrder eco;
//	private String licensing;
//	private Vector<NumberCode> licensingCode = null;
//	private EChangeRequest ecr;
	private String riskType;
	private String riskTypeName;
//	private String listInitCount;
//	private String licensingDisplay;
	private String eoName;
	private String eoNumber;
	private String eoType;
	private String state;
	private String creator;
	private String createDate;
	private String eoApproveDate;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String eoCommentD;
	private String eoCommentE;
	
	public ECOData() {
		
	}
	
	public ECOData(EChangeOrder eco){
//		super(eco);
//		setEco(getEco());
//		setLicensing(eco.getLicensingChange());
		setRiskType(StringUtil.checkNull(eco.getRiskType()));
		setRiskTypeName(ChangeUtil.getRiskTypeName(riskType,true));
//		setLicensingCode(ChangeUtil.getNumberCodeVec(this.licensing, "LICENSING"));
//		if(licensingCode == null){
//    		licensingCode = getLicensingCode();
//    	}
//    	String licensingDisplay = ChangeUtil.getCodeListDisplay(licensingCode);
//    	if(licensingDisplay.length()==0){
//    		if(true){
//    			licensingDisplay ="<b><font color='red'>선택안됨</font</b>";
//    		}else{
//    			licensingDisplay ="선택안됨";
//    		}
//    	}
//    	setLicensingDisplay(licensingDisplay);
//		setListInitCount(getListInitCount());
		setEoName(eco.getEoName());
		setEoNumber(eco.getEoNumber());
		setEoType(eco.getEoType());
		setState(eco.getLifeCycleState().toString());
		setCreator(eco.getCreatorFullName());
		setCreateDate(DateUtil.getDateString(eco.getCreateTimestamp(),"a"));
		setEoApproveDate(eco.getEoApproveDate());
		setEoCommentA(eco.getEoCommentA());
		setEoCommentB(eco.getEoCommentB());
		setEoCommentC(eco.getEoCommentC());
		setEoCommentD(eco.getEoCommentD());
		setEoCommentE(eco.getEoCommentE());
	}
	
	/**
	 * 완제품 품목
	 * @return
	 * @throws Exception
	 */
//	public List<Map<String,Object>> getCompletePartList() throws Exception{
//		List<EOCompletePartLink> list= ECOSearchHelper.service.getCompletePartLink(eco);
//		//( (이 CHANGE (ECO) 자동으로 생성,승인됨이 아닌것 , 작성자  ) || 관리자 ) && ECO
//		String eoType = eco.getEoType();
//		String state = eco.getLifeCycleState().toString();
//		String userName = eco.getCreator().getName();
//		String sessionName = SessionHelper.getPrincipal().getName();
//		
//		boolean isECO = eoType.equals(ECOKey.ECO_CHANGE);
//		boolean isCreate = userName.equals(sessionName);
//		boolean isstate = !state.equals("APPROVED") ;
//		boolean isDelete = ((isCreate && isstate) || CommonUtil.isAdmin()) && isECO;
//		
//		List<Map<String,Object>> listLink = new ArrayList<Map<String,Object>>();
//		for(EOCompletePartLink link : list){
//			
//			String version = link.getVersion();
//			WTPartMaster master = (WTPartMaster)link.getCompletePart();
//			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
//			ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
//			boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED") ? true : false; 
//			Map<String,Object> map = new HashMap<String,Object>();
//			
//			map.put("Oid", CommonUtil.getOIDString(part));
//			//System.out.println("========Oid= " + CommonUtil.getOIDString(part));
//			map.put("isDelete", isDelete);
//			map.put("likOid", CommonUtil.getOIDString(link));
//			map.put("Number", part.getNumber());
//			map.put("Name", part.getName());
//			map.put("ver", part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
//			map.put("State", part.getLifeCycleState().getDisplay(Message.getLocale()));
//			map.put("Creator", part.getCreatorFullName());
//			map.put("CreateDate", DateUtil.getDateString(part.getCreateTimestamp(), "d"));
//			map.put("isApproved", isApproved);
//			map.put("baselineOid", CommonUtil.getOIDString(baseline));
//			listLink.add(map);
//			
//		}
//		//PJT EDIT START START 20161116
//		Collections.sort(listLink,new NumberAscCompare());
//		//PJT EDIT START END
//		return listLink;
//	}
    
    /**
     * 관련 ECR
     * @return
     * @throws Exception
     */
//    public EChangeRequest getECR() throws Exception{
//    	
////    	System.out.println("===== TEST EChangeRequest getECR() =======" +eco.getEoNumber());
//    	List<EChangeRequest> list = ECRSearchHelper.service.getRequestOrderLinkECR(eco);
//    	
//    	for(EChangeRequest ecr : list){
//    		this.ecr = ecr;
//    	}
//    	
//		return ecr;
//    }
    
    
    
    /**
     * 관련 ECR Name
     * @return
     * @throws Exception
     */
//    public String getECRName() throws Exception{
//    	
//    	String ecrNumber="";
//    	if(this.ecr == null){
//    		this.ecr= getECR();
//    		if(this.ecr == null){
//    			return "";
//    		}
//    		ecrNumber = ecr.getEoNumber()+"["+ecr.getEoName()+"]";
//    	}else{
//    		ecrNumber = ecr.getEoNumber()+"["+ecr.getEoName()+"]";
//    	}
//    	
//    	return ecrNumber;
//    }
    
    /**
     * 관련 ECR Oid
     * @return
     * @throws Exception
     */
//    public String getECROid() throws Exception{
//    	
//    	String ecrOid ="";
//    	
//    	if(this.ecr == null){
//    		this.ecr= getECR();
//    		if(this.ecr == null){
//    			return "";
//    		}
//    		ecrOid = CommonUtil.getOIDString(ecr);
//    	}else{
//    		ecrOid = CommonUtil.getOIDString(ecr);
//    	}
//    	
//    	return ecrOid;
//    }
    
    /**
     * 관련 ECR Number
     * @return
     * @throws Exception
     */
//    public String getECRNumber() throws Exception{
//    	
//    	String ecrNumber ="";
//    	
//    	if(this.ecr == null){
//    		this.ecr= getECR();
//    		if(this.ecr == null){
//    			return "";
//    		}
//    		ecrNumber = ecr.getEoNumber();
//    	}else{
//    		ecrNumber = ecr.getEoNumber();
//    	}
//    	
//    	return ecrNumber;
//    }

    /**
     * 관련 ECR Number
     * @return
     * @throws Exception
     */
//    public ArrayList<String> getECRNumberAndNames() throws Exception{
//    	
//    	ArrayList<String> list = new ArrayList<String>();
//    	List<EChangeRequest> list_ECR = ECRSearchHelper.service.getRequestOrderLinkECR(eco);
//    	for (EChangeRequest ecr : list_ECR) {
//    		list.add(ecr.getEoNumber()+"  :  "+ecr.getEoName());
//		}
//    	return list;
//    }

	//PJT EDIT START START 20161116
	static class NumberAscCompare implements Comparator<Map<String,Object>> {
		 
		/**
		 * 오름차순(ASC)
		 */
			@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			//return o1.getName().compareTo(o2.getName());
				String o1N = String.valueOf(o1.get("Number"));
				String o2N = String.valueOf(o2.get("Number"));
			return o1N.compareTo(o2N);
		}
 
	}
	
	/**
	 * ERP 전송 유무 체크
	 * 승인됨,관리자, 전송이력이 없음
	 * @return
	 */
//	public boolean isERPSend(){
//		boolean isERPSend = false;
//		try{
//			isERPSend = isApproved() && CommonUtil.isAdmin() && ! ERPSearchHelper.service.checkSendEO(this.number);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return isERPSend;
//	}
	
}
