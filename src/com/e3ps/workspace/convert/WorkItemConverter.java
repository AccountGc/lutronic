package com.e3ps.workspace.convert;

import com.e3ps.workspace.service.WorkspaceHelper;

public class WorkItemConverter {

	public static void main(String[] args) throws Exception {
		WorkItemConverter converter = new WorkItemConverter();
		WorkspaceHelper.service.convert();
	}
}
