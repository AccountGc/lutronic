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

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.inf.container.WTContained;
import wt.util.WTException;
import com.ptc.windchill.annotations.metadata.*;

/**
 *
 * <p>
 * Use the <code>newECOChange</code> static factory method(s), not the <code>ECOChange</code>
 * constructor, to construct instances of this class.  Instances must be
 * constructed using the static factory(s), in order to ensure proper initialization
 * of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(superClass=Managed.class, interfaces={WTContained.class, ContentHolder.class}, 
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="eoNumber", type=String.class,
      javaDoc="EO ??"),
   @GeneratedProperty(name="eoName", type=String.class,
      javaDoc="EO Name"),
   @GeneratedProperty(name="model", type=String.class, constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="eoCommentA", type=String.class,
      javaDoc="ECR : ECR ????ECO : ECO ????",
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="eoCommentB", type=String.class,
      javaDoc="ECR : ?? ????ECO : ?? ????",
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="eoCommentC", type=String.class,
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="eoCommentD", type=String.class,
      constraints=@PropertyConstraints(upperLimit=4000)),
   @GeneratedProperty(name="eoType", type=String.class),
   @GeneratedProperty(name="eoCommentE", type=String.class),
   @GeneratedProperty(name="eoApproveDate", type=String.class)
   })
public class ECOChange extends _ECOChange {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    ECOChange
    * @exception wt.util.WTException
    **/
   public static ECOChange newECOChange()
            throws WTException {

      ECOChange instance = new ECOChange();
      instance.initialize();
      return instance;
   }

}
