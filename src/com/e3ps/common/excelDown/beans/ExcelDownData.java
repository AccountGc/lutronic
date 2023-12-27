package com.e3ps.common.excelDown.beans;

public class ExcelDownData {
	
	public static String docFileName 	 = "문서_목록.xls";
	public static String[] documentTitle = {"문서번호", "내부 문서번호", "프로젝트코드", "문서명", "문서분류", "Rev.", "상태", "작성자", "등록자", "등록일", "수정일"};
	public static int[] documentSize 	 = {25, 25, 12, 25, 25, 12, 15, 15, 15, 17, 17};
	
	public static String partFileName = "품목_목록.xls";
	public static String[] partTitle  = {"품목번호","품목명","품목분류","Rev.","상태","등록자","등록일","수정일"};
	public static int[] partSize 	  = {25, 35, 25, 17, 15, 15, 17, 17};
	
	public static String drawingFileName = "도면_목록.xls";
	public static String[] drawingTitle  = { "CAD타입", "도면번호", "도면명", "도면분류", "Rev.", "상태", "등록자", "등록일", "수정일"};
	public static int[] drawingSize 	 = {10, 35, 35, 25, 15, 15, 15, 17, 17};
	
	public static String ecrFileName = "ECR_목록.xls";
	public static String[] ecrTitle  = {"ECR번호", "ECR제목", "변경구분", "작성자", "작성부서", "작성일", "상태", "등록자", "등록일"};
	public static int[] ecrSize 	 = {20, 30, 25, 15, 20, 17, 15, 15, 17};
	
	public static String ecoFileName = "ECO_목록.xls";
	public static String[] ecoTitle  = {"ECO번호", "ECO제목", "인허가 변경","위험 통제", "상태", "등록자","승인일", "등록일"};
	public static int[] ecoSize 	 = {20, 30, 25, 15,15, 15,17,17};
	
	public static String eoFileName = "EO_목록.xls";
	public static String[] eoTitle  = {"EO번호", "EO제목", "EO구분", "상태", "등록자","승인일", "등록일"};
	public static int[] eoSize 	 = {20, 30, 25, 15, 15, 17, 17};
	
	public static String rohsFileName = "RoHs_목록.xls";
	public static String[] rohsTitle  = {"물질번호", "협력업체", "물질명", "Rev.", "상태", "등록자", "등록일", "수정일"};
	public static int[] rohsSize      = {25, 15, 25, 12, 15, 15, 17, 17};
	
	public static String moldFileName = "금형_목록.xls";
	public static String[] moldTitle  = {"문서번호","문서명","Rev.","상태","등록자","등록일","수정일"};
	public static int[] moldSize      = {25, 25, 12, 15, 15, 17, 17};
	
	public static String devFileName = "개발업무관리_목록.xls";
	public static String[] devTitle	 = {"프로젝트코드", "프로젝트명", "관리자", "예상 시작일", "예상 종료일", "등록일", "상태"};
	public static int[] devSize		 = {15, 25, 15, 17, 17, 17, 15};
	
	public static String myDevFileName = "나의 개발업무관리_목록.xls";
	public static String[] myDevTitle  = {"프로젝트코드", "프로젝트명", "프로젝트상태", "TASK명", "ACTIVITY명", "관리자", "수행자", "완료요청일", "완료일", "ACTIVITY상태"};
	public static int[] myDevSize	   = {15, 25, 15, 20, 20, 15, 15, 17, 17, 15};
	
	public static String loginHistoryFileName = "로그인 이력관리_목록.xls";
	public static String[] loginHistoryTitle  = {"이름", "아이디", "접속 시간"};
	public static int[]loginHistorySize	   = {15, 25, 15};
	
	public static String batchDownFileName = "일괄 다운로드.xls";
	public static String[]  batchDownTitle  = {"순번", "타입", "번호","버전","이름","등록자","수정자","첨부파일타입","첨부파일","다운로드결과"};
	public static int[] batchDownSize	   = {10, 15, 30, 10, 50, 10, 10 ,20, 100, 50};
	
	
	public static String userToGroupFileName = "유저별그룹_목록.xls";
	public static String[] userToGroupTitle  = {"이름", "id", "그룹"};
	public static int[] userToGroupSize 	 = {20, 20, 30,};
	
	public static String downloadHistoryFileName = "다운로드 이력관리_목록.xls";
	public static String[] downloadHistoryTitle  = {"이름", "아이디", "해당 모듈","다운횟수","다운시간","다운사유"};
	public static int[]downloadHistorySize	   = {15, 25, 15,20,20,20};
}
