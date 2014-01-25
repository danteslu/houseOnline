package com.houseOnline.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XSDReader {
	
	private final static String CLASS_XSD_PROPERTIES = "CLASS_XSD.properties";
	
	private final static Properties properties = new Properties();
	
	private final static Map<String, XSDNode> cache = new HashMap<String, XSDNode>();
	
	static {
		
		InputStream inStream = null;
				
		try {
			
			inStream = ClassLoader.getSystemResourceAsStream(CLASS_XSD_PROPERTIES);
			
			properties.load(inStream);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static XSDNode getXSDForClass(String className) throws Exception {
		
		XSDNode xsdNode = cache.get(className);
		
		if (xsdNode != null) {
			return xsdNode;
		}
		
		String xsd = getXSDPath(className);
		
		return parseXSD(xsd);
	}
	
	private static String getXSDPath(String className) {
		return properties.getProperty(className);
	}
	
	/**
	 * 
	 * 
	 * @param xsd
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	private static XSDNode parseXSD(String xsd) throws Exception {
		
		SAXReader saxReader = new SAXReader();

		Document doc = saxReader.read(xsd);

		Element root = doc.getRootElement();
		
		Element modelElement = (Element) root.selectSingleNode(XMLConstants.XSD_BASE_PATH);
		
		return parseData(modelElement, XMLConstants.XSD_BASE_PATH);

	}

	/**
	 * 
	 * @param nodeName
	 * @param typeName
	 * @param isUnbounded
	 * @return
	 * @throws DocumentException 
	 */
	private static XSDNode parseData(Element current, String xPath) throws DocumentException {
		
		XSDNode xsdNode = null;
		
		String nodeName = current.attributeValue(XMLConstants.XSD_ATTRIBUTE_NAME);
		
		String refName = current.attributeValue(XMLConstants.XSD_ATTRIBUTE_REF);
		
		String absolutePath = xPath;
		
		if (nodeName != null) {
			absolutePath = absolutePath + "[@name=\"" + nodeName + "\"]";
		}
		
		if (refName != null) {
			absolutePath = absolutePath + "[@ref=\"" + refName + "\"]";
		}
		
		String absolutesubElePath = absolutePath + XMLConstants.XSD_SUB_ELEMENT_PATH;
		
		List<Node> elementNodes = current.selectNodes(absolutesubElePath);
		
		if (elementNodes != null && elementNodes.size() > 0) {
			
			String maxOccurs = current.attributeValue(XMLConstants.XSD_ATTRIBUTE_MAX_OCCURS);
			
			xsdNode = new XSDNode(nodeName, "", XMLConstants.XSD_UNBOUNDED.equalsIgnoreCase(maxOccurs));
			
			Iterator<Node> nodes = elementNodes.iterator();
			while (nodes.hasNext()) {
				Element subEle = (Element) nodes.next();
				xsdNode.addNode(parseData(subEle, absolutesubElePath));
			}
			
			xsdNode.addNodes(parseAttrEle(current, absolutePath));
			
			xsdNode.addAnnotations(parseAnnoEle(current, absolutePath));
			
		} else {
			xsdNode = parseLeafNode(current, absolutePath);
		}
		
		return xsdNode;
	}
	
	/**
	 * 
	 * @return
	 */
	private static List<XSDAnnotation> parseAnnoEle(Element element, String xPath) {
		
		List<XSDAnnotation> annotations = new ArrayList<XSDAnnotation>();
		
		String absoluteAnnoPath = xPath + XMLConstants.XSD_DOCUMENT;
		
		List<Node> anntEles = element.selectNodes(absoluteAnnoPath);
		
		if (anntEles == null) {
			return annotations;
		}
		
		for (Node node : anntEles) {
			Element anntEle = (Element)node;
			String source = anntEle.attributeValue(XMLConstants.XSD_ANNOTATION_SOURCE);
			String text = anntEle.getText();
			annotations.add(new XSDAnnotation(source, text));
		}
		
		return annotations;
	}
	
	/**
	 * @param currentEle
	 * @return
	 */
	private static List<XSDNode> parseAttrEle(Element element, String xPath) {
		
		List<XSDNode> attrNodes = new ArrayList<XSDNode>();
		
		String absoluteAttrPath = xPath + XMLConstants.XSD_ATTRIBUTE;
		
		List<Node> attrEles = element.selectNodes(absoluteAttrPath);
		
		if (attrEles == null) {
			return attrNodes;
		}
		
		for (Node node : attrEles) {
			Element attEle = (Element)node;
			String name = attEle.attributeValue(XMLConstants.XSD_ATTRIBUTE_NAME);
			String type = attEle.attributeValue(XMLConstants.XSD_ATTRIBUTE_TYPE);
			attrNodes.add(new XSDNode(name, type, false));
		}
		
		return attrNodes;
	}
	
	/**
	 * 
	 * @param curentElement
	 * @param xPath
	 * @throws DocumentException 
	 */
	private static XSDNode parseLeafNode(Element leaf, String xPath) throws DocumentException {
		String name = leaf.attributeValue(XMLConstants.XSD_ATTRIBUTE_NAME);
		String type = leaf.attributeValue(XMLConstants.XSD_ATTRIBUTE_TYPE);
		String maxOccurs = leaf.attributeValue(XMLConstants.XSD_ATTRIBUTE_MAX_OCCURS);
		String refName = leaf.attributeValue(XMLConstants.XSD_ATTRIBUTE_REF);
		
		String customizeName = getCustomizeName(leaf, xPath);
		
		String absoluteDocPath = xPath + XMLConstants.XSD_DOCUMENT;
		
		List<Node> documentNodes = leaf.selectNodes(absoluteDocPath);
		
		boolean isIndex = false;
		if (documentNodes != null && !documentNodes.isEmpty()) {
			for (Node node : documentNodes) {
				Element documentNode = (Element)node;
				String source = documentNode.attributeValue("source");
				String text = documentNode.getText();
				isIndex = ("index".equalsIgnoreCase(source) && "true".equalsIgnoreCase(text));
			}
		}
		
		XSDNode xsdNode = null;
		
		if (!StringUtils.isEmpty(refName)) {
			xsdNode = parseRef(leaf, refName);
			xsdNode.setIsUnbounded(XMLConstants.XSD_UNBOUNDED.equalsIgnoreCase(maxOccurs));
		} else {			
			xsdNode = new XSDNode(name, type, XMLConstants.XSD_UNBOUNDED.equalsIgnoreCase(maxOccurs));
			xsdNode.setIndex(isIndex);
		}
		
		if (!StringUtils.isEmpty(customizeName)) {
			xsdNode.setName(customizeName);
		}
		
		return xsdNode;
	}
	
	private static XSDNode parseRef(Element element, String refName) throws DocumentException {
		
		Element refEle = getRefEle(element, refName);
		
		return parseData(refEle, XMLConstants.XSD_BASE_PATH);
	}
	
	private static Element getRefEle(Element element, String refName) {
		
		String refXPath = XMLConstants.XSD_BASE_PATH + "[@name=\"" + refName + "\"]";
		
		return (Element) element.selectSingleNode(refXPath);
		
	}
	
	private static String getCustomizeName(Element element, String xPath) {
		
		if (element == null) {
			return null;
		}
		
		Element jxbPropEle = (Element) element.selectSingleNode(xPath + XMLConstants.XSD_JXB_PROPERTY);
		
		if (jxbPropEle == null) {
			return null;
		}
		
		return jxbPropEle.attributeValue(XMLConstants.XSD_ATTRIBUTE_NAME);
		
	}

	public static void main(String[] args) {

		try {
			
			XSDReader xsdReader = new XSDReader();
			
			String xsd = "C:\\Dantes\\workspaces\\ADExchange\\sample\\config\\xsd\\user.xsd";

			XSDNode node = xsdReader.parseXSD(xsd);
			
			System.out.println(node.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

		}
	}
}