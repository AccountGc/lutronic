package com.e3ps.doc.access;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.folder.Folder;
import wt.org.WTGroup;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "folder", type = Folder.class),

		roleB = @GeneratedRole(name = "group", type = WTGroup.class)

)
public class FolderAccessWTGroupLink extends _FolderAccessWTGroupLink {
	static final long serialVersionUID = 1;

	public static FolderAccessWTGroupLink newFolderAccessWTGroupLink(Folder folder, WTGroup group) throws WTException {
		FolderAccessWTGroupLink instance = new FolderAccessWTGroupLink();
		instance.initialize(folder, group);
		return instance;
	}
}
