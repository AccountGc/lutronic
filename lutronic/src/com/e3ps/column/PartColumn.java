package com.e3ps.column;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;

@Getter
@Setter
public class PartColumn {

	private String part_oid; // 부품
	private String epm_oid; // 3D
	private String drawing_oid; // 2D

	public PartColumn() {

	}

	public PartColumn(WTPart part) throws Exception {

	}
}
