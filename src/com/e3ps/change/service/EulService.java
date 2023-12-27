package com.e3ps.change.service;

import java.util.Vector;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOEul;

import wt.part.WTPart;
import wt.vc.baseline.ManagedBaseline;

public interface EulService {

	void completeEBOM(EChangeOrder eco) throws Exception;

	Vector<EOEul> getEoEul(EChangeOrder eco);

	Vector<String> getEoEulTopTopAssy(EChangeOrder eco);

	ManagedBaseline createBaseline(EOEul eulb) throws Exception;

	ManagedBaseline createBaseline(WTPart wtpart, EOEul eulb) throws Exception;

}
