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

package com.e3ps.change;

import com.e3ps.change.EOEul;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
//import java.lang.String;
//import java.sql.SQLException;
import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newEulPartLink</code> static factory method(s), not the
 * <code>EulPartLink</code> constructor, to construct instances of this
 * class.  Instances must be constructed using the static factory(s), in
 * order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToObjectLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="disabled", type=boolean.class, initialValue="false"),
   @GeneratedProperty(name="linkType", type=int.class)
   },
   roleA=@GeneratedRole(name="part", type=WTPart.class),
   roleB=@GeneratedRole(name="eul", type=EOEul.class,
      cardinality=Cardinality.ZERO_TO_ONE),
   tableProperties=@TableProperties(tableName="EulPartLink")
)
public class EulPartLink extends _EulPartLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     part
    * @param     eul
    * @return    EulPartLink
    * @exception wt.util.WTException
    **/
   public static EulPartLink newEulPartLink( WTPart part, EOEul eul )
            throws WTException {

      EulPartLink instance = new EulPartLink();
      instance.initialize( part, eul );
      return instance;
   }

}
