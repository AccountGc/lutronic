package com.e3ps.column;

import java.sql.Timestamp;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.ThumbnailUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;

@Getter
@Setter
public class PartColumn {

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String ecoNo;

	public PartColumn() {

	}

	public PartColumn(WTPart part) throws Exception {
		setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		set_3d(ThumbnailUtils.thumbnailSmall(part));
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setRemarks(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_REMARKS));
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorName());
		setCreateDate(part.getCreateTimestamp());
		setModifiedDate(part.getModifyTimestamp());
		setEcoNo("");
	}
}
