/*
 * ApplicationConfig.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.houseOnline.model.Application;

/**
 * <p>ClassName: ApplicationConfig</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class ApplicationConfig {

  private static Application instance;

  public static Application getInstance() {
    init(false);
    return instance;
  }

  public static void refresh() {
    init(true);
  }

  private synchronized static void init(boolean refresh) {
    if (instance == null || refresh) {
      InputStream fr = null;
      try {
        fr = ApplicationConfig.class.getResourceAsStream("/application.xml");
        instance = (Application) JAXBHelper.newInstance(new InputStreamReader(fr, "UTF-8"), Application.class);

      } catch (java.lang.Exception e) {
        LoggerFactory.getSystemLogger().error("Failed when parsing application.xml", e);
      } finally {
        if (fr != null) {
          try {
            fr.close();
          } catch (IOException e) {
            LoggerFactory.getSystemLogger().error("Failed when closing application.xml", e);
          }
          fr = null;
        }
      }
    }
  }
}
