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

//import com.e3ps.change.ECOChange;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
import java.lang.String;
//import java.sql.SQLException;
import java.sql.Timestamp;
//import java.util.Vector;
import wt.content.ContentHolder;
//import wt.content.HttpContentOperation;
import wt.enterprise.Managed;
//import wt.fc.ObjectReference;
//import wt.fc.Persistable;
import wt.inf.container.WTContained;
//import wt.inf.container.WTContainer;
//import wt.inf.container.WTContainerRef;
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
 * Use the <code>newEChangeActivity</code> static factory method(s), not
 * the <code>EChangeActivity</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
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
   @GeneratedProperty(name="step", type=String.class),
   @GeneratedProperty(name="activeState", type=String.class),
   @GeneratedProperty(name="finishDate", type=Timestamp.class),
   @GeneratedProperty(name="sortNumber", type=int.class),
   @GeneratedProperty(name="activeType", type=String.class,
      javaDoc="CHIEF , WORKING"),
   @GeneratedProperty(name="description", type=String.class,
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="comments", type=String.class,
      constraints=@PropertyConstraints(upperLimit=4000))
   },
   foreignKeys={
   @GeneratedForeignKey(name="EOActivityLink", myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="eo", type=com.e3ps.change.ECOChange.class,
         constraints=@PropertyConstraints(required=true)),
      myRole=@MyRole(name="activity")),
   @GeneratedForeignKey(myRoleIsRoleA=false,
      foreignKeyRole=@ForeignKeyRole(name="activeUser", type=wt.org.WTUser.class),
      myRole=@MyRole(name="activity"))
   })
public class EChangeActivity extends _EChangeActivity {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    EChangeActivity
    * @exception wt.util.WTException
    **/
   public static EChangeActivity newEChangeActivity()
            throws WTException {

      EChangeActivity instance = new EChangeActivity();
      instance.initialize();
      return instance;
   }

}
