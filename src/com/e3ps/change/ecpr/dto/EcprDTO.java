package com.e3ps.change.ecpr.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcprDTO {
	private String oid;
	private String name;
	private String number;
	private String createdDate;
	private String approveDate;
	private String createDepart_name;
	private String primary;
	private String changeSection;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String writer;
	private String contents;

	// 따로 추가
	private String state;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_text;
	private String creator;
	private String createDepart;
	private String writeDate;
	private String changeCode;
	private String model;
	private String period_name;
	private String period_code;
	// auth
	private boolean _modify = false;
	private boolean _delete = false;
	private boolean _withdraw = false;
	private boolean _print = false;

	// 변수용
	private ArrayList<String> sections = new ArrayList<String>(); // 변경 구분
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련 문서
	private ArrayList<Map<String, String>> rows105 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 모델

	private boolean _isNew = false;

	public EcprDTO() {

	}

	public EcprDTO(String oid) throws Exception {
		this((ECPRRequest) CommonUtil.getObject(oid));
	}

	public EcprDTO(ECPRRequest ecpr) throws Exception {
		setOid(ecpr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(StringUtil.checkNull(ecpr.getEoName()));
		setNumber(StringUtil.checkNull(ecpr.getEoNumber()));
		setApproveDate(StringUtil.checkNull(ecpr.getApproveDate()));
		setCreateDepart_name(ecpr.getCreateDepart());
		setWriter(ecpr.getWriter());
		setChangeSection(ecpr.getChangeSection());
		setEoCommentA(StringUtil.checkNull(ecpr.getEoCommentA()));
		setEoCommentB(StringUtil.checkNull(ecpr.getEoCommentB()));
		setEoCommentC(StringUtil.checkNull(ecpr.getEoCommentC()));
		setContents(StringUtil.checkNull(ecpr.getContents()));
		setState(ecpr.getLifeCycleState().getDisplay());
		setCreatedDate(ecpr.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(ecpr.getModifyTimestamp());
		setModifiedDate_text(ecpr.getModifyTimestamp().toString().substring(0, 10));
		setCreator(ecpr.getCreatorFullName());
		setCreateDepart(StringUtil.checkNull(ecpr.getCreateDepart()));
		setWriteDate(StringUtil.checkNull(ecpr.getCreateDate()));
		setModel(EcprHelper.manager.displayToModel(ecpr.getModel()));
		NumberCode period = NumberCodeHelper.manager.getNumberCode(ecpr.getPeriod(), "PRESERATION");
		if (period != null) {
			setPeriod_code(period.getCode());
			setPeriod_name(period.getName());
		}
		set_isNew(ecpr.getIsNew());
		setAuth(ecpr);
	}

	/**
	 * 변경 구분
	 */
	public String getChangeCode() throws Exception {
		this.changeCode = NumberCodeHelper.manager.getNumberCodeName(this.changeSection, "CHANGESECTION");
		return changeCode;
	}

	/**
	 * 권한 설정
	 */
	private void setAuth(ECPRRequest ecpr) throws Exception {
		boolean isAdmin = CommonUtil.isAdmin();
		boolean isCreator = CommonUtil.isCreator(ecpr);

		if (check(ecpr, "APPROVING") && (isAdmin || isCreator)) {
			set_withdraw(true);
		}

		if (check(ecpr, "APPROVED") && is_isNew()) {
			set_print(true);
		}

		if ((check(ecpr, "INWORK") || check(ecpr, "LINE_REGISTER") || check(ecpr, "RETURN"))) {
			set_modify(true);
		}

		if (isAdmin || isCreator) {
			set_delete(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(ECPRRequest ecpr, String state) throws Exception {
		boolean check = false;
		String compare = ecpr.getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
