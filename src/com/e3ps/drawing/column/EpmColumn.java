
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
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.part.WTPart;
import wt.util.IconSelector;
import wt.vc.VersionControlHelper;

@Getter
@Setter
public class EpmColumn {

	private String epm_oid; // 3D
	private String part_oid;
	private String thumb;
	private String icon;
//	private boolean latest;
	private int rowNum;
	private String cadType;
	private String cadTypeKey;
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

	public EpmColumn(String oid) throws Exception {
		this((EPMDocument) CommonUtil.getObject(oid));
	}

	public EpmColumn(Object[] obj) throws Exception {
		this((EPMDocument) obj[0]);
	}

	public EpmColumn(EPMDocument epm) throws Exception {
		setCadType(epm.getDocType().getDisplay());
		setCadTypeKey(epm.getDocType().toString());
		iconInfo(epm);
		setEpm_oid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
//		setLatest(DrawingHelper.manager.isLatest(epm));
		setThumb(ThumbnailUtil.thumbnailSmall(epm));
		setNumber(epm.getNumber());
		setName(epm.getName());
		setLocation(epm.getLocation());
		setVersion(epm.getVersionIdentifier().getSeries().getValue() + "."
				+ epm.getIterationIdentifier().getSeries().getValue());
		setState(epm.getLifeCycleState().getDisplay());
		setCreator(epm.getCreatorFullName());
		setCreatedDate(epm.getCreateTimestamp());
		setCreatedDate_txt(epm.getCreateTimestamp().toString().substring(0, 10));
		setModifier(epm.getModifierName());
		setModifiedDate(epm.getModifyTimestamp());
		setModifiedDate_txt(epm.getModifyTimestamp().toString().substring(0, 10));
		setPartInfo(epm);
	}

	private void setPartInfo(EPMDocument epm) throws Exception {
		WTPart part = PartHelper.manager.getPart(epm);
		if (part != null) {
			setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		}
	}

	/**
	 * 최신버건과 함께 표시
	 */
//	private String setVersionInfo(EPMDocument epm) throws Exception {
//		EPMDocument latest = DrawingHelper.manager.latest(getEpm_oid());
//		String version = VersionControlHelper.getVersionDisplayIdentifier(epm) + "."
//				+ epm.getIterationIdentifier().getSeries().getValue();
//		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
//				+ latest.getIterationIdentifier().getSeries().getValue();
//		if (isLatest()) {
//			return version;
//		} else {
//			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
//		}
//	}

	/**
	 * 도면 아이콘 세팅
	 */
	private void iconInfo(EPMDocument epm) throws Exception {
		IconDelegateFactory factory = IconDelegateFactory.getInstance();
		IconDelegate delegate = factory.getIconDelegate(epm);
		IconSelector selector = delegate.getOpenIconSelector();
		setIcon("/Windchill/" + selector.getIconKey());
	}
}