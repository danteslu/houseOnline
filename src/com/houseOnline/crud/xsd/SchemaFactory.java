/*
 * SchemaFactory.java since 2010-1-16
 * 
 * ©2009 GR LTD. ALL RIGHTS RESERVED.
 *
 * THIS SOFTWARE IS ONLY TO BE USED FOR THE PURPOSE FOR WHICH IT HAS BEEN PROVIDED. NO PART OF IT IS
 * TO BE REPRODUCED, DISASSEMBLED, TRANSMITTED, STORED IN A RETRIEVAL SYSTEM NOR TRANSLATED IN ANY
 * HUMAN OR COMPUTER LANGUAGE IN ANY WAY OR FOR ANY OTHER PURPOSES WHATSOEVER WITHOUT THE PRIOR
 * WRITTEN CONSENT OF GR LTD.
 * 
 */
package com.houseOnline.crud.xsd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.fengweimin.common.ApplicationConfig;
import com.fengweimin.common.LoggerFactory;
import com.fengweimin.common.PutTimeSortedMap;
import com.fengweimin.crud.Model;
import com.fengweimin.crud.annotations.ModelConfig;

/**
 * <p>
 * ClassName: SchemaFactory
 * </p>
 * 
 * <p>
 * Abstract:
 * </p>
 * 
 * <p>
 * Amendment History (In chronological sequence): <br/>
 * 
 * </p>
 * 
 * @author
 * @since 2010-1-16
 */

public class SchemaFactory {
  /*
   * Singleton
   */
  private static SchemaFactory instance;

  private SchemaFactory() {

  };

  private synchronized static void init() {
    if (instance == null) {
      instance = new SchemaFactory();
    }
  }

  public static SchemaFactory getInstance() {
    init();
    return instance;
  }

  /*
   * Fields
   */

  private Map<String, Document> schemaMaps = new HashMap<String, Document>();
  private Map<String, SchemaElement> schemaElementMaps = new HashMap<String, SchemaElement>();

  /*
   * Methods
   */

  public synchronized void refresh() {
    schemaMaps.clear();
    schemaElementMaps.clear();
  }

  public Document getSchemaDocument(String docName) {
    if (schemaMaps.containsKey(docName)) {
      return schemaMaps.get(docName);
    } else {
      SAXReader reader = new SAXReader();
      Document document = null;
      try {
        InputStream is = SchemaFactory.class.getResourceAsStream(docName);
        if (is == null) {
          return null;
        }
        document = reader.read(is);
        schemaMaps.put(docName, document);
      } catch (DocumentException e) {
        LoggerFactory.getSystemLogger().error("Error when reading the xsd", e);
      } finally {
        reader = null;
      }
      return document;
    }
  }

  /**
   * Get schema element by name
   * 
   * @param modelName
   * @param docName
   * @return
   */
  public SchemaElement getSchemaElement(String modelName, String docName) {
    // Caching by name
    if (schemaElementMaps.containsKey(modelName)) {
      return schemaElementMaps.get(modelName);
    } else {
      Document schemaDoc = getSchemaDocument(docName);
      if (schemaDoc == null) {
        return null;
      }
      // Only support 2 level partial edit
      String elementParent = StringUtils.split(modelName, ".")[0];
      String elementName = StringUtils.substringAfterLast("." + modelName, ".");

      Node node = schemaDoc.selectSingleNode("/xs:schema//xs:element[@name='" + elementName + "']");
      String currentPath = "model" + "." + modelName;

      // This hashAccess only in the top level's element, it is used to find
      // element definition by the element's name
      Map<String, SchemaElement> hashAcess = new PutTimeSortedMap<String, SchemaElement>();

      SchemaElement element = parseNode(schemaDoc, currentPath, node, hashAcess);
      element.setHash(hashAcess);

      /* 
       * For model name cust.cmnt
       * because cmnt is a ref under cust, 
       * using //cmnt can find the definition directly but can not get the relationship between cust and cmnt
       * <xs:element name="cust">
       *   <xs:complexType>
       *     <xs:sequence>
       *       <xs:element ref="cmnt" minOccurs="0" maxOccurs="unbounded"/>   
       *     </xs:sequence>
       *   </xs:complexType>
       * </xs:element>
       * So we need to further set the relationship of cust and cmnt  
       */
      if (modelName.indexOf(".") > 0) {
        Node ref = schemaDoc.selectSingleNode("/xs:schema//xs:element[@name='" + elementParent + "']")
            .selectSingleNode(".//xs:element[@ref='" + elementName + "']");
        if (ref != null) {
          LoggerFactory.getSystemLogger().debug("got ref " + elementName + " under " + elementParent);
          String tmp = ref.valueOf("./@minOccurs");
          if (!StringUtils.isEmpty(tmp)) {
            element.required = Integer.parseInt(tmp) == 0 ? false : true;
          } else {
            // If not set minOccurs and maxOccurs in XSD, they has default value
            // 1
            element.required = true;
          }

          tmp = ref.valueOf("./@maxOccurs");
          if (!StringUtils.isEmpty(tmp)) {
            if (tmp.equals("unbounded")) {
              element.repeatable = true;
            } else {
              element.repeatable = Integer.parseInt(tmp) > 1 ? true : false;
            }
          }
        }
      }

      schemaElementMaps.put(modelName, element);
      return element;
    }
  }

  private SchemaElement parseNode(Document doc, String currentPath, Node xsdElement, Map<String, SchemaElement> hash) {
    SchemaElement element = new SchemaElement();
    if (xsdElement.getName().equals("element")) {
      element.name = currentPath;
      hash.put(currentPath, element);
      // LoggerFactory.getInstance().getSystemLogger().debug("Parse " +
      // element.name);

      String tmp = xsdElement.valueOf("./@minOccurs");
      if (!StringUtils.isEmpty(tmp)) {
        element.required = Integer.parseInt(tmp) == 0 ? false : true;
      } else {
        // If not set minOccurs and maxOccurs in XSD, they has default value 1
        element.required = true;
      }

      tmp = xsdElement.valueOf("./@maxOccurs");
      if (!StringUtils.isEmpty(tmp)) {
        if (tmp.equals("unbounded")) {
          element.repeatable = true;
        } else {
          element.repeatable = Integer.parseInt(tmp) > 1 ? true : false;
        }
      }

      element.label = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='label']");
      element.description = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='description']");

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='index']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setIndex(tmp.equals("true") ? true : false);
      }

      element.setSearchModel(xsdElement.valueOf("./xs:annotation/xs:documentation[@source='searchModel']"));
      element.setFieldColumn(xsdElement.valueOf("./xs:annotation/xs:documentation[@source='fieldColumn']"));

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='visable']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setVisable(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='disable']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setDisable(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='readonly']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setReadonly(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='increment']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setIncrement(tmp.equals("true") ? true : false);
      }

      element.setDisplay(xsdElement.valueOf("./xs:annotation/xs:documentation[@source='display']"));

      element.setDefaultValue(xsdElement.valueOf("./@default"));

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='isUrl']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setUrl(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='isId']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setId(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='rte']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setRte(tmp.equals("true") ? true : false);
      }

      tmp = xsdElement.valueOf("./xs:annotation/xs:documentation[@source='textarea']");
      if (StringUtils.isNotEmpty(tmp)) {
        element.setTextArea(tmp.equals("true") ? true : false);
      }

      /*
      Handle Build-in Type       
              <xs:element name="prodNm" type="xs:string">
                <xs:annotation>
                  <xs:documentation source="label">产品名称</xs:documentation>
                </xs:annotation>
              </xs:element>
      */
      if (StringUtils.isNotEmpty(xsdElement.valueOf("./@type"))) {
        // LoggerFactory.getInstance().getSystemLogger()
        // .debug("It is simple Type");
        String attr = xsdElement.valueOf("./@type");
        setElementType(doc, element, attr);

        if (element.type.equals(SchemaConstant.INPUT_TYPE_UNSUPPORTED)) {
          return null;
        }

      }
      // With specified xs:simpleTtype or xs:complexType
      else {
        List<Node> list = xsdElement.selectNodes("./*");
        for (Node node : list) {
          String nodeName = node.getName().toLowerCase();
          /*
          Handle simple Type       
              <xs:element name="sgType">
                <xs:annotation>
                  <xs:documentation source="label">单品属性</xs:documentation>
                </xs:annotation>
                <xs:simpleType>
                  <xs:restriction base="xs:string">
                    <xs:maxLength value="100"/>
                  </xs:restriction>
                </xs:simpleType>
              </xs:element>
          */
          if (nodeName.equals("simpletype")) {
            // LoggerFactory.getInstance().getSystemLogger().debug(
            // "It is simple Type");
            Node type = node.selectSingleNode("./xs:restriction");
            String attr = type.valueOf("./@base");
            setElementType(doc, element, attr);
            if (element.type.equals(SchemaConstant.INPUT_TYPE_UNSUPPORTED)) {
              return null;
            }

            tmp = type.valueOf("./xs:maxLength/@value");
            if (StringUtils.isNotEmpty(tmp)) {
              element.maxLength = Integer.parseInt(tmp);
            }

            tmp = type.valueOf("./xs:mininclusive/@value");
            if (StringUtils.isNotEmpty(tmp)) {
              if (element.type.equals(SchemaConstant.INPUT_TYPE_DATE)) {
                element.minDate = tmp;
              } else {
                element.minNumber = Integer.parseInt(tmp);
              }
            }

            tmp = type.valueOf("./xs:maxinclusive/@value");
            if (StringUtils.isNotEmpty(tmp)) {
              if (element.type.equals(SchemaConstant.INPUT_TYPE_DATE)) {
                element.maxDate = tmp;
              } else {
                element.maxNumber = Integer.parseInt(tmp);
              }
            }

            List<Node> enmu = type.selectNodes("./xs:enumeration");
            if (enmu.size() > 0) {
              element.type = SchemaConstant.INPUT_TYPE_SELECT;
              for (Node item : enmu) {
                // Value - Label
                element.selections.put(item.valueOf("./@value"), item.valueOf("./xs:annotation/xs:documentation"));
              }
            }

            tmp = type.valueOf("./xs:pattern/@value");
            if (StringUtils.isNotEmpty(tmp)) {
              if (tmp.equals("^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$")) {
                element.email = true;
              } else if (tmp.equals("^[2-9]\\d{2}-\\d{3}-\\d{4}$")) {
                element.phone = true;
              } else {
                // TODO: Support Other Pattern
              }
            }
          }
          /*
          Handle complex type
           */
          else if (nodeName.equals("complextype")) {
            // LoggerFactory.getInstance().getSystemLogger().debug(
            // "It is complex Type");
            // Skip to set model element as group
            Node xslist = node.selectSingleNode("./xs:sequence");
            if (xslist == null) {
              xslist = node.selectSingleNode("./xs:all");
            }
            List<Node> sons = null;
            // Sequence or All
            if (xslist != null) {
              String maxOccurs = xslist.valueOf("./@maxOccurs");
              if (StringUtils.isNotBlank(maxOccurs)
                  && (maxOccurs.equals("unbounded") || Integer.valueOf(maxOccurs) > 1)) {
                element.type = SchemaConstant.DIV_TYPE_LIST;
              } else {
                element.type = SchemaConstant.DIV_TYPE_GROUP;
              }
              sons = xslist.selectNodes("./xs:element");
            } else {
              Node choice = node.selectSingleNode("./xs:choice");
              // Choice
              if (choice != null) {
                element.type = SchemaConstant.DIV_TYPE_CHOICE;
                sons = choice.selectNodes("./xs:element");
              }
            }

            if (sons != null) {
              // LoggerFactory.getInstance().getSystemLogger().debug(
              // "There are " + sons.size() + " sons");
              boolean first = true;
              for (Node son : sons) {
                String sonName = son.valueOf("./@name");
                Node realSon = son;

                // Ref
                boolean isref = false;
                if (StringUtils.isEmpty(sonName)) {
                  isref = true;
                  sonName = son.valueOf("./@ref");
                  realSon = doc.selectSingleNode("/xs:schema/xs:element[@name='" + sonName + "']");
                  if (realSon == null) {
                    //
                  }

                  // Prevent recursive
                  if (element.name.endsWith("." + sonName)) {
                    continue;
                  }
                }

                SchemaElement ele = parseNode(doc, element.name + "." + sonName, realSon, hash);

                // set the min/max for the ref element
                if (isref) {
                  tmp = son.valueOf("./@minOccurs");
                  if (!StringUtils.isEmpty(tmp)) {
                    ele.required = Integer.parseInt(tmp) == 0 ? false : true;
                  } else {
                    // If not set minOccurs and maxOccurs in XSD, they has
                    // default value 1
                    ele.required = true;
                  }

                  tmp = son.valueOf("./@maxOccurs");
                  if (!StringUtils.isEmpty(tmp)) {
                    if (tmp.equals("unbounded")) {
                      ele.repeatable = true;
                    } else {
                      ele.repeatable = Integer.parseInt(tmp) > 1 ? true : false;
                    }
                  }
                }

                if (ele != null) {
                  element.sons.add(ele);
                  // If in a group, the first element is boolean,
                  // It will be regarded as a open indicator of the whole group
                  // We will use checkbox for it
                  if (first && element.type == SchemaConstant.DIV_TYPE_GROUP) {
                    if (ele.type.equals(SchemaConstant.INPUT_TYPE_BOOLEAN)) {
                      ele.type = SchemaConstant.INPUT_TYPE_CHECKBOX;
                    }
                    first = false;
                  }
                }
              }
            }

          } else {
            //
          }
        }
      }
      return element;
    } else {
      return null;
    }
  }

  private void setElementType(Document doc, SchemaElement element, String attr) {
    // If not starts with xs:, means it is a user defined simple type
    if (!attr.startsWith("xs:")) {
      // Try to get the defintion from Const.xsd
      Document schemaDoc = getSchemaDocument("Const");
      Node node = null;
      if (schemaDoc != null) {
        node = schemaDoc.selectSingleNode("/xs:schema//xs:simpleType[@name='" + attr + "']");
      }
      // If can not find in Const.xsd, try to find in local
      if (node == null) {
        node = doc.selectSingleNode("/xs:schema//xs:simpleType[@name='" + attr + "']");
      }
      Node type = node.selectSingleNode("./xs:restriction");
      List<Node> enmu = type.selectNodes("./xs:enumeration");
      if (enmu.size() > 0) {
        element.type = SchemaConstant.INPUT_TYPE_SELECT;
        for (Node item : enmu) {
          // Value - Label
          element.selections.put(item.valueOf("./@value"), item.valueOf("./xs:annotation/xs:documentation"));
        }
      }
      return;
    }

    if (element.isUrl) {
      element.setType(SchemaConstant.INPUT_TYPE_URL);
      return;
    }

    attr = attr.toLowerCase();
    if (attr.equals("xs:string")) {
      // genericField.type = "text";
      if (element.isRte()) {
        element.setType(SchemaConstant.INPUT_TYPE_RTE);
      } else if (element.isTextArea()) {
        element.setType(SchemaConstant.INPUT_TYPE_TEXTAREA);
      }
    } else if (attr.equals("xs:normalizedstring")) {
      // genericField.type = "text";
    } else if (attr.equals("xs:integer") || attr.equals("xs:float") || attr.equals("xs:int") || attr.equals("xs:long")
        || attr.equals("xs:double")) {
      element.number = true;
    } else if (attr.equals("xs:date")) {
      element.type = SchemaConstant.INPUT_TYPE_DATE;
    } else if (attr.equals("xs:datetime")) {
      element.type = SchemaConstant.INPUT_TYPE_DATETIME;
    } else if (attr.equals("xs:time")) {
      element.type = SchemaConstant.INPUT_TYPE_TIME;
    } else if (attr.equals("xs:boolean")) {
      element.type = SchemaConstant.INPUT_TYPE_BOOLEAN;
    } else if (attr.equals("xs:anyuri")) {
      element.type = SchemaConstant.INPUT_TYPE_FILE;
    } else {
      LoggerFactory.getSystemLogger().warn("Unsupport element type, " + attr);
      element.type = SchemaConstant.INPUT_TYPE_UNSUPPORTED;
    }
  }

}
