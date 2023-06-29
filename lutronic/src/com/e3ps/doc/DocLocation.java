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

package com.e3ps.doc;

import wt.content.ContentHolder;
import wt.fc.InvalidAttributeException;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

/**
 *
 이 클래스는 테이블을 생성 하지 말고 아래와 같이 뷰를 생성 할것

CREATE VIEW DOCLOCATION (doc,loc,CREATESTAMPA2,UPDATESTAMPA2,UPDATECOUNTA2,CLASSNAMEA2A2,IDA2A2,MODIFYSTAMPA2,MARKFORDELETEA2) AS 
SELECT E.IDA2A2 doc,T.LOC loc,E.CREATESTAMPA2 CREATESTAMPA2,E.UPDATESTAMPA2 UPDATESTAMPA2,
E.UPDATECOUNTA2 UPDATECOUNTA2,E.CLASSNAMEA2A2 CLASSNAMEA2A2,E.IDA2A2 IDA2A2,
E.MODIFYSTAMPA2 MODIFYSTAMPA2,E.MARKFORDELETEA2 MARKFORDELETEA2
FROM WTDOCUMENT E, (
SELECT F2ID,SYS_CONNECT_BY_PATH(F2N,'/') LOC FROM (
  SELECT F1.NAME F1N,F2.NAME F2N,F1.IDA2A2 F1ID,F2.IDA2A2 F2ID FROM SUBFOLDER F1,SUBFOLDER F2, SUBFOLDERLINK L WHERE F1.IDA2A2=L.IDA3A5 AND F2.IDA2A2=L.IDA3B5
)
start with F1N='Document' CONNECT BY PRIOR F2ID=F1ID
)
T WHERE E.IDA3B2FOLDERINGINFO=T.F2ID;

 *
 * @version   1.0
 * interfaces={ContentHolder.class}, 삭제
 **/
@GenAsPersistable(
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="doc", type=long.class),
   @GeneratedProperty(name="loc", type=String.class,
      constraints=@PropertyConstraints(upperLimit=2000))
   })
public class DocLocation extends _DocLocation {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    IssueRequest
    * @exception wt.util.WTException
    **/
   public static DocLocation newDocLocation()
            throws WTException {

	   DocLocation instance = new DocLocation();
      instance.initialize();
      return instance;
   }
   
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

  public void checkAttributes()
           throws InvalidAttributeException {

  }
}
