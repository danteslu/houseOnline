/*
 * CastorEnumConverter.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action.converter;

import java.lang.reflect.Method;
import java.util.Map;

import ognl.DefaultTypeConverter;

import org.apache.commons.lang3.StringUtils;

import com.houseOnline.common.LoggerFactory;

/**
 * <p>ClassName: CastorEnumConverter</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class CastorEnumConverter extends DefaultTypeConverter {
  @Override
  public Object convertValue(Map context, Object value, Class toType) {
    try {
      // For the Enum created by Castor
      if (toType.getSuperclass() == Enum.class && toType.getPackage().toString().indexOf(".schema.") >= 0) {
        String v = ((String[]) value)[0];
        if (StringUtils.isEmpty(v)) {
          return null;
        }

        Method staticMethod = toType.getMethod("fromValue", new Class[] { String.class });
        return staticMethod.invoke(null, new Object[] { v });
      }

      // For the normal Enum
      else if (toType.getSuperclass() == Enum.class) {
        String v = ((String[]) value)[0];
        return Enum.valueOf(toType, v);

        // For display
      } else if (toType == String.class) {
        return value.toString();
      }
      return null;
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed when converting castor enum", e);
      return null;
    }
  }
}
