/*
 * DefaultAction.java
 * 
 * All Rights Reserved.
 */
package com.sample.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.houseOnline.action.AbstractAction;

/**
 * <p>ClassName: DefaultAction</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 15, 2013
 */
public class DefaultAction extends AbstractAction {
  @Action(value = "/", results = { @Result(name = "success", type = "freemarker", location = "/views/index.htm") })
  public String index() {
    return SUCCESS;
  }
}
