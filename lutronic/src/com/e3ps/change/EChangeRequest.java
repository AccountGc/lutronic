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

import com.e3ps.change.ECOChange;
import com.e3ps.common.util.OwnPersistable;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
import java.lang.String;
//import java.sql.SQLException;
import wt.content.ContentHolder;
//import wt.fc.ObjectReference;
import wt.inf.container.WTContained;
//import wt.org.WTUser;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newEChangeRequest</code> static factory method(s), not
 * the <code>EChangeRequest</code> constructor, to construct instances of
 * this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=ECOChange.class, interfaces={OwnPersistable.class, WTContained.class, ContentHolder.class},
   properties={
   @GeneratedProperty(name="createDate", type=String.class),
   @GeneratedProperty(name="approveDate", type=String.class),
   @GeneratedProperty(name="createDepart", type=String.class),
   @GeneratedProperty(name="writer", type=String.class),
   @GeneratedProperty(name="proposer", type=String.class),
   @GeneratedProperty(name="changeSection", type=String.class)
   },
   foreignKeys={
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="worker", type=wt.org.WTUser.class),
      myRole=@MyRole(name="ecr"))
   })
public class EChangeRequest extends _EChangeRequest {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    EChangeRequest
    * @exception wt.util.WTException
    **/
   public static EChangeRequest newEChangeRequest()
            throws WTException {

      EChangeRequest instance = new EChangeRequest();
      instance.initialize();
      return instance;
   }

}
