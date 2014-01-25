/*
 * JsonResult.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.json.JSONWriter;

import com.houseOnline.common.LoggerFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * <p>ClassName: JsonResult</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Jan 6, 2014
 */
public class JsonResult implements Result {

  /*
   * Fields
   */
  private String root;
  private String encoding = "utf-8";

  /* (non-Javadoc)
   * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
   */
  @Override
  public void execute(ActionInvocation invocation) throws Exception {
    ActionContext actionContext = invocation.getInvocationContext();
    HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);
    HttpServletResponse response = (HttpServletResponse) actionContext.get(StrutsStatics.HTTP_RESPONSE);

    try {
      Object rootObject;
      if (this.root != null) {
        ValueStack stack = invocation.getStack();
        rootObject = stack.findValue(root);
      } else {
        rootObject = invocation.getStack().peek(); // model overrides action
      }

      JSONWriter jsonWriter = new JSONWriter();
      String json = jsonWriter.write(rootObject);

      response.setContentType("application/json;charset=" + encoding);
      response.setContentLength(json.getBytes(encoding).length);
      PrintWriter out = response.getWriter();
      out.print(json);

    } catch (IOException e) {
      LoggerFactory.getSystemLogger().error("Failed in execute", e);
      throw e;
    }
  }

  /*
   * Get & Set
   */
  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

}
