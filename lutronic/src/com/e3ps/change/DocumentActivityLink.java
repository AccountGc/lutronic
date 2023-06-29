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

import com.e3ps.change.EChangeActivity;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectToObjectLink;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newDocumentActivityLink</code> static factory method(s),
 * not the <code>DocumentActivityLink</code> constructor, to construct instances
 * of this class.  Instances must be constructed using the static factory(s),
 * in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsBinaryLink(superClass=ObjectToObjectLink.class, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="branchIdentifier", type=long.class),
   @GeneratedProperty(name="docClassName", type=String.class,
      constraints=@PropertyConstraints(upperLimit=50))
   },
   roleA=@GeneratedRole(name="doc", type=WTDocumentMaster.class,
      cardinality=Cardinality.ONE),
   roleB=@GeneratedRole(name="activity", type=EChangeActivity.class),
   tableProperties=@TableProperties(tableName="DocumentActivityLink")
)
public class DocumentActivityLink extends _DocumentActivityLink {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @param     doc
    * @param     activity
    * @return    DocumentActivityLink
    * @exception wt.util.WTException
    **/
   public static DocumentActivityLink newDocumentActivityLink( WTDocumentMaster doc, EChangeActivity activity )
            throws WTException {

      DocumentActivityLink instance = new DocumentActivityLink();
      instance.initialize( doc, activity );
      return instance;
   }

}
