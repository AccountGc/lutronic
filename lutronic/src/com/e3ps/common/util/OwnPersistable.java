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

package com.e3ps.common.util;

import wt.org.WTPrincipalReference;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

/**
 *
 * @version   1.0
 **/

@GenAsPersistable(
   properties={
   @GeneratedProperty(name="owner", type=WTPrincipalReference.class,
      constraints=@PropertyConstraints(required=true))
   })
public interface OwnPersistable extends _OwnPersistable {






}
