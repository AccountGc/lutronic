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

package com.e3ps.development;

import com.e3ps.development.devMaster;
import com.e3ps.development.devTask;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import wt.content.ContentHolder;
import wt.content.HttpContentOperation;
import wt.enterprise.Managed;
import wt.fc.ObjectReference;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newdevActive</code> static factory method(s), not the <code>devActive</code>
 * constructor, to construct instances of this class.  Instances must be
 * constructed using the static factory(s), in order to ensure proper initialization
 * of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Managed.class, interfaces={WTContained.class, ContentHolder.class}, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="name", type=String.class,
      columnProperties=@ColumnProperties(persistent=true)),
   @GeneratedProperty(name="description", type=String.class,
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="activeDate", type=String.class),
   @GeneratedProperty(name="finishDate", type=Timestamp.class),
   @GeneratedProperty(name="worker_comment", type=String.class,
   	  constraints=@PropertyConstraints(upperLimit=4000)),
   },
   foreignKeys={
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="task", type=com.e3ps.development.devTask.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="act")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="worker", type=wt.org.WTUser.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="act")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="dm", type=wt.org.WTUser.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="act")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="master", type=com.e3ps.development.devMaster.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="act"))
   })
public class devActive extends _devActive {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    devActive
    * @exception wt.util.WTException
    **/
   public static devActive newdevActive()
            throws WTException {

      devActive instance = new devActive();
      instance.initialize();
      return instance;
   }

}
