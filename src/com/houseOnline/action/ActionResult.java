/*
 * ActionResult.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import com.houseOnline.common.LoggerFactory;

/**
 * <p>ClassName: ActionResult</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class ActionResult {
  private boolean success;
  private String message;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public InputStream getStream() {
    try {
      InputStream resultStream = new ByteArrayInputStream(message.getBytes("utf-8"));
      return resultStream;
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed wnen converting result message", e);
      return new StringBufferInputStream("");
    }
  }
}
