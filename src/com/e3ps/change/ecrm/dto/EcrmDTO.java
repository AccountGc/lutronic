package com.e3ps.change.ecrm.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.service.MailUserHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.session.SessionHelper;

@Getter
@Setter
public class EcrmDTO {
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

	private ECPRRequest ecpr;
	// auth
	private boolean isModify = false;

	// 변수용
	private ArrayList<String> sections = new ArrayList<String>(); // 변경 구분
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 모델

	private boolean temprary;

	public EcrmDTO() {

	}

	public EcrmDTO(String oid) throws Exception {
		this((ECRMRequest) CommonUtil.getObject(oid));
	}

	public EcrmDTO(ECRMRequest ecrm) throws Exception {
		setOid(ecrm.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(StringUtil.checkNull(ecrm.getEoName()));
		setNumber(StringUtil.checkNull(ecrm.getEoNumber()));
		setApproveDate(StringUtil.checkNull(ecrm.getApproveDate()));
		setCreateDepart_name(EcprHelper.manager.displayToDept(ecrm.getCreateDepart()));
		setWriter(ecrm.getWriter());
		setChangeSection(ecrm.getChangeSection());
		setEoCommentA(StringUtil.checkNull(ecrm.getEoCommentA()));
		setEoCommentB(StringUtil.checkNull(ecrm.getEoCommentB()));
		setEoCommentC(StringUtil.checkNull(ecrm.getEoCommentC()));
		setContents(StringUtil.checkNull(ecrm.getContents()));

		// 따로 추가
		setState(ecrm.getLifeCycleState().getDisplay());
		setCreatedDate(ecrm.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(ecrm.getModifyTimestamp());
		setModifiedDate_text(ecrm.getModifyTimestamp().toString().substring(0, 10));
		setCreator(ecrm.getCreatorFullName());
		setCreateDepart(StringUtil.checkNull(ecrm.getCreateDepart()));
		setWriteDate(StringUtil.checkNull(ecrm.getCreateDate()));
		setModel(EcprHelper.manager.displayToModel(ecrm.getModel()));

		setAuth();
	}

	/**
	 * 회수 권한 승인중 && (소유자 || 관리자 ) && 기본 결재
	 * 
	 * @return
	 */
	public boolean isWithDraw() {
		try {
			return (state.equals("APPROVING") && (isOwner() || CommonUtil.isAdmin()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Owner 유무 체크
	 * 
	 * @return
	 */
	public boolean isOwner() {

		try {
			return SessionHelper.getPrincipal().getName().equals(getCreator());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
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
		String compare = getEcpr().getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
