package com.e3ps.workspace.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.service.AsmHelper;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;
import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsmDTO {

	private String oid;
	private String name;
	private String number;
	private String state;
	private String creator;
	private String createdDate_txt;
	private String modifiedDate_text;
	private String description;
	private String type;
	private JSONArray data = new JSONArray();

	public AsmDTO() {

	}

	public AsmDTO(String oid) throws Exception {
		this((AsmApproval) CommonUtil.getObject(oid));
	}

	public AsmDTO(AsmApproval asm) throws Exception {
		setOid(asm.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(asm.getName());
		setNumber(asm.getNumber());
		setState(asm.getLifeCycleState().getDisplay());
		setCreator(asm.getCreatorFullName());
		setCreatedDate_txt(asm.getCreateTimestamp().toString().substring(0, 16));
		setType(AsmHelper.manager.getAsmType(asm));
		setData(AsmHelper.manager.data(asm));
		setDescription(asm.getDescription());
		setModifiedDate_text(asm.getModifyTimestamp().toString().substring(0, 16));
	}
}
