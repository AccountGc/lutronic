package com.e3ps.common.util.service;

import java.util.ArrayList;

import com.e3ps.common.util.Tree;

import wt.method.RemoteInterface;

@RemoteInterface
public interface TreeService {

	ArrayList getAllChildList(Class treeClass, Tree tree, ArrayList returnlist);
}
