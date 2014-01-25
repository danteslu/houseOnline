package com.houseOnline.common.xml;

import java.util.ArrayList;
import java.util.List;

public class XSDNode {

	private String name;
	
	private List<XSDNode> nodes = new ArrayList<XSDNode>();
	
	private List<XSDAnnotation> annotations =  new ArrayList<XSDAnnotation>();

	private String type;

	private boolean isUnbounded;
	
	private boolean isIndex;
	
	public XSDNode() {
		
	}

	public XSDNode(String nodeName, String typeName, boolean isUnbounded) {
		this.name = nodeName;
		this.type = typeName;
		this.isUnbounded = isUnbounded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsUnbounded() {
		return isUnbounded;
	}

	public void setIsUnbounded(boolean isUnbounded) {
		this.isUnbounded = isUnbounded;
	}

	public List<XSDNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<XSDNode> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(XSDNode node) {
		this.nodes.add(node);
	}

	public void addNodes(List<XSDNode> nodes) {
		this.nodes.addAll(nodes);
	}
	
	public List<XSDAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<XSDAnnotation> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(XSDAnnotation annotation) {
		this.annotations.add(annotation);
	}
	
	public void addAnnotations(List<XSDAnnotation> annotations) {
		this.annotations.addAll(annotations);
	}
	
	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}
	
	public boolean isLeaf() {
		return nodes == null || nodes.isEmpty();
	}
	
	public String getClassName() {
		
		for (XSDAnnotation annotation : annotations) {
			if (XMLConstants.XSD_ANNOTATION_SOURCE_CLASSNAME
					.equalsIgnoreCase(annotation.getSource())) {
				return annotation.getText();
			}
		}
		
		return "";
	}

	public XSDNode getSubNode(String nodeName) {
		
		for (XSDNode subNode : nodes) {
			if (nodeName.equals(subNode.getName())) {
				return subNode;
			}
		}
		
		return null;
	}
	
	public String className() {

		for (XSDAnnotation annotation : annotations) {

			if (XMLConstants.XSD_ANNOTATION_SOURCE_CLASSNAME
					.equalsIgnoreCase(annotation.getSource())) {

				return annotation.getText();

			}
		}

		return "";
	}
	
	public List<String> getIndexs() {
		
		List<String> indexs = new ArrayList<String>();
		
		if (isIndex) {
			indexs.add(name);
		} else {
			for (XSDNode node : nodes) {
				List<String> subIndexs = node.getIndexs();
				for (String subIndex : subIndexs)
					indexs.add(name + "." + subIndex);
			}
		}
		
		return indexs;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("[").append("name").append(":").append(name).append(",");
		
		builder.append("isUnbounded").append(":").append(isUnbounded).append(",");
		
		builder.append("isIndex").append(":").append(isIndex).append(",");
		
		appendAnnotations(builder);
		
		appendSubNodes(builder);
		
		builder.append("]");
		
		return builder.toString();
	}
	
	private void appendAnnotations(StringBuilder builder) {
		
		builder.append("[annotations:[");
		
		for (XSDAnnotation annotation : annotations) {
			builder.append(annotation.toString());
		}
		
		builder.append("]]");
	}
	
	private void appendSubNodes(StringBuilder builder) {
		
		builder.append("[nodes:[");
		
		for (XSDNode subNode : nodes) {
			builder.append(subNode.toString());
		}
		
		builder.append("]]");
	}
	
}
