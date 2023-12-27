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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.fc.InvalidAttributeException;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newActivityDefinition</code> static factory method(s),
 * not the <code>ActivityDefinition</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="name", type=String.class),
   @GeneratedProperty(name="name_eng", type=String.class,
      javaDoc="?? ??"),
   @GeneratedProperty(name="moduleType", type=String.class,
      javaDoc="?? ??"),
   @GeneratedProperty(name="description", type=String.class,
      javaDoc="??"),
   @GeneratedProperty(name="activeType", type=String.class,
      javaDoc="??r ??"),
   @GeneratedProperty(name="sortNumber", type=int.class),
   @GeneratedProperty(name="step", type=String.class,
      javaDoc="??r ??"),
   @GeneratedProperty(name="activeCode", type=String.class,
      javaDoc="??r ?? ??"),
   @GeneratedProperty(name="isApprove", type=boolean.class, initialValue="false",
      javaDoc="?? ?? (0,1)"),
   @GeneratedProperty(name="isNeed", type=boolean.class, initialValue="false",
      javaDoc="?? ?? ( 0 ,1)"),
   @GeneratedProperty(name="activeGroup", type=String.class,
      javaDoc="active ?? ??"),
   @GeneratedProperty(name="activeGroupName", type=String.class,
      javaDoc="active Group ??"),
   @GeneratedProperty(name="isDisabled", type=boolean.class, initialValue="false"),
   @GeneratedProperty(name="activeState", type=String.class)
   })
public class ActivityDefinition extends _ActivityDefinition {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    ActivityDefinition
    * @exception wt.util.WTException
    **/
   public static ActivityDefinition newActivityDefinition()
            throws WTException {

      ActivityDefinition instance = new ActivityDefinition();
      instance.initialize();
      return instance;
   }

   /**
    * Supports initialization, following construction of an instance.  Invoked
    * by "new" factory having the same signature.
    *
    * @exception wt.util.WTException
    **/
   protected void initialize()
            throws WTException {

   }

   /**
    * Gets the value of the attribute: IDENTITY.
    * Supplies the identity of the object for business purposes.  The identity
    * is composed of name, number or possibly other attributes.  The identity
    * does not include the type of the object.
    *
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated Replaced by IdentityFactory.getDispayIdentifier(object)
    * to return a localizable equivalent of getIdentity().  To return a
    * localizable value which includes the object type, use IdentityFactory.getDisplayIdentity(object).
    * Other alternatives are ((WTObject)obj).getDisplayIdentifier() and
    * ((WTObject)obj).getDisplayIdentity().
    *
    * @return    String
    **/
   public String getIdentity() {

      return null;
   }

   /**
    * Gets the value of the attribute: TYPE.
    * Identifies the type of the object for business purposes.  This is
    * typically the class name of the object but may be derived from some
    * other attribute of the object.
    *
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated Replaced by IdentityFactory.getDispayType(object) to return
    * a localizable equivalent of getType().  Another alternative is ((WTObject)obj).getDisplayType().
    *
    * @return    String
    **/
   public String getType() {

      return null;
   }

   @Override
   public void checkAttributes()
            throws InvalidAttributeException {

   }

}
