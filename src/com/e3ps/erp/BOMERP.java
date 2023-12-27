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

package com.e3ps.erp;

import com.e3ps.common.util.OwnPersistable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import java.util.Vector;

import wt.content.ContentHolder;
import wt.fc.InvalidAttributeException;
import wt.fc.Item;
import wt.fc.Persistable;
import wt.org.WTPrincipalReference;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newBOMERP</code> static factory method(s), not the <code>BOMERP</code>
 * constructor, to construct instances of this class.  Instances must be
 * constructed using the static factory(s), in order to ensure proper initialization
 * of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable( 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="zifno", type=String.class),
   @GeneratedProperty(name="zitmsta", type=String.class),
   @GeneratedProperty(name="matnr", type=String.class),
   @GeneratedProperty(name="posnr", type=Integer.class),
   @GeneratedProperty(name="idnrk", type=String.class),
   @GeneratedProperty(name="menge", type=String.class),
   @GeneratedProperty(name="meins", type=String.class),
   @GeneratedProperty(name="aennr", type=String.class),
   @GeneratedProperty(name="zifsta", type=String.class),
   @GeneratedProperty(name="zifmsg", type=String.class),
   @GeneratedProperty(name="returnZifsta", type=String.class),
   @GeneratedProperty(name="returnZifmsg", type=String.class),
   @GeneratedProperty(name="pver", type=String.class),
   @GeneratedProperty(name="cver", type=String.class)
   })
public class BOMERP extends _BOMERP {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    BOMERP
    * @exception wt.util.WTException
    **/
   public static BOMERP newBOMERP()
            throws WTException {

	  BOMERP instance = new BOMERP();
      instance.initialize();
      return instance;
   }




	private void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	@Override
	public void checkAttributes() throws InvalidAttributeException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	@Override
	public String getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

}
