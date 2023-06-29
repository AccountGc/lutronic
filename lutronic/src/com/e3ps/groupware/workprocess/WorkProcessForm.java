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

package com.e3ps.groupware.workprocess;

import java.sql.Timestamp;

import wt.content.FormatContentHolder;
import wt.enterprise.Managed;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

/**
 * activieDate - ???:???? & ???:??????meeting - ????(12:25:12:50)$????
 * <p>
 * Use the <code>newWorkProcessForm</code> static factory method(s), not
 * the <code>WorkProcessForm</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Managed.class, interfaces={FormatContentHolder.class}, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="company", type=String.class,
      constraints=@PropertyConstraints(upperLimit=30)),
   @GeneratedProperty(name="formType", type=String.class,
      constraints=@PropertyConstraints(upperLimit=30)),
   @GeneratedProperty(name="number", type=String.class,
      constraints=@PropertyConstraints(upperLimit=20),
      columnProperties=@ColumnProperties(columnName="formNumber")),
   @GeneratedProperty(name="name", type=String.class,
      columnProperties=@ColumnProperties(persistent=true)),
   @GeneratedProperty(name="departmentName", type=String.class,
      constraints=@PropertyConstraints(upperLimit=30)),
   @GeneratedProperty(name="keepYear", type=String.class,
      constraints=@PropertyConstraints(upperLimit=10)),
   @GeneratedProperty(name="securityLevel", type=String.class,
      constraints=@PropertyConstraints(upperLimit=10)),
   @GeneratedProperty(name="reportType", type=String.class,
      constraints=@PropertyConstraints(upperLimit=40)),
   @GeneratedProperty(name="receive", type=Object.class,
      columnProperties=@ColumnProperties(columnType=ColumnType.BLOB)),
   @GeneratedProperty(name="activeDate", type=Timestamp.class),
   @GeneratedProperty(name="contents", type=Object.class,
      columnProperties=@ColumnProperties(columnType=ColumnType.BLOB)),
   @GeneratedProperty(name="meeting", type=String.class,
      constraints=@PropertyConstraints(upperLimit=40)),
   @GeneratedProperty(name="carbonCopy", type=Object.class,
      columnProperties=@ColumnProperties(columnType=ColumnType.BLOB)),
   @GeneratedProperty(name="attendance", type=String.class),
   @GeneratedProperty(name="foreignType", type=String.class)
   })
public class WorkProcessForm extends _WorkProcessForm {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    WorkProcessForm
    * @exception wt.util.WTException
    **/
   public static WorkProcessForm newWorkProcessForm()
            throws WTException {

      WorkProcessForm instance = new WorkProcessForm();
      instance.initialize();
      return instance;
   }

}
