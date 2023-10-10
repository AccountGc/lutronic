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
	// 변수용
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // 설변활동
	private ArrayList<Map<String, String>> rows500 = new ArrayList<>(); // 대상품목

	private Map<String, Object> contentMap = null;

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
		setEoCommentA(eco.getEoCommentA());
		setEoCommentB(eco.getEoCommentB());
		setEoCommentC(eco.getEoCommentC());
		setEoCommentD(eco.getEoCommentD());
		setLicensing(eco.getLicensingChange());
		setLicensing_name(licensing(eco.getLicensingChange()));
		setRiskType_name(risk(eco.getRiskType()));
		setRiskType(eco.getRiskType());
		setEoType(eco.getEoType());
		setApproveDate(eco.getEoApproveDate());
		if (eco.getModel() != null) {
			setModel_name(EcoHelper.manager.displayToModel(eco.getModel()));
		}
		setContentMap(ContentUtils.getContentByRole(eco, "ECO"));
	}

	/**
	 * 위험통제 디스플레이
	 */
	private String risk(String s) throws Exception {
		if ("".equals(s)) {
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
}
