package com.pdfbox.domain.dto;

import java.awt.Color;

public class PdfContent {

	public static final int ALIGNEMENT_CENTER = 10;
	public static final int ALIGNEMENT_LEFT = 11;
	public static final int ALIGNEMENT_SIGNATURE = 13;
	private ElementContent[][] content;
	private boolean table = false;
	private int align = ALIGNEMENT_LEFT;
	private Color color;
	private int policeSize = 10;
	private int fontType = PdfDocument.FONT_NORMAL;
	
	public PdfContent(final PdfContent src){
		this.align = src.align;
		this.color = src.color;
		this.content = src.content;
		this.policeSize = src.policeSize;
		this.table = src.table;
	}
	
	public PdfContent(ElementContent[][] content){
		this.setContent(content);
	}
	
	public PdfContent(ElementContent[][] content, String[][] values){
		this.setContent(content, values);
	}
	
	public PdfContent(ElementContent[][] content, boolean isTable){
		this.setTable(isTable);
		this.setContent(content);
	}
	
	public PdfContent(ElementContent[][] content, String[][] values, boolean isTable){
		this.setTable(isTable);
		this.setContent(content, values);
	}
	
	public PdfContent(ElementContent[][] content, int align){
		this.setContent(content);
		this.setAlign(align);
	}
	
	public PdfContent(ElementContent[][] content, String[][] values, int align){
		this.setContent(content, values);
		this.setAlign(align);
	}
	
	public PdfContent(ElementContent[][] content, boolean isTable, int align){
		this.setTable(isTable);
		this.setAlign(align);
		this.setContent(content);
	}
	
	public PdfContent(ElementContent[][] content, String[][] values, boolean isTable, int align){
		this.setTable(isTable);
		this.setAlign(align);
		this.setContent(content, values);
	}

	public ElementContent[][] getContent() {
		return content;
	}

	public void setContent(final ElementContent[][] content) {
		this.content = content;
	}

	public void setContent(final ElementContent[][] content, String[][] values) {
		this.content = content;
		for(int i = 0; i < content.length; i++){
			for(int j = 0; j < content[i].length; j++){
				for(int k = 0; k < values.length; k++){
					this.content[i][j].setText(this.content[i][j].getText().replaceAll(values[k][0], values[k][1]));
				}
			}
		}
	}
	
	public static ElementContent[][] buildContentFromString(String[][] content){
		int d1 = content.length;
		int d2 = 0;
		if(d1 > 0){
			d2 = content[0].length;
		}
		ElementContent[][] tmp = new ElementContent[d1][d2];
		for(int i = 0; i < content.length; i++){
			for(int j = 0; j < content[i].length; j++){
				tmp[i][j] = new ElementContent(content[i][j]);
			}
		}
		return tmp;
	}

	public boolean isTable() {
		return table;
	}

	public void setTable(boolean table) {
		this.table = table;
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getPoliceSize() {
		return policeSize;
	}

	public void setPoliceSize(int policeSize) {
		this.policeSize = policeSize;
	}

	public int getFontType() {
		return fontType;
	}

	public void setFontType(int fontType) {
		this.fontType = fontType;
	}
}
