package com.e3ps.change.cr.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ContentUtils;
import com.e3ps.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrDTO {

	private String oid;
	private String name;
	private String number;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_text;
	private String approveDate;
	private String createDepart_name;
	private String writer;
	private String proposer_name;
	private String primary;
	private String changeSection;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String contents;

	// 따로 추가
	private String state;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_text;
	private String creator;
	private String writeDate;
	private String proposer;
	private String changeCode;
	private String model;
	private boolean ecprStart;
	private String period_name;
	private String period_code;
	// auth
	private boolean _delete = false;
	private boolean _withdraw = false;
	private boolean _modify = false;
	private boolean _print = false;

	// 신규CR
	private boolean _isNew = false;

	// 변수용
	private String proposer_oid;
	private String createDepart;
	private ArrayList<String> sections = new ArrayList<String>(); // 변경 구분
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련 문서
	private ArrayList<Map<String, String>> rows105 = new ArrayList<>(); // 관련 ECO
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 모델

	private Map<String, Object> contentMap = new HashMap<>();

	private boolean temprary;

	public CrDTO() {

	}

	public CrDTO(String oid) throws Exception {
		this((EChangeRequest) CommonUtil.getObject(oid));
	}

	public CrDTO(EChangeRequest cr) throws Exception {
		setOid(cr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(cr.getEoName());
		setNumber(cr.getEoNumber());
		setApproveDate(StringUtil.checkNull(cr.getApproveDate()));
		setCreateDepart_name(cr.getCreateDepart());
		setWriter(cr.getWriter());
		setProposer_name(StringUtil.checkNull(cr.getProposer()));
		setChangeSection(cr.getChangeSection());
		setEoCommentA(StringUtil.checkNull(cr.getEoCommentA()));
		setEoCommentB(StringUtil.checkNull(cr.getEoCommentB()));
		setEoCommentC(StringUtil.checkNull(cr.getEoCommentC()));
		setContents(StringUtil.checkNull(cr.getContents()));
		setState(cr.getLifeCycleState().getDisplay());
		setCreatedDate(cr.getCreateTimestamp());
		setCreatedDate_text(cr.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(cr.getModifyTimestamp());
		setModifiedDate_text(cr.getModifyTimestamp().toString().substring(0, 10));
		setCreator(cr.getCreatorFullName());
		setWriteDate(StringUtil.checkNull(cr.getCreateDate()));
		setProposer(StringUtil.checkNull(cr.getProposer()));
		setModel(CrHelper.manager.displayToModel(cr.getModel()));
		setContentMap(ContentUtils.getContentByRole(cr, "ECR"));
		set_isNew(cr.getIsNew()); // true 신규

		NumberCode period = NumberCodeHelper.manager.getNumberCode(cr.getPeriod(), "PRESERATION");
		if (period != null) {
			setPeriod_code(period.getCode());
			setPeriod_name(period.getName());
		}

		setAuth(cr);
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
	private void setAuth(EChangeRequest cr) throws Exception {
		boolean isAdmin = CommonUtil.isAdmin();
		boolean isCreator = CommonUtil.isCreator(cr);

		if (check(cr, "APPROVING") && (isAdmin || isCreator)) {
			set_withdraw(true);
		}

		if (check(cr, "APPROVED") && is_isNew()) {
			set_print(true);
		}

		// 최신버전이고 결재선 지정상태일 경우 승인가능
		if ((check(cr, "INWORK") || check(cr, "LINE_REGISTER") || check(cr, "RETURN"))) {
			set_modify(true);
		}

		if (isAdmin || isCreator) {
			set_delete(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(EChangeRequest cr, String state) throws Exception {
		boolean check = false;
		String compare = cr.getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
