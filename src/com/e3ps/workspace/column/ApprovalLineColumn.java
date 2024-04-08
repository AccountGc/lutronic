package com.e3ps.workspace.column;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.service.WorkspaceHelper;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.part.WTPart;

@Getter
@Setter
public class ApprovalLineColumn {

	private String oid;
	private String poid;
	private int rowNum;
	private boolean reads;
	private String name;
	private String role;
	private String type;
	private String state;
	private String creator;
	private String submiter;
	private Timestamp createdDate;
	private Timestamp completeTime;
	private Timestamp receiveTime;
	private String persistType;
	private boolean complete;

	// 결재 진행
	private String point;

	/*
	 * 리스트 화면 변수
	 */
	private final String AGREE_COLUMN = "AGREE_COLUMN";
	private final String APPROVAL_COLUMN = "APPROVAL_COLUMN";
	private final String PROGRESS_COLUMN = "PROGRESS_COLUMN";
	private final String COMPLETE_COLUMN = "COMPLETE_COLUMN";
	private final String RECEIVE_COLUMN = "RECEIVE_COLUMN";
	private final String REJECT_COLUMN = "REJECT_COLUMN";

	public ApprovalLineColumn() {

	}

	public ApprovalLineColumn(ApprovalLine line, String column) throws Exception {
		String name = WorkspaceHelper.manager.getName(line.getMaster().getPersist());
		ApprovalMaster master = line.getMaster();
		// 결재함
		if (APPROVAL_COLUMN.equals(column)) {
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setRole(line.getRole());
//			setName(line.getName());
			setName(name);
			setCreator(line.getMaster().getOwnership().getOwner().getFullName());
			setState(line.getState());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setCreatedDate(line.getCreateTimestamp());
			setReceiveTime(line.getStartTime()); // 수신일 = 받은 시간
		} else if (RECEIVE_COLUMN.equals("RECEIVE_COLUMN")) {
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
//			setName(line.getName());
			setName(name);
			setCreator(line.getMaster().getOwnership().getOwner().getFullName());
			setRole(line.getRole());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(line.getState());
			setReceiveTime(line.getCreateTimestamp());
			setComplete(line.getCompleteTime() != null ? true : false);
		} else if (AGREE_COLUMN.equals("AGREE_COLUMN")) {
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
//			setName(line.getName());
			setName(name);
			setRole(line.getRole());
			setState(line.getState());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setReceiveTime(line.getCreateTimestamp());
		}
		persistInfo(master);
		point(master);
	}

	public ApprovalLineColumn(ApprovalMaster master, String column) throws Exception {
		String name = WorkspaceHelper.manager.getName(master.getPersist());
		if (PROGRESS_COLUMN.equals(column)) {
			// 진행함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
//			setName(master.getName());
			setName(name);
			setCreatedDate(master.getCreateTimestamp());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setReceiveTime(master.getStartTime());
		} else if (COMPLETE_COLUMN.equals("COMPLETE_COLUMN")) {
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setType(master.getType());
//			setName(master.getName());
			setName(name);
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setCreatedDate(master.getCreateTimestamp());
			setCompleteTime(master.getCompleteTime()); // 반드시 완료날짜 잇음
		} else if (REJECT_COLUMN.equals("REJECT_COLUMN")) {
			// 반려함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
//			setName(master.getName());
			setName(name);
			setCreatedDate(master.getCreateTimestamp());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setCreatedDate(master.getCreateTimestamp()); // 생성일 = 기안일
			setCompleteTime(master.getCompleteTime());
		}
		persistInfo(master);
		point(master);
	}

	/*
	 * 결재 지점
	 */
	private void point(ApprovalMaster master) throws Exception {
		// 모든 라인을 가져온 후에 작업한다.
		ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(master);

		String point = "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
				+ submitLine.getOwnership().getOwner().getFullName() + "(기안)</span></span>"
				+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";

		point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";

		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(master);
		for (int i = 0; i < agreeLines.size(); i++) {

			ApprovalLine agreeLine = (ApprovalLine) agreeLines.get(i);

			if (agreeLine.getState().equals(WorkspaceHelper.STATE_AGREE_COMPLETE)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active2'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "(합의)</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "(합의)</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}
			if (i != agreeLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}

		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
		for (int i = 0; i < approvalLines.size(); i++) {
			ApprovalLine approvalLine = (ApprovalLine) approvalLines.get(i);

			if (i == 0) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}

			if (approvalLine.getState().equals(WorkspaceHelper.STATE_APPROVAL_APPROVING)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "(결재)</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else if (approvalLine.getState().equals(WorkspaceHelper.STATE_APPROVAL_COMPLETE)) {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "(결재완료)</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "(결재대기)</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}

			if (i != approvalLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}

		setPoint(point);
	}

	/*
	 * 객체 타입 세팅
	 */
	private void persistInfo(ApprovalMaster master) throws Exception {
		Persistable per = master.getPersist();
		String persistType = "없음";
		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			if ("$$MMDocument".equals(doc.getDocType().toString())) {
				persistType = "금형문서";
			} else if ("$$ROHS".equals(doc.getDocType().toString())) {
				persistType = "ROHS";
			} else {
				persistType = "문서";
			}
		} else if (per instanceof WTPart) {
			persistType = "품목";
		} else if (per instanceof EPMDocument) {
			persistType = "도면";
		} else if (per instanceof ECRMRequest) {
			persistType = "ECRM";
		} else if (per instanceof EChangeRequest) {
			persistType = "CR";
		} else if (per instanceof EChangeOrder) {
			EChangeOrder e = (EChangeOrder) per;
			String t = e.getEoType();
			if ("CHANGE".equals(t)) {
				persistType = "ECO";
			} else {
				persistType = "EO";
			}
		} else if (per instanceof ECPRRequest) {
			persistType = "ECPR";
		} else if (per instanceof EChangeNotice) {
			persistType = "ECN";
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			String number = asm.getNumber();
			if (number.startsWith("NDBT")) {
				persistType = "일괄결재(문서)";
			} else if (number.startsWith("ROHSBT")) {
				persistType = "일괄결재(ROHS)";
			} else if (number.startsWith("MMBT")) {
				persistType = "일괄결재(금형문서)";
			} else if (number.startsWith("AMBT")) {
				persistType = "일괄결재(병리연구문서)";
			} else if (number.startsWith("BMBT")) {
				persistType = "일괄결재(임상개발문서)";
			} else if (number.startsWith("CMBT")) {
				persistType = "일괄결재(화장품문서)";
			}
		}
		setPersistType(persistType);
		setPoid(per.getPersistInfo().getObjectIdentifier().getStringValue());
	}
}
