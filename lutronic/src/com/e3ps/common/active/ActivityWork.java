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

package com.e3ps.common.active;

import com.e3ps.common.active.ActivityDefinition;
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
import wt.fc.Persistable;
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
 * Use the <code>newActivityWork</code> static factory method(s), not the
 * <code>ActivityWork</code> constructor, to construct instances of this
 * class.  Instances must be constructed using the static factory(s), in
 * order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Managed.class, interfaces={WTContained.class, ContentHolder.class}, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="activeState", type=String.class),
   @GeneratedProperty(name="finishDate", type=Timestamp.class),
   @GeneratedProperty(name="activeComment", type=String.class)
   },
   foreignKeys={
   @GeneratedForeignKey(name="ActivityWorkLink", myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="work", type=wt.fc.Persistable.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="activity")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="definition", type=com.e3ps.common.active.ActivityDefinition.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="work")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="workUser", type=wt.org.WTUser.class),
      myRole=@MyRole(name="work"))
   })
public class ActivityWork extends _ActivityWork {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    ActivityWork
    * @exception wt.util.WTException
    **/
   public static ActivityWork newActivityWork()
            throws WTException {

      ActivityWork instance = new ActivityWork();
      instance.initialize();
      return instance;
   }

}
