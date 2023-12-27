package com.e3ps.common.folder.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import wt.folder.Folder;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface CommonFolderService {

	JSONObject dhtmlxTree(Folder obj) throws WTException;

	ArrayList getFolderTree(Folder obj) throws WTException;

	ArrayList getFolderDTree(Folder obj) throws WTException;

	List<Map<String, Object>> getNextTree(Folder obj) throws WTException;

	JSONObject newGetTrees(Folder obj) throws WTException;

}
