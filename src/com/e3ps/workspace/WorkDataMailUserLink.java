package com.e3ps.workspace;

import com.e3ps.org.MailUser;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "workData", type = WorkData.class),

		roleB = @GeneratedRole(name = "mailUser", type = MailUser.class)

)
public class WorkDataMailUserLink extends _WorkDataMailUserLink {
	static final long serialVersionUID = 1;

	public static WorkDataMailUserLink newWorkDataMailUserLink(WorkData workData, MailUser mailUser)
			throws WTException {
		WorkDataMailUserLink instance = new WorkDataMailUserLink();
		instance.initialize(workData, mailUser);
		return instance;
	}

}
