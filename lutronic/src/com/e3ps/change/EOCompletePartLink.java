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
 *
 * <p>
 * Use the <code>newEOCompletePartLink</code> static factory method(s),
 * not the <code>EOCompletePartLink</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToObjectLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="version", type=String.class)
   },
   roleA=@GeneratedRole(name="completePart", type=WTPartMaster.class),
   roleB=@GeneratedRole(name="eco", type=EChangeOrder.class),
   tableProperties=@TableProperties(tableName="EOCompletePartLink")
)
public class EOCompletePartLink extends _EOCompletePartLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     completePart
    * @param     eco
    * @return    EOCompletePartLink
    * @exception wt.util.WTException
    **/
   public static EOCompletePartLink newEOCompletePartLink( WTPartMaster completePart, EChangeOrder eco )
            throws WTException {

      EOCompletePartLink instance = new EOCompletePartLink();
      instance.initialize( completePart, eco );
      return instance;
   }

}
