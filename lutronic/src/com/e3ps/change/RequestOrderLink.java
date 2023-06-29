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
import com.e3ps.change.EChangeRequest;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
import java.lang.String;
//import java.sql.SQLException;
import wt.fc.ObjectToObjectLink;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newRequestOrderLink</code> static factory method(s), not
 * the <code>RequestOrderLink</code> constructor, to construct instances
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
   @GeneratedProperty(name="ecoType", type=String.class,
      javaDoc="owner??reference")
   },
   roleA=@GeneratedRole(name="eco", type=EChangeOrder.class),
   roleB=@GeneratedRole(name="ecr", type=EChangeRequest.class),
   tableProperties=@TableProperties(tableName="RequestOrderLink")
)
public class RequestOrderLink extends _RequestOrderLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     eco
    * @param     ecr
    * @return    RequestOrderLink
    * @exception wt.util.WTException
    **/
   public static RequestOrderLink newRequestOrderLink( EChangeOrder eco, EChangeRequest ecr )
            throws WTException {

      RequestOrderLink instance = new RequestOrderLink();
      instance.initialize( eco, ecr );
      return instance;
   }

}
