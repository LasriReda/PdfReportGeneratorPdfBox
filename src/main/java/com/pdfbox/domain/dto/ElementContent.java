package com.pdfbox.domain.dto;

public class ElementContent {

	private String text;
	
	public ElementContent(String text){
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
