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

import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

/**
 * departmentName : ?????processOrder : ?? ????comment : ?????activityName
 * : ?? ????state : ????( ??, ??, ??, ?? ??)??processDate
 * : ??????approver : ???(only name)
 * <p>
 * Use the <code>newWFItemUserLink</code> static factory method(s), not
 * the <code>WFItemUserLink</code> constructor, to construct instances of
 * this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToObjectLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="departmentName", type=String.class,
      constraints=@PropertyConstraints(upperLimit=50)),
   @GeneratedProperty(name="processOrder", type=int.class),
   @GeneratedProperty(name="comment", type=Object.class,
      columnProperties=@ColumnProperties(columnName="wfComment", columnType=ColumnType.BLOB)),
   @GeneratedProperty(name="activityName", type=String.class,
      constraints=@PropertyConstraints(upperLimit=50)),
   @GeneratedProperty(name="state", type=String.class,
      constraints=@PropertyConstraints(upperLimit=20)),
   @GeneratedProperty(name="processDate", type=Timestamp.class),
   @GeneratedProperty(name="receiveDate", type=Timestamp.class),
   @GeneratedProperty(name="deleteFlag", type=boolean.class, initialValue="false"),
   @GeneratedProperty(name="approver", type=String.class,
      constraints=@PropertyConstraints(upperLimit=30)),
   @GeneratedProperty(name="seqNo", type=int.class),
   @GeneratedProperty(name="disabled", type=boolean.class),
   @GeneratedProperty(name="commissionDabled", type=boolean.class)
   },
   roleA=@GeneratedRole(name="user", type=WTUser.class),
   roleB=@GeneratedRole(name="wfitem", type=WFItem.class,
      cardinality=Cardinality.ZERO_TO_ONE),
   tableProperties=@TableProperties(tableName="WFItemUserLink")
)
public class WFItemUserLink extends _WFItemUserLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     user
    * @param     wfitem
    * @return    WFItemUserLink
    * @exception wt.util.WTException
    **/
   public static WFItemUserLink newWFItemUserLink( WTUser user, WFItem wfitem )
            throws WTException {

      WFItemUserLink instance = new WFItemUserLink();
      instance.initialize( user, wfitem );
      return instance;
   }

}
