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

import com.e3ps.change.EChangeOrder;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
import java.lang.String;
//import java.sql.SQLException;
import wt.fc.ObjectToObjectLink;
import wt.part.WTPartMaster;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 * visible : ??????? false? ? ????revise : ??? ????
 * <p>
 * Use the <code>newEcoPartLink</code> static factory method(s), not the
 * <code>EcoPartLink</code> constructor, to construct instances of this
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
   @GeneratedProperty(name="version", type=String.class,
      constraints=@PropertyConstraints(upperLimit=10)),
   @GeneratedProperty(name="visible", type=boolean.class, initialValue="true"),
   @GeneratedProperty(name="revise", type=boolean.class, initialValue="false"),
   @GeneratedProperty(name="baseline", type=boolean.class)
   },
   roleA=@GeneratedRole(name="part", type=WTPartMaster.class),
   roleB=@GeneratedRole(name="eco", type=EChangeOrder.class),
   tableProperties=@TableProperties(tableName="EcoPartLink")
)
public class EcoPartLink extends _EcoPartLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     part
    * @param     eco
    * @return    EcoPartLink
    * @exception wt.util.WTException
    **/
   public static EcoPartLink newEcoPartLink( WTPartMaster part, EChangeOrder eco )
            throws WTException {

      EcoPartLink instance = new EcoPartLink();
      instance.initialize( part, eco );
      return instance;
   }

}
