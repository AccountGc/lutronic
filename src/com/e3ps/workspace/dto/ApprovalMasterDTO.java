package com.e3ps.workspace.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.ApprovalMaster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalMasterDTO {

	private String oid;
	private String name;

	public ApprovalMasterDTO() {

	}

	public ApprovalMasterDTO(String oid) throws Exception {
		this((ApprovalMaster) CommonUtil.getObject(oid));
	}

	public ApprovalMasterDTO(ApprovalMaster master) throws Exception {
		setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(master.getName());
	}
}
