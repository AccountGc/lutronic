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

package com.e3ps.groupware.notice;

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
import wt.content.HttpContentOperation;
import wt.fc.Item;
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
 * Use the <code>newNotice</code> static factory method(s), not the <code>Notice</code>
 * constructor, to construct instances of this class.  Instances must be
 * constructed using the static factory(s), in order to ensure proper initialization
 * of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Item.class, interfaces={ContentHolder.class, OwnPersistable.class}, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="title", type=String.class),
   @GeneratedProperty(name="contents", type=String.class,constraints=@PropertyConstraints(upperLimit=6000)),
   @GeneratedProperty(name="isPopup", type=boolean.class),
   @GeneratedProperty(name="count", type=int.class,   javaDoc="0")
   })
   
public class Notice extends _Notice {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    Notice
    * @exception wt.util.WTException
    **/
   public static Notice newNotice()
            throws WTException {

      Notice instance = new Notice();
      instance.initialize();
      return instance;
   }

}
