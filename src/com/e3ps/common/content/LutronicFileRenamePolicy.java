package com.e3ps.common.content;

import java.io.File;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class LutronicFileRenamePolicy extends DefaultFileRenamePolicy {

	@Override
	public File rename(File f) {
		if (f.exists()) {
			System.out.println("삭제가 되나?");
			f.delete();
		}
		return f;
	}
}
