/*
 * LoggerFactory.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import org.apache.log4j.Logger;

/**
 * <p>ClassName: LoggerFactory</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li>Create loggers for different usage</li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class LoggerFactory {

  /**
   * Get System logger
   * The logger should be configured in log4j.properties
   * @return
   */
  public static Logger getSystemLogger() {
    return Logger.getLogger("system");
  }
}
