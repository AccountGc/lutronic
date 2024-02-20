package com.e3ps.workspace.dto;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.service.AsmHelper;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.org.WTUser;

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

	private boolean _delete = false;
	private boolean _modify = false;
	private boolean _withdraw = false;

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
		setAuth(asm);
	}

	private void setAuth(AsmApproval asm) throws Exception {
		WTUser user = CommonUtil.sessionUser();
		boolean isCreator = user.getName().equals(asm.getCreatorName());
		boolean isApproved = asm.getLifeCycleState().toString().equals("APPROVED");
		boolean isLine = asm.getLifeCycleState().toString().equals("LINE_REGISTER");
		boolean isAdmin = CommonUtil.isAdmin();
		if ((isAdmin || isCreator) && !isApproved) {
			set_delete(true);
		}

		if ((isAdmin || isCreator) && isLine) {
			set_modify(true);
		}

		if ((check(asm, "APPROVING")) && (isAdmin || isCreator)) {
			set_withdraw(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(AsmApproval asm, String state) throws Exception {
		boolean check = false;
		String compare = asm.getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}

}
