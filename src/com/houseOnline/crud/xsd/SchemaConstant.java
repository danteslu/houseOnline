/*
 * SchemaConstant.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud.xsd;

/**
 * <p>ClassName: SchemaConstant</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class SchemaConstant {
  public static String INPUT_TYPE_FILE = "file";
  public static String INPUT_TYPE_TEXT = "text";
  public static String INPUT_TYPE_URL = "url";
  public static String INPUT_TYPE_TEXTAREA = "textarea";
  public static String INPUT_TYPE_RTE = "rte";
  public static String INPUT_TYPE_BOOLEAN = "boolean";
  // If a group's first element is boolean, we will change it to checkbox so that the element can enable/disable a group
  public static String INPUT_TYPE_CHECKBOX = "checkbox";
  public static String INPUT_TYPE_DATE = "date";
  public static String INPUT_TYPE_DATETIME = "datetime";
  public static String INPUT_TYPE_TIME = "time";
  public static String INPUT_TYPE_SELECT = "select";
  public static String INPUT_TYPE_UNSUPPORTED = "unsupported";

  /**
   * Will be render as a div.group
   */
  public static String DIV_TYPE_GROUP = "group";

  /**
   * The group will be render as a table
   */
  public static String DIV_TYPE_TABLE_GROUP = "tableGroup";

  /**
   * Will be render as a table
   */
  public static String DIV_TYPE_LIST = "list";

  /**
   * Will be render as choice + divs
   */
  public static String DIV_TYPE_CHOICE = "choice";

}
