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

import java.io.IOException;
import java.io.ObjectInput;

import wt.enterprise.Managed;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.StringCase;


/**
 *
 * <p>
 * Use the <code>newAsmApproval</code> static factory method(s), not the
 * <code>AsmApproval</code> constructor, to construct instances of this
 * class.  Instances must be constructed using the static factory(s), in
 * order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Managed.class, versions={1963349263721597420L},
   properties={
   @GeneratedProperty(name="name", type=String.class,
      columnProperties=@ColumnProperties(persistent=true)),
   @GeneratedProperty(name="number", type=String.class,
      constraints=@PropertyConstraints(stringCase=StringCase.UPPER_CASE, upperLimit=20),
      columnProperties=@ColumnProperties(columnName="asmNumber")),
   @GeneratedProperty(name="description", type=String.class)
   })
public class AsmApproval extends _AsmApproval {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    AsmApproval
    * @exception wt.util.WTException
    **/
   public static AsmApproval newAsmApproval()
            throws WTException {

      AsmApproval instance = new AsmApproval();
      instance.initialize();
      return instance;
   }

   /**
    * Reads the non-transient fields of this class from an external source.
    *
    * @param     input
    * @param     readSerialVersionUID
    * @param     superDone
    * @return    boolean
    * @exception java.io.IOException
    * @exception java.lang.ClassNotFoundException
    **/
   boolean readVersion1963349263721597420L( ObjectInput input, long readSerialVersionUID, boolean superDone )
            throws IOException, ClassNotFoundException {

      if ( !superDone )                                             // if not doing backward compatibility
         super.readExternal( input );                               // handle super class

      description = (String)input.readObject();
      name = (String)input.readObject();
      number = (String)input.readObject();

      return true;
   }

}
