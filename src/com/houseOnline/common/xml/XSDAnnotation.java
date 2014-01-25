package com.houseOnline.common.xml;

public class XSDAnnotation {
	
	private String source;
	
	private String text;
	
	public XSDAnnotation() {
		
	}
	
	public XSDAnnotation(String source, String text) {
		this.source = source;
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("[source:").append(source).append(",");
		
		builder.append("[text:").append(text).append("]");
		
		return builder.toString();
	}
}
