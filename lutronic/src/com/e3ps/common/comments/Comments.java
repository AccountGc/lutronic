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

package com.e3ps.common.comments;

import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.fc.InvalidAttributeException;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;

import com.e3ps.common.util.OwnPersistable;
import com.e3ps.common.util.Tree;
import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

/**
 *
 * <p>
 * Use the <code>newComments</code> static factory method(s), not the
 * <code>Comments</code> constructor, to construct instances of this class.
 *  Instances must be constructed using the static factory(s), in order
 * to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass = WTObject.class, interfaces={OwnPersistable.class, ContentHolder.class},

serializable=Serialization.EXTERNALIZABLE_BASIC,

properties={
		@GeneratedProperty(name="comments", type=String.class,
		    constraints=@PropertyConstraints(upperLimit=1000)),
		
		@GeneratedProperty(name="cNum", type=Integer.class),
		
		@GeneratedProperty(name="cStep", type=Integer.class),
		
		@GeneratedProperty(name="cLevel", type=Integer.class),
		
		@GeneratedProperty(name="deleteYN", type=String.class),
},

foreignKeys = {

		@GeneratedForeignKey(name = "wtDocumentCommentsLink",

				foreignKeyRole = @ForeignKeyRole(name = "wtdocument", type = WTDocument.class,

				constraints = @PropertyConstraints(required = true)),

				myRole = @MyRole(name = "comments")),
		
		@GeneratedForeignKey(name = "wtPartCommentsLink",
		
			foreignKeyRole = @ForeignKeyRole(name = "wtpart", type = WTPart.class,
			
			constraints = @PropertyConstraints(required = true)),
			
			myRole = @MyRole(name = "comments", cardinality = Cardinality.ZERO_TO_ONE)),
})
public class Comments extends _Comments {
   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    Comments
    * @exception wt.util.WTException
    **/
   public static Comments newComments()
            throws WTException {

      Comments instance = new Comments();
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
