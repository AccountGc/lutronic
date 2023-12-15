package com.e3ps.doc.access;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.folder.Folder;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "folder", type = Folder.class),

		roleB = @GeneratedRole(name = "user", type = WTUser.class)

)
public class FolderAccessWTUserLink extends _FolderAccessWTUserLink {
	static final long serialVersionUID = 1;

	public static FolderAccessWTUserLink newFolderAccessWTUserLink(Folder folder, WTUser user) throws WTException {
		FolderAccessWTUserLink instance = new FolderAccessWTUserLink();
		instance.initialize(folder, user);
		return instance;
	}
}
