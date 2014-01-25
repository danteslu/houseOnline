/*
 * DemoAction.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.json.simple.JSONObject;

import com.houseOnline.crud.MongoDao;
import com.houseOnline.model.User;

/**
 * <p>ClassName: DemoAction</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 15, 2013
 */
public class DemoAction extends AbstractAction {

  /*
   * Fields
   */
  private List<User> users = new ArrayList<User>();
  private User user;

  /*
   * Methods
   */

  /**
   * 返回一个页面
   * 如果要返回一个页面的片段也可以通过该方法返回，如果需要指定返回的类型可以设置contentType参数
   * 使用freemarker来实现
   */
  @Action(value = "/demo/", results = { @Result(name = "success", type = "freemarker", location = "/views/demo.htm") })
  public String returnPage() {
    try {
      // Try to get users
      Map<String, Object> map = MongoDao.readModels(logger, "user", null);
      if (map != null) {
        for (String id : map.keySet()) {
          User u = (User) map.get(id);
          u.setId(id);
          users.add(u);
        }
      }

    } catch (Exception e) {
      logger.error("Failed in returnPage", e);
      SetFailResult();
    }
    return SUCCESS;
  }

  /**
   * 返回一个json
   * 使用stream来返回
   */
  @Action(value = "/demo/json", results = { @Result(name = "success", type = "stream", params = { "contentType",
      "application/json", "inputName", "result.stream" }) })
  public String returnJson() {
    try {
      User user = new User();
      user.setName("demo");
      SetSuccessJsonResult(user);
    } catch (Exception e) {
      logger.error("Failed in returnObjectJson", e);
      SetFailJsonResult();
    }
    return SUCCESS;
  }

  /**
   * 返回一个字符串，只返回字符串
   * 使用stream来返回，将返回的字符串设置到ActionResult的message字段中即可
   * 
   * 可以通过设置contentType控制返回的类型
   * text/html - 返回任意字符串 
   * text/xml - 返回XML，当然字符串应该是XML格式的 
   * application/json - 返回json，字符串应该是json格式的 
   */
  @Action(value = "/demo/string", results = { @Result(name = "success", type = "stream", params = { "contentType",
      "text/html", "inputName", "result.stream" }) })
  public String returnString() {
    SetSuccessResult("Return string demo.");
    return SUCCESS;
  }

  @Action(value = "/demo/xml", results = { @Result(name = "success", type = "stream", params = { "contentType",
      "text/xml", "inputName", "result.stream" }) })
  public String returnXml() {
    Document doc = DocumentHelper.createDocument();
    doc.add(DocumentHelper.createElement("xml"));
    doc.getRootElement().addText("Return XML demo.");
    SetSuccessResult(doc.asXML());
    return SUCCESS;
  }

  @Action(value = "/demo/jsonstring", results = { @Result(name = "success", type = "stream", params = { "contentType",
      "application/json", "inputName", "result.stream" }) })
  public String returnJsonString() {
    JSONObject root = new JSONObject();
    root.put("json", "Return Json demo.");
    SetSuccessResult(root.toJSONString());
    return SUCCESS;
  }

  /**
   * Create a new user
   * @return
   */
  @Action(value = "/demo/createUser", results = { @Result(name = "success", type = "stream", params = { "contentType",
      "application/json", "inputName", "result.stream" }) })
  public String createUser() {
    try {
      if (StringUtils.isBlank(user.getName())) {
        SetFailJsonResult(null, "Please input a user name");
        return SUCCESS;
      }

      if (StringUtils.isBlank(user.getEmail())) {
        SetFailJsonResult(null, "Please input a user email");
        return SUCCESS;
      }

      String id = MongoDao.newModel(logger, "user", user);
      if (StringUtils.isNotBlank(id)) {
        SetSuccessJsonResult();
      } else {
        SetFailJsonResult();
      }

    } catch (Exception e) {
      logger.error("Failed in createUser", e);
      SetFailResult();
    }
    return SUCCESS;
  }

  /*
   * Get & Set
   */
  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
