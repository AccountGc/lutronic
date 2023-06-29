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

//import com.e3ps.rohs.ROHSMaterial;

//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
//import java.lang.String;
//import java.sql.SQLException;

//import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
//import wt.vc.VersionToVersionLink;

import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newEcrToEcrLink</code> static factory method(s), not
 * the <code>EcrToEcrLink</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToObjectLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   roleA=@GeneratedRole(name="used", type=EChangeRequest.class),
   roleB=@GeneratedRole(name="useBy", type=EChangeRequest.class),
   tableProperties=@TableProperties(tableName="EcrToEcrLink")
)
public class EcrToEcrLink extends _EcrToEcrLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     represent
    * @param     composition
    * @return    DocumentToDocumentLink
    * @exception wt.util.WTException
    **/
   public static EcrToEcrLink newEcrToEcrLink( EChangeRequest used, EChangeRequest useBy )
            throws WTException {

	   EcrToEcrLink instance = new EcrToEcrLink();
      instance.initialize( used, useBy );
      return instance;
   }

}
