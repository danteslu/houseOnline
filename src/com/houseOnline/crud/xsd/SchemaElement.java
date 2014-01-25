/*
 * SchemaElement.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud.xsd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.fengweimin.common.PutTimeSortedMap;

/**
 * <p>ClassName: SchemaElement</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class SchemaElement {
  public String name = "";
  public Object value = "";
  public String label = "";
  public String description = "";
  public String type = "text";
  public boolean required = false;
  public boolean number = false;
  public boolean email = false;
  public boolean phone = false;
  public int maxLength = -1;
  public String minDate;
  public String maxDate;
  public int minNumber;
  public int maxNumber;

  /**
   * A element with max occur value unbounded
   */
  public boolean repeatable = false;

  /**
   * For a single field, it will not display for input; For list, the son field
   * will not be displayed as a col;
   * 
   * visable主要用于list/table中，表示显示为table时不显示为一列，但是具体编辑某行的时候依然可以编辑该element
   */
  public boolean visable = true;

  /**
   * 如果一个element disable为true, 则表示该element不可用，
   * 通常是一些尚未实现的功能，编辑和读取的时候均不显示
   */
  public boolean disable = false;

  public boolean isDisable() {
    return disable;
  }

  public void setDisable(boolean disable) {
    this.disable = disable;
  }

  /**
   * For text only, if readonly, can not be edit but need to be assigned the
   * value manually e.g, #model_prod_prodInfo_prodID
   */
  public boolean readonly = false;

  /**
   * For text inside a repeatable group. If producing a new group, the text's
   * value will be added by the increment. currently, can only have one
   * increment text inside a same group. e.g, prod.goodInfo.goodSerial = max + 1
   */
  public boolean increment = false;

  public boolean isIncrement() {
    return increment;
  }

  public void setIncrement(boolean increment) {
    this.increment = increment;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  public boolean isVisable() {
    return visable;
  }

  public void setVisable(boolean visable) {
    this.visable = visable;
  }

  /*
   * Below is for the searching logic for list
   */

  public String searchModel;

  public String getSearchModel() {
    return searchModel;
  }

  public void setSearchModel(String searchModel) {
    this.searchModel = searchModel;
  }

  public boolean isSearchable() {
    return StringUtils.isNotBlank(getSearchModel());
  }

  /**
   * Default Value defined by schema's default attr
   */
  public String defaultValue = null;

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * If the element is a url 
   * e.g, start with http:// https:// cms://
   */
  public boolean isUrl = false;

  public boolean isUrl() {
    return isUrl;
  }

  public void setUrl(boolean isUrl) {
    this.isUrl = isUrl;
  }

  /**
   * e.g, <xs:documentation source="fieldColumn">PKGNM</xs:documentation>
   * defined in the xsd
   */
  public String fieldColumn;;

  public String getFieldColumn() {
    return fieldColumn;
  }

  public void setFieldColumn(String fieldColumn) {
    this.fieldColumn = fieldColumn;
  }

  public String getFieldColumnName() {
    return getFieldColumn().split(" ")[0];
  }

  /**
   * For name model.prod.prodInfo.strDt. return prodInfo.strDt
   * 
   * @param name
   * @return
   */
  public String getPath() {
    String path = name;
    if (path.startsWith("model.")) {
      path = StringUtils.substringAfter(path, "model.");
      path = StringUtils.substringAfter(path, ".");
    }
    return path;
  }

  /**
   * For name model.prod.prodInfo, return model.prod.
   * 
   * @param name
   * @return
   */
  public String getBase() {
    String base = name;
    if (base.startsWith("model.")) {
      base = StringUtils.substringAfter(base, "model.");
      base = StringUtils.substringBefore(base, ".");
    }
    return "model." + base + ".";
  }

  /**
   * For name model.prod.prodInfo, return prodInfo
   * 
   * @param name
   * @return
   */
  public String getLstName() {
    return StringUtils.substringAfterLast(getName(), ".");
  }

  public boolean isRepeatable() {
    return repeatable;
  }

  public void setRepeatable(boolean repeatable) {
    this.repeatable = repeatable;
  }

  /**
   * For type choice, indicate the name of the selected son
   */
  public String choice;

  public String getChoice() {
    return choice;
  }

  public void setChoice(String choice) {
    this.choice = choice;
  }

  public Map<String, String> selections = new TreeMap<String, String>();

  public List<SchemaElement> sons = new ArrayList<SchemaElement>();

  /**
   * Return full name from the model e.g, model.cust.account.actDt
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isNumber() {
    return number;
  }

  public void setNumber(boolean number) {
    this.number = number;
  }

  public boolean isEmail() {
    return email;
  }

  public void setEmail(boolean email) {
    this.email = email;
  }

  public boolean isPhone() {
    return phone;
  }

  public void setPhone(boolean phone) {
    this.phone = phone;
  }

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public String getMinDate() {
    return minDate;
  }

  public void setMinDate(String minDate) {
    this.minDate = minDate;
  }

  public String getMaxDate() {
    return maxDate;
  }

  public void setMaxDate(String maxDate) {
    this.maxDate = maxDate;
  }

  public int getMinNumber() {
    return minNumber;
  }

  public void setMinNumber(int minNumber) {
    this.minNumber = minNumber;
  }

  public int getMaxNumber() {
    return maxNumber;
  }

  public void setMaxNumber(int maxNumber) {
    this.maxNumber = maxNumber;
  }

  public Map<String, String> getSelections() {
    return selections;
  }

  public void setSelections(Map<String, String> selections) {
    this.selections = selections;
  }

  public List<SchemaElement> getSons() {
    return sons;
  }

  public void setSons(List<SchemaElement> sons) {
    this.sons = sons;
  }

  /*
   * Store all the mapping from element name - element only in the top level
   * element
   */
  private Map<String, SchemaElement> hash;

  public Map<String, SchemaElement> getHash() {
    return hash;
  }

  public void setHash(Map<String, SchemaElement> hash) {
    this.hash = hash;
  }

  /*
   * critierias
   */
  private Map<String, SchemaElement> indexes = null;

  public void setIndexes(Map<String, SchemaElement> indexes) {
    this.indexes = indexes;
  }

  public Map<String, SchemaElement> getIndexes() {
    if (indexes == null) {
      Map<String, SchemaElement> map = new PutTimeSortedMap<String, SchemaElement>();
      for (String name : getHash().keySet()) {
        // the name is from model.cust
        if (getHash().get(name).isIndex()) {
          map.put(name, getHash().get(name));
        }
      }
      setIndexes(map);
    }
    return indexes;
  }

  /*
   * Control the element's display model
   * e,g
   * table: if it is repeatable, display it as table
   * pic: display it as a pic
   */
  private String display = null;

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  /*
   * Check if this field is the model id field, if so, it will be displayed as a hidden input with a model value
   */
  public boolean isId = false;

  public boolean isId() {
    return isId;
  }

  public void setId(boolean isId) {
    this.isId = isId;
  }

  /**
   * For xs:string, it is RTE or TextArea
   */
  private boolean isRte = false;
  private boolean isTextArea = false;

  public boolean isRte() {
    return isRte;
  }

  public void setRte(boolean isRte) {
    this.isRte = isRte;
  }

  public boolean isTextArea() {
    return isTextArea;
  }

  public void setTextArea(boolean isTextArea) {
    this.isTextArea = isTextArea;
  }

  /**
   * 是否将某字段放入索引，目前暂时只支持一级的字段
   */
  private boolean index = false;

  public boolean isIndex() {
    return index;
  }

  public void setIndex(boolean index) {
    this.index = index;
  }

}
