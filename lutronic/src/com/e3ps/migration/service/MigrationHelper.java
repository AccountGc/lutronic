package com.e3ps.migration.service;

import wt.services.ServiceFactory;

public class MigrationHelper {
	public static final MigrationService service =ServiceFactory.getService(MigrationService.class);
}
