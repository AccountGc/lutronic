package com.e3ps.change.cr.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrDTO {

	private String oid;
	private String name;
	private String number;
	private String createdDate;
	private String approveDate;
	private String createDepart_name;
	private String writer_name;
	private String proposer_name;
	private String primary;
	private String changeSection;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;

	// 변수용
	private String writer_oid;
	private String proposer_oid;
	private String createDepart_code;
	private ArrayList<String> sections = new ArrayList<String>(); // 변경 구분
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows300= new ArrayList<>(); // 모델

	public CrDTO() {

	}

	public CrDTO(String oid) throws Exception {
		this((EChangeRequest) CommonUtil.getObject(oid));
	}

	public CrDTO(EChangeRequest cr) throws Exception {

	}
}
