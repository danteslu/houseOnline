/*
 * DateHelper.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

/**
 * <p>ClassName: DateHelper</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class DateHelper {

  /*
   * Related Constant
   */
  public final static String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";

  public final static String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";

  public final static String DATE_FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

  public final static String DATE_FORMAT_yyMMddHHmmss = "yyMMddHHmmss";

  public final static String DATE_FORMAT_yyMMddHHmm = "yyMMddHHmm";

  public final static String DATE_FORMAT_HH_mm_ss = "HH:mm:ss";

  public final static String DATE_FORMAT_HH_mm = "HH:mm";

  public final static String DATE_FORMAT_MM_dd = "MM-dd";

  public final static String DATE_FORMAT_MM_dd_HH_mm = "MM-dd HH:mm";

  public final static String DATE_FORMAT_E = "E";

  public final static String DATE_FORMAT_MM = "MM";

  public final static String DATE_FORMAT_yyyy_MM = "yyyy-MM";

  /**
   * Parse a Date String to java.sql.Date
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static Date parseDate(String ds) throws ParseException {
    if (StringUtils.isBlank(ds)) {
      return null;
    }
    return Date.valueOf(ds);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDate(Date dt) {
    if (dt == null)
      return null;
    return new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd).format(dt);
  }

  public static String parseDate(java.util.Date dt) {
    if (dt == null)
      return null;
    return new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd).format(dt);
  }

  /**
   * Get current date's string
   * @return
   */
  public static String getCurrentDate() {
    return new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd).format(new Date(System.currentTimeMillis()));
  }

  /**
   * Parse a Date String to java.sql.Date
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static Date parseDateyyyyMM(String ds) throws ParseException {
    if (StringUtils.isBlank(ds)) {
      return null;
    }
    return new Date(new SimpleDateFormat(DATE_FORMAT_yyyy_MM).parse(ds).getTime());
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateMMdd(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_MM_dd).format(dt);
  }

  public static String parseDateMMdd1(java.util.Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_MM_dd).format(dt);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateYYMMddHHmmss(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd_HH_mm_ss).format(dt);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateYYMMddHHmmss1(Date date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_yyMMddHHmmss).format(date);
  }

  public static String parseDateYYMMddHHmmss1(java.util.Date date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_yyMMddHHmmss).format(date);
  }

  public static java.util.Date parseDateYYMMddHHmmss1(String ds) throws ParseException {
    return new Date(new SimpleDateFormat(DATE_FORMAT_yyMMddHHmmss).parse(ds).getTime());
  }

  public static String parseDateYYMMddHHmm(java.util.Date date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_yyMMddHHmm).format(date);
  }

  public static java.util.Date parseDateYYMMddHHmm(String ds) throws ParseException {
    return new Date(new SimpleDateFormat(DATE_FORMAT_yyMMddHHmm).parse(ds).getTime());
  }

  public static String parseDateHHmm(java.util.Date date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_HH_mm).format(date);
  }

  public static String parseDateMMddHHmm(java.util.Date date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_MM_dd_HH_mm).format(date);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateHHmmss(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_HH_mm_ss).format(dt);
  }

  /**
   * Parse a Date String to java.sql.Date
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static Date parseDateyyyyMMDD(String ds) throws ParseException {
    return new Date(new SimpleDateFormat(DATE_FORMAT_yyyyMMdd).parse(ds).getTime());
  }

  public static Date parseDateyyyy_MM_DD(String ds) throws ParseException {
    return new Date(new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd).parse(ds).getTime());
  }

  public static java.util.Date parseDateyyyy_MM_DD2(String ds) throws ParseException {
    return new java.util.Date(new SimpleDateFormat(DATE_FORMAT_yyyy_MM_dd).parse(ds).getTime());
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateE(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_E).format(dt);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateMM(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_MM).format(dt);
  }

  /**
   * Parse a java.sql.Date to String
   * 
   * @param ds
   * @return
   * @throws ParseException
   */
  public static String parseDateyyyyMM(Date dt) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_yyyy_MM).format(dt);
  }
}
