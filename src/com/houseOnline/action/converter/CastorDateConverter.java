/*
 * CastorDateConverter.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action.converter;

import java.util.Map;

import ognl.DefaultTypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.exolab.castor.types.Date;

import com.houseOnline.common.DateHelper;
import com.houseOnline.common.LoggerFactory;

/**
 * <p>ClassName: CastorDateConverter</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class CastorDateConverter extends DefaultTypeConverter {
  @Override
  public Object convertValue(Map context, Object value, Class toType) {
    try {
      if (toType == Date.class) {
        String v = ((String[]) value)[0];
        if (StringUtils.isEmpty(v)) {
          return null;
        }
        return new Date(DateHelper.parseDateyyyy_MM_DD(v).getTime());
      } else if (toType == String.class) {
        return DateHelper.parseDate(new java.sql.Date(((Date) value).toLong()));
      }
      return null;
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed when converting castor date", e);
      return null;
    }
  }
}
