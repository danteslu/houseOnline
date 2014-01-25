/*
 * AbstractAction.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action;

import org.apache.log4j.Logger;
import org.apache.struts2.json.JSONWriter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.houseOnline.common.LoggerFactory;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>ClassName: AbstractAction</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class AbstractAction extends ActionSupport {

  /*
   * Constant
   */
  public static String SUCCESS_MESSAGE = "成功处理了您的请求。";
  public static String FAIL_MESSAGE = "出现异常，请联系管理员。";

  /*
   * Fields
   */
  protected Logger logger = LoggerFactory.getSystemLogger();

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * The result of this action
   */
  protected ActionResult result;

  public void setResult(ActionResult result) {
    this.result = result;
  }

  public ActionResult getResult() {
    return result;
  }

  protected void SetSuccessResult() {
    SetSuccessResult(SUCCESS_MESSAGE);
  }

  protected void SetSuccessResult(String msg) {
    this.result = new ActionResult();
    this.result.setSuccess(true);
    this.result.setMessage(msg);
  }

  protected void SetFailResult() {
    SetFailResult(FAIL_MESSAGE);
  }

  protected void SetFailResult(String msg) {
    this.result = new ActionResult();
    this.result.setSuccess(false);
    this.result.setMessage(msg);
  }

  protected void SetSuccessJsonResult() {
    try {
      SetSuccessJsonResult(null, SUCCESS_MESSAGE);
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed in SetSuccessJsonResult", e);
    }
  }

  protected void SetSuccessJsonResult(Object o) throws Exception {
    SetSuccessJsonResult(o, SUCCESS_MESSAGE);
  }

  protected void SetSuccessJsonResult(Object o, String msg) throws Exception {
    this.result = new ActionResult();
    JSONObject root = genJsonResult(true, msg, o);
    this.result.setMessage(root.toJSONString());
  }

  protected void SetFailJsonResult() {
    try {
      SetFailJsonResult(null, FAIL_MESSAGE);
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed in SetFailedJsonResult", e);
    }
  }

  protected void SetFailJsonResult(Object o) throws Exception {
    SetFailJsonResult(o, FAIL_MESSAGE);
  }

  protected void SetFailJsonResult(Object o, String msg) throws Exception {
    this.result = new ActionResult();
    JSONObject root = genJsonResult(false, msg, o);
    this.result.setMessage(root.toJSONString());
  }

  protected JSONObject genJsonResult(boolean success, String msg, Object o) throws Exception {
    JSONObject root = new JSONObject();
    root.put("success", success);
    root.put("message", msg);
    if (o != null) {
      root.put("data", JSONValue.parse(new JSONWriter().write(o)));
    }
    return root;
  }

  public boolean hasActionErrors() {
    return false;
  }

  public boolean hasErrors() {
    return false;
  }

  public boolean hasFieldErrors() {
    return false;
  }

}
