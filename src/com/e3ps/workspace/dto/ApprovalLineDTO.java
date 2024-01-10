package com.e3ps.workspace.dto;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.service.WorkspaceHelper;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
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
	private Persistable persist;

	private boolean isApprovalLine = false;
	private boolean isAgreeLine = false;
	private boolean isReceiveLine = false;

	private String viewUrl;
	private final String context = "/Windchill/plm";

	public ApprovalLineDTO() {

	}

	public ApprovalLineDTO(String oid) throws Exception {
		this((ApprovalLine) CommonUtil.getObject(oid));
	}

	public ApprovalLineDTO(ApprovalMaster m) throws Exception {
		Persistable per = m.getPersist();
		setOid(m.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(per.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(m.getName());
		setState(m.getState());
		setPersist(per);
		url(per);
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
		setPersist(per);
		url(per);
	}

	private void url(Persistable per) throws Exception {
		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			String docType = doc.getDocType().toString();
			if ("$$MMDocument".equals(docType)) {
				setViewUrl(this.context + "/mold/view?oid=" + getPoid());
			} else if ("$$ROHS".equals(docType)) {
				setViewUrl(this.context + "/rohs/view?oid=" + getPoid());
			} else {
				setViewUrl(this.context + "/doc/view?oid=" + getPoid());
			}
		} else if (per instanceof EChangeRequest) {
			EChangeRequest ecr = (EChangeRequest) per;
			setViewUrl(this.context + "/cr/view?oid=" + getPoid());
		} else if (per instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) per;
			if (eco.getEoType().equals("CHANGE")) {
				setViewUrl(this.context + "/eco/view?oid=" + getPoid());
			} else {
				setViewUrl(this.context + "/eo/view?oid=" + getPoid());
			}
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			setViewUrl(this.context + "/asm/view?oid=" + getPoid());
		} else if (per instanceof ECPRRequest) {
			ECPRRequest ecpr = (ECPRRequest) per;
			setViewUrl(this.context + "/ecpr/view?oid=" + getPoid());
		} else if (per instanceof ECRMRequest) {
			ECRMRequest ecrm = (ECRMRequest) per;
			setViewUrl(this.context + "/ecrm/view?oid=" + getPoid());
		}
	}

	private void setLineType(ApprovalLine line) throws Exception {
		setApprovalLine(line.getType().equals(WorkspaceHelper.APPROVAL_LINE));
		setAgreeLine(line.getType().equals(WorkspaceHelper.AGREE_LINE));
		setReceiveLine(line.getType().equals(WorkspaceHelper.RECEIVE_LINE));
	}
}