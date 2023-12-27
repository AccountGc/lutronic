package com.e3ps.part.column;

import java.sql.Timestamp;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.part.service.PartHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

@Getter
@Setter
public class PartColumn {

	private boolean latest = false;
	private String part_oid; // 부품
	private String epm_oid; // 3D
	private String drawing_oid; // 2D
	private String _3d;
	private String _2d;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String modifier;
	private boolean checkout;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private boolean preOrder;

	public PartColumn() {

	}

	public PartColumn(Object[] obj) throws Exception {
		this((WTPart) obj[0]);
	}

	public PartColumn(WTPart part) throws Exception {
		setLatest(CommonUtil.isLatestVersion(part));
		setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		set_3d(ThumbnailUtil.thumbnailSmall(part));
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(setVersionInfo(part));
		setRemarks(IBAUtil.getAttrValue(part, "REMARKS"));
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorName());
		setCreatedDate(part.getCreateTimestamp());
		setCreatedDate_txt(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierName());
		setModifiedDate(part.getModifyTimestamp());
		setModifiedDate_txt(part.getModifyTimestamp().toString().substring(0, 10));
		setThumbnail(part);
		setPreOrder(IBAUtil.getBooleanValue(part, "PREORDER"));
		setCheckout(WorkInProgressHelper.isCheckedOut(part));
	}

	/**
	 * 최신버건과 함께 표시
	 */
	private String setVersionInfo(WTPart part) throws Exception {
		WTPart latest = PartHelper.manager.latest(getPart_oid());
		String version = VersionControlHelper.getVersionDisplayIdentifier(part) + "."
				+ part.getIterationIdentifier().getSeries().getValue();
		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
				+ latest.getIterationIdentifier().getSeries().getValue();
		if (isLatest()) {
			return version;
		} else {
			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
		}
	}

	private void setThumbnail(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		// 3D 캐드 세팅..
		if (epm != null) {
			EPMDocument epm_d = PartHelper.manager.getEPMDocument2D(epm);
			setEpm_oid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
			if (epm_d != null) {
				set_2d(ThumbnailUtil.thumbnailSmall(epm_d));
				setDrawing_oid(epm_d.getPersistInfo().getObjectIdentifier().getStringValue());
			}
		}
	}
}
