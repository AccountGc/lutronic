package com.e3ps.change.ecpr.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
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
public class EcprDTO {
	private String oid;
	private String name;
	private String number;
	private String createdDate;
	private String approveDate;
	private String createDepart_name;
	private String writer_name;
	private String primary;
	private String changeSection;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String writer_oid;

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

	public EcprDTO() {

	}

	public EcprDTO(String oid) throws Exception {
		this((ECPRRequest) CommonUtil.getObject(oid));
	}

	public EcprDTO(ECPRRequest cr) throws Exception {
		setOid(cr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(cr.getEoName());
		setNumber(cr.getEoNumber());
		setApproveDate(StringUtil.checkNull(cr.getApproveDate()));
		setCreateDepart_name(EcprHelper.manager.displayToDept(cr.getCreateDepart()));
		setWriter_name(CommonUtil.getUserNameFromOid(cr.getWriter()));
		setWriter_oid(CommonUtil.getUserOid(cr.getWriter()));
		setChangeSection(cr.getChangeSection());
		setEoCommentA(StringUtil.checkNull(cr.getEoCommentA()));
		setEoCommentB(StringUtil.checkNull(cr.getEoCommentB()));
		setEoCommentC(StringUtil.checkNull(cr.getEoCommentC()));

		// 따로 추가
		setState(cr.getLifeCycleState().getDisplay());
		setCreatedDate(cr.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(cr.getModifyTimestamp());
		setModifiedDate_text(cr.getModifyTimestamp().toString().substring(0, 10));
		setCreator(cr.getCreatorFullName());
		setCreateDepart(StringUtil.checkNull(cr.getCreateDepart()));
		setWriteDate(StringUtil.checkNull(cr.getCreateDate()));
		setModel(EcprHelper.manager.displayToModel(cr.getModel()));
		
		setEcpr(cr);
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
