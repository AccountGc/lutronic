/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package com.e3ps.rohs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.doc.WTDocument;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

@GenAsPersistable(superClass = WTDocument.class, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "vendor", type = String.class) })
public class ROHSMaterial extends _ROHSMaterial {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @return ROHSMaterial
	 * @exception wt.util.WTException
	 **/
	public static ROHSMaterial newROHSMaterial() throws WTException {

		ROHSMaterial instance = new ROHSMaterial();
		instance.initialize();
		return instance;
	}

}
