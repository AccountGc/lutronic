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

import com.e3ps.development.devActive;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.enterprise.RevisionControlled;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.ObjectToVersionLink;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newdevOutPutLink</code> static factory method(s), not the
 * <code>devOutPutLink</code> constructor, to construct instances of this
 * class.  Instances must be constructed using the static factory(s), in
 * order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToVersionLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   roleA=@GeneratedRole(name="act", type=devActive.class),
   roleB=@GeneratedRole(name="output", type=RevisionControlled.class),
   tableProperties=@TableProperties(tableName="devOutPutLink")
)
public class devOutPutLink extends _devOutPutLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     act
    * @param     output
    * @return    devOutPutLink
    * @exception wt.util.WTException
    **/
   public static devOutPutLink newdevOutPutLink( devActive act, RevisionControlled output )
            throws WTException {

      devOutPutLink instance = new devOutPutLink();
      instance.initialize( act, output );
      return instance;
   }

}
