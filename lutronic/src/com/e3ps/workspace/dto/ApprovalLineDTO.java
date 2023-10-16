package com.e3ps.workspace.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.service.WorkspaceHelper;

import lombok.Getter;
import lombok.Setter;
import wt.fc.Persistable;

@Getter
@Setter
public class ApprovalLineDTO {

	private String oid;
	private String poid;
	private String name;
	private String receiveTime;
	private String creator;
	private String state;
	private String type;
	private String description;
	private String role;
	private String submiter;

	private boolean isApprovalLine = false;
	private boolean isAgreeLine = false;
	private boolean isReceiveLine = false;

	public ApprovalLineDTO() {

	}

	public ApprovalLineDTO(String oid) throws Exception {
		this((ApprovalLine) CommonUtil.getObject(oid));
	}

	public ApprovalLineDTO(ApprovalLine line) throws Exception {
		ApprovalMaster master = line.getMaster();
		Persistable per = master.getPersist();
		setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(per.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(line.getName());
		setCreator(master.getOwnership().getOwner().getFullName());
		setReceiveTime(line.getCreateTimestamp().toString().substring(0, 16));
		setState(line.getState());
		setType(line.getType());
		setDescription(line.getDescription());
		setSubmiter(master.getOwnership().getOwner().getFullName());
		setRole(line.getRole());
		setLineType(line);
	}

	private void setLineType(ApprovalLine line) throws Exception {
		setApprovalLine(line.getType().equals(WorkspaceHelper.APPROVAL_LINE));
		setAgreeLine(line.getType().equals(WorkspaceHelper.AGREE_LINE));
		setReceiveLine(line.getType().equals(WorkspaceHelper.RECEIVE_LINE));

	}
}