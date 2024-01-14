package com.e3ps.part.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.service.PartHelper;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String createDate;
	private String modifier;
	private String modifyDate;
	private String ecoNo;
	private boolean isLatest;

	private String epm_oid;
	private String epm_value;
	private String viewName;

	private boolean _delete = false;
	private boolean _modify = false;
	private boolean _revise = false;

	private Map<String, String> step = new HashMap<>();

	// 댓글
	private ArrayList<CommentsDTO> comments = new ArrayList<CommentsDTO>();

	public PartDTO(String oid) throws Exception {
		this((WTPart) CommonUtil.getObject(oid));
	}

	public PartDTO(WTPart part) throws Exception {
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreateDate(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierFullName());
		setModifyDate(part.getModifyTimestamp().toString().substring(0, 10));
		setComments(CommentsHelper.manager.comments(part));
		setLatest(PartHelper.manager.isLatest(part));

		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		if (epm != null) {
			setEpm_oid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
			setEpm_value(epm.getNumber() + " [" + epm.getName() + "]");
		}

		View view = ViewHelper.getView(part);
		setViewName(view == null ? "" : view.getName());
		setAuth(part);
	}

	/**
	 * 권한
	 */
	private void setAuth(WTPart part) throws Exception {
		boolean isAdmin = CommonUtil.isAdmin();
		if (isLatest() && (check(part, "INWORK"))) {
			set_modify(true);
		}

		if (isAdmin) {
			set_delete(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(WTPart part, String state) throws Exception {
		boolean check = false;
		String compare = part.getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}
}
