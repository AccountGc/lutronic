package com.e3ps.doc.template;

import java.sql.Blob;
import java.util.ArrayList;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.util.OwnPersistable;
import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.content.ContentHolder;
import wt.fc.InvalidAttributeException;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces={OwnPersistable.class, ContentHolder.class},

serializable=Serialization.EXTERNALIZABLE_BASIC,

properties={
		@GeneratedProperty(name="docTemplateNumber", type=String.class),
		
		@GeneratedProperty(name="name", type=String.class),
		
//		@GeneratedProperty(name="docTemplateType", type=NumberCode.class),
		
		@GeneratedProperty(name="description", type=ArrayList.class,
	      columnProperties=@ColumnProperties(columnType=ColumnType.BLOB)),
		
},
foreignKeys={
	@GeneratedForeignKey(myRoleIsRoleA=false,
   foreignKeyRole=@ForeignKeyRole(name="docTemplateType", type=com.e3ps.common.code.NumberCode.class,
      constraints=@PropertyConstraints(required=false)),
   myRole=@MyRole(name="documentTemplate"))
})
public class DocumentTemplate extends _DocumentTemplate{
   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    DocumentTemplate
    * @exception wt.util.WTException
    **/
   public static DocumentTemplate newDocumentTemplate()
            throws WTException {

	   DocumentTemplate instance = new DocumentTemplate();
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
