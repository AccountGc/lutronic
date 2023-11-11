package com.e3ps.change.eco.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ContentUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.service.MailUserHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoDTO {

	private String oid;
	private String name;
	private String number;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_text;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String eoCommentD;
	private String licensing;
	private String riskType;
	private String licensing_name;
	private String riskType_name;
	private String eoType;
	private String approveDate = "";
	private String model_name;
	
	private EChangeOrder eco;
	// auth
	private boolean isModify = false;
	
	// 변수용
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // 설변활동
	private ArrayList<Map<String, String>> rows500 = new ArrayList<>(); // 대상품목

	private Map<String, Object> contentMap = null;
	
	// 결재 변수
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	
	// 외부 메일 변수
	private ArrayList<Map<String, String>> external = new ArrayList<Map<String, String>>();
	
	private boolean temprary;

	public EcoDTO() {

	}

	public EcoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EcoDTO(EChangeOrder eco) throws Exception {
		setOid(eco.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(eco.getEoName());
		setNumber(eco.getEoNumber());
		setState(eco.getLifeCycleState().getDisplay());
		setCreator(eco.getCreatorFullName());
		setCreatedDate(eco.getCreateTimestamp());
		setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(eco.getModifyTimestamp());
		setModifiedDate_text(eco.getModifyTimestamp().toString().substring(0, 10));
		setEoCommentA(StringUtil.checkNull(eco.getEoCommentA()));
		setEoCommentB(StringUtil.checkNull(eco.getEoCommentB()));
		setEoCommentC(StringUtil.checkNull(eco.getEoCommentC()));
		setEoCommentD(StringUtil.checkNull(eco.getEoCommentD()));
		setLicensing(eco.getLicensingChange());
		setLicensing_name(licensing(eco.getLicensingChange()));
		setRiskType_name(risk(eco.getRiskType()));
		setRiskType(eco.getRiskType());
		setEoType(eco.getEoType());
		setApproveDate(StringUtil.checkNull(eco.getEoApproveDate()));
		if (eco.getModel() != null) {
			setModel_name(EcoHelper.manager.displayToModel(eco.getModel()));
		}
		setContentMap(ContentUtils.getContentByRole(eco, "ECO"));
		
		setEco(eco);
		setAuth();
	}

	/**
	 * 위험통제 디스플레이
	 */
	private String risk(String s) throws Exception {
		if ("NONE".equals(s)) {
			return "N/A";
		} else if ("0".equals(s)) {
			return "불필요";
		} else if ("1".equals(s)) {
			return "필요";
		}
		return "";
	}

	/**
	 * 인허가 디스플레이
	 */
	private String licensing(String s) throws Exception {
		if ("NONE".equals(s)) {
			return "N/A";
		} else if ("0".equals(s)) {
			return "불필요";
		} else if ("1".equals(s)) {
			return "필요";
		}
		return "";
	}
	
	/**
	 * 권한 설정
	 */
	private void setAuth() throws Exception {
		// 삭제, 수정 권한 - (최신버전 && ( 작업중 || 임시저장 || 일괄결재중 || 재작업))
		if (check("INWORK") || check("TEMPRARY") || check("BATCHAPPROVAL") || check("REWORK")) {
			setModify(true);
		}
	}
	
	/**
	 * 상태값 여부 체크
	 */
	private boolean check(String state) throws Exception {
		boolean check = false;
		String compare = getEco().getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
