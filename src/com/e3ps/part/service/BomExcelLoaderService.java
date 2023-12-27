package com.e3ps.part.service;

import java.util.Hashtable;
import java.util.Vector;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface BomExcelLoaderService {

	Vector<Hashtable> loadData(String fileName) throws WTException;

}
