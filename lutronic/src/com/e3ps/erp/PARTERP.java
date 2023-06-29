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
 * Use the <code>newPARTERP</code> static factory method(s), not the <code>PARTERP</code>
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
   @GeneratedProperty(name="matnr", type=String.class),
   @GeneratedProperty(name="maktx", type=String.class),
   @GeneratedProperty(name="meins", type=String.class),
   @GeneratedProperty(name="matkl", type=String.class),
   @GeneratedProperty(name="ntgew", type=String.class),
   @GeneratedProperty(name="gewei", type=String.class),
   @GeneratedProperty(name="wrkst", type=String.class), 
   @GeneratedProperty(name="zspec", type=String.class),
   @GeneratedProperty(name="zfinsh", type=String.class),
   @GeneratedProperty(name="zmodel", type=String.class),
   @GeneratedProperty(name="zprodm", type=String.class), 
   @GeneratedProperty(name="zdept", type=String.class), 
   @GeneratedProperty(name="zmat1", type=String.class),
   @GeneratedProperty(name="zmat2", type=String.class),
   @GeneratedProperty(name="zmat3", type=String.class),
   @GeneratedProperty(name="aennr", type=String.class),
   @GeneratedProperty(name="zeivr", type=String.class),
   @GeneratedProperty(name="zifsta", type=String.class),
   @GeneratedProperty(name="zifmsg", type=String.class),
   @GeneratedProperty(name="returnZifsta", type=String.class),
   @GeneratedProperty(name="returnZifmsg", type=String.class)
   })
public class PARTERP extends _PARTERP {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    PARTERP
    * @exception wt.util.WTException
    **/
   public static PARTERP newPARTERP()
            throws WTException {

	  PARTERP instance = new PARTERP();
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
