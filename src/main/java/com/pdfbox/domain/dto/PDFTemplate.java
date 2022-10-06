package com.pdfbox.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PDFTemplate {

	TEST("test.pdf");
	
	String templateName;
	
	private PDFTemplate(String name){
		this.templateName = name;
	}
	
	public String getTemplateName(){
		return templateName;
	}
	
	@JsonCreator
    public static PDFTemplate fromString(String value) {
        return value == null ? null : PDFTemplate.valueOf(value.toUpperCase());
    }
}
