package com.e3ps.part.service;

import wt.method.RemoteInterface;
import wt.vc.Versioned;

@RemoteInterface
public interface VersionService {

	boolean isLastVersion(Versioned versioned);

	boolean isLastIteration(Versioned versioned);

}
