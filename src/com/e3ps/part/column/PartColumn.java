package com.e3ps.part.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.part.service.PartHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.vc.wip.WorkInProgressHelper;

@Getter
@Setter
public class PartColumn {

//	private boolean latest = false;
	private int rowNum;
	private String part_oid; // 부품
	private String epm_oid;
	private String epm_2d_oid;
	private String thumb;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String modifier;
	private String icon;
	private boolean checkout;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private boolean preOrder;
	private boolean visible = true;

	public PartColumn() {

	}

	public PartColumn(WTPart part) throws Exception {
		this(part, false);
	}

	public PartColumn(String oid, boolean eca) throws Exception {
		this((WTPart) CommonUtil.getObject(oid), eca);
	}

	public PartColumn(String oid) throws Exception {
		this((WTPart) CommonUtil.getObject(oid), false);
	}

	public PartColumn(Object[] obj) throws Exception {
		this((WTPart) obj[0], false);
	}

	public PartColumn(WTPart part, boolean eca) throws Exception {
//		setLatest(PartHelper.manager.isLatest(part));
		setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setThumb(ThumbnailUtil.thumbnailSmall(part));
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
//		setVersion(setVersionInfo(part));
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setRemarks(IBAUtil.getAttrValue(part, "REMARKS"));
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreatedDate(part.getCreateTimestamp());
		setCreatedDate_txt(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierFullName());
		setModifiedDate(part.getModifyTimestamp());
		setModifiedDate_txt(part.getModifyTimestamp().toString().substring(0, 10));
		boolean isCheckedOut = WorkInProgressHelper.isCheckedOut(part);
		if (isCheckedOut) {
			setIcon("/Windchill/extcore/images/icon/partcheckout.gif");
		} else {
			setIcon("/Windchill/extcore/images/icon/part.gif");
		}
		setCheckout(isCheckedOut);
		setEpmInfo(part);
		setView(part);
	}

	private void setView(WTPart part) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate((WTPartMaster) part.getMaster(), "eco", EcoPartLink.class);
		while (qr.hasMoreElements()) {
			EChangeOrder ee = (EChangeOrder) qr.nextElement();
			// 작업중 혹은 승인중?
			if (ee.getLifeCycleState().toString().equals("INWORK")
					|| ee.getLifeCycleState().toString().equals("LINE_REGISTER")
					|| ee.getLifeCycleState().toString().equals("APPROVING")
					|| ee.getLifeCycleState().toString().equals("ACTIVITY")) {
				setVisible(false);
			}
		}
	}

	private void setEpmInfo(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		if (epm != null) {
			setEpm_oid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
			EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
			if (epm2d != null) {
				setEpm_2d_oid(epm2d.getPersistInfo().getObjectIdentifier().getStringValue());
			}
		}
	}

	/**
	 * 최신버건과 함께 표시
	 */
//	private String setVersionInfo(WTPart part) throws Exception {
//		WTPart latest = PartHelper.manager.latest(getPart_oid());
//		String version = VersionControlHelper.getVersionDisplayIdentifier(part) + "."
//				+ part.getIterationIdentifier().getSeries().getValue();
//		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
//				+ latest.getIterationIdentifier().getSeries().getValue();
//		if (isLatest()) {
//			return version;
//		} else {
//			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
//		}
//	}
}
