package com.e3ps.distribute.util;

import java.util.Enumeration;

import com.e3ps.common.util.CommonUtil;

import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;

public class DistUtils {

	private DistUtils() {

	}

	public static int getType() throws Exception {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		return getType(user);
	}

	public static int getType(WTUser user) throws Exception {

		if (CommonUtil.isAdmin()) {
			return 2;
		}

		Enumeration en = user.parentGroups(false);
		while (en.hasMoreElements()) {
			Object obj = (Object) en.nextElement();
			if (obj instanceof WTOrganization) {
				continue;
			}
			WTPrincipalReference ref = (WTPrincipalReference) obj;
			WTGroup group = (WTGroup) ref.getPrincipal();

			if (!group.getDomainRef().equals(user.getDomainRef())) {
				continue;
			}

			System.out.println("group=" + group.getBusinessType());
			if (group.getBusinessType().equals("WTOrganization")) {
				if (group.getName().equals("DistributeInner")) {
					if (CommonUtil.isMember("DistributeInner", user)) {
						return 1;
					}
				} else {
					return 2;
				}
			}
		}
		return 3;
	}
}
