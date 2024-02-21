package com.e3ps.change.eco.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ContentUtils;
import com.e3ps.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class EcoDTO {

	private String oid;
	private String name;
	private String number;
	private String state;
	private String creator;
	private String sendType;
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
	private String createDepart;

	// auth
	private boolean _modify = false;
	private boolean _delete = false;
	private boolean _withdraw = false;
	private boolean _validate = false;
	private boolean _excel = false;
	private boolean _output = false;

	// 변수용
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows103 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // 설변활동
	private ArrayList<Map<String, String>> rows500 = new ArrayList<>(); // 대상품목
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 모델

	private Map<String, Object> contentMap = null;

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
		setSendTypeInfo(eco);
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
		setAuth(eco);
	}

	private void setSendTypeInfo(EChangeOrder eco) throws Exception {
		String sendType = eco.getSendType();
		if ("ECO".equals(sendType)) {
			setSendType("ECO");
		} else if ("SCO".equals(sendType)) {
			setSendType("SCO");
		} else if ("ORDER".equals(sendType)) {
			setSendType("선구매");
		}
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
		} else if ("LI002".equals(s)) {
			return "불필요";
		} else if ("LI001".equals(s)) {
			return "필요";
		}
		return "";
	}

	/**
	 * 권한 설정
	 */
	private void setAuth(EChangeOrder eco) throws Exception {
		boolean isAdmin = CommonUtil.isAdmin();
		boolean isCreator = CommonUtil.isCreator(eco);

		if (check(eco, "APPROVED") || isAdmin) {
			set_output(true);
		}

		if (check(eco, "APPROVING") && (isAdmin || isCreator)) {
			set_withdraw(true);
		}

		if (isCreator && (check(eco, "INWORK") || check(eco, "LINE_REGISTER") || (check(eco, "ACTIVITY"))
				|| check(eco, "RETURN"))) {
			set_modify(true);
		}

		if (isAdmin || isCreator) {
			set_delete(true);
		}

		WTUser sessionUser = CommonUtil.sessionUser();
		boolean creator = eco.getCreatorName().equals(sessionUser.getName());
		if ((isAdmin || creator) && check(eco, "APPROVING")) {
			set_validate(true);
		}

		if (isAdmin || check(eco, "APPROVED")) {
			set_excel(true);
		}

		// 관리자는 일단 수정 가능하게
		if (isAdmin) {
			set_modify(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(EChangeOrder eco, String state) throws Exception {
		boolean check = false;
		String compare = eco.getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
