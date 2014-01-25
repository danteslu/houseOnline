/*
 * CastorHelper.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.castor.mapping.BindingType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLClassDescriptorResolver;

/**
 * <p>ClassName: CastorHelper</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class CastorHelper {
  public static XMLClassDescriptorResolver classDescriptorResolver = (XMLClassDescriptorResolver) ClassDescriptorResolverFactory
      .createClassDescriptorResolver(BindingType.XML);

  public static Unmarshaller getUnMarshaller() {
    Unmarshaller unm = new Unmarshaller();
    unm.setResolver(classDescriptorResolver);
    return unm;
  }

  /**
   * ToString for XML Object
   * 
   * @param resp         
   * @return String with a xml header
   * @throws Exception
   */
  public static String toString(Object CastorObject) throws Exception {
    return toString(CastorObject, true);
  }

  public static String toString(Object CastorObject, boolean isValidate) throws Exception {
    StringWriter sw = null;
    try {
      sw = new StringWriter();
      Marshaller m = new Marshaller(sw);
      m.setResolver(classDescriptorResolver);
      m.setValidation(isValidate);
      m.marshal(CastorObject);
      sw.flush();
      String tmp = sw.toString();
      return tmp;
    } finally {
      if (sw != null) {
        sw.close();
        sw = null;
      }
    }
  }

  /**
   * ToString for XML Object
   * 
   * @param resp 
   * @return String without a xml header
   * @throws Exception
   */
  public static String toString2(Object CastorObject) throws Exception {
    return toString2(CastorObject, true);
  }

  public static String toString2(Object CastorObject, boolean isValidate) throws Exception {
    StringWriter sw = null;
    try {
      sw = new StringWriter();

      Marshaller m = new Marshaller(sw);
      m.setResolver(classDescriptorResolver);
      m.setValidation(isValidate);
      m.marshal(CastorObject);
      sw.flush();
      String tmp = sw.toString();
      return DocumentHelper.parseText(tmp).getRootElement().asXML();
    } finally {
      if (sw != null) {
        sw.close();
        sw = null;
      }
    }
  }

  /**
   * ToString for XML element
   * 
   * @param resp
   * @return Dom4j Element
   * @throws Exception
   */
  public static Element toElement(Object CastorObject) throws Exception {
    return toElement(CastorObject, true);
  }

  public static Document toDocument(Object CastorObject) throws Exception {
    return toElement(CastorObject, true).getDocument();
  }

  public static Element toElement(Object CastorObject, boolean isValidate) throws Exception {
    StringWriter sw = null;
    try {
      sw = new StringWriter();
      Marshaller m = new Marshaller(sw);
      m.setResolver(classDescriptorResolver);
      m.setValidation(isValidate);
      m.marshal(CastorObject);
      sw.flush();
      String tmp = sw.toString();
      return DocumentHelper.parseText(tmp).getRootElement();
    } finally {
      if (sw != null) {
        sw.close();
        sw = null;
      }
    }
  }

  public static Object toObject(java.lang.Class c, String xmlString) throws Exception {
    return toObject(c, xmlString, true);
  }

  public static Object toObject(java.lang.Class c, String xmlString, boolean isValidate) throws Exception {
    StringReader sr = null;
    try {
      sr = new StringReader(xmlString);
      Unmarshaller m = new Unmarshaller(c);
      m.setValidation(isValidate);
      Object rtnObj = m.unmarshal(sr);
      return rtnObj;
    } finally {
      if (sr != null) {
        sr.close();
        sr = null;
      }
    }
  }

  public static Object toObject(java.lang.Class c, Reader reader, boolean isValidate) throws Exception {
    Unmarshaller m = new Unmarshaller(c);
    m.setValidation(isValidate);
    Object rtnObj = m.unmarshal(reader);
    return rtnObj;
  }

  /**
   * 克隆对象
   * 
   * 有性能问题，慎用
   * 
   * @param CastorObject
   * @param c
   * @return
   * @throws Exception
   */
  public static Object clone(Object CastorObject, java.lang.Class c) throws Exception {
    String xml = toString(CastorObject);
    if (StringUtils.isNotBlank(xml)) {
      return toObject(c, xml);
    }
    return null;
  }
}
