package com.e3ps.drawing.column;

import java.sql.Timestamp;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.service.PartHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;

@Getter
@Setter
public class EpmColumn {

	private boolean latest = false;
	private String part_oid; // 부품
	private String epm_oid; // 3D
	private String drawing_oid; // 2D
	private String _3d;
	private String _2d;
	private String cadType;
	private String number;
	private String name;
	private String location;
	private String version;
	private String state;
	private String creator;
	private String modifier;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_txt;

	public EpmColumn() {

	}

	public EpmColumn(Object[] obj) throws Exception {
		this((EPMDocument) obj[0]);
	}

	public EpmColumn(EPMDocument epm) throws Exception {
		setCadType(epm.getDocType().getDisplay());
		setLatest(CommonUtil.isLatestVersion(epm));
		setEpm_oid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
		set_3d(ThumbnailUtil.thumbnailSmall(epm));
		setNumber(epm.getNumber());
		setName(epm.getName());
		setLocation(epm.getLocation());
		setVersion(setVersionInfo(epm));
		setState(epm.getLifeCycleState().getDisplay());
		setCreator(epm.getCreatorName());
		setCreatedDate(epm.getCreateTimestamp());
		setCreatedDate_txt(epm.getCreateTimestamp().toString().substring(0, 10));
		setModifier(epm.getModifierName());
		setModifiedDate(epm.getModifyTimestamp());
		setModifiedDate_txt(epm.getModifyTimestamp().toString().substring(0, 10));
		setThumbnail(epm);
	}

	/**
	 * 품목및 2D
	 */
	private void setThumbnail(EPMDocument epm) throws Exception {
		EPMDocument epm_d = PartHelper.manager.getEPMDocument2D(epm);
		if (epm_d != null) {
			set_2d(ThumbnailUtil.thumbnailSmall(epm_d));
			setDrawing_oid(epm_d.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		WTPart part = DrawingHelper.manager.getWTPart(epm);
		if (part != null) {
			setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		}
	}

	/**
	 * 최신버건과 함께 표시
	 */
	private String setVersionInfo(EPMDocument epm) throws Exception {
		EPMDocument latest = DrawingHelper.manager.latest(getEpm_oid());
		String version = VersionControlHelper.getVersionDisplayIdentifier(epm) + "."
				+ epm.getIterationIdentifier().getSeries().getValue();
		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
				+ latest.getIterationIdentifier().getSeries().getValue();
		if (isLatest()) {
			return version;
		} else {
			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
		}
	}
}